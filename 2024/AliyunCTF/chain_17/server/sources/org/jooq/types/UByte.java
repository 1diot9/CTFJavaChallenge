package org.jooq.types;

import java.io.ObjectStreamException;
import java.math.BigInteger;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/types/UByte.class */
public final class UByte extends UNumber implements Comparable<UByte> {
    private static final long serialVersionUID = -6821055240959745390L;
    public static final short MIN_VALUE = 0;
    public static final short MAX_VALUE = 255;
    private final short value;
    private static final UByte[] VALUES = mkValues();
    public static final UByte MIN = valueOf(0);
    public static final UByte MAX = valueOf(255);

    private static final UByte[] mkValues() {
        UByte[] ret = new UByte[256];
        for (int i = -128; i <= 127; i++) {
            ret[i & 255] = new UByte((byte) i);
        }
        return ret;
    }

    public static UByte valueOf(String value) throws NumberFormatException {
        return valueOfUnchecked(rangeCheck(Short.parseShort(value)));
    }

    public static UByte valueOf(byte value) {
        return valueOfUnchecked((short) (value & 255));
    }

    private static UByte valueOfUnchecked(short value) throws NumberFormatException {
        return VALUES[value & 255];
    }

    public static UByte valueOf(short value) throws NumberFormatException {
        return valueOfUnchecked(rangeCheck(value));
    }

    public static UByte valueOf(int value) throws NumberFormatException {
        return valueOfUnchecked(rangeCheck(value));
    }

    public static UByte valueOf(long value) throws NumberFormatException {
        return valueOfUnchecked(rangeCheck(value));
    }

    private UByte(long value) throws NumberFormatException {
        this.value = rangeCheck(value);
    }

    private UByte(int value) throws NumberFormatException {
        this.value = rangeCheck(value);
    }

    private UByte(short value) throws NumberFormatException {
        this.value = rangeCheck(value);
    }

    private UByte(byte value) {
        this.value = (short) (value & 255);
    }

    private UByte(String value) throws NumberFormatException {
        this.value = rangeCheck(Short.parseShort(value));
    }

    private static short rangeCheck(short value) throws NumberFormatException {
        if (value < 0 || value > 255) {
            throw new NumberFormatException("Value is out of range : " + value);
        }
        return value;
    }

    private static short rangeCheck(int value) throws NumberFormatException {
        if (value < 0 || value > 255) {
            throw new NumberFormatException("Value is out of range : " + value);
        }
        return (short) value;
    }

    private static short rangeCheck(long value) throws NumberFormatException {
        if (value < 0 || value > 255) {
            throw new NumberFormatException("Value is out of range : " + value);
        }
        return (short) value;
    }

    private Object readResolve() throws ObjectStreamException {
        return valueOf(this.value);
    }

    @Override // java.lang.Number
    public int intValue() {
        return this.value;
    }

    @Override // java.lang.Number
    public long longValue() {
        return this.value;
    }

    @Override // java.lang.Number
    public float floatValue() {
        return this.value;
    }

    @Override // java.lang.Number
    public double doubleValue() {
        return this.value;
    }

    public int hashCode() {
        return Short.valueOf(this.value).hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof UByte) && this.value == ((UByte) obj).value;
    }

    public String toString() {
        return Short.valueOf(this.value).toString();
    }

    @Override // java.lang.Comparable
    public int compareTo(UByte o) {
        if (this.value < o.value) {
            return -1;
        }
        return this.value == o.value ? 0 : 1;
    }

    @Override // org.jooq.types.UNumber
    public BigInteger toBigInteger() {
        return BigInteger.valueOf(this.value);
    }

    public UByte add(UByte val) throws NumberFormatException {
        return valueOf(this.value + val.value);
    }

    public UByte add(int val) throws NumberFormatException {
        return valueOf(this.value + val);
    }

    public UByte subtract(UByte val) {
        return valueOf(this.value - val.value);
    }

    public UByte subtract(int val) {
        return valueOf(this.value - val);
    }
}
