package org.jooq;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Stream;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.DataTypeException;
import org.jooq.exception.InvalidResultException;
import org.jooq.exception.MappingException;
import org.jooq.exception.NoDataFoundException;
import org.jooq.exception.TooManyRowsException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ResultQuery.class */
public interface ResultQuery<R extends Record> extends Fields, Query, Iterable<R>, Publisher<R> {
    @Blocking
    @NotNull
    Result<R> fetch() throws DataAccessException;

    @Blocking
    @NotNull
    ResultSet fetchResultSet() throws DataAccessException;

    @Override // java.lang.Iterable
    @Blocking
    @NotNull
    Iterator<R> iterator() throws DataAccessException;

    @Blocking
    @NotNull
    Stream<R> fetchStream() throws DataAccessException;

    @Blocking
    @NotNull
    <E> Stream<E> fetchStreamInto(Class<? extends E> cls) throws DataAccessException, MappingException;

    @Blocking
    @NotNull
    <Z extends Record> Stream<Z> fetchStreamInto(Table<Z> table) throws DataAccessException;

    @Blocking
    @NotNull
    Stream<R> stream() throws DataAccessException;

    @Blocking
    <X, A> X collect(Collector<? super R, A, X> collector) throws DataAccessException;

    @Blocking
    @NotNull
    Cursor<R> fetchLazy() throws DataAccessException;

    @Blocking
    @NotNull
    Results fetchMany() throws DataAccessException;

    @Blocking
    @NotNull
    <T> List<T> fetch(Field<T> field) throws DataAccessException;

    @Blocking
    @NotNull
    <U> List<U> fetch(Field<?> field, Class<? extends U> cls) throws DataAccessException;

    @Blocking
    @NotNull
    <T, U> List<U> fetch(Field<T> field, Converter<? super T, ? extends U> converter) throws DataAccessException;

    @Blocking
    @NotNull
    List<?> fetch(int i) throws DataAccessException;

    @Blocking
    @NotNull
    <U> List<U> fetch(int i, Class<? extends U> cls) throws DataAccessException;

    @Blocking
    @NotNull
    <U> List<U> fetch(int i, Converter<?, ? extends U> converter) throws DataAccessException;

    @Blocking
    @NotNull
    List<?> fetch(String str) throws DataAccessException;

    @Blocking
    @NotNull
    <U> List<U> fetch(String str, Class<? extends U> cls) throws DataAccessException;

    @Blocking
    @NotNull
    <U> List<U> fetch(String str, Converter<?, ? extends U> converter) throws DataAccessException;

    @Blocking
    @NotNull
    List<?> fetch(Name name) throws DataAccessException;

    @Blocking
    @NotNull
    <U> List<U> fetch(Name name, Class<? extends U> cls) throws DataAccessException;

    @Blocking
    @NotNull
    <U> List<U> fetch(Name name, Converter<?, ? extends U> converter) throws DataAccessException;

    @Blocking
    @Nullable
    <T> T fetchOne(Field<T> field) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Nullable
    <U> U fetchOne(Field<?> field, Class<? extends U> cls) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Nullable
    <T, U> U fetchOne(Field<T> field, Converter<? super T, ? extends U> converter) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Nullable
    Object fetchOne(int i) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Nullable
    <U> U fetchOne(int i, Class<? extends U> cls) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Nullable
    <U> U fetchOne(int i, Converter<?, ? extends U> converter) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Nullable
    Object fetchOne(String str) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Nullable
    <U> U fetchOne(String str, Class<? extends U> cls) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Nullable
    <U> U fetchOne(String str, Converter<?, ? extends U> converter) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Nullable
    Object fetchOne(Name name) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Nullable
    <U> U fetchOne(Name name, Class<? extends U> cls) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Nullable
    <U> U fetchOne(Name name, Converter<?, ? extends U> converter) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Nullable
    R fetchOne() throws DataAccessException, TooManyRowsException;

