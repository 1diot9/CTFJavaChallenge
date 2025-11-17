package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/UpdateFromStep.class */
public interface UpdateFromStep<R extends Record> extends UpdateWhereStep<R> {
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @CheckReturnValue
    @NotNull
    UpdateWhereStep<R> from(TableLike<?> tableLike);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @CheckReturnValue
    @NotNull
    UpdateWhereStep<R> from(TableLike<?>... tableLikeArr);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @CheckReturnValue
    @NotNull
    UpdateWhereStep<R> from(Collection<? extends TableLike<?>> collection);

    @PlainSQL
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @CheckReturnValue
    @NotNull
    UpdateWhereStep<R> from(SQL sql);

    @PlainSQL
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @CheckReturnValue
    @NotNull
    UpdateWhereStep<R> from(String str);

    @PlainSQL
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @CheckReturnValue
    @NotNull
    UpdateWhereStep<R> from(String str, Object... objArr);

    @PlainSQL
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @CheckReturnValue
    @NotNull
    UpdateWhereStep<R> from(String str, QueryPart... queryPartArr);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @CheckReturnValue
    @NotNull
    UpdateWhereStep<R> from(Name name);
}
