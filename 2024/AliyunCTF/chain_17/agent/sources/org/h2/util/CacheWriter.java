package org.h2.util;

import org.h2.message.Trace;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/CacheWriter.class */
public interface CacheWriter {
    void writeBack(CacheObject cacheObject);

    void flushLog();

    Trace getTrace();
}
