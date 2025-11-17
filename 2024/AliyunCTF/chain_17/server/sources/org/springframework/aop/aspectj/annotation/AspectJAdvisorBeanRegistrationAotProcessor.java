package org.springframework.aop.aspectj.annotation;

import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.aot.BeanRegistrationCode;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/annotation/AspectJAdvisorBeanRegistrationAotProcessor.class */
class AspectJAdvisorBeanRegistrationAotProcessor implements BeanRegistrationAotProcessor {
    private static final boolean aspectjPresent = ClassUtils.isPresent("org.aspectj.lang.annotation.Pointcut", AspectJAdvisorBeanRegistrationAotProcessor.class.getClassLoader());

    AspectJAdvisorBeanRegistrationAotProcessor() {
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationAotProcessor
    public BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
        if (aspectjPresent) {
            Class<?> beanClass = registeredBean.getBeanClass();
            if (AbstractAspectJAdvisorFactory.compiledByAjc(beanClass)) {
                return new AspectJAdvisorContribution(beanClass);
            }
            return null;
        }
        return null;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/annotation/AspectJAdvisorBeanRegistrationAotProcessor$AspectJAdvisorContribution.class */
    private static class AspectJAdvisorContribution implements BeanRegistrationAotContribution {
        private final Class<?> beanClass;

        public AspectJAdvisorContribution(Class<?> beanClass) {
            this.beanClass = beanClass;
        }

        @Override // org.springframework.beans.factory.aot.BeanRegistrationAotContribution
        public void applyTo(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode) {
            generationContext.getRuntimeHints().reflection().registerType(this.beanClass, MemberCategory.DECLARED_FIELDS);
        }
    }
}
