package org.jooq;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Stream;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.MappingException;

@Blocking
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Cursor.class */
public interface Cursor<R extends Record> extends Fields, Iterable<R>, Formattable, AutoCloseable {
    @NotNull
    RecordType<R> recordType();

    boolean hasNext() throws DataAccessException;

    @NotNull
    Result<R> fetch() throws DataAccessException;

    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    Result<R> fetch(int i) throws DataAccessException;

    @NotNull
    Result<R> fetchNext(int i) throws DataAccessException;

    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    <H extends RecordHandler<? super R>> H fetchInto(H h) throws DataAccessException;

    @NotNull
    <E> List<E> fetch(RecordMapper<? super R, E> recordMapper) throws DataAccessException;

    @NotNull
    <E> List<E> fetchInto(Class<? extends E> cls) throws DataAccessException, MappingException;

    @NotNull
    <Z extends Record> Result<Z> fetchInto(Table<Z> table) throws DataAccessException, MappingException;

    @Deprecated(forRemoval = true, since = "3.10")
    @Nullable
    R fetchOne() throws DataAccessException;

    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    <H extends RecordHandler<? super R>> H fetchOneInto(H h) throws DataAccessException;

    @Deprecated(forRemoval = true, since = "3.10")
    @Nullable
    <E> E fetchOne(RecordMapper<? super R, E> recordMapper) throws DataAccessException;

    @Deprecated(forRemoval = true, since = "3.10")
    @Nullable
    <Z extends Record> Z fetchOneInto(Table<Z> table) throws DataAccessException, MappingException;

    @Nullable
    R fetchNext() throws DataAccessException;

    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    <H extends RecordHandler<? super R>> H fetchNextInto(H h) throws DataAccessException;

    @Deprecated(forRemoval = true, since = "3.10")
    @Nullable
    <E> E fetchOneInto(Class<? extends E> cls) throws DataAccessException, MappingException;

    @Nullable
    <E> E fetchNextInto(Class<? extends E> cls) throws DataAccessException, MappingException;

    @Nullable
    <E> E fetchNext(RecordMapper<? super R, E> recordMapper) throws DataAccessException;

    @Nullable
    <Z extends Record> Z fetchNextInto(Table<Z> table) throws DataAccessException, MappingException;

    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    Optional<R> fetchOptional() throws DataAccessException;

    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    <E> Optional<E> fetchOptionalInto(Class<? extends E> cls) throws DataAccessException, MappingException;

    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    <E> Optional<E> fetchOptional(RecordMapper<? super R, E> recordMapper) throws DataAccessException;

    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    <Z extends Record> Optional<Z> fetchOptionalInto(Table<Z> table) throws DataAccessException, MappingException;

    @NotNull
    Optional<R> fetchNextOptional() throws DataAccessException;

    @NotNull
    <E> Optional<E> fetchNextOptionalInto(Class<? extends E> cls) throws DataAccessException, MappingException;

    @NotNull
    <E> Optional<E> fetchNextOptional(RecordMapper<? super R, E> recordMapper) throws DataAccessException;

    @NotNull
    <Z extends Record> Optional<Z> fetchNextOptionalInto(Table<Z> table) throws DataAccessException, MappingException;

    @NotNull
    Stream<R> stream() throws DataAccessException;

    <X, A> X collect(Collector<? super R, A, X> collector) throws DataAccessException;

    @Override // java.lang.AutoCloseable
    void close() throws DataAccessException;

    boolean isClosed();

    @Nullable
    ResultSet resultSet();
}
