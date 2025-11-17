package org.springframework.scheduling.annotation;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.FixedDelayTask;
import org.springframework.scheduling.config.FixedRateTask;
import org.springframework.scheduling.config.OneTimeTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TaskSchedulerRouter;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/annotation/ScheduledAnnotationBeanPostProcessor.class */
public class ScheduledAnnotationBeanPostProcessor implements ScheduledTaskHolder, MergedBeanDefinitionPostProcessor, DestructionAwareBeanPostProcessor, Ordered, EmbeddedValueResolverAware, BeanNameAware, BeanFactoryAware, ApplicationContextAware, SmartInitializingSingleton, DisposableBean, ApplicationListener<ApplicationContextEvent> {
    public static final String DEFAULT_TASK_SCHEDULER_BEAN_NAME = "taskScheduler";
    private static final boolean reactiveStreamsPresent = ClassUtils.isPresent("org.reactivestreams.Publisher", ScheduledAnnotationBeanPostProcessor.class.getClassLoader());
    protected final Log logger;
    private final ScheduledTaskRegistrar registrar;

    @Nullable
    private Object scheduler;

    @Nullable
    private StringValueResolver embeddedValueResolver;

    @Nullable
    private String beanName;

    @Nullable
    private BeanFactory beanFactory;

    @Nullable
    private ApplicationContext applicationContext;

    @Nullable
    private TaskSchedulerRouter localScheduler;
    private final Set<Class<?>> nonAnnotatedClasses;
    private final Map<Object, Set<ScheduledTask>> scheduledTasks;
    private final Map<Object, List<Runnable>> reactiveSubscriptions;

