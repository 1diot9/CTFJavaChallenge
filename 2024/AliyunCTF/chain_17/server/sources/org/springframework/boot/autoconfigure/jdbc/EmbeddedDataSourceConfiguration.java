package org.springframework.boot.autoconfigure.jdbc;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

@EnableConfigurationProperties({DataSourceProperties.class})
@Configuration(proxyBeanMethods = false)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/EmbeddedDataSourceConfiguration.class */
public class EmbeddedDataSourceConfiguration implements BeanClassLoaderAware {
    private ClassLoader classLoader;

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Bean(destroyMethod = "shutdown")
    public EmbeddedDatabase dataSource(DataSourceProperties properties) {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseConnection.get(this.classLoader).getType()).setName(properties.determineDatabaseName()).build();
    }
}
