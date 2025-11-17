package org.jooq.impl;

import org.jooq.Comment;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Name;
import org.jooq.Type;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TypeImpl.class */
public class TypeImpl<T> extends AbstractTypedNamed<T> implements Type<T>, QOM.UNotYetImplemented {
    /* JADX INFO: Access modifiers changed from: package-private */
    public TypeImpl(Name name, Comment comment, DataType<T> type) {
        super(name, comment, type);
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(getQualifiedName());
    }
}
