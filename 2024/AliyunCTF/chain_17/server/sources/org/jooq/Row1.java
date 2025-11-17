package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Row1.class */
public interface Row1<T1> extends Row, SelectField<Record1<T1>> {
    @NotNull
    <U> SelectField<U> mapping(Function1<? super T1, ? extends U> function1);

    @NotNull
    <U> SelectField<U> mapping(Class<U> cls, Function1<? super T1, ? extends U> function1);

    @NotNull
    Field<T1> field1();

    @Support
    @NotNull
    Condition compare(Comparator comparator, Row1<T1> row1);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Record1<T1> record1);

    @Support
    @NotNull
    Condition compare(Comparator comparator, T1 t1);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Field<T1> field);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Select<? extends Record1<T1>> select);

    @Support
    @NotNull
    Condition compare(Comparator comparator, QuantifiedSelect<? extends Record1<T1>> quantifiedSelect);

    @Support
    @NotNull
    Condition equal(Row1<T1> row1);

    @Support
    @NotNull
    Condition equal(Record1<T1> record1);

    @Support
    @NotNull
    Condition equal(T1 t1);

    @Support
    @NotNull
    Condition equal(Field<T1> field);

    @Support
    @NotNull
    Condition equal(Select<? extends Record1<T1>> select);

    @Support
    @NotNull
    Condition equal(QuantifiedSelect<? extends Record1<T1>> quantifiedSelect);

    @Support
    @NotNull
    Condition eq(Row1<T1> row1);

    @Support
    @NotNull
    Condition eq(Record1<T1> record1);

    @Support
    @NotNull
    Condition eq(T1 t1);

    @Support
    @NotNull
    Condition eq(Field<T1> field);

    @Support
    @NotNull
    Condition eq(Select<? extends Record1<T1>> select);

    @Support
    @NotNull
    Condition eq(QuantifiedSelect<? extends Record1<T1>> quantifiedSelect);

    @Support
    @NotNull
    Condition notEqual(Row1<T1> row1);

    @Support
    @NotNull
    Condition notEqual(Record1<T1> record1);

    @Support
    @NotNull
    Condition notEqual(T1 t1);

    @Support
    @NotNull
    Condition notEqual(Field<T1> field);

    @Support
    @NotNull
    Condition notEqual(Select<? extends Record1<T1>> select);

    @Support
    @NotNull
    Condition notEqual(QuantifiedSelect<? extends Record1<T1>> quantifiedSelect);

    @Support
    @NotNull
    Condition ne(Row1<T1> row1);

    @Support
    @NotNull
    Condition ne(Record1<T1> record1);

    @Support
    @NotNull
    Condition ne(T1 t1);

    @Support
    @NotNull
    Condition ne(Field<T1> field);

    @Support
    @NotNull
    Condition ne(Select<? extends Record1<T1>> select);

    @Support
    @NotNull
    Condition ne(QuantifiedSelect<? extends Record1<T1>> quantifiedSelect);

    @Support
    @NotNull
    Condition isDistinctFrom(Row1<T1> row1);

    @Support
    @NotNull
    Condition isDistinctFrom(Record1<T1> record1);

    @Support
    @NotNull
    Condition isDistinctFrom(T1 t1);

    @Support
    @NotNull
    Condition isDistinctFrom(Field<T1> field);

    @Support
    @NotNull
    Condition isDistinctFrom(Select<? extends Record1<T1>> select);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Row1<T1> row1);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Record1<T1> record1);

    @Support
    @NotNull
    Condition isNotDistinctFrom(T1 t1);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Field<T1> field);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Select<? extends Record1<T1>> select);

    @Support
    @NotNull
    Condition lessThan(Row1<T1> row1);

    @Support
    @NotNull
    Condition lessThan(Record1<T1> record1);

    @Support
    @NotNull
    Condition lessThan(T1 t1);

    @Support
    @NotNull
    Condition lessThan(Field<T1> field);

    @Support
    @NotNull
    Condition lessThan(Select<? extends Record1<T1>> select);

    @Support
    @NotNull
    Condition lessThan(QuantifiedSelect<? extends Record1<T1>> quantifiedSelect);

    @Support
    @NotNull
    Condition lt(Row1<T1> row1);

    @Support
    @NotNull
    Condition lt(Record1<T1> record1);

    @Support
    @NotNull
    Condition lt(T1 t1);

    @Support
    @NotNull
    Condition lt(Field<T1> field);

    @Support
    @NotNull
    Condition lt(Select<? extends Record1<T1>> select);

    @Support
    @NotNull
    Condition lt(QuantifiedSelect<? extends Record1<T1>> quantifiedSelect);

    @Support
    @NotNull
    Condition lessOrEqual(Row1<T1> row1);

    @Support
    @NotNull
    Condition lessOrEqual(Record1<T1> record1);

    @Support
    @NotNull
    Condition lessOrEqual(T1 t1);

    @Support
    @NotNull
    Condition lessOrEqual(Field<T1> field);

    @Support
    @NotNull
    Condition lessOrEqual(Select<? extends Record1<T1>> select);

    @Support
    @NotNull
    Condition lessOrEqual(QuantifiedSelect<? extends Record1<T1>> quantifiedSelect);

    @Support
    @NotNull
    Condition le(Row1<T1> row1);

    @Support
    @NotNull
    Condition le(Record1<T1> record1);

    @Support
    @NotNull
    Condition le(T1 t1);

    @Support
    @NotNull
    Condition le(Field<T1> field);

    @Support
    @NotNull
    Condition le(Select<? extends Record1<T1>> select);

    @Support
    @NotNull
    Condition le(QuantifiedSelect<? extends Record1<T1>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterThan(Row1<T1> row1);

    @Support
    @NotNull
    Condition greaterThan(Record1<T1> record1);

    @Support
    @NotNull
    Condition greaterThan(T1 t1);

    @Support
    @NotNull
    Condition greaterThan(Field<T1> field);

    @Support
    @NotNull
    Condition greaterThan(Select<? extends Record1<T1>> select);

    @Support
    @NotNull
    Condition greaterThan(QuantifiedSelect<? extends Record1<T1>> quantifiedSelect);

    @Support
    @NotNull
    Condition gt(Row1<T1> row1);

    @Support
    @NotNull
    Condition gt(Record1<T1> record1);

    @Support
    @NotNull
    Condition gt(T1 t1);

    @Support
    @NotNull
    Condition gt(Field<T1> field);

    @Support
    @NotNull
    Condition gt(Select<? extends Record1<T1>> select);

    @Support
    @NotNull
    Condition gt(QuantifiedSelect<? extends Record1<T1>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterOrEqual(Row1<T1> row1);

    @Support
    @NotNull
    Condition greaterOrEqual(Record1<T1> record1);

    @Support
    @NotNull
    Condition greaterOrEqual(T1 t1);

    @Support
    @NotNull
    Condition greaterOrEqual(Field<T1> field);

    @Support
    @NotNull
    Condition greaterOrEqual(Select<? extends Record1<T1>> select);

    @Support
    @NotNull
    Condition greaterOrEqual(QuantifiedSelect<? extends Record1<T1>> quantifiedSelect);

    @Support
    @NotNull
    Condition ge(Row1<T1> row1);

    @Support
    @NotNull
    Condition ge(Record1<T1> record1);

    @Support
    @NotNull
    Condition ge(T1 t1);

    @Support
    @NotNull
    Condition ge(Field<T1> field);

    @Support
    @NotNull
    Condition ge(Select<? extends Record1<T1>> select);

    @Support
    @NotNull
    Condition ge(QuantifiedSelect<? extends Record1<T1>> quantifiedSelect);

    @Support
    @NotNull
    BetweenAndStep1<T1> between(T1 t1);

    @Support
    @NotNull
    BetweenAndStep1<T1> between(Field<T1> field);

    @Support
    @NotNull
    BetweenAndStep1<T1> between(Row1<T1> row1);

    @Support
    @NotNull
    BetweenAndStep1<T1> between(Record1<T1> record1);

    @Support
    @NotNull
    Condition between(Row1<T1> row1, Row1<T1> row12);

    @Support
    @NotNull
    Condition between(Record1<T1> record1, Record1<T1> record12);

    @Support
    @NotNull
    BetweenAndStep1<T1> betweenSymmetric(T1 t1);

    @Support
    @NotNull
    BetweenAndStep1<T1> betweenSymmetric(Field<T1> field);

    @Support
    @NotNull
    BetweenAndStep1<T1> betweenSymmetric(Row1<T1> row1);

    @Support
    @NotNull
    BetweenAndStep1<T1> betweenSymmetric(Record1<T1> record1);

    @Support
    @NotNull
    Condition betweenSymmetric(Row1<T1> row1, Row1<T1> row12);

    @Support
    @NotNull
    Condition betweenSymmetric(Record1<T1> record1, Record1<T1> record12);

    @Support
    @NotNull
    BetweenAndStep1<T1> notBetween(T1 t1);

    @Support
    @NotNull
    BetweenAndStep1<T1> notBetween(Field<T1> field);

    @Support
    @NotNull
    BetweenAndStep1<T1> notBetween(Row1<T1> row1);

    @Support
    @NotNull
    BetweenAndStep1<T1> notBetween(Record1<T1> record1);

    @Support
    @NotNull
    Condition notBetween(Row1<T1> row1, Row1<T1> row12);

    @Support
    @NotNull
    Condition notBetween(Record1<T1> record1, Record1<T1> record12);

    @Support
    @NotNull
    BetweenAndStep1<T1> notBetweenSymmetric(T1 t1);

    @Support
    @NotNull
    BetweenAndStep1<T1> notBetweenSymmetric(Field<T1> field);

    @Support
    @NotNull
    BetweenAndStep1<T1> notBetweenSymmetric(Row1<T1> row1);

    @Support
    @NotNull
    BetweenAndStep1<T1> notBetweenSymmetric(Record1<T1> record1);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Row1<T1> row1, Row1<T1> row12);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Record1<T1> record1, Record1<T1> record12);

    @Support
    @NotNull
    Condition in(Collection<? extends Row1<T1>> collection);

    @Support
    @NotNull
    Condition in(Result<? extends Record1<T1>> result);

    @Support
    @NotNull
    Condition in(Row1<T1>... row1Arr);

    @Support
    @NotNull
    Condition in(Record1<T1>... record1Arr);

    @Support
    @NotNull
    Condition in(Select<? extends Record1<T1>> select);

    @Support
    @NotNull
    Condition notIn(Collection<? extends Row1<T1>> collection);

    @Support
    @NotNull
    Condition notIn(Result<? extends Record1<T1>> result);

    @Support
    @NotNull
    Condition notIn(Row1<T1>... row1Arr);

    @Support
    @NotNull
    Condition notIn(Record1<T1>... record1Arr);

    @Support
    @NotNull
    Condition notIn(Select<? extends Record1<T1>> select);
}
