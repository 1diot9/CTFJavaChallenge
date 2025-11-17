package org.springframework.boot.diagnostics.analyzer;

import java.util.List;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.context.properties.bind.validation.BindValidationException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.origin.Origin;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/diagnostics/analyzer/BindValidationFailureAnalyzer.class */
class BindValidationFailureAnalyzer extends AbstractFailureAnalyzer<Throwable> {
    BindValidationFailureAnalyzer() {
    }

    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    protected FailureAnalysis analyze(Throwable rootFailure, Throwable cause) {
        ExceptionDetails details = getBindValidationExceptionDetails(rootFailure);
        if (details == null) {
            return null;
        }
        return analyzeBindValidationException(details);
    }

    private ExceptionDetails getBindValidationExceptionDetails(Throwable rootFailure) {
        BindValidationException validationException = (BindValidationException) findCause(rootFailure, BindValidationException.class);
        if (validationException != null) {
            BindException target = (BindException) findCause(rootFailure, BindException.class);
            List<ObjectError> errors = validationException.getValidationErrors().getAllErrors();
            return new ExceptionDetails(errors, target, validationException);
        }
        org.springframework.validation.BindException bindException = (org.springframework.validation.BindException) findCause(rootFailure, org.springframework.validation.BindException.class);
        if (bindException != null) {
            List<ObjectError> errors2 = bindException.getAllErrors();
            return new ExceptionDetails(errors2, bindException.getTarget(), bindException);
        }
        return null;
    }

    private FailureAnalysis analyzeBindValidationException(ExceptionDetails details) {
        StringBuilder description = new StringBuilder(String.format("Binding to target %s failed:%n", details.getTarget()));
        for (ObjectError error : details.getErrors()) {
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                appendFieldError(description, fieldError);
            }
            description.append(String.format("%n    Reason: %s%n", error.getDefaultMessage()));
        }
        return getFailureAnalysis(description, details.getCause());
    }

    private void appendFieldError(StringBuilder description, FieldError error) {
        Origin origin = Origin.from(error);
        description.append(String.format("%n    Property: %s", error.getObjectName() + "." + error.getField()));
        description.append(String.format("%n    Value: \"%s\"", error.getRejectedValue()));
        if (origin != null) {
            description.append(String.format("%n    Origin: %s", origin));
        }
    }

    private FailureAnalysis getFailureAnalysis(Object description, Throwable cause) {
        return new FailureAnalysis(description.toString(), "Update your application's configuration", cause);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/diagnostics/analyzer/BindValidationFailureAnalyzer$ExceptionDetails.class */
    public static class ExceptionDetails {
        private final List<ObjectError> errors;
        private final Object target;
        private final Throwable cause;

        ExceptionDetails(List<ObjectError> errors, Object target, Throwable cause) {
            this.errors = errors;
            this.target = target;
            this.cause = cause;
        }

        Object getTarget() {
            return this.target;
        }

        List<ObjectError> getErrors() {
            return this.errors;
        }

        Throwable getCause() {
            return this.cause;
        }
    }
}
