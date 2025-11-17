package org.jooq.impl;

import java.util.function.BooleanSupplier;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Fields;
import org.jooq.Name;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.SelectField;
import org.jooq.conf.NestedCollectionEmulation;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractRowAsField.class */
public abstract class AbstractRowAsField<R extends Record> extends AbstractField<R> implements AutoAlias<SelectField<R>> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract Fields fields0();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract Class<R> getRecordType();

    abstract void acceptDefault(Context<?> context);

    @Override // org.jooq.impl.AutoAlias
    public /* bridge */ /* synthetic */ QueryPart autoAlias(Context context, QueryPart queryPart) {
        return autoAlias((Context<?>) context, (SelectField) queryPart);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractRowAsField(Name name, DataType<R> type) {
        super(name, type);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final AbstractRow<R> emulatedFields(Configuration configuration) {
        return (AbstractRow<R>) Tools.row0((Field<?>[]) Tools.map(fields0().fields(), x -> {
            return x.as(Tools.sanitiseName(configuration, String.valueOf(getUnqualifiedName().unquotedName()) + configuration.settings().getNamePathSeparator() + x.getName()));
        }, x$0 -> {
            return new Field[x$0];
        }));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final int projectionSize() {
        int result = 0;
        for (Field<?> field : fields0().fields()) {
            result += ((AbstractField) field).projectionSize();
        }
        return result;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresFields() {
        return true;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (forceMultisetContent(ctx, () -> {
            return getDataType().getRow().size() > 1;
        })) {
            acceptMultisetContent(ctx, getDataType().getRow(), this, this::acceptDefault);
        } else {
            acceptDefault(ctx);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean forceMultisetContent(Context<?> ctx, BooleanSupplier degreeCheck) {
        return Boolean.TRUE.equals(ctx.data(Tools.BooleanDataKey.DATA_MULTISET_CONTENT)) || (ctx.subquery() && RowAsField.NO_NATIVE_SUPPORT.contains(ctx.dialect()) && !ctx.predicandSubquery() && !ctx.derivedTableSubquery() && !ctx.setOperationSubquery() && degreeCheck.getAsBoolean());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean forceRowContent(Context<?> ctx) {
        return Boolean.TRUE.equals(ctx.data(Tools.BooleanDataKey.DATA_ROW_CONTENT));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x00a0, code lost:            r7.visit(alias(r7, r0, org.jooq.impl.Multiset.returningClob(r7, org.jooq.impl.DSL.jsonbArray(r8.fields()).nullOnNull())));     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x00d8, code lost:            r7.visit(alias(r7, r0, org.jooq.impl.DSL.xmlelement(org.jooq.impl.Names.N_RECORD, org.jooq.impl.Tools.map(r8.fields(), (v0, v1) -> { // org.jooq.impl.ThrowingObjIntFunction.apply(java.lang.Object, int):java.lang.Object            return lambda$acceptMultisetContent$3(v0, v1);        }))));     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x0068, code lost:            r7.visit(alias(r7, r0, org.jooq.impl.Multiset.returningClob(r7, org.jooq.impl.DSL.jsonArray(r8.fields()).nullOnNull())));     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static final void acceptMultisetContent(org.jooq.Context<?> r7, org.jooq.Row r8, org.jooq.Field<?> r9, java.util.function.Consumer<? super org.jooq.Context<?>> r10) {
        /*
            r0 = r7
            org.jooq.impl.Tools$BooleanDataKey r1 = org.jooq.impl.Tools.BooleanDataKey.DATA_MULTISET_CONTENT
            java.lang.Object r0 = r0.data(r1)
            r11 = r0
            r0 = r7
            org.jooq.impl.Tools$BooleanDataKey r1 = org.jooq.impl.Tools.BooleanDataKey.DATA_MULTISET_CONTENT     // Catch: java.lang.Throwable -> L112
            r2 = 1
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r2)     // Catch: java.lang.Throwable -> L112
            java.lang.Object r0 = r0.data(r1, r2)     // Catch: java.lang.Throwable -> L112
            r0 = r9
            org.jooq.Name r0 = r0.getUnqualifiedName()     // Catch: java.lang.Throwable -> L112
            r12 = r0
            int[] r0 = org.jooq.impl.AbstractRowAsField.AnonymousClass1.$SwitchMap$org$jooq$conf$NestedCollectionEmulation     // Catch: java.lang.Throwable -> L112
            r1 = r7
            org.jooq.Configuration r1 = r1.configuration()     // Catch: java.lang.Throwable -> L112
            org.jooq.conf.NestedCollectionEmulation r1 = org.jooq.impl.Tools.emulateMultiset(r1)     // Catch: java.lang.Throwable -> L112
            int r1 = r1.ordinal()     // Catch: java.lang.Throwable -> L112
            r0 = r0[r1]     // Catch: java.lang.Throwable -> L112
            switch(r0) {
                case 1: goto L50;
                case 2: goto L8a;
                case 3: goto Lc2;
                case 4: goto Lfc;
                default: goto Lfc;
            }     // Catch: java.lang.Throwable -> L112
        L50:
            int[] r0 = org.jooq.impl.AbstractRowAsField.AnonymousClass1.$SwitchMap$org$jooq$SQLDialect     // Catch: java.lang.Throwable -> L112
            r1 = r7
            org.jooq.SQLDialect r1 = r1.family()     // Catch: java.lang.Throwable -> L112
            int r1 = r1.ordinal()     // Catch: java.lang.Throwable -> L112
            r0 = r0[r1]     // Catch: java.lang.Throwable -> L112
            switch(r0) {
                default: goto L68;
            }     // Catch: java.lang.Throwable -> L112
        L68:
            r0 = r7
            r1 = r7
            r2 = r12
            r3 = r7
            r4 = r8
            org.jooq.Field[] r4 = r4.fields()     // Catch: java.lang.Throwable -> L112
            org.jooq.JSONArrayNullStep r4 = org.jooq.impl.DSL.jsonArray(r4)     // Catch: java.lang.Throwable -> L112
            org.jooq.JSONArrayReturningStep r4 = r4.nullOnNull()     // Catch: java.lang.Throwable -> L112
            org.jooq.Field r3 = org.jooq.impl.Multiset.returningClob(r3, r4)     // Catch: java.lang.Throwable -> L112
            org.jooq.Field r1 = alias(r1, r2, r3)     // Catch: java.lang.Throwable -> L112
            org.jooq.Context r0 = r0.visit(r1)     // Catch: java.lang.Throwable -> L112
            goto L103
        L8a:
            int[] r0 = org.jooq.impl.AbstractRowAsField.AnonymousClass1.$SwitchMap$org$jooq$SQLDialect     // Catch: java.lang.Throwable -> L112
            r1 = r7
            org.jooq.SQLDialect r1 = r1.family()     // Catch: java.lang.Throwable -> L112
            int r1 = r1.ordinal()     // Catch: java.lang.Throwable -> L112
            r0 = r0[r1]     // Catch: java.lang.Throwable -> L112
            switch(r0) {
                default: goto La0;
            }     // Catch: java.lang.Throwable -> L112
        La0:
            r0 = r7
            r1 = r7
            r2 = r12
            r3 = r7
            r4 = r8
            org.jooq.Field[] r4 = r4.fields()     // Catch: java.lang.Throwable -> L112
            org.jooq.JSONArrayNullStep r4 = org.jooq.impl.DSL.jsonbArray(r4)     // Catch: java.lang.Throwable -> L112
            org.jooq.JSONArrayReturningStep r4 = r4.nullOnNull()     // Catch: java.lang.Throwable -> L112
            org.jooq.Field r3 = org.jooq.impl.Multiset.returningClob(r3, r4)     // Catch: java.lang.Throwable -> L112
            org.jooq.Field r1 = alias(r1, r2, r3)     // Catch: java.lang.Throwable -> L112
            org.jooq.Context r0 = r0.visit(r1)     // Catch: java.lang.Throwable -> L112
            goto L103
        Lc2:
            int[] r0 = org.jooq.impl.AbstractRowAsField.AnonymousClass1.$SwitchMap$org$jooq$SQLDialect     // Catch: java.lang.Throwable -> L112
            r1 = r7
            org.jooq.SQLDialect r1 = r1.family()     // Catch: java.lang.Throwable -> L112
            int r1 = r1.ordinal()     // Catch: java.lang.Throwable -> L112
            r0 = r0[r1]     // Catch: java.lang.Throwable -> L112
            switch(r0) {
                default: goto Ld8;
            }     // Catch: java.lang.Throwable -> L112
        Ld8:
            r0 = r7
            r1 = r7
            r2 = r12
            org.jooq.Name r3 = org.jooq.impl.Names.N_RECORD     // Catch: java.lang.Throwable -> L112
            r4 = r8
            org.jooq.Field[] r4 = r4.fields()     // Catch: java.lang.Throwable -> L112
            void r5 = (v0, v1) -> { // org.jooq.impl.ThrowingObjIntFunction.apply(java.lang.Object, int):java.lang.Object
                return lambda$acceptMultisetContent$3(v0, v1);
            }     // Catch: java.lang.Throwable -> L112
            java.util.List r4 = org.jooq.impl.Tools.map(r4, r5)     // Catch: java.lang.Throwable -> L112
            org.jooq.Field r3 = org.jooq.impl.DSL.xmlelement(r3, r4)     // Catch: java.lang.Throwable -> L112
            org.jooq.Field r1 = alias(r1, r2, r3)     // Catch: java.lang.Throwable -> L112
            org.jooq.Context r0 = r0.visit(r1)     // Catch: java.lang.Throwable -> L112
            goto L103
        Lfc:
            r0 = r10
            r1 = r7
            r0.accept(r1)     // Catch: java.lang.Throwable -> L112
        L103:
            r0 = r7
            org.jooq.impl.Tools$BooleanDataKey r1 = org.jooq.impl.Tools.BooleanDataKey.DATA_MULTISET_CONTENT
            r2 = r11
            java.lang.Object r0 = r0.data(r1, r2)
            goto L123
        L112:
            r13 = move-exception
            r0 = r7
            org.jooq.impl.Tools$BooleanDataKey r1 = org.jooq.impl.Tools.BooleanDataKey.DATA_MULTISET_CONTENT
            r2 = r11
            java.lang.Object r0 = r0.data(r1, r2)
            r0 = r13
            throw r0
        L123:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.AbstractRowAsField.acceptMultisetContent(org.jooq.Context, org.jooq.Row, org.jooq.Field, java.util.function.Consumer):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.jooq.impl.AbstractRowAsField$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractRowAsField$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect;

        static {
            try {
                $SwitchMap$org$jooq$conf$NestedCollectionEmulation[NestedCollectionEmulation.JSON.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jooq$conf$NestedCollectionEmulation[NestedCollectionEmulation.JSONB.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$jooq$conf$NestedCollectionEmulation[NestedCollectionEmulation.XML.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$jooq$conf$NestedCollectionEmulation[NestedCollectionEmulation.NATIVE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
        }
    }

    public final SelectField<R> autoAlias(Context<?> ctx, SelectField<R> s) {
        if (RowAsField.NO_NATIVE_SUPPORT.contains(ctx.dialect())) {
            return s;
        }
        if (forceMultisetContent(ctx, () -> {
            return getDataType().getRow().size() > 1;
        })) {
            return s;
        }
        return new FieldAlias(DSL.field(s), getUnqualifiedName());
    }

    private static final Field<?> alias(Context<?> ctx, Name alias, Field<?> field) {
        return ctx.declareFields() ? field.as(alias) : field;
    }
}
