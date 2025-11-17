package org.springframework.aop.framework;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/AopProxyFactory.class */
public interface AopProxyFactory {
    AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException;
}
