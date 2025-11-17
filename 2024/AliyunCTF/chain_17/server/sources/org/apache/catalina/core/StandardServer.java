package org.apache.catalina.core;

import ch.qos.logback.core.spi.AbstractComponentTracker;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.AccessControlException;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.catalina.mbeans.MBeanFactory;
import org.apache.catalina.startup.Catalina;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.catalina.util.ServerInfo;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.StringCache;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.threads.TaskThreadFactory;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/StandardServer.class */
public final class StandardServer extends LifecycleMBeanBase implements Server {
    private static final Log log = LogFactory.getLog((Class<?>) StandardServer.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) StandardServer.class);
    private NamingResourcesImpl globalNamingResources;
    private final NamingContextListener namingContextListener;
    private ScheduledFuture<?> monitorFuture;
    private ObjectName onameStringCache;
    private ObjectName onameMBeanFactory;
    private Context globalNamingContext = null;
    private int port = 8005;
    private int portOffset = 0;
    private String address = "localhost";
    private Random random = null;
    private Service[] services = new Service[0];
    private final Object servicesLock = new Object();
    private String shutdown = "SHUTDOWN";
    final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private volatile boolean stopAwait = false;
    private Catalina catalina = null;
    private ClassLoader parentClassLoader = null;
    private volatile Thread awaitThread = null;
    private volatile ServerSocket awaitSocket = null;
    private File catalinaHome = null;
    private File catalinaBase = null;
    private final Object namingToken = new Object();
    private int utilityThreads = 2;
    private boolean utilityThreadsAsDaemon = false;
    private ScheduledThreadPoolExecutor utilityExecutor = null;
    private final Object utilityExecutorLock = new Object();
    private ScheduledExecutorService utilityExecutorWrapper = null;
    private ScheduledFuture<?> periodicLifecycleEventFuture = null;
    private int periodicEventDelay = 10;

    public StandardServer() {
        this.globalNamingResources = null;
        this.globalNamingResources = new NamingResourcesImpl();
        this.globalNamingResources.setContainer(this);
        if (isUseNaming()) {
            this.namingContextListener = new NamingContextListener();
            addLifecycleListener(this.namingContextListener);
        } else {
            this.namingContextListener = null;
        }
    }

    @Override // org.apache.catalina.Server
    public Object getNamingToken() {
        return this.namingToken;
    }

    @Override // org.apache.catalina.Server
    public Context getGlobalNamingContext() {
        return this.globalNamingContext;
    }

    public void setGlobalNamingContext(Context globalNamingContext) {
        this.globalNamingContext = globalNamingContext;
    }

    @Override // org.apache.catalina.Server
    public NamingResourcesImpl getGlobalNamingResources() {
        return this.globalNamingResources;
    }

    @Override // org.apache.catalina.Server
    public void setGlobalNamingResources(NamingResourcesImpl globalNamingResources) {
        NamingResourcesImpl oldGlobalNamingResources = this.globalNamingResources;
        this.globalNamingResources = globalNamingResources;
        this.globalNamingResources.setContainer(this);
        this.support.firePropertyChange("globalNamingResources", oldGlobalNamingResources, this.globalNamingResources);
    }

    public String getServerInfo() {
        return ServerInfo.getServerInfo();
    }

    public String getServerBuilt() {
        return ServerInfo.getServerBuilt();
    }

    public String getServerNumber() {
        return ServerInfo.getServerNumber();
    }

    @Override // org.apache.catalina.Server
    public int getPort() {
        return this.port;
    }

    @Override // org.apache.catalina.Server
    public void setPort(int port) {
        this.port = port;
    }

    @Override // org.apache.catalina.Server
    public int getPortOffset() {
        return this.portOffset;
    }

    @Override // org.apache.catalina.Server
    public void setPortOffset(int portOffset) {
        if (portOffset < 0) {
            throw new IllegalArgumentException(sm.getString("standardServer.portOffset.invalid", Integer.valueOf(portOffset)));
        }
        this.portOffset = portOffset;
    }

    @Override // org.apache.catalina.Server
    public int getPortWithOffset() {
        int port = getPort();
        if (port > 0) {
            return port + getPortOffset();
        }
        return port;
    }

    @Override // org.apache.catalina.Server
    public String getAddress() {
        return this.address;
    }

    @Override // org.apache.catalina.Server
    public void setAddress(String address) {
        this.address = address;
    }

    @Override // org.apache.catalina.Server
    public String getShutdown() {
        return this.shutdown;
    }

    @Override // org.apache.catalina.Server
    public void setShutdown(String shutdown) {
        this.shutdown = shutdown;
    }

    @Override // org.apache.catalina.Server
    public Catalina getCatalina() {
        return this.catalina;
    }

    @Override // org.apache.catalina.Server
    public void setCatalina(Catalina catalina) {
        this.catalina = catalina;
    }

    @Override // org.apache.catalina.Server
    public int getUtilityThreads() {
        return this.utilityThreads;
    }

    private static int getUtilityThreadsInternal(int utilityThreads) {
        int result = utilityThreads;
        if (result <= 0) {
            result = Runtime.getRuntime().availableProcessors() + result;
            if (result < 2) {
                result = 2;
            }
        }
        return result;
    }

    @Override // org.apache.catalina.Server
    public void setUtilityThreads(int utilityThreads) {
        int oldUtilityThreads = this.utilityThreads;
        if (getUtilityThreadsInternal(utilityThreads) < getUtilityThreadsInternal(oldUtilityThreads)) {
            return;
        }
        this.utilityThreads = utilityThreads;
        synchronized (this.utilityExecutorLock) {
            if (oldUtilityThreads != utilityThreads) {
                if (this.utilityExecutor != null) {
                    reconfigureUtilityExecutor(getUtilityThreadsInternal(utilityThreads));
                }
            }
        }
    }

    private void reconfigureUtilityExecutor(int threads) {
        if (this.utilityExecutor != null) {
            this.utilityExecutor.setCorePoolSize(threads);
            return;
        }
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(threads, new TaskThreadFactory("Catalina-utility-", this.utilityThreadsAsDaemon, 1));
        scheduledThreadPoolExecutor.setKeepAliveTime(10L, TimeUnit.SECONDS);
        scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
        scheduledThreadPoolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        this.utilityExecutor = scheduledThreadPoolExecutor;
        this.utilityExecutorWrapper = new org.apache.tomcat.util.threads.ScheduledThreadPoolExecutor(this.utilityExecutor);
    }

    public boolean getUtilityThreadsAsDaemon() {
        return this.utilityThreadsAsDaemon;
    }

    public void setUtilityThreadsAsDaemon(boolean utilityThreadsAsDaemon) {
        this.utilityThreadsAsDaemon = utilityThreadsAsDaemon;
    }

    public int getPeriodicEventDelay() {
        return this.periodicEventDelay;
    }

    public void setPeriodicEventDelay(int periodicEventDelay) {
        this.periodicEventDelay = periodicEventDelay;
    }

    @Override // org.apache.catalina.Server
    public void addService(Service service) {
        service.setServer(this);
        synchronized (this.servicesLock) {
            Service[] results = new Service[this.services.length + 1];
            System.arraycopy(this.services, 0, results, 0, this.services.length);
            results[this.services.length] = service;
            this.services = results;
            if (getState().isAvailable()) {
                try {
                    service.start();
                } catch (LifecycleException e) {
                }
            }
            this.support.firePropertyChange("service", (Object) null, service);
        }
    }

    public void stopAwait() {
        this.stopAwait = true;
        Thread t = this.awaitThread;
        if (t != null) {
            ServerSocket s = this.awaitSocket;
            if (s != null) {
                this.awaitSocket = null;
                try {
                    s.close();
                } catch (IOException e) {
                }
            }
            t.interrupt();
            try {
                t.join(1000L);
            } catch (InterruptedException e2) {
            }
        }
    }

    /* JADX WARN: Finally extract failed */
    @Override // org.apache.catalina.Server
    public void await() {
        ServerSocket serverSocket;
        boolean match;
        int ch2;
        if (getPortWithOffset() == -2) {
            return;
        }
        Thread currentThread = Thread.currentThread();
        if (getPortWithOffset() == -1) {
            try {
                this.awaitThread = currentThread;
                while (!this.stopAwait) {
                    try {
                        Thread.sleep(AbstractComponentTracker.LINGERING_TIMEOUT);
                    } catch (InterruptedException e) {
                    }
                }
                this.awaitThread = null;
                return;
            } catch (Throwable th) {
                this.awaitThread = null;
                throw th;
            }
        }
        try {
            this.awaitSocket = new ServerSocket(getPortWithOffset(), 1, InetAddress.getByName(this.address));
            try {
                this.awaitThread = currentThread;
                while (true) {
                    if (this.stopAwait || (serverSocket = this.awaitSocket) == null) {
                        break;
                    }
                    Socket socket = null;
                    StringBuilder command = new StringBuilder();
                    try {
                        long acceptStartTime = System.currentTimeMillis();
                        try {
                            socket = serverSocket.accept();
                            socket.setSoTimeout(10000);
                            InputStream stream = socket.getInputStream();
                            int expected = 1024;
                            while (expected < this.shutdown.length()) {
                                if (this.random == null) {
                                    this.random = new Random();
                                }
                                expected += this.random.nextInt() % 1024;
                            }
                            while (expected > 0) {
                                try {
                                    ch2 = stream.read();
                                } catch (IOException e2) {
                                    log.warn(sm.getString("standardServer.accept.readError"), e2);
                                    ch2 = -1;
                                }
                                if (ch2 < 32 || ch2 == 127) {
                                    break;
                                }
                                command.append((char) ch2);
                                expected--;
                            }
                            if (socket != null) {
                                try {
                                    socket.close();
                                } catch (IOException e3) {
                                }
                            }
                            match = command.toString().equals(this.shutdown);
                        } catch (SocketTimeoutException ste) {
                            log.warn(sm.getString("standardServer.accept.timeout", Long.valueOf(System.currentTimeMillis() - acceptStartTime)), ste);
                            if (socket != null) {
                                try {
                                    socket.close();
                                } catch (IOException e4) {
                                }
                            }
                        } catch (IOException e5) {
                            if (!this.stopAwait) {
                                log.error(sm.getString("standardServer.accept.error"), e5);
                                if (socket != null) {
                                    try {
                                        socket.close();
                                    } catch (IOException e6) {
                                    }
                                }
                            }
                        } catch (AccessControlException ace) {
                            log.warn(sm.getString("standardServer.accept.security"), ace);
                            if (socket != null) {
                                try {
                                    socket.close();
                                } catch (IOException e7) {
                                }
                            }
                        }
                        if (match) {
                            log.info(sm.getString("standardServer.shutdownViaPort"));
                            break;
                        }
                        log.warn(sm.getString("standardServer.invalidShutdownCommand", command.toString()));
                    } finally {
                        if (socket != null) {
                            try {
                                socket.close();
                            } catch (IOException e8) {
                            }
                        }
                    }
                }
                ServerSocket serverSocket2 = this.awaitSocket;
                this.awaitThread = null;
                this.awaitSocket = null;
                if (serverSocket2 != null) {
                    try {
                        serverSocket2.close();
                    } catch (IOException e9) {
                    }
                }
            } catch (Throwable th2) {
                ServerSocket serverSocket3 = this.awaitSocket;
                this.awaitThread = null;
                this.awaitSocket = null;
                if (serverSocket3 != null) {
                    try {
                        serverSocket3.close();
                    } catch (IOException e10) {
                    }
                }
                throw th2;
            }
        } catch (IOException e11) {
            log.error(sm.getString("standardServer.awaitSocket.fail", this.address, String.valueOf(getPortWithOffset()), String.valueOf(getPort()), String.valueOf(getPortOffset())), e11);
        }
    }

    @Override // org.apache.catalina.Server
    public Service findService(String name) {
        if (name == null) {
            return null;
        }
        synchronized (this.servicesLock) {
            for (Service service : this.services) {
                if (name.equals(service.getName())) {
                    return service;
                }
            }
            return null;
        }
    }

    @Override // org.apache.catalina.Server
    public Service[] findServices() {
        return this.services;
    }

    public ObjectName[] getServiceNames() {
        ObjectName[] onames = new ObjectName[this.services.length];
        for (int i = 0; i < this.services.length; i++) {
            onames[i] = ((StandardService) this.services[i]).getObjectName();
        }
        return onames;
    }

    @Override // org.apache.catalina.Server
    public void removeService(Service service) {
        synchronized (this.servicesLock) {
            int j = -1;
            int i = 0;
            while (true) {
                if (i >= this.services.length) {
                    break;
                }
                if (service != this.services[i]) {
                    i++;
                } else {
                    j = i;
                    break;
                }
            }
            if (j < 0) {
                return;
            }
            try {
                this.services[j].stop();
            } catch (LifecycleException e) {
            }
            int k = 0;
            Service[] results = new Service[this.services.length - 1];
            for (int i2 = 0; i2 < this.services.length; i2++) {
                if (i2 != j) {
                    int i3 = k;
                    k++;
                    results[i3] = this.services[i2];
                }
            }
            this.services = results;
            this.support.firePropertyChange("service", service, (Object) null);
        }
    }

    @Override // org.apache.catalina.Server
    public File getCatalinaBase() {
        if (this.catalinaBase != null) {
            return this.catalinaBase;
        }
        this.catalinaBase = getCatalinaHome();
        return this.catalinaBase;
    }

    @Override // org.apache.catalina.Server
    public void setCatalinaBase(File catalinaBase) {
        this.catalinaBase = catalinaBase;
    }

    @Override // org.apache.catalina.Server
    public File getCatalinaHome() {
        return this.catalinaHome;
    }

    @Override // org.apache.catalina.Server
    public void setCatalinaHome(File catalinaHome) {
        this.catalinaHome = catalinaHome;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    public String toString() {
        return "StandardServer[" + getPort() + "]";
    }

    public synchronized void storeConfig() throws InstanceNotFoundException, MBeanException {
        try {
            ObjectName sname = new ObjectName("Catalina:type=StoreConfig");
            MBeanServer server = Registry.getRegistry(null, null).getMBeanServer();
            if (server.isRegistered(sname)) {
                server.invoke(sname, "storeConfig", (Object[]) null, (String[]) null);
            } else {
                log.error(sm.getString("standardServer.storeConfig.notAvailable", sname));
            }
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            log.error(sm.getString("standardServer.storeConfig.error"), t);
        }
    }

    public synchronized void storeContext(org.apache.catalina.Context context) throws InstanceNotFoundException, MBeanException {
        try {
            ObjectName sname = new ObjectName("Catalina:type=StoreConfig");
            MBeanServer server = Registry.getRegistry(null, null).getMBeanServer();
            if (server.isRegistered(sname)) {
                server.invoke(sname, "store", new Object[]{context}, new String[]{"java.lang.String"});
            } else {
                log.error(sm.getString("standardServer.storeConfig.notAvailable", sname));
            }
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            log.error(sm.getString("standardServer.storeConfig.contextError", context.getName()), t);
        }
    }

    private boolean isUseNaming() {
        boolean useNaming = true;
        String useNamingProperty = System.getProperty("catalina.useNaming");
        if (useNamingProperty != null && useNamingProperty.equals("false")) {
            useNaming = false;
        }
        return useNaming;
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void startInternal() throws LifecycleException {
        fireLifecycleEvent(Lifecycle.CONFIGURE_START_EVENT, null);
        setState(LifecycleState.STARTING);
        synchronized (this.utilityExecutorLock) {
            reconfigureUtilityExecutor(getUtilityThreadsInternal(this.utilityThreads));
            register(this.utilityExecutor, "type=UtilityExecutor");
        }
        this.globalNamingResources.start();
        synchronized (this.servicesLock) {
            for (Service service : this.services) {
                service.start();
            }
        }
        if (this.periodicEventDelay > 0) {
            this.monitorFuture = getUtilityExecutor().scheduleWithFixedDelay(this::startPeriodicLifecycleEvent, 0L, 60L, TimeUnit.SECONDS);
        }
    }

    private void startPeriodicLifecycleEvent() {
        if (this.periodicLifecycleEventFuture == null || this.periodicLifecycleEventFuture.isDone()) {
            if (this.periodicLifecycleEventFuture != null && this.periodicLifecycleEventFuture.isDone()) {
                try {
                    this.periodicLifecycleEventFuture.get();
                } catch (InterruptedException | ExecutionException e) {
                    log.error(sm.getString("standardServer.periodicEventError"), e);
                }
            }
            this.periodicLifecycleEventFuture = getUtilityExecutor().scheduleAtFixedRate(() -> {
                fireLifecycleEvent(Lifecycle.PERIODIC_EVENT, null);
            }, this.periodicEventDelay, this.periodicEventDelay, TimeUnit.SECONDS);
        }
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
        if (this.monitorFuture != null) {
            this.monitorFuture.cancel(true);
            this.monitorFuture = null;
        }
        if (this.periodicLifecycleEventFuture != null) {
            this.periodicLifecycleEventFuture.cancel(false);
            this.periodicLifecycleEventFuture = null;
        }
        fireLifecycleEvent(Lifecycle.CONFIGURE_STOP_EVENT, null);
        for (Service service : this.services) {
            service.stop();
        }
        synchronized (this.utilityExecutorLock) {
            if (this.utilityExecutor != null) {
                this.utilityExecutor.shutdownNow();
                unregister("type=UtilityExecutor");
                this.utilityExecutor = null;
            }
        }
        this.globalNamingResources.stop();
        stopAwait();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
        super.initInternal();
        this.onameStringCache = register(new StringCache(), "type=StringCache");
        MBeanFactory factory = new MBeanFactory();
        factory.setContainer(this);
        this.onameMBeanFactory = register(factory, "type=MBeanFactory");
        this.globalNamingResources.init();
        for (Service service : this.services) {
            service.init();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void destroyInternal() throws LifecycleException {
        for (Service service : this.services) {
            service.destroy();
        }
        this.globalNamingResources.destroy();
        unregister(this.onameMBeanFactory);
        unregister(this.onameStringCache);
        super.destroyInternal();
    }

    @Override // org.apache.catalina.Server
    public ClassLoader getParentClassLoader() {
        if (this.parentClassLoader != null) {
            return this.parentClassLoader;
        }
        if (this.catalina != null) {
            return this.catalina.getParentClassLoader();
        }
        return ClassLoader.getSystemClassLoader();
    }

    @Override // org.apache.catalina.Server
    public void setParentClassLoader(ClassLoader parent) {
        ClassLoader oldParentClassLoader = this.parentClassLoader;
        this.parentClassLoader = parent;
        this.support.firePropertyChange("parentClassLoader", oldParentClassLoader, this.parentClassLoader);
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getDomainInternal() {
        Service service;
        String domain = null;
        Service[] services = findServices();
        if (services.length > 0 && (service = services[0]) != null) {
            domain = service.getDomain();
        }
        return domain;
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getObjectNameKeyProperties() {
        return "type=Server";
    }

    @Override // org.apache.catalina.Server
    public ScheduledExecutorService getUtilityExecutor() {
        return this.utilityExecutorWrapper;
    }
}
