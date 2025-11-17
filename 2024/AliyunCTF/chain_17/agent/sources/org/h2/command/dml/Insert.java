package org.h2.command.dml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.h2.api.ErrorCode;
import org.h2.command.Command;
import org.h2.command.query.Query;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Parameter;
import org.h2.expression.ValueExpression;
import org.h2.expression.condition.Comparison;
import org.h2.expression.condition.ConditionAndOr;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.mvstore.db.MVPrimaryIndex;
import org.h2.result.ResultInterface;
import org.h2.result.ResultTarget;
import org.h2.result.Row;
import org.h2.table.Column;
import org.h2.table.DataChangeDeltaTable;
import org.h2.table.Table;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/Insert.class */
public final class Insert extends CommandWithValues implements ResultTarget {
    private Table table;
    private Column[] columns;
    private Query query;
    private long rowNumber;
    private boolean insertFromSelect;
    private Boolean overridingSystem;
    private HashMap<Column, Expression> duplicateKeyAssignmentMap;
    private Value[] onDuplicateKeyRow;
    private boolean ignore;
    private ResultTarget deltaChangeCollector;
    private DataChangeDeltaTable.ResultOption deltaChangeCollectionMode;

    public Insert(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    @Override // org.h2.command.Prepared
    public void setCommand(Command command) {
        super.setCommand(command);
        if (this.query != null) {
            this.query.setCommand(command);
        }
    }

    @Override // org.h2.command.dml.DataChangeStatement
    public Table getTable() {
        return this.table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public void setColumns(Column[] columnArr) {
        this.columns = columnArr;
    }

    public void setIgnore(boolean z) {
        this.ignore = z;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public void setOverridingSystem(Boolean bool) {
        this.overridingSystem = bool;
    }

    public void addAssignmentForDuplicate(Column column, Expression expression) {
        if (this.duplicateKeyAssignmentMap == null) {
            this.duplicateKeyAssignmentMap = new HashMap<>();
        }
        if (this.duplicateKeyAssignmentMap.putIfAbsent(column, expression) != null) {
            throw DbException.get(ErrorCode.DUPLICATE_COLUMN_NAME_1, column.getName());
        }
    }

    @Override // org.h2.command.dml.DataChangeStatement
    public long update(ResultTarget resultTarget, DataChangeDeltaTable.ResultOption resultOption) {
        this.deltaChangeCollector = resultTarget;
        this.deltaChangeCollectionMode = resultOption;
        try {
            long insertRows = insertRows();
            this.deltaChangeCollector = null;
            this.deltaChangeCollectionMode = null;
            return insertRows;
        } catch (Throwable th) {
            this.deltaChangeCollector = null;
            this.deltaChangeCollectionMode = null;
            throw th;
        }
    }

    private long insertRows() {
        this.session.getUser().checkTableRight(this.table, 4);
        setCurrentRowNumber(0L);
        this.table.fire(this.session, 1, true);
        this.rowNumber = 0L;
        int size = this.valuesExpressionList.size();
        if (size > 0) {
            int length = this.columns.length;
            for (int i = 0; i < size; i++) {
                Row templateRow = this.table.getTemplateRow();
                Expression[] expressionArr = this.valuesExpressionList.get(i);
                setCurrentRowNumber(i + 1);
                for (int i2 = 0; i2 < length; i2++) {
                    int columnId = this.columns[i2].getColumnId();
                    Expression expression = expressionArr[i2];
                    if (expression != ValueExpression.DEFAULT) {
                        try {
                            templateRow.setValue(columnId, expression.getValue(this.session));
                        } catch (DbException e) {
                            throw setRow(e, i, getSimpleSQL(expressionArr));
                        }
                    }
                }
                this.rowNumber++;
                this.table.convertInsertRow(this.session, templateRow, this.overridingSystem);
                if (this.deltaChangeCollectionMode == DataChangeDeltaTable.ResultOption.NEW) {
                    this.deltaChangeCollector.addRow((Value[]) templateRow.getValueList().clone());
                }
                if (!this.table.fireBeforeRow(this.session, null, templateRow)) {
                    this.table.lock(this.session, 1);
                    try {
                        this.table.addRow(this.session, templateRow);
                        DataChangeDeltaTable.collectInsertedFinalRow(this.session, this.table, this.deltaChangeCollector, this.deltaChangeCollectionMode, templateRow);
                        this.table.fireAfterRow(this.session, null, templateRow, false);
                    } catch (DbException e2) {
                        if (handleOnDuplicate(e2, null)) {
                            this.rowNumber++;
                        } else {
                            this.rowNumber--;
                        }
                    }
                } else {
                    DataChangeDeltaTable.collectInsertedFinalRow(this.session, this.table, this.deltaChangeCollector, this.deltaChangeCollectionMode, templateRow);
                }
            }
        } else {
            this.table.lock(this.session, 1);
            if (this.insertFromSelect) {
                this.query.query(0L, this);
            } else {
                ResultInterface query = this.query.query(0L);
                while (query.next()) {
                    Value[] currentRow = query.currentRow();
                    try {
                        addRow(currentRow);
                    } catch (DbException e3) {
                        if (handleOnDuplicate(e3, currentRow)) {
                            this.rowNumber++;
                        } else {
                            this.rowNumber--;
                        }
                    }
                }
                query.close();
            }
        }
        this.table.fire(this.session, 1, false);
        return this.rowNumber;
    }

    @Override // org.h2.result.ResultTarget
    public void addRow(Value... valueArr) {
        Row templateRow = this.table.getTemplateRow();
        long j = this.rowNumber + 1;
        this.rowNumber = j;
        setCurrentRowNumber(j);
        int length = this.columns.length;
        for (int i = 0; i < length; i++) {
            templateRow.setValue(this.columns[i].getColumnId(), valueArr[i]);
        }
        this.table.convertInsertRow(this.session, templateRow, this.overridingSystem);
        if (this.deltaChangeCollectionMode == DataChangeDeltaTable.ResultOption.NEW) {
            this.deltaChangeCollector.addRow((Value[]) templateRow.getValueList().clone());
        }
        if (!this.table.fireBeforeRow(this.session, null, templateRow)) {
            this.table.addRow(this.session, templateRow);
            DataChangeDeltaTable.collectInsertedFinalRow(this.session, this.table, this.deltaChangeCollector, this.deltaChangeCollectionMode, templateRow);
            this.table.fireAfterRow(this.session, null, templateRow, false);
            return;
        }
        DataChangeDeltaTable.collectInsertedFinalRow(this.session, this.table, this.deltaChangeCollector, this.deltaChangeCollectionMode, templateRow);
    }

    @Override // org.h2.result.ResultTarget
    public long getRowCount() {
        return this.rowNumber;
    }

    @Override // org.h2.result.ResultTarget
    public void limitsWereApplied() {
    }

    @Override // org.h2.command.Prepared
    public String getPlanSQL(int i) {
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        this.table.getSQL(sb, i).append('(');
        Column.writeColumns(sb, this.columns, i);
        sb.append(")\n");
        if (this.insertFromSelect) {
            sb.append("DIRECT ");
        }
        if (!this.valuesExpressionList.isEmpty()) {
            sb.append("VALUES ");
            int i2 = 0;
            if (this.valuesExpressionList.size() > 1) {
                sb.append('\n');
            }
            Iterator<Expression[]> it = this.valuesExpressionList.iterator();
            while (it.hasNext()) {
                Expression[] next = it.next();
                int i3 = i2;
                i2++;
                if (i3 > 0) {
                    sb.append(",\n");
                }
                Expression.writeExpressions(sb.append('('), next, i).append(')');
            }
        } else {
            sb.append(this.query.getPlanSQL(i));
        }
        return sb.toString();
    }

    @Override // org.h2.command.dml.DataChangeStatement
    void doPrepare() {
        if (this.columns == null) {
            if (!this.valuesExpressionList.isEmpty() && this.valuesExpressionList.get(0).length == 0) {
                this.columns = new Column[0];
            } else {
                this.columns = this.table.getColumns();
            }
        }
        if (!this.valuesExpressionList.isEmpty()) {
            Iterator<Expression[]> it = this.valuesExpressionList.iterator();
            while (it.hasNext()) {
                Expression[] next = it.next();
                if (next.length != this.columns.length) {
                    throw DbException.get(ErrorCode.COLUMN_COUNT_DOES_NOT_MATCH);
                }
                int length = next.length;
                for (int i = 0; i < length; i++) {
                    Expression expression = next[i];
                    if (expression != null) {
                        Expression optimize = expression.optimize(this.session);
                        if (optimize instanceof Parameter) {
                            ((Parameter) optimize).setColumn(this.columns[i]);
                        }
                        next[i] = optimize;
                    }
                }
            }
            return;
        }
        this.query.prepare();
        if (this.query.getColumnCount() != this.columns.length) {
            throw DbException.get(ErrorCode.COLUMN_COUNT_DOES_NOT_MATCH);
        }
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 61;
    }

    @Override // org.h2.command.dml.DataChangeStatement
    public String getStatementName() {
        return "INSERT";
    }

    public void setInsertFromSelect(boolean z) {
        this.insertFromSelect = z;
    }

    @Override // org.h2.command.dml.DataChangeStatement, org.h2.command.Prepared
    public boolean isCacheable() {
        return this.duplicateKeyAssignmentMap == null;
    }

    private boolean handleOnDuplicate(DbException dbException, Value[] valueArr) {
        Value value;
        if (dbException.getErrorCode() != 23505) {
            throw dbException;
        }
        if (this.duplicateKeyAssignmentMap == null) {
            if (this.ignore) {
                return false;
            }
            throw dbException;
        }
        int length = this.columns.length;
        Expression[] expressionArr = valueArr == null ? this.valuesExpressionList.get(((int) getCurrentRowNumber()) - 1) : new Expression[length];
        this.onDuplicateKeyRow = new Value[this.table.getColumns().length];
        for (int i = 0; i < length; i++) {
            if (valueArr != null) {
                value = valueArr[i];
                expressionArr[i] = ValueExpression.get(value);
            } else {
                value = expressionArr[i].getValue(this.session);
            }
            this.onDuplicateKeyRow[this.columns[i].getColumnId()] = value;
        }
        StringBuilder sb = new StringBuilder("UPDATE ");
        this.table.getSQL(sb, 0).append(" SET ");
        boolean z = false;
        for (Map.Entry<Column, Expression> entry : this.duplicateKeyAssignmentMap.entrySet()) {
            if (z) {
                sb.append(", ");
            }
            z = true;
            entry.getKey().getSQL(sb, 0).append('=');
            entry.getValue().getUnenclosedSQL(sb, 0);
        }
        sb.append(" WHERE ");
        Index index = (Index) dbException.getSource();
        if (index == null) {
            throw DbException.getUnsupportedException("Unable to apply ON DUPLICATE KEY UPDATE, no index found!");
        }
        prepareUpdateCondition(index, expressionArr).getUnenclosedSQL(sb, 0);
        Update update = (Update) this.session.prepare(sb.toString());
        update.setOnDuplicateKeyInsert(this);
        Iterator<Parameter> it = update.getParameters().iterator();
        while (it.hasNext()) {
            Parameter next = it.next();
            next.setValue(this.parameters.get(next.getIndex()).getValue(this.session));
        }
        boolean z2 = update.update() > 0;
        this.onDuplicateKeyRow = null;
        return z2;
    }

    private Expression prepareUpdateCondition(Index index, Expression[] expressionArr) {
        Column[] columns;
        if (index instanceof MVPrimaryIndex) {
            MVPrimaryIndex mVPrimaryIndex = (MVPrimaryIndex) index;
            columns = new Column[]{mVPrimaryIndex.getIndexColumns()[mVPrimaryIndex.getMainIndexColumn()].column};
        } else {
            columns = index.getColumns();
        }
        Expression expression = null;
        for (Column column : columns) {
            ExpressionColumn expressionColumn = new ExpressionColumn(getDatabase(), this.table.getSchema().getName(), this.table.getName(), column.getName());
            int i = 0;
            while (true) {
                if (i >= this.columns.length) {
                    break;
                }
                if (!expressionColumn.getColumnName(this.session, i).equals(this.columns[i].getName())) {
                    i++;
                } else if (expression == null) {
                    expression = new Comparison(0, expressionColumn, expressionArr[i], false);
                } else {
                    expression = new ConditionAndOr(0, expression, new Comparison(0, expressionColumn, expressionArr[i], false));
                }
            }
        }
        return expression;
    }

    public Value getOnDuplicateKeyValue(int i) {
        return this.onDuplicateKeyRow[i];
    }

    @Override // org.h2.command.Prepared
    public void collectDependencies(HashSet<DbObject> hashSet) {
        ExpressionVisitor dependenciesVisitor = ExpressionVisitor.getDependenciesVisitor(hashSet);
        if (!this.valuesExpressionList.isEmpty()) {
            Iterator<Expression[]> it = this.valuesExpressionList.iterator();
            while (it.hasNext()) {
                for (Expression expression : it.next()) {
                    expression.isEverything(dependenciesVisitor);
                }
            }
            return;
        }
        this.query.isEverything(dependenciesVisitor);
    }
}
