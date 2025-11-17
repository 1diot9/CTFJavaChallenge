package cn.hutool.db.ds.pooled;

import ch.qos.logback.classic.ClassicConstants;
import cn.hutool.core.map.MapUtil;
import cn.hutool.db.DbUtil;
import cn.hutool.setting.dialect.Props;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/ds/pooled/PooledConnection.class */
public class PooledConnection extends ConnectionWraper {
    private final PooledDataSource ds;
    private boolean isClosed;

    public PooledConnection(PooledDataSource ds) throws SQLException {
        this.ds = ds;
        DbConfig config = ds.getConfig();
        Props info = new Props();
        String user = config.getUser();
        if (user != null) {
            info.setProperty(ClassicConstants.USER_MDC_KEY, user);
        }
        String password = config.getPass();
        if (password != null) {
            info.setProperty("password", password);
        }
        Properties connProps = config.getConnProps();
        if (MapUtil.isNotEmpty(connProps)) {
            info.putAll(connProps);
        }
        this.raw = DriverManager.getConnection(config.getUrl(), info);
    }

    public PooledConnection(PooledDataSource ds, Connection conn) {
        this.ds = ds;
        this.raw = conn;
    }

    @Override // java.sql.Connection, java.lang.AutoCloseable
    public void close() {
        this.ds.free(this);
        this.isClosed = true;
    }

    @Override // java.sql.Connection
    public boolean isClosed() throws SQLException {
        return this.isClosed || this.raw.isClosed();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PooledConnection open() {
        this.isClosed = false;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PooledConnection release() {
        DbUtil.close(this.raw);
        return this;
    }
}
