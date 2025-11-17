package cn.hutool.cron;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.resource.NoResourceException;
import cn.hutool.cron.pattern.CronPattern;
import cn.hutool.cron.task.Task;
import cn.hutool.setting.Setting;
import cn.hutool.setting.SettingRuntimeException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/CronUtil.class */
public class CronUtil {
    public static final String CRONTAB_CONFIG_PATH = "config/cron.setting";
    public static final String CRONTAB_CONFIG_PATH2 = "cron.setting";
    private static final Lock lock = new ReentrantLock();
    private static final Scheduler scheduler = new Scheduler();
    private static Setting crontabSetting;

    public static void setCronSetting(Setting cronSetting) {
        crontabSetting = cronSetting;
    }

    public static void setCronSetting(String cronSettingPath) {
        try {
            crontabSetting = new Setting(cronSettingPath, Setting.DEFAULT_CHARSET, false);
        } catch (NoResourceException | SettingRuntimeException e) {
        }
    }

    public static void setMatchSecond(boolean isMatchSecond) {
        scheduler.setMatchSecond(isMatchSecond);
    }

    public static String schedule(String schedulingPattern, Task task) {
        return scheduler.schedule(schedulingPattern, task);
    }

    public static String schedule(String id, String schedulingPattern, Task task) {
        scheduler.schedule(id, schedulingPattern, task);
        return id;
    }

    public static String schedule(String schedulingPattern, Runnable task) {
        return scheduler.schedule(schedulingPattern, task);
    }

    public static void schedule(Setting cronSetting) {
        scheduler.schedule(cronSetting);
    }

    public static boolean remove(String schedulerId) {
        return scheduler.descheduleWithStatus(schedulerId);
    }

    public static void updatePattern(String id, CronPattern pattern) {
        scheduler.updatePattern(id, pattern);
    }

    public static Scheduler getScheduler() {
        return scheduler;
    }

    public static void start() {
        start(false);
    }

    public static synchronized void start(boolean isDaemon) {
        if (scheduler.isStarted()) {
            throw new UtilException("Scheduler has been started, please stop it first!");
        }
        lock.lock();
        try {
            if (null == crontabSetting) {
                setCronSetting(CRONTAB_CONFIG_PATH);
            }
            if (null == crontabSetting) {
                setCronSetting(CRONTAB_CONFIG_PATH2);
            }
            lock.unlock();
            schedule(crontabSetting);
            scheduler.start(isDaemon);
        } catch (Throwable th) {
            lock.unlock();
            throw th;
        }
    }

    public static void restart() {
        lock.lock();
        try {
            if (null != crontabSetting) {
                crontabSetting.load();
            }
            if (scheduler.isStarted()) {
                stop();
            }
            lock.unlock();
            schedule(crontabSetting);
            scheduler.start();
        } catch (Throwable th) {
            lock.unlock();
            throw th;
        }
    }

    public static void stop() {
        scheduler.stop(true);
    }
}
