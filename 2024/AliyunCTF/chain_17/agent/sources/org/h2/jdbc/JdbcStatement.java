package org.h2.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.coyote.http11.Constants;
import org.h2.api.ErrorCode;
import org.h2.command.CommandInterface;
import org.h2.engine.Session;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.message.TraceObject;
import org.h2.result.ResultInterface;
import org.h2.result.ResultWithGeneratedKeys;
import org.h2.result.SimpleResult;
import org.h2.util.ParserUtil;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.springframework.web.servlet.tags.form.TextareaTag;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/JdbcStatement.class */
public class JdbcStatement extends TraceObject implements Statement, JdbcStatementBackwardsCompat {
    protected JdbcConnection conn;
    protected Session session;
    protected JdbcResultSet resultSet;
    protected long maxRows;
    protected long updateCount;
    protected JdbcResultSet generatedKeys;
    protected final int resultSetType;
    protected final int resultSetConcurrency;
    private volatile CommandInterface executingCommand;
    private ArrayList<String> batchCommands;
    private volatile boolean cancelled;
    private boolean closeOnCompletion;
    protected int fetchSize = SysProperties.SERVER_RESULT_SET_FETCH_SIZE;
    private boolean escapeProcessing = true;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JdbcStatement(JdbcConnection jdbcConnection, int i, int i2, int i3) {
        this.conn = jdbcConnection;
        this.session = jdbcConnection.getSession();
        setTrace(this.session.getTrace(), 8, i);
        this.resultSetType = i2;
        this.resultSetConcurrency = i3;
    }

