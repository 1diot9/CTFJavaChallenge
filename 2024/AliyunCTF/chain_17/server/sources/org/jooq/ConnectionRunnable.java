package org.jooq;

import java.sql.Connection;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ConnectionRunnable.class */
public interface ConnectionRunnable {
    void run(Connection connection) throws Throwable;
}
