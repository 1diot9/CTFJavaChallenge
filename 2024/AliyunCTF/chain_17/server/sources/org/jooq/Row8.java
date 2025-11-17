package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Row8.class */
public interface Row8<T1, T2, T3, T4, T5, T6, T7, T8> extends Row, SelectField<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> {
    @NotNull
    <U> SelectField<U> mapping(Function8<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? extends U> function8);

    @NotNull
    <U> SelectField<U> mapping(Class<U> cls, Function8<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? extends U> function8);

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

    @Support
    @NotNull
    Condition compare(Comparator comparator, Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8);

    @Support
    @NotNull
    Condition compare(Comparator comparator, T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Select<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select);

    @Support
    @NotNull
    Condition compare(Comparator comparator, QuantifiedSelect<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> quantifiedSelect);

    @Support
    @NotNull
    Condition equal(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8);

    @Support
    @NotNull
    Condition equal(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8);

    @Support
    @NotNull
    Condition equal(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);

    @Support
    @NotNull
    Condition equal(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support
    @NotNull
    Condition equal(Select<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select);

    @Support
    @NotNull
    Condition equal(QuantifiedSelect<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> quantifiedSelect);

    @Support
    @NotNull
    Condition eq(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8);

    @Support
    @NotNull
    Condition eq(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8);

    @Support
    @NotNull
    Condition eq(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);

    @Support
    @NotNull
    Condition eq(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support
    @NotNull
    Condition eq(Select<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select);

    @Support
    @NotNull
    Condition eq(QuantifiedSelect<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> quantifiedSelect);

    @Support
    @NotNull
    Condition notEqual(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8);

    @Support
    @NotNull
    Condition notEqual(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8);

    @Support
    @NotNull
    Condition notEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);

    @Support
    @NotNull
    Condition notEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support
    @NotNull
    Condition notEqual(Select<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select);

    @Support
    @NotNull
    Condition notEqual(QuantifiedSelect<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> quantifiedSelect);

    @Support
    @NotNull
    Condition ne(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8);

    @Support
    @NotNull
    Condition ne(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8);

    @Support
    @NotNull
    Condition ne(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);

    @Support
    @NotNull
    Condition ne(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support
    @NotNull
    Condition ne(Select<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select);

    @Support
    @NotNull
    Condition ne(QuantifiedSelect<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> quantifiedSelect);

    @Support
    @NotNull
    Condition isDistinctFrom(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8);

    @Support
    @NotNull
    Condition isDistinctFrom(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8);

    @Support
    @NotNull
    Condition isDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);

    @Support
    @NotNull
    Condition isDistinctFrom(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support
    @NotNull
    Condition isDistinctFrom(Select<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8);

    @Support
    @NotNull
    Condition isNotDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Select<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select);

    @Support
    @NotNull
    Condition lessThan(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8);

    @Support
    @NotNull
    Condition lessThan(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8);

    @Support
    @NotNull
    Condition lessThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);

    @Support
    @NotNull
    Condition lessThan(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support
    @NotNull
    Condition lessThan(Select<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select);

    @Support
    @NotNull
    Condition lessThan(QuantifiedSelect<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> quantifiedSelect);

    @Support
    @NotNull
    Condition lt(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8);

    @Support
    @NotNull
    Condition lt(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8);

    @Support
    @NotNull
    Condition lt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);

    @Support
    @NotNull
    Condition lt(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support
    @NotNull
    Condition lt(Select<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select);

    @Support
    @NotNull
    Condition lt(QuantifiedSelect<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> quantifiedSelect);

    @Support
    @NotNull
    Condition lessOrEqual(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8);

    @Support
    @NotNull
    Condition lessOrEqual(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8);

    @Support
    @NotNull
    Condition lessOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);

    @Support
    @NotNull
    Condition lessOrEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support
    @NotNull
    Condition lessOrEqual(Select<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select);

    @Support
    @NotNull
    Condition lessOrEqual(QuantifiedSelect<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> quantifiedSelect);

    @Support
    @NotNull
    Condition le(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8);

    @Support
    @NotNull
    Condition le(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8);

    @Support
    @NotNull
    Condition le(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);

