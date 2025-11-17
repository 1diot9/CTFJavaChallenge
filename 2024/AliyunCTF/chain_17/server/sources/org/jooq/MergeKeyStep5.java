package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

@Deprecated(forRemoval = true, since = "3.14")
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/MergeKeyStep5.class */
public interface MergeKeyStep5<R extends Record, T1, T2, T3, T4, T5> extends MergeValuesStep5<R, T1, T2, T3, T4, T5> {
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    MergeValuesStep5<R, T1, T2, T3, T4, T5> key(Field<?>... fieldArr);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    MergeValuesStep5<R, T1, T2, T3, T4, T5> key(Collection<? extends Field<?>> collection);
}
