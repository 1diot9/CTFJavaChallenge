package org.springframework.scheduling.config;

import io.micrometer.observation.ObservationRegistry;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/config/ScheduledTaskRegistrar.class */
public class ScheduledTaskRegistrar implements ScheduledTaskHolder, InitializingBean, DisposableBean {
    public static final String CRON_DISABLED = "-";

    @Nullable
    private TaskScheduler taskScheduler;

    @Nullable
    private ScheduledExecutorService localExecutor;

    @Nullable
    private ObservationRegistry observationRegistry;

    @Nullable
    private List<TriggerTask> triggerTasks;

    @Nullable
    private List<CronTask> cronTasks;

    @Nullable
    private List<IntervalTask> fixedRateTasks;

    @Nullable
    private List<IntervalTask> fixedDelayTasks;

    @Nullable
    private List<DelayedTask> oneTimeTasks;
    private final Map<Task, ScheduledTask> unresolvedTasks = new HashMap(16);
    private final Set<ScheduledTask> scheduledTasks = new LinkedHashSet(16);

    public void setTaskScheduler(TaskScheduler taskScheduler) {
        Assert.notNull(taskScheduler, "TaskScheduler must not be null");
        this.taskScheduler = taskScheduler;
    }

    public void setScheduler(@Nullable Object scheduler) {
        if (scheduler == null) {
            this.taskScheduler = null;
            return;
        }
        if (scheduler instanceof TaskScheduler) {
            TaskScheduler ts = (TaskScheduler) scheduler;
            this.taskScheduler = ts;
        } else {
            if (scheduler instanceof ScheduledExecutorService) {
                ScheduledExecutorService ses = (ScheduledExecutorService) scheduler;
                this.taskScheduler = new ConcurrentTaskScheduler(ses);
                return;
            }
            throw new IllegalArgumentException("Unsupported scheduler type: " + scheduler.getClass());
        }
    }

    @Nullable
    public TaskScheduler getScheduler() {
        return this.taskScheduler;
    }

