package org.h2.api;

import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;
import org.h2.util.IntervalUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/api/Interval.class */
public final class Interval {
    private final IntervalQualifier qualifier;
    private final boolean negative;
    private final long leading;
    private final long remaining;

    public static Interval ofYears(long j) {
        return new Interval(IntervalQualifier.YEAR, j < 0, Math.abs(j), 0L);
    }

    public static Interval ofMonths(long j) {
        return new Interval(IntervalQualifier.MONTH, j < 0, Math.abs(j), 0L);
    }

    public static Interval ofDays(long j) {
        return new Interval(IntervalQualifier.DAY, j < 0, Math.abs(j), 0L);
    }

    public static Interval ofHours(long j) {
        return new Interval(IntervalQualifier.HOUR, j < 0, Math.abs(j), 0L);
    }

    public static Interval ofMinutes(long j) {
        return new Interval(IntervalQualifier.MINUTE, j < 0, Math.abs(j), 0L);
    }

    public static Interval ofSeconds(long j) {
        return new Interval(IntervalQualifier.SECOND, j < 0, Math.abs(j), 0L);
    }

    public static Interval ofSeconds(long j, int i) {
        boolean z = (j | ((long) i)) < 0;
        if (z) {
            if (j > 0 || i > 0) {
                throw new IllegalArgumentException();
            }
            j = -j;
            i = -i;
        }
        return new Interval(IntervalQualifier.SECOND, z, j, i);
    }

    public static Interval ofNanos(long j) {
        boolean z = j < 0;
        if (z) {
            j = -j;
            if (j < 0) {
                return new Interval(IntervalQualifier.SECOND, true, 9223372036L, 854775808L);
            }
        }
        return new Interval(IntervalQualifier.SECOND, z, j / DateTimeUtils.NANOS_PER_SECOND, j % DateTimeUtils.NANOS_PER_SECOND);
    }

    public static Interval ofYearsMonths(long j, int i) {
        boolean z = (j | ((long) i)) < 0;
        if (z) {
            if (j > 0 || i > 0) {
                throw new IllegalArgumentException();
            }
            j = -j;
            i = -i;
        }
        return new Interval(IntervalQualifier.YEAR_TO_MONTH, z, j, i);
    }

    public static Interval ofDaysHours(long j, int i) {
        boolean z = (j | ((long) i)) < 0;
        if (z) {
            if (j > 0 || i > 0) {
                throw new IllegalArgumentException();
            }
            j = -j;
            i = -i;
        }
        return new Interval(IntervalQualifier.DAY_TO_HOUR, z, j, i);
    }

    public static Interval ofDaysHoursMinutes(long j, int i, int i2) {
        boolean z = ((j | ((long) i)) | ((long) i2)) < 0;
        if (z) {
            if (j > 0 || i > 0 || i2 > 0) {
                throw new IllegalArgumentException();
            }
            j = -j;
            i = -i;
            i2 = -i2;
            if ((i | i2) < 0) {
                throw new IllegalArgumentException();
            }
        }
        if (i2 >= 60) {
            throw new IllegalArgumentException();
        }
        return new Interval(IntervalQualifier.DAY_TO_MINUTE, z, j, (i * 60) + i2);
    }

    public static Interval ofDaysHoursMinutesSeconds(long j, int i, int i2, int i3) {
        return ofDaysHoursMinutesNanos(j, i, i2, i3 * DateTimeUtils.NANOS_PER_SECOND);
    }

    public static Interval ofDaysHoursMinutesNanos(long j, int i, int i2, long j2) {
        boolean z = (((j | ((long) i)) | ((long) i2)) | j2) < 0;
        if (z) {
            if (j > 0 || i > 0 || i2 > 0 || j2 > 0) {
                throw new IllegalArgumentException();
            }
            j = -j;
            i = -i;
            i2 = -i2;
            j2 = -j2;
            if ((i | i2 | j2) < 0) {
                throw new IllegalArgumentException();
            }
        }
        if (i2 >= 60 || j2 >= DateTimeUtils.NANOS_PER_MINUTE) {
            throw new IllegalArgumentException();
        }
        return new Interval(IntervalQualifier.DAY_TO_SECOND, z, j, (((i * 60) + i2) * DateTimeUtils.NANOS_PER_MINUTE) + j2);
    }

