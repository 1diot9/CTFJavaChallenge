package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Row11.class */
public interface Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> extends Row, SelectField<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> {
    @NotNull
    <U> SelectField<U> mapping(Function11<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? super T10, ? super T11, ? extends U> function11);

    @NotNull
    <U> SelectField<U> mapping(Class<U> cls, Function11<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? super T10, ? super T11, ? extends U> function11);

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

    @Support
    @NotNull
    Condition compare(Comparator comparator, Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row11);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11);

    @Support
    @NotNull
    Condition compare(Comparator comparator, T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select);

    @Support
    @NotNull
    Condition compare(Comparator comparator, QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> quantifiedSelect);

    @Support
    @NotNull
    Condition equal(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row11);

    @Support
    @NotNull
    Condition equal(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11);

    @Support
    @NotNull
    Condition equal(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11);

    @Support
    @NotNull
    Condition equal(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support
    @NotNull
    Condition equal(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select);

    @Support
    @NotNull
    Condition equal(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> quantifiedSelect);

    @Support
    @NotNull
    Condition eq(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row11);

    @Support
    @NotNull
    Condition eq(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11);

    @Support
    @NotNull
    Condition eq(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11);

    @Support
    @NotNull
    Condition eq(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support
    @NotNull
    Condition eq(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select);

    @Support
    @NotNull
    Condition eq(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> quantifiedSelect);

    @Support
    @NotNull
    Condition notEqual(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row11);

    @Support
    @NotNull
    Condition notEqual(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11);

    @Support
    @NotNull
    Condition notEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11);

    @Support
    @NotNull
    Condition notEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support
    @NotNull
    Condition notEqual(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select);

    @Support
    @NotNull
    Condition notEqual(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> quantifiedSelect);

    @Support
    @NotNull
    Condition ne(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row11);

    @Support
    @NotNull
    Condition ne(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11);

    @Support
    @NotNull
    Condition ne(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11);

    @Support
    @NotNull
    Condition ne(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support
    @NotNull
    Condition ne(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select);

    @Support
    @NotNull
    Condition ne(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> quantifiedSelect);

    @Support
    @NotNull
    Condition isDistinctFrom(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row11);

    @Support
    @NotNull
    Condition isDistinctFrom(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11);

    @Support
    @NotNull
    Condition isDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11);

    @Support
    @NotNull
    Condition isDistinctFrom(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support
    @NotNull
    Condition isDistinctFrom(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row11);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11);

    @Support
    @NotNull
    Condition isNotDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select);

    @Support
    @NotNull
    Condition lessThan(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row11);

    @Support
    @NotNull
    Condition lessThan(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11);

    @Support
    @NotNull
    Condition lessThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11);

    @Support
    @NotNull
    Condition lessThan(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support
    @NotNull
    Condition lessThan(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select);

    @Support
    @NotNull
    Condition lessThan(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> quantifiedSelect);

    @Support
    @NotNull
    Condition lt(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row11);

    @Support
    @NotNull
    Condition lt(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11);

    @Support
    @NotNull
    Condition lt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11);

    @Support
    @NotNull
    Condition lt(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support
    @NotNull
    Condition lt(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select);

    @Support
    @NotNull
    Condition lt(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> quantifiedSelect);

    @Support
    @NotNull
    Condition lessOrEqual(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row11);

    @Support
    @NotNull
    Condition lessOrEqual(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11);

    @Support
    @NotNull
    Condition lessOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11);

    @Support
    @NotNull
    Condition lessOrEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support
    @NotNull
    Condition lessOrEqual(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select);

    @Support
    @NotNull
    Condition lessOrEqual(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> quantifiedSelect);

    @Support
    @NotNull
    Condition le(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row11);

    @Support
    @NotNull
    Condition le(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11);

    @Support
    @NotNull
    Condition le(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11);

    @Support
    @NotNull
    Condition le(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support
    @NotNull
    Condition le(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select);

    @Support
    @NotNull
    Condition le(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterThan(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row11);

    @Support
    @NotNull
    Condition greaterThan(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11);

    @Support
    @NotNull
    Condition greaterThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11);

    @Support
    @NotNull
    Condition greaterThan(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support
    @NotNull
    Condition greaterThan(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select);

    @Support
    @NotNull
    Condition greaterThan(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> quantifiedSelect);

    @Support
    @NotNull
    Condition gt(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row11);

    @Support
    @NotNull
    Condition gt(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11);

    @Support
    @NotNull
    Condition gt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11);

    @Support
    @NotNull
    Condition gt(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support
    @NotNull
    Condition gt(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select);

    @Support
    @NotNull
    Condition gt(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterOrEqual(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row11);

    @Support
    @NotNull
    Condition greaterOrEqual(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11);

    @Support
    @NotNull
    Condition greaterOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11);

    @Support
    @NotNull
    Condition greaterOrEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support
    @NotNull
    Condition greaterOrEqual(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select);

    @Support
    @NotNull
    Condition greaterOrEqual(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> quantifiedSelect);

    @Support
    @NotNull
    Condition ge(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row11);

    @Support
    @NotNull
    Condition ge(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11);

    @Support
    @NotNull
    Condition ge(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11);

    @Support
    @NotNull
    Condition ge(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support
    @NotNull
    Condition ge(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select);

    @Support
    @NotNull
    Condition ge(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> quantifiedSelect);

    @Support
    @NotNull
    BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> between(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11);

    @Support
    @NotNull
    BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> between(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support
    @NotNull
    BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> between(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row11);

    @Support
    @NotNull
    BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> between(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11);

    @Support
    @NotNull
    Condition between(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row11, Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row112);

    @Support
    @NotNull
    Condition between(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11, Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record112);

    @Support
    @NotNull
    BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> betweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11);

    @Support
    @NotNull
    BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> betweenSymmetric(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support
    @NotNull
    BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> betweenSymmetric(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row11);

    @Support
    @NotNull
    BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> betweenSymmetric(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11);

    @Support
    @NotNull
    Condition betweenSymmetric(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row11, Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row112);

    @Support
    @NotNull
    Condition betweenSymmetric(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11, Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record112);

    @Support
    @NotNull
    BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetween(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11);

    @Support
    @NotNull
    BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetween(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support
    @NotNull
    BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetween(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row11);

    @Support
    @NotNull
    BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetween(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11);

    @Support
    @NotNull
    Condition notBetween(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row11, Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row112);

    @Support
    @NotNull
    Condition notBetween(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11, Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record112);

    @Support
    @NotNull
    BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11);

    @Support
    @NotNull
    BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetweenSymmetric(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Support
    @NotNull
    BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetweenSymmetric(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row11);

    @Support
    @NotNull
    BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetweenSymmetric(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row11, Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row112);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11, Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record112);

    @Support
    @NotNull
    Condition in(Collection<? extends Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> collection);

    @Support
    @NotNull
    Condition in(Result<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> result);

    @Support
    @NotNull
    Condition in(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>... row11Arr);

    @Support
    @NotNull
    Condition in(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>... record11Arr);

    @Support
    @NotNull
    Condition in(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select);

    @Support
    @NotNull
    Condition notIn(Collection<? extends Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> collection);

    @Support
    @NotNull
    Condition notIn(Result<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> result);

    @Support
    @NotNull
    Condition notIn(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>... row11Arr);

    @Support
    @NotNull
    Condition notIn(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>... record11Arr);

    @Support
    @NotNull
    Condition notIn(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select);
}
