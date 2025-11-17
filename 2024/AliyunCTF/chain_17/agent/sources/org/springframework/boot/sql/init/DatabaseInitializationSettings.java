package org.springframework.boot.sql.init;

import java.nio.charset.Charset;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/sql/init/DatabaseInitializationSettings.class */
public class DatabaseInitializationSettings {
    private List<String> schemaLocations;
    private List<String> dataLocations;
    private Charset encoding;
    private boolean continueOnError = false;
    private String separator = ";";
    private DatabaseInitializationMode mode = DatabaseInitializationMode.EMBEDDED;

    public List<String> getSchemaLocations() {
        return this.schemaLocations;
    }

    public void setSchemaLocations(List<String> schemaLocations) {
        this.schemaLocations = schemaLocations;
    }

    public List<String> getDataLocations() {
        return this.dataLocations;
    }

    public void setDataLocations(List<String> dataLocations) {
        this.dataLocations = dataLocations;
    }

    public boolean isContinueOnError() {
        return this.continueOnError;
    }

    public void setContinueOnError(boolean continueOnError) {
        this.continueOnError = continueOnError;
    }

    public String getSeparator() {
        return this.separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public Charset getEncoding() {
        return this.encoding;
    }

    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }

    public DatabaseInitializationMode getMode() {
        return this.mode;
    }

    public void setMode(DatabaseInitializationMode mode) {
        this.mode = mode;
    }
}
