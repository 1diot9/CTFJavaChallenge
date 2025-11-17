package org.h2.jdbc;

import ch.qos.logback.core.rolling.helper.DateTokenConverter;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.ClientInfoStatus;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import org.h2.api.ErrorCode;
import org.h2.api.JavaObjectSerializer;
import org.h2.command.CommandInterface;
import org.h2.engine.CastDataProvider;
import org.h2.engine.ConnectionInfo;
import org.h2.engine.Constants;
import org.h2.engine.IsolationLevel;
import org.h2.engine.Mode;
import org.h2.engine.Session;
import org.h2.engine.SessionRemote;
import org.h2.engine.SysProperties;
import org.h2.jdbc.JdbcLob;
import org.h2.message.DbException;
import org.h2.message.TraceObject;
import org.h2.result.ResultInterface;
import org.h2.server.pg.PgServer;
import org.h2.util.CloseWatcher;
import org.h2.util.TimeZoneProvider;
import org.h2.value.CompareMode;
import org.h2.value.Value;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueTimestampTimeZone;
import org.h2.value.ValueToObjectConverter;
import org.h2.value.ValueVarbinary;
import org.h2.value.ValueVarchar;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/JdbcConnection.class */
public class JdbcConnection extends TraceObject implements Connection, JdbcConnectionBackwardsCompat, CastDataProvider {
    private static final String NUM_SERVERS = "numServers";
    private static final String PREFIX_SERVER = "server";
    private static boolean keepOpenStackTrace;
    private final String url;
    private final String user;
    private Session session;
    private CommandInterface commit;
    private CommandInterface rollback;
    private CommandInterface getReadOnly;
    private CommandInterface getGeneratedKeys;
    private CommandInterface setQueryTimeout;
    private CommandInterface getQueryTimeout;
    private int savepointId;
    private String catalog;
    private Statement executingStatement;
    private final CloseWatcher watcher;
    private Map<String, String> clientInfo;
    private final ReentrantLock lock = new ReentrantLock();
    private int holdability = 1;
    private int queryTimeoutCache = -1;

