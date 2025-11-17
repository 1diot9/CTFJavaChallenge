package org.jooq.util.postgres;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Constants;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Support;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/postgres/PostgresDSL.class */
public class PostgresDSL extends DSL {
    protected PostgresDSL() {
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Condition arrayOverlap(T[] left, T[] right) {
        return arrayOverlap(val(left), val(right));
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Condition arrayOverlap(T[] left, Field<T[]> right) {
        return arrayOverlap(val(left), right);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Condition arrayOverlap(Field<T[]> left, T[] right) {
        return arrayOverlap(left, val(right));
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Condition arrayOverlap(Field<T[]> left, Field<T[]> right) {
        return DSL.condition("{0} && {1}", left, right);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Field<T[]> arrayAppend(T[] array, T value) {
        return arrayAppend0(val(array), val(value));
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Field<T[]> arrayAppend(T[] array, Field<T> value) {
        return arrayAppend0(val(array), value);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Field<T[]> arrayAppend(Field<T[]> array, T value) {
        return arrayAppend0(array, val(value));
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Field<T[]> arrayAppend(Field<T[]> array, Field<T> value) {
        return arrayAppend0(array, value);
    }

    static <T> Field<T[]> arrayAppend0(Field<T[]> array, Field<T> value) {
        return function("array_append", nullSafeDataType(array), (Field<?>[]) new Field[]{array, value});
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Field<T[]> arrayPrepend(T value, T[] array) {
        return arrayPrepend0(val(value), val(array));
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Field<T[]> arrayPrepend(Field<T> value, T[] array) {
        return arrayPrepend0(value, val(array));
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Field<T[]> arrayPrepend(T value, Field<T[]> array) {
        return arrayPrepend0(val(value), array);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Field<T[]> arrayPrepend(Field<T> value, Field<T[]> array) {
        return arrayPrepend0(value, array);
    }

    static <T> Field<T[]> arrayPrepend0(Field<T> value, Field<T[]> array) {
        return function("array_prepend", nullSafeDataType(array), (Field<?>[]) new Field[]{value, array});
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Field<T[]> arrayCat(T[] array1, T[] array2) {
        return arrayCat(val(array1), val(array2));
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Field<T[]> arrayCat(T[] array1, Field<T[]> array2) {
        return arrayCat(val(array1), array2);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Field<T[]> arrayCat(Field<T[]> array1, T[] array2) {
        return arrayCat(array1, val(array2));
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Field<T[]> arrayCat(Field<T[]> array1, Field<T[]> array2) {
        return function("array_cat", nullSafeDataType(array1), (Field<?>[]) new Field[]{array1, array2});
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Field<T[]> arrayRemove(T[] array, T element) {
        return arrayRemove0(val(array), val(element));
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Field<T[]> arrayRemove(Field<T[]> array, T element) {
        return arrayRemove0(nullSafe(array), val(element));
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Field<T[]> arrayRemove(T[] array, Field<T> element) {
        return arrayRemove0(val(array), element);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Field<T[]> arrayRemove(Field<T[]> array, Field<T> element) {
        return arrayRemove0(array, element);
    }

    static <T> Field<T[]> arrayRemove0(Field<T[]> array, Field<T> element) {
        return function("array_remove", array.getDataType(), (Field<?>[]) new Field[]{array, element});
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Field<T[]> arrayReplace(T[] array, T search, T replace) {
        return arrayReplace0(val(array), val(search), val(replace));
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Field<T[]> arrayReplace(T[] array, Field<T> search, Field<T> replace) {
        return arrayReplace0(val(array), search, replace);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Field<T[]> arrayReplace(Field<T[]> array, T search, T replace) {
        return arrayReplace0(nullSafe(array), val(search), val(replace));
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated
    @NotNull
    public static <T> Field<T[]> arrayReplace(Field<T[]> array, Field<T> search, Field<T> replace) {
        return arrayReplace0(array, search, replace);
    }

    static <T> Field<T[]> arrayReplace0(Field<T[]> array, Field<T> search, Field<T> replace) {
        return function("array_replace", array.getDataType(), (Field<?>[]) new Field[]{array, search, replace});
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayFill(T value, Integer[] dimensions) {
        return arrayFill((Field) val(value), (Field<Integer[]>) val(dimensions));
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayFill(Field<T> value, Integer[] dimensions) {
        return arrayFill(nullSafe(value), (Field<Integer[]>) val(dimensions));
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayFill(T value, Field<Integer[]> dimensions) {
        return arrayFill((Field) val(value), dimensions);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayFill(Field<T> value, Field<Integer[]> dimensions) {
        return function("array_fill", nullSafeDataType(value).getArrayDataType(), (Field<?>[]) new Field[]{value, dimensions});
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayFill(T value, Integer[] dimensions, Integer[] bounds) {
        return arrayFill((Field) val(value), (Field<Integer[]>) val(dimensions), (Field<Integer[]>) val(bounds));
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayFill(Field<T> value, Integer[] dimensions, Integer[] bounds) {
        return arrayFill(nullSafe(value), (Field<Integer[]>) val(dimensions), (Field<Integer[]>) val(bounds));
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayFill(T value, Field<Integer[]> dimensions, Field<Integer[]> bounds) {
        return arrayFill((Field) val(value), dimensions, bounds);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static <T> Field<T[]> arrayFill(Field<T> value, Field<Integer[]> dimensions, Field<Integer[]> bounds) {
        return function("array_fill", nullSafeDataType(value).getArrayDataType(), (Field<?>[]) new Field[]{value, dimensions, bounds});
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<Integer> arrayLength(Object[] array) {
        return arrayLength(val(array));
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<Integer> arrayLength(Field<? extends Object[]> array) {
        return field("{array_length}({0}, 1)", (DataType) SQLDataType.INTEGER, array);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> arrayToString(Object[] array, String delimiter) {
        return arrayToString(val(array), val(delimiter, String.class));
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> arrayToString(Object[] array, Field<String> delimiter) {
        return arrayToString(val(array), delimiter);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> arrayToString(Field<? extends Object[]> array, String delimiter) {
        return arrayToString(array, val(delimiter, String.class));
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String> arrayToString(Field<? extends Object[]> array, Field<String> delimiter) {
        return function("array_to_string", SQLDataType.VARCHAR, (Field<?>[]) new Field[]{array, delimiter});
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String[]> stringToArray(String string, String delimiter) {
        return stringToArray(val(string, String.class), val(delimiter, String.class));
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String[]> stringToArray(String string, Field<String> delimiter) {
        return stringToArray(val(string, String.class), delimiter);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String[]> stringToArray(Field<String> string, String delimiter) {
        return stringToArray(string, val(delimiter, String.class));
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String[]> stringToArray(Field<String> string, Field<String> delimiter) {
        return function("string_to_array", SQLDataType.VARCHAR.getArrayDataType(), (Field<?>[]) new Field[]{string, delimiter});
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String[]> stringToArray(String string, String delimiter, String nullString) {
        return stringToArray(val(string, String.class), val(delimiter, String.class), val(nullString, String.class));
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String[]> stringToArray(String string, Field<String> delimiter, Field<String> nullString) {
        return stringToArray(val(string, String.class), delimiter, nullString);
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String[]> stringToArray(Field<String> string, String delimiter, String nullString) {
        return stringToArray(string, val(delimiter, String.class), val(nullString, String.class));
    }

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    public static Field<String[]> stringToArray(Field<String> string, Field<String> delimiter, Field<String> nullString) {
        return function("string_to_array", SQLDataType.VARCHAR.getArrayDataType(), (Field<?>[]) new Field[]{string, delimiter, nullString});
    }

    @Support({SQLDialect.POSTGRES})
    @NotNull
    public static Table<Record> only(Table<?> table) {
        return table("{only} {0}", table);
    }

    @Support({SQLDialect.POSTGRES})
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_16)
    @NotNull
    public static Field<Long> oid(Table<?> table) {
        return field("{0}.oid", Long.class, table);
    }
}
