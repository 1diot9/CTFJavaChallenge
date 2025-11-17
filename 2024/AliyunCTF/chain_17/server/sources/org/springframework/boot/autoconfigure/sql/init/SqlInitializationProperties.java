package org.springframework.boot.autoconfigure.sql.init;

import java.nio.charset.Charset;
import java.util.List;
import org.apache.tomcat.util.net.Constants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.sql.init.DatabaseInitializationMode;

@ConfigurationProperties("spring.sql.init")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/sql/init/SqlInitializationProperties.class */
public class SqlInitializationProperties {
    private List<String> schemaLocations;
    private List<String> dataLocations;
    private String username;
    private String password;
    private Charset encoding;
    private String platform = Constants.SSL_PROTO_ALL;
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

    public String getPlatform() {
        return this.platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
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
