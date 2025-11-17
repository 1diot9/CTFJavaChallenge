package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

@Deprecated(forRemoval = true, since = "3.14")
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/MergeKeyStep1.class */
public interface MergeKeyStep1<R extends Record, T1> extends MergeValuesStep1<R, T1> {
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    MergeValuesStep1<R, T1> key(Field<?>... fieldArr);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    MergeValuesStep1<R, T1> key(Collection<? extends Field<?>> collection);
}
