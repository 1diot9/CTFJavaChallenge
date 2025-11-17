package cn.hutool.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.UniqueKeySet;
import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Editor;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.Matcher;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrJoiner;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/util/ArrayUtil.class */
public class ArrayUtil extends PrimitiveArrayUtil {
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static <T> T[] defaultIfEmpty(T[] array, T[] defaultArray) {
        return isEmpty((Object[]) array) ? defaultArray : array;
    }

    public static boolean isEmpty(Object array) {
        if (array != null) {
            return isArray(array) && 0 == Array.getLength(array);
        }
        return true;
    }

    public static <T> boolean isNotEmpty(T[] array) {
        return (null == array || array.length == 0) ? false : true;
    }

    public static boolean isNotEmpty(Object array) {
        return false == isEmpty(array);
    }

    public static <T> boolean hasNull(T... array) {
        if (isNotEmpty((Object[]) array)) {
            for (T element : array) {
                if (ObjectUtil.isNull(element)) {
                    return true;
                }
            }
        }
        return array == null;
    }

    public static <T> boolean isAllNull(T... array) {
        return null == firstNonNull(array);
    }

    public static <T> T firstNonNull(T... tArr) {
        return (T) firstMatch(ObjectUtil::isNotNull, tArr);
    }

    public static <T> T firstMatch(Matcher<T> matcher, T... array) {
        int index = matchIndex(matcher, array);
        if (index < 0) {
            return null;
        }
        return array[index];
    }

    public static <T> int matchIndex(Matcher<T> matcher, T... array) {
        return matchIndex(matcher, 0, array);
    }

    public static <T> int matchIndex(Matcher<T> matcher, int beginIndexInclude, T... array) {
        Assert.notNull(matcher, "Matcher must be not null !", new Object[0]);
        if (isNotEmpty((Object[]) array)) {
            for (int i = beginIndexInclude; i < array.length; i++) {
                if (matcher.match(array[i])) {
                    return i;
                }
            }
            return -1;
        }
        return -1;
    }

    public static <T> T[] newArray(Class<?> cls, int i) {
        return (T[]) ((Object[]) Array.newInstance(cls, i));
    }

    public static Object[] newArray(int newSize) {
        return new Object[newSize];
    }

    public static Class<?> getComponentType(Object array) {
        if (null == array) {
            return null;
        }
        return array.getClass().getComponentType();
    }

    public static Class<?> getComponentType(Class<?> arrayClass) {
        if (null == arrayClass) {
            return null;
        }
        return arrayClass.getComponentType();
    }

    public static Class<?> getArrayType(Class<?> componentType) {
        return Array.newInstance(componentType, 0).getClass();
    }

    public static Object[] cast(Class<?> type, Object arrayObj) throws NullPointerException, IllegalArgumentException {
        if (null == arrayObj) {
            throw new NullPointerException("Argument [arrayObj] is null !");
        }
        if (false == arrayObj.getClass().isArray()) {
            throw new IllegalArgumentException("Argument [arrayObj] is not array !");
        }
        if (null == type) {
            return (Object[]) arrayObj;
        }
        Class<?> componentType = type.isArray() ? type.getComponentType() : type;
        Object[] array = (Object[]) arrayObj;
        Object[] result = newArray(componentType, array.length);
        System.arraycopy(array, 0, result, 0, array.length);
        return result;
    }

    @SafeVarargs
    public static <T> T[] append(T[] tArr, T... tArr2) {
        if (isEmpty((Object[]) tArr)) {
            return tArr2;
        }
        return (T[]) insert((Object[]) tArr, tArr.length, (Object[]) tArr2);
    }

    @SafeVarargs
    public static <T> Object append(Object array, T... newElements) {
        if (isEmpty(array)) {
            return newElements;
        }
        return insert(array, length(array), newElements);
    }

    public static <T> T[] setOrAppend(T[] tArr, int i, T t) {
        if (i < tArr.length) {
            Array.set(tArr, i, t);
            return tArr;
        }
        if (isEmpty((Object[]) tArr)) {
            Object[] newArray = newArray(t.getClass(), 1);
            newArray[0] = t;
            return (T[]) append((Object[]) tArr, newArray);
        }
        return (T[]) append((Object[]) tArr, t);
    }

