package cn.hutool.core.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;
import org.apache.tomcat.util.bcel.Const;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/util/ByteUtil.class */
public class ByteUtil {
    public static final ByteOrder DEFAULT_ORDER = ByteOrder.LITTLE_ENDIAN;
    public static final ByteOrder CPU_ENDIAN;

    static {
        CPU_ENDIAN = "little".equals(System.getProperty("sun.cpu.endian")) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
    }

    public static byte intToByte(int intValue) {
        return (byte) intValue;
    }

    public static int byteToUnsignedInt(byte byteValue) {
        return byteValue & 255;
    }

    public static short bytesToShort(byte[] bytes) {
        return bytesToShort(bytes, DEFAULT_ORDER);
    }

    public static short bytesToShort(byte[] bytes, ByteOrder byteOrder) {
        return bytesToShort(bytes, 0, byteOrder);
    }

    public static short bytesToShort(byte[] bytes, int start, ByteOrder byteOrder) {
        if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
            return (short) ((bytes[start] & 255) | ((bytes[start + 1] & 255) << 8));
        }
        return (short) ((bytes[start + 1] & 255) | ((bytes[start] & 255) << 8));
    }

    public static byte[] shortToBytes(short shortValue) {
        return shortToBytes(shortValue, DEFAULT_ORDER);
    }

    public static byte[] shortToBytes(short shortValue, ByteOrder byteOrder) {
        byte[] b = new byte[2];
        if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
            b[0] = (byte) (shortValue & 255);
            b[1] = (byte) ((shortValue >> 8) & Const.MAX_ARRAY_DIMENSIONS);
        } else {
            b[1] = (byte) (shortValue & 255);
            b[0] = (byte) ((shortValue >> 8) & Const.MAX_ARRAY_DIMENSIONS);
        }
        return b;
    }

    public static int bytesToInt(byte[] bytes) {
        return bytesToInt(bytes, DEFAULT_ORDER);
    }

    public static int bytesToInt(byte[] bytes, ByteOrder byteOrder) {
        return bytesToInt(bytes, 0, byteOrder);
    }

    public static int bytesToInt(byte[] bytes, int start, ByteOrder byteOrder) {
        if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
            return (bytes[start] & 255) | ((bytes[1 + start] & 255) << 8) | ((bytes[2 + start] & 255) << 16) | ((bytes[3 + start] & 255) << 24);
        }
        return (bytes[3 + start] & 255) | ((bytes[2 + start] & 255) << 8) | ((bytes[1 + start] & 255) << 16) | ((bytes[start] & 255) << 24);
    }

    public static byte[] intToBytes(int intValue) {
        return intToBytes(intValue, DEFAULT_ORDER);
    }

    public static byte[] intToBytes(int intValue, ByteOrder byteOrder) {
        if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
            return new byte[]{(byte) (intValue & Const.MAX_ARRAY_DIMENSIONS), (byte) ((intValue >> 8) & Const.MAX_ARRAY_DIMENSIONS), (byte) ((intValue >> 16) & Const.MAX_ARRAY_DIMENSIONS), (byte) ((intValue >> 24) & Const.MAX_ARRAY_DIMENSIONS)};
        }
        return new byte[]{(byte) ((intValue >> 24) & Const.MAX_ARRAY_DIMENSIONS), (byte) ((intValue >> 16) & Const.MAX_ARRAY_DIMENSIONS), (byte) ((intValue >> 8) & Const.MAX_ARRAY_DIMENSIONS), (byte) (intValue & Const.MAX_ARRAY_DIMENSIONS)};
    }

    public static byte[] longToBytes(long longValue) {
        return longToBytes(longValue, DEFAULT_ORDER);
    }

    public static byte[] longToBytes(long longValue, ByteOrder byteOrder) {
        byte[] result = new byte[8];
        if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
            for (int i = 0; i < result.length; i++) {
                result[i] = (byte) (longValue & 255);
                longValue >>= 8;
            }
        } else {
            for (int i2 = result.length - 1; i2 >= 0; i2--) {
                result[i2] = (byte) (longValue & 255);
                longValue >>= 8;
            }
        }
        return result;
    }

    public static long bytesToLong(byte[] bytes) {
        return bytesToLong(bytes, DEFAULT_ORDER);
    }

    public static long bytesToLong(byte[] bytes, ByteOrder byteOrder) {
        return bytesToLong(bytes, 0, byteOrder);
    }

    public static long bytesToLong(byte[] bytes, int start, ByteOrder byteOrder) {
        long values = 0;
        if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
            for (int i = 7; i >= 0; i--) {
                values = (values << 8) | (bytes[i + start] & 255);
            }
        } else {
            for (int i2 = 0; i2 < 8; i2++) {
                values = (values << 8) | (bytes[i2 + start] & 255);
            }
        }
        return values;
    }

    public static byte[] floatToBytes(float floatValue) {
        return floatToBytes(floatValue, DEFAULT_ORDER);
    }

    public static byte[] floatToBytes(float floatValue, ByteOrder byteOrder) {
        return intToBytes(Float.floatToIntBits(floatValue), byteOrder);
    }

    public static float bytesToFloat(byte[] bytes) {
        return bytesToFloat(bytes, DEFAULT_ORDER);
    }

    public static float bytesToFloat(byte[] bytes, ByteOrder byteOrder) {
        return Float.intBitsToFloat(bytesToInt(bytes, byteOrder));
    }

    public static byte[] doubleToBytes(double doubleValue) {
        return doubleToBytes(doubleValue, DEFAULT_ORDER);
    }

    public static byte[] doubleToBytes(double doubleValue, ByteOrder byteOrder) {
        return longToBytes(Double.doubleToLongBits(doubleValue), byteOrder);
    }

    public static double bytesToDouble(byte[] bytes) {
        return bytesToDouble(bytes, DEFAULT_ORDER);
    }

    public static double bytesToDouble(byte[] bytes, ByteOrder byteOrder) {
        return Double.longBitsToDouble(bytesToLong(bytes, byteOrder));
    }

    public static byte[] numberToBytes(Number number) {
        return numberToBytes(number, DEFAULT_ORDER);
    }

    public static byte[] numberToBytes(Number number, ByteOrder byteOrder) {
        if (number instanceof Byte) {
            return new byte[]{number.byteValue()};
        }
        if (number instanceof Double) {
            return doubleToBytes(((Double) number).doubleValue(), byteOrder);
        }
        if (number instanceof Long) {
            return longToBytes(((Long) number).longValue(), byteOrder);
        }
        if (number instanceof Integer) {
            return intToBytes(((Integer) number).intValue(), byteOrder);
        }
        if (number instanceof Short) {
            return shortToBytes(((Short) number).shortValue(), byteOrder);
        }
        if (number instanceof Float) {
            return floatToBytes(((Float) number).floatValue(), byteOrder);
        }
        return doubleToBytes(number.doubleValue(), byteOrder);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v19, types: [java.math.BigInteger] */
    /* JADX WARN: Type inference failed for: r0v23, types: [java.math.BigDecimal] */
    /* JADX WARN: Type inference failed for: r0v24, types: [java.util.concurrent.atomic.DoubleAdder] */
    /* JADX WARN: Type inference failed for: r0v32, types: [java.lang.Float] */
    /* JADX WARN: Type inference failed for: r0v33, types: [java.util.concurrent.atomic.LongAdder] */
    /* JADX WARN: Type inference failed for: r0v36, types: [java.util.concurrent.atomic.AtomicLong] */
    /* JADX WARN: Type inference failed for: r0v39, types: [java.lang.Long] */
    /* JADX WARN: Type inference failed for: r0v40, types: [java.util.concurrent.atomic.AtomicInteger] */
    /* JADX WARN: Type inference failed for: r0v43, types: [java.lang.Integer] */
    /* JADX WARN: Type inference failed for: r0v46, types: [java.lang.Short] */
    /* JADX WARN: Type inference failed for: r0v50, types: [java.lang.Byte] */
    public static <T extends Number> T bytesToNumber(byte[] bytes, Class<T> targetClass, ByteOrder byteOrder) throws IllegalArgumentException {
        Double valueOf;
        if (Byte.class == targetClass) {
            valueOf = Byte.valueOf(bytes[0]);
        } else if (Short.class == targetClass) {
            valueOf = Short.valueOf(bytesToShort(bytes, byteOrder));
        } else if (Integer.class == targetClass) {
            valueOf = Integer.valueOf(bytesToInt(bytes, byteOrder));
        } else if (AtomicInteger.class == targetClass) {
            valueOf = new AtomicInteger(bytesToInt(bytes, byteOrder));
        } else if (Long.class == targetClass) {
            valueOf = Long.valueOf(bytesToLong(bytes, byteOrder));
        } else if (AtomicLong.class == targetClass) {
            valueOf = new AtomicLong(bytesToLong(bytes, byteOrder));
        } else if (LongAdder.class == targetClass) {
            ?? longAdder = new LongAdder();
            longAdder.add(bytesToLong(bytes, byteOrder));
            valueOf = longAdder;
        } else if (Float.class == targetClass) {
            valueOf = Float.valueOf(bytesToFloat(bytes, byteOrder));
        } else if (Double.class == targetClass) {
            valueOf = Double.valueOf(bytesToDouble(bytes, byteOrder));
        } else if (DoubleAdder.class == targetClass) {
            ?? doubleAdder = new DoubleAdder();
            doubleAdder.add(bytesToDouble(bytes, byteOrder));
            valueOf = doubleAdder;
        } else if (BigDecimal.class == targetClass) {
            valueOf = NumberUtil.toBigDecimal(Double.valueOf(bytesToDouble(bytes, byteOrder)));
        } else if (BigInteger.class == targetClass) {
            valueOf = BigInteger.valueOf(bytesToLong(bytes, byteOrder));
        } else if (Number.class == targetClass) {
            valueOf = Double.valueOf(bytesToDouble(bytes, byteOrder));
        } else {
            throw new IllegalArgumentException("Unsupported Number type: " + targetClass.getName());
        }
        return valueOf;
    }
}
