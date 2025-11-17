package org.jooq;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.DataTypeException;
import org.jooq.exception.InvalidResultException;
import org.jooq.exception.MappingException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Result.class */
public interface Result<R extends Record> extends Fields, List<R>, Attachable, Formattable {
    @NotNull
    RecordType<R> recordType();

    <T> T getValue(int i, Field<T> field) throws IndexOutOfBoundsException, IllegalArgumentException;

    @Nullable
    Object getValue(int i, int i2) throws IndexOutOfBoundsException, IllegalArgumentException;

    @Nullable
    Object getValue(int i, String str) throws IndexOutOfBoundsException, IllegalArgumentException;

    @NotNull
    <T> List<T> getValues(Field<T> field) throws IllegalArgumentException;

    @NotNull
    <U> List<U> getValues(Field<?> field, Class<? extends U> cls) throws IllegalArgumentException;

    @NotNull
    <T, U> List<U> getValues(Field<T> field, Converter<? super T, ? extends U> converter) throws IllegalArgumentException;

    @NotNull
    List<?> getValues(int i) throws IllegalArgumentException;

    @NotNull
    <U> List<U> getValues(int i, Class<? extends U> cls) throws IllegalArgumentException, DataTypeException;

    @NotNull
    <U> List<U> getValues(int i, Converter<?, ? extends U> converter) throws IllegalArgumentException, DataTypeException;

    @NotNull
    List<?> getValues(String str) throws IllegalArgumentException;

    @NotNull
    <U> List<U> getValues(String str, Class<? extends U> cls) throws IllegalArgumentException, DataTypeException;

    @NotNull
    <U> List<U> getValues(String str, Converter<?, ? extends U> converter) throws IllegalArgumentException, DataTypeException;

    @NotNull
    List<?> getValues(Name name) throws IllegalArgumentException;

    @NotNull
    <U> List<U> getValues(Name name, Class<? extends U> cls) throws IllegalArgumentException, DataTypeException;

    @NotNull
    <U> List<U> getValues(Name name, Converter<?, ? extends U> converter) throws IllegalArgumentException, DataTypeException;

    @Override // java.util.List, java.util.Collection
    boolean isEmpty();

    boolean isNotEmpty();

    <X, A> X collect(Collector<? super R, A, X> collector);

    @NotNull
    List<Map<String, Object>> intoMaps();

    @NotNull
    <K> Map<K, R> intoMap(Field<K> field) throws IllegalArgumentException, InvalidResultException;

    @NotNull
    Map<?, R> intoMap(int i) throws IllegalArgumentException, InvalidResultException;

    @NotNull
    Map<?, R> intoMap(String str) throws IllegalArgumentException, InvalidResultException;

    @NotNull
    Map<?, R> intoMap(Name name) throws IllegalArgumentException, InvalidResultException;

    @NotNull
    <K, V> Map<K, V> intoMap(Field<K> field, Field<V> field2) throws IllegalArgumentException, InvalidResultException;

    @NotNull
    Map<?, ?> intoMap(int i, int i2) throws IllegalArgumentException, InvalidResultException;

    @NotNull
    Map<?, ?> intoMap(String str, String str2) throws IllegalArgumentException, InvalidResultException;

    @NotNull
    Map<?, ?> intoMap(Name name, Name name2) throws IllegalArgumentException, InvalidResultException;

    @NotNull
    <K, E> Map<K, E> intoMap(Field<K> field, Class<? extends E> cls) throws IllegalArgumentException, InvalidResultException, MappingException;

    @NotNull
    <E> Map<?, E> intoMap(int i, Class<? extends E> cls) throws IllegalArgumentException, InvalidResultException, MappingException;

    @NotNull
    <E> Map<?, E> intoMap(String str, Class<? extends E> cls) throws IllegalArgumentException, InvalidResultException, MappingException;

    @NotNull
    <E> Map<?, E> intoMap(Name name, Class<? extends E> cls) throws IllegalArgumentException, InvalidResultException, MappingException;

    @NotNull
    <K, E> Map<K, E> intoMap(Field<K> field, RecordMapper<? super R, E> recordMapper) throws IllegalArgumentException, InvalidResultException, MappingException;

    @NotNull
    <E> Map<?, E> intoMap(int i, RecordMapper<? super R, E> recordMapper) throws IllegalArgumentException, InvalidResultException, MappingException;

    @NotNull
    <E> Map<?, E> intoMap(String str, RecordMapper<? super R, E> recordMapper) throws IllegalArgumentException, InvalidResultException, MappingException;

