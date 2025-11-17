package cn.hutool.core.lang;

import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ObjectUtil;
import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/ResourceClassLoader.class */
public class ResourceClassLoader<T extends Resource> extends SecureClassLoader {
    private final Map<String, T> resourceMap;
    private final Map<String, Class<?>> cacheClassMap;

    public ResourceClassLoader(ClassLoader parentClassLoader, Map<String, T> resourceMap) {
        super((ClassLoader) ObjectUtil.defaultIfNull(parentClassLoader, (Supplier<? extends ClassLoader>) ClassLoaderUtil::getClassLoader));
        this.resourceMap = (Map) ObjectUtil.defaultIfNull((HashMap) resourceMap, new HashMap());
        this.cacheClassMap = new HashMap();
    }

    public ResourceClassLoader<T> addResource(T resource) {
        this.resourceMap.put(resource.getName(), resource);
        return this;
    }

    @Override // java.lang.ClassLoader
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> clazz = this.cacheClassMap.computeIfAbsent(name, this::defineByName);
        if (clazz == null) {
            return super.findClass(name);
        }
        return clazz;
    }

    private Class<?> defineByName(String name) {
        Resource resource = this.resourceMap.get(name);
        if (null != resource) {
            byte[] bytes = resource.readBytes();
            return defineClass(name, bytes, 0, bytes.length);
        }
        return null;
    }
}
