package org.springframework.aop.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Advisor;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.beans.factory.NamedBean;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/interceptor/ExposeBeanNameAdvisors.class */
public abstract class ExposeBeanNameAdvisors {
    private static final String BEAN_NAME_ATTRIBUTE = ExposeBeanNameAdvisors.class.getName() + ".BEAN_NAME";

    public static String getBeanName() throws IllegalStateException {
        return getBeanName(ExposeInvocationInterceptor.currentInvocation());
    }

    public static String getBeanName(MethodInvocation mi) throws IllegalStateException {
        if (!(mi instanceof ProxyMethodInvocation)) {
            throw new IllegalArgumentException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
        }
        ProxyMethodInvocation pmi = (ProxyMethodInvocation) mi;
        String beanName = (String) pmi.getUserAttribute(BEAN_NAME_ATTRIBUTE);
        if (beanName == null) {
            throw new IllegalStateException("Cannot get bean name; not set on MethodInvocation: " + mi);
        }
        return beanName;
    }

    public static Advisor createAdvisorWithoutIntroduction(String beanName) {
        return new DefaultPointcutAdvisor(new ExposeBeanNameInterceptor(beanName));
    }

    public static Advisor createAdvisorIntroducingNamedBean(String beanName) {
        return new DefaultIntroductionAdvisor(new ExposeBeanNameIntroduction(beanName));
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/interceptor/ExposeBeanNameAdvisors$ExposeBeanNameInterceptor.class */
    private static class ExposeBeanNameInterceptor implements MethodInterceptor {
        private final String beanName;

        public ExposeBeanNameInterceptor(String beanName) {
            this.beanName = beanName;
        }

        @Override // org.aopalliance.intercept.MethodInterceptor
        @Nullable
        public Object invoke(MethodInvocation mi) throws Throwable {
            if (!(mi instanceof ProxyMethodInvocation)) {
                throw new IllegalStateException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
            }
            ProxyMethodInvocation pmi = (ProxyMethodInvocation) mi;
            pmi.setUserAttribute(ExposeBeanNameAdvisors.BEAN_NAME_ATTRIBUTE, this.beanName);
            return mi.proceed();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/interceptor/ExposeBeanNameAdvisors$ExposeBeanNameIntroduction.class */
    private static class ExposeBeanNameIntroduction extends DelegatingIntroductionInterceptor implements NamedBean {
        private final String beanName;

        public ExposeBeanNameIntroduction(String beanName) {
            this.beanName = beanName;
        }

        @Override // org.springframework.aop.support.DelegatingIntroductionInterceptor, org.aopalliance.intercept.MethodInterceptor
        @Nullable
        public Object invoke(MethodInvocation mi) throws Throwable {
            if (!(mi instanceof ProxyMethodInvocation)) {
                throw new IllegalStateException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
            }
            ProxyMethodInvocation pmi = (ProxyMethodInvocation) mi;
            pmi.setUserAttribute(ExposeBeanNameAdvisors.BEAN_NAME_ATTRIBUTE, this.beanName);
            return super.invoke(mi);
        }

        @Override // org.springframework.beans.factory.NamedBean
        public String getBeanName() {
            return this.beanName;
        }
    }
}
