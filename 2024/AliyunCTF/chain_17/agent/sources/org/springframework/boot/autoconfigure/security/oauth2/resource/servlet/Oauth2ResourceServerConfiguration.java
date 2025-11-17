package org.springframework.boot.autoconfigure.security.oauth2.resource.servlet;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerJwtConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerOpaqueTokenConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/servlet/Oauth2ResourceServerConfiguration.class */
class Oauth2ResourceServerConfiguration {
    Oauth2ResourceServerConfiguration() {
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({JwtDecoder.class})
    @Import({OAuth2ResourceServerJwtConfiguration.JwtDecoderConfiguration.class, OAuth2ResourceServerJwtConfiguration.OAuth2SecurityFilterChainConfiguration.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/servlet/Oauth2ResourceServerConfiguration$JwtConfiguration.class */
    static class JwtConfiguration {
        JwtConfiguration() {
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Import({OAuth2ResourceServerOpaqueTokenConfiguration.OpaqueTokenIntrospectionClientConfiguration.class, OAuth2ResourceServerOpaqueTokenConfiguration.OAuth2SecurityFilterChainConfiguration.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/servlet/Oauth2ResourceServerConfiguration$OpaqueTokenConfiguration.class */
    static class OpaqueTokenConfiguration {
        OpaqueTokenConfiguration() {
        }
    }
}
