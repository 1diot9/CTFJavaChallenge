package org.h2.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.h2.api.ErrorCode;
import org.h2.command.Prepared;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.constraint.Constraint;
import org.h2.engine.CastDataProvider;
import org.h2.engine.Constants;
import org.h2.engine.DbObject;
import org.h2.engine.Right;
import org.h2.engine.SessionLocal;
import org.h2.expression.ExpressionVisitor;
import org.h2.index.Index;
import org.h2.index.IndexType;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.result.DefaultRow;
import org.h2.result.LocalResult;
import org.h2.result.Row;
import org.h2.result.RowFactory;
import org.h2.result.SearchRow;
import org.h2.result.SimpleRowValue;
import org.h2.result.SortOrder;
import org.h2.schema.Schema;
import org.h2.schema.SchemaObject;
import org.h2.schema.Sequence;
import org.h2.schema.TriggerObject;
import org.h2.util.Utils;
import org.h2.value.CompareMode;
import org.h2.value.Value;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/Table.class */
public abstract class Table extends SchemaObject {
    public static final int TYPE_CACHED = 0;
    public static final int TYPE_MEMORY = 1;
    public static final int READ_LOCK = 0;
    public static final int WRITE_LOCK = 1;
    public static final int EXCLUSIVE_LOCK = 2;
    protected Column[] columns;
    protected CompareMode compareMode;
    protected boolean isHidden;
    private final HashMap<String, Column> columnMap;
    private final boolean persistIndexes;
    private final boolean persistData;
    private ArrayList<TriggerObject> triggers;
    private ArrayList<Constraint> constraints;
    private ArrayList<Sequence> sequences;
    private final CopyOnWriteArrayList<TableView> dependentViews;
    private final CopyOnWriteArrayList<MaterializedView> dependentMaterializedViews;
    private ArrayList<TableSynonym> synonyms;
    private boolean checkForeignKeyConstraints;
    private boolean onCommitDrop;
    private boolean onCommitTruncate;
    private volatile Row nullRow;
    private RowFactory rowFactory;
    private boolean tableExpression;

    public abstract void close(SessionLocal sessionLocal);

    public abstract Index addIndex(SessionLocal sessionLocal, String str, int i, IndexColumn[] indexColumnArr, int i2, IndexType indexType, boolean z, String str2);

    public abstract void removeRow(SessionLocal sessionLocal, Row row);

    public abstract long truncate(SessionLocal sessionLocal);

    public abstract void addRow(SessionLocal sessionLocal, Row row);

    public abstract void checkSupportAlter();

    public abstract TableType getTableType();

    public abstract Index getScanIndex(SessionLocal sessionLocal);

    public abstract ArrayList<Index> getIndexes();

    public abstract long getMaxDataModificationId();

    public abstract boolean isDeterministic();

    public abstract boolean canGetRowCount(SessionLocal sessionLocal);

    public abstract boolean canDrop();

    public abstract long getRowCount(SessionLocal sessionLocal);

    public abstract long getRowCountApproximation(SessionLocal sessionLocal);

    /* JADX INFO: Access modifiers changed from: protected */
    public Table(Schema schema, int i, String str, boolean z, boolean z2) {
        super(schema, i, str, 11);
        this.dependentViews = new CopyOnWriteArrayList<>();
        this.dependentMaterializedViews = new CopyOnWriteArrayList<>();
        this.checkForeignKeyConstraints = true;
        this.rowFactory = RowFactory.getRowFactory();
        this.columnMap = schema.getDatabase().newStringMap();
        this.persistIndexes = z;
        this.persistData = z2;
        this.compareMode = schema.getDatabase().getCompareMode();
    }

    @Override // org.h2.engine.DbObject
    public void rename(String str) {
        super.rename(str);
        if (this.constraints != null) {
            Iterator<Constraint> it = this.constraints.iterator();
            while (it.hasNext()) {
                it.next().rebuild();
            }
        }
    }

    public boolean isView() {
        return false;
    }

    public boolean lock(SessionLocal sessionLocal, int i) {
        return false;
    }

