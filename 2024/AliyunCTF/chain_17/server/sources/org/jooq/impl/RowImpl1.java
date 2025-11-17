package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import org.jooq.BetweenAndStep1;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.QuantifiedSelect;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.Row;
import org.jooq.Row1;
import org.jooq.Select;
import org.jooq.SelectField;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RowImpl1.class */
public final class RowImpl1<T1> extends AbstractRow<Record1<T1>> implements Row1<T1> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public RowImpl1(SelectField<T1> field1) {
        super((SelectField<?>[]) new SelectField[]{field1});
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RowImpl1(FieldsImpl<?> fields) {
        super(fields);
    }

    @Override // org.jooq.Row1
    public final <U> SelectField<U> mapping(org.jooq.Function1<? super T1, ? extends U> function) {
        return convertFrom(r -> {
            if (r == null) {
                return null;
            }
            return function.apply(r.value1());
        });
    }

    @Override // org.jooq.Row1
    public final <U> SelectField<U> mapping(Class<U> uType, org.jooq.Function1<? super T1, ? extends U> function) {
        return convertFrom(uType, r -> {
            if (r == null) {
                return null;
            }
            return function.apply(r.value1());
        });
    }

    @Override // org.jooq.Row1
    public final Field<T1> field1() {
        return (Field<T1>) this.fields.field(0);
    }

    @Override // org.jooq.Row1
    public final Condition compare(Comparator comparator, Row1<T1> row) {
        return compare(this, comparator, row);
    }

    @Override // org.jooq.Row1
    public final Condition compare(Comparator comparator, Record1<T1> record) {
        return compare(this, comparator, record.valuesRow());
    }

    @Override // org.jooq.Row1
    public final Condition compare(Comparator comparator, T1 t1) {
        return compare(comparator, (Row1) DSL.row((SelectField) Tools.field(t1, dataType(0))));
    }

    @Override // org.jooq.Row1
    public final Condition compare(Comparator comparator, Field<T1> t1) {
        return compare(comparator, (Row1) DSL.row((SelectField) Tools.nullSafe(t1, dataType(0))));
    }

    @Override // org.jooq.Row1
    public final Condition compare(Comparator comparator, Select<? extends Record1<T1>> select) {
        return new RowSubqueryCondition(this, select, comparator);
    }

    @Override // org.jooq.Row1
    public final Condition compare(Comparator comparator, QuantifiedSelect<? extends Record1<T1>> select) {
        return new RowSubqueryCondition(this, select, comparator);
    }

    @Override // org.jooq.Row1
    public final Condition equal(Row1<T1> row) {
        return compare(Comparator.EQUALS, (Row1) row);
    }

    @Override // org.jooq.Row1
    public final Condition equal(Record1<T1> record) {
        return compare(Comparator.EQUALS, (Record1) record);
    }

    @Override // org.jooq.Row1
    public final Condition equal(T1 t1) {
        return compare(Comparator.EQUALS, (Comparator) t1);
    }

    @Override // org.jooq.Row1
    public final Condition equal(Field<T1> t1) {
        return compare(Comparator.EQUALS, (Field) t1);
    }

    @Override // org.jooq.Row1
    public final Condition eq(Row1<T1> row) {
        return equal((Row1) row);
    }

    @Override // org.jooq.Row1
    public final Condition eq(Record1<T1> record) {
        return equal((Record1) record);
    }

    @Override // org.jooq.Row1
    public final Condition eq(T1 t1) {
        return equal((RowImpl1<T1>) t1);
    }

    @Override // org.jooq.Row1
    public final Condition eq(Field<T1> t1) {
        return equal((Field) t1);
    }

    @Override // org.jooq.Row1
    public final Condition notEqual(Row1<T1> row) {
        return compare(Comparator.NOT_EQUALS, (Row1) row);
    }

    @Override // org.jooq.Row1
    public final Condition notEqual(Record1<T1> record) {
        return compare(Comparator.NOT_EQUALS, (Record1) record);
    }

    @Override // org.jooq.Row1
    public final Condition notEqual(T1 t1) {
        return compare(Comparator.NOT_EQUALS, (Comparator) t1);
    }

    @Override // org.jooq.Row1
    public final Condition notEqual(Field<T1> t1) {
        return compare(Comparator.NOT_EQUALS, (Field) t1);
    }

    @Override // org.jooq.Row1
    public final Condition ne(Row1<T1> row) {
        return notEqual((Row1) row);
    }

    @Override // org.jooq.Row1
    public final Condition ne(Record1<T1> record) {
        return notEqual((Record1) record);
    }

