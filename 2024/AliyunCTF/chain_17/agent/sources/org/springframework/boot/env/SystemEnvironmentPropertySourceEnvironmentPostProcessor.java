package org.springframework.boot.env;

import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginLookup;
import org.springframework.boot.origin.SystemEnvironmentOrigin;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/env/SystemEnvironmentPropertySourceEnvironmentPostProcessor.class */
public class SystemEnvironmentPropertySourceEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    public static final int DEFAULT_ORDER = -2147483644;
    private int order = DEFAULT_ORDER;

    @Override // org.springframework.boot.env.EnvironmentPostProcessor
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        PropertySource<?> propertySource = environment.getPropertySources().get("systemEnvironment");
        if (propertySource != null) {
            replacePropertySource(environment, "systemEnvironment", propertySource, application.getEnvironmentPrefix());
        }
    }

    private void replacePropertySource(ConfigurableEnvironment environment, String sourceName, PropertySource<?> propertySource, String environmentPrefix) {
        Map<String, Object> originalSource = (Map) propertySource.getSource();
        SystemEnvironmentPropertySource source = new OriginAwareSystemEnvironmentPropertySource(sourceName, originalSource, environmentPrefix);
        environment.getPropertySources().replace(sourceName, source);
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/env/SystemEnvironmentPropertySourceEnvironmentPostProcessor$OriginAwareSystemEnvironmentPropertySource.class */
    public static class OriginAwareSystemEnvironmentPropertySource extends SystemEnvironmentPropertySource implements OriginLookup<String> {
        private final String prefix;

        OriginAwareSystemEnvironmentPropertySource(String name, Map<String, Object> source, String environmentPrefix) {
            super(name, source);
            this.prefix = determinePrefix(environmentPrefix);
        }

        private String determinePrefix(String environmentPrefix) {
            if (!StringUtils.hasText(environmentPrefix)) {
                return null;
            }
            if (environmentPrefix.endsWith(".") || environmentPrefix.endsWith("_") || environmentPrefix.endsWith("-")) {
                return environmentPrefix.substring(0, environmentPrefix.length() - 1);
            }
            return environmentPrefix;
        }

        @Override // org.springframework.core.env.SystemEnvironmentPropertySource, org.springframework.core.env.MapPropertySource, org.springframework.core.env.EnumerablePropertySource, org.springframework.core.env.PropertySource
        public boolean containsProperty(String name) {
            return super.containsProperty(name);
        }

        @Override // org.springframework.core.env.SystemEnvironmentPropertySource, org.springframework.core.env.MapPropertySource, org.springframework.core.env.PropertySource
        public Object getProperty(String name) {
            return super.getProperty(name);
        }

        @Override // org.springframework.boot.origin.OriginLookup
        public Origin getOrigin(String key) {
            String property = resolvePropertyName(key);
            if (super.containsProperty(property)) {
                return new SystemEnvironmentOrigin(property);
            }
            return null;
        }

        @Override // org.springframework.boot.origin.OriginLookup
        public String getPrefix() {
            return this.prefix;
        }
    }
}
