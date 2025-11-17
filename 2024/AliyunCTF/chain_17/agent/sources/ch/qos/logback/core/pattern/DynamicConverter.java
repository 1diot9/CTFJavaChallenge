package ch.qos.logback.core.pattern;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.status.Status;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/pattern/DynamicConverter.class */
public abstract class DynamicConverter<E> extends FormattingConverter<E> implements LifeCycle, ContextAware {
    private List<String> optionList;
    ContextAwareBase cab = new ContextAwareBase(this);
    protected boolean started = false;

    public void start() {
        this.started = true;
    }

    public void stop() {
        this.started = false;
    }

    @Override // ch.qos.logback.core.spi.LifeCycle
    public boolean isStarted() {
        return this.started;
    }

    public void setOptionList(List<String> optionList) {
        this.optionList = optionList;
    }

    public String getFirstOption() {
        if (this.optionList == null || this.optionList.size() == 0) {
            return null;
        }
        return this.optionList.get(0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<String> getOptionList() {
        return this.optionList;
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void setContext(Context context) {
        this.cab.setContext(context);
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public Context getContext() {
        return this.cab.getContext();
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addStatus(Status status) {
        this.cab.addStatus(status);
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addInfo(String msg) {
        this.cab.addInfo(msg);
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addInfo(String msg, Throwable ex) {
        this.cab.addInfo(msg, ex);
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addWarn(String msg) {
        this.cab.addWarn(msg);
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addWarn(String msg, Throwable ex) {
        this.cab.addWarn(msg, ex);
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addError(String msg) {
        this.cab.addError(msg);
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addError(String msg, Throwable ex) {
        this.cab.addError(msg, ex);
    }
}
