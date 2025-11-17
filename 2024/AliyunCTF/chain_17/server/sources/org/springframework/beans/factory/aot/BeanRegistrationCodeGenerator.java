package org.springframework.beans.factory.aot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.aot.generate.GeneratedMethods;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.generate.MethodReference;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.CodeBlock;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanRegistrationCodeGenerator.class */
class BeanRegistrationCodeGenerator implements BeanRegistrationCode {
    private static final Predicate<String> REJECT_ALL_ATTRIBUTES_FILTER = attribute -> {
        return false;
    };
    private final ClassName className;
    private final GeneratedMethods generatedMethods;
    private final List<MethodReference> instancePostProcessors = new ArrayList();
    private final RegisteredBean registeredBean;
    private final BeanRegistrationCodeFragments codeFragments;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BeanRegistrationCodeGenerator(ClassName className, GeneratedMethods generatedMethods, RegisteredBean registeredBean, BeanRegistrationCodeFragments codeFragments) {
        this.className = className;
        this.generatedMethods = generatedMethods;
        this.registeredBean = registeredBean;
        this.codeFragments = codeFragments;
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationCode
    public ClassName getClassName() {
        return this.className;
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationCode
    public GeneratedMethods getMethods() {
        return this.generatedMethods;
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationCode
    public void addInstancePostProcessor(MethodReference methodReference) {
        Assert.notNull(methodReference, "'methodReference' must not be null");
        this.instancePostProcessors.add(methodReference);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CodeBlock generateCode(GenerationContext generationContext) {
        CodeBlock.Builder code = CodeBlock.builder();
        code.add(this.codeFragments.generateNewBeanDefinitionCode(generationContext, this.registeredBean.getBeanType(), this));
        code.add(this.codeFragments.generateSetBeanDefinitionPropertiesCode(generationContext, this, this.registeredBean.getMergedBeanDefinition(), REJECT_ALL_ATTRIBUTES_FILTER));
        CodeBlock instanceSupplierCode = this.codeFragments.generateInstanceSupplierCode(generationContext, this, this.instancePostProcessors.isEmpty());
        code.add(this.codeFragments.generateSetBeanInstanceSupplierCode(generationContext, this, instanceSupplierCode, this.instancePostProcessors));
        code.add(this.codeFragments.generateReturnCode(generationContext, this));
        return code.build();
    }
}
