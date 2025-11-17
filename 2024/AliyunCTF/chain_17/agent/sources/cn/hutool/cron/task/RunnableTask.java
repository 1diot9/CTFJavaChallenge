package cn.hutool.cron.task;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/task/RunnableTask.class */
public class RunnableTask implements Task {
    private final Runnable runnable;

    public RunnableTask(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override // cn.hutool.cron.task.Task
    public void execute() {
        this.runnable.run();
    }
}
