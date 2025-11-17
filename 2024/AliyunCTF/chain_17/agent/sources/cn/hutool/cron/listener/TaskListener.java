package cn.hutool.cron.listener;

import cn.hutool.cron.TaskExecutor;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/listener/TaskListener.class */
public interface TaskListener {
    void onStart(TaskExecutor taskExecutor);

    void onSucceeded(TaskExecutor taskExecutor);

    void onFailed(TaskExecutor taskExecutor, Throwable th);
}
