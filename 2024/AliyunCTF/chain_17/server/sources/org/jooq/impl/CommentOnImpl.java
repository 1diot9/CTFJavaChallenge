package org.jooq.impl;

import java.util.Set;
import org.jooq.Comment;
import org.jooq.CommentOnFinalStep;
import org.jooq.CommentOnIsStep;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function5;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CommentOnImpl.class */
public final class CommentOnImpl extends AbstractDDLQuery implements QOM.CommentOn, CommentOnIsStep, CommentOnFinalStep {
    final Table<?> table;
    final boolean isView;
    final boolean isMaterializedView;
    final Field<?> field;
    Comment comment;
    private static final Set<SQLDialect> SUPPORTS_COMMENT_ON_VIEW = SQLDialect.supportedBy(SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> SUPPORTS_COMMENT_ON_MATERIALIZED_VIEW = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);

    /* JADX INFO: Access modifiers changed from: package-private */
    public CommentOnImpl(Configuration configuration, Table<?> table, boolean isView, boolean isMaterializedView, Field<?> field) {
        this(configuration, table, isView, isMaterializedView, field, null);
    }

    CommentOnImpl(Configuration configuration, Table<?> table, boolean isView, boolean isMaterializedView) {
        this(configuration, table, isView, isMaterializedView, null);
    }

    CommentOnImpl(Configuration configuration, Table<?> table, boolean isView, boolean isMaterializedView, Field<?> field, Comment comment) {
        super(configuration);
        this.table = table;
        this.isView = isView;
        this.isMaterializedView = isMaterializedView;
        this.field = field;
        this.comment = comment;
    }

    @Override // org.jooq.CommentOnIsStep
    public final CommentOnImpl is(String comment) {
        return is(DSL.comment(comment));
    }

    @Override // org.jooq.CommentOnIsStep
    public final CommentOnImpl is(Comment comment) {
        this.comment = comment;
        return this;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case MARIADB:
            case MYSQL:
                if (this.table != null) {
                    acceptMySQL(ctx);
                    return;
                } else {
                    acceptDefault(ctx);
                    return;
                }
            default:
                acceptDefault(ctx);
                return;
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    private final void acceptMySQL(Context<?> ctx) {
        ctx.visit(Keywords.K_ALTER_TABLE).sql(' ').visit(this.table).sql(' ').visit(Keywords.K_COMMENT).sql(" = ").visit(this.comment);
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v11, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v15, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v24, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v29, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v38, types: [org.jooq.Context] */
    private final void acceptDefault(Context<?> ctx) {
        ctx.visit(Keywords.K_COMMENT).sql(' ').visit(Keywords.K_ON).sql(' ');
        if (this.table != null) {
            if (this.isView && SUPPORTS_COMMENT_ON_VIEW.contains(ctx.dialect())) {
                ctx.visit(Keywords.K_VIEW).sql(' ');
            } else if (this.isMaterializedView && SUPPORTS_COMMENT_ON_MATERIALIZED_VIEW.contains(ctx.dialect())) {
                ctx.visit(Keywords.K_MATERIALIZED).sql(' ').visit(Keywords.K_VIEW).sql(' ');
            } else {
                ctx.visit(Keywords.K_TABLE).sql(' ');
            }
            ctx.visit(this.table);
        } else if (this.field != null) {
            ctx.visit(Keywords.K_COLUMN).sql(' ').visit(this.field);
        } else {
            throw new IllegalStateException();
        }
        ctx.sql(' ').visit(Keywords.K_IS).sql(' ').visit(this.comment);
    }

    @Override // org.jooq.impl.QOM.CommentOn
    public final Table<?> $table() {
        return this.table;
    }

    @Override // org.jooq.impl.QOM.CommentOn
    public final boolean $isView() {
        return this.isView;
    }

    @Override // org.jooq.impl.QOM.CommentOn
    public final boolean $isMaterializedView() {
        return this.isMaterializedView;
    }

    @Override // org.jooq.impl.QOM.CommentOn
    public final Field<?> $field() {
        return this.field;
    }

    @Override // org.jooq.impl.QOM.CommentOn
    public final Comment $comment() {
        return this.comment;
    }

    @Override // org.jooq.impl.QOM.CommentOn
    public final QOM.CommentOn $table(Table<?> newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($isView()), Boolean.valueOf($isMaterializedView()), $field(), $comment());
    }

    @Override // org.jooq.impl.QOM.CommentOn
    public final QOM.CommentOn $isView(boolean newValue) {
        return $constructor().apply($table(), Boolean.valueOf(newValue), Boolean.valueOf($isMaterializedView()), $field(), $comment());
    }

    @Override // org.jooq.impl.QOM.CommentOn
    public final QOM.CommentOn $isMaterializedView(boolean newValue) {
        return $constructor().apply($table(), Boolean.valueOf($isView()), Boolean.valueOf(newValue), $field(), $comment());
    }

    @Override // org.jooq.impl.QOM.CommentOn
    public final QOM.CommentOn $field(Field<?> newValue) {
        return $constructor().apply($table(), Boolean.valueOf($isView()), Boolean.valueOf($isMaterializedView()), newValue, $comment());
    }

    @Override // org.jooq.impl.QOM.CommentOn
    public final QOM.CommentOn $comment(Comment newValue) {
        return $constructor().apply($table(), Boolean.valueOf($isView()), Boolean.valueOf($isMaterializedView()), $field(), newValue);
    }

    public final Function5<? super Table<?>, ? super Boolean, ? super Boolean, ? super Field<?>, ? super Comment, ? extends QOM.CommentOn> $constructor() {
        return (a1, a2, a3, a4, a5) -> {
            return new CommentOnImpl(configuration(), a1, a2.booleanValue(), a3.booleanValue(), a4, a5);
        };
    }
}
