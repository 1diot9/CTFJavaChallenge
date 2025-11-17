package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/MergeNotMatchedValuesStep2.class */
public interface MergeNotMatchedValuesStep2<R extends Record, T1, T2> {
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    MergeNotMatchedWhereStep<R> values(T1 t1, T2 t2);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    MergeNotMatchedWhereStep<R> values(Field<T1> field, Field<T2> field2);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    MergeNotMatchedWhereStep<R> values(Collection<?> collection);
}
