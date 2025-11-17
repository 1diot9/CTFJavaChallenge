package org.apache.catalina.connector;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/CoyoteOutputStream.class */
public class CoyoteOutputStream extends ServletOutputStream {
    protected static final StringManager sm = StringManager.getManager((Class<?>) CoyoteOutputStream.class);
    protected OutputBuffer ob;

    /* JADX INFO: Access modifiers changed from: protected */
    public CoyoteOutputStream(OutputBuffer ob) {
        this.ob = ob;
    }

    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clear() {
        this.ob = null;
    }

    @Override // java.io.OutputStream
    public void write(int i) throws IOException {
        boolean nonBlocking = checkNonBlockingWrite();
        this.ob.writeByte(i);
        if (nonBlocking) {
            checkRegisterForWrite();
        }
    }

    @Override // java.io.OutputStream
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override // java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        boolean nonBlocking = checkNonBlockingWrite();
        this.ob.write(b, off, len);
        if (nonBlocking) {
            checkRegisterForWrite();
        }
    }

    public void write(ByteBuffer from) throws IOException {
        Objects.requireNonNull(from);
        boolean nonBlocking = checkNonBlockingWrite();
        this.ob.write(from);
        if (nonBlocking) {
            checkRegisterForWrite();
        }
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        boolean nonBlocking = checkNonBlockingWrite();
        this.ob.flush();
        if (nonBlocking) {
            checkRegisterForWrite();
        }
    }

    private boolean checkNonBlockingWrite() {
        boolean nonBlocking = !this.ob.isBlocking();
        if (nonBlocking && !this.ob.isReady()) {
            throw new IllegalStateException(sm.getString("coyoteOutputStream.nbNotready"));
        }
        return nonBlocking;
    }

    private void checkRegisterForWrite() {
        this.ob.checkRegisterForWrite();
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.ob.close();
    }

    @Override // jakarta.servlet.ServletOutputStream
    public boolean isReady() {
        if (this.ob == null) {
            throw new IllegalStateException(sm.getString("coyoteOutputStream.null"));
        }
        return this.ob.isReady();
    }

    @Override // jakarta.servlet.ServletOutputStream
    public void setWriteListener(WriteListener listener) {
        this.ob.setWriteListener(listener);
    }
}
