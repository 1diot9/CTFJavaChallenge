package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/impl/NumberConverter.class */
public class NumberConverter extends AbstractConverter<Number> {
    private static final long serialVersionUID = 1;
    private final Class<? extends Number> targetType;

    public NumberConverter() {
        this.targetType = Number.class;
    }

    public NumberConverter(Class<? extends Number> clazz) {
        this.targetType = null == clazz ? Number.class : clazz;
    }

    @Override // cn.hutool.core.convert.AbstractConverter
    public Class<Number> getTargetType() {
        return this.targetType;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.convert.AbstractConverter
    /* renamed from: convertInternal */
    public Number convertInternal2(Object value) {
        return convert(value, this.targetType, this::convertToStr);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // cn.hutool.core.convert.AbstractConverter
    public String convertToStr(Object value) {
        char c;
        String result = StrUtil.trim(super.convertToStr(value));
        if (null != result && result.length() > 1 && ((c = Character.toUpperCase(result.charAt(result.length() - 1))) == 'D' || c == 'L' || c == 'F')) {
            return StrUtil.subPre(result, -1);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static Number convert(Object value, Class<? extends Number> targetType, Function<Object, String> toStrFunc) {
        if (value instanceof Enum) {
            return convert(Integer.valueOf(((Enum) value).ordinal()), targetType, toStrFunc);
        }
        if (value instanceof byte[]) {
            return ByteUtil.bytesToNumber((byte[]) value, targetType, ByteUtil.DEFAULT_ORDER);
        }
        if (Byte.class == targetType) {
            if (value instanceof Number) {
                return Byte.valueOf(((Number) value).byteValue());
            }
            if (value instanceof Boolean) {
                return BooleanUtil.toByteObj(((Boolean) value).booleanValue());
            }
            String valueStr = toStrFunc.apply(value);
            try {
                if (StrUtil.isBlank(valueStr)) {
                    return null;
                }
                return Byte.valueOf(valueStr);
            } catch (NumberFormatException e) {
                return Byte.valueOf(NumberUtil.parseNumber(valueStr).byteValue());
            }
        }
        if (Short.class == targetType) {
            if (value instanceof Number) {
                return Short.valueOf(((Number) value).shortValue());
            }
            if (value instanceof Boolean) {
                return BooleanUtil.toShortObj(((Boolean) value).booleanValue());
            }
            String valueStr2 = toStrFunc.apply(value);
            try {
                if (StrUtil.isBlank(valueStr2)) {
                    return null;
                }
                return Short.valueOf(valueStr2);
            } catch (NumberFormatException e2) {
                return Short.valueOf(NumberUtil.parseNumber(valueStr2).shortValue());
            }
        }
        if (Integer.class == targetType) {
            if (value instanceof Number) {
                return Integer.valueOf(((Number) value).intValue());
            }
            if (value instanceof Boolean) {
                return BooleanUtil.toInteger(((Boolean) value).booleanValue());
            }
            if (value instanceof Date) {
                return Integer.valueOf((int) ((Date) value).getTime());
            }
            if (value instanceof Calendar) {
                return Integer.valueOf((int) ((Calendar) value).getTimeInMillis());
            }
            if (value instanceof TemporalAccessor) {
                return Integer.valueOf((int) DateUtil.toInstant((TemporalAccessor) value).toEpochMilli());
            }
            String valueStr3 = toStrFunc.apply(value);
            if (StrUtil.isBlank(valueStr3)) {
                return null;
            }
            return Integer.valueOf(NumberUtil.parseInt(valueStr3));
        }
        if (AtomicInteger.class == targetType) {
            Number number = convert(value, Integer.class, toStrFunc);
            if (null != number) {
                return new AtomicInteger(number.intValue());
            }
        } else {
            if (Long.class == targetType) {
                if (value instanceof Number) {
                    return Long.valueOf(((Number) value).longValue());
                }
                if (value instanceof Boolean) {
                    return BooleanUtil.toLongObj(((Boolean) value).booleanValue());
                }
                if (value instanceof Date) {
                    return Long.valueOf(((Date) value).getTime());
                }
                if (value instanceof Calendar) {
                    return Long.valueOf(((Calendar) value).getTimeInMillis());
                }
                if (value instanceof TemporalAccessor) {
                    return Long.valueOf(DateUtil.toInstant((TemporalAccessor) value).toEpochMilli());
                }
                String valueStr4 = toStrFunc.apply(value);
                if (StrUtil.isBlank(valueStr4)) {
                    return null;
                }
                return Long.valueOf(NumberUtil.parseLong(valueStr4));
            }
            if (AtomicLong.class == targetType) {
                Number number2 = convert(value, Long.class, toStrFunc);
                if (null != number2) {
                    return new AtomicLong(number2.longValue());
                }
            } else if (LongAdder.class == targetType) {
                Number number3 = convert(value, Long.class, toStrFunc);
                if (null != number3) {
                    LongAdder longValue = new LongAdder();
                    longValue.add(number3.longValue());
                    return longValue;
                }
            } else {
                if (Float.class == targetType) {
                    if (value instanceof Number) {
                        return Float.valueOf(((Number) value).floatValue());
                    }
                    if (value instanceof Boolean) {
                        return BooleanUtil.toFloatObj(((Boolean) value).booleanValue());
                    }
                    String valueStr5 = toStrFunc.apply(value);
                    if (StrUtil.isBlank(valueStr5)) {
                        return null;
                    }
                    return Float.valueOf(NumberUtil.parseFloat(valueStr5));
                }
                if (Double.class == targetType) {
                    if (value instanceof Number) {
                        return Double.valueOf(NumberUtil.toDouble((Number) value));
                    }
                    if (value instanceof Boolean) {
                        return BooleanUtil.toDoubleObj(((Boolean) value).booleanValue());
                    }
                    String valueStr6 = toStrFunc.apply(value);
                    if (StrUtil.isBlank(valueStr6)) {
                        return null;
                    }
                    return Double.valueOf(NumberUtil.parseDouble(valueStr6));
                }
                if (DoubleAdder.class == targetType) {
                    Number number4 = convert(value, Double.class, toStrFunc);
                    if (null != number4) {
                        DoubleAdder doubleAdder = new DoubleAdder();
                        doubleAdder.add(number4.doubleValue());
                        return doubleAdder;
                    }
                } else {
                    if (BigDecimal.class == targetType) {
                        return toBigDecimal(value, toStrFunc);
                    }
                    if (BigInteger.class == targetType) {
                        return toBigInteger(value, toStrFunc);
                    }
                    if (Number.class == targetType) {
                        if (value instanceof Number) {
                            return (Number) value;
                        }
                        if (value instanceof Boolean) {
                            return BooleanUtil.toInteger(((Boolean) value).booleanValue());
                        }
                        String valueStr7 = toStrFunc.apply(value);
                        if (StrUtil.isBlank(valueStr7)) {
                            return null;
                        }
                        return NumberUtil.parseNumber(valueStr7);
                    }
                }
            }
        }
        throw new UnsupportedOperationException(StrUtil.format("Unsupport Number type: {}", targetType.getName()));
    }

    private static BigDecimal toBigDecimal(Object value, Function<Object, String> toStrFunc) {
        if (value instanceof Number) {
            return NumberUtil.toBigDecimal((Number) value);
        }
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue() ? BigDecimal.ONE : BigDecimal.ZERO;
        }
        return NumberUtil.toBigDecimal(toStrFunc.apply(value));
    }

    private static BigInteger toBigInteger(Object value, Function<Object, String> toStrFunc) {
        if (value instanceof Long) {
            return BigInteger.valueOf(((Long) value).longValue());
        }
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue() ? BigInteger.ONE : BigInteger.ZERO;
        }
        return NumberUtil.toBigInteger(toStrFunc.apply(value));
    }
}
