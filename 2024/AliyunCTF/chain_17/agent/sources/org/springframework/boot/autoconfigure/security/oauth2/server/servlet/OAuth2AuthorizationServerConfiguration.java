package org.springframework.boot.autoconfigure.security.oauth2.server.servlet;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

@EnableConfigurationProperties({OAuth2AuthorizationServerProperties.class})
@Configuration(proxyBeanMethods = false)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/server/servlet/OAuth2AuthorizationServerConfiguration.class */
class OAuth2AuthorizationServerConfiguration {
    private final OAuth2AuthorizationServerPropertiesMapper propertiesMapper;

    OAuth2AuthorizationServerConfiguration(OAuth2AuthorizationServerProperties properties) {
        this.propertiesMapper = new OAuth2AuthorizationServerPropertiesMapper(properties);
    }

    @ConditionalOnMissingBean
    @Conditional({RegisteredClientsConfiguredCondition.class})
    @Bean
    RegisteredClientRepository registeredClientRepository() {
        return new InMemoryRegisteredClientRepository(this.propertiesMapper.asRegisteredClients());
    }

    @ConditionalOnMissingBean
    @Bean
    AuthorizationServerSettings authorizationServerSettings() {
        return this.propertiesMapper.asAuthorizationServerSettings();
    }
}
