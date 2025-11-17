package org.jooq;

import java.lang.Number;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/AlterSequenceStep.class */
public interface AlterSequenceStep<T extends Number> extends AlterSequenceFlagsStep<T> {
    @Support({SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterSequenceFinalStep renameTo(String str);

    @Support({SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterSequenceFinalStep renameTo(Name name);

    @Support({SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterSequenceFinalStep renameTo(Sequence<?> sequence);
}
