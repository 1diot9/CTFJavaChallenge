package org.jooq.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Param;
import org.jooq.Source;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ParsingStatement.class */
final class ParsingStatement implements CallableStatement {
    private final ParsingConnection connection;
    private final Statement statement;
    private final ThrowingFunction<List<List<Param<?>>>, PreparedStatement, SQLException> prepared;
    private final List<ThrowingConsumer<Statement, SQLException>> flags;
    private final List<Param<?>> binds;
    private final List<List<Param<?>>> batch;
    private PreparedStatement last;
    private PreparedStatement lastOrDummy;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ParsingStatement(ParsingConnection connection, Statement statement) {
        this.connection = connection;
        this.statement = statement;
        this.prepared = null;
        this.flags = null;
        this.binds = null;
        this.batch = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ParsingStatement(ParsingConnection connection, ThrowingFunction<List<List<Param<?>>>, PreparedStatement, SQLException> prepared) {
        this.connection = connection;
        this.statement = null;
        this.prepared = prepared;
        this.flags = new ArrayList();
        this.binds = new ArrayList();
        this.batch = new ArrayList();
    }

    private final List<Param<?>> bindValues(int index) {
        int size = this.binds.size();
        int reserve = index - size;
        if (reserve > 0) {
            this.binds.addAll(Collections.nCopies(reserve, null));
        }
        return this.binds;
    }

    private final SQLException closed() {
        return new SQLException("Statement is closed or not yet prepared.");
    }

    @NotNull
    private final Statement statement() throws SQLException {
        Statement result = statement0();
        if (result != null) {
            return result;
        }
        throw closed();
    }

    @Nullable
    private final Statement statement0() {
        if (this.last != null) {
            return this.last;
        }
        if (this.lastOrDummy != null) {
            return this.lastOrDummy;
        }
        if (this.statement != null) {
            return this.statement;
        }
        return null;
    }

    private final void setFlag(ThrowingConsumer<Statement, SQLException> set) throws SQLException {
        if (this.statement != null) {
            set.accept(this.statement);
        } else {
            this.flags.add(set);
        }
    }

    @Override // java.sql.Statement
    public final void setPoolable(boolean poolable) throws SQLException {
        setFlag(s -> {
            s.setPoolable(poolable);
        });
    }

    @Override // java.sql.Statement
    public final boolean isPoolable() throws SQLException {
        return statement().isPoolable();
    }

    @Override // java.sql.Statement
    public final void setFetchDirection(int direction) throws SQLException {
        setFlag(s -> {
            s.setFetchDirection(direction);
        });
    }

    @Override // java.sql.Statement
    public final int getFetchDirection() throws SQLException {
        return statement().getFetchDirection();
    }

    @Override // java.sql.Statement
    public final void setFetchSize(int rows) throws SQLException {
        setFlag(s -> {
            s.setFetchSize(rows);
        });
    }

    @Override // java.sql.Statement
    public final int getFetchSize() throws SQLException {
        return statement().getFetchSize();
    }

    @Override // java.sql.Statement
    public final void setMaxFieldSize(int max) throws SQLException {
        setFlag(s -> {
            s.setMaxFieldSize(max);
        });
    }

    @Override // java.sql.Statement
    public final int getMaxFieldSize() throws SQLException {
        return statement().getMaxFieldSize();
    }

    @Override // java.sql.Statement
    public final void setMaxRows(int max) throws SQLException {
        setFlag(s -> {
            s.setMaxRows(max);
        });
    }

    @Override // java.sql.Statement
    public final int getMaxRows() throws SQLException {
        return statement().getMaxRows();
    }

    public final void setLargeMaxRows(long max) throws SQLException {
        setFlag(s -> {
            s.setLargeMaxRows(max);
        });
    }

    public final long getLargeMaxRows() throws SQLException {
        return statement().getLargeMaxRows();
    }

    @Override // java.sql.Statement
    public final void setQueryTimeout(int seconds) throws SQLException {
        setFlag(s -> {
            s.setQueryTimeout(seconds);
        });
    }

    @Override // java.sql.Statement
    public final int getQueryTimeout() throws SQLException {
        return statement().getQueryTimeout();
    }

    @Override // java.sql.Statement
    public final void setEscapeProcessing(boolean enable) throws SQLException {
        setFlag(s -> {
            s.setEscapeProcessing(enable);
        });
    }

    @Override // java.sql.Statement
    public final void setCursorName(String name) throws SQLException {
        setFlag(s -> {
            s.setCursorName(name);
        });
    }

    @Override // java.sql.Statement
    public final int getResultSetConcurrency() throws SQLException {
        return statement().getResultSetConcurrency();
    }

    @Override // java.sql.Statement
    public final int getResultSetType() throws SQLException {
        return statement().getResultSetType();
    }

    @Override // java.sql.Statement
    public final int getResultSetHoldability() throws SQLException {
        return statement().getResultSetHoldability();
    }

    @Override // java.sql.Statement
    public final ResultSet executeQuery(String sql) throws SQLException {
        return this.statement.executeQuery(ParsingConnection.translate(this.connection.configuration, sql, new Param[0]).sql);
    }

    @Override // java.sql.Statement
    public final int executeUpdate(String sql) throws SQLException {
        return this.statement.executeUpdate(ParsingConnection.translate(this.connection.configuration, sql, new Param[0]).sql);
    }

    @Override // java.sql.Statement
    public final int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return this.statement.executeUpdate(ParsingConnection.translate(this.connection.configuration, sql, new Param[0]).sql, autoGeneratedKeys);
    }

