package org.springframework.boot.autoconfigure.r2dbc;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/r2dbc/MultipleConnectionPoolConfigurationsFailureAnalyzer.class */
class MultipleConnectionPoolConfigurationsFailureAnalyzer extends AbstractFailureAnalyzer<MultipleConnectionPoolConfigurationsException> {
    MultipleConnectionPoolConfigurationsFailureAnalyzer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, MultipleConnectionPoolConfigurationsException cause) {
        return new FailureAnalysis(cause.getMessage(), "Update your configuration so that R2DBC connection pooling is configured using either the spring.r2dbc.url property or the spring.r2dbc.pool.* properties", cause);
    }
}
