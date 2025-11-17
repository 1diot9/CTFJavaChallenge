package org.apache.logging.log4j.spi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.TriConsumer;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/spi/DefaultThreadContextMap.class */
public class DefaultThreadContextMap implements ThreadContextMap, ReadOnlyStringMap {
    private static final long serialVersionUID = 8218007901108944053L;
    public static final String INHERITABLE_MAP = "isThreadContextMapInheritable";
    private final boolean useMap;
    private final ThreadLocal<Map<String, String>> localMap;
    private static boolean inheritableMap;

    static {
        init();
    }

    static ThreadLocal<Map<String, String>> createThreadLocalMap(final boolean isMapEnabled) {
        if (inheritableMap) {
            return new InheritableThreadLocal<Map<String, String>>() { // from class: org.apache.logging.log4j.spi.DefaultThreadContextMap.1
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // java.lang.InheritableThreadLocal
                public Map<String, String> childValue(final Map<String, String> parentValue) {
                    if (parentValue != null && isMapEnabled) {
                        return Collections.unmodifiableMap(new HashMap(parentValue));
                    }
                    return null;
                }
            };
        }
        return new ThreadLocal<>();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void init() {
        inheritableMap = PropertiesUtil.getProperties().getBooleanProperty("isThreadContextMapInheritable");
    }

    public DefaultThreadContextMap() {
        this(true);
    }

    public DefaultThreadContextMap(final boolean useMap) {
        this.useMap = useMap;
        this.localMap = createThreadLocalMap(useMap);
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public void put(final String key, final String value) {
        if (!this.useMap) {
            return;
        }
        Map<String, String> map = this.localMap.get();
        Map<String, String> map2 = map == null ? new HashMap<>(1) : new HashMap<>(map);
        map2.put(key, value);
        this.localMap.set(Collections.unmodifiableMap(map2));
    }

    public void putAll(final Map<String, String> m) {
        if (!this.useMap) {
            return;
        }
        Map<String, String> map = this.localMap.get();
        Map<String, String> map2 = map == null ? new HashMap<>(m.size()) : new HashMap<>(map);
        for (Map.Entry<String, String> e : m.entrySet()) {
            map2.put(e.getKey(), e.getValue());
        }
        this.localMap.set(Collections.unmodifiableMap(map2));
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public String get(final String key) {
        Map<String, String> map = this.localMap.get();
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public void remove(final String key) {
        Map<String, String> map = this.localMap.get();
        if (map != null) {
            Map<String, String> copy = new HashMap<>(map);
            copy.remove(key);
            this.localMap.set(Collections.unmodifiableMap(copy));
        }
    }

    public void removeAll(final Iterable<String> keys) {
        Map<String, String> map = this.localMap.get();
        if (map != null) {
            Map<String, String> copy = new HashMap<>(map);
            for (String key : keys) {
                copy.remove(key);
            }
            this.localMap.set(Collections.unmodifiableMap(copy));
        }
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public void clear() {
        this.localMap.remove();
    }

    @Override // org.apache.logging.log4j.util.ReadOnlyStringMap
    public Map<String, String> toMap() {
        return getCopy();
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public boolean containsKey(final String key) {
        Map<String, String> map = this.localMap.get();
        return map != null && map.containsKey(key);
    }

    @Override // org.apache.logging.log4j.util.ReadOnlyStringMap
    public <V> void forEach(final BiConsumer<String, ? super V> action) {
        Map<String, String> map = this.localMap.get();
        if (map == null) {
            return;
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            action.accept(entry.getKey(), entry.getValue());
        }
    }

    @Override // org.apache.logging.log4j.util.ReadOnlyStringMap
    public <V, S> void forEach(final TriConsumer<String, ? super V, S> action, final S state) {
        Map<String, String> map = this.localMap.get();
        if (map == null) {
            return;
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            action.accept(entry.getKey(), entry.getValue(), state);
        }
    }

    @Override // org.apache.logging.log4j.util.ReadOnlyStringMap
    public <V> V getValue(String str) {
        Map<String, String> map = this.localMap.get();
        if (map == null) {
            return null;
        }
        return (V) ((String) map.get(str));
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public Map<String, String> getCopy() {
        Map<String, String> map = this.localMap.get();
        return map == null ? new HashMap() : new HashMap(map);
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public Map<String, String> getImmutableMapOrNull() {
        return this.localMap.get();
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public boolean isEmpty() {
        Map<String, String> map = this.localMap.get();
        return map == null || map.isEmpty();
    }

    @Override // org.apache.logging.log4j.util.ReadOnlyStringMap
    public int size() {
        Map<String, String> map = this.localMap.get();
        if (map == null) {
            return 0;
        }
        return map.size();
    }

    public String toString() {
        Map<String, String> map = this.localMap.get();
        return map == null ? "{}" : map.toString();
    }

    public int hashCode() {
        Map<String, String> map = this.localMap.get();
        int result = (31 * 1) + (map == null ? 0 : map.hashCode());
        return (31 * result) + Boolean.valueOf(this.useMap).hashCode();
    }

    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof DefaultThreadContextMap) {
            DefaultThreadContextMap other = (DefaultThreadContextMap) obj;
            if (this.useMap != other.useMap) {
                return false;
            }
        }
        if (!(obj instanceof ThreadContextMap)) {
            return false;
        }
        ThreadContextMap other2 = (ThreadContextMap) obj;
        Map<String, String> map = this.localMap.get();
        Map<String, String> otherMap = other2.getImmutableMapOrNull();
        return Objects.equals(map, otherMap);
    }
}
