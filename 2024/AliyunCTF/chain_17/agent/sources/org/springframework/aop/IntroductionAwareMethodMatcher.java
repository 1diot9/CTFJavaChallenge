package org.springframework.aop;

import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/IntroductionAwareMethodMatcher.class */
public interface IntroductionAwareMethodMatcher extends MethodMatcher {
    boolean matches(Method method, Class<?> targetClass, boolean hasIntroductions);
}
