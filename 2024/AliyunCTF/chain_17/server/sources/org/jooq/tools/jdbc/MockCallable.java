package org.jooq.tools.jdbc;

import org.jooq.Configuration;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/jdbc/MockCallable.class */
public interface MockCallable<T> {
    T run(Configuration configuration) throws Exception;
}
