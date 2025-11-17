package org.springframework.boot.autoconfigure.jdbc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@ConditionalOnMissingBean({NamedParameterJdbcOperations.class})
@Configuration(proxyBeanMethods = false)
@ConditionalOnSingleCandidate(JdbcTemplate.class)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/NamedParameterJdbcTemplateConfiguration.class */
class NamedParameterJdbcTemplateConfiguration {
    NamedParameterJdbcTemplateConfiguration() {
    }

    @Bean
    @Primary
    NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
        return new NamedParameterJdbcTemplate(jdbcTemplate);
    }
}
