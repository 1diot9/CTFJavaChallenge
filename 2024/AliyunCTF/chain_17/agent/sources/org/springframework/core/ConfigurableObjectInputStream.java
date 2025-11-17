package org.springframework.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/ConfigurableObjectInputStream.class */
public class ConfigurableObjectInputStream extends ObjectInputStream {

    @Nullable
    private final ClassLoader classLoader;
    private final boolean acceptProxyClasses;

    public ConfigurableObjectInputStream(InputStream in, @Nullable ClassLoader classLoader) throws IOException {
        this(in, classLoader, true);
    }

    public ConfigurableObjectInputStream(InputStream in, @Nullable ClassLoader classLoader, boolean acceptProxyClasses) throws IOException {
        super(in);
        this.classLoader = classLoader;
        this.acceptProxyClasses = acceptProxyClasses;
    }

    @Override // java.io.ObjectInputStream
    protected Class<?> resolveClass(ObjectStreamClass classDesc) throws IOException, ClassNotFoundException {
        try {
            if (this.classLoader != null) {
                return ClassUtils.forName(classDesc.getName(), this.classLoader);
            }
            return super.resolveClass(classDesc);
        } catch (ClassNotFoundException ex) {
            return resolveFallbackIfPossible(classDesc.getName(), ex);
        }
    }

    @Override // java.io.ObjectInputStream
    protected Class<?> resolveProxyClass(String[] interfaces) throws IOException, ClassNotFoundException {
        if (!this.acceptProxyClasses) {
            throw new NotSerializableException("Not allowed to accept serialized proxy classes");
        }
        if (this.classLoader != null) {
            Class<?>[] resolvedInterfaces = new Class[interfaces.length];
            for (int i = 0; i < interfaces.length; i++) {
                try {
                    resolvedInterfaces[i] = ClassUtils.forName(interfaces[i], this.classLoader);
                } catch (ClassNotFoundException ex) {
                    resolvedInterfaces[i] = resolveFallbackIfPossible(interfaces[i], ex);
                }
            }
            try {
                return ClassUtils.createCompositeInterface(resolvedInterfaces, this.classLoader);
            } catch (IllegalArgumentException ex2) {
                throw new ClassNotFoundException(null, ex2);
            }
        }
        try {
            return super.resolveProxyClass(interfaces);
        } catch (ClassNotFoundException ex3) {
            Class<?>[] resolvedInterfaces2 = new Class[interfaces.length];
            for (int i2 = 0; i2 < interfaces.length; i2++) {
                resolvedInterfaces2[i2] = resolveFallbackIfPossible(interfaces[i2], ex3);
            }
            return ClassUtils.createCompositeInterface(resolvedInterfaces2, getFallbackClassLoader());
        }
    }

    protected Class<?> resolveFallbackIfPossible(String className, ClassNotFoundException ex) throws IOException, ClassNotFoundException {
        throw ex;
    }

    @Nullable
    protected ClassLoader getFallbackClassLoader() throws IOException {
        return null;
    }
}
