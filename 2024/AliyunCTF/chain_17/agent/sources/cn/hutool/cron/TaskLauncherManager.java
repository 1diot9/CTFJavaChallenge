package cn.hutool.cron;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/TaskLauncherManager.class */
public class TaskLauncherManager implements Serializable {
    private static final long serialVersionUID = 1;
    protected Scheduler scheduler;
    protected final List<TaskLauncher> launchers = new ArrayList();

    public TaskLauncherManager(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public TaskLauncher spawnLauncher(long millis) {
        TaskLauncher launcher = new TaskLauncher(this.scheduler, millis);
        synchronized (this.launchers) {
            this.launchers.add(launcher);
        }
        this.scheduler.threadExecutor.execute(launcher);
        return launcher;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void notifyLauncherCompleted(TaskLauncher launcher) {
        synchronized (this.launchers) {
            this.launchers.remove(launcher);
        }
    }
}
