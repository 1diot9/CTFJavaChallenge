package org.springframework.boot.autoconfigure.security.rsocket;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.rsocket.RSocketMessageHandlerCustomizer;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.invocation.reactive.HandlerMethodArgumentResolver;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.rsocket.core.SecuritySocketAcceptorInterceptor;

@EnableRSocketSecurity
@AutoConfiguration
@ConditionalOnClass({SecuritySocketAcceptorInterceptor.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/rsocket/RSocketSecurityAutoConfiguration.class */
public class RSocketSecurityAutoConfiguration {
    @Bean
    RSocketServerCustomizer springSecurityRSocketSecurity(SecuritySocketAcceptorInterceptor interceptor) {
        return server -> {
            server.interceptors(registry -> {
                registry.forSocketAcceptor(interceptor);
            });
        };
    }

    @ConditionalOnClass({AuthenticationPrincipalArgumentResolver.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/rsocket/RSocketSecurityAutoConfiguration$RSocketSecurityMessageHandlerConfiguration.class */
    static class RSocketSecurityMessageHandlerConfiguration {
        RSocketSecurityMessageHandlerConfiguration() {
        }

        @Bean
        RSocketMessageHandlerCustomizer rSocketAuthenticationPrincipalMessageHandlerCustomizer() {
            return messageHandler -> {
                messageHandler.getArgumentResolverConfigurer().addCustomResolver(new HandlerMethodArgumentResolver[]{new AuthenticationPrincipalArgumentResolver()});
            };
        }
    }
}
