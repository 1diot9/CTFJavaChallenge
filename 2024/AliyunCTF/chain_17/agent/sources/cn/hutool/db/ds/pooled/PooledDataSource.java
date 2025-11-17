package cn.hutool.db.ds.pooled;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.ds.simple.AbstractDataSource;
import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/ds/pooled/PooledDataSource.class */
public class PooledDataSource extends AbstractDataSource {
    private Queue<PooledConnection> freePool;
    private int activeCount;
    private final DbConfig config;

    public static synchronized PooledDataSource getDataSource(String group) {
        return new PooledDataSource(group);
    }

    public static synchronized PooledDataSource getDataSource() {
        return new PooledDataSource();
    }

    public PooledDataSource() {
        this("");
    }

    public PooledDataSource(String group) {
        this(new DbSetting(), group);
    }

    public PooledDataSource(DbSetting setting, String group) {
        this(setting.getDbConfig(group));
    }

    public PooledDataSource(DbConfig config) {
        this.config = config;
        this.freePool = new LinkedList();
        int initialSize = config.getInitialSize();
        while (true) {
            try {
                int i = initialSize;
                initialSize--;
                if (i > 0) {
                    this.freePool.offer(newConnection());
                } else {
                    return;
                }
            } catch (SQLException e) {
                throw new DbRuntimeException(e);
            }
        }
    }

    @Override // javax.sql.DataSource
    public synchronized Connection getConnection() throws SQLException {
        return getConnection(this.config.getMaxWait());
    }

    @Override // javax.sql.DataSource
    public Connection getConnection(String username, String password) throws SQLException {
        throw new SQLException("Pooled DataSource is not allow to get special Connection!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public synchronized boolean free(PooledConnection conn) {
        this.activeCount--;
        return this.freePool.offer(conn);
    }

    public PooledConnection newConnection() throws SQLException {
        return new PooledConnection(this);
    }

    public DbConfig getConfig() {
        return this.config;
    }

    public PooledConnection getConnection(long wait) throws SQLException {
        try {
            return getConnectionDirect();
        } catch (Exception e) {
            ThreadUtil.sleep(wait);
            return getConnectionDirect();
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public synchronized void close() {
        if (CollectionUtil.isNotEmpty((Collection<?>) this.freePool)) {
            this.freePool.forEach((v0) -> {
                v0.release();
            });
            this.freePool.clear();
            this.freePool = null;
        }
    }

    protected void finalize() {
        IoUtil.close((Closeable) this);
    }

    private PooledConnection getConnectionDirect() throws SQLException {
        if (null == this.freePool) {
            throw new SQLException("PooledDataSource is closed!");
        }
        int maxActive = this.config.getMaxActive();
        if (maxActive <= 0 || maxActive < this.activeCount) {
            throw new SQLException("In used Connection is more than Max Active.");
        }
        PooledConnection conn = this.freePool.poll();
        if (null == conn || conn.open().isClosed()) {
            conn = newConnection();
        }
        this.activeCount++;
        return conn;
    }
}
