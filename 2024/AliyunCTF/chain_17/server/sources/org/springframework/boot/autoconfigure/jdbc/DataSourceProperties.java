package org.springframework.boot.autoconfigure.jdbc;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "spring.datasource")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceProperties.class */
public class DataSourceProperties implements BeanClassLoaderAware, InitializingBean {
    private ClassLoader classLoader;
    private String name;
    private Class<? extends DataSource> type;
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private String jndiName;
    private EmbeddedDatabaseConnection embeddedDatabaseConnection;
    private String uniqueName;
    private boolean generateUniqueName = true;
    private Xa xa = new Xa();

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        if (this.embeddedDatabaseConnection == null) {
            this.embeddedDatabaseConnection = EmbeddedDatabaseConnection.get(this.classLoader);
        }
    }

    public DataSourceBuilder<?> initializeDataSourceBuilder() {
        return DataSourceBuilder.create(getClassLoader()).type(getType()).driverClassName(determineDriverClassName()).url(determineUrl()).username(determineUsername()).password(determinePassword());
    }

    public boolean isGenerateUniqueName() {
        return this.generateUniqueName;
    }

    public void setGenerateUniqueName(boolean generateUniqueName) {
        this.generateUniqueName = generateUniqueName;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<? extends DataSource> getType() {
        return this.type;
    }

    public void setType(Class<? extends DataSource> type) {
        this.type = type;
    }

    public String getDriverClassName() {
        return this.driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String determineDriverClassName() {
        if (StringUtils.hasText(this.driverClassName)) {
            Assert.state(driverClassIsLoadable(), (Supplier<String>) () -> {
                return "Cannot load driver class: " + this.driverClassName;
            });
            return this.driverClassName;
        }
        String driverClassName = null;
        if (StringUtils.hasText(this.url)) {
            driverClassName = DatabaseDriver.fromJdbcUrl(this.url).getDriverClassName();
        }
        if (!StringUtils.hasText(driverClassName)) {
            driverClassName = this.embeddedDatabaseConnection.getDriverClassName();
        }
        if (!StringUtils.hasText(driverClassName)) {
            throw new DataSourceBeanCreationException("Failed to determine a suitable driver class", this, this.embeddedDatabaseConnection);
        }
        return driverClassName;
    }

    private boolean driverClassIsLoadable() {
        try {
            ClassUtils.forName(this.driverClassName, null);
            return true;
        } catch (UnsupportedClassVersionError ex) {
            throw ex;
        } catch (Throwable ex2) {
            ex2.printStackTrace();
            return false;
        }
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String determineUrl() {
        if (StringUtils.hasText(this.url)) {
            return this.url;
        }
        String databaseName = determineDatabaseName();
        String url = databaseName != null ? this.embeddedDatabaseConnection.getUrl(databaseName) : null;
        if (!StringUtils.hasText(url)) {
            throw new DataSourceBeanCreationException("Failed to determine suitable jdbc url", this, this.embeddedDatabaseConnection);
        }
        return url;
    }

    public String determineDatabaseName() {
        if (this.generateUniqueName) {
            if (this.uniqueName == null) {
                this.uniqueName = UUID.randomUUID().toString();
            }
            return this.uniqueName;
        }
        if (StringUtils.hasLength(this.name)) {
            return this.name;
        }
        if (this.embeddedDatabaseConnection != EmbeddedDatabaseConnection.NONE) {
            return "testdb";
        }
        return null;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String determineUsername() {
        if (StringUtils.hasText(this.username)) {
            return this.username;
        }
        if (EmbeddedDatabaseConnection.isEmbedded(determineDriverClassName(), determineUrl())) {
            return "sa";
        }
        return null;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String determinePassword() {
        if (StringUtils.hasText(this.password)) {
            return this.password;
        }
        if (EmbeddedDatabaseConnection.isEmbedded(determineDriverClassName(), determineUrl())) {
            return "";
        }
        return null;
    }

    public String getJndiName() {
        return this.jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public EmbeddedDatabaseConnection getEmbeddedDatabaseConnection() {
        return this.embeddedDatabaseConnection;
    }

    public void setEmbeddedDatabaseConnection(EmbeddedDatabaseConnection embeddedDatabaseConnection) {
        this.embeddedDatabaseConnection = embeddedDatabaseConnection;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public Xa getXa() {
        return this.xa;
    }

    public void setXa(Xa xa) {
        this.xa = xa;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceProperties$Xa.class */
    public static class Xa {
        private String dataSourceClassName;
        private Map<String, String> properties = new LinkedHashMap();

        public String getDataSourceClassName() {
            return this.dataSourceClassName;
        }

        public void setDataSourceClassName(String dataSourceClassName) {
            this.dataSourceClassName = dataSourceClassName;
        }

        public Map<String, String> getProperties() {
            return this.properties;
        }

        public void setProperties(Map<String, String> properties) {
            this.properties = properties;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceProperties$DataSourceBeanCreationException.class */
    public static class DataSourceBeanCreationException extends BeanCreationException {
        private final DataSourceProperties properties;
        private final EmbeddedDatabaseConnection connection;

        DataSourceBeanCreationException(String message, DataSourceProperties properties, EmbeddedDatabaseConnection connection) {
            super(message);
            this.properties = properties;
            this.connection = connection;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public DataSourceProperties getProperties() {
            return this.properties;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public EmbeddedDatabaseConnection getConnection() {
            return this.connection;
        }
    }
}
