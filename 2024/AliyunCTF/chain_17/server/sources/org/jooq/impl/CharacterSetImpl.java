package org.jooq.impl;

import org.jooq.CharacterSet;
import org.jooq.Context;
import org.jooq.Name;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CharacterSetImpl.class */
public final class CharacterSetImpl extends AbstractQueryPart implements CharacterSet, QOM.UNotYetImplemented {
    private final Name name;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CharacterSetImpl(Name name) {
        this.name = name;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(this.name);
    }

    @Override // org.jooq.CharacterSet
    public final String getName() {
        return this.name.last();
    }
}
