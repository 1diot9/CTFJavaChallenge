package org.springframework.aop.aspectj.annotation;

import java.util.List;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AbstractAspectJAdvice;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.aot.BeanFactoryInitializationCode;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/annotation/AspectJBeanFactoryInitializationAotProcessor.class */
class AspectJBeanFactoryInitializationAotProcessor implements BeanFactoryInitializationAotProcessor {
    private static final boolean aspectJPresent = ClassUtils.isPresent("org.aspectj.lang.annotation.Pointcut", AspectJBeanFactoryInitializationAotProcessor.class.getClassLoader());

    AspectJBeanFactoryInitializationAotProcessor() {
    }

    @Override // org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor
    @Nullable
    public BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {
        if (aspectJPresent) {
            return AspectDelegate.processAheadOfTime(beanFactory);
        }
        return null;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/annotation/AspectJBeanFactoryInitializationAotProcessor$AspectDelegate.class */
    private static class AspectDelegate {
        private AspectDelegate() {
        }

        @Nullable
        private static AspectContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {
            BeanFactoryAspectJAdvisorsBuilder builder = new BeanFactoryAspectJAdvisorsBuilder(beanFactory);
            List<Advisor> advisors = builder.buildAspectJAdvisors();
            if (advisors.isEmpty()) {
                return null;
            }
            return new AspectContribution(advisors);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/annotation/AspectJBeanFactoryInitializationAotProcessor$AspectContribution.class */
    public static class AspectContribution implements BeanFactoryInitializationAotContribution {
        private final List<Advisor> advisors;

        public AspectContribution(List<Advisor> advisors) {
            this.advisors = advisors;
        }

        @Override // org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution
        public void applyTo(GenerationContext generationContext, BeanFactoryInitializationCode beanFactoryInitializationCode) {
            ReflectionHints reflectionHints = generationContext.getRuntimeHints().reflection();
            for (Advisor advisor : this.advisors) {
                Advice advice = advisor.getAdvice();
                if (advice instanceof AbstractAspectJAdvice) {
                    AbstractAspectJAdvice aspectJAdvice = (AbstractAspectJAdvice) advice;
                    reflectionHints.registerMethod(aspectJAdvice.getAspectJAdviceMethod(), ExecutableMode.INVOKE);
                }
            }
        }
    }
}
