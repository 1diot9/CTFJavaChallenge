package org.apache.logging.log4j.spi;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/spi/CleanableThreadContextMap.class */
public interface CleanableThreadContextMap extends ThreadContextMap2 {
    void removeAll(final Iterable<String> keys);
}
