package ch.qos.logback.core.spi;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.WarnStatus;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/spi/ContextAwareBase.class */
public class ContextAwareBase implements ContextAware {
    private int noContextWarning;
    protected Context context;
    final Object declaredOrigin;

    public ContextAwareBase() {
        this.noContextWarning = 0;
        this.declaredOrigin = this;
    }

    public ContextAwareBase(ContextAware declaredOrigin) {
        this.noContextWarning = 0;
        this.declaredOrigin = declaredOrigin;
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void setContext(Context context) {
        if (this.context == null) {
            this.context = context;
        } else if (this.context != context) {
            throw new IllegalStateException("Context has been already set");
        }
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public Context getContext() {
        return this.context;
    }

    public StatusManager getStatusManager() {
        if (this.context == null) {
            return null;
        }
        return this.context.getStatusManager();
    }

    protected Object getDeclaredOrigin() {
        return this.declaredOrigin;
    }

    @Override // ch.qos.logback.core.spi.ContextAware
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

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addInfo(String msg) {
        addStatus(new InfoStatus(msg, getDeclaredOrigin()));
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addInfo(String msg, Throwable ex) {
        addStatus(new InfoStatus(msg, getDeclaredOrigin(), ex));
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addWarn(String msg) {
        addStatus(new WarnStatus(msg, getDeclaredOrigin()));
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addWarn(String msg, Throwable ex) {
        addStatus(new WarnStatus(msg, getDeclaredOrigin(), ex));
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addError(String msg) {
        addStatus(new ErrorStatus(msg, getDeclaredOrigin()));
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addError(String msg, Throwable ex) {
        addStatus(new ErrorStatus(msg, getDeclaredOrigin(), ex));
    }
}
