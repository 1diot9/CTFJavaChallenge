package cn.hutool.core.map.multi;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/multi/SetValueMap.class */
public class SetValueMap<K, V> extends AbsCollValueMap<K, V, Set<V>> {
    private static final long serialVersionUID = 6044017508487827899L;

    public SetValueMap() {
        this(16);
    }

    public SetValueMap(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    public SetValueMap(Map<? extends K, ? extends Collection<V>> m) {
        this(0.75f, m);
    }

    public SetValueMap(float loadFactor, Map<? extends K, ? extends Collection<V>> m) {
        this(m.size(), loadFactor);
        putAllValues(m);
    }

    public SetValueMap(int initialCapacity, float loadFactor) {
        super(new HashMap(initialCapacity, loadFactor));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // cn.hutool.core.map.multi.AbsCollValueMap
    public Set<V> createCollection() {
        return new LinkedHashSet(3);
    }
}
