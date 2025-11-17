package org.springframework.context.aot;

import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.aot.BeanRegistrationCode;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.core.KotlinDetector;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/aot/KotlinReflectionBeanRegistrationAotProcessor.class */
class KotlinReflectionBeanRegistrationAotProcessor implements BeanRegistrationAotProcessor {
    KotlinReflectionBeanRegistrationAotProcessor() {
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationAotProcessor
    @Nullable
    public BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
        Class<?> beanClass = registeredBean.getBeanClass();
        if (KotlinDetector.isKotlinType(beanClass)) {
            return new AotContribution(beanClass);
        }
        return null;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/aot/KotlinReflectionBeanRegistrationAotProcessor$AotContribution.class */
    private static class AotContribution implements BeanRegistrationAotContribution {
        private final Class<?> beanClass;

        public AotContribution(Class<?> beanClass) {
            this.beanClass = beanClass;
        }

        @Override // org.springframework.beans.factory.aot.BeanRegistrationAotContribution
        public void applyTo(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode) {
            registerHints(this.beanClass, generationContext.getRuntimeHints());
        }

        private void registerHints(Class<?> type, RuntimeHints runtimeHints) {
            if (KotlinDetector.isKotlinType(type)) {
                runtimeHints.reflection().registerType(type, MemberCategory.INTROSPECT_DECLARED_METHODS);
            }
            Class<?> superClass = type.getSuperclass();
            if (superClass != null) {
                registerHints(superClass, runtimeHints);
            }
        }
    }
}
