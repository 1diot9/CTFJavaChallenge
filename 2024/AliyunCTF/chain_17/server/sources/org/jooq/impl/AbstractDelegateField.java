package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractDelegateField.class */
abstract class AbstractDelegateField<T> extends AbstractField<T> implements QOM.UTransient {
    private Field<T> delegate;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractDelegateField(Field<T> delegate) {
        super(delegate.getQualifiedName(), delegate.getDataType(), delegate.getCommentPart());
        this.delegate = delegate;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public void accept(Context<?> ctx) {
        ctx.visit((Field<?>) this.delegate);
    }
}
