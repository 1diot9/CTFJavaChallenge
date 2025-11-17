package org.springframework.boot.context.config;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataResource.class */
public abstract class ConfigDataResource {
    private final boolean optional;

    public ConfigDataResource() {
        this(false);
    }

    protected ConfigDataResource(boolean optional) {
        this.optional = optional;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isOptional() {
        return this.optional;
    }
}
