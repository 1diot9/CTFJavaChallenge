package org.h2.jdbc;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.sql.Blob;
import java.sql.SQLException;
import org.h2.jdbc.JdbcLob;
import org.h2.message.DbException;
import org.h2.util.IOUtils;
import org.h2.util.Task;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/JdbcBlob.class */
public final class JdbcBlob extends JdbcLob implements Blob {
    public JdbcBlob(JdbcConnection jdbcConnection, Value value, JdbcLob.State state, int i) {
        super(jdbcConnection, value, state, 9, i);
    }

    @Override // java.sql.Blob
    public long length() throws SQLException {
        try {
            debugCodeCall("length");
            checkReadable();
            if (this.value.getValueType() == 7) {
                long precision = this.value.getType().getPrecision();
                if (precision > 0) {
                    return precision;
                }
            }
            return IOUtils.copyAndCloseInput(this.value.getInputStream(), null);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Blob
    public void truncate(long j) throws SQLException {
        throw unsupported("LOB update");
    }

    @Override // java.sql.Blob
    public byte[] getBytes(long j, int i) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getBytes(" + j + ", " + i + ')');
            }
            checkReadable();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            InputStream inputStream = this.value.getInputStream();
            Throwable th = null;
            try {
                try {
                    IOUtils.skipFully(inputStream, j - 1);
                    IOUtils.copy(inputStream, byteArrayOutputStream, i);
                    if (inputStream != null) {
                        if (0 != 0) {
                            try {
                                inputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            inputStream.close();
                        }
                    }
                    return byteArrayOutputStream.toByteArray();
                } finally {
                }
            } finally {
            }
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Blob
    public int setBytes(long j, byte[] bArr) throws SQLException {
        if (bArr == null) {
            throw new NullPointerException();
        }
        try {
            if (isDebugEnabled()) {
                debugCode("setBytes(" + j + ", " + quoteBytes(bArr) + ')');
            }
            checkEditable();
            if (j != 1) {
                throw DbException.getInvalidValueException("pos", Long.valueOf(j));
            }
            completeWrite(this.conn.createBlob(new ByteArrayInputStream(bArr), -1L));
            return bArr.length;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Blob
    public int setBytes(long j, byte[] bArr, int i, int i2) throws SQLException {
        if (bArr == null) {
            throw new NullPointerException();
        }
        try {
            if (isDebugEnabled()) {
                debugCode("setBytes(" + j + ", " + quoteBytes(bArr) + ", " + i + ", " + i2 + ')');
            }
            checkEditable();
            if (j != 1) {
                throw DbException.getInvalidValueException("pos", Long.valueOf(j));
            }
            completeWrite(this.conn.createBlob(new ByteArrayInputStream(bArr, i, i2), -1L));
            return (int) this.value.getType().getPrecision();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // org.h2.jdbc.JdbcLob, java.sql.Blob
    public InputStream getBinaryStream() throws SQLException {
        return super.getBinaryStream();
    }

    @Override // java.sql.Blob
    public OutputStream setBinaryStream(long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCodeCall("setBinaryStream", j);
            }
            checkEditable();
            if (j != 1) {
                throw DbException.getInvalidValueException("pos", Long.valueOf(j));
            }
            final PipedInputStream pipedInputStream = new PipedInputStream();
            Task task = new Task() { // from class: org.h2.jdbc.JdbcBlob.1
                @Override // org.h2.util.Task
                public void call() {
                    JdbcBlob.this.completeWrite(JdbcBlob.this.conn.createBlob(pipedInputStream, -1L));
                }
            };
            JdbcLob.LobPipedOutputStream lobPipedOutputStream = new JdbcLob.LobPipedOutputStream(pipedInputStream, task);
            task.execute();
            this.state = JdbcLob.State.SET_CALLED;
            return new BufferedOutputStream(lobPipedOutputStream);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Blob
    public long position(byte[] bArr, long j) throws SQLException {
        if (isDebugEnabled()) {
            debugCode("position(" + quoteBytes(bArr) + ", " + j + ')');
        }
        throw unsupported("LOB search");
    }

    @Override // java.sql.Blob
    public long position(Blob blob, long j) throws SQLException {
        if (isDebugEnabled()) {
            debugCode("position(blobPattern, " + j + ')');
        }
        throw unsupported("LOB subset");
    }

    @Override // java.sql.Blob
    public InputStream getBinaryStream(long j, long j2) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getBinaryStream(" + j + ", " + j2 + ')');
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
            return this.value.getInputStream(j, j2);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }
}
