package org.springframework.boot.jdbc;

import ch.qos.logback.classic.ClassicConstants;
import ch.qos.logback.core.joran.util.beans.BeanUtil;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zaxxer.hikari.HikariDataSource;
import java.beans.PropertyVetoException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Supplier;
import javax.sql.DataSource;
import oracle.jdbc.datasource.OracleDataSource;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceImpl;
import org.apache.commons.dbcp2.BasicDataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ResolvableType;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/DataSourceBuilder.class */
public final class DataSourceBuilder<T extends DataSource> {
    private final ClassLoader classLoader;
    private final Map<DataSourceProperty, String> values;
    private Class<T> type;
    private final DataSource deriveFrom;

    /* JADX INFO: Access modifiers changed from: private */
    @FunctionalInterface
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/DataSourceBuilder$Getter.class */
    public interface Getter<T, V> {
        V get(T instance) throws SQLException;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @FunctionalInterface
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/DataSourceBuilder$Setter.class */
    public interface Setter<T, V> {
        void set(T instance, V value) throws SQLException;
    }

    private DataSourceBuilder(ClassLoader classLoader) {
        this.values = new HashMap();
        this.classLoader = classLoader;
        this.deriveFrom = null;
    }

    private DataSourceBuilder(T t) {
        this.values = new HashMap();
        Assert.notNull(t, "DataSource must not be null");
        this.classLoader = t.getClass().getClassLoader();
        this.type = (Class<T>) t.getClass();
        this.deriveFrom = t;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <D extends DataSource> DataSourceBuilder<D> type(Class<D> type) {
        this.type = type;
        return this;
    }

    public DataSourceBuilder<T> url(String url) {
        set(DataSourceProperty.URL, url);
        return this;
    }

    public DataSourceBuilder<T> driverClassName(String driverClassName) {
        set(DataSourceProperty.DRIVER_CLASS_NAME, driverClassName);
        return this;
    }

    public DataSourceBuilder<T> username(String username) {
        set(DataSourceProperty.USERNAME, username);
        return this;
    }

    public DataSourceBuilder<T> password(String password) {
        set(DataSourceProperty.PASSWORD, password);
        return this;
    }

    private void set(DataSourceProperty property, String value) {
        this.values.put(property, value);
    }

    public T build() {
        DataSourceProperties forType = DataSourceProperties.forType(this.classLoader, this.type);
        DataSourceProperties<DataSource> deriveFromProperties = getDeriveFromProperties();
        T t = (T) BeanUtils.instantiateClass(this.type != null ? this.type : forType.getDataSourceInstanceType());
        HashSet applied = new HashSet();
        for (DataSourceProperty property : DataSourceProperty.values()) {
            String value = this.values.get(property);
            if (value == null && deriveFromProperties != null && forType.canSet(property)) {
                value = deriveFromProperties.get(this.deriveFrom, property);
            }
            if (value != null) {
                forType.set(t, property, value);
                applied.add(property);
            }
        }
        if (!applied.contains(DataSourceProperty.DRIVER_CLASS_NAME) && forType.canSet(DataSourceProperty.DRIVER_CLASS_NAME) && this.values.containsKey(DataSourceProperty.URL)) {
            String url = this.values.get(DataSourceProperty.URL);
            DatabaseDriver driver = DatabaseDriver.fromJdbcUrl(url);
            forType.set(t, DataSourceProperty.DRIVER_CLASS_NAME, driver.getDriverClassName());
        }
        return t;
    }

    private DataSourceProperties<DataSource> getDeriveFromProperties() {
        if (this.deriveFrom == null) {
            return null;
        }
        return DataSourceProperties.forType(this.classLoader, this.deriveFrom.getClass());
    }

    public static DataSourceBuilder<?> create() {
        return create(null);
    }

    public static DataSourceBuilder<?> create(ClassLoader classLoader) {
        return new DataSourceBuilder<>(classLoader);
    }

    public static DataSourceBuilder<?> derivedFrom(DataSource dataSource) {
        if (dataSource instanceof EmbeddedDatabase) {
            try {
                dataSource = (DataSource) dataSource.unwrap(DataSource.class);
            } catch (SQLException ex) {
                throw new IllegalStateException("Unable to unwrap embedded database", ex);
            }
        }
        return new DataSourceBuilder<>(unwrap(dataSource));
    }

    private static DataSource unwrap(DataSource dataSource) {
        while (dataSource.isWrapperFor(DataSource.class)) {
            try {
                DataSource unwrapped = (DataSource) dataSource.unwrap(DataSource.class);
                if (unwrapped == dataSource) {
                    return unwrapped;
                }
                dataSource = unwrapped;
            } catch (SQLException e) {
            }
        }
        return dataSource;
    }

    public static Class<? extends DataSource> findType(ClassLoader classLoader) {
        MappedDataSourceProperties<?> mappings = MappedDataSourceProperties.forType(classLoader, (Class) null);
        if (mappings != null) {
            return mappings.getDataSourceInstanceType();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/DataSourceBuilder$DataSourceProperty.class */
    public enum DataSourceProperty {
        URL(false, "url", "URL"),
        DRIVER_CLASS_NAME(true, "driverClassName"),
        USERNAME(false, "username", ClassicConstants.USER_MDC_KEY),
        PASSWORD(false, "password");

        private final boolean optional;
        private final String[] names;

        DataSourceProperty(boolean optional, String... names) {
            this.optional = optional;
            this.names = names;
        }

        boolean isOptional() {
            return this.optional;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.names[0];
        }

        Method findSetter(Class<?> type) {
            return findMethod("set", type, String.class);
        }

        Method findGetter(Class<?> type) {
            return findMethod(BeanUtil.PREFIX_GETTER_GET, type, new Class[0]);
        }

        private Method findMethod(String prefix, Class<?> type, Class<?>... paramTypes) {
            for (String name : this.names) {
                String candidate = prefix + StringUtils.capitalize(name);
                Method method = ReflectionUtils.findMethod(type, candidate, paramTypes);
                if (method != null) {
                    return method;
                }
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/DataSourceBuilder$DataSourceProperties.class */
    public interface DataSourceProperties<T extends DataSource> {
        Class<? extends T> getDataSourceInstanceType();

        boolean canSet(DataSourceProperty property);

        void set(T dataSource, DataSourceProperty property, String value);

        String get(T dataSource, DataSourceProperty property);

        static <T extends DataSource> DataSourceProperties<T> forType(ClassLoader classLoader, Class<T> type) {
            MappedDataSourceProperties<T> mapped = MappedDataSourceProperties.forType(classLoader, (Class) type);
            return mapped != null ? mapped : new ReflectionDataSourceProperties(type);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/DataSourceBuilder$MappedDataSourceProperties.class */
    public static class MappedDataSourceProperties<T extends DataSource> implements DataSourceProperties<T> {
        private final Map<DataSourceProperty, MappedDataSourceProperty<T, ?>> mappedProperties = new HashMap();
        private final Class<T> dataSourceType = (Class<T>) ResolvableType.forClass(MappedDataSourceProperties.class, getClass()).resolveGeneric(new int[0]);

        MappedDataSourceProperties() {
        }

        @Override // org.springframework.boot.jdbc.DataSourceBuilder.DataSourceProperties
        public Class<? extends T> getDataSourceInstanceType() {
            return this.dataSourceType;
        }

        /* JADX WARN: Multi-variable type inference failed */
        protected void add(DataSourceProperty property, Getter<T, String> getter, Setter<T, String> setter) {
            add(property, String.class, getter, setter);
        }

        protected <V> void add(DataSourceProperty property, Class<V> type, Getter<T, V> getter, Setter<T, V> setter) {
            this.mappedProperties.put(property, new MappedDataSourceProperty<>(property, type, getter, setter));
        }

        @Override // org.springframework.boot.jdbc.DataSourceBuilder.DataSourceProperties
        public boolean canSet(DataSourceProperty property) {
            return this.mappedProperties.containsKey(property);
        }

        @Override // org.springframework.boot.jdbc.DataSourceBuilder.DataSourceProperties
        public void set(T dataSource, DataSourceProperty property, String value) {
            MappedDataSourceProperty<T, ?> mappedProperty = getMapping(property);
            if (mappedProperty != null) {
                mappedProperty.set(dataSource, value);
            }
        }

        @Override // org.springframework.boot.jdbc.DataSourceBuilder.DataSourceProperties
        public String get(T dataSource, DataSourceProperty property) {
            MappedDataSourceProperty<T, ?> mappedProperty = getMapping(property);
            if (mappedProperty != null) {
                return mappedProperty.get(dataSource);
            }
            return null;
        }

        private MappedDataSourceProperty<T, ?> getMapping(DataSourceProperty property) {
            MappedDataSourceProperty<T, ?> mappedProperty = this.mappedProperties.get(property);
            UnsupportedDataSourcePropertyException.throwIf(!property.isOptional() && mappedProperty == null, () -> {
                return "No mapping found for " + property;
            });
            return mappedProperty;
        }

        static <T extends DataSource> MappedDataSourceProperties<T> forType(ClassLoader classLoader, Class<T> type) {
            MappedDataSourceProperties<T> pooled = lookupPooled(classLoader, type);
            if (type == null || pooled != null) {
                return pooled;
            }
            return lookupBasic(classLoader, type);
        }

        private static <T extends DataSource> MappedDataSourceProperties<T> lookupPooled(ClassLoader classLoader, Class<T> type) {
            MappedDataSourceProperties<T> result = lookup(classLoader, type, null, "com.zaxxer.hikari.HikariDataSource", HikariDataSourceProperties::new, new String[0]);
            return lookup(classLoader, type, lookup(classLoader, type, lookup(classLoader, type, lookup(classLoader, type, result, "org.apache.tomcat.jdbc.pool.DataSource", TomcatPoolDataSourceProperties::new, new String[0]), "org.apache.commons.dbcp2.BasicDataSource", MappedDbcp2DataSource::new, new String[0]), "oracle.ucp.jdbc.PoolDataSourceImpl", OraclePoolDataSourceProperties::new, "oracle.jdbc.OracleConnection"), "com.mchange.v2.c3p0.ComboPooledDataSource", ComboPooledDataSourceProperties::new, new String[0]);
        }

        private static <T extends DataSource> MappedDataSourceProperties<T> lookupBasic(ClassLoader classLoader, Class<T> dataSourceType) {
            MappedDataSourceProperties<T> result = lookup(classLoader, dataSourceType, null, "org.springframework.jdbc.datasource.SimpleDriverDataSource", SimpleDataSourceProperties::new, new String[0]);
            return lookup(classLoader, dataSourceType, lookup(classLoader, dataSourceType, lookup(classLoader, dataSourceType, result, "oracle.jdbc.datasource.OracleDataSource", OracleDataSourceProperties::new, new String[0]), "org.h2.jdbcx.JdbcDataSource", H2DataSourceProperties::new, new String[0]), "org.postgresql.ds.PGSimpleDataSource", PostgresDataSourceProperties::new, new String[0]);
        }

        private static <T extends DataSource> MappedDataSourceProperties<T> lookup(ClassLoader classLoader, Class<T> dataSourceType, MappedDataSourceProperties<T> existing, String dataSourceClassName, Supplier<MappedDataSourceProperties<?>> propertyMappingsSupplier, String... requiredClassNames) {
            if (existing != null || !allPresent(classLoader, dataSourceClassName, requiredClassNames)) {
                return existing;
            }
            MappedDataSourceProperties<T> mappedDataSourceProperties = (MappedDataSourceProperties) propertyMappingsSupplier.get();
            if (dataSourceType == null || mappedDataSourceProperties.getDataSourceInstanceType().isAssignableFrom(dataSourceType)) {
                return mappedDataSourceProperties;
            }
            return null;
        }

        private static boolean allPresent(ClassLoader classLoader, String dataSourceClassName, String[] requiredClassNames) {
            boolean result = ClassUtils.isPresent(dataSourceClassName, classLoader);
            for (String requiredClassName : requiredClassNames) {
                result = result && ClassUtils.isPresent(requiredClassName, classLoader);
            }
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/DataSourceBuilder$MappedDataSourceProperty.class */
    public static class MappedDataSourceProperty<T extends DataSource, V> {
        private final DataSourceProperty property;
        private final Class<V> type;
        private final Getter<T, V> getter;
        private final Setter<T, V> setter;

        MappedDataSourceProperty(DataSourceProperty property, Class<V> type, Getter<T, V> getter, Setter<T, V> setter) {
            this.property = property;
            this.type = type;
            this.getter = getter;
            this.setter = setter;
        }

        void set(T dataSource, String value) {
            try {
                if (this.setter == null) {
                    UnsupportedDataSourcePropertyException.throwIf(!this.property.isOptional(), () -> {
                        return "No setter mapped for '" + this.property + "' property";
                    });
                } else {
                    this.setter.set(dataSource, convertFromString(value));
                }
            } catch (SQLException ex) {
                throw new IllegalStateException(ex);
            }
        }

        String get(T dataSource) {
            try {
                if (this.getter == null) {
                    UnsupportedDataSourcePropertyException.throwIf(!this.property.isOptional(), () -> {
                        return "No getter mapped for '" + this.property + "' property";
                    });
                    return null;
                }
                return convertToString(this.getter.get(dataSource));
            } catch (SQLException ex) {
                throw new IllegalStateException(ex);
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        private V convertFromString(String str) {
            if (String.class.equals(this.type)) {
                return str;
            }
            if (Class.class.equals(this.type)) {
                return (V) ClassUtils.resolveClassName(str, null);
            }
            throw new IllegalStateException("Unsupported value type " + this.type);
        }

        /* JADX WARN: Multi-variable type inference failed */
        private String convertToString(V value) {
            if (String.class.equals(this.type)) {
                return (String) value;
            }
            if (Class.class.equals(this.type)) {
                return ((Class) value).getName();
            }
            throw new IllegalStateException("Unsupported value type " + this.type);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/DataSourceBuilder$ReflectionDataSourceProperties.class */
    public static class ReflectionDataSourceProperties<T extends DataSource> implements DataSourceProperties<T> {
        private final Map<DataSourceProperty, Method> getters;
        private final Map<DataSourceProperty, Method> setters;
        private final Class<T> dataSourceType;

        ReflectionDataSourceProperties(Class<T> dataSourceType) {
            Assert.state(dataSourceType != null, "No supported DataSource type found");
            Map<DataSourceProperty, Method> getters = new HashMap<>();
            Map<DataSourceProperty, Method> setters = new HashMap<>();
            for (DataSourceProperty property : DataSourceProperty.values()) {
                putIfNotNull(getters, property, property.findGetter(dataSourceType));
                putIfNotNull(setters, property, property.findSetter(dataSourceType));
            }
            this.dataSourceType = dataSourceType;
            this.getters = Collections.unmodifiableMap(getters);
            this.setters = Collections.unmodifiableMap(setters);
        }

        private void putIfNotNull(Map<DataSourceProperty, Method> map, DataSourceProperty property, Method method) {
            if (method != null) {
                map.put(property, method);
            }
        }

        @Override // org.springframework.boot.jdbc.DataSourceBuilder.DataSourceProperties
        public Class<T> getDataSourceInstanceType() {
            return this.dataSourceType;
        }

        @Override // org.springframework.boot.jdbc.DataSourceBuilder.DataSourceProperties
        public boolean canSet(DataSourceProperty property) {
            return this.setters.containsKey(property);
        }

        @Override // org.springframework.boot.jdbc.DataSourceBuilder.DataSourceProperties
        public void set(T dataSource, DataSourceProperty property, String value) {
            Method method = getMethod(property, this.setters);
            if (method != null) {
                ReflectionUtils.invokeMethod(method, dataSource, value);
            }
        }

        @Override // org.springframework.boot.jdbc.DataSourceBuilder.DataSourceProperties
        public String get(T dataSource, DataSourceProperty property) {
            Method method = getMethod(property, this.getters);
            if (method != null) {
                return (String) ReflectionUtils.invokeMethod(method, dataSource);
            }
            return null;
        }

        private Method getMethod(DataSourceProperty property, Map<DataSourceProperty, Method> methods) {
            Method method = methods.get(property);
            if (method == null) {
                UnsupportedDataSourcePropertyException.throwIf(!property.isOptional(), () -> {
                    return "Unable to find suitable method for " + property;
                });
                return null;
            }
            ReflectionUtils.makeAccessible(method);
            return method;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/DataSourceBuilder$HikariDataSourceProperties.class */
    public static class HikariDataSourceProperties extends MappedDataSourceProperties<HikariDataSource> {
        HikariDataSourceProperties() {
            add(DataSourceProperty.URL, (v0) -> {
                return v0.getJdbcUrl();
            }, (v0, v1) -> {
                v0.setJdbcUrl(v1);
            });
            add(DataSourceProperty.DRIVER_CLASS_NAME, (v0) -> {
                return v0.getDriverClassName();
            }, (v0, v1) -> {
                v0.setDriverClassName(v1);
            });
            add(DataSourceProperty.USERNAME, (v0) -> {
                return v0.getUsername();
            }, (v0, v1) -> {
                v0.setUsername(v1);
            });
            add(DataSourceProperty.PASSWORD, (v0) -> {
                return v0.getPassword();
            }, (v0, v1) -> {
                v0.setPassword(v1);
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/DataSourceBuilder$TomcatPoolDataSourceProperties.class */
    public static class TomcatPoolDataSourceProperties extends MappedDataSourceProperties<org.apache.tomcat.jdbc.pool.DataSource> {
        TomcatPoolDataSourceProperties() {
            add(DataSourceProperty.URL, (v0) -> {
                return v0.getUrl();
            }, (v0, v1) -> {
                v0.setUrl(v1);
            });
            add(DataSourceProperty.DRIVER_CLASS_NAME, (v0) -> {
                return v0.getDriverClassName();
            }, (v0, v1) -> {
                v0.setDriverClassName(v1);
            });
            add(DataSourceProperty.USERNAME, (v0) -> {
                return v0.getUsername();
            }, (v0, v1) -> {
                v0.setUsername(v1);
            });
            add(DataSourceProperty.PASSWORD, (v0) -> {
                return v0.getPassword();
            }, (v0, v1) -> {
                v0.setPassword(v1);
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/DataSourceBuilder$MappedDbcp2DataSource.class */
    public static class MappedDbcp2DataSource extends MappedDataSourceProperties<BasicDataSource> {
        MappedDbcp2DataSource() {
            add(DataSourceProperty.URL, (v0) -> {
                return v0.getUrl();
            }, (v0, v1) -> {
                v0.setUrl(v1);
            });
            add(DataSourceProperty.DRIVER_CLASS_NAME, (v0) -> {
                return v0.getDriverClassName();
            }, (v0, v1) -> {
                v0.setDriverClassName(v1);
            });
            add(DataSourceProperty.USERNAME, (v0) -> {
                return v0.getUsername();
            }, (v0, v1) -> {
                v0.setUsername(v1);
            });
            add(DataSourceProperty.PASSWORD, (v0) -> {
                return v0.getPassword();
            }, (v0, v1) -> {
                v0.setPassword(v1);
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/DataSourceBuilder$OraclePoolDataSourceProperties.class */
    public static class OraclePoolDataSourceProperties extends MappedDataSourceProperties<PoolDataSource> {
        @Override // org.springframework.boot.jdbc.DataSourceBuilder.MappedDataSourceProperties, org.springframework.boot.jdbc.DataSourceBuilder.DataSourceProperties
        public Class<? extends PoolDataSource> getDataSourceInstanceType() {
            return PoolDataSourceImpl.class;
        }

        OraclePoolDataSourceProperties() {
            add(DataSourceProperty.URL, (v0) -> {
                return v0.getURL();
            }, (v0, v1) -> {
                v0.setURL(v1);
            });
            add(DataSourceProperty.DRIVER_CLASS_NAME, (v0) -> {
                return v0.getConnectionFactoryClassName();
            }, (v0, v1) -> {
                v0.setConnectionFactoryClassName(v1);
            });
            add(DataSourceProperty.USERNAME, (v0) -> {
                return v0.getUser();
            }, (v0, v1) -> {
                v0.setUser(v1);
            });
            add(DataSourceProperty.PASSWORD, null, (v0, v1) -> {
                v0.setPassword(v1);
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/DataSourceBuilder$ComboPooledDataSourceProperties.class */
    public static class ComboPooledDataSourceProperties extends MappedDataSourceProperties<ComboPooledDataSource> {
        ComboPooledDataSourceProperties() {
            add(DataSourceProperty.URL, (v0) -> {
                return v0.getJdbcUrl();
            }, (v0, v1) -> {
                v0.setJdbcUrl(v1);
            });
            add(DataSourceProperty.DRIVER_CLASS_NAME, (v0) -> {
                return v0.getDriverClass();
            }, this::setDriverClass);
            add(DataSourceProperty.USERNAME, (v0) -> {
                return v0.getUser();
            }, (v0, v1) -> {
                v0.setUser(v1);
            });
            add(DataSourceProperty.PASSWORD, (v0) -> {
                return v0.getPassword();
            }, (v0, v1) -> {
                v0.setPassword(v1);
            });
        }

        private void setDriverClass(ComboPooledDataSource dataSource, String driverClass) {
            try {
                dataSource.setDriverClass(driverClass);
            } catch (PropertyVetoException ex) {
                throw new IllegalArgumentException((Throwable) ex);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/DataSourceBuilder$SimpleDataSourceProperties.class */
    public static class SimpleDataSourceProperties extends MappedDataSourceProperties<SimpleDriverDataSource> {
        SimpleDataSourceProperties() {
            add(DataSourceProperty.URL, (v0) -> {
                return v0.getUrl();
            }, (v0, v1) -> {
                v0.setUrl(v1);
            });
            add(DataSourceProperty.DRIVER_CLASS_NAME, Class.class, dataSource -> {
                return dataSource.getDriver().getClass();
            }, (v0, v1) -> {
                v0.setDriverClass(v1);
            });
            add(DataSourceProperty.USERNAME, (v0) -> {
                return v0.getUsername();
            }, (v0, v1) -> {
                v0.setUsername(v1);
            });
            add(DataSourceProperty.PASSWORD, (v0) -> {
                return v0.getPassword();
            }, (v0, v1) -> {
                v0.setPassword(v1);
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/DataSourceBuilder$OracleDataSourceProperties.class */
    public static class OracleDataSourceProperties extends MappedDataSourceProperties<OracleDataSource> {
        OracleDataSourceProperties() {
            add(DataSourceProperty.URL, (v0) -> {
                return v0.getURL();
            }, (v0, v1) -> {
                v0.setURL(v1);
            });
            add(DataSourceProperty.USERNAME, (v0) -> {
                return v0.getUser();
            }, (v0, v1) -> {
                v0.setUser(v1);
            });
            add(DataSourceProperty.PASSWORD, null, (v0, v1) -> {
                v0.setPassword(v1);
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/DataSourceBuilder$H2DataSourceProperties.class */
    public static class H2DataSourceProperties extends MappedDataSourceProperties<JdbcDataSource> {
        H2DataSourceProperties() {
            add(DataSourceProperty.URL, (v0) -> {
                return v0.getUrl();
            }, (v0, v1) -> {
                v0.setUrl(v1);
            });
            add(DataSourceProperty.USERNAME, (v0) -> {
                return v0.getUser();
            }, (v0, v1) -> {
                v0.setUser(v1);
            });
            add(DataSourceProperty.PASSWORD, (v0) -> {
                return v0.getPassword();
            }, (v0, v1) -> {
                v0.setPassword(v1);
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/DataSourceBuilder$PostgresDataSourceProperties.class */
    public static class PostgresDataSourceProperties extends MappedDataSourceProperties<PGSimpleDataSource> {
        PostgresDataSourceProperties() {
            add(DataSourceProperty.URL, (v0) -> {
                return v0.getUrl();
            }, (v0, v1) -> {
                v0.setUrl(v1);
            });
            add(DataSourceProperty.USERNAME, (v0) -> {
                return v0.getUser();
            }, (v0, v1) -> {
                v0.setUser(v1);
            });
            add(DataSourceProperty.PASSWORD, (v0) -> {
                return v0.getPassword();
            }, (v0, v1) -> {
                v0.setPassword(v1);
            });
        }
    }
}
