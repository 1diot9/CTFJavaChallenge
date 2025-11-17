package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jooq.AlterTableAddStep;
import org.jooq.AlterTableAlterConstraintStep;
import org.jooq.AlterTableAlterStep;
import org.jooq.AlterTableDropStep;
import org.jooq.AlterTableFinalStep;
import org.jooq.AlterTableRenameColumnToStep;
import org.jooq.AlterTableRenameConstraintToStep;
import org.jooq.AlterTableRenameIndexToStep;
import org.jooq.AlterTableStep;
import org.jooq.AlterTableUsingIndexStep;
import org.jooq.Clause;
import org.jooq.Comment;
import org.jooq.Configuration;
import org.jooq.Constraint;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Index;
import org.jooq.Keyword;
import org.jooq.Name;
import org.jooq.Nullability;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.TableElement;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AlterTableImpl.class */
public final class AlterTableImpl extends AbstractDDLQuery implements AlterTableStep, AlterTableAddStep, AlterTableDropStep, AlterTableAlterStep, AlterTableAlterConstraintStep, AlterTableUsingIndexStep, AlterTableRenameColumnToStep, AlterTableRenameIndexToStep, AlterTableRenameConstraintToStep, QOM.UNotYetImplemented {
    private static final Clause[] CLAUSES = {Clause.ALTER_TABLE};
    private static final Set<SQLDialect> NO_SUPPORT_IF_EXISTS = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.MARIADB);
    private static final Set<SQLDialect> NO_SUPPORT_IF_EXISTS_COLUMN = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD);
    private static final Set<SQLDialect> NO_SUPPORT_IF_EXISTS_CONSTRAINT = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> NO_SUPPORT_IF_NOT_EXISTS_COLUMN = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD);
    private static final Set<SQLDialect> SUPPORT_RENAME_COLUMN = SQLDialect.supportedBy(SQLDialect.DERBY);
    private static final Set<SQLDialect> SUPPORT_RENAME_TABLE = SQLDialect.supportedBy(SQLDialect.DERBY);
    private static final Set<SQLDialect> NO_SUPPORT_RENAME_QUALIFIED_TABLE = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> NO_SUPPORT_ALTER_TYPE_AND_NULL = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> NO_SUPPORT_DROP_CONSTRAINT = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL);
    private static final Set<SQLDialect> REQUIRE_REPEAT_ADD_ON_MULTI_ALTER = SQLDialect.supportedBy(SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> REQUIRE_REPEAT_DROP_ON_MULTI_ALTER = SQLDialect.supportedBy(SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    private final Table<?> table;
    private final boolean ifExists;
    private boolean ifExistsColumn;
    private boolean ifExistsConstraint;
    private boolean ifNotExistsColumn;
    private Comment comment;
    private Table<?> renameTo;
    private Field<?> renameColumn;
    private Field<?> renameColumnTo;
    private Index renameIndex;
    private Index renameIndexTo;
    private Constraint renameConstraint;
    private Constraint renameConstraintTo;
    private QueryPartList<TableElement> add;
    private Field<?> addColumn;
    private DataType<?> addColumnType;
    private Constraint addConstraint;
    private boolean addFirst;
    private Field<?> addBefore;
    private Field<?> addAfter;
    private Constraint alterConstraint;
    private boolean alterConstraintEnforced;
    private Field<?> alterColumn;
    private Nullability alterColumnNullability;
    private DataType<?> alterColumnType;
    private Field<?> alterColumnDefault;
    private boolean alterColumnDropDefault;
    private QueryPartList<Field<?>> dropColumns;
    private Constraint dropConstraint;
    private ConstraintType dropConstraintType;
    private QOM.Cascade dropCascade;

    @Override // org.jooq.AlterTableStep
    public /* bridge */ /* synthetic */ AlterTableDropStep dropColumns(Collection collection) {
        return dropColumns((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.AlterTableStep
    public /* bridge */ /* synthetic */ AlterTableDropStep drop(Collection collection) {
        return drop((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.AlterTableStep
    public /* bridge */ /* synthetic */ AlterTableDropStep dropColumns(Field[] fieldArr) {
        return dropColumns((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.AlterTableStep
    public /* bridge */ /* synthetic */ AlterTableDropStep drop(Field[] fieldArr) {
        return drop((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.AlterTableStep
    public /* bridge */ /* synthetic */ AlterTableDropStep dropColumnIfExists(Field field) {
        return dropColumnIfExists((Field<?>) field);
    }

    @Override // org.jooq.AlterTableStep
    public /* bridge */ /* synthetic */ AlterTableDropStep dropColumn(Field field) {
        return dropColumn((Field<?>) field);
    }

    @Override // org.jooq.AlterTableStep
    public /* bridge */ /* synthetic */ AlterTableDropStep dropIfExists(Field field) {
        return dropIfExists((Field<?>) field);
    }

    @Override // org.jooq.AlterTableStep
    public /* bridge */ /* synthetic */ AlterTableDropStep drop(Field field) {
        return drop((Field<?>) field);
    }

    @Override // org.jooq.AlterTableStep
    public /* bridge */ /* synthetic */ AlterTableAddStep addColumnIfNotExists(String str, DataType dataType) {
        return addColumnIfNotExists(str, (DataType<?>) dataType);
    }

    @Override // org.jooq.AlterTableStep
    public /* bridge */ /* synthetic */ AlterTableAddStep addColumnIfNotExists(Name name, DataType dataType) {
        return addColumnIfNotExists(name, (DataType<?>) dataType);
    }

    @Override // org.jooq.AlterTableStep
    public /* bridge */ /* synthetic */ AlterTableAddStep addColumnIfNotExists(Field field) {
        return addColumnIfNotExists((Field<?>) field);
    }

    @Override // org.jooq.AlterTableStep
    public /* bridge */ /* synthetic */ AlterTableAddStep addColumn(String str, DataType dataType) {
        return addColumn(str, (DataType<?>) dataType);
    }

    @Override // org.jooq.AlterTableStep
    public /* bridge */ /* synthetic */ AlterTableAddStep addColumn(Name name, DataType dataType) {
        return addColumn(name, (DataType<?>) dataType);
    }

    @Override // org.jooq.AlterTableStep
    public /* bridge */ /* synthetic */ AlterTableAddStep addColumn(Field field) {
        return addColumn((Field<?>) field);
    }

    @Override // org.jooq.AlterTableStep
    public /* bridge */ /* synthetic */ AlterTableAddStep addIfNotExists(String str, DataType dataType) {
        return addIfNotExists(str, (DataType<?>) dataType);
    }

    @Override // org.jooq.AlterTableStep
    public /* bridge */ /* synthetic */ AlterTableAddStep addIfNotExists(Name name, DataType dataType) {
        return addIfNotExists(name, (DataType<?>) dataType);
    }

    @Override // org.jooq.AlterTableStep
    public /* bridge */ /* synthetic */ AlterTableAddStep addIfNotExists(Field field) {
        return addIfNotExists((Field<?>) field);
    }

    @Override // org.jooq.AlterTableStep
    public /* bridge */ /* synthetic */ AlterTableAddStep add(String str, DataType dataType) {
        return add(str, (DataType<?>) dataType);
    }

    @Override // org.jooq.AlterTableStep
    public /* bridge */ /* synthetic */ AlterTableAddStep add(Name name, DataType dataType) {
        return add(name, (DataType<?>) dataType);
    }

    @Override // org.jooq.AlterTableStep
    public /* bridge */ /* synthetic */ AlterTableAddStep add(Collection collection) {
        return add((Collection<? extends TableElement>) collection);
    }

    @Override // org.jooq.AlterTableStep
    public /* bridge */ /* synthetic */ AlterTableAddStep add(Field field) {
        return add((Field<?>) field);
    }

    @Override // org.jooq.AlterTableStep
    public /* bridge */ /* synthetic */ AlterTableRenameColumnToStep renameColumn(Field field) {
        return renameColumn((Field<?>) field);
    }

    @Override // org.jooq.AlterTableStep
    public /* bridge */ /* synthetic */ AlterTableFinalStep renameTo(Table table) {
        return renameTo((Table<?>) table);
    }

    @Override // org.jooq.AlterTableAddStep
    public /* bridge */ /* synthetic */ AlterTableFinalStep after(Field field) {
        return after((Field<?>) field);
    }

    @Override // org.jooq.AlterTableAddStep
    public /* bridge */ /* synthetic */ AlterTableFinalStep before(Field field) {
        return before((Field<?>) field);
    }

    @Override // org.jooq.AlterTableRenameColumnToStep
    public /* bridge */ /* synthetic */ AlterTableFinalStep to(Field field) {
        return to((Field<?>) field);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AlterTableImpl(Configuration configuration, Table<?> table) {
        this(configuration, table, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AlterTableImpl(Configuration configuration, Table<?> table, boolean ifExists) {
        super(configuration);
        this.table = table;
        this.ifExists = ifExists;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Table<?> $table() {
        return this.table;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean $ifExists() {
        return this.ifExists;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean $ifExistsColumn() {
        return this.ifExistsColumn;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean $ifExistsConstraint() {
        return this.ifExistsConstraint;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean $ifNotExistsColumn() {
        return this.ifNotExistsColumn;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final List<TableElement> $add() {
        return this.add;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Field<?> $addColumn() {
        return this.addColumn;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final DataType<?> $addColumnType() {
        return this.addColumnType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Constraint $addConstraint() {
        return this.addConstraint;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean $addFirst() {
        return this.addFirst;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Field<?> $addBefore() {
        return this.addBefore;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Field<?> $addAfter() {
        return this.addAfter;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Field<?> $alterColumn() {
        return this.alterColumn;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Nullability $alterColumnNullability() {
        return this.alterColumnNullability;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final DataType<?> $alterColumnType() {
        return this.alterColumnType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Field<?> $alterColumnDefault() {
        return this.alterColumnDefault;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean $alterColumnDropDefault() {
        return this.alterColumnDropDefault;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Constraint $alterConstraint() {
        return this.alterConstraint;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean $alterConstraintEnforced() {
        return this.alterConstraintEnforced;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Table<?> $renameTo() {
        return this.renameTo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Field<?> $renameColumn() {
        return this.renameColumn;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Field<?> $renameColumnTo() {
        return this.renameColumnTo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Constraint $renameConstraint() {
        return this.renameConstraint;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Constraint $renameConstraintTo() {
        return this.renameConstraintTo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final List<Field<?>> $dropColumns() {
        return this.dropColumns;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final QOM.Cascade $dropCascade() {
        return this.dropCascade;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Constraint $dropConstraint() {
        return this.dropConstraint;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ConstraintType $dropConstraintType() {
        return this.dropConstraintType;
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl comment(String c) {
        return comment(DSL.comment(c));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl comment(Comment c) {
        this.comment = c;
        return this;
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl renameTo(Table<?> newName) {
        this.renameTo = newName;
        return this;
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl renameTo(Name newName) {
        return renameTo((Table<?>) DSL.table(newName));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl renameTo(String newName) {
        return renameTo(DSL.name(newName));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl renameColumn(Field<?> oldName) {
        this.renameColumn = oldName;
        return this;
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl renameColumn(Name oldName) {
        return renameColumn(DSL.field(oldName));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl renameColumn(String oldName) {
        return renameColumn(DSL.name(oldName));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl renameConstraint(Constraint oldName) {
        this.renameConstraint = oldName;
        return this;
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl renameIndex(String oldName) {
        return renameIndex(DSL.name(oldName));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl renameIndex(Name oldName) {
        return renameIndex(DSL.index(oldName));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl renameIndex(Index oldName) {
        this.renameIndex = oldName;
        return this;
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl renameConstraint(Name oldName) {
        return renameConstraint((Constraint) DSL.constraint(oldName));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl renameConstraint(String oldName) {
        return renameConstraint(DSL.name(oldName));
    }

    @Override // org.jooq.AlterTableRenameColumnToStep, org.jooq.AlterTableRenameIndexToStep, org.jooq.AlterTableRenameConstraintToStep
    public final AlterTableImpl to(String newName) {
        return to(DSL.name(newName));
    }

    @Override // org.jooq.AlterTableRenameColumnToStep, org.jooq.AlterTableRenameIndexToStep, org.jooq.AlterTableRenameConstraintToStep
    public final AlterTableImpl to(Name newName) {
        if (this.renameColumn != null) {
            return to(DSL.field(newName));
        }
        if (this.renameConstraint != null) {
            return to((Constraint) DSL.constraint(newName));
        }
        if (this.renameIndex != null) {
            return to(DSL.index(newName));
        }
        throw new IllegalStateException();
    }

    @Override // org.jooq.AlterTableRenameColumnToStep
    public final AlterTableImpl to(Field<?> newName) {
        if (this.renameColumn != null) {
            this.renameColumnTo = newName;
            return this;
        }
        throw new IllegalStateException();
    }

    @Override // org.jooq.AlterTableRenameConstraintToStep
    public final AlterTableImpl to(Constraint newName) {
        if (this.renameConstraint != null) {
            this.renameConstraintTo = newName;
            return this;
        }
        throw new IllegalStateException();
    }

    @Override // org.jooq.AlterTableRenameIndexToStep
    public final AlterTableImpl to(Index newName) {
        if (this.renameIndex != null) {
            this.renameIndexTo = newName;
            return this;
        }
        throw new IllegalStateException();
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl add(Field<?> field) {
        return addColumn(field);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl add(TableElement... fields) {
        return add((Collection<? extends TableElement>) Arrays.asList(fields));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl add(Collection<? extends TableElement> fields) {
        if (fields.size() == 1) {
            TableElement first = fields.iterator().next();
            if (first instanceof Field) {
                Field<?> f = (Field) first;
                return add(f);
            }
            if (first instanceof Constraint) {
                Constraint c = (Constraint) first;
                return add(c);
            }
            if (first instanceof Index) {
                throw new UnsupportedOperationException("ALTER TABLE .. ADD INDEX not yet supported, see https://github.com/jOOQ/jOOQ/issues/13006");
            }
        }
        this.add = new QueryPartList<>(fields);
        return this;
    }

    @Override // org.jooq.AlterTableStep
    public final <T> AlterTableImpl add(Field<T> field, DataType<T> type) {
        return addColumn((Field) field, (DataType) type);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl add(Name field, DataType<?> type) {
        return addColumn(field, type);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl add(String field, DataType<?> type) {
        return addColumn(field, type);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl addIfNotExists(Field<?> field) {
        return addColumnIfNotExists(field);
    }

    @Override // org.jooq.AlterTableStep
    public final <T> AlterTableImpl addIfNotExists(Field<T> field, DataType<T> type) {
        return addColumnIfNotExists((Field) field, (DataType) type);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl addIfNotExists(Name field, DataType<?> type) {
        return addColumnIfNotExists(field, type);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl addIfNotExists(String field, DataType<?> type) {
        return addColumnIfNotExists(field, type);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl addColumn(String field, DataType<?> type) {
        return addColumn(DSL.name(field), type);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl addColumn(Name field, DataType<?> type) {
        return addColumn(DSL.field(field, type), (DataType) type);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl addColumn(Field<?> field) {
        return addColumn((Field) field, (DataType) field.getDataType());
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.AlterTableStep
    public final <T> AlterTableImpl addColumn(Field<T> field, DataType<T> dataType) {
        this.addColumn = field;
        this.addColumnType = dataType;
        return this;
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl addColumnIfNotExists(String field, DataType<?> type) {
        return addColumnIfNotExists(DSL.name(field), type);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl addColumnIfNotExists(Name field, DataType<?> type) {
        return addColumnIfNotExists(DSL.field(field, type), (DataType) type);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl addColumnIfNotExists(Field<?> field) {
        return addColumnIfNotExists((Field) field, (DataType) field.getDataType());
    }

    @Override // org.jooq.AlterTableStep
    public final <T> AlterTableImpl addColumnIfNotExists(Field<T> field, DataType<T> type) {
        this.ifNotExistsColumn = true;
        return addColumn((Field) field, (DataType) type);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl add(Constraint constraint) {
        this.addConstraint = constraint;
        return this;
    }

    @Override // org.jooq.AlterTableAddStep
    public final AlterTableImpl first() {
        this.addFirst = true;
        return this;
    }

    @Override // org.jooq.AlterTableAddStep
    public final AlterTableImpl before(String columnName) {
        return before(DSL.name(columnName));
    }

    @Override // org.jooq.AlterTableAddStep
    public final AlterTableImpl before(Name columnName) {
        return before(DSL.field(columnName));
    }

    @Override // org.jooq.AlterTableAddStep
    public final AlterTableImpl before(Field<?> columnName) {
        this.addBefore = columnName;
        return this;
    }

    @Override // org.jooq.AlterTableAddStep
    public final AlterTableImpl after(String columnName) {
        return after(DSL.name(columnName));
    }

    @Override // org.jooq.AlterTableAddStep
    public final AlterTableImpl after(Name columnName) {
        return after(DSL.field(columnName));
    }

    @Override // org.jooq.AlterTableAddStep
    public final AlterTableImpl after(Field<?> columnName) {
        this.addAfter = columnName;
        return this;
    }

    @Override // org.jooq.AlterTableStep
    public final <T> AlterTableImpl alter(Field<T> field) {
        return alterColumn((Field) field);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl alter(Name field) {
        return alterColumn(field);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl alter(String field) {
        return alterColumn(field);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl alterColumn(Name field) {
        return alterColumn((Field) DSL.field(field));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl alterColumn(String field) {
        return alterColumn(DSL.name(field));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.AlterTableStep
    public final <T> AlterTableImpl alterColumn(Field<T> field) {
        this.alterColumn = field;
        return this;
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl alter(Constraint constraint) {
        return alterConstraint(constraint);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl alterConstraint(Name constraint) {
        return alterConstraint((Constraint) DSL.constraint(constraint));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl alterConstraint(String constraint) {
        return alterConstraint((Constraint) DSL.constraint(constraint));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl alterConstraint(Constraint constraint) {
        this.alterConstraint = constraint;
        return this;
    }

    @Override // org.jooq.AlterTableAlterConstraintStep
    public final AlterTableImpl enforced() {
        this.alterConstraintEnforced = true;
        return this;
    }

    @Override // org.jooq.AlterTableAlterConstraintStep
    public final AlterTableImpl notEnforced() {
        this.alterConstraintEnforced = false;
        return this;
    }

    @Override // org.jooq.AlterTableAlterStep
    public final AlterTableImpl set(DataType type) {
        this.alterColumnType = type;
        return this;
    }

    @Override // org.jooq.AlterTableAlterStep
    public final AlterTableImpl setNotNull() {
        this.alterColumnNullability = Nullability.NOT_NULL;
        return this;
    }

    @Override // org.jooq.AlterTableAlterStep
    public final AlterTableImpl dropNotNull() {
        this.alterColumnNullability = Nullability.NULL;
        return this;
    }

    @Override // org.jooq.AlterTableAlterStep
    public final AlterTableImpl defaultValue(Object literal) {
        return setDefault(literal);
    }

    @Override // org.jooq.AlterTableAlterStep
    public final AlterTableImpl defaultValue(Field expression) {
        return setDefault(expression);
    }

    @Override // org.jooq.AlterTableAlterStep
    public final AlterTableImpl default_(Object literal) {
        return setDefault(literal);
    }

    @Override // org.jooq.AlterTableAlterStep
    public final AlterTableImpl default_(Field expression) {
        return setDefault(expression);
    }

    @Override // org.jooq.AlterTableAlterStep
    public final AlterTableImpl setDefault(Object literal) {
        return default_(Tools.field(literal));
    }

    @Override // org.jooq.AlterTableAlterStep
    public final AlterTableImpl setDefault(Field expression) {
        this.alterColumnDefault = expression;
        return this;
    }

    @Override // org.jooq.AlterTableAlterStep
    public final AlterTableImpl dropDefault() {
        this.alterColumnDropDefault = true;
        return this;
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl drop(Field<?> field) {
        return dropColumn(field);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl drop(Name field) {
        return dropColumn(field);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl drop(String field) {
        return dropColumn(field);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropIfExists(Field<?> field) {
        return dropColumnIfExists(field);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropIfExists(Name field) {
        return dropColumnIfExists(field);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropIfExists(String field) {
        return dropColumnIfExists(field);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropColumn(Name field) {
        return dropColumn(DSL.field(field));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropColumn(String field) {
        return dropColumn(DSL.name(field));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropColumn(Field<?> field) {
        return dropColumns0(Collections.singletonList(field));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropColumnIfExists(Name field) {
        return dropColumnIfExists(DSL.field(field));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropColumnIfExists(String field) {
        return dropColumnIfExists(DSL.name(field));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropColumnIfExists(Field<?> field) {
        this.ifExistsColumn = true;
        return dropColumn(field);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl drop(Field<?>... fields) {
        return dropColumns(fields);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl drop(Name... fields) {
        return dropColumns(fields);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl drop(String... fields) {
        return dropColumns(fields);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropColumns(Field<?>... fields) {
        return dropColumns((Collection<? extends Field<?>>) Arrays.asList(fields));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropColumns(Name... fields) {
        return dropColumns((Collection<? extends Field<?>>) Tools.fieldsByName(fields));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropColumns(String... fields) {
        return dropColumns((Collection<? extends Field<?>>) Tools.fieldsByName(fields));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl drop(Collection<? extends Field<?>> fields) {
        return dropColumns(fields);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropColumns(Collection<? extends Field<?>> fields) {
        return dropColumns0(fields);
    }

    private final AlterTableImpl dropColumns0(Collection<? extends Field<?>> fields) {
        this.dropColumns = new QueryPartList<>(fields);
        return this;
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl drop(Constraint constraint) {
        this.dropConstraint = constraint;
        this.dropConstraintType = null;
        return this;
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropConstraint(Constraint constraint) {
        return drop(constraint);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropConstraint(Name constraint) {
        return dropConstraint((Constraint) DSL.constraint(constraint));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropConstraint(String constraint) {
        return dropConstraint((Constraint) DSL.constraint(constraint));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropIfExists(Constraint constraint) {
        this.ifExistsConstraint = true;
        return drop(constraint);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropConstraintIfExists(Constraint constraint) {
        return dropIfExists(constraint);
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropConstraintIfExists(Name constraint) {
        return dropIfExists((Constraint) DSL.constraint(constraint));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropConstraintIfExists(String constraint) {
        return dropIfExists((Constraint) DSL.constraint(constraint));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropPrimaryKey() {
        this.dropConstraintType = ConstraintType.PRIMARY_KEY;
        return this;
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropPrimaryKey(Constraint constraint) {
        this.dropConstraint = constraint;
        this.dropConstraintType = ConstraintType.PRIMARY_KEY;
        return this;
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropPrimaryKey(Name constraint) {
        return dropPrimaryKey((Constraint) DSL.constraint(constraint));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropPrimaryKey(String constraint) {
        return dropPrimaryKey((Constraint) DSL.constraint(constraint));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropUnique(Constraint constraint) {
        this.dropConstraint = constraint;
        this.dropConstraintType = ConstraintType.UNIQUE;
        return this;
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropUnique(Name constraint) {
        return dropUnique((Constraint) DSL.constraint(constraint));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropUnique(String constraint) {
        return dropUnique((Constraint) DSL.constraint(constraint));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropForeignKey(Constraint constraint) {
        this.dropConstraint = constraint;
        this.dropConstraintType = ConstraintType.FOREIGN_KEY;
        return this;
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropForeignKey(Name constraint) {
        return dropForeignKey((Constraint) DSL.constraint(constraint));
    }

    @Override // org.jooq.AlterTableStep
    public final AlterTableImpl dropForeignKey(String constraint) {
        return dropForeignKey((Constraint) DSL.constraint(constraint));
    }

    @Override // org.jooq.AlterTableDropStep
    public final AlterTableFinalStep cascade() {
        this.dropCascade = QOM.Cascade.CASCADE;
        return this;
    }

    @Override // org.jooq.AlterTableDropStep
    public final AlterTableFinalStep restrict() {
        this.dropCascade = QOM.Cascade.RESTRICT;
        return this;
    }

    private final boolean supportsIfExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_EXISTS.contains(ctx.dialect());
    }

    private final boolean supportsIfExistsColumn(Context<?> ctx) {
        return !NO_SUPPORT_IF_EXISTS_COLUMN.contains(ctx.dialect());
    }

    private final boolean supportsIfExistsConstraint(Context<?> ctx) {
        return !NO_SUPPORT_IF_EXISTS_CONSTRAINT.contains(ctx.dialect());
    }

    private final boolean supportsIfNotExistsColumn(Context<?> ctx) {
        return !NO_SUPPORT_IF_NOT_EXISTS_COLUMN.contains(ctx.dialect());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if ((this.ifExists && !supportsIfExists(ctx)) || ((this.ifExistsColumn && !supportsIfExistsColumn(ctx)) || ((this.ifExistsConstraint && !supportsIfExistsConstraint(ctx)) || (this.ifNotExistsColumn && !supportsIfNotExistsColumn(ctx))))) {
            Tools.tryCatch(ctx, DDLStatementType.ALTER_TABLE, this.ifExists ? Boolean.TRUE : null, (this.ifExistsColumn || this.ifExistsConstraint) ? Boolean.TRUE : this.ifNotExistsColumn ? Boolean.FALSE : null, c -> {
                accept0(c);
            });
        } else {
            accept0(ctx);
        }
    }

    private final void accept0(Context<?> ctx) {
        SQLDialect family = ctx.family();
        if (this.comment != null) {
            switch (family) {
                case MARIADB:
                case MYSQL:
                    break;
                default:
                    ctx.visit(DSL.commentOnTable(this.table).is(this.comment));
                    return;
            }
        }
        if (family == SQLDialect.FIREBIRD && this.addFirst) {
            Tools.begin(ctx, c1 -> {
                Tools.executeImmediate(c1, c2 -> {
                    accept1(c2);
                });
                c1.formatSeparator();
                Tools.executeImmediate(c1, c22 -> {
                    c22.visit(Keywords.K_ALTER_TABLE).sql(' ').visit(this.table).sql(' ').visit(Keywords.K_ALTER).sql(' ').visit(this.addColumn).sql(' ').visit(Keywords.K_POSITION).sql(" 1");
                });
            });
            return;
        }
        if (this.renameIndexTo != null) {
            switch (family) {
                case MARIADB:
                case MYSQL:
                    break;
                default:
                    ctx.visit(DSL.alterIndex(this.renameIndex).renameTo(this.renameIndexTo));
                    return;
            }
        }
        if (this.alterColumnType != null && this.alterColumnType.nullability() != Nullability.DEFAULT) {
            switch (family) {
                case POSTGRES:
                case YUGABYTEDB:
                    alterColumnTypeAndNullabilityInBlock(ctx);
                    return;
            }
        }
        if (CreateTableImpl.EMULATE_COLUMN_COMMENT_IN_BLOCK.contains(ctx.dialect())) {
            List<Field<?>> comments = addColumnComments();
            if (!comments.isEmpty()) {
                Tools.begin(ctx, c12 -> {
                    Tools.executeImmediateIf(CreateTableImpl.REQUIRE_EXECUTE_IMMEDIATE.contains(c12.dialect()), c12, c2 -> {
                        accept1(c2);
                    });
                    c12.formatSeparator();
                    Iterator it = comments.iterator();
                    while (it.hasNext()) {
                        Field<?> c = (Field) it.next();
                        Tools.executeImmediateIf(CreateTableImpl.REQUIRE_EXECUTE_IMMEDIATE.contains(ctx.dialect()), c12, c22 -> {
                            c22.visit(DSL.commentOnColumn(this.table.getQualifiedName().append(c.getUnqualifiedName())).is(c.getComment()));
                        });
                    }
                });
                return;
            }
        }
        accept1(ctx);
    }

    private final List<Field<?>> addColumnComments() {
        if (this.addColumn != null) {
            if (!this.addColumn.getComment().isEmpty()) {
                return Arrays.asList(this.addColumn);
            }
        } else if (this.add != null) {
            return Tools.map(Tools.filter(this.add, c -> {
                return (c instanceof Field) && !c.getComment().isEmpty();
            }), c2 -> {
                return (Field) c2;
            });
        }
        return Collections.emptyList();
    }

    /* JADX WARN: Code restructure failed: missing block: B:157:0x0724, code lost:            r8.sql(' ').visit(org.jooq.impl.Keywords.K_SET_DEFAULT);        r8.sql(' ').visit(r7.alterColumnDefault).end(org.jooq.Clause.ALTER_TABLE_ALTER_DEFAULT);     */
    /* JADX WARN: Code restructure failed: missing block: B:169:0x07ec, code lost:            r0 = r8.sql(' ');     */
    /* JADX WARN: Code restructure failed: missing block: B:170:0x07fb, code lost:            if (r7.alterColumnNullability.nullable() == false) goto L167;     */
    /* JADX WARN: Code restructure failed: missing block: B:171:0x07fe, code lost:            r1 = org.jooq.impl.Keywords.K_DROP_NOT_NULL;     */
    /* JADX WARN: Code restructure failed: missing block: B:172:0x0807, code lost:            r0.visit(r1);        r8.end(org.jooq.Clause.ALTER_TABLE_ALTER_NULL);     */
    /* JADX WARN: Code restructure failed: missing block: B:173:0x0804, code lost:            r1 = org.jooq.impl.Keywords.K_SET_NOT_NULL;     */
    /* JADX WARN: Type inference failed for: r0v125, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v134, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v139, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v148, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v151, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v168, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v171, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v174, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v177, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v186, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v198, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v205, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v211, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v221, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v230, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v248, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v266, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v270, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v273, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v289, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v303, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v311, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v319, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v329, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v344, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v355, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v359, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v364, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v371, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v47, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v72, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v92, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v98, types: [org.jooq.Context] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final void accept1(org.jooq.Context<?> r8) {
        /*
            Method dump skipped, instructions count: 2417
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.AlterTableImpl.accept1(org.jooq.Context):void");
    }

    private final boolean unqualifyRenameTo(Context<?> ctx) {
        return NO_SUPPORT_RENAME_QUALIFIED_TABLE.contains(ctx.dialect()) && this.renameTo.getQualifiedName().qualified() && this.renameTo.getQualifiedName().qualifier().equals(this.table.getQualifiedName().qualifier());
    }

    private final Keyword addColumnKeyword(Context<?> ctx) {
        switch (ctx.family()) {
            case TRINO:
                return Keywords.K_ADD_COLUMN;
            default:
                return Keywords.K_ADD;
        }
    }

    private final void acceptCascade(Context<?> ctx) {
        switch (ctx.family()) {
            case H2:
                acceptCascade(ctx, this.dropCascade);
                return;
            default:
                acceptCascade(ctx, this.dropCascade);
                return;
        }
    }

    /* JADX WARN: Type inference failed for: r0v12, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v19, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    private final void acceptFirstBeforeAfter(Context<?> ctx) {
        if (this.addFirst && ctx.family() != SQLDialect.FIREBIRD) {
            ctx.sql(' ').visit(Keywords.K_FIRST);
        } else if (this.addBefore != null) {
            ctx.sql(' ').visit(Keywords.K_BEFORE).sql(' ').qualify(false, c -> {
                c.visit(this.addBefore);
            });
        } else if (this.addAfter != null) {
            ctx.sql(' ').visit(Keywords.K_AFTER).sql(' ').qualify(false, c2 -> {
                c2.visit(this.addAfter);
            });
        }
    }

    private final void acceptDropColumn(Context<?> ctx) {
        switch (ctx.family()) {
            case TRINO:
                ctx.visit(Keywords.K_DROP_COLUMN);
                return;
            default:
                ctx.visit(Keywords.K_DROP);
                return;
        }
    }

    private final void alterColumnTypeAndNullabilityInBlock(Context<?> ctx) {
        Tools.begin(ctx, c1 -> {
            accept1(c1);
            c1.sql(';').formatSeparator();
            switch (c1.family()) {
                case POSTGRES:
                case YUGABYTEDB:
                    AlterTableAlterStep<?> step = c1.dsl().alterTable(this.table).alterColumn(this.alterColumn);
                    c1.visit(this.alterColumnType.nullable() ? step.dropNotNull() : step.setNotNull()).sql(';');
                    return;
                default:
                    return;
            }
        });
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }
}
