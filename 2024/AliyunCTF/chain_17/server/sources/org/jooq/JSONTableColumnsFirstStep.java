package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/JSONTableColumnsFirstStep.class */
public interface JSONTableColumnsFirstStep {
    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    JSONTableColumnForOrdinalityStep column(String str);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    JSONTableColumnForOrdinalityStep column(Name name);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    JSONTableColumnPathStep column(Field<?> field);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    JSONTableColumnPathStep column(String str, DataType<?> dataType);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    JSONTableColumnPathStep column(Name name, DataType<?> dataType);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    JSONTableColumnPathStep column(Field<?> field, DataType<?> dataType);
}
