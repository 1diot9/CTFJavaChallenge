package org.springframework.boot.logging;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/LoggingSystemProperty.class */
public enum LoggingSystemProperty {
    APPLICATION_NAME("LOGGED_APPLICATION_NAME"),
    PID("PID"),
    LOG_FILE("LOG_FILE"),
    LOG_PATH("LOG_PATH"),
    CONSOLE_CHARSET("CONSOLE_LOG_CHARSET", "logging.charset.console"),
    FILE_CHARSET("FILE_LOG_CHARSET", "logging.charset.file"),
    CONSOLE_THRESHOLD("CONSOLE_LOG_THRESHOLD", "logging.threshold.console"),
    FILE_THRESHOLD("FILE_LOG_THRESHOLD", "logging.threshold.file"),
    EXCEPTION_CONVERSION_WORD("LOG_EXCEPTION_CONVERSION_WORD", "logging.exception-conversion-word"),
    CONSOLE_PATTERN("CONSOLE_LOG_PATTERN", "logging.pattern.console"),
    FILE_PATTERN("FILE_LOG_PATTERN", "logging.pattern.file"),
    LEVEL_PATTERN("LOG_LEVEL_PATTERN", "logging.pattern.level"),
    DATEFORMAT_PATTERN("LOG_DATEFORMAT_PATTERN", "logging.pattern.dateformat"),
    CORRELATION_PATTERN("LOG_CORRELATION_PATTERN", "logging.pattern.correlation");

    private final String environmentVariableName;
    private final String applicationPropertyName;

    LoggingSystemProperty(String environmentVariableName) {
        this(environmentVariableName, null);
    }

    LoggingSystemProperty(String environmentVariableName, String applicationPropertyName) {
        this.environmentVariableName = environmentVariableName;
        this.applicationPropertyName = applicationPropertyName;
    }

    public String getEnvironmentVariableName() {
        return this.environmentVariableName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getApplicationPropertyName() {
        return this.applicationPropertyName;
    }
}
