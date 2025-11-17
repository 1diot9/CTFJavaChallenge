package org.springframework.boot.diagnostics;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/diagnostics/FailureAnalysisReporter.class */
public interface FailureAnalysisReporter {
    void report(FailureAnalysis analysis);
}
