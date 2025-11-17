package org.springframework.instrument.classloading.tomcat;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.core.OverridingClassLoader;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/instrument/classloading/tomcat/TomcatLoadTimeWeaver.class */
public class TomcatLoadTimeWeaver implements LoadTimeWeaver {
    private static final String INSTRUMENTABLE_LOADER_CLASS_NAME = "org.apache.tomcat.InstrumentableClassLoader";
    private final ClassLoader classLoader;
    private final Method addTransformerMethod;
    private final Method copyMethod;

    public TomcatLoadTimeWeaver() {
        this(ClassUtils.getDefaultClassLoader());
    }

    public TomcatLoadTimeWeaver(@Nullable ClassLoader classLoader) {
        Class<?> instrumentableLoaderClass;
        Assert.notNull(classLoader, "ClassLoader must not be null");
        this.classLoader = classLoader;
        try {
            instrumentableLoaderClass = classLoader.loadClass(INSTRUMENTABLE_LOADER_CLASS_NAME);
            instrumentableLoaderClass = instrumentableLoaderClass.isInstance(classLoader) ? instrumentableLoaderClass : classLoader.getClass();
        } catch (ClassNotFoundException e) {
            instrumentableLoaderClass = classLoader.getClass();
        }
        try {
            this.addTransformerMethod = instrumentableLoaderClass.getMethod("addTransformer", ClassFileTransformer.class);
            Method copyMethod = ClassUtils.getMethodIfAvailable(instrumentableLoaderClass, "copyWithoutTransformers", new Class[0]);
            this.copyMethod = copyMethod == null ? instrumentableLoaderClass.getMethod("getThrowawayClassLoader", new Class[0]) : copyMethod;
        } catch (Throwable ex) {
            throw new IllegalStateException("Could not initialize TomcatLoadTimeWeaver because Tomcat API classes are not available", ex);
        }
    }

    @Override // org.springframework.instrument.classloading.LoadTimeWeaver
    public void addTransformer(ClassFileTransformer transformer) {
        try {
            this.addTransformerMethod.invoke(this.classLoader, transformer);
        } catch (InvocationTargetException ex) {
            throw new IllegalStateException("Tomcat addTransformer method threw exception", ex.getCause());
        } catch (Throwable ex2) {
            throw new IllegalStateException("Could not invoke Tomcat addTransformer method", ex2);
        }
    }

    @Override // org.springframework.instrument.classloading.LoadTimeWeaver
    public ClassLoader getInstrumentableClassLoader() {
        return this.classLoader;
    }

    @Override // org.springframework.instrument.classloading.LoadTimeWeaver
    public ClassLoader getThrowawayClassLoader() {
        try {
            return new OverridingClassLoader(this.classLoader, (ClassLoader) this.copyMethod.invoke(this.classLoader, new Object[0]));
        } catch (InvocationTargetException ex) {
            throw new IllegalStateException("Tomcat copy method threw exception", ex.getCause());
        } catch (Throwable ex2) {
            throw new IllegalStateException("Could not invoke Tomcat copy method", ex2);
        }
    }
}
