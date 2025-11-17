package cn.hutool.core.date;

import cn.hutool.core.util.StrUtil;
import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/BetweenFormatter.class */
public class BetweenFormatter implements Serializable {
    private static final long serialVersionUID = 1;
    private long betweenMs;
    private Level level;
    private final int levelMaxCount;

    public BetweenFormatter(long betweenMs, Level level) {
        this(betweenMs, level, 0);
    }

    public BetweenFormatter(long betweenMs, Level level, int levelMaxCount) {
        this.betweenMs = betweenMs;
        this.level = level;
        this.levelMaxCount = levelMaxCount;
    }

    public String format() {
        StringBuilder sb = new StringBuilder();
        if (this.betweenMs > 0) {
            long day = this.betweenMs / DateUnit.DAY.getMillis();
            long hour = (this.betweenMs / DateUnit.HOUR.getMillis()) - (day * 24);
            long minute = ((this.betweenMs / DateUnit.MINUTE.getMillis()) - ((day * 24) * 60)) - (hour * 60);
            long BetweenOfSecond = ((((day * 24) + hour) * 60) + minute) * 60;
            long second = (this.betweenMs / DateUnit.SECOND.getMillis()) - BetweenOfSecond;
            long millisecond = this.betweenMs - ((BetweenOfSecond + second) * 1000);
            int level = this.level.ordinal();
            int levelCount = 0;
            if (isLevelCountValid(0) && 0 != day && level >= Level.DAY.ordinal()) {
                sb.append(day).append(Level.DAY.name);
                levelCount = 0 + 1;
            }
            if (isLevelCountValid(levelCount) && 0 != hour && level >= Level.HOUR.ordinal()) {
                sb.append(hour).append(Level.HOUR.name);
                levelCount++;
            }
            if (isLevelCountValid(levelCount) && 0 != minute && level >= Level.MINUTE.ordinal()) {
                sb.append(minute).append(Level.MINUTE.name);
                levelCount++;
            }
            if (isLevelCountValid(levelCount) && 0 != second && level >= Level.SECOND.ordinal()) {
                sb.append(second).append(Level.SECOND.name);
                levelCount++;
            }
            if (isLevelCountValid(levelCount) && 0 != millisecond && level >= Level.MILLISECOND.ordinal()) {
                sb.append(millisecond).append(Level.MILLISECOND.name);
            }
        }
        if (StrUtil.isEmpty(sb)) {
            sb.append(0).append(this.level.name);
        }
        return sb.toString();
    }

    public long getBetweenMs() {
        return this.betweenMs;
    }

    public void setBetweenMs(long betweenMs) {
        this.betweenMs = betweenMs;
    }

    public Level getLevel() {
        return this.level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/BetweenFormatter$Level.class */
    public enum Level {
        DAY("天"),
        HOUR("小时"),
        MINUTE("分"),
        SECOND("秒"),
        MILLISECOND("毫秒");

        private final String name;

        Level(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    public String toString() {
        return format();
    }

    private boolean isLevelCountValid(int levelCount) {
        return this.levelMaxCount <= 0 || levelCount < this.levelMaxCount;
    }
}
