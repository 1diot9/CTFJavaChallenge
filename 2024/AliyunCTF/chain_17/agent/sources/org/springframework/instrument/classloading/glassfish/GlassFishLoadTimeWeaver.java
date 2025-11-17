package org.springframework.instrument.classloading.glassfish;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.core.OverridingClassLoader;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/instrument/classloading/glassfish/GlassFishLoadTimeWeaver.class */
public class GlassFishLoadTimeWeaver implements LoadTimeWeaver {
    private static final String INSTRUMENTABLE_LOADER_CLASS_NAME = "org.glassfish.api.deployment.InstrumentableClassLoader";
    private final ClassLoader classLoader;
    private final Method addTransformerMethod;
    private final Method copyMethod;

    public GlassFishLoadTimeWeaver() {
        this(ClassUtils.getDefaultClassLoader());
    }

    public GlassFishLoadTimeWeaver(@Nullable ClassLoader classLoader) {
        Assert.notNull(classLoader, "ClassLoader must not be null");
        try {
            Class<?> instrumentableLoaderClass = classLoader.loadClass(INSTRUMENTABLE_LOADER_CLASS_NAME);
            this.addTransformerMethod = instrumentableLoaderClass.getMethod("addTransformer", ClassFileTransformer.class);
            this.copyMethod = instrumentableLoaderClass.getMethod("copy", new Class[0]);
            ClassLoader clazzLoader = null;
            ClassLoader classLoader2 = classLoader;
            while (true) {
                ClassLoader cl = classLoader2;
                if (cl == null || clazzLoader != null) {
                    break;
                }
                if (instrumentableLoaderClass.isInstance(cl)) {
                    clazzLoader = cl;
                }
                classLoader2 = cl.getParent();
            }
            if (clazzLoader == null) {
                throw new IllegalArgumentException(classLoader + " and its parents are not suitable ClassLoaders: A [" + instrumentableLoaderClass.getName() + "] implementation is required.");
            }
            this.classLoader = clazzLoader;
        } catch (Throwable ex) {
            throw new IllegalStateException("Could not initialize GlassFishLoadTimeWeaver because GlassFish API classes are not available", ex);
        }
    }

    @Override // org.springframework.instrument.classloading.LoadTimeWeaver
    public void addTransformer(ClassFileTransformer transformer) {
        try {
            this.addTransformerMethod.invoke(this.classLoader, transformer);
        } catch (InvocationTargetException ex) {
            throw new IllegalStateException("GlassFish addTransformer method threw exception", ex.getCause());
        } catch (Throwable ex2) {
            throw new IllegalStateException("Could not invoke GlassFish addTransformer method", ex2);
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
            throw new IllegalStateException("GlassFish copy method threw exception", ex.getCause());
        } catch (Throwable ex2) {
            throw new IllegalStateException("Could not invoke GlassFish copy method", ex2);
        }
    }
}
