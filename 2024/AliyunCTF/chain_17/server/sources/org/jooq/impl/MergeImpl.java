package org.jooq.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.jooq.CaseConditionStep;
import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.FieldOrRow;
import org.jooq.FieldOrRowOrSelect;
import org.jooq.Merge;
import org.jooq.MergeFinalStep;
import org.jooq.MergeKeyStep1;
import org.jooq.MergeKeyStep10;
import org.jooq.MergeKeyStep11;
import org.jooq.MergeKeyStep12;
import org.jooq.MergeKeyStep13;
import org.jooq.MergeKeyStep14;
import org.jooq.MergeKeyStep15;
import org.jooq.MergeKeyStep16;
import org.jooq.MergeKeyStep17;
import org.jooq.MergeKeyStep18;
import org.jooq.MergeKeyStep19;
import org.jooq.MergeKeyStep2;
import org.jooq.MergeKeyStep20;
import org.jooq.MergeKeyStep21;
import org.jooq.MergeKeyStep22;
import org.jooq.MergeKeyStep3;
import org.jooq.MergeKeyStep4;
import org.jooq.MergeKeyStep5;
import org.jooq.MergeKeyStep6;
import org.jooq.MergeKeyStep7;
import org.jooq.MergeKeyStep8;
import org.jooq.MergeKeyStep9;
import org.jooq.MergeKeyStepN;
import org.jooq.MergeMatchedDeleteStep;
import org.jooq.MergeMatchedSetMoreStep;
import org.jooq.MergeMatchedStep;
import org.jooq.MergeMatchedThenStep;
import org.jooq.MergeNotMatchedSetMoreStep;
import org.jooq.MergeNotMatchedValuesStep1;
import org.jooq.MergeNotMatchedValuesStep10;
import org.jooq.MergeNotMatchedValuesStep11;
import org.jooq.MergeNotMatchedValuesStep12;
import org.jooq.MergeNotMatchedValuesStep13;
import org.jooq.MergeNotMatchedValuesStep14;
import org.jooq.MergeNotMatchedValuesStep15;
import org.jooq.MergeNotMatchedValuesStep16;
import org.jooq.MergeNotMatchedValuesStep17;
import org.jooq.MergeNotMatchedValuesStep18;
import org.jooq.MergeNotMatchedValuesStep19;
import org.jooq.MergeNotMatchedValuesStep2;
import org.jooq.MergeNotMatchedValuesStep20;
import org.jooq.MergeNotMatchedValuesStep21;
import org.jooq.MergeNotMatchedValuesStep22;
import org.jooq.MergeNotMatchedValuesStep3;
import org.jooq.MergeNotMatchedValuesStep4;
import org.jooq.MergeNotMatchedValuesStep5;
import org.jooq.MergeNotMatchedValuesStep6;
import org.jooq.MergeNotMatchedValuesStep7;
import org.jooq.MergeNotMatchedValuesStep8;
import org.jooq.MergeNotMatchedValuesStep9;
import org.jooq.MergeNotMatchedValuesStepN;
import org.jooq.MergeNotMatchedWhereStep;
import org.jooq.MergeOnConditionStep;
import org.jooq.MergeOnStep;
import org.jooq.MergeUsingStep;
import org.jooq.MergeValuesStep1;
import org.jooq.MergeValuesStep10;
import org.jooq.MergeValuesStep11;
import org.jooq.MergeValuesStep12;
import org.jooq.MergeValuesStep13;
import org.jooq.MergeValuesStep14;
import org.jooq.MergeValuesStep15;
import org.jooq.MergeValuesStep16;
import org.jooq.MergeValuesStep17;
import org.jooq.MergeValuesStep18;
import org.jooq.MergeValuesStep19;
import org.jooq.MergeValuesStep2;
import org.jooq.MergeValuesStep20;
import org.jooq.MergeValuesStep21;
import org.jooq.MergeValuesStep22;
import org.jooq.MergeValuesStep3;
import org.jooq.MergeValuesStep4;
import org.jooq.MergeValuesStep5;
import org.jooq.MergeValuesStep6;
import org.jooq.MergeValuesStep7;
import org.jooq.MergeValuesStep8;
import org.jooq.MergeValuesStep9;
import org.jooq.MergeValuesStepN;
import org.jooq.Operator;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SQL;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.UniqueKey;
import org.jooq.impl.FieldMapForUpdate;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/MergeImpl.class */
public final class MergeImpl<R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> extends AbstractRowCountQuery implements QOM.UNotYetImplemented, MergeUsingStep<R>, MergeKeyStep1<R, T1>, MergeKeyStep2<R, T1, T2>, MergeKeyStep3<R, T1, T2, T3>, MergeKeyStep4<R, T1, T2, T3, T4>, MergeKeyStep5<R, T1, T2, T3, T4, T5>, MergeKeyStep6<R, T1, T2, T3, T4, T5, T6>, MergeKeyStep7<R, T1, T2, T3, T4, T5, T6, T7>, MergeKeyStep8<R, T1, T2, T3, T4, T5, T6, T7, T8>, MergeKeyStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9>, MergeKeyStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>, MergeKeyStep11<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>, MergeKeyStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>, MergeKeyStep13<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>, MergeKeyStep14<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>, MergeKeyStep15<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>, MergeKeyStep16<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>, MergeKeyStep17<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>, MergeKeyStep18<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>, MergeKeyStep19<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>, MergeKeyStep20<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>, MergeKeyStep21<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>, MergeKeyStep22<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>, MergeOnStep<R>, MergeOnConditionStep<R>, MergeMatchedSetMoreStep<R>, MergeMatchedThenStep<R>, MergeNotMatchedSetMoreStep<R>, MergeNotMatchedValuesStep1<R, T1>, MergeNotMatchedValuesStep2<R, T1, T2>, MergeNotMatchedValuesStep3<R, T1, T2, T3>, MergeNotMatchedValuesStep4<R, T1, T2, T3, T4>, MergeNotMatchedValuesStep5<R, T1, T2, T3, T4, T5>, MergeNotMatchedValuesStep6<R, T1, T2, T3, T4, T5, T6>, MergeNotMatchedValuesStep7<R, T1, T2, T3, T4, T5, T6, T7>, MergeNotMatchedValuesStep8<R, T1, T2, T3, T4, T5, T6, T7, T8>, MergeNotMatchedValuesStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9>, MergeNotMatchedValuesStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>, MergeNotMatchedValuesStep11<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>, MergeNotMatchedValuesStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>, MergeNotMatchedValuesStep13<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>, MergeNotMatchedValuesStep14<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>, MergeNotMatchedValuesStep15<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>, MergeNotMatchedValuesStep16<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>, MergeNotMatchedValuesStep17<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>, MergeNotMatchedValuesStep18<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>, MergeNotMatchedValuesStep19<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>, MergeNotMatchedValuesStep20<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>, MergeNotMatchedValuesStep21<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>, MergeNotMatchedValuesStep22<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>, MergeNotMatchedValuesStepN<R> {
    private static final Clause[] CLAUSES = {Clause.MERGE};
    private static final Set<SQLDialect> NO_SUPPORT_MULTI = SQLDialect.supportedBy(SQLDialect.HSQLDB);
    private static final Set<SQLDialect> REQUIRE_NEGATION = SQLDialect.supportedBy(SQLDialect.H2, SQLDialect.HSQLDB);
    private static final Set<SQLDialect> NO_SUPPORT_CONDITION_AFTER_NO_CONDITION = SQLDialect.supportedBy(SQLDialect.FIREBIRD, SQLDialect.POSTGRES);
    private final WithImpl with;
    private final Table<R> table;
    private final ConditionProviderImpl on;
    private TableLike<?> using;
    private boolean usingDual;
    private transient boolean matchedClause;
    private final List<MatchedClause> matched;
    private transient boolean notMatchedClause;
    private final List<NotMatchedClause> notMatched;
    private boolean upsertStyle;
    private QueryPartList<Field<?>> upsertFields;
    private QueryPartList<Field<?>> upsertKeys;
    private QueryPartList<Field<?>> upsertValues;
    private Select<?> upsertSelect;