    public static Object setOrAppend(Object array, int index, Object value) {
        if (index < length(array)) {
            Array.set(array, index, value);
            return array;
        }
        return append(array, value);
    }

    public static <T> T[] replace(T[] tArr, int i, T... tArr2) {
        if (isEmpty((Object[]) tArr2)) {
            return tArr;
        }
        if (isEmpty((Object[]) tArr)) {
            return tArr2;
        }
        if (i < 0) {
            return (T[]) insert((Object[]) tArr, 0, (Object[]) tArr2);
        }
        if (i >= tArr.length) {
            return (T[]) append((Object[]) tArr, (Object[]) tArr2);
        }
        if (tArr.length >= tArr2.length + i) {
            System.arraycopy(tArr2, 0, tArr, i, tArr2.length);
            return tArr;
        }
        T[] tArr3 = (T[]) newArray(tArr.getClass().getComponentType(), i + tArr2.length);
        System.arraycopy(tArr, 0, tArr3, 0, i);
        System.arraycopy(tArr2, 0, tArr3, i, tArr2.length);
        return tArr3;
    }

    public static <T> T[] insert(T[] tArr, int i, T... tArr2) {
        return (T[]) ((Object[]) insert((Object) tArr, i, (Object[]) tArr2));
    }

    public static <T> Object insert(Object array, int index, T... newElements) {
        if (isEmpty((Object[]) newElements)) {
            return array;
        }
        if (isEmpty(array)) {
            return newElements;
        }
        int len = length(array);
        if (index < 0) {
            index = (index % len) + len;
        }
        Class<?> originComponentType = array.getClass().getComponentType();
        Object newEleArr = newElements;
        if (originComponentType.isPrimitive()) {
            newEleArr = Convert.convert((Class<Object>) array.getClass(), (Object) newElements);
        }
        Object result = Array.newInstance(originComponentType, Math.max(len, index) + newElements.length);
        System.arraycopy(array, 0, result, 0, Math.min(len, index));
        System.arraycopy(newEleArr, 0, result, index, newElements.length);
        if (index < len) {
            System.arraycopy(array, index, result, index + newElements.length, len - index);
        }
        return result;
    }

    public static <T> T[] resize(T[] tArr, int i, Class<?> cls) {
        if (i < 0) {
            return tArr;
        }
        T[] tArr2 = (T[]) newArray(cls, i);
        if (i > 0 && isNotEmpty((Object[]) tArr)) {
            System.arraycopy(tArr, 0, tArr2, 0, Math.min(tArr.length, i));
        }
        return tArr2;
    }

    public static Object resize(Object array, int newSize) {
        if (newSize < 0) {
            return array;
        }
        if (null == array) {
            return null;
        }
        int length = length(array);
        Object newArray = Array.newInstance(array.getClass().getComponentType(), newSize);
        if (newSize > 0 && isNotEmpty(array)) {
            System.arraycopy(array, 0, newArray, 0, Math.min(length, newSize));
        }
        return newArray;
    }

    public static <T> T[] resize(T[] tArr, int i) {
        return (T[]) resize(tArr, i, tArr.getClass().getComponentType());
    }

    @SafeVarargs
    public static <T> T[] addAll(T[]... tArr) {
        if (tArr.length == 1) {
            return tArr[0];
        }
        int i = 0;
        for (T[] tArr2 : tArr) {
            if (isNotEmpty((Object[]) tArr2)) {
                i += tArr2.length;
            }
        }
        T[] tArr3 = (T[]) newArray(tArr.getClass().getComponentType().getComponentType(), i);
        int i2 = 0;
        for (T[] tArr4 : tArr) {
            if (isNotEmpty((Object[]) tArr4)) {
                System.arraycopy(tArr4, 0, tArr3, i2, tArr4.length);
                i2 += tArr4.length;
            }
        }
        return tArr3;
    }

    public static Object copy(Object src, int srcPos, Object dest, int destPos, int length) {
        System.arraycopy(src, srcPos, dest, destPos, length);
        return dest;
    }

