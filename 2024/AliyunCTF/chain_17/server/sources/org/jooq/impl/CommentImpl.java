package org.jooq.impl;

import org.jooq.Comment;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CommentImpl.class */
public final class CommentImpl extends AbstractQueryPart implements Comment, QOM.UEmpty {
    static final CommentImpl NO_COMMENT = new CommentImpl("");
    private final String comment;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CommentImpl(String comment) {
        this.comment = comment == null ? "" : comment;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit((Field<?>) DSL.inline(this.comment));
    }

    @Override // org.jooq.Comment
    public final String getComment() {
        return this.comment;
    }

    @Override // org.jooq.Comment
    public final String $comment() {
        return this.comment;
    }
}
