package org.springframework.boot.logging.logback;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/logback/RollingPolicySystemProperty.class */
public enum RollingPolicySystemProperty {
    FILE_NAME_PATTERN("file-name-pattern", "logging.pattern.rolling-file-name"),
    CLEAN_HISTORY_ON_START("clean-history-on-start", "logging.file.clean-history-on-start"),
    MAX_FILE_SIZE("max-file-size", "logging.file.max-size"),
    TOTAL_SIZE_CAP("total-size-cap", "logging.file.total-size-cap"),
    MAX_HISTORY("max-history", "logging.file.max-history");

    private final String environmentVariableName = "LOGBACK_ROLLINGPOLICY_" + name();
    private final String applicationPropertyName;
    private final String deprecatedApplicationPropertyName;

    RollingPolicySystemProperty(String applicationPropertyName, String deprecatedApplicationPropertyName) {
        this.applicationPropertyName = "logging.logback.rollingpolicy." + applicationPropertyName;
        this.deprecatedApplicationPropertyName = deprecatedApplicationPropertyName;
    }

    public String getEnvironmentVariableName() {
        return this.environmentVariableName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getApplicationPropertyName() {
        return this.applicationPropertyName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getDeprecatedApplicationPropertyName() {
        return this.deprecatedApplicationPropertyName;
    }
}