    @Blocking
    @Nullable
    <E> E fetchOne(RecordMapper<? super R, E> recordMapper) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Nullable
    Map<String, Object> fetchOneMap() throws DataAccessException, TooManyRowsException;

    @Blocking
    @Nullable
    Object[] fetchOneArray() throws DataAccessException, TooManyRowsException;

    @Blocking
    @Nullable
    <E> E fetchOneInto(Class<? extends E> cls) throws DataAccessException, MappingException, TooManyRowsException;

    @Blocking
    @Nullable
    <Z extends Record> Z fetchOneInto(Table<Z> table) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Nullable
    <T> T fetchSingle(Field<T> field) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Nullable
    <U> U fetchSingle(Field<?> field, Class<? extends U> cls) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Nullable
    <T, U> U fetchSingle(Field<T> field, Converter<? super T, ? extends U> converter) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Nullable
    Object fetchSingle(int i) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Nullable
    <U> U fetchSingle(int i, Class<? extends U> cls) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Nullable
    <U> U fetchSingle(int i, Converter<?, ? extends U> converter) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Nullable
    Object fetchSingle(String str) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Nullable
    <U> U fetchSingle(String str, Class<? extends U> cls) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Nullable
    <U> U fetchSingle(String str, Converter<?, ? extends U> converter) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Nullable
    Object fetchSingle(Name name) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Nullable
    <U> U fetchSingle(Name name, Class<? extends U> cls) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Nullable
    <U> U fetchSingle(Name name, Converter<?, ? extends U> converter) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @NotNull
    R fetchSingle() throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @NotNull
    <E> E fetchSingle(RecordMapper<? super R, E> recordMapper) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @NotNull
    Map<String, Object> fetchSingleMap() throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @Nullable
    Object[] fetchSingleArray() throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    <E> E fetchSingleInto(Class<? extends E> cls) throws DataAccessException, MappingException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @NotNull
    <Z extends Record> Z fetchSingleInto(Table<Z> table) throws DataAccessException, NoDataFoundException, TooManyRowsException;

    @Blocking
    @NotNull
    <T> Optional<T> fetchOptional(Field<T> field) throws DataAccessException, TooManyRowsException;

    @Blocking
    @NotNull
    <U> Optional<U> fetchOptional(Field<?> field, Class<? extends U> cls) throws DataAccessException, TooManyRowsException;

    @Blocking
    @NotNull
    <T, U> Optional<U> fetchOptional(Field<T> field, Converter<? super T, ? extends U> converter) throws DataAccessException, TooManyRowsException;

    @Blocking
    @NotNull
    Optional<?> fetchOptional(int i) throws DataAccessException, TooManyRowsException;

    @Blocking
    @NotNull
    <U> Optional<U> fetchOptional(int i, Class<? extends U> cls) throws DataAccessException, TooManyRowsException;

    @Blocking
    @NotNull
    <U> Optional<U> fetchOptional(int i, Converter<?, ? extends U> converter) throws DataAccessException, TooManyRowsException;

    @Blocking
    @NotNull
    Optional<?> fetchOptional(String str) throws DataAccessException, TooManyRowsException;

    @Blocking
    @NotNull
    <U> Optional<U> fetchOptional(String str, Class<? extends U> cls) throws DataAccessException, TooManyRowsException;

    @Blocking
    @NotNull
    <U> Optional<U> fetchOptional(String str, Converter<?, ? extends U> converter) throws DataAccessException, TooManyRowsException;

    @Blocking
    @NotNull
    Optional<?> fetchOptional(Name name) throws DataAccessException, TooManyRowsException;

    @Blocking
    @NotNull
    <U> Optional<U> fetchOptional(Name name, Class<? extends U> cls) throws DataAccessException, TooManyRowsException;

    @Blocking
    @NotNull
    <U> Optional<U> fetchOptional(Name name, Converter<?, ? extends U> converter) throws DataAccessException, TooManyRowsException;

