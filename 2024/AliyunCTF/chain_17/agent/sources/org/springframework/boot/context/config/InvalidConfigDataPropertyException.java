package org.springframework.boot.context.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.context.config.ConfigData;
import org.springframework.boot.context.properties.source.ConfigurationProperty;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.core.env.AbstractEnvironment;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/InvalidConfigDataPropertyException.class */
public class InvalidConfigDataPropertyException extends ConfigDataException {
    private static final Map<ConfigurationPropertyName, ConfigurationPropertyName> ERRORS;
    private static final Set<ConfigurationPropertyName> PROFILE_SPECIFIC_ERRORS;
    private final ConfigurationProperty property;
    private final ConfigurationPropertyName replacement;
    private final ConfigDataResource location;

    static {
        Map<ConfigurationPropertyName, ConfigurationPropertyName> errors = new LinkedHashMap<>();
        errors.put(ConfigurationPropertyName.of("spring.profiles"), ConfigurationPropertyName.of("spring.config.activate.on-profile"));
        errors.put(ConfigurationPropertyName.of("spring.profiles[0]"), ConfigurationPropertyName.of("spring.config.activate.on-profile"));
        ERRORS = Collections.unmodifiableMap(errors);
        Set<ConfigurationPropertyName> errors2 = new LinkedHashSet<>();
        errors2.add(Profiles.INCLUDE_PROFILES);
        errors2.add(Profiles.INCLUDE_PROFILES.append("[0]"));
        errors2.add(ConfigurationPropertyName.of(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME));
        errors2.add(ConfigurationPropertyName.of("spring.profiles.active[0]"));
        errors2.add(ConfigurationPropertyName.of(AbstractEnvironment.DEFAULT_PROFILES_PROPERTY_NAME));
        errors2.add(ConfigurationPropertyName.of("spring.profiles.default[0]"));
        PROFILE_SPECIFIC_ERRORS = Collections.unmodifiableSet(errors2);
    }

    InvalidConfigDataPropertyException(ConfigurationProperty property, boolean profileSpecific, ConfigurationPropertyName replacement, ConfigDataResource location) {
        super(getMessage(property, profileSpecific, replacement, location), null);
        this.property = property;
        this.replacement = replacement;
        this.location = location;
    }

    public ConfigurationProperty getProperty() {
        return this.property;
    }

    public ConfigDataResource getLocation() {
        return this.location;
    }

    public ConfigurationPropertyName getReplacement() {
        return this.replacement;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void throwIfPropertyFound(ConfigDataEnvironmentContributor contributor) {
        ConfigurationPropertySource propertySource = contributor.getConfigurationPropertySource();
        if (propertySource != null) {
            ERRORS.forEach((name, replacement) -> {
                ConfigurationProperty property = propertySource.getConfigurationProperty(name);
                if (property != null) {
                    throw new InvalidConfigDataPropertyException(property, false, replacement, contributor.getResource());
                }
            });
            if (contributor.isFromProfileSpecificImport() && !contributor.hasConfigDataOption(ConfigData.Option.IGNORE_PROFILES)) {
                PROFILE_SPECIFIC_ERRORS.forEach(name2 -> {
                    ConfigurationProperty property = propertySource.getConfigurationProperty(name2);
                    if (property != null) {
                        throw new InvalidConfigDataPropertyException(property, true, null, contributor.getResource());
                    }
                });
            }
        }
    }

    private static String getMessage(ConfigurationProperty property, boolean profileSpecific, ConfigurationPropertyName replacement, ConfigDataResource location) {
        StringBuilder message = new StringBuilder("Property '");
        message.append(property.getName());
        if (location != null) {
            message.append("' imported from location '");
            message.append(location);
        }
        message.append("' is invalid");
        if (profileSpecific) {
            message.append(" in a profile specific resource");
        }
        if (replacement != null) {
            message.append(" and should be replaced with '");
            message.append(replacement);
            message.append("'");
        }
        if (property.getOrigin() != null) {
            message.append(" [origin: ");
            message.append(property.getOrigin());
            message.append("]");
        }
        return message.toString();
    }
}
