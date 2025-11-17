package org.springframework.boot.logging;

import java.io.File;
import java.util.Properties;
import org.springframework.core.env.PropertyResolver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/LogFile.class */
public class LogFile {
    public static final String FILE_NAME_PROPERTY = "logging.file.name";
    public static final String FILE_PATH_PROPERTY = "logging.file.path";
    private final String file;
    private final String path;

    LogFile(String file) {
        this(file, null);
    }

    LogFile(String file, String path) {
        Assert.isTrue(StringUtils.hasLength(file) || StringUtils.hasLength(path), "File or Path must not be empty");
        this.file = file;
        this.path = path;
    }

    public void applyToSystemProperties() {
        applyTo(System.getProperties());
    }

    public void applyTo(Properties properties) {
        put(properties, LoggingSystemProperty.LOG_PATH, this.path);
        put(properties, LoggingSystemProperty.LOG_FILE, toString());
    }

    private void put(Properties properties, LoggingSystemProperty property, String value) {
        if (StringUtils.hasLength(value)) {
            properties.put(property.getEnvironmentVariableName(), value);
        }
    }

    public String toString() {
        if (StringUtils.hasLength(this.file)) {
            return this.file;
        }
        return new File(this.path, "spring.log").getPath();
    }

    public static LogFile get(PropertyResolver propertyResolver) {
        String file = propertyResolver.getProperty(FILE_NAME_PROPERTY);
        String path = propertyResolver.getProperty(FILE_PATH_PROPERTY);
        if (StringUtils.hasLength(file) || StringUtils.hasLength(path)) {
            return new LogFile(file, path);
        }
        return null;
    }
}
