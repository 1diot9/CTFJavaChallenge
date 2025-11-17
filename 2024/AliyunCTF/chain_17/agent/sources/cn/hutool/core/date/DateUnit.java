package cn.hutool.core.date;

import java.time.temporal.ChronoUnit;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/DateUnit.class */
public enum DateUnit {
    MS(1),
    SECOND(1000),
    MINUTE(SECOND.getMillis() * 60),
    HOUR(MINUTE.getMillis() * 60),
    DAY(HOUR.getMillis() * 24),
    WEEK(DAY.getMillis() * 7);

    private final long millis;

    DateUnit(long millis) {
        this.millis = millis;
    }

    public long getMillis() {
        return this.millis;
    }

    public ChronoUnit toChronoUnit() {
        return toChronoUnit(this);
    }

    public static DateUnit of(ChronoUnit unit) {
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[unit.ordinal()]) {
            case 1:
                return MS;
            case 2:
                return SECOND;
            case 3:
                return MINUTE;
            case 4:
                return HOUR;
            case 5:
                return DAY;
            case 6:
                return WEEK;
            default:
                return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: cn.hutool.core.date.DateUnit$1, reason: invalid class name */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/DateUnit$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoUnit;

        static {
            try {
                $SwitchMap$cn$hutool$core$date$DateUnit[DateUnit.MS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$cn$hutool$core$date$DateUnit[DateUnit.SECOND.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$cn$hutool$core$date$DateUnit[DateUnit.MINUTE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$cn$hutool$core$date$DateUnit[DateUnit.HOUR.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$cn$hutool$core$date$DateUnit[DateUnit.DAY.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$cn$hutool$core$date$DateUnit[DateUnit.WEEK.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            $SwitchMap$java$time$temporal$ChronoUnit = new int[ChronoUnit.values().length];
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.MICROS.ordinal()] = 1;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.SECONDS.ordinal()] = 2;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.MINUTES.ordinal()] = 3;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.HOURS.ordinal()] = 4;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.DAYS.ordinal()] = 5;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.WEEKS.ordinal()] = 6;
            } catch (NoSuchFieldError e12) {
            }
        }
    }

    public static ChronoUnit toChronoUnit(DateUnit unit) {
        switch (unit) {
            case MS:
                return ChronoUnit.MICROS;
            case SECOND:
                return ChronoUnit.SECONDS;
            case MINUTE:
                return ChronoUnit.MINUTES;
            case HOUR:
                return ChronoUnit.HOURS;
            case DAY:
                return ChronoUnit.DAYS;
            case WEEK:
                return ChronoUnit.WEEKS;
            default:
                return null;
        }
    }
}
