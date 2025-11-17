package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/CreateTableElementListStep.class */
public interface CreateTableElementListStep extends CreateTableAsStep {
    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep tableElements(TableElement... tableElementArr);

    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep tableElements(Collection<? extends TableElement> collection);

    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep columns(String... strArr);

    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep columns(Name... nameArr);

    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep columns(Field<?>... fieldArr);

    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep columns(Collection<? extends Field<?>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep column(Field<?> field);

    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep column(String str, DataType<?> dataType);

    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep column(Name name, DataType<?> dataType);

    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep column(Field<?> field, DataType<?> dataType);

    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep constraints(Constraint... constraintArr);

    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep constraints(Collection<? extends Constraint> collection);

    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep constraint(Constraint constraint);

    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep primaryKey(String... strArr);

    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep primaryKey(Name... nameArr);

    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep primaryKey(Field<?>... fieldArr);

    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep primaryKey(Collection<? extends Field<?>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep unique(String... strArr);

    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep unique(Name... nameArr);

    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep unique(Field<?>... fieldArr);

    @Support
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep unique(Collection<? extends Field<?>> collection);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep check(Condition condition);

    @Support({SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep indexes(Index... indexArr);

    @Support({SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep indexes(Collection<? extends Index> collection);

    @Support({SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableElementListStep index(Index index);
}
