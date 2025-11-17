package ch.qos.logback.core.net.server;

import ch.qos.logback.core.net.server.Client;
import ch.qos.logback.core.spi.ContextAware;
import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/server/ServerRunner.class */
public interface ServerRunner<T extends Client> extends ContextAware, Runnable {
    boolean isRunning();

    void stop() throws IOException;

    void accept(ClientVisitor<T> clientVisitor);
}
