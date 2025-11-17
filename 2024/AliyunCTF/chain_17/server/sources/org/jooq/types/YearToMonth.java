package org.jooq.types;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Marker;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/types/YearToMonth.class */
public final class YearToMonth extends Number implements Interval, Comparable<YearToMonth> {
    private static final Pattern PATTERN_SQL = Pattern.compile("^([+-])?(\\d+)-(\\d+)$");
    private static final Pattern PATTERN_ISO = Pattern.compile("^([+-])?P(?:([+-]?\\d+)Y)?(?:([+-]?\\d+)M)?$", 2);
    private final boolean negative;
    private final int years;
    private final int months;

    public YearToMonth() {
        this(0, 0, false);
    }

    public YearToMonth(int years) {
        this(years, 0, false);
    }

    public YearToMonth(int years, int months) {
        this(years, months, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public YearToMonth(int years, int months, boolean negative) {
        if (Math.abs(months) >= 12) {
            years += months / 12;
            months %= 12;
        }
        this.negative = negative;
        this.years = years;
        this.months = months;
    }

    public static YearToMonth valueOf(String string) {
        if (string != null) {
            Matcher matcher = PATTERN_SQL.matcher(string);
            if (matcher.find()) {
                return YearToSecond.parseYM(matcher, 0);
            }
            Matcher matcher2 = PATTERN_ISO.matcher(string);
            if (matcher2.find()) {
                boolean negative = "-".equals(matcher2.group(1));
                String group2 = matcher2.group(2);
                String group3 = matcher2.group(3);
                int years = group2 == null ? 0 : Integer.parseInt(group2);
                int months = group3 == null ? 0 : Integer.parseInt(group3);
                return new YearToMonth(years, months, negative);
            }
            return yearToMonth(string);
        }
        return null;
    }

    public static YearToMonth year(String string) {
        if (string == null) {
            return null;
        }
        try {
            return new YearToMonth(Integer.parseInt(string));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static YearToMonth yearToMonth(String string) {
        if (string != null) {
            Matcher matcher = PATTERN_SQL.matcher(string);
            if (matcher.find()) {
                return YearToSecond.parseYM(matcher, 0);
            }
            return null;
        }
        return null;
    }

    public static YearToMonth month(String string) {
        if (string == null) {
            return null;
        }
        try {
            return new YearToMonth(0, Integer.parseInt(string));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override // org.jooq.types.Interval
    public final Duration toDuration() {
        long hours = (this.years * 8766) + (this.months * 720);
        if (this.negative) {
            hours = -hours;
        }
        return Duration.ofHours(hours);
    }

    @Override // org.jooq.types.Interval
    public final YearToMonth neg() {
        return new YearToMonth(this.years, this.months, !this.negative);
    }

    @Override // org.jooq.types.Interval
    public final YearToMonth abs() {
        return new YearToMonth(this.years, this.months, false);
    }

    public final int getYears() {
        return this.years;
    }

    public final int getMonths() {
        return this.months;
    }

    @Override // org.jooq.types.Interval
    public final int getSign() {
        return this.negative ? -1 : 1;
    }

    @Override // java.lang.Number, org.jooq.types.Interval
    public final int intValue() {
        return (this.negative ? -1 : 1) * ((12 * this.years) + this.months);
    }

    @Override // java.lang.Number, org.jooq.types.Interval
    public final long longValue() {
        return intValue();
    }

    @Override // java.lang.Number, org.jooq.types.Interval
    public final float floatValue() {
        return intValue();
    }

    @Override // java.lang.Number, org.jooq.types.Interval
    public final double doubleValue() {
        return intValue();
    }

    @Override // java.lang.Comparable
    public final int compareTo(YearToMonth that) {
        if (this.years < that.years) {
            return -1;
        }
        if (this.years > that.years) {
            return 1;
        }
        if (this.months < that.months) {
            return -1;
        }
        if (this.months > that.months) {
            return 1;
        }
        return 0;
    }

    public final int hashCode() {
        int result = 0;
        if (this.months != 0) {
            result = (31 * 0) + this.months;
        }
        if (this.years != 0) {
            result = (31 * result) + this.years;
        }
        return result;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() == obj.getClass()) {
            YearToMonth other = (YearToMonth) obj;
            if (this.months != other.months || this.years != other.years) {
                return false;
            }
            if (this.negative != other.negative && intValue() != 0) {
                return false;
            }
            return true;
        }
        if (obj instanceof YearToSecond) {
            return obj.equals(this);
        }
        return false;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.negative ? "-" : Marker.ANY_NON_NULL_MARKER);
        sb.append(this.years);
        sb.append("-");
        sb.append(this.months);
        return sb.toString();
    }
}
