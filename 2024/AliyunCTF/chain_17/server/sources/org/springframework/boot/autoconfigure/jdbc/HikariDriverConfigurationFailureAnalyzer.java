package org.springframework.boot.autoconfigure.jdbc;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/HikariDriverConfigurationFailureAnalyzer.class */
class HikariDriverConfigurationFailureAnalyzer extends AbstractFailureAnalyzer<CannotGetJdbcConnectionException> {
    private static final String EXPECTED_MESSAGE = "cannot use driverClassName and dataSourceClassName together.";

    HikariDriverConfigurationFailureAnalyzer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, CannotGetJdbcConnectionException cause) {
        Throwable subCause = cause.getCause();
        if (subCause == null || !EXPECTED_MESSAGE.equals(subCause.getMessage())) {
            return null;
        }
        return new FailureAnalysis("Configuration of the Hikari connection pool failed: 'dataSourceClassName' is not supported.", "Spring Boot auto-configures only a driver and can't specify a custom DataSource. Consider configuring the Hikari DataSource in your own configuration.", cause);
    }
}
