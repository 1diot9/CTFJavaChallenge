package cn.hutool.cron;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.listener.TaskListener;
import cn.hutool.cron.listener.TaskListenerManager;
import cn.hutool.cron.pattern.CronPattern;
import cn.hutool.cron.task.InvokeTask;
import cn.hutool.cron.task.RunnableTask;
import cn.hutool.cron.task.Task;
import cn.hutool.log.StaticLog;
import cn.hutool.setting.Setting;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/Scheduler.class */
public class Scheduler implements Serializable {
    private static final long serialVersionUID = 1;
    protected boolean daemon;
    private CronTimer timer;
    protected TaskLauncherManager taskLauncherManager;
    protected TaskExecutorManager taskExecutorManager;
    protected ExecutorService threadExecutor;
    private final Lock lock = new ReentrantLock();
    protected CronConfig config = new CronConfig();
    private boolean started = false;
    protected TaskTable taskTable = new TaskTable();
    protected TaskListenerManager listenerManager = new TaskListenerManager();

    public Scheduler setTimeZone(TimeZone timeZone) {
        this.config.setTimeZone(timeZone);
        return this;
    }

    public TimeZone getTimeZone() {
        return this.config.getTimeZone();
    }

    public Scheduler setDaemon(boolean on) throws CronException {
        this.lock.lock();
        try {
            checkStarted();
            this.daemon = on;
            return this;
        } finally {
            this.lock.unlock();
        }
    }

    public Scheduler setThreadExecutor(ExecutorService threadExecutor) throws CronException {
        this.lock.lock();
        try {
            checkStarted();
            this.threadExecutor = threadExecutor;
            return this;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean isDaemon() {
        return this.daemon;
    }

    public boolean isMatchSecond() {
        return this.config.isMatchSecond();
    }

    public Scheduler setMatchSecond(boolean isMatchSecond) {
        this.config.setMatchSecond(isMatchSecond);
        return this;
    }

    public Scheduler addListener(TaskListener listener) {
        this.listenerManager.addListener(listener);
        return this;
    }

    public Scheduler removeListener(TaskListener listener) {
        this.listenerManager.removeListener(listener);
        return this;
    }

    public Scheduler schedule(Setting cronSetting) {
        if (MapUtil.isNotEmpty(cronSetting)) {
            for (Map.Entry<String, LinkedHashMap<String, String>> groupedEntry : cronSetting.getGroupedMap().entrySet()) {
                String group = groupedEntry.getKey();
                for (Map.Entry<String, String> entry : groupedEntry.getValue().entrySet()) {
                    String jobClass = entry.getKey();
                    if (StrUtil.isNotBlank(group)) {
                        jobClass = group + '.' + jobClass;
                    }
                    String pattern = entry.getValue();
                    StaticLog.debug("Load job: {} {}", pattern, jobClass);
                    try {
                        schedule("id_" + jobClass, pattern, new InvokeTask(jobClass));
                    } catch (Exception e) {
                        throw new CronException(e, "Schedule [{}] [{}] error!", pattern, jobClass);
                    }
                }
            }
        }
        return this;
    }

    public String schedule(String pattern, Runnable task) {
        return schedule(pattern, new RunnableTask(task));
    }

    public String schedule(String pattern, Task task) {
        String id = IdUtil.fastUUID();
        schedule(id, pattern, task);
        return id;
    }

    public Scheduler schedule(String id, String pattern, Runnable task) {
        return schedule(id, new CronPattern(pattern), new RunnableTask(task));
    }

    public Scheduler schedule(String id, String pattern, Task task) {
        return schedule(id, new CronPattern(pattern), task);
    }

    public Scheduler schedule(String id, CronPattern pattern, Task task) {
        this.taskTable.add(id, pattern, task);
        return this;
    }

    public Scheduler deschedule(String id) {
        descheduleWithStatus(id);
        return this;
    }

    public boolean descheduleWithStatus(String id) {
        return this.taskTable.remove(id);
    }

    public Scheduler updatePattern(String id, CronPattern pattern) {
        this.taskTable.updatePattern(id, pattern);
        return this;
    }

    public TaskTable getTaskTable() {
        return this.taskTable;
    }

    public CronPattern getPattern(String id) {
        return this.taskTable.getPattern(id);
    }

    public Task getTask(String id) {
        return this.taskTable.getTask(id);
    }

    public boolean isEmpty() {
        return this.taskTable.isEmpty();
    }

    public int size() {
        return this.taskTable.size();
    }

    public Scheduler clear() {
        this.taskTable = new TaskTable();
        return this;
    }

    public boolean isStarted() {
        return this.started;
    }

    public Scheduler start(boolean isDaemon) {
        this.daemon = isDaemon;
        return start();
    }

    public Scheduler start() {
        this.lock.lock();
        try {
            checkStarted();
            if (null == this.threadExecutor) {
                this.threadExecutor = ExecutorBuilder.create().useSynchronousQueue().setThreadFactory(ThreadFactoryBuilder.create().setNamePrefix("hutool-cron-").setDaemon(this.daemon).build()).build();
            }
            this.taskLauncherManager = new TaskLauncherManager(this);
            this.taskExecutorManager = new TaskExecutorManager(this);
            this.timer = new CronTimer(this);
            this.timer.setDaemon(this.daemon);
            this.timer.start();
            this.started = true;
            return this;
        } finally {
            this.lock.unlock();
        }
    }

    public Scheduler stop() {
        return stop(false);
    }

    public Scheduler stop(boolean clearTasks) {
        this.lock.lock();
        try {
            if (false == this.started) {
                throw new IllegalStateException("Scheduler not started !");
            }
            this.timer.stopTimer();
            this.timer = null;
            this.threadExecutor.shutdown();
            this.threadExecutor = null;
            if (clearTasks) {
                clear();
            }
            this.started = false;
            return this;
        } finally {
            this.lock.unlock();
        }
    }

    private void checkStarted() throws CronException {
        if (this.started) {
            throw new CronException("Scheduler already started!");
        }
    }
}
