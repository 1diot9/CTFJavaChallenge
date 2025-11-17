package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/AlterTypeRenameValueToStep.class */
public interface AlterTypeRenameValueToStep {
    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterTypeFinalStep to(String str);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterTypeFinalStep to(Field<String> field);
}
