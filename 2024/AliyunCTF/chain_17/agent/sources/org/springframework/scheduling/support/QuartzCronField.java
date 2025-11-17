package org.springframework.scheduling.support;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.support.CronField;
import org.springframework.util.Assert;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/support/QuartzCronField.class */
public final class QuartzCronField extends CronField {
    private final CronField.Type rollForwardType;
    private final TemporalAdjuster adjuster;
    private final String value;

    private QuartzCronField(CronField.Type type, TemporalAdjuster adjuster, String value) {
        this(type, type, adjuster, value);
    }

    private QuartzCronField(CronField.Type type, CronField.Type rollForwardType, TemporalAdjuster adjuster, String value) {
        super(type);
        this.adjuster = adjuster;
        this.value = value;
        this.rollForwardType = rollForwardType;
    }

    public static boolean isQuartzDaysOfMonthField(String value) {
        return value.contains("L") || value.contains("W");
    }

    public static QuartzCronField parseDaysOfMonth(String value) {
        TemporalAdjuster adjuster;
        int idx = value.lastIndexOf(76);
        if (idx != -1) {
            if (idx != 0) {
                throw new IllegalArgumentException("Unrecognized characters before 'L' in '" + value + "'");
            }
            if (value.length() == 2 && value.charAt(1) == 'W') {
                adjuster = lastWeekdayOfMonth();
            } else if (value.length() == 1) {
                adjuster = lastDayOfMonth();
            } else {
                int offset = Integer.parseInt(value, idx + 1, value.length(), 10);
                if (offset >= 0) {
                    throw new IllegalArgumentException("Offset '" + offset + " should be < 0 '" + value + "'");
                }
                adjuster = lastDayWithOffset(offset);
            }
            return new QuartzCronField(CronField.Type.DAY_OF_MONTH, adjuster, value);
        }
        int idx2 = value.lastIndexOf(87);
        if (idx2 != -1) {
            if (idx2 == 0) {
                throw new IllegalArgumentException("No day-of-month before 'W' in '" + value + "'");
            }
            if (idx2 != value.length() - 1) {
                throw new IllegalArgumentException("Unrecognized characters after 'W' in '" + value + "'");
            }
            int dayOfMonth = Integer.parseInt(value, 0, idx2, 10);
            TemporalAdjuster adjuster2 = weekdayNearestTo(CronField.Type.DAY_OF_MONTH.checkValidValue(dayOfMonth));
            return new QuartzCronField(CronField.Type.DAY_OF_MONTH, adjuster2, value);
        }
        throw new IllegalArgumentException("No 'L' or 'W' found in '" + value + "'");
    }

    public static boolean isQuartzDaysOfWeekField(String value) {
        return value.contains("L") || value.contains("#");
    }

    public static QuartzCronField parseDaysOfWeek(String value) {
        int idx = value.lastIndexOf(76);
        if (idx != -1) {
            if (idx != value.length() - 1) {
                throw new IllegalArgumentException("Unrecognized characters after 'L' in '" + value + "'");
            }
            if (idx == 0) {
                throw new IllegalArgumentException("No day-of-week before 'L' in '" + value + "'");
            }
            DayOfWeek dayOfWeek = parseDayOfWeek(value.substring(0, idx));
            TemporalAdjuster adjuster = lastInMonth(dayOfWeek);
            return new QuartzCronField(CronField.Type.DAY_OF_WEEK, CronField.Type.DAY_OF_MONTH, adjuster, value);
        }
        int idx2 = value.lastIndexOf(35);
        if (idx2 != -1) {
            if (idx2 == 0) {
                throw new IllegalArgumentException("No day-of-week before '#' in '" + value + "'");
            }
            if (idx2 == value.length() - 1) {
                throw new IllegalArgumentException("No ordinal after '#' in '" + value + "'");
            }
            DayOfWeek dayOfWeek2 = parseDayOfWeek(value.substring(0, idx2));
            int ordinal = Integer.parseInt(value, idx2 + 1, value.length(), 10);
            if (ordinal <= 0) {
                throw new IllegalArgumentException("Ordinal '" + ordinal + "' in '" + value + "' must be positive number ");
            }
            TemporalAdjuster adjuster2 = dayOfWeekInMonth(ordinal, dayOfWeek2);
            return new QuartzCronField(CronField.Type.DAY_OF_WEEK, CronField.Type.DAY_OF_MONTH, adjuster2, value);
        }
        throw new IllegalArgumentException("No 'L' or '#' found in '" + value + "'");
    }

    private static DayOfWeek parseDayOfWeek(String value) {
        int dayOfWeek = Integer.parseInt(value);
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        try {
            return DayOfWeek.of(dayOfWeek);
        } catch (DateTimeException ex) {
            String msg = ex.getMessage() + " '" + value + "'";
            throw new IllegalArgumentException(msg, ex);
        }
    }

    private static TemporalAdjuster atMidnight() {
        return temporal -> {
            if (temporal.isSupported(ChronoField.NANO_OF_DAY)) {
                return temporal.with(ChronoField.NANO_OF_DAY, 0L);
            }
            return temporal;
        };
    }

