package org.springframework.boot.autoconfigure.session;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer;
import org.springframework.boot.jdbc.init.PlatformPlaceholderDatabaseDriverResolver;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/JdbcSessionDataSourceScriptDatabaseInitializer.class */
public class JdbcSessionDataSourceScriptDatabaseInitializer extends DataSourceScriptDatabaseInitializer {
    public JdbcSessionDataSourceScriptDatabaseInitializer(DataSource dataSource, JdbcSessionProperties properties) {
        this(dataSource, getSettings(dataSource, properties));
    }

    public JdbcSessionDataSourceScriptDatabaseInitializer(DataSource dataSource, DatabaseInitializationSettings settings) {
        super(dataSource, settings);
    }

    static DatabaseInitializationSettings getSettings(DataSource dataSource, JdbcSessionProperties properties) {
        DatabaseInitializationSettings settings = new DatabaseInitializationSettings();
        settings.setSchemaLocations(resolveSchemaLocations(dataSource, properties));
        settings.setMode(properties.getInitializeSchema());
        settings.setContinueOnError(true);
        return settings;
    }

    private static List<String> resolveSchemaLocations(DataSource dataSource, JdbcSessionProperties properties) {
        PlatformPlaceholderDatabaseDriverResolver platformResolver = new PlatformPlaceholderDatabaseDriverResolver().withDriverPlatform(DatabaseDriver.MARIADB, "mysql");
        if (StringUtils.hasText(properties.getPlatform())) {
            return platformResolver.resolveAll(properties.getPlatform(), properties.getSchema());
        }
        return platformResolver.resolveAll(dataSource, properties.getSchema());
    }
}
