package org.springframework.boot.autoconfigure.integration;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginLookup;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationPropertiesEnvironmentPostProcessor.class */
class IntegrationPropertiesEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    IntegrationPropertiesEnvironmentPostProcessor() {
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override // org.springframework.boot.env.EnvironmentPostProcessor
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Resource resource = new ClassPathResource("META-INF/spring.integration.properties");
        if (resource.exists()) {
            registerIntegrationPropertiesPropertySource(environment, resource);
        }
    }

    protected void registerIntegrationPropertiesPropertySource(ConfigurableEnvironment environment, Resource resource) {
        PropertiesPropertySourceLoader loader = new PropertiesPropertySourceLoader();
        try {
            OriginTrackedMapPropertySource propertyFileSource = (OriginTrackedMapPropertySource) loader.load("META-INF/spring.integration.properties", resource).get(0);
            environment.getPropertySources().addLast(new IntegrationPropertiesPropertySource(propertyFileSource));
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load integration properties from " + resource, ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationPropertiesEnvironmentPostProcessor$IntegrationPropertiesPropertySource.class */
    public static final class IntegrationPropertiesPropertySource extends PropertySource<Map<String, Object>> implements OriginLookup<String> {
        private static final String PREFIX = "spring.integration.";
        private static final Map<String, String> KEYS_MAPPING;
        private final OriginTrackedMapPropertySource delegate;

        static {
            Map<String, String> mappings = new HashMap<>();
            mappings.put("spring.integration.channel.auto-create", "spring.integration.channels.autoCreate");
            mappings.put("spring.integration.channel.max-unicast-subscribers", "spring.integration.channels.maxUnicastSubscribers");
            mappings.put("spring.integration.channel.max-broadcast-subscribers", "spring.integration.channels.maxBroadcastSubscribers");
            mappings.put("spring.integration.error.require-subscribers", "spring.integration.channels.error.requireSubscribers");
            mappings.put("spring.integration.error.ignore-failures", "spring.integration.channels.error.ignoreFailures");
            mappings.put("spring.integration.endpoint.throw-exception-on-late-reply", "spring.integration.messagingTemplate.throwExceptionOnLateReply");
            mappings.put("spring.integration.endpoint.read-only-headers", "spring.integration.readOnly.headers");
            mappings.put("spring.integration.endpoint.no-auto-startup", "spring.integration.endpoints.noAutoStartup");
            KEYS_MAPPING = Collections.unmodifiableMap(mappings);
        }

        IntegrationPropertiesPropertySource(OriginTrackedMapPropertySource delegate) {
            super("META-INF/spring.integration.properties", delegate.getSource());
            this.delegate = delegate;
        }

        @Override // org.springframework.core.env.PropertySource
        public Object getProperty(String name) {
            return this.delegate.getProperty(KEYS_MAPPING.get(name));
        }

        @Override // org.springframework.boot.origin.OriginLookup
        public Origin getOrigin(String key) {
            return this.delegate.getOrigin(KEYS_MAPPING.get(key));
        }
    }
}
