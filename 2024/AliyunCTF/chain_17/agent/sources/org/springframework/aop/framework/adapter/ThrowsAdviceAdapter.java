package org.springframework.aop.framework.adapter;

import java.io.Serializable;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.ThrowsAdvice;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/adapter/ThrowsAdviceAdapter.class */
public class ThrowsAdviceAdapter implements AdvisorAdapter, Serializable {
    @Override // org.springframework.aop.framework.adapter.AdvisorAdapter
    public boolean supportsAdvice(Advice advice) {
        return advice instanceof ThrowsAdvice;
    }

    @Override // org.springframework.aop.framework.adapter.AdvisorAdapter
    public MethodInterceptor getInterceptor(Advisor advisor) {
        return new ThrowsAdviceInterceptor(advisor.getAdvice());
    }
}
