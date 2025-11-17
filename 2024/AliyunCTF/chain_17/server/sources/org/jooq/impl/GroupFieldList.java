package org.jooq.impl;

import java.util.Set;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.GroupField;
import org.jooq.QueryPart;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.UniqueKey;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/GroupFieldList.class */
public final class GroupFieldList extends QueryPartList<GroupField> {
    static final Set<SQLDialect> NO_SUPPORT_GROUP_BY_TABLE = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);
    static final Set<SQLDialect> NO_SUPPORT_GROUP_FUNCTIONAL_DEP = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.TRINO);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jooq.impl.QueryPartCollectionView
    public /* bridge */ /* synthetic */ void acceptElement(Context context, QueryPart queryPart) {
        acceptElement((Context<?>) context, (GroupField) queryPart);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GroupFieldList() {
    }

    GroupFieldList(Iterable<? extends GroupField> wrappedList) {
        super(wrappedList);
    }

    GroupFieldList(GroupField[] wrappedList) {
        super(wrappedList);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.QueryPartCollectionView
    public final boolean canAdd(GroupField e) {
        return super.canAdd((GroupFieldList) e) && !(e instanceof NoField);
    }

    @Override // org.jooq.impl.QueryPartCollectionView, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean rendersContent(Context<?> ctx) {
        return true;
    }

    @Override // org.jooq.impl.QueryPartListView, org.jooq.impl.QueryPartCollectionView
    protected final void toSQLEmptyList(Context<?> ctx) {
        ctx.visit(DSL.emptyGroupingSet());
    }

    protected final void acceptElement(Context<?> ctx, GroupField part) {
        if (part instanceof Table) {
            Table<?> t = (Table) part;
            if (NO_SUPPORT_GROUP_BY_TABLE.contains(ctx.dialect())) {
                Field<?>[] f = fields(ctx, t);
                if (f.length > 1) {
                    ctx.visit(QueryPartListView.wrap(f));
                    return;
                } else if (f.length == 1) {
                    ctx.visit(f[0]);
                    return;
                } else {
                    super.acceptElement(ctx, (Context<?>) part);
                    return;
                }
            }
            super.acceptElement(ctx, (Context<?>) part);
            return;
        }
        super.acceptElement(ctx, (Context<?>) part);
    }

    private final Field<?>[] fields(Context<?> ctx, Table<?> t) {
        UniqueKey<?> pk = t.getPrimaryKey();
        if (pk == null || NO_SUPPORT_GROUP_FUNCTIONAL_DEP.contains(ctx.dialect())) {
            return t.fields();
        }
        return t.fields(pk.getFieldsArray());
    }
}
