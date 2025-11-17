package org.springframework.scheduling.support;

import java.time.DateTimeException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.ValueRange;
import java.util.function.BiFunction;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/support/CronField.class */
public abstract class CronField {
    private static final String[] MONTHS = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
    private static final String[] DAYS = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
    private final Type type;

    @Nullable
    public abstract <T extends Temporal & Comparable<? super T>> T nextOrSame(T temporal);

    /* JADX INFO: Access modifiers changed from: protected */
    public CronField(Type type) {
        this.type = type;
    }

    public static CronField zeroNanos() {
        return BitsCronField.zeroNanos();
    }

    public static CronField parseSeconds(String value) {
        return BitsCronField.parseSeconds(value);
    }

    public static CronField parseMinutes(String value) {
        return BitsCronField.parseMinutes(value);
    }

    public static CronField parseHours(String value) {
        return BitsCronField.parseHours(value);
    }

    public static CronField parseDaysOfMonth(String value) {
        if (!QuartzCronField.isQuartzDaysOfMonthField(value)) {
            return BitsCronField.parseDaysOfMonth(value);
        }
        return parseList(value, Type.DAY_OF_MONTH, (field, type) -> {
            if (QuartzCronField.isQuartzDaysOfMonthField(field)) {
                return QuartzCronField.parseDaysOfMonth(field);
            }
            return BitsCronField.parseDaysOfMonth(field);
        });
    }

    public static CronField parseMonth(String value) {
        return BitsCronField.parseMonth(replaceOrdinals(value, MONTHS));
    }

    public static CronField parseDaysOfWeek(String value) {
        String value2 = replaceOrdinals(value, DAYS);
        if (!QuartzCronField.isQuartzDaysOfWeekField(value2)) {
            return BitsCronField.parseDaysOfWeek(value2);
        }
        return parseList(value2, Type.DAY_OF_WEEK, (field, type) -> {
            if (QuartzCronField.isQuartzDaysOfWeekField(field)) {
                return QuartzCronField.parseDaysOfWeek(field);
            }
            return BitsCronField.parseDaysOfWeek(field);
        });
    }

    private static CronField parseList(String value, Type type, BiFunction<String, Type, CronField> parseFieldFunction) {
        Assert.hasLength(value, "Value must not be empty");
        String[] fields = StringUtils.delimitedListToStringArray(value, ",");
        CronField[] cronFields = new CronField[fields.length];
        for (int i = 0; i < fields.length; i++) {
            cronFields[i] = parseFieldFunction.apply(fields[i], type);
        }
        return CompositeCronField.compose(cronFields, type, value);
    }

    private static String replaceOrdinals(String value, String[] list) {
        String value2 = value.toUpperCase();
        for (int i = 0; i < list.length; i++) {
            String replacement = Integer.toString(i + 1);
            value2 = StringUtils.replace(value2, list[i], replacement);
        }
        return value2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Type type() {
        return this.type;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    public static <T extends Temporal & Comparable<? super T>> T cast(Temporal temporal) {
        return temporal;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/support/CronField$Type.class */
    public enum Type {
        NANO(ChronoField.NANO_OF_SECOND, ChronoUnit.SECONDS, new ChronoField[0]),
        SECOND(ChronoField.SECOND_OF_MINUTE, ChronoUnit.MINUTES, ChronoField.NANO_OF_SECOND),
        MINUTE(ChronoField.MINUTE_OF_HOUR, ChronoUnit.HOURS, ChronoField.SECOND_OF_MINUTE, ChronoField.NANO_OF_SECOND),
        HOUR(ChronoField.HOUR_OF_DAY, ChronoUnit.DAYS, ChronoField.MINUTE_OF_HOUR, ChronoField.SECOND_OF_MINUTE, ChronoField.NANO_OF_SECOND),
        DAY_OF_MONTH(ChronoField.DAY_OF_MONTH, ChronoUnit.MONTHS, ChronoField.HOUR_OF_DAY, ChronoField.MINUTE_OF_HOUR, ChronoField.SECOND_OF_MINUTE, ChronoField.NANO_OF_SECOND),
        MONTH(ChronoField.MONTH_OF_YEAR, ChronoUnit.YEARS, ChronoField.DAY_OF_MONTH, ChronoField.HOUR_OF_DAY, ChronoField.MINUTE_OF_HOUR, ChronoField.SECOND_OF_MINUTE, ChronoField.NANO_OF_SECOND),
        DAY_OF_WEEK(ChronoField.DAY_OF_WEEK, ChronoUnit.WEEKS, ChronoField.HOUR_OF_DAY, ChronoField.MINUTE_OF_HOUR, ChronoField.SECOND_OF_MINUTE, ChronoField.NANO_OF_SECOND);

        private final ChronoField field;
        private final ChronoUnit higherOrder;
        private final ChronoField[] lowerOrders;

        Type(ChronoField field, ChronoUnit higherOrder, ChronoField... lowerOrders) {
            this.field = field;
            this.higherOrder = higherOrder;
            this.lowerOrders = lowerOrders;
        }

        public int get(Temporal date) {
            return date.get(this.field);
        }

        public ValueRange range() {
            return this.field.range();
        }

        public int checkValidValue(int value) {
            if (this == DAY_OF_WEEK && value == 0) {
                return value;
            }
            try {
                return this.field.checkValidIntValue(value);
            } catch (DateTimeException ex) {
                throw new IllegalArgumentException(ex.getMessage(), ex);
            }
        }

        public <T extends Temporal & Comparable<? super T>> T elapseUntil(T t, int i) {
            int i2 = get(t);
            ValueRange range = t.range(this.field);
            if (i2 < i) {
                if (range.isValidIntValue(i)) {
                    return (T) CronField.cast(t.with(this.field, i));
                }
                return (T) this.field.getBaseUnit().addTo(t, (range.getMaximum() - i2) + 1);
            }
            return (T) this.field.getBaseUnit().addTo(t, (((i + range.getMaximum()) - i2) + 1) - range.getMinimum());
        }

        public <T extends Temporal & Comparable<? super T>> T rollForward(T t) {
            Temporal addTo = this.higherOrder.addTo(t, 1L);
            return (T) this.field.adjustInto(addTo, addTo.range(this.field).getMinimum());
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v12, types: [java.time.temporal.Temporal] */
        public <T extends Temporal> T reset(T temporal) {
            for (ChronoField lowerOrder : this.lowerOrders) {
                if (temporal.isSupported(lowerOrder)) {
                    temporal = lowerOrder.adjustInto(temporal, temporal.range(lowerOrder).getMinimum());
                }
            }
            return temporal;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.field.toString();
        }
    }
}
