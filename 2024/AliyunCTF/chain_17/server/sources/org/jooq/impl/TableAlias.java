package org.jooq.impl;

import java.util.List;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TableAlias.class */
public final class TableAlias<R extends Record> extends AbstractTable<R> implements QOM.TableAlias<R>, SimpleCheckQueryPart {
    final Alias<Table<R>> alias;
    transient FieldsImpl<R> aliasedFields;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TableAlias(Table<R> table, Name alias) {
        this(table, alias, null, c -> {
            return false;
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TableAlias(Table<R> table, Name alias, Predicate<Context<?>> wrapInParentheses) {
        this(table, alias, null, wrapInParentheses);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TableAlias(Table<R> table, Name alias, Name[] fieldAliases) {
        this(table, alias, fieldAliases, c -> {
            return false;
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TableAlias(Table<R> table, Name alias, Name[] fieldAliases, Predicate<Context<?>> wrapInParentheses) {
        super(table.getOptions(), alias, table.getSchema());
        this.alias = new Alias<>(table, this, alias, fieldAliases, wrapInParentheses);
    }

    @Override // org.jooq.impl.SimpleCheckQueryPart
    public final boolean isSimple(Context<?> ctx) {
        return !ctx.declareTables();
    }

    private final FieldsImpl<R> initFieldAliases() {
        List<Field<?>> result = Tools.map(this.alias.wrapped().fieldsRow().fields(), (f, i) -> {
            Name unqualifiedName;
            if (hasFieldAliases() && this.alias.fieldAliases.length > i) {
                unqualifiedName = this.alias.fieldAliases[i];
            } else {
                unqualifiedName = f.getUnqualifiedName();
            }
            return new TableFieldImpl(unqualifiedName, Tools.removeGenerator(Tools.CONFIG.get(), f.getDataType()), this, f.getCommentPart(), f.getBinding());
        });
        return new FieldsImpl<>(result);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean hasFieldAliases() {
        return this.alias.fieldAliases != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Table<R> getAliasedTable() {
        if (this.alias != null) {
            return this.alias.wrapped();
        }
        return null;
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final UniqueKey<R> getPrimaryKey() {
        return this.alias.wrapped().getPrimaryKey();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final List<UniqueKey<R>> getUniqueKeys() {
        return this.alias.wrapped().getUniqueKeys();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final List<ForeignKey<R, ?>> getReferences() {
        return this.alias.wrapped().getReferences();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final TableField<R, ?> getRecordVersion() {
        return this.alias.wrapped().getRecordVersion();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final TableField<R, ?> getRecordTimestamp() {
        return this.alias.wrapped().getRecordTimestamp();
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(this.alias);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return null;
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table, org.jooq.SelectField
    public final Table<R> as(Name as) {
        return this.alias.wrapped().as(as);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final Table<R> as(Name as, Name... fieldAliases) {
        return this.alias.wrapped().as(as, fieldAliases);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresTables() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractTable
    public final FieldsImpl<R> fields0() {
        if (this.aliasedFields == null) {
            this.aliasedFields = initFieldAliases();
        }
        return this.aliasedFields;
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.Named
    public Name getQualifiedName() {
        return getUnqualifiedName();
    }

    @Override // org.jooq.RecordQualifier
    public Class<? extends R> getRecordType() {
        return this.alias.wrapped().getRecordType();
    }

    @Override // org.jooq.impl.QOM.TableAlias
    public final Table<R> $table() {
        return this.alias.wrapped();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.impl.QOM.Aliasable
    public final Table<R> $aliased() {
        return $table();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.impl.QOM.Aliasable
    @NotNull
    public final Name $alias() {
        return getUnqualifiedName();
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that instanceof TableAlias) {
            return getUnqualifiedName().equals(((TableAlias) that).getUnqualifiedName());
        }
        if (that instanceof TableImpl) {
            TableImpl<?> t = (TableImpl) that;
            if (t.$alias() != null) {
                return getUnqualifiedName().equals(t.$alias());
            }
            if (t.getSchema() == null) {
                return getUnqualifiedName().equals(t.getQualifiedName());
            }
        }
        return super.equals(that);
    }
}
