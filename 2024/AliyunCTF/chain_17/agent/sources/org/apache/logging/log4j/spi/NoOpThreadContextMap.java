package org.apache.logging.log4j.spi;

import java.util.HashMap;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/spi/NoOpThreadContextMap.class */
public class NoOpThreadContextMap implements ThreadContextMap {
    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public void clear() {
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public boolean containsKey(final String key) {
        return false;
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public String get(final String key) {
        return null;
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public Map<String, String> getCopy() {
        return new HashMap();
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public Map<String, String> getImmutableMapOrNull() {
        return null;
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public boolean isEmpty() {
        return true;
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public void put(final String key, final String value) {
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public void remove(final String key) {
    }
}
