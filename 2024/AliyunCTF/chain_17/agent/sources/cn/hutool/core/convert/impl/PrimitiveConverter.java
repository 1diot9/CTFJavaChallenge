package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import java.util.function.Function;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/impl/PrimitiveConverter.class */
public class PrimitiveConverter extends AbstractConverter<Object> {
    private static final long serialVersionUID = 1;
    private final Class<?> targetType;

    public PrimitiveConverter(Class<?> clazz) {
        if (null == clazz) {
            throw new NullPointerException("PrimitiveConverter not allow null target type!");
        }
        if (false == clazz.isPrimitive()) {
            throw new IllegalArgumentException("[" + clazz + "] is not a primitive class!");
        }
        this.targetType = clazz;
    }

    @Override // cn.hutool.core.convert.AbstractConverter
    /* renamed from: convertInternal */
    protected Object convertInternal2(Object value) {
        return convert(value, this.targetType, this::convertToStr);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // cn.hutool.core.convert.AbstractConverter
    public String convertToStr(Object value) {
        return StrUtil.trim(super.convertToStr(value));
    }

    @Override // cn.hutool.core.convert.AbstractConverter
    public Class<Object> getTargetType() {
        return this.targetType;
    }

    protected static Object convert(Object value, Class<?> primitiveClass, Function<Object, String> toStringFunc) {
        if (Byte.TYPE == primitiveClass) {
            return ObjectUtil.defaultIfNull((int) NumberConverter.convert(value, Byte.class, toStringFunc), 0);
        }
        if (Short.TYPE == primitiveClass) {
            return ObjectUtil.defaultIfNull((int) NumberConverter.convert(value, Short.class, toStringFunc), 0);
        }
        if (Integer.TYPE == primitiveClass) {
            return ObjectUtil.defaultIfNull((int) NumberConverter.convert(value, Integer.class, toStringFunc), 0);
        }
        if (Long.TYPE == primitiveClass) {
            return ObjectUtil.defaultIfNull((int) NumberConverter.convert(value, Long.class, toStringFunc), 0);
        }
        if (Float.TYPE == primitiveClass) {
            return ObjectUtil.defaultIfNull((int) NumberConverter.convert(value, Float.class, toStringFunc), 0);
        }
        if (Double.TYPE == primitiveClass) {
            return ObjectUtil.defaultIfNull((int) NumberConverter.convert(value, Double.class, toStringFunc), 0);
        }
        if (Character.TYPE == primitiveClass) {
            return Convert.convert(Character.class, value);
        }
        if (Boolean.TYPE == primitiveClass) {
            return Convert.convert(Boolean.class, value);
        }
        throw new ConvertException("Unsupported target type: {}", primitiveClass);
    }
}
