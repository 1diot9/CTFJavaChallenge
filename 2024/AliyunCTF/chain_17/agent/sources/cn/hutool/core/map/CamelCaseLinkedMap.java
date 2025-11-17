package cn.hutool.core.map;

import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/CamelCaseLinkedMap.class */
public class CamelCaseLinkedMap<K, V> extends CamelCaseMap<K, V> {
    private static final long serialVersionUID = 4043263744224569870L;

    public CamelCaseLinkedMap() {
        this(16);
    }

    public CamelCaseLinkedMap(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    public CamelCaseLinkedMap(Map<? extends K, ? extends V> m) {
        this(0.75f, m);
    }

    public CamelCaseLinkedMap(float loadFactor, Map<? extends K, ? extends V> m) {
        this(m.size(), loadFactor);
        putAll(m);
    }

    public CamelCaseLinkedMap(int initialCapacity, float loadFactor) {
        super(new LinkedHashMap(initialCapacity, loadFactor));
    }
}
