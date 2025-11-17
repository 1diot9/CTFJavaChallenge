package org.springframework.boot.autoconfigure.jooq;

import org.jooq.DSLContext;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.core.Ordered;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jooq/NoDslContextBeanFailureAnalyzer.class */
class NoDslContextBeanFailureAnalyzer extends AbstractFailureAnalyzer<NoSuchBeanDefinitionException> implements Ordered {
    private final BeanFactory beanFactory;

    NoDslContextBeanFailureAnalyzer(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, NoSuchBeanDefinitionException cause) {
        if (DSLContext.class.equals(cause.getBeanType()) && hasR2dbcAutoConfiguration()) {
            return new FailureAnalysis("jOOQ has not been auto-configured as R2DBC has been auto-configured in favor of JDBC and jOOQ auto-configuration does not yet support R2DBC. ", "To use jOOQ with JDBC, exclude R2dbcAutoConfiguration. To use jOOQ with R2DBC, define your own jOOQ configuration.", cause);
        }
        return null;
    }

    private boolean hasR2dbcAutoConfiguration() {
        try {
            this.beanFactory.getBean(R2dbcAutoConfiguration.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }
}
