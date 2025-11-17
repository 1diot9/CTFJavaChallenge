package org.jooq.impl;

import java.util.function.Consumer;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.SelectField;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CustomField.class */
public abstract class CustomField<T> extends AbstractField<T> implements QOM.UEmptyField<T>, QOM.UOpaque {
    private static final Clause[] CLAUSES = {Clause.CUSTOM};

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public abstract void accept(Context<?> context);

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

    protected CustomField(String name, DataType<T> type) {
        this(DSL.name(name), type);
    }

    protected CustomField(Name name, DataType<T> type) {
        super(name, type);
    }

    public static final <T> CustomField<T> of(String name, DataType<T> type, Consumer<? super Context<?>> consumer) {
        return of(DSL.name(name), type, consumer);
    }

    public static final <T> CustomField<T> of(Name name, DataType<T> type, final Consumer<? super Context<?>> consumer) {
        return new CustomField<T>(name, type) { // from class: org.jooq.impl.CustomField.1
            @Override // org.jooq.impl.CustomField, org.jooq.impl.AbstractField, org.jooq.Field, org.jooq.SelectField
            public /* bridge */ /* synthetic */ SelectField as(Name name2) {
                return super.as(name2);
            }

            @Override // org.jooq.impl.CustomField, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
            public void accept(Context<?> ctx) {
                consumer.accept(ctx);
            }
        };
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.Field, org.jooq.SelectField
    public final Field<T> as(Name alias) {
        return super.as(alias);
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
