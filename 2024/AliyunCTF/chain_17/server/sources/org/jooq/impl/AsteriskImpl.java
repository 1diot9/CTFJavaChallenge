package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.jooq.Asterisk;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Keyword;
import org.jooq.Name;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AsteriskImpl.class */
public final class AsteriskImpl extends AbstractQueryPart implements Asterisk {
    static final Lazy<AsteriskImpl> INSTANCE = Lazy.of(() -> {
        return new AsteriskImpl(new QueryPartList());
    });
    static final Set<SQLDialect> SUPPORT_NATIVE_EXCEPT = SQLDialect.supportedBy(SQLDialect.H2);
    static final Set<SQLDialect> NO_SUPPORT_UNQUALIFIED_COMBINED = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL);
    final QueryPartList<Field<?>> fields;

    private AsteriskImpl(QueryPartList<Field<?>> fields) {
        this.fields = fields;
    }

    /* JADX WARN: Type inference failed for: r0v6, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.sql('*');
        if (!this.fields.isEmpty()) {
            ctx.sql(' ').visit(Keywords.K_EXCEPT).sql(" (").visit(this.fields).sql(')');
        }
    }

    static final Keyword keyword(Context<?> ctx) {
        switch (ctx.family()) {
            case DUCKDB:
                return Keywords.K_EXCLUDE;
            default:
                return Keywords.K_EXCEPT;
        }
    }

    @Override // org.jooq.Asterisk
    public final Asterisk except(String... fieldNames) {
        return except(Tools.fieldsByName(fieldNames));
    }

    @Override // org.jooq.Asterisk
    public final Asterisk except(Name... fieldNames) {
        return except(Tools.fieldsByName(fieldNames));
    }

    @Override // org.jooq.Asterisk
    public final Asterisk except(Field<?>... f) {
        return except(Arrays.asList(f));
    }

    @Override // org.jooq.Asterisk
    public final Asterisk except(Collection<? extends Field<?>> f) {
        QueryPartList<Field<?>> list = new QueryPartList<>();
        list.addAll(this.fields);
        list.addAll(f);
        return new AsteriskImpl(list);
    }

    @Override // org.jooq.Asterisk
    public final QOM.UnmodifiableList<? extends Field<?>> $except() {
        return QOM.unmodifiable((List) this.fields);
    }
}
