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

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanRegistrationCodeFragments.class */
public interface BeanRegistrationCodeFragments {
    public static final String BEAN_DEFINITION_VARIABLE = "beanDefinition";
    public static final String INSTANCE_SUPPLIER_VARIABLE = "instanceSupplier";

    ClassName getTarget(RegisteredBean registeredBean);

    CodeBlock generateNewBeanDefinitionCode(GenerationContext generationContext, ResolvableType beanType, BeanRegistrationCode beanRegistrationCode);

    CodeBlock generateSetBeanDefinitionPropertiesCode(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode, RootBeanDefinition beanDefinition, Predicate<String> attributeFilter);

    CodeBlock generateSetBeanInstanceSupplierCode(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode, CodeBlock instanceSupplierCode, List<MethodReference> postProcessors);

    CodeBlock generateInstanceSupplierCode(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode, boolean allowDirectSupplierShortcut);

    CodeBlock generateReturnCode(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode);
}
