package org.springframework.boot.autoconfigure.graphql.security;

import graphql.GraphQL;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.graphql.reactive.GraphQlWebFluxAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.execution.ReactiveSecurityDataFetcherExceptionResolver;
import org.springframework.graphql.server.webflux.GraphQlHttpHandler;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@AutoConfiguration(after = {GraphQlWebFluxAutoConfiguration.class})
@ConditionalOnClass({GraphQL.class, GraphQlHttpHandler.class, EnableWebFluxSecurity.class})
@ConditionalOnBean({GraphQlHttpHandler.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/graphql/security/GraphQlWebFluxSecurityAutoConfiguration.class */
public class GraphQlWebFluxSecurityAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public ReactiveSecurityDataFetcherExceptionResolver reactiveSecurityDataFetcherExceptionResolver() {
        return new ReactiveSecurityDataFetcherExceptionResolver();
    }
}
