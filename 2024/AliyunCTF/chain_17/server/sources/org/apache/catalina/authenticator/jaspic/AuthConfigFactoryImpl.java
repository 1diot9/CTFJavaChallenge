package org.apache.catalina.authenticator.jaspic;

import jakarta.security.auth.message.AuthException;
import jakarta.security.auth.message.AuthStatus;
import jakarta.security.auth.message.MessageInfo;
import jakarta.security.auth.message.config.AuthConfigFactory;
import jakarta.security.auth.message.config.AuthConfigProvider;
import jakarta.security.auth.message.config.ClientAuthConfig;
import jakarta.security.auth.message.config.RegistrationListener;
import jakarta.security.auth.message.config.ServerAuthConfig;
import jakarta.security.auth.message.config.ServerAuthContext;
import jakarta.security.auth.message.module.ServerAuthModule;
import jakarta.servlet.ServletContext;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import org.apache.catalina.authenticator.jaspic.PersistentProviderRegistrations;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/jaspic/AuthConfigFactoryImpl.class */
public class AuthConfigFactoryImpl extends AuthConfigFactory {
    private static final String SERVLET_LAYER_ID = "HttpServlet";
    private static final StringManager sm = StringManager.getManager((Class<?>) AuthConfigFactoryImpl.class);
    private static final String CONFIG_PATH = "conf/jaspic-providers.xml";
    private static final File CONFIG_FILE = new File(System.getProperty("catalina.base"), CONFIG_PATH);
    private static final Object CONFIG_FILE_LOCK = new Object();
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static String DEFAULT_REGISTRATION_ID = getRegistrationID(null, null);
    private final Log log = LogFactory.getLog((Class<?>) AuthConfigFactoryImpl.class);
    private final Map<String, RegistrationContextImpl> layerAppContextRegistrations = new ConcurrentHashMap();
    private final Map<String, RegistrationContextImpl> appContextRegistrations = new ConcurrentHashMap();
    private final Map<String, RegistrationContextImpl> layerRegistrations = new ConcurrentHashMap();
    private final Map<String, RegistrationContextImpl> defaultRegistration = new ConcurrentHashMap(1);

    public AuthConfigFactoryImpl() {
        loadPersistentRegistrations();
    }

    @Override // jakarta.security.auth.message.config.AuthConfigFactory
    public AuthConfigProvider getConfigProvider(String layer, String appContext, RegistrationListener listener) {
        RegistrationContextImpl registrationContext = findRegistrationContextImpl(layer, appContext);
        if (registrationContext != null) {
            if (listener != null) {
                RegistrationListenerWrapper wrapper = new RegistrationListenerWrapper(layer, appContext, listener);
                registrationContext.addListener(wrapper);
            }
            return registrationContext.getProvider();
        }
        return null;
    }

    @Override // jakarta.security.auth.message.config.AuthConfigFactory
    public String registerConfigProvider(String className, Map<String, String> properties, String layer, String appContext, String description) {
        String registrationID = doRegisterConfigProvider(className, properties, layer, appContext, description);
        savePersistentRegistrations();
        return registrationID;
    }

    private String doRegisterConfigProvider(String className, Map<String, String> properties, String layer, String appContext, String description) {
        if (this.log.isDebugEnabled()) {
            this.log.debug(sm.getString("authConfigFactoryImpl.registerClass", className, layer, appContext));
        }
        AuthConfigProvider provider = null;
        if (className != null) {
            provider = createAuthConfigProvider(className, properties);
        }
        String registrationID = getRegistrationID(layer, appContext);
        RegistrationContextImpl registrationContextImpl = new RegistrationContextImpl(layer, appContext, description, true, provider, properties);
        addRegistrationContextImpl(layer, appContext, registrationID, registrationContextImpl);
        return registrationID;
    }