    public void setObservationRegistry(@Nullable ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    @Nullable
    public ObservationRegistry getObservationRegistry() {
        return this.observationRegistry;
    }

    public void setTriggerTasks(Map<Runnable, Trigger> triggerTasks) {
        this.triggerTasks = new ArrayList();
        triggerTasks.forEach((task, trigger) -> {
            addTriggerTask(new TriggerTask(task, trigger));
        });
    }

    public void setTriggerTasksList(List<TriggerTask> triggerTasks) {
        this.triggerTasks = triggerTasks;
    }

    public List<TriggerTask> getTriggerTaskList() {
        return this.triggerTasks != null ? Collections.unmodifiableList(this.triggerTasks) : Collections.emptyList();
    }

    public void setCronTasks(Map<Runnable, String> cronTasks) {
        this.cronTasks = new ArrayList();
        cronTasks.forEach(this::addCronTask);
    }

    public void setCronTasksList(List<CronTask> cronTasks) {
        this.cronTasks = cronTasks;
    }

    public List<CronTask> getCronTaskList() {
        return this.cronTasks != null ? Collections.unmodifiableList(this.cronTasks) : Collections.emptyList();
    }

    public void setFixedRateTasks(Map<Runnable, Long> fixedRateTasks) {
        this.fixedRateTasks = new ArrayList();
        fixedRateTasks.forEach((task, interval) -> {
            addFixedRateTask(task, Duration.ofMillis(interval.longValue()));
        });
    }

    public void setFixedRateTasksList(List<IntervalTask> fixedRateTasks) {
        this.fixedRateTasks = fixedRateTasks;
    }

    public List<IntervalTask> getFixedRateTaskList() {
        return this.fixedRateTasks != null ? Collections.unmodifiableList(this.fixedRateTasks) : Collections.emptyList();
    }

    public void setFixedDelayTasks(Map<Runnable, Long> fixedDelayTasks) {
        this.fixedDelayTasks = new ArrayList();
        fixedDelayTasks.forEach((task, delay) -> {
            addFixedDelayTask(task, Duration.ofMillis(delay.longValue()));
        });
    }

    public void setFixedDelayTasksList(List<IntervalTask> fixedDelayTasks) {
        this.fixedDelayTasks = fixedDelayTasks;
    }

    public List<IntervalTask> getFixedDelayTaskList() {
        return this.fixedDelayTasks != null ? Collections.unmodifiableList(this.fixedDelayTasks) : Collections.emptyList();
    }

    public void addTriggerTask(Runnable task, Trigger trigger) {
        addTriggerTask(new TriggerTask(task, trigger));
    }

    public void addTriggerTask(TriggerTask task) {
        if (this.triggerTasks == null) {
            this.triggerTasks = new ArrayList();
        }
        this.triggerTasks.add(task);
    }

    public void addCronTask(Runnable task, String expression) {
        if (!"-".equals(expression)) {
            addCronTask(new CronTask(task, expression));
        }
    }

    public void addCronTask(CronTask task) {
        if (this.cronTasks == null) {
            this.cronTasks = new ArrayList();
        }
        this.cronTasks.add(task);
    }

    @Deprecated(since = "6.0")
    public void addFixedRateTask(Runnable task, long interval) {
        addFixedRateTask(new IntervalTask(task, interval));
    }

    public void addFixedRateTask(Runnable task, Duration interval) {
        addFixedRateTask(new IntervalTask(task, interval));
    }

    public void addFixedRateTask(IntervalTask task) {
        if (this.fixedRateTasks == null) {
            this.fixedRateTasks = new ArrayList();
        }
        this.fixedRateTasks.add(task);
    }

    @Deprecated(since = "6.0")
    public void addFixedDelayTask(Runnable task, long interval) {
        addFixedDelayTask(new IntervalTask(task, interval));
    }

    public void addFixedDelayTask(Runnable task, Duration interval) {
        addFixedDelayTask(new IntervalTask(task, interval));
    }

    public void addFixedDelayTask(IntervalTask task) {
        if (this.fixedDelayTasks == null) {
            this.fixedDelayTasks = new ArrayList();
        }
        this.fixedDelayTasks.add(task);
    }

    public void addOneTimeTask(Runnable task, Duration initialDelay) {
        addOneTimeTask(new OneTimeTask(task, initialDelay));
    }

    public void addOneTimeTask(DelayedTask task) {
        if (this.oneTimeTasks == null) {
            this.oneTimeTasks = new ArrayList();
        }
        this.oneTimeTasks.add(task);
    }

    public boolean hasTasks() {
        return (CollectionUtils.isEmpty(this.triggerTasks) && CollectionUtils.isEmpty(this.cronTasks) && CollectionUtils.isEmpty(this.fixedRateTasks) && CollectionUtils.isEmpty(this.fixedDelayTasks) && CollectionUtils.isEmpty(this.oneTimeTasks)) ? false : true;
    }

    public void afterPropertiesSet() {
        scheduleTasks();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void scheduleTasks() {
        if (this.taskScheduler == null) {
            this.localExecutor = Executors.newSingleThreadScheduledExecutor();
            this.taskScheduler = new ConcurrentTaskScheduler(this.localExecutor);
        }
        if (this.triggerTasks != null) {
            Iterator<TriggerTask> it = this.triggerTasks.iterator();
            while (it.hasNext()) {
                addScheduledTask(scheduleTriggerTask(it.next()));
            }
        }
        if (this.cronTasks != null) {
            Iterator<CronTask> it2 = this.cronTasks.iterator();
            while (it2.hasNext()) {
                addScheduledTask(scheduleCronTask(it2.next()));
            }
        }
        if (this.fixedRateTasks != null) {
            for (IntervalTask task : this.fixedRateTasks) {
                if (task instanceof FixedRateTask) {
                    FixedRateTask fixedRateTask = (FixedRateTask) task;
                    addScheduledTask(scheduleFixedRateTask(fixedRateTask));
                } else {
                    addScheduledTask(scheduleFixedRateTask(new FixedRateTask(task)));
                }
            }
        }
        if (this.fixedDelayTasks != null) {
            for (IntervalTask task2 : this.fixedDelayTasks) {
                if (task2 instanceof FixedDelayTask) {
                    FixedDelayTask fixedDelayTask = (FixedDelayTask) task2;
                    addScheduledTask(scheduleFixedDelayTask(fixedDelayTask));
                } else {
                    addScheduledTask(scheduleFixedDelayTask(new FixedDelayTask(task2)));
                }
            }
        }
        if (this.oneTimeTasks != null) {
            for (DelayedTask task3 : this.oneTimeTasks) {
                if (task3 instanceof OneTimeTask) {
                    OneTimeTask oneTimeTask = (OneTimeTask) task3;
                    addScheduledTask(scheduleOneTimeTask(oneTimeTask));
                } else {
                    addScheduledTask(scheduleOneTimeTask(new OneTimeTask(task3)));
                }
            }
        }
    }

    private void addScheduledTask(@Nullable ScheduledTask task) {
        if (task != null) {
            this.scheduledTasks.add(task);
        }
    }

    @Nullable
    public ScheduledTask scheduleTriggerTask(TriggerTask task) {
        ScheduledTask scheduledTask = this.unresolvedTasks.remove(task);
        boolean newTask = false;
        if (scheduledTask == null) {
            scheduledTask = new ScheduledTask(task);
            newTask = true;
        }
        if (this.taskScheduler != null) {
            scheduledTask.future = this.taskScheduler.schedule(task.getRunnable(), task.getTrigger());
        } else {
            addTriggerTask(task);
            this.unresolvedTasks.put(task, scheduledTask);
        }
        if (newTask) {
            return scheduledTask;
        }
        return null;
    }

    @Nullable
    public ScheduledTask scheduleCronTask(CronTask task) {
        ScheduledTask scheduledTask = this.unresolvedTasks.remove(task);
        boolean newTask = false;
        if (scheduledTask == null) {
            scheduledTask = new ScheduledTask(task);
            newTask = true;
        }
        if (this.taskScheduler != null) {
            scheduledTask.future = this.taskScheduler.schedule(task.getRunnable(), task.getTrigger());
        } else {
            addCronTask(task);
            this.unresolvedTasks.put(task, scheduledTask);
        }
        if (newTask) {
            return scheduledTask;
        }
        return null;
    }

    @Nullable
    public ScheduledTask scheduleFixedRateTask(FixedRateTask task) {
        ScheduledTask scheduledTask = this.unresolvedTasks.remove(task);
        boolean newTask = false;
        if (scheduledTask == null) {
            scheduledTask = new ScheduledTask(task);
            newTask = true;
        }
        if (this.taskScheduler != null) {
            Duration initialDelay = task.getInitialDelayDuration();
            if (initialDelay.toNanos() > 0) {
                Instant startTime = this.taskScheduler.getClock().instant().plus((TemporalAmount) initialDelay);
                scheduledTask.future = this.taskScheduler.scheduleAtFixedRate(task.getRunnable(), startTime, task.getIntervalDuration());
            } else {
                scheduledTask.future = this.taskScheduler.scheduleAtFixedRate(task.getRunnable(), task.getIntervalDuration());
            }
        } else {
            addFixedRateTask(task);
            this.unresolvedTasks.put(task, scheduledTask);
        }
        if (newTask) {
            return scheduledTask;
        }
        return null;
    }

    @Nullable
    public ScheduledTask scheduleFixedDelayTask(FixedDelayTask task) {
        ScheduledTask scheduledTask = this.unresolvedTasks.remove(task);
        boolean newTask = false;
        if (scheduledTask == null) {
            scheduledTask = new ScheduledTask(task);
            newTask = true;
        }
        if (this.taskScheduler != null) {
            Duration initialDelay = task.getInitialDelayDuration();
            if (!initialDelay.isNegative()) {
                Instant startTime = this.taskScheduler.getClock().instant().plus((TemporalAmount) task.getInitialDelayDuration());
                scheduledTask.future = this.taskScheduler.scheduleWithFixedDelay(task.getRunnable(), startTime, task.getIntervalDuration());
            } else {
                scheduledTask.future = this.taskScheduler.scheduleWithFixedDelay(task.getRunnable(), task.getIntervalDuration());
            }
        } else {
            addFixedDelayTask(task);
            this.unresolvedTasks.put(task, scheduledTask);
        }
        if (newTask) {
            return scheduledTask;
        }
        return null;
    }

    @Nullable
    public ScheduledTask scheduleOneTimeTask(OneTimeTask task) {
        ScheduledTask scheduledTask = this.unresolvedTasks.remove(task);
        boolean newTask = false;
        if (scheduledTask == null) {
            scheduledTask = new ScheduledTask(task);
            newTask = true;
        }
        if (this.taskScheduler != null) {
            Instant startTime = this.taskScheduler.getClock().instant().plus((TemporalAmount) task.getInitialDelayDuration());
            scheduledTask.future = this.taskScheduler.schedule(task.getRunnable(), startTime);
        } else {
            addOneTimeTask(task);
            this.unresolvedTasks.put(task, scheduledTask);
        }
        if (newTask) {
            return scheduledTask;
        }
        return null;
    }

    @Override // org.springframework.scheduling.config.ScheduledTaskHolder
    public Set<ScheduledTask> getScheduledTasks() {
        return Collections.unmodifiableSet(this.scheduledTasks);
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        for (ScheduledTask task : this.scheduledTasks) {
            task.cancel(false);
        }
        if (this.localExecutor != null) {
            this.localExecutor.shutdownNow();
        }
    }
}
