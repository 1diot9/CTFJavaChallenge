package org.springframework.boot.diagnostics.analyzer;

import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.context.properties.bind.UnboundConfigurationPropertiesException;
import org.springframework.boot.context.properties.source.ConfigurationProperty;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/diagnostics/analyzer/UnboundConfigurationPropertyFailureAnalyzer.class */
class UnboundConfigurationPropertyFailureAnalyzer extends AbstractFailureAnalyzer<UnboundConfigurationPropertiesException> {
    UnboundConfigurationPropertyFailureAnalyzer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, UnboundConfigurationPropertiesException cause) {
        BindException exception = (BindException) findCause(rootFailure, BindException.class);
        return analyzeUnboundConfigurationPropertiesException(exception, cause);
    }

    private FailureAnalysis analyzeUnboundConfigurationPropertiesException(BindException cause, UnboundConfigurationPropertiesException exception) {
        StringBuilder description = new StringBuilder(String.format("Binding to target %s failed:%n", cause.getTarget()));
        for (ConfigurationProperty property : exception.getUnboundProperties()) {
            buildDescription(description, property);
            description.append(String.format("%n    Reason: %s", exception.getMessage()));
        }
        return getFailureAnalysis(description, cause);
    }

    private void buildDescription(StringBuilder description, ConfigurationProperty property) {
        if (property != null) {
            description.append(String.format("%n    Property: %s", property.getName()));
            description.append(String.format("%n    Value: \"%s\"", property.getValue()));
            description.append(String.format("%n    Origin: %s", property.getOrigin()));
        }
    }

    private FailureAnalysis getFailureAnalysis(Object description, BindException cause) {
        return new FailureAnalysis(description.toString(), "Update your application's configuration", cause);
    }
}
