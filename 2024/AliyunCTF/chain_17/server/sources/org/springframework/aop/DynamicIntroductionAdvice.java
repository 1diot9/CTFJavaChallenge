package org.springframework.aop;

import org.aopalliance.aop.Advice;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/DynamicIntroductionAdvice.class */
public interface DynamicIntroductionAdvice extends Advice {
    boolean implementsInterface(Class<?> intf);
}
