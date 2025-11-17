package org.springframework.boot.autoconfigure.orm.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.orm.jpa.vendor.Database;

@ConfigurationProperties(prefix = "spring.jpa")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/orm/jpa/JpaProperties.class */
public class JpaProperties {
    private String databasePlatform;
    private Database database;
    private Boolean openInView;
    private Map<String, String> properties = new HashMap();
    private final List<String> mappingResources = new ArrayList();
    private boolean generateDdl = false;
    private boolean showSql = false;

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public List<String> getMappingResources() {
        return this.mappingResources;
    }

    public String getDatabasePlatform() {
        return this.databasePlatform;
    }

    public void setDatabasePlatform(String databasePlatform) {
        this.databasePlatform = databasePlatform;
    }

    public Database getDatabase() {
        return this.database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public boolean isGenerateDdl() {
        return this.generateDdl;
    }

    public void setGenerateDdl(boolean generateDdl) {
        this.generateDdl = generateDdl;
    }

    public boolean isShowSql() {
        return this.showSql;
    }

    public void setShowSql(boolean showSql) {
        this.showSql = showSql;
    }

    public Boolean getOpenInView() {
        return this.openInView;
    }

    public void setOpenInView(Boolean openInView) {
        this.openInView = openInView;
    }
}
