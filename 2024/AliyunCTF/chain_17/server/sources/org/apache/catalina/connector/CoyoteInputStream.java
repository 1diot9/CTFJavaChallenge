package org.apache.catalina.connector;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Objects;
import org.apache.catalina.security.SecurityUtil;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/CoyoteInputStream.class */
public class CoyoteInputStream extends ServletInputStream {
    protected static final StringManager sm = StringManager.getManager((Class<?>) CoyoteInputStream.class);
    protected InputBuffer ib;

    /* JADX INFO: Access modifiers changed from: protected */
    public CoyoteInputStream(InputBuffer ib) {
        this.ib = ib;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clear() {
        this.ib = null;
    }

    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        checkNonBlockingRead();
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                Integer result = (Integer) AccessController.doPrivileged(new PrivilegedRead(this.ib));
                return result.intValue();
            } catch (PrivilegedActionException pae) {
                Exception e = pae.getException();
                if (e instanceof IOException) {
                    throw ((IOException) e);
                }
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return this.ib.readByte();
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                Integer result = (Integer) AccessController.doPrivileged(new PrivilegedAvailable(this.ib));
                return result.intValue();
            } catch (PrivilegedActionException pae) {
                Exception e = pae.getException();
                if (e instanceof IOException) {
                    throw ((IOException) e);
                }
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return this.ib.available();
    }

    @Override // java.io.InputStream
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override // java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        checkNonBlockingRead();
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                Integer result = (Integer) AccessController.doPrivileged(new PrivilegedReadArray(this.ib, b, off, len));
                return result.intValue();
            } catch (PrivilegedActionException pae) {
                Exception e = pae.getException();
                if (e instanceof IOException) {
                    throw ((IOException) e);
                }
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return this.ib.read(b, off, len);
    }

    public int read(ByteBuffer b) throws IOException {
        Objects.requireNonNull(b);
        checkNonBlockingRead();
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                Integer result = (Integer) AccessController.doPrivileged(new PrivilegedReadBuffer(this.ib, b));
                return result.intValue();
            } catch (PrivilegedActionException pae) {
                Exception e = pae.getException();
                if (e instanceof IOException) {
                    throw ((IOException) e);
                }
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return this.ib.read(b);
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                AccessController.doPrivileged(new PrivilegedClose(this.ib));
                return;
            } catch (PrivilegedActionException pae) {
                Exception e = pae.getException();
                if (e instanceof IOException) {
                    throw ((IOException) e);
                }
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        this.ib.close();
    }

    @Override // jakarta.servlet.ServletInputStream
    public boolean isFinished() {
        return this.ib.isFinished();
    }

    @Override // jakarta.servlet.ServletInputStream
    public boolean isReady() {
        if (this.ib == null) {
            throw new IllegalStateException(sm.getString("coyoteInputStream.null"));
        }
        return this.ib.isReady();
    }

    @Override // jakarta.servlet.ServletInputStream
    public void setReadListener(ReadListener listener) {
        this.ib.setReadListener(listener);
    }

    private void checkNonBlockingRead() {
        if (!this.ib.isBlocking() && !this.ib.isReady()) {
            throw new IllegalStateException(sm.getString("coyoteInputStream.nbNotready"));
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/CoyoteInputStream$PrivilegedAvailable.class */
    private static class PrivilegedAvailable implements PrivilegedExceptionAction<Integer> {
        private final InputBuffer inputBuffer;

        PrivilegedAvailable(InputBuffer inputBuffer) {
            this.inputBuffer = inputBuffer;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Integer run() throws IOException {
            return Integer.valueOf(this.inputBuffer.available());
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/CoyoteInputStream$PrivilegedClose.class */
    private static class PrivilegedClose implements PrivilegedExceptionAction<Void> {
        private final InputBuffer inputBuffer;

        PrivilegedClose(InputBuffer inputBuffer) {
            this.inputBuffer = inputBuffer;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Void run() throws IOException {
            this.inputBuffer.close();
            return null;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/CoyoteInputStream$PrivilegedRead.class */
    private static class PrivilegedRead implements PrivilegedExceptionAction<Integer> {
        private final InputBuffer inputBuffer;

        PrivilegedRead(InputBuffer inputBuffer) {
            this.inputBuffer = inputBuffer;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Integer run() throws IOException {
            Integer integer = Integer.valueOf(this.inputBuffer.readByte());
            return integer;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/CoyoteInputStream$PrivilegedReadArray.class */
    public static class PrivilegedReadArray implements PrivilegedExceptionAction<Integer> {
        private final InputBuffer inputBuffer;
        private final byte[] buf;
        private final int off;
        private final int len;

        PrivilegedReadArray(InputBuffer inputBuffer, byte[] buf, int off, int len) {
            this.inputBuffer = inputBuffer;
            this.buf = buf;
            this.off = off;
            this.len = len;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Integer run() throws IOException {
            Integer integer = Integer.valueOf(this.inputBuffer.read(this.buf, this.off, this.len));
            return integer;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/CoyoteInputStream$PrivilegedReadBuffer.class */
    private static class PrivilegedReadBuffer implements PrivilegedExceptionAction<Integer> {
        private final InputBuffer inputBuffer;
        private final ByteBuffer bb;

        PrivilegedReadBuffer(InputBuffer inputBuffer, ByteBuffer bb) {
            this.inputBuffer = inputBuffer;
            this.bb = bb;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Integer run() throws IOException {
            Integer integer = Integer.valueOf(this.inputBuffer.read(this.bb));
            return integer;
        }
    }
}
