package org.h2.jdbcx;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbcx/JdbcConnectionPool.class */
public final class JdbcConnectionPool implements DataSource, ConnectionEventListener, JdbcConnectionPoolBackwardsCompat {
    private static final int DEFAULT_TIMEOUT = 30;
    private static final int DEFAULT_MAX_CONNECTIONS = 10;
    private final ConnectionPoolDataSource dataSource;
    private PrintWriter logWriter;
    private final Queue<PooledConnection> recycledConnections = new ConcurrentLinkedQueue();
    private volatile int maxConnections = 10;
    private volatile int timeout = 30;
    private AtomicInteger activeConnections = new AtomicInteger();
    private AtomicBoolean isDisposed = new AtomicBoolean();

    private JdbcConnectionPool(ConnectionPoolDataSource connectionPoolDataSource) {
        this.dataSource = connectionPoolDataSource;
        if (connectionPoolDataSource != null) {
            try {
                this.logWriter = connectionPoolDataSource.getLogWriter();
            } catch (SQLException e) {
            }
        }
    }

    public static JdbcConnectionPool create(ConnectionPoolDataSource connectionPoolDataSource) {
        return new JdbcConnectionPool(connectionPoolDataSource);
    }

    public static JdbcConnectionPool create(String str, String str2, String str3) {
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setURL(str);
        jdbcDataSource.setUser(str2);
        jdbcDataSource.setPassword(str3);
        return new JdbcConnectionPool(jdbcDataSource);
    }

    public void setMaxConnections(int i) {
        if (i < 1) {
            throw new IllegalArgumentException("Invalid maxConnections value: " + i);
        }
        this.maxConnections = i;
    }

    public int getMaxConnections() {
        return this.maxConnections;
    }

    @Override // javax.sql.CommonDataSource
    public int getLoginTimeout() {
        return this.timeout;
    }

    @Override // javax.sql.CommonDataSource
    public void setLoginTimeout(int i) {
        if (i == 0) {
            i = 30;
        }
        this.timeout = i;
    }

    public void dispose() {
        this.isDisposed.set(true);
        while (true) {
            PooledConnection poll = this.recycledConnections.poll();
            if (poll != null) {
                closeConnection(poll);
            } else {
                return;
            }
        }
    }

    @Override // javax.sql.DataSource
    public Connection getConnection() throws SQLException {
        long nanoTime = System.nanoTime() + (this.timeout * DateTimeUtils.NANOS_PER_SECOND);
        int i = 0;
        while (this.activeConnections.incrementAndGet() > this.maxConnections) {
            this.activeConnections.decrementAndGet();
            i--;
            if (i < 0) {
                try {
                    i = 3;
                    Thread.sleep(1L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            if (System.nanoTime() - nanoTime > 0) {
                throw new SQLException("Login timeout", "08001", 8001);
            }
        }
        try {
            return getConnectionNow();
        } catch (Throwable th) {
            this.activeConnections.decrementAndGet();
            throw th;
        }
    }

    @Override // javax.sql.DataSource
    public Connection getConnection(String str, String str2) {
        throw new UnsupportedOperationException();
    }

    private Connection getConnectionNow() throws SQLException {
        if (this.isDisposed.get()) {
            throw new IllegalStateException("Connection pool has been disposed.");
        }
        PooledConnection poll = this.recycledConnections.poll();
        if (poll == null) {
            poll = this.dataSource.getPooledConnection();
        }
        Connection connection = poll.getConnection();
        poll.addConnectionEventListener(this);
        return connection;
    }

    private void recycleConnection(PooledConnection pooledConnection) {
        int decrementAndGet = this.activeConnections.decrementAndGet();
        if (decrementAndGet < 0) {
            this.activeConnections.incrementAndGet();
            throw new AssertionError();
        }
        if (!this.isDisposed.get() && decrementAndGet < this.maxConnections) {
            this.recycledConnections.add(pooledConnection);
            if (this.isDisposed.get()) {
                dispose();
                return;
            }
            return;
        }
        closeConnection(pooledConnection);
    }

    private void closeConnection(PooledConnection pooledConnection) {
        try {
            pooledConnection.close();
        } catch (SQLException e) {
            if (this.logWriter != null) {
                e.printStackTrace(this.logWriter);
            }
        }
    }

    @Override // javax.sql.ConnectionEventListener
    public void connectionClosed(ConnectionEvent connectionEvent) {
        PooledConnection pooledConnection = (PooledConnection) connectionEvent.getSource();
        pooledConnection.removeConnectionEventListener(this);
        recycleConnection(pooledConnection);
    }

    @Override // javax.sql.ConnectionEventListener
    public void connectionErrorOccurred(ConnectionEvent connectionEvent) {
    }

    public int getActiveConnections() {
        return this.activeConnections.get();
    }

    @Override // javax.sql.CommonDataSource
    public PrintWriter getLogWriter() {
        return this.logWriter;
    }

    @Override // javax.sql.CommonDataSource
    public void setLogWriter(PrintWriter printWriter) {
        this.logWriter = printWriter;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.sql.Wrapper
    public <T> T unwrap(Class<T> cls) throws SQLException {
        try {
            if (isWrapperFor(cls)) {
                return this;
            }
            throw DbException.getInvalidValueException("iface", cls);
        } catch (Exception e) {
            throw DbException.toSQLException(e);
        }
    }

    @Override // java.sql.Wrapper
    public boolean isWrapperFor(Class<?> cls) throws SQLException {
        return cls != null && cls.isAssignableFrom(getClass());
    }

    @Override // javax.sql.CommonDataSource
    public Logger getParentLogger() {
        return null;
    }
}
