package org.jooq;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Block.class */
public interface Block extends RowCountQuery {
    @ApiStatus.Experimental
    @NotNull
    QOM.UnmodifiableList<? extends Statement> $statements();
}
