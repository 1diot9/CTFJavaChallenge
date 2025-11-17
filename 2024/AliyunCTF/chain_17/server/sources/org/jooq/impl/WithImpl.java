package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import org.jooq.Clause;
import org.jooq.CommonTableExpression;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.DerivedColumnList;
import org.jooq.Field;
import org.jooq.InsertSetStep;
import org.jooq.InsertValuesStepN;
import org.jooq.MergeKeyStepN;
import org.jooq.MergeUsingStep;
import org.jooq.Name;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Record11;
import org.jooq.Record12;
import org.jooq.Record13;
import org.jooq.Record14;
import org.jooq.Record15;
import org.jooq.Record16;
import org.jooq.Record17;
import org.jooq.Record18;
import org.jooq.Record19;
import org.jooq.Record2;
import org.jooq.Record20;
import org.jooq.Record21;
import org.jooq.Record22;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Record6;
import org.jooq.Record7;
import org.jooq.Record8;
import org.jooq.Record9;
import org.jooq.ResultQuery;
import org.jooq.SQL;
import org.jooq.SelectField;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectSelectStep;
import org.jooq.SelectWhereStep;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.WithAsStep;
import org.jooq.WithAsStep1;
import org.jooq.WithAsStep10;
import org.jooq.WithAsStep11;
import org.jooq.WithAsStep12;
import org.jooq.WithAsStep13;
import org.jooq.WithAsStep14;
import org.jooq.WithAsStep15;
import org.jooq.WithAsStep16;
import org.jooq.WithAsStep17;
import org.jooq.WithAsStep18;
import org.jooq.WithAsStep19;
import org.jooq.WithAsStep2;
import org.jooq.WithAsStep20;
import org.jooq.WithAsStep21;
import org.jooq.WithAsStep22;
import org.jooq.WithAsStep3;
import org.jooq.WithAsStep4;
import org.jooq.WithAsStep5;
import org.jooq.WithAsStep6;
import org.jooq.WithAsStep7;
import org.jooq.WithAsStep8;
import org.jooq.WithAsStep9;
import org.jooq.WithStep;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/WithImpl.class */
public final class WithImpl extends AbstractQueryPart implements WithAsStep, WithAsStep1, WithAsStep2, WithAsStep3, WithAsStep4, WithAsStep5, WithAsStep6, WithAsStep7, WithAsStep8, WithAsStep9, WithAsStep10, WithAsStep11, WithAsStep12, WithAsStep13, WithAsStep14, WithAsStep15, WithAsStep16, WithAsStep17, WithAsStep18, WithAsStep19, WithAsStep20, WithAsStep21, WithAsStep22, WithStep, QOM.With {
    private static final Clause[] CLAUSES = {Clause.WITH};
    final CommonTableExpressionList ctes = new CommonTableExpressionList();
    final boolean recursive;
    private Configuration configuration;
    private transient Name alias;
    private transient Name[] fieldAliases;
    private transient BiFunction<? super Field<?>, ? super Integer, ? extends String> fieldNameFunction;

