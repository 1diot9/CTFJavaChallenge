package cn.hutool.core.map.multi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/multi/ListValueMap.class */
public class ListValueMap<K, V> extends AbsCollValueMap<K, V, List<V>> {
    private static final long serialVersionUID = 6044017508487827899L;

    public ListValueMap() {
        this(16);
    }

    public ListValueMap(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    public ListValueMap(Map<? extends K, ? extends Collection<V>> m) {
        this(0.75f, m);
    }

    public ListValueMap(float loadFactor, Map<? extends K, ? extends Collection<V>> m) {
        this(m.size(), loadFactor);
        putAllValues(m);
    }

    public ListValueMap(int initialCapacity, float loadFactor) {
        super(new HashMap(initialCapacity, loadFactor));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // cn.hutool.core.map.multi.AbsCollValueMap
    public List<V> createCollection() {
        return new ArrayList(3);
    }
}
