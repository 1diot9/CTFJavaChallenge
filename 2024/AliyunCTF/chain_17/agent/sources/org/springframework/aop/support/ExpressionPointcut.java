package org.springframework.aop.support;

import org.springframework.aop.Pointcut;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/ExpressionPointcut.class */
public interface ExpressionPointcut extends Pointcut {
    @Nullable
    String getExpression();
}
