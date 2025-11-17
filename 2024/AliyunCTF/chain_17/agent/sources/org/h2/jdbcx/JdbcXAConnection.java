package org.h2.jdbcx;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;
import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import org.apache.coyote.http11.Constants;
import org.apache.tomcat.jni.SSL;
import org.h2.api.ErrorCode;
import org.h2.jdbc.JdbcConnection;
import org.h2.message.DbException;
import org.h2.message.TraceObject;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbcx/JdbcXAConnection.class */
public final class JdbcXAConnection extends TraceObject implements XAConnection, XAResource {
    private final JdbcDataSourceFactory factory;
    private JdbcConnection physicalConn;
    private volatile Connection handleConn;
    private final ArrayList<ConnectionEventListener> listeners = Utils.newSmallArrayList();
    private Xid currentTransaction;
    private boolean prepared;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JdbcXAConnection(JdbcDataSourceFactory jdbcDataSourceFactory, int i, JdbcConnection jdbcConnection) {
        this.factory = jdbcDataSourceFactory;
        setTrace(jdbcDataSourceFactory.getTrace(), 13, i);
        this.physicalConn = jdbcConnection;
    }

    public XAResource getXAResource() {
        debugCodeCall("getXAResource");
        return this;
    }

    public void close() throws SQLException {
        debugCodeCall(Constants.CLOSE);
        Connection connection = this.handleConn;
        if (connection != null) {
            this.listeners.clear();
            connection.close();
        }
        if (this.physicalConn != null) {
            try {
                this.physicalConn.close();
            } finally {
                this.physicalConn = null;
            }
        }
    }

    public Connection getConnection() throws SQLException {
        debugCodeCall("getConnection");
        Connection connection = this.handleConn;
        if (connection != null) {
            connection.close();
        }
        this.physicalConn.rollback();
        this.handleConn = new PooledJdbcConnection(this.physicalConn);
        return this.handleConn;
    }

    public void addConnectionEventListener(ConnectionEventListener connectionEventListener) {
        debugCode("addConnectionEventListener(listener)");
        this.listeners.add(connectionEventListener);
    }

    public void removeConnectionEventListener(ConnectionEventListener connectionEventListener) {
        debugCode("removeConnectionEventListener(listener)");
        this.listeners.remove(connectionEventListener);
    }

    /* JADX WARN: Multi-variable type inference failed */
    void closedHandle() {
        debugCodeCall("closedHandle");
        ConnectionEvent connectionEvent = new ConnectionEvent(this);
        for (int size = this.listeners.size() - 1; size >= 0; size--) {
            this.listeners.get(size).connectionClosed(connectionEvent);
        }
        this.handleConn = null;
    }

    public int getTransactionTimeout() {
        debugCodeCall("getTransactionTimeout");
        return 0;
    }

    public boolean setTransactionTimeout(int i) {
        debugCodeCall("setTransactionTimeout", i);
        return false;
    }

    public boolean isSameRM(XAResource xAResource) {
        debugCode("isSameRM(xares)");
        return xAResource == this;
    }