    @NotNull
    <E> Map<?, E> intoMap(Name name, RecordMapper<? super R, E> recordMapper) throws IllegalArgumentException, InvalidResultException, MappingException;

    @NotNull
    Map<Record, R> intoMap(Field<?>[] fieldArr) throws IllegalArgumentException, InvalidResultException;

    @NotNull
    Map<Record, R> intoMap(int[] iArr) throws IllegalArgumentException, InvalidResultException;

    @NotNull
    Map<Record, R> intoMap(String[] strArr) throws IllegalArgumentException, InvalidResultException;

    @NotNull
    Map<Record, R> intoMap(Name[] nameArr) throws IllegalArgumentException, InvalidResultException;

    @NotNull
    Map<Record, Record> intoMap(Field<?>[] fieldArr, Field<?>[] fieldArr2) throws IllegalArgumentException, InvalidResultException;

    @NotNull
    Map<Record, Record> intoMap(int[] iArr, int[] iArr2) throws IllegalArgumentException, InvalidResultException;

    @NotNull
    Map<Record, Record> intoMap(String[] strArr, String[] strArr2) throws IllegalArgumentException, InvalidResultException;

    @NotNull
    Map<Record, Record> intoMap(Name[] nameArr, Name[] nameArr2) throws IllegalArgumentException, InvalidResultException;

    @NotNull
    <E> Map<List<?>, E> intoMap(Field<?>[] fieldArr, Class<? extends E> cls) throws IllegalArgumentException, InvalidResultException, MappingException;

    @NotNull
    <E> Map<List<?>, E> intoMap(int[] iArr, Class<? extends E> cls) throws IllegalArgumentException, InvalidResultException, MappingException;

    @NotNull
    <E> Map<List<?>, E> intoMap(String[] strArr, Class<? extends E> cls) throws IllegalArgumentException, InvalidResultException, MappingException;

    @NotNull
    <E> Map<List<?>, E> intoMap(Name[] nameArr, Class<? extends E> cls) throws IllegalArgumentException, InvalidResultException, MappingException;

    @NotNull
    <E> Map<List<?>, E> intoMap(Field<?>[] fieldArr, RecordMapper<? super R, E> recordMapper) throws IllegalArgumentException, InvalidResultException, MappingException;

    @NotNull
    <E> Map<List<?>, E> intoMap(int[] iArr, RecordMapper<? super R, E> recordMapper) throws IllegalArgumentException, InvalidResultException, MappingException;

    @NotNull
    <E> Map<List<?>, E> intoMap(String[] strArr, RecordMapper<? super R, E> recordMapper) throws IllegalArgumentException, InvalidResultException, MappingException;

    @NotNull
    <E> Map<List<?>, E> intoMap(Name[] nameArr, RecordMapper<? super R, E> recordMapper) throws IllegalArgumentException, InvalidResultException, MappingException;

    @NotNull
    <K> Map<K, R> intoMap(Class<? extends K> cls) throws MappingException, InvalidResultException;

    @NotNull
    <K, V> Map<K, V> intoMap(Class<? extends K> cls, Class<? extends V> cls2) throws MappingException, InvalidResultException;

    @NotNull
    <K, V> Map<K, V> intoMap(Class<? extends K> cls, RecordMapper<? super R, V> recordMapper) throws InvalidResultException, MappingException;

    @NotNull
    <K> Map<K, R> intoMap(RecordMapper<? super R, K> recordMapper) throws InvalidResultException, MappingException;

    @NotNull
    <K, V> Map<K, V> intoMap(RecordMapper<? super R, K> recordMapper, Class<V> cls) throws InvalidResultException, MappingException;

    @NotNull
    <K, V> Map<K, V> intoMap(RecordMapper<? super R, K> recordMapper, RecordMapper<? super R, V> recordMapper2) throws InvalidResultException, MappingException;

    @NotNull
    <S extends Record> Map<S, R> intoMap(Table<S> table) throws IllegalArgumentException, InvalidResultException;

    @NotNull
    <S extends Record, T extends Record> Map<S, T> intoMap(Table<S> table, Table<T> table2) throws IllegalArgumentException, InvalidResultException;

    @NotNull
    <E, S extends Record> Map<S, E> intoMap(Table<S> table, Class<? extends E> cls) throws IllegalArgumentException, InvalidResultException, MappingException;

    @NotNull
    <E, S extends Record> Map<S, E> intoMap(Table<S> table, RecordMapper<? super R, E> recordMapper) throws IllegalArgumentException, InvalidResultException, MappingException;