    @Override // org.jooq.Row1
    public final Condition ne(T1 t1) {
        return notEqual((RowImpl1<T1>) t1);
    }

    @Override // org.jooq.Row1
    public final Condition ne(Field<T1> t1) {
        return notEqual((Field) t1);
    }

    @Override // org.jooq.Row1
    public final Condition lessThan(Row1<T1> row) {
        return compare(Comparator.LESS, (Row1) row);
    }

    @Override // org.jooq.Row1
    public final Condition lessThan(Record1<T1> record) {
        return compare(Comparator.LESS, (Record1) record);
    }

    @Override // org.jooq.Row1
    public final Condition lessThan(T1 t1) {
        return compare(Comparator.LESS, (Comparator) t1);
    }

    @Override // org.jooq.Row1
    public final Condition lessThan(Field<T1> t1) {
        return compare(Comparator.LESS, (Field) t1);
    }

    @Override // org.jooq.Row1
    public final Condition lt(Row1<T1> row) {
        return lessThan((Row1) row);
    }

    @Override // org.jooq.Row1
    public final Condition lt(Record1<T1> record) {
        return lessThan((Record1) record);
    }

    @Override // org.jooq.Row1
    public final Condition lt(T1 t1) {
        return lessThan((RowImpl1<T1>) t1);
    }

    @Override // org.jooq.Row1
    public final Condition lt(Field<T1> t1) {
        return lessThan((Field) t1);
    }

    @Override // org.jooq.Row1
    public final Condition lessOrEqual(Row1<T1> row) {
        return compare(Comparator.LESS_OR_EQUAL, (Row1) row);
    }

    @Override // org.jooq.Row1
    public final Condition lessOrEqual(Record1<T1> record) {
        return compare(Comparator.LESS_OR_EQUAL, (Record1) record);
    }

    @Override // org.jooq.Row1
    public final Condition lessOrEqual(T1 t1) {
        return compare(Comparator.LESS_OR_EQUAL, (Comparator) t1);
    }

    @Override // org.jooq.Row1
    public final Condition lessOrEqual(Field<T1> t1) {
        return compare(Comparator.LESS_OR_EQUAL, (Field) t1);
    }

    @Override // org.jooq.Row1
    public final Condition le(Row1<T1> row) {
        return lessOrEqual((Row1) row);
    }

    @Override // org.jooq.Row1
    public final Condition le(Record1<T1> record) {
        return lessOrEqual((Record1) record);
    }

    @Override // org.jooq.Row1
    public final Condition le(T1 t1) {
        return lessOrEqual((RowImpl1<T1>) t1);
    }

    @Override // org.jooq.Row1
    public final Condition le(Field<T1> t1) {
        return lessOrEqual((Field) t1);
    }

    @Override // org.jooq.Row1
    public final Condition greaterThan(Row1<T1> row) {
        return compare(Comparator.GREATER, (Row1) row);
    }

    @Override // org.jooq.Row1
    public final Condition greaterThan(Record1<T1> record) {
        return compare(Comparator.GREATER, (Record1) record);
    }

    @Override // org.jooq.Row1
    public final Condition greaterThan(T1 t1) {
        return compare(Comparator.GREATER, (Comparator) t1);
    }

    @Override // org.jooq.Row1
    public final Condition greaterThan(Field<T1> t1) {
        return compare(Comparator.GREATER, (Field) t1);
    }

    @Override // org.jooq.Row1
    public final Condition gt(Row1<T1> row) {
        return greaterThan((Row1) row);
    }

    @Override // org.jooq.Row1
    public final Condition gt(Record1<T1> record) {
        return greaterThan((Record1) record);
    }

    @Override // org.jooq.Row1
    public final Condition gt(T1 t1) {
        return greaterThan((RowImpl1<T1>) t1);
    }

    @Override // org.jooq.Row1
    public final Condition gt(Field<T1> t1) {
        return greaterThan((Field) t1);
    }

    @Override // org.jooq.Row1
    public final Condition greaterOrEqual(Row1<T1> row) {
        return compare(Comparator.GREATER_OR_EQUAL, (Row1) row);
    }

    @Override // org.jooq.Row1
    public final Condition greaterOrEqual(Record1<T1> record) {
        return compare(Comparator.GREATER_OR_EQUAL, (Record1) record);
    }

    @Override // org.jooq.Row1
    public final Condition greaterOrEqual(T1 t1) {
        return compare(Comparator.GREATER_OR_EQUAL, (Comparator) t1);
    }

    @Override // org.jooq.Row1
    public final Condition greaterOrEqual(Field<T1> t1) {
        return compare(Comparator.GREATER_OR_EQUAL, (Field) t1);
    }

