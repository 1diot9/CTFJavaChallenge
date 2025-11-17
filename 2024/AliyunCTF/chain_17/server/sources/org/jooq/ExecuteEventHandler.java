package org.jooq;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ExecuteEventHandler.class */
public interface ExecuteEventHandler {
    void fire(ExecuteContext executeContext);
}
