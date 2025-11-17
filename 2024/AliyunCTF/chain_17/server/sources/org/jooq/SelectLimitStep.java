package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectLimitStep.class */
public interface SelectLimitStep<R extends Record> extends SelectForUpdateStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    SelectLimitPercentStep<R> limit(Number number);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectLimitPercentStep<R> limit(Field<? extends Number> field);

    @Support
    @CheckReturnValue
    @NotNull
    SelectWithTiesAfterOffsetStep<R> limit(Number number, Number number2);

    @Support
    @CheckReturnValue
    @NotNull
    SelectLimitPercentAfterOffsetStep<R> limit(Number number, Field<? extends Number> field);

    @Support
    @CheckReturnValue
    @NotNull
    SelectLimitPercentAfterOffsetStep<R> limit(Field<? extends Number> field, Number number);

    @Support
    @CheckReturnValue
    @NotNull
    SelectLimitPercentAfterOffsetStep<R> limit(Field<? extends Number> field, Field<? extends Number> field2);

    @Support
    @CheckReturnValue
    @NotNull
    SelectLimitAfterOffsetStep<R> offset(Number number);

    @Support
    @CheckReturnValue
    @NotNull
    SelectLimitAfterOffsetStep<R> offset(Field<? extends Number> field);
}