    public ResultSet executeQuery(String str) throws SQLException {
        try {
            int nextId = getNextId(4);
            if (isDebugEnabled()) {
                debugCodeAssign("ResultSet", 4, nextId, "executeQuery(" + quote(str) + ')');
            }
            Session session = this.session;
            session.lock();
            try {
                checkClosed();
                closeOldResultSet();
                CommandInterface prepareCommand = this.conn.prepareCommand(JdbcConnection.translateSQL(str, this.escapeProcessing), this.fetchSize);
                boolean z = false;
                boolean z2 = this.resultSetType != 1003;
                boolean z3 = this.resultSetConcurrency == 1008;
                setExecutingStatement(prepareCommand);
                try {
                    ResultInterface executeQuery = prepareCommand.executeQuery(this.maxRows, z2);
                    z = executeQuery.isLazy();
                    if (!z) {
                        setExecutingStatement(null);
                    }
                    if (!z) {
                        prepareCommand.close();
                    }
                    this.resultSet = new JdbcResultSet(this.conn, this, prepareCommand, executeQuery, nextId, z2, z3, false);
                    session.unlock();
                    return this.resultSet;
                } catch (Throwable th) {
                    if (!z) {
                        setExecutingStatement(null);
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                session.unlock();
                throw th2;
            }
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public final int executeUpdate(String str) throws SQLException {
        try {
            debugCodeCall("executeUpdate", str);
            long executeUpdateInternal = executeUpdateInternal(str, null);
            if (executeUpdateInternal <= 2147483647L) {
                return (int) executeUpdateInternal;
            }
            return -2;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public final long executeLargeUpdate(String str) throws SQLException {
        try {
            debugCodeCall("executeLargeUpdate", str);
            return executeUpdateInternal(str, null);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    /* JADX WARN: Finally extract failed */
    private long executeUpdateInternal(String str, Object obj) {
        if (getClass() != JdbcStatement.class) {
            throw DbException.get(ErrorCode.METHOD_NOT_ALLOWED_FOR_PREPARED_STATEMENT);
        }
        checkClosed();
        closeOldResultSet();
        CommandInterface prepareCommand = this.conn.prepareCommand(JdbcConnection.translateSQL(str, this.escapeProcessing), this.fetchSize);
        Session session = this.session;
        session.lock();
        try {
            setExecutingStatement(prepareCommand);
            try {
                ResultWithGeneratedKeys executeUpdate = prepareCommand.executeUpdate(obj);
                this.updateCount = executeUpdate.getUpdateCount();
                ResultInterface generatedKeys = executeUpdate.getGeneratedKeys();
                if (generatedKeys != null) {
                    this.generatedKeys = new JdbcResultSet(this.conn, this, prepareCommand, generatedKeys, getNextId(4), true, false, false);
                }
                setExecutingStatement(null);
                prepareCommand.close();
                return this.updateCount;
            } catch (Throwable th) {
                setExecutingStatement(null);
                throw th;
            }
        } finally {
            session.unlock();
        }
    }

    @Override // java.sql.Statement
    public final boolean execute(String str) throws SQLException {
        try {
            debugCodeCall("execute", str);
            return executeInternal(str, false);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    /* JADX WARN: Finally extract failed */
    private boolean executeInternal(String str, Object obj) {
        boolean z;
        if (getClass() != JdbcStatement.class) {
            throw DbException.get(ErrorCode.METHOD_NOT_ALLOWED_FOR_PREPARED_STATEMENT);
        }
        int nextId = getNextId(4);
        checkClosed();
        closeOldResultSet();
        CommandInterface prepareCommand = this.conn.prepareCommand(JdbcConnection.translateSQL(str, this.escapeProcessing), this.fetchSize);
        boolean z2 = false;
        Session session = this.session;
        session.lock();
        try {
            setExecutingStatement(prepareCommand);
            try {
                if (prepareCommand.isQuery()) {
                    z = true;
                    boolean z3 = this.resultSetType != 1003;
                    boolean z4 = this.resultSetConcurrency == 1008;
                    ResultInterface executeQuery = prepareCommand.executeQuery(this.maxRows, z3);
                    z2 = executeQuery.isLazy();
                    this.resultSet = new JdbcResultSet(this.conn, this, prepareCommand, executeQuery, nextId, z3, z4, false);
                } else {
                    z = false;
                    ResultWithGeneratedKeys executeUpdate = prepareCommand.executeUpdate(obj);
                    this.updateCount = executeUpdate.getUpdateCount();
                    ResultInterface generatedKeys = executeUpdate.getGeneratedKeys();
                    if (generatedKeys != null) {
                        this.generatedKeys = new JdbcResultSet(this.conn, this, prepareCommand, generatedKeys, nextId, true, false, false);
                    }
                }
                if (!z2) {
                    setExecutingStatement(null);
                }
                if (!z2) {
                    prepareCommand.close();
                }
                return z;
            } catch (Throwable th) {
                if (0 == 0) {
                    setExecutingStatement(null);
                }
                throw th;
            }
        } finally {
            session.unlock();
        }
    }

    @Override // java.sql.Statement
    public ResultSet getResultSet() throws SQLException {
        try {
            checkClosed();
            if (this.resultSet != null) {
                debugCodeAssign("ResultSet", 4, this.resultSet.getTraceId(), "getResultSet()");
            } else {
                debugCodeCall("getResultSet");
            }
            return this.resultSet;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public final int getUpdateCount() throws SQLException {
        try {
            debugCodeCall("getUpdateCount");
            checkClosed();
            if (this.updateCount <= 2147483647L) {
                return (int) this.updateCount;
            }
            return -2;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public final long getLargeUpdateCount() throws SQLException {
        try {
            debugCodeCall("getLargeUpdateCount");
            checkClosed();
            return this.updateCount;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public void close() throws SQLException {
        try {
            debugCodeCall(Constants.CLOSE);
            closeInternal();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private void closeInternal() {
        Session session = this.session;
        session.lock();
        try {
            closeOldResultSet();
            if (this.conn != null) {
                this.conn = null;
            }
        } finally {
            session.unlock();
        }
    }

    @Override // java.sql.Statement
    public Connection getConnection() {
        debugCodeCall("getConnection");
        return this.conn;
    }

    @Override // java.sql.Statement
    public SQLWarning getWarnings() throws SQLException {
        try {
            debugCodeCall("getWarnings");
            checkClosed();
            return null;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public void clearWarnings() throws SQLException {
        try {
            debugCodeCall("clearWarnings");
            checkClosed();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public void setCursorName(String str) throws SQLException {
        try {
            debugCodeCall("setCursorName", str);
            checkClosed();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public void setFetchDirection(int i) throws SQLException {
        try {
            debugCodeCall("setFetchDirection", i);
            checkClosed();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public int getFetchDirection() throws SQLException {
        try {
            debugCodeCall("getFetchDirection");
            checkClosed();
            return 1000;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public int getMaxRows() throws SQLException {
        try {
            debugCodeCall("getMaxRows");
            checkClosed();
            if (this.maxRows <= 2147483647L) {
                return (int) this.maxRows;
            }
            return 0;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public long getLargeMaxRows() throws SQLException {
        try {
            debugCodeCall("getLargeMaxRows");
            checkClosed();
            return this.maxRows;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public void setMaxRows(int i) throws SQLException {
        try {
            debugCodeCall("setMaxRows", i);
            checkClosed();
            if (i < 0) {
                throw DbException.getInvalidValueException("maxRows", Integer.valueOf(i));
            }
            this.maxRows = i;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public void setLargeMaxRows(long j) throws SQLException {
        try {
            debugCodeCall("setLargeMaxRows", j);
            checkClosed();
            if (j < 0) {
                throw DbException.getInvalidValueException("maxRows", Long.valueOf(j));
            }
            this.maxRows = j;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public void setFetchSize(int i) throws SQLException {
        try {
            debugCodeCall("setFetchSize", i);
            checkClosed();
            if (i < 0 || (i > 0 && this.maxRows > 0 && i > this.maxRows)) {
                throw DbException.getInvalidValueException(TextareaTag.ROWS_ATTRIBUTE, Integer.valueOf(i));
            }
            if (i == 0) {
                i = SysProperties.SERVER_RESULT_SET_FETCH_SIZE;
            }
            this.fetchSize = i;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public int getFetchSize() throws SQLException {
        try {
            debugCodeCall("getFetchSize");
            checkClosed();
            return this.fetchSize;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public int getResultSetConcurrency() throws SQLException {
        try {
            debugCodeCall("getResultSetConcurrency");
            checkClosed();
            return this.resultSetConcurrency;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public int getResultSetType() throws SQLException {
        try {
            debugCodeCall("getResultSetType");
            checkClosed();
            return this.resultSetType;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public int getMaxFieldSize() throws SQLException {
        try {
            debugCodeCall("getMaxFieldSize");
            checkClosed();
            return 0;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public void setMaxFieldSize(int i) throws SQLException {
        try {
            debugCodeCall("setMaxFieldSize", i);
            checkClosed();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public void setEscapeProcessing(boolean z) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setEscapeProcessing(" + z + ')');
            }
            checkClosed();
            this.escapeProcessing = z;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public void cancel() throws SQLException {
        try {
            debugCodeCall("cancel");
            checkClosed();
            CommandInterface commandInterface = this.executingCommand;
            if (commandInterface != null) {
                try {
                    commandInterface.cancel();
                    this.cancelled = true;
                } catch (Throwable th) {
                    setExecutingStatement(null);
                    throw th;
                }
            }
            setExecutingStatement(null);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override // java.sql.Statement
    public int getQueryTimeout() throws SQLException {
        try {
            debugCodeCall("getQueryTimeout");
            checkClosed();
            return this.conn.getQueryTimeout();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public void setQueryTimeout(int i) throws SQLException {
        try {
            debugCodeCall("setQueryTimeout", i);
            checkClosed();
            if (i < 0) {
                throw DbException.getInvalidValueException("seconds", Integer.valueOf(i));
            }
            this.conn.setQueryTimeout(i);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public void addBatch(String str) throws SQLException {
        try {
            debugCodeCall("addBatch", str);
            checkClosed();
            String translateSQL = JdbcConnection.translateSQL(str, this.escapeProcessing);
            if (this.batchCommands == null) {
                this.batchCommands = Utils.newSmallArrayList();
            }
            this.batchCommands.add(translateSQL);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public void clearBatch() throws SQLException {
        try {
            debugCodeCall("clearBatch");
            checkClosed();
            this.batchCommands = null;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public int[] executeBatch() throws SQLException {
        try {
            debugCodeCall("executeBatch");
            checkClosed();
            if (this.batchCommands == null) {
                this.batchCommands = new ArrayList<>();
            }
            int size = this.batchCommands.size();
            int[] iArr = new int[size];
            SQLException sQLException = new SQLException();
            for (int i = 0; i < size; i++) {
                long executeBatchElement = executeBatchElement(this.batchCommands.get(i), sQLException);
                iArr[i] = executeBatchElement <= 2147483647L ? (int) executeBatchElement : -2;
            }
            this.batchCommands = null;
            SQLException nextException = sQLException.getNextException();
            if (nextException != null) {
                throw new JdbcBatchUpdateException(nextException, iArr);
            }
            return iArr;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public long[] executeLargeBatch() throws SQLException {
        try {
            debugCodeCall("executeLargeBatch");
            checkClosed();
            if (this.batchCommands == null) {
                this.batchCommands = new ArrayList<>();
            }
            int size = this.batchCommands.size();
            long[] jArr = new long[size];
            SQLException sQLException = new SQLException();
            for (int i = 0; i < size; i++) {
                jArr[i] = executeBatchElement(this.batchCommands.get(i), sQLException);
            }
            this.batchCommands = null;
            SQLException nextException = sQLException.getNextException();
            if (nextException != null) {
                throw new JdbcBatchUpdateException(nextException, jArr);
            }
            return jArr;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private long executeBatchElement(String str, SQLException sQLException) {
        long j;
        try {
            j = executeUpdateInternal(str, null);
        } catch (Exception e) {
            sQLException.setNextException(logAndConvert(e));
            j = -3;
        }
        return j;
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        try {
            int traceId = this.generatedKeys != null ? this.generatedKeys.getTraceId() : getNextId(4);
            if (isDebugEnabled()) {
                debugCodeAssign("ResultSet", 4, traceId, "getGeneratedKeys()");
            }
            checkClosed();
            if (this.generatedKeys == null) {
                this.generatedKeys = new JdbcResultSet(this.conn, this, (CommandInterface) null, (ResultInterface) new SimpleResult(), traceId, true, false, false);
            }
            return this.generatedKeys;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public boolean getMoreResults() throws SQLException {
        try {
            debugCodeCall("getMoreResults");
            checkClosed();
            closeOldResultSet();
            return false;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public boolean getMoreResults(int i) throws SQLException {
        try {
            debugCodeCall("getMoreResults", i);
            switch (i) {
                case 1:
                case 3:
                    checkClosed();
                    closeOldResultSet();
                    return false;
                case 2:
                    return false;
                default:
                    throw DbException.getInvalidValueException("current", Integer.valueOf(i));
            }
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public final int executeUpdate(String str, int i) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("executeUpdate(" + quote(str) + ", " + i + ')');
            }
            long executeUpdateInternal = executeUpdateInternal(str, Boolean.valueOf(i == 1));
            if (executeUpdateInternal <= 2147483647L) {
                return (int) executeUpdateInternal;
            }
            return -2;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public final long executeLargeUpdate(String str, int i) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("executeLargeUpdate(" + quote(str) + ", " + i + ')');
            }
            return executeUpdateInternal(str, Boolean.valueOf(i == 1));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public final int executeUpdate(String str, int[] iArr) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("executeUpdate(" + quote(str) + ", " + quoteIntArray(iArr) + ')');
            }
            long executeUpdateInternal = executeUpdateInternal(str, iArr);
            if (executeUpdateInternal <= 2147483647L) {
                return (int) executeUpdateInternal;
            }
            return -2;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public final long executeLargeUpdate(String str, int[] iArr) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("executeLargeUpdate(" + quote(str) + ", " + quoteIntArray(iArr) + ')');
            }
            return executeUpdateInternal(str, iArr);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public final int executeUpdate(String str, String[] strArr) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("executeUpdate(" + quote(str) + ", " + quoteArray(strArr) + ')');
            }
            long executeUpdateInternal = executeUpdateInternal(str, strArr);
            if (executeUpdateInternal <= 2147483647L) {
                return (int) executeUpdateInternal;
            }
            return -2;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public final long executeLargeUpdate(String str, String[] strArr) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("executeLargeUpdate(" + quote(str) + ", " + quoteArray(strArr) + ')');
            }
            return executeUpdateInternal(str, strArr);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public final boolean execute(String str, int i) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("execute(" + quote(str) + ", " + i + ')');
            }
            return executeInternal(str, Boolean.valueOf(i == 1));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public final boolean execute(String str, int[] iArr) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("execute(" + quote(str) + ", " + quoteIntArray(iArr) + ')');
            }
            return executeInternal(str, iArr);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public final boolean execute(String str, String[] strArr) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("execute(" + quote(str) + ", " + quoteArray(strArr) + ')');
            }
            return executeInternal(str, strArr);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Statement
    public int getResultSetHoldability() throws SQLException {
        try {
            debugCodeCall("getResultSetHoldability");
            checkClosed();
            return 1;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public void closeOnCompletion() throws SQLException {
        try {
            debugCodeCall("closeOnCompletion");
            checkClosed();
            this.closeOnCompletion = true;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public boolean isCloseOnCompletion() throws SQLException {
        try {
            debugCodeCall("isCloseOnCompletion");
            checkClosed();
            return this.closeOnCompletion;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void closeIfCloseOnCompletion() {
        if (this.closeOnCompletion) {
            try {
                closeInternal();
            } catch (Exception e) {
                logAndConvert(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void checkClosed() {
        if (this.conn == null) {
            throw DbException.get(ErrorCode.OBJECT_CLOSED);
        }
        this.conn.checkClosed();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void closeOldResultSet() {
        try {
            if (this.resultSet != null) {
                this.resultSet.closeInternal(true);
            }
            if (this.generatedKeys != null) {
                this.generatedKeys.closeInternal(true);
            }
        } finally {
            this.cancelled = false;
            this.resultSet = null;
            this.updateCount = -1L;
            this.generatedKeys = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setExecutingStatement(CommandInterface commandInterface) {
        if (commandInterface == null) {
            this.conn.setExecutingStatement(null);
        } else {
            this.conn.setExecutingStatement(this);
        }
        this.executingCommand = commandInterface;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onLazyResultSetClose(CommandInterface commandInterface, boolean z) {
        setExecutingStatement(null);
        commandInterface.stop();
        if (z) {
            commandInterface.close();
        }
    }

    @Override // java.sql.Statement
    public boolean isClosed() throws SQLException {
        try {
            debugCodeCall("isClosed");
            return this.conn == null;
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

    @Override // java.sql.Statement
    public boolean isPoolable() {
        debugCodeCall("isPoolable");
        return false;
    }

    @Override // java.sql.Statement
    public void setPoolable(boolean z) {
        if (isDebugEnabled()) {
            debugCode("setPoolable(" + z + ')');
        }
    }

    @Override // org.h2.jdbc.JdbcStatementBackwardsCompat
    public String enquoteIdentifier(String str, boolean z) throws SQLException {
        if (isSimpleIdentifier(str)) {
            return z ? '\"' + str + '\"' : str;
        }
        try {
            int length = str.length();
            if (length > 0) {
                if (str.charAt(0) == '\"') {
                    checkQuotes(str, 1, length);
                    return str;
                }
                if (str.startsWith("U&\"") || str.startsWith("u&\"")) {
                    checkQuotes(str, 3, length);
                    StringUtils.decodeUnicodeStringSQL(str, 92);
                    return str;
                }
            }
            return StringUtils.quoteIdentifier(str);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private static void checkQuotes(String str, int i, int i2) {
        boolean z = true;
        for (int i3 = i; i3 < i2; i3++) {
            if (str.charAt(i3) == '\"') {
                z = !z;
            } else if (!z) {
                throw DbException.get(ErrorCode.INVALID_NAME_1, str);
            }
        }
        if (z) {
            throw DbException.get(ErrorCode.INVALID_NAME_1, str);
        }
    }

    @Override // org.h2.jdbc.JdbcStatementBackwardsCompat
    public boolean isSimpleIdentifier(String str) throws SQLException {
        try {
            checkClosed();
            Session.StaticSettings staticSettings = this.conn.getStaticSettings();
            return ParserUtil.isSimpleIdentifier(str, staticSettings.databaseToUpper, staticSettings.databaseToLower);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public String toString() {
        return getTraceObjectName();
    }
}