    public void unlock(SessionLocal sessionLocal) {
    }

    public Row getRow(SessionLocal sessionLocal, long j) {
        return null;
    }

    public boolean isInsertable() {
        return true;
    }

    public Row lockRow(SessionLocal sessionLocal, Row row, int i) {
        throw DbException.getUnsupportedException("lockRow()");
    }

    public void updateRow(SessionLocal sessionLocal, Row row, Row row2) {
        row2.setKey(row.getKey());
        removeRow(sessionLocal, row);
        addRow(sessionLocal, row2);
    }

    public String getSQLTableType() {
        if (isView()) {
            return "VIEW";
        }
        if (isTemporary()) {
            return isGlobalTemporary() ? "GLOBAL TEMPORARY" : "LOCAL TEMPORARY";
        }
        return "BASE TABLE";
    }

    public Index getScanIndex(SessionLocal sessionLocal, int[] iArr, TableFilter[] tableFilterArr, int i, SortOrder sortOrder, AllColumnsForPlan allColumnsForPlan) {
        return getScanIndex(sessionLocal);
    }

    public Index getIndex(String str) {
        ArrayList<Index> indexes = getIndexes();
        if (indexes != null) {
            Iterator<Index> it = indexes.iterator();
            while (it.hasNext()) {
                Index next = it.next();
                if (next.getName().equals(str)) {
                    return next;
                }
            }
        }
        throw DbException.get(ErrorCode.INDEX_NOT_FOUND_1, str);
    }

    public boolean isLockedExclusively() {
        return false;
    }

    public boolean canReference() {
        return true;
    }

    public long getDiskSpaceUsed() {
        return 0L;
    }

    public Column getRowIdColumn() {
        return null;
    }

    public boolean isQueryComparable() {
        return true;
    }

    public void addDependencies(HashSet<DbObject> hashSet) {
        if (hashSet.contains(this)) {
            return;
        }
        if (this.sequences != null) {
            hashSet.addAll(this.sequences);
        }
        ExpressionVisitor dependenciesVisitor = ExpressionVisitor.getDependenciesVisitor(hashSet);
        for (Column column : this.columns) {
            column.isEverything(dependenciesVisitor);
        }
        if (this.constraints != null) {
            Iterator<Constraint> it = this.constraints.iterator();
            while (it.hasNext()) {
                it.next().isEverything(dependenciesVisitor);
            }
        }
        hashSet.add(this);
    }

