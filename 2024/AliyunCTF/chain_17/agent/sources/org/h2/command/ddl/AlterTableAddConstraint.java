package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.api.ErrorCode;
import org.h2.constraint.Constraint;
import org.h2.constraint.ConstraintActionType;
import org.h2.constraint.ConstraintCheck;
import org.h2.constraint.ConstraintReferential;
import org.h2.constraint.ConstraintUnique;
import org.h2.engine.Constants;
import org.h2.engine.Database;
import org.h2.engine.NullsDistinct;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.index.Index;
import org.h2.index.IndexType;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.value.DataType;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/AlterTableAddConstraint.class */
public class AlterTableAddConstraint extends AlterTable {
    private final int type;
    private String constraintName;
    private NullsDistinct nullsDistinct;
    private IndexColumn[] indexColumns;
    private ConstraintActionType deleteAction;
    private ConstraintActionType updateAction;
    private Schema refSchema;
    private String refTableName;
    private IndexColumn[] refIndexColumns;
    private Expression checkExpression;
    private Index index;
    private Index refIndex;
    private String comment;
    private boolean checkExisting;
    private boolean primaryKeyHash;
    private final boolean ifNotExists;
    private final ArrayList<Index> createdIndexes;
    private ConstraintUnique createdUniqueConstraint;

    public AlterTableAddConstraint(SessionLocal sessionLocal, Schema schema, int i, boolean z) {
        super(sessionLocal, schema);
        this.deleteAction = ConstraintActionType.RESTRICT;
        this.updateAction = ConstraintActionType.RESTRICT;
        this.createdIndexes = new ArrayList<>();
        this.ifNotExists = z;
        this.type = i;
    }

    private String generateConstraintName(Table table) {
        if (this.constraintName == null) {
            this.constraintName = getSchema().getUniqueConstraintName(this.session, table);
        }
        return this.constraintName;
    }

    @Override // org.h2.command.ddl.AlterTable
    public long update(Table table) {
        try {
            try {
                long tryUpdate = tryUpdate(table);
                getSchema().freeUniqueName(this.constraintName);
                return tryUpdate;
            } catch (DbException e) {
                try {
                    if (this.createdUniqueConstraint != null) {
                        Index index = this.createdUniqueConstraint.getIndex();
                        getDatabase().removeSchemaObject(this.session, this.createdUniqueConstraint);
                        this.createdIndexes.remove(index);
                    }
                    Iterator<Index> it = this.createdIndexes.iterator();
                    while (it.hasNext()) {
                        getDatabase().removeSchemaObject(this.session, it.next());
                    }
                } catch (Throwable th) {
                    e.addSuppressed(th);
                }
                throw e;
            }
        } catch (Throwable th2) {
            getSchema().freeUniqueName(this.constraintName);
            throw th2;
        }
    }

