package org.springframework.aop.aspectj;

import org.springframework.core.Ordered;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/AspectJPrecedenceInformation.class */
public interface AspectJPrecedenceInformation extends Ordered {
    String getAspectName();

    int getDeclarationOrder();

    boolean isBeforeAdvice();

    boolean isAfterAdvice();
}
