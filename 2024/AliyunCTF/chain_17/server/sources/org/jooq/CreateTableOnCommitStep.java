package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/CreateTableOnCommitStep.class */
public interface CreateTableOnCommitStep extends CreateTableCommentStep {
    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableCommentStep onCommitDeleteRows();

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableCommentStep onCommitPreserveRows();

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableCommentStep onCommitDrop();
}
