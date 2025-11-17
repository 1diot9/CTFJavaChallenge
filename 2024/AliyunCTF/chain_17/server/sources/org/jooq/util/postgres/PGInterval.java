package org.jooq.util.postgres;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/postgres/PGInterval.class */
public class PGInterval extends PGobject {
    private static final int MICROS_IN_SECOND = 1000000;
    private int years;
    private int months;
    private int days;
    private int hours;
    private int minutes;
    private int wholeSeconds;
    private int microSeconds;
    private boolean isNull;

    public PGInterval() {
        this.type = "interval";
    }

    public PGInterval(String value) {
        this();
        setValue(value);
    }

    private int lookAhead(String value, int position, String find) {
        char[] tokens = find.toCharArray();
        int found = -1;
        for (char c : tokens) {
            found = value.indexOf(c, position);
            if (found > 0) {
                return found;
            }
        }
        return found;
    }

    private void parseISO8601Format(String value) {
        String dateValue;
        String timeValue = null;
        int hasTime = value.indexOf(84);
        if (hasTime > 0) {
            dateValue = value.substring(1, hasTime);
            timeValue = value.substring(hasTime + 1);
        } else {
            dateValue = value.substring(1);
        }
        int i = 0;
        while (i < dateValue.length()) {
            int lookAhead = lookAhead(dateValue, i, "YMD");
            if (lookAhead > 0) {
                int number = Integer.parseInt(dateValue.substring(i, lookAhead));
                if (dateValue.charAt(lookAhead) == 'Y') {
                    setYears(number);
                } else if (dateValue.charAt(lookAhead) == 'M') {
                    setMonths(number);
                } else if (dateValue.charAt(lookAhead) == 'D') {
                    setDays(number);
                }
                i = lookAhead;
            }
            i++;
        }
        if (timeValue != null) {
            int i2 = 0;
            while (i2 < timeValue.length()) {
                int lookAhead2 = lookAhead(timeValue, i2, "HMS");
                if (lookAhead2 > 0) {
                    if (timeValue.charAt(lookAhead2) == 'H') {
                        setHours(Integer.parseInt(timeValue.substring(i2, lookAhead2)));
                    } else if (timeValue.charAt(lookAhead2) == 'M') {
                        setMinutes(Integer.parseInt(timeValue.substring(i2, lookAhead2)));
                    } else if (timeValue.charAt(lookAhead2) == 'S') {
                        setSeconds(Double.parseDouble(timeValue.substring(i2, lookAhead2)));
                    }
                    i2 = lookAhead2;
                }
                i2++;
            }
        }
    }

    public PGInterval(int years, int months, int days, int hours, int minutes, double seconds) {
        this();
        setValue(years, months, days, hours, minutes, seconds);
    }

