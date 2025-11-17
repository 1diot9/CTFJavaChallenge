package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Constraint;
import org.jooq.Field;
import org.jooq.FieldLike;
import org.jooq.FieldOrRow;
import org.jooq.FieldOrRowOrSelect;
import org.jooq.InsertOnConflictConditionStep;
import org.jooq.InsertOnConflictDoUpdateStep;
import org.jooq.InsertOnConflictWhereIndexPredicateStep;
import org.jooq.InsertOnDuplicateSetMoreStep;
import org.jooq.InsertResultStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.InsertValuesStep1;
import org.jooq.InsertValuesStep10;
import org.jooq.InsertValuesStep11;
import org.jooq.InsertValuesStep12;
import org.jooq.InsertValuesStep13;
import org.jooq.InsertValuesStep14;
import org.jooq.InsertValuesStep15;
import org.jooq.InsertValuesStep16;
import org.jooq.InsertValuesStep17;
import org.jooq.InsertValuesStep18;
import org.jooq.InsertValuesStep19;
import org.jooq.InsertValuesStep2;
import org.jooq.InsertValuesStep20;
import org.jooq.InsertValuesStep21;
import org.jooq.InsertValuesStep22;
import org.jooq.InsertValuesStep3;
import org.jooq.InsertValuesStep4;
import org.jooq.InsertValuesStep5;
import org.jooq.InsertValuesStep6;
import org.jooq.InsertValuesStep7;
import org.jooq.InsertValuesStep8;
import org.jooq.InsertValuesStep9;
import org.jooq.InsertValuesStepN;
import org.jooq.Name;
import org.jooq.Operator;
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
import org.jooq.Row;
import org.jooq.Row1;
import org.jooq.Row10;
import org.jooq.Row11;
import org.jooq.Row12;
import org.jooq.Row13;
import org.jooq.Row14;
import org.jooq.Row15;
import org.jooq.Row16;
import org.jooq.Row17;
import org.jooq.Row18;
import org.jooq.Row19;
import org.jooq.Row2;
import org.jooq.Row20;
import org.jooq.Row21;
import org.jooq.Row22;
import org.jooq.Row3;
import org.jooq.Row4;
import org.jooq.Row5;
import org.jooq.Row6;
import org.jooq.Row7;
import org.jooq.Row8;
import org.jooq.Row9;
import org.jooq.RowN;
import org.jooq.SQL;
import org.jooq.Select;
import org.jooq.SelectField;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.Table;
import org.jooq.UniqueKey;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/InsertImpl.class */
public final class InsertImpl<R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> extends AbstractDelegatingDMLQuery<R, InsertQueryImpl<R>> implements InsertValuesStep1<R, T1>, InsertValuesStep2<R, T1, T2>, InsertValuesStep3<R, T1, T2, T3>, InsertValuesStep4<R, T1, T2, T3, T4>, InsertValuesStep5<R, T1, T2, T3, T4, T5>, InsertValuesStep6<R, T1, T2, T3, T4, T5, T6>, InsertValuesStep7<R, T1, T2, T3, T4, T5, T6, T7>, InsertValuesStep8<R, T1, T2, T3, T4, T5, T6, T7, T8>, InsertValuesStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9>, InsertValuesStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>, InsertValuesStep11<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>, InsertValuesStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>, InsertValuesStep13<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>, InsertValuesStep14<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>, InsertValuesStep15<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>, InsertValuesStep16<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>, InsertValuesStep17<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>, InsertValuesStep18<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>, InsertValuesStep19<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>, InsertValuesStep20<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>, InsertValuesStep21<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>, InsertValuesStep22<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>, InsertValuesStepN<R>, InsertSetStep<R>, InsertSetMoreStep<R>, InsertOnDuplicateSetMoreStep<R>, InsertOnConflictWhereIndexPredicateStep<R>, InsertOnConflictConditionStep<R>, QOM.Insert<R> {
    private final Table<R> into;
    private Field<?>[] fields;
    private boolean onDuplicateKeyUpdate;
    private boolean returningResult;
    private transient boolean doUpdateWhere;

