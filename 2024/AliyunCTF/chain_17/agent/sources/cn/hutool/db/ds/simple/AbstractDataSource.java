package cn.hutool.db.ds.simple;

import java.io.Closeable;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/ds/simple/AbstractDataSource.class */
public abstract class AbstractDataSource implements DataSource, Cloneable, Closeable {
    @Override // javax.sql.CommonDataSource
    public PrintWriter getLogWriter() {
        return DriverManager.getLogWriter();
    }

    @Override // javax.sql.CommonDataSource
    public void setLogWriter(PrintWriter out) {
        DriverManager.setLogWriter(out);
    }

    @Override // javax.sql.CommonDataSource
    public void setLoginTimeout(int seconds) {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override // javax.sql.CommonDataSource
    public int getLoginTimeout() {
        return DriverManager.getLoginTimeout();
    }

    @Override // java.sql.Wrapper
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException("Can't support unwrap method!");
    }

    @Override // java.sql.Wrapper
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new SQLException("Can't support isWrapperFor method!");
    }

    @Override // javax.sql.CommonDataSource
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("DataSource can't support getParentLogger method!");
    }
}
