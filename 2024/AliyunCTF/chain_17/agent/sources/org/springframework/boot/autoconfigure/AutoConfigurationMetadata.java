package org.springframework.boot.autoconfigure;

import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/AutoConfigurationMetadata.class */
public interface AutoConfigurationMetadata {
    boolean wasProcessed(String className);

    Integer getInteger(String className, String key);

    Integer getInteger(String className, String key, Integer defaultValue);

    Set<String> getSet(String className, String key);

    Set<String> getSet(String className, String key, Set<String> defaultValue);

    String get(String className, String key);

    String get(String className, String key, String defaultValue);
}
