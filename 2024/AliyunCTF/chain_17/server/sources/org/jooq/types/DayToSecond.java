package org.jooq.types;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jooq.tools.StringUtils;
import org.slf4j.Marker;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/types/DayToSecond.class */
public final class DayToSecond extends Number implements Interval, Comparable<DayToSecond> {
    private static final Pattern PATTERN_DTS = Pattern.compile("^([+-])?(?:(\\d+) )?(\\d+):(\\d+):(\\d+)(?:\\.(\\d+))?$");
    private static final Pattern PATTERN_DTM = Pattern.compile("^([+-])?(?:(\\d+) )?(\\d+):(\\d+)()()$");
    private static final Pattern PATTERN_DTH = Pattern.compile("^([+-])?(?:(\\d+) )?(\\d+)()()()$");
    private static final Pattern PATTERN_HTS = Pattern.compile("^([+-])?()(\\d+):(\\d+):(\\d+)(?:\\.(\\d+))?$");
    private static final Pattern PATTERN_HTM = Pattern.compile("^([+-])?()(\\d+):(\\d+)()()$");
    private static final Pattern PATTERN_MTS = Pattern.compile("^([+-])?()()(\\d+):(\\d+)(?:\\.(\\d+))?$");
    private final boolean negative;
    private final int days;
    private final int hours;
    private final int minutes;
    private final int seconds;
    private final int nano;

    public DayToSecond() {
        this(0, 0, 0, 0, 0, false);
    }

    public DayToSecond(int days) {
        this(days, 0, 0, 0, 0, false);
    }

    public DayToSecond(int days, int hours) {
        this(days, hours, 0, 0, 0, false);
    }

    public DayToSecond(int days, int hours, int minutes) {
        this(days, hours, minutes, 0, 0, false);
    }

    public DayToSecond(int days, int hours, int minutes, int seconds) {
        this(days, hours, minutes, seconds, 0, false);
    }

