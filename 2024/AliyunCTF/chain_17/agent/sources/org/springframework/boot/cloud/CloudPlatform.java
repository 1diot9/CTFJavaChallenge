package org.springframework.boot.cloud;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/cloud/CloudPlatform.class */
public enum CloudPlatform {
    NONE { // from class: org.springframework.boot.cloud.CloudPlatform.1
        @Override // org.springframework.boot.cloud.CloudPlatform
        public boolean isDetected(Environment environment) {
            return false;
        }
    },
    CLOUD_FOUNDRY { // from class: org.springframework.boot.cloud.CloudPlatform.2
        @Override // org.springframework.boot.cloud.CloudPlatform
        public boolean isDetected(Environment environment) {
            return environment.containsProperty("VCAP_APPLICATION") || environment.containsProperty("VCAP_SERVICES");
        }
    },
    HEROKU { // from class: org.springframework.boot.cloud.CloudPlatform.3
        @Override // org.springframework.boot.cloud.CloudPlatform
        public boolean isDetected(Environment environment) {
            return environment.containsProperty("DYNO");
        }
    },
    SAP { // from class: org.springframework.boot.cloud.CloudPlatform.4
        @Override // org.springframework.boot.cloud.CloudPlatform
        public boolean isDetected(Environment environment) {
            return environment.containsProperty("HC_LANDSCAPE");
        }
    },
    NOMAD { // from class: org.springframework.boot.cloud.CloudPlatform.5
        @Override // org.springframework.boot.cloud.CloudPlatform
        public boolean isDetected(Environment environment) {
            return environment.containsProperty("NOMAD_ALLOC_ID");
        }
    },
    KUBERNETES { // from class: org.springframework.boot.cloud.CloudPlatform.6
        private static final String KUBERNETES_SERVICE_HOST = "KUBERNETES_SERVICE_HOST";
        private static final String KUBERNETES_SERVICE_PORT = "KUBERNETES_SERVICE_PORT";
        private static final String SERVICE_HOST_SUFFIX = "_SERVICE_HOST";
        private static final String SERVICE_PORT_SUFFIX = "_SERVICE_PORT";

        @Override // org.springframework.boot.cloud.CloudPlatform
        public boolean isDetected(Environment environment) {
            if (environment instanceof ConfigurableEnvironment) {
                ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) environment;
                return isAutoDetected(configurableEnvironment);
            }
            return false;
        }

        private boolean isAutoDetected(ConfigurableEnvironment environment) {
            PropertySource<?> environmentPropertySource = environment.getPropertySources().get("systemEnvironment");
            if (environmentPropertySource != null) {
                if (environmentPropertySource.containsProperty(KUBERNETES_SERVICE_HOST) && environmentPropertySource.containsProperty(KUBERNETES_SERVICE_PORT)) {
                    return true;
                }
                if (environmentPropertySource instanceof EnumerablePropertySource) {
                    return isAutoDetected((EnumerablePropertySource<?>) environmentPropertySource);
                }
                return false;
            }
            return false;
        }

        private boolean isAutoDetected(EnumerablePropertySource<?> environmentPropertySource) {
            for (String propertyName : environmentPropertySource.getPropertyNames()) {
                if (propertyName.endsWith(SERVICE_HOST_SUFFIX)) {
                    String serviceName = propertyName.substring(0, propertyName.length() - SERVICE_HOST_SUFFIX.length());
                    if (environmentPropertySource.getProperty(serviceName + "_SERVICE_PORT") != null) {
                        return true;
                    }
                }
            }
            return false;
        }
    },
    AZURE_APP_SERVICE { // from class: org.springframework.boot.cloud.CloudPlatform.7
        private final List<String> azureEnvVariables = Arrays.asList("WEBSITE_SITE_NAME", "WEBSITE_INSTANCE_ID", "WEBSITE_RESOURCE_GROUP", "WEBSITE_SKU");

        @Override // org.springframework.boot.cloud.CloudPlatform
        public boolean isDetected(Environment environment) {
            Stream<String> stream = this.azureEnvVariables.stream();
            Objects.requireNonNull(environment);
            return stream.allMatch(environment::containsProperty);
        }
    };

    private static final String PROPERTY_NAME = "spring.main.cloud-platform";

    public abstract boolean isDetected(Environment environment);

    public boolean isActive(Environment environment) {
        String platformProperty = environment.getProperty(PROPERTY_NAME);
        return isEnforced(platformProperty) || (platformProperty == null && isDetected(environment));
    }

    public boolean isEnforced(Environment environment) {
        return isEnforced(environment.getProperty(PROPERTY_NAME));
    }

    public boolean isEnforced(Binder binder) {
        return isEnforced((String) binder.bind(PROPERTY_NAME, String.class).orElse(null));
    }

    private boolean isEnforced(String platform) {
        return name().equalsIgnoreCase(platform);
    }

    public boolean isUsingForwardHeaders() {
        return true;
    }

    public static CloudPlatform getActive(Environment environment) {
        if (environment != null) {
            for (CloudPlatform cloudPlatform : values()) {
                if (cloudPlatform.isActive(environment)) {
                    return cloudPlatform;
                }
            }
            return null;
        }
        return null;
    }
}
