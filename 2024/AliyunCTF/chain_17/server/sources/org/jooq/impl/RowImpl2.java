package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import org.jooq.BetweenAndStep2;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.QuantifiedSelect;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.Row;
import org.jooq.Row2;
import org.jooq.Select;
import org.jooq.SelectField;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RowImpl2.class */
public final class RowImpl2<T1, T2> extends AbstractRow<Record2<T1, T2>> implements Row2<T1, T2> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public RowImpl2(SelectField<T1> field1, SelectField<T2> field2) {
        super((SelectField<?>[]) new SelectField[]{field1, field2});
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RowImpl2(FieldsImpl<?> fields) {
        super(fields);
    }

    @Override // org.jooq.Row2
    public final <U> SelectField<U> mapping(Function2<? super T1, ? super T2, ? extends U> function) {
        return convertFrom(r -> {
            if (r == null) {
                return null;
            }
            return function.apply(r.value1(), r.value2());
        });
    }

    @Override // org.jooq.Row2
    public final <U> SelectField<U> mapping(Class<U> uType, Function2<? super T1, ? super T2, ? extends U> function) {
        return convertFrom(uType, r -> {
            if (r == null) {
                return null;
            }
            return function.apply(r.value1(), r.value2());
        });
    }

    @Override // org.jooq.Row2
    public final Field<T1> field1() {
        return (Field<T1>) this.fields.field(0);
    }

    @Override // org.jooq.Row2
    public final Field<T2> field2() {
        return (Field<T2>) this.fields.field(1);
    }

    @Override // org.jooq.Row2
    public final Condition compare(Comparator comparator, Row2<T1, T2> row) {
        return compare(this, comparator, row);
    }

    @Override // org.jooq.Row2
    public final Condition compare(Comparator comparator, Record2<T1, T2> record) {
        return compare(this, comparator, record.valuesRow());
    }

