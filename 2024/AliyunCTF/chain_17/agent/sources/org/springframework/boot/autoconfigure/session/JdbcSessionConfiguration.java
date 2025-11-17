package org.springframework.boot.autoconfigure.session;

import java.util.Objects;
import javax.sql.DataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.sql.init.OnDatabaseInitializationCondition;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.sql.init.dependency.DatabaseInitializationDependencyConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.SessionRepositoryCustomizer;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.session.jdbc.config.annotation.SpringSessionDataSource;
import org.springframework.session.jdbc.config.annotation.web.http.JdbcHttpSessionConfiguration;

@EnableConfigurationProperties({JdbcSessionProperties.class})
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({JdbcTemplate.class, JdbcIndexedSessionRepository.class})
@ConditionalOnMissingBean({SessionRepository.class})
@ConditionalOnBean({DataSource.class})
@Import({DatabaseInitializationDependencyConfigurer.class, JdbcHttpSessionConfiguration.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/JdbcSessionConfiguration.class */
class JdbcSessionConfiguration {
    JdbcSessionConfiguration() {
    }

    @ConditionalOnMissingBean({JdbcSessionDataSourceScriptDatabaseInitializer.class})
    @Conditional({OnJdbcSessionDatasourceInitializationCondition.class})
    @Bean
    JdbcSessionDataSourceScriptDatabaseInitializer jdbcSessionDataSourceScriptDatabaseInitializer(@SpringSessionDataSource ObjectProvider<DataSource> sessionDataSource, ObjectProvider<DataSource> dataSource, JdbcSessionProperties properties) {
        Objects.requireNonNull(dataSource);
        DataSource dataSourceToInitialize = sessionDataSource.getIfAvailable(dataSource::getObject);
        return new JdbcSessionDataSourceScriptDatabaseInitializer(dataSourceToInitialize, properties);
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    SessionRepositoryCustomizer<JdbcIndexedSessionRepository> springBootSessionRepositoryCustomizer(SessionProperties sessionProperties, JdbcSessionProperties jdbcSessionProperties, ServerProperties serverProperties) {
        return sessionRepository -> {
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            PropertyMapper.Source from = map.from((PropertyMapper) sessionProperties.determineTimeout(() -> {
                return serverProperties.getServlet().getSession().getTimeout();
            }));
            Objects.requireNonNull(sessionRepository);
            from.to(sessionRepository::setDefaultMaxInactiveInterval);
            Objects.requireNonNull(jdbcSessionProperties);
            PropertyMapper.Source from2 = map.from(jdbcSessionProperties::getTableName);
            Objects.requireNonNull(sessionRepository);
            from2.to(sessionRepository::setTableName);
            Objects.requireNonNull(jdbcSessionProperties);
            PropertyMapper.Source from3 = map.from(jdbcSessionProperties::getFlushMode);
            Objects.requireNonNull(sessionRepository);
            from3.to(sessionRepository::setFlushMode);
            Objects.requireNonNull(jdbcSessionProperties);
            PropertyMapper.Source from4 = map.from(jdbcSessionProperties::getSaveMode);
            Objects.requireNonNull(sessionRepository);
            from4.to(sessionRepository::setSaveMode);
            Objects.requireNonNull(jdbcSessionProperties);
            PropertyMapper.Source from5 = map.from(jdbcSessionProperties::getCleanupCron);
            Objects.requireNonNull(sessionRepository);
            from5.to(sessionRepository::setCleanupCron);
        };
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/JdbcSessionConfiguration$OnJdbcSessionDatasourceInitializationCondition.class */
    static class OnJdbcSessionDatasourceInitializationCondition extends OnDatabaseInitializationCondition {
        OnJdbcSessionDatasourceInitializationCondition() {
            super("Jdbc Session", "spring.session.jdbc.initialize-schema");
        }
    }
}
