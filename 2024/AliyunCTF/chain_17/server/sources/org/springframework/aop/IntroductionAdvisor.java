package org.springframework.aop;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/IntroductionAdvisor.class */
public interface IntroductionAdvisor extends Advisor, IntroductionInfo {
    ClassFilter getClassFilter();

    void validateInterfaces() throws IllegalArgumentException;
}
