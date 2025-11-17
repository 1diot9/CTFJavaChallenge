package org.jooq.impl;

import java.util.Set;
import org.jooq.BetweenAndStep1;
import org.jooq.BetweenAndStep10;
import org.jooq.BetweenAndStep11;
import org.jooq.BetweenAndStep12;
import org.jooq.BetweenAndStep13;
import org.jooq.BetweenAndStep14;
import org.jooq.BetweenAndStep15;
import org.jooq.BetweenAndStep16;
import org.jooq.BetweenAndStep17;
import org.jooq.BetweenAndStep18;
import org.jooq.BetweenAndStep19;
import org.jooq.BetweenAndStep2;
import org.jooq.BetweenAndStep20;
import org.jooq.BetweenAndStep21;
import org.jooq.BetweenAndStep22;
import org.jooq.BetweenAndStep3;
import org.jooq.BetweenAndStep4;
import org.jooq.BetweenAndStep5;
import org.jooq.BetweenAndStep6;
import org.jooq.BetweenAndStep7;
import org.jooq.BetweenAndStep8;
import org.jooq.BetweenAndStep9;
import org.jooq.BetweenAndStepN;
import org.jooq.Clause;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Field;
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
import org.jooq.SQLDialect;
import org.jooq.SelectField;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RowBetweenCondition.class */
public final class RowBetweenCondition<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> extends AbstractCondition implements BetweenAndStep1<T1>, BetweenAndStep2<T1, T2>, BetweenAndStep3<T1, T2, T3>, BetweenAndStep4<T1, T2, T3, T4>, BetweenAndStep5<T1, T2, T3, T4, T5>, BetweenAndStep6<T1, T2, T3, T4, T5, T6>, BetweenAndStep7<T1, T2, T3, T4, T5, T6, T7>, BetweenAndStep8<T1, T2, T3, T4, T5, T6, T7, T8>, BetweenAndStep9<T1, T2, T3, T4, T5, T6, T7, T8, T9>, BetweenAndStep10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>, BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>, BetweenAndStep12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>, BetweenAndStep13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>, BetweenAndStep14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>, BetweenAndStep15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>, BetweenAndStep16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>, BetweenAndStep17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>, BetweenAndStep18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>, BetweenAndStep19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>, BetweenAndStep20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>, BetweenAndStep21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>, BetweenAndStep22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>, BetweenAndStepN, QOM.UNotYetImplemented {
    private static final Clause[] CLAUSES_BETWEEN = {Clause.CONDITION, Clause.CONDITION_BETWEEN};
    private static final Clause[] CLAUSES_BETWEEN_SYMMETRIC = {Clause.CONDITION, Clause.CONDITION_BETWEEN_SYMMETRIC};
    private static final Clause[] CLAUSES_NOT_BETWEEN = {Clause.CONDITION, Clause.CONDITION_NOT_BETWEEN};
    private static final Clause[] CLAUSES_NOT_BETWEEN_SYMMETRIC = {Clause.CONDITION, Clause.CONDITION_NOT_BETWEEN_SYMMETRIC};
    private static final Set<SQLDialect> NO_SUPPORT_SYMMETRIC = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE, SQLDialect.TRINO);
    private static final Set<SQLDialect> EMULATE_BETWEEN = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL);
    private final boolean symmetric;
    private final boolean not;
    private final Row row;
    private final Row minValue;
    private Row maxValue;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RowBetweenCondition(Row row, Row minValue, boolean not, boolean symmetric) {
        this.row = row;
        this.minValue = minValue;
        this.not = not;
        this.symmetric = symmetric;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RowBetweenCondition(Row row, Row minValue, boolean not, boolean symmetric, Row maxValue) {
        this(row, minValue, not, symmetric);
        this.maxValue = maxValue;
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.Condition
    public final Condition and(Field f) {
        if (this.maxValue == null) {
            return and((Row1) DSL.row((SelectField) f));
        }
        return super.and((Field<Boolean>) f);
    }

    @Override // org.jooq.BetweenAndStep2
    public final Condition and(Field<T1> t1, Field<T2> t2) {
        return and((Row2) DSL.row((SelectField) t1, (SelectField) t2));
    }

    @Override // org.jooq.BetweenAndStep3
    public final Condition and(Field<T1> t1, Field<T2> t2, Field<T3> t3) {
        return and((Row3) DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3));
    }

    @Override // org.jooq.BetweenAndStep4
    public final Condition and(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4) {
        return and((Row4) DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4));
    }

    @Override // org.jooq.BetweenAndStep5
    public final Condition and(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5) {
        return and((Row5) DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5));
    }

    @Override // org.jooq.BetweenAndStep6
    public final Condition and(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6) {
        return and((Row6) DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5, (SelectField) t6));
    }

    @Override // org.jooq.BetweenAndStep7
    public final Condition and(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7) {
        return and((Row7) DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5, (SelectField) t6, (SelectField) t7));
    }

    @Override // org.jooq.BetweenAndStep8
    public final Condition and(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8) {
        return and((Row8) DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5, (SelectField) t6, (SelectField) t7, (SelectField) t8));
    }

    @Override // org.jooq.BetweenAndStep9
    public final Condition and(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9) {
        return and((Row9) DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5, (SelectField) t6, (SelectField) t7, (SelectField) t8, (SelectField) t9));
    }

    @Override // org.jooq.BetweenAndStep10
    public final Condition and(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10) {
        return and((Row10) DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5, (SelectField) t6, (SelectField) t7, (SelectField) t8, (SelectField) t9, (SelectField) t10));
    }

    @Override // org.jooq.BetweenAndStep11
    public final Condition and(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return and((Row11) DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5, (SelectField) t6, (SelectField) t7, (SelectField) t8, (SelectField) t9, (SelectField) t10, (SelectField) t11));
    }

    @Override // org.jooq.BetweenAndStep12
    public final Condition and(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12) {
        return and((Row12) DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5, (SelectField) t6, (SelectField) t7, (SelectField) t8, (SelectField) t9, (SelectField) t10, (SelectField) t11, (SelectField) t12));
    }

    @Override // org.jooq.BetweenAndStep13
    public final Condition and(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13) {
        return and((Row13) DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5, (SelectField) t6, (SelectField) t7, (SelectField) t8, (SelectField) t9, (SelectField) t10, (SelectField) t11, (SelectField) t12, (SelectField) t13));
    }

    @Override // org.jooq.BetweenAndStep14
    public final Condition and(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13, Field<T14> t14) {
        return and((Row14) DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5, (SelectField) t6, (SelectField) t7, (SelectField) t8, (SelectField) t9, (SelectField) t10, (SelectField) t11, (SelectField) t12, (SelectField) t13, (SelectField) t14));
    }

    @Override // org.jooq.BetweenAndStep15
    public final Condition and(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13, Field<T14> t14, Field<T15> t15) {
        return and((Row15) DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5, (SelectField) t6, (SelectField) t7, (SelectField) t8, (SelectField) t9, (SelectField) t10, (SelectField) t11, (SelectField) t12, (SelectField) t13, (SelectField) t14, (SelectField) t15));
    }

    @Override // org.jooq.BetweenAndStep16
    public final Condition and(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13, Field<T14> t14, Field<T15> t15, Field<T16> t16) {
        return and((Row16) DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5, (SelectField) t6, (SelectField) t7, (SelectField) t8, (SelectField) t9, (SelectField) t10, (SelectField) t11, (SelectField) t12, (SelectField) t13, (SelectField) t14, (SelectField) t15, (SelectField) t16));
    }

    @Override // org.jooq.BetweenAndStep17
    public final Condition and(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13, Field<T14> t14, Field<T15> t15, Field<T16> t16, Field<T17> t17) {
        return and((Row17) DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5, (SelectField) t6, (SelectField) t7, (SelectField) t8, (SelectField) t9, (SelectField) t10, (SelectField) t11, (SelectField) t12, (SelectField) t13, (SelectField) t14, (SelectField) t15, (SelectField) t16, (SelectField) t17));
    }

    @Override // org.jooq.BetweenAndStep18
    public final Condition and(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13, Field<T14> t14, Field<T15> t15, Field<T16> t16, Field<T17> t17, Field<T18> t18) {
        return and((Row18) DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5, (SelectField) t6, (SelectField) t7, (SelectField) t8, (SelectField) t9, (SelectField) t10, (SelectField) t11, (SelectField) t12, (SelectField) t13, (SelectField) t14, (SelectField) t15, (SelectField) t16, (SelectField) t17, (SelectField) t18));
    }

    @Override // org.jooq.BetweenAndStep19
    public final Condition and(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13, Field<T14> t14, Field<T15> t15, Field<T16> t16, Field<T17> t17, Field<T18> t18, Field<T19> t19) {
        return and((Row19) DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5, (SelectField) t6, (SelectField) t7, (SelectField) t8, (SelectField) t9, (SelectField) t10, (SelectField) t11, (SelectField) t12, (SelectField) t13, (SelectField) t14, (SelectField) t15, (SelectField) t16, (SelectField) t17, (SelectField) t18, (SelectField) t19));
    }

    @Override // org.jooq.BetweenAndStep20
    public final Condition and(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13, Field<T14> t14, Field<T15> t15, Field<T16> t16, Field<T17> t17, Field<T18> t18, Field<T19> t19, Field<T20> t20) {
        return and((Row20) DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5, (SelectField) t6, (SelectField) t7, (SelectField) t8, (SelectField) t9, (SelectField) t10, (SelectField) t11, (SelectField) t12, (SelectField) t13, (SelectField) t14, (SelectField) t15, (SelectField) t16, (SelectField) t17, (SelectField) t18, (SelectField) t19, (SelectField) t20));
    }

    @Override // org.jooq.BetweenAndStep21
    public final Condition and(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13, Field<T14> t14, Field<T15> t15, Field<T16> t16, Field<T17> t17, Field<T18> t18, Field<T19> t19, Field<T20> t20, Field<T21> t21) {
        return and((Row21) DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5, (SelectField) t6, (SelectField) t7, (SelectField) t8, (SelectField) t9, (SelectField) t10, (SelectField) t11, (SelectField) t12, (SelectField) t13, (SelectField) t14, (SelectField) t15, (SelectField) t16, (SelectField) t17, (SelectField) t18, (SelectField) t19, (SelectField) t20, (SelectField) t21));
    }

    @Override // org.jooq.BetweenAndStep22
    public final Condition and(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11, Field<T12> t12, Field<T13> t13, Field<T14> t14, Field<T15> t15, Field<T16> t16, Field<T17> t17, Field<T18> t18, Field<T19> t19, Field<T20> t20, Field<T21> t21, Field<T22> t22) {
        return and((Row22) DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5, (SelectField) t6, (SelectField) t7, (SelectField) t8, (SelectField) t9, (SelectField) t10, (SelectField) t11, (SelectField) t12, (SelectField) t13, (SelectField) t14, (SelectField) t15, (SelectField) t16, (SelectField) t17, (SelectField) t18, (SelectField) t19, (SelectField) t20, (SelectField) t21, (SelectField) t22));
    }

    @Override // org.jooq.BetweenAndStepN
    public final Condition and(Field<?>... fields) {
        return and(DSL.row((SelectField<?>[]) fields));
    }

    @Override // org.jooq.BetweenAndStep1
    public final Condition and(T1 t1) {
        return and(t1);
    }

    @Override // org.jooq.BetweenAndStep2
    public final Condition and(T1 t1, T2 t2) {
        return and(t1, t2);
    }

    @Override // org.jooq.BetweenAndStep3
    public final Condition and(T1 t1, T2 t2, T3 t3) {
        return and(t1, t2, t3);
    }

    @Override // org.jooq.BetweenAndStep4
    public final Condition and(T1 t1, T2 t2, T3 t3, T4 t4) {
        return and(t1, t2, t3, t4);
    }

    @Override // org.jooq.BetweenAndStep5
    public final Condition and(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return and(t1, t2, t3, t4, t5);
    }

    @Override // org.jooq.BetweenAndStep6
    public final Condition and(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) {
        return and(t1, t2, t3, t4, t5, t6);
    }

    @Override // org.jooq.BetweenAndStep7
    public final Condition and(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7) {
        return and(t1, t2, t3, t4, t5, t6, t7);
    }

    @Override // org.jooq.BetweenAndStep8
    public final Condition and(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8) {
        return and(t1, t2, t3, t4, t5, t6, t7, t8);
    }

    @Override // org.jooq.BetweenAndStep9
    public final Condition and(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9) {
        return and(t1, t2, t3, t4, t5, t6, t7, t8, t9);
    }

    @Override // org.jooq.BetweenAndStep10
    public final Condition and(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10) {
        return and(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10);
    }

    @Override // org.jooq.BetweenAndStep11
    public final Condition and(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return and(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override // org.jooq.BetweenAndStep12
    public final Condition and(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12) {
        return and(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12);
    }

    @Override // org.jooq.BetweenAndStep13
    public final Condition and(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        return and(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }

    @Override // org.jooq.BetweenAndStep14
    public final Condition and(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14) {
        return and(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14);
    }

    @Override // org.jooq.BetweenAndStep15
    public final Condition and(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15) {
        return and(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15);
    }

    @Override // org.jooq.BetweenAndStep16
    public final Condition and(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16) {
        return and(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16);
    }

    @Override // org.jooq.BetweenAndStep17
    public final Condition and(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17) {
        return and(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17);
    }

    @Override // org.jooq.BetweenAndStep18
    public final Condition and(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18) {
        return and(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18);
    }

    @Override // org.jooq.BetweenAndStep19
    public final Condition and(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18, T19 t19) {
        return and(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19);
    }

    @Override // org.jooq.BetweenAndStep20
    public final Condition and(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18, T19 t19, T20 t20) {
        return and(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20);
    }

    @Override // org.jooq.BetweenAndStep21
    public final Condition and(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18, T19 t19, T20 t20, T21 t21) {
        return and(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21);
    }

    @Override // org.jooq.BetweenAndStep22
    public final Condition and(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18, T19 t19, T20 t20, T21 t21, T22 t22) {
        return and(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22);
    }

    @Override // org.jooq.BetweenAndStepN
    public final Condition and(Object... values) {
        return and(DSL.row((SelectField<?>[]) Tools.fieldsArray(values, this.row.fields())));
    }

    @Override // org.jooq.BetweenAndStep1
    public final Condition and(Row1<T1> r) {
        this.maxValue = r;
        return this;
    }

    @Override // org.jooq.BetweenAndStep2
    public final Condition and(Row2<T1, T2> r) {
        this.maxValue = r;
        return this;
    }

    @Override // org.jooq.BetweenAndStep3
    public final Condition and(Row3<T1, T2, T3> r) {
        this.maxValue = r;
        return this;
    }

    @Override // org.jooq.BetweenAndStep4
    public final Condition and(Row4<T1, T2, T3, T4> r) {
        this.maxValue = r;
        return this;
    }

    @Override // org.jooq.BetweenAndStep5
    public final Condition and(Row5<T1, T2, T3, T4, T5> r) {
        this.maxValue = r;
        return this;
    }

    @Override // org.jooq.BetweenAndStep6
    public final Condition and(Row6<T1, T2, T3, T4, T5, T6> r) {
        this.maxValue = r;
        return this;
    }

    @Override // org.jooq.BetweenAndStep7
    public final Condition and(Row7<T1, T2, T3, T4, T5, T6, T7> r) {
        this.maxValue = r;
        return this;
    }

    @Override // org.jooq.BetweenAndStep8
    public final Condition and(Row8<T1, T2, T3, T4, T5, T6, T7, T8> r) {
        this.maxValue = r;
        return this;
    }

    @Override // org.jooq.BetweenAndStep9
    public final Condition and(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> r) {
        this.maxValue = r;
        return this;
    }

    @Override // org.jooq.BetweenAndStep10
    public final Condition and(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> r) {
        this.maxValue = r;
        return this;
    }

    @Override // org.jooq.BetweenAndStep11
    public final Condition and(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> r) {
        this.maxValue = r;
        return this;
    }

    @Override // org.jooq.BetweenAndStep12
    public final Condition and(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> r) {
        this.maxValue = r;
        return this;
    }

    @Override // org.jooq.BetweenAndStep13
    public final Condition and(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> r) {
        this.maxValue = r;
        return this;
    }

    @Override // org.jooq.BetweenAndStep14
    public final Condition and(Row14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> r) {
        this.maxValue = r;
        return this;
    }

    @Override // org.jooq.BetweenAndStep15
    public final Condition and(Row15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> r) {
        this.maxValue = r;
        return this;
    }

    @Override // org.jooq.BetweenAndStep16
    public final Condition and(Row16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> r) {
        this.maxValue = r;
        return this;
    }

    @Override // org.jooq.BetweenAndStep17
    public final Condition and(Row17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> r) {
        this.maxValue = r;
        return this;
    }

    @Override // org.jooq.BetweenAndStep18
    public final Condition and(Row18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> r) {
        this.maxValue = r;
        return this;
    }

    @Override // org.jooq.BetweenAndStep19
    public final Condition and(Row19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> r) {
        this.maxValue = r;
        return this;
    }

    @Override // org.jooq.BetweenAndStep20
    public final Condition and(Row20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> r) {
        this.maxValue = r;
        return this;
    }

    @Override // org.jooq.BetweenAndStep21
    public final Condition and(Row21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> r) {
        this.maxValue = r;
        return this;
    }

    @Override // org.jooq.BetweenAndStep22
    public final Condition and(Row22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> r) {
        this.maxValue = r;
        return this;
    }

    @Override // org.jooq.BetweenAndStepN
    public final Condition and(RowN r) {
        this.maxValue = r;
        return this;
    }

    @Override // org.jooq.BetweenAndStep1
    public final Condition and(Record1<T1> record) {
        return and((Row1) record.valuesRow());
    }

    @Override // org.jooq.BetweenAndStep2
    public final Condition and(Record2<T1, T2> record) {
        return and((Row2) record.valuesRow());
    }

    @Override // org.jooq.BetweenAndStep3
    public final Condition and(Record3<T1, T2, T3> record) {
        return and((Row3) record.valuesRow());
    }

    @Override // org.jooq.BetweenAndStep4
    public final Condition and(Record4<T1, T2, T3, T4> record) {
        return and((Row4) record.valuesRow());
    }

    @Override // org.jooq.BetweenAndStep5
    public final Condition and(Record5<T1, T2, T3, T4, T5> record) {
        return and((Row5) record.valuesRow());
    }

    @Override // org.jooq.BetweenAndStep6
    public final Condition and(Record6<T1, T2, T3, T4, T5, T6> record) {
        return and((Row6) record.valuesRow());
    }

    @Override // org.jooq.BetweenAndStep7
    public final Condition and(Record7<T1, T2, T3, T4, T5, T6, T7> record) {
        return and((Row7) record.valuesRow());
    }

    @Override // org.jooq.BetweenAndStep8
    public final Condition and(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record) {
        return and((Row8) record.valuesRow());
    }

    @Override // org.jooq.BetweenAndStep9
    public final Condition and(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record) {
        return and((Row9) record.valuesRow());
    }

    @Override // org.jooq.BetweenAndStep10
    public final Condition and(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record) {
        return and((Row10) record.valuesRow());
    }

    @Override // org.jooq.BetweenAndStep11
    public final Condition and(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return and((Row11) record.valuesRow());
    }

    @Override // org.jooq.BetweenAndStep12
    public final Condition and(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record) {
        return and((Row12) record.valuesRow());
    }

    @Override // org.jooq.BetweenAndStep13
    public final Condition and(Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> record) {
        return and((Row13) record.valuesRow());
    }

    @Override // org.jooq.BetweenAndStep14
    public final Condition and(Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> record) {
        return and((Row14) record.valuesRow());
    }

    @Override // org.jooq.BetweenAndStep15
    public final Condition and(Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> record) {
        return and((Row15) record.valuesRow());
    }

    @Override // org.jooq.BetweenAndStep16
    public final Condition and(Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> record) {
        return and((Row16) record.valuesRow());
    }

    @Override // org.jooq.BetweenAndStep17
    public final Condition and(Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> record) {
        return and((Row17) record.valuesRow());
    }

    @Override // org.jooq.BetweenAndStep18
    public final Condition and(Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> record) {
        return and((Row18) record.valuesRow());
    }

    @Override // org.jooq.BetweenAndStep19
    public final Condition and(Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> record) {
        return and((Row19) record.valuesRow());
    }

    @Override // org.jooq.BetweenAndStep20
    public final Condition and(Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> record) {
        return and((Row20) record.valuesRow());
    }

    @Override // org.jooq.BetweenAndStep21
    public final Condition and(Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> record) {
        return and((Row21) record.valuesRow());
    }

    @Override // org.jooq.BetweenAndStep22
    public final Condition and(Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> record) {
        return and((Row22) record.valuesRow());
    }

    @Override // org.jooq.BetweenAndStepN
    public final Condition and(Record record) {
        return and((RowN) new RowImplN(Tools.fieldsArray(record.intoArray(), record.fields())));
    }

    /* JADX WARN: Type inference failed for: r0v10, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v15, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v18, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v21, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v24, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v27, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        Condition or;
        if (this.symmetric && NO_SUPPORT_SYMMETRIC.contains(ctx.dialect())) {
            if (this.not) {
                or = new RowBetweenCondition(this.row, this.minValue, true, false, this.maxValue).and((Condition) new RowBetweenCondition(this.row, this.maxValue, true, false, this.minValue));
            } else {
                or = new RowBetweenCondition(this.row, this.minValue, false, false, this.maxValue).or((Condition) new RowBetweenCondition(this.row, this.maxValue, false, false, this.minValue));
            }
            ctx.visit(or);
            return;
        }
        if (this.row.size() > 1 && EMULATE_BETWEEN.contains(ctx.dialect())) {
            Condition result = AbstractRow.compare(this.row, Comparator.GREATER_OR_EQUAL, this.minValue).and(AbstractRow.compare(this.row, Comparator.LESS_OR_EQUAL, this.maxValue));
            if (this.not) {
                result = result.not();
            }
            ctx.visit(result);
            return;
        }
        ctx.visit(this.row);
        if (this.not) {
            ctx.sql(" ").visit(Keywords.K_NOT);
        }
        ctx.sql(" ").visit(Keywords.K_BETWEEN);
        if (this.symmetric) {
            ctx.sql(" ").visit(Keywords.K_SYMMETRIC);
        }
        ctx.sql(" ").visit(this.minValue);
        ctx.sql(" ").visit(Keywords.K_AND);
        ctx.sql(" ").visit(this.maxValue);
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return this.not ? this.symmetric ? CLAUSES_NOT_BETWEEN_SYMMETRIC : CLAUSES_NOT_BETWEEN : this.symmetric ? CLAUSES_BETWEEN_SYMMETRIC : CLAUSES_BETWEEN;
    }
}
