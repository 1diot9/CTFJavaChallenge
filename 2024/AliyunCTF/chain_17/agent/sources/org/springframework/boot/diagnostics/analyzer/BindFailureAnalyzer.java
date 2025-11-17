package org.springframework.boot.diagnostics.analyzer;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.context.properties.bind.UnboundConfigurationPropertiesException;
import org.springframework.boot.context.properties.bind.validation.BindValidationException;
import org.springframework.boot.context.properties.source.ConfigurationProperty;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/diagnostics/analyzer/BindFailureAnalyzer.class */
class BindFailureAnalyzer extends AbstractFailureAnalyzer<BindException> {
    BindFailureAnalyzer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, BindException cause) {
        Throwable rootCause = cause.getCause();
        if ((rootCause instanceof BindValidationException) || (rootCause instanceof UnboundConfigurationPropertiesException)) {
            return null;
        }
        return analyzeGenericBindException(rootFailure, cause);
    }

    private FailureAnalysis analyzeGenericBindException(Throwable rootFailure, BindException cause) {
        FailureAnalysis missingParametersAnalysis = MissingParameterNamesFailureAnalyzer.analyzeForMissingParameters(rootFailure);
        StringBuilder description = new StringBuilder(String.format("%s:%n", cause.getMessage()));
        ConfigurationProperty property = cause.getProperty();
        buildDescription(description, property);
        description.append(String.format("%n    Reason: %s", getMessage(cause)));
        if (missingParametersAnalysis != null) {
            MissingParameterNamesFailureAnalyzer.appendPossibility(description);
        }
        return getFailureAnalysis(description.toString(), cause, missingParametersAnalysis);
    }

    private void buildDescription(StringBuilder description, ConfigurationProperty property) {
        if (property != null) {
            description.append(String.format("%n    Property: %s", property.getName()));
            description.append(String.format("%n    Value: \"%s\"", property.getValue()));
            description.append(String.format("%n    Origin: %s", property.getOrigin()));
        }
    }

    private String getMessage(BindException cause) {
        Throwable rootCause = getRootCause(cause.getCause());
        ConversionFailedException conversionFailure = (ConversionFailedException) findCause(cause, ConversionFailedException.class);
        if (conversionFailure != null) {
            String message = "failed to convert " + conversionFailure.getSourceType() + " to " + conversionFailure.getTargetType();
            if (rootCause != null) {
                message = message + " (caused by " + getExceptionTypeAndMessage(rootCause) + ")";
            }
            return message;
        }
        if (rootCause != null && StringUtils.hasText(rootCause.getMessage())) {
            return getExceptionTypeAndMessage(rootCause);
        }
        return getExceptionTypeAndMessage(cause);
    }

    private Throwable getRootCause(Throwable cause) {
        Throwable rootCause;
        Throwable th = cause;
        while (true) {
            rootCause = th;
            if (rootCause == null || rootCause.getCause() == null) {
                break;
            }
            th = rootCause.getCause();
        }
        return rootCause;
    }

    private String getExceptionTypeAndMessage(Throwable ex) {
        String message = ex.getMessage();
        return ex.getClass().getName() + (StringUtils.hasText(message) ? ": " + message : "");
    }

    private FailureAnalysis getFailureAnalysis(String description, BindException cause, FailureAnalysis missingParametersAnalysis) {
        StringBuilder action = new StringBuilder("Update your application's configuration");
        Collection<String> validValues = findValidValues(cause);
        if (!validValues.isEmpty()) {
            action.append(String.format(". The following values are valid:%n", new Object[0]));
            validValues.forEach(value -> {
                action.append(String.format("%n    %s", value));
            });
        }
        if (missingParametersAnalysis != null) {
            action.append(String.format("%n%n%s", missingParametersAnalysis.getAction()));
        }
        return new FailureAnalysis(description, action.toString(), cause);
    }

    private Collection<String> findValidValues(BindException ex) {
        Object[] enumConstants;
        ConversionFailedException conversionFailure = (ConversionFailedException) findCause(ex, ConversionFailedException.class);
        if (conversionFailure != null && (enumConstants = conversionFailure.getTargetType().getType().getEnumConstants()) != null) {
            return (Collection) Stream.of(enumConstants).map((v0) -> {
                return v0.toString();
            }).collect(Collectors.toCollection(TreeSet::new));
        }
        return Collections.emptySet();
    }
}
