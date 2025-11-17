package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.h2.api.ErrorCode;
import org.h2.command.CommandContainer;
import org.h2.command.Parser;
import org.h2.command.ParserBase;
import org.h2.constraint.Constraint;
import org.h2.constraint.ConstraintReferential;
import org.h2.constraint.ConstraintUnique;
import org.h2.engine.Constants;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.schema.Schema;
import org.h2.schema.SchemaObject;
import org.h2.schema.Sequence;
import org.h2.schema.TriggerObject;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.table.TableBase;
import org.h2.table.TableView;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/AlterTableAlterColumn.class */
public class AlterTableAlterColumn extends CommandWithColumns {
    private String tableName;
    private Column oldColumn;
    private Column newColumn;
    private int type;
    private Expression defaultExpression;
    private Expression newSelectivity;
    private Expression usingExpression;
    private boolean addFirst;
    private String addBefore;
    private String addAfter;
    private boolean ifTableExists;
    private boolean ifNotExists;
    private ArrayList<Column> columnsToAdd;
    private ArrayList<Column> columnsToRemove;
    private boolean booleanFlag;

    public AlterTableAlterColumn(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
    }

    public void setIfTableExists(boolean z) {
        this.ifTableExists = z;
    }

    public void setTableName(String str) {
        this.tableName = str;
    }

    public void setOldColumn(Column column) {
        this.oldColumn = column;
    }

    public void setAddFirst() {
        this.addFirst = true;
    }

    public void setAddBefore(String str) {
        this.addBefore = str;
    }

