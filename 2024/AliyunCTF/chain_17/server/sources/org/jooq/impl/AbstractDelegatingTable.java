package org.jooq.impl;

import java.util.List;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Table;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractDelegatingTable.class */
public abstract class AbstractDelegatingTable<R extends Record> extends AbstractTable<R> {
    final AbstractTable<R> delegate;

    abstract <O extends Record> AbstractDelegatingTable<O> construct(AbstractTable<O> abstractTable);

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractDelegatingTable(AbstractTable<R> delegate) {
        super(delegate.getOptions(), delegate.getQualifiedName(), delegate.getSchema());
        this.delegate = delegate;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresTables() {
        return true;
    }

    @Override // org.jooq.RecordQualifier
    public final Class<? extends R> getRecordType() {
        return this.delegate.getRecordType();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table, org.jooq.SelectField
    public final Table<R> as(Name name) {
        return construct(new TableAlias(this.delegate, name));
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final Table<R> as(Name name, Name... nameArr) {
        return construct(new TableAlias(this.delegate, name, nameArr));
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final List<ForeignKey<R, ?>> getReferences() {
        return this.delegate.getReferences();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractTable
    public final FieldsImpl<R> fields0() {
        return this.delegate.fields0();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.impl.QOM.Aliasable
    public final Table<R> $aliased() {
        return construct((AbstractTable) this.delegate.$aliased());
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.impl.QOM.Aliasable
    public final Name $alias() {
        return this.delegate.$alias();
    }
}
