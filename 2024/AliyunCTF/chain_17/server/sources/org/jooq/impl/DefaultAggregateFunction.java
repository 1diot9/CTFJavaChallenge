package org.jooq.impl;

import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Name;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultAggregateFunction.class */
public final class DefaultAggregateFunction<T> extends AbstractAggregateFunction<T> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultAggregateFunction(String name, DataType<T> type, Field<?>... arguments) {
        super(name, type, arguments);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultAggregateFunction(Name name, DataType<T> type, Field<?>... arguments) {
        super(name, type, arguments);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultAggregateFunction(boolean distinct, String name, DataType<T> type, Field<?>... arguments) {
        super(distinct, name, type, arguments);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultAggregateFunction(boolean distinct, Name name, DataType<T> type, Field<?>... arguments) {
        super(distinct, name, type, arguments);
    }
}
