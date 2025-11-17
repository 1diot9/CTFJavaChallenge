package org.springframework.aop.scope;

import java.util.function.Predicate;
import javax.lang.model.element.Modifier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aot.generate.GeneratedMethod;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.aot.BeanRegistrationCode;
import org.springframework.beans.factory.aot.BeanRegistrationCodeFragments;
import org.springframework.beans.factory.aot.BeanRegistrationCodeFragmentsDecorator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.ResolvableType;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.CodeBlock;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/scope/ScopedProxyBeanRegistrationAotProcessor.class */
class ScopedProxyBeanRegistrationAotProcessor implements BeanRegistrationAotProcessor {
    private static final Log logger = LogFactory.getLog((Class<?>) ScopedProxyBeanRegistrationAotProcessor.class);

    ScopedProxyBeanRegistrationAotProcessor() {
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationAotProcessor
    public BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
        Class<?> beanClass = registeredBean.getBeanClass();
        if (beanClass.equals(ScopedProxyFactoryBean.class)) {
            String targetBeanName = getTargetBeanName(registeredBean.getMergedBeanDefinition());
            BeanDefinition targetBeanDefinition = getTargetBeanDefinition(registeredBean.getBeanFactory(), targetBeanName);
            if (targetBeanDefinition == null) {
                logger.warn("Could not handle " + ScopedProxyFactoryBean.class.getSimpleName() + ": no target bean definition found with name " + targetBeanName);
                return null;
            }
            return BeanRegistrationAotContribution.withCustomCodeFragments(codeFragments -> {
                return new ScopedProxyBeanRegistrationCodeFragments(codeFragments, registeredBean, targetBeanName, targetBeanDefinition);
            });
        }
        return null;
    }

    @Nullable
    private String getTargetBeanName(BeanDefinition beanDefinition) {
        Object value = beanDefinition.getPropertyValues().get("targetBeanName");
        if (!(value instanceof String)) {
            return null;
        }
        String targetBeanName = (String) value;
        return targetBeanName;
    }

    @Nullable
    private BeanDefinition getTargetBeanDefinition(ConfigurableBeanFactory beanFactory, @Nullable String targetBeanName) {
        if (targetBeanName != null && beanFactory.containsBean(targetBeanName)) {
            return beanFactory.getMergedBeanDefinition(targetBeanName);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/scope/ScopedProxyBeanRegistrationAotProcessor$ScopedProxyBeanRegistrationCodeFragments.class */
    public static class ScopedProxyBeanRegistrationCodeFragments extends BeanRegistrationCodeFragmentsDecorator {
        private static final String REGISTERED_BEAN_PARAMETER_NAME = "registeredBean";
        private final RegisteredBean registeredBean;
        private final String targetBeanName;
        private final BeanDefinition targetBeanDefinition;

        ScopedProxyBeanRegistrationCodeFragments(BeanRegistrationCodeFragments delegate, RegisteredBean registeredBean, String targetBeanName, BeanDefinition targetBeanDefinition) {
            super(delegate);
            this.registeredBean = registeredBean;
            this.targetBeanName = targetBeanName;
            this.targetBeanDefinition = targetBeanDefinition;
        }

        @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragmentsDecorator, org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
        public ClassName getTarget(RegisteredBean registeredBean) {
            return ClassName.get(this.targetBeanDefinition.getResolvableType().toClass());
        }

        @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragmentsDecorator, org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
        public CodeBlock generateNewBeanDefinitionCode(GenerationContext generationContext, ResolvableType beanType, BeanRegistrationCode beanRegistrationCode) {
            return super.generateNewBeanDefinitionCode(generationContext, this.targetBeanDefinition.getResolvableType(), beanRegistrationCode);
        }

        @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragmentsDecorator, org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
        public CodeBlock generateSetBeanDefinitionPropertiesCode(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode, RootBeanDefinition beanDefinition, Predicate<String> attributeFilter) {
            RootBeanDefinition processedBeanDefinition = new RootBeanDefinition(beanDefinition);
            processedBeanDefinition.setTargetType(this.targetBeanDefinition.getResolvableType());
            processedBeanDefinition.getPropertyValues().removePropertyValue("targetBeanName");
            return super.generateSetBeanDefinitionPropertiesCode(generationContext, beanRegistrationCode, processedBeanDefinition, attributeFilter);
        }

        @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragmentsDecorator, org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
        public CodeBlock generateInstanceSupplierCode(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode, boolean allowDirectSupplierShortcut) {
            GeneratedMethod generatedMethod = beanRegistrationCode.getMethods().add("getScopedProxyInstance", method -> {
                method.addJavadoc("Create the scoped proxy bean instance for '$L'.", this.registeredBean.getBeanName());
                method.addModifiers(Modifier.PRIVATE, Modifier.STATIC);
                method.returns(ScopedProxyFactoryBean.class);
                method.addParameter(RegisteredBean.class, REGISTERED_BEAN_PARAMETER_NAME, new Modifier[0]);
                method.addStatement("$T factory = new $T()", ScopedProxyFactoryBean.class, ScopedProxyFactoryBean.class);
                method.addStatement("factory.setTargetBeanName($S)", this.targetBeanName);
                method.addStatement("factory.setBeanFactory($L.getBeanFactory())", REGISTERED_BEAN_PARAMETER_NAME);
                method.addStatement("return factory", new Object[0]);
            });
            return CodeBlock.of("$T.of($L)", InstanceSupplier.class, generatedMethod.toMethodReference().toCodeBlock());
        }
    }
}
