package org.h2.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/SoftValuesHashMap.class */
public class SoftValuesHashMap<K, V> extends AbstractMap<K, V> {
    private final ReferenceQueue<V> queue = new ReferenceQueue<>();
    private final Map<K, SoftValue<V>> map = new HashMap();

    private void processQueue() {
        while (true) {
            Reference<? extends V> poll = this.queue.poll();
            if (poll == null) {
                return;
            }
            this.map.remove(((SoftValue) poll).key);
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V get(Object obj) {
        processQueue();
        SoftValue<V> softValue = this.map.get(obj);
        if (softValue == null) {
            return null;
        }
        return softValue.get();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V put(K k, V v) {
        processQueue();
        SoftValue<V> put = this.map.put(k, new SoftValue<>(v, this.queue, k));
        if (put == null) {
            return null;
        }
        return put.get();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V remove(Object obj) {
        processQueue();
        SoftValue<V> remove = this.map.remove(obj);
        if (remove == null) {
            return null;
        }
        return remove.get();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public void clear() {
        processQueue();
        this.map.clear();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/SoftValuesHashMap$SoftValue.class */
    public static class SoftValue<T> extends SoftReference<T> {
        final Object key;

        public SoftValue(T t, ReferenceQueue<T> referenceQueue, Object obj) {
            super(t, referenceQueue);
            this.key = obj;
        }
    }
}