    private AuthConfigProvider createAuthConfigProvider(String className, Map<String, String> properties) throws SecurityException {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
        }
        if (clazz == null) {
            try {
                clazz = Class.forName(className);
            } catch (IllegalArgumentException | ReflectiveOperationException e2) {
                throw new SecurityException(e2);
            }
        }
        Constructor<?> constructor = clazz.getConstructor(Map.class, AuthConfigFactory.class);
        AuthConfigProvider provider = (AuthConfigProvider) constructor.newInstance(properties, null);
        return provider;
    }

    @Override // jakarta.security.auth.message.config.AuthConfigFactory
    public String registerConfigProvider(AuthConfigProvider provider, String layer, String appContext, String description) {
        if (this.log.isDebugEnabled()) {
            this.log.debug(sm.getString("authConfigFactoryImpl.registerInstance", provider.getClass().getName(), layer, appContext));
        }
        String registrationID = getRegistrationID(layer, appContext);
        RegistrationContextImpl registrationContextImpl = new RegistrationContextImpl(layer, appContext, description, false, provider, null);
        addRegistrationContextImpl(layer, appContext, registrationID, registrationContextImpl);
        return registrationID;
    }

    private void addRegistrationContextImpl(String layer, String appContext, String registrationID, RegistrationContextImpl registrationContextImpl) {
        RegistrationContextImpl previous;
        RegistrationContextImpl registration;
        if (layer != null && appContext != null) {
            previous = this.layerAppContextRegistrations.put(registrationID, registrationContextImpl);
        } else if (layer == null && appContext != null) {
            previous = this.appContextRegistrations.put(registrationID, registrationContextImpl);
        } else if (layer != null && appContext == null) {
            previous = this.layerRegistrations.put(registrationID, registrationContextImpl);
        } else {
            previous = this.defaultRegistration.put(registrationID, registrationContextImpl);
        }
        if (previous == null) {
            if (layer != null && appContext != null && (registration = this.appContextRegistrations.get(getRegistrationID(null, appContext))) != null) {
                for (RegistrationListenerWrapper wrapper : registration.listeners) {
                    if (layer.equals(wrapper.getMessageLayer()) && appContext.equals(wrapper.getAppContext())) {
                        registration.listeners.remove(wrapper);
                        wrapper.listener.notify(wrapper.messageLayer, wrapper.appContext);
                    }
                }
            }
            if (appContext != null) {
                for (RegistrationContextImpl registration2 : this.layerRegistrations.values()) {
                    for (RegistrationListenerWrapper wrapper2 : registration2.listeners) {
                        if (appContext.equals(wrapper2.getAppContext())) {
                            registration2.listeners.remove(wrapper2);
                            wrapper2.listener.notify(wrapper2.messageLayer, wrapper2.appContext);
                        }
                    }
                }
            }
            if (layer != null || appContext != null) {
                for (RegistrationContextImpl registration3 : this.defaultRegistration.values()) {
                    for (RegistrationListenerWrapper wrapper3 : registration3.listeners) {
                        if ((appContext != null && appContext.equals(wrapper3.getAppContext())) || (layer != null && layer.equals(wrapper3.getMessageLayer()))) {
                            registration3.listeners.remove(wrapper3);
                            wrapper3.listener.notify(wrapper3.messageLayer, wrapper3.appContext);
                        }
                    }
                }
                return;
            }
            return;
        }
        for (RegistrationListenerWrapper wrapper4 : previous.listeners) {
            previous.listeners.remove(wrapper4);
            wrapper4.listener.notify(wrapper4.messageLayer, wrapper4.appContext);
        }
    }

    @Override // jakarta.security.auth.message.config.AuthConfigFactory
    public boolean removeRegistration(String registrationID) {
        RegistrationContextImpl registration = null;
        if (DEFAULT_REGISTRATION_ID.equals(registrationID)) {
            registration = this.defaultRegistration.remove(registrationID);
        }
        if (registration == null) {
            registration = this.layerAppContextRegistrations.remove(registrationID);
        }
        if (registration == null) {
            registration = this.appContextRegistrations.remove(registrationID);
        }
        if (registration == null) {
            registration = this.layerRegistrations.remove(registrationID);
        }
        if (registration == null) {
            return false;
        }
        for (RegistrationListenerWrapper wrapper : registration.listeners) {
            wrapper.getListener().notify(wrapper.getMessageLayer(), wrapper.getAppContext());
        }
        if (registration.isPersistent()) {
            savePersistentRegistrations();
            return true;
        }
        return true;
    }

    @Override // jakarta.security.auth.message.config.AuthConfigFactory
    public String[] detachListener(RegistrationListener listener, String layer, String appContext) {
        String registrationID = getRegistrationID(layer, appContext);
        RegistrationContextImpl registrationContext = findRegistrationContextImpl(layer, appContext);
        if (registrationContext != null && registrationContext.removeListener(listener)) {
            return new String[]{registrationID};
        }
        return EMPTY_STRING_ARRAY;
    }

    @Override // jakarta.security.auth.message.config.AuthConfigFactory
    public String[] getRegistrationIDs(AuthConfigProvider provider) {
        List<String> result = new ArrayList<>();
        if (provider == null) {
            result.addAll(this.layerAppContextRegistrations.keySet());
            result.addAll(this.appContextRegistrations.keySet());
            result.addAll(this.layerRegistrations.keySet());
            if (!this.defaultRegistration.isEmpty()) {
                result.add(DEFAULT_REGISTRATION_ID);
            }
        } else {
            findProvider(provider, this.layerAppContextRegistrations, result);
            findProvider(provider, this.appContextRegistrations, result);
            findProvider(provider, this.layerRegistrations, result);
            findProvider(provider, this.defaultRegistration, result);
        }
        return (String[]) result.toArray(EMPTY_STRING_ARRAY);
    }

    private void findProvider(AuthConfigProvider provider, Map<String, RegistrationContextImpl> registrations, List<String> result) {
        for (Map.Entry<String, RegistrationContextImpl> entry : registrations.entrySet()) {
            if (provider.equals(entry.getValue().getProvider())) {
                result.add(entry.getKey());
            }
        }
    }

    @Override // jakarta.security.auth.message.config.AuthConfigFactory
    public AuthConfigFactory.RegistrationContext getRegistrationContext(String registrationID) {
        AuthConfigFactory.RegistrationContext result = this.defaultRegistration.get(registrationID);
        if (result == null) {
            result = this.layerAppContextRegistrations.get(registrationID);
        }
        if (result == null) {
            result = this.appContextRegistrations.get(registrationID);
        }
        if (result == null) {
            result = this.layerRegistrations.get(registrationID);
        }
        return result;
    }

    @Override // jakarta.security.auth.message.config.AuthConfigFactory
    public void refresh() {
        loadPersistentRegistrations();
    }

    @Override // jakarta.security.auth.message.config.AuthConfigFactory
    public String registerServerAuthModule(ServerAuthModule serverAuthModule, Object context) {
        if (context == null) {
            throw new IllegalArgumentException(sm.getString("authConfigFactoryImpl.nullContext"));
        }
        if (context instanceof ServletContext) {
            ServletContext servletContext = (ServletContext) context;
            String appContext = servletContext.getVirtualServerName() + " " + servletContext.getContextPath();
            ServerAuthContext serverAuthContext = new SingleModuleServerAuthContext(serverAuthModule);
            ServerAuthConfig serverAuthConfig = new SingleContextServerAuthConfig(serverAuthContext, appContext);
            AuthConfigProvider authConfigProvider = new SingleConfigAuthConfigProvider(serverAuthConfig);
            return registerConfigProvider(authConfigProvider, SERVLET_LAYER_ID, appContext, "");
        }
        throw new IllegalArgumentException(sm.getString("authConfigFactoryImpl.unsupportedContextType", context.getClass().getName()));
    }

    @Override // jakarta.security.auth.message.config.AuthConfigFactory
    public void removeServerAuthModule(Object context) {
        if (context == null) {
            throw new IllegalArgumentException(sm.getString("authConfigFactoryImpl.nullContext"));
        }
        if (context instanceof ServletContext) {
            ServletContext servletContext = (ServletContext) context;
            String appContextID = servletContext.getVirtualServerName() + " " + servletContext.getContextPath();
            removeRegistration(getRegistrationID(SERVLET_LAYER_ID, appContextID));
        }
        throw new IllegalArgumentException(sm.getString("authConfigFactoryImpl.unsupportedContextType", context.getClass().getName()));
    }

    private static String getRegistrationID(String layer, String appContext) {
        if (layer != null && layer.length() == 0) {
            throw new IllegalArgumentException(sm.getString("authConfigFactoryImpl.zeroLengthMessageLayer"));
        }
        if (appContext == null || appContext.length() != 0) {
            return (layer == null ? "" : layer) + ":" + (appContext == null ? "" : appContext);
        }
        throw new IllegalArgumentException(sm.getString("authConfigFactoryImpl.zeroLengthAppContext"));
    }

    private void loadPersistentRegistrations() {
        synchronized (CONFIG_FILE_LOCK) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(sm.getString("authConfigFactoryImpl.load", CONFIG_FILE.getAbsolutePath()));
            }
            if (CONFIG_FILE.isFile()) {
                PersistentProviderRegistrations.Providers providers = PersistentProviderRegistrations.loadProviders(CONFIG_FILE);
                for (PersistentProviderRegistrations.Provider provider : providers.getProviders()) {
                    doRegisterConfigProvider(provider.getClassName(), provider.getProperties(), provider.getLayer(), provider.getAppContext(), provider.getDescription());
                }
            }
        }
    }

    private void savePersistentRegistrations() {
        synchronized (CONFIG_FILE_LOCK) {
            PersistentProviderRegistrations.Providers providers = new PersistentProviderRegistrations.Providers();
            savePersistentProviders(providers, this.layerAppContextRegistrations);
            savePersistentProviders(providers, this.appContextRegistrations);
            savePersistentProviders(providers, this.layerRegistrations);
            savePersistentProviders(providers, this.defaultRegistration);
            PersistentProviderRegistrations.writeProviders(providers, CONFIG_FILE);
        }
    }

    private void savePersistentProviders(PersistentProviderRegistrations.Providers providers, Map<String, RegistrationContextImpl> registrations) {
        for (Map.Entry<String, RegistrationContextImpl> entry : registrations.entrySet()) {
            savePersistentProvider(providers, entry.getValue());
        }
    }

    private void savePersistentProvider(PersistentProviderRegistrations.Providers providers, RegistrationContextImpl registrationContextImpl) {
        if (registrationContextImpl != null && registrationContextImpl.isPersistent()) {
            PersistentProviderRegistrations.Provider provider = new PersistentProviderRegistrations.Provider();
            provider.setAppContext(registrationContextImpl.getAppContext());
            if (registrationContextImpl.getProvider() != null) {
                provider.setClassName(registrationContextImpl.getProvider().getClass().getName());
            }
            provider.setDescription(registrationContextImpl.getDescription());
            provider.setLayer(registrationContextImpl.getMessageLayer());
            for (Map.Entry<String, String> property : registrationContextImpl.getProperties().entrySet()) {
                provider.addProperty(property.getKey(), property.getValue());
            }
            providers.addProvider(provider);
        }
    }

    private RegistrationContextImpl findRegistrationContextImpl(String layer, String appContext) {
        RegistrationContextImpl result = this.layerAppContextRegistrations.get(getRegistrationID(layer, appContext));
        if (result == null) {
            result = this.appContextRegistrations.get(getRegistrationID(null, appContext));
        }
        if (result == null) {
            result = this.layerRegistrations.get(getRegistrationID(layer, null));
        }
        if (result == null) {
            result = this.defaultRegistration.get(DEFAULT_REGISTRATION_ID);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/jaspic/AuthConfigFactoryImpl$RegistrationContextImpl.class */
    public static class RegistrationContextImpl implements AuthConfigFactory.RegistrationContext {
        private final String messageLayer;
        private final String appContext;
        private final String description;
        private final boolean persistent;
        private final AuthConfigProvider provider;
        private final Map<String, String> properties;
        private final List<RegistrationListenerWrapper> listeners = new CopyOnWriteArrayList();

        private RegistrationContextImpl(String messageLayer, String appContext, String description, boolean persistent, AuthConfigProvider provider, Map<String, String> properties) {
            this.messageLayer = messageLayer;
            this.appContext = appContext;
            this.description = description;
            this.persistent = persistent;
            this.provider = provider;
            Map<String, String> propertiesCopy = new HashMap<>();
            if (properties != null) {
                propertiesCopy.putAll(properties);
            }
            this.properties = Collections.unmodifiableMap(propertiesCopy);
        }

        @Override // jakarta.security.auth.message.config.AuthConfigFactory.RegistrationContext
        public String getMessageLayer() {
            return this.messageLayer;
        }

        @Override // jakarta.security.auth.message.config.AuthConfigFactory.RegistrationContext
        public String getAppContext() {
            return this.appContext;
        }

        @Override // jakarta.security.auth.message.config.AuthConfigFactory.RegistrationContext
        public String getDescription() {
            return this.description;
        }

        @Override // jakarta.security.auth.message.config.AuthConfigFactory.RegistrationContext
        public boolean isPersistent() {
            return this.persistent;
        }

        private AuthConfigProvider getProvider() {
            return this.provider;
        }

        private void addListener(RegistrationListenerWrapper listener) {
            if (listener != null) {
                this.listeners.add(listener);
            }
        }

        private Map<String, String> getProperties() {
            return this.properties;
        }

        private boolean removeListener(RegistrationListener listener) {
            boolean result = false;
            for (RegistrationListenerWrapper wrapper : this.listeners) {
                if (wrapper.getListener().equals(listener)) {
                    this.listeners.remove(wrapper);
                    result = true;
                }
            }
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/jaspic/AuthConfigFactoryImpl$RegistrationListenerWrapper.class */
    public static class RegistrationListenerWrapper {
        private final String messageLayer;
        private final String appContext;
        private final RegistrationListener listener;

        RegistrationListenerWrapper(String messageLayer, String appContext, RegistrationListener listener) {
            this.messageLayer = messageLayer;
            this.appContext = appContext;
            this.listener = listener;
        }

        public String getMessageLayer() {
            return this.messageLayer;
        }

        public String getAppContext() {
            return this.appContext;
        }

        public RegistrationListener getListener() {
            return this.listener;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/jaspic/AuthConfigFactoryImpl$SingleModuleServerAuthContext.class */
    private static class SingleModuleServerAuthContext implements ServerAuthContext {
        private final ServerAuthModule module;

        SingleModuleServerAuthContext(ServerAuthModule module) {
            this.module = module;
        }

        @Override // jakarta.security.auth.message.ServerAuth
        public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {
            return this.module.validateRequest(messageInfo, clientSubject, serviceSubject);
        }

        @Override // jakarta.security.auth.message.ServerAuth
        public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
            return this.module.secureResponse(messageInfo, serviceSubject);
        }

        @Override // jakarta.security.auth.message.ServerAuth
        public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
            this.module.cleanSubject(messageInfo, subject);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/jaspic/AuthConfigFactoryImpl$SingleContextServerAuthConfig.class */
    private static class SingleContextServerAuthConfig implements ServerAuthConfig {
        private final ServerAuthContext context;
        private final String appContext;

        SingleContextServerAuthConfig(ServerAuthContext context, String appContext) {
            this.context = context;
            this.appContext = appContext;
        }

        @Override // jakarta.security.auth.message.config.AuthConfig
        public String getMessageLayer() {
            return AuthConfigFactoryImpl.SERVLET_LAYER_ID;
        }

        @Override // jakarta.security.auth.message.config.AuthConfig
        public String getAppContext() {
            return this.appContext;
        }

        @Override // jakarta.security.auth.message.config.AuthConfig
        public String getAuthContextID(MessageInfo messageInfo) {
            return messageInfo.toString();
        }

        @Override // jakarta.security.auth.message.config.AuthConfig
        public void refresh() {
        }

        @Override // jakarta.security.auth.message.config.AuthConfig
        public boolean isProtected() {
            return false;
        }

        @Override // jakarta.security.auth.message.config.ServerAuthConfig
        public ServerAuthContext getAuthContext(String authContextID, Subject serviceSubject, Map<String, Object> properties) throws AuthException {
            return this.context;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/jaspic/AuthConfigFactoryImpl$SingleConfigAuthConfigProvider.class */
    private static class SingleConfigAuthConfigProvider implements AuthConfigProvider {
        private final ServerAuthConfig serverAuthConfig;

        SingleConfigAuthConfigProvider(ServerAuthConfig serverAuthConfig) {
            this.serverAuthConfig = serverAuthConfig;
        }

        @Override // jakarta.security.auth.message.config.AuthConfigProvider
        public ClientAuthConfig getClientAuthConfig(String layer, String appContext, CallbackHandler handler) throws AuthException {
            throw new UnsupportedOperationException();
        }

        @Override // jakarta.security.auth.message.config.AuthConfigProvider
        public ServerAuthConfig getServerAuthConfig(String layer, String appContext, CallbackHandler handler) throws AuthException {
            return this.serverAuthConfig;
        }

        @Override // jakarta.security.auth.message.config.AuthConfigProvider
        public void refresh() {
        }
    }
}
