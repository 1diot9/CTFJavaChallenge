package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/VisitContext.class */
public interface VisitContext extends Scope {
    @NotNull
    Clause clause();

    @NotNull
    Clause[] clauses();

    int clausesLength();

    @NotNull
    QueryPart queryPart();

    void queryPart(QueryPart queryPart);

    @NotNull
    QueryPart[] queryParts();

    int queryPartsLength();

    @NotNull
    Context<?> context();

    @Nullable
    RenderContext renderContext();

    @Nullable
    BindContext bindContext() throws UnsupportedOperationException;
}
