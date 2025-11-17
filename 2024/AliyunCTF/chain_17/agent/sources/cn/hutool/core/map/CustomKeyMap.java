package cn.hutool.core.map;

import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/CustomKeyMap.class */
public abstract class CustomKeyMap<K, V> extends TransMap<K, V> {
    private static final long serialVersionUID = 4043263744224569870L;

    public CustomKeyMap(Map<K, V> emptyMap) {
        super(emptyMap);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // cn.hutool.core.map.TransMap
    protected V customValue(Object obj) {
        return obj;
    }
}
