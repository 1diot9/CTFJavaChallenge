package cn.hutool.core.map;

import cn.hutool.core.util.ReferenceUtil;
import java.lang.ref.Reference;
import java.util.concurrent.ConcurrentMap;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/WeakConcurrentMap.class */
public class WeakConcurrentMap<K, V> extends ReferenceConcurrentMap<K, V> {
    public WeakConcurrentMap() {
        this(new SafeConcurrentHashMap());
    }

    public WeakConcurrentMap(ConcurrentMap<Reference<K>, V> raw) {
        super(raw, ReferenceUtil.ReferenceType.WEAK);
    }
}
