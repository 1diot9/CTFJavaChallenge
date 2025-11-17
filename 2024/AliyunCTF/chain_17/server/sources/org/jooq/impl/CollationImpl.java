package org.jooq.impl;

import org.jooq.Collation;
import org.jooq.Context;
import org.jooq.Name;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CollationImpl.class */
public final class CollationImpl extends AbstractQueryPart implements Collation, QOM.UNotYetImplemented {
    private final Name name;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CollationImpl(Name name) {
        this.name = name;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(this.name);
    }

    @Override // org.jooq.Collation
    public final String getName() {
        return this.name.last();
    }
}
