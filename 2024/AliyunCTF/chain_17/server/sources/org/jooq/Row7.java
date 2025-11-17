package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Row7.class */
public interface Row7<T1, T2, T3, T4, T5, T6, T7> extends Row, SelectField<Record7<T1, T2, T3, T4, T5, T6, T7>> {
    @NotNull
    <U> SelectField<U> mapping(Function7<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? extends U> function7);

    @NotNull
    <U> SelectField<U> mapping(Class<U> cls, Function7<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? extends U> function7);

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

    @Support
    @NotNull
    Condition compare(Comparator comparator, Row7<T1, T2, T3, T4, T5, T6, T7> row7);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Record7<T1, T2, T3, T4, T5, T6, T7> record7);

    @Support
    @NotNull
    Condition compare(Comparator comparator, T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support
    @NotNull
    Condition compare(Comparator comparator, Select<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> select);

    @Support
    @NotNull
    Condition compare(Comparator comparator, QuantifiedSelect<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> quantifiedSelect);

    @Support
    @NotNull
    Condition equal(Row7<T1, T2, T3, T4, T5, T6, T7> row7);

    @Support
    @NotNull
    Condition equal(Record7<T1, T2, T3, T4, T5, T6, T7> record7);

    @Support
    @NotNull
    Condition equal(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);

    @Support
    @NotNull
    Condition equal(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support
    @NotNull
    Condition equal(Select<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> select);

    @Support
    @NotNull
    Condition equal(QuantifiedSelect<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> quantifiedSelect);

    @Support
    @NotNull
    Condition eq(Row7<T1, T2, T3, T4, T5, T6, T7> row7);

    @Support
    @NotNull
    Condition eq(Record7<T1, T2, T3, T4, T5, T6, T7> record7);

    @Support
    @NotNull
    Condition eq(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);

    @Support
    @NotNull
    Condition eq(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support
    @NotNull
    Condition eq(Select<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> select);

    @Support
    @NotNull
    Condition eq(QuantifiedSelect<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> quantifiedSelect);

    @Support
    @NotNull
    Condition notEqual(Row7<T1, T2, T3, T4, T5, T6, T7> row7);

    @Support
    @NotNull
    Condition notEqual(Record7<T1, T2, T3, T4, T5, T6, T7> record7);

    @Support
    @NotNull
    Condition notEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);

    @Support
    @NotNull
    Condition notEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support
    @NotNull
    Condition notEqual(Select<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> select);

    @Support
    @NotNull
    Condition notEqual(QuantifiedSelect<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> quantifiedSelect);

    @Support
    @NotNull
    Condition ne(Row7<T1, T2, T3, T4, T5, T6, T7> row7);

    @Support
    @NotNull
    Condition ne(Record7<T1, T2, T3, T4, T5, T6, T7> record7);

    @Support
    @NotNull
    Condition ne(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);

    @Support
    @NotNull
    Condition ne(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support
    @NotNull
    Condition ne(Select<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> select);

    @Support
    @NotNull
    Condition ne(QuantifiedSelect<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> quantifiedSelect);

    @Support
    @NotNull
    Condition isDistinctFrom(Row7<T1, T2, T3, T4, T5, T6, T7> row7);

    @Support
    @NotNull
    Condition isDistinctFrom(Record7<T1, T2, T3, T4, T5, T6, T7> record7);

    @Support
    @NotNull
    Condition isDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);

    @Support
    @NotNull
    Condition isDistinctFrom(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support
    @NotNull
    Condition isDistinctFrom(Select<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> select);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Row7<T1, T2, T3, T4, T5, T6, T7> row7);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Record7<T1, T2, T3, T4, T5, T6, T7> record7);

    @Support
    @NotNull
    Condition isNotDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support
    @NotNull
    Condition isNotDistinctFrom(Select<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> select);

    @Support
    @NotNull
    Condition lessThan(Row7<T1, T2, T3, T4, T5, T6, T7> row7);

    @Support
    @NotNull
    Condition lessThan(Record7<T1, T2, T3, T4, T5, T6, T7> record7);

    @Support
    @NotNull
    Condition lessThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);

    @Support
    @NotNull
    Condition lessThan(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support
    @NotNull
    Condition lessThan(Select<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> select);

    @Support
    @NotNull
    Condition lessThan(QuantifiedSelect<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> quantifiedSelect);

    @Support
    @NotNull
    Condition lt(Row7<T1, T2, T3, T4, T5, T6, T7> row7);

    @Support
    @NotNull
    Condition lt(Record7<T1, T2, T3, T4, T5, T6, T7> record7);

    @Support
    @NotNull
    Condition lt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);

    @Support
    @NotNull
    Condition lt(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support
    @NotNull
    Condition lt(Select<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> select);

    @Support
    @NotNull
    Condition lt(QuantifiedSelect<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> quantifiedSelect);

    @Support
    @NotNull
    Condition lessOrEqual(Row7<T1, T2, T3, T4, T5, T6, T7> row7);

    @Support
    @NotNull
    Condition lessOrEqual(Record7<T1, T2, T3, T4, T5, T6, T7> record7);

    @Support
    @NotNull
    Condition lessOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);

    @Support
    @NotNull
    Condition lessOrEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support
    @NotNull
    Condition lessOrEqual(Select<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> select);

    @Support
    @NotNull
    Condition lessOrEqual(QuantifiedSelect<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> quantifiedSelect);

    @Support
    @NotNull
    Condition le(Row7<T1, T2, T3, T4, T5, T6, T7> row7);

    @Support
    @NotNull
    Condition le(Record7<T1, T2, T3, T4, T5, T6, T7> record7);

    @Support
    @NotNull
    Condition le(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);

    @Support
    @NotNull
    Condition le(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support
    @NotNull
    Condition le(Select<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> select);

    @Support
    @NotNull
    Condition le(QuantifiedSelect<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterThan(Row7<T1, T2, T3, T4, T5, T6, T7> row7);

    @Support
    @NotNull
    Condition greaterThan(Record7<T1, T2, T3, T4, T5, T6, T7> record7);

    @Support
    @NotNull
    Condition greaterThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);

    @Support
    @NotNull
    Condition greaterThan(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support
    @NotNull
    Condition greaterThan(Select<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> select);

    @Support
    @NotNull
    Condition greaterThan(QuantifiedSelect<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> quantifiedSelect);

    @Support
    @NotNull
    Condition gt(Row7<T1, T2, T3, T4, T5, T6, T7> row7);

    @Support
    @NotNull
    Condition gt(Record7<T1, T2, T3, T4, T5, T6, T7> record7);

    @Support
    @NotNull
    Condition gt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);

    @Support
    @NotNull
    Condition gt(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support
    @NotNull
    Condition gt(Select<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> select);

    @Support
    @NotNull
    Condition gt(QuantifiedSelect<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> quantifiedSelect);

    @Support
    @NotNull
    Condition greaterOrEqual(Row7<T1, T2, T3, T4, T5, T6, T7> row7);

    @Support
    @NotNull
    Condition greaterOrEqual(Record7<T1, T2, T3, T4, T5, T6, T7> record7);

    @Support
    @NotNull
    Condition greaterOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);

    @Support
    @NotNull
    Condition greaterOrEqual(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support
    @NotNull
    Condition greaterOrEqual(Select<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> select);

    @Support
    @NotNull
    Condition greaterOrEqual(QuantifiedSelect<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> quantifiedSelect);

    @Support
    @NotNull
    Condition ge(Row7<T1, T2, T3, T4, T5, T6, T7> row7);

    @Support
    @NotNull
    Condition ge(Record7<T1, T2, T3, T4, T5, T6, T7> record7);

    @Support
    @NotNull
    Condition ge(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);

    @Support
    @NotNull
    Condition ge(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support
    @NotNull
    Condition ge(Select<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> select);

    @Support
    @NotNull
    Condition ge(QuantifiedSelect<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> quantifiedSelect);

    @Support
    @NotNull
    BetweenAndStep7<T1, T2, T3, T4, T5, T6, T7> between(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);

    @Support
    @NotNull
    BetweenAndStep7<T1, T2, T3, T4, T5, T6, T7> between(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support
    @NotNull
    BetweenAndStep7<T1, T2, T3, T4, T5, T6, T7> between(Row7<T1, T2, T3, T4, T5, T6, T7> row7);

    @Support
    @NotNull
    BetweenAndStep7<T1, T2, T3, T4, T5, T6, T7> between(Record7<T1, T2, T3, T4, T5, T6, T7> record7);

    @Support
    @NotNull
    Condition between(Row7<T1, T2, T3, T4, T5, T6, T7> row7, Row7<T1, T2, T3, T4, T5, T6, T7> row72);

    @Support
    @NotNull
    Condition between(Record7<T1, T2, T3, T4, T5, T6, T7> record7, Record7<T1, T2, T3, T4, T5, T6, T7> record72);

    @Support
    @NotNull
    BetweenAndStep7<T1, T2, T3, T4, T5, T6, T7> betweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);

    @Support
    @NotNull
    BetweenAndStep7<T1, T2, T3, T4, T5, T6, T7> betweenSymmetric(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support
    @NotNull
    BetweenAndStep7<T1, T2, T3, T4, T5, T6, T7> betweenSymmetric(Row7<T1, T2, T3, T4, T5, T6, T7> row7);

    @Support
    @NotNull
    BetweenAndStep7<T1, T2, T3, T4, T5, T6, T7> betweenSymmetric(Record7<T1, T2, T3, T4, T5, T6, T7> record7);

    @Support
    @NotNull
    Condition betweenSymmetric(Row7<T1, T2, T3, T4, T5, T6, T7> row7, Row7<T1, T2, T3, T4, T5, T6, T7> row72);

    @Support
    @NotNull
    Condition betweenSymmetric(Record7<T1, T2, T3, T4, T5, T6, T7> record7, Record7<T1, T2, T3, T4, T5, T6, T7> record72);

    @Support
    @NotNull
    BetweenAndStep7<T1, T2, T3, T4, T5, T6, T7> notBetween(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);

    @Support
    @NotNull
    BetweenAndStep7<T1, T2, T3, T4, T5, T6, T7> notBetween(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support
    @NotNull
    BetweenAndStep7<T1, T2, T3, T4, T5, T6, T7> notBetween(Row7<T1, T2, T3, T4, T5, T6, T7> row7);

    @Support
    @NotNull
    BetweenAndStep7<T1, T2, T3, T4, T5, T6, T7> notBetween(Record7<T1, T2, T3, T4, T5, T6, T7> record7);

    @Support
    @NotNull
    Condition notBetween(Row7<T1, T2, T3, T4, T5, T6, T7> row7, Row7<T1, T2, T3, T4, T5, T6, T7> row72);

    @Support
    @NotNull
    Condition notBetween(Record7<T1, T2, T3, T4, T5, T6, T7> record7, Record7<T1, T2, T3, T4, T5, T6, T7> record72);

    @Support
    @NotNull
    BetweenAndStep7<T1, T2, T3, T4, T5, T6, T7> notBetweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);

    @Support
    @NotNull
    BetweenAndStep7<T1, T2, T3, T4, T5, T6, T7> notBetweenSymmetric(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support
    @NotNull
    BetweenAndStep7<T1, T2, T3, T4, T5, T6, T7> notBetweenSymmetric(Row7<T1, T2, T3, T4, T5, T6, T7> row7);

    @Support
    @NotNull
    BetweenAndStep7<T1, T2, T3, T4, T5, T6, T7> notBetweenSymmetric(Record7<T1, T2, T3, T4, T5, T6, T7> record7);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Row7<T1, T2, T3, T4, T5, T6, T7> row7, Row7<T1, T2, T3, T4, T5, T6, T7> row72);

    @Support
    @NotNull
    Condition notBetweenSymmetric(Record7<T1, T2, T3, T4, T5, T6, T7> record7, Record7<T1, T2, T3, T4, T5, T6, T7> record72);

    @Support
    @NotNull
    Condition in(Collection<? extends Row7<T1, T2, T3, T4, T5, T6, T7>> collection);

    @Support
    @NotNull
    Condition in(Result<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> result);

    @Support
    @NotNull
    Condition in(Row7<T1, T2, T3, T4, T5, T6, T7>... row7Arr);

    @Support
    @NotNull
    Condition in(Record7<T1, T2, T3, T4, T5, T6, T7>... record7Arr);

    @Support
    @NotNull
    Condition in(Select<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> select);

    @Support
    @NotNull
    Condition notIn(Collection<? extends Row7<T1, T2, T3, T4, T5, T6, T7>> collection);

    @Support
    @NotNull
    Condition notIn(Result<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> result);

    @Support
    @NotNull
    Condition notIn(Row7<T1, T2, T3, T4, T5, T6, T7>... row7Arr);

    @Support
    @NotNull
    Condition notIn(Record7<T1, T2, T3, T4, T5, T6, T7>... record7Arr);

    @Support
    @NotNull
    Condition notIn(Select<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> select);
}
