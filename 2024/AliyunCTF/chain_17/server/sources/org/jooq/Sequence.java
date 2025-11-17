package org.jooq;

import java.lang.Number;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Sequence.class */
public interface Sequence<T extends Number> extends Qualified, Typed<T> {
    @Nullable
    Field<T> getStartWith();

    @Nullable
    Field<T> getIncrementBy();

    @Nullable
    Field<T> getMinvalue();

    @Nullable
    Field<T> getMaxvalue();

    boolean getCycle();

    @Nullable
    Field<T> getCache();

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<T> currval();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<T> nextval();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    Select<Record1<T>> nextvals(int i);
}
