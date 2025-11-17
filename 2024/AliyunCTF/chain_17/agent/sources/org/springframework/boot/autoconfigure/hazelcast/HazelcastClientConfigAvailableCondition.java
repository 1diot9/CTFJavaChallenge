package org.springframework.boot.autoconfigure.hazelcast;

import com.hazelcast.client.config.ClientConfigRecognizer;
import com.hazelcast.config.ConfigStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotatedTypeMetadata;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastClientConfigAvailableCondition.class */
class HazelcastClientConfigAvailableCondition extends HazelcastConfigResourceCondition {
    HazelcastClientConfigAvailableCondition() {
        super("hazelcast.client.config", "file:./hazelcast-client.xml", "classpath:/hazelcast-client.xml", "file:./hazelcast-client.yaml", "classpath:/hazelcast-client.yaml", "file:./hazelcast-client.yml", "classpath:/hazelcast-client.yml");
    }

    @Override // org.springframework.boot.autoconfigure.condition.ResourceCondition, org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        if (context.getEnvironment().containsProperty("spring.hazelcast.config")) {
            ConditionOutcome configValidationOutcome = HazelcastClientValidation.clientConfigOutcome(context, "spring.hazelcast.config", startConditionMessage());
            return configValidationOutcome != null ? configValidationOutcome : ConditionOutcome.match(startConditionMessage().foundExactly("property spring.hazelcast.config"));
        }
        return getResourceOutcome(context, metadata);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastClientConfigAvailableCondition$HazelcastClientValidation.class */
    static class HazelcastClientValidation {
        HazelcastClientValidation() {
        }

        static ConditionOutcome clientConfigOutcome(ConditionContext context, String propertyName, ConditionMessage.Builder builder) {
            String resourcePath = context.getEnvironment().getProperty(propertyName);
            Resource resource = context.getResourceLoader().getResource(resourcePath);
            if (!resource.exists()) {
                return ConditionOutcome.noMatch(builder.because("Hazelcast configuration does not exist"));
            }
            try {
                InputStream in = resource.getInputStream();
                try {
                    boolean clientConfig = new ClientConfigRecognizer().isRecognized(new ConfigStream(in));
                    ConditionOutcome conditionOutcome = new ConditionOutcome(clientConfig, existingConfigurationOutcome(resource, clientConfig));
                    if (in != null) {
                        in.close();
                    }
                    return conditionOutcome;
                } finally {
                }
            } catch (Throwable th) {
                return null;
            }
        }

        private static String existingConfigurationOutcome(Resource resource, boolean client) throws IOException {
            URL location = resource.getURL();
            return client ? "Hazelcast client configuration detected at '" + location + "'" : "Hazelcast server configuration detected  at '" + location + "'";
        }
    }
}
