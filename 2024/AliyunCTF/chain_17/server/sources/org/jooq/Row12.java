package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Row12.class */
public interface Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> extends Row, SelectField<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> {
    @NotNull
    <U> SelectField<U> mapping(Function12<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? super T10, ? super T11, ? super T12, ? extends U> function12);

    @NotNull
    <U> SelectField<U> mapping(Class<U> cls, Function12<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? super T10, ? super T11, ? super T12, ? extends U> function12);

    @NotNull
    Field<T1> field1();

    @NotNull
    Field<T2> field2();

    @NotNull
    Field<T3> field3();

    @NotNull
    Field<T4> field4();

    @NotNull
    Field<T5> field5();

    @NotNull
    Field<T6> field6();

    @NotNull
    Field<T7> field7();

    @NotNull
    Field<T8> field8();

    @NotNull
    Field<T9> field9();

    @NotNull
    Field<T10> field10();

    @NotNull
    Field<T11> field11();

    @NotNull
    Field<T12> field12();

    @Support
    @NotNull
    Condition compare(Comparator comparator, Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12);

    @Support
    @NotNull
    Condition compare(Comparator comparator, T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Select<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select);

    @Support
    @NotNull
    Condition compare(Comparator comparator, QuantifiedSelect<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> quantifiedSelect);

    @Support
    @NotNull
    Condition equal(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12);

    @Support
    @NotNull
    Condition equal(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12);

    @Support
    @NotNull
    Condition equal(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12);

    @Support
    @NotNull
    Condition equal(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support
    @NotNull
    Condition equal(Select<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select);

    @Support
    @NotNull
    Condition equal(QuantifiedSelect<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> quantifiedSelect);

    @Support
    @NotNull
    Condition eq(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12);

    @Support
    @NotNull
    Condition eq(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12);

    @Support
    @NotNull
    Condition eq(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12);

    @Support
    @NotNull
    Condition eq(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support
    @NotNull
    Condition eq(Select<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select);

    @Support
    @NotNull
    Condition eq(QuantifiedSelect<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> quantifiedSelect);

    @Support
    @NotNull
    Condition notEqual(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12);

    @Support
    @NotNull
    Condition notEqual(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12);

    @Support
    @NotNull
    Condition notEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12);

    @Support
    @NotNull
    Condition notEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support
    @NotNull
    Condition notEqual(Select<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select);

    @Support
    @NotNull
    Condition notEqual(QuantifiedSelect<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> quantifiedSelect);

    @Support
    @NotNull
    Condition ne(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12);

    @Support
    @NotNull
    Condition ne(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12);

    @Support
    @NotNull
    Condition ne(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12);

    @Support
    @NotNull
    Condition ne(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support
    @NotNull
    Condition ne(Select<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select);

    @Support
    @NotNull
    Condition ne(QuantifiedSelect<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> quantifiedSelect);

    @Support
    @NotNull
    Condition isDistinctFrom(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12);

    @Support
    @NotNull
    Condition isDistinctFrom(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12);

    @Support
    @NotNull
    Condition isDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12);

    @Support
    @NotNull
    Condition isDistinctFrom(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support
    @NotNull
    Condition isDistinctFrom(Select<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12);

    @Support
    @NotNull
    Condition isNotDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Select<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select);

    @Support
    @NotNull
    Condition lessThan(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12);

    @Support
    @NotNull
    Condition lessThan(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12);

    @Support
    @NotNull
    Condition lessThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12);

    @Support
    @NotNull
    Condition lessThan(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support
    @NotNull
    Condition lessThan(Select<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select);

    @Support
    @NotNull
    Condition lessThan(QuantifiedSelect<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> quantifiedSelect);

    @Support
    @NotNull
    Condition lt(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12);

    @Support
    @NotNull
    Condition lt(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12);

    @Support
    @NotNull
    Condition lt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12);

    @Support
    @NotNull
    Condition lt(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support
    @NotNull
    Condition lt(Select<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select);

    @Support
    @NotNull
    Condition lt(QuantifiedSelect<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> quantifiedSelect);

    @Support
    @NotNull
    Condition lessOrEqual(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12);

    @Support
    @NotNull
    Condition lessOrEqual(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12);

    @Support
    @NotNull
    Condition lessOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12);

    @Support
    @NotNull
    Condition lessOrEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support
    @NotNull
    Condition lessOrEqual(Select<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select);

    @Support
    @NotNull
    Condition lessOrEqual(QuantifiedSelect<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> quantifiedSelect);

