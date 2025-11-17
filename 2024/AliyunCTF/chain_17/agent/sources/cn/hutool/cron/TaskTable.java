package cn.hutool.cron;

import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.pattern.CronPattern;
import cn.hutool.cron.task.CronTask;
import cn.hutool.cron.task.Task;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/TaskTable.class */
public class TaskTable implements Serializable {
    private static final long serialVersionUID = 1;
    public static final int DEFAULT_CAPACITY = 10;
    private final ReadWriteLock lock;
    private final List<String> ids;
    private final List<CronPattern> patterns;
    private final List<Task> tasks;
    private int size;

    public TaskTable() {
        this(10);
    }

    public TaskTable(int initialCapacity) {
        this.lock = new ReentrantReadWriteLock();
        this.ids = new ArrayList(initialCapacity);
        this.patterns = new ArrayList(initialCapacity);
        this.tasks = new ArrayList(initialCapacity);
    }

    public TaskTable add(String id, CronPattern pattern, Task task) {
        Lock writeLock = this.lock.writeLock();
        writeLock.lock();
        try {
            if (this.ids.contains(id)) {
                throw new CronException("Id [{}] has been existed!", id);
            }
            this.ids.add(id);
            this.patterns.add(pattern);
            this.tasks.add(task);
            this.size++;
            writeLock.unlock();
            return this;
        } catch (Throwable th) {
            writeLock.unlock();
            throw th;
        }
    }

    public List<String> getIds() {
        Lock readLock = this.lock.readLock();
        readLock.lock();
        try {
            return Collections.unmodifiableList(this.ids);
        } finally {
            readLock.unlock();
        }
    }

    public List<CronPattern> getPatterns() {
        Lock readLock = this.lock.readLock();
        readLock.lock();
        try {
            return Collections.unmodifiableList(this.patterns);
        } finally {
            readLock.unlock();
        }
    }

    public List<Task> getTasks() {
        Lock readLock = this.lock.readLock();
        readLock.lock();
        try {
            return Collections.unmodifiableList(this.tasks);
        } finally {
            readLock.unlock();
        }
    }

    public boolean remove(String id) {
        Lock writeLock = this.lock.writeLock();
        writeLock.lock();
        try {
            int index = this.ids.indexOf(id);
            if (index < 0) {
                return false;
            }
            this.tasks.remove(index);
            this.patterns.remove(index);
            this.ids.remove(index);
            this.size--;
            writeLock.unlock();
            return true;
        } finally {
            writeLock.unlock();
        }
    }

    public boolean updatePattern(String id, CronPattern pattern) {
        Lock writeLock = this.lock.writeLock();
        writeLock.lock();
        try {
            int index = this.ids.indexOf(id);
            if (index > -1) {
                this.patterns.set(index, pattern);
                writeLock.unlock();
                return true;
            }
            writeLock.unlock();
            return false;
        } catch (Throwable th) {
            writeLock.unlock();
            throw th;
        }
    }

    public Task getTask(int index) {
        Lock readLock = this.lock.readLock();
        readLock.lock();
        try {
            Task task = this.tasks.get(index);
            readLock.unlock();
            return task;
        } catch (Throwable th) {
            readLock.unlock();
            throw th;
        }
    }

    public Task getTask(String id) {
        int index = this.ids.indexOf(id);
        if (index > -1) {
            return getTask(index);
        }
        return null;
    }

    public CronPattern getPattern(int index) {
        Lock readLock = this.lock.readLock();
        readLock.lock();
        try {
            CronPattern cronPattern = this.patterns.get(index);
            readLock.unlock();
            return cronPattern;
        } catch (Throwable th) {
            readLock.unlock();
            throw th;
        }
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size < 1;
    }

    public CronPattern getPattern(String id) {
        int index = this.ids.indexOf(id);
        if (index > -1) {
            return getPattern(index);
        }
        return null;
    }

    public void executeTaskIfMatch(Scheduler scheduler, long millis) {
        Lock readLock = this.lock.readLock();
        readLock.lock();
        try {
            executeTaskIfMatchInternal(scheduler, millis);
            readLock.unlock();
        } catch (Throwable th) {
            readLock.unlock();
            throw th;
        }
    }

    public String toString() {
        StringBuilder builder = StrUtil.builder();
        for (int i = 0; i < this.size; i++) {
            builder.append(StrUtil.format("[{}] [{}] [{}]\n", this.ids.get(i), this.patterns.get(i), this.tasks.get(i)));
        }
        return builder.toString();
    }

    protected void executeTaskIfMatchInternal(Scheduler scheduler, long millis) {
        for (int i = 0; i < this.size; i++) {
            if (this.patterns.get(i).match(scheduler.config.timezone, millis, scheduler.config.matchSecond)) {
                scheduler.taskExecutorManager.spawnExecutor(new CronTask(this.ids.get(i), this.patterns.get(i), this.tasks.get(i)));
            }
        }
    }
}