    private int tryUpdate(Table table) {
        Constraint constraint;
        if (this.constraintName != null && getSchema().findConstraint(this.session, this.constraintName) != null) {
            if (this.ifNotExists) {
                return 0;
            }
            if (!this.session.isQuirksMode()) {
                throw DbException.get(ErrorCode.CONSTRAINT_ALREADY_EXISTS_1, this.constraintName);
            }
            this.constraintName = null;
        }
        Database database = getDatabase();
        database.lockMeta(this.session);
        table.lock(this.session, 2);
        switch (this.type) {
            case 3:
                ConstraintCheck constraintCheck = new ConstraintCheck(getSchema(), getObjectId(), generateConstraintName(table), table);
                TableFilter tableFilter = new TableFilter(this.session, table, null, false, null, 0, null);
                this.checkExpression.mapColumns(tableFilter, 0, 0);
                this.checkExpression = this.checkExpression.optimize(this.session);
                constraintCheck.setExpression(this.checkExpression);
                constraintCheck.setTableFilter(tableFilter);
                constraint = constraintCheck;
                if (this.checkExisting) {
                    constraintCheck.checkExistingData(this.session);
                    break;
                }
                break;
            case 4:
                if (this.indexColumns == null) {
                    Column[] columns = table.getColumns();
                    ArrayList arrayList = new ArrayList(columns.length);
                    for (Column column : columns) {
                        if (column.getVisible()) {
                            IndexColumn indexColumn = new IndexColumn(column.getName());
                            indexColumn.column = column;
                            arrayList.add(indexColumn);
                        }
                    }
                    if (arrayList.isEmpty()) {
                        throw DbException.get(ErrorCode.SYNTAX_ERROR_1, "UNIQUE(VALUE) on table without columns");
                    }
                    this.indexColumns = (IndexColumn[]) arrayList.toArray(new IndexColumn[0]);
                } else {
                    IndexColumn.mapColumns(this.indexColumns, table);
                }
                constraint = createUniqueConstraint(table, this.index, this.indexColumns, false);
                break;
            case 5:
                Table resolveTableOrView = this.refSchema.resolveTableOrView(this.session, this.refTableName);
                if (resolveTableOrView == null) {
                    throw DbException.get(ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1, this.refTableName);
                }
                if (resolveTableOrView != table) {
                    this.session.getUser().checkTableRight(resolveTableOrView, 32);
                }
                if (!resolveTableOrView.canReference()) {
                    StringBuilder sb = new StringBuilder("Reference ");
                    resolveTableOrView.getSQL(sb, 3);
                    throw DbException.getUnsupportedException(sb.toString());
                }
                boolean z = false;
                IndexColumn.mapColumns(this.indexColumns, table);
                if (this.refIndexColumns == null) {
                    this.refIndexColumns = resolveTableOrView.getPrimaryKey().getIndexColumns();
                } else {
                    IndexColumn.mapColumns(this.refIndexColumns, resolveTableOrView);
                }
                int length = this.indexColumns.length;
                if (this.refIndexColumns.length != length) {
                    throw DbException.get(ErrorCode.COLUMN_COUNT_DOES_NOT_MATCH);
                }
                for (IndexColumn indexColumn2 : this.indexColumns) {
                    Column column2 = indexColumn2.column;
                    if (column2.isGeneratedAlways()) {
                        switch (this.deleteAction) {
                            case SET_DEFAULT:
                            case SET_NULL:
                                throw DbException.get(ErrorCode.GENERATED_COLUMN_CANNOT_BE_UPDATABLE_BY_CONSTRAINT_2, column2.getSQLWithTable(new StringBuilder(), 3).toString(), "ON DELETE " + this.deleteAction.getSqlName());
                            default:
                                switch (this.updateAction) {
                                    case SET_DEFAULT:
                                    case SET_NULL:
                                    case CASCADE:
                                        throw DbException.get(ErrorCode.GENERATED_COLUMN_CANNOT_BE_UPDATABLE_BY_CONSTRAINT_2, column2.getSQLWithTable(new StringBuilder(), 3).toString(), "ON UPDATE " + this.updateAction.getSqlName());
                                }
                        }
                    }
                }
                for (int i = 0; i < length; i++) {
                    Column column3 = this.indexColumns[i].column;
                    Column column4 = this.refIndexColumns[i].column;
                    if (!DataType.areStableComparable(column3.getType(), column4.getType())) {
                        throw DbException.get(ErrorCode.UNCOMPARABLE_REFERENCED_COLUMN_2, column3.getCreateSQL(), column4.getCreateSQL());
                    }
                }
                ConstraintUnique uniqueConstraint = getUniqueConstraint(resolveTableOrView, this.refIndexColumns);
                if (uniqueConstraint == null && !this.session.isQuirksMode() && !this.session.getMode().createUniqueConstraintForReferencedColumns) {
                    throw DbException.get(ErrorCode.CONSTRAINT_NOT_FOUND_1, IndexColumn.writeColumns(new StringBuilder("PRIMARY KEY | UNIQUE ("), this.refIndexColumns, 3).append(')').toString());
                }
                if (this.index != null && canUseIndex(this.index, table, this.indexColumns, null)) {
                    z = true;
                    this.index.getIndexType().setBelongsToConstraint(true);
                } else {
                    this.index = getIndex(table, this.indexColumns, null);
                    if (this.index == null) {
                        this.index = createIndex(table, this.indexColumns, null);
                        z = true;
                    }
                }
                ConstraintReferential constraintReferential = new ConstraintReferential(getSchema(), getObjectId(), generateConstraintName(table), table);
                constraintReferential.setColumns(this.indexColumns);
                constraintReferential.setIndex(this.index, z);
                constraintReferential.setRefTable(resolveTableOrView);
                constraintReferential.setRefColumns(this.refIndexColumns);
                if (uniqueConstraint == null) {
                    uniqueConstraint = createUniqueConstraint(resolveTableOrView, this.refIndex, this.refIndexColumns, true);
                    addConstraintToTable(database, resolveTableOrView, uniqueConstraint);
                    this.createdUniqueConstraint = uniqueConstraint;
                }
                constraintReferential.setRefConstraint(uniqueConstraint);
                if (this.checkExisting) {
                    constraintReferential.checkExistingData(this.session);
                }
                resolveTableOrView.addConstraint(constraintReferential);
                constraintReferential.setDeleteAction(this.deleteAction);
                constraintReferential.setUpdateAction(this.updateAction);
                constraint = constraintReferential;
                break;
                break;
            case 6:
                IndexColumn.mapColumns(this.indexColumns, table);
                this.index = table.findPrimaryKey();
                ArrayList<Constraint> constraints = table.getConstraints();
                for (int i2 = 0; constraints != null && i2 < constraints.size(); i2++) {
                    if (Constraint.Type.PRIMARY_KEY == constraints.get(i2).getConstraintType()) {
                        throw DbException.get(ErrorCode.SECOND_PRIMARY_KEY);
                    }
                }
                if (this.index != null) {
                    IndexColumn[] indexColumns = this.index.getIndexColumns();
                    if (indexColumns.length != this.indexColumns.length) {
                        throw DbException.get(ErrorCode.SECOND_PRIMARY_KEY);
                    }
                    for (int i3 = 0; i3 < indexColumns.length; i3++) {
                        if (indexColumns[i3].column != this.indexColumns[i3].column) {
                            throw DbException.get(ErrorCode.SECOND_PRIMARY_KEY);
                        }
                    }
                } else {
                    IndexType createPrimaryKey = IndexType.createPrimaryKey(table.isPersistIndexes(), this.primaryKeyHash);
                    String uniqueIndexName = table.getSchema().getUniqueIndexName(this.session, table, Constants.PREFIX_PRIMARY_KEY);
                    try {
                        this.index = table.addIndex(this.session, uniqueIndexName, getDatabase().allocateObjectId(), this.indexColumns, this.indexColumns.length, createPrimaryKey, true, null);
                        getSchema().freeUniqueName(uniqueIndexName);
                    } catch (Throwable th) {
                        getSchema().freeUniqueName(uniqueIndexName);
                        throw th;
                    }
                }
                this.index.getIndexType().setBelongsToConstraint(true);
                ConstraintUnique constraintUnique = new ConstraintUnique(getSchema(), getObjectId(), generateConstraintName(table), table, true, null);
                constraintUnique.setColumns(this.indexColumns);
                constraintUnique.setIndex(this.index, true);
                constraint = constraintUnique;
                break;
            default:
                throw DbException.getInternalError("type=" + this.type);
        }
        constraint.setComment(this.comment);
        addConstraintToTable(database, table, constraint);
        return 0;
    }