    @Override // org.jooq.WithStep
    public /* bridge */ /* synthetic */ MergeKeyStepN mergeInto(Table table, Collection collection) {
        return mergeInto(table, (Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.WithStep
    public /* bridge */ /* synthetic */ MergeKeyStepN mergeInto(Table table, Field[] fieldArr) {
        return mergeInto(table, (Field<?>[]) fieldArr);
    }

    @Override // org.jooq.WithStep
    public /* bridge */ /* synthetic */ InsertValuesStepN insertInto(Table table, Collection collection) {
        return insertInto(table, (Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.WithStep
    public /* bridge */ /* synthetic */ InsertValuesStepN insertInto(Table table, Field[] fieldArr) {
        return insertInto(table, (Field<?>[]) fieldArr);
    }

    @Override // org.jooq.WithStep
    public /* bridge */ /* synthetic */ WithStep with(Collection collection) {
        return with((Collection<? extends CommonTableExpression<?>>) collection);
    }

    @Override // org.jooq.WithStep
    public /* bridge */ /* synthetic */ WithStep with(CommonTableExpression[] commonTableExpressionArr) {
        return with((CommonTableExpression<?>[]) commonTableExpressionArr);
    }

    @Override // org.jooq.WithStep
    public /* bridge */ /* synthetic */ WithAsStep with(String str, BiFunction biFunction) {
        return with(str, (BiFunction<? super Field<?>, ? super Integer, ? extends String>) biFunction);
    }

    @Override // org.jooq.WithStep
    public /* bridge */ /* synthetic */ WithAsStep with(String str, java.util.function.Function function) {
        return with(str, (java.util.function.Function<? super Field<?>, ? extends String>) function);
    }

    @Override // org.jooq.WithStep
    public /* bridge */ /* synthetic */ WithAsStep with(Name name, Collection collection) {
        return with(name, (Collection<? extends Name>) collection);
    }

    @Override // org.jooq.WithStep
    public /* bridge */ /* synthetic */ WithAsStep with(String str, Collection collection) {
        return with(str, (Collection<String>) collection);
    }

    @Override // org.jooq.impl.QOM.With
    public /* bridge */ /* synthetic */ QOM.With $commonTableExpressions(QOM.UnmodifiableList unmodifiableList) {
        return $commonTableExpressions((QOM.UnmodifiableList<? extends CommonTableExpression<?>>) unmodifiableList);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WithImpl(Configuration configuration, boolean recursive) {
        this.configuration = configuration;
        this.recursive = recursive;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (Transformations.transformInlineCTE(ctx.configuration())) {
            return;
        }
        CommonTableExpressionList list = this.ctes;
        if (!list.isEmpty()) {
            acceptWithRecursive(ctx, this.recursive);
            ctx.data(Tools.BooleanDataKey.DATA_LIST_ALREADY_INDENTED, true, c1 -> {
                c1.formatIndentStart().formatSeparator().declareCTE(true, c2 -> {
                    c2.visit(list);
                }).formatIndentEnd().formatSeparator();
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v4, types: [org.jooq.Context] */
    public static final void acceptWithRecursive(Context<?> ctx, boolean recursive) {
        ctx.visit(Keywords.K_WITH);
        if (recursive) {
            ctx.sql(' ').visit(Keywords.K_RECURSIVE);
        }
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    private final WithStep as0(ResultQuery query, Boolean materialized) {
        CommonTableExpression cte;
        DerivedColumnList dcl = this.fieldNameFunction != null ? DSL.name(this.alias).fields(this.fieldNameFunction) : DSL.name(this.alias).fields(this.fieldAliases);
        if (materialized == null) {
            cte = dcl.as(query);
        } else if (materialized.booleanValue()) {
            cte = dcl.asMaterialized(query);
        } else {
            cte = dcl.asNotMaterialized(query);
        }
        this.ctes.add((CommonTableExpressionList) cte);
        this.alias = null;
        this.fieldAliases = null;
        this.fieldNameFunction = null;
        return this;
    }

    @Override // org.jooq.WithAsStep, org.jooq.WithAsStep1, org.jooq.WithAsStep2, org.jooq.WithAsStep3, org.jooq.WithAsStep4, org.jooq.WithAsStep5, org.jooq.WithAsStep6, org.jooq.WithAsStep7, org.jooq.WithAsStep8, org.jooq.WithAsStep9, org.jooq.WithAsStep10, org.jooq.WithAsStep11, org.jooq.WithAsStep12, org.jooq.WithAsStep13, org.jooq.WithAsStep14, org.jooq.WithAsStep15, org.jooq.WithAsStep16, org.jooq.WithAsStep17, org.jooq.WithAsStep18, org.jooq.WithAsStep19, org.jooq.WithAsStep20, org.jooq.WithAsStep21, org.jooq.WithAsStep22
    public final WithStep as(ResultQuery query) {
        return as0(query, null);
    }

    @Override // org.jooq.WithAsStep, org.jooq.WithAsStep1, org.jooq.WithAsStep2, org.jooq.WithAsStep3, org.jooq.WithAsStep4, org.jooq.WithAsStep5, org.jooq.WithAsStep6, org.jooq.WithAsStep7, org.jooq.WithAsStep8, org.jooq.WithAsStep9, org.jooq.WithAsStep10, org.jooq.WithAsStep11, org.jooq.WithAsStep12, org.jooq.WithAsStep13, org.jooq.WithAsStep14, org.jooq.WithAsStep15, org.jooq.WithAsStep16, org.jooq.WithAsStep17, org.jooq.WithAsStep18, org.jooq.WithAsStep19, org.jooq.WithAsStep20, org.jooq.WithAsStep21, org.jooq.WithAsStep22
    public final WithStep asMaterialized(ResultQuery query) {
        return as0(query, true);
    }

    @Override // org.jooq.WithAsStep, org.jooq.WithAsStep1, org.jooq.WithAsStep2, org.jooq.WithAsStep3, org.jooq.WithAsStep4, org.jooq.WithAsStep5, org.jooq.WithAsStep6, org.jooq.WithAsStep7, org.jooq.WithAsStep8, org.jooq.WithAsStep9, org.jooq.WithAsStep10, org.jooq.WithAsStep11, org.jooq.WithAsStep12, org.jooq.WithAsStep13, org.jooq.WithAsStep14, org.jooq.WithAsStep15, org.jooq.WithAsStep16, org.jooq.WithAsStep17, org.jooq.WithAsStep18, org.jooq.WithAsStep19, org.jooq.WithAsStep20, org.jooq.WithAsStep21, org.jooq.WithAsStep22
    public final WithStep asNotMaterialized(ResultQuery query) {
        return as0(query, false);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a) {
        return with(DSL.name(a));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, String... f) {
        return with(DSL.name(a), (Name[]) Tools.map(f, s -> {
            return DSL.name(s);
        }, x$0 -> {
            return new Name[x$0];
        }));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, Collection<String> f) {
        return with(DSL.name(a), (Name[]) Tools.map(f, s -> {
            return DSL.name(s);
        }, x$0 -> {
            return new Name[x$0];
        }));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a) {
        return with(a, new Name[0]);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Name... f) {
        this.alias = a;
        this.fieldAliases = f;
        return this;
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Collection<? extends Name> f) {
        return with(a, (Name[]) f.toArray(Tools.EMPTY_NAME));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, java.util.function.Function<? super Field<?>, ? extends String> f) {
        this.alias = DSL.name(a);
        this.fieldNameFunction = (field, i) -> {
            return (String) f.apply(field);
        };
        return this;
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, BiFunction<? super Field<?>, ? super Integer, ? extends String> f) {
        this.alias = DSL.name(a);
        this.fieldNameFunction = f;
        return this;
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, String fieldAlias1) {
        return with(DSL.name(a), Tools.names(new String[]{fieldAlias1}));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, String fieldAlias1, String fieldAlias2) {
        return with(DSL.name(a), Tools.names(new String[]{fieldAlias1, fieldAlias2}));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, String fieldAlias1, String fieldAlias2, String fieldAlias3) {
        return with(DSL.name(a), Tools.names(new String[]{fieldAlias1, fieldAlias2, fieldAlias3}));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4) {
        return with(DSL.name(a), Tools.names(new String[]{fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4}));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5) {
        return with(DSL.name(a), Tools.names(new String[]{fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5}));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6) {
        return with(DSL.name(a), Tools.names(new String[]{fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6}));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7) {
        return with(DSL.name(a), Tools.names(new String[]{fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7}));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8) {
        return with(DSL.name(a), Tools.names(new String[]{fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8}));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9) {
        return with(DSL.name(a), Tools.names(new String[]{fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9}));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10) {
        return with(DSL.name(a), Tools.names(new String[]{fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10}));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11) {
        return with(DSL.name(a), Tools.names(new String[]{fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11}));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12) {
        return with(DSL.name(a), Tools.names(new String[]{fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12}));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13) {
        return with(DSL.name(a), Tools.names(new String[]{fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13}));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14) {
        return with(DSL.name(a), Tools.names(new String[]{fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14}));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15) {
        return with(DSL.name(a), Tools.names(new String[]{fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15}));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16) {
        return with(DSL.name(a), Tools.names(new String[]{fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16}));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17) {
        return with(DSL.name(a), Tools.names(new String[]{fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17}));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18) {
        return with(DSL.name(a), Tools.names(new String[]{fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18}));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19) {
        return with(DSL.name(a), Tools.names(new String[]{fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19}));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19, String fieldAlias20) {
        return with(DSL.name(a), Tools.names(new String[]{fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20}));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19, String fieldAlias20, String fieldAlias21) {
        return with(DSL.name(a), Tools.names(new String[]{fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21}));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(String a, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19, String fieldAlias20, String fieldAlias21, String fieldAlias22) {
        return with(DSL.name(a), Tools.names(new String[]{fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21, fieldAlias22}));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Name fieldAlias1) {
        return with(a, fieldAlias1);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Name fieldAlias1, Name fieldAlias2) {
        return with(a, fieldAlias1, fieldAlias2);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3) {
        return with(a, fieldAlias1, fieldAlias2, fieldAlias3);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4) {
        return with(a, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5) {
        return with(a, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6) {
        return with(a, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7) {
        return with(a, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8) {
        return with(a, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9) {
        return with(a, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10) {
        return with(a, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11) {
        return with(a, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12) {
        return with(a, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13) {
        return with(a, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14) {
        return with(a, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15) {
        return with(a, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16) {
        return with(a, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17) {
        return with(a, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18) {
        return with(a, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19) {
        return with(a, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19, Name fieldAlias20) {
        return with(a, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19, Name fieldAlias20, Name fieldAlias21) {
        return with(a, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Name a, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19, Name fieldAlias20, Name fieldAlias21, Name fieldAlias22) {
        return with(a, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21, fieldAlias22);
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(CommonTableExpression<?>... tables) {
        return with((Collection<? extends CommonTableExpression<?>>) Arrays.asList(tables));
    }

    @Override // org.jooq.WithStep
    public final WithImpl with(Collection<? extends CommonTableExpression<?>> tables) {
        for (CommonTableExpression<?> table : tables) {
            this.ctes.add((CommonTableExpressionList) table);
        }
        return this;
    }

    @Override // org.jooq.WithStep
    public final <R extends Record> SelectWhereStep<R> selectFrom(TableLike<R> table) {
        return new SelectImpl(this.configuration, this).from((TableLike<?>) table);
    }

    @Override // org.jooq.WithStep
    public final SelectWhereStep<Record> selectFrom(Name table) {
        return new SelectImpl(this.configuration, this).from(table);
    }

    @Override // org.jooq.WithStep
    public final SelectWhereStep<Record> selectFrom(SQL sql) {
        return new SelectImpl(this.configuration, this).from(sql);
    }

    @Override // org.jooq.WithStep
    public final SelectWhereStep<Record> selectFrom(String sql) {
        return new SelectImpl(this.configuration, this).from(sql);
    }

    @Override // org.jooq.WithStep
    public final SelectWhereStep<Record> selectFrom(String sql, Object... bindings) {
        return new SelectImpl(this.configuration, this).from(sql, bindings);
    }

    @Override // org.jooq.WithStep
    public final SelectWhereStep<Record> selectFrom(String sql, QueryPart... parts) {
        return new SelectImpl(this.configuration, this).from(sql, parts);
    }

    @Override // org.jooq.WithStep
    public final SelectSelectStep<Record> select(Collection<? extends SelectFieldOrAsterisk> fields) {
        return new SelectImpl(this.configuration, this).select(fields);
    }

    @Override // org.jooq.WithStep
    public final SelectSelectStep<Record> select(SelectFieldOrAsterisk... fields) {
        return new SelectImpl(this.configuration, this).select(fields);
    }

    @Override // org.jooq.WithStep
    public final <T1> SelectSelectStep<Record1<T1>> select(SelectField<T1> selectField) {
        return (SelectSelectStep<Record1<T1>>) select(selectField);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2> SelectSelectStep<Record2<T1, T2>> select(SelectField<T1> selectField, SelectField<T2> selectField2) {
        return (SelectSelectStep<Record2<T1, T2>>) select(selectField, selectField2);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3> SelectSelectStep<Record3<T1, T2, T3>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3) {
        return (SelectSelectStep<Record3<T1, T2, T3>>) select(selectField, selectField2, selectField3);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4> SelectSelectStep<Record4<T1, T2, T3, T4>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4) {
        return (SelectSelectStep<Record4<T1, T2, T3, T4>>) select(selectField, selectField2, selectField3, selectField4);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5> SelectSelectStep<Record5<T1, T2, T3, T4, T5>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5) {
        return (SelectSelectStep<Record5<T1, T2, T3, T4, T5>>) select(selectField, selectField2, selectField3, selectField4, selectField5);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6> SelectSelectStep<Record6<T1, T2, T3, T4, T5, T6>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6) {
        return (SelectSelectStep<Record6<T1, T2, T3, T4, T5, T6>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7> SelectSelectStep<Record7<T1, T2, T3, T4, T5, T6, T7>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7) {
        return (SelectSelectStep<Record7<T1, T2, T3, T4, T5, T6, T7>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8> SelectSelectStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8) {
        return (SelectSelectStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9> SelectSelectStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9) {
        return (SelectSelectStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> SelectSelectStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10) {
        return (SelectSelectStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> SelectSelectStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11) {
        return (SelectSelectStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> SelectSelectStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12) {
        return (SelectSelectStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> SelectSelectStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13) {
        return (SelectSelectStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> SelectSelectStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14) {
        return (SelectSelectStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> SelectSelectStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15) {
        return (SelectSelectStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> SelectSelectStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16) {
        return (SelectSelectStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> SelectSelectStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17) {
        return (SelectSelectStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> SelectSelectStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18) {
        return (SelectSelectStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> SelectSelectStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19) {
        return (SelectSelectStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> SelectSelectStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20) {
        return (SelectSelectStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> SelectSelectStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21) {
        return (SelectSelectStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20, selectField21);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> SelectSelectStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> select(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21, SelectField<T22> selectField22) {
        return (SelectSelectStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>>) select(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20, selectField21, selectField22);
    }

    @Override // org.jooq.WithStep
    public final SelectSelectStep<Record> selectDistinct(Collection<? extends SelectFieldOrAsterisk> fields) {
        return new SelectImpl(this.configuration, this, true).select(fields);
    }

    @Override // org.jooq.WithStep
    public final SelectSelectStep<Record> selectDistinct(SelectFieldOrAsterisk... fields) {
        return new SelectImpl(this.configuration, this, true).select(fields);
    }

    @Override // org.jooq.WithStep
    public final <T1> SelectSelectStep<Record1<T1>> selectDistinct(SelectField<T1> selectField) {
        return (SelectSelectStep<Record1<T1>>) selectDistinct(selectField);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2> SelectSelectStep<Record2<T1, T2>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2) {
        return (SelectSelectStep<Record2<T1, T2>>) selectDistinct(selectField, selectField2);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3> SelectSelectStep<Record3<T1, T2, T3>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3) {
        return (SelectSelectStep<Record3<T1, T2, T3>>) selectDistinct(selectField, selectField2, selectField3);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4> SelectSelectStep<Record4<T1, T2, T3, T4>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4) {
        return (SelectSelectStep<Record4<T1, T2, T3, T4>>) selectDistinct(selectField, selectField2, selectField3, selectField4);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5> SelectSelectStep<Record5<T1, T2, T3, T4, T5>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5) {
        return (SelectSelectStep<Record5<T1, T2, T3, T4, T5>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6> SelectSelectStep<Record6<T1, T2, T3, T4, T5, T6>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6) {
        return (SelectSelectStep<Record6<T1, T2, T3, T4, T5, T6>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7> SelectSelectStep<Record7<T1, T2, T3, T4, T5, T6, T7>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7) {
        return (SelectSelectStep<Record7<T1, T2, T3, T4, T5, T6, T7>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8> SelectSelectStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8) {
        return (SelectSelectStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9> SelectSelectStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9) {
        return (SelectSelectStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> SelectSelectStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10) {
        return (SelectSelectStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> SelectSelectStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11) {
        return (SelectSelectStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> SelectSelectStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12) {
        return (SelectSelectStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> SelectSelectStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13) {
        return (SelectSelectStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> SelectSelectStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14) {
        return (SelectSelectStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> SelectSelectStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15) {
        return (SelectSelectStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> SelectSelectStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16) {
        return (SelectSelectStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> SelectSelectStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17) {
        return (SelectSelectStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> SelectSelectStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18) {
        return (SelectSelectStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> SelectSelectStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19) {
        return (SelectSelectStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> SelectSelectStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20) {
        return (SelectSelectStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> SelectSelectStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21) {
        return (SelectSelectStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20, selectField21);
    }

    @Override // org.jooq.WithStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> SelectSelectStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> selectDistinct(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21, SelectField<T22> selectField22) {
        return (SelectSelectStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>>) selectDistinct(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20, selectField21, selectField22);
    }

    @Override // org.jooq.WithStep
    public final SelectSelectStep<Record1<Integer>> selectZero() {
        return new SelectImpl(this.configuration, this).select(DSL.zero());
    }

    @Override // org.jooq.WithStep
    public final SelectSelectStep<Record1<Integer>> selectOne() {
        return new SelectImpl(this.configuration, this).select(DSL.one());
    }

    @Override // org.jooq.WithStep
    public final SelectSelectStep<Record1<Integer>> selectCount() {
        return new SelectImpl(this.configuration, this).select(DSL.count());
    }

    @Override // org.jooq.WithStep
    public final <R extends Record> InsertSetStep<R> insertInto(Table<R> into) {
        return new InsertImpl(this.configuration, this, into);
    }

    @Override // org.jooq.WithStep
    public final <R extends Record, T1> InsertImpl insertInto(Table<R> into, Field<T1> field1) {
        return insertInto((Table) into, (Collection<? extends Field<?>>) Arrays.asList(field1));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record, T1, T2> InsertImpl insertInto(Table<R> into, Field<T1> field1, Field<T2> field2) {
        return insertInto((Table) into, (Collection<? extends Field<?>>) Arrays.asList(field1, field2));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record, T1, T2, T3> InsertImpl insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3) {
        return insertInto((Table) into, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record, T1, T2, T3, T4> InsertImpl insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4) {
        return insertInto((Table) into, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record, T1, T2, T3, T4, T5> InsertImpl insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5) {
        return insertInto((Table) into, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record, T1, T2, T3, T4, T5, T6> InsertImpl insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6) {
        return insertInto((Table) into, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7> InsertImpl insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7) {
        return insertInto((Table) into, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8> InsertImpl insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8) {
        return insertInto((Table) into, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9> InsertImpl insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9) {
        return insertInto((Table) into, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> InsertImpl insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10) {
        return insertInto((Table) into, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> InsertImpl insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11) {
        return insertInto((Table) into, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> InsertImpl insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12) {
        return insertInto((Table) into, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> InsertImpl insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13) {
        return insertInto((Table) into, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> InsertImpl insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14) {
        return insertInto((Table) into, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> InsertImpl insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15) {
        return insertInto((Table) into, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> InsertImpl insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16) {
        return insertInto((Table) into, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> InsertImpl insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17) {
        return insertInto((Table) into, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> InsertImpl insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18) {
        return insertInto((Table) into, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> InsertImpl insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19) {
        return insertInto((Table) into, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> InsertImpl insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20) {
        return insertInto((Table) into, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> InsertImpl insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21) {
        return insertInto((Table) into, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> InsertImpl insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22) {
        return insertInto((Table) into, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record> InsertImpl insertInto(Table<R> into, Field<?>... fields) {
        return insertInto((Table) into, (Collection<? extends Field<?>>) Arrays.asList(fields));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record> InsertImpl insertInto(Table<R> into, Collection<? extends Field<?>> fields) {
        return new InsertImpl(this.configuration, this, into, fields);
    }

    @Override // org.jooq.WithStep
    public final <R extends Record> UpdateImpl update(Table<R> table) {
        return new UpdateImpl(this.configuration, this, table);
    }

    @Override // org.jooq.WithStep
    public final <R extends Record> MergeUsingStep<R> mergeInto(Table<R> table) {
        return new MergeImpl(this.configuration, this, table);
    }

    @Override // org.jooq.WithStep
    @Deprecated(forRemoval = true, since = "3.14")
    public final <R extends Record, T1> MergeImpl mergeInto(Table<R> table, Field<T1> field1) {
        return mergeInto((Table) table, (Collection<? extends Field<?>>) Arrays.asList(field1));
    }

    @Override // org.jooq.WithStep
    @Deprecated(forRemoval = true, since = "3.14")
    public final <R extends Record, T1, T2> MergeImpl mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2) {
        return mergeInto((Table) table, (Collection<? extends Field<?>>) Arrays.asList(field1, field2));
    }

    @Override // org.jooq.WithStep
    @Deprecated(forRemoval = true, since = "3.14")
    public final <R extends Record, T1, T2, T3> MergeImpl mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3) {
        return mergeInto((Table) table, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3));
    }

    @Override // org.jooq.WithStep
    @Deprecated(forRemoval = true, since = "3.14")
    public final <R extends Record, T1, T2, T3, T4> MergeImpl mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4) {
        return mergeInto((Table) table, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4));
    }

    @Override // org.jooq.WithStep
    @Deprecated(forRemoval = true, since = "3.14")
    public final <R extends Record, T1, T2, T3, T4, T5> MergeImpl mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5) {
        return mergeInto((Table) table, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5));
    }

    @Override // org.jooq.WithStep
    @Deprecated(forRemoval = true, since = "3.14")
    public final <R extends Record, T1, T2, T3, T4, T5, T6> MergeImpl mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6) {
        return mergeInto((Table) table, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6));
    }

    @Override // org.jooq.WithStep
    @Deprecated(forRemoval = true, since = "3.14")
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7> MergeImpl mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7) {
        return mergeInto((Table) table, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7));
    }

    @Override // org.jooq.WithStep
    @Deprecated(forRemoval = true, since = "3.14")
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8> MergeImpl mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8) {
        return mergeInto((Table) table, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8));
    }

    @Override // org.jooq.WithStep
    @Deprecated(forRemoval = true, since = "3.14")
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9> MergeImpl mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9) {
        return mergeInto((Table) table, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9));
    }

    @Override // org.jooq.WithStep
    @Deprecated(forRemoval = true, since = "3.14")
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> MergeImpl mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10) {
        return mergeInto((Table) table, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10));
    }

    @Override // org.jooq.WithStep
    @Deprecated(forRemoval = true, since = "3.14")
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> MergeImpl mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11) {
        return mergeInto((Table) table, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11));
    }

    @Override // org.jooq.WithStep
    @Deprecated(forRemoval = true, since = "3.14")
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> MergeImpl mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12) {
        return mergeInto((Table) table, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12));
    }

    @Override // org.jooq.WithStep
    @Deprecated(forRemoval = true, since = "3.14")
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> MergeImpl mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13) {
        return mergeInto((Table) table, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13));
    }

    @Override // org.jooq.WithStep
    @Deprecated(forRemoval = true, since = "3.14")
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> MergeImpl mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14) {
        return mergeInto((Table) table, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14));
    }

    @Override // org.jooq.WithStep
    @Deprecated(forRemoval = true, since = "3.14")
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> MergeImpl mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15) {
        return mergeInto((Table) table, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15));
    }

    @Override // org.jooq.WithStep
    @Deprecated(forRemoval = true, since = "3.14")
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> MergeImpl mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16) {
        return mergeInto((Table) table, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16));
    }

    @Override // org.jooq.WithStep
    @Deprecated(forRemoval = true, since = "3.14")
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> MergeImpl mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17) {
        return mergeInto((Table) table, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17));
    }

    @Override // org.jooq.WithStep
    @Deprecated(forRemoval = true, since = "3.14")
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> MergeImpl mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18) {
        return mergeInto((Table) table, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18));
    }

    @Override // org.jooq.WithStep
    @Deprecated(forRemoval = true, since = "3.14")
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> MergeImpl mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19) {
        return mergeInto((Table) table, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19));
    }

    @Override // org.jooq.WithStep
    @Deprecated(forRemoval = true, since = "3.14")
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> MergeImpl mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20) {
        return mergeInto((Table) table, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20));
    }

    @Override // org.jooq.WithStep
    @Deprecated(forRemoval = true, since = "3.14")
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> MergeImpl mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21) {
        return mergeInto((Table) table, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21));
    }

    @Override // org.jooq.WithStep
    @Deprecated(forRemoval = true, since = "3.14")
    public final <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> MergeImpl mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22) {
        return mergeInto((Table) table, (Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record> MergeImpl mergeInto(Table<R> table, Field<?>... fields) {
        return mergeInto((Table) table, (Collection<? extends Field<?>>) Arrays.asList(fields));
    }

    @Override // org.jooq.WithStep
    public final <R extends Record> MergeImpl mergeInto(Table<R> table, Collection<? extends Field<?>> fields) {
        return new MergeImpl(this.configuration, this, table, fields);
    }

    @Override // org.jooq.WithStep
    public final <R extends Record> DeleteImpl delete(Table<R> table) {
        return new DeleteImpl(this.configuration, this, table);
    }

    @Override // org.jooq.impl.QOM.With
    public final QOM.UnmodifiableList<? extends CommonTableExpression<?>> $commonTableExpressions() {
        return QOM.unmodifiable((List) this.ctes);
    }

    @Override // org.jooq.impl.QOM.With
    public final WithImpl $commonTableExpressions(QOM.UnmodifiableList<? extends CommonTableExpression<?>> c) {
        return new WithImpl(this.configuration, this.recursive).with((Collection<? extends CommonTableExpression<?>>) c);
    }

    @Override // org.jooq.impl.QOM.With
    public final boolean $recursive() {
        return this.recursive;
    }

    @Override // org.jooq.impl.QOM.With
    public final WithImpl $recursive(boolean r) {
        return new WithImpl(this.configuration, r).with((Collection<? extends CommonTableExpression<?>>) this.ctes);
    }
}