    public void setAddAfter(String str) {
        this.addAfter = str;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        Sequence sequence;
        Database database = getDatabase();
        Table resolveTableOrView = getSchema().resolveTableOrView(this.session, this.tableName);
        if (resolveTableOrView == null) {
            if (this.ifTableExists) {
                return 0L;
            }
            throw DbException.get(ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1, this.tableName);
        }
        this.session.getUser().checkTableRight(resolveTableOrView, 32);
        resolveTableOrView.checkSupportAlter();
        resolveTableOrView.lock(this.session, 2);
        if (this.newColumn != null) {
            checkDefaultReferencesTable(resolveTableOrView, this.newColumn.getDefaultExpression());
            checkClustering(this.newColumn);
        }
        if (this.columnsToAdd != null) {
            Iterator<Column> it = this.columnsToAdd.iterator();
            while (it.hasNext()) {
                Column next = it.next();
                checkDefaultReferencesTable(resolveTableOrView, next.getDefaultExpression());
                checkClustering(next);
            }
        }
        switch (this.type) {
            case 7:
                if (!this.ifNotExists || this.columnsToAdd == null || this.columnsToAdd.size() != 1 || !resolveTableOrView.doesColumnExist(this.columnsToAdd.get(0).getName())) {
                    ArrayList<Sequence> generateSequences = generateSequences(this.columnsToAdd, false);
                    if (this.columnsToAdd != null) {
                        changePrimaryKeysToNotNull(this.columnsToAdd);
                    }
                    copyData(resolveTableOrView, generateSequences, true);
                    return 0L;
                }
                return 0L;
            case 8:
                if (this.oldColumn != null && this.oldColumn.isNullable()) {
                    checkNoNullValues(resolveTableOrView);
                    this.oldColumn.setNullable(false);
                    database.updateMeta(this.session, resolveTableOrView);
                    return 0L;
                }
                return 0L;
            case 9:
                if (this.oldColumn != null && !this.oldColumn.isNullable()) {
                    checkNullable(resolveTableOrView);
                    this.oldColumn.setNullable(true);
                    database.updateMeta(this.session, resolveTableOrView);
                    return 0L;
                }
                return 0L;
            case 10:
            case 98:
                if (this.oldColumn != null && !this.oldColumn.isIdentity()) {
                    if (this.defaultExpression != null) {
                        if (!this.oldColumn.isGenerated()) {
                            checkDefaultReferencesTable(resolveTableOrView, this.defaultExpression);
                            this.oldColumn.setDefaultExpression(this.session, this.defaultExpression);
                        } else {
                            return 0L;
                        }
                    } else {
                        if ((this.type == 98) == this.oldColumn.isGenerated()) {
                            this.oldColumn.setDefaultExpression(this.session, null);
                        } else {
                            return 0L;
                        }
                    }
                    database.updateMeta(this.session, resolveTableOrView);
                    return 0L;
                }
                return 0L;
            case 11:
                if (this.oldColumn != null) {
                    if (this.oldColumn.isWideningConversion(this.newColumn) && this.usingExpression == null) {
                        convertIdentityColumn(resolveTableOrView, this.oldColumn, this.newColumn);
                        this.oldColumn.copy(this.newColumn);
                        database.updateMeta(this.session, resolveTableOrView);
                    } else {
                        this.oldColumn.setSequence(null, false);
                        this.oldColumn.setDefaultExpression(this.session, null);
                        if (this.oldColumn.isNullable() && !this.newColumn.isNullable()) {
                            checkNoNullValues(resolveTableOrView);
                        } else if (!this.oldColumn.isNullable() && this.newColumn.isNullable()) {
                            checkNullable(resolveTableOrView);
                        }
                        if (this.oldColumn.getVisible() ^ this.newColumn.getVisible()) {
                            this.oldColumn.setVisible(this.newColumn.getVisible());
                        }
                        convertIdentityColumn(resolveTableOrView, this.oldColumn, this.newColumn);
                        copyData(resolveTableOrView, null, true);
                    }
                    resolveTableOrView.setModified();
                    return 0L;
                }
                return 0L;
            case 12:
                if (resolveTableOrView.getColumns().length - this.columnsToRemove.size() < 1) {
                    throw DbException.get(ErrorCode.CANNOT_DROP_LAST_COLUMN, this.columnsToRemove.get(0).getTraceSQL());
                }
                resolveTableOrView.dropMultipleColumnsConstraintsAndIndexes(this.session, this.columnsToRemove);
                copyData(resolveTableOrView, null, false);
                return 0L;
            case 13:
                if (this.oldColumn != null) {
                    this.oldColumn.setSelectivity(this.newSelectivity.optimize(this.session).getValue(this.session).getInt());
                    database.updateMeta(this.session, resolveTableOrView);
                    return 0L;
                }
                return 0L;
            case 87:
                if (this.oldColumn != null && this.oldColumn.getVisible() != this.booleanFlag) {
                    this.oldColumn.setVisible(this.booleanFlag);
                    resolveTableOrView.setModified();
                    database.updateMeta(this.session, resolveTableOrView);
                    return 0L;
                }
                return 0L;
            case 90:
                if (this.oldColumn != null) {
                    if (this.defaultExpression != null) {
                        if (!this.oldColumn.isIdentity() && !this.oldColumn.isGenerated()) {
                            checkDefaultReferencesTable(resolveTableOrView, this.defaultExpression);
                            this.oldColumn.setOnUpdateExpression(this.session, this.defaultExpression);
                        } else {
                            return 0L;
                        }
                    } else {
                        this.oldColumn.setOnUpdateExpression(this.session, null);
                    }
                    database.updateMeta(this.session, resolveTableOrView);
                    return 0L;
                }
                return 0L;
            case 99:
                if (this.oldColumn != null && (sequence = this.oldColumn.getSequence()) != null) {
                    this.oldColumn.setSequence(null, false);
                    removeSequence(resolveTableOrView, sequence);
                    database.updateMeta(this.session, resolveTableOrView);
                    return 0L;
                }
                return 0L;
            case 100:
                if (this.oldColumn != null && this.oldColumn.isDefaultOnNull() != this.booleanFlag) {
                    this.oldColumn.setDefaultOnNull(this.booleanFlag);
                    resolveTableOrView.setModified();
                    database.updateMeta(this.session, resolveTableOrView);
                    return 0L;
                }
                return 0L;
            default:
                throw DbException.getInternalError("type=" + this.type);
        }
    }

    private static void checkDefaultReferencesTable(Table table, Expression expression) {
        if (expression == null) {
            return;
        }
        HashSet hashSet = new HashSet();
        expression.isEverything(ExpressionVisitor.getDependenciesVisitor(hashSet));
        if (hashSet.contains(table)) {
            throw DbException.get(ErrorCode.COLUMN_IS_REFERENCED_1, expression.getTraceSQL());
        }
    }

    private void checkClustering(Column column) {
        if (!Constants.CLUSTERING_DISABLED.equals(getDatabase().getCluster()) && column.hasIdentityOptions()) {
            throw DbException.getUnsupportedException("CLUSTERING && identity columns");
        }
    }

    private void convertIdentityColumn(Table table, Column column, Column column2) {
        if (column2.hasIdentityOptions()) {
            if (column2.isPrimaryKey() && !column.isPrimaryKey()) {
                addConstraintCommand(Parser.newPrimaryKeyConstraintCommand(this.session, table.getSchema(), table.getName(), column2));
            }
            column2.initializeSequence(this.session, getSchema(), getObjectId(), table.isTemporary());
        }
    }

