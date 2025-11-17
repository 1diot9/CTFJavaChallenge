package ch.qos.logback.core.spi;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.WarnStatus;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/spi/ContextAwareImpl.class */
public class ContextAwareImpl implements ContextAware {
    private int noContextWarning = 0;
    protected Context context;
    final Object origin;

    public ContextAwareImpl(Context context, Object origin) {
        this.context = context;
        this.origin = origin;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object getOrigin() {
        return this.origin;
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
        addStatus(new InfoStatus(msg, getOrigin()));
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addInfo(String msg, Throwable ex) {
        addStatus(new InfoStatus(msg, getOrigin(), ex));
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addWarn(String msg) {
        addStatus(new WarnStatus(msg, getOrigin()));
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addWarn(String msg, Throwable ex) {
        addStatus(new WarnStatus(msg, getOrigin(), ex));
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addError(String msg) {
        addStatus(new ErrorStatus(msg, getOrigin()));
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addError(String msg, Throwable ex) {
        addStatus(new ErrorStatus(msg, getOrigin(), ex));
    }
}
