package org.springframework.context.support;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.crac.CheckpointException;
import org.crac.Context;
import org.crac.Core;
import org.crac.Resource;
import org.crac.RestoreException;
import org.crac.management.CRaCMXBean;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.Lifecycle;
import org.springframework.context.LifecycleProcessor;
import org.springframework.context.Phased;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.NativeDetector;
import org.springframework.core.SpringProperties;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.backoff.ExponentialBackOff;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/DefaultLifecycleProcessor.class */
public class DefaultLifecycleProcessor implements LifecycleProcessor, BeanFactoryAware {
    private final Log logger = LogFactory.getLog(getClass());
    private volatile long timeoutPerShutdownPhase = ExponentialBackOff.DEFAULT_MAX_INTERVAL;
    private volatile boolean running;

    @Nullable
    private volatile ConfigurableListableBeanFactory beanFactory;

    @Nullable
    private volatile Set<String> stoppedBeans;

    @Nullable
    private Object cracResource;
    public static final String ON_REFRESH_VALUE = "onRefresh";
    public static final String CHECKPOINT_PROPERTY_NAME = "spring.context.checkpoint";
    private static final boolean checkpointOnRefresh = ON_REFRESH_VALUE.equalsIgnoreCase(SpringProperties.getProperty(CHECKPOINT_PROPERTY_NAME));
    public static final String EXIT_PROPERTY_NAME = "spring.context.exit";
    private static final boolean exitOnRefresh = ON_REFRESH_VALUE.equalsIgnoreCase(SpringProperties.getProperty(EXIT_PROPERTY_NAME));

    public DefaultLifecycleProcessor() {
        if (!NativeDetector.inNativeImage() && ClassUtils.isPresent("org.crac.Core", getClass().getClassLoader())) {
            this.cracResource = new CracDelegate().registerResource();
        } else if (checkpointOnRefresh) {
            throw new IllegalStateException("Checkpoint on refresh requires a CRaC-enabled JVM and 'org.crac:crac' on the classpath");
        }
    }

