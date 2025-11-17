package org.springframework.core.task.support;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/task/support/TaskExecutorAdapter.class */
public class TaskExecutorAdapter implements AsyncListenableTaskExecutor {
    private final Executor concurrentExecutor;

    @Nullable
    private TaskDecorator taskDecorator;

    public TaskExecutorAdapter(Executor concurrentExecutor) {
        Assert.notNull(concurrentExecutor, "Executor must not be null");
        this.concurrentExecutor = concurrentExecutor;
    }

    public final void setTaskDecorator(TaskDecorator taskDecorator) {
        this.taskDecorator = taskDecorator;
    }

    @Override // org.springframework.core.task.TaskExecutor, java.util.concurrent.Executor
    public void execute(Runnable task) {
        try {
            doExecute(this.concurrentExecutor, this.taskDecorator, task);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(this.concurrentExecutor, task, ex);
        }
    }

    @Override // org.springframework.core.task.AsyncTaskExecutor
    public Future<?> submit(Runnable task) {
        try {
            if (this.taskDecorator == null) {
                Executor executor = this.concurrentExecutor;
                if (executor instanceof ExecutorService) {
                    ExecutorService executorService = (ExecutorService) executor;
                    return executorService.submit(task);
                }
            }
            FutureTask<Object> future = new FutureTask<>(task, null);
            doExecute(this.concurrentExecutor, this.taskDecorator, future);
            return future;
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(this.concurrentExecutor, task, ex);
        }
    }

    @Override // org.springframework.core.task.AsyncTaskExecutor
    public <T> Future<T> submit(Callable<T> task) {
        try {
            if (this.taskDecorator == null) {
                Executor executor = this.concurrentExecutor;
                if (executor instanceof ExecutorService) {
                    ExecutorService executorService = (ExecutorService) executor;
                    return executorService.submit(task);
                }
            }
            FutureTask<T> future = new FutureTask<>(task);
            doExecute(this.concurrentExecutor, this.taskDecorator, future);
            return future;
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(this.concurrentExecutor, task, ex);
        }
    }

    @Override // org.springframework.core.task.AsyncListenableTaskExecutor
    public ListenableFuture<?> submitListenable(Runnable task) {
        try {
            ListenableFutureTask<Object> future = new ListenableFutureTask<>(task, null);
            doExecute(this.concurrentExecutor, this.taskDecorator, future);
            return future;
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(this.concurrentExecutor, task, ex);
        }
    }

    @Override // org.springframework.core.task.AsyncListenableTaskExecutor
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        try {
            ListenableFutureTask<T> future = new ListenableFutureTask<>(task);
            doExecute(this.concurrentExecutor, this.taskDecorator, future);
            return future;
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(this.concurrentExecutor, task, ex);
        }
    }

    protected void doExecute(Executor concurrentExecutor, @Nullable TaskDecorator taskDecorator, Runnable runnable) throws RejectedExecutionException {
        concurrentExecutor.execute(taskDecorator != null ? taskDecorator.decorate(runnable) : runnable);
    }
}
