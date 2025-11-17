package org.springframework.boot.logging.log4j2;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.PropertySource;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/log4j2/SpringBootPropertySource.class */
public class SpringBootPropertySource implements PropertySource {
    private static final String PREFIX = "log4j.";
    private final Map<String, String> properties = Collections.singletonMap("log4j.shutdownHookEnabled", "false");

    @Override // org.apache.logging.log4j.util.PropertySource
    public void forEach(BiConsumer<String, String> action) {
        Map<String, String> map = this.properties;
        Objects.requireNonNull(action);
        map.forEach((v1, v2) -> {
            r1.accept(v1, v2);
        });
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public CharSequence getNormalForm(Iterable<? extends CharSequence> tokens) {
        return "log4j." + PropertySource.Util.joinAsCamelCase(tokens);
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public int getPriority() {
        return -200;
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public String getProperty(String key) {
        return this.properties.get(key);
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public boolean containsProperty(String key) {
        return this.properties.containsKey(key);
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public Collection<String> getPropertyNames() {
        return this.properties.keySet();
    }
}
