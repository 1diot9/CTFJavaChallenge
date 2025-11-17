package cn.hutool.cron;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/TaskLauncher.class */
public class TaskLauncher implements Runnable {
    private final Scheduler scheduler;
    private final long millis;

    public TaskLauncher(Scheduler scheduler, long millis) {
        this.scheduler = scheduler;
        this.millis = millis;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.scheduler.taskTable.executeTaskIfMatch(this.scheduler, this.millis);
        this.scheduler.taskLauncherManager.notifyLauncherCompleted(this);
    }
}
