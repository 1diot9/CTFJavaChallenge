package org.h2.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;
import org.h2.api.ErrorCode;
import org.h2.command.CommandInterface;
import org.h2.engine.Session;
import org.h2.expression.ParameterInterface;
import org.h2.message.DbException;
import org.h2.result.MergedResult;
import org.h2.result.ResultInterface;
import org.h2.result.ResultWithGeneratedKeys;
import org.h2.util.IOUtils;
import org.h2.util.LegacyDateTimeUtils;
import org.h2.util.Utils;
import org.h2.value.DataType;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueDouble;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueNumeric;
import org.h2.value.ValueReal;
import org.h2.value.ValueSmallint;
import org.h2.value.ValueTinyint;
import org.h2.value.ValueToObjectConverter;
import org.h2.value.ValueVarbinary;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/JdbcPreparedStatement.class */
public class JdbcPreparedStatement extends JdbcStatement implements PreparedStatement {
    protected CommandInterface command;
    private ArrayList<Value[]> batchParameters;
    private MergedResult batchIdentities;
    private HashMap<String, Integer> cachedColumnLabelMap;
    private final Object generatedKeysRequest;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JdbcPreparedStatement(JdbcConnection jdbcConnection, String str, int i, int i2, int i3, Object obj) {
        super(jdbcConnection, i, i2, i3);
        this.generatedKeysRequest = obj;
        setTrace(this.session.getTrace(), 3, i);
        this.command = jdbcConnection.prepareCommand(str, this.fetchSize);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCachedColumnLabelMap(HashMap<String, Integer> hashMap) {
        this.cachedColumnLabelMap = hashMap;
    }

    @Override // java.sql.PreparedStatement
    public ResultSet executeQuery() throws SQLException {
        try {
            int nextId = getNextId(4);
            debugCodeAssign("ResultSet", 4, nextId, "executeQuery()");
            this.batchIdentities = null;
            Session session = this.session;
            session.lock();
            try {
                checkClosed();
                closeOldResultSet();
                boolean z = false;
                boolean z2 = this.resultSetType != 1003;
                boolean z3 = this.resultSetConcurrency == 1008;
                try {
                    setExecutingStatement(this.command);
                    ResultInterface executeQuery = this.command.executeQuery(this.maxRows, z2);
                    z = executeQuery.isLazy();
                    if (!z) {
                        setExecutingStatement(null);
                    }
                    this.resultSet = new JdbcResultSet(this.conn, this, this.command, executeQuery, nextId, z2, z3, this.cachedColumnLabelMap);
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

    public int executeUpdate() throws SQLException {
        try {
            debugCodeCall("executeUpdate");
            checkClosed();
            this.batchIdentities = null;
            long executeUpdateInternal = executeUpdateInternal();
            if (executeUpdateInternal <= 2147483647L) {
                return (int) executeUpdateInternal;
            }
            return -2;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public long executeLargeUpdate() throws SQLException {
        try {
            debugCodeCall("executeLargeUpdate");
            checkClosed();
            this.batchIdentities = null;
            return executeUpdateInternal();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    /* JADX WARN: Finally extract failed */
    private long executeUpdateInternal() {
        closeOldResultSet();
        Session session = this.session;
        session.lock();
        try {
            try {
                setExecutingStatement(this.command);
                ResultWithGeneratedKeys executeUpdate = this.command.executeUpdate(this.generatedKeysRequest);
                this.updateCount = executeUpdate.getUpdateCount();
                ResultInterface generatedKeys = executeUpdate.getGeneratedKeys();
                if (generatedKeys != null) {
                    this.generatedKeys = new JdbcResultSet(this.conn, (JdbcStatement) this, this.command, generatedKeys, getNextId(4), true, false, false);
                }
                setExecutingStatement(null);
                return this.updateCount;
            } catch (Throwable th) {
                setExecutingStatement(null);
                throw th;
            }
        } finally {
            session.unlock();
        }
    }

    /* JADX WARN: Finally extract failed */
    @Override // java.sql.PreparedStatement
    public boolean execute() throws SQLException {
        boolean z;
        try {
            int nextId = getNextId(4);
            debugCodeCall("execute");
            checkClosed();
            Session session = this.session;
            session.lock();
            try {
                closeOldResultSet();
                boolean z2 = false;
                try {
                    setExecutingStatement(this.command);
                    if (this.command.isQuery()) {
                        z = true;
                        boolean z3 = this.resultSetType != 1003;
                        boolean z4 = this.resultSetConcurrency == 1008;
                        ResultInterface executeQuery = this.command.executeQuery(this.maxRows, z3);
                        z2 = executeQuery.isLazy();
                        this.resultSet = new JdbcResultSet(this.conn, this, this.command, executeQuery, nextId, z3, z4, this.cachedColumnLabelMap);
                    } else {
                        z = false;
                        ResultWithGeneratedKeys executeUpdate = this.command.executeUpdate(this.generatedKeysRequest);
                        this.updateCount = executeUpdate.getUpdateCount();
                        ResultInterface generatedKeys = executeUpdate.getGeneratedKeys();
                        if (generatedKeys != null) {
                            this.generatedKeys = new JdbcResultSet(this.conn, (JdbcStatement) this, this.command, generatedKeys, nextId, true, false, false);
                        }
                    }
                    if (!z2) {
                        setExecutingStatement(null);
                    }
                    session.unlock();
                    return z;
                } catch (Throwable th) {
                    if (0 == 0) {
                        setExecutingStatement(null);
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                session.unlock();
                throw th2;
            }
        } catch (Throwable th3) {
            throw logAndConvert(th3);
        }
    }

    @Override // java.sql.PreparedStatement
    public void clearParameters() throws SQLException {
        try {
            debugCodeCall("clearParameters");
            checkClosed();
            Iterator<? extends ParameterInterface> it = this.command.getParameters().iterator();
            while (it.hasNext()) {
                it.next().setValue(null, this.batchParameters == null);
            }
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // org.h2.jdbc.JdbcStatement, java.sql.Statement
    public ResultSet executeQuery(String str) throws SQLException {
        try {
            debugCodeCall("executeQuery", str);
            throw DbException.get(ErrorCode.METHOD_NOT_ALLOWED_FOR_PREPARED_STATEMENT);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // org.h2.jdbc.JdbcStatement, java.sql.Statement
    public void addBatch(String str) throws SQLException {
        try {
            debugCodeCall("addBatch", str);
            throw DbException.get(ErrorCode.METHOD_NOT_ALLOWED_FOR_PREPARED_STATEMENT);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setNull(int i, int i2) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setNull(" + i + ", " + i2 + ')');
            }
            setParameter(i, ValueNull.INSTANCE);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setInt(int i, int i2) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setInt(" + i + ", " + i2 + ')');
            }
            setParameter(i, ValueInteger.get(i2));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setString(int i, String str) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setString(" + i + ", " + quote(str) + ')');
            }
            setParameter(i, str == null ? ValueNull.INSTANCE : ValueVarchar.get(str, this.conn));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setBigDecimal(" + i + ", " + quoteBigDecimal(bigDecimal) + ')');
            }
            setParameter(i, bigDecimal == null ? ValueNull.INSTANCE : ValueNumeric.getAnyScale(bigDecimal));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setDate(int i, Date date) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setDate(" + i + ", " + quoteDate(date) + ')');
            }
            setParameter(i, date == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromDate(this.conn, null, date));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setTime(int i, Time time) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setTime(" + i + ", " + quoteTime(time) + ')');
            }
            setParameter(i, time == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromTime(this.conn, null, time));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setTimestamp(int i, Timestamp timestamp) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setTimestamp(" + i + ", " + quoteTimestamp(timestamp) + ')');
            }
            setParameter(i, timestamp == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromTimestamp(this.conn, (TimeZone) null, timestamp));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setObject(int i, Object obj) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setObject(" + i + ", x)");
            }
            if (obj == null) {
                setParameter(i, ValueNull.INSTANCE);
            } else {
                setParameter(i, ValueToObjectConverter.objectToValue(this.session, obj, -1));
            }
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setObject(int i, Object obj, int i2) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setObject(" + i + ", x, " + i2 + ')');
            }
            setObjectWithType(i, obj, DataType.convertSQLTypeToValueType(i2));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setObject(int i, Object obj, int i2, int i3) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setObject(" + i + ", x, " + i2 + ", " + i3 + ')');
            }
            setObjectWithType(i, obj, DataType.convertSQLTypeToValueType(i2));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public void setObject(int i, Object obj, SQLType sQLType) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setObject(" + i + ", x, " + DataType.sqlTypeToString(sQLType) + ')');
            }
            setObjectWithType(i, obj, DataType.convertSQLTypeToValueType(sQLType));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public void setObject(int i, Object obj, SQLType sQLType, int i2) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setObject(" + i + ", x, " + DataType.sqlTypeToString(sQLType) + ", " + i2 + ')');
            }
            setObjectWithType(i, obj, DataType.convertSQLTypeToValueType(sQLType));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private void setObjectWithType(int i, Object obj, int i2) {
        if (obj == null) {
            setParameter(i, ValueNull.INSTANCE);
            return;
        }
        Value objectToValue = ValueToObjectConverter.objectToValue(this.conn.getSession(), obj, i2);
        if (i2 != -1) {
            objectToValue = objectToValue.convertTo(i2, this.conn);
        }
        setParameter(i, objectToValue);
    }

    @Override // java.sql.PreparedStatement
    public void setBoolean(int i, boolean z) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setBoolean(" + i + ", " + z + ')');
            }
            setParameter(i, ValueBoolean.get(z));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setByte(int i, byte b) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setByte(" + i + ", " + ((int) b) + ')');
            }
            setParameter(i, ValueTinyint.get(b));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setShort(int i, short s) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setShort(" + i + ", (short) " + ((int) s) + ')');
            }
            setParameter(i, ValueSmallint.get(s));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setLong(int i, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setLong(" + i + ", " + j + "L)");
            }
            setParameter(i, ValueBigint.get(j));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setFloat(int i, float f) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setFloat(" + i + ", " + f + "f)");
            }
            setParameter(i, ValueReal.get(f));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setDouble(int i, double d) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setDouble(" + i + ", " + d + "d)");
            }
            setParameter(i, ValueDouble.get(d));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setRef(int i, Ref ref) throws SQLException {
        throw unsupported("ref");
    }

    @Override // java.sql.PreparedStatement
    public void setDate(int i, Date date, Calendar calendar) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setDate(" + i + ", " + quoteDate(date) + ", calendar)");
            }
            if (date == null) {
                setParameter(i, ValueNull.INSTANCE);
            } else {
                setParameter(i, LegacyDateTimeUtils.fromDate(this.conn, calendar != null ? calendar.getTimeZone() : null, date));
            }
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setTime(int i, Time time, Calendar calendar) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setTime(" + i + ", " + quoteTime(time) + ", calendar)");
            }
            if (time == null) {
                setParameter(i, ValueNull.INSTANCE);
            } else {
                setParameter(i, LegacyDateTimeUtils.fromTime(this.conn, calendar != null ? calendar.getTimeZone() : null, time));
            }
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setTimestamp(int i, Timestamp timestamp, Calendar calendar) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setTimestamp(" + i + ", " + quoteTimestamp(timestamp) + ", calendar)");
            }
            if (timestamp == null) {
                setParameter(i, ValueNull.INSTANCE);
            } else {
                setParameter(i, LegacyDateTimeUtils.fromTimestamp(this.conn, calendar != null ? calendar.getTimeZone() : null, timestamp));
            }
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    @Deprecated
    public void setUnicodeStream(int i, InputStream inputStream, int i2) throws SQLException {
        throw unsupported("unicodeStream");
    }

    @Override // java.sql.PreparedStatement
    public void setNull(int i, int i2, String str) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setNull(" + i + ", " + i2 + ", " + quote(str) + ')');
            }
            setNull(i, i2);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setBlob(int i, Blob blob) throws SQLException {
        Value createBlob;
        try {
            if (isDebugEnabled()) {
                debugCode("setBlob(" + i + ", x)");
            }
            checkClosed();
            if (blob == null) {
                createBlob = ValueNull.INSTANCE;
            } else {
                createBlob = this.conn.createBlob(blob.getBinaryStream(), -1L);
            }
            setParameter(i, createBlob);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setBlob(int i, InputStream inputStream) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setBlob(" + i + ", x)");
            }
            checkClosed();
            setParameter(i, this.conn.createBlob(inputStream, -1L));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setClob(int i, Clob clob) throws SQLException {
        Value createClob;
        try {
            if (isDebugEnabled()) {
                debugCode("setClob(" + i + ", x)");
            }
            checkClosed();
            if (clob == null) {
                createClob = ValueNull.INSTANCE;
            } else {
                createClob = this.conn.createClob(clob.getCharacterStream(), -1L);
            }
            setParameter(i, createClob);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setClob(int i, Reader reader) throws SQLException {
        Value createClob;
        try {
            if (isDebugEnabled()) {
                debugCode("setClob(" + i + ", x)");
            }
            checkClosed();
            if (reader == null) {
                createClob = ValueNull.INSTANCE;
            } else {
                createClob = this.conn.createClob(reader, -1L);
            }
            setParameter(i, createClob);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setArray(int i, Array array) throws SQLException {
        Value objectToValue;
        try {
            if (isDebugEnabled()) {
                debugCode("setArray(" + i + ", x)");
            }
            checkClosed();
            if (array == null) {
                objectToValue = ValueNull.INSTANCE;
            } else {
                objectToValue = ValueToObjectConverter.objectToValue(this.session, array.getArray(), 40);
            }
            setParameter(i, objectToValue);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setBytes(int i, byte[] bArr) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setBytes(" + i + ", " + quoteBytes(bArr) + ')');
            }
            setParameter(i, bArr == null ? ValueNull.INSTANCE : ValueVarbinary.get(bArr));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setBinaryStream(int i, InputStream inputStream, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setBinaryStream(" + i + ", x, " + j + "L)");
            }
            checkClosed();
            setParameter(i, this.conn.createBlob(inputStream, j));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setBinaryStream(int i, InputStream inputStream, int i2) throws SQLException {
        setBinaryStream(i, inputStream, i2);
    }

    @Override // java.sql.PreparedStatement
    public void setBinaryStream(int i, InputStream inputStream) throws SQLException {
        setBinaryStream(i, inputStream, -1);
    }

    @Override // java.sql.PreparedStatement
    public void setAsciiStream(int i, InputStream inputStream, int i2) throws SQLException {
        setAsciiStream(i, inputStream, i2);
    }

    @Override // java.sql.PreparedStatement
    public void setAsciiStream(int i, InputStream inputStream, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setAsciiStream(" + i + ", x, " + j + "L)");
            }
            checkClosed();
            setParameter(i, this.conn.createClob(IOUtils.getAsciiReader(inputStream), j));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setAsciiStream(int i, InputStream inputStream) throws SQLException {
        setAsciiStream(i, inputStream, -1);
    }

    @Override // java.sql.PreparedStatement
    public void setCharacterStream(int i, Reader reader, int i2) throws SQLException {
        setCharacterStream(i, reader, i2);
    }

    @Override // java.sql.PreparedStatement
    public void setCharacterStream(int i, Reader reader) throws SQLException {
        setCharacterStream(i, reader, -1);
    }

    @Override // java.sql.PreparedStatement
    public void setCharacterStream(int i, Reader reader, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setCharacterStream(" + i + ", x, " + j + "L)");
            }
            checkClosed();
            setParameter(i, this.conn.createClob(reader, j));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setURL(int i, URL url) throws SQLException {
        throw unsupported("url");
    }

    @Override // java.sql.PreparedStatement
    public ResultSetMetaData getMetaData() throws SQLException {
        try {
            debugCodeCall("getMetaData");
            checkClosed();
            ResultInterface metaData = this.command.getMetaData();
            if (metaData == null) {
                return null;
            }
            int nextId = getNextId(5);
            debugCodeAssign("ResultSetMetaData", 5, nextId, "getMetaData()");
            return new JdbcResultSetMetaData(null, this, metaData, this.conn.getCatalog(), this.session.getTrace(), nextId);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // org.h2.jdbc.JdbcStatement, java.sql.Statement
    public void clearBatch() throws SQLException {
        try {
            debugCodeCall("clearBatch");
            checkClosed();
            this.batchParameters = null;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // org.h2.jdbc.JdbcStatement, java.sql.Statement, java.lang.AutoCloseable
    public void close() throws SQLException {
        try {
            super.close();
            this.batchParameters = null;
            this.batchIdentities = null;
            if (this.command != null) {
                this.command.close();
                this.command = null;
            }
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // org.h2.jdbc.JdbcStatement, java.sql.Statement
    public int[] executeBatch() throws SQLException {
        try {
            debugCodeCall("executeBatch");
            if (this.batchParameters == null) {
                this.batchParameters = new ArrayList<>();
            }
            this.batchIdentities = new MergedResult();
            int size = this.batchParameters.size();
            int[] iArr = new int[size];
            SQLException sQLException = new SQLException();
            checkClosed();
            for (int i = 0; i < size; i++) {
                long executeBatchElement = executeBatchElement(this.batchParameters.get(i), sQLException);
                iArr[i] = executeBatchElement <= 2147483647L ? (int) executeBatchElement : -2;
            }
            this.batchParameters = null;
            SQLException nextException = sQLException.getNextException();
            if (nextException != null) {
                throw new JdbcBatchUpdateException(nextException, iArr);
            }
            return iArr;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // org.h2.jdbc.JdbcStatement
    public long[] executeLargeBatch() throws SQLException {
        try {
            debugCodeCall("executeLargeBatch");
            if (this.batchParameters == null) {
                this.batchParameters = new ArrayList<>();
            }
            this.batchIdentities = new MergedResult();
            int size = this.batchParameters.size();
            long[] jArr = new long[size];
            SQLException sQLException = new SQLException();
            checkClosed();
            for (int i = 0; i < size; i++) {
                jArr[i] = executeBatchElement(this.batchParameters.get(i), sQLException);
            }
            this.batchParameters = null;
            SQLException nextException = sQLException.getNextException();
            if (nextException != null) {
                throw new JdbcBatchUpdateException(nextException, jArr);
            }
            return jArr;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private long executeBatchElement(Value[] valueArr, SQLException sQLException) {
        long j;
        ArrayList<? extends ParameterInterface> parameters = this.command.getParameters();
        int length = valueArr.length;
        for (int i = 0; i < length; i++) {
            parameters.get(i).setValue(valueArr[i], false);
        }
        try {
            j = executeUpdateInternal();
            this.batchIdentities.add(((JdbcResultSet) super.getGeneratedKeys()).result);
        } catch (Exception e) {
            sQLException.setNextException(logAndConvert(e));
            j = -3;
        }
        return j;
    }

    @Override // org.h2.jdbc.JdbcStatement, java.sql.Statement
    public ResultSet getGeneratedKeys() throws SQLException {
        if (this.batchIdentities != null) {
            try {
                int nextId = getNextId(4);
                debugCodeAssign("ResultSet", 4, nextId, "getGeneratedKeys()");
                checkClosed();
                this.generatedKeys = new JdbcResultSet(this.conn, (JdbcStatement) this, (CommandInterface) null, (ResultInterface) this.batchIdentities.getResult(), nextId, true, false, false);
            } catch (Exception e) {
                throw logAndConvert(e);
            }
        }
        return super.getGeneratedKeys();
    }

    @Override // java.sql.PreparedStatement
    public void addBatch() throws SQLException {
        try {
            debugCodeCall("addBatch");
            checkClosed();
            ArrayList<? extends ParameterInterface> parameters = this.command.getParameters();
            int size = parameters.size();
            Value[] valueArr = new Value[size];
            for (int i = 0; i < size; i++) {
                ParameterInterface parameterInterface = parameters.get(i);
                parameterInterface.checkSet();
                valueArr[i] = parameterInterface.getParamValue();
            }
            if (this.batchParameters == null) {
                this.batchParameters = Utils.newSmallArrayList();
            }
            this.batchParameters.add(valueArr);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public ParameterMetaData getParameterMetaData() throws SQLException {
        try {
            int nextId = getNextId(11);
            debugCodeAssign("ParameterMetaData", 11, nextId, "getParameterMetaData()");
            checkClosed();
            return new JdbcParameterMetaData(this.session.getTrace(), this, this.command, nextId);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private void setParameter(int i, Value value) {
        checkClosed();
        int i2 = i - 1;
        ArrayList<? extends ParameterInterface> parameters = this.command.getParameters();
        if (i2 < 0 || i2 >= parameters.size()) {
            throw DbException.getInvalidValueException("parameterIndex", Integer.valueOf(i2 + 1));
        }
        parameters.get(i2).setValue(value, this.batchParameters == null);
    }

    @Override // java.sql.PreparedStatement
    public void setRowId(int i, RowId rowId) throws SQLException {
        throw unsupported("rowId");
    }

    @Override // java.sql.PreparedStatement
    public void setNString(int i, String str) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setNString(" + i + ", " + quote(str) + ')');
            }
            setParameter(i, str == null ? ValueNull.INSTANCE : ValueVarchar.get(str, this.conn));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setNCharacterStream(int i, Reader reader, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setNCharacterStream(" + i + ", x, " + j + "L)");
            }
            checkClosed();
            setParameter(i, this.conn.createClob(reader, j));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setNCharacterStream(int i, Reader reader) throws SQLException {
        setNCharacterStream(i, reader, -1L);
    }

    @Override // java.sql.PreparedStatement
    public void setNClob(int i, NClob nClob) throws SQLException {
        Value createClob;
        try {
            if (isDebugEnabled()) {
                debugCode("setNClob(" + i + ", x)");
            }
            checkClosed();
            if (nClob == null) {
                createClob = ValueNull.INSTANCE;
            } else {
                createClob = this.conn.createClob(nClob.getCharacterStream(), -1L);
            }
            setParameter(i, createClob);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setNClob(int i, Reader reader) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setNClob(" + i + ", x)");
            }
            checkClosed();
            setParameter(i, this.conn.createClob(reader, -1L));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setClob(int i, Reader reader, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setClob(" + i + ", x, " + j + "L)");
            }
            checkClosed();
            setParameter(i, this.conn.createClob(reader, j));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setBlob(int i, InputStream inputStream, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setBlob(" + i + ", x, " + j + "L)");
            }
            checkClosed();
            setParameter(i, this.conn.createBlob(inputStream, j));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setNClob(int i, Reader reader, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setNClob(" + i + ", x, " + j + "L)");
            }
            checkClosed();
            setParameter(i, this.conn.createClob(reader, j));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.PreparedStatement
    public void setSQLXML(int i, SQLXML sqlxml) throws SQLException {
        Value createClob;
        try {
            if (isDebugEnabled()) {
                debugCode("setSQLXML(" + i + ", x)");
            }
            checkClosed();
            if (sqlxml == null) {
                createClob = ValueNull.INSTANCE;
            } else {
                createClob = this.conn.createClob(sqlxml.getCharacterStream(), -1L);
            }
            setParameter(i, createClob);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // org.h2.jdbc.JdbcStatement
    public String toString() {
        return getTraceObjectName() + ": " + this.command;
    }
}
