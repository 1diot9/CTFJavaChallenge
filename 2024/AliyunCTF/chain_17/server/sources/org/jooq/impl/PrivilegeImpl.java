package org.jooq.impl;

import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.Keyword;
import org.jooq.Privilege;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/PrivilegeImpl.class */
public final class PrivilegeImpl extends AbstractQueryPart implements Privilege, SimpleQueryPart, QOM.UEmpty {
    private static final Clause[] CLAUSES = {Clause.PRIVILEGE};
    private final Keyword privilege;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PrivilegeImpl(Keyword privilege) {
        this.privilege = privilege;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(this.privilege);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }
}