    private ConstraintUnique createUniqueConstraint(Table table, Index index, IndexColumn[] indexColumnArr, boolean z) {
        int objectId;
        String generateConstraintName;
        boolean z2 = false;
        NullsDistinct nullsDistinct = this.nullsDistinct != null ? this.nullsDistinct : NullsDistinct.DISTINCT;
        if (index != null && canUseIndex(index, table, indexColumnArr, nullsDistinct)) {
            z2 = true;
            index.getIndexType().setBelongsToConstraint(true);
        } else {
            index = getIndex(table, indexColumnArr, nullsDistinct);
            if (index == null) {
                index = createIndex(table, indexColumnArr, this.nullsDistinct != null ? this.nullsDistinct : this.session.getMode().nullsDistinct);
                z2 = true;
            }
        }
        Schema schema = table.getSchema();
        if (z) {
            objectId = getDatabase().allocateObjectId();
            try {
                schema.reserveUniqueName(this.constraintName);
                generateConstraintName = schema.getUniqueConstraintName(this.session, table);
                schema.freeUniqueName(this.constraintName);
            } catch (Throwable th) {
                schema.freeUniqueName(this.constraintName);
                throw th;
            }
        } else {
            objectId = getObjectId();
            generateConstraintName = generateConstraintName(table);
        }
        if (indexColumnArr.length == 1 && nullsDistinct == NullsDistinct.ALL_DISTINCT) {
            nullsDistinct = NullsDistinct.DISTINCT;
        }
        ConstraintUnique constraintUnique = new ConstraintUnique(schema, objectId, generateConstraintName, table, false, nullsDistinct);
        constraintUnique.setColumns(indexColumnArr);
        constraintUnique.setIndex(index, z2);
        return constraintUnique;
    }

    private void addConstraintToTable(Database database, Table table, Constraint constraint) {
        if (table.isTemporary() && !table.isGlobalTemporary()) {
            this.session.addLocalTempTableConstraint(constraint);
        } else {
            database.addSchemaObject(this.session, constraint);
        }
        table.addConstraint(constraint);
    }

