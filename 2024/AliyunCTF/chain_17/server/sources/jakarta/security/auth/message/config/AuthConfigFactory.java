package jakarta.security.auth.message.config;

import jakarta.security.auth.message.module.ServerAuthModule;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedActionException;
import java.security.Security;
import java.security.SecurityPermission;
import java.util.Map;
import org.apache.catalina.authenticator.jaspic.AuthConfigFactoryImpl;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/config/AuthConfigFactory.class */
public abstract class AuthConfigFactory {
    public static final String DEFAULT_FACTORY_SECURITY_PROPERTY = "authconfigprovider.factory";
    private static final String DEFAULT_JASPI_AUTHCONFIGFACTORYIMPL = "org.apache.catalina.authenticator.jaspic.AuthConfigFactoryImpl";
    private static volatile AuthConfigFactory factory;
    public static final String GET_FACTORY_PERMISSION_NAME = "getProperty.authconfigprovider.factory";

    @Deprecated(forRemoval = true)
    public static final SecurityPermission getFactorySecurityPermission = new SecurityPermission(GET_FACTORY_PERMISSION_NAME);
    public static final String SET_FACTORY_PERMISSION_NAME = "setProperty.authconfigprovider.factory";

    @Deprecated(forRemoval = true)
    public static final SecurityPermission setFactorySecurityPermission = new SecurityPermission(SET_FACTORY_PERMISSION_NAME);
    public static final String PROVIDER_REGISTRATION_PERMISSION_NAME = "setProperty.authconfigfactory.provider";

    @Deprecated(forRemoval = true)
    public static final SecurityPermission providerRegistrationSecurityPermission = new SecurityPermission(PROVIDER_REGISTRATION_PERMISSION_NAME);

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/config/AuthConfigFactory$RegistrationContext.class */
    public interface RegistrationContext {
        String getMessageLayer();

        String getAppContext();

        String getDescription();

        boolean isPersistent();
    }

    public abstract AuthConfigProvider getConfigProvider(String str, String str2, RegistrationListener registrationListener);

    public abstract String registerConfigProvider(String str, Map<String, String> map, String str2, String str3, String str4);

    public abstract String registerConfigProvider(AuthConfigProvider authConfigProvider, String str, String str2, String str3);

    public abstract boolean removeRegistration(String str);

    public abstract String[] detachListener(RegistrationListener registrationListener, String str, String str2);

    public abstract String[] getRegistrationIDs(AuthConfigProvider authConfigProvider);

    public abstract RegistrationContext getRegistrationContext(String str);

    public abstract void refresh();

    public abstract String registerServerAuthModule(ServerAuthModule serverAuthModule, Object obj);

    public abstract void removeServerAuthModule(Object obj);

    public static AuthConfigFactory getFactory() {
        checkPermission(getFactorySecurityPermission);
        if (factory != null) {
            return factory;
        }
        synchronized (AuthConfigFactory.class) {
            if (factory == null) {
                String className = getFactoryClassName();
                try {
                    factory = (AuthConfigFactory) AccessController.doPrivileged(() -> {
                        if (className.equals(DEFAULT_JASPI_AUTHCONFIGFACTORYIMPL)) {
                            return new AuthConfigFactoryImpl();
                        }
                        Class<?> clazz = Class.forName(className);
                        return (AuthConfigFactory) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                    });
                } catch (PrivilegedActionException e) {
                    Exception inner = e.getException();
                    if (inner instanceof InstantiationException) {
                        throw new SecurityException("AuthConfigFactory error:" + inner.getCause().getMessage(), inner.getCause());
                    }
                    throw new SecurityException("AuthConfigFactory error: " + inner, inner);
                }
            }
        }
        return factory;
    }

    public static synchronized void setFactory(AuthConfigFactory factory2) {
        checkPermission(setFactorySecurityPermission);
        factory = factory2;
    }

    @Deprecated(forRemoval = true)
    private static void checkPermission(Permission permission) {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(permission);
        }
    }

    private static String getFactoryClassName() {
        String className = (String) AccessController.doPrivileged(() -> {
            return Security.getProperty(DEFAULT_FACTORY_SECURITY_PROPERTY);
        });
        if (className != null) {
            return className;
        }
        return DEFAULT_JASPI_AUTHCONFIGFACTORYIMPL;
    }
}
