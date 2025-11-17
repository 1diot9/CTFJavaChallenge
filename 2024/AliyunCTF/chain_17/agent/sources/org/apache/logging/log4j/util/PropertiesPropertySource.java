package org.apache.logging.log4j.util;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.util.PropertySource;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/PropertiesPropertySource.class */
public class PropertiesPropertySource implements PropertySource {
    private static final int DEFAULT_PRIORITY = 200;
    private static final String PREFIX = "log4j2.";
    private final Properties properties;
    private final int priority;

    public PropertiesPropertySource(final Properties properties) {
        this(properties, 200);
    }

    public PropertiesPropertySource(final Properties properties, final int priority) {
        this.properties = properties;
        this.priority = priority;
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public int getPriority() {
        return this.priority;
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public void forEach(final BiConsumer<String, String> action) {
        for (Map.Entry<Object, Object> entry : this.properties.entrySet()) {
            action.accept((String) entry.getKey(), (String) entry.getValue());
        }
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public CharSequence getNormalForm(final Iterable<? extends CharSequence> tokens) {
        CharSequence camelCase = PropertySource.Util.joinAsCamelCase(tokens);
        if (camelCase.length() > 0) {
            return PREFIX + ((Object) camelCase);
        }
        return null;
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public Collection<String> getPropertyNames() {
        return this.properties.stringPropertyNames();
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public String getProperty(final String key) {
        return this.properties.getProperty(key);
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public boolean containsProperty(final String key) {
        return getProperty(key) != null;
    }
}