    @Override // org.jooq.MergeUsingStep
    public /* bridge */ /* synthetic */ MergeKeyStepN columns(Collection collection) {
        return columns((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeUsingStep
    public /* bridge */ /* synthetic */ MergeKeyStepN columns(Field[] fieldArr) {
        return columns((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.MergeUsingStep
    public /* bridge */ /* synthetic */ MergeOnStep using(TableLike tableLike) {
        return using((TableLike<?>) tableLike);
    }

    @Override // org.jooq.MergeKeyStepN, org.jooq.MergeKeyStep1, org.jooq.MergeKeyStep2, org.jooq.MergeKeyStep3, org.jooq.MergeKeyStep4, org.jooq.MergeKeyStep5, org.jooq.MergeKeyStep6, org.jooq.MergeKeyStep7, org.jooq.MergeKeyStep8, org.jooq.MergeKeyStep9, org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStepN key(Collection collection) {
        return key((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeKeyStepN, org.jooq.MergeKeyStep1, org.jooq.MergeKeyStep2, org.jooq.MergeKeyStep3, org.jooq.MergeKeyStep4, org.jooq.MergeKeyStep5, org.jooq.MergeKeyStep6, org.jooq.MergeKeyStep7, org.jooq.MergeKeyStep8, org.jooq.MergeKeyStep9, org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStepN key(Field[] fieldArr) {
        return key((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.MergeValuesStepN, org.jooq.MergeValuesStep1, org.jooq.MergeValuesStep2, org.jooq.MergeValuesStep3, org.jooq.MergeValuesStep4, org.jooq.MergeValuesStep5, org.jooq.MergeValuesStep6, org.jooq.MergeValuesStep7, org.jooq.MergeValuesStep8, org.jooq.MergeValuesStep9, org.jooq.MergeValuesStep10, org.jooq.MergeValuesStep11, org.jooq.MergeValuesStep12, org.jooq.MergeValuesStep13, org.jooq.MergeValuesStep14, org.jooq.MergeValuesStep15, org.jooq.MergeValuesStep16, org.jooq.MergeValuesStep17, org.jooq.MergeValuesStep18, org.jooq.MergeValuesStep19, org.jooq.MergeValuesStep20, org.jooq.MergeValuesStep21, org.jooq.MergeValuesStep22, org.jooq.MergeNotMatchedValuesStep1, org.jooq.MergeNotMatchedValuesStep2, org.jooq.MergeNotMatchedValuesStep3, org.jooq.MergeNotMatchedValuesStep4, org.jooq.MergeNotMatchedValuesStep5, org.jooq.MergeNotMatchedValuesStep6, org.jooq.MergeNotMatchedValuesStep7, org.jooq.MergeNotMatchedValuesStep8, org.jooq.MergeNotMatchedValuesStep9, org.jooq.MergeNotMatchedValuesStep10, org.jooq.MergeNotMatchedValuesStep11, org.jooq.MergeNotMatchedValuesStep12, org.jooq.MergeNotMatchedValuesStep13, org.jooq.MergeNotMatchedValuesStep14, org.jooq.MergeNotMatchedValuesStep15, org.jooq.MergeNotMatchedValuesStep16, org.jooq.MergeNotMatchedValuesStep17, org.jooq.MergeNotMatchedValuesStep18, org.jooq.MergeNotMatchedValuesStep19, org.jooq.MergeNotMatchedValuesStep20, org.jooq.MergeNotMatchedValuesStep21, org.jooq.MergeNotMatchedValuesStep22, org.jooq.MergeNotMatchedValuesStepN
    public /* bridge */ /* synthetic */ Merge values(Collection collection) {
        return values((Collection<?>) collection);
    }

    @Override // org.jooq.MergeValuesStepN, org.jooq.MergeNotMatchedValuesStepN
    public /* bridge */ /* synthetic */ Merge values(Field[] fieldArr) {
        return values((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.MergeKeyStep1, org.jooq.MergeKeyStep2, org.jooq.MergeKeyStep3, org.jooq.MergeKeyStep4, org.jooq.MergeKeyStep5, org.jooq.MergeKeyStep6, org.jooq.MergeKeyStep7, org.jooq.MergeKeyStep8, org.jooq.MergeKeyStep9, org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep1 key(Collection collection) {
        return key((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeKeyStep1, org.jooq.MergeKeyStep2, org.jooq.MergeKeyStep3, org.jooq.MergeKeyStep4, org.jooq.MergeKeyStep5, org.jooq.MergeKeyStep6, org.jooq.MergeKeyStep7, org.jooq.MergeKeyStep8, org.jooq.MergeKeyStep9, org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep1 key(Field[] fieldArr) {
        return key((Field<?>[]) fieldArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep1, org.jooq.MergeNotMatchedValuesStep1
    public /* bridge */ /* synthetic */ Merge values(Object obj) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj);
    }

    @Override // org.jooq.MergeKeyStep2, org.jooq.MergeKeyStep3, org.jooq.MergeKeyStep4, org.jooq.MergeKeyStep5, org.jooq.MergeKeyStep6, org.jooq.MergeKeyStep7, org.jooq.MergeKeyStep8, org.jooq.MergeKeyStep9, org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep2 key(Collection collection) {
        return key((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeKeyStep2, org.jooq.MergeKeyStep3, org.jooq.MergeKeyStep4, org.jooq.MergeKeyStep5, org.jooq.MergeKeyStep6, org.jooq.MergeKeyStep7, org.jooq.MergeKeyStep8, org.jooq.MergeKeyStep9, org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep2 key(Field[] fieldArr) {
        return key((Field<?>[]) fieldArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep2, org.jooq.MergeNotMatchedValuesStep2
    public /* bridge */ /* synthetic */ Merge values(Object obj, Object obj2) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2);
    }

    @Override // org.jooq.MergeKeyStep3, org.jooq.MergeKeyStep4, org.jooq.MergeKeyStep5, org.jooq.MergeKeyStep6, org.jooq.MergeKeyStep7, org.jooq.MergeKeyStep8, org.jooq.MergeKeyStep9, org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep3 key(Collection collection) {
        return key((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeKeyStep3, org.jooq.MergeKeyStep4, org.jooq.MergeKeyStep5, org.jooq.MergeKeyStep6, org.jooq.MergeKeyStep7, org.jooq.MergeKeyStep8, org.jooq.MergeKeyStep9, org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep3 key(Field[] fieldArr) {
        return key((Field<?>[]) fieldArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep3, org.jooq.MergeNotMatchedValuesStep3
    public /* bridge */ /* synthetic */ Merge values(Object obj, Object obj2, Object obj3) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3);
    }

    @Override // org.jooq.MergeKeyStep4, org.jooq.MergeKeyStep5, org.jooq.MergeKeyStep6, org.jooq.MergeKeyStep7, org.jooq.MergeKeyStep8, org.jooq.MergeKeyStep9, org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep4 key(Collection collection) {
        return key((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeKeyStep4, org.jooq.MergeKeyStep5, org.jooq.MergeKeyStep6, org.jooq.MergeKeyStep7, org.jooq.MergeKeyStep8, org.jooq.MergeKeyStep9, org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep4 key(Field[] fieldArr) {
        return key((Field<?>[]) fieldArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep4, org.jooq.MergeNotMatchedValuesStep4
    public /* bridge */ /* synthetic */ Merge values(Object obj, Object obj2, Object obj3, Object obj4) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4);
    }

    @Override // org.jooq.MergeKeyStep5, org.jooq.MergeKeyStep6, org.jooq.MergeKeyStep7, org.jooq.MergeKeyStep8, org.jooq.MergeKeyStep9, org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep5 key(Collection collection) {
        return key((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeKeyStep5, org.jooq.MergeKeyStep6, org.jooq.MergeKeyStep7, org.jooq.MergeKeyStep8, org.jooq.MergeKeyStep9, org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep5 key(Field[] fieldArr) {
        return key((Field<?>[]) fieldArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep5, org.jooq.MergeNotMatchedValuesStep5
    public /* bridge */ /* synthetic */ Merge values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5);
    }

    @Override // org.jooq.MergeKeyStep6, org.jooq.MergeKeyStep7, org.jooq.MergeKeyStep8, org.jooq.MergeKeyStep9, org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep6 key(Collection collection) {
        return key((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeKeyStep6, org.jooq.MergeKeyStep7, org.jooq.MergeKeyStep8, org.jooq.MergeKeyStep9, org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep6 key(Field[] fieldArr) {
        return key((Field<?>[]) fieldArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep6, org.jooq.MergeNotMatchedValuesStep6
    public /* bridge */ /* synthetic */ Merge values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6);
    }

    @Override // org.jooq.MergeKeyStep7, org.jooq.MergeKeyStep8, org.jooq.MergeKeyStep9, org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep7 key(Collection collection) {
        return key((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeKeyStep7, org.jooq.MergeKeyStep8, org.jooq.MergeKeyStep9, org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep7 key(Field[] fieldArr) {
        return key((Field<?>[]) fieldArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep7, org.jooq.MergeNotMatchedValuesStep7
    public /* bridge */ /* synthetic */ Merge values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7);
    }

    @Override // org.jooq.MergeKeyStep8, org.jooq.MergeKeyStep9, org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep8 key(Collection collection) {
        return key((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeKeyStep8, org.jooq.MergeKeyStep9, org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep8 key(Field[] fieldArr) {
        return key((Field<?>[]) fieldArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep8, org.jooq.MergeNotMatchedValuesStep8
    public /* bridge */ /* synthetic */ Merge values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8);
    }

    @Override // org.jooq.MergeKeyStep9, org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep9 key(Collection collection) {
        return key((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeKeyStep9, org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep9 key(Field[] fieldArr) {
        return key((Field<?>[]) fieldArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep9, org.jooq.MergeNotMatchedValuesStep9
    public /* bridge */ /* synthetic */ Merge values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9);
    }

    @Override // org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep10 key(Collection collection) {
        return key((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep10 key(Field[] fieldArr) {
        return key((Field<?>[]) fieldArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep10, org.jooq.MergeNotMatchedValuesStep10
    public /* bridge */ /* synthetic */ Merge values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10);
    }

    @Override // org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep11 key(Collection collection) {
        return key((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep11 key(Field[] fieldArr) {
        return key((Field<?>[]) fieldArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep11, org.jooq.MergeNotMatchedValuesStep11
    public /* bridge */ /* synthetic */ Merge values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11);
    }

    @Override // org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep12 key(Collection collection) {
        return key((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep12 key(Field[] fieldArr) {
        return key((Field<?>[]) fieldArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep12, org.jooq.MergeNotMatchedValuesStep12
    public /* bridge */ /* synthetic */ Merge values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12);
    }

    @Override // org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep13 key(Collection collection) {
        return key((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep13 key(Field[] fieldArr) {
        return key((Field<?>[]) fieldArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep13, org.jooq.MergeNotMatchedValuesStep13
    public /* bridge */ /* synthetic */ Merge values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13);
    }

    @Override // org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep14 key(Collection collection) {
        return key((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep14 key(Field[] fieldArr) {
        return key((Field<?>[]) fieldArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep14, org.jooq.MergeNotMatchedValuesStep14
    public /* bridge */ /* synthetic */ Merge values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14);
    }

    @Override // org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep15 key(Collection collection) {
        return key((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep15 key(Field[] fieldArr) {
        return key((Field<?>[]) fieldArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep15, org.jooq.MergeNotMatchedValuesStep15
    public /* bridge */ /* synthetic */ Merge values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15);
    }

    @Override // org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep16 key(Collection collection) {
        return key((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep16 key(Field[] fieldArr) {
        return key((Field<?>[]) fieldArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep16, org.jooq.MergeNotMatchedValuesStep16
    public /* bridge */ /* synthetic */ Merge values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15, Object obj16) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15, obj16);
    }

    @Override // org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep17 key(Collection collection) {
        return key((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep17 key(Field[] fieldArr) {
        return key((Field<?>[]) fieldArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep17, org.jooq.MergeNotMatchedValuesStep17
    public /* bridge */ /* synthetic */ Merge values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15, Object obj16, Object obj17) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15, obj16, obj17);
    }

    @Override // org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep18 key(Collection collection) {
        return key((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep18 key(Field[] fieldArr) {
        return key((Field<?>[]) fieldArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep18, org.jooq.MergeNotMatchedValuesStep18
    public /* bridge */ /* synthetic */ Merge values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15, Object obj16, Object obj17, Object obj18) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15, obj16, obj17, obj18);
    }

    @Override // org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep19 key(Collection collection) {
        return key((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep19 key(Field[] fieldArr) {
        return key((Field<?>[]) fieldArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep19, org.jooq.MergeNotMatchedValuesStep19
    public /* bridge */ /* synthetic */ Merge values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15, Object obj16, Object obj17, Object obj18, Object obj19) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15, obj16, obj17, obj18, obj19);
    }

    @Override // org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep20 key(Collection collection) {
        return key((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep20 key(Field[] fieldArr) {
        return key((Field<?>[]) fieldArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep20, org.jooq.MergeNotMatchedValuesStep20
    public /* bridge */ /* synthetic */ Merge values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15, Object obj16, Object obj17, Object obj18, Object obj19, Object obj20) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15, obj16, obj17, obj18, obj19, obj20);
    }

    @Override // org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep21 key(Collection collection) {
        return key((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep21 key(Field[] fieldArr) {
        return key((Field<?>[]) fieldArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep21, org.jooq.MergeNotMatchedValuesStep21
    public /* bridge */ /* synthetic */ Merge values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15, Object obj16, Object obj17, Object obj18, Object obj19, Object obj20, Object obj21) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15, obj16, obj17, obj18, obj19, obj20, obj21);
    }

    @Override // org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep22 key(Collection collection) {
        return key((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeKeyStep22
    public /* bridge */ /* synthetic */ MergeValuesStep22 key(Field[] fieldArr) {
        return key((Field<?>[]) fieldArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep22, org.jooq.MergeNotMatchedValuesStep22
    public /* bridge */ /* synthetic */ Merge values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15, Object obj16, Object obj17, Object obj18, Object obj19, Object obj20, Object obj21, Object obj22) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15, obj16, obj17, obj18, obj19, obj20, obj21, obj22);
    }

    @Override // org.jooq.MergeOnConditionStep
    public /* bridge */ /* synthetic */ MergeOnConditionStep orNotExists(Select select) {
        return orNotExists((Select<?>) select);
    }

    @Override // org.jooq.MergeOnConditionStep
    public /* bridge */ /* synthetic */ MergeOnConditionStep orExists(Select select) {
        return orExists((Select<?>) select);
    }

    @Override // org.jooq.MergeOnConditionStep
    public /* bridge */ /* synthetic */ MergeOnConditionStep orNot(Field field) {
        return orNot((Field<Boolean>) field);
    }

    @Override // org.jooq.MergeOnConditionStep
    public /* bridge */ /* synthetic */ MergeOnConditionStep or(Field field) {
        return or((Field<Boolean>) field);
    }

    @Override // org.jooq.MergeOnConditionStep
    public /* bridge */ /* synthetic */ MergeOnConditionStep andNotExists(Select select) {
        return andNotExists((Select<?>) select);
    }

    @Override // org.jooq.MergeOnConditionStep
    public /* bridge */ /* synthetic */ MergeOnConditionStep andExists(Select select) {
        return andExists((Select<?>) select);
    }

    @Override // org.jooq.MergeOnConditionStep
    public /* bridge */ /* synthetic */ MergeOnConditionStep andNot(Field field) {
        return andNot((Field<Boolean>) field);
    }

    @Override // org.jooq.MergeOnConditionStep
    public /* bridge */ /* synthetic */ MergeOnConditionStep and(Field field) {
        return and((Field<Boolean>) field);
    }

    @Override // org.jooq.MergeMatchedStep
    public /* bridge */ /* synthetic */ MergeMatchedThenStep whenMatchedAnd(Field field) {
        return whenMatchedAnd((Field<Boolean>) field);
    }

    @Override // org.jooq.MergeNotMatchedStep
    public /* bridge */ /* synthetic */ MergeNotMatchedValuesStepN whenNotMatchedThenInsert(Collection collection) {
        return whenNotMatchedThenInsert((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.MergeNotMatchedStep
    public /* bridge */ /* synthetic */ MergeNotMatchedValuesStepN whenNotMatchedThenInsert(Field[] fieldArr) {
        return whenNotMatchedThenInsert((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.MergeMatchedSetStep, org.jooq.MergeNotMatchedSetStep
    public /* bridge */ /* synthetic */ MergeMatchedSetMoreStep set(Map map) {
        return set((Map<?, ?>) map);
    }

    @Override // org.jooq.MergeMatchedSetStep, org.jooq.MergeNotMatchedSetStep
    public /* bridge */ /* synthetic */ MergeMatchedSetMoreStep set(Field field, Object obj) {
        return set((Field<Field>) field, (Field) obj);
    }

    @Override // org.jooq.MergeMatchedDeleteStep
    public /* bridge */ /* synthetic */ MergeMatchedStep deleteWhere(Field field) {
        return deleteWhere((Field<Boolean>) field);
    }

    @Override // org.jooq.MergeNotMatchedSetStep
    public /* bridge */ /* synthetic */ MergeNotMatchedSetMoreStep set(Map map) {
        return set((Map<?, ?>) map);
    }

    @Override // org.jooq.MergeNotMatchedSetStep
    public /* bridge */ /* synthetic */ MergeNotMatchedSetMoreStep set(Field field, Object obj) {
        return set((Field<Field>) field, (Field) obj);
    }

    @Override // org.jooq.MergeNotMatchedWhereStep
    public /* bridge */ /* synthetic */ MergeFinalStep where(Field field) {
        return where((Field<Boolean>) field);
    }

    @Override // org.jooq.MergeValuesStepN, org.jooq.MergeValuesStep1, org.jooq.MergeValuesStep2, org.jooq.MergeValuesStep3, org.jooq.MergeValuesStep4, org.jooq.MergeValuesStep5, org.jooq.MergeValuesStep6, org.jooq.MergeValuesStep7, org.jooq.MergeValuesStep8, org.jooq.MergeValuesStep9, org.jooq.MergeValuesStep10, org.jooq.MergeValuesStep11, org.jooq.MergeValuesStep12, org.jooq.MergeValuesStep13, org.jooq.MergeValuesStep14, org.jooq.MergeValuesStep15, org.jooq.MergeValuesStep16, org.jooq.MergeValuesStep17, org.jooq.MergeValuesStep18, org.jooq.MergeValuesStep19, org.jooq.MergeValuesStep20, org.jooq.MergeValuesStep21, org.jooq.MergeValuesStep22, org.jooq.MergeNotMatchedValuesStep1, org.jooq.MergeNotMatchedValuesStep2, org.jooq.MergeNotMatchedValuesStep3, org.jooq.MergeNotMatchedValuesStep4, org.jooq.MergeNotMatchedValuesStep5, org.jooq.MergeNotMatchedValuesStep6, org.jooq.MergeNotMatchedValuesStep7, org.jooq.MergeNotMatchedValuesStep8, org.jooq.MergeNotMatchedValuesStep9, org.jooq.MergeNotMatchedValuesStep10, org.jooq.MergeNotMatchedValuesStep11, org.jooq.MergeNotMatchedValuesStep12, org.jooq.MergeNotMatchedValuesStep13, org.jooq.MergeNotMatchedValuesStep14, org.jooq.MergeNotMatchedValuesStep15, org.jooq.MergeNotMatchedValuesStep16, org.jooq.MergeNotMatchedValuesStep17, org.jooq.MergeNotMatchedValuesStep18, org.jooq.MergeNotMatchedValuesStep19, org.jooq.MergeNotMatchedValuesStep20, org.jooq.MergeNotMatchedValuesStep21, org.jooq.MergeNotMatchedValuesStep22, org.jooq.MergeNotMatchedValuesStepN
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Collection collection) {
        return values((Collection<?>) collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep1, org.jooq.MergeNotMatchedValuesStep1
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Object obj) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep2, org.jooq.MergeNotMatchedValuesStep2
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Object obj, Object obj2) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep3, org.jooq.MergeNotMatchedValuesStep3
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Object obj, Object obj2, Object obj3) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep4, org.jooq.MergeNotMatchedValuesStep4
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Object obj, Object obj2, Object obj3, Object obj4) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep5, org.jooq.MergeNotMatchedValuesStep5
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep6, org.jooq.MergeNotMatchedValuesStep6
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep7, org.jooq.MergeNotMatchedValuesStep7
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep8, org.jooq.MergeNotMatchedValuesStep8
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep9, org.jooq.MergeNotMatchedValuesStep9
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep10, org.jooq.MergeNotMatchedValuesStep10
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep11, org.jooq.MergeNotMatchedValuesStep11
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep12, org.jooq.MergeNotMatchedValuesStep12
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep13, org.jooq.MergeNotMatchedValuesStep13
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep14, org.jooq.MergeNotMatchedValuesStep14
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep15, org.jooq.MergeNotMatchedValuesStep15
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep16, org.jooq.MergeNotMatchedValuesStep16
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15, Object obj16) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15, obj16);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep17, org.jooq.MergeNotMatchedValuesStep17
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15, Object obj16, Object obj17) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15, obj16, obj17);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep18, org.jooq.MergeNotMatchedValuesStep18
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15, Object obj16, Object obj17, Object obj18) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15, obj16, obj17, obj18);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep19, org.jooq.MergeNotMatchedValuesStep19
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15, Object obj16, Object obj17, Object obj18, Object obj19) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15, obj16, obj17, obj18, obj19);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep20, org.jooq.MergeNotMatchedValuesStep20
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15, Object obj16, Object obj17, Object obj18, Object obj19, Object obj20) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15, obj16, obj17, obj18, obj19, obj20);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep21, org.jooq.MergeNotMatchedValuesStep21
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15, Object obj16, Object obj17, Object obj18, Object obj19, Object obj20, Object obj21) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15, obj16, obj17, obj18, obj19, obj20, obj21);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep22, org.jooq.MergeNotMatchedValuesStep22
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15, Object obj16, Object obj17, Object obj18, Object obj19, Object obj20, Object obj21, Object obj22) {
        return values((MergeImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15, obj16, obj17, obj18, obj19, obj20, obj21, obj22);
    }

    @Override // org.jooq.MergeValuesStepN, org.jooq.MergeNotMatchedValuesStepN
    public /* bridge */ /* synthetic */ MergeNotMatchedWhereStep values(Field[] fieldArr) {
        return values((Field<?>[]) fieldArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MergeImpl(Configuration configuration, WithImpl with, Table<R> table) {
        this(configuration, with, table, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MergeImpl(Configuration configuration, WithImpl with, Table<R> table, Collection<? extends Field<?>> fields) {
        super(configuration);
        this.with = with;
        this.table = table;
        this.on = new ConditionProviderImpl();
        this.matched = new ArrayList();
        this.notMatched = new ArrayList();
        if (fields != null) {
            columns(fields);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Table<R> table() {
        return this.table;
    }

    final QueryPartList<Field<?>> getUpsertFields() {
        if (this.upsertFields == null) {
            this.upsertFields = new QueryPartList<>(this.table.fields());
        }
        return this.upsertFields;
    }

    final QueryPartList<Field<?>> getUpsertKeys() {
        if (this.upsertKeys == null) {
            this.upsertKeys = new QueryPartList<>();
        }
        return this.upsertKeys;
    }

    final QueryPartList<Field<?>> getUpsertValues() {
        if (this.upsertValues == null) {
            this.upsertValues = new QueryPartList<>();
        }
        return this.upsertValues;
    }

    final MatchedClause getLastMatched() {
        return this.matched.get(this.matched.size() - 1);
    }

    final NotMatchedClause getLastNotMatched() {
        return this.notMatched.get(this.notMatched.size() - 1);
    }

    @Override // org.jooq.MergeUsingStep
    public final MergeImpl columns(Field<?>... fields) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(fields));
    }

    @Override // org.jooq.MergeUsingStep
    public final MergeImpl columns(Collection<? extends Field<?>> fields) {
        this.upsertStyle = true;
        this.upsertFields = new QueryPartList<>(fields);
        return this;
    }

    @Override // org.jooq.MergeUsingStep
    public final <T1> MergeImpl columns(Field<T1> field1) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(field1));
    }

    @Override // org.jooq.MergeUsingStep
    public final <T1, T2> MergeImpl columns(Field<T1> field1, Field<T2> field2) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(field1, field2));
    }

    @Override // org.jooq.MergeUsingStep
    public final <T1, T2, T3> MergeImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3));
    }

    @Override // org.jooq.MergeUsingStep
    public final <T1, T2, T3, T4> MergeImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4));
    }

    @Override // org.jooq.MergeUsingStep
    public final <T1, T2, T3, T4, T5> MergeImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5));
    }

    @Override // org.jooq.MergeUsingStep
    public final <T1, T2, T3, T4, T5, T6> MergeImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6));
    }

    @Override // org.jooq.MergeUsingStep
    public final <T1, T2, T3, T4, T5, T6, T7> MergeImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7));
    }

    @Override // org.jooq.MergeUsingStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8> MergeImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8));
    }

    @Override // org.jooq.MergeUsingStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9> MergeImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9));
    }

    @Override // org.jooq.MergeUsingStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> MergeImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10));
    }

    @Override // org.jooq.MergeUsingStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> MergeImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11));
    }

    @Override // org.jooq.MergeUsingStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> MergeImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12));
    }

    @Override // org.jooq.MergeUsingStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> MergeImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13));
    }

    @Override // org.jooq.MergeUsingStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> MergeImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14));
    }

    @Override // org.jooq.MergeUsingStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> MergeImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15));
    }

    @Override // org.jooq.MergeUsingStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> MergeImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16));
    }

    @Override // org.jooq.MergeUsingStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> MergeImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17));
    }

    @Override // org.jooq.MergeUsingStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> MergeImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18));
    }

    @Override // org.jooq.MergeUsingStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> MergeImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19));
    }

    @Override // org.jooq.MergeUsingStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> MergeImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20));
    }

    @Override // org.jooq.MergeUsingStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> MergeImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21));
    }

    @Override // org.jooq.MergeUsingStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> MergeImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22));
    }

    @Override // org.jooq.MergeValuesStepN, org.jooq.MergeValuesStep1, org.jooq.MergeValuesStep2, org.jooq.MergeValuesStep3, org.jooq.MergeValuesStep4, org.jooq.MergeValuesStep5, org.jooq.MergeValuesStep6, org.jooq.MergeValuesStep7, org.jooq.MergeValuesStep8, org.jooq.MergeValuesStep9, org.jooq.MergeValuesStep10, org.jooq.MergeValuesStep11, org.jooq.MergeValuesStep12, org.jooq.MergeValuesStep13, org.jooq.MergeValuesStep14, org.jooq.MergeValuesStep15, org.jooq.MergeValuesStep16, org.jooq.MergeValuesStep17, org.jooq.MergeValuesStep18, org.jooq.MergeValuesStep19, org.jooq.MergeValuesStep20, org.jooq.MergeValuesStep21, org.jooq.MergeValuesStep22
    public final MergeImpl select(Select select) {
        this.upsertStyle = true;
        this.upsertSelect = select;
        return this;
    }

    @Override // org.jooq.MergeKeyStepN, org.jooq.MergeKeyStep1, org.jooq.MergeKeyStep2, org.jooq.MergeKeyStep3, org.jooq.MergeKeyStep4, org.jooq.MergeKeyStep5, org.jooq.MergeKeyStep6, org.jooq.MergeKeyStep7, org.jooq.MergeKeyStep8, org.jooq.MergeKeyStep9, org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public final MergeImpl key(Field<?>... k) {
        return key((Collection<? extends Field<?>>) Arrays.asList(k));
    }

    @Override // org.jooq.MergeKeyStepN, org.jooq.MergeKeyStep1, org.jooq.MergeKeyStep2, org.jooq.MergeKeyStep3, org.jooq.MergeKeyStep4, org.jooq.MergeKeyStep5, org.jooq.MergeKeyStep6, org.jooq.MergeKeyStep7, org.jooq.MergeKeyStep8, org.jooq.MergeKeyStep9, org.jooq.MergeKeyStep10, org.jooq.MergeKeyStep11, org.jooq.MergeKeyStep12, org.jooq.MergeKeyStep13, org.jooq.MergeKeyStep14, org.jooq.MergeKeyStep15, org.jooq.MergeKeyStep16, org.jooq.MergeKeyStep17, org.jooq.MergeKeyStep18, org.jooq.MergeKeyStep19, org.jooq.MergeKeyStep20, org.jooq.MergeKeyStep21, org.jooq.MergeKeyStep22
    public final MergeImpl key(Collection<? extends Field<?>> keys) {
        this.upsertStyle = true;
        getUpsertKeys().addAll(keys);
        return this;
    }

    @Override // org.jooq.MergeValuesStep1, org.jooq.MergeNotMatchedValuesStep1
    public final MergeImpl values(T1 value1) {
        return values(value1);
    }

    @Override // org.jooq.MergeValuesStep2, org.jooq.MergeNotMatchedValuesStep2
    public final MergeImpl values(T1 value1, T2 value2) {
        return values(value1, value2);
    }

    @Override // org.jooq.MergeValuesStep3, org.jooq.MergeNotMatchedValuesStep3
    public final MergeImpl values(T1 value1, T2 value2, T3 value3) {
        return values(value1, value2, value3);
    }

    @Override // org.jooq.MergeValuesStep4, org.jooq.MergeNotMatchedValuesStep4
    public final MergeImpl values(T1 value1, T2 value2, T3 value3, T4 value4) {
        return values(value1, value2, value3, value4);
    }

    @Override // org.jooq.MergeValuesStep5, org.jooq.MergeNotMatchedValuesStep5
    public final MergeImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5) {
        return values(value1, value2, value3, value4, value5);
    }

    @Override // org.jooq.MergeValuesStep6, org.jooq.MergeNotMatchedValuesStep6
    public final MergeImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6) {
        return values(value1, value2, value3, value4, value5, value6);
    }

    @Override // org.jooq.MergeValuesStep7, org.jooq.MergeNotMatchedValuesStep7
    public final MergeImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7) {
        return values(value1, value2, value3, value4, value5, value6, value7);
    }

    @Override // org.jooq.MergeValuesStep8, org.jooq.MergeNotMatchedValuesStep8
    public final MergeImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8);
    }

    @Override // org.jooq.MergeValuesStep9, org.jooq.MergeNotMatchedValuesStep9
    public final MergeImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9);
    }

    @Override // org.jooq.MergeValuesStep10, org.jooq.MergeNotMatchedValuesStep10
    public final MergeImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10);
    }

    @Override // org.jooq.MergeValuesStep11, org.jooq.MergeNotMatchedValuesStep11
    public final MergeImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11);
    }

    @Override // org.jooq.MergeValuesStep12, org.jooq.MergeNotMatchedValuesStep12
    public final MergeImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11, T12 value12) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12);
    }

    @Override // org.jooq.MergeValuesStep13, org.jooq.MergeNotMatchedValuesStep13
    public final MergeImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11, T12 value12, T13 value13) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13);
    }

    @Override // org.jooq.MergeValuesStep14, org.jooq.MergeNotMatchedValuesStep14
    public final MergeImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11, T12 value12, T13 value13, T14 value14) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14);
    }

    @Override // org.jooq.MergeValuesStep15, org.jooq.MergeNotMatchedValuesStep15
    public final MergeImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11, T12 value12, T13 value13, T14 value14, T15 value15) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15);
    }

    @Override // org.jooq.MergeValuesStep16, org.jooq.MergeNotMatchedValuesStep16
    public final MergeImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11, T12 value12, T13 value13, T14 value14, T15 value15, T16 value16) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16);
    }

    @Override // org.jooq.MergeValuesStep17, org.jooq.MergeNotMatchedValuesStep17
    public final MergeImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11, T12 value12, T13 value13, T14 value14, T15 value15, T16 value16, T17 value17) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17);
    }

    @Override // org.jooq.MergeValuesStep18, org.jooq.MergeNotMatchedValuesStep18
    public final MergeImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11, T12 value12, T13 value13, T14 value14, T15 value15, T16 value16, T17 value17, T18 value18) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18);
    }

    @Override // org.jooq.MergeValuesStep19, org.jooq.MergeNotMatchedValuesStep19
    public final MergeImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11, T12 value12, T13 value13, T14 value14, T15 value15, T16 value16, T17 value17, T18 value18, T19 value19) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19);
    }

    @Override // org.jooq.MergeValuesStep20, org.jooq.MergeNotMatchedValuesStep20
    public final MergeImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11, T12 value12, T13 value13, T14 value14, T15 value15, T16 value16, T17 value17, T18 value18, T19 value19, T20 value20) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19, value20);
    }

    @Override // org.jooq.MergeValuesStep21, org.jooq.MergeNotMatchedValuesStep21
    public final MergeImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11, T12 value12, T13 value13, T14 value14, T15 value15, T16 value16, T17 value17, T18 value18, T19 value19, T20 value20, T21 value21) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19, value20, value21);
    }

    @Override // org.jooq.MergeValuesStep22, org.jooq.MergeNotMatchedValuesStep22
    public final MergeImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11, T12 value12, T13 value13, T14 value14, T15 value15, T16 value16, T17 value17, T18 value18, T19 value19, T20 value20, T21 value21, T22 value22) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19, value20, value21, value22);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep1, org.jooq.MergeNotMatchedValuesStep1
    public final MergeImpl values(Field<T1> value1) {
        return values((Field<?>[]) new Field[]{value1});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep2, org.jooq.MergeNotMatchedValuesStep2
    public final MergeImpl values(Field<T1> value1, Field<T2> value2) {
        return values((Field<?>[]) new Field[]{value1, value2});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep3, org.jooq.MergeNotMatchedValuesStep3
    public final MergeImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3) {
        return values((Field<?>[]) new Field[]{value1, value2, value3});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep4, org.jooq.MergeNotMatchedValuesStep4
    public final MergeImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep5, org.jooq.MergeNotMatchedValuesStep5
    public final MergeImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep6, org.jooq.MergeNotMatchedValuesStep6
    public final MergeImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep7, org.jooq.MergeNotMatchedValuesStep7
    public final MergeImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep8, org.jooq.MergeNotMatchedValuesStep8
    public final MergeImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep9, org.jooq.MergeNotMatchedValuesStep9
    public final MergeImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep10, org.jooq.MergeNotMatchedValuesStep10
    public final MergeImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep11, org.jooq.MergeNotMatchedValuesStep11
    public final MergeImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep12, org.jooq.MergeNotMatchedValuesStep12
    public final MergeImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11, Field<T12> value12) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep13, org.jooq.MergeNotMatchedValuesStep13
    public final MergeImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11, Field<T12> value12, Field<T13> value13) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep14, org.jooq.MergeNotMatchedValuesStep14
    public final MergeImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11, Field<T12> value12, Field<T13> value13, Field<T14> value14) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep15, org.jooq.MergeNotMatchedValuesStep15
    public final MergeImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11, Field<T12> value12, Field<T13> value13, Field<T14> value14, Field<T15> value15) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep16, org.jooq.MergeNotMatchedValuesStep16
    public final MergeImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11, Field<T12> value12, Field<T13> value13, Field<T14> value14, Field<T15> value15, Field<T16> value16) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep17, org.jooq.MergeNotMatchedValuesStep17
    public final MergeImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11, Field<T12> value12, Field<T13> value13, Field<T14> value14, Field<T15> value15, Field<T16> value16, Field<T17> value17) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep18, org.jooq.MergeNotMatchedValuesStep18
    public final MergeImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11, Field<T12> value12, Field<T13> value13, Field<T14> value14, Field<T15> value15, Field<T16> value16, Field<T17> value17, Field<T18> value18) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep19, org.jooq.MergeNotMatchedValuesStep19
    public final MergeImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11, Field<T12> value12, Field<T13> value13, Field<T14> value14, Field<T15> value15, Field<T16> value16, Field<T17> value17, Field<T18> value18, Field<T19> value19) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep20, org.jooq.MergeNotMatchedValuesStep20
    public final MergeImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11, Field<T12> value12, Field<T13> value13, Field<T14> value14, Field<T15> value15, Field<T16> value16, Field<T17> value17, Field<T18> value18, Field<T19> value19, Field<T20> value20) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19, value20});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep21, org.jooq.MergeNotMatchedValuesStep21
    public final MergeImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11, Field<T12> value12, Field<T13> value13, Field<T14> value14, Field<T15> value15, Field<T16> value16, Field<T17> value17, Field<T18> value18, Field<T19> value19, Field<T20> value20, Field<T21> value21) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19, value20, value21});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeValuesStep22, org.jooq.MergeNotMatchedValuesStep22
    public final MergeImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11, Field<T12> value12, Field<T13> value13, Field<T14> value14, Field<T15> value15, Field<T16> value16, Field<T17> value17, Field<T18> value18, Field<T19> value19, Field<T20> value20, Field<T21> value21, Field<T22> value22) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19, value20, value21, value22});
    }

    @Override // org.jooq.MergeValuesStepN, org.jooq.MergeNotMatchedValuesStepN
    public final MergeImpl values(Object... values) {
        if (this.using == null && !this.usingDual) {
            this.upsertStyle = true;
            getUpsertValues().addAll(Tools.fields(values, (Field<?>[]) getUpsertFields().toArray(Tools.EMPTY_FIELD)));
        } else {
            getLastNotMatched().insertMap.set(Tools.fields(values, (Field<?>[]) getLastNotMatched().insertMap.values.keySet().toArray(Tools.EMPTY_FIELD)));
        }
        return this;
    }

    @Override // org.jooq.MergeValuesStepN, org.jooq.MergeNotMatchedValuesStepN
    public final MergeImpl values(Field<?>... values) {
        return values((Object[]) values);
    }

    @Override // org.jooq.MergeValuesStepN, org.jooq.MergeValuesStep1, org.jooq.MergeValuesStep2, org.jooq.MergeValuesStep3, org.jooq.MergeValuesStep4, org.jooq.MergeValuesStep5, org.jooq.MergeValuesStep6, org.jooq.MergeValuesStep7, org.jooq.MergeValuesStep8, org.jooq.MergeValuesStep9, org.jooq.MergeValuesStep10, org.jooq.MergeValuesStep11, org.jooq.MergeValuesStep12, org.jooq.MergeValuesStep13, org.jooq.MergeValuesStep14, org.jooq.MergeValuesStep15, org.jooq.MergeValuesStep16, org.jooq.MergeValuesStep17, org.jooq.MergeValuesStep18, org.jooq.MergeValuesStep19, org.jooq.MergeValuesStep20, org.jooq.MergeValuesStep21, org.jooq.MergeValuesStep22, org.jooq.MergeNotMatchedValuesStep1, org.jooq.MergeNotMatchedValuesStep2, org.jooq.MergeNotMatchedValuesStep3, org.jooq.MergeNotMatchedValuesStep4, org.jooq.MergeNotMatchedValuesStep5, org.jooq.MergeNotMatchedValuesStep6, org.jooq.MergeNotMatchedValuesStep7, org.jooq.MergeNotMatchedValuesStep8, org.jooq.MergeNotMatchedValuesStep9, org.jooq.MergeNotMatchedValuesStep10, org.jooq.MergeNotMatchedValuesStep11, org.jooq.MergeNotMatchedValuesStep12, org.jooq.MergeNotMatchedValuesStep13, org.jooq.MergeNotMatchedValuesStep14, org.jooq.MergeNotMatchedValuesStep15, org.jooq.MergeNotMatchedValuesStep16, org.jooq.MergeNotMatchedValuesStep17, org.jooq.MergeNotMatchedValuesStep18, org.jooq.MergeNotMatchedValuesStep19, org.jooq.MergeNotMatchedValuesStep20, org.jooq.MergeNotMatchedValuesStep21, org.jooq.MergeNotMatchedValuesStep22, org.jooq.MergeNotMatchedValuesStepN
    public final MergeImpl values(Collection<?> values) {
        return values(values.toArray());
    }

    @Override // org.jooq.MergeUsingStep
    public final MergeImpl using(TableLike<?> u) {
        this.using = u;
        return this;
    }

    @Override // org.jooq.MergeUsingStep
    public final MergeImpl usingDual() {
        this.usingDual = true;
        return this;
    }

    @Override // org.jooq.MergeOnStep
    public final MergeImpl on(Condition conditions) {
        this.on.addConditions(conditions);
        return this;
    }

    @Override // org.jooq.MergeOnStep
    public final MergeImpl on(Condition... conditions) {
        this.on.addConditions(conditions);
        return this;
    }

    @Override // org.jooq.MergeOnStep
    public final MergeOnConditionStep<R> on(Field<Boolean> condition) {
        return on(DSL.condition(condition));
    }

    @Override // org.jooq.MergeOnStep
    public final MergeImpl on(SQL sql) {
        return on(DSL.condition(sql));
    }

    @Override // org.jooq.MergeOnStep
    public final MergeImpl on(String sql) {
        return on(DSL.condition(sql));
    }

    @Override // org.jooq.MergeOnStep
    public final MergeImpl on(String sql, Object... bindings) {
        return on(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.MergeOnStep
    public final MergeImpl on(String sql, QueryPart... parts) {
        return on(DSL.condition(sql, parts));
    }

    @Override // org.jooq.MergeOnConditionStep
    public final MergeImpl and(Condition condition) {
        this.on.addConditions(condition);
        return this;
    }

    @Override // org.jooq.MergeOnConditionStep
    public final MergeImpl and(Field<Boolean> condition) {
        return and(DSL.condition(condition));
    }

    @Override // org.jooq.MergeOnConditionStep
    public final MergeImpl and(SQL sql) {
        return and(DSL.condition(sql));
    }

    @Override // org.jooq.MergeOnConditionStep
    public final MergeImpl and(String sql) {
        return and(DSL.condition(sql));
    }

    @Override // org.jooq.MergeOnConditionStep
    public final MergeImpl and(String sql, Object... bindings) {
        return and(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.MergeOnConditionStep
    public final MergeImpl and(String sql, QueryPart... parts) {
        return and(DSL.condition(sql, parts));
    }

    @Override // org.jooq.MergeOnConditionStep
    public final MergeImpl andNot(Condition condition) {
        return and(condition.not());
    }

    @Override // org.jooq.MergeOnConditionStep
    public final MergeImpl andNot(Field<Boolean> condition) {
        return andNot(DSL.condition(condition));
    }

    @Override // org.jooq.MergeOnConditionStep
    public final MergeImpl andExists(Select<?> select) {
        return and(DSL.exists(select));
    }

    @Override // org.jooq.MergeOnConditionStep
    public final MergeImpl andNotExists(Select<?> select) {
        return and(DSL.notExists(select));
    }

    @Override // org.jooq.MergeOnConditionStep
    public final MergeImpl or(Condition condition) {
        this.on.addConditions(Operator.OR, condition);
        return this;
    }

    @Override // org.jooq.MergeOnConditionStep
    public final MergeImpl or(Field<Boolean> condition) {
        return and(DSL.condition(condition));
    }

    @Override // org.jooq.MergeOnConditionStep
    public final MergeImpl or(SQL sql) {
        return or(DSL.condition(sql));
    }

    @Override // org.jooq.MergeOnConditionStep
    public final MergeImpl or(String sql) {
        return or(DSL.condition(sql));
    }

    @Override // org.jooq.MergeOnConditionStep
    public final MergeImpl or(String sql, Object... bindings) {
        return or(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.MergeOnConditionStep
    public final MergeImpl or(String sql, QueryPart... parts) {
        return or(DSL.condition(sql, parts));
    }

    @Override // org.jooq.MergeOnConditionStep
    public final MergeImpl orNot(Condition condition) {
        return or(condition.not());
    }

    @Override // org.jooq.MergeOnConditionStep
    public final MergeImpl orNot(Field<Boolean> condition) {
        return orNot(DSL.condition(condition));
    }

    @Override // org.jooq.MergeOnConditionStep
    public final MergeImpl orExists(Select<?> select) {
        return or(DSL.exists(select));
    }

    @Override // org.jooq.MergeOnConditionStep
    public final MergeImpl orNotExists(Select<?> select) {
        return or(DSL.notExists(select));
    }

    @Override // org.jooq.MergeMatchedStep
    public final MergeImpl whenMatchedThenUpdate() {
        return whenMatchedAnd(DSL.noCondition()).thenUpdate();
    }

    @Override // org.jooq.MergeMatchedStep
    public final MergeImpl whenMatchedThenDelete() {
        return whenMatchedAnd(DSL.noCondition()).thenDelete();
    }

    @Override // org.jooq.MergeMatchedStep
    public final MergeImpl whenMatchedAnd(Condition condition) {
        this.matchedClause = true;
        this.matched.add(new MatchedClause(this.table, condition));
        this.notMatchedClause = false;
        return this;
    }

    @Override // org.jooq.MergeMatchedStep
    public final MergeImpl whenMatchedAnd(Field<Boolean> condition) {
        return whenMatchedAnd(DSL.condition(condition));
    }

    @Override // org.jooq.MergeMatchedStep
    public final MergeImpl whenMatchedAnd(SQL sql) {
        return whenMatchedAnd(DSL.condition(sql));
    }

    @Override // org.jooq.MergeMatchedStep
    public final MergeImpl whenMatchedAnd(String sql) {
        return whenMatchedAnd(DSL.condition(sql));
    }

    @Override // org.jooq.MergeMatchedStep
    public final MergeImpl whenMatchedAnd(String sql, Object... bindings) {
        return whenMatchedAnd(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.MergeMatchedStep
    public final MergeImpl whenMatchedAnd(String sql, QueryPart... parts) {
        return whenMatchedAnd(DSL.condition(sql, parts));
    }

    @Override // org.jooq.MergeMatchedThenStep
    public final MergeImpl thenUpdate() {
        return this;
    }

    @Override // org.jooq.MergeMatchedThenStep
    public final MergeImpl thenDelete() {
        getLastMatched().delete = true;
        return this;
    }

    @Override // org.jooq.MergeMatchedSetStep, org.jooq.MergeNotMatchedSetStep
    public final <T> MergeImpl set(Field<T> field, T value) {
        return set((Field) field, (Field) Tools.field(value, field));
    }

    @Override // org.jooq.MergeNotMatchedSetStep
    public final <T> MergeImpl set(Field<T> field, Field<T> value) {
        if (this.matchedClause) {
            getLastMatched().updateMap.put((FieldOrRow) field, (FieldOrRowOrSelect) Tools.nullSafe(value));
        } else if (this.notMatchedClause) {
            getLastNotMatched().insertMap.set(field, Tools.nullSafe(value));
        } else {
            throw new IllegalStateException("Cannot call where() on the current state of the MERGE statement");
        }
        return this;
    }

    @Override // org.jooq.MergeNotMatchedSetStep
    public final <T> MergeImpl setNull(Field<T> field) {
        return set((Field<Field<T>>) field, (Field<T>) null);
    }

    @Override // org.jooq.MergeNotMatchedSetStep
    public final <T> MergeImpl set(Field<T> field, Select<? extends Record1<T>> value) {
        if (value == null) {
            return set((Field<Field<T>>) field, (Field<T>) null);
        }
        return set((Field) field, (Field) value.asField());
    }

    @Override // org.jooq.MergeMatchedSetStep, org.jooq.MergeNotMatchedSetStep
    public final MergeImpl set(Map<?, ?> map) {
        if (this.matchedClause) {
            getLastMatched().updateMap.set(map);
        } else if (this.notMatchedClause) {
            getLastNotMatched().insertMap.set(map);
        } else {
            throw new IllegalStateException("Cannot call where() on the current state of the MERGE statement");
        }
        return this;
    }

    @Override // org.jooq.MergeNotMatchedSetStep
    public final MergeImpl set(Record record) {
        return set((Map<?, ?>) Tools.mapOfChangedValues(record));
    }

    @Override // org.jooq.MergeNotMatchedStep
    public final MergeImpl whenNotMatchedThenInsert() {
        return whenNotMatchedThenInsert((Collection<? extends Field<?>>) Collections.emptyList());
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeNotMatchedStep
    public final <T1> MergeImpl whenNotMatchedThenInsert(Field<T1> field1) {
        return whenNotMatchedThenInsert((Field<?>[]) new Field[]{field1});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeNotMatchedStep
    public final <T1, T2> MergeImpl whenNotMatchedThenInsert(Field<T1> field1, Field<T2> field2) {
        return whenNotMatchedThenInsert((Field<?>[]) new Field[]{field1, field2});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeNotMatchedStep
    public final <T1, T2, T3> MergeImpl whenNotMatchedThenInsert(Field<T1> field1, Field<T2> field2, Field<T3> field3) {
        return whenNotMatchedThenInsert((Field<?>[]) new Field[]{field1, field2, field3});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeNotMatchedStep
    public final <T1, T2, T3, T4> MergeImpl whenNotMatchedThenInsert(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4) {
        return whenNotMatchedThenInsert((Field<?>[]) new Field[]{field1, field2, field3, field4});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeNotMatchedStep
    public final <T1, T2, T3, T4, T5> MergeImpl whenNotMatchedThenInsert(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5) {
        return whenNotMatchedThenInsert((Field<?>[]) new Field[]{field1, field2, field3, field4, field5});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeNotMatchedStep
    public final <T1, T2, T3, T4, T5, T6> MergeImpl whenNotMatchedThenInsert(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6) {
        return whenNotMatchedThenInsert((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeNotMatchedStep
    public final <T1, T2, T3, T4, T5, T6, T7> MergeImpl whenNotMatchedThenInsert(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7) {
        return whenNotMatchedThenInsert((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeNotMatchedStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8> MergeImpl whenNotMatchedThenInsert(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8) {
        return whenNotMatchedThenInsert((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeNotMatchedStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9> MergeImpl whenNotMatchedThenInsert(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9) {
        return whenNotMatchedThenInsert((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeNotMatchedStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> MergeImpl whenNotMatchedThenInsert(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10) {
        return whenNotMatchedThenInsert((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeNotMatchedStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> MergeImpl whenNotMatchedThenInsert(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11) {
        return whenNotMatchedThenInsert((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeNotMatchedStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> MergeImpl whenNotMatchedThenInsert(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12) {
        return whenNotMatchedThenInsert((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeNotMatchedStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> MergeImpl whenNotMatchedThenInsert(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13) {
        return whenNotMatchedThenInsert((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeNotMatchedStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> MergeImpl whenNotMatchedThenInsert(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14) {
        return whenNotMatchedThenInsert((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeNotMatchedStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> MergeImpl whenNotMatchedThenInsert(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15) {
        return whenNotMatchedThenInsert((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeNotMatchedStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> MergeImpl whenNotMatchedThenInsert(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16) {
        return whenNotMatchedThenInsert((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeNotMatchedStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> MergeImpl whenNotMatchedThenInsert(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17) {
        return whenNotMatchedThenInsert((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeNotMatchedStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> MergeImpl whenNotMatchedThenInsert(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18) {
        return whenNotMatchedThenInsert((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeNotMatchedStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> MergeImpl whenNotMatchedThenInsert(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19) {
        return whenNotMatchedThenInsert((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeNotMatchedStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> MergeImpl whenNotMatchedThenInsert(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20) {
        return whenNotMatchedThenInsert((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeNotMatchedStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> MergeImpl whenNotMatchedThenInsert(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21) {
        return whenNotMatchedThenInsert((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.MergeNotMatchedStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> MergeImpl whenNotMatchedThenInsert(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22) {
        return whenNotMatchedThenInsert((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22});
    }

    @Override // org.jooq.MergeNotMatchedStep
    public final MergeImpl whenNotMatchedThenInsert(Field<?>... fields) {
        return whenNotMatchedThenInsert((Collection<? extends Field<?>>) Arrays.asList(fields));
    }

    @Override // org.jooq.MergeNotMatchedStep
    public final MergeImpl whenNotMatchedThenInsert(Collection<? extends Field<?>> fields) {
        this.notMatchedClause = true;
        this.notMatched.add(new NotMatchedClause(this.table, DSL.noCondition()));
        getLastNotMatched().insertMap.addFields(fields);
        this.matchedClause = false;
        return this;
    }

    @Override // org.jooq.MergeNotMatchedWhereStep
    public final MergeImpl where(Condition condition) {
        if (this.matchedClause) {
            getLastMatched().condition = condition;
        } else if (this.notMatchedClause) {
            getLastNotMatched().condition = condition;
        } else {
            throw new IllegalStateException("Cannot call where() on the current state of the MERGE statement");
        }
        return this;
    }

    @Override // org.jooq.MergeMatchedWhereStep, org.jooq.MergeNotMatchedWhereStep
    public final MergeMatchedDeleteStep<R> where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    @Override // org.jooq.MergeMatchedDeleteStep
    public final MergeImpl deleteWhere(Condition condition) {
        if (this.matchedClause) {
            this.matched.add(this.matched.size() - 1, new MatchedClause(this.table, condition, true));
            return this;
        }
        throw new IllegalStateException("Cannot call where() on the current state of the MERGE statement");
    }

    @Override // org.jooq.MergeMatchedDeleteStep
    public final MergeImpl deleteWhere(Field<Boolean> condition) {
        return deleteWhere(DSL.condition(condition));
    }

    private final QueryPart getStandardMerge(boolean usingSubqueries) {
        Table<?> src;
        List<Field<?>> srcFields;
        Condition and;
        Condition and2;
        if (this.upsertSelect != null) {
            Table<?> s = this.upsertSelect.asTable("s");
            src = DSL.select(Tools.map(s.fieldsRow().fields(), (f, i) -> {
                return f.as("s" + (i + 1));
            })).from(s).asTable("src");
            srcFields = Arrays.asList(src.fields());
        } else if (usingSubqueries) {
            src = DSL.select(Tools.map(getUpsertValues(), (f2, i2) -> {
                return f2.as("s" + (i2 + 1));
            })).asTable("src");
            srcFields = Arrays.asList(src.fields());
        } else {
            src = new Dual();
            srcFields = Tools.map(getUpsertValues(), f3 -> {
                return f3;
            });
        }
        Set<Field<?>> onFields = new HashSet<>();
        Condition condition = null;
        if (getUpsertKeys().isEmpty()) {
            UniqueKey<R> primaryKey = this.table.getPrimaryKey();
            if (primaryKey != null) {
                onFields.addAll(primaryKey.getFields());
                for (int i3 = 0; i3 < primaryKey.getFields().size(); i3++) {
                    Condition rhs = primaryKey.getFields().get(i3).equal(srcFields.get(i3));
                    if (condition == null) {
                        and2 = rhs;
                    } else {
                        and2 = condition.and(rhs);
                    }
                    condition = and2;
                }
            } else {
                throw new IllegalStateException("Cannot omit KEY() clause on a non-Updatable Table");
            }
        } else {
            for (int i4 = 0; i4 < getUpsertKeys().size(); i4++) {
                int matchIndex = getUpsertFields().indexOf(getUpsertKeys().get(i4));
                if (matchIndex == -1) {
                    throw new IllegalStateException("Fields in KEY() clause must be part of the fields specified in MERGE INTO table (...)");
                }
                onFields.addAll(getUpsertKeys());
                Condition rhs2 = getUpsertKeys().get(i4).equal(srcFields.get(matchIndex));
                if (condition == null) {
                    and = rhs2;
                } else {
                    and = condition.and(rhs2);
                }
                condition = and;
            }
        }
        Map<Field<?>, Field<?>> update = new LinkedHashMap<>();
        Map<Field<?>, Field<?>> insert = new LinkedHashMap<>();
        for (int i5 = 0; i5 < srcFields.size(); i5++) {
            if (!onFields.contains(getUpsertFields().get(i5))) {
                update.put(getUpsertFields().get(i5), srcFields.get(i5));
            }
            insert.put(getUpsertFields().get(i5), srcFields.get(i5));
        }
        return DSL.mergeInto(this.table).using(src).on(condition).whenMatchedThenUpdate().set(update).whenNotMatchedThenInsert().set(insert);
    }

    final MergeImpl<R, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> copy(Consumer<? super MergeImpl<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?>> consumer) {
        return (MergeImpl<R, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?>) copy(consumer, this.table);
    }

    final <O extends Record> MergeImpl<O, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> copy(Consumer<? super MergeImpl<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?>> finisher, Table<O> t) {
        MergeImpl<O, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> r = new MergeImpl<>(configuration(), this.with, t);
        r.using = this.using;
        r.usingDual = this.usingDual;
        r.on.addConditions(ConditionProviderImpl.extractCondition(this.on));
        for (MatchedClause m : this.matched) {
            MatchedClause m2 = new MatchedClause(t, m.condition, m.delete, new FieldMapForUpdate(t, m.updateMap.setClause, m.updateMap.assignmentClause));
            m2.updateMap.putAll(m.updateMap);
            r.matched.add(m2);
        }
        for (NotMatchedClause m3 : this.notMatched) {
            NotMatchedClause m22 = new NotMatchedClause(t, m3.condition);
            m22.insertMap.from(m3.insertMap);
            r.notMatched.add(m22);
        }
        finisher.accept(r);
        return r;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        Table<?> t = InlineDerivedTable.inlineDerivedTable(ctx, this.table);
        if (t instanceof InlineDerivedTable) {
            ctx.configuration().requireCommercial(() -> {
                return "InlineDerivedTable emulation for MERGE clauses is available in the commercial jOOQ editions only";
            });
        } else {
            accept0(ctx);
        }
    }

    private final void accept0(Context<?> ctx) {
        if (this.with != null) {
            ctx.visit(this.with);
        }
        if (this.upsertStyle) {
            switch (ctx.family()) {
                case H2:
                    toSQLH2Merge(ctx);
                    return;
                case MARIADB:
                case MYSQL:
                    toSQLMySQLOnDuplicateKeyUpdate(ctx);
                    return;
                case POSTGRES:
                case YUGABYTEDB:
                    toPostgresInsertOnConflict(ctx);
                    return;
                case DERBY:
                    ctx.visit(getStandardMerge(false));
                    return;
                default:
                    ctx.visit(getStandardMerge(true));
                    return;
            }
        }
        toSQLStandard(ctx);
    }

    private final void toSQLMySQLOnDuplicateKeyUpdate(Context<?> ctx) {
        FieldsImpl<?> fields = new FieldsImpl<>(getUpsertFields());
        Map<Field<?>, Field<?>> map = new LinkedHashMap<>();
        for (Field<?> field : fields.fields) {
            map.put(field, getUpsertValues().get(fields.indexOf(field)));
        }
        if (this.upsertSelect != null) {
            ctx.sql("[ merge with select is not supported in MySQL / MariaDB ]");
        } else {
            ctx.visit(DSL.insertInto(this.table, getUpsertFields()).values(getUpsertValues()).onDuplicateKeyUpdate().set(map));
        }
    }

    private final void toPostgresInsertOnConflict(Context<?> ctx) {
        if (this.upsertSelect != null) {
            ctx.visit(getStandardMerge(true));
            return;
        }
        FieldsImpl<?> fields = new FieldsImpl<>(getUpsertFields());
        Map<Field<?>, Field<?>> map = new LinkedHashMap<>();
        for (Field<?> field : fields.fields) {
            int i = fields.indexOf(field);
            if (i > -1 && i < getUpsertValues().size()) {
                map.put(field, getUpsertValues().get(i));
            }
        }
        ctx.visit(DSL.insertInto(this.table, getUpsertFields()).values(getUpsertValues()).onConflict(getUpsertKeys()).doUpdate().set(map));
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v15, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v21, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v24, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v6, types: [org.jooq.Context] */
    private final void toSQLH2Merge(Context<?> ctx) {
        ctx.visit(Keywords.K_MERGE_INTO).sql(' ').declareTables(true, c -> {
            c.visit(this.table);
        }).formatSeparator();
        ctx.sql('(').visit(QueryPartListView.wrap(Tools.collect(removeReadonly(ctx, getUpsertFields()))).qualify(false)).sql(')');
        if (!getUpsertKeys().isEmpty()) {
            ctx.formatSeparator().visit(Keywords.K_KEY).sql(" (").visit(QueryPartListView.wrap((List) getUpsertKeys()).qualify(false)).sql(')');
        }
        if (this.upsertSelect != null) {
            ctx.formatSeparator().visit(this.upsertSelect);
        } else {
            ctx.formatSeparator().visit(Keywords.K_VALUES).sql(" (").visit(QueryPartListView.wrap(Tools.collect(removeReadonly(ctx, getUpsertFields(), getUpsertValues())))).sql(')');
        }
    }

    static final Iterable<Field<?>> removeReadonly(Context<?> ctx, List<Field<?>> it) {
        return removeReadonly(ctx, it, it);
    }

    static final Iterable<Field<?>> removeReadonly(Context<?> ctx, List<Field<?>> checkIt, List<Field<?>> removeIt) {
        return removeIt;
    }

    /* JADX WARN: Type inference failed for: r0v169, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v17, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v195, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v25, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v59, types: [org.jooq.Context] */
    private final void toSQLStandard(Context<?> ctx) {
        String name;
        InlineDerivedTable.inlineDerivedTable(ctx, this.table);
        ctx.start(Clause.MERGE_MERGE_INTO).visit(Keywords.K_MERGE_INTO).sql(' ').declareTables(true, c -> {
            c.visit(this.table);
        }).end(Clause.MERGE_MERGE_INTO).formatSeparator().start(Clause.MERGE_USING).visit(Keywords.K_USING).sql(' ');
        ctx.declareTables(true, c1 -> {
            c1.data(Tools.BooleanDataKey.DATA_WRAP_DERIVED_TABLES_IN_PARENTHESES, true, c2 -> {
                if (this.usingDual) {
                    switch (c2.family()) {
                        case DERBY:
                            c2.visit(new Dual());
                            return;
                        default:
                            c2.visit(DSL.selectOne());
                            return;
                    }
                }
                c2.visit(this.using);
            });
        });
        switch (ctx.family()) {
            case POSTGRES:
                if (this.using instanceof Select) {
                    int hash = Internal.hash(this.using);
                    ctx.sql(' ').visit(Keywords.K_AS).sql(' ').sql("dummy_").sql(hash).sql('(');
                    String separator = "";
                    for (Field<?> field : this.using.asTable("t").fields()) {
                        if (StringUtils.isBlank(field.getName())) {
                            name = "dummy_" + hash + "_" + Internal.hash(field);
                        } else {
                            name = field.getName();
                        }
                        String name2 = name;
                        ctx.sql(separator).literal(name2);
                        separator = ", ";
                    }
                    ctx.sql(')');
                    break;
                } else if (this.usingDual) {
                    ctx.sql(" t");
                    break;
                }
                break;
        }
        ctx.end(Clause.MERGE_USING).formatSeparator().start(Clause.MERGE_ON).visit(Keywords.K_ON).sql(' ');
        ctx.visit((Condition) this.on);
        ctx.end(Clause.MERGE_ON).start(Clause.MERGE_WHEN_MATCHED_THEN_UPDATE).start(Clause.MERGE_SET);
        boolean emulate = false;
        boolean requireMatchedConditions = false;
        if (NO_SUPPORT_CONDITION_AFTER_NO_CONDITION.contains(ctx.dialect())) {
            boolean withoutMatchedConditionFound = false;
            for (MatchedClause m : this.matched) {
                boolean z = requireMatchedConditions | withoutMatchedConditionFound;
                requireMatchedConditions = z;
                if (!z) {
                    withoutMatchedConditionFound |= m.condition instanceof NoCondition;
                }
            }
        }
        if (NO_SUPPORT_MULTI.contains(ctx.dialect()) && this.matched.size() > 1) {
            boolean matchUpdate = false;
            boolean matchDelete = false;
            Iterator<MatchedClause> it = this.matched.iterator();
            while (it.hasNext()) {
                if (it.next().delete) {
                    boolean z2 = emulate | matchDelete;
                    emulate = z2;
                    if (!z2) {
                        matchDelete = true;
                    }
                } else {
                    boolean z3 = emulate | matchUpdate;
                    emulate = z3;
                    if (!z3) {
                        matchUpdate = true;
                    }
                }
            }
        }
        if (emulate) {
            MatchedClause update = null;
            MatchedClause delete = null;
            Condition negate = DSL.noCondition();
            for (MatchedClause m2 : this.matched) {
                Condition condition = negate.and(m2.condition);
                if (m2.delete) {
                    if (delete == null) {
                        delete = new MatchedClause(this.table, DSL.noCondition(), true);
                    }
                    delete.condition = delete.condition.or(condition);
                } else {
                    if (update == null) {
                        update = new MatchedClause(this.table, DSL.noCondition());
                    }
                    for (Map.Entry<FieldOrRow, FieldOrRowOrSelect> e : m2.updateMap.entrySet()) {
                        FieldOrRowOrSelect exp = update.updateMap.get((Object) e.getKey());
                        if (exp instanceof CaseSearched) {
                            CaseSearched c2 = (CaseSearched) exp;
                            c2.when(negate.and(condition), (Condition) e.getValue());
                        } else {
                            update.updateMap.put(e.getKey(), (FieldOrRowOrSelect) DSL.when(negate.and(condition), (Field) e.getValue()).else_((CaseConditionStep) e.getKey()));
                        }
                    }
                    update.condition = update.condition.or(condition);
                }
                if (REQUIRE_NEGATION.contains(ctx.dialect())) {
                    negate = negate.andNot(m2.condition instanceof NoCondition ? DSL.trueCondition() : m2.condition);
                }
            }
            if (delete != null) {
                toSQLMatched(ctx, delete, requireMatchedConditions);
            }
            if (update != null) {
                toSQLMatched(ctx, update, requireMatchedConditions);
            }
        } else if (REQUIRE_NEGATION.contains(ctx.dialect())) {
            Condition negate2 = DSL.noCondition();
            for (MatchedClause m3 : this.matched) {
                toSQLMatched(ctx, new MatchedClause(this.table, negate2.and(m3.condition), m3.delete, m3.updateMap), requireMatchedConditions);
                negate2 = negate2.andNot(m3.condition instanceof NoCondition ? DSL.trueCondition() : m3.condition);
            }
        } else {
            Iterator<MatchedClause> it2 = this.matched.iterator();
            while (it2.hasNext()) {
                toSQLMatched(ctx, it2.next(), requireMatchedConditions);
            }
        }
        ctx.end(Clause.MERGE_SET).end(Clause.MERGE_WHEN_MATCHED_THEN_UPDATE).start(Clause.MERGE_WHEN_NOT_MATCHED_THEN_INSERT);
        Iterator<NotMatchedClause> it3 = this.notMatched.iterator();
        while (it3.hasNext()) {
            toSQLNotMatched(ctx, it3.next());
        }
        ctx.end(Clause.MERGE_WHEN_NOT_MATCHED_THEN_INSERT);
    }

    private final void toSQLMatched(Context<?> ctx, MatchedClause m, boolean requireMatchedConditions) {
        if (m.delete) {
            toSQLMatched(ctx, null, m, requireMatchedConditions);
        } else {
            toSQLMatched(ctx, m, null, requireMatchedConditions);
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v10, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v15, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v20, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v23, types: [org.jooq.Context] */
    private final void toSQLMatched(Context<?> ctx, MatchedClause update, MatchedClause delete, boolean requireMatchedConditions) {
        ctx.formatSeparator().visit(Keywords.K_WHEN).sql(' ').visit(Keywords.K_MATCHED);
        MatchedClause m = update != null ? update : delete;
        if (requireMatchedConditions || !(m.condition instanceof NoCondition)) {
            ctx.sql(' ').visit(Keywords.K_AND).sql(' ').visit(m.condition);
        }
        ctx.sql(' ').visit(Keywords.K_THEN);
        if (update != null) {
            ctx.sql(' ').visit(Keywords.K_UPDATE).sql(' ').visit(Keywords.K_SET).formatIndentStart().formatSeparator().visit(update.updateMap).formatIndentEnd();
        }
        if (delete != null) {
            ctx.sql(' ').visit(Keywords.K_DELETE);
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v12, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v19, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v28, types: [org.jooq.Context] */
    private final void toSQLNotMatched(Context<?> ctx, NotMatchedClause m) {
        ctx.formatSeparator().visit(Keywords.K_WHEN).sql(' ').visit(Keywords.K_NOT).sql(' ').visit(Keywords.K_MATCHED).sql(' ');
        if (!(m.condition instanceof NoCondition)) {
            ctx.visit(Keywords.K_AND).sql(' ').visit(m.condition).sql(' ');
        }
        ctx.visit(Keywords.K_THEN).sql(' ').visit(Keywords.K_INSERT);
        m.insertMap.toSQLReferenceKeys(ctx);
        ctx.formatSeparator().start(Clause.MERGE_VALUES).visit(Keywords.K_VALUES).sql(' ');
        m.insertMap.toSQL92Values(ctx);
        ctx.end(Clause.MERGE_VALUES);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/MergeImpl$MatchedClause.class */
    public static final class MatchedClause implements Serializable {
        FieldMapForUpdate updateMap;
        boolean delete;
        Condition condition;

        MatchedClause(Table<?> table, Condition condition) {
            this(table, condition, false);
        }

        MatchedClause(Table<?> table, Condition condition, boolean delete) {
            this(table, condition, delete, new FieldMapForUpdate(table, FieldMapForUpdate.SetClause.MERGE, Clause.MERGE_SET_ASSIGNMENT));
        }

        MatchedClause(Table<?> table, Condition condition, boolean delete, FieldMapForUpdate updateMap) {
            this.updateMap = updateMap;
            this.condition = condition == null ? DSL.noCondition() : condition;
            this.delete = delete;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/MergeImpl$NotMatchedClause.class */
    public static final class NotMatchedClause implements Serializable {
        FieldMapsForInsert insertMap;
        Condition condition;

        NotMatchedClause(Table<?> table, Condition condition) {
            this.insertMap = new FieldMapsForInsert(table);
            this.condition = condition == null ? DSL.noCondition() : condition;
        }
    }
}
