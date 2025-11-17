package org.springframework.boot.context.properties;

import java.util.ArrayList;
import java.util.List;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.aot.BeanFactoryInitializationCode;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.bind.BindMethod;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.BindableRuntimeHintsRegistrar;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/ConfigurationPropertiesBeanFactoryInitializationAotProcessor.class */
class ConfigurationPropertiesBeanFactoryInitializationAotProcessor implements BeanFactoryInitializationAotProcessor {
    ConfigurationPropertiesBeanFactoryInitializationAotProcessor() {
    }

    @Override // org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor
    public ConfigurationPropertiesReflectionHintsContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {
        BindMethod bindMethod;
        String[] beanNames = beanFactory.getBeanNamesForAnnotation(ConfigurationProperties.class);
        List<Bindable<?>> bindables = new ArrayList<>();
        for (String beanName : beanNames) {
            Class<?> beanType = beanFactory.getType(beanName, false);
            if (beanType != null) {
                if (beanFactory.containsBeanDefinition(beanName)) {
                    bindMethod = (BindMethod) beanFactory.getBeanDefinition(beanName).getAttribute(BindMethod.class.getName());
                } else {
                    bindMethod = null;
                }
                BindMethod bindMethod2 = bindMethod;
                bindables.add(Bindable.of(ClassUtils.getUserClass(beanType)).withBindMethod(bindMethod2 != null ? bindMethod2 : BindMethod.JAVA_BEAN));
            }
        }
        if (bindables.isEmpty()) {
            return null;
        }
        return new ConfigurationPropertiesReflectionHintsContribution(bindables);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/ConfigurationPropertiesBeanFactoryInitializationAotProcessor$ConfigurationPropertiesReflectionHintsContribution.class */
    public static final class ConfigurationPropertiesReflectionHintsContribution implements BeanFactoryInitializationAotContribution {
        private final List<Bindable<?>> bindables;

        private ConfigurationPropertiesReflectionHintsContribution(List<Bindable<?>> bindables) {
            this.bindables = bindables;
        }

        @Override // org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution
        public void applyTo(GenerationContext generationContext, BeanFactoryInitializationCode beanFactoryInitializationCode) {
            BindableRuntimeHintsRegistrar.forBindables(this.bindables).registerHints(generationContext.getRuntimeHints());
        }

        Iterable<Bindable<?>> getBindables() {
            return this.bindables;
        }
    }
}
