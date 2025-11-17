package org.apache.catalina.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.catalina.AccessLog;
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
import org.apache.catalina.Realm;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.realm.NullRealm;
import org.apache.catalina.util.ServerInfo;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/StandardEngine.class */
public class StandardEngine extends ContainerBase implements Engine {
    private static final Log log = LogFactory.getLog((Class<?>) StandardEngine.class);
    private String jvmRouteId;
    private String defaultHost = null;
    private Service service = null;
    private final AtomicReference<AccessLog> defaultAccessLog = new AtomicReference<>();

    public StandardEngine() {
        this.pipeline.setBasic(new StandardEngineValve());
        this.backgroundProcessorDelay = 10;
    }

    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.Container
    public Realm getRealm() {
        Realm configured = super.getRealm();
        if (configured == null) {
            configured = new NullRealm();
            setRealm(configured);
        }
        return configured;
    }

    @Override // org.apache.catalina.Engine
    public String getDefaultHost() {
        return this.defaultHost;
    }

    @Override // org.apache.catalina.Engine
    public void setDefaultHost(String host) {
        String oldDefaultHost = this.defaultHost;
        if (host == null) {
            this.defaultHost = null;
        } else {
            this.defaultHost = host.toLowerCase(Locale.ENGLISH);
        }
        if (getState().isAvailable()) {
            this.service.getMapper().setDefaultHostName(host);
        }
        this.support.firePropertyChange("defaultHost", oldDefaultHost, this.defaultHost);
    }

    @Override // org.apache.catalina.Engine
    public void setJvmRoute(String routeId) {
        this.jvmRouteId = routeId;
    }

    @Override // org.apache.catalina.Engine
    public String getJvmRoute() {
        return this.jvmRouteId;
    }

    @Override // org.apache.catalina.Engine
    public Service getService() {
        return this.service;
    }

    @Override // org.apache.catalina.Engine
    public void setService(Service service) {
        this.service = service;
    }

    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.Container
    public void addChild(Container child) {
        if (!(child instanceof Host)) {
            throw new IllegalArgumentException(sm.getString("standardEngine.notHost"));
        }
        super.addChild(child);
    }

    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.Container
    public void setParent(Container container) {
        throw new IllegalArgumentException(sm.getString("standardEngine.notParent"));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
        getRealm();
        super.initInternal();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        if (log.isInfoEnabled()) {
            log.info(sm.getString("standardEngine.start", ServerInfo.getServerInfo()));
        }
        super.startInternal();
    }

    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.Container
    public void logAccess(Request request, Response response, long time, boolean useDefault) {
        boolean logged = false;
        if (getAccessLog() != null) {
            this.accessLog.log(request, response, time);
            logged = true;
        }
        if (!logged && useDefault) {
            AccessLog newDefaultAccessLog = this.defaultAccessLog.get();
            if (newDefaultAccessLog == null) {
                Host host = (Host) findChild(getDefaultHost());
                Context context = null;
                if (host != null && host.getState().isAvailable()) {
                    newDefaultAccessLog = host.getAccessLog();
                    if (newDefaultAccessLog != null) {
                        if (this.defaultAccessLog.compareAndSet(null, newDefaultAccessLog)) {
                            AccessLogListener l = new AccessLogListener(this, host, null);
                            l.install();
                        }
                    } else {
                        context = (Context) host.findChild("");
                        if (context != null && context.getState().isAvailable()) {
                            newDefaultAccessLog = context.getAccessLog();
                            if (newDefaultAccessLog != null && this.defaultAccessLog.compareAndSet(null, newDefaultAccessLog)) {
                                AccessLogListener l2 = new AccessLogListener(this, null, context);
                                l2.install();
                            }
                        }
                    }
                }
                if (newDefaultAccessLog == null) {
                    newDefaultAccessLog = new NoopAccessLog();
                    if (this.defaultAccessLog.compareAndSet(null, newDefaultAccessLog)) {
                        AccessLogListener l3 = new AccessLogListener(this, host, context);
                        l3.install();
                    }
                }
            }
            newDefaultAccessLog.log(request, response, time);
        }
    }

    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.Container
    public ClassLoader getParentClassLoader() {
        if (this.parentClassLoader != null) {
            return this.parentClassLoader;
        }
        if (this.service != null) {
            return this.service.getParentClassLoader();
        }
        return ClassLoader.getSystemClassLoader();
    }

    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.Container
    public File getCatalinaBase() {
        Server s;
        File base;
        if (this.service != null && (s = this.service.getServer()) != null && (base = s.getCatalinaBase()) != null) {
            return base;
        }
        return super.getCatalinaBase();
    }

    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.Container
    public File getCatalinaHome() {
        Server s;
        File base;
        if (this.service != null && (s = this.service.getServer()) != null && (base = s.getCatalinaHome()) != null) {
            return base;
        }
        return super.getCatalinaHome();
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getObjectNameKeyProperties() {
        return "type=Engine";
    }

    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.util.LifecycleMBeanBase
    protected String getDomainInternal() {
        return getName();
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/StandardEngine$NoopAccessLog.class */
    protected static final class NoopAccessLog implements AccessLog {
        protected NoopAccessLog() {
        }

        @Override // org.apache.catalina.AccessLog
        public void log(Request request, Response response, long time) {
        }

        @Override // org.apache.catalina.AccessLog
        public void setRequestAttributesEnabled(boolean requestAttributesEnabled) {
        }

        @Override // org.apache.catalina.AccessLog
        public boolean getRequestAttributesEnabled() {
            return false;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/StandardEngine$AccessLogListener.class */
    protected static final class AccessLogListener implements PropertyChangeListener, LifecycleListener, ContainerListener {
        private final StandardEngine engine;
        private final Host host;
        private final Context context;
        private volatile boolean disabled = false;

        public AccessLogListener(StandardEngine engine, Host host, Context context) {
            this.engine = engine;
            this.host = host;
            this.context = context;
        }

        public void install() {
            this.engine.addPropertyChangeListener(this);
            if (this.host != null) {
                this.host.addContainerListener(this);
                this.host.addLifecycleListener(this);
            }
            if (this.context != null) {
                this.context.addLifecycleListener(this);
            }
        }

        private void uninstall() {
            this.disabled = true;
            if (this.context != null) {
                this.context.removeLifecycleListener(this);
            }
            if (this.host != null) {
                this.host.removeLifecycleListener(this);
                this.host.removeContainerListener(this);
            }
            this.engine.removePropertyChangeListener(this);
        }

        @Override // org.apache.catalina.LifecycleListener
        public void lifecycleEvent(LifecycleEvent event) {
            if (this.disabled) {
                return;
            }
            String type = event.getType();
            if (Lifecycle.AFTER_START_EVENT.equals(type) || Lifecycle.BEFORE_STOP_EVENT.equals(type) || Lifecycle.BEFORE_DESTROY_EVENT.equals(type)) {
                this.engine.defaultAccessLog.set(null);
                uninstall();
            }
        }

        @Override // java.beans.PropertyChangeListener
        public void propertyChange(PropertyChangeEvent evt) {
            if (!this.disabled && "defaultHost".equals(evt.getPropertyName())) {
                this.engine.defaultAccessLog.set(null);
                uninstall();
            }
        }

        @Override // org.apache.catalina.ContainerListener
        public void containerEvent(ContainerEvent event) {
            if (!this.disabled && Container.ADD_CHILD_EVENT.equals(event.getType())) {
                Context context = (Context) event.getData();
                if (context.getPath().isEmpty()) {
                    this.engine.defaultAccessLog.set(null);
                    uninstall();
                }
            }
        }
    }
}
