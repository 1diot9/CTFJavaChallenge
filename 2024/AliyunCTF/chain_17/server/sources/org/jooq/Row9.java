package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Row9.class */
public interface Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> extends Row, SelectField<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> {
    @NotNull
    <U> SelectField<U> mapping(Function9<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? extends U> function9);

    @NotNull
    <U> SelectField<U> mapping(Class<U> cls, Function9<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? extends U> function9);

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

    @Support
    @NotNull
    Condition compare(Comparator comparator, Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9);

    @Support
    @NotNull
    Condition compare(Comparator comparator, T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Select<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select);

    @Support
    @NotNull
    Condition compare(Comparator comparator, QuantifiedSelect<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> quantifiedSelect);

    @Support
    @NotNull
    Condition equal(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9);

    @Support
    @NotNull
    Condition equal(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9);

    @Support
    @NotNull
    Condition equal(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9);

    @Support
    @NotNull
    Condition equal(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support
    @NotNull
    Condition equal(Select<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select);

    @Support
    @NotNull
    Condition equal(QuantifiedSelect<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> quantifiedSelect);

    @Support
    @NotNull
    Condition eq(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9);

    @Support
    @NotNull
    Condition eq(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9);

    @Support
    @NotNull
    Condition eq(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9);

    @Support
    @NotNull
    Condition eq(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support
    @NotNull
    Condition eq(Select<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select);

    @Support
    @NotNull
    Condition eq(QuantifiedSelect<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> quantifiedSelect);

    @Support
    @NotNull
    Condition notEqual(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9);

    @Support
    @NotNull
    Condition notEqual(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9);

    @Support
    @NotNull
    Condition notEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9);

    @Support
    @NotNull
    Condition notEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support
    @NotNull
    Condition notEqual(Select<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select);

    @Support
    @NotNull
    Condition notEqual(QuantifiedSelect<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> quantifiedSelect);

    @Support
    @NotNull
    Condition ne(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9);

    @Support
    @NotNull
    Condition ne(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9);

    @Support
    @NotNull
    Condition ne(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9);

    @Support
    @NotNull
    Condition ne(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support
    @NotNull
    Condition ne(Select<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select);

    @Support
    @NotNull
    Condition ne(QuantifiedSelect<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> quantifiedSelect);

    @Support
    @NotNull
    Condition isDistinctFrom(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9);

    @Support
    @NotNull
    Condition isDistinctFrom(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9);

    @Support
    @NotNull
    Condition isDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9);

    @Support
    @NotNull
    Condition isDistinctFrom(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support
    @NotNull
    Condition isDistinctFrom(Select<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9);

    @Support
    @NotNull
    Condition isNotDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Select<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select);

    @Support
    @NotNull
    Condition lessThan(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9);

    @Support
    @NotNull
    Condition lessThan(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9);

    @Support
    @NotNull
    Condition lessThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9);

    @Support
    @NotNull
    Condition lessThan(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support
    @NotNull
    Condition lessThan(Select<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select);

    @Support
    @NotNull
    Condition lessThan(QuantifiedSelect<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> quantifiedSelect);

    @Support
    @NotNull
    Condition lt(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9);

    @Support
    @NotNull
    Condition lt(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9);

    @Support
    @NotNull
    Condition lt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9);

    @Support
    @NotNull
    Condition lt(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support
    @NotNull
    Condition lt(Select<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select);

    @Support
    @NotNull
    Condition lt(QuantifiedSelect<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> quantifiedSelect);

    @Support
    @NotNull
    Condition lessOrEqual(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9);

    @Support
    @NotNull
    Condition lessOrEqual(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9);

    @Support
    @NotNull
    Condition lessOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9);

    @Support
    @NotNull
    Condition lessOrEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support
    @NotNull
    Condition lessOrEqual(Select<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select);

    @Support
    @NotNull
    Condition lessOrEqual(QuantifiedSelect<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> quantifiedSelect);

    @Support
    @NotNull
    Condition le(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9);

    @Support
    @NotNull
    Condition le(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9);

    @Support
    @NotNull
    Condition le(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9);

