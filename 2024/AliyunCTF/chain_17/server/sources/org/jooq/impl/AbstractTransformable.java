package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Name;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractTransformable.class */
public abstract class AbstractTransformable<T> extends AbstractField<T> implements Transformable<T> {
    abstract void accept0(Context<?> context);

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractTransformable(Name name, DataType<T> type) {
        super(name, type);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        accept0(ctx);
    }
}
