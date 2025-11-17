package org.springframework.boot.autoconfigure.cache;

import com.hazelcast.core.HazelcastInstance;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({HazelcastInstance.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cache/HazelcastJCacheCustomizationConfiguration.class */
class HazelcastJCacheCustomizationConfiguration {
    HazelcastJCacheCustomizationConfiguration() {
    }

    @Bean
    HazelcastPropertiesCustomizer hazelcastPropertiesCustomizer(ObjectProvider<HazelcastInstance> hazelcastInstance) {
        return new HazelcastPropertiesCustomizer(hazelcastInstance.getIfUnique());
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cache/HazelcastJCacheCustomizationConfiguration$HazelcastPropertiesCustomizer.class */
    static class HazelcastPropertiesCustomizer implements JCachePropertiesCustomizer {
        private final HazelcastInstance hazelcastInstance;

        HazelcastPropertiesCustomizer(HazelcastInstance hazelcastInstance) {
            this.hazelcastInstance = hazelcastInstance;
        }

        @Override // org.springframework.boot.autoconfigure.cache.JCachePropertiesCustomizer
        public void customize(CacheProperties cacheProperties, Properties properties) {
            Resource configLocation = cacheProperties.resolveConfigLocation(cacheProperties.getJcache().getConfig());
            if (configLocation != null) {
                properties.setProperty("hazelcast.config.location", toUri(configLocation).toString());
            } else if (this.hazelcastInstance != null) {
                properties.put("hazelcast.instance.itself", this.hazelcastInstance);
            }
        }

        private static URI toUri(Resource config) {
            try {
                return config.getURI();
            } catch (IOException ex) {
                throw new IllegalArgumentException("Could not get URI from " + config, ex);
            }
        }
    }
}