    @Support
    @NotNull
    Condition le(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12);

    @Support
    @NotNull
    Condition le(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12);

    @Support
    @NotNull
    Condition le(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12);

    @Support
    @NotNull
    Condition le(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support
    @NotNull
    Condition le(Select<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select);

    @Support
    @NotNull
    Condition le(QuantifiedSelect<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterThan(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12);

    @Support
    @NotNull
    Condition greaterThan(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12);

    @Support
    @NotNull
    Condition greaterThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12);

    @Support
    @NotNull
    Condition greaterThan(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support
    @NotNull
    Condition greaterThan(Select<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select);

    @Support
    @NotNull
    Condition greaterThan(QuantifiedSelect<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> quantifiedSelect);

    @Support
    @NotNull
    Condition gt(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12);

    @Support
    @NotNull
    Condition gt(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12);

    @Support
    @NotNull
    Condition gt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12);

    @Support
    @NotNull
    Condition gt(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support
    @NotNull
    Condition gt(Select<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select);

    @Support
    @NotNull
    Condition gt(QuantifiedSelect<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterOrEqual(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12);

    @Support
    @NotNull
    Condition greaterOrEqual(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12);

    @Support
    @NotNull
    Condition greaterOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12);

    @Support
    @NotNull
    Condition greaterOrEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support
    @NotNull
    Condition greaterOrEqual(Select<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select);

    @Support
    @NotNull
    Condition greaterOrEqual(QuantifiedSelect<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> quantifiedSelect);

    @Support
    @NotNull
    Condition ge(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12);

    @Support
    @NotNull
    Condition ge(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12);

    @Support
    @NotNull
    Condition ge(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12);

    @Support
    @NotNull
    Condition ge(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support
    @NotNull
    Condition ge(Select<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select);

    @Support
    @NotNull
    Condition ge(QuantifiedSelect<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> quantifiedSelect);

    @Support
    @NotNull
    BetweenAndStep12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> between(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12);

    @Support
    @NotNull
    BetweenAndStep12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> between(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support
    @NotNull
    BetweenAndStep12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> between(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12);

    @Support
    @NotNull
    BetweenAndStep12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> between(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12);

    @Support
    @NotNull
    Condition between(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12, Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row122);

    @Support
    @NotNull
    Condition between(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12, Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record122);

    @Support
    @NotNull
    BetweenAndStep12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> betweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12);

    @Support
    @NotNull
    BetweenAndStep12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> betweenSymmetric(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support
    @NotNull
    BetweenAndStep12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> betweenSymmetric(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12);

    @Support
    @NotNull
    BetweenAndStep12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> betweenSymmetric(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12);

    @Support
    @NotNull
    Condition betweenSymmetric(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12, Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row122);

    @Support
    @NotNull
    Condition betweenSymmetric(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12, Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record122);

    @Support
    @NotNull
    BetweenAndStep12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> notBetween(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12);

    @Support
    @NotNull
    BetweenAndStep12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> notBetween(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support
    @NotNull
    BetweenAndStep12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> notBetween(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12);

    @Support
    @NotNull
    BetweenAndStep12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> notBetween(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12);

    @Support
    @NotNull
    Condition notBetween(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12, Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row122);

    @Support
    @NotNull
    Condition notBetween(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12, Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record122);

    @Support
    @NotNull
    BetweenAndStep12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> notBetweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12);

    @Support
    @NotNull
    BetweenAndStep12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> notBetweenSymmetric(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support
    @NotNull
    BetweenAndStep12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> notBetweenSymmetric(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12);

    @Support
    @NotNull
    BetweenAndStep12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> notBetweenSymmetric(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12, Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row122);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12, Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record122);

    @Support
    @NotNull
    Condition in(Collection<? extends Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> collection);

    @Support
    @NotNull
    Condition in(Result<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> result);

    @Support
    @NotNull
    Condition in(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>... row12Arr);

    @Support
    @NotNull
    Condition in(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>... record12Arr);

    @Support
    @NotNull
    Condition in(Select<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select);

    @Support
    @NotNull
    Condition notIn(Collection<? extends Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> collection);

    @Support
    @NotNull
    Condition notIn(Result<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> result);

    @Support
    @NotNull
    Condition notIn(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>... row12Arr);

    @Support
    @NotNull
    Condition notIn(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>... record12Arr);

    @Support
    @NotNull
    Condition notIn(Select<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select);
}
