package cn.hutool.cron;

import cn.hutool.cron.task.CronTask;
import cn.hutool.cron.task.Task;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/TaskExecutor.class */
public class TaskExecutor implements Runnable {
    private final Scheduler scheduler;
    private final CronTask task;

    public Task getTask() {
        return this.task.getRaw();
    }

    public CronTask getCronTask() {
        return this.task;
    }

    public TaskExecutor(Scheduler scheduler, CronTask task) {
        this.scheduler = scheduler;
        this.task = task;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            this.scheduler.listenerManager.notifyTaskStart(this);
            this.task.execute();
            this.scheduler.listenerManager.notifyTaskSucceeded(this);
        } catch (Exception e) {
            this.scheduler.listenerManager.notifyTaskFailed(this, e);
        } finally {
            this.scheduler.taskExecutorManager.notifyExecutorCompleted(this);
        }
    }
}
