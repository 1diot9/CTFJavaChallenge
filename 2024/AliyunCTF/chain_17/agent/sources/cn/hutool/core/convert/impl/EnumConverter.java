package cn.hutool.core.convert.impl;

import ch.qos.logback.core.CoreConstants;
import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.lang.EnumItem;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.map.WeakConcurrentMap;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ModifierUtil;
import cn.hutool.core.util.ReflectUtil;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/impl/EnumConverter.class */
public class EnumConverter extends AbstractConverter<Object> {
    private static final long serialVersionUID = 1;
    private static final WeakConcurrentMap<Class<?>, Map<Class<?>, Method>> VALUE_OF_METHOD_CACHE = new WeakConcurrentMap<>();
    private final Class enumClass;

    public EnumConverter(Class enumClass) {
        this.enumClass = enumClass;
    }

    @Override // cn.hutool.core.convert.AbstractConverter
    /* renamed from: convertInternal */
    protected Object convertInternal2(Object value) {
        Enum enumValue = tryConvertEnum(value, this.enumClass);
        if (null == enumValue && false == (value instanceof String)) {
            enumValue = Enum.valueOf(this.enumClass, convertToStr(value));
        }
        if (null != enumValue) {
            return enumValue;
        }
        throw new ConvertException("Can not convert {} to {}", value, this.enumClass);
    }

    @Override // cn.hutool.core.convert.AbstractConverter
    public Class<Object> getTargetType() {
        return this.enumClass;
    }

    protected static Enum tryConvertEnum(Object value, Class enumClass) {
        EnumItem first;
        if (value == null) {
            return null;
        }
        if (EnumItem.class.isAssignableFrom(enumClass) && null != (first = (EnumItem) EnumUtil.getEnumAt(enumClass, 0))) {
            if (value instanceof Integer) {
                return (Enum) first.fromInt((Integer) value);
            }
            if (value instanceof String) {
                return (Enum) first.fromStr(value.toString());
            }
        }
        try {
            Map<Class<?>, Method> methodMap = getMethodMap(enumClass);
            if (MapUtil.isNotEmpty(methodMap)) {
                Class<?> valueClass = value.getClass();
                for (Map.Entry<Class<?>, Method> entry : methodMap.entrySet()) {
                    if (ClassUtil.isAssignable(entry.getKey(), valueClass)) {
                        return (Enum) ReflectUtil.invokeStatic(entry.getValue(), value);
                    }
                }
            }
        } catch (Exception e) {
        }
        Enum enumResult = null;
        if (value instanceof Integer) {
            enumResult = EnumUtil.getEnumAt(enumClass, ((Integer) value).intValue());
        } else if (value instanceof String) {
            try {
                enumResult = Enum.valueOf(enumClass, (String) value);
            } catch (IllegalArgumentException e2) {
            }
        }
        return enumResult;
    }

    private static Map<Class<?>, Method> getMethodMap(Class<?> enumClass) {
        return VALUE_OF_METHOD_CACHE.computeIfAbsent((WeakConcurrentMap<Class<?>, Map<Class<?>, Method>>) enumClass, (Function<? super WeakConcurrentMap<Class<?>, Map<Class<?>, Method>>, ? extends Map<Class<?>, Method>>) key -> {
            return (Map) Arrays.stream(enumClass.getMethods()).filter(ModifierUtil::isStatic).filter(m -> {
                return m.getReturnType() == enumClass;
            }).filter(m2 -> {
                return m2.getParameterCount() == 1;
            }).filter(m3 -> {
                return false == CoreConstants.VALUE_OF.equals(m3.getName());
            }).collect(Collectors.toMap(m4 -> {
                return m4.getParameterTypes()[0];
            }, m5 -> {
                return m5;
            }, (k1, k2) -> {
                return k1;
            }));
        });
    }
}
