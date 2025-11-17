package org.springframework.boot.autoconfigure.quartz;

import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.quartz.Calendar;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.sql.init.OnDatabaseInitializationCondition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.sql.init.dependency.DatabaseInitializationDependencyConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.transaction.PlatformTransactionManager;

@EnableConfigurationProperties({QuartzProperties.class})
@AutoConfiguration(after = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, LiquibaseAutoConfiguration.class, FlywayAutoConfiguration.class})
@ConditionalOnClass({Scheduler.class, SchedulerFactoryBean.class, PlatformTransactionManager.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/quartz/QuartzAutoConfiguration.class */
public class QuartzAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public SchedulerFactoryBean quartzScheduler(QuartzProperties properties, ObjectProvider<SchedulerFactoryBeanCustomizer> customizers, ObjectProvider<JobDetail> jobDetails, Map<String, Calendar> calendars, ObjectProvider<Trigger> triggers, ApplicationContext applicationContext) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        SpringBeanJobFactory jobFactory = new SpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        schedulerFactoryBean.setJobFactory(jobFactory);
        if (properties.getSchedulerName() != null) {
            schedulerFactoryBean.setSchedulerName(properties.getSchedulerName());
        }
        schedulerFactoryBean.setAutoStartup(properties.isAutoStartup());
        schedulerFactoryBean.setStartupDelay((int) properties.getStartupDelay().getSeconds());
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(properties.isWaitForJobsToCompleteOnShutdown());
        schedulerFactoryBean.setOverwriteExistingJobs(properties.isOverwriteExistingJobs());
        if (!properties.getProperties().isEmpty()) {
            schedulerFactoryBean.setQuartzProperties(asProperties(properties.getProperties()));
        }
        schedulerFactoryBean.setJobDetails((JobDetail[]) jobDetails.orderedStream().toArray(x$0 -> {
            return new JobDetail[x$0];
        }));
        schedulerFactoryBean.setCalendars(calendars);
        schedulerFactoryBean.setTriggers((Trigger[]) triggers.orderedStream().toArray(x$02 -> {
            return new Trigger[x$02];
        }));
        customizers.orderedStream().forEach(customizer -> {
            customizer.customize(schedulerFactoryBean);
        });
        return schedulerFactoryBean;
    }

    private Properties asProperties(Map<String, String> source) {
        Properties properties = new Properties();
        properties.putAll(source);
        return properties;
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnSingleCandidate(DataSource.class)
    @ConditionalOnProperty(prefix = "spring.quartz", name = {"job-store-type"}, havingValue = "jdbc")
    @Import({DatabaseInitializationDependencyConfigurer.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/quartz/QuartzAutoConfiguration$JdbcStoreTypeConfiguration.class */
    protected static class JdbcStoreTypeConfiguration {
        protected JdbcStoreTypeConfiguration() {
        }

        @Bean
        @Order(0)
        public SchedulerFactoryBeanCustomizer dataSourceCustomizer(QuartzProperties properties, DataSource dataSource, @QuartzDataSource ObjectProvider<DataSource> quartzDataSource, ObjectProvider<PlatformTransactionManager> transactionManager, @QuartzTransactionManager ObjectProvider<PlatformTransactionManager> quartzTransactionManager) {
            return schedulerFactoryBean -> {
                DataSource dataSourceToUse = getDataSource(dataSource, quartzDataSource);
                schedulerFactoryBean.setDataSource(dataSourceToUse);
                PlatformTransactionManager txManager = getTransactionManager(transactionManager, quartzTransactionManager);
                if (txManager != null) {
                    schedulerFactoryBean.setTransactionManager(txManager);
                }
            };
        }

        private DataSource getDataSource(DataSource dataSource, ObjectProvider<DataSource> quartzDataSource) {
            DataSource dataSourceIfAvailable = quartzDataSource.getIfAvailable();
            return dataSourceIfAvailable != null ? dataSourceIfAvailable : dataSource;
        }

        private PlatformTransactionManager getTransactionManager(ObjectProvider<PlatformTransactionManager> transactionManager, ObjectProvider<PlatformTransactionManager> quartzTransactionManager) {
            PlatformTransactionManager transactionManagerIfAvailable = quartzTransactionManager.getIfAvailable();
            return transactionManagerIfAvailable != null ? transactionManagerIfAvailable : transactionManager.getIfUnique();
        }

        @ConditionalOnMissingBean({QuartzDataSourceScriptDatabaseInitializer.class})
        @Conditional({OnQuartzDatasourceInitializationCondition.class})
        @Bean
        public QuartzDataSourceScriptDatabaseInitializer quartzDataSourceScriptDatabaseInitializer(DataSource dataSource, @QuartzDataSource ObjectProvider<DataSource> quartzDataSource, QuartzProperties properties) {
            DataSource dataSourceToUse = getDataSource(dataSource, quartzDataSource);
            return new QuartzDataSourceScriptDatabaseInitializer(dataSourceToUse, properties);
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/quartz/QuartzAutoConfiguration$JdbcStoreTypeConfiguration$OnQuartzDatasourceInitializationCondition.class */
        static class OnQuartzDatasourceInitializationCondition extends OnDatabaseInitializationCondition {
            OnQuartzDatasourceInitializationCondition() {
                super("Quartz", "spring.quartz.jdbc.initialize-schema");
            }
        }
    }
}
