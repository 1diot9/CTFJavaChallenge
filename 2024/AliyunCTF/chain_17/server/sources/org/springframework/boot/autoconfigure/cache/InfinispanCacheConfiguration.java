package org.springframework.boot.autoconfigure.cache;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.catalina.Lifecycle;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.spring.embedded.provider.SpringEmbeddedCacheManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({SpringEmbeddedCacheManager.class})
@ConditionalOnMissingBean({CacheManager.class})
@Conditional({CacheCondition.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cache/InfinispanCacheConfiguration.class */
public class InfinispanCacheConfiguration {
    @Bean
    public SpringEmbeddedCacheManager cacheManager(CacheManagerCustomizers customizers, EmbeddedCacheManager embeddedCacheManager) {
        SpringEmbeddedCacheManager cacheManager = new SpringEmbeddedCacheManager(embeddedCacheManager);
        return customizers.customize(cacheManager);
    }

    @ConditionalOnMissingBean
    @Bean(destroyMethod = Lifecycle.STOP_EVENT)
    public EmbeddedCacheManager infinispanCacheManager(CacheProperties cacheProperties, ObjectProvider<ConfigurationBuilder> defaultConfigurationBuilder) throws IOException {
        EmbeddedCacheManager cacheManager = createEmbeddedCacheManager(cacheProperties);
        List<String> cacheNames = cacheProperties.getCacheNames();
        if (!CollectionUtils.isEmpty(cacheNames)) {
            cacheNames.forEach(cacheName -> {
                cacheManager.defineConfiguration(cacheName, getDefaultCacheConfiguration((ConfigurationBuilder) defaultConfigurationBuilder.getIfAvailable()));
            });
        }
        return cacheManager;
    }

    private EmbeddedCacheManager createEmbeddedCacheManager(CacheProperties cacheProperties) throws IOException {
        Resource location = cacheProperties.resolveConfigLocation(cacheProperties.getInfinispan().getConfig());
        if (location != null) {
            InputStream in = location.getInputStream();
            try {
                DefaultCacheManager defaultCacheManager = new DefaultCacheManager(in);
                if (in != null) {
                    in.close();
                }
                return defaultCacheManager;
            } catch (Throwable th) {
                if (in != null) {
                    try {
                        in.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
        return new DefaultCacheManager();
    }

    private org.infinispan.configuration.cache.Configuration getDefaultCacheConfiguration(ConfigurationBuilder defaultConfigurationBuilder) {
        if (defaultConfigurationBuilder != null) {
            return defaultConfigurationBuilder.build();
        }
        return new ConfigurationBuilder().build();
    }
}
