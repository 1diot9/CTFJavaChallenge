package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Record3.class */
public interface Record3<T1, T2, T3> extends Record {
    @Override // org.jooq.Fields
    @NotNull
    Row3<T1, T2, T3> fieldsRow();

    @Override // org.jooq.Record
    @NotNull
    Row3<T1, T2, T3> valuesRow();

    @NotNull
    Field<T1> field1();

    @NotNull
    Field<T2> field2();

    @NotNull
    Field<T3> field3();

    T1 value1();

    T2 value2();

    T3 value3();

    @NotNull
    Record3<T1, T2, T3> value1(T1 t1);

    @NotNull
    Record3<T1, T2, T3> value2(T2 t2);

    @NotNull
    Record3<T1, T2, T3> value3(T3 t3);

    @NotNull
    Record3<T1, T2, T3> values(T1 t1, T2 t2, T3 t3);

    @Override // org.jooq.Record
    @NotNull
    <T> Record3<T1, T2, T3> with(Field<T> field, T t);

    @Override // org.jooq.Record
    @NotNull
    <T, U> Record3<T1, T2, T3> with(Field<T> field, U u, Converter<? extends T, ? super U> converter);

    T1 component1();

    T2 component2();

    T3 component3();

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
