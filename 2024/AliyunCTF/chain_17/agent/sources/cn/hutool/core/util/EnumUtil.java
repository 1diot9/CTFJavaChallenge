package cn.hutool.core.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.map.MapUtil;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/util/EnumUtil.class */
public class EnumUtil {
    public static boolean isEnum(Class<?> clazz) {
        Assert.notNull(clazz);
        return clazz.isEnum();
    }

    public static boolean isEnum(Object obj) {
        Assert.notNull(obj);
        return obj.getClass().isEnum();
    }

    public static String toString(Enum<?> e) {
        if (null != e) {
            return e.name();
        }
        return null;
    }

    public static <E extends Enum<E>> E getEnumAt(Class<E> enumClass, int index) {
        E[] enumConstants = enumClass.getEnumConstants();
        if (index < 0 || index >= enumConstants.length) {
            return null;
        }
        return enumConstants[index];
    }

    public static <E extends Enum<E>> E fromString(Class<E> cls, String str) {
        return (E) Enum.valueOf(cls, str);
    }

    public static <E extends Enum<E>> E fromString(Class<E> enumClass, String value, E defaultValue) {
        return (E) ObjectUtil.defaultIfNull((E) fromStringQuietly(enumClass, value), defaultValue);
    }

    public static <E extends Enum<E>> E fromStringQuietly(Class<E> cls, String str) {
        if (null == cls || StrUtil.isBlank(str)) {
            return null;
        }
        try {
            return (E) fromString(cls, str);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static <E extends Enum<E>> E likeValueOf(Class<E> enumClass, Object value) {
        if (value instanceof CharSequence) {
            value = value.toString().trim();
        }
        Field[] fields = ReflectUtil.getFields(enumClass);
        E[] enumConstants = enumClass.getEnumConstants();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (!field.getType().isEnum() && !"ENUM$VALUES".equals(fieldName) && !"ordinal".equals(fieldName)) {
                for (E e : enumConstants) {
                    if (ObjectUtil.equal(value, ReflectUtil.getFieldValue(e, field))) {
                        return e;
                    }
                }
            }
        }
        return null;
    }

    public static List<String> getNames(Class<? extends Enum<?>> clazz) {
        Enum<?>[] enums = (Enum[]) clazz.getEnumConstants();
        if (null == enums) {
            return null;
        }
        List<String> list = new ArrayList<>(enums.length);
        for (Enum<?> e : enums) {
            list.add(e.name());
        }
        return list;
    }

    public static List<Object> getFieldValues(Class<? extends Enum<?>> clazz, String fieldName) {
        Enum<?>[] enums = (Enum[]) clazz.getEnumConstants();
        if (null == enums) {
            return null;
        }
        List<Object> list = new ArrayList<>(enums.length);
        for (Enum<?> e : enums) {
            list.add(ReflectUtil.getFieldValue(e, fieldName));
        }
        return list;
    }

    public static List<String> getFieldNames(Class<? extends Enum<?>> clazz) {
        List<String> names = new ArrayList<>();
        Field[] fields = ReflectUtil.getFields(clazz);
        for (Field field : fields) {
            String name = field.getName();
            if (!field.getType().isEnum() && !name.contains("$VALUES") && !"ordinal".equals(name) && false == names.contains(name)) {
                names.add(name);
            }
        }
        return names;
    }

    public static <E extends Enum<E>> E getBy(Class<E> enumClass, Predicate<? super E> predicate) {
        return (E) Arrays.stream(enumClass.getEnumConstants()).filter(predicate).findFirst().orElse(null);
    }

    public static <E extends Enum<E>, C> E getBy(Func1<E, C> condition, C value) {
        Class<E> implClass = LambdaUtil.getRealClass(condition);
        if (Enum.class.equals(implClass)) {
            implClass = LambdaUtil.getRealClass(condition);
        }
        return (E) Arrays.stream(implClass.getEnumConstants()).filter(e -> {
            return condition.callWithRuntimeException(e).equals(value);
        }).findAny().orElse(null);
    }

    public static <E extends Enum<E>, C> E getBy(Func1<E, C> condition, C value, E defaultEnum) {
        return (E) ObjectUtil.defaultIfNull((E) getBy(condition, value), defaultEnum);
    }

    public static <E extends Enum<E>, F, C> F getFieldBy(Func1<E, F> func1, Function<E, C> function, C c) {
        Class realClass = LambdaUtil.getRealClass(func1);
        if (Enum.class.equals(realClass)) {
            realClass = LambdaUtil.getRealClass(func1);
        }
        Optional findFirst = Arrays.stream(realClass.getEnumConstants()).filter(e -> {
            return function.apply(e).equals(c);
        }).findFirst();
        func1.getClass();
        return (F) findFirst.map((v1) -> {
            return r1.callWithRuntimeException(v1);
        }).orElse(null);
    }

    public static <E extends Enum<E>> LinkedHashMap<String, E> getEnumMap(Class<E> enumClass) {
        LinkedHashMap<String, E> map = new LinkedHashMap<>();
        for (E e : enumClass.getEnumConstants()) {
            map.put(e.name(), e);
        }
        return map;
    }

    public static Map<String, Object> getNameFieldMap(Class<? extends Enum<?>> clazz, String fieldName) {
        Enum<?>[] enums = (Enum[]) clazz.getEnumConstants();
        if (null == enums) {
            return null;
        }
        Map<String, Object> map = MapUtil.newHashMap(enums.length, true);
        for (Enum<?> e : enums) {
            map.put(e.name(), ReflectUtil.getFieldValue(e, fieldName));
        }
        return map;
    }

    public static <E extends Enum<E>> boolean contains(Class<E> enumClass, String val) {
        return getEnumMap(enumClass).containsKey(val);
    }

    public static <E extends Enum<E>> boolean notContains(Class<E> enumClass, String val) {
        return false == contains(enumClass, val);
    }

    public static boolean equalsIgnoreCase(Enum<?> e, String val) {
        return StrUtil.equalsIgnoreCase(toString(e), val);
    }

    public static boolean equals(Enum<?> e, String val) {
        return StrUtil.equals(toString(e), val);
    }
}
