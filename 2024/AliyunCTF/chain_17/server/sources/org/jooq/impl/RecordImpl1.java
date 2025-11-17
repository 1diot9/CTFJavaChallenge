package org.jooq.impl;

import org.jooq.Converter;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RecordImpl1.class */
public final class RecordImpl1<T1> extends AbstractRecord implements InternalRecord, Record1<T1> {
    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public /* bridge */ /* synthetic */ Record with(Field field, Object obj, Converter converter) {
        return with(field, (Field) obj, (Converter<? extends T, ? super Field>) converter);
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public /* bridge */ /* synthetic */ Record with(Field field, Object obj) {
        return with((Field<Field>) field, (Field) obj);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RecordImpl1(AbstractRow<?> row) {
        super(row);
    }

    @Override // org.jooq.Fields
    public RowImpl1<T1> fieldsRow() {
        return new RowImpl1<>(field1());
    }

    @Override // org.jooq.Record
    public final RowImpl1<T1> valuesRow() {
        return new RowImpl1<>(Tools.field(value1(), field1()));
    }

    @Override // org.jooq.Record1
    public final Field<T1> field1() {
        return (Field<T1>) this.fields.field(0);
    }

    @Override // org.jooq.Record1
    public final T1 value1() {
        return (T1) get(0);
    }

    @Override // org.jooq.Record1
    public final Record1<T1> value1(T1 value) {
        set(0, value);
        return this;
    }

    @Override // org.jooq.Record1
    public final Record1<T1> values(T1 t1) {
        set(0, t1);
        return this;
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public <T> Record1<T1> with(Field<T> field, T value) {
        return (Record1) super.with((Field<Field<T>>) field, (Field<T>) value);
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public <T, U> Record1<T1> with(Field<T> field, U value, Converter<? extends T, ? super U> converter) {
        return (Record1) super.with((Field) field, (Field<T>) value, (Converter<? extends T, ? super Field<T>>) converter);
    }

    @Override // org.jooq.Record1
    public final T1 component1() {
        return value1();
    }
}
