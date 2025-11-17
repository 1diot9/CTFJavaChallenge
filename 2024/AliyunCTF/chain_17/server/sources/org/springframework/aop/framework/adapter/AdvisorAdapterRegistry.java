package org.springframework.aop.framework.adapter;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/adapter/AdvisorAdapterRegistry.class */
public interface AdvisorAdapterRegistry {
    Advisor wrap(Object advice) throws UnknownAdviceTypeException;

    MethodInterceptor[] getInterceptors(Advisor advisor) throws UnknownAdviceTypeException;

    void registerAdvisorAdapter(AdvisorAdapter adapter);
}
