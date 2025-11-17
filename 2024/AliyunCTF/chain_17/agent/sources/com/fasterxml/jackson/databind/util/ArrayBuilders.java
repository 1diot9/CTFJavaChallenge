package com.fasterxml.jackson.databind.util;

import java.lang.reflect.Array;
import java.util.HashSet;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/ArrayBuilders.class */
public final class ArrayBuilders {
    private BooleanBuilder _booleanBuilder = null;
    private ByteBuilder _byteBuilder = null;
    private ShortBuilder _shortBuilder = null;
    private IntBuilder _intBuilder = null;
    private LongBuilder _longBuilder = null;
    private FloatBuilder _floatBuilder = null;
    private DoubleBuilder _doubleBuilder = null;

    public BooleanBuilder getBooleanBuilder() {
        if (this._booleanBuilder == null) {
            this._booleanBuilder = new BooleanBuilder();
        }
        return this._booleanBuilder;
    }

    public ByteBuilder getByteBuilder() {
        if (this._byteBuilder == null) {
            this._byteBuilder = new ByteBuilder();
        }
        return this._byteBuilder;
    }

    public ShortBuilder getShortBuilder() {
        if (this._shortBuilder == null) {
            this._shortBuilder = new ShortBuilder();
        }
        return this._shortBuilder;
    }

    public IntBuilder getIntBuilder() {
        if (this._intBuilder == null) {
            this._intBuilder = new IntBuilder();
        }
        return this._intBuilder;
    }

    public LongBuilder getLongBuilder() {
        if (this._longBuilder == null) {
            this._longBuilder = new LongBuilder();
        }
        return this._longBuilder;
    }

    public FloatBuilder getFloatBuilder() {
        if (this._floatBuilder == null) {
            this._floatBuilder = new FloatBuilder();
        }
        return this._floatBuilder;
    }

    public DoubleBuilder getDoubleBuilder() {
        if (this._doubleBuilder == null) {
            this._doubleBuilder = new DoubleBuilder();
        }
        return this._doubleBuilder;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/ArrayBuilders$BooleanBuilder.class */
    public static final class BooleanBuilder extends PrimitiveArrayBuilder<boolean[]> {
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.util.PrimitiveArrayBuilder
        public final boolean[] _constructArray(int len) {
            return new boolean[len];
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/ArrayBuilders$ByteBuilder.class */
    public static final class ByteBuilder extends PrimitiveArrayBuilder<byte[]> {
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.util.PrimitiveArrayBuilder
        public final byte[] _constructArray(int len) {
            return new byte[len];
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/ArrayBuilders$ShortBuilder.class */
    public static final class ShortBuilder extends PrimitiveArrayBuilder<short[]> {
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.util.PrimitiveArrayBuilder
        public final short[] _constructArray(int len) {
            return new short[len];
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/ArrayBuilders$IntBuilder.class */
    public static final class IntBuilder extends PrimitiveArrayBuilder<int[]> {
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.util.PrimitiveArrayBuilder
        public final int[] _constructArray(int len) {
            return new int[len];
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/ArrayBuilders$LongBuilder.class */
    public static final class LongBuilder extends PrimitiveArrayBuilder<long[]> {
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.util.PrimitiveArrayBuilder
        public final long[] _constructArray(int len) {
            return new long[len];
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/ArrayBuilders$FloatBuilder.class */
    public static final class FloatBuilder extends PrimitiveArrayBuilder<float[]> {
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.util.PrimitiveArrayBuilder
        public final float[] _constructArray(int len) {
            return new float[len];
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/ArrayBuilders$DoubleBuilder.class */
    public static final class DoubleBuilder extends PrimitiveArrayBuilder<double[]> {
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.util.PrimitiveArrayBuilder
        public final double[] _constructArray(int len) {
            return new double[len];
        }
    }

    public static Object getArrayComparator(final Object defaultValue) {
        final int length = Array.getLength(defaultValue);
        final Class<?> defaultValueType = defaultValue.getClass();
        return new Object() { // from class: com.fasterxml.jackson.databind.util.ArrayBuilders.1
            public boolean equals(Object other) {
                if (other == this) {
                    return true;
                }
                if (!ClassUtil.hasClass(other, defaultValueType) || Array.getLength(other) != length) {
                    return false;
                }
                for (int i = 0; i < length; i++) {
                    Object value1 = Array.get(defaultValue, i);
                    Object value2 = Array.get(other, i);
                    if (value1 != value2 && value1 != null && !value1.equals(value2)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    public static <T> HashSet<T> arrayToSet(T[] elements) {
        if (elements != null) {
            int len = elements.length;
            HashSet<T> result = new HashSet<>(len);
            for (T t : elements) {
                result.add(t);
            }
            return result;
        }
        return new HashSet<>();
    }

    public static <T> T[] insertInListNoDup(T[] tArr, T t) {
        int length = tArr.length;
        for (int i = 0; i < length; i++) {
            if (tArr[i] == t) {
                if (i == 0) {
                    return tArr;
                }
                T[] tArr2 = (T[]) ((Object[]) Array.newInstance(tArr.getClass().getComponentType(), length));
                System.arraycopy(tArr, 0, tArr2, 1, i);
                tArr2[0] = t;
                int i2 = i + 1;
                int i3 = length - i2;
                if (i3 > 0) {
                    System.arraycopy(tArr, i2, tArr2, i2, i3);
                }
                return tArr2;
            }
        }
        T[] tArr3 = (T[]) ((Object[]) Array.newInstance(tArr.getClass().getComponentType(), length + 1));
        if (length > 0) {
            System.arraycopy(tArr, 0, tArr3, 1, length);
        }
        tArr3[0] = t;
        return tArr3;
    }
}
