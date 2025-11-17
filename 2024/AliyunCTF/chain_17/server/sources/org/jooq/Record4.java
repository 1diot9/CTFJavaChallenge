package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Record4.class */
public interface Record4<T1, T2, T3, T4> extends Record {
    @Override // org.jooq.Fields
    @NotNull
    Row4<T1, T2, T3, T4> fieldsRow();

    @Override // org.jooq.Record
    @NotNull
    Row4<T1, T2, T3, T4> valuesRow();

    @NotNull
    Field<T1> field1();

    @NotNull
    Field<T2> field2();

    @NotNull
    Field<T3> field3();

    @NotNull
    Field<T4> field4();

    T1 value1();

    T2 value2();

    T3 value3();

    T4 value4();

    @NotNull
    Record4<T1, T2, T3, T4> value1(T1 t1);

    @NotNull
    Record4<T1, T2, T3, T4> value2(T2 t2);

    @NotNull
    Record4<T1, T2, T3, T4> value3(T3 t3);

    @NotNull
    Record4<T1, T2, T3, T4> value4(T4 t4);

    @NotNull
    Record4<T1, T2, T3, T4> values(T1 t1, T2 t2, T3 t3, T4 t4);

    @Override // org.jooq.Record
    @NotNull
    <T> Record4<T1, T2, T3, T4> with(Field<T> field, T t);

    @Override // org.jooq.Record
    @NotNull
    <T, U> Record4<T1, T2, T3, T4> with(Field<T> field, U u, Converter<? extends T, ? super U> converter);

    T1 component1();

    T2 component2();

    T3 component3();

    T4 component4();

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
