package org.springframework.boot.autoconfigure.freemarker;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

@Configuration(proxyBeanMethods = false)
@ConditionalOnNotWebApplication
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/freemarker/FreeMarkerNonWebConfiguration.class */
class FreeMarkerNonWebConfiguration extends AbstractFreeMarkerConfiguration {
    FreeMarkerNonWebConfiguration(FreeMarkerProperties properties) {
        super(properties);
    }

    @ConditionalOnMissingBean
    @Bean
    FreeMarkerConfigurationFactoryBean freeMarkerConfiguration() {
        FreeMarkerConfigurationFactoryBean freeMarkerFactoryBean = new FreeMarkerConfigurationFactoryBean();
        applyProperties(freeMarkerFactoryBean);
        return freeMarkerFactoryBean;
    }
}
