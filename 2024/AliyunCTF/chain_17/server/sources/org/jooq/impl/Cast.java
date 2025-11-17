package org.jooq.impl;

import java.util.function.BooleanSupplier;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Keyword;
import org.jooq.QueryPart;
import org.jooq.impl.QOM;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Cast.class */
public final class Cast<T> extends AbstractField<T> implements QOM.Cast<T> {
    private final Field<?> field;

    public Cast(Field<?> field, DataType<T> type) {
        super(Names.N_CAST, type.nullable(field.getDataType().nullable()));
        this.field = field;
    }

    private final DataType<T> getSQLDataType() {
        return getDataType().getSQLDataType();
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case DERBY:
                ctx.visit(new CastDerby());
                return;
            default:
                ctx.visit(new CastNative(this.field, getDataType()));
                return;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Cast$CastDerby.class */
    private final class CastDerby extends CastNative<T> {
        CastDerby() {
            super(Cast.this.field, Cast.this.getDataType());
        }

        private final Field<Boolean> asDecodeNumberToBoolean() {
            return DSL.choose((Field) Cast.this.field).when((Field) DSL.inline(0), (Field) DSL.inline(false)).when((Field) DSL.inline((Integer) null), (Field) DSL.inline((Boolean) null)).otherwise((Field) DSL.inline(true));
        }

        private final Field<Boolean> asDecodeVarcharToBoolean() {
            Field<?> field = Cast.this.field;
            return DSL.when(field.equal((Field<?>) DSL.inline(CustomBooleanEditor.VALUE_0)), (Field) DSL.inline(false)).when(DSL.lower((Field<String>) field).equal(DSL.inline("false")), (Field) DSL.inline(false)).when(DSL.lower((Field<String>) field).equal(DSL.inline("f")), (Field) DSL.inline(false)).when(field.isNull(), (Field) DSL.inline((Boolean) null)).otherwise((Field) DSL.inline(true));
        }

        /* JADX WARN: Type inference failed for: r0v45, types: [org.jooq.Context] */
        @Override // org.jooq.impl.Cast.CastNative, org.jooq.QueryPartInternal
        public final void accept(Context<?> ctx) {
            DataType<T> type = Cast.this.getSQLDataType();
            if (Cast.this.field.getDataType().isNumeric() && type.isString() && !SQLDataType.CHAR.equals(type)) {
                ctx.visit(Keywords.K_TRIM).sql('(').visit(new CastNative(new CastNative(Cast.this.field, SQLDataType.CHAR(38)), Cast.this.getDataType())).sql(')');
                return;
            }
            if (Cast.this.field.getDataType().isString() && (SQLDataType.FLOAT.equals(type) || SQLDataType.DOUBLE.equals(type) || SQLDataType.REAL.equals(type))) {
                ctx.visit(new CastNative(new CastNative(Cast.this.field, SQLDataType.DECIMAL), Cast.this.getDataType()));
                return;
            }
            if (Cast.this.field.getDataType().isNumeric() && SQLDataType.BOOLEAN.equals(type)) {
                ctx.visit((Field<?>) asDecodeNumberToBoolean());
            } else if (Cast.this.field.getDataType().isString() && SQLDataType.BOOLEAN.equals(type)) {
                ctx.visit((Field<?>) asDecodeVarcharToBoolean());
            } else {
                super.accept(ctx);
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Cast$CastNative.class */
    static class CastNative<T> extends AbstractQueryPart implements QOM.UTransient {
        final QueryPart expression;
        final DataType<T> type;
        final Keyword typeAsKeyword;
        final boolean tryCast;

        CastNative(QueryPart expression, DataType<T> type) {
            this(expression, type, false);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public CastNative(QueryPart expression, DataType<T> type, boolean tryCast) {
            this.expression = expression;
            this.type = type;
            this.typeAsKeyword = null;
            this.tryCast = tryCast;
        }

        CastNative(QueryPart expression, Keyword typeAsKeyword) {
            this.expression = expression;
            this.type = null;
            this.typeAsKeyword = typeAsKeyword;
            this.tryCast = false;
        }

        @Override // org.jooq.QueryPartInternal
        public void accept(Context<?> ctx) {
            Cast.renderCast(ctx, c -> {
                c.visit(this.expression);
            }, c2 -> {
                if (this.typeAsKeyword != null) {
                    c2.visit(this.typeAsKeyword);
                } else {
                    c2.sql(this.type.getCastTypeName(c2.configuration()));
                }
            }, this.tryCast);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <E extends Throwable> void renderCast(Context<?> ctx, ThrowingConsumer<? super Context<?>, E> expression, ThrowingConsumer<? super Context<?>, E> type) throws Throwable {
        renderCast(ctx, expression, type, false);
    }

    /* JADX WARN: Code restructure failed: missing block: B:5:0x0024, code lost:            r3.visit(org.jooq.impl.Names.N_TRY_CAST);     */
    /* JADX WARN: Type inference failed for: r0v10, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v6, types: [org.jooq.Context] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    static <E extends java.lang.Throwable> void renderCast(org.jooq.Context<?> r3, org.jooq.impl.ThrowingConsumer<? super org.jooq.Context<?>, E> r4, org.jooq.impl.ThrowingConsumer<? super org.jooq.Context<?>, E> r5, boolean r6) throws java.lang.Throwable {
        /*
            r0 = r3
            org.jooq.RenderContext$CastMode r0 = r0.castMode()
            r7 = r0
            r0 = r6
            if (r0 == 0) goto L31
            int[] r0 = org.jooq.impl.Cast.AnonymousClass1.$SwitchMap$org$jooq$SQLDialect
            r1 = r3
            org.jooq.SQLDialect r1 = r1.family()
            int r1 = r1.ordinal()
            r0 = r0[r1]
            switch(r0) {
                default: goto L24;
            }
        L24:
            r0 = r3
            org.jooq.Name r1 = org.jooq.impl.Names.N_TRY_CAST
            org.jooq.Context r0 = r0.visit(r1)
            goto L3b
        L31:
            r0 = r3
            org.jooq.Keyword r1 = org.jooq.impl.Keywords.K_CAST
            org.jooq.Context r0 = r0.visit(r1)
        L3b:
            r0 = r3
            r1 = 40
            org.jooq.Context r0 = r0.sql(r1)
            org.jooq.RenderContext$CastMode r1 = org.jooq.RenderContext.CastMode.NEVER
            org.jooq.Context r0 = r0.castMode(r1)
            r0 = r4
            r1 = r3
            r0.accept(r1)
            r0 = r3
            r1 = r7
            org.jooq.Context r0 = r0.castMode(r1)
            r1 = 32
            org.jooq.Context r0 = r0.sql(r1)
            org.jooq.Keyword r1 = org.jooq.impl.Keywords.K_AS
            org.jooq.Context r0 = r0.visit(r1)
            r1 = 32
            org.jooq.Context r0 = r0.sql(r1)
            r0 = r5
            r1 = r3
            r0.accept(r1)
            r0 = r3
            r1 = 41
            org.jooq.Context r0 = r0.sql(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.Cast.renderCast(org.jooq.Context, org.jooq.impl.ThrowingConsumer, org.jooq.impl.ThrowingConsumer, boolean):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <E extends Throwable> void renderCastIf(Context<?> ctx, ThrowingConsumer<? super Context<?>, E> expression, ThrowingConsumer<? super Context<?>, E> type, BooleanSupplier test) throws Throwable {
        if (test.getAsBoolean()) {
            renderCast(ctx, expression, type);
        } else {
            expression.accept(ctx);
        }
    }

    @Override // org.jooq.impl.QOM.Cast
    public final Field<?> $field() {
        return this.field;
    }
}
