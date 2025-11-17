package org.h2.mvstore.type;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.WriteBuffer;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/ObjectDataType.class */
public class ObjectDataType extends BasicDataType<Object> {
    static final int TYPE_NULL = 0;
    static final int TYPE_BOOLEAN = 1;
    static final int TYPE_BYTE = 2;
    static final int TYPE_SHORT = 3;
    static final int TYPE_INT = 4;
    static final int TYPE_LONG = 5;
    static final int TYPE_BIG_INTEGER = 6;
    static final int TYPE_FLOAT = 7;
    static final int TYPE_DOUBLE = 8;
    static final int TYPE_BIG_DECIMAL = 9;
    static final int TYPE_CHAR = 10;
    static final int TYPE_STRING = 11;
    static final int TYPE_UUID = 12;
    static final int TYPE_DATE = 13;
    static final int TYPE_ARRAY = 14;
    static final int TYPE_SERIALIZED_OBJECT = 19;
    static final int TAG_BOOLEAN_TRUE = 32;
    static final int TAG_INTEGER_NEGATIVE = 33;
    static final int TAG_INTEGER_FIXED = 34;
    static final int TAG_LONG_NEGATIVE = 35;
    static final int TAG_LONG_FIXED = 36;
    static final int TAG_BIG_INTEGER_0 = 37;
    static final int TAG_BIG_INTEGER_1 = 38;
    static final int TAG_BIG_INTEGER_SMALL = 39;
    static final int TAG_FLOAT_0 = 40;
    static final int TAG_FLOAT_1 = 41;
    static final int TAG_FLOAT_FIXED = 42;
    static final int TAG_DOUBLE_0 = 43;
    static final int TAG_DOUBLE_1 = 44;
    static final int TAG_DOUBLE_FIXED = 45;
    static final int TAG_BIG_DECIMAL_0 = 46;
    static final int TAG_BIG_DECIMAL_1 = 47;
    static final int TAG_BIG_DECIMAL_SMALL = 48;
    static final int TAG_BIG_DECIMAL_SMALL_SCALED = 49;
    static final int TAG_INTEGER_0_15 = 64;
    static final int TAG_LONG_0_7 = 80;
    static final int TAG_STRING_0_15 = 88;
    static final int TAG_BYTE_ARRAY_0_15 = 104;
    static final int FLOAT_ZERO_BITS = Float.floatToIntBits(0.0f);
    static final int FLOAT_ONE_BITS = Float.floatToIntBits(1.0f);
    static final long DOUBLE_ZERO_BITS = Double.doubleToLongBits(0.0d);
    static final long DOUBLE_ONE_BITS = Double.doubleToLongBits(1.0d);
    static final Class<?>[] COMMON_CLASSES = {Boolean.TYPE, Byte.TYPE, Short.TYPE, Character.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Object.class, Boolean.class, Byte.class, Short.class, Character.class, Integer.class, Long.class, BigInteger.class, Float.class, Double.class, BigDecimal.class, String.class, UUID.class, Date.class};
    private AutoDetectDataType<Object> last = selectDataType(0);

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/ObjectDataType$Holder.class */
    private static class Holder {
        private static final HashMap<Class<?>, Integer> COMMON_CLASSES_MAP = new HashMap<>(32);

        private Holder() {
        }

        static {
            int length = ObjectDataType.COMMON_CLASSES.length;
            for (int i = 0; i < length; i++) {
                COMMON_CLASSES_MAP.put(ObjectDataType.COMMON_CLASSES[i], Integer.valueOf(i));
            }
        }

        static Integer getCommonClassId(Class<?> cls) {
            return COMMON_CLASSES_MAP.get(cls);
        }
    }