    @Override // org.h2.engine.DbObject
    public ArrayList<DbObject> getChildren() {
        ArrayList<DbObject> newSmallArrayList = Utils.newSmallArrayList();
        ArrayList<Index> indexes = getIndexes();
        if (indexes != null) {
            newSmallArrayList.addAll(indexes);
        }
        if (this.constraints != null) {
            newSmallArrayList.addAll(this.constraints);
        }
        if (this.triggers != null) {
            newSmallArrayList.addAll(this.triggers);
        }
        if (this.sequences != null) {
            newSmallArrayList.addAll(this.sequences);
        }
        newSmallArrayList.addAll(this.dependentViews);
        if (this.synonyms != null) {
            newSmallArrayList.addAll(this.synonyms);
        }
        Iterator<Right> it = this.database.getAllRights().iterator();
        while (it.hasNext()) {
            Right next = it.next();
            if (next.getGrantedObject() == this) {
                newSmallArrayList.add(next);
            }
        }
        return newSmallArrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setColumns(Column[] columnArr) {
        if (columnArr.length > 16384) {
            throw DbException.get(ErrorCode.TOO_MANY_COLUMNS_1, "16384");
        }
        this.columns = columnArr;
        if (this.columnMap.size() > 0) {
            this.columnMap.clear();
        }
        for (int i = 0; i < columnArr.length; i++) {
            Column column = columnArr[i];
            if (column.getType().getValueType() == -1) {
                throw DbException.get(ErrorCode.UNKNOWN_DATA_TYPE_1, column.getTraceSQL());
            }
            column.setTable(this, i);
            String name = column.getName();
            if (this.columnMap.putIfAbsent(name, column) != null) {
                throw DbException.get(ErrorCode.DUPLICATE_COLUMN_NAME_1, name);
            }
        }
        this.rowFactory = this.database.getRowFactory().createRowFactory(this.database, this.database.getCompareMode(), this.database, columnArr, null, false);
    }

    public void renameColumn(Column column, String str) {
        for (Column column2 : this.columns) {
            if (column2 != column && column2.getName().equals(str)) {
                throw DbException.get(ErrorCode.DUPLICATE_COLUMN_NAME_1, str);
            }
        }
        this.columnMap.remove(column.getName());
        column.rename(str);
        this.columnMap.put(str, column);
    }

    public boolean isLockedExclusivelyBy(SessionLocal sessionLocal) {
        return false;
    }

    public void updateRows(Prepared prepared, SessionLocal sessionLocal, LocalResult localResult) {
        SessionLocal.Savepoint savepoint = sessionLocal.setSavepoint();
        int i = 0;
        while (localResult.next()) {
            i++;
            if ((i & 127) == 0) {
                prepared.checkCanceled();
            }
            Row currentRowForTable = localResult.currentRowForTable();
            localResult.next();
            try {
                removeRow(sessionLocal, currentRowForTable);
            } catch (DbException e) {
                if (e.getErrorCode() == 90131 || e.getErrorCode() == 90112) {
                    sessionLocal.rollbackTo(savepoint);
                }
                throw e;
            }
        }
        localResult.reset();
        while (localResult.next()) {
            i++;
            if ((i & 127) == 0) {
                prepared.checkCanceled();
            }
            localResult.next();
            try {
                addRow(sessionLocal, localResult.currentRowForTable());
            } catch (DbException e2) {
                if (e2.getErrorCode() == 90131) {
                    sessionLocal.rollbackTo(savepoint);
                }
                throw e2;
            }
        }
    }

    public CopyOnWriteArrayList<TableView> getDependentViews() {
        return this.dependentViews;
    }

    public CopyOnWriteArrayList<MaterializedView> getDependentMaterializedViews() {
        return this.dependentMaterializedViews;
    }

    @Override // org.h2.engine.DbObject
    public void removeChildrenAndResources(SessionLocal sessionLocal) {
        while (!this.dependentViews.isEmpty()) {
            this.database.removeSchemaObject(sessionLocal, this.dependentViews.remove(0));
        }
        while (this.synonyms != null && !this.synonyms.isEmpty()) {
            this.database.removeSchemaObject(sessionLocal, this.synonyms.remove(0));
        }
        while (this.triggers != null && !this.triggers.isEmpty()) {
            this.database.removeSchemaObject(sessionLocal, this.triggers.remove(0));
        }
        while (this.constraints != null && !this.constraints.isEmpty()) {
            this.database.removeSchemaObject(sessionLocal, this.constraints.remove(0));
        }
        Iterator<Right> it = this.database.getAllRights().iterator();
        while (it.hasNext()) {
            Right next = it.next();
            if (next.getGrantedObject() == this) {
                this.database.removeDatabaseObject(sessionLocal, next);
            }
        }
        this.database.removeMeta(sessionLocal, getId());
        while (this.sequences != null && !this.sequences.isEmpty()) {
            Sequence remove = this.sequences.remove(0);
            if (this.database.getDependentTable(remove, this) == null) {
                this.database.removeSchemaObject(sessionLocal, remove);
            }
        }
    }

    public void dropMultipleColumnsConstraintsAndIndexes(SessionLocal sessionLocal, ArrayList<Column> arrayList) {
        HashSet hashSet = new HashSet();
        if (this.constraints != null) {
            Iterator<Column> it = arrayList.iterator();
            while (it.hasNext()) {
                Column next = it.next();
                Iterator<Constraint> it2 = this.constraints.iterator();
                while (it2.hasNext()) {
                    Constraint next2 = it2.next();
                    HashSet<Column> referencedColumns = next2.getReferencedColumns(this);
                    if (referencedColumns.contains(next)) {
                        if (referencedColumns.size() == 1) {
                            hashSet.add(next2);
                        } else {
                            throw DbException.get(ErrorCode.COLUMN_IS_REFERENCED_1, next2.getTraceSQL());
                        }
                    }
                }
            }
        }
        HashSet hashSet2 = new HashSet();
        ArrayList<Index> indexes = getIndexes();
        if (indexes != null) {
            Iterator<Column> it3 = arrayList.iterator();
            while (it3.hasNext()) {
                Column next3 = it3.next();
                Iterator<Index> it4 = indexes.iterator();
                while (it4.hasNext()) {
                    Index next4 = it4.next();
                    if (next4.getCreateSQL() != null && next4.getColumnIndex(next3) >= 0) {
                        if (next4.getColumns().length == 1) {
                            hashSet2.add(next4);
                        } else {
                            throw DbException.get(ErrorCode.COLUMN_IS_REFERENCED_1, next4.getTraceSQL());
                        }
                    }
                }
            }
        }
        Iterator it5 = hashSet.iterator();
        while (it5.hasNext()) {
            Constraint constraint = (Constraint) it5.next();
            if (constraint.isValid()) {
                sessionLocal.getDatabase().removeSchemaObject(sessionLocal, constraint);
            }
        }
        Iterator it6 = hashSet2.iterator();
        while (it6.hasNext()) {
            Index index = (Index) it6.next();
            if (getIndexes().contains(index)) {
                sessionLocal.getDatabase().removeSchemaObject(sessionLocal, index);
            }
        }
    }

    public RowFactory getRowFactory() {
        return this.rowFactory;
    }

    public Row createRow(Value[] valueArr, int i) {
        return this.rowFactory.createRow(valueArr, i);
    }

    public Row getTemplateRow() {
        return createRow(new Value[getColumns().length], -1);
    }

    public SearchRow getTemplateSimpleRow(boolean z) {
        if (z) {
            return new SimpleRowValue(this.columns.length);
        }
        return new DefaultRow(new Value[this.columns.length]);
    }

    public Row getNullRow() {
        Row row = this.nullRow;
        if (row == null) {
            Value[] valueArr = new Value[this.columns.length];
            Arrays.fill(valueArr, ValueNull.INSTANCE);
            Row createRow = createRow(valueArr, 1);
            row = createRow;
            this.nullRow = createRow;
        }
        return row;
    }

    public Column[] getColumns() {
        return this.columns;
    }

    @Override // org.h2.engine.DbObject
    public int getType() {
        return 0;
    }

    public Column getColumn(int i) {
        return this.columns[i];
    }

    public Column getColumn(String str) {
        Column column = this.columnMap.get(str);
        if (column == null) {
            throw DbException.get(ErrorCode.COLUMN_NOT_FOUND_1, str);
        }
        return column;
    }

    public Column getColumn(String str, boolean z) {
        Column column = this.columnMap.get(str);
        if (column == null && !z) {
            throw DbException.get(ErrorCode.COLUMN_NOT_FOUND_1, str);
        }
        return column;
    }

    public Column findColumn(String str) {
        return this.columnMap.get(str);
    }

    public boolean doesColumnExist(String str) {
        return this.columnMap.containsKey(str);
    }

    public Column getIdentityColumn() {
        for (Column column : this.columns) {
            if (column.isIdentity()) {
                return column;
            }
        }
        return null;
    }

    public PlanItem getBestPlanItem(SessionLocal sessionLocal, int[] iArr, TableFilter[] tableFilterArr, int i, SortOrder sortOrder, AllColumnsForPlan allColumnsForPlan) {
        PlanItem planItem = new PlanItem();
        planItem.setIndex(getScanIndex(sessionLocal));
        planItem.cost = planItem.getIndex().getCost(sessionLocal, null, tableFilterArr, i, null, allColumnsForPlan);
        Trace trace = sessionLocal.getTrace();
        if (trace.isDebugEnabled()) {
            trace.debug("Table      :     potential plan item cost {0} index {1}", Double.valueOf(planItem.cost), planItem.getIndex().getPlanSQL());
        }
        ArrayList<Index> indexes = getIndexes();
        IndexHints indexHints = getIndexHints(tableFilterArr, i);
        if (indexes != null && iArr != null) {
            int size = indexes.size();
            for (int i2 = 1; i2 < size; i2++) {
                Index index = indexes.get(i2);
                if (!isIndexExcludedByHints(indexHints, index)) {
                    double cost = index.getCost(sessionLocal, iArr, tableFilterArr, i, sortOrder, allColumnsForPlan);
                    if (trace.isDebugEnabled()) {
                        trace.debug("Table      :     potential plan item cost {0} index {1}", Double.valueOf(cost), index.getPlanSQL());
                    }
                    if (cost < planItem.cost) {
                        planItem.cost = cost;
                        planItem.setIndex(index);
                    }
                }
            }
        }
        return planItem;
    }

    private static boolean isIndexExcludedByHints(IndexHints indexHints, Index index) {
        return (indexHints == null || indexHints.allowIndex(index)) ? false : true;
    }

    private static IndexHints getIndexHints(TableFilter[] tableFilterArr, int i) {
        if (tableFilterArr == null) {
            return null;
        }
        return tableFilterArr[i].getIndexHints();
    }

    public Index findPrimaryKey() {
        ArrayList<Index> indexes = getIndexes();
        if (indexes != null) {
            Iterator<Index> it = indexes.iterator();
            while (it.hasNext()) {
                Index next = it.next();
                if (next.getIndexType().isPrimaryKey()) {
                    return next;
                }
            }
            return null;
        }
        return null;
    }

    public Index getPrimaryKey() {
        Index findPrimaryKey = findPrimaryKey();
        if (findPrimaryKey != null) {
            return findPrimaryKey;
        }
        throw DbException.get(ErrorCode.INDEX_NOT_FOUND_1, Constants.PREFIX_PRIMARY_KEY);
    }

    public void convertInsertRow(SessionLocal sessionLocal, Row row, Boolean bool) {
        int length = this.columns.length;
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            Value value = row.getValue(i2);
            Column column = this.columns[i2];
            if (value == ValueNull.INSTANCE && column.isDefaultOnNull()) {
                value = null;
            }
            if (column.isIdentity()) {
                if (bool != null) {
                    if (!bool.booleanValue()) {
                        value = null;
                    }
                } else if (value != null && column.isGeneratedAlways()) {
                    throw DbException.get(ErrorCode.GENERATED_COLUMN_CANNOT_BE_ASSIGNED_1, column.getSQLWithTable(new StringBuilder(), 3).toString());
                }
            } else if (column.isGeneratedAlways()) {
                if (value != null) {
                    throw DbException.get(ErrorCode.GENERATED_COLUMN_CANNOT_BE_ASSIGNED_1, column.getSQLWithTable(new StringBuilder(), 3).toString());
                }
                i++;
            }
            Value validateConvertUpdateSequence = column.validateConvertUpdateSequence(sessionLocal, value, row);
            if (validateConvertUpdateSequence != value) {
                row.setValue(i2, validateConvertUpdateSequence);
            }
        }
        if (i > 0) {
            for (int i3 = 0; i3 < length; i3++) {
                if (row.getValue(i3) == null) {
                    row.setValue(i3, this.columns[i3].validateConvertUpdateSequence(sessionLocal, null, row));
                }
            }
        }
    }