    @Override // org.jooq.Row1
    public final Condition ge(Row1<T1> row) {
        return greaterOrEqual((Row1) row);
    }

    @Override // org.jooq.Row1
    public final Condition ge(Record1<T1> record) {
        return greaterOrEqual((Record1) record);
    }

    @Override // org.jooq.Row1
    public final Condition ge(T1 t1) {
        return greaterOrEqual((RowImpl1<T1>) t1);
    }

    @Override // org.jooq.Row1
    public final Condition ge(Field<T1> t1) {
        return greaterOrEqual((Field) t1);
    }

    @Override // org.jooq.Row1
    public final BetweenAndStep1<T1> between(T1 t1) {
        return between((Row1) DSL.row((SelectField) Tools.field(t1, dataType(0))));
    }

    @Override // org.jooq.Row1
    public final BetweenAndStep1<T1> between(Field<T1> t1) {
        return between((Row1) DSL.row((SelectField) t1));
    }

    @Override // org.jooq.Row1
    public final BetweenAndStep1<T1> between(Row1<T1> row) {
        return new RowBetweenCondition(this, row, false, false);
    }

    @Override // org.jooq.Row1
    public final BetweenAndStep1<T1> between(Record1<T1> record) {
        return between((Row1) record.valuesRow());
    }

    @Override // org.jooq.Row1
    public final Condition between(Row1<T1> minValue, Row1<T1> maxValue) {
        return between((Row1) minValue).and((Row1) maxValue);
    }

    @Override // org.jooq.Row1
    public final Condition between(Record1<T1> minValue, Record1<T1> maxValue) {
        return between((Record1) minValue).and((Record1) maxValue);
    }

    @Override // org.jooq.Row1
    public final BetweenAndStep1<T1> betweenSymmetric(T1 t1) {
        return betweenSymmetric((Row1) DSL.row((SelectField) Tools.field(t1, dataType(0))));
    }

    @Override // org.jooq.Row1
    public final BetweenAndStep1<T1> betweenSymmetric(Field<T1> t1) {
        return betweenSymmetric((Row1) DSL.row((SelectField) t1));
    }

    @Override // org.jooq.Row1
    public final BetweenAndStep1<T1> betweenSymmetric(Row1<T1> row) {
        return new RowBetweenCondition(this, row, false, true);
    }

    @Override // org.jooq.Row1
    public final BetweenAndStep1<T1> betweenSymmetric(Record1<T1> record) {
        return betweenSymmetric((Row1) record.valuesRow());
    }

    @Override // org.jooq.Row1
    public final Condition betweenSymmetric(Row1<T1> minValue, Row1<T1> maxValue) {
        return betweenSymmetric((Row1) minValue).and((Row1) maxValue);
    }

    @Override // org.jooq.Row1
    public final Condition betweenSymmetric(Record1<T1> minValue, Record1<T1> maxValue) {
        return betweenSymmetric((Record1) minValue).and((Record1) maxValue);
    }

    @Override // org.jooq.Row1
    public final BetweenAndStep1<T1> notBetween(T1 t1) {
        return notBetween((Row1) DSL.row((SelectField) Tools.field(t1, dataType(0))));
    }

    @Override // org.jooq.Row1
    public final BetweenAndStep1<T1> notBetween(Field<T1> t1) {
        return notBetween((Row1) DSL.row((SelectField) t1));
    }

    @Override // org.jooq.Row1
    public final BetweenAndStep1<T1> notBetween(Row1<T1> row) {
        return new RowBetweenCondition(this, row, true, false);
    }

    @Override // org.jooq.Row1
    public final BetweenAndStep1<T1> notBetween(Record1<T1> record) {
        return notBetween((Row1) record.valuesRow());
    }

    @Override // org.jooq.Row1
    public final Condition notBetween(Row1<T1> minValue, Row1<T1> maxValue) {
        return notBetween((Row1) minValue).and((Row1) maxValue);
    }

    @Override // org.jooq.Row1
    public final Condition notBetween(Record1<T1> minValue, Record1<T1> maxValue) {
        return notBetween((Record1) minValue).and((Record1) maxValue);
    }

    @Override // org.jooq.Row1
    public final BetweenAndStep1<T1> notBetweenSymmetric(T1 t1) {
        return notBetweenSymmetric((Row1) DSL.row((SelectField) Tools.field(t1, dataType(0))));
    }

    @Override // org.jooq.Row1
    public final BetweenAndStep1<T1> notBetweenSymmetric(Field<T1> t1) {
        return notBetweenSymmetric((Row1) DSL.row((SelectField) t1));
    }