    @Override // org.jooq.util.postgres.PGobject
    public void setValue(String value) {
        this.isNull = value == null;
        if (value == null) {
            setValue(0, 0, 0, 0, 0, 0.0d);
            this.isNull = true;
            return;
        }
        boolean PostgresFormat = !value.startsWith("@");
        if (value.startsWith("P")) {
            parseISO8601Format(value);
            return;
        }
        if (!PostgresFormat && value.length() == 3 && value.charAt(2) == '0') {
            setValue(0, 0, 0, 0, 0, 0.0d);
            return;
        }
        int years = 0;
        int months = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        double seconds = 0.0d;
        String valueToken = null;
        String value2 = value.replace('+', ' ').replace('@', ' ');
        StringTokenizer st = new StringTokenizer(value2);
        int i = 1;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if ((i & 1) == 1) {
                int endHours = token.indexOf(58);
                if (endHours == -1) {
                    valueToken = token;
                } else {
                    int offset = token.charAt(0) == '-' ? 1 : 0;
                    hours = nullSafeIntGet(token.substring(offset + 0, endHours));
                    minutes = nullSafeIntGet(token.substring(endHours + 1, endHours + 3));
                    int endMinutes = token.indexOf(58, endHours + 1);
                    if (endMinutes != -1) {
                        seconds = nullSafeDoubleGet(token.substring(endMinutes + 1));
                    }
                    if (offset == 1) {
                        hours = -hours;
                        minutes = -minutes;
                        seconds = -seconds;
                    }
                    valueToken = null;
                }
            } else if (token.startsWith("year")) {
                years = nullSafeIntGet(valueToken);
            } else if (token.startsWith("mon")) {
                months = nullSafeIntGet(valueToken);
            } else if (token.startsWith("day")) {
                days = nullSafeIntGet(valueToken);
            } else if (token.startsWith("hour")) {
                hours = nullSafeIntGet(valueToken);
            } else if (token.startsWith("min")) {
                minutes = nullSafeIntGet(valueToken);
            } else if (token.startsWith("sec")) {
                seconds = nullSafeDoubleGet(valueToken);
            }
            i++;
        }
        if (!PostgresFormat && value2.endsWith("ago")) {
            setValue(-years, -months, -days, -hours, -minutes, -seconds);
        } else {
            setValue(years, months, days, hours, minutes, seconds);
        }
    }

    public void setValue(int years, int months, int days, int hours, int minutes, double seconds) {
        setYears(years);
        setMonths(months);
        setDays(days);
        setHours(hours);
        setMinutes(minutes);
        setSeconds(seconds);
    }

    @Override // org.jooq.util.postgres.PGobject
    public String getValue() {
        if (this.isNull) {
            return null;
        }
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        df.applyPattern("0.0#####");
        return String.format(Locale.ROOT, "%d years %d mons %d days %d hours %d mins %s secs", Integer.valueOf(this.years), Integer.valueOf(this.months), Integer.valueOf(this.days), Integer.valueOf(this.hours), Integer.valueOf(this.minutes), df.format(getSeconds()));
    }

    public int getYears() {
        return this.years;
    }

    public void setYears(int years) {
        this.isNull = false;
        this.years = years;
    }

    public int getMonths() {
        return this.months;
    }

    public void setMonths(int months) {
        this.isNull = false;
        this.months = months;
    }

    public int getDays() {
        return this.days;
    }

    public void setDays(int days) {
        this.isNull = false;
        this.days = days;
    }

    public int getHours() {
        return this.hours;
    }

    public void setHours(int hours) {
        this.isNull = false;
        this.hours = hours;
    }

    public int getMinutes() {
        return this.minutes;
    }

    public void setMinutes(int minutes) {
        this.isNull = false;
        this.minutes = minutes;
    }

    public double getSeconds() {
        return this.wholeSeconds + (this.microSeconds / 1000000.0d);
    }

    public int getWholeSeconds() {
        return this.wholeSeconds;
    }

    public int getMicroSeconds() {
        return this.microSeconds;
    }

    public void setSeconds(double seconds) {
        this.isNull = false;
        this.wholeSeconds = (int) seconds;
        this.microSeconds = (int) Math.round((seconds - this.wholeSeconds) * 1000000.0d);
    }

    public void add(Calendar cal) {
        if (this.isNull) {
            return;
        }
        int milliseconds = ((this.microSeconds + (this.microSeconds < 0 ? -500 : 500)) / 1000) + (this.wholeSeconds * 1000);
        cal.add(14, milliseconds);
        cal.add(12, getMinutes());
        cal.add(10, getHours());
        cal.add(5, getDays());
        cal.add(2, getMonths());
        cal.add(1, getYears());
    }

    public void add(Date date) {
        if (this.isNull) {
            return;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        add(cal);
        date.setTime(cal.getTime().getTime());
    }

    public void add(PGInterval interval) {
        if (this.isNull || interval.isNull) {
            return;
        }
        interval.setYears(interval.getYears() + getYears());
        interval.setMonths(interval.getMonths() + getMonths());
        interval.setDays(interval.getDays() + getDays());
        interval.setHours(interval.getHours() + getHours());
        interval.setMinutes(interval.getMinutes() + getMinutes());
        interval.setSeconds(interval.getSeconds() + getSeconds());
    }

    public void scale(int factor) {
        if (this.isNull) {
            return;
        }
        setYears(factor * getYears());
        setMonths(factor * getMonths());
        setDays(factor * getDays());
        setHours(factor * getHours());
        setMinutes(factor * getMinutes());
        setSeconds(factor * getSeconds());
    }

    private static int nullSafeIntGet(String value) throws NumberFormatException {
        if (value == null) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    private static double nullSafeDoubleGet(String value) throws NumberFormatException {
        if (value == null) {
            return 0.0d;
        }
        return Double.parseDouble(value);
    }

    @Override // org.jooq.util.postgres.PGobject
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PGInterval)) {
            return false;
        }
        PGInterval pgi = (PGInterval) obj;
        if (this.isNull) {
            return pgi.isNull;
        }
        return !pgi.isNull && pgi.years == this.years && pgi.months == this.months && pgi.days == this.days && pgi.hours == this.hours && pgi.minutes == this.minutes && pgi.wholeSeconds == this.wholeSeconds && pgi.microSeconds == this.microSeconds;
    }

    @Override // org.jooq.util.postgres.PGobject
    public int hashCode() {
        if (this.isNull) {
            return 0;
        }
        return (((((((((((((248 + this.microSeconds) * 31) + this.wholeSeconds) * 31) + this.minutes) * 31) + this.hours) * 31) + this.days) * 31) + this.months) * 31) + this.years) * 31;
    }

    @Override // org.jooq.util.postgres.PGobject
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
