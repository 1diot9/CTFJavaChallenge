package ch.qos.logback.core.sift;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.JoranException;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/sift/AppenderFactory.class */
public interface AppenderFactory<E> {
    Appender<E> buildAppender(Context context, String str) throws JoranException;
}
