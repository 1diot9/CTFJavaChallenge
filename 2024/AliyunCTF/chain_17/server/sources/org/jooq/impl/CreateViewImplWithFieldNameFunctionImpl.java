package org.jooq.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.CreateViewAsStep;
import org.jooq.CreateViewFinalStep;
import org.jooq.Field;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.ResultQuery;
import org.jooq.SQL;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.impl.QOM;

/* compiled from: CreateViewWithFieldNameFunctionImpl.java */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CreateViewImplWithFieldNameFunctionImpl.class */
final class CreateViewImplWithFieldNameFunctionImpl<R extends Record> extends AbstractDDLQuery implements CreateViewAsStep<R>, CreateViewFinalStep, QOM.UTransient {
    private final boolean ifNotExists;
    private final boolean orReplace;
    private final Table<?> view;
    private final BiFunction<? super Field<?>, ? super Integer, ? extends Field<?>> fieldNameFunction;
    private Field<?>[] fields;
    private ResultQuery<?> select;
    private CreateViewImpl<?> delegate;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CreateViewImplWithFieldNameFunctionImpl(Configuration configuration, Table<?> view, BiFunction<? super Field<?>, ? super Integer, ? extends Field<?>> fieldNameFunction, boolean ifNotExists, boolean orReplace) {
        super(configuration);
        this.view = view;
        this.fields = null;
        this.fieldNameFunction = fieldNameFunction;
        this.ifNotExists = ifNotExists;
        this.orReplace = orReplace;
    }

    @Override // org.jooq.CreateViewAsStep
    public final CreateViewFinalStep as(ResultQuery<? extends R> as) {
        this.select = as;
        if (this.fieldNameFunction != null) {
            if (as instanceof Select) {
                Select<?> s = (Select) as;
                List<Field<?>> select = s.getSelect();
                BiFunction<? super Field<?>, ? super Integer, ? extends Field<?>> biFunction = this.fieldNameFunction;
                Objects.requireNonNull(biFunction);
                this.fields = (Field[]) Tools.map(select, (v1, v2) -> {
                    return r2.apply(v1, v2);
                }, x$0 -> {
                    return new Field[x$0];
                });
            } else {
                Field<?>[] fields = as.fields();
                BiFunction<? super Field<?>, ? super Integer, ? extends Field<?>> biFunction2 = this.fieldNameFunction;
                Objects.requireNonNull(biFunction2);
                this.fields = (Field[]) Tools.map(fields, (v1, v2) -> {
                    return r2.apply(v1, v2);
                }, x$02 -> {
                    return new Field[x$02];
                });
            }
        }
        return delegate();
    }

    @Override // org.jooq.CreateViewAsStep
    public final CreateViewFinalStep as(SQL sql) {
        this.select = DSL.resultQuery(sql);
        if (this.fieldNameFunction != null) {
            List<Field<?>> select = delegate().parsed().getSelect();
            BiFunction<? super Field<?>, ? super Integer, ? extends Field<?>> biFunction = this.fieldNameFunction;
            Objects.requireNonNull(biFunction);
            this.fields = (Field[]) Tools.map(select, (v1, v2) -> {
                return r2.apply(v1, v2);
            }, x$0 -> {
                return new Field[x$0];
            });
        }
        return delegate();
    }

    private final CreateViewImpl<?> delegate() {
        if (this.delegate == null) {
            this.delegate = new CreateViewImpl<>(configuration(), this.view, Arrays.asList(this.fields), this.orReplace, this.ifNotExists, false, this.select);
        }
        return this.delegate;
    }

    @Override // org.jooq.CreateViewAsStep
    public final CreateViewFinalStep as(String sql) {
        return as(DSL.sql(sql));
    }

    @Override // org.jooq.CreateViewAsStep
    public final CreateViewFinalStep as(String sql, Object... bindings) {
        return as(DSL.sql(sql, bindings));
    }

    @Override // org.jooq.CreateViewAsStep
    public final CreateViewFinalStep as(String sql, QueryPart... parts) {
        return as(DSL.sql(sql, parts));
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(delegate());
    }
}