    public static Object copy(Object src, Object dest, int length) {
        System.arraycopy(src, 0, dest, 0, length);
        return dest;
    }

    public static <T> T[] clone(T[] tArr) {
        if (tArr == null) {
            return null;
        }
        return (T[]) ((Object[]) tArr.clone());
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> T clone(T t) {
        Object clone;
        if (0 != t && isArray(t)) {
            Class<?> componentType = t.getClass().getComponentType();
            if (componentType.isPrimitive()) {
                int length = Array.getLength(t);
                clone = Array.newInstance(componentType, length);
                while (true) {
                    int i = length;
                    length--;
                    if (i <= 0) {
                        break;
                    }
                    Array.set(clone, length, Array.get(t, length));
                }
            } else {
                clone = ((Object[]) t).clone();
            }
            return (T) clone;
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> T[] edit(T[] tArr, Editor<T> editor) {
        if (null == editor) {
            return tArr;
        }
        ArrayList arrayList = new ArrayList(tArr.length);
        for (T t : tArr) {
            T edit = editor.edit(t);
            if (null != edit) {
                arrayList.add(edit);
            }
        }
        return (T[]) arrayList.toArray(newArray(tArr.getClass().getComponentType(), arrayList.size()));
    }

    public static <T> T[] filter(T[] tArr, Filter<T> filter) {
        if (null == tArr || null == filter) {
            return tArr;
        }
        return (T[]) edit(tArr, t -> {
            if (filter.accept(t)) {
                return t;
            }
            return null;
        });
    }

    public static <T> T[] removeNull(T[] tArr) {
        return (T[]) edit(tArr, t -> {
            return t;
        });
    }

    public static <T extends CharSequence> T[] removeEmpty(T[] tArr) {
        return (T[]) ((CharSequence[]) filter(tArr, CharSequenceUtil::isNotEmpty));
    }

    public static <T extends CharSequence> T[] removeBlank(T[] tArr) {
        return (T[]) ((CharSequence[]) filter(tArr, CharSequenceUtil::isNotBlank));
    }

    public static String[] nullToEmpty(String[] array) {
        return (String[]) edit(array, t -> {
            return null == t ? "" : t;
        });
    }

    public static <K, V> Map<K, V> zip(K[] keys, V[] values, boolean isOrder) {
        if (isEmpty((Object[]) keys) || isEmpty((Object[]) values)) {
            return null;
        }
        int size = Math.min(keys.length, values.length);
        Map<K, V> map = MapUtil.newHashMap(size, isOrder);
        for (int i = 0; i < size; i++) {
            map.put(keys[i], values[i]);
        }
        return map;
    }

    public static <K, V> Map<K, V> zip(K[] keys, V[] values) {
        return zip(keys, values, false);
    }

    public static <T> int indexOf(T[] array, Object value, int beginIndexInclude) {
        return matchIndex(obj -> {
            return ObjectUtil.equal(value, obj);
        }, beginIndexInclude, array);
    }

    public static <T> int indexOf(T[] array, Object value) {
        return matchIndex(obj -> {
            return ObjectUtil.equal(value, obj);
        }, array);
    }

    public static int indexOfIgnoreCase(CharSequence[] array, CharSequence value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (StrUtil.equalsIgnoreCase(array[i], value)) {
                    return i;
                }
            }
            return -1;
        }
        return -1;
    }

    public static <T> int lastIndexOf(T[] array, Object value) {
        if (isEmpty((Object[]) array)) {
            return -1;
        }
        return lastIndexOf(array, value, array.length - 1);
    }

    public static <T> int lastIndexOf(T[] array, Object value, int endInclude) {
        if (isNotEmpty((Object[]) array)) {
            for (int i = endInclude; i >= 0; i--) {
                if (ObjectUtil.equal(value, array[i])) {
                    return i;
                }
            }
            return -1;
        }
        return -1;
    }

    public static <T> boolean contains(T[] array, T value) {
        return indexOf(array, value) > -1;
    }

    public static <T> boolean containsAny(T[] array, T... values) {
        for (T value : values) {
            if (contains(array, value)) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean containsAll(T[] array, T... values) {
        for (T value : values) {
            if (false == contains(array, value)) {
                return false;
            }
        }
        return true;
    }

    public static boolean containsIgnoreCase(CharSequence[] array, CharSequence value) {
        return indexOfIgnoreCase(array, value) > -1;
    }

    public static Object[] wrap(Object obj) {
        if (null == obj) {
            return null;
        }
        if (isArray(obj)) {
            try {
                return (Object[]) obj;
            } catch (Exception e) {
                String className = obj.getClass().getComponentType().getName();
                boolean z = -1;
                switch (className.hashCode()) {
                    case -1325958191:
                        if (className.equals("double")) {
                            z = 7;
                            break;
                        }
                        break;
                    case 104431:
                        if (className.equals("int")) {
                            z = true;
                            break;
                        }
                        break;
                    case 3039496:
                        if (className.equals("byte")) {
                            z = 4;
                            break;
                        }
                        break;
                    case 3052374:
                        if (className.equals("char")) {
                            z = 3;
                            break;
                        }
                        break;
                    case 3327612:
                        if (className.equals("long")) {
                            z = false;
                            break;
                        }
                        break;
                    case 64711720:
                        if (className.equals("boolean")) {
                            z = 5;
                            break;
                        }
                        break;
                    case 97526364:
                        if (className.equals("float")) {
                            z = 6;
                            break;
                        }
                        break;
                    case 109413500:
                        if (className.equals("short")) {
                            z = 2;
                            break;
                        }
                        break;
                }
                switch (z) {
                    case false:
                        return wrap((long[]) obj);
                    case true:
                        return wrap((int[]) obj);
                    case true:
                        return wrap((short[]) obj);
                    case true:
                        return wrap((char[]) obj);
                    case true:
                        return wrap((byte[]) obj);
                    case true:
                        return wrap((boolean[]) obj);
                    case true:
                        return wrap((float[]) obj);
                    case true:
                        return wrap((double[]) obj);
                    default:
                        throw new UtilException(e);
                }
            }
        }
        throw new UtilException(StrUtil.format("[{}] is not Array!", obj.getClass()));
    }

    public static boolean isArray(Object obj) {
        return null != obj && obj.getClass().isArray();
    }

    public static <T> T get(Object obj, int i) {
        if (null == obj) {
            return null;
        }
        if (i < 0) {
            i += Array.getLength(obj);
        }
        try {
            return (T) Array.get(obj, i);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> T[] getAny(Object obj, int... iArr) {
        if (null == obj) {
            return null;
        }
        if (null == iArr) {
            return (T[]) newArray(obj.getClass().getComponentType(), 0);
        }
        T[] tArr = (T[]) newArray(obj.getClass().getComponentType(), iArr.length);
        for (int i = 0; i < iArr.length; i++) {
            tArr[i] = get(obj, iArr[i]);
        }
        return tArr;
    }

    public static <T> T[] sub(T[] tArr, int i, int i2) {
        int length = length(tArr);
        if (i < 0) {
            i += length;
        }
        if (i2 < 0) {
            i2 += length;
        }
        if (i == length) {
            return (T[]) newArray(tArr.getClass().getComponentType(), 0);
        }
        if (i > i2) {
            int i3 = i;
            i = i2;
            i2 = i3;
        }
        if (i2 > length) {
            if (i >= length) {
                return (T[]) newArray(tArr.getClass().getComponentType(), 0);
            }
            i2 = length;
        }
        return (T[]) Arrays.copyOfRange(tArr, i, i2);
    }

    public static Object[] sub(Object array, int start, int end) {
        return sub(array, start, end, 1);
    }

    public static Object[] sub(Object array, int start, int end, int step) {
        int length = length(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new Object[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new Object[0];
            }
            end = length;
        }
        if (step <= 1) {
            step = 1;
        }
        ArrayList<Object> list = new ArrayList<>();
        int i = start;
        while (true) {
            int i2 = i;
            if (i2 < end) {
                list.add(get(array, i2));
                i = i2 + step;
            } else {
                return list.toArray();
            }
        }
    }

    public static String toString(Object obj) {
        if (null == obj) {
            return null;
        }
        if (obj instanceof long[]) {
            return Arrays.toString((long[]) obj);
        }
        if (obj instanceof int[]) {
            return Arrays.toString((int[]) obj);
        }
        if (obj instanceof short[]) {
            return Arrays.toString((short[]) obj);
        }
        if (obj instanceof char[]) {
            return Arrays.toString((char[]) obj);
        }
        if (obj instanceof byte[]) {
            return Arrays.toString((byte[]) obj);
        }
        if (obj instanceof boolean[]) {
            return Arrays.toString((boolean[]) obj);
        }
        if (obj instanceof float[]) {
            return Arrays.toString((float[]) obj);
        }
        if (obj instanceof double[]) {
            return Arrays.toString((double[]) obj);
        }
        if (isArray(obj)) {
            try {
                return Arrays.deepToString((Object[]) obj);
            } catch (Exception e) {
            }
        }
        return obj.toString();
    }

    public static int length(Object array) throws IllegalArgumentException {
        if (null == array) {
            return 0;
        }
        return Array.getLength(array);
    }

    public static <T> String join(T[] array, CharSequence conjunction) {
        return join(array, conjunction, null, null);
    }

    public static <T> String join(T[] array, CharSequence delimiter, String prefix, String suffix) {
        if (null == array) {
            return null;
        }
        return StrJoiner.of(delimiter, prefix, suffix).setWrapElement(true).append((Object[]) array).toString();
    }

    public static <T> String join(T[] array, CharSequence conjunction, Editor<T> editor) {
        return StrJoiner.of(conjunction).append(array, t -> {
            return String.valueOf(editor.edit(t));
        }).toString();
    }

    public static String join(Object array, CharSequence conjunction) {
        if (null == array) {
            return null;
        }
        if (false == isArray(array)) {
            throw new IllegalArgumentException(StrUtil.format("[{}] is not a Array!", array.getClass()));
        }
        return StrJoiner.of(conjunction).append(array).toString();
    }

    public static byte[] toArray(ByteBuffer bytebuffer) {
        if (bytebuffer.hasArray()) {
            return Arrays.copyOfRange(bytebuffer.array(), bytebuffer.position(), bytebuffer.limit());
        }
        int oldPosition = bytebuffer.position();
        bytebuffer.position(0);
        int size = bytebuffer.limit();
        byte[] buffers = new byte[size];
        bytebuffer.get(buffers);
        bytebuffer.position(oldPosition);
        return buffers;
    }

    public static <T> T[] toArray(Iterator<T> it, Class<T> cls) {
        return (T[]) toArray((Collection) CollUtil.newArrayList(it), (Class) cls);
    }

    public static <T> T[] toArray(Iterable<T> iterable, Class<T> cls) {
        return (T[]) toArray(CollectionUtil.toCollection(iterable), (Class) cls);
    }

    public static <T> T[] toArray(Collection<T> collection, Class<T> cls) {
        return (T[]) collection.toArray(newArray(cls, 0));
    }

    public static <T> T[] remove(T[] tArr, int i) throws IllegalArgumentException {
        return (T[]) ((Object[]) remove((Object) tArr, i));
    }

    public static <T> T[] removeEle(T[] tArr, T t) throws IllegalArgumentException {
        return (T[]) remove((Object[]) tArr, indexOf(tArr, t));
    }

    public static <T> T[] reverse(T[] array, int startIndexInclusive, int endIndexExclusive) {
        if (isEmpty((Object[]) array)) {
            return array;
        }
        int j = Math.min(array.length, endIndexExclusive) - 1;
        for (int i = Math.max(startIndexInclusive, 0); j > i; i++) {
            T tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
        }
        return array;
    }

    public static <T> T[] reverse(T[] tArr) {
        return (T[]) reverse(tArr, 0, tArr.length);
    }

    public static <T extends Comparable<? super T>> T min(T[] tArr) {
        return (T) min(tArr, null);
    }

    public static <T extends Comparable<? super T>> T min(T[] numberArray, Comparator<T> comparator) {
        if (isEmpty((Object[]) numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        T min = numberArray[0];
        for (T t : numberArray) {
            if (CompareUtil.compare(min, t, comparator) > 0) {
                min = t;
            }
        }
        return min;
    }

    public static <T extends Comparable<? super T>> T max(T[] tArr) {
        return (T) max(tArr, null);
    }

    public static <T extends Comparable<? super T>> T max(T[] numberArray, Comparator<T> comparator) {
        if (isEmpty((Object[]) numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        T max = numberArray[0];
        for (int i = 1; i < numberArray.length; i++) {
            if (CompareUtil.compare(max, numberArray[i], comparator) < 0) {
                max = numberArray[i];
            }
        }
        return max;
    }

    public static <T> T[] shuffle(T[] tArr) {
        return (T[]) shuffle(tArr, RandomUtil.getRandom());
    }

    public static <T> T[] shuffle(T[] array, Random random) {
        if (array == null || random == null || array.length <= 1) {
            return array;
        }
        for (int i = array.length; i > 1; i--) {
            swap((Object[]) array, i - 1, random.nextInt(i));
        }
        return array;
    }

    public static <T> T[] swap(T[] array, int index1, int index2) {
        if (isEmpty((Object[]) array)) {
            throw new IllegalArgumentException("Array must not empty !");
        }
        T tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
        return array;
    }

    public static Object swap(Object array, int index1, int index2) {
        if (isEmpty(array)) {
            throw new IllegalArgumentException("Array must not empty !");
        }
        Object tmp = get(array, index1);
        Array.set(array, index1, Array.get(array, index2));
        Array.set(array, index2, tmp);
        return array;
    }

    public static int emptyCount(Object... args) {
        int count = 0;
        if (isNotEmpty(args)) {
            for (Object element : args) {
                if (ObjectUtil.isEmpty(element)) {
                    count++;
                }
            }
        }
        return count;
    }

    public static boolean hasEmpty(Object... args) {
        if (isNotEmpty(args)) {
            for (Object element : args) {
                if (ObjectUtil.isEmpty(element)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public static boolean isAllEmpty(Object... args) {
        for (Object obj : args) {
            if (false == ObjectUtil.isEmpty(obj)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllNotEmpty(Object... args) {
        return false == hasEmpty(args);
    }

    public static <T> boolean isAllNotNull(T... array) {
        return false == hasNull(array);
    }

    public static <T> T[] distinct(T[] tArr) {
        if (isEmpty((Object[]) tArr)) {
            return tArr;
        }
        LinkedHashSet linkedHashSet = new LinkedHashSet(tArr.length, 1.0f);
        Collections.addAll(linkedHashSet, tArr);
        return (T[]) toArray((Collection) linkedHashSet, (Class) getComponentType(tArr));
    }

    public static <T, K> T[] distinct(T[] tArr, Function<T, K> function, boolean z) {
        if (isEmpty((Object[]) tArr)) {
            return tArr;
        }
        UniqueKeySet uniqueKeySet = new UniqueKeySet(true, (Function) function);
        if (z) {
            Collections.addAll(uniqueKeySet, tArr);
        } else {
            for (T t : tArr) {
                uniqueKeySet.addIfAbsent(t);
            }
        }
        return (T[]) toArray((Collection) uniqueKeySet, (Class) getComponentType(tArr));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T, R> R[] map(T[] tArr, Class<R> cls, Function<? super T, ? extends R> function) {
        R[] rArr = (R[]) newArray(cls, tArr.length);
        for (int i = 0; i < tArr.length; i++) {
            rArr[i] = function.apply(tArr[i]);
        }
        return rArr;
    }

    public static <T, R> R[] map(Object obj, Class<R> cls, Function<? super T, ? extends R> function) {
        int length = length(obj);
        R[] rArr = (R[]) newArray(cls, length);
        for (int i = 0; i < length; i++) {
            rArr[i] = function.apply((Object) get(obj, i));
        }
        return rArr;
    }

    public static <T, R> List<R> map(T[] array, Function<? super T, ? extends R> func) {
        return (List) Arrays.stream(array).map(func).collect(Collectors.toList());
    }

    public static <T, R> Set<R> mapToSet(T[] array, Function<? super T, ? extends R> func) {
        return (Set) Arrays.stream(array).map(func).collect(Collectors.toSet());
    }

    public static boolean equals(Object array1, Object array2) {
        if (array1 == array2) {
            return true;
        }
        if (hasNull(array1, array2)) {
            return false;
        }
        Assert.isTrue(isArray(array1), "First is not a Array !", new Object[0]);
        Assert.isTrue(isArray(array2), "Second is not a Array !", new Object[0]);
        if (array1 instanceof long[]) {
            return Arrays.equals((long[]) array1, (long[]) array2);
        }
        if (array1 instanceof int[]) {
            return Arrays.equals((int[]) array1, (int[]) array2);
        }
        if (array1 instanceof short[]) {
            return Arrays.equals((short[]) array1, (short[]) array2);
        }
        if (array1 instanceof char[]) {
            return Arrays.equals((char[]) array1, (char[]) array2);
        }
        if (array1 instanceof byte[]) {
            return Arrays.equals((byte[]) array1, (byte[]) array2);
        }
        if (array1 instanceof double[]) {
            return Arrays.equals((double[]) array1, (double[]) array2);
        }
        if (array1 instanceof float[]) {
            return Arrays.equals((float[]) array1, (float[]) array2);
        }
        if (array1 instanceof boolean[]) {
            return Arrays.equals((boolean[]) array1, (boolean[]) array2);
        }
        return Arrays.deepEquals((Object[]) array1, (Object[]) array2);
    }

    public static <T> boolean isSub(T[] array, T[] subArray) {
        return indexOfSub(array, subArray) > -1;
    }

    public static <T> int indexOfSub(T[] array, T[] subArray) {
        return indexOfSub(array, 0, subArray);
    }

    public static <T> int indexOfSub(T[] array, int beginInclude, T[] subArray) {
        int firstIndex;
        if (isEmpty((Object[]) array) || isEmpty((Object[]) subArray) || subArray.length > array.length || (firstIndex = indexOf(array, subArray[0], beginInclude)) < 0 || firstIndex + subArray.length > array.length) {
            return -1;
        }
        for (int i = 0; i < subArray.length; i++) {
            if (false == ObjectUtil.equal(array[i + firstIndex], subArray[i])) {
                return indexOfSub(array, firstIndex + 1, subArray);
            }
        }
        return firstIndex;
    }

    public static <T> int lastIndexOfSub(T[] array, T[] subArray) {
        if (isEmpty((Object[]) array) || isEmpty((Object[]) subArray)) {
            return -1;
        }
        return lastIndexOfSub(array, array.length - 1, subArray);
    }

    public static <T> int lastIndexOfSub(T[] array, int endInclude, T[] subArray) {
        int firstIndex;
        if (isEmpty((Object[]) array) || isEmpty((Object[]) subArray) || subArray.length > array.length || endInclude < 0 || (firstIndex = lastIndexOf(array, subArray[0])) < 0 || firstIndex + subArray.length > array.length) {
            return -1;
        }
        for (int i = 0; i < subArray.length; i++) {
            if (false == ObjectUtil.equal(array[i + firstIndex], subArray[i])) {
                return lastIndexOfSub(array, firstIndex - 1, subArray);
            }
        }
        return firstIndex;
    }

    public static <T> boolean isSorted(T[] array, Comparator<? super T> comparator) {
        if (array == null || comparator == null) {
            return false;
        }
        for (int i = 0; i < array.length - 1; i++) {
            if (comparator.compare(array[i], array[i + 1]) > 0) {
                return false;
            }
        }
        return true;
    }

    public static <T extends Comparable<? super T>> boolean isSorted(T[] array) {
        return isSortedASC(array);
    }

    public static <T extends Comparable<? super T>> boolean isSortedASC(T[] array) {
        if (array == null) {
            return false;
        }
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i].compareTo(array[i + 1]) > 0) {
                return false;
            }
        }
        return true;
    }

    public static <T extends Comparable<? super T>> boolean isSortedDESC(T[] array) {
        if (array == null) {
            return false;
        }
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i].compareTo(array[i + 1]) < 0) {
                return false;
            }
        }
        return true;
    }
}
