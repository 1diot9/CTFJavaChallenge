package org.springframework.boot.autoconfigure.sql.init;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.r2dbc.init.R2dbcScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.context.annotation.ImportRuntimeHints;

@ImportRuntimeHints({SqlInitializationScriptsRuntimeHints.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/sql/init/SqlR2dbcScriptDatabaseInitializer.class */
public class SqlR2dbcScriptDatabaseInitializer extends R2dbcScriptDatabaseInitializer {
    public SqlR2dbcScriptDatabaseInitializer(ConnectionFactory connectionFactory, SqlInitializationProperties properties) {
        super(connectionFactory, getSettings(properties));
    }

    public SqlR2dbcScriptDatabaseInitializer(ConnectionFactory connectionFactory, DatabaseInitializationSettings settings) {
        super(connectionFactory, settings);
    }

    public static DatabaseInitializationSettings getSettings(SqlInitializationProperties properties) {
        return SettingsCreator.createFrom(properties);
    }
}
