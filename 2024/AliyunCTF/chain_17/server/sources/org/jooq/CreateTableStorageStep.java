package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/CreateTableStorageStep.class */
public interface CreateTableStorageStep extends CreateTableFinalStep {
    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    CreateTableFinalStep storage(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    CreateTableFinalStep storage(String str, QueryPart... queryPartArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    CreateTableFinalStep storage(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    CreateTableFinalStep storage(String str);
}
