package org.springframework.boot.context.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import org.springframework.boot.origin.Origin;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataResourceNotFoundException.class */
public class ConfigDataResourceNotFoundException extends ConfigDataNotFoundException {
    private final ConfigDataResource resource;
    private final ConfigDataLocation location;

    public ConfigDataResourceNotFoundException(ConfigDataResource resource) {
        this(resource, null);
    }

    public ConfigDataResourceNotFoundException(ConfigDataResource resource, Throwable cause) {
        this(resource, null, cause);
    }

    private ConfigDataResourceNotFoundException(ConfigDataResource resource, ConfigDataLocation location, Throwable cause) {
        super(getMessage(resource, location), cause);
        Assert.notNull(resource, "Resource must not be null");
        this.resource = resource;
        this.location = location;
    }

    public ConfigDataResource getResource() {
        return this.resource;
    }

    public ConfigDataLocation getLocation() {
        return this.location;
    }

    @Override // org.springframework.boot.origin.OriginProvider
    public Origin getOrigin() {
        return Origin.from(this.location);
    }

    @Override // org.springframework.boot.context.config.ConfigDataNotFoundException
    public String getReferenceDescription() {
        return getReferenceDescription(this.resource, this.location);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigDataResourceNotFoundException withLocation(ConfigDataLocation location) {
        return new ConfigDataResourceNotFoundException(this.resource, location, getCause());
    }

    private static String getMessage(ConfigDataResource resource, ConfigDataLocation location) {
        return String.format("Config data %s cannot be found", getReferenceDescription(resource, location));
    }

    private static String getReferenceDescription(ConfigDataResource resource, ConfigDataLocation location) {
        String description = String.format("resource '%s'", resource);
        if (location != null) {
            description = description + String.format(" via location '%s'", location);
        }
        return description;
    }

    public static void throwIfDoesNotExist(ConfigDataResource resource, Path pathToCheck) {
        throwIfDoesNotExist(resource, Files.exists(pathToCheck, new LinkOption[0]));
    }

    public static void throwIfDoesNotExist(ConfigDataResource resource, File fileToCheck) {
        throwIfDoesNotExist(resource, fileToCheck.exists());
    }

    public static void throwIfDoesNotExist(ConfigDataResource resource, Resource resourceToCheck) {
        throwIfDoesNotExist(resource, resourceToCheck.exists());
    }

    private static void throwIfDoesNotExist(ConfigDataResource resource, boolean exists) {
        if (!exists) {
            throw new ConfigDataResourceNotFoundException(resource);
        }
    }
}
