package cn.hutool.core.collection;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Editor;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.Matcher;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrJoiner;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import java.lang.invoke.SerializedLambda;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/collection/IterUtil.class */
public class IterUtil {
    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case -448210889:
                if (implMethodName.equals("lambda$fieldValueMap$a3f4a90f$1")) {
                    z = 3;
                    break;
                }
                break;
            case -278891634:
                if (implMethodName.equals("lambda$fieldValueAsMap$f61513e$1")) {
                    z = 2;
                    break;
                }
                break;
            case 591690217:
                if (implMethodName.equals("lambda$fieldValueAsMap$ceda202c$1")) {
                    z = true;
                    break;
                }
                break;
            case 2114720463:
                if (implMethodName.equals("lambda$toMap$ed1d981b$1")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func1") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/collection/IterUtil") && lambda.getImplMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;")) {
                    return value -> {
                        return value;
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func1") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/collection/IterUtil") && lambda.getImplMethodSignature().equals("(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;")) {
                    String str = (String) lambda.getCapturedArg(0);
                    return value2 -> {
                        return ReflectUtil.getFieldValue(value2, str);
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func1") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/collection/IterUtil") && lambda.getImplMethodSignature().equals("(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;")) {
                    String str2 = (String) lambda.getCapturedArg(0);
                    return value3 -> {
                        return ReflectUtil.getFieldValue(value3, str2);
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func1") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/collection/IterUtil") && lambda.getImplMethodSignature().equals("(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;")) {
                    String str3 = (String) lambda.getCapturedArg(0);
                    return value4 -> {
                        return ReflectUtil.getFieldValue(value4, str3);
                    };
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    public static <T> Iterator<T> getIter(Iterable<T> iterable) {
        if (null == iterable) {
            return null;
        }
        return iterable.iterator();
    }

    public static boolean isEmpty(Iterable<?> iterable) {
        return null == iterable || isEmpty(iterable.iterator());
    }

    public static boolean isEmpty(Iterator<?> Iterator) {
        return null == Iterator || false == Iterator.hasNext();
    }

    public static boolean isNotEmpty(Iterable<?> iterable) {
        return null != iterable && isNotEmpty(iterable.iterator());
    }

    public static boolean isNotEmpty(Iterator<?> Iterator) {
        return null != Iterator && Iterator.hasNext();
    }

    public static boolean hasNull(Iterable<?> iter) {
        return hasNull(null == iter ? null : iter.iterator());
    }

    public static boolean hasNull(Iterator<?> iter) {
        if (null == iter) {
            return true;
        }
        while (iter.hasNext()) {
            if (null == iter.next()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAllNull(Iterable<?> iter) {
        return isAllNull(null == iter ? null : iter.iterator());
    }

    public static boolean isAllNull(Iterator<?> iter) {
        return null == getFirstNoneNull(iter);
    }

    public static <T> Map<T, Integer> countMap(Iterator<T> iter) {
        HashMap<T, Integer> countMap = new HashMap<>();
        if (null != iter) {
            while (iter.hasNext()) {
                T t = iter.next();
                countMap.put(t, Integer.valueOf(countMap.getOrDefault(t, 0).intValue() + 1));
            }
        }
        return countMap;
    }

    public static <K, V> Map<K, V> fieldValueMap(Iterator<V> iter, String fieldName) {
        return toMap(iter, new HashMap(), value4 -> {
            return ReflectUtil.getFieldValue(value4, fieldName);
        });
    }

    public static <K, V> Map<K, V> fieldValueAsMap(Iterator<?> iter, String fieldNameForKey, String fieldNameForValue) {
        return toMap(iter, new HashMap(), value3 -> {
            return ReflectUtil.getFieldValue(value3, fieldNameForKey);
        }, value2 -> {
            return ReflectUtil.getFieldValue(value2, fieldNameForValue);
        });
    }

    public static <V> List<Object> fieldValueList(Iterable<V> iterable, String fieldName) {
        return fieldValueList(getIter((Iterable) iterable), fieldName);
    }

    public static <V> List<Object> fieldValueList(Iterator<V> iter, String fieldName) {
        List<Object> result = new ArrayList<>();
        if (null != iter) {
            while (iter.hasNext()) {
                V value = iter.next();
                result.add(ReflectUtil.getFieldValue(value, fieldName));
            }
        }
        return result;
    }

    public static <T> String join(Iterator<T> iterator, CharSequence conjunction) {
        return StrJoiner.of(conjunction).append((Iterator) iterator).toString();
    }

    public static <T> String join(Iterator<T> iterator, CharSequence conjunction, String prefix, String suffix) {
        return StrJoiner.of(conjunction, prefix, suffix).setWrapElement(true).append((Iterator) iterator).toString();
    }

    public static <T> String join(Iterator<T> iterator, CharSequence conjunction, Function<T, ? extends CharSequence> func) {
        if (null == iterator) {
            return null;
        }
        return StrJoiner.of(conjunction).append(iterator, func).toString();
    }

    public static <K, V> HashMap<K, V> toMap(Iterable<Map.Entry<K, V>> entryIter) {
        HashMap<K, V> map = new HashMap<>();
        if (isNotEmpty(entryIter)) {
            for (Map.Entry<K, V> entry : entryIter) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    public static <K, V> Map<K, V> toMap(Iterable<K> keys, Iterable<V> values) {
        return toMap((Iterable) keys, (Iterable) values, false);
    }

    public static <K, V> Map<K, V> toMap(Iterable<K> keys, Iterable<V> values, boolean isOrder) {
        return toMap(null == keys ? null : keys.iterator(), null == values ? null : values.iterator(), isOrder);
    }

    public static <K, V> Map<K, V> toMap(Iterator<K> keys, Iterator<V> values) {
        return toMap((Iterator) keys, (Iterator) values, false);
    }

    public static <K, V> Map<K, V> toMap(Iterator<K> keys, Iterator<V> values, boolean isOrder) {
        Map<K, V> resultMap = MapUtil.newHashMap(isOrder);
        if (isNotEmpty((Iterator<?>) keys)) {
            while (keys.hasNext()) {
                resultMap.put(keys.next(), (null == values || !values.hasNext()) ? null : values.next());
            }
        }
        return resultMap;
    }

    public static <K, V> Map<K, List<V>> toListMap(Iterable<V> iterable, Function<V, K> keyMapper) {
        return toListMap(iterable, keyMapper, v -> {
            return v;
        });
    }

    public static <T, K, V> Map<K, List<V>> toListMap(Iterable<T> iterable, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return toListMap(MapUtil.newHashMap(), iterable, keyMapper, valueMapper);
    }

    public static <T, K, V> Map<K, List<V>> toListMap(Map<K, List<V>> resultMap, Iterable<T> iterable, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        if (null == resultMap) {
            resultMap = MapUtil.newHashMap();
        }
        if (ObjectUtil.isNull(iterable)) {
            return resultMap;
        }
        for (T value : iterable) {
            ((List) resultMap.computeIfAbsent(keyMapper.apply(value), k -> {
                return new ArrayList();
            })).add(valueMapper.apply(value));
        }
        return resultMap;
    }

    public static <K, V> Map<K, V> toMap(Iterable<V> iterable, Function<V, K> keyMapper) {
        return toMap(iterable, keyMapper, v -> {
            return v;
        });
    }

    public static <T, K, V> Map<K, V> toMap(Iterable<T> iterable, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return toMap(MapUtil.newHashMap(), iterable, keyMapper, valueMapper);
    }

    public static <T, K, V> Map<K, V> toMap(Map<K, V> resultMap, Iterable<T> iterable, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        if (null == resultMap) {
            resultMap = MapUtil.newHashMap();
        }
        if (ObjectUtil.isNull(iterable)) {
            return resultMap;
        }
        for (T value : iterable) {
            resultMap.put(keyMapper.apply(value), valueMapper.apply(value));
        }
        return resultMap;
    }

    public static <E> List<E> toList(Iterable<E> iter) {
        if (null == iter) {
            return null;
        }
        return toList(iter.iterator());
    }

    public static <E> List<E> toList(Iterator<E> iter) {
        return ListUtil.toList(iter);
    }

    public static <E> Iterator<E> asIterator(Enumeration<E> e) {
        return new EnumerationIter(e);
    }

    public static <E> Iterable<E> asIterable(Iterator<E> iter) {
        return () -> {
            return iter;
        };
    }

    public static <E> E get(Iterator<E> iterator, int index) throws IndexOutOfBoundsException {
        if (null == iterator) {
            return null;
        }
        Assert.isTrue(index >= 0, "[index] must be >= 0", new Object[0]);
        while (iterator.hasNext()) {
            index--;
            if (-1 == index) {
                return iterator.next();
            }
            iterator.next();
        }
        return null;
    }

    /* JADX WARN: Type inference failed for: r0v10, types: [T, java.lang.Object] */
    public static <T> T getFirst(Iterable<T> iterable) {
        if (iterable instanceof List) {
            List list = (List) iterable;
            if (CollUtil.isEmpty((Collection<?>) list)) {
                return null;
            }
            return list.get(0);
        }
        return (T) getFirst(getIter((Iterable) iterable));
    }

    public static <T> T getFirstNoneNull(Iterable<T> iterable) {
        if (null == iterable) {
            return null;
        }
        return (T) getFirstNoneNull(iterable.iterator());
    }

    public static <T> T getFirst(Iterator<T> it) {
        return (T) get(it, 0);
    }

    public static <T> T getFirstNoneNull(Iterator<T> it) {
        return (T) firstMatch(it, Objects::nonNull);
    }

    public static <T> T firstMatch(Iterator<T> iterator, Matcher<T> matcher) {
        Assert.notNull(matcher, "Matcher must be not null !", new Object[0]);
        if (null == iterator) {
            return null;
        }
        while (iterator.hasNext()) {
            T next = iterator.next();
            if (matcher.match(next)) {
                return next;
            }
        }
        return null;
    }

    public static Class<?> getElementType(Iterable<?> iterable) {
        return getElementType((Iterator<?>) getIter((Iterable) iterable));
    }

    public static Class<?> getElementType(Iterator<?> iterator) {
        Object ele;
        if (null == iterator || null == (ele = getFirstNoneNull(iterator))) {
            return null;
        }
        return ele.getClass();
    }

    public static <T> List<T> edit(Iterable<T> iter, Editor<T> editor) {
        List<T> result = new ArrayList<>();
        if (null == iter) {
            return result;
        }
        for (T t : iter) {
            T modified = null == editor ? t : editor.edit(t);
            if (null != modified) {
                result.add(modified);
            }
        }
        return result;
    }

    public static <T extends Iterable<E>, E> T filter(T iter, Filter<E> filter) {
        if (null == iter) {
            return null;
        }
        filter(iter.iterator(), filter);
        return iter;
    }

    public static <E> Iterator<E> filter(Iterator<E> iter, Filter<E> filter) {
        if (null == iter || null == filter) {
            return iter;
        }
        while (iter.hasNext()) {
            if (false == filter.accept(iter.next())) {
                iter.remove();
            }
        }
        return iter;
    }

    public static <E> List<E> filterToList(Iterator<E> iter, Filter<E> filter) {
        return toList(filtered(iter, filter));
    }

    public static <E> FilterIter<E> filtered(Iterator<? extends E> iterator, Filter<? super E> filter) {
        return new FilterIter<>(iterator, filter);
    }

    public static <K, V> Map<K, V> toMap(Iterator<V> iterator, Map<K, V> map, Func1<V, K> keyFunc) {
        return toMap(iterator, map, keyFunc, value -> {
            return value;
        });
    }

    public static <K, V, E> Map<K, V> toMap(Iterator<E> iterator, Map<K, V> map, Func1<E, K> keyFunc, Func1<E, V> valueFunc) {
        if (null == iterator) {
            return map;
        }
        if (null == map) {
            map = MapUtil.newHashMap(true);
        }
        while (iterator.hasNext()) {
            E element = iterator.next();
            try {
                map.put(keyFunc.call(element), valueFunc.call(element));
            } catch (Exception e) {
                throw new UtilException(e);
            }
        }
        return map;
    }

    public static <T> Iterator<T> empty() {
        return Collections.emptyIterator();
    }

    public static <F, T> Iterator<T> trans(Iterator<F> iterator, Function<? super F, ? extends T> function) {
        return new TransIter(iterator, function);
    }

    public static int size(Iterable<?> iterable) {
        if (null == iterable) {
            return 0;
        }
        if (iterable instanceof Collection) {
            return ((Collection) iterable).size();
        }
        return size(iterable.iterator());
    }

    public static int size(Iterator<?> iterator) {
        int size = 0;
        if (iterator != null) {
            while (iterator.hasNext()) {
                iterator.next();
                size++;
            }
        }
        return size;
    }

    public static boolean isEqualList(Iterable<?> list1, Iterable<?> list2) {
        if (list1 == list2) {
            return true;
        }
        Iterator<?> it1 = list1.iterator();
        Iterator<?> it2 = list2.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            Object obj1 = it1.next();
            Object obj2 = it2.next();
            if (false == Objects.equals(obj1, obj2)) {
                return false;
            }
        }
        return false == (it1.hasNext() || it2.hasNext());
    }

    public static void clear(Iterator<?> iterator) {
        if (null != iterator) {
            while (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }
    }

    public static <E> void forEach(Iterator<E> iterator, Consumer<? super E> consumer) {
        if (iterator != null) {
            while (iterator.hasNext()) {
                E element = iterator.next();
                if (null != consumer) {
                    consumer.accept(element);
                }
            }
        }
    }

    public static <E> String toStr(Iterator<E> iterator) {
        return toStr(iterator, ObjectUtil::toString);
    }

    public static <E> String toStr(Iterator<E> iterator, Function<? super E, String> transFunc) {
        return toStr(iterator, transFunc, ", ", "[", "]");
    }

    public static <E> String toStr(Iterator<E> iterator, Function<? super E, String> transFunc, String delimiter, String prefix, String suffix) {
        StrJoiner strJoiner = StrJoiner.of(delimiter, prefix, suffix);
        strJoiner.append(iterator, transFunc);
        return strJoiner.toString();
    }

    public static Iterator<?> getIter(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Iterator) {
            return (Iterator) obj;
        }
        if (obj instanceof Iterable) {
            return ((Iterable) obj).iterator();
        }
        if (ArrayUtil.isArray(obj)) {
            return new ArrayIter(obj);
        }
        if (obj instanceof Enumeration) {
            return new EnumerationIter((Enumeration) obj);
        }
        if (obj instanceof Map) {
            return ((Map) obj).entrySet().iterator();
        }
        if (obj instanceof NodeList) {
            return new NodeListIter((NodeList) obj);
        }
        if (obj instanceof Node) {
            return new NodeListIter(((Node) obj).getChildNodes());
        }
        if (obj instanceof Dictionary) {
            return new EnumerationIter(((Dictionary) obj).elements());
        }
        try {
            Object iterator = ReflectUtil.invoke(obj, "iterator", new Object[0]);
            if (iterator instanceof Iterator) {
                return (Iterator) iterator;
            }
        } catch (RuntimeException e) {
        }
        return new ArrayIter(new Object[]{obj});
    }
}
