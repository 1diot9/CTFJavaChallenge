package org.springframework.boot.diagnostics.analyzer;

import java.util.stream.Collectors;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyNameException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/diagnostics/analyzer/InvalidConfigurationPropertyNameFailureAnalyzer.class */
class InvalidConfigurationPropertyNameFailureAnalyzer extends AbstractFailureAnalyzer<InvalidConfigurationPropertyNameException> {
    InvalidConfigurationPropertyNameFailureAnalyzer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, InvalidConfigurationPropertyNameException cause) {
        BeanCreationException exception = (BeanCreationException) findCause(rootFailure, BeanCreationException.class);
        String action = String.format("Modify '%s' so that it conforms to the canonical names requirements.", cause.getName());
        return new FailureAnalysis(buildDescription(cause, exception), action, cause);
    }

    private String buildDescription(InvalidConfigurationPropertyNameException cause, BeanCreationException exception) {
        StringBuilder description = new StringBuilder(String.format("Configuration property name '%s' is not valid:%n", cause.getName()));
        String invalid = (String) cause.getInvalidCharacters().stream().map(this::quote).collect(Collectors.joining(", "));
        description.append(String.format("%n    Invalid characters: %s", invalid));
        if (exception != null) {
            description.append(String.format("%n    Bean: %s", exception.getBeanName()));
        }
        description.append(String.format("%n    Reason: Canonical names should be kebab-case ('-' separated), lowercase alpha-numeric characters and must start with a letter", new Object[0]));
        return description.toString();
    }

    private String quote(Character c) {
        return "'" + c + "'";
    }
}
