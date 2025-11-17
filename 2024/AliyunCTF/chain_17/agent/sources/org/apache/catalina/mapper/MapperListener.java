package org.apache.catalina.mapper;

import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerEvent;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Service;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.Wrapper;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/mapper/MapperListener.class */
public class MapperListener extends LifecycleMBeanBase implements ContainerListener, LifecycleListener {
    private final Mapper mapper;
    private final Service service;
    private final String domain = null;
    private static final Log log = LogFactory.getLog((Class<?>) MapperListener.class);
    private static final StringManager sm = StringManager.getManager(Constants.Package);

    public MapperListener(Service service) {
        this.service = service;
        this.mapper = service.getMapper();
    }

    @Override // org.apache.catalina.util.LifecycleBase
    public void startInternal() throws LifecycleException {
        setState(LifecycleState.STARTING);
        Engine engine = this.service.getContainer();
        if (engine == null) {
            return;
        }
        findDefaultHost();
        addListeners(engine);
        Container[] conHosts = engine.findChildren();
        for (Container conHost : conHosts) {
            Host host = (Host) conHost;
            if (!LifecycleState.NEW.equals(host.getState())) {
                registerHost(host);
            }
        }
    }

    @Override // org.apache.catalina.util.LifecycleBase
    public void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
        Engine engine = this.service.getContainer();
        if (engine == null) {
            return;
        }
        removeListeners(engine);
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getDomainInternal() {
        if (this.service instanceof LifecycleMBeanBase) {
            return this.service.getDomain();
        }
        return null;
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getObjectNameKeyProperties() {
        return "type=Mapper";
    }

    @Override // org.apache.catalina.ContainerListener
    public void containerEvent(ContainerEvent event) {
        if (Container.ADD_CHILD_EVENT.equals(event.getType())) {
            Container child = (Container) event.getData();
            addListeners(child);
            if (child.getState().isAvailable()) {
                if (child instanceof Host) {
                    registerHost((Host) child);
                    return;
                }
                if (child instanceof Context) {
                    registerContext((Context) child);
                    return;
                } else {
                    if ((child instanceof Wrapper) && child.getParent().getState().isAvailable()) {
                        registerWrapper((Wrapper) child);
                        return;
                    }
                    return;
                }
            }
            return;
        }
        if (Container.REMOVE_CHILD_EVENT.equals(event.getType())) {
            removeListeners((Container) event.getData());
            return;
        }
        if (Host.ADD_ALIAS_EVENT.equals(event.getType())) {
            this.mapper.addHostAlias(((Host) event.getSource()).getName(), event.getData().toString());
            return;
        }
        if (Host.REMOVE_ALIAS_EVENT.equals(event.getType())) {
            this.mapper.removeHostAlias(event.getData().toString());
            return;
        }
        if (Wrapper.ADD_MAPPING_EVENT.equals(event.getType())) {
            Wrapper wrapper = (Wrapper) event.getSource();
            Context context = (Context) wrapper.getParent();
            String contextPath = context.getPath();
            if ("/".equals(contextPath)) {
                contextPath = "";
            }
            String version = context.getWebappVersion();
            String hostName = context.getParent().getName();
            String wrapperName = wrapper.getName();
            String mapping = (String) event.getData();
            boolean jspWildCard = "jsp".equals(wrapperName) && mapping.endsWith("/*");
            this.mapper.addWrapper(hostName, contextPath, version, mapping, wrapper, jspWildCard, context.isResourceOnlyServlet(wrapperName));
            return;
        }
        if (Wrapper.REMOVE_MAPPING_EVENT.equals(event.getType())) {
            Context context2 = (Context) ((Wrapper) event.getSource()).getParent();
            String contextPath2 = context2.getPath();
            if ("/".equals(contextPath2)) {
                contextPath2 = "";
            }
            String version2 = context2.getWebappVersion();
            String hostName2 = context2.getParent().getName();
            this.mapper.removeWrapper(hostName2, contextPath2, version2, (String) event.getData());
            return;
        }
        if (Context.ADD_WELCOME_FILE_EVENT.equals(event.getType())) {
            Context context3 = (Context) event.getSource();
            String hostName3 = context3.getParent().getName();
            String contextPath3 = context3.getPath();
            if ("/".equals(contextPath3)) {
                contextPath3 = "";
            }
            String welcomeFile = (String) event.getData();
            this.mapper.addWelcomeFile(hostName3, contextPath3, context3.getWebappVersion(), welcomeFile);
            return;
        }
        if (Context.REMOVE_WELCOME_FILE_EVENT.equals(event.getType())) {
            Context context4 = (Context) event.getSource();
            String hostName4 = context4.getParent().getName();
            String contextPath4 = context4.getPath();
            if ("/".equals(contextPath4)) {
                contextPath4 = "";
            }
            String welcomeFile2 = (String) event.getData();
            this.mapper.removeWelcomeFile(hostName4, contextPath4, context4.getWebappVersion(), welcomeFile2);
            return;
        }
        if (Context.CLEAR_WELCOME_FILES_EVENT.equals(event.getType())) {
            Context context5 = (Context) event.getSource();
            String hostName5 = context5.getParent().getName();
            String contextPath5 = context5.getPath();
            if ("/".equals(contextPath5)) {
                contextPath5 = "";
            }
            this.mapper.clearWelcomeFiles(hostName5, contextPath5, context5.getWebappVersion());
        }
    }

    private void findDefaultHost() {
        Engine engine = this.service.getContainer();
        String defaultHost = engine.getDefaultHost();
        boolean found = false;
        if (defaultHost != null && defaultHost.length() > 0) {
            Container[] containers = engine.findChildren();
            int length = containers.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                Container container = containers[i];
                Host host = (Host) container;
                if (defaultHost.equalsIgnoreCase(host.getName())) {
                    found = true;
                    break;
                }
                String[] aliases = host.findAliases();
                int length2 = aliases.length;
                int i2 = 0;
                while (true) {
                    if (i2 < length2) {
                        String alias = aliases[i2];
                        if (!defaultHost.equalsIgnoreCase(alias)) {
                            i2++;
                        } else {
                            found = true;
                            break;
                        }
                    }
                }
                i++;
            }
        }
        if (found) {
            this.mapper.setDefaultHostName(defaultHost);
        } else {
            log.error(sm.getString("mapperListener.unknownDefaultHost", defaultHost, this.service));
        }
    }

