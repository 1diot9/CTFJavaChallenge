package org.springframework.boot.autoconfigure.data.jdbc;

import java.util.Optional;
import java.util.Set;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.core.convert.DataAccessStrategy;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.core.convert.RelationResolver;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.JdbcRepositoryConfigExtension;
import org.springframework.data.relational.RelationalManagedTypes;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.transaction.PlatformTransactionManager;

@AutoConfiguration(after = {JdbcTemplateAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
@ConditionalOnClass({NamedParameterJdbcOperations.class, AbstractJdbcConfiguration.class})
@ConditionalOnBean({NamedParameterJdbcOperations.class, PlatformTransactionManager.class})
@ConditionalOnProperty(prefix = "spring.data.jdbc.repositories", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/jdbc/JdbcRepositoriesAutoConfiguration.class */
public class JdbcRepositoriesAutoConfiguration {

    @ConditionalOnMissingBean({JdbcRepositoryConfigExtension.class})
    @Configuration(proxyBeanMethods = false)
    @Import({JdbcRepositoriesRegistrar.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/jdbc/JdbcRepositoriesAutoConfiguration$JdbcRepositoriesConfiguration.class */
    static class JdbcRepositoriesConfiguration {
        JdbcRepositoriesConfiguration() {
        }
    }

    @ConditionalOnMissingBean({AbstractJdbcConfiguration.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/jdbc/JdbcRepositoriesAutoConfiguration$SpringBootJdbcConfiguration.class */
    static class SpringBootJdbcConfiguration extends AbstractJdbcConfiguration {
        private final ApplicationContext applicationContext;

        SpringBootJdbcConfiguration(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        protected Set<Class<?>> getInitialEntitySet() throws ClassNotFoundException {
            return new EntityScanner(this.applicationContext).scan(Table.class);
        }

        @ConditionalOnMissingBean
        @Bean
        public RelationalManagedTypes jdbcManagedTypes() throws ClassNotFoundException {
            return super.jdbcManagedTypes();
        }

        @ConditionalOnMissingBean
        @Bean
        public JdbcMappingContext jdbcMappingContext(Optional<NamingStrategy> namingStrategy, JdbcCustomConversions customConversions, RelationalManagedTypes jdbcManagedTypes) {
            return super.jdbcMappingContext(namingStrategy, customConversions, jdbcManagedTypes);
        }

        @ConditionalOnMissingBean
        @Bean
        public JdbcConverter jdbcConverter(JdbcMappingContext mappingContext, NamedParameterJdbcOperations operations, @Lazy RelationResolver relationResolver, JdbcCustomConversions conversions, Dialect dialect) {
            return super.jdbcConverter(mappingContext, operations, relationResolver, conversions, dialect);
        }

        @ConditionalOnMissingBean
        @Bean
        public JdbcCustomConversions jdbcCustomConversions() {
            return super.jdbcCustomConversions();
        }

        @ConditionalOnMissingBean
        @Bean
        public JdbcAggregateTemplate jdbcAggregateTemplate(ApplicationContext applicationContext, JdbcMappingContext mappingContext, JdbcConverter converter, DataAccessStrategy dataAccessStrategy) {
            return super.jdbcAggregateTemplate(applicationContext, mappingContext, converter, dataAccessStrategy);
        }

        @ConditionalOnMissingBean
        @Bean
        public DataAccessStrategy dataAccessStrategyBean(NamedParameterJdbcOperations operations, JdbcConverter jdbcConverter, JdbcMappingContext context, Dialect dialect) {
            return super.dataAccessStrategyBean(operations, jdbcConverter, context, dialect);
        }

        @ConditionalOnMissingBean
        @Bean
        public Dialect jdbcDialect(NamedParameterJdbcOperations operations) {
            return super.jdbcDialect(operations);
        }
    }
}
