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
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import org.jooq.Attachable;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.exception.ControlFlowSignal;
import org.jooq.impl.Tools;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.jdbc.JDBC41ResultSet;
import org.jooq.tools.jdbc.JDBCUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CursorImpl.class */
public final class CursorImpl<R extends Record> extends AbstractCursor<R> {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) CursorImpl.class);
    final ExecuteContext ctx;
    final ExecuteListener listener;
    private final boolean[] intern;
    private final boolean keepResultSet;
    private final boolean keepStatement;
    private final boolean autoclosing;
    private final int maxRows;
    private final Supplier<? extends R> factory;
    private boolean isClosed;
    private transient CursorImpl<R>.CursorResultSet rs;
    private transient Iterator<R> iterator;
    private transient int rows;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CursorImpl(ExecuteContext ctx, ExecuteListener listener, Field<?>[] fields, int[] internIndexes, boolean keepStatement, boolean keepResultSet) {
        this(ctx, listener, fields, internIndexes, keepStatement, keepResultSet, RecordImplN.class, 0, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CursorImpl(ExecuteContext ctx, ExecuteListener listener, Field<?>[] fields, int[] internIndexes, boolean keepStatement, boolean keepResultSet, Class<? extends R> type, int maxRows, boolean autoclosing) {
        super(ctx.configuration(), Tools.row0(fields));
        this.ctx = ctx;
        this.listener = listener != null ? listener : ExecuteListeners.getAndStart(ctx);
        this.factory = Tools.recordFactory(type, this.fields);
        this.keepStatement = keepStatement;
        this.keepResultSet = keepResultSet;
        this.rs = new CursorResultSet();
        this.maxRows = maxRows;
        this.autoclosing = autoclosing;
        if (internIndexes != null) {
            this.intern = new boolean[fields.length];
            for (int i : internIndexes) {
                this.intern[i] = true;
            }
            return;
        }
        this.intern = null;
    }

    @Override // org.jooq.impl.AbstractFormattable
    final List<? extends Attachable> getAttachables() {
        return Collections.emptyList();
    }

    @Override // java.lang.Iterable
    public final Iterator<R> iterator() {
        if (this.iterator == null) {
            this.iterator = new CursorIterator();
            this.listener.fetchStart(this.ctx);
        }
        return this.iterator;
    }

    @Override // org.jooq.Cursor
    public final Result<R> fetchNext(int number) {
        iterator();
        ResultImpl<R> result = new ResultImpl<>(((DefaultExecuteContext) this.ctx).originalConfiguration(), this.fields);
        this.ctx.result(result);
        this.listener.resultStart(this.ctx);
        for (int i = 0; i < number && iterator().hasNext(); i++) {
            result.addRecord(iterator().next());
        }
        this.ctx.result(result);
        this.listener.resultEnd(this.ctx);
        return result;
    }

    @Override // org.jooq.Cursor, java.lang.AutoCloseable
    public final void close() {
        JDBCUtils.safeClose((ResultSet) this.rs);
        this.rs = null;
        this.isClosed = true;
    }

    @Override // org.jooq.Cursor
    public final boolean isClosed() {
        return this.isClosed;
    }

    @Override // org.jooq.Cursor
    public final ResultSet resultSet() {
        return this.rs;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CursorImpl$CursorResultSet.class */
    public final class CursorResultSet extends JDBC41ResultSet implements ResultSet {
        CursorResultSet() {
        }

        @Override // java.sql.Wrapper
        public final <T> T unwrap(Class<T> cls) throws SQLException {
            return (T) CursorImpl.this.ctx.resultSet().unwrap(cls);
        }

        @Override // java.sql.Wrapper
        public final boolean isWrapperFor(Class<?> iface) throws SQLException {
            return CursorImpl.this.ctx.resultSet().isWrapperFor(iface);
        }

        @Override // java.sql.ResultSet
        public final Statement getStatement() throws SQLException {
            return CursorImpl.this.ctx.resultSet().getStatement();
        }

        @Override // java.sql.ResultSet
        public final SQLWarning getWarnings() throws SQLException {
            return CursorImpl.this.ctx.resultSet().getWarnings();
        }

        @Override // java.sql.ResultSet
        public final void clearWarnings() throws SQLException {
            CursorImpl.this.ctx.resultSet().clearWarnings();
        }

        @Override // java.sql.ResultSet
        public final String getCursorName() throws SQLException {
            return CursorImpl.this.ctx.resultSet().getCursorName();
        }

        @Override // java.sql.ResultSet
        public final ResultSetMetaData getMetaData() throws SQLException {
            return CursorImpl.this.ctx.resultSet().getMetaData();
        }

        @Override // java.sql.ResultSet
        public final int findColumn(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().findColumn(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final void setFetchDirection(int direction) throws SQLException {
            CursorImpl.this.ctx.resultSet().setFetchDirection(direction);
        }

        @Override // java.sql.ResultSet
        public final int getFetchDirection() throws SQLException {
            return CursorImpl.this.ctx.resultSet().getFetchDirection();
        }

        @Override // java.sql.ResultSet
        public final void setFetchSize(int rows) throws SQLException {
            CursorImpl.this.ctx.resultSet().setFetchSize(rows);
        }

        @Override // java.sql.ResultSet
        public final int getFetchSize() throws SQLException {
            return CursorImpl.this.ctx.resultSet().getFetchSize();
        }

        @Override // java.sql.ResultSet
        public final int getType() throws SQLException {
            return CursorImpl.this.ctx.resultSet().getType();
        }

        @Override // java.sql.ResultSet
        public final int getConcurrency() throws SQLException {
            return CursorImpl.this.ctx.resultSet().getConcurrency();
        }

        @Override // java.sql.ResultSet
        public final int getHoldability() throws SQLException {
            return CursorImpl.this.ctx.resultSet().getHoldability();
        }

        @Override // java.sql.ResultSet
        public final boolean isBeforeFirst() throws SQLException {
            return CursorImpl.this.ctx.resultSet().isBeforeFirst();
        }

        @Override // java.sql.ResultSet
        public final boolean isAfterLast() throws SQLException {
            return CursorImpl.this.ctx.resultSet().isAfterLast();
        }

        @Override // java.sql.ResultSet
        public final boolean isFirst() throws SQLException {
            return CursorImpl.this.ctx.resultSet().isFirst();
        }

        @Override // java.sql.ResultSet
        public final boolean isLast() throws SQLException {
            return CursorImpl.this.ctx.resultSet().isLast();
        }

        @Override // java.sql.ResultSet
        public final boolean next() throws SQLException {
            return CursorImpl.this.ctx.resultSet().next();
        }

        @Override // java.sql.ResultSet
        public final boolean previous() throws SQLException {
            return CursorImpl.this.ctx.resultSet().previous();
        }

        @Override // java.sql.ResultSet
        public final void beforeFirst() throws SQLException {
            CursorImpl.this.ctx.resultSet().beforeFirst();
        }

        @Override // java.sql.ResultSet
        public final void afterLast() throws SQLException {
            CursorImpl.this.ctx.resultSet().afterLast();
        }

        @Override // java.sql.ResultSet
        public final boolean first() throws SQLException {
            return CursorImpl.this.ctx.resultSet().first();
        }

        @Override // java.sql.ResultSet
        public final boolean last() throws SQLException {
            return CursorImpl.this.ctx.resultSet().last();
        }

        @Override // java.sql.ResultSet
        public final int getRow() throws SQLException {
            return CursorImpl.this.ctx.resultSet().getRow();
        }

        @Override // java.sql.ResultSet
        public final boolean absolute(int row) throws SQLException {
            return CursorImpl.this.ctx.resultSet().absolute(row);
        }

        @Override // java.sql.ResultSet
        public final boolean relative(int r) throws SQLException {
            return CursorImpl.this.ctx.resultSet().relative(r);
        }

        @Override // java.sql.ResultSet
        public final void moveToInsertRow() throws SQLException {
            CursorImpl.this.ctx.resultSet().moveToInsertRow();
        }

        @Override // java.sql.ResultSet
        public final void moveToCurrentRow() throws SQLException {
            CursorImpl.this.ctx.resultSet().moveToCurrentRow();
        }

        @Override // java.sql.ResultSet, java.lang.AutoCloseable
        public final void close() throws SQLException {
            CursorImpl.this.ctx.rows(CursorImpl.this.rows);
            try {
                CursorImpl.this.listener.fetchEnd(CursorImpl.this.ctx);
            } finally {
                Tools.safeClose(CursorImpl.this.listener, CursorImpl.this.ctx, CursorImpl.this.keepStatement, CursorImpl.this.keepResultSet);
            }
        }

        @Override // java.sql.ResultSet
        public final boolean isClosed() throws SQLException {
            return CursorImpl.this.ctx.resultSet().isClosed();
        }

        @Override // java.sql.ResultSet
        public final boolean wasNull() throws SQLException {
            return CursorImpl.this.ctx.resultSet().wasNull();
        }

        @Override // java.sql.ResultSet
        public final java.sql.Array getArray(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getArray(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final java.sql.Array getArray(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getArray(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final InputStream getAsciiStream(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getAsciiStream(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final InputStream getAsciiStream(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getAsciiStream(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final BigDecimal getBigDecimal(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getBigDecimal(columnIndex);
        }

        @Override // java.sql.ResultSet
        @Deprecated
        public final BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getBigDecimal(columnIndex, scale);
        }

        @Override // java.sql.ResultSet
        public final BigDecimal getBigDecimal(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getBigDecimal(columnLabel);
        }

        @Override // java.sql.ResultSet
        @Deprecated
        public final BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getBigDecimal(columnLabel, scale);
        }

        @Override // java.sql.ResultSet
        public final InputStream getBinaryStream(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getBinaryStream(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final InputStream getBinaryStream(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getBinaryStream(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final Blob getBlob(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getBlob(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final Blob getBlob(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getBlob(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final boolean getBoolean(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getBoolean(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final boolean getBoolean(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getBoolean(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final byte getByte(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getByte(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final byte getByte(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getByte(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final byte[] getBytes(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getBytes(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final byte[] getBytes(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getBytes(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final Reader getCharacterStream(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getCharacterStream(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final Reader getCharacterStream(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getCharacterStream(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final Clob getClob(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getClob(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final Clob getClob(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getClob(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final Date getDate(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getDate(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final Date getDate(int columnIndex, Calendar cal) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getDate(columnIndex, cal);
        }

        @Override // java.sql.ResultSet
        public final Date getDate(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getDate(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final Date getDate(String columnLabel, Calendar cal) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getDate(columnLabel, cal);
        }

        @Override // java.sql.ResultSet
        public final double getDouble(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getDouble(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final double getDouble(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getDouble(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final float getFloat(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getFloat(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final float getFloat(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getFloat(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final int getInt(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getInt(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final int getInt(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getInt(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final long getLong(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getLong(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final long getLong(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getLong(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final Reader getNCharacterStream(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getNCharacterStream(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final Reader getNCharacterStream(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getNCharacterStream(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final NClob getNClob(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getNClob(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final NClob getNClob(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getNClob(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final String getNString(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getNString(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final String getNString(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getNString(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final Object getObject(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getObject(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getObject(columnIndex, map);
        }

        @Override // java.sql.ResultSet
        public final Object getObject(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getObject(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getObject(columnLabel, map);
        }

        @Override // java.sql.ResultSet
        public final Ref getRef(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getRef(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final Ref getRef(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getRef(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final RowId getRowId(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getRowId(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final RowId getRowId(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getRowId(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final short getShort(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getShort(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final short getShort(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getShort(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final SQLXML getSQLXML(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getSQLXML(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final SQLXML getSQLXML(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getSQLXML(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final String getString(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getString(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final String getString(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getString(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final Time getTime(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getTime(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final Time getTime(int columnIndex, Calendar cal) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getTime(columnIndex, cal);
        }

        @Override // java.sql.ResultSet
        public final Time getTime(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getTime(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final Time getTime(String columnLabel, Calendar cal) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getTime(columnLabel, cal);
        }

        @Override // java.sql.ResultSet
        public final Timestamp getTimestamp(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getTimestamp(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getTimestamp(columnIndex, cal);
        }

        @Override // java.sql.ResultSet
        public final Timestamp getTimestamp(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getTimestamp(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getTimestamp(columnLabel, cal);
        }

        @Override // java.sql.ResultSet
        @Deprecated
        public final InputStream getUnicodeStream(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getUnicodeStream(columnIndex);
        }

        @Override // java.sql.ResultSet
        @Deprecated
        public final InputStream getUnicodeStream(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getUnicodeStream(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final URL getURL(int columnIndex) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getURL(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final URL getURL(String columnLabel) throws SQLException {
            return CursorImpl.this.ctx.resultSet().getURL(columnLabel);
        }

        @Override // org.jooq.tools.jdbc.JDBC41ResultSet
        public final <T> T getObject(int i, Class<T> cls) throws SQLException {
            return (T) CursorImpl.this.ctx.resultSet().getObject(i, cls);
        }

        @Override // org.jooq.tools.jdbc.JDBC41ResultSet
        public final <T> T getObject(String str, Class<T> cls) throws SQLException {
            return (T) CursorImpl.this.ctx.resultSet().getObject(str, cls);
        }

        private final void logUpdate(int columnIndex, Object x) throws SQLException {
            if (CursorImpl.log.isDebugEnabled()) {
                CursorImpl.log.debug("Updating Result", "Updating Result position " + getRow() + ":" + columnIndex + " with value " + String.valueOf(x));
            }
        }

        private final void logUpdate(String columnLabel, Object x) throws SQLException {
            if (CursorImpl.log.isDebugEnabled()) {
                CursorImpl.log.debug("Updating Result", "Updating Result position " + getRow() + ":" + columnLabel + " with value " + String.valueOf(x));
            }
        }

        @Override // java.sql.ResultSet
        public final boolean rowUpdated() throws SQLException {
            return CursorImpl.this.ctx.resultSet().rowUpdated();
        }

        @Override // java.sql.ResultSet
        public final boolean rowInserted() throws SQLException {
            return CursorImpl.this.ctx.resultSet().rowInserted();
        }

        @Override // java.sql.ResultSet
        public final boolean rowDeleted() throws SQLException {
            return CursorImpl.this.ctx.resultSet().rowDeleted();
        }

        @Override // java.sql.ResultSet
        public final void insertRow() throws SQLException {
            CursorImpl.this.ctx.resultSet().insertRow();
        }

        @Override // java.sql.ResultSet
        public final void updateRow() throws SQLException {
            CursorImpl.this.ctx.resultSet().updateRow();
        }

        @Override // java.sql.ResultSet
        public final void deleteRow() throws SQLException {
            CursorImpl.this.ctx.resultSet().deleteRow();
        }

        @Override // java.sql.ResultSet
        public final void refreshRow() throws SQLException {
            CursorImpl.this.ctx.resultSet().refreshRow();
        }

        @Override // java.sql.ResultSet
        public final void cancelRowUpdates() throws SQLException {
            CursorImpl.this.ctx.resultSet().cancelRowUpdates();
        }

        @Override // java.sql.ResultSet
        public final void updateNull(int columnIndex) throws SQLException {
            logUpdate(columnIndex, (Object) null);
            CursorImpl.this.ctx.resultSet().updateNull(columnIndex);
        }

        @Override // java.sql.ResultSet
        public final void updateNull(String columnLabel) throws SQLException {
            logUpdate(columnLabel, (Object) null);
            CursorImpl.this.ctx.resultSet().updateNull(columnLabel);
        }

        @Override // java.sql.ResultSet
        public final void updateArray(int columnIndex, java.sql.Array x) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateArray(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateArray(String columnLabel, java.sql.Array x) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateArray(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateAsciiStream(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateAsciiStream(columnIndex, x, length);
        }

        @Override // java.sql.ResultSet
        public final void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateAsciiStream(columnIndex, x, length);
        }

        @Override // java.sql.ResultSet
        public final void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateAsciiStream(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateAsciiStream(columnLabel, x, length);
        }

        @Override // java.sql.ResultSet
        public final void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateAsciiStream(columnLabel, x, length);
        }

        @Override // java.sql.ResultSet
        public final void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateBigDecimal(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateBigDecimal(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateBinaryStream(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateBinaryStream(columnIndex, x, length);
        }

        @Override // java.sql.ResultSet
        public final void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateBinaryStream(columnIndex, x, length);
        }

        @Override // java.sql.ResultSet
        public final void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateBinaryStream(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateBinaryStream(columnLabel, x, length);
        }

        @Override // java.sql.ResultSet
        public final void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateBinaryStream(columnLabel, x, length);
        }

        @Override // java.sql.ResultSet
        public final void updateBlob(int columnIndex, Blob x) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateBlob(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateBlob(int columnIndex, InputStream x, long length) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateBlob(columnIndex, x, length);
        }

        @Override // java.sql.ResultSet
        public final void updateBlob(String columnLabel, Blob x) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateBlob(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateBlob(int columnIndex, InputStream x) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateBlob(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateBlob(String columnLabel, InputStream x) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateBlob(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateBlob(String columnLabel, InputStream x, long length) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateBlob(columnLabel, x, length);
        }

        @Override // java.sql.ResultSet
        public final void updateBoolean(int columnIndex, boolean x) throws SQLException {
            logUpdate(columnIndex, Boolean.valueOf(x));
            CursorImpl.this.ctx.resultSet().updateBoolean(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateBoolean(String columnLabel, boolean x) throws SQLException {
            logUpdate(columnLabel, Boolean.valueOf(x));
            CursorImpl.this.ctx.resultSet().updateBoolean(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateByte(int columnIndex, byte x) throws SQLException {
            logUpdate(columnIndex, Byte.valueOf(x));
            CursorImpl.this.ctx.resultSet().updateByte(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateByte(String columnLabel, byte x) throws SQLException {
            logUpdate(columnLabel, Byte.valueOf(x));
            CursorImpl.this.ctx.resultSet().updateByte(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateBytes(int columnIndex, byte[] x) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateBytes(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateBytes(String columnLabel, byte[] x) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateBytes(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateCharacterStream(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateCharacterStream(columnIndex, x, length);
        }

        @Override // java.sql.ResultSet
        public final void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateCharacterStream(columnIndex, x, length);
        }

        @Override // java.sql.ResultSet
        public final void updateCharacterStream(String columnLabel, Reader x) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateCharacterStream(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateCharacterStream(String columnLabel, Reader x, int length) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateCharacterStream(columnLabel, x, length);
        }

        @Override // java.sql.ResultSet
        public final void updateCharacterStream(String columnLabel, Reader x, long length) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateCharacterStream(columnLabel, x, length);
        }

        @Override // java.sql.ResultSet
        public final void updateClob(int columnIndex, Clob x) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateClob(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateClob(int columnIndex, Reader x) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateClob(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateClob(int columnIndex, Reader x, long length) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateClob(columnIndex, x, length);
        }

        @Override // java.sql.ResultSet
        public final void updateClob(String columnLabel, Clob x) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateClob(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateClob(String columnLabel, Reader x) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateClob(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateClob(String columnLabel, Reader x, long length) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateClob(columnLabel, x, length);
        }

        @Override // java.sql.ResultSet
        public final void updateDate(int columnIndex, Date x) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateDate(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateDate(String columnLabel, Date x) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateDate(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateDouble(int columnIndex, double x) throws SQLException {
            logUpdate(columnIndex, Double.valueOf(x));
            CursorImpl.this.ctx.resultSet().updateDouble(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateDouble(String columnLabel, double x) throws SQLException {
            logUpdate(columnLabel, Double.valueOf(x));
            CursorImpl.this.ctx.resultSet().updateDouble(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateFloat(int columnIndex, float x) throws SQLException {
            logUpdate(columnIndex, Float.valueOf(x));
            CursorImpl.this.ctx.resultSet().updateFloat(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateFloat(String columnLabel, float x) throws SQLException {
            logUpdate(columnLabel, Float.valueOf(x));
            CursorImpl.this.ctx.resultSet().updateFloat(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateInt(int columnIndex, int x) throws SQLException {
            logUpdate(columnIndex, Integer.valueOf(x));
            CursorImpl.this.ctx.resultSet().updateInt(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateInt(String columnLabel, int x) throws SQLException {
            logUpdate(columnLabel, Integer.valueOf(x));
            CursorImpl.this.ctx.resultSet().updateInt(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateLong(int columnIndex, long x) throws SQLException {
            logUpdate(columnIndex, Long.valueOf(x));
            CursorImpl.this.ctx.resultSet().updateLong(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateLong(String columnLabel, long x) throws SQLException {
            logUpdate(columnLabel, Long.valueOf(x));
            CursorImpl.this.ctx.resultSet().updateLong(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateNCharacterStream(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateNCharacterStream(columnIndex, x, length);
        }

        @Override // java.sql.ResultSet
        public final void updateNCharacterStream(String columnLabel, Reader x) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateNCharacterStream(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateNCharacterStream(String columnLabel, Reader x, long length) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateNCharacterStream(columnLabel, x, length);
        }

        @Override // java.sql.ResultSet
        public final void updateNClob(int columnIndex, NClob x) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateNClob(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateNClob(int columnIndex, Reader x, long length) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateNClob(columnIndex, x, length);
        }

        @Override // java.sql.ResultSet
        public final void updateNClob(int columnIndex, Reader x) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateNClob(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateNClob(String columnLabel, NClob x) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateNClob(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateNClob(String columnLabel, Reader x, long length) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateNClob(columnLabel, x, length);
        }

        @Override // java.sql.ResultSet
        public final void updateNClob(String columnLabel, Reader x) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateNClob(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateNString(int columnIndex, String x) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateNString(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateNString(String columnLabel, String x) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateNString(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateObject(int columnIndex, Object x) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateObject(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateObject(columnIndex, x, scaleOrLength);
        }

        @Override // java.sql.ResultSet
        public final void updateObject(String columnLabel, Object x) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateObject(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateObject(columnLabel, x, scaleOrLength);
        }

        @Override // java.sql.ResultSet
        public final void updateRef(int columnIndex, Ref x) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateRef(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateRef(String columnLabel, Ref x) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateRef(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateRowId(int columnIndex, RowId x) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateRowId(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateRowId(String columnLabel, RowId x) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateRowId(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateShort(int columnIndex, short x) throws SQLException {
            logUpdate(columnIndex, Short.valueOf(x));
            CursorImpl.this.ctx.resultSet().updateShort(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateShort(String columnLabel, short x) throws SQLException {
            logUpdate(columnLabel, Short.valueOf(x));
            CursorImpl.this.ctx.resultSet().updateShort(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateSQLXML(int columnIndex, SQLXML x) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateSQLXML(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateSQLXML(String columnLabel, SQLXML x) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateSQLXML(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateString(int columnIndex, String x) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateString(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateString(String columnLabel, String x) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateString(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateTime(int columnIndex, Time x) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateTime(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateTime(String columnLabel, Time x) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateTime(columnLabel, x);
        }

        @Override // java.sql.ResultSet
        public final void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateTimestamp(columnIndex, x);
        }

        @Override // java.sql.ResultSet
        public final void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateTimestamp(columnLabel, x);
        }

        public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateObject(columnIndex, x, targetSqlType, scaleOrLength);
        }

        public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateObject(columnLabel, x, targetSqlType, scaleOrLength);
        }

        public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
            logUpdate(columnIndex, x);
            CursorImpl.this.ctx.resultSet().updateObject(columnIndex, x, targetSqlType);
        }

        public void updateObject(String columnLabel, Object x, SQLType targetSqlType) throws SQLException {
            logUpdate(columnLabel, x);
            CursorImpl.this.ctx.resultSet().updateObject(columnLabel, x, targetSqlType);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CursorImpl$CursorIterator.class */
    public final class CursorIterator implements Iterator<R> {
        private R next;
        private Boolean hasNext;
        private final CursorRecordInitialiser initialiser;
        private final RecordDelegate<AbstractRecord> recordDelegate;

        CursorIterator() {
            this.initialiser = new CursorRecordInitialiser(CursorImpl.this.ctx, CursorImpl.this.listener, new DefaultBindingGetResultSetContext(CursorImpl.this.ctx, CursorImpl.this.rs, 0), CursorImpl.this.fields, 0, CursorImpl.this.intern);
            this.recordDelegate = Tools.newRecord(true, (Supplier) CursorImpl.this.factory, ((DefaultExecuteContext) CursorImpl.this.ctx).originalConfiguration());
        }

        @Override // java.util.Iterator
        public final boolean hasNext() {
            if (this.hasNext == null) {
                if (CursorImpl.this.maxRows > 0 && CursorImpl.this.rows >= CursorImpl.this.maxRows) {
                    return false;
                }
                this.next = (R) fetchNext();
                this.hasNext = Boolean.valueOf(this.next != null);
            }
            return this.hasNext.booleanValue();
        }

        @Override // java.util.Iterator
        public final R next() {
            if (!hasNext()) {
                throw new NoSuchElementException("There are no more records to fetch from this Cursor");
            }
            R result = this.next;
            this.hasNext = null;
            this.next = null;
            return result;
        }

        private final R fetchNext() {
            AbstractRecord record = null;
            try {
                if (!CursorImpl.this.isClosed && CursorImpl.this.rs.next()) {
                    record = this.recordDelegate.operate(this.initialiser.reset());
                    CursorImpl.this.rows++;
                }
                if (record == null && CursorImpl.this.autoclosing) {
                    CursorImpl.this.close();
                }
                return record;
            } catch (RuntimeException e) {
                CursorImpl.this.ctx.exception(e);
                CursorImpl.this.listener.exception(CursorImpl.this.ctx);
                throw CursorImpl.this.ctx.exception();
            } catch (SQLException e2) {
                CursorImpl.this.ctx.sqlException(e2);
                CursorImpl.this.listener.exception(CursorImpl.this.ctx);
                throw CursorImpl.this.ctx.exception();
            } catch (ControlFlowSignal e3) {
                throw e3;
            }
        }

        @Override // java.util.Iterator
        public final void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CursorImpl$CursorRecordInitialiser.class */
    public static class CursorRecordInitialiser implements ThrowingFunction<AbstractRecord, AbstractRecord, SQLException> {
        private final ExecuteContext ctx;
        private final ExecuteListener listener;
        private final AbstractRow<?> initialiserFields;
        private int offset;
        private final boolean[] intern;
        private final DefaultBindingGetResultSetContext<?> rsContext;

        /* JADX INFO: Access modifiers changed from: package-private */
        public CursorRecordInitialiser(ExecuteContext ctx, ExecuteListener listener, DefaultBindingGetResultSetContext<?> rsContext, AbstractRow<?> initialiserFields, int offset, boolean[] intern) {
            this.ctx = ctx;
            this.listener = listener;
            this.rsContext = rsContext;
            this.initialiserFields = initialiserFields;
            this.offset = offset;
            this.intern = intern;
        }

        CursorRecordInitialiser reset() {
            this.offset = 0;
            return this;
        }

        @Override // org.jooq.impl.ThrowingFunction
        public AbstractRecord apply(AbstractRecord record) throws SQLException {
            this.ctx.record(record);
            this.listener.recordStart(this.ctx);
            int size = this.initialiserFields.size();
            for (int i = 0; i < size; i++) {
                setValue(record, this.initialiserFields.field(i), i);
            }
            if (this.intern != null) {
                for (int i2 = 0; i2 < this.intern.length; i2++) {
                    if (this.intern[i2]) {
                        record.intern0(i2);
                    }
                }
            }
            this.ctx.record(record);
            this.listener.recordEnd(this.ctx);
            return record;
        }

        private final <T> void setValue(AbstractRecord record, Field<T> field, int index) throws SQLException {
            Object value;
            try {
                AbstractRow<?> nested = null;
                Class<? extends AbstractRecord> recordType = null;
                Field<?> f = Tools.uncoerce(field);
                if ((f instanceof AbstractRowAsField) && RowAsField.NO_NATIVE_SUPPORT.contains(this.ctx.dialect()) && !Boolean.TRUE.equals(this.ctx.data(Tools.BooleanDataKey.DATA_MULTISET_CONTENT))) {
                    nested = ((AbstractRowAsField) f).emulatedFields(this.ctx.configuration());
                    recordType = ((AbstractRowAsField) f).getRecordType();
                } else if (f.getDataType().isEmbeddable()) {
                    nested = Tools.row0(Tools.embeddedFields(f));
                    recordType = Tools.embeddedRecordType(f);
                }
                int nestedOffset = this.offset + index;
                if (nested != null) {
                    CursorRecordInitialiser operation = new CursorRecordInitialiser(this.ctx, this.listener, this.rsContext, nested, nestedOffset, this.intern);
                    value = Tools.newRecord(true, recordType, nested, ((DefaultExecuteContext) this.ctx).originalConfiguration()).operate(operation);
                    if (f != field) {
                        value = field.getConverter().from(value, this.ctx.converterContext());
                    }
                    this.offset += ((operation.offset - nestedOffset) + nested.size()) - 1;
                } else {
                    this.rsContext.index(nestedOffset + 1);
                    this.rsContext.field(field);
                    field.getBinding().get(this.rsContext);
                    value = this.rsContext.value();
                }
                record.values[index] = value;
                record.originals[index] = value;
            } catch (Exception e) {
                throw new SQLException("Error while reading field: " + String.valueOf(field) + ", at JDBC index: " + (this.offset + index + 1), e);
            }
        }
    }
}
