package org.springframework.boot.diagnostics;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/diagnostics/LoggingFailureAnalysisReporter.class */
public final class LoggingFailureAnalysisReporter implements FailureAnalysisReporter {
    private static final Log logger = LogFactory.getLog((Class<?>) LoggingFailureAnalysisReporter.class);

    @Override // org.springframework.boot.diagnostics.FailureAnalysisReporter
    public void report(FailureAnalysis failureAnalysis) {
        if (logger.isDebugEnabled()) {
            logger.debug("Application failed to start due to an exception", failureAnalysis.getCause());
        }
        if (logger.isErrorEnabled()) {
            logger.error(buildMessage(failureAnalysis));
        }
    }

    private String buildMessage(FailureAnalysis failureAnalysis) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%n%n", new Object[0]));
        builder.append(String.format("***************************%n", new Object[0]));
        builder.append(String.format("APPLICATION FAILED TO START%n", new Object[0]));
        builder.append(String.format("***************************%n%n", new Object[0]));
        builder.append(String.format("Description:%n%n", new Object[0]));
        builder.append(String.format("%s%n", failureAnalysis.getDescription()));
        if (StringUtils.hasText(failureAnalysis.getAction())) {
            builder.append(String.format("%nAction:%n%n", new Object[0]));
            builder.append(String.format("%s%n", failureAnalysis.getAction()));
        }
        return builder.toString();
    }
}