    private void removeSequence(Table table, Sequence sequence) {
        if (sequence != null) {
            table.removeSequence(sequence);
            sequence.setBelongsToTable(false);
            getDatabase().removeSchemaObject(this.session, sequence);
        }
    }

    private void copyData(Table table, ArrayList<Sequence> arrayList, boolean z) {
        String name;
        if (table.isTemporary()) {
            throw DbException.getUnsupportedException("TEMP TABLE");
        }
        Database database = getDatabase();
        String tempTableName = database.getTempTableName(table.getName(), this.session);
        Column[] columns = table.getColumns();
        Table cloneTableStructure = cloneTableStructure(table, columns, database, tempTableName, new ArrayList<>(columns.length));
        if (arrayList != null) {
            Iterator<Sequence> it = arrayList.iterator();
            while (it.hasNext()) {
                table.addSequence(it.next());
            }
        }
        try {
            checkViews(table, cloneTableStructure);
            String name2 = table.getName();
            ArrayList arrayList2 = new ArrayList(table.getDependentViews());
            Iterator it2 = arrayList2.iterator();
            while (it2.hasNext()) {
                table.removeDependentView((TableView) it2.next());
            }
            StringBuilder sb = new StringBuilder("DROP TABLE ");
            table.getSQL(sb, 0).append(" IGNORE");
            execute(sb.toString());
            database.renameSchemaObject(this.session, cloneTableStructure, name2);
            Iterator<DbObject> it3 = cloneTableStructure.getChildren().iterator();
            while (it3.hasNext()) {
                DbObject next = it3.next();
                if (!(next instanceof Sequence) && (name = next.getName()) != null && next.getCreateSQL() != null && name.startsWith(tempTableName + "_")) {
                    String substring = name.substring(tempTableName.length() + 1);
                    SchemaObject schemaObject = (SchemaObject) next;
                    if (schemaObject instanceof Constraint) {
                        if (schemaObject.getSchema().findConstraint(this.session, substring) != null) {
                            substring = schemaObject.getSchema().getUniqueConstraintName(this.session, cloneTableStructure);
                        }
                    } else if ((schemaObject instanceof Index) && schemaObject.getSchema().findIndex(this.session, substring) != null) {
                        substring = schemaObject.getSchema().getUniqueIndexName(this.session, cloneTableStructure, substring);
                    }
                    database.renameSchemaObject(this.session, schemaObject, substring);
                }
            }
            if (z) {
                createConstraints();
            }
            Iterator it4 = arrayList2.iterator();
            while (it4.hasNext()) {
                execute(((TableView) it4.next()).getCreateSQL(true, true));
            }
        } catch (DbException e) {
            StringBuilder sb2 = new StringBuilder("DROP TABLE ");
            cloneTableStructure.getSQL(sb2, 0);
            execute(sb2.toString());
            throw e;
        }
    }