    @Blocking
    @NotNull
    Optional<R> fetchOptional() throws DataAccessException, TooManyRowsException;

    @Blocking
    @NotNull
    <E> Optional<E> fetchOptional(RecordMapper<? super R, E> recordMapper) throws DataAccessException, TooManyRowsException;

    @Blocking
    @NotNull
    Optional<Map<String, Object>> fetchOptionalMap() throws DataAccessException, TooManyRowsException;

    @Blocking
    @NotNull
    Optional<Object[]> fetchOptionalArray() throws DataAccessException, TooManyRowsException;

    @Blocking
    @NotNull
    <E> Optional<E> fetchOptionalInto(Class<? extends E> cls) throws DataAccessException, MappingException, TooManyRowsException;

    @Blocking
    @NotNull
    <Z extends Record> Optional<Z> fetchOptionalInto(Table<Z> table) throws DataAccessException, TooManyRowsException;

    @Blocking
    @Nullable
    <T> T fetchAny(Field<T> field) throws DataAccessException;

    @Blocking
    @Nullable
    <U> U fetchAny(Field<?> field, Class<? extends U> cls) throws DataAccessException;

    @Blocking
    @Nullable
    <T, U> U fetchAny(Field<T> field, Converter<? super T, ? extends U> converter) throws DataAccessException;

    @Blocking
    @Nullable
    Object fetchAny(int i) throws DataAccessException;

    @Blocking
    @Nullable
    <U> U fetchAny(int i, Class<? extends U> cls) throws DataAccessException;

    @Blocking
    @Nullable
    <U> U fetchAny(int i, Converter<?, ? extends U> converter) throws DataAccessException;

    @Blocking
    @Nullable
    Object fetchAny(String str) throws DataAccessException;

    @Blocking
    @Nullable
    <U> U fetchAny(String str, Class<? extends U> cls) throws DataAccessException;

    @Blocking
    @Nullable
    <U> U fetchAny(String str, Converter<?, ? extends U> converter) throws DataAccessException;

    @Blocking
    @Nullable
    Object fetchAny(Name name) throws DataAccessException;

    @Blocking
    @Nullable
    <U> U fetchAny(Name name, Class<? extends U> cls) throws DataAccessException;

    @Blocking
    @Nullable
    <U> U fetchAny(Name name, Converter<?, ? extends U> converter) throws DataAccessException;

    @Blocking
    @Nullable
    R fetchAny() throws DataAccessException;

    @Blocking
    @Nullable
    <E> E fetchAny(RecordMapper<? super R, E> recordMapper) throws DataAccessException;

    @Blocking
    @Nullable
    Map<String, Object> fetchAnyMap() throws DataAccessException;

    @Blocking
    @Nullable
    Object[] fetchAnyArray() throws DataAccessException;

    @Blocking
    @Nullable
    <E> E fetchAnyInto(Class<? extends E> cls) throws DataAccessException, MappingException;

    @Blocking
    @Nullable
    <Z extends Record> Z fetchAnyInto(Table<Z> table) throws DataAccessException;

    @Blocking
    @NotNull
    List<Map<String, Object>> fetchMaps() throws DataAccessException;

    @Blocking
    @NotNull
    <K> Map<K, R> fetchMap(Field<K> field) throws DataAccessException;

    @Blocking
    @NotNull
    Map<?, R> fetchMap(int i) throws DataAccessException;

    @Blocking
    @NotNull
    Map<?, R> fetchMap(String str) throws DataAccessException;

    @Blocking
    @NotNull
    Map<?, R> fetchMap(Name name) throws DataAccessException;

    @Blocking
    @NotNull
    <K, V> Map<K, V> fetchMap(Field<K> field, Field<V> field2) throws DataAccessException;

    @Blocking
    @NotNull
    Map<?, ?> fetchMap(int i, int i2) throws DataAccessException;

