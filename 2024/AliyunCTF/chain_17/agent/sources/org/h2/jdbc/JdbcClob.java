package org.h2.jdbc;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.SQLException;
import org.h2.jdbc.JdbcLob;
import org.h2.message.DbException;
import org.h2.store.RangeReader;
import org.h2.util.IOUtils;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/JdbcClob.class */
public final class JdbcClob extends JdbcLob implements NClob {
    public JdbcClob(JdbcConnection jdbcConnection, Value value, JdbcLob.State state, int i) {
        super(jdbcConnection, value, state, 10, i);
    }

    @Override // java.sql.Clob
    public long length() throws SQLException {
        try {
            debugCodeCall("length");
            checkReadable();
            if (this.value.getValueType() == 3) {
                long precision = this.value.getType().getPrecision();
                if (precision > 0) {
                    return precision;
                }
            }
            return IOUtils.copyAndCloseInput(this.value.getReader(), null, Long.MAX_VALUE);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Clob
    public void truncate(long j) throws SQLException {
        throw unsupported("LOB update");
    }

    @Override // java.sql.Clob
    public InputStream getAsciiStream() throws SQLException {
        try {
            debugCodeCall("getAsciiStream");
            checkReadable();
            return IOUtils.getInputStreamFromString(this.value.getString());
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Clob
    public OutputStream setAsciiStream(long j) throws SQLException {
        throw unsupported("LOB update");
    }

    @Override // org.h2.jdbc.JdbcLob, java.sql.Clob
    public Reader getCharacterStream() throws SQLException {
        return super.getCharacterStream();
    }

    @Override // java.sql.Clob
    public Writer setCharacterStream(long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCodeCall("setCharacterStream", j);
            }
            checkEditable();
            if (j != 1) {
                throw DbException.getInvalidValueException("pos", Long.valueOf(j));
            }
            this.state = JdbcLob.State.SET_CALLED;
            return setCharacterStreamImpl();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Clob
    public String getSubString(long j, int i) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getSubString(" + j + ", " + i + ')');
            }
            checkReadable();
            if (j < 1) {
                throw DbException.getInvalidValueException("pos", Long.valueOf(j));
            }
            if (i < 0) {
                throw DbException.getInvalidValueException("length", Integer.valueOf(i));
            }
            StringWriter stringWriter = new StringWriter(Math.min(4096, i));
            Reader reader = this.value.getReader();
            Throwable th = null;
            try {
                try {
                    IOUtils.skipFully(reader, j - 1);
                    IOUtils.copyAndCloseInput(reader, stringWriter, i);
                    if (reader != null) {
                        if (0 != 0) {
                            try {
                                reader.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            reader.close();
                        }
                    }
                    return stringWriter.toString();
                } finally {
                }
            } finally {
            }
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Clob
    public int setString(long j, String str) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setString(" + j + ", " + quote(str) + ')');
            }
            checkEditable();
            if (j != 1) {
                throw DbException.getInvalidValueException("pos", Long.valueOf(j));
            }
            if (str == null) {
                throw DbException.getInvalidValueException("str", str);
            }
            completeWrite(this.conn.createClob(new StringReader(str), -1L));
            return str.length();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Clob
    public int setString(long j, String str, int i, int i2) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setString(" + j + ", " + quote(str) + ", " + i + ", " + i2 + ')');
            }
            checkEditable();
            if (j != 1) {
                throw DbException.getInvalidValueException("pos", Long.valueOf(j));
            }
            if (str == null) {
                throw DbException.getInvalidValueException("str", str);
            }
            completeWrite(this.conn.createClob(new RangeReader(new StringReader(str), i, i2), -1L));
            return (int) this.value.getType().getPrecision();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Clob
    public long position(String str, long j) throws SQLException {
        throw unsupported("LOB search");
    }

    @Override // java.sql.Clob
    public long position(Clob clob, long j) throws SQLException {
        throw unsupported("LOB search");
    }

    @Override // java.sql.Clob
    public Reader getCharacterStream(long j, long j2) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getCharacterStream(" + j + ", " + j2 + ')');
            }
            checkReadable();
            if (this.state == JdbcLob.State.NEW) {
                if (j != 1) {
                    throw DbException.getInvalidValueException("pos", Long.valueOf(j));
                }
                if (j2 != 0) {
                    throw DbException.getInvalidValueException("length", Long.valueOf(j));
                }
            }
            return this.value.getReader(j, j2);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }
}
