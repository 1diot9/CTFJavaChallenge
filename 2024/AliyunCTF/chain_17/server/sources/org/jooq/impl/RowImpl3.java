package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import org.jooq.BetweenAndStep3;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Function3;
import org.jooq.QuantifiedSelect;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.Row;
import org.jooq.Row3;
import org.jooq.Select;
import org.jooq.SelectField;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RowImpl3.class */
public final class RowImpl3<T1, T2, T3> extends AbstractRow<Record3<T1, T2, T3>> implements Row3<T1, T2, T3> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public RowImpl3(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3) {
        super((SelectField<?>[]) new SelectField[]{field1, field2, field3});
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RowImpl3(FieldsImpl<?> fields) {
        super(fields);
    }

    @Override // org.jooq.Row3
    public final <U> SelectField<U> mapping(Function3<? super T1, ? super T2, ? super T3, ? extends U> function) {
        return convertFrom(r -> {
            if (r == null) {
                return null;
            }
            return function.apply(r.value1(), r.value2(), r.value3());
        });
    }

    @Override // org.jooq.Row3
    public final <U> SelectField<U> mapping(Class<U> uType, Function3<? super T1, ? super T2, ? super T3, ? extends U> function) {
        return convertFrom(uType, r -> {
            if (r == null) {
                return null;
            }
            return function.apply(r.value1(), r.value2(), r.value3());
        });
    }

    @Override // org.jooq.Row3
    public final Field<T1> field1() {
        return (Field<T1>) this.fields.field(0);
    }

    @Override // org.jooq.Row3
    public final Field<T2> field2() {
        return (Field<T2>) this.fields.field(1);
    }

    @Override // org.jooq.Row3
    public final Field<T3> field3() {
        return (Field<T3>) this.fields.field(2);
    }

    @Override // org.jooq.Row3
    public final Condition compare(Comparator comparator, Row3<T1, T2, T3> row) {
        return compare(this, comparator, row);
    }

    @Override // org.jooq.Row3
    public final Condition compare(Comparator comparator, Record3<T1, T2, T3> record) {
        return compare(this, comparator, record.valuesRow());
    }

