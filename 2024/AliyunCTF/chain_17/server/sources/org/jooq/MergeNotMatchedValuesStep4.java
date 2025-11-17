package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/MergeNotMatchedValuesStep4.class */
public interface MergeNotMatchedValuesStep4<R extends Record, T1, T2, T3, T4> {
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    MergeNotMatchedWhereStep<R> values(T1 t1, T2 t2, T3 t3, T4 t4);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    MergeNotMatchedWhereStep<R> values(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    MergeNotMatchedWhereStep<R> values(Collection<?> collection);
}
