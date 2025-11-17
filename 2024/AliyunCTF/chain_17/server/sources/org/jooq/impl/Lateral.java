package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.TableOptions;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Lateral.class */
public final class Lateral<R extends Record> extends AbstractTable<R> implements QOM.Lateral<R> {
    private final Table<R> table;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Lateral(Table<R> table) {
        super(TableOptions.expression(), table.getQualifiedName(), table.getSchema());
        this.table = table;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresTables() {
        return true;
    }

    @Override // org.jooq.RecordQualifier
    public final Class<? extends R> getRecordType() {
        return this.table.getRecordType();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table, org.jooq.SelectField
    public final Table<R> as(Name alias) {
        return new Lateral(this.table.as(alias));
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final Table<R> as(Name alias, Name... fieldAliases) {
        return new Lateral(this.table.as(alias, fieldAliases));
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(Keywords.K_LATERAL).sql(' ').visit(this.table);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractTable
    public final FieldsImpl<R> fields0() {
        return new FieldsImpl<>(this.table.fields());
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.impl.QOM.Aliasable
    public final Table<R> $aliased() {
        return DSL.lateral((TableLike) ((QOM.Aliasable) this.table).$aliased());
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.impl.QOM.Aliasable
    public final Name $alias() {
        return ((QOM.Aliasable) this.table).$alias();
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Table<R>, ? extends QOM.Lateral<R>> $constructor() {
        return t -> {
            return new Lateral(t);
        };
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final Table<R> $arg1() {
        return this.table;
    }
}