    @NotNull
    <K> Map<K, Result<R>> intoGroups(Field<K> field) throws IllegalArgumentException;

    @NotNull
    Map<?, Result<R>> intoGroups(int i) throws IllegalArgumentException;

    @NotNull
    Map<?, Result<R>> intoGroups(String str) throws IllegalArgumentException;

    @NotNull
    Map<?, Result<R>> intoGroups(Name name) throws IllegalArgumentException;

    @NotNull
    <K, V> Map<K, List<V>> intoGroups(Field<K> field, Field<V> field2) throws IllegalArgumentException;

    @NotNull
    Map<?, List<?>> intoGroups(int i, int i2) throws IllegalArgumentException;

    @NotNull
    Map<?, List<?>> intoGroups(String str, String str2) throws IllegalArgumentException;

    @NotNull
    Map<?, List<?>> intoGroups(Name name, Name name2) throws IllegalArgumentException;

    @NotNull
    <K, E> Map<K, List<E>> intoGroups(Field<K> field, Class<? extends E> cls) throws IllegalArgumentException, MappingException;

    @NotNull
    <E> Map<?, List<E>> intoGroups(int i, Class<? extends E> cls) throws IllegalArgumentException, MappingException;

    @NotNull
    <E> Map<?, List<E>> intoGroups(String str, Class<? extends E> cls) throws IllegalArgumentException, MappingException;

    @NotNull
    <E> Map<?, List<E>> intoGroups(Name name, Class<? extends E> cls) throws IllegalArgumentException, MappingException;

    @NotNull
    <K, E> Map<K, List<E>> intoGroups(Field<K> field, RecordMapper<? super R, E> recordMapper) throws IllegalArgumentException, MappingException;

    @NotNull
    <E> Map<?, List<E>> intoGroups(int i, RecordMapper<? super R, E> recordMapper) throws IllegalArgumentException, MappingException;

    @NotNull
    <E> Map<?, List<E>> intoGroups(String str, RecordMapper<? super R, E> recordMapper) throws IllegalArgumentException, MappingException;

    @NotNull
    <E> Map<?, List<E>> intoGroups(Name name, RecordMapper<? super R, E> recordMapper) throws IllegalArgumentException, MappingException;

    @NotNull
    Map<Record, Result<R>> intoGroups(Field<?>[] fieldArr) throws IllegalArgumentException;

    @NotNull
    Map<Record, Result<R>> intoGroups(int[] iArr) throws IllegalArgumentException;

    @NotNull
    Map<Record, Result<R>> intoGroups(String[] strArr) throws IllegalArgumentException;

    @NotNull
    Map<Record, Result<R>> intoGroups(Name[] nameArr) throws IllegalArgumentException;

    @NotNull
    Map<Record, Result<Record>> intoGroups(Field<?>[] fieldArr, Field<?>[] fieldArr2) throws IllegalArgumentException;

    @NotNull
    Map<Record, Result<Record>> intoGroups(int[] iArr, int[] iArr2) throws IllegalArgumentException;

    @NotNull
    Map<Record, Result<Record>> intoGroups(String[] strArr, String[] strArr2) throws IllegalArgumentException;

    @NotNull
    Map<Record, Result<Record>> intoGroups(Name[] nameArr, Name[] nameArr2) throws IllegalArgumentException;

    @NotNull
    <E> Map<Record, List<E>> intoGroups(Field<?>[] fieldArr, Class<? extends E> cls) throws IllegalArgumentException, MappingException;

    @NotNull
    <E> Map<Record, List<E>> intoGroups(int[] iArr, Class<? extends E> cls) throws IllegalArgumentException, MappingException;

    @NotNull
    <E> Map<Record, List<E>> intoGroups(String[] strArr, Class<? extends E> cls) throws IllegalArgumentException, MappingException;

    @NotNull
    <E> Map<Record, List<E>> intoGroups(Name[] nameArr, Class<? extends E> cls) throws IllegalArgumentException, MappingException;

    @NotNull
    <E> Map<Record, List<E>> intoGroups(Field<?>[] fieldArr, RecordMapper<? super R, E> recordMapper) throws IllegalArgumentException, MappingException;

    @NotNull
    <E> Map<Record, List<E>> intoGroups(int[] iArr, RecordMapper<? super R, E> recordMapper) throws IllegalArgumentException, MappingException;

    @NotNull
    <E> Map<Record, List<E>> intoGroups(String[] strArr, RecordMapper<? super R, E> recordMapper) throws IllegalArgumentException, MappingException;

