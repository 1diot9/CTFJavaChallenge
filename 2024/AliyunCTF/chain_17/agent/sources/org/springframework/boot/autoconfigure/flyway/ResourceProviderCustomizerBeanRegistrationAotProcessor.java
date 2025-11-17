package org.springframework.boot.autoconfigure.flyway;

import javax.lang.model.element.Modifier;
import org.springframework.aot.generate.GeneratedMethod;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.aot.BeanRegistrationCode;
import org.springframework.beans.factory.aot.BeanRegistrationCodeFragments;
import org.springframework.beans.factory.aot.BeanRegistrationCodeFragmentsDecorator;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.javapoet.CodeBlock;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/ResourceProviderCustomizerBeanRegistrationAotProcessor.class */
class ResourceProviderCustomizerBeanRegistrationAotProcessor implements BeanRegistrationAotProcessor {
    ResourceProviderCustomizerBeanRegistrationAotProcessor() {
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationAotProcessor
    public BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
        if (registeredBean.getBeanClass().equals(ResourceProviderCustomizer.class)) {
            return BeanRegistrationAotContribution.withCustomCodeFragments(codeFragments -> {
                return new AotContribution(codeFragments, registeredBean);
            });
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/ResourceProviderCustomizerBeanRegistrationAotProcessor$AotContribution.class */
    public static class AotContribution extends BeanRegistrationCodeFragmentsDecorator {
        private final RegisteredBean registeredBean;

        protected AotContribution(BeanRegistrationCodeFragments delegate, RegisteredBean registeredBean) {
            super(delegate);
            this.registeredBean = registeredBean;
        }

        @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragmentsDecorator, org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
        public CodeBlock generateInstanceSupplierCode(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode, boolean allowDirectSupplierShortcut) {
            GeneratedMethod generatedMethod = beanRegistrationCode.getMethods().add("getInstance", method -> {
                method.addJavadoc("Get the bean instance for '$L'.", this.registeredBean.getBeanName());
                method.addModifiers(Modifier.PRIVATE, Modifier.STATIC);
                method.returns(NativeImageResourceProviderCustomizer.class);
                CodeBlock.Builder code = CodeBlock.builder();
                code.addStatement("return new $T()", NativeImageResourceProviderCustomizer.class);
                method.addCode(code.build());
            });
            return generatedMethod.toMethodReference().toCodeBlock();
        }
    }
}
