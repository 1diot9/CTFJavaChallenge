package org.springframework.boot.context.config;

import org.springframework.boot.context.properties.source.ConfigurationProperty;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.origin.Origin;
import org.springframework.core.env.PropertySource;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/InactiveConfigDataAccessException.class */
public class InactiveConfigDataAccessException extends ConfigDataException {
    private final PropertySource<?> propertySource;
    private final ConfigDataResource location;
    private final String propertyName;
    private final Origin origin;

    /* JADX INFO: Access modifiers changed from: package-private */
    public InactiveConfigDataAccessException(PropertySource<?> propertySource, ConfigDataResource location, String propertyName, Origin origin) {
        super(getMessage(propertySource, location, propertyName, origin), null);
        this.propertySource = propertySource;
        this.location = location;
        this.propertyName = propertyName;
        this.origin = origin;
    }

    private static String getMessage(PropertySource<?> propertySource, ConfigDataResource location, String propertyName, Origin origin) {
        StringBuilder message = new StringBuilder("Inactive property source '");
        message.append(propertySource.getName());
        if (location != null) {
            message.append("' imported from location '");
            message.append(location);
        }
        message.append("' cannot contain property '");
        message.append(propertyName);
        message.append("'");
        if (origin != null) {
            message.append(" [origin: ");
            message.append(origin);
            message.append("]");
        }
        return message.toString();
    }

    public PropertySource<?> getPropertySource() {
        return this.propertySource;
    }

    public ConfigDataResource getLocation() {
        return this.location;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public Origin getOrigin() {
        return this.origin;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void throwIfPropertyFound(ConfigDataEnvironmentContributor contributor, ConfigurationPropertyName name) {
        ConfigurationPropertySource source = contributor.getConfigurationPropertySource();
        ConfigurationProperty property = source != null ? source.getConfigurationProperty(name) : null;
        if (property != null) {
            PropertySource<?> propertySource = contributor.getPropertySource();
            ConfigDataResource location = contributor.getResource();
            throw new InactiveConfigDataAccessException(propertySource, location, name.toString(), property.getOrigin());
        }
    }
}
