package org.h2.util;

import ch.qos.logback.classic.ClassicConstants;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Hashtable;
import java.util.Properties;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import org.h2.Driver;
import org.h2.engine.Constants;
import org.h2.jdbcx.JdbcDataSource;
import org.osgi.framework.BundleContext;
import org.osgi.service.jdbc.DataSourceFactory;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/OsgiDataSourceFactory.class */
public class OsgiDataSourceFactory implements DataSourceFactory {
    private final Driver driver;

    public OsgiDataSourceFactory(Driver driver) {
        this.driver = driver;
    }

    public DataSource createDataSource(Properties properties) throws SQLException {
        Properties properties2 = new Properties();
        if (properties != null) {
            properties2.putAll(properties);
        }
        rejectUnsupportedOptions(properties2);
        rejectPoolingOptions(properties2);
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        setupH2DataSource(jdbcDataSource, properties2);
        return jdbcDataSource;
    }

    public ConnectionPoolDataSource createConnectionPoolDataSource(Properties properties) throws SQLException {
        Properties properties2 = new Properties();
        if (properties != null) {
            properties2.putAll(properties);
        }
        rejectUnsupportedOptions(properties2);
        rejectPoolingOptions(properties2);
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        setupH2DataSource(jdbcDataSource, properties2);
        return jdbcDataSource;
    }

    public XADataSource createXADataSource(Properties properties) throws SQLException {
        Properties properties2 = new Properties();
        if (properties != null) {
            properties2.putAll(properties);
        }
        rejectUnsupportedOptions(properties2);
        rejectPoolingOptions(properties2);
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        setupH2DataSource(jdbcDataSource, properties2);
        return jdbcDataSource;
    }

    public java.sql.Driver createDriver(Properties properties) throws SQLException {
        if (properties != null && !properties.isEmpty()) {
            throw new SQLException();
        }
        return this.driver;
    }

    private static void rejectUnsupportedOptions(Properties properties) throws SQLFeatureNotSupportedException {
        if (properties.containsKey("roleName")) {
            throw new SQLFeatureNotSupportedException("The roleName property is not supported by H2");
        }
        if (properties.containsKey("dataSourceName")) {
            throw new SQLFeatureNotSupportedException("The dataSourceName property is not supported by H2");
        }
    }

    private static void setupH2DataSource(JdbcDataSource jdbcDataSource, Properties properties) {
        if (properties.containsKey(ClassicConstants.USER_MDC_KEY)) {
            jdbcDataSource.setUser((String) properties.remove(ClassicConstants.USER_MDC_KEY));
        }
        if (properties.containsKey("password")) {
            jdbcDataSource.setPassword((String) properties.remove("password"));
        }
        if (properties.containsKey("description")) {
            jdbcDataSource.setDescription((String) properties.remove("description"));
        }
        StringBuilder sb = new StringBuilder();
        if (properties.containsKey("url")) {
            sb.append(properties.remove("url"));
            properties.remove("networkProtocol");
            properties.remove("serverName");
            properties.remove("portNumber");
            properties.remove("databaseName");
        } else {
            sb.append(Constants.START_URL);
            String str = "";
            if (properties.containsKey("networkProtocol")) {
                str = (String) properties.remove("networkProtocol");
                sb.append(str).append(":");
            }
            if (properties.containsKey("serverName")) {
                sb.append("//").append(properties.remove("serverName"));
                if (properties.containsKey("portNumber")) {
                    sb.append(":").append(properties.remove("portNumber"));
                }
                sb.append("/");
            } else if (properties.containsKey("portNumber")) {
                sb.append("//localhost:").append(properties.remove("portNumber")).append("/");
            } else if (str.equals("tcp") || str.equals("ssl")) {
                sb.append("//localhost/");
            }
            if (properties.containsKey("databaseName")) {
                sb.append(properties.remove("databaseName"));
            }
        }
        for (Object obj : properties.keySet()) {
            sb.append(";").append(obj).append("=").append(properties.get(obj));
        }
        if (sb.length() > Constants.START_URL.length()) {
            jdbcDataSource.setURL(sb.toString());
        }
    }

    private static void rejectPoolingOptions(Properties properties) throws SQLFeatureNotSupportedException {
        if (properties.containsKey("initialPoolSize") || properties.containsKey("maxIdleTime") || properties.containsKey("maxPoolSize") || properties.containsKey("maxStatements") || properties.containsKey("minPoolSize") || properties.containsKey("propertyCycle")) {
            throw new SQLFeatureNotSupportedException("Pooling properties are not supported by H2");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void registerService(BundleContext bundleContext, Driver driver) {
        Hashtable hashtable = new Hashtable();
        hashtable.put("osgi.jdbc.driver.class", Driver.class.getName());
        hashtable.put("osgi.jdbc.driver.name", "H2 JDBC Driver");
        hashtable.put("osgi.jdbc.driver.version", Constants.FULL_VERSION);
        hashtable.put("osgi.jdbc.datasourcefactory.capability", new String[]{"driver", "datasource", "connectionpooldatasource", "xadatasource"});
        bundleContext.registerService(DataSourceFactory.class.getName(), new OsgiDataSourceFactory(driver), hashtable);
    }
}
