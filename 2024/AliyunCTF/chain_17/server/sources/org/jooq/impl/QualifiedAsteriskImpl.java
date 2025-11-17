package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.QualifiedAsterisk;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QualifiedAsteriskImpl.class */
final class QualifiedAsteriskImpl extends AbstractQueryPart implements QualifiedAsterisk {
    private final Table<?> table;
    final QueryPartList<Field<?>> fields;

    /* renamed from: org.jooq.impl.QualifiedAsteriskImpl$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QualifiedAsteriskImpl$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public QualifiedAsteriskImpl(Table<?> table) {
        this(table, null);
    }

    QualifiedAsteriskImpl(Table<?> table, QueryPartList<Field<?>> fields) {
        this.table = table;
        this.fields = fields == null ? new QueryPartList<>() : fields;
    }

    /* JADX WARN: Type inference failed for: r0v10, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(this.table).sql('.').visit(AsteriskImpl.INSTANCE.get());
                if (!this.fields.isEmpty()) {
                    ctx.sql(' ').visit(Keywords.K_EXCEPT).sql(" (").visit(this.fields).sql(')');
                    return;
                }
                return;
        }
    }

    @Override // org.jooq.QualifiedAsterisk
    public final Table<?> qualifier() {
        return this.table;
    }

    @Override // org.jooq.QualifiedAsterisk
    public final QualifiedAsterisk except(String... fieldNames) {
        return except(Tools.fieldsByName(fieldNames));
    }

    @Override // org.jooq.QualifiedAsterisk
    public final QualifiedAsterisk except(Name... fieldNames) {
        return except(Tools.fieldsByName(fieldNames));
    }

    @Override // org.jooq.QualifiedAsterisk
    public final QualifiedAsterisk except(Field<?>... f) {
        return except(Arrays.asList(f));
    }

    @Override // org.jooq.QualifiedAsterisk
    public final QualifiedAsterisk except(Collection<? extends Field<?>> f) {
        QueryPartList<Field<?>> list = new QueryPartList<>();
        list.addAll(this.fields);
        list.addAll(f);
        return new QualifiedAsteriskImpl(this.table, list);
    }

    @Override // org.jooq.QualifiedAsterisk
    public final Table<?> $table() {
        return this.table;
    }

    @Override // org.jooq.QualifiedAsterisk
    public final QOM.UnmodifiableList<? extends Field<?>> $except() {
        return QOM.unmodifiable((List) this.fields);
    }
}
