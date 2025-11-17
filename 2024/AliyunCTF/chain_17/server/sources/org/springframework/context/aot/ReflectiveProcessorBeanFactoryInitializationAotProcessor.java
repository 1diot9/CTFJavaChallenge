package org.springframework.context.aot;

import java.util.Arrays;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.annotation.ReflectiveRuntimeHintsRegistrar;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.aot.BeanFactoryInitializationCode;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RegisteredBean;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/aot/ReflectiveProcessorBeanFactoryInitializationAotProcessor.class */
class ReflectiveProcessorBeanFactoryInitializationAotProcessor implements BeanFactoryInitializationAotProcessor {
    private static final ReflectiveRuntimeHintsRegistrar REGISTRAR = new ReflectiveRuntimeHintsRegistrar();

    ReflectiveProcessorBeanFactoryInitializationAotProcessor() {
    }

    @Override // org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor
    public BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {
        Class<?>[] beanTypes = (Class[]) Arrays.stream(beanFactory.getBeanDefinitionNames()).map(beanName -> {
            return RegisteredBean.of(beanFactory, beanName).getBeanClass();
        }).toArray(x$0 -> {
            return new Class[x$0];
        });
        return new ReflectiveProcessorBeanFactoryInitializationAotContribution(beanTypes);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/aot/ReflectiveProcessorBeanFactoryInitializationAotProcessor$ReflectiveProcessorBeanFactoryInitializationAotContribution.class */
    private static class ReflectiveProcessorBeanFactoryInitializationAotContribution implements BeanFactoryInitializationAotContribution {
        private final Class<?>[] types;

        public ReflectiveProcessorBeanFactoryInitializationAotContribution(Class<?>[] types) {
            this.types = types;
        }

        @Override // org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution
        public void applyTo(GenerationContext generationContext, BeanFactoryInitializationCode beanFactoryInitializationCode) {
            RuntimeHints runtimeHints = generationContext.getRuntimeHints();
            ReflectiveProcessorBeanFactoryInitializationAotProcessor.REGISTRAR.registerRuntimeHints(runtimeHints, this.types);
        }
    }
}
