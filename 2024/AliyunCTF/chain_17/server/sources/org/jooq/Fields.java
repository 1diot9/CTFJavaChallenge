package org.jooq;

import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Fields.class */
public interface Fields {
    @NotNull
    Field<?>[] fields();

    @NotNull
    Row fieldsRow();

    @NotNull
    Stream<Field<?>> fieldStream();

    @Nullable
    <T> Field<T> field(Field<T> field);

    @Nullable
    Field<?> field(String str);

    @Nullable
    <T> Field<T> field(String str, Class<T> cls);

    @Nullable
    <T> Field<T> field(String str, DataType<T> dataType);

    @Nullable
    Field<?> field(Name name);

    @Nullable
    <T> Field<T> field(Name name, Class<T> cls);

    @Nullable
    <T> Field<T> field(Name name, DataType<T> dataType);

    @Nullable
    Field<?> field(int i);

    @Nullable
    <T> Field<T> field(int i, Class<T> cls);

    @Nullable
    <T> Field<T> field(int i, DataType<T> dataType);

    @Nullable
    Field<?>[] fields(Field<?>... fieldArr);

    @Nullable
    Field<?>[] fields(String... strArr);

    @Nullable
    Field<?>[] fields(Name... nameArr);

    @Nullable
    Field<?>[] fields(int... iArr);

    int indexOf(Field<?> field);

    int indexOf(String str);

    int indexOf(Name name);

    @NotNull
    Class<?>[] types();

    @Nullable
    Class<?> type(int i);

    @Nullable
    Class<?> type(String str);

    @Nullable
    Class<?> type(Name name);

    @NotNull
    DataType<?>[] dataTypes();

    @Nullable
    DataType<?> dataType(int i);

    @Nullable
    DataType<?> dataType(String str);

    @Nullable
    DataType<?> dataType(Name name);
}
