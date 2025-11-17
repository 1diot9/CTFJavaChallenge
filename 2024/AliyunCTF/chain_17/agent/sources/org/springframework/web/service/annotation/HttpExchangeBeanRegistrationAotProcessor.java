package org.springframework.web.service.annotation;

import java.util.ArrayList;
import java.util.List;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.hint.ProxyHints;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.aot.BeanRegistrationCode;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/annotation/HttpExchangeBeanRegistrationAotProcessor.class */
class HttpExchangeBeanRegistrationAotProcessor implements BeanRegistrationAotProcessor {
    HttpExchangeBeanRegistrationAotProcessor() {
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationAotProcessor
    @Nullable
    public BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
        Class<?> beanClass = registeredBean.getBeanClass();
        List<Class<?>> exchangeInterfaces = new ArrayList<>();
        MergedAnnotations.Search search = MergedAnnotations.search(MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
        for (Class<?> interfaceClass : ClassUtils.getAllInterfacesForClass(beanClass)) {
            ReflectionUtils.doWithMethods(interfaceClass, method -> {
                if (!exchangeInterfaces.contains(interfaceClass) && search.from(method).isPresent(HttpExchange.class)) {
                    exchangeInterfaces.add(interfaceClass);
                }
            });
        }
        if (!exchangeInterfaces.isEmpty()) {
            return new AotContribution(exchangeInterfaces);
        }
        return null;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/annotation/HttpExchangeBeanRegistrationAotProcessor$AotContribution.class */
    private static class AotContribution implements BeanRegistrationAotContribution {
        private final List<Class<?>> httpExchangeInterfaces;

        public AotContribution(List<Class<?>> httpExchangeInterfaces) {
            this.httpExchangeInterfaces = httpExchangeInterfaces;
        }

        @Override // org.springframework.beans.factory.aot.BeanRegistrationAotContribution
        public void applyTo(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode) {
            ProxyHints proxyHints = generationContext.getRuntimeHints().proxies();
            for (Class<?> httpExchangeInterface : this.httpExchangeInterfaces) {
                proxyHints.registerJdkProxy(AopProxyUtils.completeJdkProxyInterfaces(httpExchangeInterface));
            }
        }
    }
}
