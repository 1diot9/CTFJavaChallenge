package org.springframework.context.support;

import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.DecoratingClassLoader;
import org.springframework.core.OverridingClassLoader;
import org.springframework.core.SmartClassLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/ContextTypeMatchClassLoader.class */
class ContextTypeMatchClassLoader extends DecoratingClassLoader implements SmartClassLoader {

    @Nullable
    private static final Method findLoadedClassMethod;
    private final Map<String, byte[]> bytesCache;

    static {
        Method method;
        ClassLoader.registerAsParallelCapable();
        try {
            method = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
            ReflectionUtils.makeAccessible(method);
        } catch (Throwable ex) {
            method = null;
            LogFactory.getLog((Class<?>) ContextTypeMatchClassLoader.class).debug("ClassLoader.findLoadedClass not accessible -> will always override requested class", ex);
        }
        findLoadedClassMethod = method;
    }

    public ContextTypeMatchClassLoader(@Nullable ClassLoader parent) {
        super(parent);
        this.bytesCache = new ConcurrentHashMap(256);
    }

    @Override // java.lang.ClassLoader
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return new ContextOverridingClassLoader(getParent()).loadClass(name);
    }

    @Override // org.springframework.core.SmartClassLoader
    public boolean isClassReloadable(Class<?> clazz) {
        return clazz.getClassLoader() instanceof ContextOverridingClassLoader;
    }

    @Override // org.springframework.core.SmartClassLoader
    public Class<?> publicDefineClass(String name, byte[] b, @Nullable ProtectionDomain protectionDomain) {
        return defineClass(name, b, 0, b.length, protectionDomain);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/ContextTypeMatchClassLoader$ContextOverridingClassLoader.class */
    private class ContextOverridingClassLoader extends OverridingClassLoader {
        public ContextOverridingClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override // org.springframework.core.OverridingClassLoader
        protected boolean isEligibleForOverriding(String className) {
            if (isExcluded(className) || ContextTypeMatchClassLoader.this.isExcluded(className)) {
                return false;
            }
            if (ContextTypeMatchClassLoader.findLoadedClassMethod != null) {
                ClassLoader parent = getParent();
                while (true) {
                    ClassLoader parent2 = parent;
                    if (parent2 == null) {
                        return true;
                    }
                    if (ReflectionUtils.invokeMethod(ContextTypeMatchClassLoader.findLoadedClassMethod, parent2, className) != null) {
                        return false;
                    }
                    parent = parent2.getParent();
                }
            } else {
                return true;
            }
        }

        @Override // org.springframework.core.OverridingClassLoader
        protected Class<?> loadClassForOverriding(String name) throws ClassNotFoundException {
            byte[] bytes = ContextTypeMatchClassLoader.this.bytesCache.get(name);
            if (bytes == null) {
                bytes = loadBytesForClass(name);
                if (bytes != null) {
                    ContextTypeMatchClassLoader.this.bytesCache.put(name, bytes);
                } else {
                    return null;
                }
            }
            return defineClass(name, bytes, 0, bytes.length);
        }
    }
}