    public JdbcConnection(String str, Properties properties, String str2, Object obj, boolean z) throws SQLException {
        try {
            ConnectionInfo connectionInfo = new ConnectionInfo(str, properties, str2, obj);
            if (z) {
                connectionInfo.setProperty("FORBID_CREATION", Constants.CLUSTERING_ENABLED);
            }
            String baseDir = SysProperties.getBaseDir();
            if (baseDir != null) {
                connectionInfo.setBaseDir(baseDir);
            }
            this.session = new SessionRemote(connectionInfo).connectEmbeddedOrServer(false);
            setTrace(this.session.getTrace(), 1, getNextId(1));
            this.user = connectionInfo.getUserName();
            if (isInfoEnabled()) {
                this.trace.infoCode("Connection " + getTraceObjectName() + " = DriverManager.getConnection(" + quote(connectionInfo.getOriginalURL()) + ", " + quote(this.user) + ", \"\");");
            }
            this.url = connectionInfo.getURL();
            closeOld();
            this.watcher = CloseWatcher.register(this, this.session, keepOpenStackTrace);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public JdbcConnection(JdbcConnection jdbcConnection) {
        this.session = jdbcConnection.session;
        setTrace(this.session.getTrace(), 1, getNextId(1));
        this.user = jdbcConnection.user;
        this.url = jdbcConnection.url;
        this.catalog = jdbcConnection.catalog;
        this.commit = jdbcConnection.commit;
        this.getGeneratedKeys = jdbcConnection.getGeneratedKeys;
        this.getQueryTimeout = jdbcConnection.getQueryTimeout;
        this.getReadOnly = jdbcConnection.getReadOnly;
        this.rollback = jdbcConnection.rollback;
        this.watcher = null;
        if (jdbcConnection.clientInfo != null) {
            this.clientInfo = new HashMap(jdbcConnection.clientInfo);
        }
    }

    public JdbcConnection(Session session, String str, String str2) {
        this.session = session;
        setTrace(session.getTrace(), 1, getNextId(1));
        this.user = str;
        this.url = str2;
        this.watcher = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void lock() {
        this.lock.lock();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void unlock() {
        this.lock.unlock();
    }

    private void closeOld() {
        while (true) {
            CloseWatcher pollUnclosed = CloseWatcher.pollUnclosed();
            if (pollUnclosed != null) {
                try {
                    pollUnclosed.getCloseable().close();
                } catch (Exception e) {
                    this.trace.error(e, "closing session");
                }
                keepOpenStackTrace = true;
                String openStackTrace = pollUnclosed.getOpenStackTrace();
                this.trace.error(DbException.get(ErrorCode.TRACE_CONNECTION_NOT_CLOSED), openStackTrace);
            } else {
                return;
            }
        }
    }

    @Override // java.sql.Connection
    public Statement createStatement() throws SQLException {
        try {
            int nextId = getNextId(8);
            debugCodeAssign("Statement", 8, nextId, "createStatement()");
            checkClosed();
            return new JdbcStatement(this, nextId, 1003, 1007);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public Statement createStatement(int i, int i2) throws SQLException {
        try {
            int nextId = getNextId(8);
            if (isDebugEnabled()) {
                debugCodeAssign("Statement", 8, nextId, "createStatement(" + i + ", " + i2 + ')');
            }
            checkTypeConcurrency(i, i2);
            checkClosed();
            return new JdbcStatement(this, nextId, i, i2);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public Statement createStatement(int i, int i2, int i3) throws SQLException {
        try {
            int nextId = getNextId(8);
            if (isDebugEnabled()) {
                debugCodeAssign("Statement", 8, nextId, "createStatement(" + i + ", " + i2 + ", " + i3 + ')');
            }
            checkTypeConcurrency(i, i2);
            checkHoldability(i3);
            checkClosed();
            return new JdbcStatement(this, nextId, i, i2);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public PreparedStatement prepareStatement(String str) throws SQLException {
        try {
            int nextId = getNextId(3);
            if (isDebugEnabled()) {
                debugCodeAssign("PreparedStatement", 3, nextId, "prepareStatement(" + quote(str) + ')');
            }
            checkClosed();
            return new JdbcPreparedStatement(this, translateSQL(str), nextId, 1003, 1007, null);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public DatabaseMetaData getMetaData() throws SQLException {
        try {
            int nextId = getNextId(2);
            debugCodeAssign("DatabaseMetaData", 2, nextId, "getMetaData()");
            checkClosed();
            return new JdbcDatabaseMetaData(this, this.trace, nextId);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public Session getSession() {
        return this.session;
    }

    /* JADX WARN: Finally extract failed */
    @Override // java.sql.Connection, java.lang.AutoCloseable
    public void close() throws SQLException {
        lock();
        try {
            try {
                debugCodeCall(org.apache.coyote.http11.Constants.CLOSE);
                Session session = this.session;
                if (session == null) {
                    return;
                }
                CloseWatcher.unregister(this.watcher);
                session.cancel();
                session.lock();
                try {
                    if (this.executingStatement != null) {
                        try {
                            this.executingStatement.cancel();
                        } catch (NullPointerException | SQLException e) {
                        }
                    }
                    try {
                        if (!session.isClosed()) {
                            try {
                                if (session.hasPendingTransaction()) {
                                    try {
                                        rollbackInternal();
                                    } catch (DbException e2) {
                                        if (e2.getErrorCode() != 90067 && e2.getErrorCode() != 90098) {
                                            throw e2;
                                        }
                                    }
                                }
                                closePreparedCommands();
                                session.close();
                            } catch (Throwable th) {
                                session.close();
                                throw th;
                            }
                        }
                        this.session = null;
                        session.unlock();
                        unlock();
                    } catch (Throwable th2) {
                        this.session = null;
                        throw th2;
                    }
                } catch (Throwable th3) {
                    session.unlock();
                    throw th3;
                }
            } catch (Throwable th4) {
                throw logAndConvert(th4);
            }
        } finally {
            unlock();
        }
    }

    private void closePreparedCommands() {
        this.commit = closeAndSetNull(this.commit);
        this.rollback = closeAndSetNull(this.rollback);
        this.getReadOnly = closeAndSetNull(this.getReadOnly);
        this.getGeneratedKeys = closeAndSetNull(this.getGeneratedKeys);
        this.getQueryTimeout = closeAndSetNull(this.getQueryTimeout);
        this.setQueryTimeout = closeAndSetNull(this.setQueryTimeout);
    }

    private static CommandInterface closeAndSetNull(CommandInterface commandInterface) {
        if (commandInterface != null) {
            commandInterface.close();
            return null;
        }
        return null;
    }

    @Override // java.sql.Connection
    public void setAutoCommit(boolean z) throws SQLException {
        lock();
        try {
            try {
                if (isDebugEnabled()) {
                    debugCode("setAutoCommit(" + z + ')');
                }
                checkClosed();
                Session session = this.session;
                session.lock();
                if (z) {
                    try {
                        if (!session.getAutoCommit()) {
                            commit();
                        }
                    } catch (Throwable th) {
                        session.unlock();
                        throw th;
                    }
                }
                session.setAutoCommit(z);
                session.unlock();
            } catch (Exception e) {
                throw logAndConvert(e);
            }
        } finally {
            unlock();
        }
    }

    @Override // java.sql.Connection
    public boolean getAutoCommit() throws SQLException {
        lock();
        try {
            try {
                checkClosed();
                debugCodeCall("getAutoCommit");
                boolean autoCommit = this.session.getAutoCommit();
                unlock();
                return autoCommit;
            } catch (Exception e) {
                throw logAndConvert(e);
            }
        } catch (Throwable th) {
            unlock();
            throw th;
        }
    }

    @Override // java.sql.Connection
    public void commit() throws SQLException {
        lock();
        try {
            try {
                debugCodeCall("commit");
                checkClosed();
                if (SysProperties.FORCE_AUTOCOMMIT_OFF_ON_COMMIT && getAutoCommit()) {
                    throw DbException.get(ErrorCode.METHOD_DISABLED_ON_AUTOCOMMIT_TRUE, "commit()");
                }
                this.commit = prepareCommand("COMMIT", this.commit);
                this.commit.executeUpdate(null);
                unlock();
            } catch (Exception e) {
                throw logAndConvert(e);
            }
        } catch (Throwable th) {
            unlock();
            throw th;
        }
    }

    @Override // java.sql.Connection
    public void rollback() throws SQLException {
        lock();
        try {
            try {
                debugCodeCall("rollback");
                checkClosed();
                if (SysProperties.FORCE_AUTOCOMMIT_OFF_ON_COMMIT && getAutoCommit()) {
                    throw DbException.get(ErrorCode.METHOD_DISABLED_ON_AUTOCOMMIT_TRUE, "rollback()");
                }
                rollbackInternal();
                unlock();
            } catch (Exception e) {
                throw logAndConvert(e);
            }
        } catch (Throwable th) {
            unlock();
            throw th;
        }
    }

    @Override // java.sql.Connection
    public boolean isClosed() throws SQLException {
        try {
            debugCodeCall("isClosed");
            if (this.session != null) {
                if (!this.session.isClosed()) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public String nativeSQL(String str) throws SQLException {
        try {
            debugCodeCall("nativeSQL", str);
            checkClosed();
            return translateSQL(str);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public void setReadOnly(boolean z) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setReadOnly(" + z + ')');
            }
            checkClosed();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public boolean isReadOnly() throws SQLException {
        try {
            debugCodeCall("isReadOnly");
            checkClosed();
            this.getReadOnly = prepareCommand("CALL READONLY()", this.getReadOnly);
            ResultInterface executeQuery = this.getReadOnly.executeQuery(0L, false);
            executeQuery.next();
            return executeQuery.currentRow()[0].getBoolean();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public void setCatalog(String str) throws SQLException {
        try {
            debugCodeCall("setCatalog", str);
            checkClosed();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public String getCatalog() throws SQLException {
        try {
            debugCodeCall("getCatalog");
            checkClosed();
            if (this.catalog == null) {
                CommandInterface prepareCommand = prepareCommand("CALL DATABASE()", Integer.MAX_VALUE);
                ResultInterface executeQuery = prepareCommand.executeQuery(0L, false);
                executeQuery.next();
                this.catalog = executeQuery.currentRow()[0].getString();
                prepareCommand.close();
            }
            return this.catalog;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public SQLWarning getWarnings() throws SQLException {
        try {
            debugCodeCall("getWarnings");
            checkClosed();
            return null;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public void clearWarnings() throws SQLException {
        try {
            debugCodeCall("clearWarnings");
            checkClosed();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public PreparedStatement prepareStatement(String str, int i, int i2) throws SQLException {
        try {
            int nextId = getNextId(3);
            if (isDebugEnabled()) {
                debugCodeAssign("PreparedStatement", 3, nextId, "prepareStatement(" + quote(str) + ", " + i + ", " + i2 + ')');
            }
            checkTypeConcurrency(i, i2);
            checkClosed();
            return new JdbcPreparedStatement(this, translateSQL(str), nextId, i, i2, null);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public void setTransactionIsolation(int i) throws SQLException {
        try {
            debugCodeCall("setTransactionIsolation", i);
            checkClosed();
            if (!getAutoCommit()) {
                commit();
            }
            this.session.setIsolationLevel(IsolationLevel.fromJdbc(i));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setQueryTimeout(int i) throws SQLException {
        try {
            debugCodeCall("setQueryTimeout", i);
            checkClosed();
            this.setQueryTimeout = prepareCommand("SET QUERY_TIMEOUT ?", this.setQueryTimeout);
            this.setQueryTimeout.getParameters().get(0).setValue(ValueInteger.get(i * 1000), false);
            this.setQueryTimeout.executeUpdate(null);
            this.queryTimeoutCache = i;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getQueryTimeout() throws SQLException {
        try {
            if (this.queryTimeoutCache == -1) {
                checkClosed();
                this.getQueryTimeout = prepareCommand(!this.session.isOldInformationSchema() ? "SELECT SETTING_VALUE FROM INFORMATION_SCHEMA.SETTINGS WHERE SETTING_NAME=?" : "SELECT `VALUE` FROM INFORMATION_SCHEMA.SETTINGS WHERE NAME=?", this.getQueryTimeout);
                this.getQueryTimeout.getParameters().get(0).setValue(ValueVarchar.get("QUERY_TIMEOUT"), false);
                ResultInterface executeQuery = this.getQueryTimeout.executeQuery(0L, false);
                executeQuery.next();
                int i = executeQuery.currentRow()[0].getInt();
                executeQuery.close();
                if (i != 0) {
                    i = (i + 999) / 1000;
                }
                this.queryTimeoutCache = i;
            }
            return this.queryTimeoutCache;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public int getTransactionIsolation() throws SQLException {
        try {
            debugCodeCall("getTransactionIsolation");
            checkClosed();
            return this.session.getIsolationLevel().getJdbc();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public void setHoldability(int i) throws SQLException {
        try {
            debugCodeCall("setHoldability", i);
            checkClosed();
            checkHoldability(i);
            this.holdability = i;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public int getHoldability() throws SQLException {
        try {
            debugCodeCall("getHoldability");
            checkClosed();
            return this.holdability;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        try {
            debugCodeCall("getTypeMap");
            checkClosed();
            return null;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setTypeMap(" + quoteMap(map) + ')');
            }
            checkMap(map);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public CallableStatement prepareCall(String str) throws SQLException {
        try {
            int nextId = getNextId(0);
            if (isDebugEnabled()) {
                debugCodeAssign("CallableStatement", 0, nextId, "prepareCall(" + quote(str) + ')');
            }
            checkClosed();
            return new JdbcCallableStatement(this, translateSQL(str), nextId, 1003, 1007);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public CallableStatement prepareCall(String str, int i, int i2) throws SQLException {
        try {
            int nextId = getNextId(0);
            if (isDebugEnabled()) {
                debugCodeAssign("CallableStatement", 0, nextId, "prepareCall(" + quote(str) + ", " + i + ", " + i2 + ')');
            }
            checkTypeConcurrency(i, i2);
            checkClosed();
            return new JdbcCallableStatement(this, translateSQL(str), nextId, i, i2);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public CallableStatement prepareCall(String str, int i, int i2, int i3) throws SQLException {
        try {
            int nextId = getNextId(0);
            if (isDebugEnabled()) {
                debugCodeAssign("CallableStatement", 0, nextId, "prepareCall(" + quote(str) + ", " + i + ", " + i2 + ", " + i3 + ')');
            }
            checkTypeConcurrency(i, i2);
            checkHoldability(i3);
            checkClosed();
            return new JdbcCallableStatement(this, translateSQL(str), nextId, i, i2);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public Savepoint setSavepoint() throws SQLException {
        try {
            int nextId = getNextId(6);
            debugCodeAssign("Savepoint", 6, nextId, "setSavepoint()");
            checkClosed();
            prepareCommand("SAVEPOINT " + JdbcSavepoint.getName(null, this.savepointId), Integer.MAX_VALUE).executeUpdate(null);
            JdbcSavepoint jdbcSavepoint = new JdbcSavepoint(this, this.savepointId, null, this.trace, nextId);
            this.savepointId++;
            return jdbcSavepoint;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public Savepoint setSavepoint(String str) throws SQLException {
        try {
            int nextId = getNextId(6);
            if (isDebugEnabled()) {
                debugCodeAssign("Savepoint", 6, nextId, "setSavepoint(" + quote(str) + ')');
            }
            checkClosed();
            prepareCommand("SAVEPOINT " + JdbcSavepoint.getName(str, 0), Integer.MAX_VALUE).executeUpdate(null);
            return new JdbcSavepoint(this, 0, str, this.trace, nextId);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public void rollback(Savepoint savepoint) throws SQLException {
        try {
            JdbcSavepoint convertSavepoint = convertSavepoint(savepoint);
            if (isDebugEnabled()) {
                debugCode("rollback(" + convertSavepoint.getTraceObjectName() + ')');
            }
            checkClosed();
            convertSavepoint.rollback();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        try {
            debugCode("releaseSavepoint(savepoint)");
            checkClosed();
            convertSavepoint(savepoint).release();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private static JdbcSavepoint convertSavepoint(Savepoint savepoint) {
        if (!(savepoint instanceof JdbcSavepoint)) {
            throw DbException.get(ErrorCode.SAVEPOINT_IS_INVALID_1, String.valueOf(savepoint));
        }
        return (JdbcSavepoint) savepoint;
    }

    @Override // java.sql.Connection
    public PreparedStatement prepareStatement(String str, int i, int i2, int i3) throws SQLException {
        try {
            int nextId = getNextId(3);
            if (isDebugEnabled()) {
                debugCodeAssign("PreparedStatement", 3, nextId, "prepareStatement(" + quote(str) + ", " + i + ", " + i2 + ", " + i3 + ')');
            }
            checkTypeConcurrency(i, i2);
            checkHoldability(i3);
            checkClosed();
            return new JdbcPreparedStatement(this, translateSQL(str), nextId, i, i2, null);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public PreparedStatement prepareStatement(String str, int i) throws SQLException {
        try {
            int nextId = getNextId(3);
            if (isDebugEnabled()) {
                debugCodeAssign("PreparedStatement", 3, nextId, "prepareStatement(" + quote(str) + ", " + i + ')');
            }
            checkClosed();
            return new JdbcPreparedStatement(this, translateSQL(str), nextId, 1003, 1007, Boolean.valueOf(i == 1));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public PreparedStatement prepareStatement(String str, int[] iArr) throws SQLException {
        try {
            int nextId = getNextId(3);
            if (isDebugEnabled()) {
                debugCodeAssign("PreparedStatement", 3, nextId, "prepareStatement(" + quote(str) + ", " + quoteIntArray(iArr) + ')');
            }
            checkClosed();
            return new JdbcPreparedStatement(this, translateSQL(str), nextId, 1003, 1007, iArr);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public PreparedStatement prepareStatement(String str, String[] strArr) throws SQLException {
        try {
            int nextId = getNextId(3);
            if (isDebugEnabled()) {
                debugCodeAssign("PreparedStatement", 3, nextId, "prepareStatement(" + quote(str) + ", " + quoteArray(strArr) + ')');
            }
            checkClosed();
            return new JdbcPreparedStatement(this, translateSQL(str), nextId, 1003, 1007, strArr);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CommandInterface prepareCommand(String str, int i) {
        return this.session.prepareCommand(str, i);
    }

    private CommandInterface prepareCommand(String str, CommandInterface commandInterface) {
        return commandInterface == null ? this.session.prepareCommand(str, Integer.MAX_VALUE) : commandInterface;
    }

    private static int translateGetEnd(String str, int i, char c) {
        char charAt;
        char charAt2;
        int length = str.length();
        switch (c) {
            case '\"':
                int indexOf = str.indexOf(34, i + 1);
                if (indexOf < 0) {
                    throw DbException.getSyntaxError(str, i);
                }
                return indexOf;
            case '#':
            case '%':
            case '&':
            case '(':
            case ')':
            case '*':
            case '+':
            case ',':
            case '.':
            default:
                throw DbException.getInternalError("c=" + c);
            case '$':
                if (i < length - 1 && str.charAt(i + 1) == '$' && (i == 0 || str.charAt(i - 1) <= ' ')) {
                    int indexOf2 = str.indexOf(ClassUtils.CGLIB_CLASS_SEPARATOR, i + 2);
                    if (indexOf2 < 0) {
                        throw DbException.getSyntaxError(str, i);
                    }
                    return indexOf2 + 1;
                }
                return i;
            case '\'':
                int indexOf3 = str.indexOf(39, i + 1);
                if (indexOf3 < 0) {
                    throw DbException.getSyntaxError(str, i);
                }
                return indexOf3;
            case '-':
                checkRunOver(i + 1, length, str);
                if (str.charAt(i + 1) == '-') {
                    i += 2;
                    while (i < length && (charAt = str.charAt(i)) != '\r' && charAt != '\n') {
                        i++;
                    }
                }
                return i;
            case '/':
                checkRunOver(i + 1, length, str);
                if (str.charAt(i + 1) == '*') {
                    int indexOf4 = str.indexOf("*/", i + 2);
                    if (indexOf4 < 0) {
                        throw DbException.getSyntaxError(str, i);
                    }
                    i = indexOf4 + 1;
                } else if (str.charAt(i + 1) == '/') {
                    i += 2;
                    while (i < length && (charAt2 = str.charAt(i)) != '\r' && charAt2 != '\n') {
                        i++;
                    }
                }
                return i;
        }
    }

    private static String translateSQL(String str) {
        return translateSQL(str, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String translateSQL(String str, boolean z) {
        if (str == null) {
            throw DbException.getInvalidValueException("SQL", null);
        }
        if (!z || str.indexOf(123) < 0) {
            return str;
        }
        return translateSQLImpl(str);
    }

    private static String translateSQLImpl(String str) {
        int length = str.length();
        char[] cArr = null;
        int i = 0;
        int i2 = 0;
        while (i2 < length) {
            char charAt = str.charAt(i2);
            switch (charAt) {
                case '\"':
                case '\'':
                case '-':
                case '/':
                    i2 = translateGetEnd(str, i2, charAt);
                    break;
                case '$':
                    i2 = translateGetEnd(str, i2, charAt);
                    break;
                case '{':
                    i++;
                    if (cArr == null) {
                        cArr = str.toCharArray();
                    }
                    cArr[i2] = ' ';
                    while (Character.isSpaceChar(cArr[i2])) {
                        i2++;
                        checkRunOver(i2, length, str);
                    }
                    int i3 = i2;
                    if (cArr[i2] >= '0' && cArr[i2] <= '9') {
                        cArr[i2 - 1] = '{';
                        while (true) {
                            checkRunOver(i2, length, str);
                            char c = cArr[i2];
                            if (c != '}') {
                                switch (c) {
                                    case '\"':
                                    case '\'':
                                    case '-':
                                    case '/':
                                        i2 = translateGetEnd(str, i2, c);
                                        break;
                                }
                                i2++;
                            } else {
                                i--;
                                break;
                            }
                        }
                    } else {
                        if (cArr[i2] == '?') {
                            int i4 = i2 + 1;
                            checkRunOver(i4, length, str);
                            while (Character.isSpaceChar(cArr[i4])) {
                                i4++;
                                checkRunOver(i4, length, str);
                            }
                            if (str.charAt(i4) != '=') {
                                throw DbException.getSyntaxError(str, i4, "=");
                            }
                            i2 = i4 + 1;
                            checkRunOver(i2, length, str);
                            while (Character.isSpaceChar(cArr[i2])) {
                                i2++;
                                checkRunOver(i2, length, str);
                            }
                        }
                        while (!Character.isSpaceChar(cArr[i2])) {
                            i2++;
                            checkRunOver(i2, length, str);
                        }
                        int i5 = 0;
                        if (found(str, i3, "fn")) {
                            i5 = 2;
                        } else if (!found(str, i3, "escape") && !found(str, i3, "call")) {
                            if (found(str, i3, "oj")) {
                                i5 = 2;
                            } else if (!found(str, i3, "ts") && !found(str, i3, "t") && !found(str, i3, DateTokenConverter.CONVERTER_KEY)) {
                                if (found(str, i3, "params")) {
                                    i5 = "params".length();
                                }
                            }
                        }
                        i2 = i3;
                        while (i5 > 0) {
                            cArr[i2] = ' ';
                            i2++;
                            i5--;
                        }
                        break;
                    }
                    break;
                case '}':
                    i--;
                    if (i < 0) {
                        throw DbException.getSyntaxError(str, i2);
                    }
                    cArr[i2] = ' ';
                    break;
            }
            i2++;
        }
        if (i != 0) {
            throw DbException.getSyntaxError(str, str.length() - 1);
        }
        if (cArr != null) {
            str = new String(cArr);
        }
        return str;
    }

    private static void checkRunOver(int i, int i2, String str) {
        if (i >= i2) {
            throw DbException.getSyntaxError(str, i);
        }
    }

    private static boolean found(String str, int i, String str2) {
        return str.regionMatches(true, i, str2, 0, str2.length());
    }

    private static void checkTypeConcurrency(int i, int i2) {
        switch (i) {
            case 1003:
            case 1004:
            case PgServer.PG_TYPE_INT2_ARRAY /* 1005 */:
                switch (i2) {
                    case 1007:
                    case 1008:
                        return;
                    default:
                        throw DbException.getInvalidValueException("resultSetConcurrency", Integer.valueOf(i2));
                }
            default:
                throw DbException.getInvalidValueException("resultSetType", Integer.valueOf(i));
        }
    }

    private static void checkHoldability(int i) {
        if (i != 1 && i != 2) {
            throw DbException.getInvalidValueException("resultSetHoldability", Integer.valueOf(i));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void checkClosed() {
        if (this.session == null) {
            throw DbException.get(ErrorCode.OBJECT_CLOSED);
        }
        if (this.session.isClosed()) {
            throw DbException.get(ErrorCode.DATABASE_CALLED_AT_SHUTDOWN);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getURL() {
        checkClosed();
        return this.url;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getUser() {
        checkClosed();
        return this.user;
    }

    private void rollbackInternal() {
        this.rollback = prepareCommand("ROLLBACK", this.rollback);
        this.rollback.executeUpdate(null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setExecutingStatement(Statement statement) {
        this.executingStatement = statement;
    }

    @Override // java.sql.Connection
    public Clob createClob() throws SQLException {
        try {
            int nextId = getNextId(10);
            debugCodeAssign("Clob", 10, nextId, "createClob()");
            checkClosed();
            return new JdbcClob(this, ValueVarchar.EMPTY, JdbcLob.State.NEW, nextId);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public Blob createBlob() throws SQLException {
        try {
            int nextId = getNextId(9);
            debugCodeAssign("Blob", 9, nextId, "createClob()");
            checkClosed();
            return new JdbcBlob(this, ValueVarbinary.EMPTY, JdbcLob.State.NEW, nextId);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public NClob createNClob() throws SQLException {
        try {
            int nextId = getNextId(10);
            debugCodeAssign("NClob", 10, nextId, "createNClob()");
            checkClosed();
            return new JdbcClob(this, ValueVarchar.EMPTY, JdbcLob.State.NEW, nextId);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public SQLXML createSQLXML() throws SQLException {
        try {
            int nextId = getNextId(17);
            debugCodeAssign("SQLXML", 17, nextId, "createSQLXML()");
            checkClosed();
            return new JdbcSQLXML(this, ValueVarchar.EMPTY, JdbcLob.State.NEW, nextId);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public Array createArrayOf(String str, Object[] objArr) throws SQLException {
        try {
            int nextId = getNextId(16);
            debugCodeAssign("Array", 16, nextId, "createArrayOf()");
            checkClosed();
            return new JdbcArray(this, ValueToObjectConverter.objectToValue(this.session, objArr, 40), nextId);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public Struct createStruct(String str, Object[] objArr) throws SQLException {
        throw unsupported("Struct");
    }

    @Override // java.sql.Connection
    public boolean isValid(int i) {
        lock();
        try {
            try {
                debugCodeCall("isValid", i);
                if (this.session == null || this.session.isClosed()) {
                    unlock();
                    return false;
                }
                getTransactionIsolation();
                unlock();
                return true;
            } catch (Exception e) {
                logAndConvert(e);
                unlock();
                return false;
            }
        } catch (Throwable th) {
            unlock();
            throw th;
        }
    }

    @Override // java.sql.Connection
    public void setClientInfo(String str, String str2) throws SQLClientInfoException {
        try {
            if (isDebugEnabled()) {
                debugCode("setClientInfo(" + quote(str) + ", " + quote(str2) + ')');
            }
            checkClosed();
            if (Objects.equals(str2, getClientInfo(str))) {
                return;
            }
            if (isInternalProperty(str)) {
                throw new SQLClientInfoException("Property name '" + str + " is used internally by H2.", (Map<String, ClientInfoStatus>) Collections.emptyMap());
            }
            Pattern pattern = getMode().supportedClientInfoPropertiesRegEx;
            if (pattern != null && pattern.matcher(str).matches()) {
                if (this.clientInfo == null) {
                    this.clientInfo = new HashMap();
                }
                this.clientInfo.put(str, str2);
                return;
            }
            throw new SQLClientInfoException("Client info name '" + str + "' not supported.", (Map<String, ClientInfoStatus>) Collections.emptyMap());
        } catch (Exception e) {
            throw convertToClientInfoException(logAndConvert(e));
        }
    }

    private static boolean isInternalProperty(String str) {
        return NUM_SERVERS.equals(str) || str.startsWith(PREFIX_SERVER);
    }

    private static SQLClientInfoException convertToClientInfoException(SQLException sQLException) {
        if (sQLException instanceof SQLClientInfoException) {
            return (SQLClientInfoException) sQLException;
        }
        return new SQLClientInfoException(sQLException.getMessage(), sQLException.getSQLState(), sQLException.getErrorCode(), null, null);
    }

    @Override // java.sql.Connection
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        try {
            if (isDebugEnabled()) {
                debugCode("setClientInfo(properties)");
            }
            checkClosed();
            if (this.clientInfo == null) {
                this.clientInfo = new HashMap();
            } else {
                this.clientInfo.clear();
            }
            for (Map.Entry entry : properties.entrySet()) {
                setClientInfo((String) entry.getKey(), (String) entry.getValue());
            }
        } catch (Exception e) {
            throw convertToClientInfoException(logAndConvert(e));
        }
    }

    @Override // java.sql.Connection
    public Properties getClientInfo() throws SQLException {
        try {
            debugCodeCall("getClientInfo");
            checkClosed();
            ArrayList<String> clusterServers = this.session.getClusterServers();
            Properties properties = new Properties();
            if (this.clientInfo != null) {
                for (Map.Entry<String, String> entry : this.clientInfo.entrySet()) {
                    properties.setProperty(entry.getKey(), entry.getValue());
                }
            }
            properties.setProperty(NUM_SERVERS, Integer.toString(clusterServers.size()));
            for (int i = 0; i < clusterServers.size(); i++) {
                properties.setProperty(PREFIX_SERVER + i, clusterServers.get(i));
            }
            return properties;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Connection
    public String getClientInfo(String str) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCodeCall("getClientInfo", str);
            }
            checkClosed();
            if (str == null) {
                throw DbException.getInvalidValueException("name", null);
            }
            return getClientInfo().getProperty(str);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
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
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Wrapper
    public boolean isWrapperFor(Class<?> cls) throws SQLException {
        return cls != null && cls.isAssignableFrom(getClass());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Value createClob(Reader reader, long j) {
        if (reader == null) {
            return ValueNull.INSTANCE;
        }
        if (j <= 0) {
            j = -1;
        }
        return this.session.addTemporaryLob(this.session.getDataHandler().getLobStorage().createClob(reader, j));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Value createBlob(InputStream inputStream, long j) {
        if (inputStream == null) {
            return ValueNull.INSTANCE;
        }
        if (j <= 0) {
            j = -1;
        }
        return this.session.addTemporaryLob(this.session.getDataHandler().getLobStorage().createBlob(inputStream, j));
    }

    public void setSchema(String str) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCodeCall("setSchema", str);
            }
            checkClosed();
            this.session.setCurrentSchemaName(str);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public String getSchema() throws SQLException {
        try {
            debugCodeCall("getSchema");
            checkClosed();
            return this.session.getCurrentSchemaName();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public void abort(Executor executor) {
    }

    public void setNetworkTimeout(Executor executor, int i) {
    }

    public int getNetworkTimeout() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void checkMap(Map<String, Class<?>> map) {
        if (map != null && map.size() > 0) {
            throw DbException.getUnsupportedException("map.size > 0");
        }
    }

    public String toString() {
        return getTraceObjectName() + ": url=" + this.url + " user=" + this.user;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CompareMode getCompareMode() {
        return this.session.getDataHandler().getCompareMode();
    }

    @Override // org.h2.engine.CastDataProvider
    public Mode getMode() {
        return this.session.getMode();
    }

    public Session.StaticSettings getStaticSettings() {
        checkClosed();
        return this.session.getStaticSettings();
    }

    @Override // org.h2.engine.CastDataProvider
    public ValueTimestampTimeZone currentTimestamp() {
        Session session = this.session;
        if (session == null) {
            throw DbException.get(ErrorCode.OBJECT_CLOSED);
        }
        return session.currentTimestamp();
    }

    @Override // org.h2.engine.CastDataProvider
    public TimeZoneProvider currentTimeZone() {
        Session session = this.session;
        if (session == null) {
            throw DbException.get(ErrorCode.OBJECT_CLOSED);
        }
        return session.currentTimeZone();
    }

    @Override // org.h2.engine.CastDataProvider
    public JavaObjectSerializer getJavaObjectSerializer() {
        Session session = this.session;
        if (session == null) {
            throw DbException.get(ErrorCode.OBJECT_CLOSED);
        }
        return session.getJavaObjectSerializer();
    }

    @Override // org.h2.engine.CastDataProvider
    public boolean zeroBasedEnums() {
        Session session = this.session;
        if (session == null) {
            throw DbException.get(ErrorCode.OBJECT_CLOSED);
        }
        return session.zeroBasedEnums();
    }
}
