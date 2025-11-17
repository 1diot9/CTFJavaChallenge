package org.jooq;

import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Record;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Select.class */
public interface Select<R extends Record> extends ResultQuery<R>, TableLike<R>, FieldLike, FieldOrRowOrSelect {
    @Support
    @CheckReturnValue
    @NotNull
    Select<R> union(Select<? extends R> select);

    @Support
    @CheckReturnValue
    @NotNull
    Select<R> unionDistinct(Select<? extends R> select);

    @Support
    @CheckReturnValue
    @NotNull
    Select<R> unionAll(Select<? extends R> select);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    Select<R> except(Select<? extends R> select);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    Select<R> exceptDistinct(Select<? extends R> select);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    Select<R> exceptAll(Select<? extends R> select);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    Select<R> intersect(Select<? extends R> select);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    Select<R> intersectDistinct(Select<? extends R> select);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    Select<R> intersectAll(Select<? extends R> select);

    @CheckReturnValue
    @NotNull
    List<Field<?>> getSelect();

    @ApiStatus.Experimental
    @Nullable
    QOM.With $with();

    @CheckReturnValue
    @ApiStatus.Experimental
    @NotNull
    Select<?> $with(QOM.With with);

    @ApiStatus.Experimental
    @NotNull
    QOM.UnmodifiableList<? extends SelectFieldOrAsterisk> $select();

    @CheckReturnValue
    @ApiStatus.Experimental
    @NotNull
    Select<?> $select(Collection<? extends SelectFieldOrAsterisk> collection);

    @ApiStatus.Experimental
    boolean $distinct();

    @CheckReturnValue
    @ApiStatus.Experimental
    @NotNull
    Select<R> $distinct(boolean z);

    @ApiStatus.Experimental
    @NotNull
    QOM.UnmodifiableList<? extends SelectFieldOrAsterisk> $distinctOn();

    @CheckReturnValue
    @ApiStatus.Experimental
    @NotNull
    Select<R> $distinctOn(Collection<? extends SelectFieldOrAsterisk> collection);

    @ApiStatus.Experimental
    @NotNull
    QOM.UnmodifiableList<? extends Table<?>> $from();

    @CheckReturnValue
    @ApiStatus.Experimental
    @NotNull
    Select<R> $from(Collection<? extends Table<?>> collection);

    @ApiStatus.Experimental
    @Nullable
    Condition $where();

    @CheckReturnValue
    @ApiStatus.Experimental
    @NotNull
    Select<R> $where(Condition condition);

    @ApiStatus.Experimental
    @NotNull
    QOM.UnmodifiableList<? extends GroupField> $groupBy();

    @CheckReturnValue
    @ApiStatus.Experimental
    @NotNull
    Select<R> $groupBy(Collection<? extends GroupField> collection);

    @ApiStatus.Experimental
    boolean $groupByDistinct();

    @CheckReturnValue
    @ApiStatus.Experimental
    @NotNull
    Select<R> $groupByDistinct(boolean z);

    @ApiStatus.Experimental
    @Nullable
    Condition $having();

    @CheckReturnValue
    @ApiStatus.Experimental
    @NotNull
    Select<R> $having(Condition condition);

    @ApiStatus.Experimental
    @NotNull
    QOM.UnmodifiableList<? extends WindowDefinition> $window();

    @CheckReturnValue
    @ApiStatus.Experimental
    @NotNull
    Select<R> $window(Collection<? extends WindowDefinition> collection);

    @ApiStatus.Experimental
    @Nullable
    Condition $qualify();

    @CheckReturnValue
    @ApiStatus.Experimental
    @NotNull
    Select<R> $qualify(Condition condition);

    @ApiStatus.Experimental
    @NotNull
    QOM.UnmodifiableList<? extends SortField<?>> $orderBy();

    @CheckReturnValue
    @ApiStatus.Experimental
    @NotNull
    Select<R> $orderBy(Collection<? extends SortField<?>> collection);

    @ApiStatus.Experimental
    @Nullable
    Field<? extends Number> $limit();

    @CheckReturnValue
    @ApiStatus.Experimental
    @NotNull
    Select<R> $limit(Field<? extends Number> field);

    @ApiStatus.Experimental
    boolean $limitPercent();

    @CheckReturnValue
    @ApiStatus.Experimental
    @NotNull
    Select<R> $limitPercent(boolean z);

    @ApiStatus.Experimental
    boolean $limitWithTies();

    @CheckReturnValue
    @ApiStatus.Experimental
    @NotNull
    Select<R> $limitWithTies(boolean z);

    @ApiStatus.Experimental
    @Nullable
    Field<? extends Number> $offset();

    @CheckReturnValue
    @ApiStatus.Experimental
    @NotNull
    Select<R> $offset(Field<? extends Number> field);
}
