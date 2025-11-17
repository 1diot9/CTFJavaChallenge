package cn.hutool.core.map;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/FuncMap.class */
public class FuncMap<K, V> extends TransMap<K, V> {
    private static final long serialVersionUID = 1;
    private final Function<Object, K> keyFunc;
    private final Function<Object, V> valueFunc;

    public FuncMap(Supplier<Map<K, V>> mapFactory, Function<Object, K> keyFunc, Function<Object, V> valueFunc) {
        this(mapFactory.get(), keyFunc, valueFunc);
    }

    public FuncMap(Map<K, V> emptyMap, Function<Object, K> keyFunc, Function<Object, V> valueFunc) {
        super(emptyMap);
        this.keyFunc = keyFunc;
        this.valueFunc = valueFunc;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // cn.hutool.core.map.TransMap
    protected K customKey(Object obj) {
        if (null != this.keyFunc) {
            return this.keyFunc.apply(obj);
        }
        return obj;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // cn.hutool.core.map.TransMap
    protected V customValue(Object obj) {
        if (null != this.valueFunc) {
            return this.valueFunc.apply(obj);
        }
        return obj;
    }
}