    @NotNull
    <E> Map<Record, List<E>> intoGroups(Name[] nameArr, RecordMapper<? super R, E> recordMapper) throws IllegalArgumentException, MappingException;

    @NotNull
    <K> Map<K, Result<R>> intoGroups(Class<? extends K> cls) throws MappingException;

    @NotNull
    <K, V> Map<K, List<V>> intoGroups(Class<? extends K> cls, Class<? extends V> cls2) throws MappingException;

    @NotNull
    <K, V> Map<K, List<V>> intoGroups(Class<? extends K> cls, RecordMapper<? super R, V> recordMapper) throws MappingException;

    @NotNull
    <K> Map<K, Result<R>> intoGroups(RecordMapper<? super R, K> recordMapper) throws MappingException;

    @NotNull
    <K, V> Map<K, List<V>> intoGroups(RecordMapper<? super R, K> recordMapper, Class<V> cls) throws MappingException;

    @NotNull
    <K, V> Map<K, List<V>> intoGroups(RecordMapper<? super R, K> recordMapper, RecordMapper<? super R, V> recordMapper2) throws MappingException;

    @NotNull
    <S extends Record> Map<S, Result<R>> intoGroups(Table<S> table) throws IllegalArgumentException;

    @NotNull
    <S extends Record, T extends Record> Map<S, Result<T>> intoGroups(Table<S> table, Table<T> table2) throws IllegalArgumentException;

    @NotNull
    <E, S extends Record> Map<S, List<E>> intoGroups(Table<S> table, Class<? extends E> cls) throws IllegalArgumentException, MappingException;

    @NotNull
    <E, S extends Record> Map<S, List<E>> intoGroups(Table<S> table, RecordMapper<? super R, E> recordMapper) throws IllegalArgumentException, MappingException;

    @Nullable
    Object[][] intoArrays();

    @Nullable
    Object[] intoArray(int i) throws IllegalArgumentException;

    <U> U[] intoArray(int i, Class<? extends U> cls) throws IllegalArgumentException, DataTypeException;

    <U> U[] intoArray(int i, Converter<?, ? extends U> converter) throws IllegalArgumentException, DataTypeException;

    @Nullable
    Object[] intoArray(String str) throws IllegalArgumentException;

    <U> U[] intoArray(String str, Class<? extends U> cls) throws IllegalArgumentException, DataTypeException;

    <U> U[] intoArray(String str, Converter<?, ? extends U> converter) throws IllegalArgumentException, DataTypeException;

    @Nullable
    Object[] intoArray(Name name) throws IllegalArgumentException;

    <U> U[] intoArray(Name name, Class<? extends U> cls) throws IllegalArgumentException, DataTypeException;

    <U> U[] intoArray(Name name, Converter<?, ? extends U> converter) throws IllegalArgumentException, DataTypeException;

    <T> T[] intoArray(Field<T> field) throws IllegalArgumentException;

    <U> U[] intoArray(Field<?> field, Class<? extends U> cls) throws IllegalArgumentException, DataTypeException;

    <T, U> U[] intoArray(Field<T> field, Converter<? super T, ? extends U> converter) throws IllegalArgumentException, DataTypeException;

    @NotNull
    <E> Set<E> intoSet(RecordMapper<? super R, E> recordMapper);

    @NotNull
    Set<?> intoSet(int i) throws IllegalArgumentException;

    @NotNull
    <U> Set<U> intoSet(int i, Class<? extends U> cls) throws IllegalArgumentException, DataTypeException;

    @NotNull
    <U> Set<U> intoSet(int i, Converter<?, ? extends U> converter) throws IllegalArgumentException, DataTypeException;

    @NotNull
    Set<?> intoSet(String str) throws IllegalArgumentException;

    @NotNull
    <U> Set<U> intoSet(String str, Class<? extends U> cls) throws IllegalArgumentException, DataTypeException;

    @NotNull
    <U> Set<U> intoSet(String str, Converter<?, ? extends U> converter) throws IllegalArgumentException, DataTypeException;

    @NotNull
    Set<?> intoSet(Name name) throws IllegalArgumentException;

    @NotNull
    <U> Set<U> intoSet(Name name, Class<? extends U> cls) throws IllegalArgumentException, DataTypeException;

    @NotNull
    <U> Set<U> intoSet(Name name, Converter<?, ? extends U> converter) throws IllegalArgumentException, DataTypeException;

    @NotNull
    <T> Set<T> intoSet(Field<T> field) throws IllegalArgumentException;

    @NotNull
    <U> Set<U> intoSet(Field<?> field, Class<? extends U> cls) throws IllegalArgumentException, DataTypeException;