    @Support
    @NotNull
    Condition le(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support
    @NotNull
    Condition le(Select<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select);

    @Support
    @NotNull
    Condition le(QuantifiedSelect<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterThan(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9);

    @Support
    @NotNull
    Condition greaterThan(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9);

    @Support
    @NotNull
    Condition greaterThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9);

    @Support
    @NotNull
    Condition greaterThan(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support
    @NotNull
    Condition greaterThan(Select<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select);

    @Support
    @NotNull
    Condition greaterThan(QuantifiedSelect<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> quantifiedSelect);

    @Support
    @NotNull
    Condition gt(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9);

    @Support
    @NotNull
    Condition gt(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9);

    @Support
    @NotNull
    Condition gt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9);

    @Support
    @NotNull
    Condition gt(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support
    @NotNull
    Condition gt(Select<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select);

    @Support
    @NotNull
    Condition gt(QuantifiedSelect<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterOrEqual(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9);

    @Support
    @NotNull
    Condition greaterOrEqual(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9);

    @Support
    @NotNull
    Condition greaterOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9);

    @Support
    @NotNull
    Condition greaterOrEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support
    @NotNull
    Condition greaterOrEqual(Select<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select);

    @Support
    @NotNull
    Condition greaterOrEqual(QuantifiedSelect<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> quantifiedSelect);

    @Support
    @NotNull
    Condition ge(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9);

    @Support
    @NotNull
    Condition ge(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9);

    @Support
    @NotNull
    Condition ge(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9);

    @Support
    @NotNull
    Condition ge(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support
    @NotNull
    Condition ge(Select<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select);

    @Support
    @NotNull
    Condition ge(QuantifiedSelect<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> quantifiedSelect);

    @Support
    @NotNull
    BetweenAndStep9<T1, T2, T3, T4, T5, T6, T7, T8, T9> between(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9);

    @Support
    @NotNull
    BetweenAndStep9<T1, T2, T3, T4, T5, T6, T7, T8, T9> between(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support
    @NotNull
    BetweenAndStep9<T1, T2, T3, T4, T5, T6, T7, T8, T9> between(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9);

    @Support
    @NotNull
    BetweenAndStep9<T1, T2, T3, T4, T5, T6, T7, T8, T9> between(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9);

    @Support
    @NotNull
    Condition between(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9, Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row92);

    @Support
    @NotNull
    Condition between(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9, Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record92);

    @Support
    @NotNull
    BetweenAndStep9<T1, T2, T3, T4, T5, T6, T7, T8, T9> betweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9);

    @Support
    @NotNull
    BetweenAndStep9<T1, T2, T3, T4, T5, T6, T7, T8, T9> betweenSymmetric(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support
    @NotNull
    BetweenAndStep9<T1, T2, T3, T4, T5, T6, T7, T8, T9> betweenSymmetric(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9);

    @Support
    @NotNull
    BetweenAndStep9<T1, T2, T3, T4, T5, T6, T7, T8, T9> betweenSymmetric(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9);

    @Support
    @NotNull
    Condition betweenSymmetric(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9, Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row92);

    @Support
    @NotNull
    Condition betweenSymmetric(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9, Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record92);

    @Support
    @NotNull
    BetweenAndStep9<T1, T2, T3, T4, T5, T6, T7, T8, T9> notBetween(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9);

    @Support
    @NotNull
    BetweenAndStep9<T1, T2, T3, T4, T5, T6, T7, T8, T9> notBetween(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support
    @NotNull
    BetweenAndStep9<T1, T2, T3, T4, T5, T6, T7, T8, T9> notBetween(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9);

    @Support
    @NotNull
    BetweenAndStep9<T1, T2, T3, T4, T5, T6, T7, T8, T9> notBetween(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9);

    @Support
    @NotNull
    Condition notBetween(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9, Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row92);

    @Support
    @NotNull
    Condition notBetween(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9, Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record92);

    @Support
    @NotNull
    BetweenAndStep9<T1, T2, T3, T4, T5, T6, T7, T8, T9> notBetweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9);

    @Support
    @NotNull
    BetweenAndStep9<T1, T2, T3, T4, T5, T6, T7, T8, T9> notBetweenSymmetric(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support
    @NotNull
    BetweenAndStep9<T1, T2, T3, T4, T5, T6, T7, T8, T9> notBetweenSymmetric(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9);

    @Support
    @NotNull
    BetweenAndStep9<T1, T2, T3, T4, T5, T6, T7, T8, T9> notBetweenSymmetric(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9, Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row92);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9, Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record92);

    @Support
    @NotNull
    Condition in(Collection<? extends Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> collection);

    @Support
    @NotNull
    Condition in(Result<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> result);

    @Support
    @NotNull
    Condition in(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9>... row9Arr);

    @Support
    @NotNull
    Condition in(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>... record9Arr);

    @Support
    @NotNull
    Condition in(Select<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select);

    @Support
    @NotNull
    Condition notIn(Collection<? extends Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> collection);

    @Support
    @NotNull
    Condition notIn(Result<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> result);

    @Support
    @NotNull
    Condition notIn(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9>... row9Arr);

    @Support
    @NotNull
    Condition notIn(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>... record9Arr);

    @Support
    @NotNull
    Condition notIn(Select<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select);
}
