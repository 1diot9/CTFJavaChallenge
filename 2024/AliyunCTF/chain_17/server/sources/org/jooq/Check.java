package org.jooq;

import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Check.class */
public interface Check<R extends Record> extends Named {
    Table<R> getTable();

    Condition condition();

    Constraint constraint();

    boolean enforced();
}