    private Index createIndex(Table table, IndexColumn[] indexColumnArr, NullsDistinct nullsDistinct) {
        IndexType createNonUnique;
        int allocateObjectId = getDatabase().allocateObjectId();
        if (nullsDistinct != null) {
            createNonUnique = IndexType.createUnique(table.isPersistIndexes(), false, indexColumnArr.length, nullsDistinct);
        } else {
            createNonUnique = IndexType.createNonUnique(table.isPersistIndexes());
        }
        createNonUnique.setBelongsToConstraint(true);
        String uniqueIndexName = table.getSchema().getUniqueIndexName(this.session, table, (this.constraintName == null ? "CONSTRAINT" : this.constraintName) + "_INDEX_");
        try {
            Index addIndex = table.addIndex(this.session, uniqueIndexName, allocateObjectId, indexColumnArr, nullsDistinct != null ? indexColumnArr.length : 0, createNonUnique, true, null);
            this.createdIndexes.add(addIndex);
            getSchema().freeUniqueName(uniqueIndexName);
            return addIndex;
        } catch (Throwable th) {
            getSchema().freeUniqueName(uniqueIndexName);
            throw th;
        }
    }

    public void setDeleteAction(ConstraintActionType constraintActionType) {
        this.deleteAction = constraintActionType;
    }

    public void setUpdateAction(ConstraintActionType constraintActionType) {
        this.updateAction = constraintActionType;
    }

    private static ConstraintUnique getUniqueConstraint(Table table, IndexColumn[] indexColumnArr) {
        Constraint.Type constraintType;
        ArrayList<Constraint> constraints = table.getConstraints();
        if (constraints != null) {
            Iterator<Constraint> it = constraints.iterator();
            while (it.hasNext()) {
                Constraint next = it.next();
                if (next.getTable() == table && ((constraintType = next.getConstraintType()) == Constraint.Type.PRIMARY_KEY || constraintType == Constraint.Type.UNIQUE)) {
                    if (canUseIndex(next.getIndex(), table, indexColumnArr, NullsDistinct.DISTINCT)) {
                        return (ConstraintUnique) next;
                    }
                }
            }
            return null;
        }
        return null;
    }

    private static Index getIndex(Table table, IndexColumn[] indexColumnArr, NullsDistinct nullsDistinct) {
        ArrayList<Index> indexes = table.getIndexes();
        Index index = null;
        if (indexes != null) {
            Iterator<Index> it = indexes.iterator();
            while (it.hasNext()) {
                Index next = it.next();
                if (canUseIndex(next, table, indexColumnArr, nullsDistinct) && (index == null || next.getIndexColumns().length < index.getIndexColumns().length)) {
                    index = next;
                }
            }
        }
        return index;
    }

    private static boolean canUseIndex(Index index, Table table, IndexColumn[] indexColumnArr, NullsDistinct nullsDistinct) {
        int i;
        if (index.getTable() != table) {
            return false;
        }
        if (nullsDistinct != null) {
            i = index.getUniqueColumnCount();
            if (i != indexColumnArr.length || index.getIndexType().getEffectiveNullsDistinct().compareTo(nullsDistinct) < 0) {
                return false;
            }
        } else {
            if (index.getCreateSQL() == null) {
                return false;
            }
            int length = index.getColumns().length;
            i = length;
            if (length != indexColumnArr.length) {
                return false;
            }
        }
        for (IndexColumn indexColumn : indexColumnArr) {
            int columnIndex = index.getColumnIndex(indexColumn.column);
            if (columnIndex < 0 || columnIndex >= i) {
                return false;
            }
        }
        return true;
    }

    public void setConstraintName(String str) {
        this.constraintName = str;
    }

    public String getConstraintName() {
        return this.constraintName;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return this.type;
    }

    public void setNullsDistinct(NullsDistinct nullsDistinct) {
        this.nullsDistinct = nullsDistinct;
    }

    public void setCheckExpression(Expression expression) {
        this.checkExpression = expression;
    }

    public void setIndexColumns(IndexColumn[] indexColumnArr) {
        this.indexColumns = indexColumnArr;
    }

    public IndexColumn[] getIndexColumns() {
        return this.indexColumns;
    }

    public void setRefTableName(Schema schema, String str) {
        this.refSchema = schema;
        this.refTableName = str;
    }

    public void setRefIndexColumns(IndexColumn[] indexColumnArr) {
        this.refIndexColumns = indexColumnArr;
    }

    public void setIndex(Index index) {
        this.index = index;
    }

    public void setRefIndex(Index index) {
        this.refIndex = index;
    }

    public void setComment(String str) {
        this.comment = str;
    }

    public void setCheckExisting(boolean z) {
        this.checkExisting = z;
    }

    public void setPrimaryKeyHash(boolean z) {
        this.primaryKeyHash = z;
    }
}
