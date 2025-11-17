package ch.qos.logback.core;

import ch.qos.logback.core.spi.ConfigurationEvent;
import ch.qos.logback.core.spi.ConfigurationEventListener;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.spi.LogbackLock;
import ch.qos.logback.core.spi.SequenceNumberGenerator;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.util.ExecutorServiceUtil;
import ch.qos.logback.core.util.NetworkAddressUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/ContextBase.class */
public class ContextBase implements Context, LifeCycle {
    private String name;
    private ScheduledExecutorService scheduledExecutorService;
    private ThreadPoolExecutor threadPoolExecutor;
    private ExecutorService alternateExecutorService;
    private LifeCycleManager lifeCycleManager;
    private SequenceNumberGenerator sequenceNumberGenerator;
    private boolean started;
    private long birthTime = System.currentTimeMillis();
    private StatusManager sm = new BasicStatusManager();
    Map<String, String> propertyMap = new HashMap();
    Map<String, Object> objectMap = new ConcurrentHashMap();
    LogbackLock configurationLock = new LogbackLock();
    private final List<ConfigurationEventListener> configurationEventListenerList = new CopyOnWriteArrayList();
    protected List<ScheduledFuture<?>> scheduledFutures = new ArrayList(1);

    public ContextBase() {
        initCollisionMaps();
    }

    @Override // ch.qos.logback.core.Context
    public StatusManager getStatusManager() {
        return this.sm;
    }

    public void setStatusManager(StatusManager statusManager) {
        if (statusManager == null) {
            throw new IllegalArgumentException("null StatusManager not allowed");
        }
        this.sm = statusManager;
    }

    @Override // ch.qos.logback.core.Context, ch.qos.logback.core.spi.PropertyContainer
    public Map<String, String> getCopyOfPropertyMap() {
        return new HashMap(this.propertyMap);
    }

