package org.jooq.impl;

import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FieldProxy.class */
final class FieldProxy<T> extends AbstractField<T> implements TableField<Record, T>, QOM.UProxy<Field<T>>, ScopeMappable {
    private AbstractField<T> delegate;
    private final int position;
    Query scopeOwner;
    boolean resolved;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FieldProxy(AbstractField<T> delegate, int position) {
        super(delegate.getQualifiedName(), new DataTypeProxy((AbstractDataType) delegate.getDataType()), delegate.getCommentPart(), delegate.getBinding());
        this.delegate = delegate;
        this.position = position;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int position() {
        return this.position;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void delegate(AbstractField<T> newDelegate) {
        resolve();
        this.delegate = newDelegate;
        ((DataTypeProxy) getDataType()).type((AbstractDataType) newDelegate.getDataType());
    }

    final FieldProxy<T> resolve() {
        this.resolved = true;
        this.scopeOwner = null;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void scopeOwner(Query query) {
        if (!this.resolved && this.scopeOwner == null) {
            this.scopeOwner = query;
        }
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.Named
    public final Name getQualifiedName() {
        return this.delegate.getQualifiedName();
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public final int hashCode() {
        return this.delegate.hashCode();
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public final boolean equals(Object that) {
        return this.delegate.equals(that);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresFields() {
        return this.delegate.declaresFields();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresTables() {
        return this.delegate.declaresTables();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresWindows() {
        return this.delegate.declaresWindows();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresCTE() {
        return this.delegate.declaresCTE();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean generatesCast() {
        return this.delegate.generatesCast();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean rendersContent(Context<?> ctx) {
        return this.delegate.rendersContent(ctx);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        this.delegate.accept(ctx);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return this.delegate.clauses(ctx);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public final String toString() {
        return this.delegate.toString();
    }

    @Override // org.jooq.TableField
    public final Table<Record> getTable() {
        if (this.delegate instanceof TableField) {
            return ((TableField) this.delegate).getTable();
        }
        return null;
    }

    @Override // org.jooq.impl.QOM.UProxy
    public final Field<T> $delegate() {
        return this.delegate;
    }
}
