package org.springframework.boot.info;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.ImportRuntimeHints;

@ImportRuntimeHints({BuildPropertiesRuntimeHints.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/info/BuildProperties.class */
public class BuildProperties extends InfoProperties {
    public BuildProperties(Properties entries) {
        super(processEntries(entries));
    }

    public String getGroup() {
        return get("group");
    }

    public String getArtifact() {
        return get("artifact");
    }

    public String getName() {
        return get("name");
    }

    public String getVersion() {
        return get("version");
    }

    public Instant getTime() {
        return getInstant("time");
    }

    private static Properties processEntries(Properties properties) {
        coerceDate(properties, "time");
        return properties;
    }

    private static void coerceDate(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value != null) {
            try {
                String updatedValue = String.valueOf(((Instant) DateTimeFormatter.ISO_INSTANT.parse(value, Instant::from)).toEpochMilli());
                properties.setProperty(key, updatedValue);
            } catch (DateTimeException e) {
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/info/BuildProperties$BuildPropertiesRuntimeHints.class */
    static class BuildPropertiesRuntimeHints implements RuntimeHintsRegistrar {
        BuildPropertiesRuntimeHints() {
        }

        @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.resources().registerPattern("META-INF/build-info.properties");
        }
    }
}
