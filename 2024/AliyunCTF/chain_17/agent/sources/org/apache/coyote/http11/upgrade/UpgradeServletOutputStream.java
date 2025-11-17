package org.apache.coyote.http11.upgrade;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import java.io.IOException;
import org.apache.coyote.Request;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.net.DispatchType;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/upgrade/UpgradeServletOutputStream.class */
public class UpgradeServletOutputStream extends ServletOutputStream {
    private static final Log log = LogFactory.getLog((Class<?>) UpgradeServletOutputStream.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) UpgradeServletOutputStream.class);
    private final UpgradeProcessorBase processor;
    private final SocketWrapperBase<?> socketWrapper;
    private final UpgradeInfo upgradeInfo;
    private final Object registeredLock = new Object();
    private final Object writeLock = new Object();
    private volatile boolean flushing = false;
    private volatile boolean closed = false;
    private volatile WriteListener listener = null;
    private boolean registered = false;

    public UpgradeServletOutputStream(UpgradeProcessorBase processor, SocketWrapperBase<?> socketWrapper, UpgradeInfo upgradeInfo) {
        this.processor = processor;
        this.socketWrapper = socketWrapper;
        this.upgradeInfo = upgradeInfo;
    }

    @Override // jakarta.servlet.ServletOutputStream
    public final boolean isReady() {
        if (this.listener == null) {
            throw new IllegalStateException(sm.getString("upgrade.sos.canWrite.ise"));
        }
        if (this.closed) {
            return false;
        }
        synchronized (this.registeredLock) {
            if (this.flushing) {
                this.registered = true;
                return false;
            }
            if (this.registered) {
                return false;
            }
            boolean result = this.socketWrapper.isReadyForWrite();
            this.registered = !result;
            return result;
        }
    }

    @Override // jakarta.servlet.ServletOutputStream
    public final void setWriteListener(WriteListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException(sm.getString("upgrade.sos.writeListener.null"));
        }
        if (this.listener != null) {
            throw new IllegalArgumentException(sm.getString("upgrade.sos.writeListener.set"));
        }
        if (this.closed) {
            throw new IllegalStateException(sm.getString("upgrade.sos.write.closed"));
        }
        this.listener = listener;
        synchronized (this.registeredLock) {
            this.registered = true;
            Request request = this.processor.getRequest();
            if (request != null && request.isRequestThread()) {
                this.processor.addDispatch(DispatchType.NON_BLOCKING_WRITE);
            } else {
                this.socketWrapper.registerWriteInterest();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isClosed() {
        return this.closed;
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        synchronized (this.writeLock) {
            preWriteChecks();
            writeInternal(new byte[]{(byte) b}, 0, 1);
        }
    }

    @Override // java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        synchronized (this.writeLock) {
            preWriteChecks();
            writeInternal(b, off, len);
        }
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        preWriteChecks();
        flushInternal(this.listener == null, true);
    }

    private void flushInternal(boolean block, boolean updateFlushing) throws IOException {
        try {
            synchronized (this.writeLock) {
                if (updateFlushing) {
                    this.flushing = this.socketWrapper.flush(block);
                    if (this.flushing) {
                        this.socketWrapper.registerWriteInterest();
                    }
                } else {
                    this.socketWrapper.flush(block);
                }
            }
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            onError(t);
            if (t instanceof IOException) {
                throw ((IOException) t);
            }
            throw new IOException(t);
        }
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.closed = true;
        flushInternal(this.listener == null, false);
    }

    private void preWriteChecks() {
        if (this.listener != null && !this.socketWrapper.canWrite()) {
            throw new IllegalStateException(sm.getString("upgrade.sos.write.ise"));
        }
        if (this.closed) {
            throw new IllegalStateException(sm.getString("upgrade.sos.write.closed"));
        }
    }

    private void writeInternal(byte[] b, int off, int len) throws IOException {
        if (this.listener == null) {
            this.socketWrapper.write(true, b, off, len);
        } else {
            this.socketWrapper.write(false, b, off, len);
        }
        this.upgradeInfo.addBytesSent(len);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void onWritePossible() {
        try {
            if (this.flushing) {
                flushInternal(false, true);
                if (this.flushing) {
                    return;
                }
            } else {
                flushInternal(false, false);
            }
            boolean fire = false;
            synchronized (this.registeredLock) {
                if (this.socketWrapper.isReadyForWrite()) {
                    this.registered = false;
                    fire = true;
                } else {
                    this.registered = true;
                }
            }
            if (fire) {
                ClassLoader oldCL = this.processor.getUpgradeToken().getContextBind().bind(false, null);
                try {
                    try {
                        this.listener.onWritePossible();
                        this.processor.getUpgradeToken().getContextBind().unbind(false, oldCL);
                    } catch (Throwable t) {
                        ExceptionUtils.handleThrowable(t);
                        onError(t);
                        this.processor.getUpgradeToken().getContextBind().unbind(false, oldCL);
                    }
                } catch (Throwable th) {
                    this.processor.getUpgradeToken().getContextBind().unbind(false, oldCL);
                    throw th;
                }
            }
        } catch (IOException ioe) {
            onError(ioe);
        }
    }

    private void onError(Throwable t) {
        if (this.listener == null) {
            return;
        }
        ClassLoader oldCL = this.processor.getUpgradeToken().getContextBind().bind(false, null);
        try {
            try {
                this.listener.onError(t);
                this.processor.getUpgradeToken().getContextBind().unbind(false, oldCL);
            } catch (Throwable t2) {
                ExceptionUtils.handleThrowable(t2);
                log.warn(sm.getString("upgrade.sos.onErrorFail"), t2);
                this.processor.getUpgradeToken().getContextBind().unbind(false, oldCL);
            }
            try {
                close();
            } catch (IOException ioe) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("upgrade.sos.errorCloseFail"), ioe);
                }
            }
        } catch (Throwable th) {
            this.processor.getUpgradeToken().getContextBind().unbind(false, oldCL);
            throw th;
        }
    }
}