    @Blocking
    @NotNull
    Map<?, ?> fetchMap(String str, String str2) throws DataAccessException;

    @Blocking
    @NotNull
    Map<?, ?> fetchMap(Name name, Name name2) throws DataAccessException;

    @Blocking
    @NotNull
    Map<Record, R> fetchMap(Field<?>[] fieldArr) throws DataAccessException;

    @Blocking
    @NotNull
    Map<Record, R> fetchMap(int[] iArr) throws DataAccessException;

    @Blocking
    @NotNull
    Map<Record, R> fetchMap(String[] strArr) throws DataAccessException;

    @Blocking
    @NotNull
    Map<Record, R> fetchMap(Name[] nameArr) throws DataAccessException;

    @Blocking
    @NotNull
    Map<Record, Record> fetchMap(Field<?>[] fieldArr, Field<?>[] fieldArr2) throws DataAccessException;

    @Blocking
    @NotNull
    Map<Record, Record> fetchMap(int[] iArr, int[] iArr2) throws DataAccessException;

    @Blocking
    @NotNull
    Map<Record, Record> fetchMap(String[] strArr, String[] strArr2) throws DataAccessException;

    @Blocking
    @NotNull
    Map<Record, Record> fetchMap(Name[] nameArr, Name[] nameArr2) throws DataAccessException;

    @Blocking
    @NotNull
    <E> Map<List<?>, E> fetchMap(Field<?>[] fieldArr, Class<? extends E> cls) throws DataAccessException, MappingException;

    @Blocking
    @NotNull
    <E> Map<List<?>, E> fetchMap(int[] iArr, Class<? extends E> cls) throws DataAccessException, MappingException;

    @Blocking
    @NotNull
    <E> Map<List<?>, E> fetchMap(String[] strArr, Class<? extends E> cls) throws DataAccessException, MappingException;

    @Blocking
    @NotNull
    <E> Map<List<?>, E> fetchMap(Name[] nameArr, Class<? extends E> cls) throws DataAccessException, MappingException;

    @Blocking
    @NotNull
    <E> Map<List<?>, E> fetchMap(Field<?>[] fieldArr, RecordMapper<? super R, E> recordMapper) throws DataAccessException, MappingException;

    @Blocking
    @NotNull
    <E> Map<List<?>, E> fetchMap(int[] iArr, RecordMapper<? super R, E> recordMapper) throws DataAccessException, MappingException;

    @Blocking
    @NotNull
    <E> Map<List<?>, E> fetchMap(String[] strArr, RecordMapper<? super R, E> recordMapper) throws DataAccessException, MappingException;

    @Blocking
    @NotNull
    <E> Map<List<?>, E> fetchMap(Name[] nameArr, RecordMapper<? super R, E> recordMapper) throws DataAccessException, MappingException;

    @Blocking
    @NotNull
    <K> Map<K, R> fetchMap(Class<? extends K> cls) throws DataAccessException, MappingException, InvalidResultException;

    @Blocking
    @NotNull
    <K, V> Map<K, V> fetchMap(Class<? extends K> cls, Class<? extends V> cls2) throws DataAccessException, MappingException, InvalidResultException;

    @Blocking
    @NotNull
    <K, V> Map<K, V> fetchMap(Class<? extends K> cls, RecordMapper<? super R, V> recordMapper) throws DataAccessException, InvalidResultException, MappingException;

    @Blocking
    @NotNull
    <K> Map<K, R> fetchMap(RecordMapper<? super R, K> recordMapper) throws DataAccessException, InvalidResultException, MappingException;

    @Blocking
    @NotNull
    <K, V> Map<K, V> fetchMap(RecordMapper<? super R, K> recordMapper, Class<V> cls) throws DataAccessException, InvalidResultException, MappingException;

    @Blocking
    @NotNull
    <K, V> Map<K, V> fetchMap(RecordMapper<? super R, K> recordMapper, RecordMapper<? super R, V> recordMapper2) throws DataAccessException, InvalidResultException, MappingException;

