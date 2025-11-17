package org.jooq.impl;

import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.QueryPart;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Coerce.class */
public final class Coerce<T> extends AbstractField<T> implements AutoAlias<Field<T>>, QOM.Coerce<T> {
    final AbstractField<?> field;

    @Override // org.jooq.impl.AutoAlias
    public /* bridge */ /* synthetic */ QueryPart autoAlias(Context context, QueryPart queryPart) {
        return autoAlias((Context<?>) context, (Field) queryPart);
    }

    public Coerce(Field<?> field, DataType<T> type) {
        super(field.getQualifiedName(), type);
        this.field = (AbstractField) Tools.uncoerce(field);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit((Field<?>) this.field);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return this.field.clauses(ctx);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public boolean isPossiblyNullable() {
        return this.field.isPossiblyNullable();
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.Field, org.jooq.SelectField
    public final Field<T> as(Name name) {
        return (Field<T>) this.field.as(name).coerce(getDataType());
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean rendersContent(Context<?> ctx) {
        return this.field.rendersContent(ctx);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresFields() {
        return this.field.declaresFields();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresTables() {
        return this.field.declaresTables();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresWindows() {
        return this.field.declaresWindows();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresCTE() {
        return this.field.declaresCTE();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean generatesCast() {
        return this.field.generatesCast();
    }

    public final Field<T> autoAlias(Context<?> ctx, Field<T> f) {
        if (this.field instanceof AutoAlias) {
            return ((Field) ((AutoAlias) this.field).autoAlias(ctx, this.field)).coerce(getDataType());
        }
        return f;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.impl.QOM.Aliasable
    public final Field<?> $aliased() {
        return this.field.$aliased().coerce(getDataType());
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.impl.QOM.Aliasable
    public final Name $alias() {
        return this.field.$alias();
    }

    @Override // org.jooq.impl.QOM.Coerce
    public final Field<?> $field() {
        return this.field;
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof Coerce) {
            Coerce<?> o = (Coerce) that;
            return StringUtils.equals($field(), o.$field());
        }
        return super.equals(that);
    }
}