    public DayToSecond(int days, int hours, int minutes, int seconds, int nano) {
        this(days, hours, minutes, seconds, nano, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DayToSecond(int days, int hours, int minutes, int seconds, int nano, boolean negative) {
        if (Math.abs(nano) >= 1000000000) {
            seconds += nano / 1000000000;
            nano %= 1000000000;
        }
        if (Math.abs(seconds) >= 60) {
            minutes += seconds / 60;
            seconds %= 60;
        }
        if (Math.abs(minutes) >= 60) {
            hours += minutes / 60;
            minutes %= 60;
        }
        if (Math.abs(hours) >= 24) {
            days += hours / 24;
            hours %= 24;
        }
        this.negative = negative;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.nano = nano;
    }

    public static DayToSecond valueOf(String string) {
        if (string != null) {
            try {
                return valueOf(Double.parseDouble(string));
            } catch (NumberFormatException e) {
                DayToSecond result = dayToSecond(string);
                if (result != null) {
                    return result;
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

    public static DayToSecond day(String string) {
        if (string == null) {
            return null;
        }
        try {
            return new DayToSecond(Integer.parseInt(string));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static DayToSecond dayToHour(String string) {
        if (string != null) {
            Matcher matcher = PATTERN_DTH.matcher(string);
            if (matcher.find()) {
                return YearToSecond.parseDS(matcher, 0);
            }
            return null;
        }
        return null;
    }

    public static DayToSecond dayToMinute(String string) {
        if (string != null) {
            Matcher matcher = PATTERN_DTM.matcher(string);
            if (matcher.find()) {
                return YearToSecond.parseDS(matcher, 0);
            }
            return null;
        }
        return null;
    }

    public static DayToSecond dayToSecond(String string) {
        if (string != null) {
            Matcher matcher = PATTERN_DTS.matcher(string);
            if (matcher.find()) {
                return YearToSecond.parseDS(matcher, 0);
            }
            return null;
        }
        return null;
    }

    public static DayToSecond hour(String string) {
        if (string == null) {
            return null;
        }
        try {
            return new DayToSecond(0, Integer.parseInt(string));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static DayToSecond hourToMinute(String string) {
        if (string != null) {
            Matcher matcher = PATTERN_HTM.matcher(string);
            if (matcher.find()) {
                return YearToSecond.parseDS(matcher, 0);
            }
            return null;
        }
        return null;
    }

    public static DayToSecond hourToSecond(String string) {
        if (string != null) {
            Matcher matcher = PATTERN_HTS.matcher(string);
            if (matcher.find()) {
                return YearToSecond.parseDS(matcher, 0);
            }
            return null;
        }
        return null;
    }

    public static DayToSecond minute(String string) {
        if (string == null) {
            return null;
        }
        try {
            return new DayToSecond(0, 0, Integer.parseInt(string));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static DayToSecond minuteToSecond(String string) {
        if (string != null) {
            Matcher matcher = PATTERN_MTS.matcher(string);
            if (matcher.find()) {
                return YearToSecond.parseDS(matcher, 0);
            }
            return null;
        }
        return null;
    }

    public static DayToSecond second(String string) {
        if (string == null) {
            return null;
        }
        try {
            return valueOf(Double.parseDouble(string) * 1000.0d);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static DayToSecond valueOf(double milli) {
        double abs = Math.abs(milli);
        int n = (int) ((abs % 1000.0d) * 1000000.0d);
        double abs2 = Math.floor(abs / 1000.0d);
        int s = (int) (abs2 % 60.0d);
        double abs3 = Math.floor(abs2 / 60.0d);
        int m = (int) (abs3 % 60.0d);
        double abs4 = Math.floor(abs3 / 60.0d);
        int h = (int) (abs4 % 24.0d);
        int d = (int) Math.floor(abs4 / 24.0d);
        DayToSecond result = new DayToSecond(d, h, m, s, n);
        if (milli < 0.0d) {
            result = result.neg();
        }
        return result;
    }

    public static DayToSecond valueOf(long second, int nanos) {
        long abs = Math.abs(second);
        int s = (int) (abs % 60);
        long abs2 = abs / 60;
        int m = (int) (abs2 % 60);
        long abs3 = abs2 / 60;
        int h = (int) (abs3 % 24);
        int d = (int) (abs3 / 24);
        DayToSecond result = new DayToSecond(d, h, m, s, nanos);
        if (second < 0) {
            result = result.neg();
        }
        return result;
    }

    public static DayToSecond valueOf(Duration duration) {
        if (duration == null) {
            return null;
        }
        long s = duration.get(ChronoUnit.SECONDS);
        int n = (int) duration.get(ChronoUnit.NANOS);
        if (s < 0) {
            n = 1000000000 - n;
            s++;
        }
        return valueOf(s, n);
    }

    @Override // org.jooq.types.Interval
    public final Duration toDuration() {
        return Duration.ofSeconds((long) getTotalSeconds(), getSign() * getNano());
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
        return getTotalMilli();
    }

    @Override // org.jooq.types.Interval
    public final DayToSecond neg() {
        return new DayToSecond(this.days, this.hours, this.minutes, this.seconds, this.nano, !this.negative);
    }

    @Override // org.jooq.types.Interval
    public final DayToSecond abs() {
        return new DayToSecond(this.days, this.hours, this.minutes, this.seconds, this.nano, false);
    }

    public final int getDays() {
        return this.days;
    }

    public final int getHours() {
        return this.hours;
    }

    public final int getMinutes() {
        return this.minutes;
    }

    public final int getSeconds() {
        return this.seconds;
    }

    public final int getMilli() {
        return this.nano / 1000000;
    }

    public final int getMicro() {
        return this.nano / 1000;
    }

    public final int getNano() {
        return this.nano;
    }

    public final double getTotalDays() {
        return getSign() * ((this.nano / 8.64E13d) + (this.seconds / 86400.0d) + (this.minutes / 1440.0d) + (this.hours / 24.0d) + this.days);
    }

    public final double getTotalHours() {
        return getSign() * ((this.nano / 3.6E12d) + (this.seconds / 3600.0d) + (this.minutes / 60.0d) + this.hours + (24.0d * this.days));
    }

    public final double getTotalMinutes() {
        return getSign() * ((this.nano / 6.0E10d) + (this.seconds / 60.0d) + this.minutes + (60.0d * this.hours) + (1440.0d * this.days));
    }

    public final double getTotalSeconds() {
        return getSign() * ((this.nano / 1.0E9d) + this.seconds + (60.0d * this.minutes) + (3600.0d * this.hours) + (86400.0d * this.days));
    }

    public final double getTotalMilli() {
        return getSign() * ((this.nano / 1000000.0d) + (1000.0d * this.seconds) + (60000.0d * this.minutes) + (3600000.0d * this.hours) + (8.64E7d * this.days));
    }

    public final double getTotalMicro() {
        return getSign() * ((this.nano / 1000.0d) + (1000000.0d * this.seconds) + (6.0E7d * this.minutes) + (3.6E9d * this.hours) + (8.64E10d * this.days));
    }

    public final double getTotalNano() {
        return getSign() * (this.nano + (1.0E9d * this.seconds) + (6.0E10d * this.minutes) + (3.6E12d * this.hours) + (8.64E13d * this.days));
    }

    @Override // org.jooq.types.Interval
    public final int getSign() {
        return this.negative ? -1 : 1;
    }

    @Override // java.lang.Comparable
    public final int compareTo(DayToSecond that) {
        if (this.days < that.days) {
            return -1;
        }
        if (this.days > that.days) {
            return 1;
        }
        if (this.hours < that.hours) {
            return -1;
        }
        if (this.hours > that.hours) {
            return 1;
        }
        if (this.minutes < that.minutes) {
            return -1;
        }
        if (this.minutes > that.minutes) {
            return 1;
        }
        if (this.seconds < that.seconds) {
            return -1;
        }
        if (this.seconds > that.seconds) {
            return 1;
        }
        return Integer.compare(this.nano, that.nano);
    }

    public final int hashCode() {
        int result = 0;
        if (this.days != 0) {
            result = (31 * 0) + this.days;
        }
        if (this.hours != 0) {
            result = (31 * result) + this.hours;
        }
        if (this.minutes != 0) {
            result = (31 * result) + this.minutes;
        }
        if (this.nano != 0) {
            result = (31 * result) + this.nano;
        }
        if (this.seconds != 0) {
            result = (31 * result) + this.seconds;
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
            DayToSecond other = (DayToSecond) obj;
            if (this.days != other.days || this.hours != other.hours || this.minutes != other.minutes || this.nano != other.nano || this.seconds != other.seconds) {
                return false;
            }
            if (this.negative != other.negative && doubleValue() != 0.0d) {
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
        sb.append(this.days);
        sb.append(" ");
        if (this.hours < 10) {
            sb.append(CustomBooleanEditor.VALUE_0);
        }
        sb.append(this.hours);
        sb.append(":");
        if (this.minutes < 10) {
            sb.append(CustomBooleanEditor.VALUE_0);
        }
        sb.append(this.minutes);
        sb.append(":");
        if (this.seconds < 10) {
            sb.append(CustomBooleanEditor.VALUE_0);
        }
        sb.append(this.seconds);
        sb.append(".");
        sb.append(StringUtils.leftPad(this.nano, 9, CustomBooleanEditor.VALUE_0));
        return sb.toString();
    }
}
