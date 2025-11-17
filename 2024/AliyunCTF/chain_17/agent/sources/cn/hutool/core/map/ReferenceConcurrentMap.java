package cn.hutool.core.map;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReferenceUtil;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/ReferenceConcurrentMap.class */
public class ReferenceConcurrentMap<K, V> implements ConcurrentMap<K, V>, Iterable<Map.Entry<K, V>>, Serializable {
    final ConcurrentMap<Reference<K>, V> raw;
    private final ReferenceQueue<K> lastQueue = new ReferenceQueue<>();
    private final ReferenceUtil.ReferenceType keyType;
    private BiConsumer<Reference<? extends K>, V> purgeListener;

    public ReferenceConcurrentMap(ConcurrentMap<Reference<K>, V> raw, ReferenceUtil.ReferenceType referenceType) {
        this.raw = raw;
        this.keyType = referenceType;
    }

    public void setPurgeListener(BiConsumer<Reference<? extends K>, V> purgeListener) {
        this.purgeListener = purgeListener;
    }

    @Override // java.util.Map
    public int size() {
        purgeStaleKeys();
        return this.raw.size();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return 0 == size();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Map
    public V get(Object obj) {
        purgeStaleKeys();
        return this.raw.get(ofKey(obj, null));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Map
    public boolean containsKey(Object obj) {
        purgeStaleKeys();
        return this.raw.containsKey(ofKey(obj, null));
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        purgeStaleKeys();
        return this.raw.containsValue(value);
    }

    @Override // java.util.Map
    public V put(K key, V value) {
        purgeStaleKeys();
        return this.raw.put(ofKey(key, this.lastQueue), value);
    }

    @Override // java.util.concurrent.ConcurrentMap, java.util.Map
    public V putIfAbsent(K key, V value) {
        purgeStaleKeys();
        return this.raw.putIfAbsent(ofKey(key, this.lastQueue), value);
    }

    @Override // java.util.Map
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override // java.util.concurrent.ConcurrentMap, java.util.Map
    public V replace(K key, V value) {
        purgeStaleKeys();
        return this.raw.replace(ofKey(key, this.lastQueue), value);
    }

    @Override // java.util.concurrent.ConcurrentMap, java.util.Map
    public boolean replace(K key, V oldValue, V newValue) {
        purgeStaleKeys();
        return this.raw.replace(ofKey(key, this.lastQueue), oldValue, newValue);
    }

    @Override // java.util.concurrent.ConcurrentMap, java.util.Map
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        purgeStaleKeys();
        this.raw.replaceAll((kWeakKey, value) -> {
            return function.apply(kWeakKey.get(), value);
        });
    }

    @Override // java.util.concurrent.ConcurrentMap, java.util.Map
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        purgeStaleKeys();
        return this.raw.computeIfAbsent(ofKey(key, this.lastQueue), kWeakKey -> {
            return mappingFunction.apply(key);
        });
    }

    @Override // java.util.concurrent.ConcurrentMap, java.util.Map
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        purgeStaleKeys();
        return this.raw.computeIfPresent(ofKey(key, this.lastQueue), (kWeakKey, value) -> {
            return remappingFunction.apply(key, value);
        });
    }

    public V computeIfAbsent(K key, Func0<? extends V> supplier) {
        return computeIfAbsent((ReferenceConcurrentMap<K, V>) key, (Function<? super ReferenceConcurrentMap<K, V>, ? extends V>) keyParam -> {
            return supplier.callWithRuntimeException();
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Map
    public V remove(Object obj) {
        purgeStaleKeys();
        return this.raw.remove(ofKey(obj, null));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.concurrent.ConcurrentMap, java.util.Map
    public boolean remove(Object obj, Object value) {
        purgeStaleKeys();
        return this.raw.remove(ofKey(obj, null), value);
    }

    @Override // java.util.Map
    public void clear() {
        this.raw.clear();
        do {
        } while (this.lastQueue.poll() != null);
    }

    @Override // java.util.Map
    public Set<K> keySet() {
        Collection<K> trans = CollUtil.trans(this.raw.keySet(), reference -> {
            if (null == reference) {
                return null;
            }
            return reference.get();
        });
        return new HashSet(trans);
    }

    @Override // java.util.Map
    public Collection<V> values() {
        purgeStaleKeys();
        return this.raw.values();
    }

    @Override // java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        purgeStaleKeys();
        return (Set) this.raw.entrySet().stream().map(entry -> {
            return new AbstractMap.SimpleImmutableEntry(((Reference) entry.getKey()).get(), entry.getValue());
        }).collect(Collectors.toSet());
    }

    @Override // java.util.concurrent.ConcurrentMap, java.util.Map
    public void forEach(BiConsumer<? super K, ? super V> action) {
        purgeStaleKeys();
        this.raw.forEach((key, value) -> {
            action.accept(key.get(), value);
        });
    }

    @Override // java.lang.Iterable
    public Iterator<Map.Entry<K, V>> iterator() {
        return entrySet().iterator();
    }

    @Override // java.util.concurrent.ConcurrentMap, java.util.Map
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        purgeStaleKeys();
        return this.raw.compute(ofKey(key, this.lastQueue), (kWeakKey, value) -> {
            return remappingFunction.apply(key, value);
        });
    }

    @Override // java.util.concurrent.ConcurrentMap, java.util.Map
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        purgeStaleKeys();
        return this.raw.merge(ofKey(key, this.lastQueue), value, remappingFunction);
    }

    private void purgeStaleKeys() {
        while (true) {
            Reference<? extends K> reference = this.lastQueue.poll();
            if (reference != null) {
                V value = this.raw.remove(reference);
                if (null != this.purgeListener) {
                    this.purgeListener.accept(reference, value);
                }
            } else {
                return;
            }
        }
    }

    private Reference<K> ofKey(K key, ReferenceQueue<? super K> queue) {
        switch (this.keyType) {
            case WEAK:
                return new WeakKey(key, queue);
            case SOFT:
                return new SoftKey(key, queue);
            default:
                throw new IllegalArgumentException("Unsupported key type: " + this.keyType);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/ReferenceConcurrentMap$WeakKey.class */
    public static class WeakKey<K> extends WeakReference<K> {
        private final int hashCode;

        WeakKey(K key, ReferenceQueue<? super K> queue) {
            super(key, queue);
            this.hashCode = key.hashCode();
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (other instanceof WeakKey) {
                return ObjectUtil.equals(((WeakKey) other).get(), get());
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/ReferenceConcurrentMap$SoftKey.class */
    public static class SoftKey<K> extends SoftReference<K> {
        private final int hashCode;

        SoftKey(K key, ReferenceQueue<? super K> queue) {
            super(key, queue);
            this.hashCode = key.hashCode();
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (other instanceof SoftKey) {
                return ObjectUtil.equals(((SoftKey) other).get(), get());
            }
            return false;
        }
    }
}
