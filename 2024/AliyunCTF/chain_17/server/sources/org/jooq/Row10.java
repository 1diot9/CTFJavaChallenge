package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Row10.class */
public interface Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> extends Row, SelectField<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> {
    @NotNull
    <U> SelectField<U> mapping(Function10<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? super T10, ? extends U> function10);

    @NotNull
    <U> SelectField<U> mapping(Class<U> cls, Function10<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? super T10, ? extends U> function10);

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

    @Support
    @NotNull
    Condition compare(Comparator comparator, Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10);

    @Support
    @NotNull
    Condition compare(Comparator comparator, T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Select<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select);

    @Support
    @NotNull
    Condition compare(Comparator comparator, QuantifiedSelect<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> quantifiedSelect);

    @Support
    @NotNull
    Condition equal(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10);

    @Support
    @NotNull
    Condition equal(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10);

    @Support
    @NotNull
    Condition equal(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);

    @Support
    @NotNull
    Condition equal(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support
    @NotNull
    Condition equal(Select<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select);

    @Support
    @NotNull
    Condition equal(QuantifiedSelect<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> quantifiedSelect);

    @Support
    @NotNull
    Condition eq(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10);

    @Support
    @NotNull
    Condition eq(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10);

    @Support
    @NotNull
    Condition eq(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);

    @Support
    @NotNull
    Condition eq(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support
    @NotNull
    Condition eq(Select<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select);

    @Support
    @NotNull
    Condition eq(QuantifiedSelect<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> quantifiedSelect);

    @Support
    @NotNull
    Condition notEqual(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10);

    @Support
    @NotNull
    Condition notEqual(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10);

    @Support
    @NotNull
    Condition notEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);

    @Support
    @NotNull
    Condition notEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support
    @NotNull
    Condition notEqual(Select<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select);

    @Support
    @NotNull
    Condition notEqual(QuantifiedSelect<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> quantifiedSelect);

    @Support
    @NotNull
    Condition ne(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10);

    @Support
    @NotNull
    Condition ne(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10);

    @Support
    @NotNull
    Condition ne(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);

    @Support
    @NotNull
    Condition ne(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support
    @NotNull
    Condition ne(Select<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select);

    @Support
    @NotNull
    Condition ne(QuantifiedSelect<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> quantifiedSelect);

    @Support
    @NotNull
    Condition isDistinctFrom(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10);

    @Support
    @NotNull
    Condition isDistinctFrom(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10);

    @Support
    @NotNull
    Condition isDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);

    @Support
    @NotNull
    Condition isDistinctFrom(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support
    @NotNull
    Condition isDistinctFrom(Select<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10);

    @Support
    @NotNull
    Condition isNotDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Select<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select);

    @Support
    @NotNull
    Condition lessThan(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10);

    @Support
    @NotNull
    Condition lessThan(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10);

    @Support
    @NotNull
    Condition lessThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);

    @Support
    @NotNull
    Condition lessThan(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support
    @NotNull
    Condition lessThan(Select<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select);

    @Support
    @NotNull
    Condition lessThan(QuantifiedSelect<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> quantifiedSelect);

    @Support
    @NotNull
    Condition lt(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10);

    @Support
    @NotNull
    Condition lt(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10);

    @Support
    @NotNull
    Condition lt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);

    @Support
    @NotNull
    Condition lt(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support
    @NotNull
    Condition lt(Select<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select);

    @Support
    @NotNull
    Condition lt(QuantifiedSelect<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> quantifiedSelect);

    @Support
    @NotNull
    Condition lessOrEqual(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10);

    @Support
    @NotNull
    Condition lessOrEqual(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10);

    @Support
    @NotNull
    Condition lessOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);

    @Support
    @NotNull
    Condition lessOrEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support
    @NotNull
    Condition lessOrEqual(Select<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select);

    @Support
    @NotNull
    Condition lessOrEqual(QuantifiedSelect<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> quantifiedSelect);

    @Support
    @NotNull
    Condition le(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10);

    @Support
    @NotNull
    Condition le(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10);

    @Support
    @NotNull
    Condition le(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);

