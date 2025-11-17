package cn.hutool.db.ds;

import cn.hutool.core.clone.CloneRuntimeException;
import cn.hutool.core.io.IoUtil;
import java.io.Closeable;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/ds/DataSourceWrapper.class */
public class DataSourceWrapper implements DataSource, Closeable, Cloneable {
    private final DataSource ds;
    private final String driver;

    public static DataSourceWrapper wrap(DataSource ds, String driver) {
        return new DataSourceWrapper(ds, driver);
    }

    public DataSourceWrapper(DataSource ds, String driver) {
        this.ds = ds;
        this.driver = driver;
    }

    public String getDriver() {
        return this.driver;
    }

    public DataSource getRaw() {
        return this.ds;
    }

    @Override // javax.sql.CommonDataSource
    public PrintWriter getLogWriter() throws SQLException {
        return this.ds.getLogWriter();
    }

    @Override // javax.sql.CommonDataSource
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.ds.setLogWriter(out);
    }

    @Override // javax.sql.CommonDataSource
    public void setLoginTimeout(int seconds) throws SQLException {
        this.ds.setLoginTimeout(seconds);
    }

    @Override // javax.sql.CommonDataSource
    public int getLoginTimeout() throws SQLException {
        return this.ds.getLoginTimeout();
    }

    @Override // javax.sql.CommonDataSource
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return this.ds.getParentLogger();
    }

    @Override // java.sql.Wrapper
    public <T> T unwrap(Class<T> cls) throws SQLException {
        return (T) this.ds.unwrap(cls);
    }

    @Override // java.sql.Wrapper
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.ds.isWrapperFor(iface);
    }

    @Override // javax.sql.DataSource
    public Connection getConnection() throws SQLException {
        return this.ds.getConnection();
    }

    @Override // javax.sql.DataSource
    public Connection getConnection(String username, String password) throws SQLException {
        return this.ds.getConnection(username, password);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        if (this.ds instanceof AutoCloseable) {
            IoUtil.close((AutoCloseable) this.ds);
        }
    }

    /* renamed from: clone, reason: merged with bridge method [inline-methods] */
    public DataSourceWrapper m296clone() {
        try {
            return (DataSourceWrapper) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new CloneRuntimeException(e);
        }
    }
}
