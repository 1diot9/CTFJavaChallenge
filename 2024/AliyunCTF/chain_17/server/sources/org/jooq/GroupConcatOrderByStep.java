package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/GroupConcatOrderByStep.class */
public interface GroupConcatOrderByStep extends GroupConcatSeparatorStep {
    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    GroupConcatSeparatorStep orderBy(OrderField<?>... orderFieldArr);

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @NotNull
    GroupConcatSeparatorStep orderBy(Collection<? extends OrderField<?>> collection);
}
