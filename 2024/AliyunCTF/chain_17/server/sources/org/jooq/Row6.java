package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Row6.class */
public interface Row6<T1, T2, T3, T4, T5, T6> extends Row, SelectField<Record6<T1, T2, T3, T4, T5, T6>> {
    @NotNull
    <U> SelectField<U> mapping(Function6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? extends U> function6);

    @NotNull
    <U> SelectField<U> mapping(Class<U> cls, Function6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? extends U> function6);

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

    @Support
    @NotNull
    Condition compare(Comparator comparator, Row6<T1, T2, T3, T4, T5, T6> row6);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Record6<T1, T2, T3, T4, T5, T6> record6);

    @Support
    @NotNull
    Condition compare(Comparator comparator, T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Select<? extends Record6<T1, T2, T3, T4, T5, T6>> select);

    @Support
    @NotNull
    Condition compare(Comparator comparator, QuantifiedSelect<? extends Record6<T1, T2, T3, T4, T5, T6>> quantifiedSelect);

    @Support
    @NotNull
    Condition equal(Row6<T1, T2, T3, T4, T5, T6> row6);

    @Support
    @NotNull
    Condition equal(Record6<T1, T2, T3, T4, T5, T6> record6);

    @Support
    @NotNull
    Condition equal(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

    @Support
    @NotNull
    Condition equal(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support
    @NotNull
    Condition equal(Select<? extends Record6<T1, T2, T3, T4, T5, T6>> select);

    @Support
    @NotNull
    Condition equal(QuantifiedSelect<? extends Record6<T1, T2, T3, T4, T5, T6>> quantifiedSelect);

    @Support
    @NotNull
    Condition eq(Row6<T1, T2, T3, T4, T5, T6> row6);

    @Support
    @NotNull
    Condition eq(Record6<T1, T2, T3, T4, T5, T6> record6);

    @Support
    @NotNull
    Condition eq(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

    @Support
    @NotNull
    Condition eq(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support
    @NotNull
    Condition eq(Select<? extends Record6<T1, T2, T3, T4, T5, T6>> select);

    @Support
    @NotNull
    Condition eq(QuantifiedSelect<? extends Record6<T1, T2, T3, T4, T5, T6>> quantifiedSelect);

    @Support
    @NotNull
    Condition notEqual(Row6<T1, T2, T3, T4, T5, T6> row6);

    @Support
    @NotNull
    Condition notEqual(Record6<T1, T2, T3, T4, T5, T6> record6);

    @Support
    @NotNull
    Condition notEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

    @Support
    @NotNull
    Condition notEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support
    @NotNull
    Condition notEqual(Select<? extends Record6<T1, T2, T3, T4, T5, T6>> select);

    @Support
    @NotNull
    Condition notEqual(QuantifiedSelect<? extends Record6<T1, T2, T3, T4, T5, T6>> quantifiedSelect);

    @Support
    @NotNull
    Condition ne(Row6<T1, T2, T3, T4, T5, T6> row6);

    @Support
    @NotNull
    Condition ne(Record6<T1, T2, T3, T4, T5, T6> record6);

    @Support
    @NotNull
    Condition ne(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

    @Support
    @NotNull
    Condition ne(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support
    @NotNull
    Condition ne(Select<? extends Record6<T1, T2, T3, T4, T5, T6>> select);

    @Support
    @NotNull
    Condition ne(QuantifiedSelect<? extends Record6<T1, T2, T3, T4, T5, T6>> quantifiedSelect);

    @Support
    @NotNull
    Condition isDistinctFrom(Row6<T1, T2, T3, T4, T5, T6> row6);

    @Support
    @NotNull
    Condition isDistinctFrom(Record6<T1, T2, T3, T4, T5, T6> record6);

    @Support
    @NotNull
    Condition isDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

    @Support
    @NotNull
    Condition isDistinctFrom(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support
    @NotNull
    Condition isDistinctFrom(Select<? extends Record6<T1, T2, T3, T4, T5, T6>> select);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Row6<T1, T2, T3, T4, T5, T6> row6);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Record6<T1, T2, T3, T4, T5, T6> record6);

    @Support
    @NotNull
    Condition isNotDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Select<? extends Record6<T1, T2, T3, T4, T5, T6>> select);

    @Support
    @NotNull
    Condition lessThan(Row6<T1, T2, T3, T4, T5, T6> row6);

    @Support
    @NotNull
    Condition lessThan(Record6<T1, T2, T3, T4, T5, T6> record6);

    @Support
    @NotNull
    Condition lessThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

    @Support
    @NotNull
    Condition lessThan(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support
    @NotNull
    Condition lessThan(Select<? extends Record6<T1, T2, T3, T4, T5, T6>> select);

    @Support
    @NotNull
    Condition lessThan(QuantifiedSelect<? extends Record6<T1, T2, T3, T4, T5, T6>> quantifiedSelect);

    @Support
    @NotNull
    Condition lt(Row6<T1, T2, T3, T4, T5, T6> row6);

    @Support
    @NotNull
    Condition lt(Record6<T1, T2, T3, T4, T5, T6> record6);

    @Support
    @NotNull
    Condition lt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

    @Support
    @NotNull
    Condition lt(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support
    @NotNull
    Condition lt(Select<? extends Record6<T1, T2, T3, T4, T5, T6>> select);

    @Support
    @NotNull
    Condition lt(QuantifiedSelect<? extends Record6<T1, T2, T3, T4, T5, T6>> quantifiedSelect);

    @Support
    @NotNull
    Condition lessOrEqual(Row6<T1, T2, T3, T4, T5, T6> row6);

    @Support
    @NotNull
    Condition lessOrEqual(Record6<T1, T2, T3, T4, T5, T6> record6);

    @Support
    @NotNull
    Condition lessOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

    @Support
    @NotNull
    Condition lessOrEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support
    @NotNull
    Condition lessOrEqual(Select<? extends Record6<T1, T2, T3, T4, T5, T6>> select);

    @Support
    @NotNull
    Condition lessOrEqual(QuantifiedSelect<? extends Record6<T1, T2, T3, T4, T5, T6>> quantifiedSelect);

    @Support
    @NotNull
    Condition le(Row6<T1, T2, T3, T4, T5, T6> row6);

    @Support
    @NotNull
    Condition le(Record6<T1, T2, T3, T4, T5, T6> record6);

    @Support
    @NotNull
    Condition le(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

    @Support
    @NotNull
    Condition le(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support
    @NotNull
    Condition le(Select<? extends Record6<T1, T2, T3, T4, T5, T6>> select);

    @Support
    @NotNull
    Condition le(QuantifiedSelect<? extends Record6<T1, T2, T3, T4, T5, T6>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterThan(Row6<T1, T2, T3, T4, T5, T6> row6);

    @Support
    @NotNull
    Condition greaterThan(Record6<T1, T2, T3, T4, T5, T6> record6);

    @Support
    @NotNull
    Condition greaterThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

    @Support
    @NotNull
    Condition greaterThan(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support
    @NotNull
    Condition greaterThan(Select<? extends Record6<T1, T2, T3, T4, T5, T6>> select);

    @Support
    @NotNull
    Condition greaterThan(QuantifiedSelect<? extends Record6<T1, T2, T3, T4, T5, T6>> quantifiedSelect);

    @Support
    @NotNull
    Condition gt(Row6<T1, T2, T3, T4, T5, T6> row6);

    @Support
    @NotNull
    Condition gt(Record6<T1, T2, T3, T4, T5, T6> record6);

    @Support
    @NotNull
    Condition gt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

    @Support
    @NotNull
    Condition gt(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support
    @NotNull
    Condition gt(Select<? extends Record6<T1, T2, T3, T4, T5, T6>> select);

    @Support
    @NotNull
    Condition gt(QuantifiedSelect<? extends Record6<T1, T2, T3, T4, T5, T6>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterOrEqual(Row6<T1, T2, T3, T4, T5, T6> row6);

    @Support
    @NotNull
    Condition greaterOrEqual(Record6<T1, T2, T3, T4, T5, T6> record6);

    @Support
    @NotNull
    Condition greaterOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

    @Support
    @NotNull
    Condition greaterOrEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support
    @NotNull
    Condition greaterOrEqual(Select<? extends Record6<T1, T2, T3, T4, T5, T6>> select);

    @Support
    @NotNull
    Condition greaterOrEqual(QuantifiedSelect<? extends Record6<T1, T2, T3, T4, T5, T6>> quantifiedSelect);

    @Support
    @NotNull
    Condition ge(Row6<T1, T2, T3, T4, T5, T6> row6);

    @Support
    @NotNull
    Condition ge(Record6<T1, T2, T3, T4, T5, T6> record6);

    @Support
    @NotNull
    Condition ge(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

    @Support
    @NotNull
    Condition ge(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support
    @NotNull
    Condition ge(Select<? extends Record6<T1, T2, T3, T4, T5, T6>> select);

    @Support
    @NotNull
    Condition ge(QuantifiedSelect<? extends Record6<T1, T2, T3, T4, T5, T6>> quantifiedSelect);

    @Support
    @NotNull
    BetweenAndStep6<T1, T2, T3, T4, T5, T6> between(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

    @Support
    @NotNull
    BetweenAndStep6<T1, T2, T3, T4, T5, T6> between(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support
    @NotNull
    BetweenAndStep6<T1, T2, T3, T4, T5, T6> between(Row6<T1, T2, T3, T4, T5, T6> row6);

    @Support
    @NotNull
    BetweenAndStep6<T1, T2, T3, T4, T5, T6> between(Record6<T1, T2, T3, T4, T5, T6> record6);

    @Support
    @NotNull
    Condition between(Row6<T1, T2, T3, T4, T5, T6> row6, Row6<T1, T2, T3, T4, T5, T6> row62);

    @Support
    @NotNull
    Condition between(Record6<T1, T2, T3, T4, T5, T6> record6, Record6<T1, T2, T3, T4, T5, T6> record62);

    @Support
    @NotNull
    BetweenAndStep6<T1, T2, T3, T4, T5, T6> betweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

    @Support
    @NotNull
    BetweenAndStep6<T1, T2, T3, T4, T5, T6> betweenSymmetric(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support
    @NotNull
    BetweenAndStep6<T1, T2, T3, T4, T5, T6> betweenSymmetric(Row6<T1, T2, T3, T4, T5, T6> row6);

    @Support
    @NotNull
    BetweenAndStep6<T1, T2, T3, T4, T5, T6> betweenSymmetric(Record6<T1, T2, T3, T4, T5, T6> record6);

    @Support
    @NotNull
    Condition betweenSymmetric(Row6<T1, T2, T3, T4, T5, T6> row6, Row6<T1, T2, T3, T4, T5, T6> row62);

    @Support
    @NotNull
    Condition betweenSymmetric(Record6<T1, T2, T3, T4, T5, T6> record6, Record6<T1, T2, T3, T4, T5, T6> record62);

    @Support
    @NotNull
    BetweenAndStep6<T1, T2, T3, T4, T5, T6> notBetween(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

    @Support
    @NotNull
    BetweenAndStep6<T1, T2, T3, T4, T5, T6> notBetween(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support
    @NotNull
    BetweenAndStep6<T1, T2, T3, T4, T5, T6> notBetween(Row6<T1, T2, T3, T4, T5, T6> row6);

    @Support
    @NotNull
    BetweenAndStep6<T1, T2, T3, T4, T5, T6> notBetween(Record6<T1, T2, T3, T4, T5, T6> record6);

    @Support
    @NotNull
    Condition notBetween(Row6<T1, T2, T3, T4, T5, T6> row6, Row6<T1, T2, T3, T4, T5, T6> row62);

    @Support
    @NotNull
    Condition notBetween(Record6<T1, T2, T3, T4, T5, T6> record6, Record6<T1, T2, T3, T4, T5, T6> record62);

    @Support
    @NotNull
    BetweenAndStep6<T1, T2, T3, T4, T5, T6> notBetweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

    @Support
    @NotNull
    BetweenAndStep6<T1, T2, T3, T4, T5, T6> notBetweenSymmetric(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support
    @NotNull
    BetweenAndStep6<T1, T2, T3, T4, T5, T6> notBetweenSymmetric(Row6<T1, T2, T3, T4, T5, T6> row6);

    @Support
    @NotNull
    BetweenAndStep6<T1, T2, T3, T4, T5, T6> notBetweenSymmetric(Record6<T1, T2, T3, T4, T5, T6> record6);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Row6<T1, T2, T3, T4, T5, T6> row6, Row6<T1, T2, T3, T4, T5, T6> row62);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Record6<T1, T2, T3, T4, T5, T6> record6, Record6<T1, T2, T3, T4, T5, T6> record62);

    @Support
    @NotNull
    Condition in(Collection<? extends Row6<T1, T2, T3, T4, T5, T6>> collection);

    @Support
    @NotNull
    Condition in(Result<? extends Record6<T1, T2, T3, T4, T5, T6>> result);

    @Support
    @NotNull
    Condition in(Row6<T1, T2, T3, T4, T5, T6>... row6Arr);

    @Support
    @NotNull
    Condition in(Record6<T1, T2, T3, T4, T5, T6>... record6Arr);

    @Support
    @NotNull
    Condition in(Select<? extends Record6<T1, T2, T3, T4, T5, T6>> select);

    @Support
    @NotNull
    Condition notIn(Collection<? extends Row6<T1, T2, T3, T4, T5, T6>> collection);

    @Support
    @NotNull
    Condition notIn(Result<? extends Record6<T1, T2, T3, T4, T5, T6>> result);

    @Support
    @NotNull
    Condition notIn(Row6<T1, T2, T3, T4, T5, T6>... row6Arr);

    @Support
    @NotNull
    Condition notIn(Record6<T1, T2, T3, T4, T5, T6>... record6Arr);

    @Support
    @NotNull
    Condition notIn(Select<? extends Record6<T1, T2, T3, T4, T5, T6>> select);
}