    @Override // org.jooq.Row2
    public final Condition compare(Comparator comparator, T1 t1, T2 t2) {
        return compare(comparator, (Row2) DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1))));
    }

    @Override // org.jooq.Row2
    public final Condition compare(Comparator comparator, Field<T1> t1, Field<T2> t2) {
        return compare(comparator, (Row2) DSL.row((SelectField) Tools.nullSafe(t1, dataType(0)), (SelectField) Tools.nullSafe(t2, dataType(1))));
    }

    @Override // org.jooq.Row2
    public final Condition compare(Comparator comparator, Select<? extends Record2<T1, T2>> select) {
        return new RowSubqueryCondition(this, select, comparator);
    }

    @Override // org.jooq.Row2
    public final Condition compare(Comparator comparator, QuantifiedSelect<? extends Record2<T1, T2>> select) {
        return new RowSubqueryCondition(this, select, comparator);
    }

    @Override // org.jooq.Row2
    public final Condition equal(Row2<T1, T2> row) {
        return compare(Comparator.EQUALS, (Row2) row);
    }

    @Override // org.jooq.Row2
    public final Condition equal(Record2<T1, T2> record) {
        return compare(Comparator.EQUALS, record);
    }

    @Override // org.jooq.Row2
    public final Condition equal(T1 t1, T2 t2) {
        return compare(Comparator.EQUALS, (Comparator) t1, (T1) t2);
    }

    @Override // org.jooq.Row2
    public final Condition equal(Field<T1> t1, Field<T2> t2) {
        return compare(Comparator.EQUALS, (Field) t1, (Field) t2);
    }

    @Override // org.jooq.Row2
    public final Condition eq(Row2<T1, T2> row) {
        return equal(row);
    }

    @Override // org.jooq.Row2
    public final Condition eq(Record2<T1, T2> record) {
        return equal(record);
    }

    @Override // org.jooq.Row2
    public final Condition eq(T1 t1, T2 t2) {
        return equal((RowImpl2<T1, T2>) t1, (T1) t2);
    }

    @Override // org.jooq.Row2
    public final Condition eq(Field<T1> t1, Field<T2> t2) {
        return equal((Field) t1, (Field) t2);
    }

    @Override // org.jooq.Row2
    public final Condition notEqual(Row2<T1, T2> row) {
        return compare(Comparator.NOT_EQUALS, (Row2) row);
    }

    @Override // org.jooq.Row2
    public final Condition notEqual(Record2<T1, T2> record) {
        return compare(Comparator.NOT_EQUALS, record);
    }

    @Override // org.jooq.Row2
    public final Condition notEqual(T1 t1, T2 t2) {
        return compare(Comparator.NOT_EQUALS, (Comparator) t1, (T1) t2);
    }

    @Override // org.jooq.Row2
    public final Condition notEqual(Field<T1> t1, Field<T2> t2) {
        return compare(Comparator.NOT_EQUALS, (Field) t1, (Field) t2);
    }

    @Override // org.jooq.Row2
    public final Condition ne(Row2<T1, T2> row) {
        return notEqual(row);
    }

    @Override // org.jooq.Row2
    public final Condition ne(Record2<T1, T2> record) {
        return notEqual(record);
    }

    @Override // org.jooq.Row2
    public final Condition ne(T1 t1, T2 t2) {
        return notEqual((RowImpl2<T1, T2>) t1, (T1) t2);
    }

    @Override // org.jooq.Row2
    public final Condition ne(Field<T1> t1, Field<T2> t2) {
        return notEqual((Field) t1, (Field) t2);
    }

    @Override // org.jooq.Row2
    public final Condition lessThan(Row2<T1, T2> row) {
        return compare(Comparator.LESS, (Row2) row);
    }

    @Override // org.jooq.Row2
    public final Condition lessThan(Record2<T1, T2> record) {
        return compare(Comparator.LESS, record);
    }

    @Override // org.jooq.Row2
    public final Condition lessThan(T1 t1, T2 t2) {
        return compare(Comparator.LESS, (Comparator) t1, (T1) t2);
    }

    @Override // org.jooq.Row2
    public final Condition lessThan(Field<T1> t1, Field<T2> t2) {
        return compare(Comparator.LESS, (Field) t1, (Field) t2);
    }

    @Override // org.jooq.Row2
    public final Condition lt(Row2<T1, T2> row) {
        return lessThan(row);
    }

    @Override // org.jooq.Row2
    public final Condition lt(Record2<T1, T2> record) {
        return lessThan(record);
    }

    @Override // org.jooq.Row2
    public final Condition lt(T1 t1, T2 t2) {
        return lessThan((RowImpl2<T1, T2>) t1, (T1) t2);
    }

    @Override // org.jooq.Row2
    public final Condition lt(Field<T1> t1, Field<T2> t2) {
        return lessThan((Field) t1, (Field) t2);
    }

    @Override // org.jooq.Row2
    public final Condition lessOrEqual(Row2<T1, T2> row) {
        return compare(Comparator.LESS_OR_EQUAL, (Row2) row);
    }

    @Override // org.jooq.Row2
    public final Condition lessOrEqual(Record2<T1, T2> record) {
        return compare(Comparator.LESS_OR_EQUAL, record);
    }

    @Override // org.jooq.Row2
    public final Condition lessOrEqual(T1 t1, T2 t2) {
        return compare(Comparator.LESS_OR_EQUAL, (Comparator) t1, (T1) t2);
    }

    @Override // org.jooq.Row2
    public final Condition lessOrEqual(Field<T1> t1, Field<T2> t2) {
        return compare(Comparator.LESS_OR_EQUAL, (Field) t1, (Field) t2);
    }

    @Override // org.jooq.Row2
    public final Condition le(Row2<T1, T2> row) {
        return lessOrEqual(row);
    }

    @Override // org.jooq.Row2
    public final Condition le(Record2<T1, T2> record) {
        return lessOrEqual(record);
    }

    @Override // org.jooq.Row2
    public final Condition le(T1 t1, T2 t2) {
        return lessOrEqual((RowImpl2<T1, T2>) t1, (T1) t2);
    }

    @Override // org.jooq.Row2
    public final Condition le(Field<T1> t1, Field<T2> t2) {
        return lessOrEqual((Field) t1, (Field) t2);
    }

    @Override // org.jooq.Row2
    public final Condition greaterThan(Row2<T1, T2> row) {
        return compare(Comparator.GREATER, (Row2) row);
    }

    @Override // org.jooq.Row2
    public final Condition greaterThan(Record2<T1, T2> record) {
        return compare(Comparator.GREATER, record);
    }

    @Override // org.jooq.Row2
    public final Condition greaterThan(T1 t1, T2 t2) {
        return compare(Comparator.GREATER, (Comparator) t1, (T1) t2);
    }

    @Override // org.jooq.Row2
    public final Condition greaterThan(Field<T1> t1, Field<T2> t2) {
        return compare(Comparator.GREATER, (Field) t1, (Field) t2);
    }

    @Override // org.jooq.Row2
    public final Condition gt(Row2<T1, T2> row) {
        return greaterThan(row);
    }

    @Override // org.jooq.Row2
    public final Condition gt(Record2<T1, T2> record) {
        return greaterThan(record);
    }

    @Override // org.jooq.Row2
    public final Condition gt(T1 t1, T2 t2) {
        return greaterThan((RowImpl2<T1, T2>) t1, (T1) t2);
    }

    @Override // org.jooq.Row2
    public final Condition gt(Field<T1> t1, Field<T2> t2) {
        return greaterThan((Field) t1, (Field) t2);
    }

    @Override // org.jooq.Row2
    public final Condition greaterOrEqual(Row2<T1, T2> row) {
        return compare(Comparator.GREATER_OR_EQUAL, (Row2) row);
    }

    @Override // org.jooq.Row2
    public final Condition greaterOrEqual(Record2<T1, T2> record) {
        return compare(Comparator.GREATER_OR_EQUAL, record);
    }

    @Override // org.jooq.Row2
    public final Condition greaterOrEqual(T1 t1, T2 t2) {
        return compare(Comparator.GREATER_OR_EQUAL, (Comparator) t1, (T1) t2);
    }

    @Override // org.jooq.Row2
    public final Condition greaterOrEqual(Field<T1> t1, Field<T2> t2) {
        return compare(Comparator.GREATER_OR_EQUAL, (Field) t1, (Field) t2);
    }

    @Override // org.jooq.Row2
    public final Condition ge(Row2<T1, T2> row) {
        return greaterOrEqual(row);
    }

    @Override // org.jooq.Row2
    public final Condition ge(Record2<T1, T2> record) {
        return greaterOrEqual(record);
    }

    @Override // org.jooq.Row2
    public final Condition ge(T1 t1, T2 t2) {
        return greaterOrEqual((RowImpl2<T1, T2>) t1, (T1) t2);
    }

    @Override // org.jooq.Row2
    public final Condition ge(Field<T1> t1, Field<T2> t2) {
        return greaterOrEqual((Field) t1, (Field) t2);
    }

    @Override // org.jooq.Row2
    public final BetweenAndStep2<T1, T2> between(T1 t1, T2 t2) {
        return between(DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1))));
    }

    @Override // org.jooq.Row2
    public final BetweenAndStep2<T1, T2> between(Field<T1> t1, Field<T2> t2) {
        return between(DSL.row((SelectField) t1, (SelectField) t2));
    }

    @Override // org.jooq.Row2
    public final BetweenAndStep2<T1, T2> between(Row2<T1, T2> row) {
        return new RowBetweenCondition(this, row, false, false);
    }

    @Override // org.jooq.Row2
    public final BetweenAndStep2<T1, T2> between(Record2<T1, T2> record) {
        return between(record.valuesRow());
    }

    @Override // org.jooq.Row2
    public final Condition between(Row2<T1, T2> minValue, Row2<T1, T2> maxValue) {
        return between(minValue).and(maxValue);
    }

    @Override // org.jooq.Row2
    public final Condition between(Record2<T1, T2> minValue, Record2<T1, T2> maxValue) {
        return between(minValue).and(maxValue);
    }

    @Override // org.jooq.Row2
    public final BetweenAndStep2<T1, T2> betweenSymmetric(T1 t1, T2 t2) {
        return betweenSymmetric(DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1))));
    }

    @Override // org.jooq.Row2
    public final BetweenAndStep2<T1, T2> betweenSymmetric(Field<T1> t1, Field<T2> t2) {
        return betweenSymmetric(DSL.row((SelectField) t1, (SelectField) t2));
    }

    @Override // org.jooq.Row2
    public final BetweenAndStep2<T1, T2> betweenSymmetric(Row2<T1, T2> row) {
        return new RowBetweenCondition(this, row, false, true);
    }

    @Override // org.jooq.Row2
    public final BetweenAndStep2<T1, T2> betweenSymmetric(Record2<T1, T2> record) {
        return betweenSymmetric(record.valuesRow());
    }

    @Override // org.jooq.Row2
    public final Condition betweenSymmetric(Row2<T1, T2> minValue, Row2<T1, T2> maxValue) {
        return betweenSymmetric(minValue).and(maxValue);
    }

    @Override // org.jooq.Row2
    public final Condition betweenSymmetric(Record2<T1, T2> minValue, Record2<T1, T2> maxValue) {
        return betweenSymmetric(minValue).and(maxValue);
    }

    @Override // org.jooq.Row2
    public final BetweenAndStep2<T1, T2> notBetween(T1 t1, T2 t2) {
        return notBetween(DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1))));
    }

    @Override // org.jooq.Row2
    public final BetweenAndStep2<T1, T2> notBetween(Field<T1> t1, Field<T2> t2) {
        return notBetween(DSL.row((SelectField) t1, (SelectField) t2));
    }

    @Override // org.jooq.Row2
    public final BetweenAndStep2<T1, T2> notBetween(Row2<T1, T2> row) {
        return new RowBetweenCondition(this, row, true, false);
    }

    @Override // org.jooq.Row2
    public final BetweenAndStep2<T1, T2> notBetween(Record2<T1, T2> record) {
        return notBetween(record.valuesRow());
    }

    @Override // org.jooq.Row2
    public final Condition notBetween(Row2<T1, T2> minValue, Row2<T1, T2> maxValue) {
        return notBetween(minValue).and(maxValue);
    }

    @Override // org.jooq.Row2
    public final Condition notBetween(Record2<T1, T2> minValue, Record2<T1, T2> maxValue) {
        return notBetween(minValue).and(maxValue);
    }

    @Override // org.jooq.Row2
    public final BetweenAndStep2<T1, T2> notBetweenSymmetric(T1 t1, T2 t2) {
        return notBetweenSymmetric(DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1))));
    }

    @Override // org.jooq.Row2
    public final BetweenAndStep2<T1, T2> notBetweenSymmetric(Field<T1> t1, Field<T2> t2) {
        return notBetweenSymmetric(DSL.row((SelectField) t1, (SelectField) t2));
    }

    @Override // org.jooq.Row2
    public final BetweenAndStep2<T1, T2> notBetweenSymmetric(Row2<T1, T2> row) {
        return new RowBetweenCondition(this, row, true, true);
    }

    @Override // org.jooq.Row2
    public final BetweenAndStep2<T1, T2> notBetweenSymmetric(Record2<T1, T2> record) {
        return notBetweenSymmetric(record.valuesRow());
    }

    @Override // org.jooq.Row2
    public final Condition notBetweenSymmetric(Row2<T1, T2> minValue, Row2<T1, T2> maxValue) {
        return notBetweenSymmetric(minValue).and(maxValue);
    }

    @Override // org.jooq.Row2
    public final Condition notBetweenSymmetric(Record2<T1, T2> minValue, Record2<T1, T2> maxValue) {
        return notBetweenSymmetric(minValue).and(maxValue);
    }

    @Override // org.jooq.Row2
    public final Condition isNotDistinctFrom(Row2<T1, T2> row) {
        return new RowIsDistinctFrom((Row) this, (Row) row, true);
    }

    @Override // org.jooq.Row2
    public final Condition isNotDistinctFrom(Record2<T1, T2> record) {
        return isNotDistinctFrom(record.valuesRow());
    }

    @Override // org.jooq.Row2
    public final Condition isNotDistinctFrom(T1 t1, T2 t2) {
        return isNotDistinctFrom((Field) Tools.field(t1, dataType(0)), (Field) Tools.field(t2, dataType(1)));
    }

    @Override // org.jooq.Row2
    public final Condition isNotDistinctFrom(Field<T1> t1, Field<T2> t2) {
        return isNotDistinctFrom(DSL.row((SelectField) t1, (SelectField) t2));
    }

    @Override // org.jooq.Row2
    public final Condition isNotDistinctFrom(Select<? extends Record2<T1, T2>> select) {
        return new RowIsDistinctFrom((Row) this, (Select<?>) select, true);
    }

    @Override // org.jooq.Row2
    public final Condition isDistinctFrom(Row2<T1, T2> row) {
        return new RowIsDistinctFrom((Row) this, (Row) row, false);
    }

    @Override // org.jooq.Row2
    public final Condition isDistinctFrom(Record2<T1, T2> record) {
        return isDistinctFrom(record.valuesRow());
    }

    @Override // org.jooq.Row2
    public final Condition isDistinctFrom(T1 t1, T2 t2) {
        return isDistinctFrom((Field) Tools.field(t1, dataType(0)), (Field) Tools.field(t2, dataType(1)));
    }

    @Override // org.jooq.Row2
    public final Condition isDistinctFrom(Field<T1> t1, Field<T2> t2) {
        return isDistinctFrom(DSL.row((SelectField) t1, (SelectField) t2));
    }

    @Override // org.jooq.Row2
    public final Condition isDistinctFrom(Select<? extends Record2<T1, T2>> select) {
        return new RowIsDistinctFrom((Row) this, (Select<?>) select, false);
    }

    @Override // org.jooq.Row2
    public final Condition in(Row2<T1, T2>... rows) {
        return in(Arrays.asList(rows));
    }

    @Override // org.jooq.Row2
    public final Condition in(Record2<T1, T2>... records) {
        QueryPartList<Row> rows = new QueryPartList<>();
        for (Record2<T1, T2> record2 : records) {
            rows.add((QueryPartList<Row>) record2.valuesRow());
        }
        return new RowInCondition(this, rows, false);
    }

    @Override // org.jooq.Row2
    public final Condition notIn(Row2<T1, T2>... rows) {
        return notIn(Arrays.asList(rows));
    }

    @Override // org.jooq.Row2
    public final Condition notIn(Record2<T1, T2>... records) {
        QueryPartList<Row> rows = new QueryPartList<>();
        for (Record2<T1, T2> record2 : records) {
            rows.add((QueryPartList<Row>) record2.valuesRow());
        }
        return new RowInCondition(this, rows, true);
    }

    @Override // org.jooq.Row2
    public final Condition in(Collection<? extends Row2<T1, T2>> rows) {
        return new RowInCondition(this, new QueryPartList(rows), false);
    }

    @Override // org.jooq.Row2
    public final Condition in(Result<? extends Record2<T1, T2>> result) {
        return new RowInCondition(this, new QueryPartList(Tools.rows(result)), false);
    }

    @Override // org.jooq.Row2
    public final Condition notIn(Collection<? extends Row2<T1, T2>> rows) {
        return new RowInCondition(this, new QueryPartList(rows), true);
    }

    @Override // org.jooq.Row2
    public final Condition notIn(Result<? extends Record2<T1, T2>> result) {
        return new RowInCondition(this, new QueryPartList(Tools.rows(result)), true);
    }

    @Override // org.jooq.Row2
    public final Condition equal(Select<? extends Record2<T1, T2>> select) {
        return compare(Comparator.EQUALS, select);
    }

    @Override // org.jooq.Row2
    public final Condition equal(QuantifiedSelect<? extends Record2<T1, T2>> select) {
        return compare(Comparator.EQUALS, select);
    }

    @Override // org.jooq.Row2
    public final Condition eq(Select<? extends Record2<T1, T2>> select) {
        return equal(select);
    }

    @Override // org.jooq.Row2
    public final Condition eq(QuantifiedSelect<? extends Record2<T1, T2>> select) {
        return equal(select);
    }

    @Override // org.jooq.Row2
    public final Condition notEqual(Select<? extends Record2<T1, T2>> select) {
        return compare(Comparator.NOT_EQUALS, select);
    }

    @Override // org.jooq.Row2
    public final Condition notEqual(QuantifiedSelect<? extends Record2<T1, T2>> select) {
        return compare(Comparator.NOT_EQUALS, select);
    }

    @Override // org.jooq.Row2
    public final Condition ne(Select<? extends Record2<T1, T2>> select) {
        return notEqual(select);
    }

    @Override // org.jooq.Row2
    public final Condition ne(QuantifiedSelect<? extends Record2<T1, T2>> select) {
        return notEqual(select);
    }

    @Override // org.jooq.Row2
    public final Condition greaterThan(Select<? extends Record2<T1, T2>> select) {
        return compare(Comparator.GREATER, select);
    }

    @Override // org.jooq.Row2
    public final Condition greaterThan(QuantifiedSelect<? extends Record2<T1, T2>> select) {
        return compare(Comparator.GREATER, select);
    }

    @Override // org.jooq.Row2
    public final Condition gt(Select<? extends Record2<T1, T2>> select) {
        return greaterThan(select);
    }

    @Override // org.jooq.Row2
    public final Condition gt(QuantifiedSelect<? extends Record2<T1, T2>> select) {
        return greaterThan(select);
    }

    @Override // org.jooq.Row2
    public final Condition greaterOrEqual(Select<? extends Record2<T1, T2>> select) {
        return compare(Comparator.GREATER_OR_EQUAL, select);
    }

    @Override // org.jooq.Row2
    public final Condition greaterOrEqual(QuantifiedSelect<? extends Record2<T1, T2>> select) {
        return compare(Comparator.GREATER_OR_EQUAL, select);
    }

    @Override // org.jooq.Row2
    public final Condition ge(Select<? extends Record2<T1, T2>> select) {
        return greaterOrEqual(select);
    }

    @Override // org.jooq.Row2
    public final Condition ge(QuantifiedSelect<? extends Record2<T1, T2>> select) {
        return greaterOrEqual(select);
    }

    @Override // org.jooq.Row2
    public final Condition lessThan(Select<? extends Record2<T1, T2>> select) {
        return compare(Comparator.LESS, select);
    }

    @Override // org.jooq.Row2
    public final Condition lessThan(QuantifiedSelect<? extends Record2<T1, T2>> select) {
        return compare(Comparator.LESS, select);
    }

    @Override // org.jooq.Row2
    public final Condition lt(Select<? extends Record2<T1, T2>> select) {
        return lessThan(select);
    }

    @Override // org.jooq.Row2
    public final Condition lt(QuantifiedSelect<? extends Record2<T1, T2>> select) {
        return lessThan(select);
    }

    @Override // org.jooq.Row2
    public final Condition lessOrEqual(Select<? extends Record2<T1, T2>> select) {
        return compare(Comparator.LESS_OR_EQUAL, select);
    }

    @Override // org.jooq.Row2
    public final Condition lessOrEqual(QuantifiedSelect<? extends Record2<T1, T2>> select) {
        return compare(Comparator.LESS_OR_EQUAL, select);
    }

    @Override // org.jooq.Row2
    public final Condition le(Select<? extends Record2<T1, T2>> select) {
        return lessOrEqual(select);
    }

    @Override // org.jooq.Row2
    public final Condition le(QuantifiedSelect<? extends Record2<T1, T2>> select) {
        return lessOrEqual(select);
    }

    @Override // org.jooq.Row2
    public final Condition in(Select<? extends Record2<T1, T2>> select) {
        return compare(Comparator.IN, select);
    }

    @Override // org.jooq.Row2
    public final Condition notIn(Select<? extends Record2<T1, T2>> select) {
        return compare(Comparator.NOT_IN, select);
    }

    @Override // org.jooq.Row2
    public final Condition overlaps(T1 t1, T2 t2) {
        return overlaps(DSL.row(t1, t2));
    }

    @Override // org.jooq.Row2
    public final Condition overlaps(Field<T1> t1, Field<T2> t2) {
        return overlaps(DSL.row((SelectField) t1, (SelectField) t2));
    }

    @Override // org.jooq.Row2
    public final Condition overlaps(Row2<T1, T2> row) {
        return new RowOverlaps(this, row);
    }
}
