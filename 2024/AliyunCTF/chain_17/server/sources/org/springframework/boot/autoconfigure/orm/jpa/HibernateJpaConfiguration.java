package org.springframework.boot.autoconfigure.orm.jpa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeHint;
import org.springframework.aot.hint.TypeReference;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.SchemaManagementProvider;
import org.springframework.boot.jdbc.metadata.CompositeDataSourcePoolMetadataProvider;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringJtaPlatform;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.ClassUtils;

@EnableConfigurationProperties({HibernateProperties.class})
@Configuration(proxyBeanMethods = false)
@ConditionalOnSingleCandidate(DataSource.class)
@ImportRuntimeHints({HibernateRuntimeHints.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaConfiguration.class */
class HibernateJpaConfiguration extends JpaBaseConfiguration {
    private static final String JTA_PLATFORM = "hibernate.transaction.jta.platform";
    private static final String PROVIDER_DISABLES_AUTOCOMMIT = "hibernate.connection.provider_disables_autocommit";
    private final HibernateProperties hibernateProperties;
    private final HibernateDefaultDdlAutoProvider defaultDdlAutoProvider;
    private final DataSourcePoolMetadataProvider poolMetadataProvider;
    private final List<HibernatePropertiesCustomizer> hibernatePropertiesCustomizers;
    private static final Log logger = LogFactory.getLog((Class<?>) HibernateJpaConfiguration.class);
    private static final String[] NO_JTA_PLATFORM_CLASSES = {"org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform", "org.hibernate.service.jta.platform.internal.NoJtaPlatform"};

    HibernateJpaConfiguration(DataSource dataSource, JpaProperties jpaProperties, ConfigurableListableBeanFactory beanFactory, ObjectProvider<JtaTransactionManager> jtaTransactionManager, HibernateProperties hibernateProperties, ObjectProvider<Collection<DataSourcePoolMetadataProvider>> metadataProviders, ObjectProvider<SchemaManagementProvider> providers, ObjectProvider<PhysicalNamingStrategy> physicalNamingStrategy, ObjectProvider<ImplicitNamingStrategy> implicitNamingStrategy, ObjectProvider<HibernatePropertiesCustomizer> hibernatePropertiesCustomizers) {
        super(dataSource, jpaProperties, jtaTransactionManager);
        this.hibernateProperties = hibernateProperties;
        this.defaultDdlAutoProvider = new HibernateDefaultDdlAutoProvider(providers);
        this.poolMetadataProvider = new CompositeDataSourcePoolMetadataProvider(metadataProviders.getIfAvailable());
        this.hibernatePropertiesCustomizers = determineHibernatePropertiesCustomizers(physicalNamingStrategy.getIfAvailable(), implicitNamingStrategy.getIfAvailable(), beanFactory, hibernatePropertiesCustomizers.orderedStream().toList());
    }

    private List<HibernatePropertiesCustomizer> determineHibernatePropertiesCustomizers(PhysicalNamingStrategy physicalNamingStrategy, ImplicitNamingStrategy implicitNamingStrategy, ConfigurableListableBeanFactory beanFactory, List<HibernatePropertiesCustomizer> hibernatePropertiesCustomizers) {
        List<HibernatePropertiesCustomizer> customizers = new ArrayList<>();
        if (ClassUtils.isPresent("org.hibernate.resource.beans.container.spi.BeanContainer", getClass().getClassLoader())) {
            customizers.add(properties -> {
                properties.put("hibernate.resource.beans.container", new SpringBeanContainer(beanFactory));
            });
        }
        if (physicalNamingStrategy != null || implicitNamingStrategy != null) {
            customizers.add(new NamingStrategiesHibernatePropertiesCustomizer(physicalNamingStrategy, implicitNamingStrategy));
        }
        customizers.addAll(hibernatePropertiesCustomizers);
        return customizers;
    }

    @Override // org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration
    protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Override // org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration
    protected Map<String, Object> getVendorProperties() {
        Supplier<String> defaultDdlMode = () -> {
            return this.defaultDdlAutoProvider.getDefaultDdlAuto(getDataSource());
        };
        return new LinkedHashMap(this.hibernateProperties.determineHibernateProperties(getProperties().getProperties(), new HibernateSettings().ddlAuto(defaultDdlMode).hibernatePropertiesCustomizers(this.hibernatePropertiesCustomizers)));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration
    public void customizeVendorProperties(Map<String, Object> vendorProperties) {
        super.customizeVendorProperties(vendorProperties);
        if (!vendorProperties.containsKey(JTA_PLATFORM)) {
            configureJtaPlatform(vendorProperties);
        }
        if (!vendorProperties.containsKey(PROVIDER_DISABLES_AUTOCOMMIT)) {
            configureProviderDisablesAutocommit(vendorProperties);
        }
    }

    private void configureJtaPlatform(Map<String, Object> vendorProperties) throws LinkageError {
        JtaTransactionManager jtaTransactionManager = getJtaTransactionManager();
        if (jtaTransactionManager == null) {
            vendorProperties.put(JTA_PLATFORM, getNoJtaPlatformManager());
        } else if (!runningOnWebSphere()) {
            configureSpringJtaPlatform(vendorProperties, jtaTransactionManager);
        }
    }

    private void configureProviderDisablesAutocommit(Map<String, Object> vendorProperties) {
        if (isDataSourceAutoCommitDisabled() && !isJta()) {
            vendorProperties.put(PROVIDER_DISABLES_AUTOCOMMIT, "true");
        }
    }

    private boolean isDataSourceAutoCommitDisabled() {
        DataSourcePoolMetadata poolMetadata = this.poolMetadataProvider.getDataSourcePoolMetadata(getDataSource());
        return poolMetadata != null && Boolean.FALSE.equals(poolMetadata.getDefaultAutoCommit());
    }

    private boolean runningOnWebSphere() {
        return ClassUtils.isPresent("com.ibm.websphere.jtaextensions.ExtendedJTATransaction", getClass().getClassLoader());
    }

    private void configureSpringJtaPlatform(Map<String, Object> vendorProperties, JtaTransactionManager jtaTransactionManager) {
        try {
            vendorProperties.put(JTA_PLATFORM, new SpringJtaPlatform(jtaTransactionManager));
        } catch (LinkageError ex) {
            if (!isUsingJndi()) {
                throw new IllegalStateException("Unable to set Hibernate JTA platform, are you using the correct version of Hibernate?", ex);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Unable to set Hibernate JTA platform : " + ex.getMessage());
            }
        }
    }

    private boolean isUsingJndi() {
        try {
            return JndiLocatorDelegate.isDefaultJndiEnvironmentAvailable();
        } catch (Error e) {
            return false;
        }
    }

    private Object getNoJtaPlatformManager() {
        for (String candidate : NO_JTA_PLATFORM_CLASSES) {
            try {
                return Class.forName(candidate).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (Exception e) {
            }
        }
        throw new IllegalStateException("No available JtaPlatform candidates amongst " + Arrays.toString(NO_JTA_PLATFORM_CLASSES));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaConfiguration$NamingStrategiesHibernatePropertiesCustomizer.class */
    public static class NamingStrategiesHibernatePropertiesCustomizer implements HibernatePropertiesCustomizer {
        private final PhysicalNamingStrategy physicalNamingStrategy;
        private final ImplicitNamingStrategy implicitNamingStrategy;

        NamingStrategiesHibernatePropertiesCustomizer(PhysicalNamingStrategy physicalNamingStrategy, ImplicitNamingStrategy implicitNamingStrategy) {
            this.physicalNamingStrategy = physicalNamingStrategy;
            this.implicitNamingStrategy = implicitNamingStrategy;
        }

        @Override // org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer
        public void customize(Map<String, Object> hibernateProperties) {
            if (this.physicalNamingStrategy != null) {
                hibernateProperties.put("hibernate.physical_naming_strategy", this.physicalNamingStrategy);
            }
            if (this.implicitNamingStrategy != null) {
                hibernateProperties.put("hibernate.implicit_naming_strategy", this.implicitNamingStrategy);
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaConfiguration$HibernateRuntimeHints.class */
    static class HibernateRuntimeHints implements RuntimeHintsRegistrar {
        private static final Consumer<TypeHint.Builder> INVOKE_DECLARED_CONSTRUCTORS = TypeHint.builtWith(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);

        HibernateRuntimeHints() {
        }

        @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            for (String noJtaPlatformClass : HibernateJpaConfiguration.NO_JTA_PLATFORM_CLASSES) {
                hints.reflection().registerType(TypeReference.of(noJtaPlatformClass), INVOKE_DECLARED_CONSTRUCTORS);
            }
            hints.reflection().registerType(SpringImplicitNamingStrategy.class, INVOKE_DECLARED_CONSTRUCTORS);
            hints.reflection().registerType(CamelCaseToUnderscoresNamingStrategy.class, INVOKE_DECLARED_CONSTRUCTORS);
        }
    }
}