    @Support
    @NotNull
    Condition le(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support
    @NotNull
    Condition le(Select<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select);

    @Support
    @NotNull
    Condition le(QuantifiedSelect<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterThan(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8);

    @Support
    @NotNull
    Condition greaterThan(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8);

    @Support
    @NotNull
    Condition greaterThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);

    @Support
    @NotNull
    Condition greaterThan(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support
    @NotNull
    Condition greaterThan(Select<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select);

    @Support
    @NotNull
    Condition greaterThan(QuantifiedSelect<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> quantifiedSelect);

    @Support
    @NotNull
    Condition gt(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8);

    @Support
    @NotNull
    Condition gt(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8);

    @Support
    @NotNull
    Condition gt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);

    @Support
    @NotNull
    Condition gt(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support
    @NotNull
    Condition gt(Select<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select);

    @Support
    @NotNull
    Condition gt(QuantifiedSelect<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterOrEqual(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8);

    @Support
    @NotNull
    Condition greaterOrEqual(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8);

    @Support
    @NotNull
    Condition greaterOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);

    @Support
    @NotNull
    Condition greaterOrEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support
    @NotNull
    Condition greaterOrEqual(Select<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select);

    @Support
    @NotNull
    Condition greaterOrEqual(QuantifiedSelect<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> quantifiedSelect);

    @Support
    @NotNull
    Condition ge(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8);

    @Support
    @NotNull
    Condition ge(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8);

    @Support
    @NotNull
    Condition ge(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);

    @Support
    @NotNull
    Condition ge(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support
    @NotNull
    Condition ge(Select<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select);

    @Support
    @NotNull
    Condition ge(QuantifiedSelect<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> quantifiedSelect);

    @Support
    @NotNull
    BetweenAndStep8<T1, T2, T3, T4, T5, T6, T7, T8> between(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);

    @Support
    @NotNull
    BetweenAndStep8<T1, T2, T3, T4, T5, T6, T7, T8> between(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support
    @NotNull
    BetweenAndStep8<T1, T2, T3, T4, T5, T6, T7, T8> between(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8);

    @Support
    @NotNull
    BetweenAndStep8<T1, T2, T3, T4, T5, T6, T7, T8> between(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8);

    @Support
    @NotNull
    Condition between(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8, Row8<T1, T2, T3, T4, T5, T6, T7, T8> row82);

    @Support
    @NotNull
    Condition between(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8, Record8<T1, T2, T3, T4, T5, T6, T7, T8> record82);

    @Support
    @NotNull
    BetweenAndStep8<T1, T2, T3, T4, T5, T6, T7, T8> betweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);

    @Support
    @NotNull
    BetweenAndStep8<T1, T2, T3, T4, T5, T6, T7, T8> betweenSymmetric(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support
    @NotNull
    BetweenAndStep8<T1, T2, T3, T4, T5, T6, T7, T8> betweenSymmetric(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8);

    @Support
    @NotNull
    BetweenAndStep8<T1, T2, T3, T4, T5, T6, T7, T8> betweenSymmetric(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8);

    @Support
    @NotNull
    Condition betweenSymmetric(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8, Row8<T1, T2, T3, T4, T5, T6, T7, T8> row82);

    @Support
    @NotNull
    Condition betweenSymmetric(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8, Record8<T1, T2, T3, T4, T5, T6, T7, T8> record82);

    @Support
    @NotNull
    BetweenAndStep8<T1, T2, T3, T4, T5, T6, T7, T8> notBetween(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);

    @Support
    @NotNull
    BetweenAndStep8<T1, T2, T3, T4, T5, T6, T7, T8> notBetween(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support
    @NotNull
    BetweenAndStep8<T1, T2, T3, T4, T5, T6, T7, T8> notBetween(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8);

    @Support
    @NotNull
    BetweenAndStep8<T1, T2, T3, T4, T5, T6, T7, T8> notBetween(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8);

    @Support
    @NotNull
    Condition notBetween(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8, Row8<T1, T2, T3, T4, T5, T6, T7, T8> row82);

    @Support
    @NotNull
    Condition notBetween(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8, Record8<T1, T2, T3, T4, T5, T6, T7, T8> record82);

    @Support
    @NotNull
    BetweenAndStep8<T1, T2, T3, T4, T5, T6, T7, T8> notBetweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);

    @Support
    @NotNull
    BetweenAndStep8<T1, T2, T3, T4, T5, T6, T7, T8> notBetweenSymmetric(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support
    @NotNull
    BetweenAndStep8<T1, T2, T3, T4, T5, T6, T7, T8> notBetweenSymmetric(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8);

    @Support
    @NotNull
    BetweenAndStep8<T1, T2, T3, T4, T5, T6, T7, T8> notBetweenSymmetric(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8, Row8<T1, T2, T3, T4, T5, T6, T7, T8> row82);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8, Record8<T1, T2, T3, T4, T5, T6, T7, T8> record82);

    @Support
    @NotNull
    Condition in(Collection<? extends Row8<T1, T2, T3, T4, T5, T6, T7, T8>> collection);

    @Support
    @NotNull
    Condition in(Result<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> result);

    @Support
    @NotNull
    Condition in(Row8<T1, T2, T3, T4, T5, T6, T7, T8>... row8Arr);

    @Support
    @NotNull
    Condition in(Record8<T1, T2, T3, T4, T5, T6, T7, T8>... record8Arr);

    @Support
    @NotNull
    Condition in(Select<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select);

    @Support
    @NotNull
    Condition notIn(Collection<? extends Row8<T1, T2, T3, T4, T5, T6, T7, T8>> collection);

    @Support
    @NotNull
    Condition notIn(Result<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> result);

    @Support
    @NotNull
    Condition notIn(Row8<T1, T2, T3, T4, T5, T6, T7, T8>... row8Arr);

    @Support
    @NotNull
    Condition notIn(Record8<T1, T2, T3, T4, T5, T6, T7, T8>... record8Arr);

    @Support
    @NotNull
    Condition notIn(Select<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select);
}