    public void setTimeoutPerShutdownPhase(long timeoutPerShutdownPhase) {
        this.timeoutPerShutdownPhase = timeoutPerShutdownPhase;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException("DefaultLifecycleProcessor requires a ConfigurableListableBeanFactory: " + beanFactory);
        }
        ConfigurableListableBeanFactory clbf = (ConfigurableListableBeanFactory) beanFactory;
        this.beanFactory = clbf;
    }

    private ConfigurableListableBeanFactory getBeanFactory() {
        ConfigurableListableBeanFactory beanFactory = this.beanFactory;
        Assert.state(beanFactory != null, "No BeanFactory available");
        return beanFactory;
    }

    @Override // org.springframework.context.Lifecycle
    public void start() {
        this.stoppedBeans = null;
        startBeans(false);
        this.running = true;
    }

    @Override // org.springframework.context.Lifecycle
    public void stop() {
        stopBeans();
        this.running = false;
    }

    @Override // org.springframework.context.LifecycleProcessor
    public void onRefresh() {
        if (checkpointOnRefresh) {
            new CracDelegate().checkpointRestore();
        }
        if (exitOnRefresh) {
            Runtime.getRuntime().halt(0);
        }
        this.stoppedBeans = null;
        try {
            startBeans(true);
            this.running = true;
        } catch (ApplicationContextException ex) {
            stopBeans();
            throw ex;
        }
    }

    @Override // org.springframework.context.LifecycleProcessor
    public void onClose() {
        stopBeans();
        this.running = false;
    }

    @Override // org.springframework.context.Lifecycle
    public boolean isRunning() {
        return this.running;
    }

    void stopForRestart() {
        if (this.running) {
            this.stoppedBeans = Collections.newSetFromMap(new ConcurrentHashMap());
            stopBeans();
            this.running = false;
        }
    }

    void restartAfterStop() {
        if (this.stoppedBeans != null) {
            startBeans(true);
            this.stoppedBeans = null;
            this.running = true;
        }
    }

    private void startBeans(boolean autoStartupOnly) {
        Map<String, Lifecycle> lifecycleBeans = getLifecycleBeans();
        Map<Integer, LifecycleGroup> phases = new TreeMap<>();
        lifecycleBeans.forEach((beanName, bean) -> {
            if (!autoStartupOnly || isAutoStartupCandidate(beanName, bean)) {
                int phase = getPhase(bean);
                ((LifecycleGroup) phases.computeIfAbsent(Integer.valueOf(phase), p -> {
                    return new LifecycleGroup(phase, this.timeoutPerShutdownPhase, lifecycleBeans, autoStartupOnly);
                })).add(beanName, bean);
            }
        });
        if (!phases.isEmpty()) {
            phases.values().forEach((v0) -> {
                v0.start();
            });
        }
    }

    private boolean isAutoStartupCandidate(String beanName, Lifecycle bean) {
        Set<String> stoppedBeans = this.stoppedBeans;
        if (stoppedBeans != null) {
            return stoppedBeans.contains(beanName);
        }
        if (bean instanceof SmartLifecycle) {
            SmartLifecycle smartLifecycle = (SmartLifecycle) bean;
            if (smartLifecycle.isAutoStartup()) {
                return true;
            }
        }
        return false;
    }

    private void doStart(Map<String, ? extends Lifecycle> lifecycleBeans, String beanName, boolean autoStartupOnly) {
        Lifecycle bean = lifecycleBeans.remove(beanName);
        if (bean != null && bean != this) {
            String[] dependenciesForBean = getBeanFactory().getDependenciesForBean(beanName);
            for (String dependency : dependenciesForBean) {
                doStart(lifecycleBeans, dependency, autoStartupOnly);
            }
            if (bean.isRunning()) {
                return;
            }
            if (!autoStartupOnly || toBeStarted(beanName, bean)) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Starting bean '" + beanName + "' of type [" + bean.getClass().getName() + "]");
                }
                try {
                    bean.start();
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Successfully started bean '" + beanName + "'");
                    }
                } catch (Throwable ex) {
                    throw new ApplicationContextException("Failed to start bean '" + beanName + "'", ex);
                }
            }
        }
    }

    private boolean toBeStarted(String beanName, Lifecycle bean) {
        Set<String> stoppedBeans = this.stoppedBeans;
        if (stoppedBeans != null) {
            return stoppedBeans.contains(beanName);
        }
        if (bean instanceof SmartLifecycle) {
            SmartLifecycle smartLifecycle = (SmartLifecycle) bean;
            if (!smartLifecycle.isAutoStartup()) {
                return false;
            }
        }
        return true;
    }

    private void stopBeans() {
        Map<String, Lifecycle> lifecycleBeans = getLifecycleBeans();
        Map<Integer, LifecycleGroup> phases = new TreeMap<>((Comparator<? super Integer>) Comparator.reverseOrder());
        lifecycleBeans.forEach((beanName, bean) -> {
            int shutdownPhase = getPhase(bean);
            ((LifecycleGroup) phases.computeIfAbsent(Integer.valueOf(shutdownPhase), p -> {
                return new LifecycleGroup(shutdownPhase, this.timeoutPerShutdownPhase, lifecycleBeans, false);
            })).add(beanName, bean);
        });
        if (!phases.isEmpty()) {
            phases.values().forEach((v0) -> {
                v0.stop();
            });
        }
    }

    private void doStop(Map<String, ? extends Lifecycle> lifecycleBeans, final String beanName, final CountDownLatch latch, final Set<String> countDownBeanNames) {
        Lifecycle bean = lifecycleBeans.remove(beanName);
        if (bean != null) {
            String[] dependentBeans = getBeanFactory().getDependentBeans(beanName);
            for (String dependentBean : dependentBeans) {
                doStop(lifecycleBeans, dependentBean, latch, countDownBeanNames);
            }
            try {
                if (bean.isRunning()) {
                    Set<String> stoppedBeans = this.stoppedBeans;
                    if (stoppedBeans != null) {
                        stoppedBeans.add(beanName);
                    }
                    if (bean instanceof SmartLifecycle) {
                        SmartLifecycle smartLifecycle = (SmartLifecycle) bean;
                        if (this.logger.isTraceEnabled()) {
                            this.logger.trace("Asking bean '" + beanName + "' of type [" + bean.getClass().getName() + "] to stop");
                        }
                        countDownBeanNames.add(beanName);
                        smartLifecycle.stop(() -> {
                            latch.countDown();
                            countDownBeanNames.remove(beanName);
                            if (this.logger.isDebugEnabled()) {
                                this.logger.debug("Bean '" + beanName + "' completed its stop procedure");
                            }
                        });
                    } else {
                        if (this.logger.isTraceEnabled()) {
                            this.logger.trace("Stopping bean '" + beanName + "' of type [" + bean.getClass().getName() + "]");
                        }
                        bean.stop();
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Successfully stopped bean '" + beanName + "'");
                        }
                    }
                } else if (bean instanceof SmartLifecycle) {
                    latch.countDown();
                }
            } catch (Throwable ex) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn("Failed to stop bean '" + beanName + "'", ex);
                }
            }
        }
    }

    protected Map<String, Lifecycle> getLifecycleBeans() {
        Object bean;
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        Map<String, Lifecycle> beans = new LinkedHashMap<>();
        String[] beanNames = beanFactory.getBeanNamesForType(Lifecycle.class, false, false);
        for (String beanName : beanNames) {
            String beanNameToRegister = BeanFactoryUtils.transformedBeanName(beanName);
            boolean isFactoryBean = beanFactory.isFactoryBean(beanNameToRegister);
            String beanNameToCheck = isFactoryBean ? "&" + beanName : beanName;
            if (((beanFactory.containsSingleton(beanNameToRegister) && (!isFactoryBean || matchesBeanType(Lifecycle.class, beanNameToCheck, beanFactory))) || matchesBeanType(SmartLifecycle.class, beanNameToCheck, beanFactory)) && (bean = beanFactory.getBean(beanNameToCheck)) != this && (bean instanceof Lifecycle)) {
                Lifecycle lifecycle = (Lifecycle) bean;
                beans.put(beanNameToRegister, lifecycle);
            }
        }
        return beans;
    }

    private boolean matchesBeanType(Class<?> targetType, String beanName, BeanFactory beanFactory) {
        Class<?> beanType = beanFactory.getType(beanName);
        return beanType != null && targetType.isAssignableFrom(beanType);
    }

    protected int getPhase(Lifecycle bean) {
        if (!(bean instanceof Phased)) {
            return 0;
        }
        Phased phased = (Phased) bean;
        return phased.getPhase();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/DefaultLifecycleProcessor$LifecycleGroup.class */
    public class LifecycleGroup {
        private final int phase;
        private final long timeout;
        private final Map<String, ? extends Lifecycle> lifecycleBeans;
        private final boolean autoStartupOnly;
        private final List<LifecycleGroupMember> members = new ArrayList();
        private int smartMemberCount;

        public LifecycleGroup(int phase, long timeout, Map<String, ? extends Lifecycle> lifecycleBeans, boolean autoStartupOnly) {
            this.phase = phase;
            this.timeout = timeout;
            this.lifecycleBeans = lifecycleBeans;
            this.autoStartupOnly = autoStartupOnly;
        }

        public void add(String name, Lifecycle bean) {
            this.members.add(new LifecycleGroupMember(name, bean));
            if (bean instanceof SmartLifecycle) {
                this.smartMemberCount++;
            }
        }

        public void start() {
            if (this.members.isEmpty()) {
                return;
            }
            if (DefaultLifecycleProcessor.this.logger.isDebugEnabled()) {
                DefaultLifecycleProcessor.this.logger.debug("Starting beans in phase " + this.phase);
            }
            for (LifecycleGroupMember member : this.members) {
                DefaultLifecycleProcessor.this.doStart(this.lifecycleBeans, member.name, this.autoStartupOnly);
            }
        }

        public void stop() {
            if (this.members.isEmpty()) {
                return;
            }
            if (DefaultLifecycleProcessor.this.logger.isDebugEnabled()) {
                DefaultLifecycleProcessor.this.logger.debug("Stopping beans in phase " + this.phase);
            }
            CountDownLatch latch = new CountDownLatch(this.smartMemberCount);
            Set<String> countDownBeanNames = Collections.synchronizedSet(new LinkedHashSet());
            Set<String> lifecycleBeanNames = new HashSet<>(this.lifecycleBeans.keySet());
            for (LifecycleGroupMember member : this.members) {
                if (lifecycleBeanNames.contains(member.name)) {
                    DefaultLifecycleProcessor.this.doStop(this.lifecycleBeans, member.name, latch, countDownBeanNames);
                } else if (member.bean instanceof SmartLifecycle) {
                    latch.countDown();
                }
            }
            try {
                latch.await(this.timeout, TimeUnit.MILLISECONDS);
                if (latch.getCount() > 0 && !countDownBeanNames.isEmpty() && DefaultLifecycleProcessor.this.logger.isInfoEnabled()) {
                    Log log = DefaultLifecycleProcessor.this.logger;
                    log.info("Failed to shut down " + countDownBeanNames.size() + " bean" + (countDownBeanNames.size() > 1 ? "s" : "") + " with phase value " + this.phase + " within timeout of " + this.timeout + "ms: " + log);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/DefaultLifecycleProcessor$LifecycleGroupMember.class */
    public static final class LifecycleGroupMember extends Record {
        private final String name;
        private final Lifecycle bean;

        private LifecycleGroupMember(String name, Lifecycle bean) {
            this.name = name;
            this.bean = bean;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, LifecycleGroupMember.class), LifecycleGroupMember.class, "name;bean", "FIELD:Lorg/springframework/context/support/DefaultLifecycleProcessor$LifecycleGroupMember;->name:Ljava/lang/String;", "FIELD:Lorg/springframework/context/support/DefaultLifecycleProcessor$LifecycleGroupMember;->bean:Lorg/springframework/context/Lifecycle;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, LifecycleGroupMember.class), LifecycleGroupMember.class, "name;bean", "FIELD:Lorg/springframework/context/support/DefaultLifecycleProcessor$LifecycleGroupMember;->name:Ljava/lang/String;", "FIELD:Lorg/springframework/context/support/DefaultLifecycleProcessor$LifecycleGroupMember;->bean:Lorg/springframework/context/Lifecycle;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, LifecycleGroupMember.class, Object.class), LifecycleGroupMember.class, "name;bean", "FIELD:Lorg/springframework/context/support/DefaultLifecycleProcessor$LifecycleGroupMember;->name:Ljava/lang/String;", "FIELD:Lorg/springframework/context/support/DefaultLifecycleProcessor$LifecycleGroupMember;->bean:Lorg/springframework/context/Lifecycle;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String name() {
            return this.name;
        }

        public Lifecycle bean() {
            return this.bean;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/DefaultLifecycleProcessor$CracDelegate.class */
    private class CracDelegate {
        private CracDelegate() {
        }

        public Object registerResource() {
            DefaultLifecycleProcessor.this.logger.debug("Registering JVM checkpoint/restore callback for Spring-managed lifecycle beans");
            CracResourceAdapter resourceAdapter = new CracResourceAdapter();
            Core.getGlobalContext().register(resourceAdapter);
            return resourceAdapter;
        }

        public void checkpointRestore() {
            DefaultLifecycleProcessor.this.logger.info("Triggering JVM checkpoint/restore");
            try {
                Core.checkpointRestore();
            } catch (CheckpointException ex) {
                throw new ApplicationContextException("Failed to take CRaC checkpoint on refresh", ex);
            } catch (UnsupportedOperationException ex2) {
                throw new ApplicationContextException("CRaC checkpoint not supported on current JVM", ex2);
            } catch (RestoreException ex3) {
                throw new ApplicationContextException("Failed to restore CRaC checkpoint on refresh", ex3);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/DefaultLifecycleProcessor$CracResourceAdapter.class */
    public class CracResourceAdapter implements Resource {

        @Nullable
        private CyclicBarrier barrier;

        private CracResourceAdapter() {
        }

        public void beforeCheckpoint(Context<? extends Resource> context) {
            this.barrier = new CyclicBarrier(2);
            Thread thread = new Thread(() -> {
                awaitPreventShutdownBarrier();
                awaitPreventShutdownBarrier();
            }, "prevent-shutdown");
            thread.setDaemon(false);
            thread.start();
            awaitPreventShutdownBarrier();
            DefaultLifecycleProcessor.this.logger.debug("Stopping Spring-managed lifecycle beans before JVM checkpoint");
            DefaultLifecycleProcessor.this.stopForRestart();
        }

        public void afterRestore(Context<? extends Resource> context) {
            DefaultLifecycleProcessor.this.logger.info("Restarting Spring-managed lifecycle beans after JVM restore");
            DefaultLifecycleProcessor.this.restartAfterStop();
            this.barrier = null;
            if (!DefaultLifecycleProcessor.checkpointOnRefresh) {
                DefaultLifecycleProcessor.this.logger.info("Spring-managed lifecycle restart completed (restored JVM running for " + CRaCMXBean.getCRaCMXBean().getUptimeSinceRestore() + " ms)");
            }
        }

        private void awaitPreventShutdownBarrier() {
            try {
                if (this.barrier != null) {
                    this.barrier.await();
                }
            } catch (Exception ex) {
                DefaultLifecycleProcessor.this.logger.trace("Exception from prevent-shutdown barrier", ex);
            }
        }
    }
}
