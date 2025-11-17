package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Record10.class */
public interface Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> extends Record {
    @Override // org.jooq.Fields
    @NotNull
    Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> fieldsRow();

    @Override // org.jooq.Record
    @NotNull
    Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> valuesRow();

    @NotNull
    Field<T1> field1();

    @NotNull
    Field<T2> field2();

    @NotNull
    Field<T3> field3();

    @NotNull
    Field<T4> field4();

    @NotNull
    Field<T5> field5();

    @NotNull
    Field<T6> field6();

    @NotNull
    Field<T7> field7();

    @NotNull
    Field<T8> field8();

    @NotNull
    Field<T9> field9();

    @NotNull
    Field<T10> field10();

    T1 value1();

    T2 value2();

    T3 value3();

    T4 value4();

    T5 value5();

    T6 value6();

    T7 value7();

    T8 value8();

    T9 value9();

    T10 value10();

    @NotNull
    Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> value1(T1 t1);

    @NotNull
    Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> value2(T2 t2);

    @NotNull
    Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> value3(T3 t3);

    @NotNull
    Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> value4(T4 t4);

    @NotNull
    Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> value5(T5 t5);

    @NotNull
    Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> value6(T6 t6);

    @NotNull
    Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> value7(T7 t7);

    @NotNull
    Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> value8(T8 t8);

    @NotNull
    Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> value9(T9 t9);

    @NotNull
    Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> value10(T10 t10);

    @NotNull
    Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> values(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);

    @Override // org.jooq.Record
    @NotNull
    <T> Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> with(Field<T> field, T t);

    @Override // org.jooq.Record
    @NotNull
    <T, U> Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> with(Field<T> field, U u, Converter<? extends T, ? super U> converter);

    T1 component1();

    T2 component2();

    T3 component3();

    T4 component4();

    T5 component5();

    T6 component6();

    T7 component7();

    T8 component8();

    T9 component9();

    T10 component10();

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
