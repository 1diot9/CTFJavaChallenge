package org.jooq;

import java.util.Collection;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/InsertQuery.class */
public interface InsertQuery<R extends Record> extends StoreQuery<R>, Insert<R>, ConditionProvider {
    @Support
    void newRecord();

    @Support
    void addRecord(R r);

    @Support
    void onConflict(Field<?>... fieldArr);

    @Support
    void onConflict(Collection<? extends Field<?>> collection);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    void onConflictOnConstraint(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    void onConflictOnConstraint(Constraint constraint);

    @Support
    void onConflictOnConstraint(UniqueKey<R> uniqueKey);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    void onDuplicateKeyUpdate(boolean z);

    @Support
    void onDuplicateKeyIgnore(boolean z);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    <T> void addValueForUpdate(Field<T> field, T t);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    <T> void addValueForUpdate(Field<T> field, Field<T> field2);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    void addValuesForUpdate(Map<?, ?> map);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    void setRecordForUpdate(R r);

    @Support({SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    void onConflictWhere(Condition condition);

    @Override // org.jooq.ConditionProvider
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    void addConditions(Condition condition);

    @Override // org.jooq.ConditionProvider
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    void addConditions(Condition... conditionArr);

    @Override // org.jooq.ConditionProvider
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    void addConditions(Collection<? extends Condition> collection);

    @Override // org.jooq.ConditionProvider
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    void addConditions(Operator operator, Condition condition);

    @Override // org.jooq.ConditionProvider
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    void addConditions(Operator operator, Condition... conditionArr);

    @Override // org.jooq.ConditionProvider
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    void addConditions(Operator operator, Collection<? extends Condition> collection);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    void setDefaultValues();

    @Support
    void setSelect(Field<?>[] fieldArr, Select<?> select);

    @Support
    void setSelect(Collection<? extends Field<?>> collection, Select<?> select);

    @Override // org.jooq.StoreQuery
    @Support
    void setReturning();

    @Override // org.jooq.StoreQuery
    @Support
    void setReturning(Identity<R, ?> identity);

    @Override // org.jooq.StoreQuery
    @Support
    void setReturning(SelectFieldOrAsterisk... selectFieldOrAsteriskArr);

    @Override // org.jooq.StoreQuery
    @Support
    void setReturning(Collection<? extends SelectFieldOrAsterisk> collection);

    @Override // org.jooq.StoreQuery
    @Support
    @Nullable
    R getReturnedRecord();

    @Override // org.jooq.StoreQuery
    @Support
    @NotNull
    Result<R> getReturnedRecords();
}
