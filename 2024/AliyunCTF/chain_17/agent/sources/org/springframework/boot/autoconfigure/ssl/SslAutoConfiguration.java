package org.springframework.boot.autoconfigure.ssl;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.boot.ssl.SslBundleRegistry;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties({SslProperties.class})
@AutoConfiguration
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/ssl/SslAutoConfiguration.class */
public class SslAutoConfiguration {
    private final SslProperties sslProperties;

    SslAutoConfiguration(SslProperties sslProperties) {
        this.sslProperties = sslProperties;
    }

    @Bean
    FileWatcher fileWatcher() {
        return new FileWatcher(this.sslProperties.getBundle().getWatch().getFile().getQuietPeriod());
    }

    @Bean
    SslPropertiesBundleRegistrar sslPropertiesSslBundleRegistrar(FileWatcher fileWatcher) {
        return new SslPropertiesBundleRegistrar(this.sslProperties, fileWatcher);
    }

    @ConditionalOnMissingBean({SslBundleRegistry.class, SslBundles.class})
    @Bean
    DefaultSslBundleRegistry sslBundleRegistry(ObjectProvider<SslBundleRegistrar> sslBundleRegistrars) {
        DefaultSslBundleRegistry registry = new DefaultSslBundleRegistry();
        sslBundleRegistrars.orderedStream().forEach(registrar -> {
            registrar.registerBundles(registry);
        });
        return registry;
    }
}
