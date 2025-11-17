package org.springframework.boot.context.config;

import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.core.style.ToStringCreator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataActivationContext.class */
public class ConfigDataActivationContext {
    private final CloudPlatform cloudPlatform;
    private final Profiles profiles;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigDataActivationContext(Environment environment, Binder binder) {
        this.cloudPlatform = deduceCloudPlatform(environment, binder);
        this.profiles = null;
    }

    ConfigDataActivationContext(CloudPlatform cloudPlatform, Profiles profiles) {
        this.cloudPlatform = cloudPlatform;
        this.profiles = profiles;
    }

    private CloudPlatform deduceCloudPlatform(Environment environment, Binder binder) {
        for (CloudPlatform candidate : CloudPlatform.values()) {
            if (candidate.isEnforced(binder)) {
                return candidate;
            }
        }
        return CloudPlatform.getActive(environment);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigDataActivationContext withProfiles(Profiles profiles) {
        return new ConfigDataActivationContext(this.cloudPlatform, profiles);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CloudPlatform getCloudPlatform() {
        return this.cloudPlatform;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Profiles getProfiles() {
        return this.profiles;
    }

    public String toString() {
        ToStringCreator creator = new ToStringCreator(this);
        creator.append("cloudPlatform", this.cloudPlatform);
        creator.append("profiles", this.profiles);
        return creator.toString();
    }
}
