package org.h2.table;

import org.h2.engine.SessionLocal;
import org.h2.expression.function.table.TableFunction;
import org.h2.result.ResultInterface;
import org.h2.schema.Schema;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/FunctionTable.class */
public class FunctionTable extends VirtualConstructedTable {
    private final TableFunction function;

    public FunctionTable(Schema schema, SessionLocal sessionLocal, TableFunction tableFunction) {
        super(schema, 0, tableFunction.getName());
        this.function = tableFunction;
        tableFunction.optimize(sessionLocal);
        ResultInterface valueTemplate = tableFunction.getValueTemplate(sessionLocal);
        int visibleColumnCount = valueTemplate.getVisibleColumnCount();
        Column[] columnArr = new Column[visibleColumnCount];
        for (int i = 0; i < visibleColumnCount; i++) {
            columnArr[i] = new Column(valueTemplate.getColumnName(i), valueTemplate.getColumnType(i));
        }
        setColumns(columnArr);
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
        return this.function.getValue(sessionLocal);
    }

    @Override // org.h2.schema.SchemaObject, org.h2.engine.DbObject, org.h2.util.HasSQL
    public String getSQL(int i) {
        return this.function.getSQL(i);
    }

    @Override // org.h2.schema.SchemaObject, org.h2.engine.DbObject, org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return sb.append(this.function.getSQL(i));
    }

    @Override // org.h2.table.Table
    public boolean isDeterministic() {
        return this.function.isDeterministic();
    }
}
