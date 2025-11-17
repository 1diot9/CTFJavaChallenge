package org.springframework.boot.autoconfigure.security.oauth2.resource.reactive;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.SpringReactiveOpaqueTokenIntrospector;
import org.springframework.security.web.server.SecurityWebFilterChain;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/reactive/ReactiveOAuth2ResourceServerOpaqueTokenConfiguration.class */
class ReactiveOAuth2ResourceServerOpaqueTokenConfiguration {
    ReactiveOAuth2ResourceServerOpaqueTokenConfiguration() {
    }

    @ConditionalOnMissingBean({ReactiveOpaqueTokenIntrospector.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/reactive/ReactiveOAuth2ResourceServerOpaqueTokenConfiguration$OpaqueTokenIntrospectionClientConfiguration.class */
    static class OpaqueTokenIntrospectionClientConfiguration {
        OpaqueTokenIntrospectionClientConfiguration() {
        }

        @ConditionalOnProperty(name = {"spring.security.oauth2.resourceserver.opaquetoken.introspection-uri"})
        @Bean
        SpringReactiveOpaqueTokenIntrospector opaqueTokenIntrospector(OAuth2ResourceServerProperties properties) {
            OAuth2ResourceServerProperties.Opaquetoken opaqueToken = properties.getOpaquetoken();
            return new SpringReactiveOpaqueTokenIntrospector(opaqueToken.getIntrospectionUri(), opaqueToken.getClientId(), opaqueToken.getClientSecret());
        }
    }

    @ConditionalOnMissingBean({SecurityWebFilterChain.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/reactive/ReactiveOAuth2ResourceServerOpaqueTokenConfiguration$WebSecurityConfiguration.class */
    static class WebSecurityConfiguration {
        WebSecurityConfiguration() {
        }

        @ConditionalOnBean({ReactiveOpaqueTokenIntrospector.class})
        @Bean
        SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
            http.authorizeExchange(exchanges -> {
                exchanges.anyExchange().authenticated();
            });
            http.oauth2ResourceServer(resourceServer -> {
                resourceServer.opaqueToken(Customizer.withDefaults());
            });
            return http.build();
        }
    }
}
