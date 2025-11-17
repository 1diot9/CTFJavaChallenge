package org.jooq;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Named.class */
public interface Named extends QueryPart {
    @NotNull
    String getName();

    @NotNull
    Name getQualifiedName();

    @NotNull
    Name getUnqualifiedName();

    @NotNull
    String getComment();

    @NotNull
    Comment getCommentPart();

    @ApiStatus.Experimental
    @NotNull
    Name $name();
}
