package cn.hutool.cron.timingwheel;

import cn.hutool.core.thread.ThreadUtil;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/timingwheel/SystemTimer.class */
public class SystemTimer {
    private final TimingWheel timeWheel;
    private final DelayQueue<TimerTaskList> delayQueue = new DelayQueue<>();
    private long delayQueueTimeout = 100;
    private ExecutorService bossThreadPool;

    public SystemTimer() {
        DelayQueue<TimerTaskList> delayQueue = this.delayQueue;
        delayQueue.getClass();
        this.timeWheel = new TimingWheel(1L, 20, (v1) -> {
            r5.offer(v1);
        });
    }

    public SystemTimer setDelayQueueTimeout(long delayQueueTimeout) {
        this.delayQueueTimeout = delayQueueTimeout;
        return this;
    }

    public SystemTimer start() {
        this.bossThreadPool = ThreadUtil.newSingleExecutor();
        this.bossThreadPool.submit(() -> {
            do {
            } while (false != advanceClock());
        });
        return this;
    }

    public void stop() {
        this.bossThreadPool.shutdown();
    }

    public void addTask(TimerTask timerTask) {
        if (false == this.timeWheel.addTask(timerTask)) {
            ThreadUtil.execAsync(timerTask.getTask());
        }
    }

    private boolean advanceClock() {
        try {
            TimerTaskList timerTaskList = poll();
            if (null != timerTaskList) {
                this.timeWheel.advanceClock(timerTaskList.getExpire());
                timerTaskList.flush(this::addTask);
            }
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    private TimerTaskList poll() throws InterruptedException {
        if (this.delayQueueTimeout > 0) {
            return this.delayQueue.poll(this.delayQueueTimeout, TimeUnit.MILLISECONDS);
        }
        return this.delayQueue.poll();
    }
}
