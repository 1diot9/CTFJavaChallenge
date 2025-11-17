package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Record1.class */
public interface Record1<T1> extends Record {
    @Override // org.jooq.Fields
    @NotNull
    Row1<T1> fieldsRow();

    @Override // org.jooq.Record
    @NotNull
    Row1<T1> valuesRow();

    @NotNull
    Field<T1> field1();

    T1 value1();

    @NotNull
    Record1<T1> value1(T1 t1);

    @NotNull
    Record1<T1> values(T1 t1);

    @Override // org.jooq.Record
    @NotNull
    <T> Record1<T1> with(Field<T> field, T t);

    @Override // org.jooq.Record
    @NotNull
    <T, U> Record1<T1> with(Field<T> field, U u, Converter<? extends T, ? super U> converter);

    T1 component1();

    @Override // org.jooq.Record
    @NotNull
    /* bridge */ /* synthetic */ default Record with(Field field, Object obj, Converter converter) {
        return with(field, (Field) obj, (Converter<? extends T, ? super Field>) converter);
    }

    @Override // org.jooq.Record
    @NotNull
    /* bridge */ /* synthetic */ default Record with(Field field, Object obj) {
        return with((Field<Field>) field, (Field) obj);
    }
}
