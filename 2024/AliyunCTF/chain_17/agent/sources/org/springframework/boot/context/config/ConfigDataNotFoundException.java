package org.springframework.boot.context.config;

import org.springframework.boot.origin.OriginProvider;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataNotFoundException.class */
public abstract class ConfigDataNotFoundException extends ConfigDataException implements OriginProvider {
    public abstract String getReferenceDescription();

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigDataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
