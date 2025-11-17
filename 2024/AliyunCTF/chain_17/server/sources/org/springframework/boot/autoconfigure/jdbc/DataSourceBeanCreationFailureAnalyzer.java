package org.springframework.boot.autoconfigure.jdbc;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceBeanCreationFailureAnalyzer.class */
class DataSourceBeanCreationFailureAnalyzer extends AbstractFailureAnalyzer<DataSourceProperties.DataSourceBeanCreationException> {
    private final Environment environment;

    DataSourceBeanCreationFailureAnalyzer(Environment environment) {
        this.environment = environment;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, DataSourceProperties.DataSourceBeanCreationException cause) {
        return getFailureAnalysis(cause);
    }

    private FailureAnalysis getFailureAnalysis(DataSourceProperties.DataSourceBeanCreationException cause) {
        String description = getDescription(cause);
        String action = getAction(cause);
        return new FailureAnalysis(description, action, cause);
    }

    private String getDescription(DataSourceProperties.DataSourceBeanCreationException cause) {
        StringBuilder description = new StringBuilder();
        description.append("Failed to configure a DataSource: ");
        if (!StringUtils.hasText(cause.getProperties().getUrl())) {
            description.append("'url' attribute is not specified and ");
        }
        description.append(String.format("no embedded datasource could be configured.%n", new Object[0]));
        description.append(String.format("%nReason: %s%n", cause.getMessage()));
        return description.toString();
    }

    private String getAction(DataSourceProperties.DataSourceBeanCreationException cause) {
        StringBuilder action = new StringBuilder();
        action.append(String.format("Consider the following:%n", new Object[0]));
        if (EmbeddedDatabaseConnection.NONE == cause.getConnection()) {
            action.append(String.format("\tIf you want an embedded database (H2, HSQL or Derby), please put it on the classpath.%n", new Object[0]));
        } else {
            action.append(String.format("\tReview the configuration of %s%n.", cause.getConnection()));
        }
        action.append("\tIf you have database settings to be loaded from a particular profile you may need to activate it").append(getActiveProfiles());
        return action.toString();
    }

    private String getActiveProfiles() {
        StringBuilder message = new StringBuilder();
        String[] profiles = this.environment.getActiveProfiles();
        if (ObjectUtils.isEmpty((Object[]) profiles)) {
            message.append(" (no profiles are currently active).");
        } else {
            message.append(" (the profiles ");
            message.append(StringUtils.arrayToCommaDelimitedString(profiles));
            message.append(" are currently active).");
        }
        return message.toString();
    }
}