    public Xid[] recover(int i) throws XAException {
        debugCodeCall("recover", quoteFlags(i));
        checkOpen();
        try {
            Statement createStatement = this.physicalConn.createStatement();
            Throwable th = null;
            try {
                try {
                    ResultSet executeQuery = createStatement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.IN_DOUBT ORDER BY TRANSACTION_NAME");
                    ArrayList newSmallArrayList = Utils.newSmallArrayList();
                    while (executeQuery.next()) {
                        newSmallArrayList.add(new JdbcXid(this.factory, getNextId(15), executeQuery.getString("TRANSACTION_NAME")));
                    }
                    executeQuery.close();
                    Xid[] xidArr = (Xid[]) newSmallArrayList.toArray(new Xid[0]);
                    if (!newSmallArrayList.isEmpty()) {
                        this.prepared = true;
                    }
                    if (createStatement != null) {
                        if (0 != 0) {
                            try {
                                createStatement.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            createStatement.close();
                        }
                    }
                    return xidArr;
                } finally {
                }
            } finally {
            }
        } catch (SQLException e) {
            XAException xAException = new XAException(-3);
            xAException.initCause(e);
            throw xAException;
        }
    }

    public int prepare(Xid xid) throws XAException {
        if (isDebugEnabled()) {
            debugCode("prepare(" + quoteXid(xid) + ')');
        }
        checkOpen();
        if (!this.currentTransaction.equals(xid)) {
            throw new XAException(-5);
        }
        try {
            Statement createStatement = this.physicalConn.createStatement();
            Throwable th = null;
            try {
                try {
                    createStatement.execute(JdbcXid.toString(new StringBuilder("PREPARE COMMIT \""), xid).append('\"').toString());
                    this.prepared = true;
                    if (createStatement != null) {
                        if (0 != 0) {
                            try {
                                createStatement.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            createStatement.close();
                        }
                    }
                    return 0;
                } finally {
                }
            } finally {
            }
        } catch (SQLException e) {
            throw convertException(e);
        }
    }

    public void forget(Xid xid) {
        if (isDebugEnabled()) {
            debugCode("forget(" + quoteXid(xid) + ')');
        }
        this.prepared = false;
    }

    public void rollback(Xid xid) throws XAException {
        if (isDebugEnabled()) {
            debugCode("rollback(" + quoteXid(xid) + ')');
        }
        try {
            if (this.prepared) {
                Statement createStatement = this.physicalConn.createStatement();
                Throwable th = null;
                try {
                    createStatement.execute(JdbcXid.toString(new StringBuilder("ROLLBACK TRANSACTION \""), xid).append('\"').toString());
                    if (createStatement != null) {
                        if (0 != 0) {
                            try {
                                createStatement.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            createStatement.close();
                        }
                    }
                    this.prepared = false;
                } finally {
                }
            } else {
                this.physicalConn.rollback();
            }
            this.physicalConn.setAutoCommit(true);
            this.currentTransaction = null;
        } catch (SQLException e) {
            throw convertException(e);
        }
    }

    public void end(Xid xid, int i) throws XAException {
        if (isDebugEnabled()) {
            debugCode("end(" + quoteXid(xid) + ", " + quoteFlags(i) + ')');
        }
        if (i == 33554432) {
            return;
        }
        if (!this.currentTransaction.equals(xid)) {
            throw new XAException(-9);
        }
        this.prepared = false;
    }

    public void start(Xid xid, int i) throws XAException {
        if (isDebugEnabled()) {
            debugCode("start(" + quoteXid(xid) + ", " + quoteFlags(i) + ')');
        }
        if (i == 134217728) {
            return;
        }
        if (i == 2097152) {
            if (this.currentTransaction != null && !this.currentTransaction.equals(xid)) {
                throw new XAException(-3);
            }
        } else if (this.currentTransaction != null) {
            throw new XAException(-4);
        }
        try {
            this.physicalConn.setAutoCommit(false);
            this.currentTransaction = xid;
            this.prepared = false;
        } catch (SQLException e) {
            throw convertException(e);
        }
    }

    public void commit(Xid xid, boolean z) throws XAException {
        if (isDebugEnabled()) {
            debugCode("commit(" + quoteXid(xid) + ", " + z + ')');
        }
        try {
            if (z) {
                this.physicalConn.commit();
            } else {
                Statement createStatement = this.physicalConn.createStatement();
                Throwable th = null;
                try {
                    try {
                        createStatement.execute(JdbcXid.toString(new StringBuilder("COMMIT TRANSACTION \""), xid).append('\"').toString());
                        this.prepared = false;
                        if (createStatement != null) {
                            if (0 != 0) {
                                try {
                                    createStatement.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                createStatement.close();
                            }
                        }
                    } finally {
                    }
                } finally {
                }
            }
            this.physicalConn.setAutoCommit(true);
            this.currentTransaction = null;
        } catch (SQLException e) {
            throw convertException(e);
        }
    }

    public void addStatementEventListener(StatementEventListener statementEventListener) {
        throw new UnsupportedOperationException();
    }

    public void removeStatementEventListener(StatementEventListener statementEventListener) {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        return getTraceObjectName() + ": " + this.physicalConn;
    }

    private static XAException convertException(SQLException sQLException) {
        XAException xAException = new XAException(sQLException.getMessage());
        xAException.initCause(sQLException);
        return xAException;
    }

    private static String quoteXid(Xid xid) {
        return JdbcXid.toString(new StringBuilder(), xid).toString().replace('-', '$');
    }

    private static String quoteFlags(int i) {
        StringBuilder sb = new StringBuilder();
        if ((i & SSL.SSL_OP_TLS_ROLLBACK_BUG) != 0) {
            sb.append("|XAResource.TMENDRSCAN");
        }
        if ((i & 536870912) != 0) {
            sb.append("|XAResource.TMFAIL");
        }
        if ((i & 2097152) != 0) {
            sb.append("|XAResource.TMJOIN");
        }
        if ((i & 1073741824) != 0) {
            sb.append("|XAResource.TMONEPHASE");
        }
        if ((i & SSL.SSL_OP_NO_TLSv1_2) != 0) {
            sb.append("|XAResource.TMRESUME");
        }
        if ((i & SSL.SSL_OP_NO_SSLv2) != 0) {
            sb.append("|XAResource.TMSTARTRSCAN");
        }
        if ((i & SSL.SSL_OP_NO_TLSv1) != 0) {
            sb.append("|XAResource.TMSUCCESS");
        }
        if ((i & SSL.SSL_OP_NO_SSLv3) != 0) {
            sb.append("|XAResource.TMSUSPEND");
        }
        if ((i & 3) != 0) {
            sb.append("|XAResource.XA_RDONLY");
        }
        if (sb.length() == 0) {
            sb.append("|XAResource.TMNOFLAGS");
        }
        return sb.substring(1);
    }

    private void checkOpen() throws XAException {
        if (this.physicalConn == null) {
            throw new XAException(-3);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbcx/JdbcXAConnection$PooledJdbcConnection.class */
    final class PooledJdbcConnection extends JdbcConnection {
        private boolean isClosed;

        public PooledJdbcConnection(JdbcConnection jdbcConnection) {
            super(jdbcConnection);
        }

        @Override // org.h2.jdbc.JdbcConnection, java.sql.Connection, java.lang.AutoCloseable
        public void close() throws SQLException {
            lock();
            try {
                if (!this.isClosed) {
                    try {
                        rollback();
                        setAutoCommit(true);
                    } catch (SQLException e) {
                    }
                    JdbcXAConnection.this.closedHandle();
                    this.isClosed = true;
                }
            } finally {
                unlock();
            }
        }

        @Override // org.h2.jdbc.JdbcConnection, java.sql.Connection
        public boolean isClosed() throws SQLException {
            boolean z;
            lock();
            try {
                if (!this.isClosed) {
                    if (!super.isClosed()) {
                        z = false;
                        return z;
                    }
                }
                z = true;
                return z;
            } finally {
                unlock();
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.jdbc.JdbcConnection
        public void checkClosed() {
            lock();
            try {
                if (this.isClosed) {
                    throw DbException.get(ErrorCode.OBJECT_CLOSED);
                }
                super.checkClosed();
            } finally {
                unlock();
            }
        }
    }
}
