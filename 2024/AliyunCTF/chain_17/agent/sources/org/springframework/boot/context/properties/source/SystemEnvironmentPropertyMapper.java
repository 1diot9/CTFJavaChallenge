package org.springframework.boot.context.properties.source;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.BiPredicate;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/SystemEnvironmentPropertyMapper.class */
final class SystemEnvironmentPropertyMapper implements PropertyMapper {
    public static final PropertyMapper INSTANCE = new SystemEnvironmentPropertyMapper();

    SystemEnvironmentPropertyMapper() {
    }

    @Override // org.springframework.boot.context.properties.source.PropertyMapper
    public List<String> map(ConfigurationPropertyName configurationPropertyName) {
        String name = convertName(configurationPropertyName);
        String legacyName = convertLegacyName(configurationPropertyName);
        return name.equals(legacyName) ? Collections.singletonList(name) : Arrays.asList(name, legacyName);
    }

    private String convertName(ConfigurationPropertyName name) {
        return convertName(name, name.getNumberOfElements());
    }

    private String convertName(ConfigurationPropertyName name, int numberOfElements) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < numberOfElements; i++) {
            if (!result.isEmpty()) {
                result.append('_');
            }
            result.append(name.getElement(i, ConfigurationPropertyName.Form.UNIFORM).toUpperCase(Locale.ENGLISH));
        }
        return result.toString();
    }

    private String convertLegacyName(ConfigurationPropertyName name) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < name.getNumberOfElements(); i++) {
            if (!result.isEmpty()) {
                result.append('_');
            }
            result.append(convertLegacyNameElement(name.getElement(i, ConfigurationPropertyName.Form.ORIGINAL)));
        }
        return result.toString();
    }

    private Object convertLegacyNameElement(String element) {
        return element.replace('-', '_').toUpperCase(Locale.ENGLISH);
    }

    @Override // org.springframework.boot.context.properties.source.PropertyMapper
    public ConfigurationPropertyName map(String propertySourceName) {
        return convertName(propertySourceName);
    }

    private ConfigurationPropertyName convertName(String propertySourceName) {
        try {
            return ConfigurationPropertyName.adapt(propertySourceName, '_', this::processElementValue);
        } catch (Exception e) {
            return ConfigurationPropertyName.EMPTY;
        }
    }

    private CharSequence processElementValue(CharSequence value) {
        String result = value.toString().toLowerCase(Locale.ENGLISH);
        return isNumber(result) ? "[" + result + "]" : result;
    }

    private static boolean isNumber(String string) {
        return string.chars().allMatch(Character::isDigit);
    }

    @Override // org.springframework.boot.context.properties.source.PropertyMapper
    public BiPredicate<ConfigurationPropertyName, ConfigurationPropertyName> getAncestorOfCheck() {
        return this::isAncestorOf;
    }

    private boolean isAncestorOf(ConfigurationPropertyName name, ConfigurationPropertyName candidate) {
        return name.isAncestorOf(candidate) || isLegacyAncestorOf(name, candidate);
    }

    private boolean isLegacyAncestorOf(ConfigurationPropertyName name, ConfigurationPropertyName candidate) {
        ConfigurationPropertyName legacyCompatibleName;
        return hasDashedEntries(name) && (legacyCompatibleName = buildLegacyCompatibleName(name)) != null && legacyCompatibleName.isAncestorOf(candidate);
    }

    private ConfigurationPropertyName buildLegacyCompatibleName(ConfigurationPropertyName name) {
        StringBuilder legacyCompatibleName = new StringBuilder();
        for (int i = 0; i < name.getNumberOfElements(); i++) {
            if (i != 0) {
                legacyCompatibleName.append('.');
            }
            legacyCompatibleName.append(name.getElement(i, ConfigurationPropertyName.Form.DASHED).replace('-', '.'));
        }
        return ConfigurationPropertyName.ofIfValid(legacyCompatibleName);
    }

    boolean hasDashedEntries(ConfigurationPropertyName name) {
        for (int i = 0; i < name.getNumberOfElements(); i++) {
            if (name.getElement(i, ConfigurationPropertyName.Form.DASHED).indexOf(45) != -1) {
                return true;
            }
        }
        return false;
    }
}
