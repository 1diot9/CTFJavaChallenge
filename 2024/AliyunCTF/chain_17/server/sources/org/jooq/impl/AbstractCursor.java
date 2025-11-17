package org.jooq.impl;

import java.util.List;
import java.util.Optional;
import java.util.Spliterators;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.jooq.Configuration;
import org.jooq.Cursor;
import org.jooq.Record;
import org.jooq.RecordHandler;
import org.jooq.RecordMapper;
import org.jooq.RecordType;
import org.jooq.Result;
import org.jooq.Table;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractCursor.class */
abstract class AbstractCursor<R extends Record> extends AbstractResult<R> implements Cursor<R> {
    @Override // org.jooq.Cursor
    public /* bridge */ /* synthetic */ RecordType recordType() {
        return super.recordType();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractCursor(Configuration configuration, AbstractRow<R> row) {
        super(configuration, row);
    }

    @Override // org.jooq.Cursor
    public final Stream<R> stream() {
        return (Stream) StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), 272), false).onClose(() -> {
            close();
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Cursor
    public final <X, A> X collect(Collector<? super R, A, X> collector) {
        return (X) stream().collect(collector);
    }

    @Override // org.jooq.Cursor
    public final boolean hasNext() {
        return iterator().hasNext();
    }

    @Override // org.jooq.Cursor
    public final Result<R> fetch() {
        return fetch(Integer.MAX_VALUE);
    }

    @Override // org.jooq.Cursor
    @Deprecated
    public final R fetchOne() {
        return fetchNext();
    }

    @Override // org.jooq.Cursor
    @Deprecated
    public final <E> E fetchOne(RecordMapper<? super R, E> recordMapper) {
        return (E) fetchNext(recordMapper);
    }

    @Override // org.jooq.Cursor
    @Deprecated
    public final <H extends RecordHandler<? super R>> H fetchOneInto(H h) {
        return (H) fetchNextInto((AbstractCursor<R>) h);
    }

    @Override // org.jooq.Cursor
    @Deprecated
    public final <Z extends Record> Z fetchOneInto(Table<Z> table) {
        return (Z) fetchNextInto(table);
    }

    @Override // org.jooq.Cursor
    @Deprecated
    public final <E> E fetchOneInto(Class<? extends E> cls) {
        return (E) fetchNextInto(cls);
    }

    @Override // org.jooq.Cursor
    public final R fetchNext() {
        Result<R> result = fetch(1);
        if (result.size() == 1) {
            return (R) result.get(0);
        }
        return null;
    }

    @Override // org.jooq.Cursor
    @Deprecated
    public final Optional<R> fetchOptional() {
        return fetchNextOptional();
    }

    @Override // org.jooq.Cursor
    @Deprecated
    public final <E> Optional<E> fetchOptional(RecordMapper<? super R, E> mapper) {
        return fetchNextOptional(mapper);
    }

    @Override // org.jooq.Cursor
    @Deprecated
    public final <E> Optional<E> fetchOptionalInto(Class<? extends E> type) {
        return fetchNextOptionalInto(type);
    }

    @Override // org.jooq.Cursor
    @Deprecated
    public final <Z extends Record> Optional<Z> fetchOptionalInto(Table<Z> table) {
        return fetchNextOptionalInto(table);
    }

    @Override // org.jooq.Cursor
    public final Optional<R> fetchNextOptional() {
        return Optional.ofNullable(fetchNext());
    }

    @Override // org.jooq.Cursor
    public final <E> Optional<E> fetchNextOptional(RecordMapper<? super R, E> mapper) {
        return Optional.ofNullable(fetchNext(mapper));
    }

    @Override // org.jooq.Cursor
    public final <E> Optional<E> fetchNextOptionalInto(Class<? extends E> type) {
        return Optional.ofNullable(fetchNextInto(type));
    }

    @Override // org.jooq.Cursor
    public final <Z extends Record> Optional<Z> fetchNextOptionalInto(Table<Z> table) {
        return Optional.ofNullable(fetchNextInto(table));
    }

    @Override // org.jooq.Cursor
    public final Result<R> fetch(int number) {
        return fetchNext(number);
    }

    @Override // org.jooq.Cursor
    public final <H extends RecordHandler<? super R>> H fetchNextInto(H handler) {
        handler.next(fetchNext());
        return handler;
    }

    @Override // org.jooq.Cursor
    public final <H extends RecordHandler<? super R>> H fetchInto(H handler) {
        forEach(handler);
        return handler;
    }

    @Override // org.jooq.Cursor
    public final <E> E fetchNext(RecordMapper<? super R, E> mapper) {
        R record = fetchNext();
        if (record == null) {
            return null;
        }
        return mapper.map(record);
    }

    @Override // org.jooq.Cursor
    public final <E> List<E> fetch(RecordMapper<? super R, E> mapper) {
        return fetch().map(mapper);
    }

    @Override // org.jooq.Cursor
    public final <E> E fetchNextInto(Class<? extends E> cls) {
        R fetchNext = fetchNext();
        if (fetchNext == null) {
            return null;
        }
        return (E) fetchNext.into(cls);
    }

    @Override // org.jooq.Cursor
    public final <E> List<E> fetchInto(Class<? extends E> clazz) {
        return fetch().into(clazz);
    }

    @Override // org.jooq.Cursor
    public final <Z extends Record> Z fetchNextInto(Table<Z> table) {
        return (Z) fetchNext().into(table);
    }

    @Override // org.jooq.Cursor
    public final <Z extends Record> Result<Z> fetchInto(Table<Z> table) {
        return fetch().into(table);
    }
}
