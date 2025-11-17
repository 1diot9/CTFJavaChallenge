package org.springframework.boot.autoconfigure.security.reactive;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterChainProxy;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@EnableConfigurationProperties({SecurityProperties.class})
@AutoConfiguration
@ConditionalOnClass({Flux.class, EnableWebFluxSecurity.class, WebFilterChainProxy.class, WebFluxConfigurer.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/reactive/ReactiveSecurityAutoConfiguration.class */
public class ReactiveSecurityAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/reactive/ReactiveSecurityAutoConfiguration$SpringBootWebFluxSecurityConfiguration.class */
    class SpringBootWebFluxSecurityConfiguration {
        SpringBootWebFluxSecurityConfiguration() {
        }

        @ConditionalOnMissingBean({ReactiveAuthenticationManager.class, ReactiveUserDetailsService.class, SecurityWebFilterChain.class})
        @Bean
        ReactiveAuthenticationManager denyAllAuthenticationManager() {
            return authentication -> {
                return Mono.error(new UsernameNotFoundException(authentication.getName()));
            };
        }

        @ConditionalOnMissingBean({WebFilterChainProxy.class})
        @Configuration(proxyBeanMethods = false)
        @EnableWebFluxSecurity
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/reactive/ReactiveSecurityAutoConfiguration$SpringBootWebFluxSecurityConfiguration$EnableWebFluxSecurityConfiguration.class */
        static class EnableWebFluxSecurityConfiguration {
            EnableWebFluxSecurityConfiguration() {
            }
        }
    }
}
