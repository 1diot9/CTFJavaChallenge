package org.springframework.boot.autoconfigure.r2dbc;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.core.Ordered;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/r2dbc/NoConnectionFactoryBeanFailureAnalyzer.class */
class NoConnectionFactoryBeanFailureAnalyzer extends AbstractFailureAnalyzer<NoSuchBeanDefinitionException> implements Ordered {
    private final ClassLoader classLoader;

    NoConnectionFactoryBeanFailureAnalyzer() {
        this(NoConnectionFactoryBeanFailureAnalyzer.class.getClassLoader());
    }

    NoConnectionFactoryBeanFailureAnalyzer(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, NoSuchBeanDefinitionException cause) {
        if (ConnectionFactory.class.equals(cause.getBeanType()) && this.classLoader.getResource("META-INF/services/io.r2dbc.spi.ConnectionFactoryProvider") == null) {
            return new FailureAnalysis("No R2DBC ConnectionFactory bean is available and no /META-INF/services/io.r2dbc.spi.ConnectionFactoryProvider resource could be found.", "Check that the R2DBC driver for your database is on the classpath.", cause);
        }
        return null;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }
}
