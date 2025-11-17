package org.apache.tomcat.util.collections;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/collections/ManagedConcurrentWeakHashMap.class */
public class ManagedConcurrentWeakHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {
    private final ConcurrentMap<Key, V> map = new ConcurrentHashMap();
    private final ReferenceQueue<Object> queue = new ReferenceQueue<>();

    public void maintain() {
        while (true) {
            Key key = (Key) this.queue.poll();
            if (key != null) {
                if (!key.isDead()) {
                    key.ackDeath();
                    this.map.remove(key);
                }
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/collections/ManagedConcurrentWeakHashMap$Key.class */
    public static class Key extends WeakReference<Object> {
        private final int hash;
        private boolean dead;

        Key(Object key, ReferenceQueue<Object> queue) {
            super(key, queue);
            this.hash = key.hashCode();
        }

        public int hashCode() {
            return this.hash;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (this.dead || !(obj instanceof Reference)) {
                return false;
            }
            Object oA = get();
            Object oB = ((Reference) obj).get();
            if (oA == oB) {
                return true;
            }
            if (oA == null || oB == null) {
                return false;
            }
            return oA.equals(oB);
        }

        public void ackDeath() {
            this.dead = true;
        }

        public boolean isDead() {
            return this.dead;
        }
    }

    private Key createStoreKey(Object key) {
        return new Key(key, this.queue);
    }

    private Key createLookupKey(Object key) {
        return new Key(key, null);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public int size() {
        return this.map.size();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsValue(Object value) {
        if (value == null) {
            return false;
        }
        return this.map.containsValue(value);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object key) {
        if (key == null) {
            return false;
        }
        return this.map.containsKey(createLookupKey(key));
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V get(Object key) {
        if (key == null) {
            return null;
        }
        return this.map.get(createLookupKey(key));
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V put(K key, V value) {
        Objects.requireNonNull(value);
        return this.map.put(createStoreKey(key), value);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V remove(Object key) {
        return this.map.remove(createLookupKey(key));
    }

    @Override // java.util.AbstractMap, java.util.Map
    public void clear() {
        this.map.clear();
        maintain();
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public V putIfAbsent(K key, V value) {
        Objects.requireNonNull(value);
        Key storeKey = createStoreKey(key);
        V oldValue = this.map.putIfAbsent(storeKey, value);
        if (oldValue != null) {
            storeKey.ackDeath();
        }
        return oldValue;
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public boolean remove(Object key, Object value) {
        if (value == null) {
            return false;
        }
        return this.map.remove(createLookupKey(key), value);
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public boolean replace(K key, V oldValue, V newValue) {
        Objects.requireNonNull(newValue);
        return this.map.replace(createLookupKey(key), oldValue, newValue);
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public V replace(K key, V value) {
        Objects.requireNonNull(value);
        return this.map.replace(createLookupKey(key), value);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Collection<V> values() {
        return this.map.values();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        return new AbstractSet<Map.Entry<K, V>>() { // from class: org.apache.tomcat.util.collections.ManagedConcurrentWeakHashMap.1
            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public boolean isEmpty() {
                return ManagedConcurrentWeakHashMap.this.map.isEmpty();
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public int size() {
                return ManagedConcurrentWeakHashMap.this.map.size();
            }

            /* renamed from: org.apache.tomcat.util.collections.ManagedConcurrentWeakHashMap$1$1, reason: invalid class name and collision with other inner class name */
            /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/collections/ManagedConcurrentWeakHashMap$1$1.class */
            class C00001 implements Iterator<Map.Entry<K, V>> {
                private final Iterator<Map.Entry<Key, V>> it;

                C00001() {
                    this.it = ManagedConcurrentWeakHashMap.this.map.entrySet().iterator();
                }

                @Override // java.util.Iterator
                public boolean hasNext() {
                    return this.it.hasNext();
                }

                @Override // java.util.Iterator
                public Map.Entry<K, V> next() {
                    return new Map.Entry<K, V>() { // from class: org.apache.tomcat.util.collections.ManagedConcurrentWeakHashMap.1.1.1
                        private final Map.Entry<Key, V> en;

                        {
                            this.en = C00001.this.it.next();
                        }

                        @Override // java.util.Map.Entry
                        public K getKey() {
                            return (K) this.en.getKey().get();
                        }

                        @Override // java.util.Map.Entry
                        public V getValue() {
                            return this.en.getValue();
                        }

                        @Override // java.util.Map.Entry
                        public V setValue(V value) {
                            Objects.requireNonNull(value);
                            return this.en.setValue(value);
                        }
                    };
                }

                @Override // java.util.Iterator
                public void remove() {
                    this.it.remove();
                }
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
            public Iterator<Map.Entry<K, V>> iterator() {
                return new C00001();
            }
        };
    }
}
