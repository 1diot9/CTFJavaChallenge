package ch.qos.logback.core.filter;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.spi.LifeCycle;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/filter/Filter.class */
public abstract class Filter<E> extends ContextAwareBase implements LifeCycle {
    private String name;
    boolean start = false;

    public abstract FilterReply decide(E e);

    public void start() {
        this.start = true;
    }

    @Override // ch.qos.logback.core.spi.LifeCycle
    public boolean isStarted() {
        return this.start;
    }

    @Override // ch.qos.logback.core.spi.LifeCycle
    public void stop() {
        this.start = false;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
