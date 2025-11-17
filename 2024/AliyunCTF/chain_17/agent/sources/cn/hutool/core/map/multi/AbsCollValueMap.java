package cn.hutool.core.map.multi;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapWrapper;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/multi/AbsCollValueMap.class */
public abstract class AbsCollValueMap<K, V, C extends Collection<V>> extends MapWrapper<K, C> {
    private static final long serialVersionUID = 1;
    protected static final int DEFAULT_COLLECTION_INITIAL_CAPACITY = 3;

    protected abstract C createCollection();

    public AbsCollValueMap() {
        this(16);
    }

    public AbsCollValueMap(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    public AbsCollValueMap(Map<? extends K, C> m) {
        this(0.75f, m);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public AbsCollValueMap(float loadFactor, Map<? extends K, C> map) {
        this(map.size(), loadFactor);
        putAll(map);
    }

    public AbsCollValueMap(int initialCapacity, float loadFactor) {
        super(new HashMap(initialCapacity, loadFactor));
    }

    public void putAllValues(Map<? extends K, ? extends Collection<V>> m) {
        if (null != m) {
            m.forEach((key, valueColl) -> {
                if (null != valueColl) {
                    valueColl.forEach(obj -> {
                        putValue(key, obj);
                    });
                }
            });
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v2, types: [java.util.Collection] */
    public void putValue(K key, V value) {
        C collection = (Collection) get(key);
        if (null == collection) {
            collection = createCollection();
            put(key, collection);
        }
        collection.add(value);
    }

    public V get(K k, int i) {
        return (V) CollUtil.get((Collection) get(k), i);
    }

    public boolean removeValue(K key, V value) {
        Collection collection = (Collection) get(key);
        return null != collection && collection.remove(value);
    }

    public boolean removeValues(K key, Collection<V> values) {
        Collection collection = (Collection) get(key);
        return null != collection && collection.removeAll(values);
    }
}