    private void registerHost(Host host) {
        String[] aliases = host.findAliases();
        this.mapper.addHost(host.getName(), aliases, host);
        for (Container container : host.findChildren()) {
            if (container.getState().isAvailable()) {
                registerContext((Context) container);
            }
        }
        findDefaultHost();
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("mapperListener.registerHost", host.getName(), this.domain, this.service));
        }
    }

    private void unregisterHost(Host host) {
        String hostname = host.getName();
        this.mapper.removeHost(hostname);
        findDefaultHost();
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("mapperListener.unregisterHost", hostname, this.domain, this.service));
        }
    }

    private void unregisterWrapper(Wrapper wrapper) {
        Context context = (Context) wrapper.getParent();
        String contextPath = context.getPath();
        String wrapperName = wrapper.getName();
        if ("/".equals(contextPath)) {
            contextPath = "";
        }
        String version = context.getWebappVersion();
        String hostName = context.getParent().getName();
        String[] mappings = wrapper.findMappings();
        for (String mapping : mappings) {
            this.mapper.removeWrapper(hostName, contextPath, version, mapping);
        }
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("mapperListener.unregisterWrapper", wrapperName, contextPath, this.service));
        }
    }

    private void registerContext(Context context) {
        String contextPath = context.getPath();
        if ("/".equals(contextPath)) {
            contextPath = "";
        }
        Host host = (Host) context.getParent();
        WebResourceRoot resources = context.getResources();
        String[] welcomeFiles = context.findWelcomeFiles();
        List<WrapperMappingInfo> wrappers = new ArrayList<>();
        for (Container container : context.findChildren()) {
            prepareWrapperMappingInfo(context, (Wrapper) container, wrappers);
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("mapperListener.registerWrapper", container.getName(), contextPath, this.service));
            }
        }
        this.mapper.addContextVersion(host.getName(), host, contextPath, context.getWebappVersion(), context, welcomeFiles, resources, wrappers);
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("mapperListener.registerContext", contextPath, this.service));
        }
    }

    private void unregisterContext(Context context) {
        String contextPath = context.getPath();
        if ("/".equals(contextPath)) {
            contextPath = "";
        }
        String hostName = context.getParent().getName();
        if (context.getPaused()) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("mapperListener.pauseContext", contextPath, this.service));
            }
            this.mapper.pauseContextVersion(context, hostName, contextPath, context.getWebappVersion());
        } else {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("mapperListener.unregisterContext", contextPath, this.service));
            }
            this.mapper.removeContextVersion(context, hostName, contextPath, context.getWebappVersion());
        }
    }

    private void registerWrapper(Wrapper wrapper) {
        Context context = (Context) wrapper.getParent();
        String contextPath = context.getPath();
        if ("/".equals(contextPath)) {
            contextPath = "";
        }
        String version = context.getWebappVersion();
        String hostName = context.getParent().getName();
        List<WrapperMappingInfo> wrappers = new ArrayList<>();
        prepareWrapperMappingInfo(context, wrapper, wrappers);
        this.mapper.addWrappers(hostName, contextPath, version, wrappers);
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("mapperListener.registerWrapper", wrapper.getName(), contextPath, this.service));
        }
    }

    private void prepareWrapperMappingInfo(Context context, Wrapper wrapper, List<WrapperMappingInfo> wrappers) {
        String wrapperName = wrapper.getName();
        boolean resourceOnly = context.isResourceOnlyServlet(wrapperName);
        String[] mappings = wrapper.findMappings();
        for (String mapping : mappings) {
            boolean jspWildCard = wrapperName.equals("jsp") && mapping.endsWith("/*");
            wrappers.add(new WrapperMappingInfo(mapping, wrapper, jspWildCard, resourceOnly));
        }
    }

    @Override // org.apache.catalina.LifecycleListener
    public void lifecycleEvent(LifecycleEvent event) {
        if (!event.getType().equals(Lifecycle.AFTER_START_EVENT)) {
            if (event.getType().equals(Lifecycle.BEFORE_STOP_EVENT)) {
                Object obj = event.getSource();
                if (obj instanceof Wrapper) {
                    unregisterWrapper((Wrapper) obj);
                    return;
                } else if (obj instanceof Context) {
                    unregisterContext((Context) obj);
                    return;
                } else {
                    if (obj instanceof Host) {
                        unregisterHost((Host) obj);
                        return;
                    }
                    return;
                }
            }
            return;
        }
        Object obj2 = event.getSource();
        if (obj2 instanceof Wrapper) {
            Wrapper w = (Wrapper) obj2;
            if (w.getParent().getState().isAvailable()) {
                registerWrapper(w);
                return;
            }
            return;
        }
        if (!(obj2 instanceof Context)) {
            if (obj2 instanceof Host) {
                registerHost((Host) obj2);
            }
        } else {
            Context c = (Context) obj2;
            if (c.getParent().getState().isAvailable()) {
                registerContext(c);
            }
        }
    }

    private void addListeners(Container container) {
        container.addContainerListener(this);
        container.addLifecycleListener(this);
        for (Container child : container.findChildren()) {
            addListeners(child);
        }
    }

    private void removeListeners(Container container) {
        container.removeContainerListener(this);
        container.removeLifecycleListener(this);
        for (Container child : container.findChildren()) {
            removeListeners(child);
        }
    }
}
