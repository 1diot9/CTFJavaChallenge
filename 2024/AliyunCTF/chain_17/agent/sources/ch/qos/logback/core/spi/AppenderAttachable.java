package ch.qos.logback.core.spi;

import ch.qos.logback.core.Appender;
import java.util.Iterator;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/spi/AppenderAttachable.class */
public interface AppenderAttachable<E> {
    void addAppender(Appender<E> appender);

    Iterator<Appender<E>> iteratorForAppenders();

    Appender<E> getAppender(String str);

    boolean isAttached(Appender<E> appender);

    void detachAndStopAllAppenders();

    boolean detachAppender(Appender<E> appender);

    boolean detachAppender(String str);
}