    @Override // org.jooq.InsertValuesStep1, org.jooq.InsertValuesStep2, org.jooq.InsertValuesStep3, org.jooq.InsertValuesStep4, org.jooq.InsertValuesStep5, org.jooq.InsertValuesStep6, org.jooq.InsertValuesStep7, org.jooq.InsertValuesStep8, org.jooq.InsertValuesStep9, org.jooq.InsertValuesStep10, org.jooq.InsertValuesStep11, org.jooq.InsertValuesStep12, org.jooq.InsertValuesStep13, org.jooq.InsertValuesStep14, org.jooq.InsertValuesStep15, org.jooq.InsertValuesStep16, org.jooq.InsertValuesStep17, org.jooq.InsertValuesStep18, org.jooq.InsertValuesStep19, org.jooq.InsertValuesStep20, org.jooq.InsertValuesStep21, org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStep1 values(Collection collection) {
        return values((Collection<?>) collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep1
    public /* bridge */ /* synthetic */ InsertValuesStep1 values(Object obj) {
        return values((InsertImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj);
    }

    @Override // org.jooq.InsertOnDuplicateStep
    public /* bridge */ /* synthetic */ InsertOnConflictWhereIndexPredicateStep onConflict(Collection collection) {
        return onConflict((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.InsertOnDuplicateStep
    public /* bridge */ /* synthetic */ InsertOnConflictWhereIndexPredicateStep onConflict(Field[] fieldArr) {
        return onConflict((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.InsertValuesStep2, org.jooq.InsertValuesStep3, org.jooq.InsertValuesStep4, org.jooq.InsertValuesStep5, org.jooq.InsertValuesStep6, org.jooq.InsertValuesStep7, org.jooq.InsertValuesStep8, org.jooq.InsertValuesStep9, org.jooq.InsertValuesStep10, org.jooq.InsertValuesStep11, org.jooq.InsertValuesStep12, org.jooq.InsertValuesStep13, org.jooq.InsertValuesStep14, org.jooq.InsertValuesStep15, org.jooq.InsertValuesStep16, org.jooq.InsertValuesStep17, org.jooq.InsertValuesStep18, org.jooq.InsertValuesStep19, org.jooq.InsertValuesStep20, org.jooq.InsertValuesStep21, org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStep2 values(Collection collection) {
        return values((Collection<?>) collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep2
    public /* bridge */ /* synthetic */ InsertValuesStep2 values(Object obj, Object obj2) {
        return values((InsertImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2);
    }

    @Override // org.jooq.InsertValuesStep3, org.jooq.InsertValuesStep4, org.jooq.InsertValuesStep5, org.jooq.InsertValuesStep6, org.jooq.InsertValuesStep7, org.jooq.InsertValuesStep8, org.jooq.InsertValuesStep9, org.jooq.InsertValuesStep10, org.jooq.InsertValuesStep11, org.jooq.InsertValuesStep12, org.jooq.InsertValuesStep13, org.jooq.InsertValuesStep14, org.jooq.InsertValuesStep15, org.jooq.InsertValuesStep16, org.jooq.InsertValuesStep17, org.jooq.InsertValuesStep18, org.jooq.InsertValuesStep19, org.jooq.InsertValuesStep20, org.jooq.InsertValuesStep21, org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStep3 values(Collection collection) {
        return values((Collection<?>) collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep3
    public /* bridge */ /* synthetic */ InsertValuesStep3 values(Object obj, Object obj2, Object obj3) {
        return values((InsertImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3);
    }

    @Override // org.jooq.InsertValuesStep4, org.jooq.InsertValuesStep5, org.jooq.InsertValuesStep6, org.jooq.InsertValuesStep7, org.jooq.InsertValuesStep8, org.jooq.InsertValuesStep9, org.jooq.InsertValuesStep10, org.jooq.InsertValuesStep11, org.jooq.InsertValuesStep12, org.jooq.InsertValuesStep13, org.jooq.InsertValuesStep14, org.jooq.InsertValuesStep15, org.jooq.InsertValuesStep16, org.jooq.InsertValuesStep17, org.jooq.InsertValuesStep18, org.jooq.InsertValuesStep19, org.jooq.InsertValuesStep20, org.jooq.InsertValuesStep21, org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStep4 values(Collection collection) {
        return values((Collection<?>) collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep4
    public /* bridge */ /* synthetic */ InsertValuesStep4 values(Object obj, Object obj2, Object obj3, Object obj4) {
        return values((InsertImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4);
    }

    @Override // org.jooq.InsertValuesStep5, org.jooq.InsertValuesStep6, org.jooq.InsertValuesStep7, org.jooq.InsertValuesStep8, org.jooq.InsertValuesStep9, org.jooq.InsertValuesStep10, org.jooq.InsertValuesStep11, org.jooq.InsertValuesStep12, org.jooq.InsertValuesStep13, org.jooq.InsertValuesStep14, org.jooq.InsertValuesStep15, org.jooq.InsertValuesStep16, org.jooq.InsertValuesStep17, org.jooq.InsertValuesStep18, org.jooq.InsertValuesStep19, org.jooq.InsertValuesStep20, org.jooq.InsertValuesStep21, org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStep5 values(Collection collection) {
        return values((Collection<?>) collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep5
    public /* bridge */ /* synthetic */ InsertValuesStep5 values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        return values((InsertImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5);
    }

    @Override // org.jooq.InsertValuesStep6, org.jooq.InsertValuesStep7, org.jooq.InsertValuesStep8, org.jooq.InsertValuesStep9, org.jooq.InsertValuesStep10, org.jooq.InsertValuesStep11, org.jooq.InsertValuesStep12, org.jooq.InsertValuesStep13, org.jooq.InsertValuesStep14, org.jooq.InsertValuesStep15, org.jooq.InsertValuesStep16, org.jooq.InsertValuesStep17, org.jooq.InsertValuesStep18, org.jooq.InsertValuesStep19, org.jooq.InsertValuesStep20, org.jooq.InsertValuesStep21, org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStep6 values(Collection collection) {
        return values((Collection<?>) collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep6
    public /* bridge */ /* synthetic */ InsertValuesStep6 values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
        return values((InsertImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6);
    }

    @Override // org.jooq.InsertValuesStep7, org.jooq.InsertValuesStep8, org.jooq.InsertValuesStep9, org.jooq.InsertValuesStep10, org.jooq.InsertValuesStep11, org.jooq.InsertValuesStep12, org.jooq.InsertValuesStep13, org.jooq.InsertValuesStep14, org.jooq.InsertValuesStep15, org.jooq.InsertValuesStep16, org.jooq.InsertValuesStep17, org.jooq.InsertValuesStep18, org.jooq.InsertValuesStep19, org.jooq.InsertValuesStep20, org.jooq.InsertValuesStep21, org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStep7 values(Collection collection) {
        return values((Collection<?>) collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep7
    public /* bridge */ /* synthetic */ InsertValuesStep7 values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
        return values((InsertImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7);
    }

    @Override // org.jooq.InsertValuesStep8, org.jooq.InsertValuesStep9, org.jooq.InsertValuesStep10, org.jooq.InsertValuesStep11, org.jooq.InsertValuesStep12, org.jooq.InsertValuesStep13, org.jooq.InsertValuesStep14, org.jooq.InsertValuesStep15, org.jooq.InsertValuesStep16, org.jooq.InsertValuesStep17, org.jooq.InsertValuesStep18, org.jooq.InsertValuesStep19, org.jooq.InsertValuesStep20, org.jooq.InsertValuesStep21, org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStep8 values(Collection collection) {
        return values((Collection<?>) collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep8
    public /* bridge */ /* synthetic */ InsertValuesStep8 values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8) {
        return values((InsertImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8);
    }

    @Override // org.jooq.InsertValuesStep9, org.jooq.InsertValuesStep10, org.jooq.InsertValuesStep11, org.jooq.InsertValuesStep12, org.jooq.InsertValuesStep13, org.jooq.InsertValuesStep14, org.jooq.InsertValuesStep15, org.jooq.InsertValuesStep16, org.jooq.InsertValuesStep17, org.jooq.InsertValuesStep18, org.jooq.InsertValuesStep19, org.jooq.InsertValuesStep20, org.jooq.InsertValuesStep21, org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStep9 values(Collection collection) {
        return values((Collection<?>) collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep9
    public /* bridge */ /* synthetic */ InsertValuesStep9 values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9) {
        return values((InsertImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9);
    }

    @Override // org.jooq.InsertValuesStep10, org.jooq.InsertValuesStep11, org.jooq.InsertValuesStep12, org.jooq.InsertValuesStep13, org.jooq.InsertValuesStep14, org.jooq.InsertValuesStep15, org.jooq.InsertValuesStep16, org.jooq.InsertValuesStep17, org.jooq.InsertValuesStep18, org.jooq.InsertValuesStep19, org.jooq.InsertValuesStep20, org.jooq.InsertValuesStep21, org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStep10 values(Collection collection) {
        return values((Collection<?>) collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep10
    public /* bridge */ /* synthetic */ InsertValuesStep10 values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10) {
        return values((InsertImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10);
    }

    @Override // org.jooq.InsertValuesStep11, org.jooq.InsertValuesStep12, org.jooq.InsertValuesStep13, org.jooq.InsertValuesStep14, org.jooq.InsertValuesStep15, org.jooq.InsertValuesStep16, org.jooq.InsertValuesStep17, org.jooq.InsertValuesStep18, org.jooq.InsertValuesStep19, org.jooq.InsertValuesStep20, org.jooq.InsertValuesStep21, org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStep11 values(Collection collection) {
        return values((Collection<?>) collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep11
    public /* bridge */ /* synthetic */ InsertValuesStep11 values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11) {
        return values((InsertImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11);
    }

    @Override // org.jooq.InsertValuesStep12, org.jooq.InsertValuesStep13, org.jooq.InsertValuesStep14, org.jooq.InsertValuesStep15, org.jooq.InsertValuesStep16, org.jooq.InsertValuesStep17, org.jooq.InsertValuesStep18, org.jooq.InsertValuesStep19, org.jooq.InsertValuesStep20, org.jooq.InsertValuesStep21, org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStep12 values(Collection collection) {
        return values((Collection<?>) collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep12
    public /* bridge */ /* synthetic */ InsertValuesStep12 values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12) {
        return values((InsertImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12);
    }

    @Override // org.jooq.InsertValuesStep13, org.jooq.InsertValuesStep14, org.jooq.InsertValuesStep15, org.jooq.InsertValuesStep16, org.jooq.InsertValuesStep17, org.jooq.InsertValuesStep18, org.jooq.InsertValuesStep19, org.jooq.InsertValuesStep20, org.jooq.InsertValuesStep21, org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStep13 values(Collection collection) {
        return values((Collection<?>) collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep13
    public /* bridge */ /* synthetic */ InsertValuesStep13 values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13) {
        return values((InsertImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13);
    }

    @Override // org.jooq.InsertValuesStep14, org.jooq.InsertValuesStep15, org.jooq.InsertValuesStep16, org.jooq.InsertValuesStep17, org.jooq.InsertValuesStep18, org.jooq.InsertValuesStep19, org.jooq.InsertValuesStep20, org.jooq.InsertValuesStep21, org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStep14 values(Collection collection) {
        return values((Collection<?>) collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep14
    public /* bridge */ /* synthetic */ InsertValuesStep14 values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14) {
        return values((InsertImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14);
    }

    @Override // org.jooq.InsertValuesStep15, org.jooq.InsertValuesStep16, org.jooq.InsertValuesStep17, org.jooq.InsertValuesStep18, org.jooq.InsertValuesStep19, org.jooq.InsertValuesStep20, org.jooq.InsertValuesStep21, org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStep15 values(Collection collection) {
        return values((Collection<?>) collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep15
    public /* bridge */ /* synthetic */ InsertValuesStep15 values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15) {
        return values((InsertImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15);
    }

    @Override // org.jooq.InsertValuesStep16, org.jooq.InsertValuesStep17, org.jooq.InsertValuesStep18, org.jooq.InsertValuesStep19, org.jooq.InsertValuesStep20, org.jooq.InsertValuesStep21, org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStep16 values(Collection collection) {
        return values((Collection<?>) collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep16
    public /* bridge */ /* synthetic */ InsertValuesStep16 values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15, Object obj16) {
        return values((InsertImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15, obj16);
    }

    @Override // org.jooq.InsertValuesStep17, org.jooq.InsertValuesStep18, org.jooq.InsertValuesStep19, org.jooq.InsertValuesStep20, org.jooq.InsertValuesStep21, org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStep17 values(Collection collection) {
        return values((Collection<?>) collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep17
    public /* bridge */ /* synthetic */ InsertValuesStep17 values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15, Object obj16, Object obj17) {
        return values((InsertImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15, obj16, obj17);
    }

    @Override // org.jooq.InsertValuesStep18, org.jooq.InsertValuesStep19, org.jooq.InsertValuesStep20, org.jooq.InsertValuesStep21, org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStep18 values(Collection collection) {
        return values((Collection<?>) collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep18
    public /* bridge */ /* synthetic */ InsertValuesStep18 values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15, Object obj16, Object obj17, Object obj18) {
        return values((InsertImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15, obj16, obj17, obj18);
    }

    @Override // org.jooq.InsertValuesStep19, org.jooq.InsertValuesStep20, org.jooq.InsertValuesStep21, org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStep19 values(Collection collection) {
        return values((Collection<?>) collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep19
    public /* bridge */ /* synthetic */ InsertValuesStep19 values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15, Object obj16, Object obj17, Object obj18, Object obj19) {
        return values((InsertImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15, obj16, obj17, obj18, obj19);
    }

    @Override // org.jooq.InsertValuesStep20, org.jooq.InsertValuesStep21, org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStep20 values(Collection collection) {
        return values((Collection<?>) collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep20
    public /* bridge */ /* synthetic */ InsertValuesStep20 values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15, Object obj16, Object obj17, Object obj18, Object obj19, Object obj20) {
        return values((InsertImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15, obj16, obj17, obj18, obj19, obj20);
    }

    @Override // org.jooq.InsertValuesStep21, org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStep21 values(Collection collection) {
        return values((Collection<?>) collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep21
    public /* bridge */ /* synthetic */ InsertValuesStep21 values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15, Object obj16, Object obj17, Object obj18, Object obj19, Object obj20, Object obj21) {
        return values((InsertImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15, obj16, obj17, obj18, obj19, obj20, obj21);
    }

    @Override // org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStep22 values(Collection collection) {
        return values((Collection<?>) collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep22
    public /* bridge */ /* synthetic */ InsertValuesStep22 values(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12, Object obj13, Object obj14, Object obj15, Object obj16, Object obj17, Object obj18, Object obj19, Object obj20, Object obj21, Object obj22) {
        return values((InsertImpl<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>) obj, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12, obj13, obj14, obj15, obj16, obj17, obj18, obj19, obj20, obj21, obj22);
    }

    @Override // org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStepN values(Collection collection) {
        return values((Collection<?>) collection);
    }

    @Override // org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStepN values(Field[] fieldArr) {
        return values((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.InsertSetStep, org.jooq.InsertSetMoreStep
    public /* bridge */ /* synthetic */ InsertSetMoreStep set(Collection collection) {
        return set((Collection<? extends Record>) collection);
    }

    @Override // org.jooq.InsertSetStep, org.jooq.InsertSetMoreStep, org.jooq.InsertOnDuplicateSetStep
    public /* bridge */ /* synthetic */ InsertSetMoreStep set(Map map) {
        return set((Map<?, ?>) map);
    }

    @Override // org.jooq.InsertSetStep, org.jooq.InsertSetMoreStep, org.jooq.InsertOnDuplicateSetStep
    public /* bridge */ /* synthetic */ InsertSetMoreStep set(Field field, Object obj) {
        return set((Field<Field>) field, (Field) obj);
    }

    @Override // org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStepN columns(Collection collection) {
        return columns((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.InsertSetStep
    public /* bridge */ /* synthetic */ InsertValuesStepN columns(Field[] fieldArr) {
        return columns((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.InsertOnDuplicateSetStep
    public /* bridge */ /* synthetic */ InsertOnDuplicateSetMoreStep set(Map map) {
        return set((Map<?, ?>) map);
    }

    @Override // org.jooq.InsertOnDuplicateSetStep
    public /* bridge */ /* synthetic */ InsertOnDuplicateSetMoreStep set(Field field, Object obj) {
        return set((Field<Field>) field, (Field) obj);
    }

    @Override // org.jooq.InsertOnConflictWhereStep
    public /* bridge */ /* synthetic */ InsertOnConflictConditionStep whereNotExists(Select select) {
        return whereNotExists((Select<?>) select);
    }

    @Override // org.jooq.InsertOnConflictWhereStep
    public /* bridge */ /* synthetic */ InsertOnConflictConditionStep whereExists(Select select) {
        return whereExists((Select<?>) select);
    }

    @Override // org.jooq.InsertOnConflictWhereStep, org.jooq.InsertOnConflictWhereIndexPredicateStep
    public /* bridge */ /* synthetic */ InsertOnConflictConditionStep where(Field field) {
        return where((Field<Boolean>) field);
    }

    @Override // org.jooq.InsertOnConflictWhereStep, org.jooq.InsertOnConflictWhereIndexPredicateStep
    public /* bridge */ /* synthetic */ InsertOnConflictConditionStep where(Collection collection) {
        return where((Collection<? extends Condition>) collection);
    }

    @Override // org.jooq.InsertOnConflictWhereIndexPredicateStep
    public /* bridge */ /* synthetic */ InsertOnConflictDoUpdateStep where(Field field) {
        return where((Field<Boolean>) field);
    }

    @Override // org.jooq.InsertOnConflictWhereIndexPredicateStep
    public /* bridge */ /* synthetic */ InsertOnConflictDoUpdateStep where(Collection collection) {
        return where((Collection<? extends Condition>) collection);
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public /* bridge */ /* synthetic */ InsertOnConflictConditionStep orNotExists(Select select) {
        return orNotExists((Select<?>) select);
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public /* bridge */ /* synthetic */ InsertOnConflictConditionStep orExists(Select select) {
        return orExists((Select<?>) select);
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public /* bridge */ /* synthetic */ InsertOnConflictConditionStep orNot(Field field) {
        return orNot((Field<Boolean>) field);
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public /* bridge */ /* synthetic */ InsertOnConflictConditionStep or(Field field) {
        return or((Field<Boolean>) field);
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public /* bridge */ /* synthetic */ InsertOnConflictConditionStep andNotExists(Select select) {
        return andNotExists((Select<?>) select);
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public /* bridge */ /* synthetic */ InsertOnConflictConditionStep andExists(Select select) {
        return andExists((Select<?>) select);
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public /* bridge */ /* synthetic */ InsertOnConflictConditionStep andNot(Field field) {
        return andNot((Field<Boolean>) field);
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public /* bridge */ /* synthetic */ InsertOnConflictConditionStep and(Field field) {
        return and((Field<Boolean>) field);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public InsertImpl(Configuration configuration, WithImpl with, Table<R> into) {
        this(configuration, with, into, Collections.emptyList());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public InsertImpl(Configuration configuration, WithImpl with, Table<R> into, Collection<? extends Field<?>> fields) {
        super(new InsertQueryImpl(configuration, with, into));
        this.into = into;
        columns(fields);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep1, org.jooq.InsertValuesStep2, org.jooq.InsertValuesStep3, org.jooq.InsertValuesStep4, org.jooq.InsertValuesStep5, org.jooq.InsertValuesStep6, org.jooq.InsertValuesStep7, org.jooq.InsertValuesStep8, org.jooq.InsertValuesStep9, org.jooq.InsertValuesStep10, org.jooq.InsertValuesStep11, org.jooq.InsertValuesStep12, org.jooq.InsertValuesStep13, org.jooq.InsertValuesStep14, org.jooq.InsertValuesStep15, org.jooq.InsertValuesStep16, org.jooq.InsertValuesStep17, org.jooq.InsertValuesStep18, org.jooq.InsertValuesStep19, org.jooq.InsertValuesStep20, org.jooq.InsertValuesStep21, org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public final InsertImpl select(Select select) {
        ((InsertQueryImpl) getDelegate()).setSelect(this.fields, (Select<?>) select);
        return this;
    }

    @Override // org.jooq.InsertValuesStep1
    public final InsertImpl values(T1 value1) {
        return values(value1);
    }

    @Override // org.jooq.InsertValuesStep2
    public final InsertImpl values(T1 value1, T2 value2) {
        return values(value1, value2);
    }

    @Override // org.jooq.InsertValuesStep3
    public final InsertImpl values(T1 value1, T2 value2, T3 value3) {
        return values(value1, value2, value3);
    }

    @Override // org.jooq.InsertValuesStep4
    public final InsertImpl values(T1 value1, T2 value2, T3 value3, T4 value4) {
        return values(value1, value2, value3, value4);
    }

    @Override // org.jooq.InsertValuesStep5
    public final InsertImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5) {
        return values(value1, value2, value3, value4, value5);
    }

    @Override // org.jooq.InsertValuesStep6
    public final InsertImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6) {
        return values(value1, value2, value3, value4, value5, value6);
    }

    @Override // org.jooq.InsertValuesStep7
    public final InsertImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7) {
        return values(value1, value2, value3, value4, value5, value6, value7);
    }

    @Override // org.jooq.InsertValuesStep8
    public final InsertImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8);
    }

    @Override // org.jooq.InsertValuesStep9
    public final InsertImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9);
    }

    @Override // org.jooq.InsertValuesStep10
    public final InsertImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10);
    }

    @Override // org.jooq.InsertValuesStep11
    public final InsertImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11);
    }

    @Override // org.jooq.InsertValuesStep12
    public final InsertImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11, T12 value12) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12);
    }

    @Override // org.jooq.InsertValuesStep13
    public final InsertImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11, T12 value12, T13 value13) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13);
    }

    @Override // org.jooq.InsertValuesStep14
    public final InsertImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11, T12 value12, T13 value13, T14 value14) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14);
    }

    @Override // org.jooq.InsertValuesStep15
    public final InsertImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11, T12 value12, T13 value13, T14 value14, T15 value15) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15);
    }

    @Override // org.jooq.InsertValuesStep16
    public final InsertImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11, T12 value12, T13 value13, T14 value14, T15 value15, T16 value16) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16);
    }

    @Override // org.jooq.InsertValuesStep17
    public final InsertImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11, T12 value12, T13 value13, T14 value14, T15 value15, T16 value16, T17 value17) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17);
    }

    @Override // org.jooq.InsertValuesStep18
    public final InsertImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11, T12 value12, T13 value13, T14 value14, T15 value15, T16 value16, T17 value17, T18 value18) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18);
    }

    @Override // org.jooq.InsertValuesStep19
    public final InsertImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11, T12 value12, T13 value13, T14 value14, T15 value15, T16 value16, T17 value17, T18 value18, T19 value19) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19);
    }

    @Override // org.jooq.InsertValuesStep20
    public final InsertImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11, T12 value12, T13 value13, T14 value14, T15 value15, T16 value16, T17 value17, T18 value18, T19 value19, T20 value20) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19, value20);
    }

    @Override // org.jooq.InsertValuesStep21
    public final InsertImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11, T12 value12, T13 value13, T14 value14, T15 value15, T16 value16, T17 value17, T18 value18, T19 value19, T20 value20, T21 value21) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19, value20, value21);
    }

    @Override // org.jooq.InsertValuesStep22
    public final InsertImpl values(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8, T9 value9, T10 value10, T11 value11, T12 value12, T13 value13, T14 value14, T15 value15, T16 value16, T17 value17, T18 value18, T19 value19, T20 value20, T21 value21, T22 value22) {
        return values(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19, value20, value21, value22);
    }

    @Override // org.jooq.InsertValuesStepN
    public final InsertImpl values(RowN values) {
        return values(values.fields());
    }

    @Override // org.jooq.InsertValuesStepN
    public final InsertImpl valuesOfRows(RowN... values) {
        return valuesOfRows((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep1
    public final InsertImpl values(Row1<T1> values) {
        return values(values.fields());
    }

    @Override // org.jooq.InsertValuesStep1
    public final InsertImpl valuesOfRows(Row1<T1>... values) {
        return valuesOfRows((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep2
    public final InsertImpl values(Row2<T1, T2> values) {
        return values(values.fields());
    }

    @Override // org.jooq.InsertValuesStep2
    public final InsertImpl valuesOfRows(Row2<T1, T2>... values) {
        return valuesOfRows((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep3
    public final InsertImpl values(Row3<T1, T2, T3> values) {
        return values(values.fields());
    }

    @Override // org.jooq.InsertValuesStep3
    public final InsertImpl valuesOfRows(Row3<T1, T2, T3>... values) {
        return valuesOfRows((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep4
    public final InsertImpl values(Row4<T1, T2, T3, T4> values) {
        return values(values.fields());
    }

    @Override // org.jooq.InsertValuesStep4
    public final InsertImpl valuesOfRows(Row4<T1, T2, T3, T4>... values) {
        return valuesOfRows((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep5
    public final InsertImpl values(Row5<T1, T2, T3, T4, T5> values) {
        return values(values.fields());
    }

    @Override // org.jooq.InsertValuesStep5
    public final InsertImpl valuesOfRows(Row5<T1, T2, T3, T4, T5>... values) {
        return valuesOfRows((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep6
    public final InsertImpl values(Row6<T1, T2, T3, T4, T5, T6> values) {
        return values(values.fields());
    }

    @Override // org.jooq.InsertValuesStep6
    public final InsertImpl valuesOfRows(Row6<T1, T2, T3, T4, T5, T6>... values) {
        return valuesOfRows((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep7
    public final InsertImpl values(Row7<T1, T2, T3, T4, T5, T6, T7> values) {
        return values(values.fields());
    }

    @Override // org.jooq.InsertValuesStep7
    public final InsertImpl valuesOfRows(Row7<T1, T2, T3, T4, T5, T6, T7>... values) {
        return valuesOfRows((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep8
    public final InsertImpl values(Row8<T1, T2, T3, T4, T5, T6, T7, T8> values) {
        return values(values.fields());
    }

    @Override // org.jooq.InsertValuesStep8
    public final InsertImpl valuesOfRows(Row8<T1, T2, T3, T4, T5, T6, T7, T8>... values) {
        return valuesOfRows((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep9
    public final InsertImpl values(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> values) {
        return values(values.fields());
    }

    @Override // org.jooq.InsertValuesStep9
    public final InsertImpl valuesOfRows(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9>... values) {
        return valuesOfRows((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep10
    public final InsertImpl values(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> values) {
        return values(values.fields());
    }

    @Override // org.jooq.InsertValuesStep10
    public final InsertImpl valuesOfRows(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>... values) {
        return valuesOfRows((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep11
    public final InsertImpl values(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> values) {
        return values(values.fields());
    }

    @Override // org.jooq.InsertValuesStep11
    public final InsertImpl valuesOfRows(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>... values) {
        return valuesOfRows((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep12
    public final InsertImpl values(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> values) {
        return values(values.fields());
    }

    @Override // org.jooq.InsertValuesStep12
    public final InsertImpl valuesOfRows(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>... values) {
        return valuesOfRows((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep13
    public final InsertImpl values(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> values) {
        return values(values.fields());
    }

    @Override // org.jooq.InsertValuesStep13
    public final InsertImpl valuesOfRows(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>... values) {
        return valuesOfRows((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep14
    public final InsertImpl values(Row14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> values) {
        return values(values.fields());
    }

    @Override // org.jooq.InsertValuesStep14
    public final InsertImpl valuesOfRows(Row14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>... values) {
        return valuesOfRows((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep15
    public final InsertImpl values(Row15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> values) {
        return values(values.fields());
    }

    @Override // org.jooq.InsertValuesStep15
    public final InsertImpl valuesOfRows(Row15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>... values) {
        return valuesOfRows((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep16
    public final InsertImpl values(Row16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> values) {
        return values(values.fields());
    }

    @Override // org.jooq.InsertValuesStep16
    public final InsertImpl valuesOfRows(Row16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>... values) {
        return valuesOfRows((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep17
    public final InsertImpl values(Row17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> values) {
        return values(values.fields());
    }

    @Override // org.jooq.InsertValuesStep17
    public final InsertImpl valuesOfRows(Row17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>... values) {
        return valuesOfRows((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep18
    public final InsertImpl values(Row18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> values) {
        return values(values.fields());
    }

    @Override // org.jooq.InsertValuesStep18
    public final InsertImpl valuesOfRows(Row18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>... values) {
        return valuesOfRows((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep19
    public final InsertImpl values(Row19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> values) {
        return values(values.fields());
    }

    @Override // org.jooq.InsertValuesStep19
    public final InsertImpl valuesOfRows(Row19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>... values) {
        return valuesOfRows((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep20
    public final InsertImpl values(Row20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> values) {
        return values(values.fields());
    }

    @Override // org.jooq.InsertValuesStep20
    public final InsertImpl valuesOfRows(Row20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>... values) {
        return valuesOfRows((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep21
    public final InsertImpl values(Row21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> values) {
        return values(values.fields());
    }

    @Override // org.jooq.InsertValuesStep21
    public final InsertImpl valuesOfRows(Row21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>... values) {
        return valuesOfRows((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep22
    public final InsertImpl values(Row22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> values) {
        return values(values.fields());
    }

    @Override // org.jooq.InsertValuesStep22
    public final InsertImpl valuesOfRows(Row22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>... values) {
        return valuesOfRows((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStepN
    public final InsertImpl values(Record values) {
        return values(values.intoArray());
    }

    @Override // org.jooq.InsertValuesStepN
    public final InsertImpl valuesOfRecords(Record... values) {
        return valuesOfRecords((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep1
    public final InsertImpl values(Record1<T1> values) {
        return values(values.intoArray());
    }

    @Override // org.jooq.InsertValuesStep1
    public final InsertImpl valuesOfRecords(Record1<T1>... values) {
        return valuesOfRecords((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep2
    public final InsertImpl values(Record2<T1, T2> values) {
        return values(values.intoArray());
    }

    @Override // org.jooq.InsertValuesStep2
    public final InsertImpl valuesOfRecords(Record2<T1, T2>... values) {
        return valuesOfRecords((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep3
    public final InsertImpl values(Record3<T1, T2, T3> values) {
        return values(values.intoArray());
    }

    @Override // org.jooq.InsertValuesStep3
    public final InsertImpl valuesOfRecords(Record3<T1, T2, T3>... values) {
        return valuesOfRecords((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep4
    public final InsertImpl values(Record4<T1, T2, T3, T4> values) {
        return values(values.intoArray());
    }

    @Override // org.jooq.InsertValuesStep4
    public final InsertImpl valuesOfRecords(Record4<T1, T2, T3, T4>... values) {
        return valuesOfRecords((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep5
    public final InsertImpl values(Record5<T1, T2, T3, T4, T5> values) {
        return values(values.intoArray());
    }

    @Override // org.jooq.InsertValuesStep5
    public final InsertImpl valuesOfRecords(Record5<T1, T2, T3, T4, T5>... values) {
        return valuesOfRecords((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep6
    public final InsertImpl values(Record6<T1, T2, T3, T4, T5, T6> values) {
        return values(values.intoArray());
    }

    @Override // org.jooq.InsertValuesStep6
    public final InsertImpl valuesOfRecords(Record6<T1, T2, T3, T4, T5, T6>... values) {
        return valuesOfRecords((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep7
    public final InsertImpl values(Record7<T1, T2, T3, T4, T5, T6, T7> values) {
        return values(values.intoArray());
    }

    @Override // org.jooq.InsertValuesStep7
    public final InsertImpl valuesOfRecords(Record7<T1, T2, T3, T4, T5, T6, T7>... values) {
        return valuesOfRecords((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep8
    public final InsertImpl values(Record8<T1, T2, T3, T4, T5, T6, T7, T8> values) {
        return values(values.intoArray());
    }

    @Override // org.jooq.InsertValuesStep8
    public final InsertImpl valuesOfRecords(Record8<T1, T2, T3, T4, T5, T6, T7, T8>... values) {
        return valuesOfRecords((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep9
    public final InsertImpl values(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> values) {
        return values(values.intoArray());
    }

    @Override // org.jooq.InsertValuesStep9
    public final InsertImpl valuesOfRecords(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>... values) {
        return valuesOfRecords((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep10
    public final InsertImpl values(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> values) {
        return values(values.intoArray());
    }

    @Override // org.jooq.InsertValuesStep10
    public final InsertImpl valuesOfRecords(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>... values) {
        return valuesOfRecords((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep11
    public final InsertImpl values(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> values) {
        return values(values.intoArray());
    }

    @Override // org.jooq.InsertValuesStep11
    public final InsertImpl valuesOfRecords(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>... values) {
        return valuesOfRecords((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep12
    public final InsertImpl values(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> values) {
        return values(values.intoArray());
    }

    @Override // org.jooq.InsertValuesStep12
    public final InsertImpl valuesOfRecords(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>... values) {
        return valuesOfRecords((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep13
    public final InsertImpl values(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> values) {
        return values(values.intoArray());
    }

    @Override // org.jooq.InsertValuesStep13
    public final InsertImpl valuesOfRecords(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>... values) {
        return valuesOfRecords((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep14
    public final InsertImpl values(Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> values) {
        return values(values.intoArray());
    }

    @Override // org.jooq.InsertValuesStep14
    public final InsertImpl valuesOfRecords(Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>... values) {
        return valuesOfRecords((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep15
    public final InsertImpl values(Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> values) {
        return values(values.intoArray());
    }

    @Override // org.jooq.InsertValuesStep15
    public final InsertImpl valuesOfRecords(Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>... values) {
        return valuesOfRecords((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep16
    public final InsertImpl values(Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> values) {
        return values(values.intoArray());
    }

    @Override // org.jooq.InsertValuesStep16
    public final InsertImpl valuesOfRecords(Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>... values) {
        return valuesOfRecords((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep17
    public final InsertImpl values(Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> values) {
        return values(values.intoArray());
    }

    @Override // org.jooq.InsertValuesStep17
    public final InsertImpl valuesOfRecords(Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>... values) {
        return valuesOfRecords((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep18
    public final InsertImpl values(Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> values) {
        return values(values.intoArray());
    }

    @Override // org.jooq.InsertValuesStep18
    public final InsertImpl valuesOfRecords(Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>... values) {
        return valuesOfRecords((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep19
    public final InsertImpl values(Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> values) {
        return values(values.intoArray());
    }

    @Override // org.jooq.InsertValuesStep19
    public final InsertImpl valuesOfRecords(Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>... values) {
        return valuesOfRecords((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep20
    public final InsertImpl values(Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> values) {
        return values(values.intoArray());
    }

    @Override // org.jooq.InsertValuesStep20
    public final InsertImpl valuesOfRecords(Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>... values) {
        return valuesOfRecords((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep21
    public final InsertImpl values(Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> values) {
        return values(values.intoArray());
    }

    @Override // org.jooq.InsertValuesStep21
    public final InsertImpl valuesOfRecords(Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>... values) {
        return valuesOfRecords((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStep22
    public final InsertImpl values(Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> values) {
        return values(values.intoArray());
    }

    @Override // org.jooq.InsertValuesStep22
    public final InsertImpl valuesOfRecords(Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>... values) {
        return valuesOfRecords((Collection) Arrays.asList(values));
    }

    @Override // org.jooq.InsertValuesStepN
    public final InsertImpl valuesOfRows(Collection values) {
        for (Object row : values) {
            values(((Row) row).fields());
        }
        return this;
    }

    @Override // org.jooq.InsertValuesStepN
    public final InsertImpl valuesOfRecords(Collection values) {
        for (Object record : values) {
            values(((Record) record).intoArray());
        }
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public final InsertImpl values(Object... values) {
        if (values.length == 0) {
            return defaultValues();
        }
        if (!Tools.isEmpty(this.fields) && this.fields.length != values.length) {
            throw new IllegalArgumentException("The number of values must match the number of fields");
        }
        ((InsertQueryImpl) getDelegate()).newRecord();
        if (Tools.isEmpty(this.fields)) {
            for (int i = 0; i < values.length; i++) {
                addValue((InsertQueryImpl) getDelegate(), null, i, values[i]);
            }
        } else {
            for (int i2 = 0; i2 < this.fields.length; i2++) {
                addValue((InsertQueryImpl) getDelegate(), this.fields.length > 0 ? this.fields[i2] : 0, i2, values[i2]);
            }
        }
        return this;
    }

    @Override // org.jooq.InsertValuesStep1, org.jooq.InsertValuesStep2, org.jooq.InsertValuesStep3, org.jooq.InsertValuesStep4, org.jooq.InsertValuesStep5, org.jooq.InsertValuesStep6, org.jooq.InsertValuesStep7, org.jooq.InsertValuesStep8, org.jooq.InsertValuesStep9, org.jooq.InsertValuesStep10, org.jooq.InsertValuesStep11, org.jooq.InsertValuesStep12, org.jooq.InsertValuesStep13, org.jooq.InsertValuesStep14, org.jooq.InsertValuesStep15, org.jooq.InsertValuesStep16, org.jooq.InsertValuesStep17, org.jooq.InsertValuesStep18, org.jooq.InsertValuesStep19, org.jooq.InsertValuesStep20, org.jooq.InsertValuesStep21, org.jooq.InsertValuesStep22, org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public final InsertImpl values(Collection<?> values) {
        return values(values.toArray());
    }

    private final <T> void addValue(InsertQueryImpl<R> delegate, Field<T> field, int index, Object object) {
        if (object instanceof Field) {
            Field f = (Field) object;
            delegate.addValue((Field) field, index, f);
        } else if (object instanceof FieldLike) {
            FieldLike f2 = (FieldLike) object;
            delegate.addValue((Field) field, index, (Field) f2.asField());
        } else if (field != null) {
            delegate.addValue((Field<int>) field, index, (int) field.getDataType().convert(object));
        } else {
            delegate.addValue((Field<int>) field, index, (int) object);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep1
    public final InsertImpl values(Field<T1> value1) {
        return values((Field<?>[]) new Field[]{value1});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep2
    public final InsertImpl values(Field<T1> value1, Field<T2> value2) {
        return values((Field<?>[]) new Field[]{value1, value2});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep3
    public final InsertImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3) {
        return values((Field<?>[]) new Field[]{value1, value2, value3});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep4
    public final InsertImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep5
    public final InsertImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep6
    public final InsertImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep7
    public final InsertImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep8
    public final InsertImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep9
    public final InsertImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep10
    public final InsertImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep11
    public final InsertImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep12
    public final InsertImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11, Field<T12> value12) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep13
    public final InsertImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11, Field<T12> value12, Field<T13> value13) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep14
    public final InsertImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11, Field<T12> value12, Field<T13> value13, Field<T14> value14) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep15
    public final InsertImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11, Field<T12> value12, Field<T13> value13, Field<T14> value14, Field<T15> value15) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep16
    public final InsertImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11, Field<T12> value12, Field<T13> value13, Field<T14> value14, Field<T15> value15, Field<T16> value16) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep17
    public final InsertImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11, Field<T12> value12, Field<T13> value13, Field<T14> value14, Field<T15> value15, Field<T16> value16, Field<T17> value17) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep18
    public final InsertImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11, Field<T12> value12, Field<T13> value13, Field<T14> value14, Field<T15> value15, Field<T16> value16, Field<T17> value17, Field<T18> value18) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep19
    public final InsertImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11, Field<T12> value12, Field<T13> value13, Field<T14> value14, Field<T15> value15, Field<T16> value16, Field<T17> value17, Field<T18> value18, Field<T19> value19) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep20
    public final InsertImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11, Field<T12> value12, Field<T13> value13, Field<T14> value14, Field<T15> value15, Field<T16> value16, Field<T17> value17, Field<T18> value18, Field<T19> value19, Field<T20> value20) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19, value20});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep21
    public final InsertImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11, Field<T12> value12, Field<T13> value13, Field<T14> value14, Field<T15> value15, Field<T16> value16, Field<T17> value17, Field<T18> value18, Field<T19> value19, Field<T20> value20, Field<T21> value21) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19, value20, value21});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStep22
    public final InsertImpl values(Field<T1> value1, Field<T2> value2, Field<T3> value3, Field<T4> value4, Field<T5> value5, Field<T6> value6, Field<T7> value7, Field<T8> value8, Field<T9> value9, Field<T10> value10, Field<T11> value11, Field<T12> value12, Field<T13> value13, Field<T14> value14, Field<T15> value15, Field<T16> value16, Field<T17> value17, Field<T18> value18, Field<T19> value19, Field<T20> value20, Field<T21> value21, Field<T22> value22) {
        return values((Field<?>[]) new Field[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19, value20, value21, value22});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertValuesStepN, org.jooq.InsertSetStep
    public final InsertImpl values(Field<?>... values) {
        if (values.length == 0) {
            return defaultValues();
        }
        if (!Tools.isEmpty(this.fields) && this.fields.length != values.length) {
            throw new IllegalArgumentException("The number of values must match the number of fields");
        }
        ((InsertQueryImpl) getDelegate()).newRecord();
        if (Tools.isEmpty(this.fields)) {
            for (int i = 0; i < values.length; i++) {
                addValue((InsertQueryImpl) getDelegate(), (Field) 0, i, values[i]);
            }
        } else {
            for (int i2 = 0; i2 < this.fields.length; i2++) {
                addValue((InsertQueryImpl) getDelegate(), this.fields[i2], i2, values[i2]);
            }
        }
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep
    public final <T1> InsertImpl columns(Field<T1> field1) {
        return columns((Field<?>[]) new Field[]{field1});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep
    public final <T1, T2> InsertImpl columns(Field<T1> field1, Field<T2> field2) {
        return columns((Field<?>[]) new Field[]{field1, field2});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep
    public final <T1, T2, T3> InsertImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3) {
        return columns((Field<?>[]) new Field[]{field1, field2, field3});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep
    public final <T1, T2, T3, T4> InsertImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4) {
        return columns((Field<?>[]) new Field[]{field1, field2, field3, field4});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep
    public final <T1, T2, T3, T4, T5> InsertImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5) {
        return columns((Field<?>[]) new Field[]{field1, field2, field3, field4, field5});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep
    public final <T1, T2, T3, T4, T5, T6> InsertImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6) {
        return columns((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep
    public final <T1, T2, T3, T4, T5, T6, T7> InsertImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7) {
        return columns((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8> InsertImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8) {
        return columns((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9> InsertImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9) {
        return columns((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> InsertImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10) {
        return columns((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> InsertImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11) {
        return columns((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> InsertImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12) {
        return columns((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> InsertImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13) {
        return columns((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> InsertImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14) {
        return columns((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> InsertImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15) {
        return columns((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> InsertImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16) {
        return columns((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> InsertImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17) {
        return columns((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> InsertImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18) {
        return columns((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> InsertImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19) {
        return columns((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> InsertImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20) {
        return columns((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> InsertImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21) {
        return columns((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> InsertImpl columns(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22) {
        return columns((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22});
    }

    @Override // org.jooq.InsertSetStep
    public final InsertImpl columns(Field<?>... f) {
        this.fields = Tools.isEmpty(f) ? this.into.fields() : f;
        return this;
    }

    @Override // org.jooq.InsertSetStep
    public final InsertImpl columns(Collection<? extends Field<?>> f) {
        return columns((Field<?>[]) f.toArray(Tools.EMPTY_FIELD));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep
    public final InsertImpl defaultValues() {
        ((InsertQueryImpl) getDelegate()).setDefaultValues();
        return this;
    }

    @Override // org.jooq.InsertOnConflictDoUpdateStep
    public final InsertImpl doUpdate() {
        this.doUpdateWhere = true;
        return onDuplicateKeyUpdate();
    }

    @Override // org.jooq.InsertOnConflictDoUpdateStep
    public final InsertImpl doNothing() {
        this.doUpdateWhere = true;
        return onDuplicateKeyIgnore();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertOnDuplicateStep
    public final InsertImpl onConflictOnConstraint(Constraint constraint) {
        ((InsertQueryImpl) getDelegate()).onConflictOnConstraint(constraint);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertOnDuplicateStep
    public final InsertImpl onConflictOnConstraint(Name constraint) {
        ((InsertQueryImpl) getDelegate()).onConflictOnConstraint(constraint);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertOnDuplicateStep
    public final InsertImpl onConflictOnConstraint(UniqueKey<R> constraint) {
        ((InsertQueryImpl) getDelegate()).onConflictOnConstraint(constraint);
        return this;
    }

    @Override // org.jooq.InsertOnDuplicateStep
    public final InsertImpl onConflict(Field<?>... keys) {
        return onConflict((Collection<? extends Field<?>>) Arrays.asList(keys));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertOnDuplicateStep
    public final InsertImpl onConflict(Collection<? extends Field<?>> keys) {
        ((InsertQueryImpl) getDelegate()).onConflict(keys);
        return this;
    }

    @Override // org.jooq.InsertOnDuplicateStep
    public final InsertImpl onConflictDoNothing() {
        this.doUpdateWhere = true;
        onConflict(new Field[0]).doNothing();
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertOnDuplicateStep
    public final InsertImpl onDuplicateKeyUpdate() {
        this.doUpdateWhere = true;
        this.onDuplicateKeyUpdate = true;
        ((InsertQueryImpl) getDelegate()).onDuplicateKeyUpdate(true);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertOnDuplicateStep
    public final InsertImpl onDuplicateKeyIgnore() {
        this.doUpdateWhere = true;
        ((InsertQueryImpl) getDelegate()).onDuplicateKeyIgnore(true);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep, org.jooq.InsertSetMoreStep, org.jooq.InsertOnDuplicateSetStep
    public final <T> InsertImpl set(Field<T> field, T value) {
        if (this.onDuplicateKeyUpdate) {
            ((InsertQueryImpl) getDelegate()).addValueForUpdate((Field<Field<T>>) field, (Field<T>) value);
        } else {
            ((InsertQueryImpl) getDelegate()).addValue((Field<Field<T>>) field, (Field<T>) value);
        }
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertOnDuplicateSetStep
    public final <T> InsertImpl set(Field<T> field, Field<T> value) {
        if (this.onDuplicateKeyUpdate) {
            ((InsertQueryImpl) getDelegate()).addValueForUpdate((Field) field, (Field) value);
        } else {
            ((InsertQueryImpl) getDelegate()).addValue((Field) field, (Field) value);
        }
        return this;
    }

    @Override // org.jooq.InsertOnDuplicateSetStep
    public final <T> InsertImpl set(Field<T> field, Select<? extends Record1<T>> value) {
        return set((Field) field, (Field) value.asField());
    }

    @Override // org.jooq.InsertOnDuplicateSetStep
    public final <T> InsertImpl setNull(Field<T> field) {
        return set((Field<Field<T>>) field, (Field<T>) null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetStep, org.jooq.InsertSetMoreStep, org.jooq.InsertOnDuplicateSetStep
    public final InsertImpl set(Map<?, ?> map) {
        if (this.onDuplicateKeyUpdate) {
            ((InsertQueryImpl) getDelegate()).addValuesForUpdate(map);
        } else {
            ((InsertQueryImpl) getDelegate()).addValues(map);
        }
        return this;
    }

    @Override // org.jooq.InsertOnDuplicateSetStep
    public final InsertImpl set(Record record) {
        return set((Map<?, ?>) Tools.mapOfChangedValues(record));
    }

    @Override // org.jooq.InsertSetStep, org.jooq.InsertSetMoreStep
    public final InsertImpl set(Record... records) {
        return set((Collection<? extends Record>) Arrays.asList(records));
    }

    @Override // org.jooq.InsertSetStep, org.jooq.InsertSetMoreStep
    public final InsertImpl set(Collection<? extends Record> records) {
        for (Record record : records) {
            set(record).newRecord();
        }
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertOnDuplicateSetStep
    public final InsertImpl setAllToExcluded() {
        Iterator<Q> it = ((InsertQueryImpl) getDelegate()).$columns().iterator();
        while (it.hasNext()) {
            Field<T> field = (Field) it.next();
            set((Field) field, (Field) DSL.excluded(field));
        }
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertOnConflictConditionStep
    public final InsertImpl and(Condition condition) {
        ((InsertQueryImpl) getDelegate()).addConditions(condition);
        return this;
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public final InsertImpl and(Field<Boolean> condition) {
        return and(DSL.condition(condition));
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public final InsertImpl and(SQL sql) {
        return and(DSL.condition(sql));
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public final InsertImpl and(String sql) {
        return and(DSL.condition(sql));
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public final InsertImpl and(String sql, Object... bindings) {
        return and(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public final InsertImpl and(String sql, QueryPart... parts) {
        return and(DSL.condition(sql, parts));
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public final InsertImpl andNot(Condition condition) {
        return and(DSL.not(condition));
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public final InsertImpl andNot(Field<Boolean> condition) {
        return and(DSL.not(DSL.condition(condition)));
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public final InsertImpl andExists(Select<?> select) {
        return and(DSL.exists(select));
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public final InsertImpl andNotExists(Select<?> select) {
        return and(DSL.notExists(select));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertOnConflictConditionStep
    public final InsertImpl or(Condition condition) {
        ((InsertQueryImpl) getDelegate()).addConditions(Operator.OR, condition);
        return this;
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public final InsertImpl or(Field<Boolean> condition) {
        return or(DSL.condition(condition));
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public final InsertImpl or(SQL sql) {
        return or(DSL.condition(sql));
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public final InsertImpl or(String sql) {
        return or(DSL.condition(sql));
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public final InsertImpl or(String sql, Object... bindings) {
        return or(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public final InsertImpl or(String sql, QueryPart... parts) {
        return or(DSL.condition(sql, parts));
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public final InsertImpl orNot(Condition condition) {
        return or(DSL.not(condition));
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public final InsertImpl orNot(Field<Boolean> condition) {
        return or(DSL.not(DSL.condition(condition)));
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public final InsertImpl orExists(Select<?> select) {
        return or(DSL.exists(select));
    }

    @Override // org.jooq.InsertOnConflictConditionStep
    public final InsertImpl orNotExists(Select<?> select) {
        return or(DSL.notExists(select));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertOnConflictWhereIndexPredicateStep
    public final InsertImpl where(Condition condition) {
        if (this.doUpdateWhere) {
            ((InsertQueryImpl) getDelegate()).addConditions(condition);
        } else {
            ((InsertQueryImpl) getDelegate()).onConflictWhere(condition);
        }
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertOnConflictWhereIndexPredicateStep
    public final InsertImpl where(Condition... conditions) {
        if (this.doUpdateWhere) {
            ((InsertQueryImpl) getDelegate()).addConditions(conditions);
        } else {
            ((InsertQueryImpl) getDelegate()).onConflictWhere(DSL.and(conditions));
        }
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertOnConflictWhereStep, org.jooq.InsertOnConflictWhereIndexPredicateStep
    public final InsertImpl where(Collection<? extends Condition> conditions) {
        if (this.doUpdateWhere) {
            ((InsertQueryImpl) getDelegate()).addConditions(conditions);
        } else {
            ((InsertQueryImpl) getDelegate()).onConflictWhere(DSL.and(conditions));
        }
        return this;
    }

    @Override // org.jooq.InsertOnConflictWhereStep, org.jooq.InsertOnConflictWhereIndexPredicateStep
    public final InsertImpl where(Field<Boolean> field) {
        return where(DSL.condition(field));
    }

    @Override // org.jooq.InsertOnConflictWhereIndexPredicateStep
    public final InsertImpl where(SQL sql) {
        return where(DSL.condition(sql));
    }

    @Override // org.jooq.InsertOnConflictWhereIndexPredicateStep
    public final InsertImpl where(String sql) {
        return where(DSL.condition(sql));
    }

    @Override // org.jooq.InsertOnConflictWhereIndexPredicateStep
    public final InsertImpl where(String sql, Object... bindings) {
        return where(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.InsertOnConflictWhereIndexPredicateStep
    public final InsertImpl where(String sql, QueryPart... parts) {
        return where(DSL.condition(sql, parts));
    }

    @Override // org.jooq.InsertOnConflictWhereStep
    public final InsertImpl whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    @Override // org.jooq.InsertOnConflictWhereStep
    public final InsertImpl whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertSetMoreStep
    public final InsertImpl newRecord() {
        ((InsertQueryImpl) getDelegate()).newRecord();
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertReturningStep
    public final InsertResultStep<R> returning() {
        ((InsertQueryImpl) getDelegate()).setReturning();
        return new InsertAsResultQuery((InsertQueryImpl) getDelegate(), this.returningResult);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertReturningStep
    public final InsertResultStep<R> returning(SelectFieldOrAsterisk... f) {
        ((InsertQueryImpl) getDelegate()).setReturning(f);
        return new InsertAsResultQuery((InsertQueryImpl) getDelegate(), this.returningResult);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertReturningStep
    public final InsertResultStep<R> returning(Collection<? extends SelectFieldOrAsterisk> f) {
        ((InsertQueryImpl) getDelegate()).setReturning(f);
        return new InsertAsResultQuery((InsertQueryImpl) getDelegate(), this.returningResult);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertReturningStep
    public final InsertResultStep<Record> returningResult(SelectFieldOrAsterisk... f) {
        this.returningResult = true;
        ((InsertQueryImpl) getDelegate()).setReturning(f);
        return new InsertAsResultQuery((InsertQueryImpl) getDelegate(), this.returningResult);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InsertReturningStep
    public final InsertResultStep<Record> returningResult(Collection<? extends SelectFieldOrAsterisk> f) {
        this.returningResult = true;
        ((InsertQueryImpl) getDelegate()).setReturning(f);
        return new InsertAsResultQuery((InsertQueryImpl) getDelegate(), this.returningResult);
    }

    @Override // org.jooq.InsertReturningStep
    public final <T1> InsertResultStep<Record1<T1>> returningResult(SelectField<T1> selectField) {
        return (InsertResultStep<Record1<T1>>) returningResult(selectField);
    }

    @Override // org.jooq.InsertReturningStep
    public final <T1, T2> InsertResultStep<Record2<T1, T2>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2) {
        return (InsertResultStep<Record2<T1, T2>>) returningResult(selectField, selectField2);
    }

    @Override // org.jooq.InsertReturningStep
    public final <T1, T2, T3> InsertResultStep<Record3<T1, T2, T3>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3) {
        return (InsertResultStep<Record3<T1, T2, T3>>) returningResult(selectField, selectField2, selectField3);
    }

    @Override // org.jooq.InsertReturningStep
    public final <T1, T2, T3, T4> InsertResultStep<Record4<T1, T2, T3, T4>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4) {
        return (InsertResultStep<Record4<T1, T2, T3, T4>>) returningResult(selectField, selectField2, selectField3, selectField4);
    }

    @Override // org.jooq.InsertReturningStep
    public final <T1, T2, T3, T4, T5> InsertResultStep<Record5<T1, T2, T3, T4, T5>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5) {
        return (InsertResultStep<Record5<T1, T2, T3, T4, T5>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5);
    }

    @Override // org.jooq.InsertReturningStep
    public final <T1, T2, T3, T4, T5, T6> InsertResultStep<Record6<T1, T2, T3, T4, T5, T6>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6) {
        return (InsertResultStep<Record6<T1, T2, T3, T4, T5, T6>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6);
    }

    @Override // org.jooq.InsertReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7> InsertResultStep<Record7<T1, T2, T3, T4, T5, T6, T7>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7) {
        return (InsertResultStep<Record7<T1, T2, T3, T4, T5, T6, T7>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7);
    }

    @Override // org.jooq.InsertReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8> InsertResultStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8) {
        return (InsertResultStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8);
    }

    @Override // org.jooq.InsertReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9> InsertResultStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9) {
        return (InsertResultStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9);
    }

    @Override // org.jooq.InsertReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> InsertResultStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10) {
        return (InsertResultStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10);
    }

    @Override // org.jooq.InsertReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> InsertResultStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11) {
        return (InsertResultStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11);
    }

    @Override // org.jooq.InsertReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> InsertResultStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12) {
        return (InsertResultStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12);
    }

    @Override // org.jooq.InsertReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> InsertResultStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13) {
        return (InsertResultStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13);
    }

    @Override // org.jooq.InsertReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> InsertResultStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14) {
        return (InsertResultStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14);
    }

    @Override // org.jooq.InsertReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> InsertResultStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15) {
        return (InsertResultStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15);
    }

    @Override // org.jooq.InsertReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> InsertResultStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16) {
        return (InsertResultStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16);
    }

    @Override // org.jooq.InsertReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> InsertResultStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17) {
        return (InsertResultStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17);
    }

    @Override // org.jooq.InsertReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> InsertResultStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18) {
        return (InsertResultStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18);
    }

    @Override // org.jooq.InsertReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> InsertResultStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19) {
        return (InsertResultStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19);
    }

    @Override // org.jooq.InsertReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> InsertResultStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20) {
        return (InsertResultStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20);
    }

    @Override // org.jooq.InsertReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> InsertResultStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21) {
        return (InsertResultStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20, selectField21);
    }

    @Override // org.jooq.InsertReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> InsertResultStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21, SelectField<T22> selectField22) {
        return (InsertResultStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20, selectField21, selectField22);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public final WithImpl $with() {
        return ((InsertQueryImpl) getDelegate()).$with();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public final Table<R> $into() {
        return ((InsertQueryImpl) getDelegate()).$into();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public final QOM.Insert<?> $into(Table<?> newInto) {
        return ((InsertQueryImpl) getDelegate()).$into(newInto);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public final QOM.UnmodifiableList<? extends Field<?>> $columns() {
        return ((InsertQueryImpl) getDelegate()).$columns();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public final QOM.Insert<?> $columns(Collection<? extends Field<?>> columns) {
        return ((InsertQueryImpl) getDelegate()).$columns(columns);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public final Select<?> $select() {
        return ((InsertQueryImpl) getDelegate()).$select();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public final QOM.Insert<?> $select(Select<?> select) {
        return ((InsertQueryImpl) getDelegate()).$select(select);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public final boolean $defaultValues() {
        return ((InsertQueryImpl) getDelegate()).$defaultValues();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public final QOM.Insert<?> $defaultValues(boolean defaultValues) {
        return ((InsertQueryImpl) getDelegate()).$defaultValues(defaultValues);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public final QOM.UnmodifiableList<? extends Row> $values() {
        return ((InsertQueryImpl) getDelegate()).$values();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public final QOM.Insert<?> $values(Collection<? extends Row> values) {
        return ((InsertQueryImpl) getDelegate()).$values(values);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public final boolean $onDuplicateKeyIgnore() {
        return ((InsertQueryImpl) getDelegate()).$onDuplicateKeyIgnore();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public final QOM.Insert<?> $onDuplicateKeyIgnore(boolean onDuplicateKeyIgnore) {
        return ((InsertQueryImpl) getDelegate()).$onDuplicateKeyIgnore(onDuplicateKeyIgnore);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public boolean $onDuplicateKeyUpdate() {
        return ((InsertQueryImpl) getDelegate()).$onDuplicateKeyUpdate();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public final QOM.Insert<?> $onDuplicateKeyUpdate(boolean onDuplicateKeyUpdate) {
        return ((InsertQueryImpl) getDelegate()).$onDuplicateKeyUpdate(onDuplicateKeyUpdate);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public final QOM.UnmodifiableList<? extends Field<?>> $onConflict() {
        return ((InsertQueryImpl) getDelegate()).$onConflict();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public final QOM.Insert<?> $onConflict(Collection<? extends Field<?>> onConflictFields) {
        return ((InsertQueryImpl) getDelegate()).$onConflict(onConflictFields);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public final Condition $onConflictWhere() {
        return ((InsertQueryImpl) getDelegate()).$onConflictWhere();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public final QOM.Insert<?> $onConflictWhere(Condition where) {
        return ((InsertQueryImpl) getDelegate()).$onConflictWhere(where);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public final QOM.UnmodifiableMap<? extends FieldOrRow, ? extends FieldOrRowOrSelect> $updateSet() {
        return ((InsertQueryImpl) getDelegate()).$updateSet();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public final QOM.Insert<?> $updateSet(Map<? extends FieldOrRow, ? extends FieldOrRowOrSelect> updateSet) {
        return ((InsertQueryImpl) getDelegate()).$updateSet(updateSet);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public final Condition $updateWhere() {
        return ((InsertQueryImpl) getDelegate()).$updateWhere();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public final QOM.Insert<?> $updateWhere(Condition where) {
        return ((InsertQueryImpl) getDelegate()).$updateWhere(where);
    }
}
