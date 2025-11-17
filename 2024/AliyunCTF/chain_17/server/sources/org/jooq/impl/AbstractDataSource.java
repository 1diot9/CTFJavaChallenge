package org.jooq.impl;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractDataSource.class */
abstract class AbstractDataSource implements DataSource {
    @Override // javax.sql.CommonDataSource
    public final PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException("getLogWriter");
    }

    @Override // javax.sql.CommonDataSource
    public final void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException("setLogWriter");
    }

    @Override // javax.sql.CommonDataSource
    public final void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException("setLoginTimeout");
    }

    @Override // javax.sql.CommonDataSource
    public final int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override // javax.sql.CommonDataSource
    public final Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger("global");
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.sql.Wrapper
    public final <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return this;
        }
        throw new SQLException("DataSource of type [" + getClass().getName() + "] cannot be unwrapped as [" + iface.getName() + "]");
    }

    @Override // java.sql.Wrapper
    public final boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }
}
