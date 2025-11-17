package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/DeleteQuery.class */
public interface DeleteQuery<R extends Record> extends ConditionProvider, Delete<R> {
    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    void addUsing(TableLike<?> tableLike);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    void addUsing(TableLike<?>... tableLikeArr);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    void addUsing(Collection<? extends TableLike<?>> collection);

    @Override // org.jooq.ConditionProvider
    @Support
    void addConditions(Condition condition);

    @Override // org.jooq.ConditionProvider
    @Support
    void addConditions(Condition... conditionArr);

    @Override // org.jooq.ConditionProvider
    @Support
    void addConditions(Collection<? extends Condition> collection);

    @Override // org.jooq.ConditionProvider
    @Support
    void addConditions(Operator operator, Condition condition);

    @Override // org.jooq.ConditionProvider
    @Support
    void addConditions(Operator operator, Condition... conditionArr);

    @Override // org.jooq.ConditionProvider
    @Support
    void addConditions(Operator operator, Collection<? extends Condition> collection);

    @Support
    void addOrderBy(OrderField<?>... orderFieldArr);

    @Support
    void addOrderBy(Collection<? extends OrderField<?>> collection);

    @Support
    void addLimit(Number number);

    @Support
    void addLimit(Field<? extends Number> field);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    void setReturning();

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    void setReturning(SelectFieldOrAsterisk... selectFieldOrAsteriskArr);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    void setReturning(Collection<? extends SelectFieldOrAsterisk> collection);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @Nullable
    R getReturnedRecord();

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Result<R> getReturnedRecords();
}
