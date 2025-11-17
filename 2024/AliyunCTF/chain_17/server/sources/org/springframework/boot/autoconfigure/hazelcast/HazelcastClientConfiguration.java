package org.springframework.boot.autoconfigure.hazelcast;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.client.config.YamlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import java.io.IOException;
import java.net.URL;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

@ConditionalOnMissingBean({HazelcastInstance.class})
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({HazelcastClient.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastClientConfiguration.class */
class HazelcastClientConfiguration {
    static final String CONFIG_SYSTEM_PROPERTY = "hazelcast.client.config";

    HazelcastClientConfiguration() {
    }

    private static HazelcastInstance getHazelcastInstance(ClientConfig config) {
        if (StringUtils.hasText(config.getInstanceName())) {
            return HazelcastClient.getOrCreateHazelcastClient(config);
        }
        return HazelcastClient.newHazelcastClient(config);
    }

    @ConditionalOnMissingBean({ClientConfig.class})
    @Configuration(proxyBeanMethods = false)
    @Conditional({HazelcastClientConfigAvailableCondition.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastClientConfiguration$HazelcastClientConfigFileConfiguration.class */
    static class HazelcastClientConfigFileConfiguration {
        HazelcastClientConfigFileConfiguration() {
        }

        @Bean
        HazelcastInstance hazelcastInstance(HazelcastProperties properties, ResourceLoader resourceLoader) throws IOException {
            Resource configLocation = properties.resolveConfigLocation();
            ClientConfig config = configLocation != null ? loadClientConfig(configLocation) : ClientConfig.load();
            config.setClassLoader(resourceLoader.getClassLoader());
            return HazelcastClientConfiguration.getHazelcastInstance(config);
        }

        private ClientConfig loadClientConfig(Resource configLocation) throws IOException {
            URL configUrl = configLocation.getURL();
            String configFileName = configUrl.getPath();
            if (configFileName.endsWith(".yaml") || configFileName.endsWith(".yml")) {
                return new YamlClientConfigBuilder(configUrl).build();
            }
            return new XmlClientConfigBuilder(configUrl).build();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnSingleCandidate(ClientConfig.class)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastClientConfiguration$HazelcastClientConfigConfiguration.class */
    static class HazelcastClientConfigConfiguration {
        HazelcastClientConfigConfiguration() {
        }

        @Bean
        HazelcastInstance hazelcastInstance(ClientConfig config) {
            return HazelcastClientConfiguration.getHazelcastInstance(config);
        }
    }
}
