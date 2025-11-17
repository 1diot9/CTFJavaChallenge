package cn.hutool.core.map;

import java.util.Map;
import java.util.function.Function;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/FuncKeyMap.class */
public class FuncKeyMap<K, V> extends CustomKeyMap<K, V> {
    private static final long serialVersionUID = 1;
    private final Function<Object, K> keyFunc;

    public FuncKeyMap(Map<K, V> emptyMap, Function<Object, K> keyFunc) {
        super(emptyMap);
        this.keyFunc = keyFunc;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // cn.hutool.core.map.TransMap
    protected K customKey(Object obj) {
        if (null != this.keyFunc) {
            return this.keyFunc.apply(obj);
        }
        return obj;
    }
}