    public ScheduledAnnotationBeanPostProcessor() {
        this.logger = LogFactory.getLog(getClass());
        this.nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap(64));
        this.scheduledTasks = new IdentityHashMap(16);
        this.reactiveSubscriptions = new IdentityHashMap(16);
        this.registrar = new ScheduledTaskRegistrar();
    }

    public ScheduledAnnotationBeanPostProcessor(ScheduledTaskRegistrar registrar) {
        this.logger = LogFactory.getLog(getClass());
        this.nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap(64));
        this.scheduledTasks = new IdentityHashMap(16);
        this.reactiveSubscriptions = new IdentityHashMap(16);
        Assert.notNull(registrar, "ScheduledTaskRegistrar must not be null");
        this.registrar = registrar;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    public void setScheduler(Object scheduler) {
        this.scheduler = scheduler;
    }

    @Override // org.springframework.context.EmbeddedValueResolverAware
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        if (this.beanFactory == null) {
            this.beanFactory = applicationContext;
        }
    }

    @Override // org.springframework.beans.factory.SmartInitializingSingleton
    public void afterSingletonsInstantiated() {
        this.nonAnnotatedClasses.clear();
        if (this.applicationContext == null) {
            finishRegistration();
        }
    }

    private void finishRegistration() {
        if (this.scheduler != null) {
            this.registrar.setScheduler(this.scheduler);
        } else {
            this.localScheduler = new TaskSchedulerRouter();
            this.localScheduler.setBeanName(this.beanName);
            this.localScheduler.setBeanFactory(this.beanFactory);
            this.registrar.setTaskScheduler(this.localScheduler);
        }
        BeanFactory beanFactory = this.beanFactory;
        if (beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory lbf = (ListableBeanFactory) beanFactory;
            Map<String, SchedulingConfigurer> beans = lbf.getBeansOfType(SchedulingConfigurer.class);
            List<SchedulingConfigurer> configurers = new ArrayList<>(beans.values());
            AnnotationAwareOrderComparator.sort(configurers);
            for (SchedulingConfigurer configurer : configurers) {
                configurer.configureTasks(this.registrar);
            }
        }
        this.registrar.afterPropertiesSet();
    }

    @Override // org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if ((bean instanceof AopInfrastructureBean) || (bean instanceof TaskScheduler) || (bean instanceof ScheduledExecutorService)) {
            return bean;
        }
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        if (!this.nonAnnotatedClasses.contains(targetClass) && AnnotationUtils.isCandidateClass(targetClass, List.of(Scheduled.class, Schedules.class))) {
            Map<Method, Set<Scheduled>> annotatedMethods = MethodIntrospector.selectMethods(targetClass, method -> {
                Set<Scheduled> scheduledAnnotations = AnnotatedElementUtils.getMergedRepeatableAnnotations(method, Scheduled.class, Schedules.class);
                if (scheduledAnnotations.isEmpty()) {
                    return null;
                }
                return scheduledAnnotations;
            });
            if (annotatedMethods.isEmpty()) {
                this.nonAnnotatedClasses.add(targetClass);
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("No @Scheduled annotations found on bean class: " + targetClass);
                }
            } else {
                annotatedMethods.forEach((method2, scheduledAnnotations) -> {
                    scheduledAnnotations.forEach(scheduled -> {
                        processScheduled(scheduled, method2, bean);
                    });
                });
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace(annotatedMethods.size() + " @Scheduled methods processed on bean '" + beanName + "': " + annotatedMethods);
                }
            }
        }
        return bean;
    }

    protected void processScheduled(Scheduled scheduled, Method method, Object bean) {
        if (reactiveStreamsPresent && ScheduledAnnotationReactiveSupport.isReactive(method)) {
            processScheduledAsync(scheduled, method, bean);
        } else {
            processScheduledSync(scheduled, method, bean);
        }
    }

    private void processScheduledSync(Scheduled scheduled, Method method, Object bean) {
        try {
            Runnable task = createRunnable(bean, method, scheduled.scheduler());
            processScheduledTask(scheduled, task, method, bean);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Could not create recurring task for @Scheduled method '" + method.getName() + "': " + ex.getMessage());
        }
    }

    private void processScheduledAsync(Scheduled scheduled, Method method, Object bean) {
        try {
            ScheduledTaskRegistrar scheduledTaskRegistrar = this.registrar;
            Objects.requireNonNull(scheduledTaskRegistrar);
            Runnable task = ScheduledAnnotationReactiveSupport.createSubscriptionRunnable(method, bean, scheduled, scheduledTaskRegistrar::getObservationRegistry, this.reactiveSubscriptions.computeIfAbsent(bean, k -> {
                return new CopyOnWriteArrayList();
            }));
            processScheduledTask(scheduled, task, method, bean);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Could not create recurring task for @Scheduled method '" + method.getName() + "': " + ex.getMessage());
        }
    }

    private void processScheduledTask(Scheduled scheduled, Runnable runnable, Method method, Object bean) {
        CronTrigger trigger;
        try {
            boolean processedSchedule = false;
            Set<ScheduledTask> tasks = new LinkedHashSet<>(4);
            Duration initialDelay = toDuration(scheduled.initialDelay(), scheduled.timeUnit());
            String initialDelayString = scheduled.initialDelayString();
            if (StringUtils.hasText(initialDelayString)) {
                Assert.isTrue(initialDelay.isNegative(), "Specify 'initialDelay' or 'initialDelayString', not both");
                if (this.embeddedValueResolver != null) {
                    initialDelayString = this.embeddedValueResolver.resolveStringValue(initialDelayString);
                }
                if (StringUtils.hasLength(initialDelayString)) {
                    try {
                        initialDelay = toDuration(initialDelayString, scheduled.timeUnit());
                    } catch (RuntimeException e) {
                        throw new IllegalArgumentException("Invalid initialDelayString value \"" + initialDelayString + "\" - cannot parse into long");
                    }
                }
            }
            String cron = scheduled.cron();
            if (StringUtils.hasText(cron)) {
                String zone = scheduled.zone();
                if (this.embeddedValueResolver != null) {
                    cron = this.embeddedValueResolver.resolveStringValue(cron);
                    zone = this.embeddedValueResolver.resolveStringValue(zone);
                }
                if (StringUtils.hasLength(cron)) {
                    Assert.isTrue(initialDelay.isNegative(), "'initialDelay' not supported for cron triggers");
                    processedSchedule = true;
                    if (!"-".equals(cron)) {
                        if (StringUtils.hasText(zone)) {
                            trigger = new CronTrigger(cron, StringUtils.parseTimeZoneString(zone));
                        } else {
                            trigger = new CronTrigger(cron);
                        }
                        tasks.add(this.registrar.scheduleCronTask(new CronTask(runnable, trigger)));
                    }
                }
            }
            Duration delayToUse = initialDelay.isNegative() ? Duration.ZERO : initialDelay;
            Duration fixedDelay = toDuration(scheduled.fixedDelay(), scheduled.timeUnit());
            if (!fixedDelay.isNegative()) {
                Assert.isTrue(!processedSchedule, "Exactly one of the 'cron', 'fixedDelay' or 'fixedRate' attributes is required");
                processedSchedule = true;
                tasks.add(this.registrar.scheduleFixedDelayTask(new FixedDelayTask(runnable, fixedDelay, delayToUse)));
            }
            String fixedDelayString = scheduled.fixedDelayString();
            if (StringUtils.hasText(fixedDelayString)) {
                if (this.embeddedValueResolver != null) {
                    fixedDelayString = this.embeddedValueResolver.resolveStringValue(fixedDelayString);
                }
                if (StringUtils.hasLength(fixedDelayString)) {
                    Assert.isTrue(!processedSchedule, "Exactly one of the 'cron', 'fixedDelay' or 'fixedRate' attributes is required");
                    processedSchedule = true;
                    try {
                        tasks.add(this.registrar.scheduleFixedDelayTask(new FixedDelayTask(runnable, toDuration(fixedDelayString, scheduled.timeUnit()), delayToUse)));
                    } catch (RuntimeException e2) {
                        throw new IllegalArgumentException("Invalid fixedDelayString value \"" + fixedDelayString + "\" - cannot parse into long");
                    }
                }
            }
            Duration fixedRate = toDuration(scheduled.fixedRate(), scheduled.timeUnit());
            if (!fixedRate.isNegative()) {
                Assert.isTrue(!processedSchedule, "Exactly one of the 'cron', 'fixedDelay' or 'fixedRate' attributes is required");
                processedSchedule = true;
                tasks.add(this.registrar.scheduleFixedRateTask(new FixedRateTask(runnable, fixedRate, delayToUse)));
            }
            String fixedRateString = scheduled.fixedRateString();
            if (StringUtils.hasText(fixedRateString)) {
                if (this.embeddedValueResolver != null) {
                    fixedRateString = this.embeddedValueResolver.resolveStringValue(fixedRateString);
                }
                if (StringUtils.hasLength(fixedRateString)) {
                    Assert.isTrue(!processedSchedule, "Exactly one of the 'cron', 'fixedDelay' or 'fixedRate' attributes is required");
                    processedSchedule = true;
                    try {
                        tasks.add(this.registrar.scheduleFixedRateTask(new FixedRateTask(runnable, toDuration(fixedRateString, scheduled.timeUnit()), delayToUse)));
                    } catch (RuntimeException e3) {
                        throw new IllegalArgumentException("Invalid fixedRateString value \"" + fixedRateString + "\" - cannot parse into long");
                    }
                }
            }
            if (!processedSchedule) {
                if (initialDelay.isNegative()) {
                    throw new IllegalArgumentException("One-time task only supported with specified initial delay");
                }
                tasks.add(this.registrar.scheduleOneTimeTask(new OneTimeTask(runnable, delayToUse)));
            }
            synchronized (this.scheduledTasks) {
                Set<ScheduledTask> regTasks = this.scheduledTasks.computeIfAbsent(bean, key -> {
                    return new LinkedHashSet(4);
                });
                regTasks.addAll(tasks);
            }
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Encountered invalid @Scheduled method '" + method.getName() + "': " + ex.getMessage());
        }
    }

    protected Runnable createRunnable(Object target, Method method, @Nullable String qualifier) {
        Runnable runnable = createRunnable(target, method);
        if (runnable != null) {
            return runnable;
        }
        Assert.isTrue(method.getParameterCount() == 0, "Only no-arg methods may be annotated with @Scheduled");
        Method invocableMethod = AopUtils.selectInvocableMethod(method, target.getClass());
        ScheduledTaskRegistrar scheduledTaskRegistrar = this.registrar;
        Objects.requireNonNull(scheduledTaskRegistrar);
        return new ScheduledMethodRunnable(target, invocableMethod, qualifier, scheduledTaskRegistrar::getObservationRegistry);
    }

    @Nullable
    @Deprecated(since = "6.1")
    protected Runnable createRunnable(Object target, Method method) {
        return null;
    }

    private static Duration toDuration(long value, TimeUnit timeUnit) {
        try {
            return Duration.of(value, timeUnit.toChronoUnit());
        } catch (Exception ex) {
            ex.getMessage();
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Unsupported unit " + timeUnit + " for value \"" + value + "\": " + illegalArgumentException);
            throw illegalArgumentException;
        }
    }

    private static Duration toDuration(String value, TimeUnit timeUnit) {
        if (isDurationString(value)) {
            return Duration.parse(value);
        }
        return toDuration(Long.parseLong(value), timeUnit);
    }

    private static boolean isDurationString(String value) {
        return value.length() > 1 && (isP(value.charAt(0)) || isP(value.charAt(1)));
    }

    private static boolean isP(char ch2) {
        return ch2 == 'P' || ch2 == 'p';
    }

    @Override // org.springframework.scheduling.config.ScheduledTaskHolder
    public Set<ScheduledTask> getScheduledTasks() {
        Set<ScheduledTask> result = new LinkedHashSet<>();
        synchronized (this.scheduledTasks) {
            Collection<Set<ScheduledTask>> allTasks = this.scheduledTasks.values();
            for (Set<ScheduledTask> tasks : allTasks) {
                result.addAll(tasks);
            }
        }
        result.addAll(this.registrar.getScheduledTasks());
        return result;
    }

    @Override // org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor
    public void postProcessBeforeDestruction(Object bean, String beanName) {
        Set<ScheduledTask> tasks;
        List<Runnable> liveSubscriptions;
        synchronized (this.scheduledTasks) {
            tasks = this.scheduledTasks.remove(bean);
            liveSubscriptions = this.reactiveSubscriptions.remove(bean);
        }
        if (tasks != null) {
            for (ScheduledTask task : tasks) {
                task.cancel(false);
            }
        }
        if (liveSubscriptions != null) {
            for (Runnable subscription : liveSubscriptions) {
                subscription.run();
            }
        }
    }

    @Override // org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor
    public boolean requiresDestruction(Object bean) {
        boolean z;
        synchronized (this.scheduledTasks) {
            z = this.scheduledTasks.containsKey(bean) || this.reactiveSubscriptions.containsKey(bean);
        }
        return z;
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        synchronized (this.scheduledTasks) {
            Collection<Set<ScheduledTask>> allTasks = this.scheduledTasks.values();
            for (Set<ScheduledTask> tasks : allTasks) {
                for (ScheduledTask task : tasks) {
                    task.cancel(false);
                }
            }
            this.scheduledTasks.clear();
            Collection<List<Runnable>> allLiveSubscriptions = this.reactiveSubscriptions.values();
            for (List<Runnable> liveSubscriptions : allLiveSubscriptions) {
                for (Runnable liveSubscription : liveSubscriptions) {
                    liveSubscription.run();
                }
            }
        }
        this.registrar.destroy();
        if (this.localScheduler != null) {
            this.localScheduler.destroy();
        }
    }

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(ApplicationContextEvent event) {
        if (event.getApplicationContext() == this.applicationContext) {
            if (event instanceof ContextRefreshedEvent) {
                finishRegistration();
                return;
            }
            if (event instanceof ContextClosedEvent) {
                synchronized (this.scheduledTasks) {
                    Collection<Set<ScheduledTask>> allTasks = this.scheduledTasks.values();
                    for (Set<ScheduledTask> tasks : allTasks) {
                        for (ScheduledTask task : tasks) {
                            task.cancel(false);
                        }
                    }
                }
            }
        }
    }
}
