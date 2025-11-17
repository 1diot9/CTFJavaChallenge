package org.h2.value;

import java.util.HashMap;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/CaseInsensitiveMap.class */
public class CaseInsensitiveMap<V> extends HashMap<String, V> {
    private static final long serialVersionUID = 1;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.HashMap, java.util.Map
    public /* bridge */ /* synthetic */ Object putIfAbsent(Object obj, Object obj2) {
        return putIfAbsent((String) obj, (String) obj2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public /* bridge */ /* synthetic */ Object put(Object obj, Object obj2) {
        return put((String) obj, (String) obj2);
    }

    public CaseInsensitiveMap() {
    }

    public CaseInsensitiveMap(int i) {
        super(i);
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public V get(Object obj) {
        return (V) super.get(StringUtils.toUpperEnglish((String) obj));
    }

    public V put(String str, V v) {
        return (V) super.put((CaseInsensitiveMap<V>) StringUtils.toUpperEnglish(str), (String) v);
    }

    public V putIfAbsent(String str, V v) {
        return (V) super.putIfAbsent((CaseInsensitiveMap<V>) StringUtils.toUpperEnglish(str), (String) v);
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object obj) {
        return super.containsKey(StringUtils.toUpperEnglish((String) obj));
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public V remove(Object obj) {
        return (V) super.remove(StringUtils.toUpperEnglish((String) obj));
    }
}
