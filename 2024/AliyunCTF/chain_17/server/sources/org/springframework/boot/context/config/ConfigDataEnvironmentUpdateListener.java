package org.springframework.boot.context.config;

import java.util.EventListener;
import org.springframework.core.env.PropertySource;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataEnvironmentUpdateListener.class */
public interface ConfigDataEnvironmentUpdateListener extends EventListener {
    public static final ConfigDataEnvironmentUpdateListener NONE = new ConfigDataEnvironmentUpdateListener() { // from class: org.springframework.boot.context.config.ConfigDataEnvironmentUpdateListener.1
    };

    default void onPropertySourceAdded(PropertySource<?> propertySource, ConfigDataLocation location, ConfigDataResource resource) {
    }

    default void onSetProfiles(Profiles profiles) {
    }
}
