package org.apache.logging.log4j.spi;

import cn.hutool.core.text.StrPool;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.apache.logging.log4j.util.StringMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/spi/CopyOnWriteSortedArrayThreadContextMap.class */
public class CopyOnWriteSortedArrayThreadContextMap implements ReadOnlyThreadContextMap, ObjectThreadContextMap, CopyOnWrite {
    public static final String INHERITABLE_MAP = "isThreadContextMapInheritable";
    protected static final int DEFAULT_INITIAL_CAPACITY = 16;
    protected static final String PROPERTY_NAME_INITIAL_CAPACITY = "log4j2.ThreadContext.initial.capacity";
    private static final StringMap EMPTY_CONTEXT_DATA = new SortedArrayStringMap(1);
    private static volatile int initialCapacity;
    private static volatile boolean inheritableMap;
    private final ThreadLocal<StringMap> localMap = createThreadLocalMap();

    static {
        EMPTY_CONTEXT_DATA.freeze();
        init();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void init() {
        PropertiesUtil properties = PropertiesUtil.getProperties();
        initialCapacity = properties.getIntegerProperty(PROPERTY_NAME_INITIAL_CAPACITY, 16);
        inheritableMap = properties.getBooleanProperty("isThreadContextMapInheritable");
    }

    private ThreadLocal<StringMap> createThreadLocalMap() {
        if (inheritableMap) {
            return new InheritableThreadLocal<StringMap>() { // from class: org.apache.logging.log4j.spi.CopyOnWriteSortedArrayThreadContextMap.1
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // java.lang.InheritableThreadLocal
                public StringMap childValue(final StringMap parentValue) {
                    if (parentValue == null) {
                        return null;
                    }
                    StringMap stringMap = CopyOnWriteSortedArrayThreadContextMap.this.createStringMap(parentValue);
                    stringMap.freeze();
                    return stringMap;
                }
            };
        }
        return new ThreadLocal<>();
    }

    protected StringMap createStringMap() {
        return new SortedArrayStringMap(initialCapacity);
    }

    protected StringMap createStringMap(final ReadOnlyStringMap original) {
        return new SortedArrayStringMap(original);
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public void put(final String key, final String value) {
        putValue(key, value);
    }

    @Override // org.apache.logging.log4j.spi.ObjectThreadContextMap
    public void putValue(final String key, final Object value) {
        StringMap map = this.localMap.get();
        StringMap map2 = map == null ? createStringMap() : createStringMap(map);
        map2.putValue(key, value);
        map2.freeze();
        this.localMap.set(map2);
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap2
    public void putAll(final Map<String, String> values) {
        if (values == null || values.isEmpty()) {
            return;
        }
        StringMap map = this.localMap.get();
        StringMap map2 = map == null ? createStringMap() : createStringMap(map);
        for (Map.Entry<String, String> entry : values.entrySet()) {
            map2.putValue(entry.getKey(), entry.getValue());
        }
        map2.freeze();
        this.localMap.set(map2);
    }

    @Override // org.apache.logging.log4j.spi.ObjectThreadContextMap
    public <V> void putAllValues(final Map<String, V> values) {
        if (values == null || values.isEmpty()) {
            return;
        }
        StringMap map = this.localMap.get();
        StringMap map2 = map == null ? createStringMap() : createStringMap(map);
        for (Map.Entry<String, V> entry : values.entrySet()) {
            map2.putValue(entry.getKey(), entry.getValue());
        }
        map2.freeze();
        this.localMap.set(map2);
    }

    @Override // org.apache.logging.log4j.spi.ReadOnlyThreadContextMap, org.apache.logging.log4j.spi.ThreadContextMap
    public String get(final String key) {
        return (String) getValue(key);
    }

    @Override // org.apache.logging.log4j.spi.ObjectThreadContextMap
    public <V> V getValue(String str) {
        StringMap stringMap = this.localMap.get();
        if (stringMap == null) {
            return null;
        }
        return (V) stringMap.getValue(str);
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public void remove(final String key) {
        StringMap map = this.localMap.get();
        if (map != null) {
            StringMap copy = createStringMap(map);
            copy.remove(key);
            copy.freeze();
            this.localMap.set(copy);
        }
    }

    @Override // org.apache.logging.log4j.spi.CleanableThreadContextMap
    public void removeAll(final Iterable<String> keys) {
        StringMap map = this.localMap.get();
        if (map != null) {
            StringMap copy = createStringMap(map);
            for (String key : keys) {
                copy.remove(key);
            }
            copy.freeze();
            this.localMap.set(copy);
        }
    }

    @Override // org.apache.logging.log4j.spi.ReadOnlyThreadContextMap, org.apache.logging.log4j.spi.ThreadContextMap
    public void clear() {
        this.localMap.remove();
    }

    @Override // org.apache.logging.log4j.spi.ReadOnlyThreadContextMap, org.apache.logging.log4j.spi.ThreadContextMap
    public boolean containsKey(final String key) {
        StringMap map = this.localMap.get();
        return map != null && map.containsKey(key);
    }

    @Override // org.apache.logging.log4j.spi.ReadOnlyThreadContextMap, org.apache.logging.log4j.spi.ThreadContextMap
    public Map<String, String> getCopy() {
        StringMap map = this.localMap.get();
        return map == null ? new HashMap() : map.toMap();
    }

    @Override // org.apache.logging.log4j.spi.ReadOnlyThreadContextMap, org.apache.logging.log4j.spi.ThreadContextMap2
    public StringMap getReadOnlyContextData() {
        StringMap map = this.localMap.get();
        return map == null ? EMPTY_CONTEXT_DATA : map;
    }

    @Override // org.apache.logging.log4j.spi.ReadOnlyThreadContextMap, org.apache.logging.log4j.spi.ThreadContextMap
    public Map<String, String> getImmutableMapOrNull() {
        StringMap map = this.localMap.get();
        if (map == null) {
            return null;
        }
        return Collections.unmodifiableMap(map.toMap());
    }

    @Override // org.apache.logging.log4j.spi.ReadOnlyThreadContextMap, org.apache.logging.log4j.spi.ThreadContextMap
    public boolean isEmpty() {
        StringMap map = this.localMap.get();
        return map == null || map.isEmpty();
    }

    public String toString() {
        StringMap map = this.localMap.get();
        return map == null ? StrPool.EMPTY_JSON : map.toString();
    }

    public int hashCode() {
        StringMap map = this.localMap.get();
        int result = (31 * 1) + (map == null ? 0 : map.hashCode());
        return result;
    }

    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof ThreadContextMap)) {
            return false;
        }
        ThreadContextMap other = (ThreadContextMap) obj;
        Map<String, String> map = getImmutableMapOrNull();
        Map<String, String> otherMap = other.getImmutableMapOrNull();
        return Objects.equals(map, otherMap);
    }
}
