package ch.qos.logback.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/util/Duration.class */
public class Duration {
    private static final String DOUBLE_PART = "([0-9]*(.[0-9]+)?)";
    private static final int DOUBLE_GROUP = 1;
    private static final String UNIT_PART = "(|milli(second)?|second(e)?|minute|hour|day)s?";
    private static final int UNIT_GROUP = 3;
    private static final Pattern DURATION_PATTERN = Pattern.compile("([0-9]*(.[0-9]+)?)\\s*(|milli(second)?|second(e)?|minute|hour|day)s?", 2);
    static final long SECONDS_COEFFICIENT = 1000;
    static final long MINUTES_COEFFICIENT = 60000;
    static final long HOURS_COEFFICIENT = 3600000;
    static final long DAYS_COEFFICIENT = 86400000;
    final long millis;

    public Duration(long millis) {
        this.millis = millis;
    }

    public static Duration buildByMilliseconds(double value) {
        return new Duration((long) value);
    }

    public static Duration buildBySeconds(double value) {
        return new Duration((long) (1000.0d * value));
    }

    public static Duration buildByMinutes(double value) {
        return new Duration((long) (60000.0d * value));
    }

    public static Duration buildByHours(double value) {
        return new Duration((long) (3600000.0d * value));
    }

    public static Duration buildByDays(double value) {
        return new Duration((long) (8.64E7d * value));
    }

    public static Duration buildUnbounded() {
        return new Duration(Long.MAX_VALUE);
    }

    public long getMilliseconds() {
        return this.millis;
    }

    public static Duration valueOf(String durationStr) {
        Matcher matcher = DURATION_PATTERN.matcher(durationStr);
        if (matcher.matches()) {
            String doubleStr = matcher.group(1);
            String unitStr = matcher.group(3);
            double doubleValue = Double.valueOf(doubleStr).doubleValue();
            if (unitStr.equalsIgnoreCase("milli") || unitStr.equalsIgnoreCase("millisecond") || unitStr.length() == 0) {
                return buildByMilliseconds(doubleValue);
            }
            if (unitStr.equalsIgnoreCase("second") || unitStr.equalsIgnoreCase("seconde")) {
                return buildBySeconds(doubleValue);
            }
            if (unitStr.equalsIgnoreCase("minute")) {
                return buildByMinutes(doubleValue);
            }
            if (unitStr.equalsIgnoreCase("hour")) {
                return buildByHours(doubleValue);
            }
            if (unitStr.equalsIgnoreCase("day")) {
                return buildByDays(doubleValue);
            }
            throw new IllegalStateException("Unexpected " + unitStr);
        }
        throw new IllegalArgumentException("String value [" + durationStr + "] is not in the expected format.");
    }

    public String toString() {
        if (this.millis < 1000) {
            return this.millis + " milliseconds";
        }
        if (this.millis < 60000) {
            return (this.millis / 1000) + " seconds";
        }
        if (this.millis < 3600000) {
            return (this.millis / 60000) + " minutes";
        }
        return (this.millis / 3600000) + " hours";
    }
}
