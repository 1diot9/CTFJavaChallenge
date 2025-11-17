package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Name;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableOptions;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractAutoAliasTable.class */
public abstract class AbstractAutoAliasTable<R extends Record> extends AbstractTable<R> implements AutoAlias<Table<R>> {
    final Name alias;
    final Name[] fieldAliases;

    /* renamed from: construct */
    abstract AbstractAutoAliasTable<R> construct2(Name name, Name[] nameArr);

    @Override // org.jooq.impl.AutoAlias
    public /* bridge */ /* synthetic */ QueryPart autoAlias(Context context, QueryPart queryPart) {
        return autoAlias((Context<?>) context, (Table) queryPart);
    }

    AbstractAutoAliasTable(Name alias) {
        this(alias, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractAutoAliasTable(Name alias, Name[] fieldAliases) {
        super(TableOptions.expression(), alias != null ? alias : DSL.name("t"));
        this.alias = alias;
        this.fieldAliases = fieldAliases;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresTables() {
        return true;
    }

    public final Table<R> autoAlias(Context<?> ctx, Table<R> t) {
        return t.as(this.alias, this.fieldAliases);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table, org.jooq.SelectField
    public final Table<R> as(Name as) {
        return new TableAlias(construct2(as, null), as, this.fieldAliases);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final Table<R> as(Name as, Name... fields) {
        return new TableAlias(construct2(as, fields), as, fields);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.impl.QOM.Aliasable
    public final Table<R> $aliased() {
        return construct2(this.alias, null);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.impl.QOM.Aliasable
    public final Name $alias() {
        return this.alias;
    }
}
