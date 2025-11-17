package org.jooq;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/TableLike.class */
public interface TableLike<R extends Record> extends Fields, QueryPart {
    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<Result<R>> asMultiset();

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<Result<R>> asMultiset(String str);

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<Result<R>> asMultiset(Name name);

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<Result<R>> asMultiset(Field<?> field);

    @Support
    @NotNull
    Table<R> asTable();

    @Support
    @NotNull
    Table<R> asTable(String str);

    @Support
    @NotNull
    Table<R> asTable(String str, String... strArr);

    @Support
    @NotNull
    Table<R> asTable(String str, Collection<? extends String> collection);

    @Support
    @NotNull
    Table<R> asTable(Name name);

    @Support
    @NotNull
    Table<R> asTable(Name name, Name... nameArr);

    @Support
    @NotNull
    Table<R> asTable(Name name, Collection<? extends Name> collection);

    @Support
    @NotNull
    Table<R> asTable(Table<?> table);

    @Support
    @NotNull
    Table<R> asTable(Table<?> table, Field<?>... fieldArr);

    @Support
    @NotNull
    Table<R> asTable(Table<?> table, Collection<? extends Field<?>> collection);

    @Support
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    Table<R> asTable(String str, Function<? super Field<?>, ? extends String> function);

    @Support
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    Table<R> asTable(String str, BiFunction<? super Field<?>, ? super Integer, ? extends String> biFunction);
}
