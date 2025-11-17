package org.jooq.impl;

import java.util.function.Consumer;
import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CustomCondition.class */
public abstract class CustomCondition extends AbstractCondition implements QOM.UEmptyCondition, QOM.UOpaque {
    private static final Clause[] CLAUSES = {Clause.CUSTOM};

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public abstract void accept(Context<?> context);

    @Override // org.jooq.impl.AbstractCondition, org.jooq.Condition
    public /* bridge */ /* synthetic */ Condition and(Field field) {
        return super.and((Field<Boolean>) field);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.Field, org.jooq.SelectField
    public /* bridge */ /* synthetic */ Field as(Name name) {
        return super.as(name);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.impl.QOM.Aliasable
    public /* bridge */ /* synthetic */ Field $aliased() {
        return super.$aliased();
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.impl.QOM.Aliasable
    public /* bridge */ /* synthetic */ Name $alias() {
        return super.$alias();
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public /* bridge */ /* synthetic */ boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public /* bridge */ /* synthetic */ int hashCode() {
        return super.hashCode();
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.Named
    public /* bridge */ /* synthetic */ Name getQualifiedName() {
        return super.getQualifiedName();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean generatesCast() {
        return super.generatesCast();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean declaresCTE() {
        return super.declaresCTE();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean declaresWindows() {
        return super.declaresWindows();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean rendersContent(Context context) {
        return super.rendersContent(context);
    }

    protected CustomCondition() {
    }

    public static final CustomCondition of(final Consumer<? super Context<?>> consumer) {
        return new CustomCondition() { // from class: org.jooq.impl.CustomCondition.1
            @Override // org.jooq.impl.CustomCondition, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
            public void accept(Context<?> ctx) {
                consumer.accept(ctx);
            }
        };
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresFields() {
        return super.declaresFields();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresTables() {
        return super.declaresTables();
    }
}
