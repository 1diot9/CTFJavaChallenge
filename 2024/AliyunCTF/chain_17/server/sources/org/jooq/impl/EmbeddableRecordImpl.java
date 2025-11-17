package org.jooq.impl;

import org.jooq.Converter;
import org.jooq.EmbeddableRecord;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Row;
import org.jooq.TableField;

@org.jooq.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/EmbeddableRecordImpl.class */
public class EmbeddableRecordImpl<R extends EmbeddableRecord<R>> extends AbstractRecord implements EmbeddableRecord<R> {
    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public /* bridge */ /* synthetic */ int compareTo(Record record) {
        return super.compareTo(record);
    }

    @Override // org.jooq.impl.AbstractRecord
    public /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public /* bridge */ /* synthetic */ Object into(Object obj) {
        return super.into((EmbeddableRecordImpl<R>) obj);
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public /* bridge */ /* synthetic */ Record with(Field field, Object obj, Converter converter) {
        return with(field, (Field) obj, (Converter<? extends T, ? super Field>) converter);
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public /* bridge */ /* synthetic */ Record with(Field field, Object obj) {
        return with((Field<Field>) field, (Field) obj);
    }

    @Override // org.jooq.impl.AbstractStore, org.jooq.Record
    public /* bridge */ /* synthetic */ boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override // org.jooq.impl.AbstractStore, org.jooq.Record
    public /* bridge */ /* synthetic */ int hashCode() {
        return super.hashCode();
    }

    @Deprecated
    public EmbeddableRecordImpl(Field<?>... fields) {
        super(fields);
    }

    @Deprecated
    public EmbeddableRecordImpl(TableField<?, ?>... fields) {
        super(fields);
    }

    public EmbeddableRecordImpl(Row fields) {
        super((AbstractRow<?>) fields);
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public final <T> R with(Field<T> field, T value) {
        return (R) super.with((Field<Field<T>>) field, (Field<T>) value);
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public final <T, U> R with(Field<T> field, U value, Converter<? extends T, ? super U> converter) {
        return (R) super.with((Field) field, (Field<T>) value, (Converter<? extends T, ? super Field<T>>) converter);
    }

    @Override // org.jooq.Fields
    public Row fieldsRow() {
        return this.fields;
    }

    @Override // org.jooq.Record
    public Row valuesRow() {
        return Tools.row0(Tools.fieldsArray(intoArray(), this.fields.fields.fields()));
    }

    @Override // org.jooq.impl.AbstractRecord, org.jooq.Record
    public final R original() {
        return (R) super.original();
    }
}
