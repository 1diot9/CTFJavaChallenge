package org.springframework.boot.autoconfigure.r2dbc;

import io.r2dbc.spi.ConnectionFactoryOptions;
import java.util.function.Supplier;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.r2dbc.EmbeddedDatabaseConnection;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/r2dbc/ConnectionFactoryOptionsInitializer.class */
class ConnectionFactoryOptionsInitializer {
    /* JADX INFO: Access modifiers changed from: package-private */
    public ConnectionFactoryOptions.Builder initialize(R2dbcProperties properties, R2dbcConnectionDetails connectionDetails, Supplier<EmbeddedDatabaseConnection> embeddedDatabaseConnection) {
        if (connectionDetails != null) {
            return connectionDetails.getConnectionFactoryOptions().mutate();
        }
        EmbeddedDatabaseConnection embeddedConnection = embeddedDatabaseConnection.get();
        if (embeddedConnection != EmbeddedDatabaseConnection.NONE) {
            return initializeEmbeddedOptions(properties, embeddedConnection);
        }
        throw connectionFactoryBeanCreationException("Failed to determine a suitable R2DBC Connection URL", null, embeddedConnection);
    }

    private ConnectionFactoryOptions.Builder initializeEmbeddedOptions(R2dbcProperties properties, EmbeddedDatabaseConnection embeddedDatabaseConnection) {
        String url = embeddedDatabaseConnection.getUrl(determineEmbeddedDatabaseName(properties));
        if (url == null) {
            throw connectionFactoryBeanCreationException("Failed to determine a suitable R2DBC Connection URL", url, embeddedDatabaseConnection);
        }
        ConnectionFactoryOptions.Builder builder = ConnectionFactoryOptions.parse(url).mutate();
        String username = determineEmbeddedUsername(properties);
        if (StringUtils.hasText(username)) {
            builder.option(ConnectionFactoryOptions.USER, username);
        }
        if (StringUtils.hasText(properties.getPassword())) {
            builder.option(ConnectionFactoryOptions.PASSWORD, properties.getPassword());
        }
        return builder;
    }

    private String determineEmbeddedDatabaseName(R2dbcProperties properties) {
        String databaseName = determineDatabaseName(properties);
        return databaseName != null ? databaseName : "testdb";
    }

    private String determineDatabaseName(R2dbcProperties properties) {
        if (properties.isGenerateUniqueName()) {
            return properties.determineUniqueName();
        }
        if (StringUtils.hasLength(properties.getName())) {
            return properties.getName();
        }
        return null;
    }

    private String determineEmbeddedUsername(R2dbcProperties properties) {
        String username = ifHasText(properties.getUsername());
        return username != null ? username : "sa";
    }

    private ConnectionFactoryBeanCreationException connectionFactoryBeanCreationException(String message, String r2dbcUrl, EmbeddedDatabaseConnection embeddedDatabaseConnection) {
        return new ConnectionFactoryBeanCreationException(message, r2dbcUrl, embeddedDatabaseConnection);
    }

    private String ifHasText(String candidate) {
        if (StringUtils.hasText(candidate)) {
            return candidate;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/r2dbc/ConnectionFactoryOptionsInitializer$ConnectionFactoryBeanCreationException.class */
    public static class ConnectionFactoryBeanCreationException extends BeanCreationException {
        private final String url;
        private final EmbeddedDatabaseConnection embeddedDatabaseConnection;

        ConnectionFactoryBeanCreationException(String message, String url, EmbeddedDatabaseConnection embeddedDatabaseConnection) {
            super(message);
            this.url = url;
            this.embeddedDatabaseConnection = embeddedDatabaseConnection;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String getUrl() {
            return this.url;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public EmbeddedDatabaseConnection getEmbeddedDatabaseConnection() {
            return this.embeddedDatabaseConnection;
        }
    }
}
