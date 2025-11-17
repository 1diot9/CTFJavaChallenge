package org.springframework.boot.autoconfigure.batch;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.transaction.annotation.Isolation;

@ConfigurationProperties(prefix = "spring.batch")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/batch/BatchProperties.class */
public class BatchProperties {
    private final Job job = new Job();
    private final Jdbc jdbc = new Jdbc();

    public Job getJob() {
        return this.job;
    }

    public Jdbc getJdbc() {
        return this.jdbc;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/batch/BatchProperties$Job.class */
    public static class Job {
        private String name = "";

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/batch/BatchProperties$Jdbc.class */
    public static class Jdbc {
        private static final String DEFAULT_SCHEMA_LOCATION = "classpath:org/springframework/batch/core/schema-@@platform@@.sql";
        private Isolation isolationLevelForCreate;
        private String platform;
        private String tablePrefix;
        private String schema = DEFAULT_SCHEMA_LOCATION;
        private DatabaseInitializationMode initializeSchema = DatabaseInitializationMode.EMBEDDED;

        public Isolation getIsolationLevelForCreate() {
            return this.isolationLevelForCreate;
        }

        public void setIsolationLevelForCreate(Isolation isolationLevelForCreate) {
            this.isolationLevelForCreate = isolationLevelForCreate;
        }

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

        public String getTablePrefix() {
            return this.tablePrefix;
        }

        public void setTablePrefix(String tablePrefix) {
            this.tablePrefix = tablePrefix;
        }

        public DatabaseInitializationMode getInitializeSchema() {
            return this.initializeSchema;
        }

        public void setInitializeSchema(DatabaseInitializationMode initializeSchema) {
            this.initializeSchema = initializeSchema;
        }
    }
}
