package org.jooq;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Comment.class */
public interface Comment extends ColumnElement {
    String getComment();

    @ApiStatus.Experimental
    @NotNull
    String $comment();
}
