package ch.qos.logback.core.net.server;

import java.io.Closeable;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/server/Client.class */
public interface Client extends Runnable, Closeable {
    void close();
}
