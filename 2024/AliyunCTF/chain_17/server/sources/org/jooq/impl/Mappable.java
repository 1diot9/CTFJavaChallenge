package org.jooq.impl;

import org.jetbrains.annotations.NotNull;
import org.jooq.Configuration;
import org.jooq.Converter;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Table;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Mappable.class */
interface Mappable<R extends Record> {
    @NotNull
    RecordMapper<R, ?> mapper(int i);

    @NotNull
    <U> RecordMapper<R, U> mapper(int i, Configuration configuration, Class<? extends U> cls);

    @NotNull
    <U> RecordMapper<R, U> mapper(int i, Converter<?, ? extends U> converter);

    @NotNull
    RecordMapper<R, Record> mapper(int[] iArr);

    @NotNull
    RecordMapper<R, ?> mapper(String str);

    @NotNull
    <U> RecordMapper<R, U> mapper(String str, Configuration configuration, Class<? extends U> cls);

    @NotNull
    <U> RecordMapper<R, U> mapper(String str, Converter<?, ? extends U> converter);

    @NotNull
    RecordMapper<R, Record> mapper(String[] strArr);

    @NotNull
    RecordMapper<R, ?> mapper(Name name);

    @NotNull
    <U> RecordMapper<R, U> mapper(Name name, Configuration configuration, Class<? extends U> cls);

    @NotNull
    <U> RecordMapper<R, U> mapper(Name name, Converter<?, ? extends U> converter);

    @NotNull
    RecordMapper<R, Record> mapper(Name[] nameArr);

    @NotNull
    <T> RecordMapper<R, T> mapper(Field<T> field);

    @NotNull
    <U> RecordMapper<R, U> mapper(Field<?> field, Configuration configuration, Class<? extends U> cls);

    @NotNull
    <T, U> RecordMapper<R, U> mapper(Field<T> field, Converter<? super T, ? extends U> converter);

    @NotNull
    RecordMapper<R, Record> mapper(Field<?>[] fieldArr);

    @NotNull
    <S extends Record> RecordMapper<R, S> mapper(Table<S> table);

    @NotNull
    <E> RecordMapper<R, E> mapper(Configuration configuration, Class<? extends E> cls);
}