    private Table cloneTableStructure(Table table, Column[] columnArr, Database database, String str, ArrayList<Column> arrayList) {
        int length;
        for (Column column : columnArr) {
            arrayList.add(column.getClone());
        }
        switch (this.type) {
            case 7:
                if (this.addFirst) {
                    length = 0;
                } else if (this.addBefore != null) {
                    length = table.getColumn(this.addBefore).getColumnId();
                } else if (this.addAfter != null) {
                    length = table.getColumn(this.addAfter).getColumnId() + 1;
                } else {
                    length = columnArr.length;
                }
                if (this.columnsToAdd != null) {
                    Iterator<Column> it = this.columnsToAdd.iterator();
                    while (it.hasNext()) {
                        int i = length;
                        length++;
                        arrayList.add(i, it.next());
                    }
                    break;
                }
                break;
            case 11:
                arrayList.set(this.oldColumn.getColumnId(), this.newColumn);
                break;
            case 12:
                Iterator<Column> it2 = this.columnsToRemove.iterator();
                while (it2.hasNext()) {
                    Column next = it2.next();
                    Column column2 = null;
                    Iterator<Column> it3 = arrayList.iterator();
                    while (true) {
                        if (it3.hasNext()) {
                            Column next2 = it3.next();
                            if (next2.getName().equals(next.getName())) {
                                column2 = next2;
                            }
                        }
                    }
                    if (column2 == null) {
                        throw DbException.getInternalError(next.getCreateSQL());
                    }
                    arrayList.remove(column2);
                }
                break;
        }
        int allocateObjectId = database.allocateObjectId();
        CreateTableData createTableData = new CreateTableData();
        createTableData.tableName = str;
        createTableData.id = allocateObjectId;
        createTableData.columns = arrayList;
        createTableData.temporary = table.isTemporary();
        createTableData.persistData = table.isPersistData();
        createTableData.persistIndexes = table.isPersistIndexes();
        createTableData.isHidden = table.isHidden();
        createTableData.session = this.session;
        Table createTable = getSchema().createTable(createTableData);
        createTable.setComment(table.getComment());
        String createSQLForMeta = createTable.getCreateSQLForMeta();
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        Iterator<Column> it4 = arrayList.iterator();
        while (it4.hasNext()) {
            Column next3 = it4.next();
            if (!next3.isGenerated()) {
                switch (this.type) {
                    case 7:
                        if (this.columnsToAdd != null && this.columnsToAdd.contains(next3)) {
                            if (this.usingExpression != null) {
                                this.usingExpression.getUnenclosedSQL(addColumn(next3, sb, sb2), 0);
                                break;
                            } else {
                                break;
                            }
                        }
                        break;
                    case 11:
                        if (next3.equals(this.newColumn) && this.usingExpression != null) {
                            this.usingExpression.getUnenclosedSQL(addColumn(next3, sb, sb2), 0);
                            break;
                        }
                        break;
                }
                next3.getSQL(addColumn(next3, sb, sb2), 0);
            }
        }
        String name = createTable.getName();
        Schema schema = createTable.getSchema();
        createTable.removeChildrenAndResources(this.session);
        execute(createSQLForMeta);
        Table tableOrView = schema.getTableOrView(this.session, name);
        ArrayList newSmallArrayList = Utils.newSmallArrayList();
        ArrayList newSmallArrayList2 = Utils.newSmallArrayList();
        boolean z = false;
        Iterator<DbObject> it5 = table.getChildren().iterator();
        while (it5.hasNext()) {
            DbObject next4 = it5.next();
            if (!(next4 instanceof Sequence) && (!(next4 instanceof Index) || !((Index) next4).getIndexType().getBelongsToConstraint())) {
                if (next4.getCreateSQL() != null && !(next4 instanceof TableView)) {
                    if (next4.getType() == 0) {
                        throw DbException.getInternalError();
                    }
                    String quoteIdentifier = ParserBase.quoteIdentifier(str + "_" + next4.getName(), 0);
                    String str2 = null;
                    if (next4 instanceof ConstraintReferential) {
                        ConstraintReferential constraintReferential = (ConstraintReferential) next4;
                        if (constraintReferential.getTable() != table) {
                            str2 = constraintReferential.getCreateSQLForCopy(constraintReferential.getTable(), tableOrView, quoteIdentifier, false);
                        }
                    }
                    if (str2 == null) {
                        str2 = next4.getCreateSQLForCopy(tableOrView, quoteIdentifier);
                    }
                    if (str2 != null) {
                        if (next4 instanceof TriggerObject) {
                            newSmallArrayList2.add(str2);
                        } else {
                            if (!z) {
                                Index index = null;
                                if (next4 instanceof ConstraintUnique) {
                                    ConstraintUnique constraintUnique = (ConstraintUnique) next4;
                                    if (constraintUnique.getConstraintType() == Constraint.Type.PRIMARY_KEY) {
                                        index = constraintUnique.getIndex();
                                    }
                                } else if (next4 instanceof Index) {
                                    index = (Index) next4;
                                }
                                if (index != null && TableBase.getMainIndexColumn(index.getIndexType(), index.getIndexColumns()) != -1) {
                                    execute(str2);
                                    z = true;
                                }
                            }
                            newSmallArrayList.add(str2);
                        }
                    }
                }
            }
        }
        StringBuilder append = tableOrView.getSQL(new StringBuilder(128).append("INSERT INTO "), 0).append('(').append((CharSequence) sb).append(") OVERRIDING SYSTEM VALUE SELECT ");
        if (sb2.length() == 0) {
            append.append('*');
        } else {
            append.append((CharSequence) sb2);
        }
        table.getSQL(append.append(" FROM "), 0);
        try {
            execute(append.toString());
            Iterator it6 = newSmallArrayList.iterator();
            while (it6.hasNext()) {
                execute((String) it6.next());
            }
            table.setModified();
            Iterator<Column> it7 = arrayList.iterator();
            while (it7.hasNext()) {
                Column next5 = it7.next();
                Sequence sequence = next5.getSequence();
                if (sequence != null) {
                    table.removeSequence(sequence);
                    next5.setSequence(null, false);
                }
            }
            Iterator it8 = newSmallArrayList2.iterator();
            while (it8.hasNext()) {
                execute((String) it8.next());
            }
            return tableOrView;
        } catch (Throwable th) {
            StringBuilder sb3 = new StringBuilder("DROP TABLE ");
            tableOrView.getSQL(sb3, 0);
            execute(sb3.toString());
            throw th;
        }
    }

