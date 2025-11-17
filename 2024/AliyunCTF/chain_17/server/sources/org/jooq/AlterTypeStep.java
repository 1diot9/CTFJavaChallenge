package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/AlterTypeStep.class */
public interface AlterTypeStep {
    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterTypeFinalStep renameTo(String str);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterTypeFinalStep renameTo(Name name);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterTypeFinalStep setSchema(String str);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterTypeFinalStep setSchema(Name name);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterTypeFinalStep setSchema(Schema schema);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterTypeFinalStep addValue(String str);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterTypeFinalStep addValue(Field<String> field);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterTypeRenameValueToStep renameValue(String str);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterTypeRenameValueToStep renameValue(Field<String> field);
}
