package org.springframework.boot.autoconfigure.liquibase;

import javax.sql.DataSource;
import liquibase.UpdateSummaryEnum;
import liquibase.UpdateSummaryOutputEnum;
import liquibase.change.DatabaseChange;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.sql.init.dependency.DatabaseInitializationDependencyConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@AutoConfiguration(after = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@ConditionalOnClass({SpringLiquibase.class, DatabaseChange.class})
@ImportRuntimeHints({LiquibaseAutoConfigurationRuntimeHints.class})
@ConditionalOnProperty(prefix = "spring.liquibase", name = {"enabled"}, matchIfMissing = true)
@Conditional({LiquibaseDataSourceCondition.class})
@Import({DatabaseInitializationDependencyConfigurer.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/liquibase/LiquibaseAutoConfiguration.class */
public class LiquibaseAutoConfiguration {
    @Bean
    public LiquibaseSchemaManagementProvider liquibaseDefaultDdlModeProvider(ObjectProvider<SpringLiquibase> liquibases) {
        return new LiquibaseSchemaManagementProvider(liquibases);
    }

    @EnableConfigurationProperties({LiquibaseProperties.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ConnectionCallback.class})
    @ConditionalOnMissingBean({SpringLiquibase.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/liquibase/LiquibaseAutoConfiguration$LiquibaseConfiguration.class */
    public static class LiquibaseConfiguration {
        @ConditionalOnMissingBean({LiquibaseConnectionDetails.class})
        @Bean
        PropertiesLiquibaseConnectionDetails liquibaseConnectionDetails(LiquibaseProperties properties, ObjectProvider<JdbcConnectionDetails> jdbcConnectionDetails) {
            return new PropertiesLiquibaseConnectionDetails(properties);
        }

        @Bean
        public SpringLiquibase liquibase(ObjectProvider<DataSource> dataSource, @LiquibaseDataSource ObjectProvider<DataSource> liquibaseDataSource, LiquibaseProperties properties, LiquibaseConnectionDetails connectionDetails) {
            SpringLiquibase liquibase = createSpringLiquibase(liquibaseDataSource.getIfAvailable(), dataSource.getIfUnique(), connectionDetails);
            liquibase.setChangeLog(properties.getChangeLog());
            liquibase.setClearCheckSums(properties.isClearChecksums());
            liquibase.setContexts(properties.getContexts());
            liquibase.setDefaultSchema(properties.getDefaultSchema());
            liquibase.setLiquibaseSchema(properties.getLiquibaseSchema());
            liquibase.setLiquibaseTablespace(properties.getLiquibaseTablespace());
            liquibase.setDatabaseChangeLogTable(properties.getDatabaseChangeLogTable());
            liquibase.setDatabaseChangeLogLockTable(properties.getDatabaseChangeLogLockTable());
            liquibase.setDropFirst(properties.isDropFirst());
            liquibase.setShouldRun(properties.isEnabled());
            liquibase.setLabelFilter(properties.getLabelFilter());
            liquibase.setChangeLogParameters(properties.getParameters());
            liquibase.setRollbackFile(properties.getRollbackFile());
            liquibase.setTestRollbackOnUpdate(properties.isTestRollbackOnUpdate());
            liquibase.setTag(properties.getTag());
            if (properties.getShowSummary() != null) {
                liquibase.setShowSummary(UpdateSummaryEnum.valueOf(properties.getShowSummary().name()));
            }
            if (properties.getShowSummaryOutput() != null) {
                liquibase.setShowSummaryOutput(UpdateSummaryOutputEnum.valueOf(properties.getShowSummaryOutput().name()));
            }
            return liquibase;
        }

        private SpringLiquibase createSpringLiquibase(DataSource liquibaseDataSource, DataSource dataSource, LiquibaseConnectionDetails connectionDetails) {
            SpringLiquibase springLiquibase;
            DataSource migrationDataSource = getMigrationDataSource(liquibaseDataSource, dataSource, connectionDetails);
            if (migrationDataSource == liquibaseDataSource || migrationDataSource == dataSource) {
                springLiquibase = new SpringLiquibase();
            } else {
                springLiquibase = new DataSourceClosingSpringLiquibase();
            }
            SpringLiquibase liquibase = springLiquibase;
            liquibase.setDataSource(migrationDataSource);
            return liquibase;
        }

        /* JADX WARN: Type inference failed for: r0v18, types: [javax.sql.DataSource] */
        /* JADX WARN: Type inference failed for: r0v25, types: [javax.sql.DataSource] */
        private DataSource getMigrationDataSource(DataSource liquibaseDataSource, DataSource dataSource, LiquibaseConnectionDetails connectionDetails) {
            if (liquibaseDataSource != null) {
                return liquibaseDataSource;
            }
            String url = connectionDetails.getJdbcUrl();
            if (url != null) {
                DataSourceBuilder<?> builder = DataSourceBuilder.create().type(SimpleDriverDataSource.class);
                builder.url(url);
                applyConnectionDetails(connectionDetails, builder);
                return builder.build();
            }
            String user = connectionDetails.getUsername();
            if (user != null && dataSource != null) {
                DataSourceBuilder<?> builder2 = DataSourceBuilder.derivedFrom(dataSource).type(SimpleDriverDataSource.class);
                applyConnectionDetails(connectionDetails, builder2);
                return builder2.build();
            }
            Assert.state(dataSource != null, "Liquibase migration DataSource missing");
            return dataSource;
        }

        private void applyConnectionDetails(LiquibaseConnectionDetails connectionDetails, DataSourceBuilder<?> builder) {
            builder.username(connectionDetails.getUsername());
            builder.password(connectionDetails.getPassword());
            String driverClassName = connectionDetails.getDriverClassName();
            if (StringUtils.hasText(driverClassName)) {
                builder.driverClassName(driverClassName);
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/liquibase/LiquibaseAutoConfiguration$LiquibaseDataSourceCondition.class */
    static final class LiquibaseDataSourceCondition extends AnyNestedCondition {
        LiquibaseDataSourceCondition() {
            super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnBean({DataSource.class})
        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/liquibase/LiquibaseAutoConfiguration$LiquibaseDataSourceCondition$DataSourceBeanCondition.class */
        private static final class DataSourceBeanCondition {
            private DataSourceBeanCondition() {
            }
        }

        @ConditionalOnBean({JdbcConnectionDetails.class})
        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/liquibase/LiquibaseAutoConfiguration$LiquibaseDataSourceCondition$JdbcConnectionDetailsCondition.class */
        private static final class JdbcConnectionDetailsCondition {
            private JdbcConnectionDetailsCondition() {
            }
        }

        @ConditionalOnProperty(prefix = "spring.liquibase", name = {"url"})
        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/liquibase/LiquibaseAutoConfiguration$LiquibaseDataSourceCondition$LiquibaseUrlCondition.class */
        private static final class LiquibaseUrlCondition {
            private LiquibaseUrlCondition() {
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/liquibase/LiquibaseAutoConfiguration$LiquibaseAutoConfigurationRuntimeHints.class */
    static class LiquibaseAutoConfigurationRuntimeHints implements RuntimeHintsRegistrar {
        LiquibaseAutoConfigurationRuntimeHints() {
        }

        @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.resources().registerPattern("db/changelog/*");
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/liquibase/LiquibaseAutoConfiguration$PropertiesLiquibaseConnectionDetails.class */
    static final class PropertiesLiquibaseConnectionDetails implements LiquibaseConnectionDetails {
        private final LiquibaseProperties properties;

        PropertiesLiquibaseConnectionDetails(LiquibaseProperties properties) {
            this.properties = properties;
        }

        @Override // org.springframework.boot.autoconfigure.liquibase.LiquibaseConnectionDetails
        public String getUsername() {
            return this.properties.getUser();
        }

        @Override // org.springframework.boot.autoconfigure.liquibase.LiquibaseConnectionDetails
        public String getPassword() {
            return this.properties.getPassword();
        }

        @Override // org.springframework.boot.autoconfigure.liquibase.LiquibaseConnectionDetails
        public String getJdbcUrl() {
            return this.properties.getUrl();
        }

        @Override // org.springframework.boot.autoconfigure.liquibase.LiquibaseConnectionDetails
        public String getDriverClassName() {
            String driverClassName = this.properties.getDriverClassName();
            return driverClassName != null ? driverClassName : super.getDriverClassName();
        }
    }
}
