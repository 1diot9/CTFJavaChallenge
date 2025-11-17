package org.springframework.boot.context.properties;

import java.util.stream.Collectors;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/IncompatibleConfigurationFailureAnalyzer.class */
class IncompatibleConfigurationFailureAnalyzer extends AbstractFailureAnalyzer<IncompatibleConfigurationException> {
    IncompatibleConfigurationFailureAnalyzer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, IncompatibleConfigurationException cause) {
        String action = String.format("Review the docs for %s and change the configured values.", cause.getIncompatibleKeys().stream().collect(Collectors.joining(", ")));
        return new FailureAnalysis(cause.getMessage(), action, cause);
    }
}
