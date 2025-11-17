package org.springframework.boot.autoconfigure.session;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.session.FlushMode;
import org.springframework.session.SaveMode;

@ConfigurationProperties(prefix = "spring.session.jdbc")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/JdbcSessionProperties.class */
public class JdbcSessionProperties {
    private static final String DEFAULT_SCHEMA_LOCATION = "classpath:org/springframework/session/jdbc/schema-@@platform@@.sql";
    private static final String DEFAULT_TABLE_NAME = "SPRING_SESSION";
    private static final String DEFAULT_CLEANUP_CRON = "0 * * * * *";
    private String platform;
    private String schema = DEFAULT_SCHEMA_LOCATION;
    private String tableName = DEFAULT_TABLE_NAME;
    private String cleanupCron = DEFAULT_CLEANUP_CRON;
    private DatabaseInitializationMode initializeSchema = DatabaseInitializationMode.EMBEDDED;
    private FlushMode flushMode = FlushMode.ON_SAVE;
    private SaveMode saveMode = SaveMode.ON_SET_ATTRIBUTE;

    public String getSchema() {
        return this.schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getPlatform() {
        return this.platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getCleanupCron() {
        return this.cleanupCron;
    }

    public void setCleanupCron(String cleanupCron) {
        this.cleanupCron = cleanupCron;
    }

    public DatabaseInitializationMode getInitializeSchema() {
        return this.initializeSchema;
    }

    public void setInitializeSchema(DatabaseInitializationMode initializeSchema) {
        this.initializeSchema = initializeSchema;
    }

    public FlushMode getFlushMode() {
        return this.flushMode;
    }

    public void setFlushMode(FlushMode flushMode) {
        this.flushMode = flushMode;
    }

    public SaveMode getSaveMode() {
        return this.saveMode;
    }

    public void setSaveMode(SaveMode saveMode) {
        this.saveMode = saveMode;
    }
}
