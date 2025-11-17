package org.apache.logging.log4j.status;

import java.io.Closeable;
import java.util.EventListener;
import org.apache.logging.log4j.Level;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/status/StatusListener.class */
public interface StatusListener extends Closeable, EventListener {
    void log(StatusData data);

    Level getStatusLevel();
}
