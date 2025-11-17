package org.springframework.boot.autoconfigure.sql.init;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.context.annotation.ImportRuntimeHints;

@ImportRuntimeHints({SqlInitializationScriptsRuntimeHints.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/sql/init/SqlDataSourceScriptDatabaseInitializer.class */
public class SqlDataSourceScriptDatabaseInitializer extends DataSourceScriptDatabaseInitializer {
    public SqlDataSourceScriptDatabaseInitializer(DataSource dataSource, SqlInitializationProperties properties) {
        this(dataSource, getSettings(properties));
    }

    public SqlDataSourceScriptDatabaseInitializer(DataSource dataSource, DatabaseInitializationSettings settings) {
        super(dataSource, settings);
    }

    public static DatabaseInitializationSettings getSettings(SqlInitializationProperties properties) {
        return SettingsCreator.createFrom(properties);
    }
}
