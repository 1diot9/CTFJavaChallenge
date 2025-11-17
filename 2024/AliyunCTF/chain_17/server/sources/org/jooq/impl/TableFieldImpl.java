package org.jooq.impl;

import org.jooq.Binding;
import org.jooq.Clause;
import org.jooq.Comment;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.DMLQuery;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.Update;
import org.jooq.conf.RenderImplicitJoinType;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TableFieldImpl.class */
public final class TableFieldImpl<R extends Record, T> extends AbstractField<T> implements TableField<R, T>, SimpleQueryPart, TypedReference<T>, ScopeMappable, QOM.UEmpty {
    private static final Clause[] CLAUSES = {Clause.FIELD, Clause.FIELD_REFERENCE};
    private final Table<R> table;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TableFieldImpl(Name name, DataType<T> type, Comment comment) {
        this(name, type, table(name), comment, type.getBinding());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TableFieldImpl(Name name, DataType<T> type, Table<R> table, Comment comment, Binding<?, T> binding) {
        super(qualify(table, name), type, comment, binding);
        this.table = table;
    }

    private static final Table<Record> table(Name name) {
        if (name.qualified()) {
            return DSL.table(name.qualifier());
        }
        return null;
    }

    @Override // org.jooq.TableField
    public final Table<R> getTable() {
        return this.table;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        return true;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public boolean declaresFields() {
        return super.declaresFields();
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        TableImpl<?> t;
        Table<?> root;
        Condition onKey0;
        if ((this.table instanceof TableImpl) && (root = (t = (TableImpl) this.table).pathRoot()) != null && implicitJoinAsScalarSubquery(ctx, t, root)) {
            if ((ctx.topLevelForLanguageContext() instanceof Update) && !UpdateQueryImpl.NO_SUPPORT_UPDATE_JOIN.contains(ctx.dialect()) && t.childPath != null) {
                accept1(ctx);
                return;
            }
            Table<?> parent = t.alias.wrapped.as(t);
            Field<T> parentField = parent.field(this);
            if (t.childPath != null) {
                onKey0 = JoinTable.onKey0(t.childPath, t.path, parent);
            } else {
                onKey0 = JoinTable.onKey0(t.parentPath.getForeignKey(), parent, t.path);
            }
            Condition c = onKey0;
            ctx.visit(DSL.field(DSL.select(parentField).from(parent).where(c)));
            return;
        }
        accept1(ctx);
    }

    private final boolean implicitJoinAsScalarSubquery(Context<?> ctx, TableImpl<?> t, Table<?> root) {
        return !Boolean.TRUE.equals(ctx.data(Tools.BooleanDataKey.DATA_RENDER_IMPLICIT_JOIN)) && (!(t.childPath == null || ctx.settings().getRenderImplicitJoinType() != RenderImplicitJoinType.SCALAR_SUBQUERY || ctx.inScope(t)) || (!(t.parentPath == null || ctx.settings().getRenderImplicitJoinToManyType() != RenderImplicitJoinType.SCALAR_SUBQUERY || ctx.inScope(t)) || ((ctx.topLevelForLanguageContext() instanceof DMLQuery) && (!ctx.subquery() || root.equals(ctx.data(Tools.SimpleDataKey.DATA_DML_TARGET_TABLE))))));
    }

    private final void accept1(Context<?> ctx) {
        accept2(ctx, getTable(), getUnqualifiedName());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void accept2(Context<?> ctx, Table<?> table, Name unqualifiedName) {
        ctx.data(Tools.BooleanDataKey.DATA_OMIT_CLAUSE_EVENT_EMISSION, true, c -> {
            if (c.qualify() && table != null && !Boolean.FALSE.equals(ctx.data(Tools.ExtendedDataKey.DATA_RENDER_TABLE))) {
                if ((table instanceof TableImpl) && ((TableImpl) table).where != null) {
                    c.qualify(false, c2 -> {
                        c2.visit(table).sql('.');
                    });
                } else {
                    c.visit(table).sql('.');
                }
            }
            c.visit(unqualifiedName);
        });
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public int hashCode() {
        if (getTable() == null) {
            return getUnqualifiedName().hashCode();
        }
        return ((Schema) StringUtils.defaultIfNull(getTable().getSchema(), SchemaImpl.DEFAULT_SCHEMA.get())).getQualifiedName().append(getTable().getUnqualifiedName()).append(getUnqualifiedName()).hashCode();
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that instanceof TableField) {
            TableField<?, ?> other = (TableField) that;
            return StringUtils.equals(getTable(), other.getTable()) && StringUtils.equals(getName(), other.getName());
        }
        return super.equals(that);
    }
}
