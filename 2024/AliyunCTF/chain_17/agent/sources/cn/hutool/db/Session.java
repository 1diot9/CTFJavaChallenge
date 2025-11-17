package cn.hutool.db;

import cn.hutool.core.lang.func.VoidFunc1;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.dialect.Dialect;
import cn.hutool.db.dialect.DialectFactory;
import cn.hutool.db.ds.DSFactory;
import cn.hutool.db.sql.Wrapper;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import javax.sql.DataSource;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/Session.class */
public class Session extends AbstractDb implements Closeable {
    private static final long serialVersionUID = 3421251905539056945L;
    private static final Log log = LogFactory.get();

    public static Session create() {
        return new Session(DSFactory.get());
    }

    public static Session create(String group) {
        return new Session(DSFactory.get(group));
    }

    public static Session create(DataSource ds) {
        return new Session(ds);
    }

    public Session(DataSource ds) {
        this(ds, DialectFactory.getDialect(ds));
    }

    public Session(DataSource ds, String driverClassName) {
        this(ds, DialectFactory.newDialect(driverClassName));
    }

    public Session(DataSource ds, Dialect dialect) {
        super(ds, dialect);
    }

    @Override // cn.hutool.db.AbstractDb
    public SqlConnRunner getRunner() {
        return this.runner;
    }

    public void beginTransaction() throws SQLException {
        Connection conn = getConnection();
        checkTransactionSupported(conn);
        conn.setAutoCommit(false);
    }

    public void commit() throws SQLException {
        try {
            getConnection().commit();
        } finally {
            try {
                getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                log.error(e);
            }
        }
    }

    public void rollback() throws SQLException {
        try {
            getConnection().rollback();
        } finally {
            try {
                getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                log.error(e);
            }
        }
    }

    public void quietRollback() {
        try {
            try {
                getConnection().rollback();
            } catch (Exception e) {
                log.error(e);
                try {
                    getConnection().setAutoCommit(true);
                } catch (SQLException e2) {
                    log.error(e2);
                }
            }
        } finally {
            try {
                getConnection().setAutoCommit(true);
            } catch (SQLException e3) {
                log.error(e3);
            }
        }
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        try {
            getConnection().rollback(savepoint);
        } finally {
            try {
                getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                log.error(e);
            }
        }
    }

    public void quietRollback(Savepoint savepoint) {
        try {
            try {
                getConnection().rollback(savepoint);
            } catch (Exception e) {
                log.error(e);
                try {
                    getConnection().setAutoCommit(true);
                } catch (SQLException e2) {
                    log.error(e2);
                }
            }
        } finally {
            try {
                getConnection().setAutoCommit(true);
            } catch (SQLException e3) {
                log.error(e3);
            }
        }
    }

    public Savepoint setSavepoint() throws SQLException {
        return getConnection().setSavepoint();
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        return getConnection().setSavepoint(name);
    }

    public void setTransactionIsolation(int level) throws SQLException {
        if (!getConnection().getMetaData().supportsTransactionIsolationLevel(level)) {
            throw new SQLException(StrUtil.format("Transaction isolation [{}] not support!", Integer.valueOf(level)));
        }
        getConnection().setTransactionIsolation(level);
    }

    public void tx(VoidFunc1<Session> func) throws SQLException {
        try {
            beginTransaction();
            func.call(this);
            commit();
        } catch (Throwable e) {
            quietRollback();
            if (!(e instanceof SQLException)) {
                throw new SQLException(e);
            }
        }
    }

    @Override // cn.hutool.db.AbstractDb
    public Session setWrapper(Character wrapperChar) {
        return (Session) super.setWrapper(wrapperChar);
    }

    @Override // cn.hutool.db.AbstractDb
    public Session setWrapper(Wrapper wrapper) {
        return (Session) super.setWrapper(wrapper);
    }

    @Override // cn.hutool.db.AbstractDb
    public Session disableWrapper() {
        return (Session) super.disableWrapper();
    }

    @Override // cn.hutool.db.AbstractDb
    public Connection getConnection() throws SQLException {
        return ThreadLocalConnection.INSTANCE.get(this.ds);
    }

    @Override // cn.hutool.db.AbstractDb
    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (false == conn.getAutoCommit()) {
                    return;
                }
            } catch (SQLException e) {
                log.error(e);
            }
        }
        ThreadLocalConnection.INSTANCE.close(this.ds);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        closeConnection(null);
    }
}
