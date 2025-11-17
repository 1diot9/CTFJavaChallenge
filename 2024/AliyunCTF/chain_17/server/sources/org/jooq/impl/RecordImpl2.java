package org.jooq.impl;

import org.jooq.Converter;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record2;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RecordImpl2.class */
public final class RecordImpl2<T1, T2> extends AbstractRecord implements InternalRecord, Record2<T1, T2> {
    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public /* bridge */ /* synthetic */ Record with(Field field, Object obj, Converter converter) {
        return with(field, (Field) obj, (Converter<? extends T, ? super Field>) converter);
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public /* bridge */ /* synthetic */ Record with(Field field, Object obj) {
        return with((Field<Field>) field, (Field) obj);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RecordImpl2(AbstractRow<?> row) {
        super(row);
    }

    @Override // org.jooq.Fields
    public RowImpl2<T1, T2> fieldsRow() {
        return new RowImpl2<>(field1(), field2());
    }

    @Override // org.jooq.Record
    public final RowImpl2<T1, T2> valuesRow() {
        return new RowImpl2<>(Tools.field(value1(), field1()), Tools.field(value2(), field2()));
    }

    @Override // org.jooq.Record2
    public final Field<T1> field1() {
        return (Field<T1>) this.fields.field(0);
    }

    @Override // org.jooq.Record2
    public final Field<T2> field2() {
        return (Field<T2>) this.fields.field(1);
    }

    @Override // org.jooq.Record2
    public final T1 value1() {
        return (T1) get(0);
    }

    @Override // org.jooq.Record2
    public final T2 value2() {
        return (T2) get(1);
    }

    @Override // org.jooq.Record2
    public final Record2<T1, T2> value1(T1 value) {
        set(0, value);
        return this;
    }

    @Override // org.jooq.Record2
    public final Record2<T1, T2> value2(T2 value) {
        set(1, value);
        return this;
    }

    @Override // org.jooq.Record2
    public final Record2<T1, T2> values(T1 t1, T2 t2) {
        set(0, t1);
        set(1, t2);
        return this;
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public <T> Record2<T1, T2> with(Field<T> field, T value) {
        return (Record2) super.with((Field<Field<T>>) field, (Field<T>) value);
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public <T, U> Record2<T1, T2> with(Field<T> field, U value, Converter<? extends T, ? super U> converter) {
        return (Record2) super.with((Field) field, (Field<T>) value, (Converter<? extends T, ? super Field<T>>) converter);
    }

    @Override // org.jooq.Record2
    public final T1 component1() {
        return value1();
    }

    @Override // org.jooq.Record2
    public final T2 component2() {
        return value2();
    }
}
