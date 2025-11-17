package ch.qos.logback.classic.net.server;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.net.server.Client;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/net/server/RemoteAppenderClient.class */
interface RemoteAppenderClient extends Client {
    void setLoggerContext(LoggerContext loggerContext);
}
