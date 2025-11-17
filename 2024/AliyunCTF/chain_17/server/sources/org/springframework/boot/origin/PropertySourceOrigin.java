package org.springframework.boot.origin;

import org.springframework.core.env.PropertySource;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/origin/PropertySourceOrigin.class */
public class PropertySourceOrigin implements Origin {
    private final PropertySource<?> propertySource;
    private final String propertyName;

    public PropertySourceOrigin(PropertySource<?> propertySource, String propertyName) {
        Assert.notNull(propertySource, "PropertySource must not be null");
        Assert.hasLength(propertyName, "PropertyName must not be empty");
        this.propertySource = propertySource;
        this.propertyName = propertyName;
    }

    public PropertySource<?> getPropertySource() {
        return this.propertySource;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public String toString() {
        return "\"" + this.propertyName + "\" from property source \"" + this.propertySource.getName() + "\"";
    }

    public static Origin get(PropertySource<?> propertySource, String name) {
        Origin origin = OriginLookup.getOrigin(propertySource, name);
        return origin != null ? origin : new PropertySourceOrigin(propertySource, name);
    }
}
