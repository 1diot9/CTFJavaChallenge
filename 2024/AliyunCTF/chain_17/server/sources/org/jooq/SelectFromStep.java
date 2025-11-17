package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectFromStep.class */
public interface SelectFromStep<R extends Record> extends SelectWhereStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> from(TableLike<?> tableLike);

    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> from(TableLike<?>... tableLikeArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> from(Collection<? extends TableLike<?>> collection);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> from(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> from(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> from(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> from(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> from(Name name);

    @Support
    @CheckReturnValue
    @NotNull
    SelectFromStep<R> hint(String str);
}
