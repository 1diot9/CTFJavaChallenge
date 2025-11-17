package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/AlterSchemaStep.class */
public interface AlterSchemaStep {
    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterSchemaFinalStep renameTo(String str);

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterSchemaFinalStep renameTo(Name name);

    @Support({SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterSchemaFinalStep renameTo(Schema schema);
}
