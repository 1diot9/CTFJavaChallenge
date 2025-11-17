package org.jooq.types;

import java.math.BigInteger;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/types/UShort.class */
public final class UShort extends UNumber implements Comparable<UShort> {
    private static final long serialVersionUID = -6821055240959745390L;
    public static final int MIN_VALUE = 0;
    private final int value;
    public static final UShort MIN = valueOf(0);
    public static final int MAX_VALUE = 65535;
    public static final UShort MAX = valueOf(MAX_VALUE);

    public static UShort valueOf(String value) throws NumberFormatException {
        return new UShort(value);
    }

    public static UShort valueOf(short value) {
        return new UShort(value);
    }

    public static UShort valueOf(int value) throws NumberFormatException {
        return new UShort(value);
    }

    private UShort(int value) throws NumberFormatException {
        this.value = value;
        rangeCheck();
    }

    private UShort(short value) {
        this.value = value & 65535;
    }

    private UShort(String value) throws NumberFormatException {
        this.value = Integer.parseInt(value);
        rangeCheck();
    }

    private void rangeCheck() throws NumberFormatException {
        if (this.value < 0 || this.value > 65535) {
            throw new NumberFormatException("Value is out of range : " + this.value);
        }
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

    @Override // org.jooq.types.UNumber
    public BigInteger toBigInteger() {
        return BigInteger.valueOf(this.value);
    }

    public int hashCode() {
        return Integer.valueOf(this.value).hashCode();
    }

    public boolean equals(Object obj) {
        return (obj instanceof UShort) && this.value == ((UShort) obj).value;
    }

    public String toString() {
        return Integer.valueOf(this.value).toString();
    }

    @Override // java.lang.Comparable
    public int compareTo(UShort o) {
        if (this.value < o.value) {
            return -1;
        }
        return this.value == o.value ? 0 : 1;
    }

    public UShort add(UShort val) throws NumberFormatException {
        return valueOf(this.value + val.value);
    }

    public UShort add(int val) throws NumberFormatException {
        return valueOf(this.value + val);
    }

    public UShort subtract(UShort val) {
        return valueOf(this.value - val.value);
    }

    public UShort subtract(int val) {
        return valueOf(this.value - val);
    }
}
