package org.springframework.boot.context.config;

import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/StandardConfigDataReference.class */
public class StandardConfigDataReference {
    private final ConfigDataLocation configDataLocation;
    private final String resourceLocation;
    private final String directory;
    private final String profile;
    private final PropertySourceLoader propertySourceLoader;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StandardConfigDataReference(ConfigDataLocation configDataLocation, String directory, String root, String profile, String extension, PropertySourceLoader propertySourceLoader) {
        this.configDataLocation = configDataLocation;
        String profileSuffix = StringUtils.hasText(profile) ? "-" + profile : "";
        this.resourceLocation = root + profileSuffix + (extension != null ? "." + extension : "");
        this.directory = directory;
        this.profile = profile;
        this.propertySourceLoader = propertySourceLoader;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigDataLocation getConfigDataLocation() {
        return this.configDataLocation;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getResourceLocation() {
        return this.resourceLocation;
    }

    boolean isMandatoryDirectory() {
        return (this.configDataLocation.isOptional() || this.directory == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getDirectory() {
        return this.directory;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getProfile() {
        return this.profile;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isSkippable() {
        return (!this.configDataLocation.isOptional() && this.directory == null && this.profile == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PropertySourceLoader getPropertySourceLoader() {
        return this.propertySourceLoader;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        StandardConfigDataReference other = (StandardConfigDataReference) obj;
        return this.resourceLocation.equals(other.resourceLocation);
    }

    public int hashCode() {
        return this.resourceLocation.hashCode();
    }

    public String toString() {
        return this.resourceLocation;
    }
}
