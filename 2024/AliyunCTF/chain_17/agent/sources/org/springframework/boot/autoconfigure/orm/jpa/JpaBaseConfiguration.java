package org.springframework.boot.autoconfigure.orm.jpa;

import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.autoconfigure.web.servlet.ConditionalOnMissingFilterBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypesScanner;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableConfigurationProperties({JpaProperties.class})
@Configuration(proxyBeanMethods = false)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/orm/jpa/JpaBaseConfiguration.class */
public abstract class JpaBaseConfiguration {
    private final DataSource dataSource;
    private final JpaProperties properties;
    private final JtaTransactionManager jtaTransactionManager;

    protected abstract AbstractJpaVendorAdapter createJpaVendorAdapter();

    protected abstract Map<String, Object> getVendorProperties();

    /* JADX INFO: Access modifiers changed from: protected */
    public JpaBaseConfiguration(DataSource dataSource, JpaProperties properties, ObjectProvider<JtaTransactionManager> jtaTransactionManager) {
        this.dataSource = dataSource;
        this.properties = properties;
        this.jtaTransactionManager = jtaTransactionManager.getIfAvailable();
    }

    @ConditionalOnMissingBean({TransactionManager.class})
    @Bean
    public PlatformTransactionManager transactionManager(ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManagerCustomizers.ifAvailable(customizers -> {
            customizers.customize((TransactionManager) transactionManager);
        });
        return transactionManager;
    }

    @ConditionalOnMissingBean
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        AbstractJpaVendorAdapter adapter = createJpaVendorAdapter();
        adapter.setShowSql(this.properties.isShowSql());
        if (this.properties.getDatabase() != null) {
            adapter.setDatabase(this.properties.getDatabase());
        }
        if (this.properties.getDatabasePlatform() != null) {
            adapter.setDatabasePlatform(this.properties.getDatabasePlatform());
        }
        adapter.setGenerateDdl(this.properties.isGenerateDdl());
        return adapter;
    }

    @ConditionalOnMissingBean
    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder(JpaVendorAdapter jpaVendorAdapter, ObjectProvider<PersistenceUnitManager> persistenceUnitManager, ObjectProvider<EntityManagerFactoryBuilderCustomizer> customizers) {
        EntityManagerFactoryBuilder builder = new EntityManagerFactoryBuilder(jpaVendorAdapter, this.properties.getProperties(), persistenceUnitManager.getIfAvailable());
        customizers.orderedStream().forEach(customizer -> {
            customizer.customize(builder);
        });
        return builder;
    }

    @ConditionalOnMissingBean({LocalContainerEntityManagerFactoryBean.class, EntityManagerFactory.class})
    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder factoryBuilder, PersistenceManagedTypes persistenceManagedTypes) {
        Map<String, Object> vendorProperties = getVendorProperties();
        customizeVendorProperties(vendorProperties);
        return factoryBuilder.dataSource(this.dataSource).managedTypes(persistenceManagedTypes).properties(vendorProperties).mappingResources(getMappingResources()).jta(isJta()).build();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void customizeVendorProperties(Map<String, Object> vendorProperties) {
    }

    private String[] getMappingResources() {
        List<String> mappingResources = this.properties.getMappingResources();
        if (ObjectUtils.isEmpty(mappingResources)) {
            return null;
        }
        return StringUtils.toStringArray(mappingResources);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JtaTransactionManager getJtaTransactionManager() {
        return this.jtaTransactionManager;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean isJta() {
        return this.jtaTransactionManager != null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final JpaProperties getProperties() {
        return this.properties;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final DataSource getDataSource() {
        return this.dataSource;
    }

    @ConditionalOnMissingBean({LocalContainerEntityManagerFactoryBean.class, EntityManagerFactory.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/orm/jpa/JpaBaseConfiguration$PersistenceManagedTypesConfiguration.class */
    static class PersistenceManagedTypesConfiguration {
        PersistenceManagedTypesConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        @Primary
        static PersistenceManagedTypes persistenceManagedTypes(BeanFactory beanFactory, ResourceLoader resourceLoader) {
            String[] packagesToScan = getPackagesToScan(beanFactory);
            return new PersistenceManagedTypesScanner(resourceLoader).scan(packagesToScan);
        }

        private static String[] getPackagesToScan(BeanFactory beanFactory) {
            List<String> packages = EntityScanPackages.get(beanFactory).getPackageNames();
            if (packages.isEmpty() && AutoConfigurationPackages.has(beanFactory)) {
                packages = AutoConfigurationPackages.get(beanFactory);
            }
            return StringUtils.toStringArray(packages);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({WebMvcConfigurer.class})
    @ConditionalOnMissingBean({OpenEntityManagerInViewInterceptor.class, OpenEntityManagerInViewFilter.class})
    @ConditionalOnMissingFilterBean({OpenEntityManagerInViewFilter.class})
    @ConditionalOnProperty(prefix = "spring.jpa", name = {"open-in-view"}, havingValue = "true", matchIfMissing = true)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/orm/jpa/JpaBaseConfiguration$JpaWebConfiguration.class */
    protected static class JpaWebConfiguration {
        private static final Log logger = LogFactory.getLog((Class<?>) JpaWebConfiguration.class);
        private final JpaProperties jpaProperties;

        protected JpaWebConfiguration(JpaProperties jpaProperties) {
            this.jpaProperties = jpaProperties;
        }

        @Bean
        public OpenEntityManagerInViewInterceptor openEntityManagerInViewInterceptor() {
            if (this.jpaProperties.getOpenInView() == null) {
                logger.warn("spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning");
            }
            return new OpenEntityManagerInViewInterceptor();
        }

        @Bean
        public WebMvcConfigurer openEntityManagerInViewInterceptorConfigurer(final OpenEntityManagerInViewInterceptor interceptor) {
            return new WebMvcConfigurer() { // from class: org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration.JpaWebConfiguration.1
                @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
                public void addInterceptors(InterceptorRegistry registry) {
                    registry.addWebRequestInterceptor(interceptor);
                }
            };
        }
    }
}
