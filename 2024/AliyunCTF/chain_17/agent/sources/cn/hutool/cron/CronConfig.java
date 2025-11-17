package cn.hutool.cron;

import java.util.TimeZone;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/CronConfig.class */
public class CronConfig {
    protected TimeZone timezone = TimeZone.getDefault();
    protected boolean matchSecond;

    public CronConfig setTimeZone(TimeZone timezone) {
        this.timezone = timezone;
        return this;
    }

    public TimeZone getTimeZone() {
        return this.timezone;
    }

    public boolean isMatchSecond() {
        return this.matchSecond;
    }

    public CronConfig setMatchSecond(boolean isMatchSecond) {
        this.matchSecond = isMatchSecond;
        return this;
    }
}