    @Support
    @NotNull
    Condition le(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support
    @NotNull
    Condition le(Select<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select);

    @Support
    @NotNull
    Condition le(QuantifiedSelect<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterThan(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10);

    @Support
    @NotNull
    Condition greaterThan(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10);

    @Support
    @NotNull
    Condition greaterThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);

    @Support
    @NotNull
    Condition greaterThan(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support
    @NotNull
    Condition greaterThan(Select<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select);

    @Support
    @NotNull
    Condition greaterThan(QuantifiedSelect<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> quantifiedSelect);

    @Support
    @NotNull
    Condition gt(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10);

    @Support
    @NotNull
    Condition gt(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10);

    @Support
    @NotNull
    Condition gt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);

    @Support
    @NotNull
    Condition gt(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support
    @NotNull
    Condition gt(Select<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select);

    @Support
    @NotNull
    Condition gt(QuantifiedSelect<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterOrEqual(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10);

    @Support
    @NotNull
    Condition greaterOrEqual(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10);

    @Support
    @NotNull
    Condition greaterOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);

    @Support
    @NotNull
    Condition greaterOrEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support
    @NotNull
    Condition greaterOrEqual(Select<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select);

    @Support
    @NotNull
    Condition greaterOrEqual(QuantifiedSelect<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> quantifiedSelect);

    @Support
    @NotNull
    Condition ge(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10);

    @Support
    @NotNull
    Condition ge(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10);

    @Support
    @NotNull
    Condition ge(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);

    @Support
    @NotNull
    Condition ge(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support
    @NotNull
    Condition ge(Select<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select);

    @Support
    @NotNull
    Condition ge(QuantifiedSelect<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> quantifiedSelect);

    @Support
    @NotNull
    BetweenAndStep10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> between(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);

    @Support
    @NotNull
    BetweenAndStep10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> between(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support
    @NotNull
    BetweenAndStep10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> between(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10);

    @Support
    @NotNull
    BetweenAndStep10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> between(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10);

    @Support
    @NotNull
    Condition between(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10, Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row102);

    @Support
    @NotNull
    Condition between(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10, Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record102);

    @Support
    @NotNull
    BetweenAndStep10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> betweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);

    @Support
    @NotNull
    BetweenAndStep10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> betweenSymmetric(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support
    @NotNull
    BetweenAndStep10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> betweenSymmetric(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10);

    @Support
    @NotNull
    BetweenAndStep10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> betweenSymmetric(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10);

    @Support
    @NotNull
    Condition betweenSymmetric(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10, Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row102);

    @Support
    @NotNull
    Condition betweenSymmetric(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10, Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record102);

    @Support
    @NotNull
    BetweenAndStep10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> notBetween(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);

    @Support
    @NotNull
    BetweenAndStep10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> notBetween(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support
    @NotNull
    BetweenAndStep10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> notBetween(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10);

    @Support
    @NotNull
    BetweenAndStep10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> notBetween(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10);

    @Support
    @NotNull
    Condition notBetween(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10, Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row102);

    @Support
    @NotNull
    Condition notBetween(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10, Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record102);

    @Support
    @NotNull
    BetweenAndStep10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> notBetweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);

    @Support
    @NotNull
    BetweenAndStep10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> notBetweenSymmetric(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support
    @NotNull
    BetweenAndStep10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> notBetweenSymmetric(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10);

    @Support
    @NotNull
    BetweenAndStep10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> notBetweenSymmetric(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10, Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row102);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10, Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record102);

    @Support
    @NotNull
    Condition in(Collection<? extends Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> collection);

    @Support
    @NotNull
    Condition in(Result<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> result);

    @Support
    @NotNull
    Condition in(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>... row10Arr);

    @Support
    @NotNull
    Condition in(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>... record10Arr);

    @Support
    @NotNull
    Condition in(Select<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select);

    @Support
    @NotNull
    Condition notIn(Collection<? extends Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> collection);

    @Support
    @NotNull
    Condition notIn(Result<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> result);

    @Support
    @NotNull
    Condition notIn(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>... row10Arr);

    @Support
    @NotNull
    Condition notIn(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>... record10Arr);

    @Support
    @NotNull
    Condition notIn(Select<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select);
}
