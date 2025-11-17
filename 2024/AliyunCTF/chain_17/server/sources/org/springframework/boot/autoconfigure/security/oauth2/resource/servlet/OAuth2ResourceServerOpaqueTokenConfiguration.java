package org.springframework.boot.autoconfigure.security.oauth2.resource.servlet;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.SpringOpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/servlet/OAuth2ResourceServerOpaqueTokenConfiguration.class */
class OAuth2ResourceServerOpaqueTokenConfiguration {
    OAuth2ResourceServerOpaqueTokenConfiguration() {
    }

    @ConditionalOnMissingBean({OpaqueTokenIntrospector.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/servlet/OAuth2ResourceServerOpaqueTokenConfiguration$OpaqueTokenIntrospectionClientConfiguration.class */
    static class OpaqueTokenIntrospectionClientConfiguration {
        OpaqueTokenIntrospectionClientConfiguration() {
        }

        @ConditionalOnProperty(name = {"spring.security.oauth2.resourceserver.opaquetoken.introspection-uri"})
        @Bean
        SpringOpaqueTokenIntrospector opaqueTokenIntrospector(OAuth2ResourceServerProperties properties) {
            OAuth2ResourceServerProperties.Opaquetoken opaqueToken = properties.getOpaquetoken();
            return new SpringOpaqueTokenIntrospector(opaqueToken.getIntrospectionUri(), opaqueToken.getClientId(), opaqueToken.getClientSecret());
        }
    }

    @ConditionalOnDefaultWebSecurity
    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/servlet/OAuth2ResourceServerOpaqueTokenConfiguration$OAuth2SecurityFilterChainConfiguration.class */
    static class OAuth2SecurityFilterChainConfiguration {
        OAuth2SecurityFilterChainConfiguration() {
        }

        @ConditionalOnBean({OpaqueTokenIntrospector.class})
        @Bean
        SecurityFilterChain opaqueTokenSecurityFilterChain(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests(requests -> {
                ((AuthorizeHttpRequestsConfigurer.AuthorizedUrl) requests.anyRequest()).authenticated();
            });
            http.oauth2ResourceServer(resourceServer -> {
                resourceServer.opaqueToken(Customizer.withDefaults());
            });
            return (SecurityFilterChain) http.build();
        }
    }
}
