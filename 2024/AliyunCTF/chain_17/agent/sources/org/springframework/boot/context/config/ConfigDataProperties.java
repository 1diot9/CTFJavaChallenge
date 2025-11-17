package org.springframework.boot.context.config;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.util.ObjectUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataProperties.class */
public class ConfigDataProperties {
    private static final ConfigurationPropertyName NAME = ConfigurationPropertyName.of("spring.config");
    private static final Bindable<ConfigDataProperties> BINDABLE_PROPERTIES = Bindable.of(ConfigDataProperties.class);
    private final List<ConfigDataLocation> imports;
    private final Activate activate;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigDataProperties(@Name("import") List<ConfigDataLocation> imports, Activate activate) {
        this.imports = imports != null ? imports : Collections.emptyList();
        this.activate = activate;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<ConfigDataLocation> getImports() {
        return this.imports;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isActive(ConfigDataActivationContext activationContext) {
        return this.activate == null || this.activate.isActive(activationContext);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigDataProperties withoutImports() {
        return new ConfigDataProperties(null, this.activate);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ConfigDataProperties get(Binder binder) {
        return (ConfigDataProperties) binder.bind(NAME, BINDABLE_PROPERTIES, new ConfigDataLocationBindHandler()).orElse(null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataProperties$Activate.class */
    public static class Activate {
        private final CloudPlatform onCloudPlatform;
        private final String[] onProfile;

        Activate(CloudPlatform onCloudPlatform, String[] onProfile) {
            this.onProfile = onProfile;
            this.onCloudPlatform = onCloudPlatform;
        }

        boolean isActive(ConfigDataActivationContext activationContext) {
            if (activationContext == null) {
                return false;
            }
            boolean activate = 1 != 0 && isActive(activationContext.getCloudPlatform());
            boolean activate2 = activate && isActive(activationContext.getProfiles());
            return activate2;
        }

        private boolean isActive(CloudPlatform cloudPlatform) {
            return this.onCloudPlatform == null || this.onCloudPlatform == cloudPlatform;
        }

        private boolean isActive(Profiles profiles) {
            if (!ObjectUtils.isEmpty((Object[]) this.onProfile)) {
                if (profiles != null) {
                    Objects.requireNonNull(profiles);
                    if (matchesActiveProfiles(profiles::isAccepted)) {
                    }
                }
                return false;
            }
            return true;
        }

        private boolean matchesActiveProfiles(Predicate<String> activeProfiles) {
            return org.springframework.core.env.Profiles.of(this.onProfile).matches(activeProfiles);
        }
    }
}
