package org.apache.logging.log4j.util;

import aQute.bnd.annotation.spi.ServiceProvider;
import java.util.Collection;
import java.util.Objects;
import java.util.Properties;
import org.apache.logging.log4j.util.PropertySource;

@ServiceProvider(value = PropertySource.class, resolution = "optional")
/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/SystemPropertiesPropertySource.class */
public class SystemPropertiesPropertySource implements PropertySource {
    private static final int DEFAULT_PRIORITY = 0;
    private static final String PREFIX = "log4j2.";

    public static String getSystemProperty(final String key, final String defaultValue) {
        try {
            return System.getProperty(key, defaultValue);
        } catch (SecurityException e) {
            return defaultValue;
        }
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public int getPriority() {
        return 0;
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public void forEach(final BiConsumer<String, String> action) {
        Object[] keySet;
        try {
            Properties properties = System.getProperties();
            synchronized (properties) {
                keySet = properties.keySet().toArray();
            }
            for (Object key : keySet) {
                String keyStr = Objects.toString(key, null);
                action.accept(keyStr, properties.getProperty(keyStr));
            }
        } catch (SecurityException e) {
        }
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public CharSequence getNormalForm(final Iterable<? extends CharSequence> tokens) {
        return PREFIX + ((Object) PropertySource.Util.joinAsCamelCase(tokens));
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public Collection<String> getPropertyNames() {
        try {
            return System.getProperties().stringPropertyNames();
        } catch (SecurityException e) {
            return super.getPropertyNames();
        }
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public String getProperty(final String key) {
        try {
            return System.getProperty(key);
        } catch (SecurityException e) {
            return super.getProperty(key);
        }
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public boolean containsProperty(final String key) {
        return getProperty(key) != null;
    }
}
