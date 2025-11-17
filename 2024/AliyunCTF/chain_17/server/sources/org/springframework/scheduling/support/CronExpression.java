package org.springframework.scheduling.support;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Arrays;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/support/CronExpression.class */
public final class CronExpression {
    static final int MAX_ATTEMPTS = 366;
    private static final String[] MACROS = {"@yearly", "0 0 0 1 1 *", "@annually", "0 0 0 1 1 *", "@monthly", "0 0 0 1 * *", "@weekly", "0 0 0 * * 0", "@daily", "0 0 0 * * *", "@midnight", "0 0 0 * * *", "@hourly", "0 0 * * * *"};
    private final CronField[] fields;
    private final String expression;

    private CronExpression(CronField seconds, CronField minutes, CronField hours, CronField daysOfMonth, CronField months, CronField daysOfWeek, String expression) {
        this.fields = new CronField[]{daysOfWeek, months, daysOfMonth, hours, minutes, seconds, CronField.zeroNanos()};
        this.expression = expression;
    }

    public static CronExpression parse(String expression) {
        Assert.hasLength(expression, "Expression must not be empty");
        String expression2 = resolveMacros(expression);
        String[] fields = StringUtils.tokenizeToStringArray(expression2, " ");
        if (fields.length != 6) {
            throw new IllegalArgumentException(String.format("Cron expression must consist of 6 fields (found %d in \"%s\")", Integer.valueOf(fields.length), expression2));
        }
        try {
            CronField seconds = CronField.parseSeconds(fields[0]);
            CronField minutes = CronField.parseMinutes(fields[1]);
            CronField hours = CronField.parseHours(fields[2]);
            CronField daysOfMonth = CronField.parseDaysOfMonth(fields[3]);
            CronField months = CronField.parseMonth(fields[4]);
            CronField daysOfWeek = CronField.parseDaysOfWeek(fields[5]);
            return new CronExpression(seconds, minutes, hours, daysOfMonth, months, daysOfWeek, expression2);
        } catch (IllegalArgumentException ex) {
            String msg = ex.getMessage() + " in cron expression \"" + expression2 + "\"";
            throw new IllegalArgumentException(msg, ex);
        }
    }

    public static boolean isValidExpression(@Nullable String expression) {
        if (expression == null) {
            return false;
        }
        try {
            parse(expression);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static String resolveMacros(String expression) {
        String expression2 = expression.trim();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < MACROS.length) {
                if (!MACROS[i2].equalsIgnoreCase(expression2)) {
                    i = i2 + 2;
                } else {
                    return MACROS[i2 + 1];
                }
            } else {
                return expression2;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Nullable
    public <T extends Temporal & Comparable<? super T>> T next(T t) {
        return (T) nextOrSame(ChronoUnit.NANOS.addTo(t, 1L));
    }

    @Nullable
    private <T extends Temporal & Comparable<? super T>> T nextOrSame(T t) {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            T t2 = (T) nextOrSameInternal(t);
            if (t2 == null || t2.equals(t)) {
                return t2;
            }
            t = t2;
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v10, types: [java.time.temporal.Temporal] */
    @Nullable
    private <T extends Temporal & Comparable<? super T>> T nextOrSameInternal(T temporal) {
        for (CronField field : this.fields) {
            temporal = field.nextOrSame(temporal);
            if (temporal == null) {
                return null;
            }
        }
        return temporal;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof CronExpression) {
                CronExpression that = (CronExpression) other;
                if (Arrays.equals(this.fields, that.fields)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Arrays.hashCode(this.fields);
    }

    public String toString() {
        return this.expression;
    }
}
