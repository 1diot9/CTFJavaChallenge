package org.springframework.boot.autoconfigure.batch;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer;
import org.springframework.boot.jdbc.init.PlatformPlaceholderDatabaseDriverResolver;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/batch/BatchDataSourceScriptDatabaseInitializer.class */
public class BatchDataSourceScriptDatabaseInitializer extends DataSourceScriptDatabaseInitializer {
    public BatchDataSourceScriptDatabaseInitializer(DataSource dataSource, BatchProperties.Jdbc properties) {
        this(dataSource, getSettings(dataSource, properties));
    }

    public BatchDataSourceScriptDatabaseInitializer(DataSource dataSource, DatabaseInitializationSettings settings) {
        super(dataSource, settings);
    }

    public static DatabaseInitializationSettings getSettings(DataSource dataSource, BatchProperties.Jdbc properties) {
        DatabaseInitializationSettings settings = new DatabaseInitializationSettings();
        settings.setSchemaLocations(resolveSchemaLocations(dataSource, properties));
        settings.setMode(properties.getInitializeSchema());
        settings.setContinueOnError(true);
        return settings;
    }

    private static List<String> resolveSchemaLocations(DataSource dataSource, BatchProperties.Jdbc properties) {
        PlatformPlaceholderDatabaseDriverResolver platformResolver = new PlatformPlaceholderDatabaseDriverResolver();
        if (StringUtils.hasText(properties.getPlatform())) {
            return platformResolver.resolveAll(properties.getPlatform(), properties.getSchema());
        }
        return platformResolver.resolveAll(dataSource, properties.getSchema());
    }
}
