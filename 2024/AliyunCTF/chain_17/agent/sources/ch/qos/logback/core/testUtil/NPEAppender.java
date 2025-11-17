package ch.qos.logback.core.testUtil;

import ch.qos.logback.core.AppenderBase;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/testUtil/NPEAppender.class */
public class NPEAppender<E> extends AppenderBase<E> {
    @Override // ch.qos.logback.core.AppenderBase
    protected void append(E eventObject) {
        throw new NullPointerException();
    }
}
