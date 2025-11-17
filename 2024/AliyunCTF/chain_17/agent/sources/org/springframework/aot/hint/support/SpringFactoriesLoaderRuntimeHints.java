package org.springframework.aot.hint.support;

import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.log.LogMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/support/SpringFactoriesLoaderRuntimeHints.class */
class SpringFactoriesLoaderRuntimeHints implements RuntimeHintsRegistrar {
    private static final List<String> RESOURCE_LOCATIONS = List.of(SpringFactoriesLoader.FACTORIES_RESOURCE_LOCATION);
    private static final Log logger = LogFactory.getLog((Class<?>) SpringFactoriesLoaderRuntimeHints.class);

    SpringFactoriesLoaderRuntimeHints() {
    }

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
        ClassLoader classLoaderToUse = classLoader != null ? classLoader : SpringFactoriesLoaderRuntimeHints.class.getClassLoader();
        for (String resourceLocation : RESOURCE_LOCATIONS) {
            registerHints(hints, classLoaderToUse, resourceLocation);
        }
    }

    private void registerHints(RuntimeHints hints, ClassLoader classLoader, String resourceLocation) {
        hints.resources().registerPattern(resourceLocation);
        Map<String, List<String>> factories = ExtendedSpringFactoriesLoader.accessLoadFactoriesResource(classLoader, resourceLocation);
        factories.forEach((factoryClassName, implementationClassNames) -> {
            registerHints(hints, classLoader, factoryClassName, implementationClassNames);
        });
    }

    private void registerHints(RuntimeHints hints, ClassLoader classLoader, String factoryClassName, List<String> implementationClassNames) {
        Class<?> factoryClass = resolveClassName(classLoader, factoryClassName);
        if (factoryClass == null) {
            if (logger.isTraceEnabled()) {
                logger.trace(LogMessage.format("Skipping factories for [%s]", factoryClassName));
                return;
            }
            return;
        }
        if (logger.isTraceEnabled()) {
            logger.trace(LogMessage.format("Processing factories for [%s]", factoryClassName));
        }
        hints.reflection().registerType(factoryClass, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
        for (String implementationClassName : implementationClassNames) {
            Class<?> implementationType = resolveClassName(classLoader, implementationClassName);
            if (logger.isTraceEnabled()) {
                logger.trace(LogMessage.format("%s factory type [%s] and implementation [%s]", implementationType != null ? "Processing" : "Skipping", factoryClassName, implementationClassName));
            }
            if (implementationType != null) {
                hints.reflection().registerType(implementationType, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
            }
        }
    }

    @Nullable
    private Class<?> resolveClassName(ClassLoader classLoader, String factoryClassName) {
        try {
            Class<?> clazz = ClassUtils.resolveClassName(factoryClassName, classLoader);
            clazz.getDeclaredConstructors();
            return clazz;
        } catch (Throwable th) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/support/SpringFactoriesLoaderRuntimeHints$ExtendedSpringFactoriesLoader.class */
    public static class ExtendedSpringFactoriesLoader extends SpringFactoriesLoader {
        ExtendedSpringFactoriesLoader(@Nullable ClassLoader classLoader, Map<String, List<String>> factories) {
            super(classLoader, factories);
        }

        static Map<String, List<String>> accessLoadFactoriesResource(ClassLoader classLoader, String resourceLocation) {
            return SpringFactoriesLoader.loadFactoriesResource(classLoader, resourceLocation);
        }
    }
}
