package org.jooq.impl;

import org.jetbrains.annotations.NotNull;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FieldAlias.class */
public final class FieldAlias<T> extends AbstractField<T> implements QOM.FieldAlias<T>, SimpleCheckQueryPart {
    private final Alias<Field<T>> alias;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FieldAlias(Field<T> field, Name alias) {
        super(alias, field.getDataType());
        this.alias = new Alias<>(field, this, alias);
    }

    @Override // org.jooq.impl.SimpleCheckQueryPart
    public final boolean isSimple(Context<?> ctx) {
        return !ctx.declareFields();
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(this.alias);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return null;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.Field, org.jooq.SelectField
    public final Field<T> as(Name as) {
        return this.alias.wrapped().as(as);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresFields() {
        return true;
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.Named
    public Name getQualifiedName() {
        return getUnqualifiedName();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Field<T> getAliasedField() {
        if (this.alias != null) {
            return this.alias.wrapped();
        }
        return null;
    }

    @Override // org.jooq.impl.QOM.FieldAlias
    public final Field<T> $field() {
        return this.alias.wrapped();
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.impl.QOM.Aliasable
    public final Field<?> $aliased() {
        return this.alias.wrapped();
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.impl.QOM.Aliasable
    @NotNull
    public final Name $alias() {
        return getQualifiedName();
    }
}