    private static TemporalAdjuster lastDayOfMonth() {
        TemporalAdjuster adjuster = TemporalAdjusters.lastDayOfMonth();
        return temporal -> {
            Temporal result = adjuster.adjustInto(temporal);
            return rollbackToMidnight(temporal, result);
        };
    }

    private static TemporalAdjuster lastWeekdayOfMonth() {
        TemporalAdjuster adjuster = TemporalAdjusters.lastDayOfMonth();
        return temporal -> {
            Temporal result;
            Temporal lastDom = adjuster.adjustInto(temporal);
            int dow = lastDom.get(ChronoField.DAY_OF_WEEK);
            if (dow == 6) {
                result = lastDom.minus(1L, ChronoUnit.DAYS);
            } else if (dow == 7) {
                result = lastDom.minus(2L, ChronoUnit.DAYS);
            } else {
                result = lastDom;
            }
            return rollbackToMidnight(temporal, result);
        };
    }

    private static TemporalAdjuster lastDayWithOffset(int offset) {
        Assert.isTrue(offset < 0, "Offset should be < 0");
        TemporalAdjuster adjuster = TemporalAdjusters.lastDayOfMonth();
        return temporal -> {
            Temporal result = adjuster.adjustInto(temporal).plus(offset, ChronoUnit.DAYS);
            return rollbackToMidnight(temporal, result);
        };
    }

    private static TemporalAdjuster weekdayNearestTo(int dayOfMonth) {
        return temporal -> {
            int current = CronField.Type.DAY_OF_MONTH.get(temporal);
            DayOfWeek dayOfWeek = DayOfWeek.from(temporal);
            if ((current == dayOfMonth && isWeekday(dayOfWeek)) || ((dayOfWeek == DayOfWeek.FRIDAY && current == dayOfMonth - 1) || ((dayOfWeek == DayOfWeek.MONDAY && current == dayOfMonth + 1) || (dayOfWeek == DayOfWeek.MONDAY && dayOfMonth == 1 && current == 3)))) {
                return temporal;
            }
            int count = 0;
            while (true) {
                int i = count;
                count++;
                if (i < 366) {
                    if (current == dayOfMonth) {
                        DayOfWeek dayOfWeek2 = DayOfWeek.from(temporal);
                        if (dayOfWeek2 == DayOfWeek.SATURDAY) {
                            if (dayOfMonth != 1) {
                                temporal = temporal.minus(1L, ChronoUnit.DAYS);
                            } else {
                                temporal = temporal.plus(2L, ChronoUnit.DAYS);
                            }
                        } else if (dayOfWeek2 == DayOfWeek.SUNDAY) {
                            temporal = temporal.plus(1L, ChronoUnit.DAYS);
                        }
                        return atMidnight().adjustInto(temporal);
                    }
                    temporal = CronField.Type.DAY_OF_MONTH.elapseUntil(cast(temporal), dayOfMonth);
                    current = CronField.Type.DAY_OF_MONTH.get(temporal);
                } else {
                    return null;
                }
            }
        };
    }

    private static boolean isWeekday(DayOfWeek dayOfWeek) {
        return (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) ? false : true;
    }

    private static TemporalAdjuster lastInMonth(DayOfWeek dayOfWeek) {
        TemporalAdjuster adjuster = TemporalAdjusters.lastInMonth(dayOfWeek);
        return temporal -> {
            Temporal result = adjuster.adjustInto(temporal);
            return rollbackToMidnight(temporal, result);
        };
    }

    private static TemporalAdjuster dayOfWeekInMonth(int ordinal, DayOfWeek dayOfWeek) {
        TemporalAdjuster adjuster = TemporalAdjusters.dayOfWeekInMonth(ordinal, dayOfWeek);
        return temporal -> {
            Temporal result = adjuster.adjustInto(temporal);
            return rollbackToMidnight(temporal, result);
        };
    }

    private static Temporal rollbackToMidnight(Temporal current, Temporal result) {
        if (result.get(ChronoField.DAY_OF_MONTH) == current.get(ChronoField.DAY_OF_MONTH)) {
            return current;
        }
        return atMidnight().adjustInto(result);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.scheduling.support.CronField
    public <T extends Temporal & Comparable<? super T>> T nextOrSame(T t) {
        Temporal adjust = adjust(t);
        if (adjust != null && ((Comparable) adjust).compareTo(t) < 0) {
            adjust = adjust(this.rollForwardType.rollForward(t));
            if (adjust != null) {
                adjust = type().reset(adjust);
            }
        }
        return (T) adjust;
    }

    @Nullable
    private <T extends Temporal & Comparable<? super T>> T adjust(T t) {
        return (T) this.adjuster.adjustInto(t);
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuartzCronField)) {
            return false;
        }
        QuartzCronField other = (QuartzCronField) o;
        return type() == other.type() && this.value.equals(other.value);
    }

    public String toString() {
        return type() + " '" + this.value + "'";
    }
}
