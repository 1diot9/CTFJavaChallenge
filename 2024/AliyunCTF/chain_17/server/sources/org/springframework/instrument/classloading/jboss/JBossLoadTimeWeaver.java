package org.springframework.instrument.classloading.jboss;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.instrument.classloading.SimpleThrowawayClassLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/instrument/classloading/jboss/JBossLoadTimeWeaver.class */
public class JBossLoadTimeWeaver implements LoadTimeWeaver {
    private static final String DELEGATING_TRANSFORMER_CLASS_NAME = "org.jboss.as.server.deployment.module.DelegatingClassFileTransformer";
    private static final String WRAPPER_TRANSFORMER_CLASS_NAME = "org.jboss.modules.JLIClassTransformer";
    private final ClassLoader classLoader;
    private final Object delegatingTransformer;
    private final Method addTransformer;

    public JBossLoadTimeWeaver() {
        this(ClassUtils.getDefaultClassLoader());
    }

    public JBossLoadTimeWeaver(@Nullable ClassLoader classLoader) {
        Assert.notNull(classLoader, "ClassLoader must not be null");
        this.classLoader = classLoader;
        try {
            Field transformer = ReflectionUtils.findField(classLoader.getClass(), "transformer");
            if (transformer == null) {
                throw new IllegalArgumentException("Could not find 'transformer' field on JBoss ClassLoader: " + classLoader.getClass().getName());
            }
            transformer.setAccessible(true);
            Object suggestedTransformer = transformer.get(classLoader);
            if (suggestedTransformer.getClass().getName().equals(WRAPPER_TRANSFORMER_CLASS_NAME)) {
                Field wrappedTransformer = ReflectionUtils.findField(suggestedTransformer.getClass(), "transformer");
                if (wrappedTransformer == null) {
                    throw new IllegalArgumentException("Could not find 'transformer' field on JBoss JLIClassTransformer: " + suggestedTransformer.getClass().getName());
                }
                wrappedTransformer.setAccessible(true);
                suggestedTransformer = wrappedTransformer.get(suggestedTransformer);
            }
            if (!suggestedTransformer.getClass().getName().equals(DELEGATING_TRANSFORMER_CLASS_NAME)) {
                throw new IllegalStateException("Transformer not of the expected type DelegatingClassFileTransformer: " + suggestedTransformer.getClass().getName());
            }
            this.delegatingTransformer = suggestedTransformer;
            Method addTransformer = ReflectionUtils.findMethod(this.delegatingTransformer.getClass(), "addTransformer", ClassFileTransformer.class);
            if (addTransformer == null) {
                throw new IllegalArgumentException("Could not find 'addTransformer' method on JBoss DelegatingClassFileTransformer: " + this.delegatingTransformer.getClass().getName());
            }
            addTransformer.setAccessible(true);
            this.addTransformer = addTransformer;
        } catch (Throwable ex) {
            throw new IllegalStateException("Could not initialize JBoss LoadTimeWeaver", ex);
        }
    }

    @Override // org.springframework.instrument.classloading.LoadTimeWeaver
    public void addTransformer(ClassFileTransformer transformer) {
        try {
            this.addTransformer.invoke(this.delegatingTransformer, transformer);
        } catch (Throwable ex) {
            throw new IllegalStateException("Could not add transformer on JBoss ClassLoader: " + this.classLoader, ex);
        }
    }

    @Override // org.springframework.instrument.classloading.LoadTimeWeaver
    public ClassLoader getInstrumentableClassLoader() {
        return this.classLoader;
    }

    @Override // org.springframework.instrument.classloading.LoadTimeWeaver
    public ClassLoader getThrowawayClassLoader() {
        return new SimpleThrowawayClassLoader(getInstrumentableClassLoader());
    }
}
