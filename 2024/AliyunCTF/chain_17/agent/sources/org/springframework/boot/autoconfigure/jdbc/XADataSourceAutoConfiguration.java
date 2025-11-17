package org.springframework.boot.autoconfigure.jdbc;

import ch.qos.logback.classic.ClassicConstants;
import jakarta.transaction.TransactionManager;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertyNameAliases;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.boot.jdbc.XADataSourceWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

@EnableConfigurationProperties({DataSourceProperties.class})
@AutoConfiguration(before = {DataSourceAutoConfiguration.class})
@ConditionalOnClass({DataSource.class, TransactionManager.class, EmbeddedDatabaseType.class})
@ConditionalOnMissingBean({DataSource.class})
@ConditionalOnBean({XADataSourceWrapper.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/XADataSourceAutoConfiguration.class */
public class XADataSourceAutoConfiguration implements BeanClassLoaderAware {
    private ClassLoader classLoader;

    @ConditionalOnMissingBean({JdbcConnectionDetails.class})
    @Bean
    PropertiesJdbcConnectionDetails jdbcConnectionDetails(DataSourceProperties properties) {
        return new PropertiesJdbcConnectionDetails(properties);
    }

    @Bean
    public DataSource dataSource(XADataSourceWrapper wrapper, DataSourceProperties properties, JdbcConnectionDetails connectionDetails, ObjectProvider<XADataSource> xaDataSource) throws Exception {
        return wrapper.wrapDataSource(xaDataSource.getIfAvailable(() -> {
            return createXaDataSource(properties, connectionDetails);
        }));
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    private XADataSource createXaDataSource(DataSourceProperties properties, JdbcConnectionDetails connectionDetails) {
        String className = connectionDetails.getXaDataSourceClassName();
        Assert.state(StringUtils.hasLength(className), "No XA DataSource class name specified");
        XADataSource dataSource = createXaDataSourceInstance(className);
        bindXaProperties(dataSource, properties, connectionDetails);
        return dataSource;
    }

    private XADataSource createXaDataSourceInstance(String className) {
        try {
            Class<?> dataSourceClass = ClassUtils.forName(className, this.classLoader);
            Object instance = BeanUtils.instantiateClass(dataSourceClass);
            Assert.isInstanceOf(XADataSource.class, instance);
            return (XADataSource) instance;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create XADataSource instance from '" + className + "'");
        }
    }

    private void bindXaProperties(XADataSource target, DataSourceProperties dataSourceProperties, JdbcConnectionDetails connectionDetails) {
        Binder binder = new Binder(getBinderSource(dataSourceProperties, connectionDetails));
        binder.bind(ConfigurationPropertyName.EMPTY, Bindable.ofInstance(target));
    }

    private ConfigurationPropertySource getBinderSource(DataSourceProperties dataSourceProperties, JdbcConnectionDetails connectionDetails) {
        Map<Object, Object> properties = new HashMap<>(dataSourceProperties.getXa().getProperties());
        properties.computeIfAbsent(ClassicConstants.USER_MDC_KEY, key -> {
            return connectionDetails.getUsername();
        });
        properties.computeIfAbsent("password", key2 -> {
            return connectionDetails.getPassword();
        });
        try {
            properties.computeIfAbsent("url", key3 -> {
                return connectionDetails.getJdbcUrl();
            });
        } catch (DataSourceProperties.DataSourceBeanCreationException e) {
        }
        MapConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
        ConfigurationPropertyNameAliases aliases = new ConfigurationPropertyNameAliases();
        aliases.addAliases(ClassicConstants.USER_MDC_KEY, "username");
        return source.withAliases(aliases);
    }
}
