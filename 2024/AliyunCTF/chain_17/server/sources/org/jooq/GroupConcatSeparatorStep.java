package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/GroupConcatSeparatorStep.class */
public interface GroupConcatSeparatorStep extends AggregateFunction<String> {
    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO})
    @NotNull
    AggregateFunction<String> separator(String str);
}
