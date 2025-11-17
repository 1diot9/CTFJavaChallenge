package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import org.jooq.BetweenAndStep4;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Function4;
import org.jooq.QuantifiedSelect;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.Row;
import org.jooq.Row4;
import org.jooq.Select;
import org.jooq.SelectField;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RowImpl4.class */
public final class RowImpl4<T1, T2, T3, T4> extends AbstractRow<Record4<T1, T2, T3, T4>> implements Row4<T1, T2, T3, T4> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public RowImpl4(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4) {
        super((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4});
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RowImpl4(FieldsImpl<?> fields) {
        super(fields);
    }

    @Override // org.jooq.Row4
    public final <U> SelectField<U> mapping(Function4<? super T1, ? super T2, ? super T3, ? super T4, ? extends U> function) {
        return convertFrom(r -> {
            if (r == null) {
                return null;
            }
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4());
        });
    }

    @Override // org.jooq.Row4
    public final <U> SelectField<U> mapping(Class<U> uType, Function4<? super T1, ? super T2, ? super T3, ? super T4, ? extends U> function) {
        return convertFrom(uType, r -> {
            if (r == null) {
                return null;
            }
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4());
        });
    }

    @Override // org.jooq.Row4
    public final Field<T1> field1() {
        return (Field<T1>) this.fields.field(0);
    }

    @Override // org.jooq.Row4
    public final Field<T2> field2() {
        return (Field<T2>) this.fields.field(1);
    }

    @Override // org.jooq.Row4
    public final Field<T3> field3() {
        return (Field<T3>) this.fields.field(2);
    }

    @Override // org.jooq.Row4
    public final Field<T4> field4() {
        return (Field<T4>) this.fields.field(3);
    }

    @Override // org.jooq.Row4
    public final Condition compare(Comparator comparator, Row4<T1, T2, T3, T4> row) {
        return compare(this, comparator, row);
    }

    @Override // org.jooq.Row4
    public final Condition compare(Comparator comparator, Record4<T1, T2, T3, T4> record) {
        return compare(this, comparator, record.valuesRow());
    }

    @Override // org.jooq.Row4
    public final Condition compare(Comparator comparator, T1 t1, T2 t2, T3 t3, T4 t4) {
        return compare(comparator, (Row4) DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1)), (SelectField) Tools.field(t3, dataType(2)), (SelectField) Tools.field(t4, dataType(3))));
    }

    @Override // org.jooq.Row4
    public final Condition compare(Comparator comparator, Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4) {
        return compare(comparator, (Row4) DSL.row((SelectField) Tools.nullSafe(t1, dataType(0)), (SelectField) Tools.nullSafe(t2, dataType(1)), (SelectField) Tools.nullSafe(t3, dataType(2)), (SelectField) Tools.nullSafe(t4, dataType(3))));
    }

    @Override // org.jooq.Row4
    public final Condition compare(Comparator comparator, Select<? extends Record4<T1, T2, T3, T4>> select) {
        return new RowSubqueryCondition(this, select, comparator);
    }

    @Override // org.jooq.Row4
    public final Condition compare(Comparator comparator, QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> select) {
        return new RowSubqueryCondition(this, select, comparator);
    }

    @Override // org.jooq.Row4
    public final Condition equal(Row4<T1, T2, T3, T4> row) {
        return compare(Comparator.EQUALS, (Row4) row);
    }

    @Override // org.jooq.Row4
    public final Condition equal(Record4<T1, T2, T3, T4> record) {
        return compare(Comparator.EQUALS, record);
    }

    @Override // org.jooq.Row4
    public final Condition equal(T1 t1, T2 t2, T3 t3, T4 t4) {
        return compare(Comparator.EQUALS, (Comparator) t1, (T1) t2, (T2) t3, (T3) t4);
    }

    @Override // org.jooq.Row4
    public final Condition equal(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4) {
        return compare(Comparator.EQUALS, (Field) t1, (Field) t2, (Field) t3, (Field) t4);
    }

    @Override // org.jooq.Row4
    public final Condition eq(Row4<T1, T2, T3, T4> row) {
        return equal(row);
    }

    @Override // org.jooq.Row4
    public final Condition eq(Record4<T1, T2, T3, T4> record) {
        return equal(record);
    }

    @Override // org.jooq.Row4
    public final Condition eq(T1 t1, T2 t2, T3 t3, T4 t4) {
        return equal((RowImpl4<T1, T2, T3, T4>) t1, (T1) t2, (T2) t3, (T3) t4);
    }

    @Override // org.jooq.Row4
    public final Condition eq(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4) {
        return equal((Field) t1, (Field) t2, (Field) t3, (Field) t4);
    }

    @Override // org.jooq.Row4
    public final Condition notEqual(Row4<T1, T2, T3, T4> row) {
        return compare(Comparator.NOT_EQUALS, (Row4) row);
    }

    @Override // org.jooq.Row4
    public final Condition notEqual(Record4<T1, T2, T3, T4> record) {
        return compare(Comparator.NOT_EQUALS, record);
    }

    @Override // org.jooq.Row4
    public final Condition notEqual(T1 t1, T2 t2, T3 t3, T4 t4) {
        return compare(Comparator.NOT_EQUALS, (Comparator) t1, (T1) t2, (T2) t3, (T3) t4);
    }

    @Override // org.jooq.Row4
    public final Condition notEqual(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4) {
        return compare(Comparator.NOT_EQUALS, (Field) t1, (Field) t2, (Field) t3, (Field) t4);
    }

    @Override // org.jooq.Row4
    public final Condition ne(Row4<T1, T2, T3, T4> row) {
        return notEqual(row);
    }

    @Override // org.jooq.Row4
    public final Condition ne(Record4<T1, T2, T3, T4> record) {
        return notEqual(record);
    }

    @Override // org.jooq.Row4
    public final Condition ne(T1 t1, T2 t2, T3 t3, T4 t4) {
        return notEqual((RowImpl4<T1, T2, T3, T4>) t1, (T1) t2, (T2) t3, (T3) t4);
    }

    @Override // org.jooq.Row4
    public final Condition ne(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4) {
        return notEqual((Field) t1, (Field) t2, (Field) t3, (Field) t4);
    }

    @Override // org.jooq.Row4
    public final Condition lessThan(Row4<T1, T2, T3, T4> row) {
        return compare(Comparator.LESS, (Row4) row);
    }

    @Override // org.jooq.Row4
    public final Condition lessThan(Record4<T1, T2, T3, T4> record) {
        return compare(Comparator.LESS, record);
    }

    @Override // org.jooq.Row4
    public final Condition lessThan(T1 t1, T2 t2, T3 t3, T4 t4) {
        return compare(Comparator.LESS, (Comparator) t1, (T1) t2, (T2) t3, (T3) t4);
    }

    @Override // org.jooq.Row4
    public final Condition lessThan(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4) {
        return compare(Comparator.LESS, (Field) t1, (Field) t2, (Field) t3, (Field) t4);
    }

    @Override // org.jooq.Row4
    public final Condition lt(Row4<T1, T2, T3, T4> row) {
        return lessThan(row);
    }

    @Override // org.jooq.Row4
    public final Condition lt(Record4<T1, T2, T3, T4> record) {
        return lessThan(record);
    }

    @Override // org.jooq.Row4
    public final Condition lt(T1 t1, T2 t2, T3 t3, T4 t4) {
        return lessThan((RowImpl4<T1, T2, T3, T4>) t1, (T1) t2, (T2) t3, (T3) t4);
    }

    @Override // org.jooq.Row4
    public final Condition lt(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4) {
        return lessThan((Field) t1, (Field) t2, (Field) t3, (Field) t4);
    }

    @Override // org.jooq.Row4
    public final Condition lessOrEqual(Row4<T1, T2, T3, T4> row) {
        return compare(Comparator.LESS_OR_EQUAL, (Row4) row);
    }

    @Override // org.jooq.Row4
    public final Condition lessOrEqual(Record4<T1, T2, T3, T4> record) {
        return compare(Comparator.LESS_OR_EQUAL, record);
    }

    @Override // org.jooq.Row4
    public final Condition lessOrEqual(T1 t1, T2 t2, T3 t3, T4 t4) {
        return compare(Comparator.LESS_OR_EQUAL, (Comparator) t1, (T1) t2, (T2) t3, (T3) t4);
    }

    @Override // org.jooq.Row4
    public final Condition lessOrEqual(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4) {
        return compare(Comparator.LESS_OR_EQUAL, (Field) t1, (Field) t2, (Field) t3, (Field) t4);
    }

    @Override // org.jooq.Row4
    public final Condition le(Row4<T1, T2, T3, T4> row) {
        return lessOrEqual(row);
    }

    @Override // org.jooq.Row4
    public final Condition le(Record4<T1, T2, T3, T4> record) {
        return lessOrEqual(record);
    }

    @Override // org.jooq.Row4
    public final Condition le(T1 t1, T2 t2, T3 t3, T4 t4) {
        return lessOrEqual((RowImpl4<T1, T2, T3, T4>) t1, (T1) t2, (T2) t3, (T3) t4);
    }

    @Override // org.jooq.Row4
    public final Condition le(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4) {
        return lessOrEqual((Field) t1, (Field) t2, (Field) t3, (Field) t4);
    }

    @Override // org.jooq.Row4
    public final Condition greaterThan(Row4<T1, T2, T3, T4> row) {
        return compare(Comparator.GREATER, (Row4) row);
    }

    @Override // org.jooq.Row4
    public final Condition greaterThan(Record4<T1, T2, T3, T4> record) {
        return compare(Comparator.GREATER, record);
    }

    @Override // org.jooq.Row4
    public final Condition greaterThan(T1 t1, T2 t2, T3 t3, T4 t4) {
        return compare(Comparator.GREATER, (Comparator) t1, (T1) t2, (T2) t3, (T3) t4);
    }

    @Override // org.jooq.Row4
    public final Condition greaterThan(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4) {
        return compare(Comparator.GREATER, (Field) t1, (Field) t2, (Field) t3, (Field) t4);
    }

    @Override // org.jooq.Row4
    public final Condition gt(Row4<T1, T2, T3, T4> row) {
        return greaterThan(row);
    }

    @Override // org.jooq.Row4
    public final Condition gt(Record4<T1, T2, T3, T4> record) {
        return greaterThan(record);
    }

    @Override // org.jooq.Row4
    public final Condition gt(T1 t1, T2 t2, T3 t3, T4 t4) {
        return greaterThan((RowImpl4<T1, T2, T3, T4>) t1, (T1) t2, (T2) t3, (T3) t4);
    }

    @Override // org.jooq.Row4
    public final Condition gt(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4) {
        return greaterThan((Field) t1, (Field) t2, (Field) t3, (Field) t4);
    }

    @Override // org.jooq.Row4
    public final Condition greaterOrEqual(Row4<T1, T2, T3, T4> row) {
        return compare(Comparator.GREATER_OR_EQUAL, (Row4) row);
    }

    @Override // org.jooq.Row4
    public final Condition greaterOrEqual(Record4<T1, T2, T3, T4> record) {
        return compare(Comparator.GREATER_OR_EQUAL, record);
    }

    @Override // org.jooq.Row4
    public final Condition greaterOrEqual(T1 t1, T2 t2, T3 t3, T4 t4) {
        return compare(Comparator.GREATER_OR_EQUAL, (Comparator) t1, (T1) t2, (T2) t3, (T3) t4);
    }

    @Override // org.jooq.Row4
    public final Condition greaterOrEqual(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4) {
        return compare(Comparator.GREATER_OR_EQUAL, (Field) t1, (Field) t2, (Field) t3, (Field) t4);
    }

    @Override // org.jooq.Row4
    public final Condition ge(Row4<T1, T2, T3, T4> row) {
        return greaterOrEqual(row);
    }

    @Override // org.jooq.Row4
    public final Condition ge(Record4<T1, T2, T3, T4> record) {
        return greaterOrEqual(record);
    }

    @Override // org.jooq.Row4
    public final Condition ge(T1 t1, T2 t2, T3 t3, T4 t4) {
        return greaterOrEqual((RowImpl4<T1, T2, T3, T4>) t1, (T1) t2, (T2) t3, (T3) t4);
    }

    @Override // org.jooq.Row4
    public final Condition ge(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4) {
        return greaterOrEqual((Field) t1, (Field) t2, (Field) t3, (Field) t4);
    }

    @Override // org.jooq.Row4
    public final BetweenAndStep4<T1, T2, T3, T4> between(T1 t1, T2 t2, T3 t3, T4 t4) {
        return between(DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1)), (SelectField) Tools.field(t3, dataType(2)), (SelectField) Tools.field(t4, dataType(3))));
    }

    @Override // org.jooq.Row4
    public final BetweenAndStep4<T1, T2, T3, T4> between(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4) {
        return between(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4));
    }

    @Override // org.jooq.Row4
    public final BetweenAndStep4<T1, T2, T3, T4> between(Row4<T1, T2, T3, T4> row) {
        return new RowBetweenCondition(this, row, false, false);
    }

    @Override // org.jooq.Row4
    public final BetweenAndStep4<T1, T2, T3, T4> between(Record4<T1, T2, T3, T4> record) {
        return between(record.valuesRow());
    }

    @Override // org.jooq.Row4
    public final Condition between(Row4<T1, T2, T3, T4> minValue, Row4<T1, T2, T3, T4> maxValue) {
        return between(minValue).and(maxValue);
    }

    @Override // org.jooq.Row4
    public final Condition between(Record4<T1, T2, T3, T4> minValue, Record4<T1, T2, T3, T4> maxValue) {
        return between(minValue).and(maxValue);
    }

    @Override // org.jooq.Row4
    public final BetweenAndStep4<T1, T2, T3, T4> betweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4) {
        return betweenSymmetric(DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1)), (SelectField) Tools.field(t3, dataType(2)), (SelectField) Tools.field(t4, dataType(3))));
    }

    @Override // org.jooq.Row4
    public final BetweenAndStep4<T1, T2, T3, T4> betweenSymmetric(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4) {
        return betweenSymmetric(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4));
    }

    @Override // org.jooq.Row4
    public final BetweenAndStep4<T1, T2, T3, T4> betweenSymmetric(Row4<T1, T2, T3, T4> row) {
        return new RowBetweenCondition(this, row, false, true);
    }

    @Override // org.jooq.Row4
    public final BetweenAndStep4<T1, T2, T3, T4> betweenSymmetric(Record4<T1, T2, T3, T4> record) {
        return betweenSymmetric(record.valuesRow());
    }

    @Override // org.jooq.Row4
    public final Condition betweenSymmetric(Row4<T1, T2, T3, T4> minValue, Row4<T1, T2, T3, T4> maxValue) {
        return betweenSymmetric(minValue).and(maxValue);
    }

    @Override // org.jooq.Row4
    public final Condition betweenSymmetric(Record4<T1, T2, T3, T4> minValue, Record4<T1, T2, T3, T4> maxValue) {
        return betweenSymmetric(minValue).and(maxValue);
    }

    @Override // org.jooq.Row4
    public final BetweenAndStep4<T1, T2, T3, T4> notBetween(T1 t1, T2 t2, T3 t3, T4 t4) {
        return notBetween(DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1)), (SelectField) Tools.field(t3, dataType(2)), (SelectField) Tools.field(t4, dataType(3))));
    }

    @Override // org.jooq.Row4
    public final BetweenAndStep4<T1, T2, T3, T4> notBetween(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4) {
        return notBetween(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4));
    }

    @Override // org.jooq.Row4
    public final BetweenAndStep4<T1, T2, T3, T4> notBetween(Row4<T1, T2, T3, T4> row) {
        return new RowBetweenCondition(this, row, true, false);
    }

    @Override // org.jooq.Row4
    public final BetweenAndStep4<T1, T2, T3, T4> notBetween(Record4<T1, T2, T3, T4> record) {
        return notBetween(record.valuesRow());
    }

    @Override // org.jooq.Row4
    public final Condition notBetween(Row4<T1, T2, T3, T4> minValue, Row4<T1, T2, T3, T4> maxValue) {
        return notBetween(minValue).and(maxValue);
    }

    @Override // org.jooq.Row4
    public final Condition notBetween(Record4<T1, T2, T3, T4> minValue, Record4<T1, T2, T3, T4> maxValue) {
        return notBetween(minValue).and(maxValue);
    }

    @Override // org.jooq.Row4
    public final BetweenAndStep4<T1, T2, T3, T4> notBetweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4) {
        return notBetweenSymmetric(DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1)), (SelectField) Tools.field(t3, dataType(2)), (SelectField) Tools.field(t4, dataType(3))));
    }

    @Override // org.jooq.Row4
    public final BetweenAndStep4<T1, T2, T3, T4> notBetweenSymmetric(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4) {
        return notBetweenSymmetric(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4));
    }

    @Override // org.jooq.Row4
    public final BetweenAndStep4<T1, T2, T3, T4> notBetweenSymmetric(Row4<T1, T2, T3, T4> row) {
        return new RowBetweenCondition(this, row, true, true);
    }

    @Override // org.jooq.Row4
    public final BetweenAndStep4<T1, T2, T3, T4> notBetweenSymmetric(Record4<T1, T2, T3, T4> record) {
        return notBetweenSymmetric(record.valuesRow());
    }

    @Override // org.jooq.Row4
    public final Condition notBetweenSymmetric(Row4<T1, T2, T3, T4> minValue, Row4<T1, T2, T3, T4> maxValue) {
        return notBetweenSymmetric(minValue).and(maxValue);
    }

    @Override // org.jooq.Row4
    public final Condition notBetweenSymmetric(Record4<T1, T2, T3, T4> minValue, Record4<T1, T2, T3, T4> maxValue) {
        return notBetweenSymmetric(minValue).and(maxValue);
    }

    @Override // org.jooq.Row4
    public final Condition isNotDistinctFrom(Row4<T1, T2, T3, T4> row) {
        return new RowIsDistinctFrom((Row) this, (Row) row, true);
    }

    @Override // org.jooq.Row4
    public final Condition isNotDistinctFrom(Record4<T1, T2, T3, T4> record) {
        return isNotDistinctFrom(record.valuesRow());
    }

    @Override // org.jooq.Row4
    public final Condition isNotDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4) {
        return isNotDistinctFrom((Field) Tools.field(t1, dataType(0)), (Field) Tools.field(t2, dataType(1)), (Field) Tools.field(t3, dataType(2)), (Field) Tools.field(t4, dataType(3)));
    }

    @Override // org.jooq.Row4
    public final Condition isNotDistinctFrom(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4) {
        return isNotDistinctFrom(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4));
    }

    @Override // org.jooq.Row4
    public final Condition isNotDistinctFrom(Select<? extends Record4<T1, T2, T3, T4>> select) {
        return new RowIsDistinctFrom((Row) this, (Select<?>) select, true);
    }

    @Override // org.jooq.Row4
    public final Condition isDistinctFrom(Row4<T1, T2, T3, T4> row) {
        return new RowIsDistinctFrom((Row) this, (Row) row, false);
    }

    @Override // org.jooq.Row4
    public final Condition isDistinctFrom(Record4<T1, T2, T3, T4> record) {
        return isDistinctFrom(record.valuesRow());
    }

    @Override // org.jooq.Row4
    public final Condition isDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4) {
        return isDistinctFrom((Field) Tools.field(t1, dataType(0)), (Field) Tools.field(t2, dataType(1)), (Field) Tools.field(t3, dataType(2)), (Field) Tools.field(t4, dataType(3)));
    }

    @Override // org.jooq.Row4
    public final Condition isDistinctFrom(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4) {
        return isDistinctFrom(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4));
    }

    @Override // org.jooq.Row4
    public final Condition isDistinctFrom(Select<? extends Record4<T1, T2, T3, T4>> select) {
        return new RowIsDistinctFrom((Row) this, (Select<?>) select, false);
    }

    @Override // org.jooq.Row4
    public final Condition in(Row4<T1, T2, T3, T4>... rows) {
        return in(Arrays.asList(rows));
    }

    @Override // org.jooq.Row4
    public final Condition in(Record4<T1, T2, T3, T4>... records) {
        QueryPartList<Row> rows = new QueryPartList<>();
        for (Record4<T1, T2, T3, T4> record4 : records) {
            rows.add((QueryPartList<Row>) record4.valuesRow());
        }
        return new RowInCondition(this, rows, false);
    }

    @Override // org.jooq.Row4
    public final Condition notIn(Row4<T1, T2, T3, T4>... rows) {
        return notIn(Arrays.asList(rows));
    }

    @Override // org.jooq.Row4
    public final Condition notIn(Record4<T1, T2, T3, T4>... records) {
        QueryPartList<Row> rows = new QueryPartList<>();
        for (Record4<T1, T2, T3, T4> record4 : records) {
            rows.add((QueryPartList<Row>) record4.valuesRow());
        }
        return new RowInCondition(this, rows, true);
    }

    @Override // org.jooq.Row4
    public final Condition in(Collection<? extends Row4<T1, T2, T3, T4>> rows) {
        return new RowInCondition(this, new QueryPartList(rows), false);
    }

    @Override // org.jooq.Row4
    public final Condition in(Result<? extends Record4<T1, T2, T3, T4>> result) {
        return new RowInCondition(this, new QueryPartList(Tools.rows(result)), false);
    }

    @Override // org.jooq.Row4
    public final Condition notIn(Collection<? extends Row4<T1, T2, T3, T4>> rows) {
        return new RowInCondition(this, new QueryPartList(rows), true);
    }

    @Override // org.jooq.Row4
    public final Condition notIn(Result<? extends Record4<T1, T2, T3, T4>> result) {
        return new RowInCondition(this, new QueryPartList(Tools.rows(result)), true);
    }

    @Override // org.jooq.Row4
    public final Condition equal(Select<? extends Record4<T1, T2, T3, T4>> select) {
        return compare(Comparator.EQUALS, select);
    }

    @Override // org.jooq.Row4
    public final Condition equal(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> select) {
        return compare(Comparator.EQUALS, select);
    }

    @Override // org.jooq.Row4
    public final Condition eq(Select<? extends Record4<T1, T2, T3, T4>> select) {
        return equal(select);
    }

    @Override // org.jooq.Row4
    public final Condition eq(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> select) {
        return equal(select);
    }

    @Override // org.jooq.Row4
    public final Condition notEqual(Select<? extends Record4<T1, T2, T3, T4>> select) {
        return compare(Comparator.NOT_EQUALS, select);
    }

    @Override // org.jooq.Row4
    public final Condition notEqual(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> select) {
        return compare(Comparator.NOT_EQUALS, select);
    }

    @Override // org.jooq.Row4
    public final Condition ne(Select<? extends Record4<T1, T2, T3, T4>> select) {
        return notEqual(select);
    }

    @Override // org.jooq.Row4
    public final Condition ne(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> select) {
        return notEqual(select);
    }

    @Override // org.jooq.Row4
    public final Condition greaterThan(Select<? extends Record4<T1, T2, T3, T4>> select) {
        return compare(Comparator.GREATER, select);
    }

    @Override // org.jooq.Row4
    public final Condition greaterThan(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> select) {
        return compare(Comparator.GREATER, select);
    }

    @Override // org.jooq.Row4
    public final Condition gt(Select<? extends Record4<T1, T2, T3, T4>> select) {
        return greaterThan(select);
    }

    @Override // org.jooq.Row4
    public final Condition gt(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> select) {
        return greaterThan(select);
    }

    @Override // org.jooq.Row4
    public final Condition greaterOrEqual(Select<? extends Record4<T1, T2, T3, T4>> select) {
        return compare(Comparator.GREATER_OR_EQUAL, select);
    }

    @Override // org.jooq.Row4
    public final Condition greaterOrEqual(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> select) {
        return compare(Comparator.GREATER_OR_EQUAL, select);
    }

    @Override // org.jooq.Row4
    public final Condition ge(Select<? extends Record4<T1, T2, T3, T4>> select) {
        return greaterOrEqual(select);
    }

    @Override // org.jooq.Row4
    public final Condition ge(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> select) {
        return greaterOrEqual(select);
    }

    @Override // org.jooq.Row4
    public final Condition lessThan(Select<? extends Record4<T1, T2, T3, T4>> select) {
        return compare(Comparator.LESS, select);
    }

    @Override // org.jooq.Row4
    public final Condition lessThan(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> select) {
        return compare(Comparator.LESS, select);
    }

    @Override // org.jooq.Row4
    public final Condition lt(Select<? extends Record4<T1, T2, T3, T4>> select) {
        return lessThan(select);
    }

    @Override // org.jooq.Row4
    public final Condition lt(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> select) {
        return lessThan(select);
    }

    @Override // org.jooq.Row4
    public final Condition lessOrEqual(Select<? extends Record4<T1, T2, T3, T4>> select) {
        return compare(Comparator.LESS_OR_EQUAL, select);
    }

    @Override // org.jooq.Row4
    public final Condition lessOrEqual(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> select) {
        return compare(Comparator.LESS_OR_EQUAL, select);
    }

    @Override // org.jooq.Row4
    public final Condition le(Select<? extends Record4<T1, T2, T3, T4>> select) {
        return lessOrEqual(select);
    }

    @Override // org.jooq.Row4
    public final Condition le(QuantifiedSelect<? extends Record4<T1, T2, T3, T4>> select) {
        return lessOrEqual(select);
    }

    @Override // org.jooq.Row4
    public final Condition in(Select<? extends Record4<T1, T2, T3, T4>> select) {
        return compare(Comparator.IN, select);
    }

    @Override // org.jooq.Row4
    public final Condition notIn(Select<? extends Record4<T1, T2, T3, T4>> select) {
        return compare(Comparator.NOT_IN, select);
    }
}
