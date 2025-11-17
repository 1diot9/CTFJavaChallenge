package org.springframework.util;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.TimeZone;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ObjectUtils.class */
public abstract class ObjectUtils {
    private static final String EMPTY_STRING = "";
    private static final String NULL_STRING = "null";
    private static final String ARRAY_START = "{";
    private static final String ARRAY_END = "}";
    private static final String EMPTY_ARRAY = "{}";
    private static final String ARRAY_ELEMENT_SEPARATOR = ", ";
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final String NON_EMPTY_ARRAY = "{...}";
    private static final String COLLECTION = "[...]";
    private static final String MAP = "{...}";

    public static boolean isCheckedException(Throwable ex) {
        return ((ex instanceof RuntimeException) || (ex instanceof Error)) ? false : true;
    }

    public static boolean isCompatibleWithThrowsClause(Throwable ex, @Nullable Class<?>... declaredExceptions) {
        if (!isCheckedException(ex)) {
            return true;
        }
        if (declaredExceptions != null) {
            for (Class<?> declaredException : declaredExceptions) {
                if (declaredException.isInstance(ex)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public static boolean isArray(@Nullable Object obj) {
        return obj != null && obj.getClass().isArray();
    }

    public static boolean isEmpty(@Nullable Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(@Nullable Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof Optional) {
            Optional<?> optional = (Optional) obj;
            return optional.isEmpty();
        }
        if (obj instanceof CharSequence) {
            CharSequence charSequence = (CharSequence) obj;
            return charSequence.isEmpty();
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        if (obj instanceof Collection) {
            Collection<?> collection = (Collection) obj;
            return collection.isEmpty();
        }
        if (obj instanceof Map) {
            Map<?, ?> map = (Map) obj;
            return map.isEmpty();
        }
        return false;
    }

    @Nullable
    public static Object unwrapOptional(@Nullable Object obj) {
        if (obj instanceof Optional) {
            Optional<?> optional = (Optional) obj;
            if (optional.isEmpty()) {
                return null;
            }
            Object result = optional.get();
            Assert.isTrue(!(result instanceof Optional), "Multi-level Optional usage not supported");
            return result;
        }
        return obj;
    }

    public static boolean containsElement(@Nullable Object[] array, Object element) {
        if (array == null) {
            return false;
        }
        for (Object arrayEle : array) {
            if (nullSafeEquals(arrayEle, element)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsConstant(Enum<?>[] enumValues, String constant) {
        return containsConstant(enumValues, constant, false);
    }

    public static boolean containsConstant(Enum<?>[] enumValues, String constant, boolean caseSensitive) {
        for (Enum<?> candidate : enumValues) {
            if (caseSensitive) {
                if (candidate.toString().equals(constant)) {
                    return true;
                }
            } else if (candidate.toString().equalsIgnoreCase(constant)) {
                return true;
            }
        }
        return false;
    }

    public static <E extends Enum<?>> E caseInsensitiveValueOf(E[] enumValues, String constant) {
        for (E candidate : enumValues) {
            if (candidate.toString().equalsIgnoreCase(constant)) {
                return candidate;
            }
        }
        throw new IllegalArgumentException("Constant [" + constant + "] does not exist in enum type " + enumValues.getClass().componentType().getName());
    }

    public static <A, O extends A> A[] addObjectToArray(@Nullable A[] aArr, @Nullable O o) {
        return (A[]) addObjectToArray(aArr, o, aArr != null ? aArr.length : 0);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <A, O extends A> A[] addObjectToArray(@Nullable A[] aArr, @Nullable O o, int i) {
        Class<?> cls = Object.class;
        if (aArr != null) {
            cls = aArr.getClass().componentType();
        } else if (o != null) {
            cls = o.getClass();
        }
        A[] aArr2 = (A[]) ((Object[]) Array.newInstance(cls, aArr != null ? aArr.length + 1 : 1));
        if (aArr != null) {
            System.arraycopy(aArr, 0, aArr2, 0, i);
            System.arraycopy(aArr, i, aArr2, i + 1, aArr.length - i);
        }
        aArr2[i] = o;
        return aArr2;
    }

    public static Object[] toObjectArray(@Nullable Object source) {
        if (source instanceof Object[]) {
            Object[] objects = (Object[]) source;
            return objects;
        }
        if (source == null) {
            return EMPTY_OBJECT_ARRAY;
        }
        if (!source.getClass().isArray()) {
            throw new IllegalArgumentException("Source is not an array: " + source);
        }
        int length = Array.getLength(source);
        if (length == 0) {
            return EMPTY_OBJECT_ARRAY;
        }
        Class<?> wrapperType = Array.get(source, 0).getClass();
        Object[] newArray = (Object[]) Array.newInstance(wrapperType, length);
        for (int i = 0; i < length; i++) {
            newArray[i] = Array.get(source, i);
        }
        return newArray;
    }

    public static boolean nullSafeEquals(@Nullable Object o1, @Nullable Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (o1.equals(o2)) {
            return true;
        }
        if (o1.getClass().isArray() && o2.getClass().isArray()) {
            return arrayEquals(o1, o2);
        }
        return false;
    }

    private static boolean arrayEquals(Object o1, Object o2) {
        if (o1 instanceof Object[]) {
            Object[] objects1 = (Object[]) o1;
            if (o2 instanceof Object[]) {
                Object[] objects2 = (Object[]) o2;
                return Arrays.equals(objects1, objects2);
            }
        }
        if (o1 instanceof boolean[]) {
            boolean[] booleans1 = (boolean[]) o1;
            if (o2 instanceof boolean[]) {
                boolean[] booleans2 = (boolean[]) o2;
                return Arrays.equals(booleans1, booleans2);
            }
        }
        if (o1 instanceof byte[]) {
            byte[] bytes1 = (byte[]) o1;
            if (o2 instanceof byte[]) {
                byte[] bytes2 = (byte[]) o2;
                return Arrays.equals(bytes1, bytes2);
            }
        }
        if (o1 instanceof char[]) {
            char[] chars1 = (char[]) o1;
            if (o2 instanceof char[]) {
                char[] chars2 = (char[]) o2;
                return Arrays.equals(chars1, chars2);
            }
        }
        if (o1 instanceof double[]) {
            double[] doubles1 = (double[]) o1;
            if (o2 instanceof double[]) {
                double[] doubles2 = (double[]) o2;
                return Arrays.equals(doubles1, doubles2);
            }
        }
        if (o1 instanceof float[]) {
            float[] floats1 = (float[]) o1;
            if (o2 instanceof float[]) {
                float[] floats2 = (float[]) o2;
                return Arrays.equals(floats1, floats2);
            }
        }
        if (o1 instanceof int[]) {
            int[] ints1 = (int[]) o1;
            if (o2 instanceof int[]) {
                int[] ints2 = (int[]) o2;
                return Arrays.equals(ints1, ints2);
            }
        }
        if (o1 instanceof long[]) {
            long[] longs1 = (long[]) o1;
            if (o2 instanceof long[]) {
                long[] longs2 = (long[]) o2;
                return Arrays.equals(longs1, longs2);
            }
        }
        if (!(o1 instanceof short[])) {
            return false;
        }
        short[] shorts1 = (short[]) o1;
        if (o2 instanceof short[]) {
            short[] shorts2 = (short[]) o2;
            return Arrays.equals(shorts1, shorts2);
        }
        return false;
    }

    public static int nullSafeHash(@Nullable Object... elements) {
        if (elements == null) {
            return 0;
        }
        int result = 1;
        for (Object element : elements) {
            result = (31 * result) + nullSafeHashCode(element);
        }
        return result;
    }

    public static int nullSafeHashCode(@Nullable Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj.getClass().isArray()) {
            if (obj instanceof Object[]) {
                Object[] objects = (Object[]) obj;
                return Arrays.hashCode(objects);
            }
            if (obj instanceof boolean[]) {
                boolean[] booleans = (boolean[]) obj;
                return Arrays.hashCode(booleans);
            }
            if (obj instanceof byte[]) {
                byte[] bytes = (byte[]) obj;
                return Arrays.hashCode(bytes);
            }
            if (obj instanceof char[]) {
                char[] chars = (char[]) obj;
                return Arrays.hashCode(chars);
            }
            if (obj instanceof double[]) {
                double[] doubles = (double[]) obj;
                return Arrays.hashCode(doubles);
            }
            if (obj instanceof float[]) {
                float[] floats = (float[]) obj;
                return Arrays.hashCode(floats);
            }
            if (obj instanceof int[]) {
                int[] ints = (int[]) obj;
                return Arrays.hashCode(ints);
            }
            if (obj instanceof long[]) {
                long[] longs = (long[]) obj;
                return Arrays.hashCode(longs);
            }
            if (obj instanceof short[]) {
                short[] shorts = (short[]) obj;
                return Arrays.hashCode(shorts);
            }
        }
        return obj.hashCode();
    }

    @Deprecated(since = "6.1")
    public static int nullSafeHashCode(@Nullable Object[] array) {
        return Arrays.hashCode(array);
    }

    @Deprecated(since = "6.1")
    public static int nullSafeHashCode(@Nullable boolean[] array) {
        return Arrays.hashCode(array);
    }

    @Deprecated(since = "6.1")
    public static int nullSafeHashCode(@Nullable byte[] array) {
        return Arrays.hashCode(array);
    }

    @Deprecated(since = "6.1")
    public static int nullSafeHashCode(@Nullable char[] array) {
        return Arrays.hashCode(array);
    }

    @Deprecated(since = "6.1")
    public static int nullSafeHashCode(@Nullable double[] array) {
        return Arrays.hashCode(array);
    }

    @Deprecated(since = "6.1")
    public static int nullSafeHashCode(@Nullable float[] array) {
        return Arrays.hashCode(array);
    }

    @Deprecated(since = "6.1")
    public static int nullSafeHashCode(@Nullable int[] array) {
        return Arrays.hashCode(array);
    }

    @Deprecated(since = "6.1")
    public static int nullSafeHashCode(@Nullable long[] array) {
        return Arrays.hashCode(array);
    }

    @Deprecated(since = "6.1")
    public static int nullSafeHashCode(@Nullable short[] array) {
        return Arrays.hashCode(array);
    }

    public static String identityToString(@Nullable Object obj) {
        if (obj == null) {
            return "";
        }
        return obj.getClass().getName() + "@" + getIdentityHexString(obj);
    }

    public static String getIdentityHexString(Object obj) {
        return Integer.toHexString(System.identityHashCode(obj));
    }

    public static String getDisplayString(@Nullable Object obj) {
        if (obj == null) {
            return "";
        }
        return nullSafeToString(obj);
    }

    public static String nullSafeClassName(@Nullable Object obj) {
        return obj != null ? obj.getClass().getName() : "null";
    }

    public static String nullSafeToString(@Nullable Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof String) {
            String string = (String) obj;
            return string;
        }
        if (obj instanceof Object[]) {
            Object[] objects = (Object[]) obj;
            return nullSafeToString(objects);
        }
        if (obj instanceof boolean[]) {
            boolean[] booleans = (boolean[]) obj;
            return nullSafeToString(booleans);
        }
        if (obj instanceof byte[]) {
            byte[] bytes = (byte[]) obj;
            return nullSafeToString(bytes);
        }
        if (obj instanceof char[]) {
            char[] chars = (char[]) obj;
            return nullSafeToString(chars);
        }
        if (obj instanceof double[]) {
            double[] doubles = (double[]) obj;
            return nullSafeToString(doubles);
        }
        if (obj instanceof float[]) {
            float[] floats = (float[]) obj;
            return nullSafeToString(floats);
        }
        if (obj instanceof int[]) {
            int[] ints = (int[]) obj;
            return nullSafeToString(ints);
        }
        if (obj instanceof long[]) {
            long[] longs = (long[]) obj;
            return nullSafeToString(longs);
        }
        if (obj instanceof short[]) {
            short[] shorts = (short[]) obj;
            return nullSafeToString(shorts);
        }
        String str = obj.toString();
        return str != null ? str : "";
    }

    public static String nullSafeToString(@Nullable Object[] array) {
        if (array == null) {
            return "null";
        }
        int length = array.length;
        if (length == 0) {
            return "{}";
        }
        StringJoiner stringJoiner = new StringJoiner(ARRAY_ELEMENT_SEPARATOR, "{", "}");
        for (Object o : array) {
            stringJoiner.add(String.valueOf(o));
        }
        return stringJoiner.toString();
    }

    public static String nullSafeToString(@Nullable boolean[] array) {
        if (array == null) {
            return "null";
        }
        int length = array.length;
        if (length == 0) {
            return "{}";
        }
        StringJoiner stringJoiner = new StringJoiner(ARRAY_ELEMENT_SEPARATOR, "{", "}");
        for (boolean b : array) {
            stringJoiner.add(String.valueOf(b));
        }
        return stringJoiner.toString();
    }

    public static String nullSafeToString(@Nullable byte[] array) {
        if (array == null) {
            return "null";
        }
        int length = array.length;
        if (length == 0) {
            return "{}";
        }
        StringJoiner stringJoiner = new StringJoiner(ARRAY_ELEMENT_SEPARATOR, "{", "}");
        for (byte b : array) {
            stringJoiner.add(String.valueOf((int) b));
        }
        return stringJoiner.toString();
    }

    public static String nullSafeToString(@Nullable char[] array) {
        if (array == null) {
            return "null";
        }
        int length = array.length;
        if (length == 0) {
            return "{}";
        }
        StringJoiner stringJoiner = new StringJoiner(ARRAY_ELEMENT_SEPARATOR, "{", "}");
        for (char c : array) {
            stringJoiner.add("'" + String.valueOf(c) + "'");
        }
        return stringJoiner.toString();
    }

    public static String nullSafeToString(@Nullable double[] array) {
        if (array == null) {
            return "null";
        }
        int length = array.length;
        if (length == 0) {
            return "{}";
        }
        StringJoiner stringJoiner = new StringJoiner(ARRAY_ELEMENT_SEPARATOR, "{", "}");
        for (double d : array) {
            stringJoiner.add(String.valueOf(d));
        }
        return stringJoiner.toString();
    }

    public static String nullSafeToString(@Nullable float[] array) {
        if (array == null) {
            return "null";
        }
        int length = array.length;
        if (length == 0) {
            return "{}";
        }
        StringJoiner stringJoiner = new StringJoiner(ARRAY_ELEMENT_SEPARATOR, "{", "}");
        for (float f : array) {
            stringJoiner.add(String.valueOf(f));
        }
        return stringJoiner.toString();
    }

    public static String nullSafeToString(@Nullable int[] array) {
        if (array == null) {
            return "null";
        }
        int length = array.length;
        if (length == 0) {
            return "{}";
        }
        StringJoiner stringJoiner = new StringJoiner(ARRAY_ELEMENT_SEPARATOR, "{", "}");
        for (int i : array) {
            stringJoiner.add(String.valueOf(i));
        }
        return stringJoiner.toString();
    }

    public static String nullSafeToString(@Nullable long[] array) {
        if (array == null) {
            return "null";
        }
        int length = array.length;
        if (length == 0) {
            return "{}";
        }
        StringJoiner stringJoiner = new StringJoiner(ARRAY_ELEMENT_SEPARATOR, "{", "}");
        for (long l : array) {
            stringJoiner.add(String.valueOf(l));
        }
        return stringJoiner.toString();
    }

    public static String nullSafeToString(@Nullable short[] array) {
        if (array == null) {
            return "null";
        }
        int length = array.length;
        if (length == 0) {
            return "{}";
        }
        StringJoiner stringJoiner = new StringJoiner(ARRAY_ELEMENT_SEPARATOR, "{", "}");
        for (short s : array) {
            stringJoiner.add(String.valueOf((int) s));
        }
        return stringJoiner.toString();
    }

    public static String nullSafeConciseToString(@Nullable Object obj) {
        String str;
        if (obj == null) {
            return "null";
        }
        if (obj instanceof Optional) {
            Optional<?> optional = (Optional) obj;
            return optional.isEmpty() ? "Optional.empty" : "Optional[%s]".formatted(nullSafeConciseToString(optional.get()));
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0 ? "{}" : "{...}";
        }
        if (obj instanceof Collection) {
            return COLLECTION;
        }
        if (obj instanceof Map) {
            return "{...}";
        }
        if (obj instanceof Class) {
            Class<?> clazz = (Class) obj;
            return clazz.getName();
        }
        if (obj instanceof Charset) {
            Charset charset = (Charset) obj;
            return charset.name();
        }
        if (obj instanceof TimeZone) {
            TimeZone timeZone = (TimeZone) obj;
            return timeZone.getID();
        }
        if (obj instanceof ZoneId) {
            ZoneId zoneId = (ZoneId) obj;
            return zoneId.getId();
        }
        if (obj instanceof CharSequence) {
            CharSequence charSequence = (CharSequence) obj;
            return StringUtils.truncate(charSequence);
        }
        Class<?> type = obj.getClass();
        if (ClassUtils.isSimpleValueType(type) && (str = obj.toString()) != null) {
            return StringUtils.truncate(str);
        }
        return type.getTypeName() + "@" + getIdentityHexString(obj);
    }
}