    public void convertUpdateRow(SessionLocal sessionLocal, Row row, boolean z) {
        int length = this.columns.length;
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            Value value = row.getValue(i2);
            Column column = this.columns[i2];
            if (column.isGenerated()) {
                if (value != null) {
                    if (!z) {
                        throw DbException.get(ErrorCode.GENERATED_COLUMN_CANNOT_BE_ASSIGNED_1, column.getSQLWithTable(new StringBuilder(), 3).toString());
                    }
                    row.setValue(i2, null);
                }
                i++;
            } else {
                Value validateConvertUpdateSequence = column.validateConvertUpdateSequence(sessionLocal, value, row);
                if (validateConvertUpdateSequence != value) {
                    row.setValue(i2, validateConvertUpdateSequence);
                }
            }
        }
        if (i > 0) {
            for (int i3 = 0; i3 < length; i3++) {
                if (row.getValue(i3) == null) {
                    row.setValue(i3, this.columns[i3].validateConvertUpdateSequence(sessionLocal, null, row));
                }
            }
        }
    }

    private static void remove(ArrayList<? extends DbObject> arrayList, DbObject dbObject) {
        if (arrayList != null) {
            arrayList.remove(dbObject);
        }
    }

    public void removeIndex(Index index) {
        ArrayList<Index> indexes = getIndexes();
        if (indexes != null) {
            remove(indexes, index);
            if (index.getIndexType().isPrimaryKey()) {
                for (Column column : index.getColumns()) {
                    column.setPrimaryKey(false);
                }
            }
        }
    }

    public void removeDependentView(TableView tableView) {
        this.dependentViews.remove(tableView);
    }

    public void removeDependentMaterializedView(MaterializedView materializedView) {
        this.dependentMaterializedViews.remove(materializedView);
    }

    public void removeSynonym(TableSynonym tableSynonym) {
        remove(this.synonyms, tableSynonym);
    }

    public void removeConstraint(Constraint constraint) {
        remove(this.constraints, constraint);
    }

    public final void removeSequence(Sequence sequence) {
        remove(this.sequences, sequence);
    }

    public void removeTrigger(TriggerObject triggerObject) {
        remove(this.triggers, triggerObject);
    }

    public void addDependentView(TableView tableView) {
        this.dependentViews.add(tableView);
    }

    public void addDependentMaterializedView(MaterializedView materializedView) {
        this.dependentMaterializedViews.add(materializedView);
    }

    public void addSynonym(TableSynonym tableSynonym) {
        this.synonyms = add(this.synonyms, tableSynonym);
    }

    public void addConstraint(Constraint constraint) {
        if (this.constraints == null || !this.constraints.contains(constraint)) {
            this.constraints = add(this.constraints, constraint);
        }
    }

    public ArrayList<Constraint> getConstraints() {
        return this.constraints;
    }

    public void addSequence(Sequence sequence) {
        this.sequences = add(this.sequences, sequence);
    }

    public void addTrigger(TriggerObject triggerObject) {
        this.triggers = add(this.triggers, triggerObject);
    }

    private static <T> ArrayList<T> add(ArrayList<T> arrayList, T t) {
        if (arrayList == null) {
            arrayList = Utils.newSmallArrayList();
        }
        arrayList.add(t);
        return arrayList;
    }

    public void fire(SessionLocal sessionLocal, int i, boolean z) {
        if (this.triggers != null) {
            Iterator<TriggerObject> it = this.triggers.iterator();
            while (it.hasNext()) {
                it.next().fire(sessionLocal, i, z);
            }
        }
    }

    public boolean hasSelectTrigger() {
        if (this.triggers != null) {
            Iterator<TriggerObject> it = this.triggers.iterator();
            while (it.hasNext()) {
                if (it.next().isSelectTrigger()) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public boolean fireRow() {
        return ((this.constraints == null || this.constraints.isEmpty()) && (this.triggers == null || this.triggers.isEmpty())) ? false : true;
    }

    public boolean fireBeforeRow(SessionLocal sessionLocal, Row row, Row row2) {
        boolean fireRow = fireRow(sessionLocal, row, row2, true, false);
        fireConstraints(sessionLocal, row, row2, true);
        return fireRow;
    }

    private void fireConstraints(SessionLocal sessionLocal, Row row, Row row2, boolean z) {
        if (this.constraints != null) {
            Iterator<Constraint> it = this.constraints.iterator();
            while (it.hasNext()) {
                Constraint next = it.next();
                if (next.isBefore() == z) {
                    next.checkRow(sessionLocal, this, row, row2);
                }
            }
        }
    }

    public void fireAfterRow(SessionLocal sessionLocal, Row row, Row row2, boolean z) {
        fireRow(sessionLocal, row, row2, false, z);
        if (!z) {
            fireConstraints(sessionLocal, row, row2, false);
        }
    }

    private boolean fireRow(SessionLocal sessionLocal, Row row, Row row2, boolean z, boolean z2) {
        if (this.triggers != null) {
            Iterator<TriggerObject> it = this.triggers.iterator();
            while (it.hasNext()) {
                if (it.next().fireRow(sessionLocal, this, row, row2, z, z2)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public boolean isGlobalTemporary() {
        return false;
    }

    public boolean canTruncate() {
        return false;
    }

    public void setCheckForeignKeyConstraints(SessionLocal sessionLocal, boolean z, boolean z2) {
        if (z && z2 && this.constraints != null) {
            Iterator<Constraint> it = this.constraints.iterator();
            while (it.hasNext()) {
                Constraint next = it.next();
                if (next.getConstraintType() == Constraint.Type.REFERENTIAL) {
                    next.checkExistingData(sessionLocal);
                }
            }
        }
        this.checkForeignKeyConstraints = z;
    }

    public boolean getCheckForeignKeyConstraints() {
        return this.checkForeignKeyConstraints;
    }

    public Index getIndexForColumn(Column column, boolean z, boolean z2) {
        ArrayList<Index> indexes = getIndexes();
        Index index = null;
        if (indexes != null) {
            int size = indexes.size();
            for (int i = 1; i < size; i++) {
                Index index2 = indexes.get(i);
                if ((!z || index2.canGetFirstOrLast()) && ((!z2 || index2.canFindNext()) && index2.isFirstColumn(column) && (index == null || index.getColumns().length > index2.getColumns().length))) {
                    index = index2;
                }
            }
        }
        return index;
    }

    public boolean getOnCommitDrop() {
        return this.onCommitDrop;
    }

    public void setOnCommitDrop(boolean z) {
        this.onCommitDrop = z;
    }

    public boolean getOnCommitTruncate() {
        return this.onCommitTruncate;
    }

    public void setOnCommitTruncate(boolean z) {
        this.onCommitTruncate = z;
    }

    public void removeIndexOrTransferOwnership(SessionLocal sessionLocal, Index index) {
        boolean z = false;
        if (this.constraints != null) {
            Iterator<Constraint> it = this.constraints.iterator();
            while (it.hasNext()) {
                Constraint next = it.next();
                if (next.usesIndex(index)) {
                    next.setIndexOwner(index);
                    this.database.updateMeta(sessionLocal, next);
                    z = true;
                }
            }
        }
        if (!z) {
            this.database.removeSchemaObject(sessionLocal, index);
        }
    }

    public void removeColumnExpressionsDependencies(SessionLocal sessionLocal) {
        for (Column column : this.columns) {
            column.setDefaultExpression(sessionLocal, null);
            column.setOnUpdateExpression(sessionLocal, null);
        }
    }

    public ArrayList<SessionLocal> checkDeadlock(SessionLocal sessionLocal, SessionLocal sessionLocal2, Set<SessionLocal> set) {
        return null;
    }

    public boolean isPersistIndexes() {
        return this.persistIndexes;
    }

    public boolean isPersistData() {
        return this.persistData;
    }

    public int compareValues(CastDataProvider castDataProvider, Value value, Value value2) {
        return value.compareTo(value2, castDataProvider, this.compareMode);
    }

    public CompareMode getCompareMode() {
        return this.compareMode;
    }

    public void checkWritingAllowed() {
        this.database.checkWritingAllowed();
    }

    @Override // org.h2.schema.SchemaObject
    public boolean isHidden() {
        return this.isHidden;
    }

    public void setHidden(boolean z) {
        this.isHidden = z;
    }

    public boolean isRowLockable() {
        return false;
    }

    public void setTableExpression(boolean z) {
        this.tableExpression = z;
    }

    public boolean isTableExpression() {
        return this.tableExpression;
    }

    public ArrayList<TriggerObject> getTriggers() {
        return this.triggers;
    }

    public int getMainIndexColumn() {
        return -1;
    }
}