    @Override // org.jooq.Row1
    public final BetweenAndStep1<T1> notBetweenSymmetric(Row1<T1> row) {
        return new RowBetweenCondition(this, row, true, true);
    }

    @Override // org.jooq.Row1
    public final BetweenAndStep1<T1> notBetweenSymmetric(Record1<T1> record) {
        return notBetweenSymmetric((Row1) record.valuesRow());
    }

    @Override // org.jooq.Row1
    public final Condition notBetweenSymmetric(Row1<T1> minValue, Row1<T1> maxValue) {
        return notBetweenSymmetric((Row1) minValue).and((Row1) maxValue);
    }

    @Override // org.jooq.Row1
    public final Condition notBetweenSymmetric(Record1<T1> minValue, Record1<T1> maxValue) {
        return notBetweenSymmetric((Record1) minValue).and((Record1) maxValue);
    }

    @Override // org.jooq.Row1
    public final Condition isNotDistinctFrom(Row1<T1> row) {
        return new RowIsDistinctFrom((Row) this, (Row) row, true);
    }

    @Override // org.jooq.Row1
    public final Condition isNotDistinctFrom(Record1<T1> record) {
        return isNotDistinctFrom((Row1) record.valuesRow());
    }

    @Override // org.jooq.Row1
    public final Condition isNotDistinctFrom(T1 t1) {
        return isNotDistinctFrom((Field) Tools.field(t1, dataType(0)));
    }

    @Override // org.jooq.Row1
    public final Condition isNotDistinctFrom(Field<T1> t1) {
        return isNotDistinctFrom((Row1) DSL.row((SelectField) t1));
    }

    @Override // org.jooq.Row1
    public final Condition isNotDistinctFrom(Select<? extends Record1<T1>> select) {
        return new RowIsDistinctFrom((Row) this, (Select<?>) select, true);
    }

    @Override // org.jooq.Row1
    public final Condition isDistinctFrom(Row1<T1> row) {
        return new RowIsDistinctFrom((Row) this, (Row) row, false);
    }

    @Override // org.jooq.Row1
    public final Condition isDistinctFrom(Record1<T1> record) {
        return isDistinctFrom((Row1) record.valuesRow());
    }

    @Override // org.jooq.Row1
    public final Condition isDistinctFrom(T1 t1) {
        return isDistinctFrom((Field) Tools.field(t1, dataType(0)));
    }

    @Override // org.jooq.Row1
    public final Condition isDistinctFrom(Field<T1> t1) {
        return isDistinctFrom((Row1) DSL.row((SelectField) t1));
    }

    @Override // org.jooq.Row1
    public final Condition isDistinctFrom(Select<? extends Record1<T1>> select) {
        return new RowIsDistinctFrom((Row) this, (Select<?>) select, false);
    }

    @Override // org.jooq.Row1
    public final Condition in(Row1<T1>... rows) {
        return in(Arrays.asList(rows));
    }

    @Override // org.jooq.Row1
    public final Condition in(Record1<T1>... records) {
        QueryPartList<Row> rows = new QueryPartList<>();
        for (Record1<T1> record1 : records) {
            rows.add((QueryPartList<Row>) record1.valuesRow());
        }
        return new RowInCondition(this, rows, false);
    }

    @Override // org.jooq.Row1
    public final Condition notIn(Row1<T1>... rows) {
        return notIn(Arrays.asList(rows));
    }

    @Override // org.jooq.Row1
    public final Condition notIn(Record1<T1>... records) {
        QueryPartList<Row> rows = new QueryPartList<>();
        for (Record1<T1> record1 : records) {
            rows.add((QueryPartList<Row>) record1.valuesRow());
        }
        return new RowInCondition(this, rows, true);
    }

    @Override // org.jooq.Row1
    public final Condition in(Collection<? extends Row1<T1>> rows) {
        return new RowInCondition(this, new QueryPartList(rows), false);
    }

    @Override // org.jooq.Row1
    public final Condition in(Result<? extends Record1<T1>> result) {
        return new RowInCondition(this, new QueryPartList(Tools.rows(result)), false);
    }

    @Override // org.jooq.Row1
    public final Condition notIn(Collection<? extends Row1<T1>> rows) {
        return new RowInCondition(this, new QueryPartList(rows), true);
    }

    @Override // org.jooq.Row1
    public final Condition notIn(Result<? extends Record1<T1>> result) {
        return new RowInCondition(this, new QueryPartList(Tools.rows(result)), true);
    }

    @Override // org.jooq.Row1
    public final Condition equal(Select<? extends Record1<T1>> select) {
        return compare(Comparator.EQUALS, (Select) select);
    }

