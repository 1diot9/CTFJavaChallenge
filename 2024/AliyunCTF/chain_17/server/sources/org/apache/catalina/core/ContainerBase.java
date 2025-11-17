package org.apache.catalina.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.management.ObjectName;
import org.apache.catalina.AccessLog;
import org.apache.catalina.Cluster;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerEvent;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Loader;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Realm;
import org.apache.catalina.Server;
import org.apache.catalina.Valve;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.ContextName;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.MultiThrowable;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.threads.InlineExecutorService;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ContainerBase.class */
public abstract class ContainerBase extends LifecycleMBeanBase implements Container {
    protected ScheduledFuture<?> backgroundProcessorFuture;
    protected ScheduledFuture<?> monitorFuture;
    protected ExecutorService startStopExecutor;
    private static final Log log = LogFactory.getLog((Class<?>) ContainerBase.class);
    protected static final StringManager sm = StringManager.getManager((Class<?>) ContainerBase.class);
    protected final HashMap<String, Container> children = new HashMap<>();
    protected int backgroundProcessorDelay = -1;
    protected final List<ContainerListener> listeners = new CopyOnWriteArrayList();
    protected Log logger = null;
    protected String logName = null;
    protected Cluster cluster = null;
    private final ReadWriteLock clusterLock = new ReentrantReadWriteLock();
    protected String name = null;
    protected Container parent = null;
    protected ClassLoader parentClassLoader = null;
    protected final Pipeline pipeline = new StandardPipeline(this);
    private volatile Realm realm = null;
    private final ReadWriteLock realmLock = new ReentrantReadWriteLock();
    protected boolean startChildren = true;
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);
    protected volatile AccessLog accessLog = null;
    private volatile boolean accessLogScanComplete = false;
    private int startStopThreads = 1;

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ContainerBase$PrivilegedAddChild.class */
    protected class PrivilegedAddChild implements PrivilegedAction<Void> {
        private final Container child;

        PrivilegedAddChild(Container child) {
            this.child = child;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public Void run() {
            ContainerBase.this.addChildInternal(this.child);
            return null;
        }
    }

    @Override // org.apache.catalina.Container
    public int getStartStopThreads() {
        return this.startStopThreads;
    }

    @Override // org.apache.catalina.Container
    public void setStartStopThreads(int startStopThreads) {
        int oldStartStopThreads = this.startStopThreads;
        this.startStopThreads = startStopThreads;
        if (oldStartStopThreads != startStopThreads && this.startStopExecutor != null) {
            reconfigureStartStopExecutor(getStartStopThreads());
        }
    }

    @Override // org.apache.catalina.Container
    public int getBackgroundProcessorDelay() {
        return this.backgroundProcessorDelay;
    }

    @Override // org.apache.catalina.Container
    public void setBackgroundProcessorDelay(int delay) {
        this.backgroundProcessorDelay = delay;
    }

    @Override // org.apache.catalina.Container
    public Log getLogger() {
        if (this.logger != null) {
            return this.logger;
        }
        this.logger = LogFactory.getLog(getLogName());
        return this.logger;
    }

    @Override // org.apache.catalina.Container
    public String getLogName() {
        if (this.logName != null) {
            return this.logName;
        }
        String loggerName = null;
        Container container = this;
        while (true) {
            Container current = container;
            if (current != null) {
                String name = current.getName();
                if (name == null || name.equals("")) {
                    name = "/";
                } else if (name.startsWith("##")) {
                    name = "/" + name;
                }
                loggerName = "[" + name + "]" + (loggerName != null ? "." + loggerName : "");
                container = current.getParent();
            } else {
                this.logName = ContainerBase.class.getName() + "." + loggerName;
                return this.logName;
            }
        }
    }

    @Override // org.apache.catalina.Container
    public Cluster getCluster() {
        Lock readLock = this.clusterLock.readLock();
        readLock.lock();
        try {
            if (this.cluster != null) {
                return this.cluster;
            }
            if (this.parent != null) {
                return this.parent.getCluster();
            }
            return null;
        } finally {
            readLock.unlock();
        }
    }

    protected Cluster getClusterInternal() {
        Lock readLock = this.clusterLock.readLock();
        readLock.lock();
        try {
            return this.cluster;
        } finally {
            readLock.unlock();
        }
    }

    @Override // org.apache.catalina.Container
    public void setCluster(Cluster cluster) {
        Lock writeLock = this.clusterLock.writeLock();
        writeLock.lock();
        try {
            Cluster oldCluster = this.cluster;
            if (oldCluster == cluster) {
                return;
            }
            this.cluster = cluster;
            if (getState().isAvailable() && (oldCluster instanceof Lifecycle)) {
                try {
                    ((Lifecycle) oldCluster).stop();
                } catch (LifecycleException e) {
                    log.error(sm.getString("containerBase.cluster.stop"), e);
                }
            }
            if (cluster != null) {
                cluster.setContainer(this);
            }
            if (getState().isAvailable() && (cluster instanceof Lifecycle)) {
                try {
                    ((Lifecycle) cluster).start();
                } catch (LifecycleException e2) {
                    log.error(sm.getString("containerBase.cluster.start"), e2);
                }
            }
            writeLock.unlock();
            this.support.firePropertyChange("cluster", oldCluster, cluster);
        } finally {
            writeLock.unlock();
        }
    }

    @Override // org.apache.catalina.Container
    public String getName() {
        return this.name;
    }

    @Override // org.apache.catalina.Container
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException(sm.getString("containerBase.nullName"));
        }
        String oldName = this.name;
        this.name = name;
        this.support.firePropertyChange("name", oldName, this.name);
    }

    public boolean getStartChildren() {
        return this.startChildren;
    }

    public void setStartChildren(boolean startChildren) {
        boolean oldStartChildren = this.startChildren;
        this.startChildren = startChildren;
        this.support.firePropertyChange("startChildren", oldStartChildren, this.startChildren);
    }

    @Override // org.apache.catalina.Container
    public Container getParent() {
        return this.parent;
    }

    @Override // org.apache.catalina.Container
    public void setParent(Container container) {
        Container oldParent = this.parent;
        this.parent = container;
        this.support.firePropertyChange("parent", oldParent, this.parent);
    }

    @Override // org.apache.catalina.Container
    public ClassLoader getParentClassLoader() {
        if (this.parentClassLoader != null) {
            return this.parentClassLoader;
        }
        if (this.parent != null) {
            return this.parent.getParentClassLoader();
        }
        return ClassLoader.getSystemClassLoader();
    }

    @Override // org.apache.catalina.Container
    public void setParentClassLoader(ClassLoader parent) {
        ClassLoader oldParentClassLoader = this.parentClassLoader;
        this.parentClassLoader = parent;
        this.support.firePropertyChange("parentClassLoader", oldParentClassLoader, this.parentClassLoader);
    }

    @Override // org.apache.catalina.Container
    public Pipeline getPipeline() {
        return this.pipeline;
    }

    @Override // org.apache.catalina.Container
    public Realm getRealm() {
        Lock l = this.realmLock.readLock();
        l.lock();
        try {
            if (this.realm != null) {
                return this.realm;
            }
            if (this.parent != null) {
                return this.parent.getRealm();
            }
            return null;
        } finally {
            l.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Realm getRealmInternal() {
        Lock l = this.realmLock.readLock();
        l.lock();
        try {
            return this.realm;
        } finally {
            l.unlock();
        }
    }

    @Override // org.apache.catalina.Container
    public void setRealm(Realm realm) {
        Lock l = this.realmLock.writeLock();
        l.lock();
        try {
            Realm oldRealm = this.realm;
            if (oldRealm == realm) {
                return;
            }
            this.realm = realm;
            if (getState().isAvailable() && (oldRealm instanceof Lifecycle)) {
                try {
                    ((Lifecycle) oldRealm).stop();
                } catch (LifecycleException e) {
                    log.error(sm.getString("containerBase.realm.stop"), e);
                }
            }
            if (realm != null) {
                realm.setContainer(this);
            }
            if (getState().isAvailable() && (realm instanceof Lifecycle)) {
                try {
                    ((Lifecycle) realm).start();
                } catch (LifecycleException e2) {
                    log.error(sm.getString("containerBase.realm.start"), e2);
                }
            }
            this.support.firePropertyChange("realm", oldRealm, this.realm);
            l.unlock();
        } finally {
            l.unlock();
        }
    }

    @Override // org.apache.catalina.Container
    public void addChild(Container child) {
        if (Globals.IS_SECURITY_ENABLED) {
            PrivilegedAction<Void> dp = new PrivilegedAddChild(child);
            AccessController.doPrivileged(dp);
        } else {
            addChildInternal(child);
        }
    }

    private void addChildInternal(Container child) {
        if (log.isDebugEnabled()) {
            log.debug("Add child " + child + " " + this);
        }
        synchronized (this.children) {
            if (this.children.get(child.getName()) != null) {
                throw new IllegalArgumentException(sm.getString("containerBase.child.notUnique", child.getName()));
            }
            child.setParent(this);
            this.children.put(child.getName(), child);
        }
        fireContainerEvent(Container.ADD_CHILD_EVENT, child);
        try {
            if ((getState().isAvailable() || LifecycleState.STARTING_PREP.equals(getState())) && this.startChildren) {
                child.start();
            }
        } catch (LifecycleException e) {
            throw new IllegalStateException(sm.getString("containerBase.child.start"), e);
        }
    }

    @Override // org.apache.catalina.Container
    public void addContainerListener(ContainerListener listener) {
        this.listeners.add(listener);
    }

    @Override // org.apache.catalina.Container
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    @Override // org.apache.catalina.Container
    public Container findChild(String name) {
        Container container;
        if (name == null) {
            return null;
        }
        synchronized (this.children) {
            container = this.children.get(name);
        }
        return container;
    }

    @Override // org.apache.catalina.Container
    public Container[] findChildren() {
        Container[] containerArr;
        synchronized (this.children) {
            containerArr = (Container[]) this.children.values().toArray(new Container[0]);
        }
        return containerArr;
    }

    @Override // org.apache.catalina.Container
    public ContainerListener[] findContainerListeners() {
        return (ContainerListener[]) this.listeners.toArray(new ContainerListener[0]);
    }

    @Override // org.apache.catalina.Container
    public void removeChild(Container child) {
        if (child == null) {
            return;
        }
        try {
            if (child.getState().isAvailable()) {
                child.stop();
            }
        } catch (LifecycleException e) {
            log.error(sm.getString("containerBase.child.stop"), e);
        }
        boolean destroy = false;
        try {
            if (!LifecycleState.DESTROYING.equals(child.getState())) {
                child.destroy();
                destroy = true;
            }
        } catch (LifecycleException e2) {
            log.error(sm.getString("containerBase.child.destroy"), e2);
        }
        if (!destroy) {
            fireContainerEvent(Container.REMOVE_CHILD_EVENT, child);
        }
        synchronized (this.children) {
            if (this.children.get(child.getName()) == null) {
                return;
            }
            this.children.remove(child.getName());
        }
    }

    @Override // org.apache.catalina.Container
    public void removeContainerListener(ContainerListener listener) {
        this.listeners.remove(listener);
    }

    @Override // org.apache.catalina.Container
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    private void reconfigureStartStopExecutor(int threads) {
        if (threads == 1) {
            if (!(this.startStopExecutor instanceof InlineExecutorService)) {
                this.startStopExecutor = new InlineExecutorService();
            }
        } else {
            Server server = Container.getService(this).getServer();
            server.setUtilityThreads(threads);
            this.startStopExecutor = server.getUtilityExecutor();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        reconfigureStartStopExecutor(getStartStopThreads());
        this.logger = null;
        getLogger();
        Cluster cluster = getClusterInternal();
        if (cluster instanceof Lifecycle) {
            ((Lifecycle) cluster).start();
        }
        Realm realm = getRealmInternal();
        if (realm instanceof Lifecycle) {
            ((Lifecycle) realm).start();
        }
        Container[] children = findChildren();
        List<Future<Void>> results = new ArrayList<>(children.length);
        for (Container child : children) {
            results.add(this.startStopExecutor.submit(new StartChild(child)));
        }
        MultiThrowable multiThrowable = null;
        for (Future<Void> result : results) {
            try {
                result.get();
            } catch (Throwable e) {
                log.error(sm.getString("containerBase.threadedStartFailed"), e);
                if (multiThrowable == null) {
                    multiThrowable = new MultiThrowable();
                }
                multiThrowable.add(e);
            }
        }
        if (multiThrowable != null) {
            throw new LifecycleException(sm.getString("containerBase.threadedStartFailed"), multiThrowable.getThrowable());
        }
        if (this.pipeline instanceof Lifecycle) {
            ((Lifecycle) this.pipeline).start();
        }
        setState(LifecycleState.STARTING);
        if (this.backgroundProcessorDelay > 0) {
            this.monitorFuture = Container.getService(this).getServer().getUtilityExecutor().scheduleWithFixedDelay(new ContainerBackgroundProcessorMonitor(), 0L, 60L, TimeUnit.SECONDS);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleBase
    public synchronized void stopInternal() throws LifecycleException {
        if (this.monitorFuture != null) {
            this.monitorFuture.cancel(true);
            this.monitorFuture = null;
        }
        threadStop();
        setState(LifecycleState.STOPPING);
        if ((this.pipeline instanceof Lifecycle) && ((Lifecycle) this.pipeline).getState().isAvailable()) {
            ((Lifecycle) this.pipeline).stop();
        }
        Container[] children = findChildren();
        List<Future<Void>> results = new ArrayList<>(children.length);
        for (Container child : children) {
            results.add(this.startStopExecutor.submit(new StopChild(child)));
        }
        boolean fail = false;
        for (Future<Void> result : results) {
            try {
                result.get();
            } catch (Exception e) {
                log.error(sm.getString("containerBase.threadedStopFailed"), e);
                fail = true;
            }
        }
        if (fail) {
            throw new LifecycleException(sm.getString("containerBase.threadedStopFailed"));
        }
        Realm realm = getRealmInternal();
        if (realm instanceof Lifecycle) {
            ((Lifecycle) realm).stop();
        }
        Cluster cluster = getClusterInternal();
        if (cluster instanceof Lifecycle) {
            ((Lifecycle) cluster).stop();
        }
        if (this.startStopExecutor != null) {
            this.startStopExecutor.shutdownNow();
            this.startStopExecutor = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void destroyInternal() throws LifecycleException {
        Realm realm = getRealmInternal();
        if (realm instanceof Lifecycle) {
            ((Lifecycle) realm).destroy();
        }
        Cluster cluster = getClusterInternal();
        if (cluster instanceof Lifecycle) {
            ((Lifecycle) cluster).destroy();
        }
        if (this.pipeline instanceof Lifecycle) {
            ((Lifecycle) this.pipeline).destroy();
        }
        for (Container child : findChildren()) {
            removeChild(child);
        }
        if (this.parent != null) {
            this.parent.removeChild(this);
        }
        super.destroyInternal();
    }

    @Override // org.apache.catalina.Container
    public void logAccess(Request request, Response response, long time, boolean useDefault) {
        boolean logged = false;
        if (getAccessLog() != null) {
            getAccessLog().log(request, response, time);
            logged = true;
        }
        if (getParent() != null) {
            getParent().logAccess(request, response, time, useDefault && !logged);
        }
    }

    @Override // org.apache.catalina.Container
    public AccessLog getAccessLog() {
        if (this.accessLogScanComplete) {
            return this.accessLog;
        }
        AccessLogAdapter adapter = null;
        Valve[] valves = getPipeline().getValves();
        for (Valve valve : valves) {
            if (valve instanceof AccessLog) {
                if (adapter == null) {
                    adapter = new AccessLogAdapter((AccessLog) valve);
                } else {
                    adapter.add((AccessLog) valve);
                }
            }
        }
        if (adapter != null) {
            this.accessLog = adapter;
        }
        this.accessLogScanComplete = true;
        return this.accessLog;
    }

    public synchronized void addValve(Valve valve) {
        this.pipeline.addValve(valve);
    }

    @Override // org.apache.catalina.Container
    public synchronized void backgroundProcess() {
        if (!getState().isAvailable()) {
            return;
        }
        Cluster cluster = getClusterInternal();
        if (cluster != null) {
            try {
                cluster.backgroundProcess();
            } catch (Exception e) {
                log.warn(sm.getString("containerBase.backgroundProcess.cluster", cluster), e);
            }
        }
        Realm realm = getRealmInternal();
        if (realm != null) {
            try {
                realm.backgroundProcess();
            } catch (Exception e2) {
                log.warn(sm.getString("containerBase.backgroundProcess.realm", realm), e2);
            }
        }
        Valve first = this.pipeline.getFirst();
        while (true) {
            Valve current = first;
            if (current != null) {
                try {
                    current.backgroundProcess();
                } catch (Exception e3) {
                    log.warn(sm.getString("containerBase.backgroundProcess.valve", current), e3);
                }
                first = current.getNext();
            } else {
                fireLifecycleEvent(Lifecycle.PERIODIC_EVENT, null);
                return;
            }
        }
    }

    @Override // org.apache.catalina.Container
    public File getCatalinaBase() {
        if (this.parent == null) {
            return null;
        }
        return this.parent.getCatalinaBase();
    }

    @Override // org.apache.catalina.Container
    public File getCatalinaHome() {
        if (this.parent == null) {
            return null;
        }
        return this.parent.getCatalinaHome();
    }

    @Override // org.apache.catalina.Container
    public void fireContainerEvent(String type, Object data) {
        if (this.listeners.size() < 1) {
            return;
        }
        ContainerEvent event = new ContainerEvent(this, type, data);
        for (ContainerListener listener : this.listeners) {
            listener.containerEvent(event);
        }
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getDomainInternal() {
        Container p = getParent();
        if (p == null) {
            return null;
        }
        return p.getDomain();
    }

    @Override // org.apache.catalina.Container
    public String getMBeanKeyProperties() {
        Container c = this;
        StringBuilder keyProperties = new StringBuilder();
        int containerCount = 0;
        while (true) {
            if (c instanceof Engine) {
                break;
            }
            if (c instanceof Wrapper) {
                keyProperties.insert(0, ",servlet=");
                keyProperties.insert(9, c.getName());
            } else if (c instanceof Context) {
                keyProperties.insert(0, ",context=");
                ContextName cn = new ContextName(c.getName(), false);
                keyProperties.insert(9, cn.getDisplayName());
            } else if (c instanceof Host) {
                keyProperties.insert(0, ",host=");
                keyProperties.insert(6, c.getName());
            } else {
                if (c == null) {
                    keyProperties.append(",container");
                    int i = containerCount;
                    int i2 = containerCount + 1;
                    keyProperties.append(i);
                    keyProperties.append("=null");
                    break;
                }
                keyProperties.append(",container");
                int i3 = containerCount;
                containerCount++;
                keyProperties.append(i3);
                keyProperties.append('=');
                keyProperties.append(c.getName());
            }
            c = c.getParent();
        }
        return keyProperties.toString();
    }

    public ObjectName[] getChildren() {
        List<ObjectName> names;
        synchronized (this.children) {
            names = new ArrayList<>(this.children.size());
            for (Container next : this.children.values()) {
                if (next instanceof ContainerBase) {
                    names.add(next.getObjectName());
                }
            }
        }
        return (ObjectName[]) names.toArray(new ObjectName[0]);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void threadStart() {
        if (this.backgroundProcessorDelay > 0) {
            if (getState().isAvailable() || LifecycleState.STARTING_PREP.equals(getState())) {
                if (this.backgroundProcessorFuture == null || this.backgroundProcessorFuture.isDone()) {
                    if (this.backgroundProcessorFuture != null && this.backgroundProcessorFuture.isDone()) {
                        try {
                            this.backgroundProcessorFuture.get();
                        } catch (InterruptedException | ExecutionException e) {
                            log.error(sm.getString("containerBase.backgroundProcess.error"), e);
                        }
                    }
                    this.backgroundProcessorFuture = Container.getService(this).getServer().getUtilityExecutor().scheduleWithFixedDelay(new ContainerBackgroundProcessor(), this.backgroundProcessorDelay, this.backgroundProcessorDelay, TimeUnit.SECONDS);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void threadStop() {
        if (this.backgroundProcessorFuture != null) {
            this.backgroundProcessorFuture.cancel(true);
            this.backgroundProcessorFuture = null;
        }
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        Container parent = getParent();
        if (parent != null) {
            sb.append(parent.toString());
            sb.append('.');
        }
        sb.append(getClass().getSimpleName());
        sb.append('[');
        sb.append(getName());
        sb.append(']');
        return sb.toString();
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ContainerBase$ContainerBackgroundProcessorMonitor.class */
    protected class ContainerBackgroundProcessorMonitor implements Runnable {
        protected ContainerBackgroundProcessorMonitor() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (ContainerBase.this.getState().isAvailable()) {
                ContainerBase.this.threadStart();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ContainerBase$ContainerBackgroundProcessor.class */
    public class ContainerBackgroundProcessor implements Runnable {
        protected ContainerBackgroundProcessor() {
        }

        @Override // java.lang.Runnable
        public void run() {
            processChildren(ContainerBase.this);
        }

        protected void processChildren(Container container) {
            ClassLoader originalClassLoader = null;
            try {
                try {
                    if (container instanceof Context) {
                        Loader loader = ((Context) container).getLoader();
                        if (loader == null) {
                            if (container instanceof Context) {
                                ((Context) container).unbind(false, null);
                                return;
                            }
                            return;
                        }
                        originalClassLoader = ((Context) container).bind(false, null);
                    }
                    container.backgroundProcess();
                    Container[] children = container.findChildren();
                    for (Container child : children) {
                        if (child.getBackgroundProcessorDelay() <= 0) {
                            processChildren(child);
                        }
                    }
                    if (container instanceof Context) {
                        ((Context) container).unbind(false, originalClassLoader);
                    }
                } catch (Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                    ContainerBase.log.error(ContainerBase.sm.getString("containerBase.backgroundProcess.error"), t);
                    if (container instanceof Context) {
                        ((Context) container).unbind(false, originalClassLoader);
                    }
                }
            } catch (Throwable th) {
                if (container instanceof Context) {
                    ((Context) container).unbind(false, originalClassLoader);
                }
                throw th;
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ContainerBase$StartChild.class */
    private static class StartChild implements Callable<Void> {
        private Container child;

        StartChild(Container child) {
            this.child = child;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Callable
        public Void call() throws LifecycleException {
            this.child.start();
            return null;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ContainerBase$StopChild.class */
    private static class StopChild implements Callable<Void> {
        private Container child;

        StopChild(Container child) {
            this.child = child;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Callable
        public Void call() throws LifecycleException {
            if (this.child.getState().isAvailable()) {
                this.child.stop();
                return null;
            }
            return null;
        }
    }
}
