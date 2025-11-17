package org.h2.table;

import org.h2.command.dml.DataChangeStatement;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.result.ResultTarget;
import org.h2.result.Row;
import org.h2.schema.Schema;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/DataChangeDeltaTable.class */
public class DataChangeDeltaTable extends VirtualConstructedTable {
    private final DataChangeStatement statement;
    private final ResultOption resultOption;
    private final Expression[] expressions;

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/DataChangeDeltaTable$ResultOption.class */
    public enum ResultOption {
        OLD,
        NEW,
        FINAL
    }

    public static void collectInsertedFinalRow(SessionLocal sessionLocal, Table table, ResultTarget resultTarget, ResultOption resultOption, Row row) {
        Column identityColumn;
        if (sessionLocal.getMode().takeInsertedIdentity && (identityColumn = table.getIdentityColumn()) != null) {
            sessionLocal.setLastIdentity(row.getValue(identityColumn.getColumnId()));
        }
        if (resultOption == ResultOption.FINAL) {
            resultTarget.addRow(row.getValueList());
        }
    }

    public DataChangeDeltaTable(Schema schema, SessionLocal sessionLocal, DataChangeStatement dataChangeStatement, ResultOption resultOption) {
        super(schema, 0, dataChangeStatement.getStatementName());
        this.statement = dataChangeStatement;
        this.resultOption = resultOption;
        Column[] columns = dataChangeStatement.getTable().getColumns();
        int length = columns.length;
        Column[] columnArr = new Column[length];
        for (int i = 0; i < length; i++) {
            columnArr[i] = columns[i].getClone();
        }
        setColumns(columnArr);
        Expression[] expressionArr = new Expression[length];
        String name = getName();
        for (int i2 = 0; i2 < length; i2++) {
            expressionArr[i2] = new ExpressionColumn(this.database, null, name, columnArr[i2].getName());
        }
        this.expressions = expressionArr;
    }

    @Override // org.h2.table.Table
    public boolean canGetRowCount(SessionLocal sessionLocal) {
        return false;
    }

    @Override // org.h2.table.Table
    public long getRowCount(SessionLocal sessionLocal) {
        return Long.MAX_VALUE;
    }

    @Override // org.h2.table.Table
    public long getRowCountApproximation(SessionLocal sessionLocal) {
        return Long.MAX_VALUE;
    }

    @Override // org.h2.table.VirtualConstructedTable
    public ResultInterface getResult(SessionLocal sessionLocal) {
        this.statement.prepare();
        int length = this.expressions.length;
        LocalResult localResult = new LocalResult(sessionLocal, this.expressions, length, length);
        localResult.setForDataChangeDeltaTable();
        this.statement.update(localResult, this.resultOption);
        return localResult;
    }

    @Override // org.h2.schema.SchemaObject, org.h2.engine.DbObject, org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return sb.append(this.resultOption.name()).append(" TABLE (").append(this.statement.getSQL()).append(')');
    }

    @Override // org.h2.table.Table
    public boolean isDeterministic() {
        return false;
    }
}
