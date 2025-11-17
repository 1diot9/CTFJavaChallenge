package org.springframework.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Set;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/NumberUtils.class */
public abstract class NumberUtils {
    private static final BigInteger LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
    private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);
    public static final Set<Class<?>> STANDARD_NUMBER_TYPES = Set.of(Byte.class, Short.class, Integer.class, Long.class, BigInteger.class, Float.class, Double.class, BigDecimal.class);

    /* JADX WARN: Multi-variable type inference failed */
    public static <T extends Number> T convertNumberToTargetClass(Number number, Class<T> targetClass) throws IllegalArgumentException {
        Assert.notNull(number, "Number must not be null");
        Assert.notNull(targetClass, "Target class must not be null");
        if (targetClass.isInstance(number)) {
            return number;
        }
        if (Byte.class == targetClass) {
            long value = checkedLongValue(number, targetClass);
            if (value < -128 || value > 127) {
                raiseOverflowException(number, targetClass);
            }
            return Byte.valueOf(number.byteValue());
        }
        if (Short.class == targetClass) {
            long value2 = checkedLongValue(number, targetClass);
            if (value2 < -32768 || value2 > 32767) {
                raiseOverflowException(number, targetClass);
            }
            return Short.valueOf(number.shortValue());
        }
        if (Integer.class == targetClass) {
            long value3 = checkedLongValue(number, targetClass);
            if (value3 < -2147483648L || value3 > 2147483647L) {
                raiseOverflowException(number, targetClass);
            }
            return Integer.valueOf(number.intValue());
        }
        if (Long.class == targetClass) {
            return Long.valueOf(checkedLongValue(number, targetClass));
        }
        if (BigInteger.class == targetClass) {
            if (number instanceof BigDecimal) {
                BigDecimal bigDecimal = (BigDecimal) number;
                return bigDecimal.toBigInteger();
            }
            return BigInteger.valueOf(number.longValue());
        }
        if (Float.class == targetClass) {
            return Float.valueOf(number.floatValue());
        }
        if (Double.class == targetClass) {
            return Double.valueOf(number.doubleValue());
        }
        if (BigDecimal.class == targetClass) {
            return new BigDecimal(number.toString());
        }
        throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" + number.getClass().getName() + "] to unsupported target class [" + targetClass.getName() + "]");
    }

    private static long checkedLongValue(Number number, Class<? extends Number> targetClass) {
        BigInteger bigInt = null;
        if (number instanceof BigInteger) {
            BigInteger bigInteger = (BigInteger) number;
            bigInt = bigInteger;
        } else if (number instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) number;
            bigInt = bigDecimal.toBigInteger();
        }
        if (bigInt != null && (bigInt.compareTo(LONG_MIN) < 0 || bigInt.compareTo(LONG_MAX) > 0)) {
            raiseOverflowException(number, targetClass);
        }
        return number.longValue();
    }

    private static void raiseOverflowException(Number number, Class<?> targetClass) {
        throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" + number.getClass().getName() + "] to target class [" + targetClass.getName() + "]: overflow");
    }

    public static <T extends Number> T parseNumber(String text, Class<T> targetClass) {
        Assert.notNull(text, "Text must not be null");
        Assert.notNull(targetClass, "Target class must not be null");
        String trimmed = StringUtils.trimAllWhitespace(text);
        if (Byte.class == targetClass) {
            return isHexNumber(trimmed) ? Byte.decode(trimmed) : Byte.valueOf(trimmed);
        }
        if (Short.class == targetClass) {
            return isHexNumber(trimmed) ? Short.decode(trimmed) : Short.valueOf(trimmed);
        }
        if (Integer.class == targetClass) {
            return isHexNumber(trimmed) ? Integer.decode(trimmed) : Integer.valueOf(trimmed);
        }
        if (Long.class == targetClass) {
            return isHexNumber(trimmed) ? Long.decode(trimmed) : Long.valueOf(trimmed);
        }
        if (BigInteger.class == targetClass) {
            return isHexNumber(trimmed) ? decodeBigInteger(trimmed) : new BigInteger(trimmed);
        }
        if (Float.class == targetClass) {
            return Float.valueOf(trimmed);
        }
        if (Double.class == targetClass) {
            return Double.valueOf(trimmed);
        }
        if (BigDecimal.class == targetClass || Number.class == targetClass) {
            return new BigDecimal(trimmed);
        }
        throw new IllegalArgumentException("Cannot convert String [" + text + "] to target class [" + targetClass.getName() + "]");
    }

    public static <T extends Number> T parseNumber(String str, Class<T> cls, @Nullable NumberFormat numberFormat) {
        if (numberFormat != null) {
            Assert.notNull(str, "Text must not be null");
            Assert.notNull(cls, "Target class must not be null");
            DecimalFormat decimalFormat = null;
            boolean z = false;
            if (numberFormat instanceof DecimalFormat) {
                decimalFormat = (DecimalFormat) numberFormat;
                if (BigDecimal.class == cls && !decimalFormat.isParseBigDecimal()) {
                    decimalFormat.setParseBigDecimal(true);
                    z = true;
                }
            }
            try {
                try {
                    T t = (T) convertNumberToTargetClass(numberFormat.parse(StringUtils.trimAllWhitespace(str)), cls);
                    if (z) {
                        decimalFormat.setParseBigDecimal(false);
                    }
                    return t;
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Could not parse number: " + e.getMessage());
                }
            } catch (Throwable th) {
                if (z) {
                    decimalFormat.setParseBigDecimal(false);
                }
                throw th;
            }
        }
        return (T) parseNumber(str, cls);
    }

    private static boolean isHexNumber(String value) {
        int index = value.startsWith("-") ? 1 : 0;
        return value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index);
    }

    private static BigInteger decodeBigInteger(String value) {
        int radix = 10;
        int index = 0;
        boolean negative = false;
        if (value.startsWith("-")) {
            negative = true;
            index = 0 + 1;
        }
        if (value.startsWith("0x", index) || value.startsWith("0X", index)) {
            index += 2;
            radix = 16;
        } else if (value.startsWith("#", index)) {
            index++;
            radix = 16;
        } else if (value.startsWith(CustomBooleanEditor.VALUE_0, index) && value.length() > 1 + index) {
            index++;
            radix = 8;
        }
        BigInteger result = new BigInteger(value.substring(index), radix);
        return negative ? result.negate() : result;
    }
}
