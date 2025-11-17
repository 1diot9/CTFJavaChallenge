package org.h2.command.dml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.h2.api.ErrorCode;
import org.h2.command.Command;
import org.h2.command.query.Query;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.Parameter;
import org.h2.expression.ValueExpression;
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
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/Merge.class */
public final class Merge extends CommandWithValues {
    private boolean isReplace;
    private Table table;
    private Column[] columns;
    private Column[] keys;
    private Query query;
    private Update update;

    public Merge(SessionLocal sessionLocal, boolean z) {
        super(sessionLocal);
        this.isReplace = z;
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

    public void setKeys(Column[] columnArr) {
        this.keys = columnArr;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    @Override // org.h2.command.dml.DataChangeStatement
    public long update(ResultTarget resultTarget, DataChangeDeltaTable.ResultOption resultOption) {
        long j = 0;
        this.session.getUser().checkTableRight(this.table, 4);
        this.session.getUser().checkTableRight(this.table, 8);
        setCurrentRowNumber(0L);
        if (!this.valuesExpressionList.isEmpty()) {
            int size = this.valuesExpressionList.size();
            for (int i = 0; i < size; i++) {
                setCurrentRowNumber(i + 1);
                Expression[] expressionArr = this.valuesExpressionList.get(i);
                Row templateRow = this.table.getTemplateRow();
                int length = this.columns.length;
                for (int i2 = 0; i2 < length; i2++) {
                    int columnId = this.columns[i2].getColumnId();
                    Expression expression = expressionArr[i2];
                    if (expression != ValueExpression.DEFAULT) {
                        try {
                            templateRow.setValue(columnId, expression.getValue(this.session));
                        } catch (DbException e) {
                            throw setRow(e, j, getSimpleSQL(expressionArr));
                        }
                    }
                }
                j += merge(templateRow, expressionArr, resultTarget, resultOption);
            }
        } else {
            this.query.setNeverLazy(true);
            ResultInterface query = this.query.query(0L);
            this.table.fire(this.session, 3, true);
            this.table.lock(this.session, 1);
            while (query.next()) {
                Value[] currentRow = query.currentRow();
                Row templateRow2 = this.table.getTemplateRow();
                setCurrentRowNumber(j);
                for (int i3 = 0; i3 < this.columns.length; i3++) {
                    templateRow2.setValue(this.columns[i3].getColumnId(), currentRow[i3]);
                }
                j += merge(templateRow2, null, resultTarget, resultOption);
            }
            query.close();
            this.table.fire(this.session, 3, false);
        }
        return j;
    }

    private int merge(Row row, Expression[] expressionArr, ResultTarget resultTarget, DataChangeDeltaTable.ResultOption resultOption) {
        long update;
        Index index;
        Column[] columns;
        boolean z;
        if (this.update == null) {
            update = 0;
        } else {
            ArrayList<Parameter> parameters = this.update.getParameters();
            int i = 0;
            int length = this.columns.length;
            for (int i2 = 0; i2 < length; i2++) {
                Column column = this.columns[i2];
                if (column.isGeneratedAlways()) {
                    if (expressionArr == null || expressionArr[i2] != ValueExpression.DEFAULT) {
                        throw DbException.get(ErrorCode.GENERATED_COLUMN_CANNOT_BE_ASSIGNED_1, column.getSQLWithTable(new StringBuilder(), 3).toString());
                    }
                } else {
                    Value value = row.getValue(column.getColumnId());
                    if (value == null) {
                        Expression effectiveDefaultExpression = column.getEffectiveDefaultExpression();
                        value = effectiveDefaultExpression != null ? effectiveDefaultExpression.getValue(this.session) : ValueNull.INSTANCE;
                    }
                    int i3 = i;
                    i++;
                    parameters.get(i3).setValue(value);
                }
            }
            for (Column column2 : this.keys) {
                Value value2 = row.getValue(column2.getColumnId());
                if (value2 == null) {
                    throw DbException.get(ErrorCode.COLUMN_CONTAINS_NULL_VALUES_1, column2.getTraceSQL());
                }
                int i4 = i;
                i++;
                parameters.get(i4).setValue(value2);
            }
            update = this.update.update(resultTarget, resultOption);
        }
        if (update != 0) {
            if (update == 1) {
                return this.isReplace ? 2 : 1;
            }
            throw DbException.get(ErrorCode.DUPLICATE_KEY_1, this.table.getTraceSQL());
        }
        try {
            this.table.convertInsertRow(this.session, row, null);
            if (resultOption == DataChangeDeltaTable.ResultOption.NEW) {
                resultTarget.addRow((Value[]) row.getValueList().clone());
            }
            if (!this.table.fireBeforeRow(this.session, null, row)) {
                this.table.lock(this.session, 1);
                this.table.addRow(this.session, row);
                DataChangeDeltaTable.collectInsertedFinalRow(this.session, this.table, resultTarget, resultOption, row);
                this.table.fireAfterRow(this.session, null, row, false);
                return 1;
            }
            DataChangeDeltaTable.collectInsertedFinalRow(this.session, this.table, resultTarget, resultOption, row);
            return 1;
        } catch (DbException e) {
            if (e.getErrorCode() == 23505 && (index = (Index) e.getSource()) != null) {
                if (index instanceof MVPrimaryIndex) {
                    MVPrimaryIndex mVPrimaryIndex = (MVPrimaryIndex) index;
                    columns = new Column[]{mVPrimaryIndex.getIndexColumns()[mVPrimaryIndex.getMainIndexColumn()].column};
                } else {
                    columns = index.getColumns();
                }
                if (columns.length <= this.keys.length) {
                    z = true;
                    int i5 = 0;
                    while (true) {
                        if (i5 >= columns.length) {
                            break;
                        }
                        if (columns[i5] == this.keys[i5]) {
                            i5++;
                        } else {
                            z = false;
                            break;
                        }
                    }
                } else {
                    z = false;
                }
                if (z) {
                    throw DbException.get(ErrorCode.CONCURRENT_UPDATE_1, this.table.getName());
                }
            }
            throw e;
        }
    }

    @Override // org.h2.command.Prepared
    public String getPlanSQL(int i) {
        StringBuilder sb = new StringBuilder(this.isReplace ? "REPLACE INTO " : "MERGE INTO ");
        this.table.getSQL(sb, i).append('(');
        Column.writeColumns(sb, this.columns, i);
        sb.append(')');
        if (!this.isReplace && this.keys != null) {
            sb.append(" KEY(");
            Column.writeColumns(sb, this.keys, i);
            sb.append(')');
        }
        sb.append('\n');
        if (!this.valuesExpressionList.isEmpty()) {
            sb.append("VALUES ");
            int i2 = 0;
            Iterator<Expression[]> it = this.valuesExpressionList.iterator();
            while (it.hasNext()) {
                Expression[] next = it.next();
                int i3 = i2;
                i2++;
                if (i3 > 0) {
                    sb.append(", ");
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
                for (int i = 0; i < next.length; i++) {
                    Expression expression = next[i];
                    if (expression != null) {
                        next[i] = expression.optimize(this.session);
                    }
                }
            }
        } else {
            this.query.prepare();
            if (this.query.getColumnCount() != this.columns.length) {
                throw DbException.get(ErrorCode.COLUMN_COUNT_DOES_NOT_MATCH);
            }
        }
        if (this.keys == null) {
            Index primaryKey = this.table.getPrimaryKey();
            if (primaryKey == null) {
                throw DbException.get(ErrorCode.CONSTRAINT_NOT_FOUND_1, "PRIMARY KEY");
            }
            this.keys = primaryKey.getColumns();
        }
        if (this.isReplace) {
            for (Column column : this.keys) {
                boolean z = false;
                Column[] columnArr = this.columns;
                int length = columnArr.length;
                int i2 = 0;
                while (true) {
                    if (i2 >= length) {
                        break;
                    }
                    if (columnArr[i2].getColumnId() != column.getColumnId()) {
                        i2++;
                    } else {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    return;
                }
            }
        }
        StringBuilder append = this.table.getSQL(new StringBuilder("UPDATE "), 0).append(" SET ");
        boolean z2 = false;
        int length2 = this.columns.length;
        for (int i3 = 0; i3 < length2; i3++) {
            Column column2 = this.columns[i3];
            if (!column2.isGeneratedAlways()) {
                if (z2) {
                    append.append(", ");
                }
                z2 = true;
                column2.getSQL(append, 0).append("=?");
            }
        }
        if (!z2) {
            throw DbException.getSyntaxError(this.sqlStatement, this.sqlStatement.length(), "Valid MERGE INTO statement with at least one updatable column");
        }
        Column.writeColumns(append.append(" WHERE "), this.keys, " AND ", "=?", 0);
        this.update = (Update) this.session.prepare(append.toString());
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return this.isReplace ? 63 : 62;
    }

    @Override // org.h2.command.dml.DataChangeStatement
    public String getStatementName() {
        return this.isReplace ? "REPLACE" : "MERGE";
    }

    @Override // org.h2.command.Prepared
    public void collectDependencies(HashSet<DbObject> hashSet) {
        if (this.query != null) {
            this.query.collectDependencies(hashSet);
        }
    }
}
