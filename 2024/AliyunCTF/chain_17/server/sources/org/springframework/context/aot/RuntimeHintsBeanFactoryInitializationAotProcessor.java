package org.springframework.context.aot;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.aot.AotServices;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.aot.BeanFactoryInitializationCode;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.log.LogMessage;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/aot/RuntimeHintsBeanFactoryInitializationAotProcessor.class */
class RuntimeHintsBeanFactoryInitializationAotProcessor implements BeanFactoryInitializationAotProcessor {
    private static final Log logger = LogFactory.getLog((Class<?>) RuntimeHintsBeanFactoryInitializationAotProcessor.class);

    @Override // org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor
    public BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {
        Map<Class<? extends RuntimeHintsRegistrar>, RuntimeHintsRegistrar> registrars = (Map) AotServices.factories(beanFactory.getBeanClassLoader()).load(RuntimeHintsRegistrar.class).stream().collect(LinkedHashMap::new, (map, item) -> {
            map.put(item.getClass(), item);
        }, (v0, v1) -> {
            v0.putAll(v1);
        });
        extractFromBeanFactory(beanFactory).forEach(registrarClass -> {
            registrars.computeIfAbsent(registrarClass, BeanUtils::instantiateClass);
        });
        return new RuntimeHintsRegistrarContribution(registrars.values(), beanFactory.getBeanClassLoader());
    }

    private Set<Class<? extends RuntimeHintsRegistrar>> extractFromBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        Set<Class<? extends RuntimeHintsRegistrar>> registrarClasses = new LinkedHashSet<>();
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            beanFactory.findAllAnnotationsOnBean(beanName, ImportRuntimeHints.class, true).forEach(annotation -> {
                registrarClasses.addAll(extractFromBeanDefinition(beanName, annotation));
            });
        }
        return registrarClasses;
    }

    private Set<Class<? extends RuntimeHintsRegistrar>> extractFromBeanDefinition(String beanName, ImportRuntimeHints annotation) {
        Set<Class<? extends RuntimeHintsRegistrar>> registrars = new LinkedHashSet<>();
        for (Class<? extends RuntimeHintsRegistrar> registrarClass : annotation.value()) {
            if (logger.isTraceEnabled()) {
                logger.trace(LogMessage.format("Loaded [%s] registrar from annotated bean [%s]", registrarClass.getCanonicalName(), beanName));
            }
            registrars.add(registrarClass);
        }
        return registrars;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/aot/RuntimeHintsBeanFactoryInitializationAotProcessor$RuntimeHintsRegistrarContribution.class */
    static class RuntimeHintsRegistrarContribution implements BeanFactoryInitializationAotContribution {
        private final Iterable<RuntimeHintsRegistrar> registrars;

        @Nullable
        private final ClassLoader beanClassLoader;

        RuntimeHintsRegistrarContribution(Iterable<RuntimeHintsRegistrar> registrars, @Nullable ClassLoader beanClassLoader) {
            this.registrars = registrars;
            this.beanClassLoader = beanClassLoader;
        }

        @Override // org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution
        public void applyTo(GenerationContext generationContext, BeanFactoryInitializationCode beanFactoryInitializationCode) {
            RuntimeHints hints = generationContext.getRuntimeHints();
            this.registrars.forEach(registrar -> {
                if (RuntimeHintsBeanFactoryInitializationAotProcessor.logger.isTraceEnabled()) {
                    RuntimeHintsBeanFactoryInitializationAotProcessor.logger.trace(LogMessage.format("Processing RuntimeHints contribution from [%s]", registrar.getClass().getCanonicalName()));
                }
                registrar.registerHints(hints, this.beanClassLoader);
            });
        }
    }
}
