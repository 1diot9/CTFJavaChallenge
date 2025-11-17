package ch.qos.logback.core.read;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.helpers.CyclicBuffer;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/read/CyclicBufferAppender.class */
public class CyclicBufferAppender<E> extends AppenderBase<E> {
    CyclicBuffer<E> cb;
    int maxSize = 512;

    @Override // ch.qos.logback.core.AppenderBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        this.cb = new CyclicBuffer<>(this.maxSize);
        super.start();
    }

    @Override // ch.qos.logback.core.AppenderBase, ch.qos.logback.core.spi.LifeCycle
    public void stop() {
        this.cb = null;
        super.stop();
    }

    @Override // ch.qos.logback.core.AppenderBase
    protected void append(E eventObject) {
        if (!isStarted()) {
            return;
        }
        this.cb.add(eventObject);
    }

    public int getLength() {
        if (isStarted()) {
            return this.cb.length();
        }
        return 0;
    }

    public E get(int i) {
        if (isStarted()) {
            return this.cb.get(i);
        }
        return null;
    }

    public void reset() {
        this.cb.clear();
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
}
