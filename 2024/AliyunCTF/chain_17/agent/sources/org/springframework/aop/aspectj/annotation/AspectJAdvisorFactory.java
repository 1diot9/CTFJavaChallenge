package org.springframework.aop.aspectj.annotation;

import java.lang.reflect.Method;
import java.util.List;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/annotation/AspectJAdvisorFactory.class */
public interface AspectJAdvisorFactory {
    boolean isAspect(Class<?> clazz);

    void validate(Class<?> aspectClass) throws AopConfigException;

    List<Advisor> getAdvisors(MetadataAwareAspectInstanceFactory aspectInstanceFactory);

    @Nullable
    Advisor getAdvisor(Method candidateAdviceMethod, MetadataAwareAspectInstanceFactory aspectInstanceFactory, int declarationOrder, String aspectName);

    @Nullable
    Advice getAdvice(Method candidateAdviceMethod, AspectJExpressionPointcut expressionPointcut, MetadataAwareAspectInstanceFactory aspectInstanceFactory, int declarationOrder, String aspectName);
}
