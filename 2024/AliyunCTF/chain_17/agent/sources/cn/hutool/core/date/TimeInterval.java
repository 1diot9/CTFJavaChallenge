package cn.hutool.core.date;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/TimeInterval.class */
public class TimeInterval extends GroupTimeInterval {
    private static final long serialVersionUID = 1;
    private static final String DEFAULT_ID = "";

    public TimeInterval() {
        this(false);
    }

    public TimeInterval(boolean isNano) {
        super(isNano);
        start();
    }

    public long start() {
        return start("");
    }

    public long intervalRestart() {
        return intervalRestart("");
    }

    public TimeInterval restart() {
        start("");
        return this;
    }

    public long interval() {
        return interval("");
    }

    public String intervalPretty() {
        return intervalPretty("");
    }

    public long intervalMs() {
        return intervalMs("");
    }

    public long intervalSecond() {
        return intervalSecond("");
    }

    public long intervalMinute() {
        return intervalMinute("");
    }

    public long intervalHour() {
        return intervalHour("");
    }

    public long intervalDay() {
        return intervalDay("");
    }

    public long intervalWeek() {
        return intervalWeek("");
    }
}