    @NotNull
    <T, U> Set<U> intoSet(Field<T> field, Converter<? super T, ? extends U> converter) throws IllegalArgumentException, DataTypeException;

    @NotNull
    Result<Record> into(Field<?>... fieldArr);

    @NotNull
    <T1> Result<Record1<T1>> into(Field<T1> field);

    @NotNull
    <T1, T2> Result<Record2<T1, T2>> into(Field<T1> field, Field<T2> field2);

    @NotNull
    <T1, T2, T3> Result<Record3<T1, T2, T3>> into(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @NotNull
    <T1, T2, T3, T4> Result<Record4<T1, T2, T3, T4>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @NotNull
    <T1, T2, T3, T4, T5> Result<Record5<T1, T2, T3, T4, T5>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @NotNull
    <T1, T2, T3, T4, T5, T6> Result<Record6<T1, T2, T3, T4, T5, T6>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7> Result<Record7<T1, T2, T3, T4, T5, T6, T7>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8> Result<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9> Result<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Result<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Result<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Result<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Result<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Result<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Result<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Result<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Result<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Result<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Result<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Result<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Result<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Result<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> into(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22);

    @NotNull
    <E> List<E> into(Class<? extends E> cls) throws MappingException;

    @NotNull
    <Z extends Record> Result<Z> into(Table<Z> table) throws MappingException;

    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    <H extends RecordHandler<? super R>> H into(H h);

    @NotNull
    ResultSet intoResultSet();

    @NotNull
    <E> List<E> map(RecordMapper<? super R, E> recordMapper);

    @NotNull
    <T extends Comparable<? super T>> Result<R> sortAsc(Field<T> field) throws IllegalArgumentException;

    @NotNull
    <T extends Comparable<? super T>> Result<R> sortDesc(Field<T> field) throws IllegalArgumentException;

    @NotNull
    Result<R> sortAsc(int i) throws IllegalArgumentException;

    @NotNull
    Result<R> sortDesc(int i) throws IllegalArgumentException;

    @NotNull
    Result<R> sortAsc(String str) throws IllegalArgumentException;

    @NotNull
    Result<R> sortDesc(String str) throws IllegalArgumentException;

    @NotNull
    Result<R> sortAsc(Name name) throws IllegalArgumentException;

    @NotNull
    Result<R> sortDesc(Name name) throws IllegalArgumentException;

    @NotNull
    <T> Result<R> sortAsc(Field<T> field, java.util.Comparator<? super T> comparator) throws IllegalArgumentException;

    @NotNull
    <T> Result<R> sortDesc(Field<T> field, java.util.Comparator<? super T> comparator) throws IllegalArgumentException;

    @NotNull
    Result<R> sortAsc(int i, java.util.Comparator<?> comparator) throws IllegalArgumentException;

    @NotNull
    Result<R> sortDesc(int i, java.util.Comparator<?> comparator) throws IllegalArgumentException;

    @NotNull
    Result<R> sortAsc(String str, java.util.Comparator<?> comparator) throws IllegalArgumentException;

    @NotNull
    Result<R> sortDesc(String str, java.util.Comparator<?> comparator) throws IllegalArgumentException;

    @NotNull
    Result<R> sortAsc(Name name, java.util.Comparator<?> comparator) throws IllegalArgumentException;

    @NotNull
    Result<R> sortDesc(Name name, java.util.Comparator<?> comparator) throws IllegalArgumentException;

    @NotNull
    Result<R> sortAsc(java.util.Comparator<? super R> comparator);

    @NotNull
    Result<R> sortDesc(java.util.Comparator<? super R> comparator);

    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    Result<R> intern(Field<?>... fieldArr);

    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    Result<R> intern(int... iArr);

    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    Result<R> intern(String... strArr);

    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    Result<R> intern(Name... nameArr);

    @Blocking
    @NotNull
    <O extends UpdatableRecord<O>> Result<O> fetchParents(ForeignKey<R, O> foreignKey) throws DataAccessException;

    @Blocking
    @NotNull
    <O extends TableRecord<O>> Result<O> fetchChildren(ForeignKey<O, R> foreignKey) throws DataAccessException;

    @NotNull
    <O extends UpdatableRecord<O>> Table<O> parents(ForeignKey<R, O> foreignKey);

    @NotNull
    <O extends TableRecord<O>> Table<O> children(ForeignKey<O, R> foreignKey);

    @Override // org.jooq.Attachable
    void attach(Configuration configuration);

    @Override // org.jooq.Attachable
    void detach();
}
