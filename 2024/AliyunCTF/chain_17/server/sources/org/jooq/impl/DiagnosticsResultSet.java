package org.jooq.impl;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.function.Predicate;
import org.jooq.conf.Settings;
import org.jooq.tools.jdbc.DefaultResultSet;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DiagnosticsResultSet.class */
public final class DiagnosticsResultSet extends DefaultResultSet {
    final DiagnosticsConnection connection;
    final String sql;
    final ResultSetMetaData meta;
    final java.util.BitSet nullable;
    final java.util.BitSet read;
    final int columns;
    int current;
    int rows;
    int wasColumnIndex;
    boolean wasPrimitive;
    boolean wasNullable;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DiagnosticsResultSet(ResultSet delegate, String sql, Statement creator, DiagnosticsConnection connection) throws SQLException {
        super(delegate, creator);
        this.connection = connection;
        this.sql = sql;
        this.meta = delegate.getMetaData();
        this.columns = this.meta.getColumnCount();
        this.read = new java.util.BitSet(this.columns);
        this.nullable = new java.util.BitSet(this.columns);
        for (int i = 0; i < this.columns; i++) {
            this.nullable.set(i, this.meta.isNullable(i + 1) != 0);
        }
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final String getString(int columnIndex) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getString(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final boolean getBoolean(int columnIndex) throws SQLException {
        wasPrimitive(columnIndex);
        read(columnIndex);
        return super.getBoolean(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final byte getByte(int columnIndex) throws SQLException {
        wasPrimitive(columnIndex);
        read(columnIndex);
        return super.getByte(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final short getShort(int columnIndex) throws SQLException {
        wasPrimitive(columnIndex);
        read(columnIndex);
        return super.getShort(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final int getInt(int columnIndex) throws SQLException {
        wasPrimitive(columnIndex);
        read(columnIndex);
        return super.getInt(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final long getLong(int columnIndex) throws SQLException {
        wasPrimitive(columnIndex);
        read(columnIndex);
        return super.getLong(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final float getFloat(int columnIndex) throws SQLException {
        wasPrimitive(columnIndex);
        read(columnIndex);
        return super.getFloat(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final double getDouble(int columnIndex) throws SQLException {
        wasPrimitive(columnIndex);
        read(columnIndex);
        return super.getDouble(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    @Deprecated
    public final BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getBigDecimal(columnIndex, scale);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final byte[] getBytes(int columnIndex) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getBytes(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Date getDate(int columnIndex) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getDate(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Time getTime(int columnIndex) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getTime(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Timestamp getTimestamp(int columnIndex) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getTimestamp(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final InputStream getAsciiStream(int columnIndex) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getAsciiStream(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    @Deprecated
    public final InputStream getUnicodeStream(int columnIndex) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getUnicodeStream(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final InputStream getBinaryStream(int columnIndex) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getBinaryStream(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final String getString(String columnLabel) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getString(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final boolean getBoolean(String columnLabel) throws SQLException {
        wasPrimitive(columnLabel);
        read(columnLabel);
        return super.getBoolean(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final byte getByte(String columnLabel) throws SQLException {
        wasPrimitive(columnLabel);
        read(columnLabel);
        return super.getByte(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final short getShort(String columnLabel) throws SQLException {
        wasPrimitive(columnLabel);
        read(columnLabel);
        return super.getShort(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final int getInt(String columnLabel) throws SQLException {
        wasPrimitive(columnLabel);
        read(columnLabel);
        return super.getInt(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final long getLong(String columnLabel) throws SQLException {
        wasPrimitive(columnLabel);
        read(columnLabel);
        return super.getLong(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final float getFloat(String columnLabel) throws SQLException {
        wasPrimitive(columnLabel);
        read(columnLabel);
        return super.getFloat(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final double getDouble(String columnLabel) throws SQLException {
        wasPrimitive(columnLabel);
        read(columnLabel);
        return super.getDouble(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    @Deprecated
    public final BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getBigDecimal(columnLabel, scale);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final byte[] getBytes(String columnLabel) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getBytes(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Date getDate(String columnLabel) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getDate(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Time getTime(String columnLabel) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getTime(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Timestamp getTimestamp(String columnLabel) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getTimestamp(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final InputStream getAsciiStream(String columnLabel) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getAsciiStream(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    @Deprecated
    public final InputStream getUnicodeStream(String columnLabel) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getUnicodeStream(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final InputStream getBinaryStream(String columnLabel) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getBinaryStream(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Object getObject(int columnIndex) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getObject(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Object getObject(String columnLabel) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getObject(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Reader getCharacterStream(int columnIndex) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getCharacterStream(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Reader getCharacterStream(String columnLabel) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getCharacterStream(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getBigDecimal(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getBigDecimal(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getObject(columnIndex, map);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Ref getRef(int columnIndex) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getRef(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Blob getBlob(int columnIndex) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getBlob(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Clob getClob(int columnIndex) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getClob(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final java.sql.Array getArray(int columnIndex) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getArray(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getObject(columnLabel, map);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Ref getRef(String columnLabel) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getRef(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Blob getBlob(String columnLabel) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getBlob(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Clob getClob(String columnLabel) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getClob(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final java.sql.Array getArray(String columnLabel) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getArray(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Date getDate(int columnIndex, Calendar cal) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getDate(columnIndex, cal);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Date getDate(String columnLabel, Calendar cal) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getDate(columnLabel, cal);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Time getTime(int columnIndex, Calendar cal) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getTime(columnIndex, cal);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Time getTime(String columnLabel, Calendar cal) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getTime(columnLabel, cal);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getTimestamp(columnIndex, cal);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getTimestamp(columnLabel, cal);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final URL getURL(int columnIndex) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getURL(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final URL getURL(String columnLabel) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getURL(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final RowId getRowId(int columnIndex) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getRowId(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final RowId getRowId(String columnLabel) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getRowId(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final NClob getNClob(int columnIndex) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getNClob(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final NClob getNClob(String columnLabel) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getNClob(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final SQLXML getSQLXML(int columnIndex) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getSQLXML(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final SQLXML getSQLXML(String columnLabel) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getSQLXML(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final String getNString(int columnIndex) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getNString(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final String getNString(String columnLabel) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getNString(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Reader getNCharacterStream(int columnIndex) throws SQLException {
        checkPrimitive();
        read(columnIndex);
        return super.getNCharacterStream(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final Reader getNCharacterStream(String columnLabel) throws SQLException {
        checkPrimitive();
        read(columnLabel);
        return super.getNCharacterStream(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, org.jooq.tools.jdbc.JDBC41ResultSet
    public final <T> T getObject(int i, Class<T> cls) throws SQLException {
        checkPrimitive();
        read(i);
        return (T) super.getObject(i, cls);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, org.jooq.tools.jdbc.JDBC41ResultSet
    public final <T> T getObject(String str, Class<T> cls) throws SQLException {
        checkPrimitive();
        read(str);
        return (T) super.getObject(str, cls);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final boolean wasNull() throws SQLException {
        if (!this.wasPrimitive && check((v0) -> {
            return v0.isDiagnosticsUnnecessaryWasNullCall();
        })) {
            DefaultDiagnosticsContext ctx = ctx("ResultSet::wasNull was called unnecessarily.");
            ctx.resultSetUnnecessaryWasNullCall = true;
            ctx.resultSetColumnIndex = this.wasColumnIndex;
            this.connection.listeners.unnecessaryWasNullCall(ctx);
        }
        this.wasPrimitive = false;
        this.wasNullable = false;
        return super.wasNull();
    }

    private final void wasPrimitive(int columnIndex) throws SQLException {
        checkPrimitive();
        this.wasColumnIndex = columnIndex;
        this.wasPrimitive = true;
        this.wasNullable = this.nullable.get(columnIndex - 1);
    }

    private final void wasPrimitive(String columnLabel) throws SQLException {
        wasPrimitive(super.findColumn(columnLabel));
    }

    private final void checkPrimitive() throws SQLException {
        if (this.wasPrimitive && this.wasNullable && check((v0) -> {
            return v0.isDiagnosticsMissingWasNullCall();
        })) {
            DefaultDiagnosticsContext ctx = ctx("ResultSet::wasNull was not called.");
            ctx.resultSetMissingWasNullCall = true;
            ctx.resultSetColumnIndex = this.wasColumnIndex;
            this.connection.listeners.missingWasNullCall(ctx);
        }
        this.wasPrimitive = false;
        this.wasNullable = false;
    }

    private final void read(int columnIndex) {
        this.read.set(columnIndex - 1);
    }

    private final void read(String columnLabel) throws SQLException {
        read(super.findColumn(columnLabel));
    }

    private final boolean check(Predicate<? super Settings> test) {
        return this.connection.check(test);
    }

    private final DefaultDiagnosticsContext ctx(String message) throws SQLException {
        DefaultDiagnosticsContext ctx = new DefaultDiagnosticsContext(this.connection.configuration, message, this.sql);
        ctx.resultSet = super.getDelegate();
        ctx.resultSetWrapper = this;
        ctx.resultSetConsumedColumnCount = this.read.cardinality();
        ctx.resultSetFetchedColumnCount = this.columns;
        ctx.resultSetConsumedRows = this.current;
        ctx.resultSetFetchedRows = this.current + 1;
        return ctx;
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final void beforeFirst() throws SQLException {
        checkPrimitive();
        super.beforeFirst();
        moveAbsolute(true, super.getRow());
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final void afterLast() throws SQLException {
        checkPrimitive();
        super.afterLast();
        moveAbsolute(true, super.getRow());
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final boolean first() throws SQLException {
        checkPrimitive();
        return moveAbsolute(super.first(), super.getRow());
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final boolean last() throws SQLException {
        checkPrimitive();
        return moveAbsolute(super.last(), super.getRow());
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final boolean absolute(int row) throws SQLException {
        checkPrimitive();
        return moveAbsolute(super.absolute(row), super.getRow());
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final boolean relative(int relative) throws SQLException {
        checkPrimitive();
        return moveRelative(super.relative(relative), relative);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final boolean next() throws SQLException {
        checkPrimitive();
        return moveRelative(super.next(), 1);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public final boolean previous() throws SQLException {
        checkPrimitive();
        return moveRelative(super.previous(), -1);
    }

    private final boolean moveRelative(boolean success, int relative) {
        if (success) {
            this.current += relative;
            this.rows = Math.max(this.rows, this.current);
        }
        return success;
    }

    private final boolean moveAbsolute(boolean success, int absolute) {
        if (success) {
            this.current = absolute;
            this.rows = Math.max(this.rows, this.current);
        }
        return success;
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public boolean isBeforeFirst() throws SQLException {
        checkPrimitive();
        return super.isBeforeFirst();
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public boolean isAfterLast() throws SQLException {
        checkPrimitive();
        return super.isAfterLast();
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public boolean isFirst() throws SQLException {
        checkPrimitive();
        return super.isFirst();
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public boolean isLast() throws SQLException {
        checkPrimitive();
        return super.isLast();
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet, java.lang.AutoCloseable
    public final void close() throws SQLException {
        checkPrimitive();
        try {
            if (check((v0) -> {
                return v0.isDiagnosticsTooManyRowsFetched();
            })) {
                if (this.current < this.rows) {
                    int i = this.rows;
                    this.current = i;
                    super.absolute(i);
                }
                DefaultDiagnosticsContext c1 = ctx("Too many rows fetched");
                c1.resultSetClosing = true;
                if (super.next()) {
                    this.connection.listeners.tooManyRowsFetched(c1);
                }
            }
            if (check((v0) -> {
                return v0.isDiagnosticsTooManyColumnsFetched();
            })) {
                DefaultDiagnosticsContext c2 = ctx("Too many columns fetched");
                c2.resultSetClosing = true;
                if (this.read.cardinality() != this.columns) {
                    this.connection.listeners.tooManyColumnsFetched(c2);
                }
            }
        } catch (SQLException e) {
        }
        super.close();
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void setFetchDirection(int direction) throws SQLException {
        checkPrimitive();
        super.setFetchDirection(direction);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void setFetchSize(int rows) throws SQLException {
        checkPrimitive();
        super.setFetchSize(rows);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public boolean rowUpdated() throws SQLException {
        checkPrimitive();
        return super.rowUpdated();
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public boolean rowInserted() throws SQLException {
        checkPrimitive();
        return super.rowInserted();
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public boolean rowDeleted() throws SQLException {
        checkPrimitive();
        return super.rowDeleted();
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateNull(int columnIndex) throws SQLException {
        checkPrimitive();
        super.updateNull(columnIndex);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        checkPrimitive();
        super.updateBoolean(columnIndex, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateByte(int columnIndex, byte x) throws SQLException {
        checkPrimitive();
        super.updateByte(columnIndex, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateShort(int columnIndex, short x) throws SQLException {
        checkPrimitive();
        super.updateShort(columnIndex, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateInt(int columnIndex, int x) throws SQLException {
        checkPrimitive();
        super.updateInt(columnIndex, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateLong(int columnIndex, long x) throws SQLException {
        checkPrimitive();
        super.updateLong(columnIndex, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateFloat(int columnIndex, float x) throws SQLException {
        checkPrimitive();
        super.updateFloat(columnIndex, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateDouble(int columnIndex, double x) throws SQLException {
        checkPrimitive();
        super.updateDouble(columnIndex, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        checkPrimitive();
        super.updateBigDecimal(columnIndex, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateString(int columnIndex, String x) throws SQLException {
        checkPrimitive();
        super.updateString(columnIndex, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        checkPrimitive();
        super.updateBytes(columnIndex, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateDate(int columnIndex, Date x) throws SQLException {
        checkPrimitive();
        super.updateDate(columnIndex, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateTime(int columnIndex, Time x) throws SQLException {
        checkPrimitive();
        super.updateTime(columnIndex, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        checkPrimitive();
        super.updateTimestamp(columnIndex, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        checkPrimitive();
        super.updateAsciiStream(columnIndex, x, length);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        checkPrimitive();
        super.updateBinaryStream(columnIndex, x, length);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        checkPrimitive();
        super.updateCharacterStream(columnIndex, x, length);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        checkPrimitive();
        super.updateObject(columnIndex, x, scaleOrLength);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateObject(int columnIndex, Object x) throws SQLException {
        checkPrimitive();
        super.updateObject(columnIndex, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateNull(String columnLabel) throws SQLException {
        checkPrimitive();
        super.updateNull(columnLabel);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        checkPrimitive();
        super.updateBoolean(columnLabel, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateByte(String columnLabel, byte x) throws SQLException {
        checkPrimitive();
        super.updateByte(columnLabel, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateShort(String columnLabel, short x) throws SQLException {
        checkPrimitive();
        super.updateShort(columnLabel, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateInt(String columnLabel, int x) throws SQLException {
        checkPrimitive();
        super.updateInt(columnLabel, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateLong(String columnLabel, long x) throws SQLException {
        checkPrimitive();
        super.updateLong(columnLabel, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateFloat(String columnLabel, float x) throws SQLException {
        checkPrimitive();
        super.updateFloat(columnLabel, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateDouble(String columnLabel, double x) throws SQLException {
        checkPrimitive();
        super.updateDouble(columnLabel, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        checkPrimitive();
        super.updateBigDecimal(columnLabel, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateString(String columnLabel, String x) throws SQLException {
        checkPrimitive();
        super.updateString(columnLabel, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        checkPrimitive();
        super.updateBytes(columnLabel, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateDate(String columnLabel, Date x) throws SQLException {
        checkPrimitive();
        super.updateDate(columnLabel, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateTime(String columnLabel, Time x) throws SQLException {
        checkPrimitive();
        super.updateTime(columnLabel, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        checkPrimitive();
        super.updateTimestamp(columnLabel, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        checkPrimitive();
        super.updateAsciiStream(columnLabel, x, length);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        checkPrimitive();
        super.updateBinaryStream(columnLabel, x, length);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        checkPrimitive();
        super.updateCharacterStream(columnLabel, reader, length);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        checkPrimitive();
        super.updateObject(columnLabel, x, scaleOrLength);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateObject(String columnLabel, Object x) throws SQLException {
        checkPrimitive();
        super.updateObject(columnLabel, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void insertRow() throws SQLException {
        checkPrimitive();
        super.insertRow();
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateRow() throws SQLException {
        checkPrimitive();
        super.updateRow();
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void deleteRow() throws SQLException {
        checkPrimitive();
        super.deleteRow();
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void refreshRow() throws SQLException {
        checkPrimitive();
        super.refreshRow();
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        checkPrimitive();
        super.updateRef(columnIndex, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateRef(String columnLabel, Ref x) throws SQLException {
        checkPrimitive();
        super.updateRef(columnLabel, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        checkPrimitive();
        super.updateBlob(columnIndex, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        checkPrimitive();
        super.updateBlob(columnLabel, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        checkPrimitive();
        super.updateClob(columnIndex, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateClob(String columnLabel, Clob x) throws SQLException {
        checkPrimitive();
        super.updateClob(columnLabel, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateArray(int columnIndex, java.sql.Array x) throws SQLException {
        checkPrimitive();
        super.updateArray(columnIndex, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateArray(String columnLabel, java.sql.Array x) throws SQLException {
        checkPrimitive();
        super.updateArray(columnLabel, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        checkPrimitive();
        super.updateRowId(columnIndex, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        checkPrimitive();
        super.updateRowId(columnLabel, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public boolean isClosed() throws SQLException {
        checkPrimitive();
        return super.isClosed();
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateNString(int columnIndex, String nString) throws SQLException {
        checkPrimitive();
        super.updateNString(columnIndex, nString);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateNString(String columnLabel, String nString) throws SQLException {
        checkPrimitive();
        super.updateNString(columnLabel, nString);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        checkPrimitive();
        super.updateNClob(columnIndex, nClob);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        checkPrimitive();
        super.updateNClob(columnLabel, nClob);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        checkPrimitive();
        super.updateSQLXML(columnIndex, xmlObject);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        checkPrimitive();
        super.updateSQLXML(columnLabel, xmlObject);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        checkPrimitive();
        super.updateNCharacterStream(columnIndex, x, length);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        checkPrimitive();
        super.updateNCharacterStream(columnLabel, reader, length);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        checkPrimitive();
        super.updateAsciiStream(columnIndex, x, length);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        checkPrimitive();
        super.updateBinaryStream(columnIndex, x, length);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        checkPrimitive();
        super.updateCharacterStream(columnIndex, x, length);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        checkPrimitive();
        super.updateAsciiStream(columnLabel, x, length);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        checkPrimitive();
        super.updateBinaryStream(columnLabel, x, length);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        checkPrimitive();
        super.updateCharacterStream(columnLabel, reader, length);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        checkPrimitive();
        super.updateBlob(columnIndex, inputStream, length);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        checkPrimitive();
        super.updateBlob(columnLabel, inputStream, length);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        checkPrimitive();
        super.updateClob(columnIndex, reader, length);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        checkPrimitive();
        super.updateClob(columnLabel, reader, length);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        checkPrimitive();
        super.updateNClob(columnIndex, reader, length);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        checkPrimitive();
        super.updateNClob(columnLabel, reader, length);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        checkPrimitive();
        super.updateNCharacterStream(columnIndex, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        checkPrimitive();
        super.updateNCharacterStream(columnLabel, reader);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        checkPrimitive();
        super.updateAsciiStream(columnIndex, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        checkPrimitive();
        super.updateBinaryStream(columnIndex, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        checkPrimitive();
        super.updateCharacterStream(columnIndex, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        checkPrimitive();
        super.updateAsciiStream(columnLabel, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        checkPrimitive();
        super.updateBinaryStream(columnLabel, x);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        checkPrimitive();
        super.updateCharacterStream(columnLabel, reader);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        checkPrimitive();
        super.updateBlob(columnIndex, inputStream);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        checkPrimitive();
        super.updateBlob(columnLabel, inputStream);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        checkPrimitive();
        super.updateClob(columnIndex, reader);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        checkPrimitive();
        super.updateClob(columnLabel, reader);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        checkPrimitive();
        super.updateNClob(columnIndex, reader);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        checkPrimitive();
        super.updateNClob(columnLabel, reader);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet
    public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        checkPrimitive();
        super.updateObject(columnIndex, x, targetSqlType, scaleOrLength);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet
    public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        checkPrimitive();
        super.updateObject(columnLabel, x, targetSqlType, scaleOrLength);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet
    public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
        checkPrimitive();
        super.updateObject(columnIndex, x, targetSqlType);
    }

    @Override // org.jooq.tools.jdbc.DefaultResultSet
    public void updateObject(String columnLabel, Object x, SQLType targetSqlType) throws SQLException {
        checkPrimitive();
        super.updateObject(columnLabel, x, targetSqlType);
    }
}
