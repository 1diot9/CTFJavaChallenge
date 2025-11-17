package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import org.jooq.BetweenAndStep11;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Function11;
import org.jooq.QuantifiedSelect;
import org.jooq.Record11;
import org.jooq.Result;
import org.jooq.Row;
import org.jooq.Row11;
import org.jooq.Select;
import org.jooq.SelectField;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RowImpl11.class */
public final class RowImpl11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> extends AbstractRow<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> implements Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public RowImpl11(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11) {
        super((SelectField<?>[]) new SelectField[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11});
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RowImpl11(FieldsImpl<?> fields) {
        super(fields);
    }

    @Override // org.jooq.Row11
    public final <U> SelectField<U> mapping(Function11<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? super T10, ? super T11, ? extends U> function) {
        return convertFrom(r -> {
            if (r == null) {
                return null;
            }
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4(), r.value5(), r.value6(), r.value7(), r.value8(), r.value9(), r.value10(), r.value11());
        });
    }

    @Override // org.jooq.Row11
    public final <U> SelectField<U> mapping(Class<U> uType, Function11<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? super T10, ? super T11, ? extends U> function) {
        return convertFrom(uType, r -> {
            if (r == null) {
                return null;
            }
            return function.apply(r.value1(), r.value2(), r.value3(), r.value4(), r.value5(), r.value6(), r.value7(), r.value8(), r.value9(), r.value10(), r.value11());
        });
    }

    @Override // org.jooq.Row11
    public final Field<T1> field1() {
        return (Field<T1>) this.fields.field(0);
    }

    @Override // org.jooq.Row11
    public final Field<T2> field2() {
        return (Field<T2>) this.fields.field(1);
    }

    @Override // org.jooq.Row11
    public final Field<T3> field3() {
        return (Field<T3>) this.fields.field(2);
    }

    @Override // org.jooq.Row11
    public final Field<T4> field4() {
        return (Field<T4>) this.fields.field(3);
    }

    @Override // org.jooq.Row11
    public final Field<T5> field5() {
        return (Field<T5>) this.fields.field(4);
    }

    @Override // org.jooq.Row11
    public final Field<T6> field6() {
        return (Field<T6>) this.fields.field(5);
    }

    @Override // org.jooq.Row11
    public final Field<T7> field7() {
        return (Field<T7>) this.fields.field(6);
    }

    @Override // org.jooq.Row11
    public final Field<T8> field8() {
        return (Field<T8>) this.fields.field(7);
    }

    @Override // org.jooq.Row11
    public final Field<T9> field9() {
        return (Field<T9>) this.fields.field(8);
    }

    @Override // org.jooq.Row11
    public final Field<T10> field10() {
        return (Field<T10>) this.fields.field(9);
    }

    @Override // org.jooq.Row11
    public final Field<T11> field11() {
        return (Field<T11>) this.fields.field(10);
    }

    @Override // org.jooq.Row11
    public final Condition compare(Comparator comparator, Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return compare(this, comparator, row);
    }

    @Override // org.jooq.Row11
    public final Condition compare(Comparator comparator, Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return compare(this, comparator, record.valuesRow());
    }

    @Override // org.jooq.Row11
    public final Condition compare(Comparator comparator, T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return compare(comparator, (Row11) DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1)), (SelectField) Tools.field(t3, dataType(2)), (SelectField) Tools.field(t4, dataType(3)), (SelectField) Tools.field(t5, dataType(4)), (SelectField) Tools.field(t6, dataType(5)), (SelectField) Tools.field(t7, dataType(6)), (SelectField) Tools.field(t8, dataType(7)), (SelectField) Tools.field(t9, dataType(8)), (SelectField) Tools.field(t10, dataType(9)), (SelectField) Tools.field(t11, dataType(10))));
    }

    @Override // org.jooq.Row11
    public final Condition compare(Comparator comparator, Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return compare(comparator, (Row11) DSL.row((SelectField) Tools.nullSafe(t1, dataType(0)), (SelectField) Tools.nullSafe(t2, dataType(1)), (SelectField) Tools.nullSafe(t3, dataType(2)), (SelectField) Tools.nullSafe(t4, dataType(3)), (SelectField) Tools.nullSafe(t5, dataType(4)), (SelectField) Tools.nullSafe(t6, dataType(5)), (SelectField) Tools.nullSafe(t7, dataType(6)), (SelectField) Tools.nullSafe(t8, dataType(7)), (SelectField) Tools.nullSafe(t9, dataType(8)), (SelectField) Tools.nullSafe(t10, dataType(9)), (SelectField) Tools.nullSafe(t11, dataType(10))));
    }

    @Override // org.jooq.Row11
    public final Condition compare(Comparator comparator, Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return new RowSubqueryCondition(this, select, comparator);
    }

    @Override // org.jooq.Row11
    public final Condition compare(Comparator comparator, QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return new RowSubqueryCondition(this, select, comparator);
    }

    @Override // org.jooq.Row11
    public final Condition equal(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return compare(Comparator.EQUALS, (Row11) row);
    }

    @Override // org.jooq.Row11
    public final Condition equal(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return compare(Comparator.EQUALS, record);
    }

    @Override // org.jooq.Row11
    public final Condition equal(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return compare(Comparator.EQUALS, (Comparator) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5, (T5) t6, (T6) t7, (T7) t8, (T8) t9, (T9) t10, (T10) t11);
    }

    @Override // org.jooq.Row11
    public final Condition equal(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return compare(Comparator.EQUALS, (Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5, (Field) t6, (Field) t7, (Field) t8, (Field) t9, (Field) t10, (Field) t11);
    }

    @Override // org.jooq.Row11
    public final Condition eq(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return equal(row);
    }

    @Override // org.jooq.Row11
    public final Condition eq(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return equal(record);
    }

    @Override // org.jooq.Row11
    public final Condition eq(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return equal((RowImpl11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5, (T5) t6, (T6) t7, (T7) t8, (T8) t9, (T9) t10, (T10) t11);
    }

    @Override // org.jooq.Row11
    public final Condition eq(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return equal((Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5, (Field) t6, (Field) t7, (Field) t8, (Field) t9, (Field) t10, (Field) t11);
    }

    @Override // org.jooq.Row11
    public final Condition notEqual(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return compare(Comparator.NOT_EQUALS, (Row11) row);
    }

    @Override // org.jooq.Row11
    public final Condition notEqual(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return compare(Comparator.NOT_EQUALS, record);
    }

    @Override // org.jooq.Row11
    public final Condition notEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return compare(Comparator.NOT_EQUALS, (Comparator) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5, (T5) t6, (T6) t7, (T7) t8, (T8) t9, (T9) t10, (T10) t11);
    }

    @Override // org.jooq.Row11
    public final Condition notEqual(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return compare(Comparator.NOT_EQUALS, (Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5, (Field) t6, (Field) t7, (Field) t8, (Field) t9, (Field) t10, (Field) t11);
    }

    @Override // org.jooq.Row11
    public final Condition ne(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return notEqual(row);
    }

    @Override // org.jooq.Row11
    public final Condition ne(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return notEqual(record);
    }

    @Override // org.jooq.Row11
    public final Condition ne(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return notEqual((RowImpl11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5, (T5) t6, (T6) t7, (T7) t8, (T8) t9, (T9) t10, (T10) t11);
    }

    @Override // org.jooq.Row11
    public final Condition ne(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return notEqual((Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5, (Field) t6, (Field) t7, (Field) t8, (Field) t9, (Field) t10, (Field) t11);
    }

    @Override // org.jooq.Row11
    public final Condition lessThan(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return compare(Comparator.LESS, (Row11) row);
    }

    @Override // org.jooq.Row11
    public final Condition lessThan(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return compare(Comparator.LESS, record);
    }

    @Override // org.jooq.Row11
    public final Condition lessThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return compare(Comparator.LESS, (Comparator) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5, (T5) t6, (T6) t7, (T7) t8, (T8) t9, (T9) t10, (T10) t11);
    }

    @Override // org.jooq.Row11
    public final Condition lessThan(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return compare(Comparator.LESS, (Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5, (Field) t6, (Field) t7, (Field) t8, (Field) t9, (Field) t10, (Field) t11);
    }

    @Override // org.jooq.Row11
    public final Condition lt(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return lessThan(row);
    }

    @Override // org.jooq.Row11
    public final Condition lt(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return lessThan(record);
    }

    @Override // org.jooq.Row11
    public final Condition lt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return lessThan((RowImpl11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5, (T5) t6, (T6) t7, (T7) t8, (T8) t9, (T9) t10, (T10) t11);
    }

    @Override // org.jooq.Row11
    public final Condition lt(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return lessThan((Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5, (Field) t6, (Field) t7, (Field) t8, (Field) t9, (Field) t10, (Field) t11);
    }

    @Override // org.jooq.Row11
    public final Condition lessOrEqual(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return compare(Comparator.LESS_OR_EQUAL, (Row11) row);
    }

    @Override // org.jooq.Row11
    public final Condition lessOrEqual(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return compare(Comparator.LESS_OR_EQUAL, record);
    }

    @Override // org.jooq.Row11
    public final Condition lessOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return compare(Comparator.LESS_OR_EQUAL, (Comparator) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5, (T5) t6, (T6) t7, (T7) t8, (T8) t9, (T9) t10, (T10) t11);
    }

    @Override // org.jooq.Row11
    public final Condition lessOrEqual(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return compare(Comparator.LESS_OR_EQUAL, (Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5, (Field) t6, (Field) t7, (Field) t8, (Field) t9, (Field) t10, (Field) t11);
    }

    @Override // org.jooq.Row11
    public final Condition le(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return lessOrEqual(row);
    }

    @Override // org.jooq.Row11
    public final Condition le(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return lessOrEqual(record);
    }

    @Override // org.jooq.Row11
    public final Condition le(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return lessOrEqual((RowImpl11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5, (T5) t6, (T6) t7, (T7) t8, (T8) t9, (T9) t10, (T10) t11);
    }

    @Override // org.jooq.Row11
    public final Condition le(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return lessOrEqual((Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5, (Field) t6, (Field) t7, (Field) t8, (Field) t9, (Field) t10, (Field) t11);
    }

    @Override // org.jooq.Row11
    public final Condition greaterThan(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return compare(Comparator.GREATER, (Row11) row);
    }

    @Override // org.jooq.Row11
    public final Condition greaterThan(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return compare(Comparator.GREATER, record);
    }

    @Override // org.jooq.Row11
    public final Condition greaterThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return compare(Comparator.GREATER, (Comparator) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5, (T5) t6, (T6) t7, (T7) t8, (T8) t9, (T9) t10, (T10) t11);
    }

    @Override // org.jooq.Row11
    public final Condition greaterThan(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return compare(Comparator.GREATER, (Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5, (Field) t6, (Field) t7, (Field) t8, (Field) t9, (Field) t10, (Field) t11);
    }

    @Override // org.jooq.Row11
    public final Condition gt(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return greaterThan(row);
    }

    @Override // org.jooq.Row11
    public final Condition gt(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return greaterThan(record);
    }

    @Override // org.jooq.Row11
    public final Condition gt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return greaterThan((RowImpl11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5, (T5) t6, (T6) t7, (T7) t8, (T8) t9, (T9) t10, (T10) t11);
    }

    @Override // org.jooq.Row11
    public final Condition gt(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return greaterThan((Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5, (Field) t6, (Field) t7, (Field) t8, (Field) t9, (Field) t10, (Field) t11);
    }

    @Override // org.jooq.Row11
    public final Condition greaterOrEqual(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return compare(Comparator.GREATER_OR_EQUAL, (Row11) row);
    }

    @Override // org.jooq.Row11
    public final Condition greaterOrEqual(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return compare(Comparator.GREATER_OR_EQUAL, record);
    }

    @Override // org.jooq.Row11
    public final Condition greaterOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return compare(Comparator.GREATER_OR_EQUAL, (Comparator) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5, (T5) t6, (T6) t7, (T7) t8, (T8) t9, (T9) t10, (T10) t11);
    }

    @Override // org.jooq.Row11
    public final Condition greaterOrEqual(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return compare(Comparator.GREATER_OR_EQUAL, (Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5, (Field) t6, (Field) t7, (Field) t8, (Field) t9, (Field) t10, (Field) t11);
    }

    @Override // org.jooq.Row11
    public final Condition ge(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return greaterOrEqual(row);
    }

    @Override // org.jooq.Row11
    public final Condition ge(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return greaterOrEqual(record);
    }

    @Override // org.jooq.Row11
    public final Condition ge(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return greaterOrEqual((RowImpl11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>) t1, (T1) t2, (T2) t3, (T3) t4, (T4) t5, (T5) t6, (T6) t7, (T7) t8, (T8) t9, (T9) t10, (T10) t11);
    }

    @Override // org.jooq.Row11
    public final Condition ge(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return greaterOrEqual((Field) t1, (Field) t2, (Field) t3, (Field) t4, (Field) t5, (Field) t6, (Field) t7, (Field) t8, (Field) t9, (Field) t10, (Field) t11);
    }

    @Override // org.jooq.Row11
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> between(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return between(DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1)), (SelectField) Tools.field(t3, dataType(2)), (SelectField) Tools.field(t4, dataType(3)), (SelectField) Tools.field(t5, dataType(4)), (SelectField) Tools.field(t6, dataType(5)), (SelectField) Tools.field(t7, dataType(6)), (SelectField) Tools.field(t8, dataType(7)), (SelectField) Tools.field(t9, dataType(8)), (SelectField) Tools.field(t10, dataType(9)), (SelectField) Tools.field(t11, dataType(10))));
    }

    @Override // org.jooq.Row11
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> between(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return between(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5, (SelectField) t6, (SelectField) t7, (SelectField) t8, (SelectField) t9, (SelectField) t10, (SelectField) t11));
    }

    @Override // org.jooq.Row11
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> between(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return new RowBetweenCondition(this, row, false, false);
    }

    @Override // org.jooq.Row11
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> between(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return between(record.valuesRow());
    }

    @Override // org.jooq.Row11
    public final Condition between(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> minValue, Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> maxValue) {
        return between(minValue).and(maxValue);
    }

    @Override // org.jooq.Row11
    public final Condition between(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> minValue, Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> maxValue) {
        return between(minValue).and(maxValue);
    }

    @Override // org.jooq.Row11
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> betweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return betweenSymmetric(DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1)), (SelectField) Tools.field(t3, dataType(2)), (SelectField) Tools.field(t4, dataType(3)), (SelectField) Tools.field(t5, dataType(4)), (SelectField) Tools.field(t6, dataType(5)), (SelectField) Tools.field(t7, dataType(6)), (SelectField) Tools.field(t8, dataType(7)), (SelectField) Tools.field(t9, dataType(8)), (SelectField) Tools.field(t10, dataType(9)), (SelectField) Tools.field(t11, dataType(10))));
    }

    @Override // org.jooq.Row11
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> betweenSymmetric(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return betweenSymmetric(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5, (SelectField) t6, (SelectField) t7, (SelectField) t8, (SelectField) t9, (SelectField) t10, (SelectField) t11));
    }

    @Override // org.jooq.Row11
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> betweenSymmetric(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return new RowBetweenCondition(this, row, false, true);
    }

    @Override // org.jooq.Row11
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> betweenSymmetric(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return betweenSymmetric(record.valuesRow());
    }

    @Override // org.jooq.Row11
    public final Condition betweenSymmetric(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> minValue, Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> maxValue) {
        return betweenSymmetric(minValue).and(maxValue);
    }

    @Override // org.jooq.Row11
    public final Condition betweenSymmetric(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> minValue, Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> maxValue) {
        return betweenSymmetric(minValue).and(maxValue);
    }

    @Override // org.jooq.Row11
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetween(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return notBetween(DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1)), (SelectField) Tools.field(t3, dataType(2)), (SelectField) Tools.field(t4, dataType(3)), (SelectField) Tools.field(t5, dataType(4)), (SelectField) Tools.field(t6, dataType(5)), (SelectField) Tools.field(t7, dataType(6)), (SelectField) Tools.field(t8, dataType(7)), (SelectField) Tools.field(t9, dataType(8)), (SelectField) Tools.field(t10, dataType(9)), (SelectField) Tools.field(t11, dataType(10))));
    }

    @Override // org.jooq.Row11
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetween(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return notBetween(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5, (SelectField) t6, (SelectField) t7, (SelectField) t8, (SelectField) t9, (SelectField) t10, (SelectField) t11));
    }

    @Override // org.jooq.Row11
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetween(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return new RowBetweenCondition(this, row, true, false);
    }

    @Override // org.jooq.Row11
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetween(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return notBetween(record.valuesRow());
    }

    @Override // org.jooq.Row11
    public final Condition notBetween(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> minValue, Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> maxValue) {
        return notBetween(minValue).and(maxValue);
    }

    @Override // org.jooq.Row11
    public final Condition notBetween(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> minValue, Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> maxValue) {
        return notBetween(minValue).and(maxValue);
    }

    @Override // org.jooq.Row11
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return notBetweenSymmetric(DSL.row((SelectField) Tools.field(t1, dataType(0)), (SelectField) Tools.field(t2, dataType(1)), (SelectField) Tools.field(t3, dataType(2)), (SelectField) Tools.field(t4, dataType(3)), (SelectField) Tools.field(t5, dataType(4)), (SelectField) Tools.field(t6, dataType(5)), (SelectField) Tools.field(t7, dataType(6)), (SelectField) Tools.field(t8, dataType(7)), (SelectField) Tools.field(t9, dataType(8)), (SelectField) Tools.field(t10, dataType(9)), (SelectField) Tools.field(t11, dataType(10))));
    }

    @Override // org.jooq.Row11
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetweenSymmetric(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return notBetweenSymmetric(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5, (SelectField) t6, (SelectField) t7, (SelectField) t8, (SelectField) t9, (SelectField) t10, (SelectField) t11));
    }

    @Override // org.jooq.Row11
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetweenSymmetric(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return new RowBetweenCondition(this, row, true, true);
    }

    @Override // org.jooq.Row11
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetweenSymmetric(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return notBetweenSymmetric(record.valuesRow());
    }

    @Override // org.jooq.Row11
    public final Condition notBetweenSymmetric(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> minValue, Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> maxValue) {
        return notBetweenSymmetric(minValue).and(maxValue);
    }

    @Override // org.jooq.Row11
    public final Condition notBetweenSymmetric(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> minValue, Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> maxValue) {
        return notBetweenSymmetric(minValue).and(maxValue);
    }

    @Override // org.jooq.Row11
    public final Condition isNotDistinctFrom(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return new RowIsDistinctFrom((Row) this, (Row) row, true);
    }

    @Override // org.jooq.Row11
    public final Condition isNotDistinctFrom(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return isNotDistinctFrom(record.valuesRow());
    }

    @Override // org.jooq.Row11
    public final Condition isNotDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return isNotDistinctFrom((Field) Tools.field(t1, dataType(0)), (Field) Tools.field(t2, dataType(1)), (Field) Tools.field(t3, dataType(2)), (Field) Tools.field(t4, dataType(3)), (Field) Tools.field(t5, dataType(4)), (Field) Tools.field(t6, dataType(5)), (Field) Tools.field(t7, dataType(6)), (Field) Tools.field(t8, dataType(7)), (Field) Tools.field(t9, dataType(8)), (Field) Tools.field(t10, dataType(9)), (Field) Tools.field(t11, dataType(10)));
    }

    @Override // org.jooq.Row11
    public final Condition isNotDistinctFrom(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return isNotDistinctFrom(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5, (SelectField) t6, (SelectField) t7, (SelectField) t8, (SelectField) t9, (SelectField) t10, (SelectField) t11));
    }

    @Override // org.jooq.Row11
    public final Condition isNotDistinctFrom(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return new RowIsDistinctFrom((Row) this, (Select<?>) select, true);
    }

    @Override // org.jooq.Row11
    public final Condition isDistinctFrom(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return new RowIsDistinctFrom((Row) this, (Row) row, false);
    }

    @Override // org.jooq.Row11
    public final Condition isDistinctFrom(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return isDistinctFrom(record.valuesRow());
    }

    @Override // org.jooq.Row11
    public final Condition isDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return isDistinctFrom((Field) Tools.field(t1, dataType(0)), (Field) Tools.field(t2, dataType(1)), (Field) Tools.field(t3, dataType(2)), (Field) Tools.field(t4, dataType(3)), (Field) Tools.field(t5, dataType(4)), (Field) Tools.field(t6, dataType(5)), (Field) Tools.field(t7, dataType(6)), (Field) Tools.field(t8, dataType(7)), (Field) Tools.field(t9, dataType(8)), (Field) Tools.field(t10, dataType(9)), (Field) Tools.field(t11, dataType(10)));
    }

    @Override // org.jooq.Row11
    public final Condition isDistinctFrom(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return isDistinctFrom(DSL.row((SelectField) t1, (SelectField) t2, (SelectField) t3, (SelectField) t4, (SelectField) t5, (SelectField) t6, (SelectField) t7, (SelectField) t8, (SelectField) t9, (SelectField) t10, (SelectField) t11));
    }

    @Override // org.jooq.Row11
    public final Condition isDistinctFrom(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return new RowIsDistinctFrom((Row) this, (Select<?>) select, false);
    }

    @Override // org.jooq.Row11
    public final Condition in(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>... rows) {
        return in(Arrays.asList(rows));
    }

    @Override // org.jooq.Row11
    public final Condition in(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>... records) {
        QueryPartList<Row> rows = new QueryPartList<>();
        for (Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11 : records) {
            rows.add((QueryPartList<Row>) record11.valuesRow());
        }
        return new RowInCondition(this, rows, false);
    }

    @Override // org.jooq.Row11
    public final Condition notIn(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>... rows) {
        return notIn(Arrays.asList(rows));
    }

    @Override // org.jooq.Row11
    public final Condition notIn(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>... records) {
        QueryPartList<Row> rows = new QueryPartList<>();
        for (Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record11 : records) {
            rows.add((QueryPartList<Row>) record11.valuesRow());
        }
        return new RowInCondition(this, rows, true);
    }

    @Override // org.jooq.Row11
    public final Condition in(Collection<? extends Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> rows) {
        return new RowInCondition(this, new QueryPartList(rows), false);
    }

    @Override // org.jooq.Row11
    public final Condition in(Result<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> result) {
        return new RowInCondition(this, new QueryPartList(Tools.rows(result)), false);
    }

    @Override // org.jooq.Row11
    public final Condition notIn(Collection<? extends Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> rows) {
        return new RowInCondition(this, new QueryPartList(rows), true);
    }

    @Override // org.jooq.Row11
    public final Condition notIn(Result<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> result) {
        return new RowInCondition(this, new QueryPartList(Tools.rows(result)), true);
    }

    @Override // org.jooq.Row11
    public final Condition equal(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.EQUALS, select);
    }

    @Override // org.jooq.Row11
    public final Condition equal(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.EQUALS, select);
    }

    @Override // org.jooq.Row11
    public final Condition eq(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return equal(select);
    }

    @Override // org.jooq.Row11
    public final Condition eq(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return equal(select);
    }

    @Override // org.jooq.Row11
    public final Condition notEqual(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.NOT_EQUALS, select);
    }

    @Override // org.jooq.Row11
    public final Condition notEqual(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.NOT_EQUALS, select);
    }

    @Override // org.jooq.Row11
    public final Condition ne(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return notEqual(select);
    }

    @Override // org.jooq.Row11
    public final Condition ne(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return notEqual(select);
    }

    @Override // org.jooq.Row11
    public final Condition greaterThan(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.GREATER, select);
    }

    @Override // org.jooq.Row11
    public final Condition greaterThan(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.GREATER, select);
    }

    @Override // org.jooq.Row11
    public final Condition gt(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return greaterThan(select);
    }

    @Override // org.jooq.Row11
    public final Condition gt(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return greaterThan(select);
    }

    @Override // org.jooq.Row11
    public final Condition greaterOrEqual(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.GREATER_OR_EQUAL, select);
    }

    @Override // org.jooq.Row11
    public final Condition greaterOrEqual(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.GREATER_OR_EQUAL, select);
    }

    @Override // org.jooq.Row11
    public final Condition ge(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return greaterOrEqual(select);
    }

    @Override // org.jooq.Row11
    public final Condition ge(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return greaterOrEqual(select);
    }

    @Override // org.jooq.Row11
    public final Condition lessThan(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.LESS, select);
    }

    @Override // org.jooq.Row11
    public final Condition lessThan(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.LESS, select);
    }

    @Override // org.jooq.Row11
    public final Condition lt(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return lessThan(select);
    }

    @Override // org.jooq.Row11
    public final Condition lt(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return lessThan(select);
    }

    @Override // org.jooq.Row11
    public final Condition lessOrEqual(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.LESS_OR_EQUAL, select);
    }

    @Override // org.jooq.Row11
    public final Condition lessOrEqual(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.LESS_OR_EQUAL, select);
    }

    @Override // org.jooq.Row11
    public final Condition le(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return lessOrEqual(select);
    }

    @Override // org.jooq.Row11
    public final Condition le(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return lessOrEqual(select);
    }

    @Override // org.jooq.Row11
    public final Condition in(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.IN, select);
    }

    @Override // org.jooq.Row11
    public final Condition notIn(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.NOT_IN, select);
    }
}
