package cn.hutool.core.date;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/TemporalUtil.class */
public class TemporalUtil {
    public static Duration between(Temporal startTimeInclude, Temporal endTimeExclude) {
        return Duration.between(startTimeInclude, endTimeExclude);
    }

    public static long between(Temporal startTimeInclude, Temporal endTimeExclude, ChronoUnit unit) {
        return unit.between(startTimeInclude, endTimeExclude);
    }

    public static ChronoUnit toChronoUnit(TimeUnit unit) throws IllegalArgumentException {
        if (null == unit) {
            return null;
        }
        switch (AnonymousClass1.$SwitchMap$java$util$concurrent$TimeUnit[unit.ordinal()]) {
            case 1:
                return ChronoUnit.NANOS;
            case 2:
                return ChronoUnit.MICROS;
            case 3:
                return ChronoUnit.MILLIS;
            case 4:
                return ChronoUnit.SECONDS;
            case 5:
                return ChronoUnit.MINUTES;
            case 6:
                return ChronoUnit.HOURS;
            case 7:
                return ChronoUnit.DAYS;
            default:
                throw new IllegalArgumentException("Unknown TimeUnit constant");
        }
    }

    public static TimeUnit toTimeUnit(ChronoUnit unit) throws IllegalArgumentException {
        if (null == unit) {
            return null;
        }
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[unit.ordinal()]) {
            case 1:
                return TimeUnit.NANOSECONDS;
            case 2:
                return TimeUnit.MICROSECONDS;
            case 3:
                return TimeUnit.MILLISECONDS;
            case 4:
                return TimeUnit.SECONDS;
            case 5:
                return TimeUnit.MINUTES;
            case 6:
                return TimeUnit.HOURS;
            case 7:
                return TimeUnit.DAYS;
            default:
                throw new IllegalArgumentException("ChronoUnit cannot be converted to TimeUnit: " + unit);
        }
    }

    /* renamed from: cn.hutool.core.date.TemporalUtil$1, reason: invalid class name */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/TemporalUtil$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$util$concurrent$TimeUnit;
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoUnit = new int[ChronoUnit.values().length];

        static {
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.NANOS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.MICROS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.MILLIS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.SECONDS.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.MINUTES.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.HOURS.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.DAYS.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            $SwitchMap$java$util$concurrent$TimeUnit = new int[TimeUnit.values().length];
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.NANOSECONDS.ordinal()] = 1;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.MICROSECONDS.ordinal()] = 2;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.MILLISECONDS.ordinal()] = 3;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.SECONDS.ordinal()] = 4;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.MINUTES.ordinal()] = 5;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.HOURS.ordinal()] = 6;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.DAYS.ordinal()] = 7;
            } catch (NoSuchFieldError e14) {
            }
        }
    }

    public static <T extends Temporal> T offset(T t, long j, TemporalUnit temporalUnit) {
        if (null == t) {
            return null;
        }
        return (T) t.plus(j, temporalUnit);
    }

    public <T extends Temporal> T offset(T t, DayOfWeek dayOfWeek, boolean z) {
        return (T) t.with(z ? TemporalAdjusters.previous(dayOfWeek) : TemporalAdjusters.next(dayOfWeek));
    }
}
