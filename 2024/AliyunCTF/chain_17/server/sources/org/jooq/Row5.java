package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Row5.class */
public interface Row5<T1, T2, T3, T4, T5> extends Row, SelectField<Record5<T1, T2, T3, T4, T5>> {
    @NotNull
    <U> SelectField<U> mapping(Function5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? extends U> function5);

    @NotNull
    <U> SelectField<U> mapping(Class<U> cls, Function5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? extends U> function5);

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

    @Support
    @NotNull
    Condition compare(Comparator comparator, Row5<T1, T2, T3, T4, T5> row5);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Record5<T1, T2, T3, T4, T5> record5);

    @Support
    @NotNull
    Condition compare(Comparator comparator, T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Select<? extends Record5<T1, T2, T3, T4, T5>> select);

    @Support
    @NotNull
    Condition compare(Comparator comparator, QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> quantifiedSelect);

    @Support
    @NotNull
    Condition equal(Row5<T1, T2, T3, T4, T5> row5);

    @Support
    @NotNull
    Condition equal(Record5<T1, T2, T3, T4, T5> record5);

    @Support
    @NotNull
    Condition equal(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @NotNull
    Condition equal(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @NotNull
    Condition equal(Select<? extends Record5<T1, T2, T3, T4, T5>> select);

    @Support
    @NotNull
    Condition equal(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> quantifiedSelect);

    @Support
    @NotNull
    Condition eq(Row5<T1, T2, T3, T4, T5> row5);

    @Support
    @NotNull
    Condition eq(Record5<T1, T2, T3, T4, T5> record5);

    @Support
    @NotNull
    Condition eq(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @NotNull
    Condition eq(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @NotNull
    Condition eq(Select<? extends Record5<T1, T2, T3, T4, T5>> select);

    @Support
    @NotNull
    Condition eq(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> quantifiedSelect);

    @Support
    @NotNull
    Condition notEqual(Row5<T1, T2, T3, T4, T5> row5);

    @Support
    @NotNull
    Condition notEqual(Record5<T1, T2, T3, T4, T5> record5);

    @Support
    @NotNull
    Condition notEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @NotNull
    Condition notEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @NotNull
    Condition notEqual(Select<? extends Record5<T1, T2, T3, T4, T5>> select);

    @Support
    @NotNull
    Condition notEqual(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> quantifiedSelect);

    @Support
    @NotNull
    Condition ne(Row5<T1, T2, T3, T4, T5> row5);

    @Support
    @NotNull
    Condition ne(Record5<T1, T2, T3, T4, T5> record5);

    @Support
    @NotNull
    Condition ne(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @NotNull
    Condition ne(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @NotNull
    Condition ne(Select<? extends Record5<T1, T2, T3, T4, T5>> select);

    @Support
    @NotNull
    Condition ne(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> quantifiedSelect);

    @Support
    @NotNull
    Condition isDistinctFrom(Row5<T1, T2, T3, T4, T5> row5);

    @Support
    @NotNull
    Condition isDistinctFrom(Record5<T1, T2, T3, T4, T5> record5);

    @Support
    @NotNull
    Condition isDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @NotNull
    Condition isDistinctFrom(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @NotNull
    Condition isDistinctFrom(Select<? extends Record5<T1, T2, T3, T4, T5>> select);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Row5<T1, T2, T3, T4, T5> row5);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Record5<T1, T2, T3, T4, T5> record5);

    @Support
    @NotNull
    Condition isNotDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Select<? extends Record5<T1, T2, T3, T4, T5>> select);

    @Support
    @NotNull
    Condition lessThan(Row5<T1, T2, T3, T4, T5> row5);

    @Support
    @NotNull
    Condition lessThan(Record5<T1, T2, T3, T4, T5> record5);

    @Support
    @NotNull
    Condition lessThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @NotNull
    Condition lessThan(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @NotNull
    Condition lessThan(Select<? extends Record5<T1, T2, T3, T4, T5>> select);

    @Support
    @NotNull
    Condition lessThan(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> quantifiedSelect);

    @Support
    @NotNull
    Condition lt(Row5<T1, T2, T3, T4, T5> row5);

    @Support
    @NotNull
    Condition lt(Record5<T1, T2, T3, T4, T5> record5);

    @Support
    @NotNull
    Condition lt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @NotNull
    Condition lt(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @NotNull
    Condition lt(Select<? extends Record5<T1, T2, T3, T4, T5>> select);

    @Support
    @NotNull
    Condition lt(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> quantifiedSelect);

    @Support
    @NotNull
    Condition lessOrEqual(Row5<T1, T2, T3, T4, T5> row5);

    @Support
    @NotNull
    Condition lessOrEqual(Record5<T1, T2, T3, T4, T5> record5);

    @Support
    @NotNull
    Condition lessOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @NotNull
    Condition lessOrEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @NotNull
    Condition lessOrEqual(Select<? extends Record5<T1, T2, T3, T4, T5>> select);

    @Support
    @NotNull
    Condition lessOrEqual(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> quantifiedSelect);

    @Support
    @NotNull
    Condition le(Row5<T1, T2, T3, T4, T5> row5);

    @Support
    @NotNull
    Condition le(Record5<T1, T2, T3, T4, T5> record5);

    @Support
    @NotNull
    Condition le(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @NotNull
    Condition le(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @NotNull
    Condition le(Select<? extends Record5<T1, T2, T3, T4, T5>> select);

    @Support
    @NotNull
    Condition le(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterThan(Row5<T1, T2, T3, T4, T5> row5);

    @Support
    @NotNull
    Condition greaterThan(Record5<T1, T2, T3, T4, T5> record5);

    @Support
    @NotNull
    Condition greaterThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @NotNull
    Condition greaterThan(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @NotNull
    Condition greaterThan(Select<? extends Record5<T1, T2, T3, T4, T5>> select);

    @Support
    @NotNull
    Condition greaterThan(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> quantifiedSelect);

    @Support
    @NotNull
    Condition gt(Row5<T1, T2, T3, T4, T5> row5);

    @Support
    @NotNull
    Condition gt(Record5<T1, T2, T3, T4, T5> record5);

    @Support
    @NotNull
    Condition gt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @NotNull
    Condition gt(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @NotNull
    Condition gt(Select<? extends Record5<T1, T2, T3, T4, T5>> select);

    @Support
    @NotNull
    Condition gt(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterOrEqual(Row5<T1, T2, T3, T4, T5> row5);

    @Support
    @NotNull
    Condition greaterOrEqual(Record5<T1, T2, T3, T4, T5> record5);

    @Support
    @NotNull
    Condition greaterOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @NotNull
    Condition greaterOrEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @NotNull
    Condition greaterOrEqual(Select<? extends Record5<T1, T2, T3, T4, T5>> select);

    @Support
    @NotNull
    Condition greaterOrEqual(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> quantifiedSelect);

    @Support
    @NotNull
    Condition ge(Row5<T1, T2, T3, T4, T5> row5);

    @Support
    @NotNull
    Condition ge(Record5<T1, T2, T3, T4, T5> record5);

    @Support
    @NotNull
    Condition ge(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @NotNull
    Condition ge(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @NotNull
    Condition ge(Select<? extends Record5<T1, T2, T3, T4, T5>> select);

    @Support
    @NotNull
    Condition ge(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> quantifiedSelect);

    @Support
    @NotNull
    BetweenAndStep5<T1, T2, T3, T4, T5> between(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @NotNull
    BetweenAndStep5<T1, T2, T3, T4, T5> between(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @NotNull
    BetweenAndStep5<T1, T2, T3, T4, T5> between(Row5<T1, T2, T3, T4, T5> row5);

    @Support
    @NotNull
    BetweenAndStep5<T1, T2, T3, T4, T5> between(Record5<T1, T2, T3, T4, T5> record5);

    @Support
    @NotNull
    Condition between(Row5<T1, T2, T3, T4, T5> row5, Row5<T1, T2, T3, T4, T5> row52);

    @Support
    @NotNull
    Condition between(Record5<T1, T2, T3, T4, T5> record5, Record5<T1, T2, T3, T4, T5> record52);

    @Support
    @NotNull
    BetweenAndStep5<T1, T2, T3, T4, T5> betweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @NotNull
    BetweenAndStep5<T1, T2, T3, T4, T5> betweenSymmetric(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @NotNull
    BetweenAndStep5<T1, T2, T3, T4, T5> betweenSymmetric(Row5<T1, T2, T3, T4, T5> row5);

    @Support
    @NotNull
    BetweenAndStep5<T1, T2, T3, T4, T5> betweenSymmetric(Record5<T1, T2, T3, T4, T5> record5);

    @Support
    @NotNull
    Condition betweenSymmetric(Row5<T1, T2, T3, T4, T5> row5, Row5<T1, T2, T3, T4, T5> row52);

    @Support
    @NotNull
    Condition betweenSymmetric(Record5<T1, T2, T3, T4, T5> record5, Record5<T1, T2, T3, T4, T5> record52);

    @Support
    @NotNull
    BetweenAndStep5<T1, T2, T3, T4, T5> notBetween(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @NotNull
    BetweenAndStep5<T1, T2, T3, T4, T5> notBetween(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @NotNull
    BetweenAndStep5<T1, T2, T3, T4, T5> notBetween(Row5<T1, T2, T3, T4, T5> row5);

    @Support
    @NotNull
    BetweenAndStep5<T1, T2, T3, T4, T5> notBetween(Record5<T1, T2, T3, T4, T5> record5);

    @Support
    @NotNull
    Condition notBetween(Row5<T1, T2, T3, T4, T5> row5, Row5<T1, T2, T3, T4, T5> row52);

    @Support
    @NotNull
    Condition notBetween(Record5<T1, T2, T3, T4, T5> record5, Record5<T1, T2, T3, T4, T5> record52);

    @Support
    @NotNull
    BetweenAndStep5<T1, T2, T3, T4, T5> notBetweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @NotNull
    BetweenAndStep5<T1, T2, T3, T4, T5> notBetweenSymmetric(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @NotNull
    BetweenAndStep5<T1, T2, T3, T4, T5> notBetweenSymmetric(Row5<T1, T2, T3, T4, T5> row5);

    @Support
    @NotNull
    BetweenAndStep5<T1, T2, T3, T4, T5> notBetweenSymmetric(Record5<T1, T2, T3, T4, T5> record5);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Row5<T1, T2, T3, T4, T5> row5, Row5<T1, T2, T3, T4, T5> row52);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Record5<T1, T2, T3, T4, T5> record5, Record5<T1, T2, T3, T4, T5> record52);

    @Support
    @NotNull
    Condition in(Collection<? extends Row5<T1, T2, T3, T4, T5>> collection);

    @Support
    @NotNull
    Condition in(Result<? extends Record5<T1, T2, T3, T4, T5>> result);

    @Support
    @NotNull
    Condition in(Row5<T1, T2, T3, T4, T5>... row5Arr);

    @Support
    @NotNull
    Condition in(Record5<T1, T2, T3, T4, T5>... record5Arr);

    @Support
    @NotNull
    Condition in(Select<? extends Record5<T1, T2, T3, T4, T5>> select);

    @Support
    @NotNull
    Condition notIn(Collection<? extends Row5<T1, T2, T3, T4, T5>> collection);

    @Support
    @NotNull
    Condition notIn(Result<? extends Record5<T1, T2, T3, T4, T5>> result);

    @Support
    @NotNull
    Condition notIn(Row5<T1, T2, T3, T4, T5>... row5Arr);

    @Support
    @NotNull
    Condition notIn(Record5<T1, T2, T3, T4, T5>... record5Arr);

    @Support
    @NotNull
    Condition notIn(Select<? extends Record5<T1, T2, T3, T4, T5>> select);
}
