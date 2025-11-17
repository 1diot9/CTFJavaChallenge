package ch.qos.logback.core.recovery;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/recovery/ResilientOutputStreamBase.class */
public abstract class ResilientOutputStreamBase extends OutputStream {
    static final int STATUS_COUNT_LIMIT = 8;
    private Context context;
    private RecoveryCoordinator recoveryCoordinator;
    protected OutputStream os;
    private int noContextWarning = 0;
    private int statusCount = 0;
    protected boolean presumedClean = true;
    List<RecoveryListener> recoveryListeners = new ArrayList(0);

    abstract String getDescription();

    abstract OutputStream openNewOutputStream() throws IOException;

    private boolean isPresumedInError() {
        return (this.recoveryCoordinator == null || this.presumedClean) ? false : true;
    }

    public void addRecoveryListener(RecoveryListener listener) {
        this.recoveryListeners.add(listener);
    }

    public void removeRecoveryListener(RecoveryListener listener) {
        this.recoveryListeners.remove(listener);
    }

    @Override // java.io.OutputStream
    public void write(byte[] b, int off, int len) {
        if (isPresumedInError()) {
            if (!this.recoveryCoordinator.isTooSoon()) {
                attemptRecovery();
            }
        } else {
            try {
                this.os.write(b, off, len);
                postSuccessfulWrite();
            } catch (IOException e) {
                postIOFailure(e);
            }
        }
    }

    @Override // java.io.OutputStream
    public void write(int b) {
        if (isPresumedInError()) {
            if (!this.recoveryCoordinator.isTooSoon()) {
                attemptRecovery();
            }
        } else {
            try {
                this.os.write(b);
                postSuccessfulWrite();
            } catch (IOException e) {
                postIOFailure(e);
            }
        }
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() {
        if (this.os != null) {
            try {
                this.os.flush();
                postSuccessfulWrite();
            } catch (IOException e) {
                postIOFailure(e);
            }
        }
    }

    private void postSuccessfulWrite() {
        if (this.recoveryCoordinator != null) {
            this.recoveryCoordinator = null;
            this.statusCount = 0;
            this.recoveryListeners.forEach(listener -> {
                listener.recoveryOccured();
            });
            addStatus(new InfoStatus("Recovered from IO failure on " + getDescription(), this));
        }
    }

    public void postIOFailure(IOException e) {
        addStatusIfCountNotOverLimit(new ErrorStatus("IO failure while writing to " + getDescription(), this, e));
        this.presumedClean = false;
        if (this.recoveryCoordinator == null) {
            this.recoveryCoordinator = new RecoveryCoordinator();
            this.recoveryListeners.forEach(listener -> {
                listener.newFailure(e);
            });
        }
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.os != null) {
            this.os.close();
        }
    }

    void attemptRecovery() {
        try {
            close();
        } catch (IOException e) {
        }
        addStatusIfCountNotOverLimit(new InfoStatus("Attempting to recover from IO failure on " + getDescription(), this));
        try {
            this.os = openNewOutputStream();
            this.presumedClean = true;
        } catch (IOException e2) {
            addStatusIfCountNotOverLimit(new ErrorStatus("Failed to open " + getDescription(), this, e2));
        }
    }

    void addStatusIfCountNotOverLimit(Status s) {
        this.statusCount++;
        if (this.statusCount < 8) {
            addStatus(s);
        }
        if (this.statusCount == 8) {
            addStatus(s);
            addStatus(new InfoStatus("Will supress future messages regarding " + getDescription(), this));
        }
    }

    public void addStatus(Status status) {
        if (this.context == null) {
            int i = this.noContextWarning;
            this.noContextWarning = i + 1;
            if (i == 0) {
                System.out.println("LOGBACK: No context given for " + String.valueOf(this));
                return;
            }
            return;
        }
        StatusManager sm = this.context.getStatusManager();
        if (sm != null) {
            sm.add(status);
        }
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
