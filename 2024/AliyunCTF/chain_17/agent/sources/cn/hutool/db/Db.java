package cn.hutool.db;

import cn.hutool.core.lang.func.VoidFunc1;
import cn.hutool.db.dialect.Dialect;
import cn.hutool.db.dialect.DialectFactory;
import cn.hutool.db.ds.DSFactory;
import cn.hutool.db.sql.Wrapper;
import cn.hutool.db.transaction.TransactionLevel;
import cn.hutool.log.StaticLog;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/Db.class */
public class Db extends AbstractDb {
    private static final long serialVersionUID = -3378415769645309514L;

    public static Db use() {
        return use(DSFactory.get());
    }

    public static Db use(String group) {
        return use(DSFactory.get(group));
    }

    public static Db use(DataSource ds) {
        if (ds == null) {
            return null;
        }
        return new Db(ds);
    }

    public static Db use(DataSource ds, Dialect dialect) {
        return new Db(ds, dialect);
    }

    public static Db use(DataSource ds, String driverClassName) {
        return new Db(ds, DialectFactory.newDialect(driverClassName));
    }

    public Db(DataSource ds) {
        this(ds, DialectFactory.getDialect(ds));
    }

    public Db(DataSource ds, String driverClassName) {
        this(ds, DialectFactory.newDialect(driverClassName));
    }

    public Db(DataSource ds, Dialect dialect) {
        super(ds, dialect);
    }

    @Override // cn.hutool.db.AbstractDb
    public Db setWrapper(Character wrapperChar) {
        return (Db) super.setWrapper(wrapperChar);
    }

    @Override // cn.hutool.db.AbstractDb
    public Db setWrapper(Wrapper wrapper) {
        return (Db) super.setWrapper(wrapper);
    }

    @Override // cn.hutool.db.AbstractDb
    public Db disableWrapper() {
        return (Db) super.disableWrapper();
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
            }
        }
        ThreadLocalConnection.INSTANCE.close(this.ds);
    }

    public Db tx(VoidFunc1<Db> func) throws SQLException {
        return tx(null, func);
    }

    public Db tx(TransactionLevel transactionLevel, VoidFunc1<Db> func) throws SQLException {
        int level;
        Connection conn = getConnection();
        checkTransactionSupported(conn);
        if (null != transactionLevel && conn.getTransactionIsolation() < (level = transactionLevel.getLevel())) {
            conn.setTransactionIsolation(level);
        }
        boolean autoCommit = conn.getAutoCommit();
        if (autoCommit) {
            conn.setAutoCommit(false);
        }
        try {
            try {
                func.call(this);
                conn.commit();
                quietSetAutoCommit(conn, Boolean.valueOf(autoCommit));
                closeConnection(conn);
                return this;
            } catch (Throwable e) {
                quietRollback(conn);
                if (e instanceof SQLException) {
                    throw ((SQLException) e);
                }
                throw new SQLException(e);
            }
        } catch (Throwable th) {
            quietSetAutoCommit(conn, Boolean.valueOf(autoCommit));
            closeConnection(conn);
            throw th;
        }
    }

    private void quietRollback(Connection conn) {
        if (null != conn) {
            try {
                conn.rollback();
            } catch (Exception e) {
                StaticLog.error(e);
            }
        }
    }

    private void quietSetAutoCommit(Connection conn, Boolean autoCommit) {
        if (null != conn && null != autoCommit) {
            try {
                conn.setAutoCommit(autoCommit.booleanValue());
            } catch (Exception e) {
                StaticLog.error(e);
            }
        }
    }
}
