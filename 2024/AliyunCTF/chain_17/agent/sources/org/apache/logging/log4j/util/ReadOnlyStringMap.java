package org.apache.logging.log4j.util;

import java.io.Serializable;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/ReadOnlyStringMap.class */
public interface ReadOnlyStringMap extends Serializable {
    Map<String, String> toMap();

    boolean containsKey(String key);

    <V> void forEach(final BiConsumer<String, ? super V> action);

    <V, S> void forEach(final TriConsumer<String, ? super V, S> action, final S state);

    <V> V getValue(final String key);

    boolean isEmpty();

    int size();
}
