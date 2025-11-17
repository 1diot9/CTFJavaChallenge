package org.springframework.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/CollectionFactory.class */
public final class CollectionFactory {
    private static final Set<Class<?>> approximableCollectionTypes = Set.of((Object[]) new Class[]{Collection.class, List.class, Set.class, SortedSet.class, NavigableSet.class, ArrayList.class, LinkedList.class, HashSet.class, LinkedHashSet.class, TreeSet.class, EnumSet.class});
    private static final Set<Class<?>> approximableMapTypes = Set.of(Map.class, MultiValueMap.class, SortedMap.class, NavigableMap.class, HashMap.class, LinkedHashMap.class, LinkedMultiValueMap.class, TreeMap.class, EnumMap.class);

    private CollectionFactory() {
    }

    public static boolean isApproximableCollectionType(@Nullable Class<?> collectionType) {
        return collectionType != null && (approximableCollectionTypes.contains(collectionType) || collectionType.getName().equals("java.util.SequencedSet") || collectionType.getName().equals("java.util.SequencedCollection"));
    }

    public static <E> Collection<E> createApproximateCollection(@Nullable Object collection, int capacity) {
        if (collection instanceof EnumSet) {
            EnumSet enumSet = (EnumSet) collection;
            Collection<E> copy = EnumSet.copyOf(enumSet);
            copy.clear();
            return copy;
        }
        if (collection instanceof SortedSet) {
            SortedSet sortedSet = (SortedSet) collection;
            return new TreeSet(sortedSet.comparator());
        }
        if (collection instanceof LinkedList) {
            return new LinkedList();
        }
        if (collection instanceof List) {
            return new ArrayList(capacity);
        }
        return new LinkedHashSet(capacity);
    }

    public static <E> Collection<E> createCollection(Class<?> collectionType, int capacity) {
        return createCollection(collectionType, null, capacity);
    }

    public static <E> Collection<E> createCollection(Class<?> collectionType, @Nullable Class<?> elementType, int capacity) {
        Assert.notNull(collectionType, "Collection type must not be null");
        if (LinkedHashSet.class == collectionType || Set.class == collectionType || Collection.class == collectionType || collectionType.getName().equals("java.util.SequencedSet") || collectionType.getName().equals("java.util.SequencedCollection")) {
            return new LinkedHashSet(capacity);
        }
        if (ArrayList.class == collectionType || List.class == collectionType) {
            return new ArrayList(capacity);
        }
        if (LinkedList.class == collectionType) {
            return new LinkedList();
        }
        if (TreeSet.class == collectionType || NavigableSet.class == collectionType || SortedSet.class == collectionType) {
            return new TreeSet();
        }
        if (EnumSet.class.isAssignableFrom(collectionType)) {
            Assert.notNull(elementType, "Cannot create EnumSet for unknown element type");
            return EnumSet.noneOf(asEnumType(elementType));
        }
        if (HashSet.class == collectionType) {
            return new HashSet(capacity);
        }
        if (collectionType.isInterface() || !Collection.class.isAssignableFrom(collectionType)) {
            throw new IllegalArgumentException("Unsupported Collection type: " + collectionType.getName());
        }
        try {
            return (Collection) ReflectionUtils.accessibleConstructor(collectionType, new Class[0]).newInstance(new Object[0]);
        } catch (Throwable ex) {
            throw new IllegalArgumentException("Could not instantiate Collection type: " + collectionType.getName(), ex);
        }
    }

    public static boolean isApproximableMapType(@Nullable Class<?> mapType) {
        return mapType != null && (approximableMapTypes.contains(mapType) || mapType.getName().equals("java.util.SequencedMap"));
    }

    public static <K, V> Map<K, V> createApproximateMap(@Nullable Object map, int capacity) {
        if (map instanceof EnumMap) {
            EnumMap enumMap = (EnumMap) map;
            EnumMap copy = new EnumMap(enumMap);
            copy.clear();
            return copy;
        }
        if (map instanceof SortedMap) {
            SortedMap sortedMap = (SortedMap) map;
            return new TreeMap(sortedMap.comparator());
        }
        if (map instanceof MultiValueMap) {
            return new LinkedMultiValueMap(capacity);
        }
        return new LinkedHashMap(capacity);
    }

    public static <K, V> Map<K, V> createMap(Class<?> mapType, int capacity) {
        return createMap(mapType, null, capacity);
    }

    public static <K, V> Map<K, V> createMap(Class<?> mapType, @Nullable Class<?> keyType, int capacity) {
        Assert.notNull(mapType, "Map type must not be null");
        if (LinkedHashMap.class == mapType || Map.class == mapType || mapType.getName().equals("java.util.SequencedMap")) {
            return new LinkedHashMap(capacity);
        }
        if (LinkedMultiValueMap.class == mapType || MultiValueMap.class == mapType) {
            return new LinkedMultiValueMap();
        }
        if (TreeMap.class == mapType || SortedMap.class == mapType || NavigableMap.class == mapType) {
            return new TreeMap();
        }
        if (EnumMap.class == mapType) {
            Assert.notNull(keyType, "Cannot create EnumMap for unknown key type");
            return new EnumMap(asEnumType(keyType));
        }
        if (HashMap.class == mapType) {
            return new HashMap(capacity);
        }
        if (mapType.isInterface() || !Map.class.isAssignableFrom(mapType)) {
            throw new IllegalArgumentException("Unsupported Map type: " + mapType.getName());
        }
        try {
            return (Map) ReflectionUtils.accessibleConstructor(mapType, new Class[0]).newInstance(new Object[0]);
        } catch (Throwable ex) {
            throw new IllegalArgumentException("Could not instantiate Map type: " + mapType.getName(), ex);
        }
    }

    public static Properties createStringAdaptingProperties() {
        return new SortedProperties(false) { // from class: org.springframework.core.CollectionFactory.1
            @Override // java.util.Properties
            @Nullable
            public String getProperty(String key) {
                Object value = get(key);
                if (value != null) {
                    return value.toString();
                }
                return null;
            }
        };
    }

    public static Properties createSortedProperties(boolean omitComments) {
        return new SortedProperties(omitComments);
    }

    public static Properties createSortedProperties(Properties properties, boolean omitComments) {
        return new SortedProperties(properties, omitComments);
    }

    private static Class<? extends Enum> asEnumType(Class<?> enumType) {
        Assert.notNull(enumType, "Enum type must not be null");
        if (!Enum.class.isAssignableFrom(enumType)) {
            throw new IllegalArgumentException("Supplied type is not an enum: " + enumType.getName());
        }
        return enumType.asSubclass(Enum.class);
    }
}