    @Blocking
    @NotNull
    <S extends Record> Map<S, R> fetchMap(Table<S> table) throws DataAccessException;

    @Blocking
    @NotNull
    <S extends Record, T extends Record> Map<S, T> fetchMap(Table<S> table, Table<T> table2) throws DataAccessException;

    @Blocking
    @NotNull
    <E, S extends Record> Map<S, E> fetchMap(Table<S> table, Class<? extends E> cls) throws DataAccessException, MappingException;

    @Blocking
    @NotNull
    <E, S extends Record> Map<S, E> fetchMap(Table<S> table, RecordMapper<? super R, E> recordMapper) throws DataAccessException, MappingException;

    @Blocking
    @NotNull
    <K, E> Map<K, E> fetchMap(Field<K> field, Class<? extends E> cls) throws DataAccessException;

    @Blocking
    @NotNull
    <E> Map<?, E> fetchMap(int i, Class<? extends E> cls) throws DataAccessException;

    @Blocking
    @NotNull
    <E> Map<?, E> fetchMap(String str, Class<? extends E> cls) throws DataAccessException;

    @Blocking
    @NotNull
    <E> Map<?, E> fetchMap(Name name, Class<? extends E> cls) throws DataAccessException;

    @Blocking
    @NotNull
    <K, E> Map<K, E> fetchMap(Field<K> field, RecordMapper<? super R, E> recordMapper) throws DataAccessException;

    @Blocking
    @NotNull
    <E> Map<?, E> fetchMap(int i, RecordMapper<? super R, E> recordMapper) throws DataAccessException;

    @Blocking
    @NotNull
    <E> Map<?, E> fetchMap(String str, RecordMapper<? super R, E> recordMapper) throws DataAccessException;

    @Blocking
    @NotNull
    <E> Map<?, E> fetchMap(Name name, RecordMapper<? super R, E> recordMapper) throws DataAccessException;

    @Blocking
    @NotNull
    <K> Map<K, Result<R>> fetchGroups(Field<K> field) throws DataAccessException;

    @Blocking
    @NotNull
    Map<?, Result<R>> fetchGroups(int i) throws DataAccessException;

    @Blocking
    @NotNull
    Map<?, Result<R>> fetchGroups(String str) throws DataAccessException;

    @Blocking
    @NotNull
    Map<?, Result<R>> fetchGroups(Name name) throws DataAccessException;

    @Blocking
    @NotNull
    <K, V> Map<K, List<V>> fetchGroups(Field<K> field, Field<V> field2) throws DataAccessException;

    @Blocking
    @NotNull
    Map<?, List<?>> fetchGroups(int i, int i2) throws DataAccessException;

    @Blocking
    @NotNull
    Map<?, List<?>> fetchGroups(String str, String str2) throws DataAccessException;

    @Blocking
    @NotNull
    Map<?, List<?>> fetchGroups(Name name, Name name2) throws DataAccessException;

    @Blocking
    @NotNull
    Map<Record, Result<R>> fetchGroups(Field<?>[] fieldArr) throws DataAccessException;

    @Blocking
    @NotNull
    Map<Record, Result<R>> fetchGroups(int[] iArr) throws DataAccessException;

    @Blocking
    @NotNull
    Map<Record, Result<R>> fetchGroups(String[] strArr) throws DataAccessException;

    @Blocking
    @NotNull
    Map<Record, Result<R>> fetchGroups(Name[] nameArr) throws DataAccessException;

    @Blocking
    @NotNull
    Map<Record, Result<Record>> fetchGroups(Field<?>[] fieldArr, Field<?>[] fieldArr2) throws DataAccessException;

    @Blocking
    @NotNull
    Map<Record, Result<Record>> fetchGroups(int[] iArr, int[] iArr2) throws DataAccessException;

    @Blocking
    @NotNull
    Map<Record, Result<Record>> fetchGroups(String[] strArr, String[] strArr2) throws DataAccessException;

