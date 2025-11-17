package org.springframework.boot.loader.launch;

import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

/* loaded from: agent.jar:org/springframework/boot/loader/launch/SystemPropertyUtils.class */
final class SystemPropertyUtils {
    private static final String PLACEHOLDER_PREFIX = "${";
    private static final String PLACEHOLDER_SUFFIX = "}";
    private static final String VALUE_SEPARATOR = ":";
    private static final String SIMPLE_PREFIX = "${".substring(1);

    private SystemPropertyUtils() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String resolvePlaceholders(Properties properties, String text) {
        if (text != null) {
            return parseStringValue(properties, text, text, new HashSet());
        }
        return null;
    }

    private static String parseStringValue(Properties properties, String value, String current, Set<String> visitedPlaceholders) {
        int indexOf;
        int separatorIndex;
        StringBuilder result = new StringBuilder(current);
        int startIndex = current.indexOf("${");
        while (startIndex != -1) {
            int endIndex = findPlaceholderEndIndex(result, startIndex);
            if (endIndex == -1) {
                startIndex = -1;
            } else {
                String placeholder = result.substring(startIndex + "${".length(), endIndex);
                if (!visitedPlaceholders.add(placeholder)) {
                    throw new IllegalArgumentException("Circular placeholder reference '" + placeholder + "' in property definitions");
                }
                String placeholder2 = parseStringValue(properties, value, placeholder, visitedPlaceholders);
                String propertyValue = resolvePlaceholder(properties, value, placeholder2);
                if (propertyValue == null && (separatorIndex = placeholder2.indexOf(":")) != -1) {
                    String actualPlaceholder = placeholder2.substring(0, separatorIndex);
                    String defaultValue = placeholder2.substring(separatorIndex + ":".length());
                    String propertyValue2 = resolvePlaceholder(properties, value, actualPlaceholder);
                    propertyValue = propertyValue2 != null ? propertyValue2 : defaultValue;
                }
                if (propertyValue != null) {
                    String propertyValue3 = parseStringValue(properties, value, propertyValue, visitedPlaceholders);
                    result.replace(startIndex, endIndex + "}".length(), propertyValue3);
                    indexOf = result.indexOf("${", startIndex + propertyValue3.length());
                } else {
                    indexOf = result.indexOf("${", endIndex + "}".length());
                }
                startIndex = indexOf;
                visitedPlaceholders.remove(placeholder);
            }
        }
        return result.toString();
    }

    private static String resolvePlaceholder(Properties properties, String text, String placeholderName) {
        String propertyValue = getProperty(placeholderName, null, text);
        if (propertyValue != null) {
            return propertyValue;
        }
        if (properties != null) {
            return properties.getProperty(placeholderName);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getProperty(String key) {
        return getProperty(key, null, "");
    }

    private static String getProperty(String key, String defaultValue, String text) {
        try {
            String value = System.getProperty(key);
            String value2 = value != null ? value : System.getenv(key);
            String value3 = value2 != null ? value2 : System.getenv(key.replace('.', '_'));
            String value4 = value3 != null ? value3 : System.getenv(key.toUpperCase(Locale.ENGLISH).replace('.', '_'));
            return value4 != null ? value4 : defaultValue;
        } catch (Throwable ex) {
            System.err.println("Could not resolve key '" + key + "' in '" + text + "' as system property or in environment: " + ex);
            return defaultValue;
        }
    }

    private static int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
        int index = startIndex + "${".length();
        int withinNestedPlaceholder = 0;
        while (index < buf.length()) {
            if (substringMatch(buf, index, "}")) {
                if (withinNestedPlaceholder > 0) {
                    withinNestedPlaceholder--;
                    index += "}".length();
                } else {
                    return index;
                }
            } else if (substringMatch(buf, index, SIMPLE_PREFIX)) {
                withinNestedPlaceholder++;
                index += SIMPLE_PREFIX.length();
            } else {
                index++;
            }
        }
        return -1;
    }

    private static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
        for (int j = 0; j < substring.length(); j++) {
            int i = index + j;
            if (i >= str.length() || str.charAt(i) != substring.charAt(j)) {
                return false;
            }
        }
        return true;
    }
}
