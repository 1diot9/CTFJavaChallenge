package org.jooq.tools.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/jdbc/SingleConnectionDataSource.class */
public class SingleConnectionDataSource implements DataSource {
    private final Connection delegate;

    public SingleConnectionDataSource(Connection delegate) {
        this.delegate = delegate;
    }

    @Override // javax.sql.CommonDataSource
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getAnonymousLogger().getParent();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.sql.Wrapper
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (isWrapperFor(iface)) {
            return this;
        }
        throw new SQLException("DataSource does not implement " + String.valueOf(iface));
    }

    @Override // java.sql.Wrapper
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    @Override // javax.sql.DataSource
    public Connection getConnection() throws SQLException {
        return new DefaultConnection(this.delegate) { // from class: org.jooq.tools.jdbc.SingleConnectionDataSource.1
            @Override // org.jooq.tools.jdbc.DefaultConnection, java.sql.Connection, java.lang.AutoCloseable
            public void close() throws SQLException {
            }
        };
    }

    @Override // javax.sql.DataSource
    public Connection getConnection(String username, String password) throws SQLException {
        throw new SQLFeatureNotSupportedException("SingleConnectionDataSource cannot create new connections");
    }

    @Override // javax.sql.CommonDataSource
    public PrintWriter getLogWriter() throws SQLException {
        throw new SQLFeatureNotSupportedException("getLogWriter");
    }

    @Override // javax.sql.CommonDataSource
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new SQLFeatureNotSupportedException("setLogWriter");
    }

    @Override // javax.sql.CommonDataSource
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new SQLFeatureNotSupportedException("setLoginTimeout");
    }

    @Override // javax.sql.CommonDataSource
    public int getLoginTimeout() throws SQLException {
        throw new SQLFeatureNotSupportedException("getLoginTimeout");
    }
}
