package org.springframework.boot.autoconfigure.r2dbc;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/r2dbc/MissingR2dbcPoolDependencyFailureAnalyzer.class */
class MissingR2dbcPoolDependencyFailureAnalyzer extends AbstractFailureAnalyzer<MissingR2dbcPoolDependencyException> {
    MissingR2dbcPoolDependencyFailureAnalyzer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, MissingR2dbcPoolDependencyException cause) {
        return new FailureAnalysis(cause.getMessage(), "Update your application's build to depend on io.r2dbc:r2dbc-pool or your application's configuration to disable R2DBC connection pooling.", cause);
    }
}
