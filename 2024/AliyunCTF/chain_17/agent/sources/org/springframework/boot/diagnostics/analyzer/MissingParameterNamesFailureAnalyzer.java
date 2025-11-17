package org.springframework.boot.diagnostics.analyzer;

import java.util.HashSet;
import java.util.Set;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.FailureAnalyzer;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

@Order(Integer.MAX_VALUE)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/diagnostics/analyzer/MissingParameterNamesFailureAnalyzer.class */
class MissingParameterNamesFailureAnalyzer implements FailureAnalyzer {
    private static final String USE_PARAMETERS_MESSAGE = "Ensure that the compiler uses the '-parameters' flag";
    static final String POSSIBILITY = "This may be due to missing parameter name information";
    static String ACTION = "Ensure that your compiler is configured to use the '-parameters' flag.\nYou may need to update both your build tool settings as well as your IDE.\n(See https://github.com/spring-projects/spring-framework/wiki/Upgrading-to-Spring-Framework-6.x#parameter-name-retention)\n";

    MissingParameterNamesFailureAnalyzer() {
    }

    @Override // org.springframework.boot.diagnostics.FailureAnalyzer
    public FailureAnalysis analyze(Throwable failure) {
        return analyzeForMissingParameters(failure);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static FailureAnalysis analyzeForMissingParameters(Throwable failure) {
        return analyzeForMissingParameters(failure, failure, new HashSet());
    }

    private static FailureAnalysis analyzeForMissingParameters(Throwable rootFailure, Throwable cause, Set<Throwable> seen) {
        if (cause != null && seen.add(cause)) {
            if (isSpringParametersException(cause)) {
                return getAnalysis(rootFailure, cause);
            }
            FailureAnalysis analysis = analyzeForMissingParameters(rootFailure, cause.getCause(), seen);
            if (analysis != null) {
                return analysis;
            }
            for (Throwable suppressed : cause.getSuppressed()) {
                FailureAnalysis analysis2 = analyzeForMissingParameters(rootFailure, suppressed, seen);
                if (analysis2 != null) {
                    return analysis2;
                }
            }
            return null;
        }
        return null;
    }

    private static boolean isSpringParametersException(Throwable failure) {
        String message = failure.getMessage();
        return message != null && message.contains(USE_PARAMETERS_MESSAGE) && isSpringException(failure);
    }

    private static boolean isSpringException(Throwable failure) {
        StackTraceElement[] elements = failure.getStackTrace();
        return elements.length > 0 && isSpringClass(elements[0].getClassName());
    }

    private static boolean isSpringClass(String className) {
        return className != null && className.startsWith("org.springframework.");
    }

    private static FailureAnalysis getAnalysis(Throwable rootFailure, Throwable cause) {
        StringBuilder description = new StringBuilder(String.format("%s:%n", cause.getMessage()));
        if (rootFailure != cause) {
            description.append(String.format("%n    Resulting Failure: %s", getExceptionTypeAndMessage(rootFailure)));
        }
        return new FailureAnalysis(description.toString(), ACTION, rootFailure);
    }

    private static String getExceptionTypeAndMessage(Throwable ex) {
        String message = ex.getMessage();
        return ex.getClass().getName() + (StringUtils.hasText(message) ? ": " + message : "");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void appendPossibility(StringBuilder description) {
        if (!description.toString().endsWith(System.lineSeparator())) {
            description.append("%n".formatted(new Object[0]));
        }
        description.append("%n%s".formatted(POSSIBILITY));
    }
}
