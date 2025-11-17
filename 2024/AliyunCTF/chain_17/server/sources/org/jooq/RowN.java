package org.jooq;

import java.util.Collection;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/RowN.class */
public interface RowN extends Row, SelectField<Record> {
    @NotNull
    <U> SelectField<U> mapping(Function<? super Object[], ? extends U> function);

    @NotNull
    <U> SelectField<U> mapping(Class<U> cls, Function<? super Object[], ? extends U> function);

    @Support
    @NotNull
    Condition compare(Comparator comparator, RowN rowN);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Record record);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Object... objArr);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Field<?>... fieldArr);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Select<? extends Record> select);

    @Support
    @NotNull
    Condition compare(Comparator comparator, QuantifiedSelect<? extends Record> quantifiedSelect);

    @Support
    @NotNull
    Condition equal(RowN rowN);

    @Support
    @NotNull
    Condition equal(Record record);

    @Support
    @NotNull
    Condition equal(Object... objArr);

    @Support
    @NotNull
    Condition equal(Field<?>... fieldArr);

    @Support
    @NotNull
    Condition equal(Select<? extends Record> select);

    @Support
    @NotNull
    Condition equal(QuantifiedSelect<? extends Record> quantifiedSelect);

    @Support
    @NotNull
    Condition eq(RowN rowN);

    @Support
    @NotNull
    Condition eq(Record record);

    @Support
    @NotNull
    Condition eq(Object... objArr);

    @Support
    @NotNull
    Condition eq(Field<?>... fieldArr);

    @Support
    @NotNull
    Condition eq(Select<? extends Record> select);

    @Support
    @NotNull
    Condition eq(QuantifiedSelect<? extends Record> quantifiedSelect);

    @Support
    @NotNull
    Condition notEqual(RowN rowN);

    @Support
    @NotNull
    Condition notEqual(Record record);

    @Support
    @NotNull
    Condition notEqual(Object... objArr);

    @Support
    @NotNull
    Condition notEqual(Field<?>... fieldArr);

    @Support
    @NotNull
    Condition notEqual(Select<? extends Record> select);

    @Support
    @NotNull
    Condition notEqual(QuantifiedSelect<? extends Record> quantifiedSelect);

    @Support
    @NotNull
    Condition ne(RowN rowN);

    @Support
    @NotNull
    Condition ne(Record record);

    @Support
    @NotNull
    Condition ne(Object... objArr);

    @Support
    @NotNull
    Condition ne(Field<?>... fieldArr);

    @Support
    @NotNull
    Condition ne(Select<? extends Record> select);

    @Support
    @NotNull
    Condition ne(QuantifiedSelect<? extends Record> quantifiedSelect);

    @Support
    @NotNull
    Condition isDistinctFrom(RowN rowN);

    @Support
    @NotNull
    Condition isDistinctFrom(Record record);

    @Support
    @NotNull
    Condition isDistinctFrom(Object... objArr);

    @Support
    @NotNull
    Condition isDistinctFrom(Field<?>... fieldArr);

    @Support
    @NotNull
    Condition isDistinctFrom(Select<? extends Record> select);

    @Support
    @NotNull
    Condition isNotDistinctFrom(RowN rowN);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Record record);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Object... objArr);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Field<?>... fieldArr);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Select<? extends Record> select);

    @Support
    @NotNull
    Condition lessThan(RowN rowN);

    @Support
    @NotNull
    Condition lessThan(Record record);

    @Support
    @NotNull
    Condition lessThan(Object... objArr);

    @Support
    @NotNull
    Condition lessThan(Field<?>... fieldArr);

    @Support
    @NotNull
    Condition lessThan(Select<? extends Record> select);

    @Support
    @NotNull
    Condition lessThan(QuantifiedSelect<? extends Record> quantifiedSelect);

    @Support
    @NotNull
    Condition lt(RowN rowN);

    @Support
    @NotNull
    Condition lt(Record record);

    @Support
    @NotNull
    Condition lt(Object... objArr);

    @Support
    @NotNull
    Condition lt(Field<?>... fieldArr);

    @Support
    @NotNull
    Condition lt(Select<? extends Record> select);

    @Support
    @NotNull
    Condition lt(QuantifiedSelect<? extends Record> quantifiedSelect);

    @Support
    @NotNull
    Condition lessOrEqual(RowN rowN);

    @Support
    @NotNull
    Condition lessOrEqual(Record record);

    @Support
    @NotNull
    Condition lessOrEqual(Object... objArr);

    @Support
    @NotNull
    Condition lessOrEqual(Field<?>... fieldArr);

    @Support
    @NotNull
    Condition lessOrEqual(Select<? extends Record> select);

    @Support
    @NotNull
    Condition lessOrEqual(QuantifiedSelect<? extends Record> quantifiedSelect);

    @Support
    @NotNull
    Condition le(RowN rowN);

    @Support
    @NotNull
    Condition le(Record record);

    @Support
    @NotNull
    Condition le(Object... objArr);

    @Support
    @NotNull
    Condition le(Field<?>... fieldArr);

    @Support
    @NotNull
    Condition le(Select<? extends Record> select);

    @Support
    @NotNull
    Condition le(QuantifiedSelect<? extends Record> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterThan(RowN rowN);

    @Support
    @NotNull
    Condition greaterThan(Record record);

    @Support
    @NotNull
    Condition greaterThan(Object... objArr);

    @Support
    @NotNull
    Condition greaterThan(Field<?>... fieldArr);

    @Support
    @NotNull
    Condition greaterThan(Select<? extends Record> select);

    @Support
    @NotNull
    Condition greaterThan(QuantifiedSelect<? extends Record> quantifiedSelect);

    @Support
    @NotNull
    Condition gt(RowN rowN);

    @Support
    @NotNull
    Condition gt(Record record);

    @Support
    @NotNull
    Condition gt(Object... objArr);

    @Support
    @NotNull
    Condition gt(Field<?>... fieldArr);

    @Support
    @NotNull
    Condition gt(Select<? extends Record> select);

    @Support
    @NotNull
    Condition gt(QuantifiedSelect<? extends Record> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterOrEqual(RowN rowN);

    @Support
    @NotNull
    Condition greaterOrEqual(Record record);

    @Support
    @NotNull
    Condition greaterOrEqual(Object... objArr);

    @Support
    @NotNull
    Condition greaterOrEqual(Field<?>... fieldArr);

    @Support
    @NotNull
    Condition greaterOrEqual(Select<? extends Record> select);

    @Support
    @NotNull
    Condition greaterOrEqual(QuantifiedSelect<? extends Record> quantifiedSelect);

    @Support
    @NotNull
    Condition ge(RowN rowN);

    @Support
    @NotNull
    Condition ge(Record record);

    @Support
    @NotNull
    Condition ge(Object... objArr);

    @Support
    @NotNull
    Condition ge(Field<?>... fieldArr);

    @Support
    @NotNull
    Condition ge(Select<? extends Record> select);

    @Support
    @NotNull
    Condition ge(QuantifiedSelect<? extends Record> quantifiedSelect);

    @Support
    @NotNull
    BetweenAndStepN between(Object... objArr);

    @Support
    @NotNull
    BetweenAndStepN between(Field<?>... fieldArr);

    @Support
    @NotNull
    BetweenAndStepN between(RowN rowN);

    @Support
    @NotNull
    BetweenAndStepN between(Record record);

    @Support
    @NotNull
    Condition between(RowN rowN, RowN rowN2);

    @Support
    @NotNull
    Condition between(Record record, Record record2);

    @Support
    @NotNull
    BetweenAndStepN betweenSymmetric(Object... objArr);

    @Support
    @NotNull
    BetweenAndStepN betweenSymmetric(Field<?>... fieldArr);

    @Support
    @NotNull
    BetweenAndStepN betweenSymmetric(RowN rowN);

    @Support
    @NotNull
    BetweenAndStepN betweenSymmetric(Record record);

    @Support
    @NotNull
    Condition betweenSymmetric(RowN rowN, RowN rowN2);

    @Support
    @NotNull
    Condition betweenSymmetric(Record record, Record record2);

    @Support
    @NotNull
    BetweenAndStepN notBetween(Object... objArr);

    @Support
    @NotNull
    BetweenAndStepN notBetween(Field<?>... fieldArr);

    @Support
    @NotNull
    BetweenAndStepN notBetween(RowN rowN);

    @Support
    @NotNull
    BetweenAndStepN notBetween(Record record);

    @Support
    @NotNull
    Condition notBetween(RowN rowN, RowN rowN2);

    @Support
    @NotNull
    Condition notBetween(Record record, Record record2);

    @Support
    @NotNull
    BetweenAndStepN notBetweenSymmetric(Object... objArr);

    @Support
    @NotNull
    BetweenAndStepN notBetweenSymmetric(Field<?>... fieldArr);

    @Support
    @NotNull
    BetweenAndStepN notBetweenSymmetric(RowN rowN);

    @Support
    @NotNull
    BetweenAndStepN notBetweenSymmetric(Record record);

    @Support
    @NotNull
    Condition notBetweenSymmetric(RowN rowN, RowN rowN2);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Record record, Record record2);

    @Support
    @NotNull
    Condition in(Collection<? extends RowN> collection);

    @Support
    @NotNull
    Condition in(Result<? extends Record> result);

    @Support
    @NotNull
    Condition in(RowN... rowNArr);

    @Support
    @NotNull
    Condition in(Record... recordArr);

    @Support
    @NotNull
    Condition in(Select<? extends Record> select);

    @Support
    @NotNull
    Condition notIn(Collection<? extends RowN> collection);

    @Support
    @NotNull
    Condition notIn(Result<? extends Record> result);

    @Support
    @NotNull
    Condition notIn(RowN... rowNArr);

    @Support
    @NotNull
    Condition notIn(Record... recordArr);

    @Support
    @NotNull
    Condition notIn(Select<? extends Record> select);
}
