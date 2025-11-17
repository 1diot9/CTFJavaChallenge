package org.jooq.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.OrderField;
import org.jooq.SQLDialect;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/IndexImpl.class */
public class IndexImpl extends AbstractNamed implements Index, QOM.UNotYetImplemented {
    private static final Set<SQLDialect> NO_SUPPORT_INDEX_QUALIFICATION = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    private final Table<?> table;
    private final SortField<?>[] fields;
    private final Condition where;
    private final boolean unique;

    /* JADX INFO: Access modifiers changed from: package-private */
    public IndexImpl(Name name) {
        this(name, null, Tools.EMPTY_SORTFIELD, null, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IndexImpl(Name name, Table<?> table, OrderField<?>[] fields, Condition where, boolean unique) {
        super(name.empty() ? name : qualify(table, name), CommentImpl.NO_COMMENT);
        this.table = table;
        this.fields = Tools.sortFields(fields);
        this.where = where;
        this.unique = unique;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final SortField<?>[] $fields() {
        return this.fields;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean $unique() {
        return this.unique;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Condition $where() {
        return this.where;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (NO_SUPPORT_INDEX_QUALIFICATION.contains(ctx.dialect())) {
            ctx.visit(getUnqualifiedName());
        } else if (getTable() == null) {
            ctx.visit(new QualifiedImpl(getQualifiedName()));
        } else {
            ctx.visit(new QualifiedImpl(DSL.name(getTable().getQualifiedName().qualifier(), getUnqualifiedName())));
        }
    }

    @Override // org.jooq.Index
    public final Table<?> getTable() {
        return this.table;
    }

    @Override // org.jooq.Index
    public final List<SortField<?>> getFields() {
        return Arrays.asList(this.fields);
    }

    @Override // org.jooq.Index
    public final Condition getWhere() {
        return this.where;
    }

    @Override // org.jooq.Index
    public boolean getUnique() {
        return this.unique;
    }

    @Override // org.jooq.Index
    public final Table<?> $table() {
        return this.table;
    }
}
