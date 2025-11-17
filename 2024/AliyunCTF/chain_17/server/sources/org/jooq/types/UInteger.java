package org.jooq.types;

import java.io.ObjectStreamException;
import java.math.BigInteger;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/types/UInteger.class */
public final class UInteger extends UNumber implements Comparable<UInteger> {
    private static final int DEFAULT_PRECACHE_SIZE = 256;
    private static final long serialVersionUID = -6821055240959745390L;
    public static final long MIN_VALUE = 0;
    private final long value;
    private static final Class<UInteger> CLASS = UInteger.class;
    private static final String CLASS_NAME = CLASS.getName();
    private static final String PRECACHE_PROPERTY = CLASS_NAME + ".precacheSize";
    private static final UInteger[] VALUES = mkValues();
    public static final UInteger MIN = valueOf(0L);
    public static final long MAX_VALUE = 4294967295L;
    public static final UInteger MAX = valueOf(MAX_VALUE);

    private static final int getPrecacheSize() {
        try {
            String prop = System.getProperty(PRECACHE_PROPERTY);
            if (prop == null || prop.length() <= 0) {
                return 256;
            }
            try {
                long propParsed = Long.parseLong(prop);
                if (propParsed < 0) {
                    return 0;
                }
                if (propParsed > 2147483647L) {
                    return Integer.MAX_VALUE;
                }
                return (int) propParsed;
            } catch (NumberFormatException e) {
                return 256;
            }
        } catch (SecurityException e2) {
            return 256;
        }
    }

    private static final UInteger[] mkValues() {
        int precacheSize = getPrecacheSize();
        if (precacheSize <= 0) {
            return null;
        }
        UInteger[] ret = new UInteger[precacheSize];
        for (int i = 0; i < precacheSize; i++) {
            ret[i] = new UInteger(i);
        }
        return ret;
    }

    private UInteger(long value, boolean unused) {
        this.value = value;
    }

    private static UInteger getCached(long value) {
        if (VALUES != null && value < VALUES.length) {
            return VALUES[(int) value];
        }
        return null;
    }

    private static UInteger valueOfUnchecked(long value) {
        UInteger cached = getCached(value);
        if (cached != null) {
            return cached;
        }
        return new UInteger(value, true);
    }

    public static UInteger valueOf(String value) throws NumberFormatException {
        return valueOfUnchecked(rangeCheck(Long.parseLong(value)));
    }

    public static UInteger valueOf(int value) {
        return valueOfUnchecked(value & MAX_VALUE);
    }

    public static UInteger valueOf(long value) throws NumberFormatException {
        return valueOfUnchecked(rangeCheck(value));
    }

    private UInteger(long value) throws NumberFormatException {
        this.value = rangeCheck(value);
    }

    private UInteger(int value) {
        this.value = value & MAX_VALUE;
    }

    private UInteger(String value) throws NumberFormatException {
        this.value = rangeCheck(Long.parseLong(value));
    }

    private static long rangeCheck(long value) throws NumberFormatException {
        if (value < 0 || value > MAX_VALUE) {
            throw new NumberFormatException("Value is out of range : " + value);
        }
        return value;
    }

    private Object readResolve() throws ObjectStreamException {
        rangeCheck(this.value);
        UInteger cached = getCached(this.value);
        if (cached != null) {
            return cached;
        }
        return this;
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
        return (float) this.value;
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
        return Long.valueOf(this.value).hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof UInteger) && this.value == ((UInteger) obj).value;
    }

    public String toString() {
        return Long.valueOf(this.value).toString();
    }

    @Override // java.lang.Comparable
    public int compareTo(UInteger o) {
        if (this.value < o.value) {
            return -1;
        }
        return this.value == o.value ? 0 : 1;
    }

    public UInteger add(UInteger val) {
        return valueOf(this.value + val.value);
    }

    public UInteger add(int val) {
        return valueOf(this.value + val);
    }

    public UInteger subtract(UInteger val) {
        return valueOf(this.value - val.value);
    }

    public UInteger subtract(int val) {
        return valueOf(this.value - val);
    }
}
