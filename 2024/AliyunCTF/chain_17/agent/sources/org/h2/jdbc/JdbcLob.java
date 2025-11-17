package org.h2.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.SQLException;
import org.h2.api.ErrorCode;
import org.h2.message.DbException;
import org.h2.message.TraceObject;
import org.h2.mvstore.DataUtils;
import org.h2.util.IOUtils;
import org.h2.util.Task;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/JdbcLob.class */
public abstract class JdbcLob extends TraceObject {
    final JdbcConnection conn;
    Value value;
    State state;

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/JdbcLob$State.class */
    public enum State {
        NEW,
        SET_CALLED,
        WITH_VALUE,
        CLOSED
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/JdbcLob$LobPipedOutputStream.class */
    public static final class LobPipedOutputStream extends PipedOutputStream {
        private final Task task;

        /* JADX INFO: Access modifiers changed from: package-private */
        public LobPipedOutputStream(PipedInputStream pipedInputStream, Task task) throws IOException {
            super(pipedInputStream);
            this.task = task;
        }

        @Override // java.io.PipedOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            super.close();
            try {
                this.task.get();
            } catch (Exception e) {
                throw DataUtils.convertToIOException(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JdbcLob(JdbcConnection jdbcConnection, Value value, State state, int i, int i2) {
        setTrace(jdbcConnection.getSession().getTrace(), i, i2);
        this.conn = jdbcConnection;
        this.value = value;
        this.state = state;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void checkClosed() {
        this.conn.checkClosed();
        if (this.state == State.CLOSED) {
            throw DbException.get(ErrorCode.OBJECT_CLOSED);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void checkEditable() {
        checkClosed();
        if (this.state != State.NEW) {
            throw DbException.getUnsupportedException("Allocate a new object to set its value.");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void checkReadable() throws SQLException, IOException {
        checkClosed();
        if (this.state == State.SET_CALLED) {
            throw DbException.getUnsupportedException("Stream setter is not yet closed.");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void completeWrite(Value value) {
        checkClosed();
        this.state = State.WITH_VALUE;
        this.value = value;
    }

    public void free() {
        debugCodeCall("free");
        this.state = State.CLOSED;
        this.value = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public InputStream getBinaryStream() throws SQLException {
        try {
            debugCodeCall("getBinaryStream");
            checkReadable();
            return this.value.getInputStream();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Reader getCharacterStream() throws SQLException {
        try {
            debugCodeCall("getCharacterStream");
            checkReadable();
            return this.value.getReader();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Writer setCharacterStreamImpl() throws IOException {
        return IOUtils.getBufferedWriter(setClobOutputStreamImpl());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LobPipedOutputStream setClobOutputStreamImpl() throws IOException {
        final PipedInputStream pipedInputStream = new PipedInputStream();
        Task task = new Task() { // from class: org.h2.jdbc.JdbcLob.1
            @Override // org.h2.util.Task
            public void call() {
                JdbcLob.this.completeWrite(JdbcLob.this.conn.createClob(IOUtils.getReader(pipedInputStream), -1L));
            }
        };
        LobPipedOutputStream lobPipedOutputStream = new LobPipedOutputStream(pipedInputStream, task);
        task.execute();
        return lobPipedOutputStream;
    }

    public String toString() {
        StringBuilder append = new StringBuilder().append(getTraceObjectName()).append(": ");
        if (this.state == State.SET_CALLED) {
            append.append("<setter_in_progress>");
        } else if (this.state == State.CLOSED) {
            append.append("<closed>");
        } else {
            append.append(this.value.getTraceSQL());
        }
        return append.toString();
    }
}
