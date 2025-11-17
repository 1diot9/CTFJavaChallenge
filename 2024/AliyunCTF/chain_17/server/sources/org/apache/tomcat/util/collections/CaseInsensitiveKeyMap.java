package org.apache.tomcat.util.collections;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/collections/CaseInsensitiveKeyMap.class */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String, V> {
    private static final StringManager sm = StringManager.getManager((Class<?>) CaseInsensitiveKeyMap.class);
    private final Map<Key, V> map = new HashMap();

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.AbstractMap, java.util.Map
    public /* bridge */ /* synthetic */ Object put(Object obj, Object obj2) {
        return put((String) obj, (String) obj2);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V get(Object key) {
        return this.map.get(Key.getInstance(key));
    }

    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return this.map.put(caseInsensitiveKey, value);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.AbstractMap, java.util.Map
    public void putAll(Map<? extends String, ? extends V> map) {
        super.putAll(map);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object key) {
        return this.map.containsKey(Key.getInstance(key));
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V remove(Object key) {
        return this.map.remove(Key.getInstance(key));
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<Map.Entry<String, V>> entrySet() {
        return new EntrySet(this.map.entrySet());
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/collections/CaseInsensitiveKeyMap$EntrySet.class */
    private static class EntrySet<V> extends AbstractSet<Map.Entry<String, V>> {
        private final Set<Map.Entry<Key, V>> entrySet;

        EntrySet(Set<Map.Entry<Key, V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Map.Entry<String, V>> iterator() {
            return new EntryIterator(this.entrySet.iterator());
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return this.entrySet.size();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/collections/CaseInsensitiveKeyMap$EntryIterator.class */
    private static class EntryIterator<V> implements Iterator<Map.Entry<String, V>> {
        private final Iterator<Map.Entry<Key, V>> iterator;

        EntryIterator(Iterator<Map.Entry<Key, V>> iterator) {
            this.iterator = iterator;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override // java.util.Iterator
        public Map.Entry<String, V> next() {
            Map.Entry<Key, V> entry = this.iterator.next();
            return new EntryImpl(entry.getKey().getKey(), entry.getValue());
        }

        @Override // java.util.Iterator
        public void remove() {
            this.iterator.remove();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/collections/CaseInsensitiveKeyMap$EntryImpl.class */
    public static class EntryImpl<V> implements Map.Entry<String, V> {
        private final String key;
        private final V value;

        EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Map.Entry
        public String getKey() {
            return this.key;
        }

        @Override // java.util.Map.Entry
        public V getValue() {
            return this.value;
        }

        @Override // java.util.Map.Entry
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/collections/CaseInsensitiveKeyMap$Key.class */
    public static class Key {
        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return this.key;
        }

        public int hashCode() {
            return this.lcKey.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return this.lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}
