package org.jooq.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.QueryPart;
import org.jooq.SQLDialect;
import org.jooq.Table;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TableList.class */
public final class TableList extends QueryPartList<Table<?>> {
    private static final Set<SQLDialect> UNQUALIFY_FIELDS = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jooq.impl.QueryPartCollectionView
    public /* bridge */ /* synthetic */ void acceptElement(Context context, QueryPart queryPart) {
        acceptElement((Context<?>) context, (Table<?>) queryPart);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TableList() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TableList(List<? extends Table<?>> wrappedList) {
        super(wrappedList);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @SafeVarargs
    public TableList(Table<?>... wrappedList) {
        super(wrappedList);
    }

    @Override // org.jooq.impl.QueryPartCollectionView, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean rendersContent(Context<?> ctx) {
        return true;
    }

    protected void acceptElement(Context<?> ctx, Table<?> part) {
        Tools.visitAutoAliased(ctx, part, (v0) -> {
            return v0.declareTables();
        }, (c, t) -> {
            super.acceptElement((Context<?>) c, (Context) t);
        });
    }

    @Override // org.jooq.impl.QueryPartListView, org.jooq.impl.QueryPartCollectionView
    protected void toSQLEmptyList(Context<?> ctx) {
        ctx.visit(new Dual());
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresTables() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final List<Field<?>> fields() {
        return Tools.flatMap(this, t -> {
            return Arrays.asList(t.fieldsRow().fields());
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void toSQLFields(Context<?> ctx) {
        ctx.qualify(!UNQUALIFY_FIELDS.contains(ctx.dialect()) && ctx.qualify(), c -> {
            String sep = "";
            Iterator<T> it = iterator();
            while (it.hasNext()) {
                Table<?> table = (Table) it.next();
                for (Field<?> field : table.fieldsRow().fields()) {
                    ctx.sql(sep);
                    ctx.visit(field);
                    sep = ", ";
                }
            }
        });
    }
}