    public static Interval ofHoursMinutes(long j, int i) {
        boolean z = (j | ((long) i)) < 0;
        if (z) {
            if (j > 0 || i > 0) {
                throw new IllegalArgumentException();
            }
            j = -j;
            i = -i;
        }
        return new Interval(IntervalQualifier.HOUR_TO_MINUTE, z, j, i);
    }

    public static Interval ofHoursMinutesSeconds(long j, int i, int i2) {
        return ofHoursMinutesNanos(j, i, i2 * DateTimeUtils.NANOS_PER_SECOND);
    }

    public static Interval ofHoursMinutesNanos(long j, int i, long j2) {
        boolean z = ((j | ((long) i)) | j2) < 0;
        if (z) {
            if (j > 0 || i > 0 || j2 > 0) {
                throw new IllegalArgumentException();
            }
            j = -j;
            i = -i;
            j2 = -j2;
            if ((i | j2) < 0) {
                throw new IllegalArgumentException();
            }
        }
        if (j2 >= DateTimeUtils.NANOS_PER_MINUTE) {
            throw new IllegalArgumentException();
        }
        return new Interval(IntervalQualifier.HOUR_TO_SECOND, z, j, (i * DateTimeUtils.NANOS_PER_MINUTE) + j2);
    }

    public static Interval ofMinutesSeconds(long j, int i) {
        return ofMinutesNanos(j, i * DateTimeUtils.NANOS_PER_SECOND);
    }

    public static Interval ofMinutesNanos(long j, long j2) {
        boolean z = (j | j2) < 0;
        if (z) {
            if (j > 0 || j2 > 0) {
                throw new IllegalArgumentException();
            }
            j = -j;
            j2 = -j2;
        }
        return new Interval(IntervalQualifier.MINUTE_TO_SECOND, z, j, j2);
    }

    public Interval(IntervalQualifier intervalQualifier, boolean z, long j, long j2) {
        this.qualifier = intervalQualifier;
        try {
            this.negative = IntervalUtils.validateInterval(intervalQualifier, z, j, j2);
            this.leading = j;
            this.remaining = j2;
        } catch (DbException e) {
            throw new IllegalArgumentException();
        }
    }

    public IntervalQualifier getQualifier() {
        return this.qualifier;
    }

    public boolean isNegative() {
        return this.negative;
    }

    public long getLeading() {
        return this.leading;
    }

    public long getRemaining() {
        return this.remaining;
    }

    public long getYears() {
        return IntervalUtils.yearsFromInterval(this.qualifier, this.negative, this.leading, this.remaining);
    }

    public long getMonths() {
        return IntervalUtils.monthsFromInterval(this.qualifier, this.negative, this.leading, this.remaining);
    }

    public long getDays() {
        return IntervalUtils.daysFromInterval(this.qualifier, this.negative, this.leading, this.remaining);
    }

    public long getHours() {
        return IntervalUtils.hoursFromInterval(this.qualifier, this.negative, this.leading, this.remaining);
    }

    public long getMinutes() {
        return IntervalUtils.minutesFromInterval(this.qualifier, this.negative, this.leading, this.remaining);
    }

    public long getSeconds() {
        if (this.qualifier == IntervalQualifier.SECOND) {
            return this.negative ? -this.leading : this.leading;
        }
        return getSecondsAndNanos() / DateTimeUtils.NANOS_PER_SECOND;
    }

    public long getNanosOfSecond() {
        if (this.qualifier == IntervalQualifier.SECOND) {
            return this.negative ? -this.remaining : this.remaining;
        }
        return getSecondsAndNanos() % DateTimeUtils.NANOS_PER_SECOND;
    }

    public long getSecondsAndNanos() {
        return IntervalUtils.nanosFromInterval(this.qualifier, this.negative, this.leading, this.remaining);
    }

    public int hashCode() {
        return (31 * ((31 * ((31 * ((31 * 1) + this.qualifier.hashCode())) + (this.negative ? 1231 : 1237))) + ((int) (this.leading ^ (this.leading >>> 32))))) + ((int) (this.remaining ^ (this.remaining >>> 32)));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Interval)) {
            return false;
        }
        Interval interval = (Interval) obj;
        return this.qualifier == interval.qualifier && this.negative == interval.negative && this.leading == interval.leading && this.remaining == interval.remaining;
    }

    public String toString() {
        return IntervalUtils.appendInterval(new StringBuilder(), getQualifier(), this.negative, this.leading, this.remaining).toString();
    }
}
