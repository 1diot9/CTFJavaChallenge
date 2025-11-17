package org.springframework.scheduling.concurrent;

import jakarta.enterprise.concurrent.ManagedExecutors;
import jakarta.enterprise.concurrent.ManagedTaskListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.SchedulingAwareRunnable;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.util.ClassUtils;
import org.springframework.util.concurrent.ListenableFuture;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/concurrent/ConcurrentTaskExecutor.class */
public class ConcurrentTaskExecutor implements AsyncListenableTaskExecutor, SchedulingTaskExecutor {
    private static final Executor STUB_EXECUTOR = task -> {
        throw new IllegalStateException("Executor not configured");
    };

    @Nullable
    private static Class<?> managedExecutorServiceClass;
    private Executor concurrentExecutor;
    private TaskExecutorAdapter adaptedExecutor;

    @Nullable
    private TaskDecorator taskDecorator;

    static {
        try {
            managedExecutorServiceClass = ClassUtils.forName("jakarta.enterprise.concurrent.ManagedExecutorService", ConcurrentTaskScheduler.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            managedExecutorServiceClass = null;
        }
    }

    @Deprecated(since = "6.1")
    public ConcurrentTaskExecutor() {
        this.concurrentExecutor = STUB_EXECUTOR;
        this.adaptedExecutor = new TaskExecutorAdapter(STUB_EXECUTOR);
        this.concurrentExecutor = Executors.newSingleThreadExecutor();
        this.adaptedExecutor = new TaskExecutorAdapter(this.concurrentExecutor);
    }

    public ConcurrentTaskExecutor(@Nullable Executor executor) {
        this.concurrentExecutor = STUB_EXECUTOR;
        this.adaptedExecutor = new TaskExecutorAdapter(STUB_EXECUTOR);
        if (executor != null) {
            setConcurrentExecutor(executor);
        }
    }

    public final void setConcurrentExecutor(Executor executor) {
        this.concurrentExecutor = executor;
        this.adaptedExecutor = getAdaptedExecutor(this.concurrentExecutor);
    }

    public final Executor getConcurrentExecutor() {
        return this.concurrentExecutor;
    }

    public final void setTaskDecorator(TaskDecorator taskDecorator) {
        this.taskDecorator = taskDecorator;
        this.adaptedExecutor.setTaskDecorator(taskDecorator);
    }

    @Override // org.springframework.core.task.TaskExecutor, java.util.concurrent.Executor
    public void execute(Runnable task) {
        this.adaptedExecutor.execute(task);
    }

    @Override // org.springframework.core.task.AsyncTaskExecutor
    @Deprecated
    public void execute(Runnable task, long startTimeout) {
        this.adaptedExecutor.execute(task, startTimeout);
    }

    @Override // org.springframework.core.task.AsyncTaskExecutor
    public Future<?> submit(Runnable task) {
        return this.adaptedExecutor.submit(task);
    }

    @Override // org.springframework.core.task.AsyncTaskExecutor
    public <T> Future<T> submit(Callable<T> task) {
        return this.adaptedExecutor.submit(task);
    }

    @Override // org.springframework.core.task.AsyncListenableTaskExecutor
    public ListenableFuture<?> submitListenable(Runnable task) {
        return this.adaptedExecutor.submitListenable(task);
    }

    @Override // org.springframework.core.task.AsyncListenableTaskExecutor
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        return this.adaptedExecutor.submitListenable(task);
    }

    private TaskExecutorAdapter getAdaptedExecutor(Executor concurrentExecutor) {
        if (managedExecutorServiceClass != null && managedExecutorServiceClass.isInstance(concurrentExecutor)) {
            return new ManagedTaskExecutorAdapter(concurrentExecutor);
        }
        TaskExecutorAdapter adapter = new TaskExecutorAdapter(concurrentExecutor);
        if (this.taskDecorator != null) {
            adapter.setTaskDecorator(this.taskDecorator);
        }
        return adapter;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/concurrent/ConcurrentTaskExecutor$ManagedTaskExecutorAdapter.class */
    public static class ManagedTaskExecutorAdapter extends TaskExecutorAdapter {
        public ManagedTaskExecutorAdapter(Executor concurrentExecutor) {
            super(concurrentExecutor);
        }

        @Override // org.springframework.core.task.support.TaskExecutorAdapter, org.springframework.core.task.TaskExecutor, java.util.concurrent.Executor
        public void execute(Runnable task) {
            super.execute(ManagedTaskBuilder.buildManagedTask(task, task.toString()));
        }

        @Override // org.springframework.core.task.support.TaskExecutorAdapter, org.springframework.core.task.AsyncTaskExecutor
        public Future<?> submit(Runnable task) {
            return super.submit(ManagedTaskBuilder.buildManagedTask(task, task.toString()));
        }

        @Override // org.springframework.core.task.support.TaskExecutorAdapter, org.springframework.core.task.AsyncTaskExecutor
        public <T> Future<T> submit(Callable<T> task) {
            return super.submit(ManagedTaskBuilder.buildManagedTask(task, task.toString()));
        }

        @Override // org.springframework.core.task.support.TaskExecutorAdapter, org.springframework.core.task.AsyncListenableTaskExecutor
        public ListenableFuture<?> submitListenable(Runnable task) {
            return super.submitListenable(ManagedTaskBuilder.buildManagedTask(task, task.toString()));
        }

        @Override // org.springframework.core.task.support.TaskExecutorAdapter, org.springframework.core.task.AsyncListenableTaskExecutor
        public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
            return super.submitListenable(ManagedTaskBuilder.buildManagedTask(task, task.toString()));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/concurrent/ConcurrentTaskExecutor$ManagedTaskBuilder.class */
    public static class ManagedTaskBuilder {
        protected ManagedTaskBuilder() {
        }

        public static Runnable buildManagedTask(Runnable task, String identityName) {
            Map<String, String> properties;
            if (task instanceof SchedulingAwareRunnable) {
                SchedulingAwareRunnable schedulingAwareRunnable = (SchedulingAwareRunnable) task;
                properties = new HashMap<>(4);
                properties.put("jakarta.enterprise.concurrent.LONGRUNNING_HINT", Boolean.toString(schedulingAwareRunnable.isLongLived()));
            } else {
                properties = new HashMap<>(2);
            }
            properties.put("jakarta.enterprise.concurrent.IDENTITY_NAME", identityName);
            return ManagedExecutors.managedTask(task, properties, (ManagedTaskListener) null);
        }

        public static <T> Callable<T> buildManagedTask(Callable<T> task, String identityName) {
            Map<String, String> properties = new HashMap<>(2);
            properties.put("jakarta.enterprise.concurrent.IDENTITY_NAME", identityName);
            return ManagedExecutors.managedTask(task, properties, (ManagedTaskListener) null);
        }
    }
}
