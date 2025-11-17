package org.jooq.impl;

import java.util.Collection;
import java.util.Set;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Fields;
import org.jooq.JSON;
import org.jooq.JSONArrayAggOrderByStep;
import org.jooq.JSONArrayAggReturningStep;
import org.jooq.JSONArrayNullStep;
import org.jooq.JSONArrayReturningStep;
import org.jooq.JSONB;
import org.jooq.JSONEntry;
import org.jooq.JSONObjectNullStep;
import org.jooq.JSONObjectReturningStep;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.Scope;
import org.jooq.Select;
import org.jooq.TableLike;
import org.jooq.XML;
import org.jooq.XMLAggOrderByStep;
import org.jooq.conf.NestedCollectionEmulation;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Multiset.class */
public final class Multiset<R extends Record> extends AbstractField<Result<R>> implements QOM.Multiset<R> {
    static final Set<SQLDialect> NO_SUPPORT_JSON_COMPARE = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    static final Set<SQLDialect> NO_SUPPORT_JSONB_COMPARE = SQLDialect.supportedBy(new SQLDialect[0]);
    static final Set<SQLDialect> NO_SUPPORT_XML_COMPARE = SQLDialect.supportedBy(SQLDialect.POSTGRES);
    static final Set<SQLDialect> FORCE_LIMIT_IN_DERIVED_TABLE = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.TRINO);
    final TableLike<R> table;
    final Select<R> select;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public Multiset(org.jooq.TableLike<R> r5) {
        /*
            r4 = this;
            r0 = r4
            r1 = r5
            r2 = r5
            boolean r2 = r2 instanceof org.jooq.Select
            if (r2 == 0) goto L12
            r2 = r5
            org.jooq.Select r2 = (org.jooq.Select) r2
            r6 = r2
            r2 = r6
            goto L16
        L12:
            r2 = r5
            org.jooq.SelectWhereStep r2 = org.jooq.impl.DSL.selectFrom(r2)
        L16:
            r0.<init>(r1, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.Multiset.<init>(org.jooq.TableLike):void");
    }

    private Multiset(TableLike<R> table, Select<R> select) {
        super(Names.N_MULTISET, new MultisetDataType((AbstractRow) DSL.row((Collection<?>) select.getSelect()), select.getRecordType()));
        this.table = table;
        this.select = select;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (Boolean.TRUE.equals(ctx.data(Tools.BooleanDataKey.DATA_MULTISET_CONDITION))) {
            ctx.data().remove(Tools.BooleanDataKey.DATA_MULTISET_CONDITION);
            ctx.data(Tools.BooleanDataKey.DATA_MULTISET_CONTENT, true, c -> {
                accept0(c, true);
            });
            ctx.data(Tools.BooleanDataKey.DATA_MULTISET_CONDITION, true);
            return;
        }
        ctx.data(Tools.BooleanDataKey.DATA_MULTISET_CONTENT, true, c2 -> {
            accept0(c2, false);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.jooq.impl.Multiset$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Multiset$1.class */
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

    /* JADX WARN: Code restructure failed: missing block: B:10:?, code lost:            return;     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x011c, code lost:            r0 = jsonArrayaggEmulation(r15, r0, true, false);        r20 = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x012a, code lost:            if (r16 == false) goto L14;     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x012d, code lost:            r20 = r0.orderBy(r0.fields());     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x013d, code lost:            r0 = org.jooq.impl.JSONArrayAgg.patchOracleArrayAggBug(r15, org.jooq.impl.DSL.select(org.jooq.impl.DSL.coalesce(returningClob(r15, r20), (org.jooq.Field<?>[]) new org.jooq.Field[]{returningClob(r15, org.jooq.impl.DSL.jsonArray((org.jooq.Field<?>[]) new org.jooq.Field[0]))})).from(r0));     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0169, code lost:            if (r16 == false) goto L19;     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x017a, code lost:            if (org.jooq.impl.Multiset.NO_SUPPORT_JSON_COMPARE.contains(r15.dialect()) == false) goto L19;     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x017d, code lost:            r15.visit(org.jooq.impl.DSL.field(r0).cast(org.jooq.impl.SQLDataType.VARCHAR));     */
    /* JADX WARN: Code restructure failed: missing block: B:20:?, code lost:            return;     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0194, code lost:            org.jooq.impl.Tools.visitSubquery(r15, r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:22:?, code lost:            return;     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x01fe, code lost:            if (org.jooq.impl.DerivedTable.NO_SUPPORT_CORRELATED_DERIVED_TABLE.contains(r15.dialect()) == false) goto L28;     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0208, code lost:            if (isSimple(r14.select) == false) goto L28;     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x020b, code lost:            r0 = jsonbArrayaggEmulation(r15, org.jooq.impl.DSL.row((java.util.Collection<?>) org.jooq.impl.Tools.map(r14.select.getSelect(), (v0) -> { // org.jooq.impl.ThrowingFunction.apply(java.lang.Object):java.lang.Object            return lambda$accept0$3(v0);        })), true, r14.select.$distinct()).orderBy(r14.select.$orderBy());        org.jooq.impl.Tools.visitSubquery(r15, r14.select.$select(java.util.Arrays.asList(org.jooq.impl.DSL.coalesce(returningClob(r15, r0), (org.jooq.Field<?>[]) new org.jooq.Field[]{returningClob(r15, org.jooq.impl.DSL.jsonbArray((org.jooq.Field<?>[]) new org.jooq.Field[0]))}))).$distinct(false).$orderBy(java.util.Arrays.asList(new org.jooq.SortField[0])));     */
    /* JADX WARN: Code restructure failed: missing block: B:30:?, code lost:            return;     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0288, code lost:            r0 = jsonbArrayaggEmulation(r15, r0, false, false);        r20 = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0296, code lost:            if (r16 == false) goto L31;     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x0299, code lost:            r20 = r0.orderBy(r0.fields());     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x02a9, code lost:            r0 = org.jooq.impl.JSONArrayAgg.patchOracleArrayAggBug(r15, org.jooq.impl.DSL.select(org.jooq.impl.DSL.coalesce(returningClob(r15, r20), (org.jooq.Field<?>[]) new org.jooq.Field[]{returningClob(r15, org.jooq.impl.DSL.jsonbArray((org.jooq.Field<?>[]) new org.jooq.Field[0]))})).from(r0));     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x02d5, code lost:            if (r16 == false) goto L36;     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x02e6, code lost:            if (org.jooq.impl.Multiset.NO_SUPPORT_JSONB_COMPARE.contains(r15.dialect()) == false) goto L36;     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x02e9, code lost:            r15.visit(org.jooq.impl.DSL.field(r0).cast(org.jooq.impl.SQLDataType.VARCHAR));     */
    /* JADX WARN: Code restructure failed: missing block: B:39:?, code lost:            return;     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x0300, code lost:            org.jooq.impl.Tools.visitSubquery(r15, r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:41:?, code lost:            return;     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x036a, code lost:            if (org.jooq.impl.DerivedTable.NO_SUPPORT_CORRELATED_DERIVED_TABLE.contains(r15.dialect()) == false) goto L47;     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x0374, code lost:            if (isSimple(r14.select) == false) goto L47;     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x0380, code lost:            if (r14.select.$distinct() != false) goto L47;     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x0383, code lost:            acceptMultisetSubqueryForXMLEmulation(r15, r16, r14.select.$select(java.util.Arrays.asList(org.jooq.impl.DSL.xmlelement(nResult(r15), (org.jooq.Field<?>[]) new org.jooq.Field[]{xmlaggEmulation(r15, org.jooq.impl.DSL.row((java.util.Collection<?>) org.jooq.impl.Tools.map(r14.select.getSelect(), (v0) -> { // org.jooq.impl.ThrowingFunction.apply(java.lang.Object):java.lang.Object            return lambda$accept0$4(v0);        })), true).orderBy(r14.select.$orderBy())}))).$orderBy(java.util.Arrays.asList(new org.jooq.SortField[0])));     */
    /* JADX WARN: Code restructure failed: missing block: B:51:?, code lost:            return;     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x03df, code lost:            r0 = xmlaggEmulation(r15, r0, false);        r20 = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x03ec, code lost:            if (r16 == false) goto L50;     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x03ef, code lost:            r20 = r0.orderBy(r0.fields());     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x03ff, code lost:            acceptMultisetSubqueryForXMLEmulation(r15, r16, org.jooq.impl.DSL.select(org.jooq.impl.DSL.xmlelement(nResult(r15), (org.jooq.Field<?>[]) new org.jooq.Field[]{r20})).from(r0));     */
    /* JADX WARN: Code restructure failed: missing block: B:56:?, code lost:            return;     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x0092, code lost:            if (org.jooq.impl.DerivedTable.NO_SUPPORT_CORRELATED_DERIVED_TABLE.contains(r15.dialect()) == false) goto L11;     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x009c, code lost:            if (isSimple(r14.select) == false) goto L11;     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x009f, code lost:            r0 = jsonArrayaggEmulation(r15, org.jooq.impl.DSL.row((java.util.Collection<?>) org.jooq.impl.Tools.map(r14.select.getSelect(), (v0) -> { // org.jooq.impl.ThrowingFunction.apply(java.lang.Object):java.lang.Object            return lambda$accept0$2(v0);        })), true, r14.select.$distinct()).orderBy(r14.select.$orderBy());        org.jooq.impl.Tools.visitSubquery(r15, r14.select.$select(java.util.Arrays.asList(org.jooq.impl.DSL.coalesce(returningClob(r15, r0), (org.jooq.Field<?>[]) new org.jooq.Field[]{returningClob(r15, org.jooq.impl.DSL.jsonArray((org.jooq.Field<?>[]) new org.jooq.Field[0]))}))).$distinct(false).$orderBy(java.util.Arrays.asList(new org.jooq.SortField[0])));     */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final void accept0(org.jooq.Context<?> r15, boolean r16) {
        /*
            Method dump skipped, instructions count: 1074
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.Multiset.accept0(org.jooq.Context, boolean):void");
    }

    private static final void acceptMultisetSubqueryForXMLEmulation(Context<?> ctx, boolean multisetCondition, Select<Record1<XML>> s) {
        if (multisetCondition && NO_SUPPORT_XML_COMPARE.contains(ctx.dialect())) {
            ctx.visit(DSL.xmlserializeContent((Field<XML>) DSL.field(s), SQLDataType.VARCHAR));
        } else {
            Tools.visitSubquery(ctx, s);
        }
    }

    private static final boolean isSimple(Select<?> s) {
        return s.$groupBy().isEmpty() && s.$having() == null && s.$window().isEmpty() && s.$qualify() == null && !Tools.selectQueryImpl(s).hasUnions() && s.$offset() == null && s.$limit() == null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Name nResult(Scope ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                return Names.N_RESULT;
        }
    }

    static final Name xsiNil(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                return DSL.name("xsi:nil");
        }
    }

    static final <J> Field<J> returningClob(Scope ctx, JSONObjectReturningStep<J> j) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                return j;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <J> Field<J> returningClob(Scope ctx, JSONArrayReturningStep<J> j) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                return j;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <J> Field<J> returningClob(Scope ctx, JSONArrayAggReturningStep<J> j) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                return j;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final JSONArrayAggOrderByStep<JSON> jsonArrayaggEmulation(Context<?> ctx, Fields fields, boolean agg, boolean distinct) {
        return jsonxArrayaggEmulation(ctx, fields, agg, distinct ? DSL::jsonArrayAggDistinct : DSL::jsonArrayAgg, DSL::jsonObject, DSL::jsonArray);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final JSONArrayAggOrderByStep<JSONB> jsonbArrayaggEmulation(Context<?> ctx, Fields fields, boolean agg, boolean distinct) {
        return jsonxArrayaggEmulation(ctx, fields, agg, distinct ? DSL::jsonbArrayAggDistinct : DSL::jsonbArrayAgg, DSL::jsonbObject, DSL::jsonbArray);
    }

    static final <J> JSONArrayAggOrderByStep<J> jsonxArrayaggEmulation(Context<?> ctx, Fields fields, boolean agg, java.util.function.Function<? super Field<?>, ? extends JSONArrayAggOrderByStep<J>> jsonxArrayAgg, java.util.function.Function<? super Collection<? extends JSONEntry<?>>, ? extends JSONObjectNullStep<J>> jsonxObject, java.util.function.Function<? super Collection<? extends Field<?>>, ? extends JSONArrayNullStep<J>> jsonxArray) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                return jsonxArrayAgg.apply(returningClob(ctx, jsonxArray.apply(Tools.map(fields.fields(), (f, i) -> {
                    return JSONEntryImpl.unescapeNestedJSON(ctx, castForJSON(ctx, agg ? f : DSL.field(Tools.fieldName(i), f.getDataType())));
                })).nullOnNull()));
        }
    }

    static final Field<?> castForJSON(Context<?> ctx, Field<?> field) {
        return field;
    }

    static final Field<?> castForXML(Context<?> ctx, Field<?> field) {
        return field;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final XMLAggOrderByStep<XML> xmlaggEmulation(Context<?> ctx, Fields fields, boolean agg) {
        return DSL.xmlagg(DSL.xmlelement(Names.N_RECORD, Tools.map(fields.fields(), (f, i) -> {
            Field<?> v = castForXML(ctx, agg ? f : DSL.field(Tools.fieldName(i), f.getDataType()));
            String n = Tools.fieldNameString(i);
            DataType<?> t = v.getDataType();
            if (t.isString() || t.isArray()) {
                return DSL.xmlelement(n, DSL.xmlattributes((Field<?>[]) new Field[]{DSL.when(v.isNull(), (Field) DSL.inline("true")).as(xsiNil(ctx))}), (Field<?>[]) new Field[]{v});
            }
            return DSL.xmlelement(n, (Field<?>[]) new Field[]{v});
        })));
    }

    @Override // org.jooq.impl.QOM.Multiset
    public final TableLike<R> $table() {
        return this.table;
    }
}
