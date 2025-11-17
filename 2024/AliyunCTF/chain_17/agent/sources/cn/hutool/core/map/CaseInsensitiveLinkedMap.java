package cn.hutool.core.map;

import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/CaseInsensitiveLinkedMap.class */
public class CaseInsensitiveLinkedMap<K, V> extends CaseInsensitiveMap<K, V> {
    private static final long serialVersionUID = 4043263744224569870L;

    public CaseInsensitiveLinkedMap() {
        this(16);
    }

    public CaseInsensitiveLinkedMap(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    public CaseInsensitiveLinkedMap(Map<? extends K, ? extends V> m) {
        this(0.75f, m);
    }

    public CaseInsensitiveLinkedMap(float loadFactor, Map<? extends K, ? extends V> m) {
        this(m.size(), loadFactor);
        putAll(m);
    }

    public CaseInsensitiveLinkedMap(int initialCapacity, float loadFactor) {
        super(new LinkedHashMap(initialCapacity, loadFactor));
    }
}
