package cn.hutool.core.bean;

import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.map.WeakConcurrentMap;
import java.util.function.Function;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/bean/BeanDescCache.class */
public enum BeanDescCache {
    INSTANCE;

    private final WeakConcurrentMap<Class<?>, BeanDesc> bdCache = new WeakConcurrentMap<>();

    BeanDescCache() {
    }

    public BeanDesc getBeanDesc(Class<?> beanClass, Func0<BeanDesc> supplier) {
        return this.bdCache.computeIfAbsent((WeakConcurrentMap<Class<?>, BeanDesc>) beanClass, (Function<? super WeakConcurrentMap<Class<?>, BeanDesc>, ? extends BeanDesc>) key -> {
            return (BeanDesc) supplier.callWithRuntimeException();
        });
    }

    public void clear() {
        this.bdCache.clear();
    }
}
