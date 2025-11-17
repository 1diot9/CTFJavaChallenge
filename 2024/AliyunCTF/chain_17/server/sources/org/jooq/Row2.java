package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Row2.class */
public interface Row2<T1, T2> extends Row, SelectField<Record2<T1, T2>> {
    @NotNull
    <U> SelectField<U> mapping(Function2<? super T1, ? super T2, ? extends U> function2);

    @NotNull
    <U> SelectField<U> mapping(Class<U> cls, Function2<? super T1, ? super T2, ? extends U> function2);

    @NotNull
    Field<T1> field1();

    @NotNull
    Field<T2> field2();

    @Support
    @NotNull
    Condition compare(Comparator comparator, Row2<T1, T2> row2);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Record2<T1, T2> record2);

    @Support
    @NotNull
    Condition compare(Comparator comparator, T1 t1, T2 t2);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Field<T1> field, Field<T2> field2);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Select<? extends Record2<T1, T2>> select);

    @Support
    @NotNull
    Condition compare(Comparator comparator, QuantifiedSelect<? extends Record2<T1, T2>> quantifiedSelect);

    @Support
    @NotNull
    Condition equal(Row2<T1, T2> row2);

    @Support
    @NotNull
    Condition equal(Record2<T1, T2> record2);

    @Support
    @NotNull
    Condition equal(T1 t1, T2 t2);

    @Support
    @NotNull
    Condition equal(Field<T1> field, Field<T2> field2);

    @Support
    @NotNull
    Condition equal(Select<? extends Record2<T1, T2>> select);

    @Support
    @NotNull
    Condition equal(QuantifiedSelect<? extends Record2<T1, T2>> quantifiedSelect);

    @Support
    @NotNull
    Condition eq(Row2<T1, T2> row2);

    @Support
    @NotNull
    Condition eq(Record2<T1, T2> record2);

    @Support
    @NotNull
    Condition eq(T1 t1, T2 t2);

    @Support
    @NotNull
    Condition eq(Field<T1> field, Field<T2> field2);

    @Support
    @NotNull
    Condition eq(Select<? extends Record2<T1, T2>> select);

    @Support
    @NotNull
    Condition eq(QuantifiedSelect<? extends Record2<T1, T2>> quantifiedSelect);

    @Support
    @NotNull
    Condition notEqual(Row2<T1, T2> row2);

    @Support
    @NotNull
    Condition notEqual(Record2<T1, T2> record2);

    @Support
    @NotNull
    Condition notEqual(T1 t1, T2 t2);

    @Support
    @NotNull
    Condition notEqual(Field<T1> field, Field<T2> field2);

    @Support
    @NotNull
    Condition notEqual(Select<? extends Record2<T1, T2>> select);

    @Support
    @NotNull
    Condition notEqual(QuantifiedSelect<? extends Record2<T1, T2>> quantifiedSelect);

    @Support
    @NotNull
    Condition ne(Row2<T1, T2> row2);

    @Support
    @NotNull
    Condition ne(Record2<T1, T2> record2);

    @Support
    @NotNull
    Condition ne(T1 t1, T2 t2);

    @Support
    @NotNull
    Condition ne(Field<T1> field, Field<T2> field2);

    @Support
    @NotNull
    Condition ne(Select<? extends Record2<T1, T2>> select);

    @Support
    @NotNull
    Condition ne(QuantifiedSelect<? extends Record2<T1, T2>> quantifiedSelect);

    @Support
    @NotNull
    Condition isDistinctFrom(Row2<T1, T2> row2);

    @Support
    @NotNull
    Condition isDistinctFrom(Record2<T1, T2> record2);

    @Support
    @NotNull
    Condition isDistinctFrom(T1 t1, T2 t2);

    @Support
    @NotNull
    Condition isDistinctFrom(Field<T1> field, Field<T2> field2);

    @Support
    @NotNull
    Condition isDistinctFrom(Select<? extends Record2<T1, T2>> select);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Row2<T1, T2> row2);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Record2<T1, T2> record2);

    @Support
    @NotNull
    Condition isNotDistinctFrom(T1 t1, T2 t2);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Field<T1> field, Field<T2> field2);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Select<? extends Record2<T1, T2>> select);

    @Support
    @NotNull
    Condition lessThan(Row2<T1, T2> row2);

    @Support
    @NotNull
    Condition lessThan(Record2<T1, T2> record2);

    @Support
    @NotNull
    Condition lessThan(T1 t1, T2 t2);

    @Support
    @NotNull
    Condition lessThan(Field<T1> field, Field<T2> field2);

    @Support
    @NotNull
    Condition lessThan(Select<? extends Record2<T1, T2>> select);

    @Support
    @NotNull
    Condition lessThan(QuantifiedSelect<? extends Record2<T1, T2>> quantifiedSelect);

    @Support
    @NotNull
    Condition lt(Row2<T1, T2> row2);

    @Support
    @NotNull
    Condition lt(Record2<T1, T2> record2);

    @Support
    @NotNull
    Condition lt(T1 t1, T2 t2);

    @Support
    @NotNull
    Condition lt(Field<T1> field, Field<T2> field2);

    @Support
    @NotNull
    Condition lt(Select<? extends Record2<T1, T2>> select);

    @Support
    @NotNull
    Condition lt(QuantifiedSelect<? extends Record2<T1, T2>> quantifiedSelect);

    @Support
    @NotNull
    Condition lessOrEqual(Row2<T1, T2> row2);

    @Support
    @NotNull
    Condition lessOrEqual(Record2<T1, T2> record2);

    @Support
    @NotNull
    Condition lessOrEqual(T1 t1, T2 t2);

    @Support
    @NotNull
    Condition lessOrEqual(Field<T1> field, Field<T2> field2);

    @Support
    @NotNull
    Condition lessOrEqual(Select<? extends Record2<T1, T2>> select);

    @Support
    @NotNull
    Condition lessOrEqual(QuantifiedSelect<? extends Record2<T1, T2>> quantifiedSelect);

    @Support
    @NotNull
    Condition le(Row2<T1, T2> row2);

    @Support
    @NotNull
    Condition le(Record2<T1, T2> record2);

    @Support
    @NotNull
    Condition le(T1 t1, T2 t2);

    @Support
    @NotNull
    Condition le(Field<T1> field, Field<T2> field2);

    @Support
    @NotNull
    Condition le(Select<? extends Record2<T1, T2>> select);

    @Support
    @NotNull
    Condition le(QuantifiedSelect<? extends Record2<T1, T2>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterThan(Row2<T1, T2> row2);

    @Support
    @NotNull
    Condition greaterThan(Record2<T1, T2> record2);

    @Support
    @NotNull
    Condition greaterThan(T1 t1, T2 t2);

    @Support
    @NotNull
    Condition greaterThan(Field<T1> field, Field<T2> field2);

    @Support
    @NotNull
    Condition greaterThan(Select<? extends Record2<T1, T2>> select);

    @Support
    @NotNull
    Condition greaterThan(QuantifiedSelect<? extends Record2<T1, T2>> quantifiedSelect);

    @Support
    @NotNull
    Condition gt(Row2<T1, T2> row2);

    @Support
    @NotNull
    Condition gt(Record2<T1, T2> record2);

    @Support
    @NotNull
    Condition gt(T1 t1, T2 t2);

    @Support
    @NotNull
    Condition gt(Field<T1> field, Field<T2> field2);

    @Support
    @NotNull
    Condition gt(Select<? extends Record2<T1, T2>> select);

    @Support
    @NotNull
    Condition gt(QuantifiedSelect<? extends Record2<T1, T2>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterOrEqual(Row2<T1, T2> row2);

    @Support
    @NotNull
    Condition greaterOrEqual(Record2<T1, T2> record2);

    @Support
    @NotNull
    Condition greaterOrEqual(T1 t1, T2 t2);

    @Support
    @NotNull
    Condition greaterOrEqual(Field<T1> field, Field<T2> field2);

    @Support
    @NotNull
    Condition greaterOrEqual(Select<? extends Record2<T1, T2>> select);

    @Support
    @NotNull
    Condition greaterOrEqual(QuantifiedSelect<? extends Record2<T1, T2>> quantifiedSelect);

    @Support
    @NotNull
    Condition ge(Row2<T1, T2> row2);

    @Support
    @NotNull
    Condition ge(Record2<T1, T2> record2);

    @Support
    @NotNull
    Condition ge(T1 t1, T2 t2);

    @Support
    @NotNull
    Condition ge(Field<T1> field, Field<T2> field2);

    @Support
    @NotNull
    Condition ge(Select<? extends Record2<T1, T2>> select);

    @Support
    @NotNull
    Condition ge(QuantifiedSelect<? extends Record2<T1, T2>> quantifiedSelect);

    @Support
    @NotNull
    BetweenAndStep2<T1, T2> between(T1 t1, T2 t2);

    @Support
    @NotNull
    BetweenAndStep2<T1, T2> between(Field<T1> field, Field<T2> field2);

    @Support
    @NotNull
    BetweenAndStep2<T1, T2> between(Row2<T1, T2> row2);

    @Support
    @NotNull
    BetweenAndStep2<T1, T2> between(Record2<T1, T2> record2);

    @Support
    @NotNull
    Condition between(Row2<T1, T2> row2, Row2<T1, T2> row22);

    @Support
    @NotNull
    Condition between(Record2<T1, T2> record2, Record2<T1, T2> record22);

    @Support
    @NotNull
    BetweenAndStep2<T1, T2> betweenSymmetric(T1 t1, T2 t2);

    @Support
    @NotNull
    BetweenAndStep2<T1, T2> betweenSymmetric(Field<T1> field, Field<T2> field2);

    @Support
    @NotNull
    BetweenAndStep2<T1, T2> betweenSymmetric(Row2<T1, T2> row2);

    @Support
    @NotNull
    BetweenAndStep2<T1, T2> betweenSymmetric(Record2<T1, T2> record2);

    @Support
    @NotNull
    Condition betweenSymmetric(Row2<T1, T2> row2, Row2<T1, T2> row22);

    @Support
    @NotNull
    Condition betweenSymmetric(Record2<T1, T2> record2, Record2<T1, T2> record22);

    @Support
    @NotNull
    BetweenAndStep2<T1, T2> notBetween(T1 t1, T2 t2);

    @Support
    @NotNull
    BetweenAndStep2<T1, T2> notBetween(Field<T1> field, Field<T2> field2);

    @Support
    @NotNull
    BetweenAndStep2<T1, T2> notBetween(Row2<T1, T2> row2);

    @Support
    @NotNull
    BetweenAndStep2<T1, T2> notBetween(Record2<T1, T2> record2);

    @Support
    @NotNull
    Condition notBetween(Row2<T1, T2> row2, Row2<T1, T2> row22);

    @Support
    @NotNull
    Condition notBetween(Record2<T1, T2> record2, Record2<T1, T2> record22);

    @Support
    @NotNull
    BetweenAndStep2<T1, T2> notBetweenSymmetric(T1 t1, T2 t2);

    @Support
    @NotNull
    BetweenAndStep2<T1, T2> notBetweenSymmetric(Field<T1> field, Field<T2> field2);

    @Support
    @NotNull
    BetweenAndStep2<T1, T2> notBetweenSymmetric(Row2<T1, T2> row2);

    @Support
    @NotNull
    BetweenAndStep2<T1, T2> notBetweenSymmetric(Record2<T1, T2> record2);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Row2<T1, T2> row2, Row2<T1, T2> row22);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Record2<T1, T2> record2, Record2<T1, T2> record22);

    @Support
    @NotNull
    Condition in(Collection<? extends Row2<T1, T2>> collection);

    @Support
    @NotNull
    Condition in(Result<? extends Record2<T1, T2>> result);

    @Support
    @NotNull
    Condition in(Row2<T1, T2>... row2Arr);

    @Support
    @NotNull
    Condition in(Record2<T1, T2>... record2Arr);

    @Support
    @NotNull
    Condition in(Select<? extends Record2<T1, T2>> select);

    @Support
    @NotNull
    Condition notIn(Collection<? extends Row2<T1, T2>> collection);

    @Support
    @NotNull
    Condition notIn(Result<? extends Record2<T1, T2>> result);

    @Support
    @NotNull
    Condition notIn(Row2<T1, T2>... row2Arr);

    @Support
    @NotNull
    Condition notIn(Record2<T1, T2>... record2Arr);

    @Support
    @NotNull
    Condition notIn(Select<? extends Record2<T1, T2>> select);

    @Support
    @NotNull
    Condition overlaps(T1 t1, T2 t2);

    @Support
    @NotNull
    Condition overlaps(Field<T1> field, Field<T2> field2);

    @Support
    @NotNull
    Condition overlaps(Row2<T1, T2> row2);
}
