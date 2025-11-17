package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Row3.class */
public interface Row3<T1, T2, T3> extends Row, SelectField<Record3<T1, T2, T3>> {
    @NotNull
    <U> SelectField<U> mapping(Function3<? super T1, ? super T2, ? super T3, ? extends U> function3);

    @NotNull
    <U> SelectField<U> mapping(Class<U> cls, Function3<? super T1, ? super T2, ? super T3, ? extends U> function3);

    @NotNull
    Field<T1> field1();

    @NotNull
    Field<T2> field2();

    @NotNull
    Field<T3> field3();

    @Support
    @NotNull
    Condition compare(Comparator comparator, Row3<T1, T2, T3> row3);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Record3<T1, T2, T3> record3);

    @Support
    @NotNull
    Condition compare(Comparator comparator, T1 t1, T2 t2, T3 t3);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Select<? extends Record3<T1, T2, T3>> select);

    @Support
    @NotNull
    Condition compare(Comparator comparator, QuantifiedSelect<? extends Record3<T1, T2, T3>> quantifiedSelect);

    @Support
    @NotNull
    Condition equal(Row3<T1, T2, T3> row3);

    @Support
    @NotNull
    Condition equal(Record3<T1, T2, T3> record3);

    @Support
    @NotNull
    Condition equal(T1 t1, T2 t2, T3 t3);

    @Support
    @NotNull
    Condition equal(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support
    @NotNull
    Condition equal(Select<? extends Record3<T1, T2, T3>> select);

    @Support
    @NotNull
    Condition equal(QuantifiedSelect<? extends Record3<T1, T2, T3>> quantifiedSelect);

    @Support
    @NotNull
    Condition eq(Row3<T1, T2, T3> row3);

    @Support
    @NotNull
    Condition eq(Record3<T1, T2, T3> record3);

    @Support
    @NotNull
    Condition eq(T1 t1, T2 t2, T3 t3);

    @Support
    @NotNull
    Condition eq(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support
    @NotNull
    Condition eq(Select<? extends Record3<T1, T2, T3>> select);

    @Support
    @NotNull
    Condition eq(QuantifiedSelect<? extends Record3<T1, T2, T3>> quantifiedSelect);

    @Support
    @NotNull
    Condition notEqual(Row3<T1, T2, T3> row3);

    @Support
    @NotNull
    Condition notEqual(Record3<T1, T2, T3> record3);

    @Support
    @NotNull
    Condition notEqual(T1 t1, T2 t2, T3 t3);

    @Support
    @NotNull
    Condition notEqual(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support
    @NotNull
    Condition notEqual(Select<? extends Record3<T1, T2, T3>> select);

    @Support
    @NotNull
    Condition notEqual(QuantifiedSelect<? extends Record3<T1, T2, T3>> quantifiedSelect);

    @Support
    @NotNull
    Condition ne(Row3<T1, T2, T3> row3);

    @Support
    @NotNull
    Condition ne(Record3<T1, T2, T3> record3);

    @Support
    @NotNull
    Condition ne(T1 t1, T2 t2, T3 t3);

    @Support
    @NotNull
    Condition ne(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support
    @NotNull
    Condition ne(Select<? extends Record3<T1, T2, T3>> select);

    @Support
    @NotNull
    Condition ne(QuantifiedSelect<? extends Record3<T1, T2, T3>> quantifiedSelect);

    @Support
    @NotNull
    Condition isDistinctFrom(Row3<T1, T2, T3> row3);

    @Support
    @NotNull
    Condition isDistinctFrom(Record3<T1, T2, T3> record3);

    @Support
    @NotNull
    Condition isDistinctFrom(T1 t1, T2 t2, T3 t3);

    @Support
    @NotNull
    Condition isDistinctFrom(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support
    @NotNull
    Condition isDistinctFrom(Select<? extends Record3<T1, T2, T3>> select);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Row3<T1, T2, T3> row3);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Record3<T1, T2, T3> record3);

    @Support
    @NotNull
    Condition isNotDistinctFrom(T1 t1, T2 t2, T3 t3);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Select<? extends Record3<T1, T2, T3>> select);

    @Support
    @NotNull
    Condition lessThan(Row3<T1, T2, T3> row3);

    @Support
    @NotNull
    Condition lessThan(Record3<T1, T2, T3> record3);

    @Support
    @NotNull
    Condition lessThan(T1 t1, T2 t2, T3 t3);

    @Support
    @NotNull
    Condition lessThan(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support
    @NotNull
    Condition lessThan(Select<? extends Record3<T1, T2, T3>> select);

    @Support
    @NotNull
    Condition lessThan(QuantifiedSelect<? extends Record3<T1, T2, T3>> quantifiedSelect);

    @Support
    @NotNull
    Condition lt(Row3<T1, T2, T3> row3);

    @Support
    @NotNull
    Condition lt(Record3<T1, T2, T3> record3);

    @Support
    @NotNull
    Condition lt(T1 t1, T2 t2, T3 t3);

    @Support
    @NotNull
    Condition lt(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support
    @NotNull
    Condition lt(Select<? extends Record3<T1, T2, T3>> select);

    @Support
    @NotNull
    Condition lt(QuantifiedSelect<? extends Record3<T1, T2, T3>> quantifiedSelect);

    @Support
    @NotNull
    Condition lessOrEqual(Row3<T1, T2, T3> row3);

    @Support
    @NotNull
    Condition lessOrEqual(Record3<T1, T2, T3> record3);

    @Support
    @NotNull
    Condition lessOrEqual(T1 t1, T2 t2, T3 t3);

    @Support
    @NotNull
    Condition lessOrEqual(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support
    @NotNull
    Condition lessOrEqual(Select<? extends Record3<T1, T2, T3>> select);

    @Support
    @NotNull
    Condition lessOrEqual(QuantifiedSelect<? extends Record3<T1, T2, T3>> quantifiedSelect);

    @Support
    @NotNull
    Condition le(Row3<T1, T2, T3> row3);

    @Support
    @NotNull
    Condition le(Record3<T1, T2, T3> record3);

    @Support
    @NotNull
    Condition le(T1 t1, T2 t2, T3 t3);

    @Support
    @NotNull
    Condition le(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support
    @NotNull
    Condition le(Select<? extends Record3<T1, T2, T3>> select);

    @Support
    @NotNull
    Condition le(QuantifiedSelect<? extends Record3<T1, T2, T3>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterThan(Row3<T1, T2, T3> row3);

    @Support
    @NotNull
    Condition greaterThan(Record3<T1, T2, T3> record3);

    @Support
    @NotNull
    Condition greaterThan(T1 t1, T2 t2, T3 t3);

    @Support
    @NotNull
    Condition greaterThan(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support
    @NotNull
    Condition greaterThan(Select<? extends Record3<T1, T2, T3>> select);

    @Support
    @NotNull
    Condition greaterThan(QuantifiedSelect<? extends Record3<T1, T2, T3>> quantifiedSelect);

    @Support
    @NotNull
    Condition gt(Row3<T1, T2, T3> row3);

    @Support
    @NotNull
    Condition gt(Record3<T1, T2, T3> record3);

    @Support
    @NotNull
    Condition gt(T1 t1, T2 t2, T3 t3);

    @Support
    @NotNull
    Condition gt(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support
    @NotNull
    Condition gt(Select<? extends Record3<T1, T2, T3>> select);

    @Support
    @NotNull
    Condition gt(QuantifiedSelect<? extends Record3<T1, T2, T3>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterOrEqual(Row3<T1, T2, T3> row3);

    @Support
    @NotNull
    Condition greaterOrEqual(Record3<T1, T2, T3> record3);

    @Support
    @NotNull
    Condition greaterOrEqual(T1 t1, T2 t2, T3 t3);

    @Support
    @NotNull
    Condition greaterOrEqual(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support
    @NotNull
    Condition greaterOrEqual(Select<? extends Record3<T1, T2, T3>> select);

    @Support
    @NotNull
    Condition greaterOrEqual(QuantifiedSelect<? extends Record3<T1, T2, T3>> quantifiedSelect);

    @Support
    @NotNull
    Condition ge(Row3<T1, T2, T3> row3);

    @Support
    @NotNull
    Condition ge(Record3<T1, T2, T3> record3);

    @Support
    @NotNull
    Condition ge(T1 t1, T2 t2, T3 t3);

    @Support
    @NotNull
    Condition ge(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support
    @NotNull
    Condition ge(Select<? extends Record3<T1, T2, T3>> select);

    @Support
    @NotNull
    Condition ge(QuantifiedSelect<? extends Record3<T1, T2, T3>> quantifiedSelect);

    @Support
    @NotNull
    BetweenAndStep3<T1, T2, T3> between(T1 t1, T2 t2, T3 t3);

    @Support
    @NotNull
    BetweenAndStep3<T1, T2, T3> between(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support
    @NotNull
    BetweenAndStep3<T1, T2, T3> between(Row3<T1, T2, T3> row3);

    @Support
    @NotNull
    BetweenAndStep3<T1, T2, T3> between(Record3<T1, T2, T3> record3);

    @Support
    @NotNull
    Condition between(Row3<T1, T2, T3> row3, Row3<T1, T2, T3> row32);

    @Support
    @NotNull
    Condition between(Record3<T1, T2, T3> record3, Record3<T1, T2, T3> record32);

    @Support
    @NotNull
    BetweenAndStep3<T1, T2, T3> betweenSymmetric(T1 t1, T2 t2, T3 t3);

    @Support
    @NotNull
    BetweenAndStep3<T1, T2, T3> betweenSymmetric(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support
    @NotNull
    BetweenAndStep3<T1, T2, T3> betweenSymmetric(Row3<T1, T2, T3> row3);

    @Support
    @NotNull
    BetweenAndStep3<T1, T2, T3> betweenSymmetric(Record3<T1, T2, T3> record3);

    @Support
    @NotNull
    Condition betweenSymmetric(Row3<T1, T2, T3> row3, Row3<T1, T2, T3> row32);

    @Support
    @NotNull
    Condition betweenSymmetric(Record3<T1, T2, T3> record3, Record3<T1, T2, T3> record32);

    @Support
    @NotNull
    BetweenAndStep3<T1, T2, T3> notBetween(T1 t1, T2 t2, T3 t3);

    @Support
    @NotNull
    BetweenAndStep3<T1, T2, T3> notBetween(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support
    @NotNull
    BetweenAndStep3<T1, T2, T3> notBetween(Row3<T1, T2, T3> row3);

    @Support
    @NotNull
    BetweenAndStep3<T1, T2, T3> notBetween(Record3<T1, T2, T3> record3);

    @Support
    @NotNull
    Condition notBetween(Row3<T1, T2, T3> row3, Row3<T1, T2, T3> row32);

    @Support
    @NotNull
    Condition notBetween(Record3<T1, T2, T3> record3, Record3<T1, T2, T3> record32);

    @Support
    @NotNull
    BetweenAndStep3<T1, T2, T3> notBetweenSymmetric(T1 t1, T2 t2, T3 t3);

    @Support
    @NotNull
    BetweenAndStep3<T1, T2, T3> notBetweenSymmetric(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support
    @NotNull
    BetweenAndStep3<T1, T2, T3> notBetweenSymmetric(Row3<T1, T2, T3> row3);

    @Support
    @NotNull
    BetweenAndStep3<T1, T2, T3> notBetweenSymmetric(Record3<T1, T2, T3> record3);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Row3<T1, T2, T3> row3, Row3<T1, T2, T3> row32);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Record3<T1, T2, T3> record3, Record3<T1, T2, T3> record32);

    @Support
    @NotNull
    Condition in(Collection<? extends Row3<T1, T2, T3>> collection);

    @Support
    @NotNull
    Condition in(Result<? extends Record3<T1, T2, T3>> result);

    @Support
    @NotNull
    Condition in(Row3<T1, T2, T3>... row3Arr);

    @Support
    @NotNull
    Condition in(Record3<T1, T2, T3>... record3Arr);

    @Support
    @NotNull
    Condition in(Select<? extends Record3<T1, T2, T3>> select);

    @Support
    @NotNull
    Condition notIn(Collection<? extends Row3<T1, T2, T3>> collection);

    @Support
    @NotNull
    Condition notIn(Result<? extends Record3<T1, T2, T3>> result);

    @Support
    @NotNull
    Condition notIn(Row3<T1, T2, T3>... row3Arr);

    @Support
    @NotNull
    Condition notIn(Record3<T1, T2, T3>... record3Arr);

    @Support
    @NotNull
    Condition notIn(Select<? extends Record3<T1, T2, T3>> select);
}
