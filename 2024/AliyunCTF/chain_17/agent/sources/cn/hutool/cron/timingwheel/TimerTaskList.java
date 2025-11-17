package cn.hutool.cron.timingwheel;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/timingwheel/TimerTaskList.class */
public class TimerTaskList implements Delayed {
    private final AtomicLong expire = new AtomicLong(-1);
    private final TimerTask root = new TimerTask(null, -1);

    public TimerTaskList() {
        this.root.prev = this.root;
        this.root.next = this.root;
    }

    public boolean setExpiration(long expire) {
        return this.expire.getAndSet(expire) != expire;
    }

    public long getExpire() {
        return this.expire.get();
    }

    public void addTask(TimerTask timerTask) {
        synchronized (this) {
            if (timerTask.timerTaskList == null) {
                timerTask.timerTaskList = this;
                TimerTask tail = this.root.prev;
                timerTask.next = this.root;
                timerTask.prev = tail;
                tail.next = timerTask;
                this.root.prev = timerTask;
            }
        }
    }

    public void removeTask(TimerTask timerTask) {
        synchronized (this) {
            if (equals(timerTask.timerTaskList)) {
                timerTask.next.prev = timerTask.prev;
                timerTask.prev.next = timerTask.next;
                timerTask.timerTaskList = null;
                timerTask.next = null;
                timerTask.prev = null;
            }
        }
    }

    public synchronized void flush(Consumer<TimerTask> flush) {
        TimerTask timerTask = this.root.next;
        while (true) {
            TimerTask timerTask2 = timerTask;
            if (false == timerTask2.equals(this.root)) {
                removeTask(timerTask2);
                flush.accept(timerTask2);
                timerTask = this.root.next;
            } else {
                this.expire.set(-1L);
                return;
            }
        }
    }

    @Override // java.util.concurrent.Delayed
    public long getDelay(TimeUnit unit) {
        return Math.max(0L, unit.convert(this.expire.get() - System.currentTimeMillis(), TimeUnit.MILLISECONDS));
    }

    @Override // java.lang.Comparable
    public int compareTo(Delayed o) {
        if (o instanceof TimerTaskList) {
            return Long.compare(this.expire.get(), ((TimerTaskList) o).expire.get());
        }
        return 0;
    }
}
