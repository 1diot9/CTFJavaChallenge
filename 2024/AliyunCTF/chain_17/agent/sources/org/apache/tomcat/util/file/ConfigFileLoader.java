package org.apache.tomcat.util.file;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/file/ConfigFileLoader.class */
public class ConfigFileLoader {
    private static ConfigurationSource source;

    public static final ConfigurationSource getSource() {
        if (source == null) {
            return ConfigurationSource.DEFAULT;
        }
        return source;
    }

    public static final void setSource(ConfigurationSource source2) {
        if (source == null) {
            source = source2;
        }
    }

    private ConfigFileLoader() {
    }
}
