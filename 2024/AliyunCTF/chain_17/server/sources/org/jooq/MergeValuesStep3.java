package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

@Deprecated(forRemoval = true, since = "3.14")
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/MergeValuesStep3.class */
public interface MergeValuesStep3<R extends Record, T1, T2, T3> {
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    Merge<R> values(T1 t1, T2 t2, T3 t3);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    Merge<R> values(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    Merge<R> values(Collection<?> collection);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    Merge<R> select(Select<? extends Record3<T1, T2, T3>> select);
}