    @Blocking
    @NotNull
    Map<Record, Result<Record>> fetchGroups(Name[] nameArr, Name[] nameArr2) throws DataAccessException;

    @Blocking
    @NotNull
    <E> Map<Record, List<E>> fetchGroups(Field<?>[] fieldArr, Class<? extends E> cls) throws MappingException;

    @Blocking
    @NotNull
    <E> Map<Record, List<E>> fetchGroups(int[] iArr, Class<? extends E> cls) throws MappingException;

    @Blocking
    @NotNull
    <E> Map<Record, List<E>> fetchGroups(String[] strArr, Class<? extends E> cls) throws MappingException;

    @Blocking
    @NotNull
    <E> Map<Record, List<E>> fetchGroups(Name[] nameArr, Class<? extends E> cls) throws MappingException;

    @Blocking
    @NotNull
    <E> Map<Record, List<E>> fetchGroups(Field<?>[] fieldArr, RecordMapper<? super R, E> recordMapper) throws MappingException;

    @Blocking
    @NotNull
    <E> Map<Record, List<E>> fetchGroups(int[] iArr, RecordMapper<? super R, E> recordMapper) throws MappingException;

    @Blocking
    @NotNull
    <E> Map<Record, List<E>> fetchGroups(String[] strArr, RecordMapper<? super R, E> recordMapper) throws MappingException;

    @Blocking
    @NotNull
    <E> Map<Record, List<E>> fetchGroups(Name[] nameArr, RecordMapper<? super R, E> recordMapper) throws MappingException;

    @Blocking
    @NotNull
    <K> Map<K, Result<R>> fetchGroups(Class<? extends K> cls) throws MappingException;

    @Blocking
    @NotNull
    <K, V> Map<K, List<V>> fetchGroups(Class<? extends K> cls, Class<? extends V> cls2) throws MappingException;

    @Blocking
    @NotNull
    <K, V> Map<K, List<V>> fetchGroups(Class<? extends K> cls, RecordMapper<? super R, V> recordMapper) throws MappingException;

    @Blocking
    @NotNull
    <K> Map<K, Result<R>> fetchGroups(RecordMapper<? super R, K> recordMapper) throws MappingException;

    @Blocking
    @NotNull
    <K, V> Map<K, List<V>> fetchGroups(RecordMapper<? super R, K> recordMapper, Class<V> cls) throws MappingException;

    @Blocking
    @NotNull
    <K, V> Map<K, List<V>> fetchGroups(RecordMapper<? super R, K> recordMapper, RecordMapper<? super R, V> recordMapper2) throws MappingException;

    @Blocking
    @NotNull
    <S extends Record> Map<S, Result<R>> fetchGroups(Table<S> table) throws DataAccessException;

    @Blocking
    @NotNull
    <S extends Record, T extends Record> Map<S, Result<T>> fetchGroups(Table<S> table, Table<T> table2) throws DataAccessException;

    @Blocking
    @NotNull
    <E, S extends Record> Map<S, List<E>> fetchGroups(Table<S> table, Class<? extends E> cls) throws DataAccessException, MappingException;

    @Blocking
    @NotNull
    <E, S extends Record> Map<S, List<E>> fetchGroups(Table<S> table, RecordMapper<? super R, E> recordMapper) throws DataAccessException, MappingException;

    @Blocking
    @NotNull
    <K, E> Map<K, List<E>> fetchGroups(Field<K> field, Class<? extends E> cls) throws DataAccessException, MappingException;

    @Blocking
    @NotNull
    <E> Map<?, List<E>> fetchGroups(int i, Class<? extends E> cls) throws DataAccessException, MappingException;

    @Blocking
    @NotNull
    <E> Map<?, List<E>> fetchGroups(String str, Class<? extends E> cls) throws DataAccessException, MappingException;