    @Override // org.jooq.Row3
    public final Condition compare(Comparator comparator, T1 t1, T2 t2, T3 t3) {
        return compare(comparator, (Row3) DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1)), (SelectField) Tools.field(t3, dataType(2))));
    }

    @Override // org.jooq.Row3
    public final Condition compare(Comparator comparator, Field<T1> t1, Field<T2> t2, Field<T3> t3) {
        return compare(comparator, (Row3) DSL.row((SelectField) Tools.nullSafe(t1, dataType(0)), (SelectField) Tools.nullSafe(t2, dataType(1)), (SelectField) Tools.nullSafe(t3, dataType(2))));
    }

    @Override // org.jooq.Row3
    public final Condition compare(Comparator comparator, Select<? extends Record3<T1, T2, T3>> select) {
        return new RowSubqueryCondition(this, select, comparator);
    }

    @Override // org.jooq.Row3
    public final Condition compare(Comparator comparator, QuantifiedSelect<? extends Record3<T1, T2, T3>> select) {
        return new RowSubqueryCondition(this, select, comparator);
    }

    @Override // org.jooq.Row3
    public final Condition equal(Row3<T1, T2, T3> row) {
        return compare(Comparator.EQUALS, (Row3) row);
    }

    @Override // org.jooq.Row3
    public final Condition equal(Record3<T1, T2, T3> record) {
        return compare(Comparator.EQUALS, record);
    }

    @Override // org.jooq.Row3
    public final Condition equal(T1 t1, T2 t2, T3 t3) {
        return compare(Comparator.EQUALS, (Comparator) t1, (T1) t2, (T2) t3);
    }

    @Override // org.jooq.Row3
    public final Condition equal(Field<T1> t1, Field<T2> t2, Field<T3> t3) {
        return compare(Comparator.EQUALS, (Field) t1, (Field) t2, (Field) t3);
    }

    @Override // org.jooq.Row3
    public final Condition eq(Row3<T1, T2, T3> row) {
        return equal(row);
    }

    @Override // org.jooq.Row3
    public final Condition eq(Record3<T1, T2, T3> record) {
        return equal(record);
    }

    @Override // org.jooq.Row3
    public final Condition eq(T1 t1, T2 t2, T3 t3) {
        return equal((RowImpl3<T1, T2, T3>) t1, (T1) t2, (T2) t3);
    }

    @Override // org.jooq.Row3
    public final Condition eq(Field<T1> t1, Field<T2> t2, Field<T3> t3) {
        return equal((Field) t1, (Field) t2, (Field) t3);
    }

    @Override // org.jooq.Row3
    public final Condition notEqual(Row3<T1, T2, T3> row) {
        return compare(Comparator.NOT_EQUALS, (Row3) row);
    }

    @Override // org.jooq.Row3
    public final Condition notEqual(Record3<T1, T2, T3> record) {
        return compare(Comparator.NOT_EQUALS, record);
    }

    @Override // org.jooq.Row3
    public final Condition notEqual(T1 t1, T2 t2, T3 t3) {
        return compare(Comparator.NOT_EQUALS, (Comparator) t1, (T1) t2, (T2) t3);
    }

    @Override // org.jooq.Row3
    public final Condition notEqual(Field<T1> t1, Field<T2> t2, Field<T3> t3) {
        return compare(Comparator.NOT_EQUALS, (Field) t1, (Field) t2, (Field) t3);
    }

    @Override // org.jooq.Row3
    public final Condition ne(Row3<T1, T2, T3> row) {
        return notEqual(row);
    }

    @Override // org.jooq.Row3
    public final Condition ne(Record3<T1, T2, T3> record) {
        return notEqual(record);
    }

    @Override // org.jooq.Row3
    public final Condition ne(T1 t1, T2 t2, T3 t3) {
        return notEqual((RowImpl3<T1, T2, T3>) t1, (T1) t2, (T2) t3);
    }

    @Override // org.jooq.Row3
    public final Condition ne(Field<T1> t1, Field<T2> t2, Field<T3> t3) {
        return notEqual((Field) t1, (Field) t2, (Field) t3);
    }

    @Override // org.jooq.Row3
    public final Condition lessThan(Row3<T1, T2, T3> row) {
        return compare(Comparator.LESS, (Row3) row);
    }

    @Override // org.jooq.Row3
    public final Condition lessThan(Record3<T1, T2, T3> record) {
        return compare(Comparator.LESS, record);
    }

    @Override // org.jooq.Row3
    public final Condition lessThan(T1 t1, T2 t2, T3 t3) {
        return compare(Comparator.LESS, (Comparator) t1, (T1) t2, (T2) t3);
    }

    @Override // org.jooq.Row3
    public final Condition lessThan(Field<T1> t1, Field<T2> t2, Field<T3> t3) {
        return compare(Comparator.LESS, (Field) t1, (Field) t2, (Field) t3);
    }

    @Override // org.jooq.Row3
    public final Condition lt(Row3<T1, T2, T3> row) {
        return lessThan(row);
    }

    @Override // org.jooq.Row3
    public final Condition lt(Record3<T1, T2, T3> record) {
        return lessThan(record);
    }

    @Override // org.jooq.Row3
    public final Condition lt(T1 t1, T2 t2, T3 t3) {
        return lessThan((RowImpl3<T1, T2, T3>) t1, (T1) t2, (T2) t3);
    }

    @Override // org.jooq.Row3
    public final Condition lt(Field<T1> t1, Field<T2> t2, Field<T3> t3) {
        return lessThan((Field) t1, (Field) t2, (Field) t3);
    }

    @Override // org.jooq.Row3
    public final Condition lessOrEqual(Row3<T1, T2, T3> row) {
        return compare(Comparator.LESS_OR_EQUAL, (Row3) row);
    }

    @Override // org.jooq.Row3
    public final Condition lessOrEqual(Record3<T1, T2, T3> record) {
        return compare(Comparator.LESS_OR_EQUAL, record);
    }

    @Override // org.jooq.Row3
    public final Condition lessOrEqual(T1 t1, T2 t2, T3 t3) {
        return compare(Comparator.LESS_OR_EQUAL, (Comparator) t1, (T1) t2, (T2) t3);
    }

    @Override // org.jooq.Row3
    public final Condition lessOrEqual(Field<T1> t1, Field<T2> t2, Field<T3> t3) {
        return compare(Comparator.LESS_OR_EQUAL, (Field) t1, (Field) t2, (Field) t3);
    }

    @Override // org.jooq.Row3
    public final Condition le(Row3<T1, T2, T3> row) {
        return lessOrEqual(row);
    }

    @Override // org.jooq.Row3
    public final Condition le(Record3<T1, T2, T3> record) {
        return lessOrEqual(record);
    }

    @Override // org.jooq.Row3
    public final Condition le(T1 t1, T2 t2, T3 t3) {
        return lessOrEqual((RowImpl3<T1, T2, T3>) t1, (T1) t2, (T2) t3);
    }

    @Override // org.jooq.Row3
    public final Condition le(Field<T1> t1, Field<T2> t2, Field<T3> t3) {
        return lessOrEqual((Field) t1, (Field) t2, (Field) t3);
    }

    @Override // org.jooq.Row3
    public final Condition greaterThan(Row3<T1, T2, T3> row) {
        return compare(Comparator.GREATER, (Row3) row);
    }

    @Override // org.jooq.Row3
    public final Condition greaterThan(Record3<T1, T2, T3> record) {
        return compare(Comparator.GREATER, record);
    }

    @Override // org.jooq.Row3
    public final Condition greaterThan(T1 t1, T2 t2, T3 t3) {
        return compare(Comparator.GREATER, (Comparator) t1, (T1) t2, (T2) t3);
    }

    @Override // org.jooq.Row3
    public final Condition greaterThan(Field<T1> t1, Field<T2> t2, Field<T3> t3) {
        return compare(Comparator.GREATER, (Field) t1, (Field) t2, (Field) t3);
    }

    @Override // org.jooq.Row3
    public final Condition gt(Row3<T1, T2, T3> row) {
        return greaterThan(row);
    }

    @Override // org.jooq.Row3
    public final Condition gt(Record3<T1, T2, T3> record) {
        return greaterThan(record);
    }

    @Override // org.jooq.Row3
    public final Condition gt(T1 t1, T2 t2, T3 t3) {
        return greaterThan((RowImpl3<T1, T2, T3>) t1, (T1) t2, (T2) t3);
    }

    @Override // org.jooq.Row3
    public final Condition gt(Field<T1> t1, Field<T2> t2, Field<T3> t3) {
        return greaterThan((Field) t1, (Field) t2, (Field) t3);
    }

    @Override // org.jooq.Row3
    public final Condition greaterOrEqual(Row3<T1, T2, T3> row) {
        return compare(Comparator.GREATER_OR_EQUAL, (Row3) row);
    }

    @Override // org.jooq.Row3
    public final Condition greaterOrEqual(Record3<T1, T2, T3> record) {
        return compare(Comparator.GREATER_OR_EQUAL, record);
    }

    @Override // org.jooq.Row3
    public final Condition greaterOrEqual(T1 t1, T2 t2, T3 t3) {
        return compare(Comparator.GREATER_OR_EQUAL, (Comparator) t1, (T1) t2, (T2) t3);
    }

    @Override // org.jooq.Row3
    public final Condition greaterOrEqual(Field<T1> t1, Field<T2> t2, Field<T3> t3) {
        return compare(Comparator.GREATER_OR_EQUAL, (Field) t1, (Field) t2, (Field) t3);
    }

    @Override // org.jooq.Row3
    public final Condition ge(Row3<T1, T2, T3> row) {
        return greaterOrEqual(row);
    }

    @Override // org.jooq.Row3
    public final Condition ge(Record3<T1, T2, T3> record) {
        return greaterOrEqual(record);
    }

    @Override // org.jooq.Row3
    public final Condition ge(T1 t1, T2 t2, T3 t3) {
        return greaterOrEqual((RowImpl3<T1, T2, T3>) t1, (T1) t2, (T2) t3);
    }

    @Override // org.jooq.Row3
    public final Condition ge(Field<T1> t1, Field<T2> t2, Field<T3> t3) {
        return greaterOrEqual((Field) t1, (Field) t2, (Field) t3);
    }

    @Override // org.jooq.Row3
    public final BetweenAndStep3<T1, T2, T3> between(T1 t1, T2 t2, T3 t3) {
        return between(DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1)), (SelectField) Tools.field(t3, dataType(2))));
    }

    @Override // org.jooq.Row3
    public final BetweenAndStep3<T1, T2, T3> between(Field<T1> t1, Field<T2> t2, Field<T3> t3) {
        return between(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3));
    }

    @Override // org.jooq.Row3
    public final BetweenAndStep3<T1, T2, T3> between(Row3<T1, T2, T3> row) {
        return new RowBetweenCondition(this, row, false, false);
    }

    @Override // org.jooq.Row3
    public final BetweenAndStep3<T1, T2, T3> between(Record3<T1, T2, T3> record) {
        return between(record.valuesRow());
    }

    @Override // org.jooq.Row3
    public final Condition between(Row3<T1, T2, T3> minValue, Row3<T1, T2, T3> maxValue) {
        return between(minValue).and(maxValue);
    }

    @Override // org.jooq.Row3
    public final Condition between(Record3<T1, T2, T3> minValue, Record3<T1, T2, T3> maxValue) {
        return between(minValue).and(maxValue);
    }

    @Override // org.jooq.Row3
    public final BetweenAndStep3<T1, T2, T3> betweenSymmetric(T1 t1, T2 t2, T3 t3) {
        return betweenSymmetric(DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1)), (SelectField) Tools.field(t3, dataType(2))));
    }

    @Override // org.jooq.Row3
    public final BetweenAndStep3<T1, T2, T3> betweenSymmetric(Field<T1> t1, Field<T2> t2, Field<T3> t3) {
        return betweenSymmetric(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3));
    }

    @Override // org.jooq.Row3
    public final BetweenAndStep3<T1, T2, T3> betweenSymmetric(Row3<T1, T2, T3> row) {
        return new RowBetweenCondition(this, row, false, true);
    }

    @Override // org.jooq.Row3
    public final BetweenAndStep3<T1, T2, T3> betweenSymmetric(Record3<T1, T2, T3> record) {
        return betweenSymmetric(record.valuesRow());
    }

    @Override // org.jooq.Row3
    public final Condition betweenSymmetric(Row3<T1, T2, T3> minValue, Row3<T1, T2, T3> maxValue) {
        return betweenSymmetric(minValue).and(maxValue);
    }

    @Override // org.jooq.Row3
    public final Condition betweenSymmetric(Record3<T1, T2, T3> minValue, Record3<T1, T2, T3> maxValue) {
        return betweenSymmetric(minValue).and(maxValue);
    }

    @Override // org.jooq.Row3
    public final BetweenAndStep3<T1, T2, T3> notBetween(T1 t1, T2 t2, T3 t3) {
        return notBetween(DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1)), (SelectField) Tools.field(t3, dataType(2))));
    }

    @Override // org.jooq.Row3
    public final BetweenAndStep3<T1, T2, T3> notBetween(Field<T1> t1, Field<T2> t2, Field<T3> t3) {
        return notBetween(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3));
    }

    @Override // org.jooq.Row3
    public final BetweenAndStep3<T1, T2, T3> notBetween(Row3<T1, T2, T3> row) {
        return new RowBetweenCondition(this, row, true, false);
    }

    @Override // org.jooq.Row3
    public final BetweenAndStep3<T1, T2, T3> notBetween(Record3<T1, T2, T3> record) {
        return notBetween(record.valuesRow());
    }

    @Override // org.jooq.Row3
    public final Condition notBetween(Row3<T1, T2, T3> minValue, Row3<T1, T2, T3> maxValue) {
        return notBetween(minValue).and(maxValue);
    }

    @Override // org.jooq.Row3
    public final Condition notBetween(Record3<T1, T2, T3> minValue, Record3<T1, T2, T3> maxValue) {
        return notBetween(minValue).and(maxValue);
    }

    @Override // org.jooq.Row3
    public final BetweenAndStep3<T1, T2, T3> notBetweenSymmetric(T1 t1, T2 t2, T3 t3) {
        return notBetweenSymmetric(DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1)), (SelectField) Tools.field(t3, dataType(2))));
    }

    @Override // org.jooq.Row3
    public final BetweenAndStep3<T1, T2, T3> notBetweenSymmetric(Field<T1> t1, Field<T2> t2, Field<T3> t3) {
        return notBetweenSymmetric(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3));
    }

    @Override // org.jooq.Row3
    public final BetweenAndStep3<T1, T2, T3> notBetweenSymmetric(Row3<T1, T2, T3> row) {
        return new RowBetweenCondition(this, row, true, true);
    }

    @Override // org.jooq.Row3
    public final BetweenAndStep3<T1, T2, T3> notBetweenSymmetric(Record3<T1, T2, T3> record) {
        return notBetweenSymmetric(record.valuesRow());
    }

    @Override // org.jooq.Row3
    public final Condition notBetweenSymmetric(Row3<T1, T2, T3> minValue, Row3<T1, T2, T3> maxValue) {
        return notBetweenSymmetric(minValue).and(maxValue);
    }

    @Override // org.jooq.Row3
    public final Condition notBetweenSymmetric(Record3<T1, T2, T3> minValue, Record3<T1, T2, T3> maxValue) {
        return notBetweenSymmetric(minValue).and(maxValue);
    }

    @Override // org.jooq.Row3
    public final Condition isNotDistinctFrom(Row3<T1, T2, T3> row) {
        return new RowIsDistinctFrom((Row) this, (Row) row, true);
    }

    @Override // org.jooq.Row3
    public final Condition isNotDistinctFrom(Record3<T1, T2, T3> record) {
        return isNotDistinctFrom(record.valuesRow());
    }

    @Override // org.jooq.Row3
    public final Condition isNotDistinctFrom(T1 t1, T2 t2, T3 t3) {
        return isNotDistinctFrom((Field) Tools.field(t1, dataType(0)), (Field) Tools.field(t2, dataType(1)), (Field) Tools.field(t3, dataType(2)));
    }

    @Override // org.jooq.Row3
    public final Condition isNotDistinctFrom(Field<T1> t1, Field<T2> t2, Field<T3> t3) {
        return isNotDistinctFrom(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3));
    }

    @Override // org.jooq.Row3
    public final Condition isNotDistinctFrom(Select<? extends Record3<T1, T2, T3>> select) {
        return new RowIsDistinctFrom((Row) this, (Select<?>) select, true);
    }

    @Override // org.jooq.Row3
    public final Condition isDistinctFrom(Row3<T1, T2, T3> row) {
        return new RowIsDistinctFrom((Row) this, (Row) row, false);
    }

    @Override // org.jooq.Row3
    public final Condition isDistinctFrom(Record3<T1, T2, T3> record) {
        return isDistinctFrom(record.valuesRow());
    }

    @Override // org.jooq.Row3
    public final Condition isDistinctFrom(T1 t1, T2 t2, T3 t3) {
        return isDistinctFrom((Field) Tools.field(t1, dataType(0)), (Field) Tools.field(t2, dataType(1)), (Field) Tools.field(t3, dataType(2)));
    }

    @Override // org.jooq.Row3
    public final Condition isDistinctFrom(Field<T1> t1, Field<T2> t2, Field<T3> t3) {
        return isDistinctFrom(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3));
    }

    @Override // org.jooq.Row3
    public final Condition isDistinctFrom(Select<? extends Record3<T1, T2, T3>> select) {
        return new RowIsDistinctFrom((Row) this, (Select<?>) select, false);
    }

    @Override // org.jooq.Row3
    public final Condition in(Row3<T1, T2, T3>... rows) {
        return in(Arrays.asList(rows));
    }

    @Override // org.jooq.Row3
    public final Condition in(Record3<T1, T2, T3>... records) {
        QueryPartList<Row> rows = new QueryPartList<>();
        for (Record3<T1, T2, T3> record3 : records) {
            rows.add((QueryPartList<Row>) record3.valuesRow());
        }
        return new RowInCondition(this, rows, false);
    }

    @Override // org.jooq.Row3
    public final Condition notIn(Row3<T1, T2, T3>... rows) {
        return notIn(Arrays.asList(rows));
    }

    @Override // org.jooq.Row3
    public final Condition notIn(Record3<T1, T2, T3>... records) {
        QueryPartList<Row> rows = new QueryPartList<>();
        for (Record3<T1, T2, T3> record3 : records) {
            rows.add((QueryPartList<Row>) record3.valuesRow());
        }
        return new RowInCondition(this, rows, true);
    }

    @Override // org.jooq.Row3
    public final Condition in(Collection<? extends Row3<T1, T2, T3>> rows) {
        return new RowInCondition(this, new QueryPartList(rows), false);
    }

    @Override // org.jooq.Row3
    public final Condition in(Result<? extends Record3<T1, T2, T3>> result) {
        return new RowInCondition(this, new QueryPartList(Tools.rows(result)), false);
    }

    @Override // org.jooq.Row3
    public final Condition notIn(Collection<? extends Row3<T1, T2, T3>> rows) {
        return new RowInCondition(this, new QueryPartList(rows), true);
    }

    @Override // org.jooq.Row3
    public final Condition notIn(Result<? extends Record3<T1, T2, T3>> result) {
        return new RowInCondition(this, new QueryPartList(Tools.rows(result)), true);
    }

    @Override // org.jooq.Row3
    public final Condition equal(Select<? extends Record3<T1, T2, T3>> select) {
        return compare(Comparator.EQUALS, select);
    }

    @Override // org.jooq.Row3
    public final Condition equal(QuantifiedSelect<? extends Record3<T1, T2, T3>> select) {
        return compare(Comparator.EQUALS, select);
    }

    @Override // org.jooq.Row3
    public final Condition eq(Select<? extends Record3<T1, T2, T3>> select) {
        return equal(select);
    }

    @Override // org.jooq.Row3
    public final Condition eq(QuantifiedSelect<? extends Record3<T1, T2, T3>> select) {
        return equal(select);
    }

    @Override // org.jooq.Row3
    public final Condition notEqual(Select<? extends Record3<T1, T2, T3>> select) {
        return compare(Comparator.NOT_EQUALS, select);
    }

    @Override // org.jooq.Row3
    public final Condition notEqual(QuantifiedSelect<? extends Record3<T1, T2, T3>> select) {
        return compare(Comparator.NOT_EQUALS, select);
    }

    @Override // org.jooq.Row3
    public final Condition ne(Select<? extends Record3<T1, T2, T3>> select) {
        return notEqual(select);
    }

    @Override // org.jooq.Row3
    public final Condition ne(QuantifiedSelect<? extends Record3<T1, T2, T3>> select) {
        return notEqual(select);
    }

    @Override // org.jooq.Row3
    public final Condition greaterThan(Select<? extends Record3<T1, T2, T3>> select) {
        return compare(Comparator.GREATER, select);
    }

    @Override // org.jooq.Row3
    public final Condition greaterThan(QuantifiedSelect<? extends Record3<T1, T2, T3>> select) {
        return compare(Comparator.GREATER, select);
    }

    @Override // org.jooq.Row3
    public final Condition gt(Select<? extends Record3<T1, T2, T3>> select) {
        return greaterThan(select);
    }

    @Override // org.jooq.Row3
    public final Condition gt(QuantifiedSelect<? extends Record3<T1, T2, T3>> select) {
        return greaterThan(select);
    }

    @Override // org.jooq.Row3
    public final Condition greaterOrEqual(Select<? extends Record3<T1, T2, T3>> select) {
        return compare(Comparator.GREATER_OR_EQUAL, select);
    }

    @Override // org.jooq.Row3
    public final Condition greaterOrEqual(QuantifiedSelect<? extends Record3<T1, T2, T3>> select) {
        return compare(Comparator.GREATER_OR_EQUAL, select);
    }

    @Override // org.jooq.Row3
    public final Condition ge(Select<? extends Record3<T1, T2, T3>> select) {
        return greaterOrEqual(select);
    }

    @Override // org.jooq.Row3
    public final Condition ge(QuantifiedSelect<? extends Record3<T1, T2, T3>> select) {
        return greaterOrEqual(select);
    }

    @Override // org.jooq.Row3
    public final Condition lessThan(Select<? extends Record3<T1, T2, T3>> select) {
        return compare(Comparator.LESS, select);
    }

    @Override // org.jooq.Row3
    public final Condition lessThan(QuantifiedSelect<? extends Record3<T1, T2, T3>> select) {
        return compare(Comparator.LESS, select);
    }

    @Override // org.jooq.Row3
    public final Condition lt(Select<? extends Record3<T1, T2, T3>> select) {
        return lessThan(select);
    }

    @Override // org.jooq.Row3
    public final Condition lt(QuantifiedSelect<? extends Record3<T1, T2, T3>> select) {
        return lessThan(select);
    }

    @Override // org.jooq.Row3
    public final Condition lessOrEqual(Select<? extends Record3<T1, T2, T3>> select) {
        return compare(Comparator.LESS_OR_EQUAL, select);
    }

    @Override // org.jooq.Row3
    public final Condition lessOrEqual(QuantifiedSelect<? extends Record3<T1, T2, T3>> select) {
        return compare(Comparator.LESS_OR_EQUAL, select);
    }

    @Override // org.jooq.Row3
    public final Condition le(Select<? extends Record3<T1, T2, T3>> select) {
        return lessOrEqual(select);
    }

    @Override // org.jooq.Row3
    public final Condition le(QuantifiedSelect<? extends Record3<T1, T2, T3>> select) {
        return lessOrEqual(select);
    }

    @Override // org.jooq.Row3
    public final Condition in(Select<? extends Record3<T1, T2, T3>> select) {
        return compare(Comparator.IN, select);
    }

    @Override // org.jooq.Row3
    public final Condition notIn(Select<? extends Record3<T1, T2, T3>> select) {
        return compare(Comparator.NOT_IN, select);
    }
}
