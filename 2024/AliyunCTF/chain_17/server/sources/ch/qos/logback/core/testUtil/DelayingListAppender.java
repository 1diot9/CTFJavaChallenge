package ch.qos.logback.core.testUtil;

import ch.qos.logback.core.read.ListAppender;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/testUtil/DelayingListAppender.class */
public class DelayingListAppender<E> extends ListAppender<E> {
    public int delay = 1;
    public boolean interrupted = false;

    public void setDelay(int ms) {
        this.delay = ms;
    }

    @Override // ch.qos.logback.core.read.ListAppender, ch.qos.logback.core.AppenderBase
    public void append(E e) {
        try {
            Thread.sleep(this.delay);
        } catch (InterruptedException e2) {
            this.interrupted = true;
        }
        super.append(e);
    }
}
