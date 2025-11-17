package org.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;
import org.h2.api.ErrorCode;
import org.h2.engine.Constants;
import org.h2.jdbc.JdbcConnection;
import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/Driver.class */
public class Driver implements java.sql.Driver, JdbcDriverBackwardsCompat {
    private static final String DEFAULT_URL = "jdbc:default:connection";
    private static boolean registered;
    private static final Driver INSTANCE = new Driver();
    private static final ThreadLocal<Connection> DEFAULT_CONNECTION = new ThreadLocal<>();

    static {
        load();
    }

    @Override // java.sql.Driver
    public Connection connect(String str, Properties properties) throws SQLException {
        if (str == null) {
            throw DbException.getJdbcSQLException(ErrorCode.URL_FORMAT_ERROR_2, null, Constants.URL_FORMAT, null);
        }
        if (str.startsWith(Constants.START_URL)) {
            return new JdbcConnection(str, properties, null, null, false);
        }
        if (str.equals("jdbc:default:connection")) {
            return DEFAULT_CONNECTION.get();
        }
        return null;
    }

    @Override // java.sql.Driver
    public boolean acceptsURL(String str) throws SQLException {
        if (str == null) {
            throw DbException.getJdbcSQLException(ErrorCode.URL_FORMAT_ERROR_2, null, Constants.URL_FORMAT, null);
        }
        if (str.startsWith(Constants.START_URL)) {
            return true;
        }
        return str.equals("jdbc:default:connection") && DEFAULT_CONNECTION.get() != null;
    }

    @Override // java.sql.Driver
    public int getMajorVersion() {
        return 2;
    }

    @Override // java.sql.Driver
    public int getMinorVersion() {
        return 2;
    }

    @Override // java.sql.Driver
    public DriverPropertyInfo[] getPropertyInfo(String str, Properties properties) {
        return new DriverPropertyInfo[0];
    }

    @Override // java.sql.Driver
    public boolean jdbcCompliant() {
        return true;
    }

    public Logger getParentLogger() {
        return null;
    }

    public static synchronized Driver load() {
        try {
            if (!registered) {
                registered = true;
                DriverManager.registerDriver(INSTANCE);
            }
        } catch (SQLException e) {
            DbException.traceThrowable(e);
        }
        return INSTANCE;
    }

    public static synchronized void unload() {
        try {
            if (registered) {
                registered = false;
                DriverManager.deregisterDriver(INSTANCE);
            }
        } catch (SQLException e) {
            DbException.traceThrowable(e);
        }
    }

    public static void setDefaultConnection(Connection connection) {
        if (connection == null) {
            DEFAULT_CONNECTION.remove();
        } else {
            DEFAULT_CONNECTION.set(connection);
        }
    }

    public static void setThreadContextClassLoader(Thread thread) {
        try {
            thread.setContextClassLoader(Driver.class.getClassLoader());
        } catch (Throwable th) {
        }
    }
}
