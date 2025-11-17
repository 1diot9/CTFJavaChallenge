package org.springframework.boot.context.config;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataResolutionResult.class */
public class ConfigDataResolutionResult {
    private final ConfigDataLocation location;
    private final ConfigDataResource resource;
    private final boolean profileSpecific;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigDataResolutionResult(ConfigDataLocation location, ConfigDataResource resource, boolean profileSpecific) {
        this.location = location;
        this.resource = resource;
        this.profileSpecific = profileSpecific;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigDataLocation getLocation() {
        return this.location;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigDataResource getResource() {
        return this.resource;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isProfileSpecific() {
        return this.profileSpecific;
    }
}