    @Blocking
    @NotNull
    <E> Map<?, List<E>> fetchGroups(Name name, Class<? extends E> cls) throws DataAccessException, MappingException;

    @Blocking
    @NotNull
    <K, E> Map<K, List<E>> fetchGroups(Field<K> field, RecordMapper<? super R, E> recordMapper) throws DataAccessException, MappingException;

    @Blocking
    @NotNull
    <E> Map<?, List<E>> fetchGroups(int i, RecordMapper<? super R, E> recordMapper) throws DataAccessException, MappingException;

    @Blocking
    @NotNull
    <E> Map<?, List<E>> fetchGroups(String str, RecordMapper<? super R, E> recordMapper) throws DataAccessException, MappingException;

    @Blocking
    @NotNull
    <E> Map<?, List<E>> fetchGroups(Name name, RecordMapper<? super R, E> recordMapper) throws DataAccessException, MappingException;

    @Blocking
    @Nullable
    Object[][] fetchArrays() throws DataAccessException;

    @Blocking
    @NotNull
    R[] fetchArray() throws DataAccessException;

    @Blocking
    @Nullable
    Object[] fetchArray(int i) throws DataAccessException;

    @Blocking
    <U> U[] fetchArray(int i, Class<? extends U> cls) throws DataAccessException;

    @Blocking
    <U> U[] fetchArray(int i, Converter<?, ? extends U> converter) throws DataAccessException;

    @Blocking
    @Nullable
    Object[] fetchArray(String str) throws DataAccessException;

    @Blocking
    <U> U[] fetchArray(String str, Class<? extends U> cls) throws DataAccessException;

    @Blocking
    <U> U[] fetchArray(String str, Converter<?, ? extends U> converter) throws DataAccessException;

    @Blocking
    @Nullable
    Object[] fetchArray(Name name) throws DataAccessException;

    @Blocking
    <U> U[] fetchArray(Name name, Class<? extends U> cls) throws DataAccessException;

    @Blocking
    <U> U[] fetchArray(Name name, Converter<?, ? extends U> converter) throws DataAccessException;

    @Blocking
    <T> T[] fetchArray(Field<T> field) throws DataAccessException;

    @Blocking
    <U> U[] fetchArray(Field<?> field, Class<? extends U> cls) throws DataAccessException;

    @Blocking
    <T, U> U[] fetchArray(Field<T> field, Converter<? super T, ? extends U> converter) throws DataAccessException;

    @Blocking
    @NotNull
    <E> Set<E> fetchSet(RecordMapper<? super R, E> recordMapper) throws DataAccessException;

    @Blocking
    @NotNull
    Set<?> fetchSet(int i) throws DataAccessException;

    @Blocking
    @NotNull
    <U> Set<U> fetchSet(int i, Class<? extends U> cls) throws DataAccessException;

    @Blocking
    @NotNull
    <U> Set<U> fetchSet(int i, Converter<?, ? extends U> converter) throws DataAccessException;

    @Blocking
    @NotNull
    Set<?> fetchSet(String str) throws DataAccessException;

    @Blocking
    @NotNull
    <U> Set<U> fetchSet(String str, Class<? extends U> cls) throws DataAccessException;

    @Blocking
    @NotNull
    <U> Set<U> fetchSet(String str, Converter<?, ? extends U> converter) throws DataAccessException;

    @Blocking
    @NotNull
    Set<?> fetchSet(Name name) throws DataAccessException;

    @Blocking
    @NotNull
    <U> Set<U> fetchSet(Name name, Class<? extends U> cls) throws DataAccessException;

    @Blocking
    @NotNull
    <U> Set<U> fetchSet(Name name, Converter<?, ? extends U> converter) throws DataAccessException;

    @Blocking
    @NotNull
    <T> Set<T> fetchSet(Field<T> field) throws DataAccessException;

    @Blocking
    @NotNull
    <U> Set<U> fetchSet(Field<?> field, Class<? extends U> cls) throws DataAccessException;

