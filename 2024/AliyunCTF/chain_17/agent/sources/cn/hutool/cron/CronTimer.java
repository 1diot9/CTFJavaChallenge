package cn.hutool.cron;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/CronTimer.class */
public class CronTimer extends Thread implements Serializable {
    private static final long serialVersionUID = 1;
    private static final Log log = LogFactory.get();
    private final long TIMER_UNIT_SECOND = DateUnit.SECOND.getMillis();
    private final long TIMER_UNIT_MINUTE = DateUnit.MINUTE.getMillis();
    private boolean isStop;
    private final Scheduler scheduler;

    public CronTimer(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        long timerUnit = this.scheduler.config.matchSecond ? this.TIMER_UNIT_SECOND : this.TIMER_UNIT_MINUTE;
        long thisTime = System.currentTimeMillis();
        while (false == this.isStop) {
            long nextTime = ((thisTime / timerUnit) + serialVersionUID) * timerUnit;
            long sleep = nextTime - System.currentTimeMillis();
            if (isValidSleepMillis(sleep, timerUnit)) {
                if (false == ThreadUtil.safeSleep(sleep)) {
                    break;
                }
                thisTime = System.currentTimeMillis();
                spawnLauncher(thisTime);
            } else {
                thisTime = System.currentTimeMillis();
            }
        }
        log.debug("Hutool-cron timer stopped.", new Object[0]);
    }

    public synchronized void stopTimer() {
        this.isStop = true;
        ThreadUtil.interrupt(this, true);
    }

    private void spawnLauncher(long millis) {
        this.scheduler.taskLauncherManager.spawnLauncher(millis);
    }

    private static boolean isValidSleepMillis(long millis, long timerUnit) {
        return millis > 0 && millis < 2 * timerUnit;
    }
}
