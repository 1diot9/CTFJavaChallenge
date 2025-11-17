package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/DeleteUsingStep.class */
public interface DeleteUsingStep<R extends Record> extends DeleteWhereStep<R> {
    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    DeleteWhereStep<R> using(TableLike<?> tableLike);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    DeleteWhereStep<R> using(TableLike<?>... tableLikeArr);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    DeleteWhereStep<R> using(Collection<? extends TableLike<?>> collection);

    @PlainSQL
    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    DeleteWhereStep<R> using(SQL sql);

    @PlainSQL
    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    DeleteWhereStep<R> using(String str);

    @PlainSQL
    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    DeleteWhereStep<R> using(String str, Object... objArr);

    @PlainSQL
    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    DeleteWhereStep<R> using(String str, QueryPart... queryPartArr);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    DeleteWhereStep<R> using(Name name);
}
