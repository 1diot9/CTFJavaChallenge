package org.springframework.boot.diagnostics;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/diagnostics/FailureAnalyzer.class */
public interface FailureAnalyzer {
    FailureAnalysis analyze(Throwable failure);
}
