package ch.qos.logback.core.pattern;

import ch.qos.logback.core.Context;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/pattern/PostCompileProcessor.class */
public interface PostCompileProcessor<E> {
    void process(Context context, Converter<E> converter);
}
