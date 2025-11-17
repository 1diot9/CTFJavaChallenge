package org.apache.logging.log4j.util;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/StringMap.class */
public interface StringMap extends ReadOnlyStringMap {
    void clear();

    boolean equals(final Object obj);

    void freeze();

    int hashCode();

    boolean isFrozen();

    void putAll(final ReadOnlyStringMap source);

    void putValue(final String key, final Object value);

    void remove(final String key);
}
