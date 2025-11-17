package org.springframework.boot.autoconfigure.quartz;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer;
import org.springframework.boot.jdbc.init.PlatformPlaceholderDatabaseDriverResolver;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/quartz/QuartzDataSourceScriptDatabaseInitializer.class */
public class QuartzDataSourceScriptDatabaseInitializer extends DataSourceScriptDatabaseInitializer {
    private final List<String> commentPrefixes;

    public QuartzDataSourceScriptDatabaseInitializer(DataSource dataSource, QuartzProperties properties) {
        this(dataSource, getSettings(dataSource, properties), properties.getJdbc().getCommentPrefix());
    }

    public QuartzDataSourceScriptDatabaseInitializer(DataSource dataSource, DatabaseInitializationSettings settings) {
        this(dataSource, settings, null);
    }

    private QuartzDataSourceScriptDatabaseInitializer(DataSource dataSource, DatabaseInitializationSettings settings, List<String> commentPrefixes) {
        super(dataSource, settings);
        this.commentPrefixes = commentPrefixes;
    }

    @Override // org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer
    protected void customize(ResourceDatabasePopulator populator) {
        if (!ObjectUtils.isEmpty(this.commentPrefixes)) {
            populator.setCommentPrefixes((String[]) this.commentPrefixes.toArray(new String[0]));
        }
    }

    public static DatabaseInitializationSettings getSettings(DataSource dataSource, QuartzProperties properties) {
        DatabaseInitializationSettings settings = new DatabaseInitializationSettings();
        settings.setSchemaLocations(resolveSchemaLocations(dataSource, properties.getJdbc()));
        settings.setMode(properties.getJdbc().getInitializeSchema());
        settings.setContinueOnError(true);
        return settings;
    }

    private static List<String> resolveSchemaLocations(DataSource dataSource, QuartzProperties.Jdbc properties) {
        PlatformPlaceholderDatabaseDriverResolver platformResolver = new PlatformPlaceholderDatabaseDriverResolver().withDriverPlatform(DatabaseDriver.DB2, "db2_v95").withDriverPlatform(DatabaseDriver.MYSQL, "mysql_innodb").withDriverPlatform(DatabaseDriver.MARIADB, "mysql_innodb").withDriverPlatform(DatabaseDriver.POSTGRESQL, "postgres").withDriverPlatform(DatabaseDriver.SQLSERVER, "sqlServer");
        if (StringUtils.hasText(properties.getPlatform())) {
            return platformResolver.resolveAll(properties.getPlatform(), properties.getSchema());
        }
        return platformResolver.resolveAll(dataSource, properties.getSchema());
    }
}
