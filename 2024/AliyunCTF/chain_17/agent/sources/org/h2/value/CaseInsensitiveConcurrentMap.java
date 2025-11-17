package org.h2.value;

import java.util.concurrent.ConcurrentHashMap;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/CaseInsensitiveConcurrentMap.class */
public class CaseInsensitiveConcurrentMap<V> extends ConcurrentHashMap<String, V> {
    private static final long serialVersionUID = 1;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.concurrent.ConcurrentHashMap, java.util.Map, java.util.concurrent.ConcurrentMap
    public /* bridge */ /* synthetic */ Object putIfAbsent(Object obj, Object obj2) {
        return putIfAbsent((String) obj, (String) obj2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.concurrent.ConcurrentHashMap, java.util.AbstractMap, java.util.Map
    public /* bridge */ /* synthetic */ Object put(Object obj, Object obj2) {
        return put((String) obj, (String) obj2);
    }

    @Override // java.util.concurrent.ConcurrentHashMap, java.util.AbstractMap, java.util.Map
    public V get(Object obj) {
        return (V) super.get(StringUtils.toUpperEnglish((String) obj));
    }

    public V put(String str, V v) {
        return (V) super.put((CaseInsensitiveConcurrentMap<V>) StringUtils.toUpperEnglish(str), (String) v);
    }

    public V putIfAbsent(String str, V v) {
        return (V) super.putIfAbsent((CaseInsensitiveConcurrentMap<V>) StringUtils.toUpperEnglish(str), (String) v);
    }

    @Override // java.util.concurrent.ConcurrentHashMap, java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object obj) {
        return super.containsKey(StringUtils.toUpperEnglish((String) obj));
    }

    @Override // java.util.concurrent.ConcurrentHashMap, java.util.AbstractMap, java.util.Map
    public V remove(Object obj) {
        return (V) super.remove(StringUtils.toUpperEnglish((String) obj));
    }
}
