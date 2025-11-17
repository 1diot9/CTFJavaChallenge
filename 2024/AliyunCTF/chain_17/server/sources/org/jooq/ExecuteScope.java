package org.jooq;

import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ExecuteScope.class */
public interface ExecuteScope extends Scope {
    ConverterContext converterContext();

    @Nullable
    ExecuteContext executeContext();
}
