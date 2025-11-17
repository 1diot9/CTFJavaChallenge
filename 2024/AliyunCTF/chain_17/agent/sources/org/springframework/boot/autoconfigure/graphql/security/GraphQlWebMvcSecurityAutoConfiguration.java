package org.springframework.boot.autoconfigure.graphql.security;

import graphql.GraphQL;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.graphql.servlet.GraphQlWebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.execution.SecurityDataFetcherExceptionResolver;
import org.springframework.graphql.server.webmvc.GraphQlHttpHandler;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@AutoConfiguration(after = {GraphQlWebMvcAutoConfiguration.class})
@ConditionalOnClass({GraphQL.class, GraphQlHttpHandler.class, EnableWebSecurity.class})
@ConditionalOnBean({GraphQlHttpHandler.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/graphql/security/GraphQlWebMvcSecurityAutoConfiguration.class */
public class GraphQlWebMvcSecurityAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public SecurityDataFetcherExceptionResolver securityDataFetcherExceptionResolver() {
        return new SecurityDataFetcherExceptionResolver();
    }
}
