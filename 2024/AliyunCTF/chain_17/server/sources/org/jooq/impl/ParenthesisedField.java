package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ParenthesisedField.class */
public final class ParenthesisedField<T> extends AbstractDelegateField<T> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public ParenthesisedField(Field<T> delegate) {
        super(delegate);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        return true;
    }

    @Override // org.jooq.impl.AbstractDelegateField, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.sql('(');
        super.accept(ctx);
        ctx.sql(')');
    }
}
