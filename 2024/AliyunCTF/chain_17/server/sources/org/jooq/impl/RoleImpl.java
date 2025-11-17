package org.jooq.impl;

import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.Name;
import org.jooq.Role;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RoleImpl.class */
public final class RoleImpl extends AbstractNamed implements Role {
    private static final Clause[] CLAUSES = {Clause.ROLE};

    /* JADX INFO: Access modifiers changed from: package-private */
    public RoleImpl(Name name) {
        super(name, CommentImpl.NO_COMMENT);
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(getQualifiedName());
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }
}