    @Override // java.sql.Statement
    public final int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return this.statement.executeUpdate(ParsingConnection.translate(this.connection.configuration, sql, new Param[0]).sql, columnIndexes);
    }

    @Override // java.sql.Statement
    public final int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return this.statement.executeUpdate(ParsingConnection.translate(this.connection.configuration, sql, new Param[0]).sql, columnNames);
    }

    @Override // java.sql.Statement
    public final boolean execute(String sql) throws SQLException {
        return this.statement.execute(ParsingConnection.translate(this.connection.configuration, sql, new Param[0]).sql);
    }

    @Override // java.sql.Statement
    public final boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return this.statement.execute(ParsingConnection.translate(this.connection.configuration, sql, new Param[0]).sql, autoGeneratedKeys);
    }

    @Override // java.sql.Statement
    public final boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return this.statement.execute(ParsingConnection.translate(this.connection.configuration, sql, new Param[0]).sql, columnIndexes);
    }

    @Override // java.sql.Statement
    public final boolean execute(String sql, String[] columnNames) throws SQLException {
        return this.statement.execute(ParsingConnection.translate(this.connection.configuration, sql, new Param[0]).sql, columnNames);
    }

    public final long executeLargeUpdate(String sql) throws SQLException {
        return this.statement.executeLargeUpdate(ParsingConnection.translate(this.connection.configuration, sql, new Param[0]).sql);
    }

    public final long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return this.statement.executeLargeUpdate(ParsingConnection.translate(this.connection.configuration, sql, new Param[0]).sql, autoGeneratedKeys);
    }

    public final long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return this.statement.executeLargeUpdate(ParsingConnection.translate(this.connection.configuration, sql, new Param[0]).sql, columnIndexes);
    }

    public final long executeLargeUpdate(String sql, String[] columnNames) throws SQLException {
        return this.statement.executeLargeUpdate(ParsingConnection.translate(this.connection.configuration, sql, new Param[0]).sql, columnNames);
    }

    @Override // java.sql.Statement
    public final void addBatch(String sql) throws SQLException {
        this.statement.addBatch(ParsingConnection.translate(this.connection.configuration, sql, new Param[0]).sql);
    }

    @Override // java.sql.Statement
    public final Connection getConnection() throws SQLException {
        return this.connection;
    }

    private final Statement last() throws SQLException {
        if (this.last != null) {
            return this.last;
        }
        if (this.statement != null) {
            return this.statement;
        }
        throw new SQLException("No PreparedStatement is available yet");
    }

    private final PreparedStatement lastOrDummy() throws SQLException {
        if (this.last != null) {
            return this.last;
        }
        if (this.lastOrDummy != null) {
            return this.lastOrDummy;
        }
        PreparedStatement prepareAndBind0 = prepareAndBind0();
        this.lastOrDummy = prepareAndBind0;
        return prepareAndBind0;
    }

    private final PreparedStatement prepareAndBind() throws SQLException {
        if (this.lastOrDummy != null) {
            this.lastOrDummy.close();
            this.lastOrDummy = null;
        }
        PreparedStatement prepareAndBind0 = prepareAndBind0();
        this.last = prepareAndBind0;
        return prepareAndBind0;
    }

    private final PreparedStatement prepareAndBind0() throws SQLException {
        PreparedStatement result = this.prepared.apply(this.batch.isEmpty() ? Collections.singletonList(this.binds) : this.batch);
        for (ThrowingConsumer<Statement, SQLException> flag : this.flags) {
            flag.accept(result);
        }
        return result;
    }

    @Override // java.sql.PreparedStatement
    public final ResultSet executeQuery() throws SQLException {
        return prepareAndBind().executeQuery();
    }

    @Override // java.sql.PreparedStatement
    public final int executeUpdate() throws SQLException {
        return prepareAndBind().executeUpdate();
    }

    @Override // java.sql.PreparedStatement
    public final boolean execute() throws SQLException {
        return prepareAndBind().execute();
    }

    public final long executeLargeUpdate() throws SQLException {
        return prepareAndBind().executeLargeUpdate();
    }

    @Override // java.sql.PreparedStatement
    public final void addBatch() throws SQLException {
        this.batch.add(new ArrayList(this.binds));
    }

    @Override // java.sql.Statement
    public final void clearBatch() throws SQLException {
        statement().clearBatch();
        if (this.batch != null) {
            this.batch.clear();
        }
    }

    @Override // java.sql.Statement
    public final int[] executeBatch() throws SQLException {
        if (this.statement != null) {
            return this.statement.executeBatch();
        }
        return prepareAndBind().executeBatch();
    }

    public final long[] executeLargeBatch() throws SQLException {
        if (this.statement != null) {
            return this.statement.executeLargeBatch();
        }
        return prepareAndBind().executeLargeBatch();
    }

    @Override // java.sql.Statement, java.lang.AutoCloseable
    public final void close() throws SQLException {
        Statement s = statement0();
        if (s != null) {
            s.close();
        }
    }

    @Override // java.sql.Statement
    public final boolean isClosed() throws SQLException {
        Statement s = statement0();
        if (s != null) {
            return s.isClosed();
        }
        return true;
    }

    @Override // java.sql.Statement
    public final void cancel() throws SQLException {
        statement().cancel();
    }

    @Override // java.sql.Statement
    public final ResultSet getResultSet() throws SQLException {
        return last().getResultSet();
    }

    @Override // java.sql.PreparedStatement
    public final ResultSetMetaData getMetaData() throws SQLException {
        return lastOrDummy().getMetaData();
    }

    @Override // java.sql.Statement
    public final int getUpdateCount() throws SQLException {
        return last().getUpdateCount();
    }

    public final long getLargeUpdateCount() throws SQLException {
        return last().getLargeUpdateCount();
    }

    @Override // java.sql.Statement
    public final boolean getMoreResults() throws SQLException {
        return last().getMoreResults();
    }

    @Override // java.sql.Statement
    public final boolean getMoreResults(int current) throws SQLException {
        return last().getMoreResults(current);
    }

    @Override // java.sql.Statement
    public final ResultSet getGeneratedKeys() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.PreparedStatement
    public final void clearParameters() throws SQLException {
        this.binds.clear();
    }

    private final void set(int parameterIndex, Supplier<Param<?>> supplier) {
        bindValues(parameterIndex).set(parameterIndex - 1, supplier.get());
    }

    @Override // java.sql.PreparedStatement
    public final void setNull(int parameterIndex, int sqlType) throws SQLException {
        set(parameterIndex, () -> {
            return DSL.val((Object) null, DefaultDataType.getDataType(this.connection.configuration.dialect(), sqlType));
        });
    }

    @Override // java.sql.PreparedStatement
    public final void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        set(parameterIndex, () -> {
            return DSL.val((Object) null, DefaultDataType.getDataType(this.connection.configuration.dialect(), sqlType));
        });
    }

    @Override // java.sql.PreparedStatement
    public final void setBoolean(int parameterIndex, boolean x) throws SQLException {
        set(parameterIndex, () -> {
            return DSL.val(x);
        });
    }

    @Override // java.sql.PreparedStatement
    public final void setByte(int parameterIndex, byte x) throws SQLException {
        set(parameterIndex, () -> {
            return DSL.val(x);
        });
    }

    @Override // java.sql.PreparedStatement
    public final void setShort(int parameterIndex, short x) throws SQLException {
        set(parameterIndex, () -> {
            return DSL.val(x);
        });
    }

    @Override // java.sql.PreparedStatement
    public final void setInt(int parameterIndex, int x) throws SQLException {
        set(parameterIndex, () -> {
            return DSL.val(x);
        });
    }

    @Override // java.sql.PreparedStatement
    public final void setLong(int parameterIndex, long x) throws SQLException {
        set(parameterIndex, () -> {
            return DSL.val(x);
        });
    }

    @Override // java.sql.PreparedStatement
    public final void setFloat(int parameterIndex, float x) throws SQLException {
        set(parameterIndex, () -> {
            return DSL.val(x);
        });
    }

    @Override // java.sql.PreparedStatement
    public final void setDouble(int parameterIndex, double x) throws SQLException {
        set(parameterIndex, () -> {
            return DSL.val(x);
        });
    }

    @Override // java.sql.PreparedStatement
    public final void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        set(parameterIndex, () -> {
            return DSL.val(x);
        });
    }

    @Override // java.sql.PreparedStatement
    public final void setString(int parameterIndex, String x) throws SQLException {
        set(parameterIndex, () -> {
            return DSL.val(x);
        });
    }

    @Override // java.sql.PreparedStatement
    public final void setBytes(int parameterIndex, byte[] x) throws SQLException {
        set(parameterIndex, () -> {
            return DSL.val(x);
        });
    }

    @Override // java.sql.PreparedStatement
    public final void setDate(int parameterIndex, Date x) throws SQLException {
        set(parameterIndex, () -> {
            return DSL.val(x);
        });
    }

    @Override // java.sql.PreparedStatement
    public final void setTime(int parameterIndex, Time x) throws SQLException {
        set(parameterIndex, () -> {
            return DSL.val(x);
        });
    }

    @Override // java.sql.PreparedStatement
    public final void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        set(parameterIndex, () -> {
            return DSL.val(x);
        });
    }

    @Override // java.sql.PreparedStatement
    public final void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        set(parameterIndex, () -> {
            return DSL.val(x, DefaultDataType.getDataType(this.connection.configuration.dialect(), targetSqlType));
        });
    }

    @Override // java.sql.PreparedStatement
    public final void setObject(int parameterIndex, Object x) throws SQLException {
        set(parameterIndex, () -> {
            return DSL.val(x);
        });
    }

    @Override // java.sql.PreparedStatement
    public final void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        set(parameterIndex, () -> {
            return DSL.val(x, DefaultDataType.getDataType(this.connection.configuration.dialect(), targetSqlType));
        });
    }

    public final void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        set(parameterIndex, () -> {
            return DSL.val(x, DefaultDataType.getDataType(this.connection.configuration.dialect(), targetSqlType));
        });
    }

    public final void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
        set(parameterIndex, () -> {
            return DSL.val(x, DefaultDataType.getDataType(this.connection.configuration.dialect(), targetSqlType));
        });
    }

    @Override // java.sql.PreparedStatement
    public final void setNString(int parameterIndex, String value) throws SQLException {
        set(parameterIndex, () -> {
            return DSL.val(value, SQLDataType.NVARCHAR);
        });
    }

    private static final byte[] readBytes(InputStream x, int length) {
        try {
            return x.readNBytes(length);
        } catch (IOException e) {
            throw new org.jooq.exception.IOException("Could not read source", e);
        }
    }

    @Override // java.sql.PreparedStatement
    public final void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        setString(parameterIndex, Source.of(x, Charset.forName("US-ASCII")).readString());
    }

    @Override // java.sql.PreparedStatement
    public final void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        setString(parameterIndex, Source.of(x, length, Charset.forName("US-ASCII")).readString());
    }

    @Override // java.sql.PreparedStatement
    public final void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        setAsciiStream(parameterIndex, x, Tools.asInt(length));
    }

    @Override // java.sql.PreparedStatement
    public final void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        setString(parameterIndex, Source.of(x, length, Charset.forName("UTF-8")).readString());
    }

    @Override // java.sql.PreparedStatement
    public final void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        setString(parameterIndex, Source.of(reader).readString());
    }

    @Override // java.sql.PreparedStatement
    public final void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        setString(parameterIndex, Source.of(reader, length).readString());
    }

    @Override // java.sql.PreparedStatement
    public final void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        setCharacterStream(parameterIndex, reader, Tools.asInt(length));
    }

    @Override // java.sql.PreparedStatement
    public final void setClob(int parameterIndex, Reader reader) throws SQLException {
        setCharacterStream(parameterIndex, reader);
    }

    @Override // java.sql.PreparedStatement
    public final void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        setCharacterStream(parameterIndex, reader, length);
    }

    @Override // java.sql.PreparedStatement
    public final void setClob(int parameterIndex, Clob x) throws SQLException {
        setCharacterStream(parameterIndex, x.getCharacterStream(), Tools.asInt(x.length()));
    }

    @Override // java.sql.PreparedStatement
    public final void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        setNString(parameterIndex, Source.of(value).readString());
    }

    @Override // java.sql.PreparedStatement
    public final void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        setNString(parameterIndex, Source.of(value, Tools.asInt(length)).readString());
    }

    @Override // java.sql.PreparedStatement
    public final void setNClob(int parameterIndex, Reader reader) throws SQLException {
        setNCharacterStream(parameterIndex, reader);
    }

    @Override // java.sql.PreparedStatement
    public final void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        setNCharacterStream(parameterIndex, reader, length);
    }

    @Override // java.sql.PreparedStatement
    public final void setNClob(int parameterIndex, NClob value) throws SQLException {
        setNClob(parameterIndex, value.getCharacterStream(), value.length());
    }

    @Override // java.sql.PreparedStatement
    public final void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        setBinaryStream(parameterIndex, x, Integer.MAX_VALUE);
    }

    @Override // java.sql.PreparedStatement
    public final void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        setBytes(parameterIndex, readBytes(x, length));
    }

    @Override // java.sql.PreparedStatement
    public final void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        setBinaryStream(parameterIndex, x, Tools.asInt(length));
    }

    @Override // java.sql.PreparedStatement
    public final void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        setBinaryStream(parameterIndex, inputStream);
    }

    @Override // java.sql.PreparedStatement
    public final void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        setBinaryStream(parameterIndex, inputStream, length);
    }

    @Override // java.sql.PreparedStatement
    public final void setBlob(int parameterIndex, Blob x) throws SQLException {
        setBlob(parameterIndex, x.getBinaryStream(), x.length());
    }

    @Override // java.sql.PreparedStatement
    public final void setRef(int parameterIndex, Ref x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.PreparedStatement
    public final void setArray(int parameterIndex, java.sql.Array x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.PreparedStatement
    public final void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.PreparedStatement
    public final void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.PreparedStatement
    public final void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.PreparedStatement
    public final void setURL(int parameterIndex, URL x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.PreparedStatement
    public final void setRowId(int parameterIndex, RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.PreparedStatement
    public final void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setURL(String parameterName, URL val) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setNull(String parameterName, int sqlType) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setBoolean(String parameterName, boolean x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setByte(String parameterName, byte x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setShort(String parameterName, short x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setInt(String parameterName, int x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setLong(String parameterName, long x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setFloat(String parameterName, float x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setDouble(String parameterName, double x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setString(String parameterName, String x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setBytes(String parameterName, byte[] x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setDate(String parameterName, Date x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setTime(String parameterName, Time x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setTimestamp(String parameterName, Timestamp x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setObject(String parameterName, Object x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setRowId(String parameterName, RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setNString(String parameterName, String value) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setNClob(String parameterName, NClob value) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setClob(String parameterName, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setNClob(String parameterName, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setBlob(String parameterName, Blob x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setClob(String parameterName, Clob x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setAsciiStream(String parameterName, InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setBinaryStream(String parameterName, InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setCharacterStream(String parameterName, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setNCharacterStream(String parameterName, Reader value) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setClob(String parameterName, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setBlob(String parameterName, InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void setNClob(String parameterName, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public final void setObject(String parameterName, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public final void setObject(String parameterName, Object x, SQLType targetSqlType) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final boolean wasNull() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final String getString(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final boolean getBoolean(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final byte getByte(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final short getShort(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final int getInt(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final long getLong(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final float getFloat(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final double getDouble(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final byte[] getBytes(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Date getDate(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Time getTime(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Timestamp getTimestamp(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Object getObject(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Ref getRef(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Blob getBlob(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Clob getClob(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final java.sql.Array getArray(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final URL getURL(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final String getString(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final boolean getBoolean(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final byte getByte(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final short getShort(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final int getInt(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final long getLong(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final float getFloat(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final double getDouble(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final byte[] getBytes(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Date getDate(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Time getTime(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Timestamp getTimestamp(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Object getObject(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final BigDecimal getBigDecimal(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Ref getRef(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Blob getBlob(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Clob getClob(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final java.sql.Array getArray(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Date getDate(String parameterName, Calendar cal) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Time getTime(String parameterName, Calendar cal) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final URL getURL(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final RowId getRowId(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final RowId getRowId(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final NClob getNClob(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final NClob getNClob(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final SQLXML getSQLXML(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final SQLXML getSQLXML(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final String getNString(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final String getNString(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Reader getNCharacterStream(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Reader getNCharacterStream(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Reader getCharacterStream(int parameterIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.CallableStatement
    public final Reader getCharacterStream(String parameterName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public final <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public final <T> T getObject(String parameterName, Class<T> type) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public final void registerOutParameter(int parameterIndex, SQLType sqlType) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public final void registerOutParameter(int parameterIndex, SQLType sqlType, int scale) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public final void registerOutParameter(int parameterIndex, SQLType sqlType, String typeName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public final void registerOutParameter(String parameterName, SQLType sqlType) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public final void registerOutParameter(String parameterName, SQLType sqlType, int scale) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public final void registerOutParameter(String parameterName, SQLType sqlType, String typeName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.PreparedStatement
    public final ParameterMetaData getParameterMetaData() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.Statement
    public final SQLWarning getWarnings() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.Statement
    public final void clearWarnings() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public final void closeOnCompletion() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public final boolean isCloseOnCompletion() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.Wrapper
    public final <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override // java.sql.Wrapper
    public final boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}
