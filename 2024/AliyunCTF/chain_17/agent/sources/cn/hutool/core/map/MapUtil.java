package cn.hutool.core.map;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.Editor;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.stream.CollectorUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/MapUtil.class */
public class MapUtil {
    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    public static final float DEFAULT_LOAD_FACTOR = 0.75f;

    public static boolean isEmpty(Map<?, ?> map) {
        return null == map || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return null != map && false == map.isEmpty();
    }

    public static <K, V> Map<K, V> emptyIfNull(Map<K, V> set) {
        return null == set ? Collections.emptyMap() : set;
    }

    public static <T extends Map<K, V>, K, V> T defaultIfEmpty(T map, T defaultMap) {
        return isEmpty(map) ? defaultMap : map;
    }

    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>();
    }

    public static <K, V> HashMap<K, V> newHashMap(int size, boolean isLinked) {
        int initialCapacity = ((int) (size / 0.75f)) + 1;
        return isLinked ? new LinkedHashMap(initialCapacity) : new HashMap<>(initialCapacity);
    }

    public static <K, V> HashMap<K, V> newHashMap(int size) {
        return newHashMap(size, false);
    }

    public static <K, V> HashMap<K, V> newHashMap(boolean isLinked) {
        return newHashMap(16, isLinked);
    }

    public static <K, V> TreeMap<K, V> newTreeMap(Comparator<? super K> comparator) {
        return new TreeMap<>(comparator);
    }

    public static <K, V> TreeMap<K, V> newTreeMap(Map<K, V> map, Comparator<? super K> comparator) {
        TreeMap<K, V> treeMap = new TreeMap<>(comparator);
        if (false == isEmpty(map)) {
            treeMap.putAll(map);
        }
        return treeMap;
    }

    public static <K, V> Map<K, V> newIdentityMap(int size) {
        return new IdentityHashMap(size);
    }

    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap() {
        return new ConcurrentHashMap<>(16);
    }

    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(int size) {
        int initCapacity = size <= 0 ? 16 : size;
        return new ConcurrentHashMap<>(initCapacity);
    }

    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(Map<K, V> map) {
        if (isEmpty(map)) {
            return new ConcurrentHashMap<>(16);
        }
        return new ConcurrentHashMap<>(map);
    }

    public static <K, V> Map<K, V> createMap(Class<?> mapType) {
        if (null == mapType || mapType.isAssignableFrom(AbstractMap.class)) {
            return new HashMap();
        }
        try {
            return (Map) ReflectUtil.newInstance(mapType, new Object[0]);
        } catch (UtilException e) {
            return new HashMap();
        }
    }

    public static <K, V> HashMap<K, V> of(K key, V value) {
        return of(key, value, false);
    }

    public static <K, V> HashMap<K, V> of(K key, V value, boolean isOrder) {
        HashMap<K, V> map = newHashMap(isOrder);
        map.put(key, value);
        return map;
    }

    @SafeVarargs
    @Deprecated
    public static <K, V> Map<K, V> of(Pair<K, V>... pairs) {
        Map<K, V> map = new HashMap<>();
        for (Pair<K, V> pair : pairs) {
            map.put(pair.getKey(), pair.getValue());
        }
        return map;
    }

    @SafeVarargs
    public static <K, V> Map<K, V> ofEntries(Map.Entry<K, V>... entries) {
        Map<K, V> map = new HashMap<>();
        for (Map.Entry<K, V> pair : entries) {
            map.put(pair.getKey(), pair.getValue());
        }
        return map;
    }

    public static HashMap<Object, Object> of(Object[] array) {
        if (array == null) {
            return null;
        }
        HashMap<Object, Object> map = new HashMap<>((int) (array.length * 1.5d));
        for (int i = 0; i < array.length; i++) {
            Object object = array[i];
            if (object instanceof Map.Entry) {
                Map.Entry entry = (Map.Entry) object;
                map.put(entry.getKey(), entry.getValue());
            } else if (object instanceof Object[]) {
                Object[] entry2 = (Object[]) object;
                if (entry2.length > 1) {
                    map.put(entry2[0], entry2[1]);
                }
            } else if (object instanceof Iterable) {
                Iterator iter = ((Iterable) object).iterator();
                if (iter.hasNext()) {
                    Object key = iter.next();
                    if (iter.hasNext()) {
                        Object value = iter.next();
                        map.put(key, value);
                    }
                }
            } else if (object instanceof Iterator) {
                Iterator iter2 = (Iterator) object;
                if (iter2.hasNext()) {
                    Object key2 = iter2.next();
                    if (iter2.hasNext()) {
                        Object value2 = iter2.next();
                        map.put(key2, value2);
                    }
                }
            } else {
                throw new IllegalArgumentException(StrUtil.format("Array element {}, '{}', is not type of Map.Entry or Array or Iterable or Iterator", Integer.valueOf(i), object));
            }
        }
        return map;
    }

    public static <K, V> Map<K, List<V>> toListMap(Iterable<? extends Map<K, V>> mapList) {
        HashMap<K, List<V>> resultMap = new HashMap<>();
        if (CollUtil.isEmpty(mapList)) {
            return resultMap;
        }
        for (Map<K, V> map : mapList) {
            Set<Map.Entry<K, V>> entrySet = map.entrySet();
            for (Map.Entry<K, V> entry : entrySet) {
                K key = entry.getKey();
                List<V> valueList = resultMap.get(key);
                if (null == valueList) {
                    resultMap.put(key, CollUtil.newArrayList(entry.getValue()));
                } else {
                    valueList.add(entry.getValue());
                }
            }
        }
        return resultMap;
    }

    public static <K, V> List<Map<K, V>> toMapList(Map<K, ? extends Iterable<V>> listMap) {
        boolean isEnd;
        ArrayList arrayList = new ArrayList();
        if (isEmpty(listMap)) {
            return arrayList;
        }
        int index = 0;
        do {
            isEnd = true;
            HashMap hashMap = new HashMap();
            for (Map.Entry<K, ? extends Iterable<V>> entry : listMap.entrySet()) {
                List<V> vList = CollUtil.newArrayList(entry.getValue());
                int vListSize = vList.size();
                if (index < vListSize) {
                    hashMap.put(entry.getKey(), vList.get(index));
                    if (index != vListSize - 1) {
                        isEnd = false;
                    }
                }
            }
            if (false == hashMap.isEmpty()) {
                arrayList.add(hashMap);
            }
            index++;
        } while (false == isEnd);
        return arrayList;
    }

    public static <K, V> Map<K, List<V>> grouping(Iterable<Map.Entry<K, V>> entries) {
        Map<K, List<V>> map = new HashMap<>();
        if (CollUtil.isEmpty(entries)) {
            return map;
        }
        for (Map.Entry<K, V> pair : entries) {
            List<V> values = map.computeIfAbsent(pair.getKey(), k -> {
                return new ArrayList();
            });
            values.add(pair.getValue());
        }
        return map;
    }

    public static <K, V> Map<K, V> toCamelCaseMap(Map<K, V> map) {
        return map instanceof LinkedHashMap ? new CamelCaseLinkedMap(map) : new CamelCaseMap(map);
    }

    public static Object[][] toObjectArray(Map<?, ?> map) {
        if (map == null) {
            return (Object[][]) null;
        }
        Object[][] result = new Object[map.size()][2];
        if (map.isEmpty()) {
            return result;
        }
        int index = 0;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            result[index][0] = entry.getKey();
            result[index][1] = entry.getValue();
            index++;
        }
        return result;
    }

    public static <K, V> String join(Map<K, V> map, String separator, String keyValueSeparator, String... otherParams) {
        return join(map, separator, keyValueSeparator, false, otherParams);
    }

    public static String sortJoin(Map<?, ?> params, String separator, String keyValueSeparator, boolean isIgnoreNull, String... otherParams) {
        return join(sort(params), separator, keyValueSeparator, isIgnoreNull, otherParams);
    }

    public static <K, V> String joinIgnoreNull(Map<K, V> map, String separator, String keyValueSeparator, String... otherParams) {
        return join(map, separator, keyValueSeparator, true, otherParams);
    }

    public static <K, V> String join(Map<K, V> map, String separator, String keyValueSeparator, boolean isIgnoreNull, String... otherParams) {
        StringBuilder strBuilder = StrUtil.builder();
        boolean isFirst = true;
        if (isNotEmpty(map)) {
            for (Map.Entry<K, V> entry : map.entrySet()) {
                if (false == isIgnoreNull || (entry.getKey() != null && entry.getValue() != null)) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        strBuilder.append(separator);
                    }
                    strBuilder.append(Convert.toStr(entry.getKey())).append(keyValueSeparator).append(Convert.toStr(entry.getValue()));
                }
            }
        }
        if (ArrayUtil.isNotEmpty((Object[]) otherParams)) {
            for (String otherParam : otherParams) {
                strBuilder.append(otherParam);
            }
        }
        return strBuilder.toString();
    }

    public static <K, V> Map<K, V> edit(Map<K, V> map, Editor<Map.Entry<K, V>> editor) {
        if (null == map || null == editor) {
            return map;
        }
        Map<K, V> map2 = (Map) ReflectUtil.newInstanceIfPossible(map.getClass());
        if (null == map2) {
            map2 = new HashMap(map.size(), 1.0f);
        }
        if (isEmpty(map)) {
            return map2;
        }
        for (Map.Entry<K, V> entry : map.entrySet()) {
            Map.Entry<K, V> modified = editor.edit(entry);
            if (null != modified) {
                map2.put(modified.getKey(), modified.getValue());
            }
        }
        return map2;
    }

    public static <K, V> Map<K, V> filter(Map<K, V> map, Filter<Map.Entry<K, V>> filter) {
        if (null == map || null == filter) {
            return map;
        }
        return edit(map, t -> {
            if (filter.accept(t)) {
                return t;
            }
            return null;
        });
    }

    public static <K, V, R> Map<K, R> map(Map<K, V> map, BiFunction<K, V, R> biFunction) {
        if (null == map || null == biFunction) {
            return newHashMap();
        }
        return (Map) map.entrySet().stream().collect(CollectorUtil.toMap((v0) -> {
            return v0.getKey();
        }, m -> {
            return biFunction.apply(m.getKey(), m.getValue());
        }, (l, r) -> {
            return l;
        }));
    }

    public static <K, V> Map<K, V> filter(Map<K, V> map, K... keys) {
        if (null == map || null == keys) {
            return map;
        }
        Map<K, V> map2 = (Map) ReflectUtil.newInstanceIfPossible(map.getClass());
        if (null == map2) {
            map2 = new HashMap(map.size(), 1.0f);
        }
        if (isEmpty(map)) {
            return map2;
        }
        for (K key : keys) {
            if (map.containsKey(key)) {
                map2.put(key, map.get(key));
            }
        }
        return map2;
    }

    public static <T> Map<T, T> reverse(Map<T, T> map) {
        return edit(map, t -> {
            return new Map.Entry<T, T>() { // from class: cn.hutool.core.map.MapUtil.1
                @Override // java.util.Map.Entry
                public T getKey() {
                    return (T) t.getValue();
                }

                @Override // java.util.Map.Entry
                public T getValue() {
                    return (T) t.getKey();
                }

                @Override // java.util.Map.Entry
                public T setValue(T t) {
                    throw new UnsupportedOperationException("Unsupported setValue method !");
                }
            };
        });
    }

    public static <K, V> Map<V, K> inverse(Map<K, V> map) {
        Map<V, K> result = createMap(map.getClass());
        map.forEach((key, value) -> {
            result.put(value, key);
        });
        return result;
    }

    public static <K, V> TreeMap<K, V> sort(Map<K, V> map) {
        return sort(map, null);
    }

    public static <K, V> TreeMap<K, V> sort(Map<K, V> map, Comparator<? super K> comparator) {
        if (null == map) {
            return null;
        }
        if (map instanceof TreeMap) {
            TreeMap<K, V> result = (TreeMap) map;
            if (null == comparator || comparator.equals(result.comparator())) {
                return result;
            }
        }
        return newTreeMap(map, comparator);
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean isDesc) {
        Map<K, V> result = new LinkedHashMap<>();
        Comparator<Map.Entry<K, V>> entryComparator = Map.Entry.comparingByValue();
        if (isDesc) {
            entryComparator = entryComparator.reversed();
        }
        map.entrySet().stream().sorted(entryComparator).forEachOrdered(e -> {
        });
        return result;
    }

    public static MapProxy createProxy(Map<?, ?> map) {
        return MapProxy.create(map);
    }

    public static <K, V> MapWrapper<K, V> wrap(Map<K, V> map) {
        return new MapWrapper<>(map);
    }

    public static <K, V> Map<K, V> unmodifiable(Map<K, V> map) {
        return Collections.unmodifiableMap(map);
    }

    public static <K, V> MapBuilder<K, V> builder() {
        return builder(new HashMap());
    }

    public static <K, V> MapBuilder<K, V> builder(Map<K, V> map) {
        return new MapBuilder<>(map);
    }

    public static <K, V> MapBuilder<K, V> builder(K k, V v) {
        return builder(new HashMap()).put(k, v);
    }

    public static <K, V> Map<K, V> getAny(Map<K, V> map, K... keys) {
        return filter(map, entry -> {
            return ArrayUtil.contains(keys, entry.getKey());
        });
    }

    public static <K, V> Map<K, V> removeAny(Map<K, V> map, K... keys) {
        for (K key : keys) {
            map.remove(key);
        }
        return map;
    }

    public static String getStr(Map<?, ?> map, Object key) {
        return (String) get(map, key, String.class);
    }

    public static String getStr(Map<?, ?> map, Object key, String defaultValue) {
        return (String) get(map, key, (Class<String>) String.class, defaultValue);
    }

    public static Integer getInt(Map<?, ?> map, Object key) {
        return (Integer) get(map, key, Integer.class);
    }

    public static Integer getInt(Map<?, ?> map, Object key, Integer defaultValue) {
        return (Integer) get(map, key, (Class<Integer>) Integer.class, defaultValue);
    }

    public static Double getDouble(Map<?, ?> map, Object key) {
        return (Double) get(map, key, Double.class);
    }

    public static Double getDouble(Map<?, ?> map, Object key, Double defaultValue) {
        return (Double) get(map, key, (Class<Double>) Double.class, defaultValue);
    }

    public static Float getFloat(Map<?, ?> map, Object key) {
        return (Float) get(map, key, Float.class);
    }

    public static Float getFloat(Map<?, ?> map, Object key, Float defaultValue) {
        return (Float) get(map, key, (Class<Float>) Float.class, defaultValue);
    }

    public static Short getShort(Map<?, ?> map, Object key) {
        return (Short) get(map, key, Short.class);
    }

    public static Short getShort(Map<?, ?> map, Object key, Short defaultValue) {
        return (Short) get(map, key, (Class<Short>) Short.class, defaultValue);
    }

    public static Boolean getBool(Map<?, ?> map, Object key) {
        return (Boolean) get(map, key, Boolean.class);
    }

    public static Boolean getBool(Map<?, ?> map, Object key, Boolean defaultValue) {
        return (Boolean) get(map, key, (Class<Boolean>) Boolean.class, defaultValue);
    }

    public static Character getChar(Map<?, ?> map, Object key) {
        return (Character) get(map, key, Character.class);
    }

    public static Character getChar(Map<?, ?> map, Object key, Character defaultValue) {
        return (Character) get(map, key, (Class<Character>) Character.class, defaultValue);
    }

    public static Long getLong(Map<?, ?> map, Object key) {
        return (Long) get(map, key, Long.class);
    }

    public static Long getLong(Map<?, ?> map, Object key, Long defaultValue) {
        return (Long) get(map, key, (Class<Long>) Long.class, defaultValue);
    }

    public static Date getDate(Map<?, ?> map, Object key) {
        return (Date) get(map, key, Date.class);
    }

    public static Date getDate(Map<?, ?> map, Object key, Date defaultValue) {
        return (Date) get(map, key, (Class<Date>) Date.class, defaultValue);
    }

    public static <T> T get(Map<?, ?> map, Object obj, Class<T> cls) {
        return (T) get(map, obj, cls, (Object) null);
    }

    public static <T> T get(Map<?, ?> map, Object obj, Class<T> cls, T t) {
        return null == map ? t : (T) Convert.convert((Class) cls, map.get(obj), (Object) t);
    }

    public static <T> T getQuietly(Map<?, ?> map, Object obj, Class<T> cls, T t) {
        return null == map ? t : (T) Convert.convertQuietly(cls, map.get(obj), t);
    }

    public static <T> T get(Map<?, ?> map, Object obj, TypeReference<T> typeReference) {
        return (T) get(map, obj, typeReference, (Object) null);
    }

    public static <T> T get(Map<?, ?> map, Object obj, TypeReference<T> typeReference, T t) {
        return null == map ? t : (T) Convert.convert(typeReference, map.get(obj), t);
    }

    public static <T> T getQuietly(Map<?, ?> map, Object obj, TypeReference<T> typeReference, T t) {
        return null == map ? t : (T) Convert.convertQuietly(typeReference, map.get(obj), t);
    }

    public static <K, V> Map<K, V> renameKey(Map<K, V> map, K oldKey, K newKey) {
        if (isNotEmpty(map) && map.containsKey(oldKey)) {
            if (map.containsKey(newKey)) {
                throw new IllegalArgumentException(StrUtil.format("The key '{}' exist !", newKey));
            }
            map.put(newKey, map.remove(oldKey));
        }
        return map;
    }

    public static <K, V> Map<K, V> removeNullValue(Map<K, V> map) {
        if (isEmpty(map)) {
            return map;
        }
        Iterator<Map.Entry<K, V>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<K, V> entry = iter.next();
            if (null == entry.getValue()) {
                iter.remove();
            }
        }
        return map;
    }

    public static <K, V> Map<K, V> empty() {
        return Collections.emptyMap();
    }

    public static <K, V, T extends Map<K, V>> T empty(Class<?> cls) {
        if (null == cls) {
            return (T) Collections.emptyMap();
        }
        if (NavigableMap.class == cls) {
            return Collections.emptyNavigableMap();
        }
        if (SortedMap.class == cls) {
            return Collections.emptySortedMap();
        }
        if (Map.class == cls) {
            return (T) Collections.emptyMap();
        }
        throw new IllegalArgumentException(StrUtil.format("[{}] is not support to get empty!", cls));
    }

    public static void clear(Map<?, ?>... maps) {
        for (Map<?, ?> map : maps) {
            if (isNotEmpty(map)) {
                map.clear();
            }
        }
    }

    public static <K, V> ArrayList<V> valuesOfKeys(Map<K, V> map, Iterator<K> keys) {
        ArrayList<V> values = new ArrayList<>();
        while (keys.hasNext()) {
            values.add(map.get(keys.next()));
        }
        return values;
    }

    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return entry(key, value, true);
    }

    public static <K, V> Map.Entry<K, V> entry(K key, V value, boolean isImmutable) {
        return isImmutable ? new AbstractMap.SimpleImmutableEntry(key, value) : new AbstractMap.SimpleEntry(key, value);
    }

    public static <K, V> V computeIfAbsent(Map<K, V> map, K key, Function<? super K, ? extends V> mappingFunction) {
        V value = map.get(key);
        if (null == value) {
            map.putIfAbsent(key, mappingFunction.apply(key));
            value = map.get(key);
        }
        return value;
    }
}
