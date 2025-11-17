package org.jooq.impl;

import org.jooq.Converter;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record9;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RecordImpl9.class */
public final class RecordImpl9<T1, T2, T3, T4, T5, T6, T7, T8, T9> extends AbstractRecord implements InternalRecord, Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> {
    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public /* bridge */ /* synthetic */ Record with(Field field, Object obj, Converter converter) {
        return with(field, (Field) obj, (Converter<? extends T, ? super Field>) converter);
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public /* bridge */ /* synthetic */ Record with(Field field, Object obj) {
        return with((Field<Field>) field, (Field) obj);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RecordImpl9(AbstractRow<?> row) {
        super(row);
    }

    @Override // org.jooq.Fields
    public RowImpl9<T1, T2, T3, T4, T5, T6, T7, T8, T9> fieldsRow() {
        return new RowImpl9<>(field1(), field2(), field3(), field4(), field5(), field6(), field7(), field8(), field9());
    }

    @Override // org.jooq.Record
    public final RowImpl9<T1, T2, T3, T4, T5, T6, T7, T8, T9> valuesRow() {
        return new RowImpl9<>(Tools.field(value1(), field1()), Tools.field(value2(), field2()), Tools.field(value3(), field3()), Tools.field(value4(), field4()), Tools.field(value5(), field5()), Tools.field(value6(), field6()), Tools.field(value7(), field7()), Tools.field(value8(), field8()), Tools.field(value9(), field9()));
    }

    @Override // org.jooq.Record9
    public final Field<T1> field1() {
        return (Field<T1>) this.fields.field(0);
    }

    @Override // org.jooq.Record9
    public final Field<T2> field2() {
        return (Field<T2>) this.fields.field(1);
    }

    @Override // org.jooq.Record9
    public final Field<T3> field3() {
        return (Field<T3>) this.fields.field(2);
    }

    @Override // org.jooq.Record9
    public final Field<T4> field4() {
        return (Field<T4>) this.fields.field(3);
    }

    @Override // org.jooq.Record9
    public final Field<T5> field5() {
        return (Field<T5>) this.fields.field(4);
    }

    @Override // org.jooq.Record9
    public final Field<T6> field6() {
        return (Field<T6>) this.fields.field(5);
    }

    @Override // org.jooq.Record9
    public final Field<T7> field7() {
        return (Field<T7>) this.fields.field(6);
    }

    @Override // org.jooq.Record9
    public final Field<T8> field8() {
        return (Field<T8>) this.fields.field(7);
    }

    @Override // org.jooq.Record9
    public final Field<T9> field9() {
        return (Field<T9>) this.fields.field(8);
    }

    @Override // org.jooq.Record9
    public final T1 value1() {
        return (T1) get(0);
    }

    @Override // org.jooq.Record9
    public final T2 value2() {
        return (T2) get(1);
    }

    @Override // org.jooq.Record9
    public final T3 value3() {
        return (T3) get(2);
    }

    @Override // org.jooq.Record9
    public final T4 value4() {
        return (T4) get(3);
    }

    @Override // org.jooq.Record9
    public final T5 value5() {
        return (T5) get(4);
    }

    @Override // org.jooq.Record9
    public final T6 value6() {
        return (T6) get(5);
    }

    @Override // org.jooq.Record9
    public final T7 value7() {
        return (T7) get(6);
    }

    @Override // org.jooq.Record9
    public final T8 value8() {
        return (T8) get(7);
    }

    @Override // org.jooq.Record9
    public final T9 value9() {
        return (T9) get(8);
    }

    @Override // org.jooq.Record9
    public final Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> value1(T1 value) {
        set(0, value);
        return this;
    }

    @Override // org.jooq.Record9
    public final Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> value2(T2 value) {
        set(1, value);
        return this;
    }

    @Override // org.jooq.Record9
    public final Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> value3(T3 value) {
        set(2, value);
        return this;
    }

    @Override // org.jooq.Record9
    public final Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> value4(T4 value) {
        set(3, value);
        return this;
    }

    @Override // org.jooq.Record9
    public final Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> value5(T5 value) {
        set(4, value);
        return this;
    }

    @Override // org.jooq.Record9
    public final Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> value6(T6 value) {
        set(5, value);
        return this;
    }

    @Override // org.jooq.Record9
    public final Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> value7(T7 value) {
        set(6, value);
        return this;
    }

    @Override // org.jooq.Record9
    public final Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> value8(T8 value) {
        set(7, value);
        return this;
    }

    @Override // org.jooq.Record9
    public final Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> value9(T9 value) {
        set(8, value);
        return this;
    }

    @Override // org.jooq.Record9
    public final Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> values(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9) {
        set(0, t1);
        set(1, t2);
        set(2, t3);
        set(3, t4);
        set(4, t5);
        set(5, t6);
        set(6, t7);
        set(7, t8);
        set(8, t9);
        return this;
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public <T> Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> with(Field<T> field, T value) {
        return (Record9) super.with((Field<Field<T>>) field, (Field<T>) value);
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public <T, U> Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> with(Field<T> field, U value, Converter<? extends T, ? super U> converter) {
        return (Record9) super.with((Field) field, (Field<T>) value, (Converter<? extends T, ? super Field<T>>) converter);
    }

    @Override // org.jooq.Record9
    public final T1 component1() {
        return value1();
    }

    @Override // org.jooq.Record9
    public final T2 component2() {
        return value2();
    }

    @Override // org.jooq.Record9
    public final T3 component3() {
        return value3();
    }

    @Override // org.jooq.Record9
    public final T4 component4() {
        return value4();
    }

    @Override // org.jooq.Record9
    public final T5 component5() {
        return value5();
    }

    @Override // org.jooq.Record9
    public final T6 component6() {
        return value6();
    }

    @Override // org.jooq.Record9
    public final T7 component7() {
        return value7();
    }

    @Override // org.jooq.Record9
    public final T8 component8() {
        return value8();
    }

    @Override // org.jooq.Record9
    public final T9 component9() {
        return value9();
    }
}
