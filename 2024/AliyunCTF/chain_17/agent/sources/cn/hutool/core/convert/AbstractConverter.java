package cn.hutool.core.convert;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/AbstractConverter.class */
public abstract class AbstractConverter<T> implements Converter<T>, Serializable {
    private static final long serialVersionUID = 1;

    protected abstract T convertInternal(Object obj);

    public T convertQuietly(Object value, T defaultValue) {
        try {
            return convert(value, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // cn.hutool.core.convert.Converter
    public T convert(Object value, T defaultValue) {
        Class targetType = getTargetType();
        if (null == targetType && null == defaultValue) {
            throw new NullPointerException(StrUtil.format("[type] and [defaultValue] are both null for Converter [{}], we can not know what type to convert !", getClass().getName()));
        }
        if (null == targetType) {
            targetType = defaultValue.getClass();
        }
        if (null == value) {
            return defaultValue;
        }
        if (null == defaultValue || targetType.isInstance(defaultValue)) {
            if (targetType.isInstance(value) && false == Map.class.isAssignableFrom(targetType)) {
                return targetType.cast(value);
            }
            T result = convertInternal(value);
            return null == result ? defaultValue : result;
        }
        throw new IllegalArgumentException(StrUtil.format("Default value [{}]({}) is not the instance of [{}]", defaultValue, defaultValue.getClass(), targetType));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String convertToStr(Object value) {
        if (null == value) {
            return null;
        }
        if (value instanceof CharSequence) {
            return value.toString();
        }
        if (ArrayUtil.isArray(value)) {
            return ArrayUtil.toString(value);
        }
        if (CharUtil.isChar(value)) {
            return CharUtil.toString(((Character) value).charValue());
        }
        return value.toString();
    }

    public Class<T> getTargetType() {
        return (Class<T>) ClassUtil.getTypeArgument(getClass());
    }
}
