package org.apache.logging.log4j.spi;

import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/spi/ObjectThreadContextMap.class */
public interface ObjectThreadContextMap extends CleanableThreadContextMap {
    <V> V getValue(String key);

    <V> void putValue(String key, V value);

    <V> void putAllValues(Map<String, V> values);
}
