package org.h2.command.dml;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/FilteredDataChangeStatement.class */
public abstract class FilteredDataChangeStatement extends DataChangeStatement {
    Expression condition;
    TableFilter targetTableFilter;
    Expression fetchExpr;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FilteredDataChangeStatement(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    @Override // org.h2.command.dml.DataChangeStatement
    public final Table getTable() {
        return this.targetTableFilter.getTable();
    }

    public final void setTableFilter(TableFilter tableFilter) {
        this.targetTableFilter = tableFilter;
    }

    public final TableFilter getTableFilter() {
        return this.targetTableFilter;
    }

    public final void setCondition(Expression expression) {
        this.condition = expression;
    }

    public final Expression getCondition() {
        return this.condition;
    }

    public void setFetch(Expression expression) {
        this.fetchExpr = expression;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean nextRow(long j, long j2) {
        if (j < 0 || j2 < j) {
            while (this.targetTableFilter.next()) {
                setCurrentRowNumber(j2 + 1);
                if (this.condition == null || this.condition.getBooleanValue(this.session)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void appendFilterCondition(StringBuilder sb, int i) {
        if (this.condition != null) {
            sb.append("\nWHERE ");
            this.condition.getUnenclosedSQL(sb, i);
        }
        if (this.fetchExpr != null) {
            sb.append("\nFETCH FIRST ");
            String sql = this.fetchExpr.getSQL(i, 2);
            if (CustomBooleanEditor.VALUE_1.equals(sql)) {
                sb.append("ROW ONLY");
            } else {
                sb.append(sql).append(" ROWS ONLY");
            }
        }
    }
}
