package org.springframework.boot.autoconfigure.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import javax.sql.DataSource;
import oracle.jdbc.OracleConnection;
import oracle.ucp.jdbc.PoolDataSourceImpl;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceConfiguration.class */
abstract class DataSourceConfiguration {
    DataSourceConfiguration() {
    }

    private static <T> T createDataSource(JdbcConnectionDetails jdbcConnectionDetails, Class<? extends DataSource> cls, ClassLoader classLoader) {
        return (T) DataSourceBuilder.create(classLoader).type(cls).driverClassName(jdbcConnectionDetails.getDriverClassName()).url(jdbcConnectionDetails.getJdbcUrl()).username(jdbcConnectionDetails.getUsername()).password(jdbcConnectionDetails.getPassword()).build();
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({org.apache.tomcat.jdbc.pool.DataSource.class})
    @ConditionalOnMissingBean({DataSource.class})
    @ConditionalOnProperty(name = {"spring.datasource.type"}, havingValue = "org.apache.tomcat.jdbc.pool.DataSource", matchIfMissing = true)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceConfiguration$Tomcat.class */
    static class Tomcat {
        Tomcat() {
        }

        @ConditionalOnMissingBean({PropertiesJdbcConnectionDetails.class})
        @Bean
        static TomcatJdbcConnectionDetailsBeanPostProcessor tomcatJdbcConnectionDetailsBeanPostProcessor(ObjectProvider<JdbcConnectionDetails> connectionDetailsProvider) {
            return new TomcatJdbcConnectionDetailsBeanPostProcessor(connectionDetailsProvider);
        }

        @ConfigurationProperties(prefix = "spring.datasource.tomcat")
        @Bean
        org.apache.tomcat.jdbc.pool.DataSource dataSource(DataSourceProperties properties, JdbcConnectionDetails connectionDetails) {
            org.apache.tomcat.jdbc.pool.DataSource dataSource = (org.apache.tomcat.jdbc.pool.DataSource) DataSourceConfiguration.createDataSource(connectionDetails, org.apache.tomcat.jdbc.pool.DataSource.class, properties.getClassLoader());
            DatabaseDriver databaseDriver = DatabaseDriver.fromJdbcUrl(connectionDetails.getJdbcUrl());
            String validationQuery = databaseDriver.getValidationQuery();
            if (validationQuery != null) {
                dataSource.setTestOnBorrow(true);
                dataSource.setValidationQuery(validationQuery);
            }
            return dataSource;
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({HikariDataSource.class})
    @ConditionalOnMissingBean({DataSource.class})
    @ConditionalOnProperty(name = {"spring.datasource.type"}, havingValue = "com.zaxxer.hikari.HikariDataSource", matchIfMissing = true)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceConfiguration$Hikari.class */
    static class Hikari {
        Hikari() {
        }

        @Bean
        static HikariJdbcConnectionDetailsBeanPostProcessor jdbcConnectionDetailsHikariBeanPostProcessor(ObjectProvider<JdbcConnectionDetails> connectionDetailsProvider) {
            return new HikariJdbcConnectionDetailsBeanPostProcessor(connectionDetailsProvider);
        }

        @ConfigurationProperties(prefix = "spring.datasource.hikari")
        @Bean
        HikariDataSource dataSource(DataSourceProperties properties, JdbcConnectionDetails connectionDetails) {
            HikariDataSource dataSource = (HikariDataSource) DataSourceConfiguration.createDataSource(connectionDetails, HikariDataSource.class, properties.getClassLoader());
            if (StringUtils.hasText(properties.getName())) {
                dataSource.setPoolName(properties.getName());
            }
            return dataSource;
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({BasicDataSource.class})
    @ConditionalOnMissingBean({DataSource.class})
    @ConditionalOnProperty(name = {"spring.datasource.type"}, havingValue = "org.apache.commons.dbcp2.BasicDataSource", matchIfMissing = true)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceConfiguration$Dbcp2.class */
    static class Dbcp2 {
        Dbcp2() {
        }

        @Bean
        static Dbcp2JdbcConnectionDetailsBeanPostProcessor dbcp2JdbcConnectionDetailsBeanPostProcessor(ObjectProvider<JdbcConnectionDetails> connectionDetailsProvider) {
            return new Dbcp2JdbcConnectionDetailsBeanPostProcessor(connectionDetailsProvider);
        }

        @ConfigurationProperties(prefix = "spring.datasource.dbcp2")
        @Bean
        BasicDataSource dataSource(DataSourceProperties properties, JdbcConnectionDetails connectionDetails) {
            return (BasicDataSource) DataSourceConfiguration.createDataSource(connectionDetails, BasicDataSource.class, properties.getClassLoader());
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({PoolDataSourceImpl.class, OracleConnection.class})
    @ConditionalOnMissingBean({DataSource.class})
    @ConditionalOnProperty(name = {"spring.datasource.type"}, havingValue = "oracle.ucp.jdbc.PoolDataSource", matchIfMissing = true)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceConfiguration$OracleUcp.class */
    static class OracleUcp {
        OracleUcp() {
        }

        @Bean
        static OracleUcpJdbcConnectionDetailsBeanPostProcessor oracleUcpJdbcConnectionDetailsBeanPostProcessor(ObjectProvider<JdbcConnectionDetails> connectionDetailsProvider) {
            return new OracleUcpJdbcConnectionDetailsBeanPostProcessor(connectionDetailsProvider);
        }

        @ConfigurationProperties(prefix = "spring.datasource.oracleucp")
        @Bean
        PoolDataSourceImpl dataSource(DataSourceProperties properties, JdbcConnectionDetails connectionDetails) throws SQLException {
            PoolDataSourceImpl dataSource = (PoolDataSourceImpl) DataSourceConfiguration.createDataSource(connectionDetails, PoolDataSourceImpl.class, properties.getClassLoader());
            if (StringUtils.hasText(properties.getName())) {
                dataSource.setConnectionPoolName(properties.getName());
            }
            return dataSource;
        }
    }

    @ConditionalOnMissingBean({DataSource.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = {"spring.datasource.type"})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceConfiguration$Generic.class */
    static class Generic {
        Generic() {
        }

        @Bean
        DataSource dataSource(DataSourceProperties properties, JdbcConnectionDetails connectionDetails) {
            return (DataSource) DataSourceConfiguration.createDataSource(connectionDetails, properties.getType(), properties.getClassLoader());
        }
    }
}
