package org.jooq.impl;

import java.util.Collection;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.QualifiedAsterisk;
import org.jooq.Query;
import org.jooq.Table;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QualifiedAsteriskProxy.class */
final class QualifiedAsteriskProxy extends AbstractQueryPart implements QualifiedAsterisk, QOM.UProxy<QualifiedAsterisk>, ScopeMappable {
    private QualifiedAsteriskImpl delegate;
    private final int position;
    Query scopeOwner;
    boolean resolved;

    /* JADX INFO: Access modifiers changed from: package-private */
    public QualifiedAsteriskProxy(QualifiedAsteriskImpl delegate, int position) {
        this.delegate = delegate;
        this.position = position;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int position() {
        return this.position;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void delegate(QualifiedAsteriskImpl newDelegate) {
        resolve();
        this.delegate = newDelegate;
    }

    final QualifiedAsteriskProxy resolve() {
        this.resolved = true;
        this.scopeOwner = null;
        return this;
    }

    final void scopeOwner(Query query) {
        if (!this.resolved && this.scopeOwner == null) {
            this.scopeOwner = query;
        }
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public final int hashCode() {
        return this.delegate.hashCode();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
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

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        this.delegate.accept(ctx);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return this.delegate.clauses(ctx);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public final String toString() {
        return this.delegate.toString();
    }

    @Override // org.jooq.QualifiedAsterisk
    public final Table<?> qualifier() {
        return this.delegate.qualifier();
    }

    @Override // org.jooq.QualifiedAsterisk
    public final QualifiedAsterisk except(String... fieldNames) {
        return new QualifiedAsteriskProxy((QualifiedAsteriskImpl) this.delegate.except(fieldNames), this.position);
    }

    @Override // org.jooq.QualifiedAsterisk
    public final QualifiedAsterisk except(Name... fieldNames) {
        return new QualifiedAsteriskProxy((QualifiedAsteriskImpl) this.delegate.except(fieldNames), this.position);
    }

    @Override // org.jooq.QualifiedAsterisk
    public final QualifiedAsterisk except(Field<?>... fields) {
        return new QualifiedAsteriskProxy((QualifiedAsteriskImpl) this.delegate.except(fields), this.position);
    }

    @Override // org.jooq.QualifiedAsterisk
    public final QualifiedAsterisk except(Collection<? extends Field<?>> fields) {
        return new QualifiedAsteriskProxy((QualifiedAsteriskImpl) this.delegate.except(fields), this.position);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UProxy
    public final QualifiedAsterisk $delegate() {
        return this.delegate;
    }

    @Override // org.jooq.QualifiedAsterisk
    public final Table<?> $table() {
        return this.delegate.$table();
    }

    @Override // org.jooq.QualifiedAsterisk
    public final QOM.UnmodifiableList<? extends Field<?>> $except() {
        return this.delegate.$except();
    }
}