    @Override // org.h2.mvstore.type.DataType
    public Object[] createStorage(int i) {
        return new Object[i];
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
    public int compare(Object obj, Object obj2) {
        int typeId = getTypeId(obj);
        int typeId2 = typeId - getTypeId(obj2);
        if (typeId2 == 0) {
            return newType(typeId).compare(obj, obj2);
        }
        return Integer.signum(typeId2);
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public int getMemory(Object obj) {
        return switchType(obj).getMemory(obj);
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public void write(WriteBuffer writeBuffer, Object obj) {
        switchType(obj).write(writeBuffer, obj);
    }

    private AutoDetectDataType<Object> newType(int i) {
        if (i == this.last.typeId) {
            return this.last;
        }
        return selectDataType(i);
    }

    private AutoDetectDataType selectDataType(int i) {
        switch (i) {
            case 0:
                return NullType.INSTANCE;
            case 1:
                return BooleanType.INSTANCE;
            case 2:
                return ByteType.INSTANCE;
            case 3:
                return ShortType.INSTANCE;
            case 4:
                return IntegerType.INSTANCE;
            case 5:
                return LongType.INSTANCE;
            case 6:
                return BigIntegerType.INSTANCE;
            case 7:
                return FloatType.INSTANCE;
            case 8:
                return DoubleType.INSTANCE;
            case 9:
                return BigDecimalType.INSTANCE;
            case 10:
                return CharacterType.INSTANCE;
            case 11:
                return StringType.INSTANCE;
            case 12:
                return UUIDType.INSTANCE;
            case 13:
                return DateType.INSTANCE;
            case 14:
                return new ObjectArrayType();
            case 15:
            case 16:
            case 17:
            case 18:
            default:
                throw DataUtils.newMVStoreException(3, "Unsupported type {0}", Integer.valueOf(i));
            case 19:
                return new SerializedObjectType(this);
        }
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public Object read(ByteBuffer byteBuffer) {
        byte b;
        byte b2 = byteBuffer.get();
        if (b2 <= 19) {
            b = b2;
        } else {
            switch (b2) {
                case 32:
                    b = 1;
                    break;
                case 33:
                case 34:
                    b = 4;
                    break;
                case 35:
                case 36:
                    b = 5;
                    break;
                case 37:
                case 38:
                case 39:
                    b = 6;
                    break;
                case 40:
                case 41:
                case 42:
                    b = 7;
                    break;
                case 43:
                case 44:
                case 45:
                    b = 8;
                    break;
                case 46:
                case 47:
                case 48:
                case 49:
                    b = 9;
                    break;
                default:
                    if (b2 >= 64 && b2 <= 79) {
                        b = 4;
                        break;
                    } else if (b2 >= 88 && b2 <= 103) {
                        b = 11;
                        break;
                    } else if (b2 >= 80 && b2 <= 87) {
                        b = 5;
                        break;
                    } else if (b2 >= 104 && b2 <= 119) {
                        b = 14;
                        break;
                    } else {
                        throw DataUtils.newMVStoreException(6, "Unknown tag {0}", Integer.valueOf(b2));
                    }
                    break;
            }
        }
        AutoDetectDataType<Object> autoDetectDataType = this.last;
        if (b != autoDetectDataType.typeId) {
            AutoDetectDataType<Object> newType = newType(b);
            autoDetectDataType = newType;
            this.last = newType;
        }
        return autoDetectDataType.read(byteBuffer, b2);
    }

    private static int getTypeId(Object obj) {
        if (obj instanceof Integer) {
            return 4;
        }
        if (obj instanceof String) {
            return 11;
        }
        if (obj instanceof Long) {
            return 5;
        }
        if (obj instanceof Double) {
            return 8;
        }
        if (obj instanceof Float) {
            return 7;
        }
        if (obj instanceof Boolean) {
            return 1;
        }
        if (obj instanceof UUID) {
            return 12;
        }
        if (obj instanceof Byte) {
            return 2;
        }
        if (obj instanceof Short) {
            return 3;
        }
        if (obj instanceof Character) {
            return 10;
        }
        if (obj == null) {
            return 0;
        }
        if (isDate(obj)) {
            return 13;
        }
        if (isBigInteger(obj)) {
            return 6;
        }
        if (isBigDecimal(obj)) {
            return 9;
        }
        if (obj.getClass().isArray()) {
            return 14;
        }
        return 19;
    }

    AutoDetectDataType<Object> switchType(Object obj) {
        int typeId = getTypeId(obj);
        AutoDetectDataType<Object> autoDetectDataType = this.last;
        if (typeId != autoDetectDataType.typeId) {
            AutoDetectDataType<Object> newType = newType(typeId);
            autoDetectDataType = newType;
            this.last = newType;
        }
        return autoDetectDataType;
    }

    static boolean isBigInteger(Object obj) {
        return obj != null && obj.getClass() == BigInteger.class;
    }

    static boolean isBigDecimal(Object obj) {
        return obj != null && obj.getClass() == BigDecimal.class;
    }

    static boolean isDate(Object obj) {
        return obj != null && obj.getClass() == Date.class;
    }

    static boolean isArray(Object obj) {
        return obj != null && obj.getClass().isArray();
    }

    public static byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new ObjectOutputStream(byteArrayOutputStream).writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        } catch (Throwable th) {
            throw DataUtils.newIllegalArgumentException("Could not serialize {0}", obj, th);
        }
    }

    public static Object deserialize(byte[] bArr) {
        try {
            return new ObjectInputStream(new ByteArrayInputStream(bArr)).readObject();
        } catch (Throwable th) {
            throw DataUtils.newIllegalArgumentException("Could not deserialize {0}", Arrays.toString(bArr), th);
        }
    }

    public static int compareNotNull(byte[] bArr, byte[] bArr2) {
        if (bArr == bArr2) {
            return 0;
        }
        int min = Math.min(bArr.length, bArr2.length);
        for (int i = 0; i < min; i++) {
            int i2 = bArr[i] & 255;
            int i3 = bArr2[i] & 255;
            if (i2 != i3) {
                return i2 > i3 ? 1 : -1;
            }
        }
        return Integer.signum(bArr.length - bArr2.length);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/ObjectDataType$AutoDetectDataType.class */
    public static abstract class AutoDetectDataType<T> extends BasicDataType<T> {
        private final ObjectDataType base;
        final int typeId;

        abstract Object read(ByteBuffer byteBuffer, int i);

        AutoDetectDataType(int i) {
            this.base = null;
            this.typeId = i;
        }

        AutoDetectDataType(ObjectDataType objectDataType, int i) {
            this.base = objectDataType;
            this.typeId = i;
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public int getMemory(T t) {
            return getType(t).getMemory(t);
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public void write(WriteBuffer writeBuffer, T t) {
            getType(t).write(writeBuffer, t);
        }

        DataType<Object> getType(Object obj) {
            return this.base.switchType(obj);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/ObjectDataType$NullType.class */
    public static class NullType extends AutoDetectDataType<Object> {
        static final NullType INSTANCE = new NullType();

        private NullType() {
            super(0);
        }

        @Override // org.h2.mvstore.type.DataType
        public Object[] createStorage(int i) {
            return null;
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
        public int compare(Object obj, Object obj2) {
            return 0;
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public int getMemory(Object obj) {
            return 0;
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public void write(WriteBuffer writeBuffer, Object obj) {
            writeBuffer.put((byte) 0);
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public Object read(ByteBuffer byteBuffer) {
            return null;
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType
        public Object read(ByteBuffer byteBuffer, int i) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/ObjectDataType$BooleanType.class */
    public static class BooleanType extends AutoDetectDataType<Boolean> {
        static final BooleanType INSTANCE = new BooleanType();

        private BooleanType() {
            super(1);
        }

        @Override // org.h2.mvstore.type.DataType
        public Boolean[] createStorage(int i) {
            return new Boolean[i];
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
        public int compare(Boolean bool, Boolean bool2) {
            return bool.compareTo(bool2);
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public int getMemory(Boolean bool) {
            return 0;
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public void write(WriteBuffer writeBuffer, Boolean bool) {
            writeBuffer.put((byte) (bool.booleanValue() ? 32 : 1));
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public Boolean read(ByteBuffer byteBuffer) {
            return byteBuffer.get() == 32 ? Boolean.TRUE : Boolean.FALSE;
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType
        public Boolean read(ByteBuffer byteBuffer, int i) {
            return i == 1 ? Boolean.FALSE : Boolean.TRUE;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/ObjectDataType$ByteType.class */
    public static class ByteType extends AutoDetectDataType<Byte> {
        static final ByteType INSTANCE = new ByteType();

        private ByteType() {
            super(2);
        }

        @Override // org.h2.mvstore.type.DataType
        public Byte[] createStorage(int i) {
            return new Byte[i];
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
        public int compare(Byte b, Byte b2) {
            return b.compareTo(b2);
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public int getMemory(Byte b) {
            return 1;
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public void write(WriteBuffer writeBuffer, Byte b) {
            writeBuffer.put((byte) 2);
            writeBuffer.put(b.byteValue());
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public Byte read(ByteBuffer byteBuffer) {
            return Byte.valueOf(byteBuffer.get());
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType
        public Object read(ByteBuffer byteBuffer, int i) {
            return Byte.valueOf(byteBuffer.get());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/ObjectDataType$CharacterType.class */
    public static class CharacterType extends AutoDetectDataType<Character> {
        static final CharacterType INSTANCE = new CharacterType();

        private CharacterType() {
            super(10);
        }

        @Override // org.h2.mvstore.type.DataType
        public Character[] createStorage(int i) {
            return new Character[i];
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
        public int compare(Character ch2, Character ch3) {
            return ch2.compareTo(ch3);
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public int getMemory(Character ch2) {
            return 24;
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public void write(WriteBuffer writeBuffer, Character ch2) {
            writeBuffer.put((byte) 10);
            writeBuffer.putChar(ch2.charValue());
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public Character read(ByteBuffer byteBuffer) {
            return Character.valueOf(byteBuffer.getChar());
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType
        public Character read(ByteBuffer byteBuffer, int i) {
            return Character.valueOf(byteBuffer.getChar());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/ObjectDataType$ShortType.class */
    public static class ShortType extends AutoDetectDataType<Short> {
        static final ShortType INSTANCE = new ShortType();

        private ShortType() {
            super(3);
        }

        @Override // org.h2.mvstore.type.DataType
        public Short[] createStorage(int i) {
            return new Short[i];
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
        public int compare(Short sh, Short sh2) {
            return sh.compareTo(sh2);
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public int getMemory(Short sh) {
            return 24;
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public void write(WriteBuffer writeBuffer, Short sh) {
            writeBuffer.put((byte) 3);
            writeBuffer.putShort(sh.shortValue());
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public Short read(ByteBuffer byteBuffer) {
            return read(byteBuffer, (int) byteBuffer.get());
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType
        public Short read(ByteBuffer byteBuffer, int i) {
            return Short.valueOf(byteBuffer.getShort());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/ObjectDataType$IntegerType.class */
    public static class IntegerType extends AutoDetectDataType<Integer> {
        static final IntegerType INSTANCE = new IntegerType();

        private IntegerType() {
            super(4);
        }

        @Override // org.h2.mvstore.type.DataType
        public Integer[] createStorage(int i) {
            return new Integer[i];
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
        public int compare(Integer num, Integer num2) {
            return num.compareTo(num2);
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public int getMemory(Integer num) {
            return 24;
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public void write(WriteBuffer writeBuffer, Integer num) {
            int intValue = num.intValue();
            if (intValue < 0) {
                if ((-intValue) < 0 || (-intValue) > 2097151) {
                    writeBuffer.put((byte) 34).putInt(intValue);
                    return;
                } else {
                    writeBuffer.put((byte) 33).putVarInt(-intValue);
                    return;
                }
            }
            if (intValue <= 15) {
                writeBuffer.put((byte) (64 + intValue));
            } else if (intValue <= 2097151) {
                writeBuffer.put((byte) 4).putVarInt(intValue);
            } else {
                writeBuffer.put((byte) 34).putInt(intValue);
            }
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public Integer read(ByteBuffer byteBuffer) {
            return read(byteBuffer, (int) byteBuffer.get());
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType
        public Integer read(ByteBuffer byteBuffer, int i) {
            switch (i) {
                case 4:
                    return Integer.valueOf(DataUtils.readVarInt(byteBuffer));
                case 33:
                    return Integer.valueOf(-DataUtils.readVarInt(byteBuffer));
                case 34:
                    return Integer.valueOf(byteBuffer.getInt());
                default:
                    return Integer.valueOf(i - 64);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/ObjectDataType$LongType.class */
    public static class LongType extends AutoDetectDataType<Long> {
        static final LongType INSTANCE = new LongType();

        private LongType() {
            super(5);
        }

        @Override // org.h2.mvstore.type.DataType
        public Long[] createStorage(int i) {
            return new Long[i];
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
        public int compare(Long l, Long l2) {
            return l.compareTo(l2);
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public int getMemory(Long l) {
            return 30;
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public void write(WriteBuffer writeBuffer, Long l) {
            long longValue = l.longValue();
            if (longValue < 0) {
                if ((-longValue) < 0 || (-longValue) > DataUtils.COMPRESSED_VAR_LONG_MAX) {
                    writeBuffer.put((byte) 36);
                    writeBuffer.putLong(longValue);
                    return;
                } else {
                    writeBuffer.put((byte) 35);
                    writeBuffer.putVarLong(-longValue);
                    return;
                }
            }
            if (longValue <= 7) {
                writeBuffer.put((byte) (80 + longValue));
            } else if (longValue <= DataUtils.COMPRESSED_VAR_LONG_MAX) {
                writeBuffer.put((byte) 5);
                writeBuffer.putVarLong(longValue);
            } else {
                writeBuffer.put((byte) 36);
                writeBuffer.putLong(longValue);
            }
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public Long read(ByteBuffer byteBuffer) {
            return read(byteBuffer, (int) byteBuffer.get());
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType
        public Long read(ByteBuffer byteBuffer, int i) {
            switch (i) {
                case 5:
                    return Long.valueOf(DataUtils.readVarLong(byteBuffer));
                case 35:
                    return Long.valueOf(-DataUtils.readVarLong(byteBuffer));
                case 36:
                    return Long.valueOf(byteBuffer.getLong());
                default:
                    return Long.valueOf(i - 80);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/ObjectDataType$FloatType.class */
    public static class FloatType extends AutoDetectDataType<Float> {
        static final FloatType INSTANCE = new FloatType();

        private FloatType() {
            super(7);
        }

        @Override // org.h2.mvstore.type.DataType
        public Float[] createStorage(int i) {
            return new Float[i];
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
        public int compare(Float f, Float f2) {
            return f.compareTo(f2);
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public int getMemory(Float f) {
            return 24;
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public void write(WriteBuffer writeBuffer, Float f) {
            float floatValue = f.floatValue();
            int floatToIntBits = Float.floatToIntBits(floatValue);
            if (floatToIntBits == ObjectDataType.FLOAT_ZERO_BITS) {
                writeBuffer.put((byte) 40);
                return;
            }
            if (floatToIntBits == ObjectDataType.FLOAT_ONE_BITS) {
                writeBuffer.put((byte) 41);
                return;
            }
            int reverse = Integer.reverse(floatToIntBits);
            if (reverse >= 0 && reverse <= 2097151) {
                writeBuffer.put((byte) 7).putVarInt(reverse);
            } else {
                writeBuffer.put((byte) 42).putFloat(floatValue);
            }
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public Float read(ByteBuffer byteBuffer) {
            return read(byteBuffer, (int) byteBuffer.get());
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType
        public Float read(ByteBuffer byteBuffer, int i) {
            switch (i) {
                case 40:
                    return Float.valueOf(0.0f);
                case 41:
                    return Float.valueOf(1.0f);
                case 42:
                    return Float.valueOf(byteBuffer.getFloat());
                default:
                    return Float.valueOf(Float.intBitsToFloat(Integer.reverse(DataUtils.readVarInt(byteBuffer))));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/ObjectDataType$DoubleType.class */
    public static class DoubleType extends AutoDetectDataType<Double> {
        static final DoubleType INSTANCE = new DoubleType();

        private DoubleType() {
            super(8);
        }

        @Override // org.h2.mvstore.type.DataType
        public Double[] createStorage(int i) {
            return new Double[i];
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
        public int compare(Double d, Double d2) {
            return d.compareTo(d2);
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public int getMemory(Double d) {
            return 30;
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public void write(WriteBuffer writeBuffer, Double d) {
            double doubleValue = d.doubleValue();
            long doubleToLongBits = Double.doubleToLongBits(doubleValue);
            if (doubleToLongBits == ObjectDataType.DOUBLE_ZERO_BITS) {
                writeBuffer.put((byte) 43);
                return;
            }
            if (doubleToLongBits == ObjectDataType.DOUBLE_ONE_BITS) {
                writeBuffer.put((byte) 44);
                return;
            }
            long reverse = Long.reverse(doubleToLongBits);
            if (reverse >= 0 && reverse <= DataUtils.COMPRESSED_VAR_LONG_MAX) {
                writeBuffer.put((byte) 8);
                writeBuffer.putVarLong(reverse);
            } else {
                writeBuffer.put((byte) 45);
                writeBuffer.putDouble(doubleValue);
            }
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public Double read(ByteBuffer byteBuffer) {
            return read(byteBuffer, (int) byteBuffer.get());
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType
        public Double read(ByteBuffer byteBuffer, int i) {
            switch (i) {
                case 43:
                    return Double.valueOf(0.0d);
                case 44:
                    return Double.valueOf(1.0d);
                case 45:
                    return Double.valueOf(byteBuffer.getDouble());
                default:
                    return Double.valueOf(Double.longBitsToDouble(Long.reverse(DataUtils.readVarLong(byteBuffer))));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/ObjectDataType$BigIntegerType.class */
    public static class BigIntegerType extends AutoDetectDataType<BigInteger> {
        static final BigIntegerType INSTANCE = new BigIntegerType();

        private BigIntegerType() {
            super(6);
        }

        @Override // org.h2.mvstore.type.DataType
        public BigInteger[] createStorage(int i) {
            return new BigInteger[i];
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
        public int compare(BigInteger bigInteger, BigInteger bigInteger2) {
            return bigInteger.compareTo(bigInteger2);
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public int getMemory(BigInteger bigInteger) {
            return 100;
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public void write(WriteBuffer writeBuffer, BigInteger bigInteger) {
            if (BigInteger.ZERO.equals(bigInteger)) {
                writeBuffer.put((byte) 37);
                return;
            }
            if (BigInteger.ONE.equals(bigInteger)) {
                writeBuffer.put((byte) 38);
            } else if (bigInteger.bitLength() <= 63) {
                writeBuffer.put((byte) 39).putVarLong(bigInteger.longValue());
            } else {
                byte[] byteArray = bigInteger.toByteArray();
                writeBuffer.put((byte) 6).putVarInt(byteArray.length).put(byteArray);
            }
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public BigInteger read(ByteBuffer byteBuffer) {
            return read(byteBuffer, (int) byteBuffer.get());
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType
        public BigInteger read(ByteBuffer byteBuffer, int i) {
            switch (i) {
                case 37:
                    return BigInteger.ZERO;
                case 38:
                    return BigInteger.ONE;
                case 39:
                    return BigInteger.valueOf(DataUtils.readVarLong(byteBuffer));
                default:
                    byte[] newBytes = Utils.newBytes(DataUtils.readVarInt(byteBuffer));
                    byteBuffer.get(newBytes);
                    return new BigInteger(newBytes);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/ObjectDataType$BigDecimalType.class */
    public static class BigDecimalType extends AutoDetectDataType<BigDecimal> {
        static final BigDecimalType INSTANCE = new BigDecimalType();

        private BigDecimalType() {
            super(9);
        }

        @Override // org.h2.mvstore.type.DataType
        public BigDecimal[] createStorage(int i) {
            return new BigDecimal[i];
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
        public int compare(BigDecimal bigDecimal, BigDecimal bigDecimal2) {
            return bigDecimal.compareTo(bigDecimal2);
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public int getMemory(BigDecimal bigDecimal) {
            return 150;
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public void write(WriteBuffer writeBuffer, BigDecimal bigDecimal) {
            if (BigDecimal.ZERO.equals(bigDecimal)) {
                writeBuffer.put((byte) 46);
                return;
            }
            if (BigDecimal.ONE.equals(bigDecimal)) {
                writeBuffer.put((byte) 47);
                return;
            }
            int scale = bigDecimal.scale();
            BigInteger unscaledValue = bigDecimal.unscaledValue();
            if (unscaledValue.bitLength() < 64) {
                if (scale == 0) {
                    writeBuffer.put((byte) 48);
                } else {
                    writeBuffer.put((byte) 49).putVarInt(scale);
                }
                writeBuffer.putVarLong(unscaledValue.longValue());
                return;
            }
            byte[] byteArray = unscaledValue.toByteArray();
            writeBuffer.put((byte) 9).putVarInt(scale).putVarInt(byteArray.length).put(byteArray);
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public BigDecimal read(ByteBuffer byteBuffer) {
            return read(byteBuffer, (int) byteBuffer.get());
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType
        public BigDecimal read(ByteBuffer byteBuffer, int i) {
            switch (i) {
                case 46:
                    return BigDecimal.ZERO;
                case 47:
                    return BigDecimal.ONE;
                case 48:
                    return BigDecimal.valueOf(DataUtils.readVarLong(byteBuffer));
                case 49:
                    return BigDecimal.valueOf(DataUtils.readVarLong(byteBuffer), DataUtils.readVarInt(byteBuffer));
                default:
                    int readVarInt = DataUtils.readVarInt(byteBuffer);
                    byte[] newBytes = Utils.newBytes(DataUtils.readVarInt(byteBuffer));
                    byteBuffer.get(newBytes);
                    return new BigDecimal(new BigInteger(newBytes), readVarInt);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/ObjectDataType$StringType.class */
    public static class StringType extends AutoDetectDataType<String> {
        static final StringType INSTANCE = new StringType();

        private StringType() {
            super(11);
        }

        @Override // org.h2.mvstore.type.DataType
        public String[] createStorage(int i) {
            return new String[i];
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public int getMemory(String str) {
            return 24 + (2 * str.length());
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
        public int compare(String str, String str2) {
            return str.compareTo(str2);
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public void write(WriteBuffer writeBuffer, String str) {
            int length = str.length();
            if (length <= 15) {
                writeBuffer.put((byte) (88 + length));
            } else {
                writeBuffer.put((byte) 11).putVarInt(length);
            }
            writeBuffer.putStringData(str, length);
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public String read(ByteBuffer byteBuffer) {
            return read(byteBuffer, (int) byteBuffer.get());
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType
        public String read(ByteBuffer byteBuffer, int i) {
            int i2;
            if (i == 11) {
                i2 = DataUtils.readVarInt(byteBuffer);
            } else {
                i2 = i - 88;
            }
            return DataUtils.readString(byteBuffer, i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/ObjectDataType$UUIDType.class */
    public static class UUIDType extends AutoDetectDataType<UUID> {
        static final UUIDType INSTANCE = new UUIDType();

        private UUIDType() {
            super(12);
        }

        @Override // org.h2.mvstore.type.DataType
        public UUID[] createStorage(int i) {
            return new UUID[i];
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public int getMemory(UUID uuid) {
            return 40;
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
        public int compare(UUID uuid, UUID uuid2) {
            return uuid.compareTo(uuid2);
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public void write(WriteBuffer writeBuffer, UUID uuid) {
            writeBuffer.put((byte) 12);
            writeBuffer.putLong(uuid.getMostSignificantBits());
            writeBuffer.putLong(uuid.getLeastSignificantBits());
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public UUID read(ByteBuffer byteBuffer) {
            return read(byteBuffer, (int) byteBuffer.get());
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType
        public UUID read(ByteBuffer byteBuffer, int i) {
            return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/ObjectDataType$DateType.class */
    public static class DateType extends AutoDetectDataType<Date> {
        static final DateType INSTANCE = new DateType();

        private DateType() {
            super(13);
        }

        @Override // org.h2.mvstore.type.DataType
        public Date[] createStorage(int i) {
            return new Date[i];
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public int getMemory(Date date) {
            return 40;
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
        public int compare(Date date, Date date2) {
            return date.compareTo(date2);
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public void write(WriteBuffer writeBuffer, Date date) {
            writeBuffer.put((byte) 13);
            writeBuffer.putLong(date.getTime());
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public Date read(ByteBuffer byteBuffer) {
            return read(byteBuffer, (int) byteBuffer.get());
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType
        public Date read(ByteBuffer byteBuffer, int i) {
            return new Date(byteBuffer.getLong());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/ObjectDataType$ObjectArrayType.class */
    public static class ObjectArrayType extends AutoDetectDataType<Object> {
        private final ObjectDataType elementType;

        ObjectArrayType() {
            super(14);
            this.elementType = new ObjectDataType();
        }

        @Override // org.h2.mvstore.type.DataType
        public Object[] createStorage(int i) {
            return new Object[i];
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public int getMemory(Object obj) {
            if (!ObjectDataType.isArray(obj)) {
                return super.getMemory(obj);
            }
            int i = 64;
            Class<?> componentType = obj.getClass().getComponentType();
            if (componentType.isPrimitive()) {
                int length = Array.getLength(obj);
                if (componentType == Boolean.TYPE || componentType == Byte.TYPE) {
                    i = 64 + length;
                } else if (componentType == Character.TYPE || componentType == Short.TYPE) {
                    i = 64 + (length * 2);
                } else if (componentType == Integer.TYPE || componentType == Float.TYPE) {
                    i = 64 + (length * 4);
                } else if (componentType == Double.TYPE || componentType == Long.TYPE) {
                    i = 64 + (length * 8);
                }
            } else {
                for (Object obj2 : (Object[]) obj) {
                    if (obj2 != null) {
                        i += this.elementType.getMemory(obj2);
                    }
                }
            }
            return i * 2;
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
        public int compare(Object obj, Object obj2) {
            int compare;
            if (!ObjectDataType.isArray(obj) || !ObjectDataType.isArray(obj2)) {
                return super.compare(obj, obj2);
            }
            if (obj == obj2) {
                return 0;
            }
            Class<?> componentType = obj.getClass().getComponentType();
            Class<?> componentType2 = obj2.getClass().getComponentType();
            if (componentType != componentType2) {
                Integer commonClassId = Holder.getCommonClassId(componentType);
                Integer commonClassId2 = Holder.getCommonClassId(componentType2);
                if (commonClassId != null) {
                    if (commonClassId2 != null) {
                        return commonClassId.compareTo(commonClassId2);
                    }
                    return -1;
                }
                if (commonClassId2 != null) {
                    return 1;
                }
                return componentType.getName().compareTo(componentType2.getName());
            }
            int length = Array.getLength(obj);
            int length2 = Array.getLength(obj2);
            int min = Math.min(length, length2);
            if (componentType.isPrimitive()) {
                if (componentType == Byte.TYPE) {
                    return ObjectDataType.compareNotNull((byte[]) obj, (byte[]) obj2);
                }
                for (int i = 0; i < min; i++) {
                    if (componentType == Boolean.TYPE) {
                        compare = Integer.signum((((boolean[]) obj)[i] ? 1 : 0) - (((boolean[]) obj2)[i] ? 1 : 0));
                    } else if (componentType == Character.TYPE) {
                        compare = Integer.signum(((char[]) obj)[i] - ((char[]) obj2)[i]);
                    } else if (componentType == Short.TYPE) {
                        compare = Integer.signum(((short[]) obj)[i] - ((short[]) obj2)[i]);
                    } else if (componentType == Integer.TYPE) {
                        compare = Integer.compare(((int[]) obj)[i], ((int[]) obj2)[i]);
                    } else if (componentType == Float.TYPE) {
                        compare = Float.compare(((float[]) obj)[i], ((float[]) obj2)[i]);
                    } else if (componentType == Double.TYPE) {
                        compare = Double.compare(((double[]) obj)[i], ((double[]) obj2)[i]);
                    } else {
                        compare = Long.compare(((long[]) obj)[i], ((long[]) obj2)[i]);
                    }
                    if (compare != 0) {
                        return compare;
                    }
                }
            } else {
                Object[] objArr = (Object[]) obj;
                Object[] objArr2 = (Object[]) obj2;
                for (int i2 = 0; i2 < min; i2++) {
                    int compare2 = this.elementType.compare(objArr[i2], objArr2[i2]);
                    if (compare2 != 0) {
                        return compare2;
                    }
                }
            }
            return Integer.compare(length, length2);
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public void write(WriteBuffer writeBuffer, Object obj) {
            if (!ObjectDataType.isArray(obj)) {
                super.write(writeBuffer, obj);
                return;
            }
            Class<?> componentType = obj.getClass().getComponentType();
            Integer commonClassId = Holder.getCommonClassId(componentType);
            if (commonClassId != null) {
                if (componentType.isPrimitive()) {
                    if (componentType == Byte.TYPE) {
                        byte[] bArr = (byte[]) obj;
                        int length = bArr.length;
                        if (length <= 15) {
                            writeBuffer.put((byte) (104 + length));
                        } else {
                            writeBuffer.put((byte) 14).put((byte) commonClassId.intValue()).putVarInt(length);
                        }
                        writeBuffer.put(bArr);
                        return;
                    }
                    int length2 = Array.getLength(obj);
                    writeBuffer.put((byte) 14).put((byte) commonClassId.intValue()).putVarInt(length2);
                    for (int i = 0; i < length2; i++) {
                        if (componentType == Boolean.TYPE) {
                            writeBuffer.put((byte) (((boolean[]) obj)[i] ? 1 : 0));
                        } else if (componentType == Character.TYPE) {
                            writeBuffer.putChar(((char[]) obj)[i]);
                        } else if (componentType == Short.TYPE) {
                            writeBuffer.putShort(((short[]) obj)[i]);
                        } else if (componentType == Integer.TYPE) {
                            writeBuffer.putInt(((int[]) obj)[i]);
                        } else if (componentType == Float.TYPE) {
                            writeBuffer.putFloat(((float[]) obj)[i]);
                        } else if (componentType == Double.TYPE) {
                            writeBuffer.putDouble(((double[]) obj)[i]);
                        } else {
                            writeBuffer.putLong(((long[]) obj)[i]);
                        }
                    }
                    return;
                }
                writeBuffer.put((byte) 14).put((byte) commonClassId.intValue());
            } else {
                writeBuffer.put((byte) 14).put((byte) -1);
                StringDataType.INSTANCE.write(writeBuffer, componentType.getName());
            }
            Object[] objArr = (Object[]) obj;
            writeBuffer.putVarInt(objArr.length);
            for (Object obj2 : objArr) {
                this.elementType.write(writeBuffer, obj2);
            }
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public Object read(ByteBuffer byteBuffer) {
            return read(byteBuffer, byteBuffer.get());
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType
        public Object read(ByteBuffer byteBuffer, int i) {
            Class<?> cls;
            if (i != 14) {
                byte[] newBytes = Utils.newBytes(i - 104);
                byteBuffer.get(newBytes);
                return newBytes;
            }
            byte b = byteBuffer.get();
            if (b == -1) {
                String read = StringDataType.INSTANCE.read(byteBuffer);
                try {
                    cls = Class.forName(read);
                } catch (Exception e) {
                    throw DataUtils.newMVStoreException(8, "Could not get class {0}", read, e);
                }
            } else {
                cls = ObjectDataType.COMMON_CLASSES[b];
            }
            int readVarInt = DataUtils.readVarInt(byteBuffer);
            try {
                Object newInstance = Array.newInstance(cls, readVarInt);
                if (cls.isPrimitive()) {
                    for (int i2 = 0; i2 < readVarInt; i2++) {
                        if (cls == Boolean.TYPE) {
                            ((boolean[]) newInstance)[i2] = byteBuffer.get() == 1;
                        } else if (cls == Byte.TYPE) {
                            ((byte[]) newInstance)[i2] = byteBuffer.get();
                        } else if (cls == Character.TYPE) {
                            ((char[]) newInstance)[i2] = byteBuffer.getChar();
                        } else if (cls == Short.TYPE) {
                            ((short[]) newInstance)[i2] = byteBuffer.getShort();
                        } else if (cls == Integer.TYPE) {
                            ((int[]) newInstance)[i2] = byteBuffer.getInt();
                        } else if (cls == Float.TYPE) {
                            ((float[]) newInstance)[i2] = byteBuffer.getFloat();
                        } else if (cls == Double.TYPE) {
                            ((double[]) newInstance)[i2] = byteBuffer.getDouble();
                        } else {
                            ((long[]) newInstance)[i2] = byteBuffer.getLong();
                        }
                    }
                } else {
                    Object[] objArr = (Object[]) newInstance;
                    for (int i3 = 0; i3 < readVarInt; i3++) {
                        objArr[i3] = this.elementType.read(byteBuffer);
                    }
                }
                return newInstance;
            } catch (Exception e2) {
                throw DataUtils.newMVStoreException(8, "Could not create array of type {0} length {1}", cls, Integer.valueOf(readVarInt), e2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/ObjectDataType$SerializedObjectType.class */
    public static class SerializedObjectType extends AutoDetectDataType<Object> {
        private int averageSize;

        SerializedObjectType(ObjectDataType objectDataType) {
            super(objectDataType, 19);
            this.averageSize = 10000;
        }

        @Override // org.h2.mvstore.type.DataType
        public Object[] createStorage(int i) {
            return new Object[i];
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
        public int compare(Object obj, Object obj2) {
            if (obj == obj2) {
                return 0;
            }
            DataType<Object> type = getType(obj);
            DataType<Object> type2 = getType(obj2);
            if (type != this || type2 != this) {
                if (type == type2) {
                    return type.compare(obj, obj2);
                }
                return super.compare(obj, obj2);
            }
            if ((obj instanceof Comparable) && obj.getClass().isAssignableFrom(obj2.getClass())) {
                return ((Comparable) obj).compareTo(obj2);
            }
            if ((obj2 instanceof Comparable) && obj2.getClass().isAssignableFrom(obj.getClass())) {
                return -((Comparable) obj2).compareTo(obj);
            }
            return ObjectDataType.compareNotNull(ObjectDataType.serialize(obj), ObjectDataType.serialize(obj2));
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public int getMemory(Object obj) {
            DataType<Object> type = getType(obj);
            if (type == this) {
                return this.averageSize;
            }
            return type.getMemory(obj);
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType, org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public void write(WriteBuffer writeBuffer, Object obj) {
            DataType<Object> type = getType(obj);
            if (type != this) {
                type.write(writeBuffer, obj);
                return;
            }
            byte[] serialize = ObjectDataType.serialize(obj);
            this.averageSize = (int) (((serialize.length * 2) + (15 * this.averageSize)) / 16);
            writeBuffer.put((byte) 19).putVarInt(serialize.length).put(serialize);
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public Object read(ByteBuffer byteBuffer) {
            return read(byteBuffer, byteBuffer.get());
        }

        @Override // org.h2.mvstore.type.ObjectDataType.AutoDetectDataType
        public Object read(ByteBuffer byteBuffer, int i) {
            byte[] newBytes = Utils.newBytes(DataUtils.readVarInt(byteBuffer));
            this.averageSize = (int) (((newBytes.length * 2) + (15 * this.averageSize)) / 16);
            byteBuffer.get(newBytes);
            return ObjectDataType.deserialize(newBytes);
        }
    }
}
