package org.apache.logging.log4j.util;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/IndexedReadOnlyStringMap.class */
public interface IndexedReadOnlyStringMap extends ReadOnlyStringMap {
    String getKeyAt(final int index);

    <V> V getValueAt(final int index);

    int indexOfKey(final String key);
}
