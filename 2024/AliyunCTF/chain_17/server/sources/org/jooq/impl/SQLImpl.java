package org.jooq.impl;

import java.util.List;
import java.util.Objects;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.QueryPart;
import org.jooq.SQL;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SQLImpl.class */
public final class SQLImpl extends AbstractQueryPart implements SQL, QOM.UEmpty {
    private static final Clause[] CLAUSES = {Clause.TEMPLATE};
    final String sql;
    final boolean isName;
    final boolean raw;
    final List<QueryPart> substitutes;

    /* renamed from: org.jooq.impl.SQLImpl$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SQLImpl$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SQLImpl(String sql, boolean raw, Object... input) {
        this.sql = (String) Objects.requireNonNull(sql);
        this.raw = raw;
        this.substitutes = Tools.queryParts(input);
        this.isName = this.substitutes.isEmpty() && isName(sql);
    }

    static final boolean isName(String sql) {
        int l = sql.length();
        if (l == 0 || !Character.isJavaIdentifierStart(sql.charAt(0))) {
            return false;
        }
        for (int i = 1; i < l; i++) {
            if (!Character.isJavaIdentifierPart(sql.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                if (this.raw) {
                    ctx.sql(this.sql);
                    return;
                } else {
                    Tools.renderAndBind(ctx, this.sql, this.substitutes);
                    return;
                }
        }
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public String toString() {
        return this.sql;
    }
}
