package org.springframework.boot.autoconfigure.jdbc;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.sql.init.dependency.DatabaseInitializationDependencyConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;

@AutoConfiguration(after = {JdbcTemplateAutoConfiguration.class})
@ConditionalOnSingleCandidate(NamedParameterJdbcTemplate.class)
@ConditionalOnMissingBean({JdbcClient.class})
@Import({DatabaseInitializationDependencyConfigurer.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/JdbcClientAutoConfiguration.class */
public class JdbcClientAutoConfiguration {
    @Bean
    JdbcClient jdbcClient(NamedParameterJdbcTemplate jdbcTemplate) {
        return JdbcClient.create(jdbcTemplate);
    }
}
