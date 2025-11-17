package org.h2.table;

import java.util.ArrayList;
import org.h2.command.query.TableValueConstructor;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.result.ResultInterface;
import org.h2.result.SimpleResult;
import org.h2.schema.Schema;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/TableValueConstructorTable.class */
public class TableValueConstructorTable extends VirtualConstructedTable {
    private final ArrayList<ArrayList<Expression>> rows;

    public TableValueConstructorTable(Schema schema, SessionLocal sessionLocal, Column[] columnArr, ArrayList<ArrayList<Expression>> arrayList) {
        super(schema, 0, "VALUES");
        setColumns(columnArr);
        this.rows = arrayList;
    }

    @Override // org.h2.table.Table
    public boolean canGetRowCount(SessionLocal sessionLocal) {
        return true;
    }

    @Override // org.h2.table.Table
    public long getRowCount(SessionLocal sessionLocal) {
        return this.rows.size();
    }

    @Override // org.h2.table.Table
    public long getRowCountApproximation(SessionLocal sessionLocal) {
        return this.rows.size();
    }

    @Override // org.h2.table.VirtualConstructedTable
    public ResultInterface getResult(SessionLocal sessionLocal) {
        SimpleResult simpleResult = new SimpleResult();
        int length = this.columns.length;
        for (int i = 0; i < length; i++) {
            Column column = this.columns[i];
            simpleResult.addColumn(column.getName(), column.getType());
        }
        TableValueConstructor.getVisibleResult(sessionLocal, simpleResult, this.columns, this.rows);
        return simpleResult;
    }

    @Override // org.h2.schema.SchemaObject, org.h2.engine.DbObject, org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        sb.append('(');
        TableValueConstructor.getValuesSQL(sb, i, this.rows);
        return sb.append(')');
    }

    @Override // org.h2.table.Table
    public boolean isDeterministic() {
        return true;
    }
}
