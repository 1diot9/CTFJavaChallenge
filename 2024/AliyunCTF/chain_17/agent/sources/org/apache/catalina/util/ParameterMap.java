package org.apache.catalina.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/util/ParameterMap.class */
public final class ParameterMap<K, V> implements Map<K, V>, Serializable {
    private static final long serialVersionUID = 2;
    private final Map<K, V> delegatedMap;
    private final Map<K, V> unmodifiableDelegatedMap;
    private boolean locked;
    private static final StringManager sm = StringManager.getManager("org.apache.catalina.util");

    public ParameterMap() {
        this.locked = false;
        this.delegatedMap = new LinkedHashMap();
        this.unmodifiableDelegatedMap = Collections.unmodifiableMap(this.delegatedMap);
    }

    public ParameterMap(int initialCapacity) {
        this.locked = false;
        this.delegatedMap = new LinkedHashMap(initialCapacity);
        this.unmodifiableDelegatedMap = Collections.unmodifiableMap(this.delegatedMap);
    }

    public ParameterMap(int initialCapacity, float loadFactor) {
        this.locked = false;
        this.delegatedMap = new LinkedHashMap(initialCapacity, loadFactor);
        this.unmodifiableDelegatedMap = Collections.unmodifiableMap(this.delegatedMap);
    }

    public ParameterMap(Map<K, V> map) {
        this.locked = false;
        this.delegatedMap = new LinkedHashMap(map);
        this.unmodifiableDelegatedMap = Collections.unmodifiableMap(this.delegatedMap);
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override // java.util.Map
    public void clear() {
        checkLocked();
        this.delegatedMap.clear();
    }

    @Override // java.util.Map
    public V put(K key, V value) {
        checkLocked();
        return this.delegatedMap.put(key, value);
    }

    @Override // java.util.Map
    public void putAll(Map<? extends K, ? extends V> map) {
        checkLocked();
        this.delegatedMap.putAll(map);
    }

    @Override // java.util.Map
    public V remove(Object key) {
        checkLocked();
        return this.delegatedMap.remove(key);
    }

    private void checkLocked() {
        if (this.locked) {
            throw new IllegalStateException(sm.getString("parameterMap.locked"));
        }
    }

    @Override // java.util.Map
    public int size() {
        return this.delegatedMap.size();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.delegatedMap.isEmpty();
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return this.delegatedMap.containsKey(key);
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        return this.delegatedMap.containsValue(value);
    }

    @Override // java.util.Map
    public V get(Object key) {
        return this.delegatedMap.get(key);
    }

    @Override // java.util.Map
    public Set<K> keySet() {
        if (this.locked) {
            return this.unmodifiableDelegatedMap.keySet();
        }
        return this.delegatedMap.keySet();
    }

    @Override // java.util.Map
    public Collection<V> values() {
        if (this.locked) {
            return this.unmodifiableDelegatedMap.values();
        }
        return this.delegatedMap.values();
    }

    @Override // java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        if (this.locked) {
            return this.unmodifiableDelegatedMap.entrySet();
        }
        return this.delegatedMap.entrySet();
    }
}
