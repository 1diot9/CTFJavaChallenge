package org.springframework.boot.autoconfigure.liquibase;

import java.io.File;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

@ConfigurationProperties(prefix = "spring.liquibase", ignoreUnknownFields = false)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/liquibase/LiquibaseProperties.class */
public class LiquibaseProperties {
    private boolean clearChecksums;
    private String contexts;
    private String defaultSchema;
    private String liquibaseSchema;
    private String liquibaseTablespace;
    private boolean dropFirst;
    private String user;
    private String password;
    private String driverClassName;
    private String url;
    private String labelFilter;
    private Map<String, String> parameters;
    private File rollbackFile;
    private boolean testRollbackOnUpdate;
    private String tag;
    private ShowSummary showSummary;
    private ShowSummaryOutput showSummaryOutput;
    private String changeLog = "classpath:/db/changelog/db.changelog-master.yaml";
    private String databaseChangeLogTable = "DATABASECHANGELOG";
    private String databaseChangeLogLockTable = "DATABASECHANGELOGLOCK";
    private boolean enabled = true;

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/liquibase/LiquibaseProperties$ShowSummary.class */
    public enum ShowSummary {
        OFF,
        SUMMARY,
        VERBOSE
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/liquibase/LiquibaseProperties$ShowSummaryOutput.class */
    public enum ShowSummaryOutput {
        LOG,
        CONSOLE,
        ALL
    }

    public String getChangeLog() {
        return this.changeLog;
    }

    public void setChangeLog(String changeLog) {
        Assert.notNull(changeLog, "ChangeLog must not be null");
        this.changeLog = changeLog;
    }

    public String getContexts() {
        return this.contexts;
    }

    public void setContexts(String contexts) {
        this.contexts = contexts;
    }

    public String getDefaultSchema() {
        return this.defaultSchema;
    }

    public void setDefaultSchema(String defaultSchema) {
        this.defaultSchema = defaultSchema;
    }

    public String getLiquibaseSchema() {
        return this.liquibaseSchema;
    }

    public void setLiquibaseSchema(String liquibaseSchema) {
        this.liquibaseSchema = liquibaseSchema;
    }

    public String getLiquibaseTablespace() {
        return this.liquibaseTablespace;
    }

    public void setLiquibaseTablespace(String liquibaseTablespace) {
        this.liquibaseTablespace = liquibaseTablespace;
    }

    public String getDatabaseChangeLogTable() {
        return this.databaseChangeLogTable;
    }

    public void setDatabaseChangeLogTable(String databaseChangeLogTable) {
        this.databaseChangeLogTable = databaseChangeLogTable;
    }

    public String getDatabaseChangeLogLockTable() {
        return this.databaseChangeLogLockTable;
    }

    public void setDatabaseChangeLogLockTable(String databaseChangeLogLockTable) {
        this.databaseChangeLogLockTable = databaseChangeLogLockTable;
    }

    public boolean isDropFirst() {
        return this.dropFirst;
    }

    public void setDropFirst(boolean dropFirst) {
        this.dropFirst = dropFirst;
    }

    public boolean isClearChecksums() {
        return this.clearChecksums;
    }

    public void setClearChecksums(boolean clearChecksums) {
        this.clearChecksums = clearChecksums;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClassName() {
        return this.driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLabelFilter() {
        return this.labelFilter;
    }

    public void setLabelFilter(String labelFilter) {
        this.labelFilter = labelFilter;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public File getRollbackFile() {
        return this.rollbackFile;
    }

    public void setRollbackFile(File rollbackFile) {
        this.rollbackFile = rollbackFile;
    }

    public boolean isTestRollbackOnUpdate() {
        return this.testRollbackOnUpdate;
    }

    public void setTestRollbackOnUpdate(boolean testRollbackOnUpdate) {
        this.testRollbackOnUpdate = testRollbackOnUpdate;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public ShowSummary getShowSummary() {
        return this.showSummary;
    }

    public void setShowSummary(ShowSummary showSummary) {
        this.showSummary = showSummary;
    }

    public ShowSummaryOutput getShowSummaryOutput() {
        return this.showSummaryOutput;
    }

    public void setShowSummaryOutput(ShowSummaryOutput showSummaryOutput) {
        this.showSummaryOutput = showSummaryOutput;
    }
}
