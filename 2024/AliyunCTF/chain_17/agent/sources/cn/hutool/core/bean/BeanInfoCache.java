package cn.hutool.core.bean;

import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.map.ReferenceConcurrentMap;
import cn.hutool.core.map.WeakConcurrentMap;
import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.function.Function;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/bean/BeanInfoCache.class */
public enum BeanInfoCache {
    INSTANCE;

    private final WeakConcurrentMap<Class<?>, Map<String, PropertyDescriptor>> pdCache = new WeakConcurrentMap<>();
    private final WeakConcurrentMap<Class<?>, Map<String, PropertyDescriptor>> ignoreCasePdCache = new WeakConcurrentMap<>();

    BeanInfoCache() {
    }

    public Map<String, PropertyDescriptor> getPropertyDescriptorMap(Class<?> beanClass, boolean ignoreCase) {
        return getCache(ignoreCase).get(beanClass);
    }

    public Map<String, PropertyDescriptor> getPropertyDescriptorMap(Class<?> beanClass, boolean ignoreCase, Func0<Map<String, PropertyDescriptor>> supplier) {
        return getCache(ignoreCase).computeIfAbsent((ReferenceConcurrentMap<Class<?>, Map<String, PropertyDescriptor>>) beanClass, (Function<? super ReferenceConcurrentMap<Class<?>, Map<String, PropertyDescriptor>>, ? extends Map<String, PropertyDescriptor>>) key -> {
            return (Map) supplier.callWithRuntimeException();
        });
    }

    public void putPropertyDescriptorMap(Class<?> beanClass, Map<String, PropertyDescriptor> fieldNamePropertyDescriptorMap, boolean ignoreCase) {
        getCache(ignoreCase).put(beanClass, fieldNamePropertyDescriptorMap);
    }

    public void clear() {
        this.pdCache.clear();
        this.ignoreCasePdCache.clear();
    }

    private ReferenceConcurrentMap<Class<?>, Map<String, PropertyDescriptor>> getCache(boolean ignoreCase) {
        return ignoreCase ? this.ignoreCasePdCache : this.pdCache;
    }
}
