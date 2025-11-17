package org.slf4j.spi;

import java.util.function.Supplier;
import org.slf4j.Marker;
import org.slf4j.helpers.CheckReturnValue;

/* loaded from: server.jar:BOOT-INF/lib/slf4j-api-2.0.11.jar:org/slf4j/spi/LoggingEventBuilder.class */
public interface LoggingEventBuilder {
    @CheckReturnValue
    LoggingEventBuilder setCause(Throwable th);

    @CheckReturnValue
    LoggingEventBuilder addMarker(Marker marker);

    @CheckReturnValue
    LoggingEventBuilder addArgument(Object obj);

    @CheckReturnValue
    LoggingEventBuilder addArgument(Supplier<?> supplier);

    @CheckReturnValue
    LoggingEventBuilder addKeyValue(String str, Object obj);

    @CheckReturnValue
    LoggingEventBuilder addKeyValue(String str, Supplier<Object> supplier);

    @CheckReturnValue
    LoggingEventBuilder setMessage(String str);

    @CheckReturnValue
    LoggingEventBuilder setMessage(Supplier<String> supplier);

    void log();

    void log(String str);

    void log(String str, Object obj);

    void log(String str, Object obj, Object obj2);

    void log(String str, Object... objArr);

    void log(Supplier<String> supplier);
}
