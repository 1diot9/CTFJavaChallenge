package ch.qos.logback.core.net.server;

import ch.qos.logback.core.net.server.Client;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/server/ClientVisitor.class */
public interface ClientVisitor<T extends Client> {
    void visit(T t);
}