    @Blocking
    @NotNull
    <T, U> Set<U> fetchSet(Field<T> field, Converter<? super T, ? extends U> converter) throws DataAccessException;

    @Blocking
    @NotNull
    <E> List<E> fetchInto(Class<? extends E> cls) throws DataAccessException, MappingException;

    @Blocking
    @NotNull
    <Z extends Record> Result<Z> fetchInto(Table<Z> table) throws DataAccessException;

    @Blocking
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    <H extends RecordHandler<? super R>> H fetchInto(H h) throws DataAccessException;

    @Blocking
    @NotNull
    <E> List<E> fetch(RecordMapper<? super R, E> recordMapper) throws DataAccessException;

    @NotNull
    CompletionStage<Result<R>> fetchAsync();

    @NotNull
    CompletionStage<Result<R>> fetchAsync(Executor executor);

    @Nullable
    Result<R> getResult();

    @NotNull
    Class<? extends R> getRecordType();

    @Override // org.jooq.Query
    @NotNull
    ResultQuery<R> bind(String str, Object obj) throws IllegalArgumentException, DataTypeException;

    @Override // org.jooq.Query
    @NotNull
    ResultQuery<R> bind(int i, Object obj) throws IllegalArgumentException, DataTypeException;

    @Override // org.jooq.Query
    @NotNull
    ResultQuery<R> poolable(boolean z);

    @Override // org.jooq.Query
    @NotNull
    ResultQuery<R> queryTimeout(int i);

    @Override // org.jooq.Query
    @NotNull
    CloseableResultQuery<R> keepStatement(boolean z);

    @NotNull
    ResultQuery<R> maxRows(int i);

    @NotNull
    ResultQuery<R> fetchSize(int i);

    @NotNull
    ResultQuery<R> resultSetConcurrency(int i);

    @NotNull
    ResultQuery<R> resultSetType(int i);

    @NotNull
    ResultQuery<R> resultSetHoldability(int i);

    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    ResultQuery<R> intern(Field<?>... fieldArr);

    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    ResultQuery<R> intern(int... iArr);

    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    ResultQuery<R> intern(String... strArr);

    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    ResultQuery<R> intern(Name... nameArr);

    @NotNull
    <X extends Record> ResultQuery<X> coerce(Table<X> table);

    @NotNull
    ResultQuery<Record> coerce(Field<?>... fieldArr);

    @NotNull
    ResultQuery<Record> coerce(Collection<? extends Field<?>> collection);

    @NotNull
    <T1> ResultQuery<Record1<T1>> coerce(Field<T1> field);

    @NotNull
    <T1, T2> ResultQuery<Record2<T1, T2>> coerce(Field<T1> field, Field<T2> field2);

    @NotNull
    <T1, T2, T3> ResultQuery<Record3<T1, T2, T3>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @NotNull
    <T1, T2, T3, T4> ResultQuery<Record4<T1, T2, T3, T4>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @NotNull
    <T1, T2, T3, T4, T5> ResultQuery<Record5<T1, T2, T3, T4, T5>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @NotNull
    <T1, T2, T3, T4, T5, T6> ResultQuery<Record6<T1, T2, T3, T4, T5, T6>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7> ResultQuery<Record7<T1, T2, T3, T4, T5, T6, T7>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8> ResultQuery<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9> ResultQuery<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> ResultQuery<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> ResultQuery<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> ResultQuery<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> ResultQuery<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> ResultQuery<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> ResultQuery<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> ResultQuery<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> ResultQuery<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> ResultQuery<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> ResultQuery<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> ResultQuery<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> ResultQuery<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21);

    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> ResultQuery<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22);

    @Override // java.lang.Iterable
    @Blocking
    @NotNull
    default Spliterator<R> spliterator() {
        return super.spliterator();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.lang.Iterable
    @Blocking
    default void forEach(Consumer<? super R> consumer) {
        super.forEach(consumer);
    }
}
