package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/AlterTableStep.class */
public interface AlterTableStep {
    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableFinalStep comment(String str);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableFinalStep comment(Comment comment);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableFinalStep renameTo(Table<?> table);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableFinalStep renameTo(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableFinalStep renameTo(String str);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableRenameColumnToStep renameColumn(Field<?> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableRenameColumnToStep renameColumn(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableRenameColumnToStep renameColumn(String str);

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterTableRenameIndexToStep renameIndex(Name name);

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterTableRenameIndexToStep renameIndex(Index index);

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterTableRenameIndexToStep renameIndex(String str);

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableRenameConstraintToStep renameConstraint(Constraint constraint);

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableRenameConstraintToStep renameConstraint(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableRenameConstraintToStep renameConstraint(String str);

    @Support({SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    AlterTableAlterConstraintStep alter(Constraint constraint);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <T> AlterTableAlterStep<T> alter(Field<T> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableAlterStep<Object> alter(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableAlterStep<Object> alter(String str);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <T> AlterTableAlterStep<T> alterColumn(Field<T> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableAlterStep<Object> alterColumn(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableAlterStep<Object> alterColumn(String str);

    @Support({SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    AlterTableAlterConstraintStep alterConstraint(Constraint constraint);

    @Support({SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    AlterTableAlterConstraintStep alterConstraint(Name name);

    @Support({SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    AlterTableAlterConstraintStep alterConstraint(String str);

    @Support
    @CheckReturnValue
    @NotNull
    AlterTableAddStep add(Field<?> field);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableAddStep add(TableElement... tableElementArr);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableAddStep add(Collection<? extends TableElement> collection);

    @Support
    @CheckReturnValue
    @NotNull
    <T> AlterTableAddStep add(Field<T> field, DataType<T> dataType);

    @Support
    @CheckReturnValue
    @NotNull
    AlterTableAddStep add(Name name, DataType<?> dataType);

    @Support
    @CheckReturnValue
    @NotNull
    AlterTableAddStep add(String str, DataType<?> dataType);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableAddStep addIfNotExists(Field<?> field);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <T> AlterTableAddStep addIfNotExists(Field<T> field, DataType<T> dataType);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableAddStep addIfNotExists(Name name, DataType<?> dataType);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableAddStep addIfNotExists(String str, DataType<?> dataType);

    @Support
    @CheckReturnValue
    @NotNull
    AlterTableAddStep addColumn(Field<?> field);

    @Support
    @CheckReturnValue
    @NotNull
    <T> AlterTableAddStep addColumn(Field<T> field, DataType<T> dataType);

    @Support
    @CheckReturnValue
    @NotNull
    AlterTableAddStep addColumn(Name name, DataType<?> dataType);

    @Support
    @CheckReturnValue
    @NotNull
    AlterTableAddStep addColumn(String str, DataType<?> dataType);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableAddStep addColumnIfNotExists(Field<?> field);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    <T> AlterTableAddStep addColumnIfNotExists(Field<T> field, DataType<T> dataType);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableAddStep addColumnIfNotExists(Name name, DataType<?> dataType);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableAddStep addColumnIfNotExists(String str, DataType<?> dataType);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableUsingIndexStep add(Constraint constraint);

    @Support
    @CheckReturnValue
    @NotNull
    AlterTableDropStep drop(Field<?> field);

    @Support
    @CheckReturnValue
    @NotNull
    AlterTableDropStep drop(Name name);

    @Support
    @CheckReturnValue
    @NotNull
    AlterTableDropStep drop(String str);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropIfExists(Field<?> field);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropIfExists(Name name);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropIfExists(String str);

    @Support
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropColumn(Field<?> field);

    @Support
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropColumn(Name name);

    @Support
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropColumn(String str);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropColumnIfExists(Field<?> field);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropColumnIfExists(Name name);

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropColumnIfExists(String str);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep drop(Field<?>... fieldArr);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep drop(Name... nameArr);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep drop(String... strArr);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropColumns(Field<?>... fieldArr);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropColumns(Name... nameArr);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropColumns(String... strArr);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep drop(Collection<? extends Field<?>> collection);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropColumns(Collection<? extends Field<?>> collection);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep drop(Constraint constraint);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropConstraint(Constraint constraint);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropConstraint(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropConstraint(String str);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropIfExists(Constraint constraint);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropConstraintIfExists(Constraint constraint);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropConstraintIfExists(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropConstraintIfExists(String str);

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropPrimaryKey();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropPrimaryKey(Constraint constraint);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropPrimaryKey(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropPrimaryKey(String str);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropUnique(Constraint constraint);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropUnique(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropUnique(String str);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropForeignKey(Constraint constraint);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropForeignKey(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableDropStep dropForeignKey(String str);
}
