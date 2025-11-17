package cn.hutool.core.util;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/util/ObjectUtil.class */
public class ObjectUtil {
    public static boolean equals(Object obj1, Object obj2) {
        return equal(obj1, obj2);
    }

    public static boolean equal(Object obj1, Object obj2) {
        if ((obj1 instanceof BigDecimal) && (obj2 instanceof BigDecimal)) {
            return NumberUtil.equals((BigDecimal) obj1, (BigDecimal) obj2);
        }
        return Objects.equals(obj1, obj2);
    }

    public static boolean notEqual(Object obj1, Object obj2) {
        return false == equal(obj1, obj2);
    }

    public static int length(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length();
        }
        if (obj instanceof Collection) {
            return ((Collection) obj).size();
        }
        if (obj instanceof Map) {
            return ((Map) obj).size();
        }
        if (obj instanceof Iterator) {
            Iterator<?> iter = (Iterator) obj;
            int count = 0;
            while (iter.hasNext()) {
                count++;
                iter.next();
            }
            return count;
        }
        if (obj instanceof Enumeration) {
            Enumeration<?> enumeration = (Enumeration) obj;
            int count2 = 0;
            while (enumeration.hasMoreElements()) {
                count2++;
                enumeration.nextElement();
            }
            return count2;
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj);
        }
        return -1;
    }

    public static boolean contains(Object obj, Object element) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof String) {
            if (element == null) {
                return false;
            }
            return ((String) obj).contains(element.toString());
        }
        if (obj instanceof Collection) {
            return ((Collection) obj).contains(element);
        }
        if (obj instanceof Map) {
            return ((Map) obj).containsValue(element);
        }
        if (obj instanceof Iterator) {
            Iterator<?> iter = (Iterator) obj;
            while (iter.hasNext()) {
                Object o = iter.next();
                if (equal(o, element)) {
                    return true;
                }
            }
            return false;
        }
        if (obj instanceof Enumeration) {
            Enumeration<?> enumeration = (Enumeration) obj;
            while (enumeration.hasMoreElements()) {
                Object o2 = enumeration.nextElement();
                if (equal(o2, element)) {
                    return true;
                }
            }
            return false;
        }
        if (obj.getClass().isArray()) {
            int len = Array.getLength(obj);
            for (int i = 0; i < len; i++) {
                Object o3 = Array.get(obj, i);
                if (equal(o3, element)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public static boolean isNull(Object obj) {
        return null == obj || obj.equals(null);
    }

    public static boolean isNotNull(Object obj) {
        return null != obj && false == obj.equals(null);
    }

    public static boolean isEmpty(Object obj) {
        if (null == obj) {
            return true;
        }
        if (obj instanceof CharSequence) {
            return StrUtil.isEmpty((CharSequence) obj);
        }
        if (obj instanceof Map) {
            return MapUtil.isEmpty((Map) obj);
        }
        if (obj instanceof Iterable) {
            return IterUtil.isEmpty((Iterable<?>) obj);
        }
        if (obj instanceof Iterator) {
            return IterUtil.isEmpty((Iterator<?>) obj);
        }
        if (ArrayUtil.isArray(obj)) {
            return ArrayUtil.isEmpty(obj);
        }
        return false;
    }

    public static boolean isNotEmpty(Object obj) {
        return false == isEmpty(obj);
    }

    public static <T> T defaultIfNull(T object, T defaultValue) {
        return isNull(object) ? defaultValue : object;
    }

    public static <T> T defaultIfNull(T source, Supplier<? extends T> defaultValueSupplier) {
        if (isNull(source)) {
            return defaultValueSupplier.get();
        }
        return source;
    }

    public static <T> T defaultIfNull(T source, Function<T, ? extends T> defaultValueSupplier) {
        if (isNull(source)) {
            return defaultValueSupplier.apply(null);
        }
        return source;
    }

    @Deprecated
    public static <T> T defaultIfNull(Object source, Supplier<? extends T> handle, T defaultValue) {
        if (isNotNull(source)) {
            return handle.get();
        }
        return defaultValue;
    }

    public static <T, R> T defaultIfNull(R source, Function<R, ? extends T> handle, T defaultValue) {
        if (isNotNull(source)) {
            return handle.apply(source);
        }
        return defaultValue;
    }

    @Deprecated
    public static <T> T defaultIfEmpty(String str, Supplier<? extends T> handle, T defaultValue) {
        if (StrUtil.isNotEmpty(str)) {
            return handle.get();
        }
        return defaultValue;
    }

    public static <T> T defaultIfEmpty(String str, Function<CharSequence, ? extends T> handle, T defaultValue) {
        if (StrUtil.isNotEmpty(str)) {
            return handle.apply(str);
        }
        return defaultValue;
    }

    public static <T extends CharSequence> T defaultIfEmpty(T str, T defaultValue) {
        return StrUtil.isEmpty(str) ? defaultValue : str;
    }

    public static <T extends CharSequence> T defaultIfEmpty(T str, Supplier<? extends T> defaultValueSupplier) {
        if (StrUtil.isEmpty(str)) {
            return defaultValueSupplier.get();
        }
        return str;
    }

    public static <T extends CharSequence> T defaultIfEmpty(T str, Function<T, ? extends T> defaultValueSupplier) {
        if (StrUtil.isEmpty(str)) {
            return defaultValueSupplier.apply(null);
        }
        return str;
    }

    public static <T extends CharSequence> T defaultIfBlank(T str, T defaultValue) {
        return StrUtil.isBlank(str) ? defaultValue : str;
    }

    public static <T extends CharSequence> T defaultIfBlank(T str, Supplier<? extends T> defaultValueSupplier) {
        if (StrUtil.isBlank(str)) {
            return defaultValueSupplier.get();
        }
        return str;
    }

    public static <T extends CharSequence> T defaultIfBlank(T str, Function<T, ? extends T> defaultValueSupplier) {
        if (StrUtil.isBlank(str)) {
            return defaultValueSupplier.apply(null);
        }
        return str;
    }

    public static <T> T clone(T t) {
        Object clone = ArrayUtil.clone(t);
        if (null == clone) {
            if (t instanceof Cloneable) {
                clone = ReflectUtil.invoke(t, "clone", new Object[0]);
            } else {
                clone = cloneByStream(t);
            }
        }
        return (T) clone;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> T cloneIfPossible(T obj) {
        T clone = null;
        try {
            clone = clone(obj);
        } catch (Exception e) {
        }
        return clone == null ? obj : clone;
    }

    public static <T> T cloneByStream(T t) {
        return (T) SerializeUtil.clone(t);
    }

    public static <T> byte[] serialize(T obj) {
        return SerializeUtil.serialize(obj);
    }

    public static <T> T deserialize(byte[] bArr) {
        return (T) SerializeUtil.deserialize(bArr);
    }

    public static boolean isBasicType(Object object) {
        if (null == object) {
            return false;
        }
        return ClassUtil.isBasicType(object.getClass());
    }

    public static boolean isValidIfNumber(Object obj) {
        if (obj instanceof Number) {
            return NumberUtil.isValidNumber((Number) obj);
        }
        return true;
    }

    public static <T extends Comparable<? super T>> int compare(T c1, T c2) {
        return CompareUtil.compare(c1, c2);
    }

    public static <T extends Comparable<? super T>> int compare(T c1, T c2, boolean nullGreater) {
        return CompareUtil.compare((Comparable) c1, (Comparable) c2, nullGreater);
    }

    public static Class<?> getTypeArgument(Object obj) {
        return getTypeArgument(obj, 0);
    }

    public static Class<?> getTypeArgument(Object obj, int index) {
        return ClassUtil.getTypeArgument(obj.getClass(), index);
    }

    public static String toString(Object obj) {
        if (null == obj) {
            return "null";
        }
        if (obj instanceof Map) {
            return obj.toString();
        }
        return Convert.toStr(obj);
    }

    public static int emptyCount(Object... objs) {
        return ArrayUtil.emptyCount(objs);
    }

    public static boolean hasNull(Object... objs) {
        return ArrayUtil.hasNull(objs);
    }

    public static boolean hasEmpty(Object... objs) {
        return ArrayUtil.hasEmpty(objs);
    }

    public static boolean isAllEmpty(Object... objs) {
        return ArrayUtil.isAllEmpty(objs);
    }

    public static boolean isAllNotEmpty(Object... objs) {
        return ArrayUtil.isAllNotEmpty(objs);
    }
}