    @Override // org.jooq.Row1
    public final Condition equal(QuantifiedSelect<? extends Record1<T1>> select) {
        return compare(Comparator.EQUALS, (QuantifiedSelect) select);
    }

    @Override // org.jooq.Row1
    public final Condition eq(Select<? extends Record1<T1>> select) {
        return equal((Select) select);
    }

    @Override // org.jooq.Row1
    public final Condition eq(QuantifiedSelect<? extends Record1<T1>> select) {
        return equal((QuantifiedSelect) select);
    }

    @Override // org.jooq.Row1
    public final Condition notEqual(Select<? extends Record1<T1>> select) {
        return compare(Comparator.NOT_EQUALS, (Select) select);
    }

    @Override // org.jooq.Row1
    public final Condition notEqual(QuantifiedSelect<? extends Record1<T1>> select) {
        return compare(Comparator.NOT_EQUALS, (QuantifiedSelect) select);
    }

    @Override // org.jooq.Row1
    public final Condition ne(Select<? extends Record1<T1>> select) {
        return notEqual((Select) select);
    }

    @Override // org.jooq.Row1
    public final Condition ne(QuantifiedSelect<? extends Record1<T1>> select) {
        return notEqual((QuantifiedSelect) select);
    }

    @Override // org.jooq.Row1
    public final Condition greaterThan(Select<? extends Record1<T1>> select) {
        return compare(Comparator.GREATER, (Select) select);
    }

    @Override // org.jooq.Row1
    public final Condition greaterThan(QuantifiedSelect<? extends Record1<T1>> select) {
        return compare(Comparator.GREATER, (QuantifiedSelect) select);
    }

    @Override // org.jooq.Row1
    public final Condition gt(Select<? extends Record1<T1>> select) {
        return greaterThan((Select) select);
    }

    @Override // org.jooq.Row1
    public final Condition gt(QuantifiedSelect<? extends Record1<T1>> select) {
        return greaterThan((QuantifiedSelect) select);
    }

    @Override // org.jooq.Row1
    public final Condition greaterOrEqual(Select<? extends Record1<T1>> select) {
        return compare(Comparator.GREATER_OR_EQUAL, (Select) select);
    }

    @Override // org.jooq.Row1
    public final Condition greaterOrEqual(QuantifiedSelect<? extends Record1<T1>> select) {
        return compare(Comparator.GREATER_OR_EQUAL, (QuantifiedSelect) select);
    }

    @Override // org.jooq.Row1
    public final Condition ge(Select<? extends Record1<T1>> select) {
        return greaterOrEqual((Select) select);
    }

    @Override // org.jooq.Row1
    public final Condition ge(QuantifiedSelect<? extends Record1<T1>> select) {
        return greaterOrEqual((QuantifiedSelect) select);
    }

    @Override // org.jooq.Row1
    public final Condition lessThan(Select<? extends Record1<T1>> select) {
        return compare(Comparator.LESS, (Select) select);
    }

    @Override // org.jooq.Row1
    public final Condition lessThan(QuantifiedSelect<? extends Record1<T1>> select) {
        return compare(Comparator.LESS, (QuantifiedSelect) select);
    }

    @Override // org.jooq.Row1
    public final Condition lt(Select<? extends Record1<T1>> select) {
        return lessThan((Select) select);
    }

    @Override // org.jooq.Row1
    public final Condition lt(QuantifiedSelect<? extends Record1<T1>> select) {
        return lessThan((QuantifiedSelect) select);
    }

    @Override // org.jooq.Row1
    public final Condition lessOrEqual(Select<? extends Record1<T1>> select) {
        return compare(Comparator.LESS_OR_EQUAL, (Select) select);
    }

    @Override // org.jooq.Row1
    public final Condition lessOrEqual(QuantifiedSelect<? extends Record1<T1>> select) {
        return compare(Comparator.LESS_OR_EQUAL, (QuantifiedSelect) select);
    }

    @Override // org.jooq.Row1
    public final Condition le(Select<? extends Record1<T1>> select) {
        return lessOrEqual((Select) select);
    }

    @Override // org.jooq.Row1
    public final Condition le(QuantifiedSelect<? extends Record1<T1>> select) {
        return lessOrEqual((QuantifiedSelect) select);
    }

    @Override // org.jooq.Row1
    public final Condition in(Select<? extends Record1<T1>> select) {
        return compare(Comparator.IN, (Select) select);
    }

    @Override // org.jooq.Row1
    public final Condition notIn(Select<? extends Record1<T1>> select) {
        return compare(Comparator.NOT_IN, (Select) select);
    }
}