    @Override // ch.qos.logback.core.Context
    public void putProperty(String key, String val) {
        if (CoreConstants.HOSTNAME_KEY.equalsIgnoreCase(key)) {
            putHostnameProperty(val);
        } else {
            this.propertyMap.put(key, val);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initCollisionMaps() {
        putObject(CoreConstants.FA_FILENAME_COLLISION_MAP, new HashMap());
        putObject(CoreConstants.RFA_FILENAME_PATTERN_COLLISION_MAP, new HashMap());
    }

    @Override // ch.qos.logback.core.Context, ch.qos.logback.core.spi.PropertyContainer
    public String getProperty(String key) {
        if (CoreConstants.CONTEXT_NAME_KEY.equals(key)) {
            return getName();
        }
        if (CoreConstants.HOSTNAME_KEY.equalsIgnoreCase(key)) {
            return lazyGetHostname();
        }
        return this.propertyMap.get(key);
    }

    private String lazyGetHostname() {
        String hostname = this.propertyMap.get(CoreConstants.HOSTNAME_KEY);
        if (hostname == null) {
            hostname = new NetworkAddressUtil(this).safelyGetLocalHostName();
            putHostnameProperty(hostname);
        }
        return hostname;
    }

    private void putHostnameProperty(String hostname) {
        String existingHostname = this.propertyMap.get(CoreConstants.HOSTNAME_KEY);
        if (existingHostname == null) {
            this.propertyMap.put(CoreConstants.HOSTNAME_KEY, hostname);
        }
    }

    @Override // ch.qos.logback.core.Context
    public Object getObject(String key) {
        return this.objectMap.get(key);
    }

    @Override // ch.qos.logback.core.Context
    public void putObject(String key, Object value) {
        this.objectMap.put(key, value);
    }

    public void removeObject(String key) {
        this.objectMap.remove(key);
    }

    @Override // ch.qos.logback.core.Context
    public String getName() {
        return this.name;
    }

    public void start() {
        this.started = true;
    }

    public void stop() {
        stopExecutorServices();
        this.started = false;
    }

    @Override // ch.qos.logback.core.spi.LifeCycle
    public boolean isStarted() {
        return this.started;
    }

    public void reset() {
        removeShutdownHook();
        getLifeCycleManager().reset();
        this.propertyMap.clear();
        this.objectMap.clear();
    }

    @Override // ch.qos.logback.core.Context
    public void setName(String name) throws IllegalStateException {
        if (name != null && name.equals(this.name)) {
            return;
        }
        if (this.name == null || "default".equals(this.name)) {
            this.name = name;
            return;
        }
        throw new IllegalStateException("Context has been already given a name");
    }

    @Override // ch.qos.logback.core.Context
    public long getBirthTime() {
        return this.birthTime;
    }

    @Override // ch.qos.logback.core.Context
    public Object getConfigurationLock() {
        return this.configurationLock;
    }

    @Override // ch.qos.logback.core.Context
    public synchronized ExecutorService getExecutorService() {
        if (this.threadPoolExecutor == null) {
            this.threadPoolExecutor = ExecutorServiceUtil.newThreadPoolExecutor();
        }
        return this.threadPoolExecutor;
    }

    @Override // ch.qos.logback.core.Context
    public synchronized ExecutorService getAlternateExecutorService() {
        if (this.alternateExecutorService == null) {
            this.alternateExecutorService = ExecutorServiceUtil.newAlternateThreadPoolExecutor();
        }
        return this.alternateExecutorService;
    }

    @Override // ch.qos.logback.core.Context
    public synchronized ScheduledExecutorService getScheduledExecutorService() {
        if (this.scheduledExecutorService == null) {
            this.scheduledExecutorService = ExecutorServiceUtil.newScheduledExecutorService();
        }
        return this.scheduledExecutorService;
    }

    private synchronized void stopExecutorServices() {
        ExecutorServiceUtil.shutdown(this.scheduledExecutorService);
        this.scheduledExecutorService = null;
        ExecutorServiceUtil.shutdown(this.threadPoolExecutor);
        this.threadPoolExecutor = null;
    }

    private void removeShutdownHook() {
        Thread hook = (Thread) getObject(CoreConstants.SHUTDOWN_HOOK_THREAD);
        if (hook != null) {
            removeObject(CoreConstants.SHUTDOWN_HOOK_THREAD);
            try {
                this.sm.add(new InfoStatus("Removing shutdownHook " + String.valueOf(hook), this));
                Runtime runtime = Runtime.getRuntime();
                boolean result = runtime.removeShutdownHook(hook);
                this.sm.add(new InfoStatus("ShutdownHook removal result: " + result, this));
            } catch (IllegalStateException e) {
            }
        }
    }

    @Override // ch.qos.logback.core.Context
    public void register(LifeCycle component) {
        getLifeCycleManager().register(component);
    }

    synchronized LifeCycleManager getLifeCycleManager() {
        if (this.lifeCycleManager == null) {
            this.lifeCycleManager = new LifeCycleManager();
        }
        return this.lifeCycleManager;
    }

    public String toString() {
        return this.name;
    }

    @Override // ch.qos.logback.core.Context
    public void addScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFutures.add(scheduledFuture);
    }

    @Deprecated
    public List<ScheduledFuture<?>> getScheduledFutures() {
        return getCopyOfScheduledFutures();
    }

    public List<ScheduledFuture<?>> getCopyOfScheduledFutures() {
        return new ArrayList(this.scheduledFutures);
    }

    @Override // ch.qos.logback.core.Context
    public SequenceNumberGenerator getSequenceNumberGenerator() {
        return this.sequenceNumberGenerator;
    }

    @Override // ch.qos.logback.core.Context
    public void setSequenceNumberGenerator(SequenceNumberGenerator sequenceNumberGenerator) {
        this.sequenceNumberGenerator = sequenceNumberGenerator;
    }

    @Override // ch.qos.logback.core.Context
    public void addConfigurationEventListener(ConfigurationEventListener listener) {
        this.configurationEventListenerList.add(listener);
    }

    @Override // ch.qos.logback.core.Context
    public void fireConfigurationEvent(ConfigurationEvent configurationEvent) {
        this.configurationEventListenerList.forEach(l -> {
            l.listen(configurationEvent);
        });
    }
}
