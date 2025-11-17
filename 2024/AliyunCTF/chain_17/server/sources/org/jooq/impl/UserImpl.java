package org.jooq.impl;

import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.Name;
import org.jooq.User;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/UserImpl.class */
public final class UserImpl extends AbstractNamed implements User {
    private static final Clause[] CLAUSES = {Clause.USER};

    /* JADX INFO: Access modifiers changed from: package-private */
    public UserImpl(Name name) {
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
