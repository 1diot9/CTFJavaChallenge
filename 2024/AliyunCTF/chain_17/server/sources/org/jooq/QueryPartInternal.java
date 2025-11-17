package org.jooq;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/QueryPartInternal.class */
public interface QueryPartInternal extends QueryPart {
    @Deprecated
    boolean rendersContent(Context<?> context);

    @Deprecated
    void accept(Context<?> context);

    Clause[] clauses(Context<?> context);

    boolean declaresFields();

    boolean declaresTables();

    boolean declaresWindows();

    boolean declaresCTE();

    boolean generatesCast();
}
