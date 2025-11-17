package org.jooq.types;

import java.time.Duration;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jooq.tools.Convert;
import org.jooq.tools.StringUtils;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/types/YearToSecond.class */
public final class YearToSecond extends Number implements Interval, Comparable<YearToSecond> {
    private static final Pattern PATTERN = Pattern.compile("^([+-])?(\\d+)-(\\d+) ([+-])?(?:(\\d+) )?(\\d+):(\\d+):(\\d+)(?:\\.(\\d+))?$");
    private final YearToMonth yearToMonth;
    private final DayToSecond dayToSecond;

    public YearToSecond(YearToMonth yearToMonth, DayToSecond dayToSecond) {
        this.yearToMonth = yearToMonth == null ? new YearToMonth() : yearToMonth;
        this.dayToSecond = dayToSecond == null ? new DayToSecond() : dayToSecond;
    }

    public static YearToSecond valueOf(double milli) {
        double abs = Math.abs(milli);
        int y = (int) (abs / 3.15576E10d);
        double abs2 = abs % 3.15576E10d;
        int m = (int) (abs2 / 2.592E9d);
        YearToSecond result = new YearToSecond(new YearToMonth(y, m), DayToSecond.valueOf(abs2 % 2.592E9d));
        if (milli < 0.0d) {
            result = result.neg();
        }
        return result;
    }

    public static YearToSecond valueOf(Duration duration) {
        if (duration == null) {
            return null;
        }
        return valueOf(duration.toMillis());
    }

    @Override // org.jooq.types.Interval
    public final Duration toDuration() {
        return this.yearToMonth.toDuration().plus(this.dayToSecond.toDuration());
    }

    public static YearToSecond valueOf(Period period) {
        if (period == null) {
            return null;
        }
        return new YearToSecond(new YearToMonth(period.getYears(), period.getMonths()), new DayToSecond(period.getDays()));
    }

    public static YearToSecond valueOf(String string) {
        if (string != null) {
            try {
                return valueOf(Double.parseDouble(string));
            } catch (NumberFormatException e) {
                Matcher matcher = PATTERN.matcher(string);
                if (matcher.find()) {
                    return new YearToSecond(parseYM(matcher, 0), parseDS(matcher, 3));
                }
                try {
                    return valueOf(Duration.parse(string));
                } catch (DateTimeParseException e2) {
                    return null;
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static YearToMonth parseYM(Matcher matcher, int groupOffset) {
        boolean negativeYM = "-".equals(matcher.group(groupOffset + 1));
        int years = Integer.parseInt(matcher.group(groupOffset + 2));
        int months = Integer.parseInt(matcher.group(groupOffset + 3));
        return new YearToMonth(years, months, negativeYM);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static DayToSecond parseDS(Matcher matcher, int groupOffset) {
        boolean negativeDS = "-".equals(matcher.group(groupOffset + 1));
        int days = ((Integer) Convert.convert(matcher.group(groupOffset + 2), Integer.TYPE)).intValue();
        int hours = ((Integer) Convert.convert(matcher.group(groupOffset + 3), Integer.TYPE)).intValue();
        int minutes = ((Integer) Convert.convert(matcher.group(groupOffset + 4), Integer.TYPE)).intValue();
        int seconds = ((Integer) Convert.convert(matcher.group(groupOffset + 5), Integer.TYPE)).intValue();
        int nano = ((Integer) Convert.convert(StringUtils.rightPad(matcher.group(groupOffset + 6), 9, CustomBooleanEditor.VALUE_0), Integer.TYPE)).intValue();
        return new DayToSecond(days, hours, minutes, seconds, nano, negativeDS);
    }

    @Override // org.jooq.types.Interval
    public final YearToSecond neg() {
        return new YearToSecond(this.yearToMonth.neg(), this.dayToSecond.neg());
    }

    @Override // org.jooq.types.Interval
    public final YearToSecond abs() {
        return new YearToSecond(this.yearToMonth.abs(), this.dayToSecond.abs());
    }

    public final YearToMonth getYearToMonth() {
        return this.yearToMonth;
    }

    public final DayToSecond getDayToSecond() {
        return this.dayToSecond;
    }

    public final int getYears() {
        return this.yearToMonth.getYears();
    }

    public final int getMonths() {
        return this.yearToMonth.getMonths();
    }

    public final int getDays() {
        return this.dayToSecond.getDays();
    }

    public final int getHours() {
        return this.dayToSecond.getHours();
    }

    public final int getMinutes() {
        return this.dayToSecond.getMinutes();
    }

    public final int getSeconds() {
        return this.dayToSecond.getSeconds();
    }

    public final int getMilli() {
        return this.dayToSecond.getMilli();
    }

    public final int getMicro() {
        return this.dayToSecond.getMicro();
    }

    public final int getNano() {
        return this.dayToSecond.getNano();
    }

    @Override // org.jooq.types.Interval
    public final int getSign() {
        double value = doubleValue();
        if (value > 0.0d) {
            return 1;
        }
        if (value < 0.0d) {
            return -1;
        }
        return 0;
    }

    @Override // java.lang.Number, org.jooq.types.Interval
    public final int intValue() {
        return (int) doubleValue();
    }

    @Override // java.lang.Number, org.jooq.types.Interval
    public final long longValue() {
        return (long) doubleValue();
    }

    @Override // java.lang.Number, org.jooq.types.Interval
    public final float floatValue() {
        return (float) doubleValue();
    }

    @Override // java.lang.Number, org.jooq.types.Interval
    public final double doubleValue() {
        return (((this.yearToMonth.getYears() * 365.25d) + (this.yearToMonth.getMonths() * 30)) * 8.64E7d * this.yearToMonth.getSign()) + this.dayToSecond.doubleValue();
    }

    @Override // java.lang.Comparable
    public final int compareTo(YearToSecond that) {
        return Double.compare(doubleValue(), that.doubleValue());
    }

    public int hashCode() {
        int result = 0;
        int h1 = this.dayToSecond.hashCode();
        int h2 = this.yearToMonth.hashCode();
        if (h1 != 0) {
            result = (31 * 0) + h1;
        }
        if (h2 != 0) {
            result = (31 * result) + h2;
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() == obj.getClass()) {
            YearToSecond other = (YearToSecond) obj;
            if (this.dayToSecond == null) {
                if (other.dayToSecond != null) {
                    return false;
                }
            } else if (!this.dayToSecond.equals(other.dayToSecond)) {
                return false;
            }
            if (this.yearToMonth == null) {
                if (other.yearToMonth != null) {
                    return false;
                }
                return true;
            }
            if (!this.yearToMonth.equals(other.yearToMonth)) {
                return false;
            }
            return true;
        }
        if (obj instanceof YearToMonth) {
            YearToMonth other2 = (YearToMonth) obj;
            return getDayToSecond().intValue() == 0 && getYearToMonth().equals(other2);
        }
        if (obj instanceof DayToSecond) {
            DayToSecond other3 = (DayToSecond) obj;
            return getYearToMonth().intValue() == 0 && getDayToSecond().equals(other3);
        }
        return false;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.yearToMonth);
        sb.append(' ');
        sb.append(this.dayToSecond);
        return sb.toString();
    }
}
