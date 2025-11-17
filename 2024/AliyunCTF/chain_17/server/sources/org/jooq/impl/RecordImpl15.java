package org.jooq.impl;

import org.jooq.Converter;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record15;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RecordImpl15.class */
public final class RecordImpl15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> extends AbstractRecord implements InternalRecord, Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> {
    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public /* bridge */ /* synthetic */ Record with(Field field, Object obj, Converter converter) {
        return with(field, (Field) obj, (Converter<? extends T, ? super Field>) converter);
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public /* bridge */ /* synthetic */ Record with(Field field, Object obj) {
        return with((Field<Field>) field, (Field) obj);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RecordImpl15(AbstractRow<?> row) {
        super(row);
    }

    @Override // org.jooq.Fields
    public RowImpl15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> fieldsRow() {
        return new RowImpl15<>(field1(), field2(), field3(), field4(), field5(), field6(), field7(), field8(), field9(), field10(), field11(), field12(), field13(), field14(), field15());
    }

    @Override // org.jooq.Record
    public final RowImpl15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> valuesRow() {
        return new RowImpl15<>(Tools.field(value1(), field1()), Tools.field(value2(), field2()), Tools.field(value3(), field3()), Tools.field(value4(), field4()), Tools.field(value5(), field5()), Tools.field(value6(), field6()), Tools.field(value7(), field7()), Tools.field(value8(), field8()), Tools.field(value9(), field9()), Tools.field(value10(), field10()), Tools.field(value11(), field11()), Tools.field(value12(), field12()), Tools.field(value13(), field13()), Tools.field(value14(), field14()), Tools.field(value15(), field15()));
    }

    @Override // org.jooq.Record15
    public final Field<T1> field1() {
        return (Field<T1>) this.fields.field(0);
    }

    @Override // org.jooq.Record15
    public final Field<T2> field2() {
        return (Field<T2>) this.fields.field(1);
    }

    @Override // org.jooq.Record15
    public final Field<T3> field3() {
        return (Field<T3>) this.fields.field(2);
    }

    @Override // org.jooq.Record15
    public final Field<T4> field4() {
        return (Field<T4>) this.fields.field(3);
    }

    @Override // org.jooq.Record15
    public final Field<T5> field5() {
        return (Field<T5>) this.fields.field(4);
    }

    @Override // org.jooq.Record15
    public final Field<T6> field6() {
        return (Field<T6>) this.fields.field(5);
    }

    @Override // org.jooq.Record15
    public final Field<T7> field7() {
        return (Field<T7>) this.fields.field(6);
    }

    @Override // org.jooq.Record15
    public final Field<T8> field8() {
        return (Field<T8>) this.fields.field(7);
    }

    @Override // org.jooq.Record15
    public final Field<T9> field9() {
        return (Field<T9>) this.fields.field(8);
    }

    @Override // org.jooq.Record15
    public final Field<T10> field10() {
        return (Field<T10>) this.fields.field(9);
    }

    @Override // org.jooq.Record15
    public final Field<T11> field11() {
        return (Field<T11>) this.fields.field(10);
    }

    @Override // org.jooq.Record15
    public final Field<T12> field12() {
        return (Field<T12>) this.fields.field(11);
    }

    @Override // org.jooq.Record15
    public final Field<T13> field13() {
        return (Field<T13>) this.fields.field(12);
    }

    @Override // org.jooq.Record15
    public final Field<T14> field14() {
        return (Field<T14>) this.fields.field(13);
    }

    @Override // org.jooq.Record15
    public final Field<T15> field15() {
        return (Field<T15>) this.fields.field(14);
    }

    @Override // org.jooq.Record15
    public final T1 value1() {
        return (T1) get(0);
    }

    @Override // org.jooq.Record15
    public final T2 value2() {
        return (T2) get(1);
    }

    @Override // org.jooq.Record15
    public final T3 value3() {
        return (T3) get(2);
    }

    @Override // org.jooq.Record15
    public final T4 value4() {
        return (T4) get(3);
    }

    @Override // org.jooq.Record15
    public final T5 value5() {
        return (T5) get(4);
    }

    @Override // org.jooq.Record15
    public final T6 value6() {
        return (T6) get(5);
    }

    @Override // org.jooq.Record15
    public final T7 value7() {
        return (T7) get(6);
    }

    @Override // org.jooq.Record15
    public final T8 value8() {
        return (T8) get(7);
    }

    @Override // org.jooq.Record15
    public final T9 value9() {
        return (T9) get(8);
    }

    @Override // org.jooq.Record15
    public final T10 value10() {
        return (T10) get(9);
    }

    @Override // org.jooq.Record15
    public final T11 value11() {
        return (T11) get(10);
    }

    @Override // org.jooq.Record15
    public final T12 value12() {
        return (T12) get(11);
    }

    @Override // org.jooq.Record15
    public final T13 value13() {
        return (T13) get(12);
    }

    @Override // org.jooq.Record15
    public final T14 value14() {
        return (T14) get(13);
    }

    @Override // org.jooq.Record15
    public final T15 value15() {
        return (T15) get(14);
    }

    @Override // org.jooq.Record15
    public final Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> value1(T1 value) {
        set(0, value);
        return this;
    }

    @Override // org.jooq.Record15
    public final Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> value2(T2 value) {
        set(1, value);
        return this;
    }

    @Override // org.jooq.Record15
    public final Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> value3(T3 value) {
        set(2, value);
        return this;
    }

    @Override // org.jooq.Record15
    public final Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> value4(T4 value) {
        set(3, value);
        return this;
    }

    @Override // org.jooq.Record15
    public final Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> value5(T5 value) {
        set(4, value);
        return this;
    }

    @Override // org.jooq.Record15
    public final Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> value6(T6 value) {
        set(5, value);
        return this;
    }

    @Override // org.jooq.Record15
    public final Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> value7(T7 value) {
        set(6, value);
        return this;
    }

    @Override // org.jooq.Record15
    public final Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> value8(T8 value) {
        set(7, value);
        return this;
    }

    @Override // org.jooq.Record15
    public final Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> value9(T9 value) {
        set(8, value);
        return this;
    }

    @Override // org.jooq.Record15
    public final Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> value10(T10 value) {
        set(9, value);
        return this;
    }

    @Override // org.jooq.Record15
    public final Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> value11(T11 value) {
        set(10, value);
        return this;
    }

    @Override // org.jooq.Record15
    public final Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> value12(T12 value) {
        set(11, value);
        return this;
    }

    @Override // org.jooq.Record15
    public final Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> value13(T13 value) {
        set(12, value);
        return this;
    }

    @Override // org.jooq.Record15
    public final Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> value14(T14 value) {
        set(13, value);
        return this;
    }

    @Override // org.jooq.Record15
    public final Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> value15(T15 value) {
        set(14, value);
        return this;
    }

    @Override // org.jooq.Record15
    public final Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> values(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15) {
        set(0, t1);
        set(1, t2);
        set(2, t3);
        set(3, t4);
        set(4, t5);
        set(5, t6);
        set(6, t7);
        set(7, t8);
        set(8, t9);
        set(9, t10);
        set(10, t11);
        set(11, t12);
        set(12, t13);
        set(13, t14);
        set(14, t15);
        return this;
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public <T> Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> with(Field<T> field, T value) {
        return (Record15) super.with((Field<Field<T>>) field, (Field<T>) value);
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public <T, U> Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> with(Field<T> field, U value, Converter<? extends T, ? super U> converter) {
        return (Record15) super.with((Field) field, (Field<T>) value, (Converter<? extends T, ? super Field<T>>) converter);
    }

    @Override // org.jooq.Record15
    public final T1 component1() {
        return value1();
    }

    @Override // org.jooq.Record15
    public final T2 component2() {
        return value2();
    }

    @Override // org.jooq.Record15
    public final T3 component3() {
        return value3();
    }

    @Override // org.jooq.Record15
    public final T4 component4() {
        return value4();
    }

    @Override // org.jooq.Record15
    public final T5 component5() {
        return value5();
    }

    @Override // org.jooq.Record15
    public final T6 component6() {
        return value6();
    }

    @Override // org.jooq.Record15
    public final T7 component7() {
        return value7();
    }

    @Override // org.jooq.Record15
    public final T8 component8() {
        return value8();
    }

    @Override // org.jooq.Record15
    public final T9 component9() {
        return value9();
    }

    @Override // org.jooq.Record15
    public final T10 component10() {
        return value10();
    }

    @Override // org.jooq.Record15
    public final T11 component11() {
        return value11();
    }

    @Override // org.jooq.Record15
    public final T12 component12() {
        return value12();
    }

    @Override // org.jooq.Record15
    public final T13 component13() {
        return value13();
    }

    @Override // org.jooq.Record15
    public final T14 component14() {
        return value14();
    }

    @Override // org.jooq.Record15
    public final T15 component15() {
        return value15();
    }
}