    private static StringBuilder addColumn(Column column, StringBuilder sb, StringBuilder sb2) {
        if (sb.length() > 0) {
            sb.append(", ");
        }
        column.getSQL(sb, 0);
        if (sb2.length() > 0) {
            sb2.append(", ");
        }
        return sb2;
    }

    private void checkViews(SchemaObject schemaObject, SchemaObject schemaObject2) {
        String name = schemaObject.getName();
        String name2 = schemaObject2.getName();
        Database database = schemaObject.getDatabase();
        database.renameSchemaObject(this.session, schemaObject, database.getTempTableName(name, this.session));
        try {
            database.renameSchemaObject(this.session, schemaObject2, name);
            checkViewsAreValid(schemaObject);
            try {
                database.renameSchemaObject(this.session, schemaObject2, name2);
                database.renameSchemaObject(this.session, schemaObject, name);
            } finally {
            }
        } catch (Throwable th) {
            try {
                database.renameSchemaObject(this.session, schemaObject2, name2);
                database.renameSchemaObject(this.session, schemaObject, name);
                throw th;
            } finally {
            }
        }
    }

    private void checkViewsAreValid(DbObject dbObject) {
        Iterator<DbObject> it = dbObject.getChildren().iterator();
        while (it.hasNext()) {
            DbObject next = it.next();
            if (next instanceof TableView) {
                try {
                    this.session.prepare(((TableView) next).getQuerySQL());
                    checkViewsAreValid(next);
                } catch (DbException e) {
                    throw DbException.get(ErrorCode.COLUMN_IS_REFERENCED_1, e, next.getTraceSQL());
                }
            }
        }
    }

    private void execute(String str) {
        new CommandContainer(this.session, str, this.session.prepare(str)).executeUpdate(null);
    }

    private void checkNullable(Table table) {
        if (this.oldColumn.isIdentity()) {
            throw DbException.get(ErrorCode.COLUMN_MUST_NOT_BE_NULLABLE_1, this.oldColumn.getName());
        }
        Iterator<Index> it = table.getIndexes().iterator();
        while (it.hasNext()) {
            Index next = it.next();
            if (next.getColumnIndex(this.oldColumn) >= 0 && next.getIndexType().isPrimaryKey()) {
                throw DbException.get(ErrorCode.COLUMN_MUST_NOT_BE_NULLABLE_1, this.oldColumn.getName());
            }
        }
    }

    private void checkNoNullValues(Table table) {
        StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM ");
        table.getSQL(sb, 0).append(" WHERE ");
        this.oldColumn.getSQL(sb, 0).append(" IS NULL");
        ResultInterface query = this.session.prepare(sb.toString()).query(0L);
        query.next();
        if (query.currentRow()[0].getInt() > 0) {
            throw DbException.get(ErrorCode.COLUMN_CONTAINS_NULL_VALUES_1, this.oldColumn.getTraceSQL());
        }
    }

    public void setType(int i) {
        this.type = i;
    }

    public void setSelectivity(Expression expression) {
        this.newSelectivity = expression;
    }

    public void setDefaultExpression(Expression expression) {
        this.defaultExpression = expression;
    }

    public void setUsingExpression(Expression expression) {
        this.usingExpression = expression;
    }

    public void setNewColumn(Column column) {
        this.newColumn = column;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return this.type;
    }

    public void setIfNotExists(boolean z) {
        this.ifNotExists = z;
    }

    @Override // org.h2.command.ddl.CommandWithColumns
    public void addColumn(Column column) {
        if (this.columnsToAdd == null) {
            this.columnsToAdd = new ArrayList<>();
        }
        this.columnsToAdd.add(column);
    }

    public void setColumnsToRemove(ArrayList<Column> arrayList) {
        this.columnsToRemove = arrayList;
    }

    public void setBooleanFlag(boolean z) {
        this.booleanFlag = z;
    }
}
