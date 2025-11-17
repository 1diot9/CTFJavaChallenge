package cn.hutool.cron.listener;

import cn.hutool.cron.TaskExecutor;
import cn.hutool.log.StaticLog;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/listener/TaskListenerManager.class */
public class TaskListenerManager implements Serializable {
    private static final long serialVersionUID = 1;
    private final List<TaskListener> listeners = new ArrayList();

    public TaskListenerManager addListener(TaskListener listener) {
        synchronized (this.listeners) {
            this.listeners.add(listener);
        }
        return this;
    }

    public TaskListenerManager removeListener(TaskListener listener) {
        synchronized (this.listeners) {
            this.listeners.remove(listener);
        }
        return this;
    }

    public void notifyTaskStart(TaskExecutor executor) {
        synchronized (this.listeners) {
            for (TaskListener taskListener : this.listeners) {
                if (null != taskListener) {
                    taskListener.onStart(executor);
                }
            }
        }
    }

    public void notifyTaskSucceeded(TaskExecutor executor) {
        synchronized (this.listeners) {
            for (TaskListener listener : this.listeners) {
                listener.onSucceeded(executor);
            }
        }
    }

    public void notifyTaskFailed(TaskExecutor executor, Throwable exception) {
        synchronized (this.listeners) {
            int size = this.listeners.size();
            if (size > 0) {
                for (TaskListener listener : this.listeners) {
                    listener.onFailed(executor, exception);
                }
            } else {
                StaticLog.error(exception, exception.getMessage(), new Object[0]);
            }
        }
    }
}
