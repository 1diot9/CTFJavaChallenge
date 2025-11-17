package org.springframework.aop.support;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.DynamicIntroductionAdvice;
import org.springframework.aop.IntroductionInterceptor;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/DelegatingIntroductionInterceptor.class */
public class DelegatingIntroductionInterceptor extends IntroductionInfoSupport implements IntroductionInterceptor {

    @Nullable
    private Object delegate;

    public DelegatingIntroductionInterceptor(Object delegate) {
        init(delegate);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public DelegatingIntroductionInterceptor() {
        init(this);
    }

    private void init(Object delegate) {
        Assert.notNull(delegate, "Delegate must not be null");
        this.delegate = delegate;
        implementInterfacesOnObject(delegate);
        suppressInterface(IntroductionInterceptor.class);
        suppressInterface(DynamicIntroductionAdvice.class);
    }

    @Nullable
    public Object invoke(MethodInvocation mi) throws Throwable {
        if (isMethodOnIntroducedInterface(mi)) {
            Object retVal = AopUtils.invokeJoinpointUsingReflection(this.delegate, mi.getMethod(), mi.getArguments());
            if (retVal == this.delegate && (mi instanceof ProxyMethodInvocation)) {
                ProxyMethodInvocation pmi = (ProxyMethodInvocation) mi;
                Object proxy = pmi.getProxy();
                if (mi.getMethod().getReturnType().isInstance(proxy)) {
                    retVal = proxy;
                }
            }
            return retVal;
        }
        return doProceed(mi);
    }

    @Nullable
    protected Object doProceed(MethodInvocation mi) throws Throwable {
        return mi.proceed();
    }
}
