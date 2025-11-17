package org.springframework.boot.context.properties;

import java.util.Objects;
import java.util.function.Predicate;
import javax.lang.model.element.Modifier;
import org.springframework.aot.generate.GeneratedMethod;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.aot.BeanRegistrationCode;
import org.springframework.beans.factory.aot.BeanRegistrationCodeFragments;
import org.springframework.beans.factory.aot.BeanRegistrationCodeFragmentsDecorator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.BindMethod;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.CodeBlock;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/ConfigurationPropertiesBeanRegistrationAotProcessor.class */
class ConfigurationPropertiesBeanRegistrationAotProcessor implements BeanRegistrationAotProcessor {
    ConfigurationPropertiesBeanRegistrationAotProcessor() {
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationAotProcessor
    public BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
        if (!isImmutableConfigurationPropertiesBeanDefinition(registeredBean.getMergedBeanDefinition())) {
            return null;
        }
        return BeanRegistrationAotContribution.withCustomCodeFragments(codeFragments -> {
            return new ConfigurationPropertiesBeanRegistrationCodeFragments(codeFragments, registeredBean);
        });
    }

    private boolean isImmutableConfigurationPropertiesBeanDefinition(BeanDefinition beanDefinition) {
        return BindMethod.VALUE_OBJECT.equals(BindMethodAttribute.get(beanDefinition));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/ConfigurationPropertiesBeanRegistrationAotProcessor$ConfigurationPropertiesBeanRegistrationCodeFragments.class */
    public static class ConfigurationPropertiesBeanRegistrationCodeFragments extends BeanRegistrationCodeFragmentsDecorator {
        private static final String REGISTERED_BEAN_PARAMETER_NAME = "registeredBean";
        private final RegisteredBean registeredBean;

        ConfigurationPropertiesBeanRegistrationCodeFragments(BeanRegistrationCodeFragments codeFragments, RegisteredBean registeredBean) {
            super(codeFragments);
            this.registeredBean = registeredBean;
        }

        @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragmentsDecorator, org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
        public CodeBlock generateSetBeanDefinitionPropertiesCode(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode, RootBeanDefinition beanDefinition, Predicate<String> attributeFilter) {
            String str = BindMethodAttribute.NAME;
            Objects.requireNonNull(str);
            return super.generateSetBeanDefinitionPropertiesCode(generationContext, beanRegistrationCode, beanDefinition, attributeFilter.or((v1) -> {
                return r5.equals(v1);
            }));
        }

        @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragmentsDecorator, org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
        public ClassName getTarget(RegisteredBean registeredBean) {
            return ClassName.get(this.registeredBean.getBeanClass());
        }

        @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragmentsDecorator, org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
        public CodeBlock generateInstanceSupplierCode(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode, boolean allowDirectSupplierShortcut) {
            GeneratedMethod generatedMethod = beanRegistrationCode.getMethods().add("getInstance", method -> {
                Class<?> beanClass = this.registeredBean.getBeanClass();
                method.addJavadoc("Get the bean instance for '$L'.", this.registeredBean.getBeanName()).addModifiers(Modifier.PRIVATE, Modifier.STATIC).returns(beanClass).addParameter(RegisteredBean.class, REGISTERED_BEAN_PARAMETER_NAME, new Modifier[0]).addStatement("$T beanFactory = registeredBean.getBeanFactory()", BeanFactory.class).addStatement("$T beanName = registeredBean.getBeanName()", String.class).addStatement("$T<?> beanClass = registeredBean.getBeanClass()", Class.class).addStatement("return ($T) $T.from(beanFactory, beanName, beanClass)", beanClass, ConstructorBound.class);
            });
            return CodeBlock.of("$T.of($T::$L)", InstanceSupplier.class, beanRegistrationCode.getClassName(), generatedMethod.getName());
        }
    }
}
