package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Row4.class */
public interface Row4<T1, T2, T3, T4> extends Row, SelectField<Record4<T1, T2, T3, T4>> {
    @NotNull
    <U> SelectField<U> mapping(Function4<? super T1, ? super T2, ? super T3, ? super T4, ? extends U> function4);

    @NotNull
    <U> SelectField<U> mapping(Class<U> cls, Function4<? super T1, ? super T2, ? super T3, ? super T4, ? extends U> function4);

    @NotNull
    Field<T1> field1();

    @NotNull
    Field<T2> field2();

    @NotNull
    Field<T3> field3();

    @NotNull
    Field<T4> field4();

    @Support
    @NotNull
    Condition compare(Comparator comparator, Row4<T1, T2, T3, T4> row4);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Record4<T1, T2, T3, T4> record4);

    @Support
    @NotNull
    Condition compare(Comparator comparator, T1 t1, T2 t2, T3 t3, T4 t4);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Select<? extends Record4<T1, T2, T3, T4>> select);

    @Support
    @NotNull
    Condition compare(Comparator comparator, QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> quantifiedSelect);

    @Support
    @NotNull
    Condition equal(Row4<T1, T2, T3, T4> row4);

    @Support
    @NotNull
    Condition equal(Record4<T1, T2, T3, T4> record4);

    @Support
    @NotNull
    Condition equal(T1 t1, T2 t2, T3 t3, T4 t4);

    @Support
    @NotNull
    Condition equal(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support
    @NotNull
    Condition equal(Select<? extends Record4<T1, T2, T3, T4>> select);

    @Support
    @NotNull
    Condition equal(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> quantifiedSelect);

    @Support
    @NotNull
    Condition eq(Row4<T1, T2, T3, T4> row4);

    @Support
    @NotNull
    Condition eq(Record4<T1, T2, T3, T4> record4);

    @Support
    @NotNull
    Condition eq(T1 t1, T2 t2, T3 t3, T4 t4);

    @Support
    @NotNull
    Condition eq(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support
    @NotNull
    Condition eq(Select<? extends Record4<T1, T2, T3, T4>> select);

    @Support
    @NotNull
    Condition eq(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> quantifiedSelect);

    @Support
    @NotNull
    Condition notEqual(Row4<T1, T2, T3, T4> row4);

    @Support
    @NotNull
    Condition notEqual(Record4<T1, T2, T3, T4> record4);

    @Support
    @NotNull
    Condition notEqual(T1 t1, T2 t2, T3 t3, T4 t4);

    @Support
    @NotNull
    Condition notEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support
    @NotNull
    Condition notEqual(Select<? extends Record4<T1, T2, T3, T4>> select);

    @Support
    @NotNull
    Condition notEqual(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> quantifiedSelect);

    @Support
    @NotNull
    Condition ne(Row4<T1, T2, T3, T4> row4);

    @Support
    @NotNull
    Condition ne(Record4<T1, T2, T3, T4> record4);

    @Support
    @NotNull
    Condition ne(T1 t1, T2 t2, T3 t3, T4 t4);

    @Support
    @NotNull
    Condition ne(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support
    @NotNull
    Condition ne(Select<? extends Record4<T1, T2, T3, T4>> select);

    @Support
    @NotNull
    Condition ne(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> quantifiedSelect);

    @Support
    @NotNull
    Condition isDistinctFrom(Row4<T1, T2, T3, T4> row4);

    @Support
    @NotNull
    Condition isDistinctFrom(Record4<T1, T2, T3, T4> record4);

    @Support
    @NotNull
    Condition isDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4);

    @Support
    @NotNull
    Condition isDistinctFrom(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support
    @NotNull
    Condition isDistinctFrom(Select<? extends Record4<T1, T2, T3, T4>> select);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Row4<T1, T2, T3, T4> row4);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Record4<T1, T2, T3, T4> record4);

    @Support
    @NotNull
    Condition isNotDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Select<? extends Record4<T1, T2, T3, T4>> select);

    @Support
    @NotNull
    Condition lessThan(Row4<T1, T2, T3, T4> row4);

    @Support
    @NotNull
    Condition lessThan(Record4<T1, T2, T3, T4> record4);

    @Support
    @NotNull
    Condition lessThan(T1 t1, T2 t2, T3 t3, T4 t4);

    @Support
    @NotNull
    Condition lessThan(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support
    @NotNull
    Condition lessThan(Select<? extends Record4<T1, T2, T3, T4>> select);

    @Support
    @NotNull
    Condition lessThan(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> quantifiedSelect);

    @Support
    @NotNull
    Condition lt(Row4<T1, T2, T3, T4> row4);

    @Support
    @NotNull
    Condition lt(Record4<T1, T2, T3, T4> record4);

    @Support
    @NotNull
    Condition lt(T1 t1, T2 t2, T3 t3, T4 t4);

    @Support
    @NotNull
    Condition lt(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support
    @NotNull
    Condition lt(Select<? extends Record4<T1, T2, T3, T4>> select);

    @Support
    @NotNull
    Condition lt(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> quantifiedSelect);

    @Support
    @NotNull
    Condition lessOrEqual(Row4<T1, T2, T3, T4> row4);

    @Support
    @NotNull
    Condition lessOrEqual(Record4<T1, T2, T3, T4> record4);

    @Support
    @NotNull
    Condition lessOrEqual(T1 t1, T2 t2, T3 t3, T4 t4);

    @Support
    @NotNull
    Condition lessOrEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support
    @NotNull
    Condition lessOrEqual(Select<? extends Record4<T1, T2, T3, T4>> select);

    @Support
    @NotNull
    Condition lessOrEqual(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> quantifiedSelect);

    @Support
    @NotNull
    Condition le(Row4<T1, T2, T3, T4> row4);

    @Support
    @NotNull
    Condition le(Record4<T1, T2, T3, T4> record4);

    @Support
    @NotNull
    Condition le(T1 t1, T2 t2, T3 t3, T4 t4);

    @Support
    @NotNull
    Condition le(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support
    @NotNull
    Condition le(Select<? extends Record4<T1, T2, T3, T4>> select);

    @Support
    @NotNull
    Condition le(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterThan(Row4<T1, T2, T3, T4> row4);

    @Support
    @NotNull
    Condition greaterThan(Record4<T1, T2, T3, T4> record4);

    @Support
    @NotNull
    Condition greaterThan(T1 t1, T2 t2, T3 t3, T4 t4);

    @Support
    @NotNull
    Condition greaterThan(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support
    @NotNull
    Condition greaterThan(Select<? extends Record4<T1, T2, T3, T4>> select);

    @Support
    @NotNull
    Condition greaterThan(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> quantifiedSelect);

    @Support
    @NotNull
    Condition gt(Row4<T1, T2, T3, T4> row4);

    @Support
    @NotNull
    Condition gt(Record4<T1, T2, T3, T4> record4);

    @Support
    @NotNull
    Condition gt(T1 t1, T2 t2, T3 t3, T4 t4);

    @Support
    @NotNull
    Condition gt(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support
    @NotNull
    Condition gt(Select<? extends Record4<T1, T2, T3, T4>> select);

    @Support
    @NotNull
    Condition gt(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterOrEqual(Row4<T1, T2, T3, T4> row4);

    @Support
    @NotNull
    Condition greaterOrEqual(Record4<T1, T2, T3, T4> record4);

    @Support
    @NotNull
    Condition greaterOrEqual(T1 t1, T2 t2, T3 t3, T4 t4);

    @Support
    @NotNull
    Condition greaterOrEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support
    @NotNull
    Condition greaterOrEqual(Select<? extends Record4<T1, T2, T3, T4>> select);

    @Support
    @NotNull
    Condition greaterOrEqual(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> quantifiedSelect);

    @Support
    @NotNull
    Condition ge(Row4<T1, T2, T3, T4> row4);

    @Support
    @NotNull
    Condition ge(Record4<T1, T2, T3, T4> record4);

    @Support
    @NotNull
    Condition ge(T1 t1, T2 t2, T3 t3, T4 t4);

    @Support
    @NotNull
    Condition ge(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support
    @NotNull
    Condition ge(Select<? extends Record4<T1, T2, T3, T4>> select);

    @Support
    @NotNull
    Condition ge(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> quantifiedSelect);

    @Support
    @NotNull
    BetweenAndStep4<T1, T2, T3, T4> between(T1 t1, T2 t2, T3 t3, T4 t4);

    @Support
    @NotNull
    BetweenAndStep4<T1, T2, T3, T4> between(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support
    @NotNull
    BetweenAndStep4<T1, T2, T3, T4> between(Row4<T1, T2, T3, T4> row4);

    @Support
    @NotNull
    BetweenAndStep4<T1, T2, T3, T4> between(Record4<T1, T2, T3, T4> record4);

    @Support
    @NotNull
    Condition between(Row4<T1, T2, T3, T4> row4, Row4<T1, T2, T3, T4> row42);

    @Support
    @NotNull
    Condition between(Record4<T1, T2, T3, T4> record4, Record4<T1, T2, T3, T4> record42);

    @Support
    @NotNull
    BetweenAndStep4<T1, T2, T3, T4> betweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4);

    @Support
    @NotNull
    BetweenAndStep4<T1, T2, T3, T4> betweenSymmetric(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support
    @NotNull
    BetweenAndStep4<T1, T2, T3, T4> betweenSymmetric(Row4<T1, T2, T3, T4> row4);

    @Support
    @NotNull
    BetweenAndStep4<T1, T2, T3, T4> betweenSymmetric(Record4<T1, T2, T3, T4> record4);

    @Support
    @NotNull
    Condition betweenSymmetric(Row4<T1, T2, T3, T4> row4, Row4<T1, T2, T3, T4> row42);

    @Support
    @NotNull
    Condition betweenSymmetric(Record4<T1, T2, T3, T4> record4, Record4<T1, T2, T3, T4> record42);

    @Support
    @NotNull
    BetweenAndStep4<T1, T2, T3, T4> notBetween(T1 t1, T2 t2, T3 t3, T4 t4);

    @Support
    @NotNull
    BetweenAndStep4<T1, T2, T3, T4> notBetween(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support
    @NotNull
    BetweenAndStep4<T1, T2, T3, T4> notBetween(Row4<T1, T2, T3, T4> row4);

    @Support
    @NotNull
    BetweenAndStep4<T1, T2, T3, T4> notBetween(Record4<T1, T2, T3, T4> record4);

    @Support
    @NotNull
    Condition notBetween(Row4<T1, T2, T3, T4> row4, Row4<T1, T2, T3, T4> row42);

    @Support
    @NotNull
    Condition notBetween(Record4<T1, T2, T3, T4> record4, Record4<T1, T2, T3, T4> record42);

    @Support
    @NotNull
    BetweenAndStep4<T1, T2, T3, T4> notBetweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4);

    @Support
    @NotNull
    BetweenAndStep4<T1, T2, T3, T4> notBetweenSymmetric(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support
    @NotNull
    BetweenAndStep4<T1, T2, T3, T4> notBetweenSymmetric(Row4<T1, T2, T3, T4> row4);

    @Support
    @NotNull
    BetweenAndStep4<T1, T2, T3, T4> notBetweenSymmetric(Record4<T1, T2, T3, T4> record4);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Row4<T1, T2, T3, T4> row4, Row4<T1, T2, T3, T4> row42);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Record4<T1, T2, T3, T4> record4, Record4<T1, T2, T3, T4> record42);

    @Support
    @NotNull
    Condition in(Collection<? extends Row4<T1, T2, T3, T4>> collection);

    @Support
    @NotNull
    Condition in(Result<? extends Record4<T1, T2, T3, T4>> result);

    @Support
    @NotNull
    Condition in(Row4<T1, T2, T3, T4>... row4Arr);

    @Support
    @NotNull
    Condition in(Record4<T1, T2, T3, T4>... record4Arr);

    @Support
    @NotNull
    Condition in(Select<? extends Record4<T1, T2, T3, T4>> select);

    @Support
    @NotNull
    Condition notIn(Collection<? extends Row4<T1, T2, T3, T4>> collection);

    @Support
    @NotNull
    Condition notIn(Result<? extends Record4<T1, T2, T3, T4>> result);

    @Support
    @NotNull
    Condition notIn(Row4<T1, T2, T3, T4>... row4Arr);

    @Support
    @NotNull
    Condition notIn(Record4<T1, T2, T3, T4>... record4Arr);

    @Support
    @NotNull
    Condition notIn(Select<? extends Record4<T1, T2, T3, T4>> select);
}
