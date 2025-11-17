package org.jooq.types;

import java.math.BigInteger;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/types/ULong.class */
public final class ULong extends UNumber implements Comparable<ULong> {
    private static final long serialVersionUID = -6821055240959745390L;
    public static final BigInteger MIN_VALUE = BigInteger.ZERO;
    public static final BigInteger MAX_VALUE = new BigInteger("18446744073709551615");
    public static final BigInteger MAX_VALUE_LONG = new BigInteger("9223372036854775808");
    public static final ULong MIN = valueOf(MIN_VALUE.longValue());
    public static final ULong MAX = valueOf(MAX_VALUE);
    private final long value;

    public static ULong valueOf(String value) throws NumberFormatException {
        return new ULong(value);
    }

    public static ULong valueOf(long value) {
        return new ULong(value);
    }

    public static ULong valueOf(BigInteger value) throws NumberFormatException {
        return new ULong(value);
    }

    public static int compare(long x, long y) {
        long x2 = x - Long.MIN_VALUE;
        long y2 = y - Long.MIN_VALUE;
        if (x2 < y2) {
            return -1;
        }
        return x2 == y2 ? 0 : 1;
    }

    private ULong(BigInteger value) throws NumberFormatException {
        if (value.compareTo(MIN_VALUE) < 0 || value.compareTo(MAX_VALUE) > 0) {
            throw new NumberFormatException();
        }
        this.value = value.longValue();
    }

    private ULong(long value) {
        this.value = value;
    }

    private ULong(String value) throws NumberFormatException {
        if (value == null) {
            throw new NumberFormatException("null");
        }
        int length = value.length();
        if (length == 0) {
            throw new NumberFormatException("Empty input string");
        }
        if (value.charAt(0) == '-') {
            throw new NumberFormatException(String.format("Illegal leading minus sign on unsigned string %s", value));
        }
        if (length <= 18) {
            this.value = Long.parseLong(value, 10);
            return;
        }
        long first = Long.parseLong(value.substring(0, length - 1), 10);
        int second = Character.digit(value.charAt(length - 1), 10);
        if (second < 0) {
            throw new NumberFormatException("Bad digit at end of " + value);
        }
        long result = (first * 10) + second;
        if (compare(result, first) < 0) {
            throw new NumberFormatException(String.format("String value %s exceeds range of unsigned long", value));
        }
        this.value = result;
    }

    @Override // java.lang.Number
    public int intValue() {
        return (int) this.value;
    }

    @Override // java.lang.Number
    public long longValue() {
        return this.value;
    }

    @Override // java.lang.Number
    public float floatValue() {
        if (this.value < 0) {
            return ((float) (this.value & Long.MAX_VALUE)) + 9.223372E18f;
        }
        return (float) this.value;
    }

    @Override // java.lang.Number
    public double doubleValue() {
        if (this.value < 0) {
            return (this.value & Long.MAX_VALUE) + 9.223372036854776E18d;
        }
        return this.value;
    }

    public int hashCode() {
        return Long.valueOf(this.value).hashCode();
    }

    public boolean equals(Object obj) {
        return (obj instanceof ULong) && this.value == ((ULong) obj).value;
    }

    public String toString() {
        if (this.value >= 0) {
            return Long.toString(this.value);
        }
        return BigInteger.valueOf(this.value & Long.MAX_VALUE).add(MAX_VALUE_LONG).toString();
    }

    @Override // java.lang.Comparable
    public int compareTo(ULong o) {
        return compare(this.value, o.value);
    }

    public ULong add(ULong val) throws NumberFormatException {
        if (this.value < 0 && val.value < 0) {
            throw new NumberFormatException();
        }
        long result = this.value + val.value;
        if ((this.value < 0 || val.value < 0) && result >= 0) {
            throw new NumberFormatException();
        }
        return valueOf(result);
    }

    public ULong add(int val) throws NumberFormatException {
        return add(val);
    }

    public ULong add(long val) throws NumberFormatException {
        if (val < 0) {
            return subtract(Math.abs(val));
        }
        long result = this.value + val;
        if (this.value < 0 && result >= 0) {
            throw new NumberFormatException();
        }
        return valueOf(result);
    }

    public ULong subtract(ULong val) {
        if (compareTo(val) < 0) {
            throw new NumberFormatException();
        }
        long result = this.value - val.value;
        if (this.value < 0 && result >= 0) {
            throw new NumberFormatException();
        }
        return valueOf(result);
    }

    public ULong subtract(int val) {
        return subtract(val);
    }

    public ULong subtract(long val) {
        if (val < 0) {
            return add(-val);
        }
        if (compare(this.value, val) < 0) {
            throw new NumberFormatException();
        }
        long result = this.value - val;
        if (this.value < 0 && result >= 0) {
            throw new NumberFormatException();
        }
        return valueOf(result);
    }
}
