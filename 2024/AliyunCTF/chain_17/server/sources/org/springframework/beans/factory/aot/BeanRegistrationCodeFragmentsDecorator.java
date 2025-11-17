package org.springframework.beans.factory.aot;

import java.util.List;
import java.util.function.Predicate;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.generate.MethodReference;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.ResolvableType;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.CodeBlock;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanRegistrationCodeFragmentsDecorator.class */
public class BeanRegistrationCodeFragmentsDecorator implements BeanRegistrationCodeFragments {
    private final BeanRegistrationCodeFragments delegate;

    /* JADX INFO: Access modifiers changed from: protected */
    public BeanRegistrationCodeFragmentsDecorator(BeanRegistrationCodeFragments delegate) {
        Assert.notNull(delegate, "Delegate must not be null");
        this.delegate = delegate;
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
    public ClassName getTarget(RegisteredBean registeredBean) {
        return this.delegate.getTarget(registeredBean);
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
    public CodeBlock generateNewBeanDefinitionCode(GenerationContext generationContext, ResolvableType beanType, BeanRegistrationCode beanRegistrationCode) {
        return this.delegate.generateNewBeanDefinitionCode(generationContext, beanType, beanRegistrationCode);
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
    public CodeBlock generateSetBeanDefinitionPropertiesCode(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode, RootBeanDefinition beanDefinition, Predicate<String> attributeFilter) {
        return this.delegate.generateSetBeanDefinitionPropertiesCode(generationContext, beanRegistrationCode, beanDefinition, attributeFilter);
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
    public CodeBlock generateSetBeanInstanceSupplierCode(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode, CodeBlock instanceSupplierCode, List<MethodReference> postProcessors) {
        return this.delegate.generateSetBeanInstanceSupplierCode(generationContext, beanRegistrationCode, instanceSupplierCode, postProcessors);
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
    public CodeBlock generateInstanceSupplierCode(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode, boolean allowDirectSupplierShortcut) {
        return this.delegate.generateInstanceSupplierCode(generationContext, beanRegistrationCode, allowDirectSupplierShortcut);
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
    public CodeBlock generateReturnCode(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode) {
        return this.delegate.generateReturnCode(generationContext, beanRegistrationCode);
    }
}
