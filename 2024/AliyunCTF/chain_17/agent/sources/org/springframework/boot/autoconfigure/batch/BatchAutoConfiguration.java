package org.springframework.boot.autoconfigure.batch;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.sql.init.OnDatabaseInitializationCondition;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.sql.init.dependency.DatabaseInitializationDependencyConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.util.StringUtils;

@EnableConfigurationProperties({BatchProperties.class})
@AutoConfiguration(after = {HibernateJpaAutoConfiguration.class, TransactionAutoConfiguration.class})
@ConditionalOnClass({JobLauncher.class, DataSource.class, DatabasePopulator.class})
@ConditionalOnMissingBean(value = {DefaultBatchConfiguration.class}, annotation = {EnableBatchProcessing.class})
@ConditionalOnBean({DataSource.class, PlatformTransactionManager.class})
@Import({DatabaseInitializationDependencyConfigurer.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/batch/BatchAutoConfiguration.class */
public class BatchAutoConfiguration {
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.batch.job", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
    @Bean
    public JobLauncherApplicationRunner jobLauncherApplicationRunner(JobLauncher jobLauncher, JobExplorer jobExplorer, JobRepository jobRepository, BatchProperties properties) {
        JobLauncherApplicationRunner runner = new JobLauncherApplicationRunner(jobLauncher, jobExplorer, jobRepository);
        String jobName = properties.getJob().getName();
        if (StringUtils.hasText(jobName)) {
            runner.setJobName(jobName);
        }
        return runner;
    }

    @ConditionalOnMissingBean({ExitCodeGenerator.class})
    @Bean
    public JobExecutionExitCodeGenerator jobExecutionExitCodeGenerator() {
        return new JobExecutionExitCodeGenerator();
    }

    @Configuration(proxyBeanMethods = false)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/batch/BatchAutoConfiguration$SpringBootBatchConfiguration.class */
    static class SpringBootBatchConfiguration extends DefaultBatchConfiguration {
        private final DataSource dataSource;
        private final PlatformTransactionManager transactionManager;
        private final BatchProperties properties;
        private final List<BatchConversionServiceCustomizer> batchConversionServiceCustomizers;

        SpringBootBatchConfiguration(DataSource dataSource, @BatchDataSource ObjectProvider<DataSource> batchDataSource, PlatformTransactionManager transactionManager, BatchProperties properties, ObjectProvider<BatchConversionServiceCustomizer> batchConversionServiceCustomizers) {
            this.dataSource = batchDataSource.getIfAvailable(() -> {
                return dataSource;
            });
            this.transactionManager = transactionManager;
            this.properties = properties;
            this.batchConversionServiceCustomizers = batchConversionServiceCustomizers.orderedStream().toList();
        }

        protected DataSource getDataSource() {
            return this.dataSource;
        }

        protected PlatformTransactionManager getTransactionManager() {
            return this.transactionManager;
        }

        protected String getTablePrefix() {
            String tablePrefix = this.properties.getJdbc().getTablePrefix();
            return tablePrefix != null ? tablePrefix : super.getTablePrefix();
        }

        protected Isolation getIsolationLevelForCreate() {
            Isolation isolation = this.properties.getJdbc().getIsolationLevelForCreate();
            return isolation != null ? isolation : super.getIsolationLevelForCreate();
        }

        protected ConfigurableConversionService getConversionService() {
            ConfigurableConversionService conversionService = super.getConversionService();
            for (BatchConversionServiceCustomizer customizer : this.batchConversionServiceCustomizers) {
                customizer.customize(conversionService);
            }
            return conversionService;
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Conditional({OnBatchDatasourceInitializationCondition.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/batch/BatchAutoConfiguration$DataSourceInitializerConfiguration.class */
    static class DataSourceInitializerConfiguration {
        DataSourceInitializerConfiguration() {
        }

        @ConditionalOnMissingBean({BatchDataSourceScriptDatabaseInitializer.class})
        @Bean
        BatchDataSourceScriptDatabaseInitializer batchDataSourceInitializer(DataSource dataSource, @BatchDataSource ObjectProvider<DataSource> batchDataSource, BatchProperties properties) {
            return new BatchDataSourceScriptDatabaseInitializer(batchDataSource.getIfAvailable(() -> {
                return dataSource;
            }), properties.getJdbc());
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/batch/BatchAutoConfiguration$OnBatchDatasourceInitializationCondition.class */
    static class OnBatchDatasourceInitializationCondition extends OnDatabaseInitializationCondition {
        OnBatchDatasourceInitializationCondition() {
            super("Batch", "spring.batch.jdbc.initialize-schema", "spring.batch.initialize-schema");
        }
    }
}
