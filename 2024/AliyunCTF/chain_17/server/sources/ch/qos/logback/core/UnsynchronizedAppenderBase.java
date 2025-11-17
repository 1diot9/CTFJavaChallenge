package ch.qos.logback.core;

import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.FilterAttachableImpl;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.status.WarnStatus;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/UnsynchronizedAppenderBase.class */
public abstract class UnsynchronizedAppenderBase<E> extends ContextAwareBase implements Appender<E> {
    protected String name;
    static final int ALLOWED_REPEATS = 3;
    protected boolean started = false;
    private ThreadLocal<Boolean> guard = new ThreadLocal<>();
    private FilterAttachableImpl<E> fai = new FilterAttachableImpl<>();
    private int statusRepeatCount = 0;
    private int exceptionCount = 0;

    protected abstract void append(E e);

    @Override // ch.qos.logback.core.Appender
    public String getName() {
        return this.name;
    }

    @Override // ch.qos.logback.core.Appender
    public void doAppend(E eventObject) {
        try {
            if (Boolean.TRUE.equals(this.guard.get())) {
                return;
            }
            this.guard.set(Boolean.TRUE);
            if (this.started) {
                if (getFilterChainDecision(eventObject) == FilterReply.DENY) {
                    return;
                }
                append(eventObject);
            } else {
                int i = this.statusRepeatCount;
                this.statusRepeatCount = i + 1;
                if (i < 3) {
                    addStatus(new WarnStatus("Attempted to append to non started appender [" + this.name + "].", this));
                }
            }
        } catch (Exception e) {
            int i2 = this.exceptionCount;
            this.exceptionCount = i2 + 1;
            if (i2 < 3) {
                addError("Appender [" + this.name + "] failed to append.", e);
            }
        } finally {
            this.guard.set(Boolean.FALSE);
        }
    }

    @Override // ch.qos.logback.core.Appender
    public void setName(String name) {
        this.name = name;
    }

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

    public String toString() {
        return getClass().getName() + "[" + this.name + "]";
    }

    @Override // ch.qos.logback.core.spi.FilterAttachable
    public void addFilter(Filter<E> newFilter) {
        this.fai.addFilter(newFilter);
    }

    @Override // ch.qos.logback.core.spi.FilterAttachable
    public void clearAllFilters() {
        this.fai.clearAllFilters();
    }

    @Override // ch.qos.logback.core.spi.FilterAttachable
    public List<Filter<E>> getCopyOfAttachedFiltersList() {
        return this.fai.getCopyOfAttachedFiltersList();
    }

    @Override // ch.qos.logback.core.spi.FilterAttachable
    public FilterReply getFilterChainDecision(E event) {
        return this.fai.getFilterChainDecision(event);
    }
}
