package org.springframework.scheduling.config;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.SchedulingAwareRunnable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;
import org.springframework.util.function.SingletonSupplier;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/config/TaskSchedulerRouter.class */
public class TaskSchedulerRouter implements TaskScheduler, BeanNameAware, BeanFactoryAware, DisposableBean {
    public static final String DEFAULT_TASK_SCHEDULER_BEAN_NAME = "taskScheduler";
    protected static final Log logger = LogFactory.getLog((Class<?>) TaskSchedulerRouter.class);

    @Nullable
    private String beanName;

    @Nullable
    private BeanFactory beanFactory;

    @Nullable
    private StringValueResolver embeddedValueResolver;
    private final Supplier<TaskScheduler> defaultScheduler = SingletonSupplier.of(this::determineDefaultScheduler);

    @Nullable
    private volatile ScheduledExecutorService localExecutor;

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(@Nullable String name) {
        this.beanName = name;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(@Nullable BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        if (beanFactory instanceof ConfigurableBeanFactory) {
            ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
            this.embeddedValueResolver = new EmbeddedValueResolver(configurableBeanFactory);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        return determineTargetScheduler(task).schedule(task, trigger);
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> schedule(Runnable task, Instant startTime) {
        return determineTargetScheduler(task).schedule(task, startTime);
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Instant startTime, Duration period) {
        return determineTargetScheduler(task).scheduleAtFixedRate(task, startTime, period);
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Duration period) {
        return determineTargetScheduler(task).scheduleAtFixedRate(task, period);
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Instant startTime, Duration delay) {
        return determineTargetScheduler(task).scheduleWithFixedDelay(task, startTime, delay);
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Duration delay) {
        return determineTargetScheduler(task).scheduleWithFixedDelay(task, delay);
    }

    protected TaskScheduler determineTargetScheduler(Runnable task) {
        String qualifier = determineQualifier(task);
        if (this.embeddedValueResolver != null && StringUtils.hasLength(qualifier)) {
            qualifier = this.embeddedValueResolver.resolveStringValue(qualifier);
        }
        if (StringUtils.hasLength(qualifier)) {
            return determineQualifiedScheduler(qualifier);
        }
        return this.defaultScheduler.get();
    }

    @Nullable
    protected String determineQualifier(Runnable task) {
        if (!(task instanceof SchedulingAwareRunnable)) {
            return null;
        }
        SchedulingAwareRunnable sar = (SchedulingAwareRunnable) task;
        return sar.getQualifier();
    }

    protected TaskScheduler determineQualifiedScheduler(String qualifier) {
        Assert.state(this.beanFactory != null, "BeanFactory must be set to find qualified scheduler");
        try {
            return (TaskScheduler) BeanFactoryAnnotationUtils.qualifiedBeanOfType(this.beanFactory, TaskScheduler.class, qualifier);
        } catch (BeanNotOfRequiredTypeException | NoSuchBeanDefinitionException e) {
            return new ConcurrentTaskScheduler((ScheduledExecutorService) BeanFactoryAnnotationUtils.qualifiedBeanOfType(this.beanFactory, ScheduledExecutorService.class, qualifier));
        }
    }

    protected TaskScheduler determineDefaultScheduler() {
        Assert.state(this.beanFactory != null, "BeanFactory must be set to find default scheduler");
        try {
            return (TaskScheduler) resolveSchedulerBean(this.beanFactory, TaskScheduler.class, false);
        } catch (NoUniqueBeanDefinitionException ex) {
            if (logger.isTraceEnabled()) {
                logger.trace("Could not find unique TaskScheduler bean - attempting to resolve by name: " + ex.getMessage());
            }
            try {
                return (TaskScheduler) resolveSchedulerBean(this.beanFactory, TaskScheduler.class, true);
            } catch (NoSuchBeanDefinitionException e) {
                if (logger.isInfoEnabled()) {
                    logger.info("More than one TaskScheduler bean exists within the context, and none is named 'taskScheduler'. Mark one of them as primary or name it 'taskScheduler' (possibly as an alias); or implement the SchedulingConfigurer interface and call ScheduledTaskRegistrar#setScheduler explicitly within the configureTasks() callback: " + ex.getBeanNamesFound());
                }
                ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
                this.localExecutor = localExecutor;
                return new ConcurrentTaskScheduler(localExecutor);
            }
        } catch (NoSuchBeanDefinitionException ex2) {
            if (logger.isTraceEnabled()) {
                logger.trace("Could not find default TaskScheduler bean - attempting to find ScheduledExecutorService: " + ex2.getMessage());
            }
            try {
                return new ConcurrentTaskScheduler((ScheduledExecutorService) resolveSchedulerBean(this.beanFactory, ScheduledExecutorService.class, false));
            } catch (NoUniqueBeanDefinitionException ex22) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Could not find unique ScheduledExecutorService bean - attempting to resolve by name: " + ex22.getMessage());
                }
                try {
                    return new ConcurrentTaskScheduler((ScheduledExecutorService) resolveSchedulerBean(this.beanFactory, ScheduledExecutorService.class, true));
                } catch (NoSuchBeanDefinitionException e2) {
                    if (logger.isInfoEnabled()) {
                        logger.info("More than one ScheduledExecutorService bean exists within the context, and none is named 'taskScheduler'. Mark one of them as primary or name it 'taskScheduler' (possibly as an alias); or implement the SchedulingConfigurer interface and call ScheduledTaskRegistrar#setScheduler explicitly within the configureTasks() callback: " + ex22.getBeanNamesFound());
                    }
                    ScheduledExecutorService localExecutor2 = Executors.newSingleThreadScheduledExecutor();
                    this.localExecutor = localExecutor2;
                    return new ConcurrentTaskScheduler(localExecutor2);
                }
            } catch (NoSuchBeanDefinitionException ex23) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Could not find default ScheduledExecutorService bean - falling back to default: " + ex23.getMessage());
                }
                logger.info("No TaskScheduler/ScheduledExecutorService bean found for scheduled processing");
                ScheduledExecutorService localExecutor22 = Executors.newSingleThreadScheduledExecutor();
                this.localExecutor = localExecutor22;
                return new ConcurrentTaskScheduler(localExecutor22);
            }
        }
    }

    private <T> T resolveSchedulerBean(BeanFactory beanFactory, Class<T> cls, boolean z) {
        if (z) {
            T t = (T) beanFactory.getBean("taskScheduler", cls);
            if (this.beanName != null) {
                BeanFactory beanFactory2 = this.beanFactory;
                if (beanFactory2 instanceof ConfigurableBeanFactory) {
                    ((ConfigurableBeanFactory) beanFactory2).registerDependentBean("taskScheduler", this.beanName);
                }
            }
            return t;
        }
        if (beanFactory instanceof AutowireCapableBeanFactory) {
            NamedBeanHolder<T> resolveNamedBean = ((AutowireCapableBeanFactory) beanFactory).resolveNamedBean(cls);
            if (this.beanName != null && (beanFactory instanceof ConfigurableBeanFactory)) {
                ((ConfigurableBeanFactory) beanFactory).registerDependentBean(resolveNamedBean.getBeanName(), this.beanName);
            }
            return resolveNamedBean.getBeanInstance();
        }
        return (T) beanFactory.getBean(cls);
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        ScheduledExecutorService localExecutor = this.localExecutor;
        if (localExecutor != null) {
            localExecutor.shutdownNow();
        }
    }
}
