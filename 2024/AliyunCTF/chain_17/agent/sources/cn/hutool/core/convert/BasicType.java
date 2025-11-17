package cn.hutool.core.convert;

import cn.hutool.core.map.SafeConcurrentHashMap;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/BasicType.class */
public enum BasicType {
    BYTE,
    SHORT,
    INT,
    INTEGER,
    LONG,
    DOUBLE,
    FLOAT,
    BOOLEAN,
    CHAR,
    CHARACTER,
    STRING;

    public static final Map<Class<?>, Class<?>> WRAPPER_PRIMITIVE_MAP = new SafeConcurrentHashMap(8);
    public static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_MAP = new SafeConcurrentHashMap(8);

    static {
        WRAPPER_PRIMITIVE_MAP.put(Boolean.class, Boolean.TYPE);
        WRAPPER_PRIMITIVE_MAP.put(Byte.class, Byte.TYPE);
        WRAPPER_PRIMITIVE_MAP.put(Character.class, Character.TYPE);
        WRAPPER_PRIMITIVE_MAP.put(Double.class, Double.TYPE);
        WRAPPER_PRIMITIVE_MAP.put(Float.class, Float.TYPE);
        WRAPPER_PRIMITIVE_MAP.put(Integer.class, Integer.TYPE);
        WRAPPER_PRIMITIVE_MAP.put(Long.class, Long.TYPE);
        WRAPPER_PRIMITIVE_MAP.put(Short.class, Short.TYPE);
        for (Map.Entry<Class<?>, Class<?>> entry : WRAPPER_PRIMITIVE_MAP.entrySet()) {
            PRIMITIVE_WRAPPER_MAP.put(entry.getValue(), entry.getKey());
        }
    }

    public static Class<?> wrap(Class<?> clazz) {
        if (null == clazz || false == clazz.isPrimitive()) {
            return clazz;
        }
        Class<?> result = PRIMITIVE_WRAPPER_MAP.get(clazz);
        return null == result ? clazz : result;
    }

    public static Class<?> unWrap(Class<?> clazz) {
        if (null == clazz || clazz.isPrimitive()) {
            return clazz;
        }
        Class<?> result = WRAPPER_PRIMITIVE_MAP.get(clazz);
        return null == result ? clazz : result;
    }
}
