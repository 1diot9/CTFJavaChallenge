package org.apache.logging.log4j.util;

import aQute.bnd.annotation.spi.ServiceProvider;
import java.util.Collection;
import java.util.Map;

@ServiceProvider(value = PropertySource.class, resolution = "optional")
/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/EnvironmentPropertySource.class */
public class EnvironmentPropertySource implements PropertySource {
    private static final String PREFIX = "LOG4J_";
    private static final int DEFAULT_PRIORITY = 100;

    @Override // org.apache.logging.log4j.util.PropertySource
    public int getPriority() {
        return 100;
    }

    private void logException(final SecurityException e) {
        LowLevelLogUtil.logException("The system environment variables are not available to Log4j due to security restrictions: " + e, e);
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public void forEach(final BiConsumer<String, String> action) {
        try {
            Map<String, String> getenv = System.getenv();
            for (Map.Entry<String, String> entry : getenv.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith(PREFIX)) {
                    action.accept(key.substring(PREFIX.length()), entry.getValue());
                }
            }
        } catch (SecurityException e) {
            logException(e);
        }
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public CharSequence getNormalForm(final Iterable<? extends CharSequence> tokens) {
        StringBuilder sb = new StringBuilder("LOG4J");
        boolean empty = true;
        for (CharSequence token : tokens) {
            empty = false;
            sb.append('_');
            for (int i = 0; i < token.length(); i++) {
                sb.append(Character.toUpperCase(token.charAt(i)));
            }
        }
        if (empty) {
            return null;
        }
        return sb.toString();
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public Collection<String> getPropertyNames() {
        try {
            return System.getenv().keySet();
        } catch (SecurityException e) {
            logException(e);
            return super.getPropertyNames();
        }
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public String getProperty(final String key) {
        try {
            return System.getenv(key);
        } catch (SecurityException e) {
            logException(e);
            return super.getProperty(key);
        }
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public boolean containsProperty(final String key) {
        try {
            return System.getenv().containsKey(key);
        } catch (SecurityException e) {
            logException(e);
            return super.containsProperty(key);
        }
    }
}
