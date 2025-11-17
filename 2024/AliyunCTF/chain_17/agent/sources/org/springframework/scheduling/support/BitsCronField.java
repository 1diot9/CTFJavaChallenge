package org.springframework.scheduling.support;

import ch.qos.logback.core.CoreConstants;
import java.time.DateTimeException;
import java.time.temporal.Temporal;
import java.time.temporal.ValueRange;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.support.CronField;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/support/BitsCronField.class */
public final class BitsCronField extends CronField {
    private static final long MASK = -1;

    @Nullable
    private static BitsCronField zeroNanos = null;
    private long bits;

    private BitsCronField(CronField.Type type) {
        super(type);
    }

    public static BitsCronField zeroNanos() {
        if (zeroNanos == null) {
            BitsCronField field = new BitsCronField(CronField.Type.NANO);
            field.setBit(0);
            zeroNanos = field;
        }
        return zeroNanos;
    }

    public static BitsCronField parseSeconds(String value) {
        return parseField(value, CronField.Type.SECOND);
    }

    public static BitsCronField parseMinutes(String value) {
        return parseField(value, CronField.Type.MINUTE);
    }

    public static BitsCronField parseHours(String value) {
        return parseField(value, CronField.Type.HOUR);
    }

    public static BitsCronField parseDaysOfMonth(String value) {
        return parseDate(value, CronField.Type.DAY_OF_MONTH);
    }

    public static BitsCronField parseMonth(String value) {
        return parseField(value, CronField.Type.MONTH);
    }

    public static BitsCronField parseDaysOfWeek(String value) {
        BitsCronField result = parseDate(value, CronField.Type.DAY_OF_WEEK);
        if (result.getBit(0)) {
            result.setBit(7);
            result.clearBit(0);
        }
        return result;
    }

    private static BitsCronField parseDate(String value, CronField.Type type) {
        if (value.equals(CoreConstants.NA)) {
            value = "*";
        }
        return parseField(value, type);
    }

    private static BitsCronField parseField(String value, CronField.Type type) {
        Assert.hasLength(value, "Value must not be empty");
        Assert.notNull(type, "Type must not be null");
        try {
            BitsCronField result = new BitsCronField(type);
            String[] fields = StringUtils.delimitedListToStringArray(value, ",");
            for (String field : fields) {
                int slashPos = field.indexOf(47);
                if (slashPos == -1) {
                    result.setBits(parseRange(field, type));
                } else {
                    String rangeStr = field.substring(0, slashPos);
                    String deltaStr = field.substring(slashPos + 1);
                    ValueRange range = parseRange(rangeStr, type);
                    if (rangeStr.indexOf(45) == -1) {
                        range = ValueRange.of(range.getMinimum(), type.range().getMaximum());
                    }
                    int delta = Integer.parseInt(deltaStr);
                    if (delta <= 0) {
                        throw new IllegalArgumentException("Incrementer delta must be 1 or higher");
                    }
                    result.setBits(range, delta);
                }
            }
            return result;
        } catch (IllegalArgumentException | DateTimeException ex) {
            String msg = ex.getMessage() + " '" + value + "'";
            throw new IllegalArgumentException(msg, ex);
        }
    }

    private static ValueRange parseRange(String value, CronField.Type type) {
        if (value.equals("*")) {
            return type.range();
        }
        int hyphenPos = value.indexOf(45);
        if (hyphenPos == -1) {
            int result = type.checkValidValue(Integer.parseInt(value));
            return ValueRange.of(result, result);
        }
        int min = Integer.parseInt(value, 0, hyphenPos, 10);
        int max = Integer.parseInt(value, hyphenPos + 1, value.length(), 10);
        int min2 = type.checkValidValue(min);
        int max2 = type.checkValidValue(max);
        if (type == CronField.Type.DAY_OF_WEEK && min2 == 7) {
            min2 = 0;
        }
        return ValueRange.of(min2, max2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v20, types: [java.time.temporal.Temporal] */
    /* JADX WARN: Type inference failed for: r0v29, types: [java.time.temporal.Temporal] */
    /* JADX WARN: Type inference failed for: r0v35, types: [java.time.temporal.Temporal] */
    @Override // org.springframework.scheduling.support.CronField
    @Nullable
    public <T extends Temporal & Comparable<? super T>> T nextOrSame(T t) {
        int i = type().get(t);
        int nextSetBit = nextSetBit(i);
        if (nextSetBit == -1) {
            t = type().rollForward(t);
            nextSetBit = nextSetBit(0);
        }
        if (nextSetBit == i) {
            return t;
        }
        int i2 = 0;
        int i3 = type().get(t);
        while (i3 != nextSetBit) {
            int i4 = i2;
            i2++;
            if (i4 >= 366) {
                break;
            }
            t = type().elapseUntil(t, nextSetBit);
            i3 = type().get(t);
            nextSetBit = nextSetBit(i3);
            if (nextSetBit == -1) {
                t = type().rollForward(t);
                nextSetBit = nextSetBit(0);
            }
        }
        if (i2 >= 366) {
            return null;
        }
        return (T) type().reset(t);
    }

    boolean getBit(int index) {
        return (this.bits & (1 << index)) != 0;
    }

    private int nextSetBit(int fromIndex) {
        long result = this.bits & ((-1) << fromIndex);
        if (result != 0) {
            return Long.numberOfTrailingZeros(result);
        }
        return -1;
    }

    private void setBits(ValueRange range) {
        if (range.getMinimum() == range.getMaximum()) {
            setBit((int) range.getMinimum());
            return;
        }
        long minMask = (-1) << ((int) range.getMinimum());
        long maxMask = (-1) >>> ((int) (-(range.getMaximum() + 1)));
        this.bits |= minMask & maxMask;
    }

    private void setBits(ValueRange range, int delta) {
        if (delta == 1) {
            setBits(range);
            return;
        }
        int minimum = (int) range.getMinimum();
        while (true) {
            int i = minimum;
            if (i <= range.getMaximum()) {
                setBit(i);
                minimum = i + delta;
            } else {
                return;
            }
        }
    }

    private void setBit(int index) {
        this.bits |= 1 << index;
    }

    private void clearBit(int index) {
        this.bits &= (1 << index) ^ (-1);
    }

    public int hashCode() {
        return Long.hashCode(this.bits);
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BitsCronField)) {
            return false;
        }
        BitsCronField other = (BitsCronField) o;
        return type() == other.type() && this.bits == other.bits;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(type().toString());
        builder.append(" {");
        int i = nextSetBit(0);
        if (i != -1) {
            builder.append(i);
            int nextSetBit = nextSetBit(i + 1);
            while (true) {
                int i2 = nextSetBit;
                if (i2 == -1) {
                    break;
                }
                builder.append(", ");
                builder.append(i2);
                nextSetBit = nextSetBit(i2 + 1);
            }
        }
        builder.append('}');
        return builder.toString();
    }
}
