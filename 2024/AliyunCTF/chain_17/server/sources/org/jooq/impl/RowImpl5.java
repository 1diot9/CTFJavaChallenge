package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import org.jooq.BetweenAndStep5;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Function5;
import org.jooq.QuantifiedSelect;
import org.jooq.Record5;
import org.jooq.Result;
import org.jooq.Row;
import org.jooq.Row5;
import org.jooq.Select;
import org.jooq.SelectField;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RowImpl5.class */
public final class RowImpl5<T1, T2, T3, T4, T5> extends AbstractRow<Record5<T1, T2, T3, T4, T5>> implements Row5<T1, T2, T3, T4, T5> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public RowImpl5(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5) {
        super((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5});
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RowImpl5(FieldsImpl<?> fields) {
        super(fields);
    }

    @Override // org.jooq.Row5
    public final <U> SelectField<U> mapping(Function5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? extends U> function) {
        return convertFrom(r -> {
            if (r == null) {
                return null;
            }
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4(), r.value5());
        });
    }

    @Override // org.jooq.Row5
    public final <U> SelectField<U> mapping(Class<U> uType, Function5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? extends U> function) {
        return convertFrom(uType, r -> {
            if (r == null) {
                return null;
            }
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4(), r.value5());
        });
    }

    @Override // org.jooq.Row5
    public final Field<T1> field1() {
        return (Field<T1>) this.fields.field(0);
    }

    @Override // org.jooq.Row5
    public final Field<T2> field2() {
        return (Field<T2>) this.fields.field(1);
    }

    @Override // org.jooq.Row5
    public final Field<T3> field3() {
        return (Field<T3>) this.fields.field(2);
    }

    @Override // org.jooq.Row5
    public final Field<T4> field4() {
        return (Field<T4>) this.fields.field(3);
    }

    @Override // org.jooq.Row5
    public final Field<T5> field5() {
        return (Field<T5>) this.fields.field(4);
    }

    @Override // org.jooq.Row5
    public final Condition compare(Comparator comparator, Row5<T1, T2, T3, T4, T5> row) {
        return compare(this, comparator, row);
    }

    @Override // org.jooq.Row5
    public final Condition compare(Comparator comparator, Record5<T1, T2, T3, T4, T5> record) {
        return compare(this, comparator, record.valuesRow());
    }

    @Override // org.jooq.Row5
    public final Condition compare(Comparator comparator, T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return compare(comparator, (Row5) DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1)), (SelectField) Tools.field(t3, dataType(2)), (SelectField) Tools.field(t4, dataType(3)), (SelectField) Tools.field(t5, dataType(4))));
    }

    @Override // org.jooq.Row5
    public final Condition compare(Comparator comparator, Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5) {
        return compare(comparator, (Row5) DSL.row((SelectField) Tools.nullSafe(t1, dataType(0)), (SelectField) Tools.nullSafe(t2, dataType(1)), (SelectField) Tools.nullSafe(t3, dataType(2)), (SelectField) Tools.nullSafe(t4, dataType(3)), (SelectField) Tools.nullSafe(t5, dataType(4))));
    }

    @Override // org.jooq.Row5
    public final Condition compare(Comparator comparator, Select<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return new RowSubqueryCondition(this, select, comparator);
    }

    @Override // org.jooq.Row5
    public final Condition compare(Comparator comparator, QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return new RowSubqueryCondition(this, select, comparator);
    }

    @Override // org.jooq.Row5
    public final Condition equal(Row5<T1, T2, T3, T4, T5> row) {
        return compare(Comparator.EQUALS, (Row5) row);
    }

    @Override // org.jooq.Row5
    public final Condition equal(Record5<T1, T2, T3, T4, T5> record) {
        return compare(Comparator.EQUALS, record);
    }

    @Override // org.jooq.Row5
    public final Condition equal(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return compare(Comparator.EQUALS, (Comparator) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5);
    }

    @Override // org.jooq.Row5
    public final Condition equal(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5) {
        return compare(Comparator.EQUALS, (Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5);
    }

    @Override // org.jooq.Row5
    public final Condition eq(Row5<T1, T2, T3, T4, T5> row) {
        return equal(row);
    }

    @Override // org.jooq.Row5
    public final Condition eq(Record5<T1, T2, T3, T4, T5> record) {
        return equal(record);
    }

    @Override // org.jooq.Row5
    public final Condition eq(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return equal((RowImpl5<T1, T2, T3, T4, T5>) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5);
    }

    @Override // org.jooq.Row5
    public final Condition eq(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5) {
        return equal((Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5);
    }

    @Override // org.jooq.Row5
    public final Condition notEqual(Row5<T1, T2, T3, T4, T5> row) {
        return compare(Comparator.NOT_EQUALS, (Row5) row);
    }

    @Override // org.jooq.Row5
    public final Condition notEqual(Record5<T1, T2, T3, T4, T5> record) {
        return compare(Comparator.NOT_EQUALS, record);
    }

    @Override // org.jooq.Row5
    public final Condition notEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return compare(Comparator.NOT_EQUALS, (Comparator) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5);
    }

    @Override // org.jooq.Row5
    public final Condition notEqual(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5) {
        return compare(Comparator.NOT_EQUALS, (Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5);
    }

    @Override // org.jooq.Row5
    public final Condition ne(Row5<T1, T2, T3, T4, T5> row) {
        return notEqual(row);
    }

    @Override // org.jooq.Row5
    public final Condition ne(Record5<T1, T2, T3, T4, T5> record) {
        return notEqual(record);
    }

    @Override // org.jooq.Row5
    public final Condition ne(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return notEqual((RowImpl5<T1, T2, T3, T4, T5>) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5);
    }

    @Override // org.jooq.Row5
    public final Condition ne(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5) {
        return notEqual((Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5);
    }

    @Override // org.jooq.Row5
    public final Condition lessThan(Row5<T1, T2, T3, T4, T5> row) {
        return compare(Comparator.LESS, (Row5) row);
    }

    @Override // org.jooq.Row5
    public final Condition lessThan(Record5<T1, T2, T3, T4, T5> record) {
        return compare(Comparator.LESS, record);
    }

    @Override // org.jooq.Row5
    public final Condition lessThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return compare(Comparator.LESS, (Comparator) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5);
    }

    @Override // org.jooq.Row5
    public final Condition lessThan(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5) {
        return compare(Comparator.LESS, (Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5);
    }

    @Override // org.jooq.Row5
    public final Condition lt(Row5<T1, T2, T3, T4, T5> row) {
        return lessThan(row);
    }

    @Override // org.jooq.Row5
    public final Condition lt(Record5<T1, T2, T3, T4, T5> record) {
        return lessThan(record);
    }

    @Override // org.jooq.Row5
    public final Condition lt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return lessThan((RowImpl5<T1, T2, T3, T4, T5>) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5);
    }

    @Override // org.jooq.Row5
    public final Condition lt(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5) {
        return lessThan((Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5);
    }

    @Override // org.jooq.Row5
    public final Condition lessOrEqual(Row5<T1, T2, T3, T4, T5> row) {
        return compare(Comparator.LESS_OR_EQUAL, (Row5) row);
    }

    @Override // org.jooq.Row5
    public final Condition lessOrEqual(Record5<T1, T2, T3, T4, T5> record) {
        return compare(Comparator.LESS_OR_EQUAL, record);
    }

    @Override // org.jooq.Row5
    public final Condition lessOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return compare(Comparator.LESS_OR_EQUAL, (Comparator) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5);
    }

    @Override // org.jooq.Row5
    public final Condition lessOrEqual(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5) {
        return compare(Comparator.LESS_OR_EQUAL, (Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5);
    }

    @Override // org.jooq.Row5
    public final Condition le(Row5<T1, T2, T3, T4, T5> row) {
        return lessOrEqual(row);
    }

    @Override // org.jooq.Row5
    public final Condition le(Record5<T1, T2, T3, T4, T5> record) {
        return lessOrEqual(record);
    }

    @Override // org.jooq.Row5
    public final Condition le(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return lessOrEqual((RowImpl5<T1, T2, T3, T4, T5>) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5);
    }

    @Override // org.jooq.Row5
    public final Condition le(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5) {
        return lessOrEqual((Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5);
    }

    @Override // org.jooq.Row5
    public final Condition greaterThan(Row5<T1, T2, T3, T4, T5> row) {
        return compare(Comparator.GREATER, (Row5) row);
    }

    @Override // org.jooq.Row5
    public final Condition greaterThan(Record5<T1, T2, T3, T4, T5> record) {
        return compare(Comparator.GREATER, record);
    }

    @Override // org.jooq.Row5
    public final Condition greaterThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return compare(Comparator.GREATER, (Comparator) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5);
    }

    @Override // org.jooq.Row5
    public final Condition greaterThan(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5) {
        return compare(Comparator.GREATER, (Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5);
    }

    @Override // org.jooq.Row5
    public final Condition gt(Row5<T1, T2, T3, T4, T5> row) {
        return greaterThan(row);
    }

    @Override // org.jooq.Row5
    public final Condition gt(Record5<T1, T2, T3, T4, T5> record) {
        return greaterThan(record);
    }

    @Override // org.jooq.Row5
    public final Condition gt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return greaterThan((RowImpl5<T1, T2, T3, T4, T5>) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5);
    }

    @Override // org.jooq.Row5
    public final Condition gt(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5) {
        return greaterThan((Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5);
    }

    @Override // org.jooq.Row5
    public final Condition greaterOrEqual(Row5<T1, T2, T3, T4, T5> row) {
        return compare(Comparator.GREATER_OR_EQUAL, (Row5) row);
    }

    @Override // org.jooq.Row5
    public final Condition greaterOrEqual(Record5<T1, T2, T3, T4, T5> record) {
        return compare(Comparator.GREATER_OR_EQUAL, record);
    }

    @Override // org.jooq.Row5
    public final Condition greaterOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return compare(Comparator.GREATER_OR_EQUAL, (Comparator) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5);
    }

    @Override // org.jooq.Row5
    public final Condition greaterOrEqual(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5) {
        return compare(Comparator.GREATER_OR_EQUAL, (Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5);
    }

    @Override // org.jooq.Row5
    public final Condition ge(Row5<T1, T2, T3, T4, T5> row) {
        return greaterOrEqual(row);
    }

    @Override // org.jooq.Row5
    public final Condition ge(Record5<T1, T2, T3, T4, T5> record) {
        return greaterOrEqual(record);
    }

    @Override // org.jooq.Row5
    public final Condition ge(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return greaterOrEqual((RowImpl5<T1, T2, T3, T4, T5>) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5);
    }

    @Override // org.jooq.Row5
    public final Condition ge(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5) {
        return greaterOrEqual((Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5);
    }

    @Override // org.jooq.Row5
    public final BetweenAndStep5<T1, T2, T3, T4, T5> between(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return between(DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1)), (SelectField) Tools.field(t3, dataType(2)), (SelectField) Tools.field(t4, dataType(3)), (SelectField) Tools.field(t5, dataType(4))));
    }

    @Override // org.jooq.Row5
    public final BetweenAndStep5<T1, T2, T3, T4, T5> between(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5) {
        return between(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5));
    }

    @Override // org.jooq.Row5
    public final BetweenAndStep5<T1, T2, T3, T4, T5> between(Row5<T1, T2, T3, T4, T5> row) {
        return new RowBetweenCondition(this, row, false, false);
    }

    @Override // org.jooq.Row5
    public final BetweenAndStep5<T1, T2, T3, T4, T5> between(Record5<T1, T2, T3, T4, T5> record) {
        return between(record.valuesRow());
    }

    @Override // org.jooq.Row5
    public final Condition between(Row5<T1, T2, T3, T4, T5> minValue, Row5<T1, T2, T3, T4, T5> maxValue) {
        return between(minValue).and(maxValue);
    }

    @Override // org.jooq.Row5
    public final Condition between(Record5<T1, T2, T3, T4, T5> minValue, Record5<T1, T2, T3, T4, T5> maxValue) {
        return between(minValue).and(maxValue);
    }

    @Override // org.jooq.Row5
    public final BetweenAndStep5<T1, T2, T3, T4, T5> betweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return betweenSymmetric(DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1)), (SelectField) Tools.field(t3, dataType(2)), (SelectField) Tools.field(t4, dataType(3)), (SelectField) Tools.field(t5, dataType(4))));
    }

    @Override // org.jooq.Row5
    public final BetweenAndStep5<T1, T2, T3, T4, T5> betweenSymmetric(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5) {
        return betweenSymmetric(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5));
    }

    @Override // org.jooq.Row5
    public final BetweenAndStep5<T1, T2, T3, T4, T5> betweenSymmetric(Row5<T1, T2, T3, T4, T5> row) {
        return new RowBetweenCondition(this, row, false, true);
    }

    @Override // org.jooq.Row5
    public final BetweenAndStep5<T1, T2, T3, T4, T5> betweenSymmetric(Record5<T1, T2, T3, T4, T5> record) {
        return betweenSymmetric(record.valuesRow());
    }

    @Override // org.jooq.Row5
    public final Condition betweenSymmetric(Row5<T1, T2, T3, T4, T5> minValue, Row5<T1, T2, T3, T4, T5> maxValue) {
        return betweenSymmetric(minValue).and(maxValue);
    }

    @Override // org.jooq.Row5
    public final Condition betweenSymmetric(Record5<T1, T2, T3, T4, T5> minValue, Record5<T1, T2, T3, T4, T5> maxValue) {
        return betweenSymmetric(minValue).and(maxValue);
    }

    @Override // org.jooq.Row5
    public final BetweenAndStep5<T1, T2, T3, T4, T5> notBetween(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return notBetween(DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1)), (SelectField) Tools.field(t3, dataType(2)), (SelectField) Tools.field(t4, dataType(3)), (SelectField) Tools.field(t5, dataType(4))));
    }

    @Override // org.jooq.Row5
    public final BetweenAndStep5<T1, T2, T3, T4, T5> notBetween(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5) {
        return notBetween(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5));
    }

    @Override // org.jooq.Row5
    public final BetweenAndStep5<T1, T2, T3, T4, T5> notBetween(Row5<T1, T2, T3, T4, T5> row) {
        return new RowBetweenCondition(this, row, true, false);
    }

    @Override // org.jooq.Row5
    public final BetweenAndStep5<T1, T2, T3, T4, T5> notBetween(Record5<T1, T2, T3, T4, T5> record) {
        return notBetween(record.valuesRow());
    }

    @Override // org.jooq.Row5
    public final Condition notBetween(Row5<T1, T2, T3, T4, T5> minValue, Row5<T1, T2, T3, T4, T5> maxValue) {
        return notBetween(minValue).and(maxValue);
    }

    @Override // org.jooq.Row5
    public final Condition notBetween(Record5<T1, T2, T3, T4, T5> minValue, Record5<T1, T2, T3, T4, T5> maxValue) {
        return notBetween(minValue).and(maxValue);
    }

    @Override // org.jooq.Row5
    public final BetweenAndStep5<T1, T2, T3, T4, T5> notBetweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return notBetweenSymmetric(DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1)), (SelectField) Tools.field(t3, dataType(2)), (SelectField) Tools.field(t4, dataType(3)), (SelectField) Tools.field(t5, dataType(4))));
    }

    @Override // org.jooq.Row5
    public final BetweenAndStep5<T1, T2, T3, T4, T5> notBetweenSymmetric(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5) {
        return notBetweenSymmetric(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5));
    }

    @Override // org.jooq.Row5
    public final BetweenAndStep5<T1, T2, T3, T4, T5> notBetweenSymmetric(Row5<T1, T2, T3, T4, T5> row) {
        return new RowBetweenCondition(this, row, true, true);
    }

    @Override // org.jooq.Row5
    public final BetweenAndStep5<T1, T2, T3, T4, T5> notBetweenSymmetric(Record5<T1, T2, T3, T4, T5> record) {
        return notBetweenSymmetric(record.valuesRow());
    }

    @Override // org.jooq.Row5
    public final Condition notBetweenSymmetric(Row5<T1, T2, T3, T4, T5> minValue, Row5<T1, T2, T3, T4, T5> maxValue) {
        return notBetweenSymmetric(minValue).and(maxValue);
    }

    @Override // org.jooq.Row5
    public final Condition notBetweenSymmetric(Record5<T1, T2, T3, T4, T5> minValue, Record5<T1, T2, T3, T4, T5> maxValue) {
        return notBetweenSymmetric(minValue).and(maxValue);
    }

    @Override // org.jooq.Row5
    public final Condition isNotDistinctFrom(Row5<T1, T2, T3, T4, T5> row) {
        return new RowIsDistinctFrom((Row) this, (Row) row, true);
    }

    @Override // org.jooq.Row5
    public final Condition isNotDistinctFrom(Record5<T1, T2, T3, T4, T5> record) {
        return isNotDistinctFrom(record.valuesRow());
    }

    @Override // org.jooq.Row5
    public final Condition isNotDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return isNotDistinctFrom((Field) Tools.field(t1, dataType(0)), (Field) Tools.field(t2, dataType(1)), (Field) Tools.field(t3, dataType(2)), (Field) Tools.field(t4, dataType(3)), (Field) Tools.field(t5, dataType(4)));
    }

    @Override // org.jooq.Row5
    public final Condition isNotDistinctFrom(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5) {
        return isNotDistinctFrom(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5));
    }

    @Override // org.jooq.Row5
    public final Condition isNotDistinctFrom(Select<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return new RowIsDistinctFrom((Row) this, (Select<?>) select, true);
    }

    @Override // org.jooq.Row5
    public final Condition isDistinctFrom(Row5<T1, T2, T3, T4, T5> row) {
        return new RowIsDistinctFrom((Row) this, (Row) row, false);
    }

    @Override // org.jooq.Row5
    public final Condition isDistinctFrom(Record5<T1, T2, T3, T4, T5> record) {
        return isDistinctFrom(record.valuesRow());
    }

    @Override // org.jooq.Row5
    public final Condition isDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return isDistinctFrom((Field) Tools.field(t1, dataType(0)), (Field) Tools.field(t2, dataType(1)), (Field) Tools.field(t3, dataType(2)), (Field) Tools.field(t4, dataType(3)), (Field) Tools.field(t5, dataType(4)));
    }

    @Override // org.jooq.Row5
    public final Condition isDistinctFrom(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5) {
        return isDistinctFrom(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5));
    }

    @Override // org.jooq.Row5
    public final Condition isDistinctFrom(Select<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return new RowIsDistinctFrom((Row) this, (Select<?>) select, false);
    }

    @Override // org.jooq.Row5
    public final Condition in(Row5<T1, T2, T3, T4, T5>... rows) {
        return in(Arrays.asList(rows));
    }

    @Override // org.jooq.Row5
    public final Condition in(Record5<T1, T2, T3, T4, T5>... records) {
        QueryPartList<Row> rows = new QueryPartList<>();
        for (Record5<T1, T2, T3, T4, T5> record5 : records) {
            rows.add((QueryPartList<Row>) record5.valuesRow());
        }
        return new RowInCondition(this, rows, false);
    }

    @Override // org.jooq.Row5
    public final Condition notIn(Row5<T1, T2, T3, T4, T5>... rows) {
        return notIn(Arrays.asList(rows));
    }

    @Override // org.jooq.Row5
    public final Condition notIn(Record5<T1, T2, T3, T4, T5>... records) {
        QueryPartList<Row> rows = new QueryPartList<>();
        for (Record5<T1, T2, T3, T4, T5> record5 : records) {
            rows.add((QueryPartList<Row>) record5.valuesRow());
        }
        return new RowInCondition(this, rows, true);
    }

    @Override // org.jooq.Row5
    public final Condition in(Collection<? extends Row5<T1, T2, T3, T4, T5>> rows) {
        return new RowInCondition(this, new QueryPartList(rows), false);
    }

    @Override // org.jooq.Row5
    public final Condition in(Result<? extends Record5<T1, T2, T3, T4, T5>> result) {
        return new RowInCondition(this, new QueryPartList(Tools.rows(result)), false);
    }

    @Override // org.jooq.Row5
    public final Condition notIn(Collection<? extends Row5<T1, T2, T3, T4, T5>> rows) {
        return new RowInCondition(this, new QueryPartList(rows), true);
    }

    @Override // org.jooq.Row5
    public final Condition notIn(Result<? extends Record5<T1, T2, T3, T4, T5>> result) {
        return new RowInCondition(this, new QueryPartList(Tools.rows(result)), true);
    }

    @Override // org.jooq.Row5
    public final Condition equal(Select<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return compare(Comparator.EQUALS, select);
    }

    @Override // org.jooq.Row5
    public final Condition equal(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return compare(Comparator.EQUALS, select);
    }

    @Override // org.jooq.Row5
    public final Condition eq(Select<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return equal(select);
    }

    @Override // org.jooq.Row5
    public final Condition eq(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return equal(select);
    }

    @Override // org.jooq.Row5
    public final Condition notEqual(Select<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return compare(Comparator.NOT_EQUALS, select);
    }

    @Override // org.jooq.Row5
    public final Condition notEqual(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return compare(Comparator.NOT_EQUALS, select);
    }

    @Override // org.jooq.Row5
    public final Condition ne(Select<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return notEqual(select);
    }

    @Override // org.jooq.Row5
    public final Condition ne(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return notEqual(select);
    }

    @Override // org.jooq.Row5
    public final Condition greaterThan(Select<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return compare(Comparator.GREATER, select);
    }

    @Override // org.jooq.Row5
    public final Condition greaterThan(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return compare(Comparator.GREATER, select);
    }

    @Override // org.jooq.Row5
    public final Condition gt(Select<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return greaterThan(select);
    }

    @Override // org.jooq.Row5
    public final Condition gt(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return greaterThan(select);
    }

    @Override // org.jooq.Row5
    public final Condition greaterOrEqual(Select<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return compare(Comparator.GREATER_OR_EQUAL, select);
    }

    @Override // org.jooq.Row5
    public final Condition greaterOrEqual(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return compare(Comparator.GREATER_OR_EQUAL, select);
    }

    @Override // org.jooq.Row5
    public final Condition ge(Select<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return greaterOrEqual(select);
    }

    @Override // org.jooq.Row5
    public final Condition ge(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return greaterOrEqual(select);
    }

    @Override // org.jooq.Row5
    public final Condition lessThan(Select<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return compare(Comparator.LESS, select);
    }

    @Override // org.jooq.Row5
    public final Condition lessThan(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return compare(Comparator.LESS, select);
    }

    @Override // org.jooq.Row5
    public final Condition lt(Select<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return lessThan(select);
    }

    @Override // org.jooq.Row5
    public final Condition lt(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return lessThan(select);
    }

    @Override // org.jooq.Row5
    public final Condition lessOrEqual(Select<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return compare(Comparator.LESS_OR_EQUAL, select);
    }

    @Override // org.jooq.Row5
    public final Condition lessOrEqual(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return compare(Comparator.LESS_OR_EQUAL, select);
    }

    @Override // org.jooq.Row5
    public final Condition le(Select<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return lessOrEqual(select);
    }

    @Override // org.jooq.Row5
    public final Condition le(QuantifiedSelect<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return lessOrEqual(select);
    }

    @Override // org.jooq.Row5
    public final Condition in(Select<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return compare(Comparator.IN, select);
    }

    @Override // org.jooq.Row5
    public final Condition notIn(Select<? extends Record5<T1, T2, T3, T4, T5>> select) {
        return compare(Comparator.NOT_IN, select);
    }
}
