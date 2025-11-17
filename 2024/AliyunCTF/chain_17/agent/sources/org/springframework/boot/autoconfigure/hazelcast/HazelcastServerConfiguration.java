package org.springframework.boot.autoconfigure.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.context.SpringManagedContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.slf4j.Logger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

@ConditionalOnMissingBean({HazelcastInstance.class})
@Configuration(proxyBeanMethods = false)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastServerConfiguration.class */
class HazelcastServerConfiguration {
    static final String CONFIG_SYSTEM_PROPERTY = "hazelcast.config";
    static final String HAZELCAST_LOGGING_TYPE = "hazelcast.logging.type";

    HazelcastServerConfiguration() {
    }

    private static HazelcastInstance getHazelcastInstance(Config config) {
        if (StringUtils.hasText(config.getInstanceName())) {
            return Hazelcast.getOrCreateHazelcastInstance(config);
        }
        return Hazelcast.newHazelcastInstance(config);
    }

    @ConditionalOnMissingBean({Config.class})
    @Configuration(proxyBeanMethods = false)
    @Conditional({ConfigAvailableCondition.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastServerConfiguration$HazelcastServerConfigFileConfiguration.class */
    static class HazelcastServerConfigFileConfiguration {
        HazelcastServerConfigFileConfiguration() {
        }

        @Bean
        HazelcastInstance hazelcastInstance(HazelcastProperties properties, ResourceLoader resourceLoader, ObjectProvider<HazelcastConfigCustomizer> hazelcastConfigCustomizers) throws IOException {
            Resource configLocation = properties.resolveConfigLocation();
            Config config = configLocation != null ? loadConfig(configLocation) : Config.load();
            config.setClassLoader(resourceLoader.getClassLoader());
            hazelcastConfigCustomizers.orderedStream().forEach(customizer -> {
                customizer.customize(config);
            });
            return HazelcastServerConfiguration.getHazelcastInstance(config);
        }

        private Config loadConfig(Resource configLocation) throws IOException {
            URL configUrl = configLocation.getURL();
            Config config = loadConfig(configUrl);
            if (ResourceUtils.isFileURL(configUrl)) {
                config.setConfigurationFile(configLocation.getFile());
            } else {
                config.setConfigurationUrl(configUrl);
            }
            return config;
        }

        private Config loadConfig(URL configUrl) throws IOException {
            InputStream stream = configUrl.openStream();
            try {
                Config loadFromStream = Config.loadFromStream(stream);
                if (stream != null) {
                    stream.close();
                }
                return loadFromStream;
            } catch (Throwable th) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnSingleCandidate(Config.class)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastServerConfiguration$HazelcastServerConfigConfiguration.class */
    static class HazelcastServerConfigConfiguration {
        HazelcastServerConfigConfiguration() {
        }

        @Bean
        HazelcastInstance hazelcastInstance(Config config) {
            return HazelcastServerConfiguration.getHazelcastInstance(config);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({SpringManagedContext.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastServerConfiguration$SpringManagedContextHazelcastConfigCustomizerConfiguration.class */
    static class SpringManagedContextHazelcastConfigCustomizerConfiguration {
        SpringManagedContextHazelcastConfigCustomizerConfiguration() {
        }

        @Bean
        @Order(0)
        HazelcastConfigCustomizer springManagedContextHazelcastConfigCustomizer(ApplicationContext applicationContext) {
            return config -> {
                SpringManagedContext managementContext = new SpringManagedContext();
                managementContext.setApplicationContext(applicationContext);
                config.setManagedContext(managementContext);
            };
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Logger.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastServerConfiguration$HazelcastLoggingConfigCustomizerConfiguration.class */
    static class HazelcastLoggingConfigCustomizerConfiguration {
        HazelcastLoggingConfigCustomizerConfiguration() {
        }

        @Bean
        @Order(0)
        HazelcastConfigCustomizer loggingHazelcastConfigCustomizer() {
            return config -> {
                if (!config.getProperties().containsKey(HazelcastServerConfiguration.HAZELCAST_LOGGING_TYPE)) {
                    config.setProperty(HazelcastServerConfiguration.HAZELCAST_LOGGING_TYPE, "slf4j");
                }
            };
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastServerConfiguration$ConfigAvailableCondition.class */
    static class ConfigAvailableCondition extends HazelcastConfigResourceCondition {
        ConfigAvailableCondition() {
            super(HazelcastServerConfiguration.CONFIG_SYSTEM_PROPERTY, "file:./hazelcast.xml", "classpath:/hazelcast.xml", "file:./hazelcast.yaml", "classpath:/hazelcast.yaml", "file:./hazelcast.yml", "classpath:/hazelcast.yml");
        }
    }
}
