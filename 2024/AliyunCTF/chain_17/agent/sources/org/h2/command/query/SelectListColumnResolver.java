package org.h2.command.query;

import java.util.ArrayList;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/query/SelectListColumnResolver.class */
public class SelectListColumnResolver implements ColumnResolver {
    private final Select select;
    private final Expression[] expressions;
    private final Column[] columns;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SelectListColumnResolver(Select select) {
        this.select = select;
        int columnCount = select.getColumnCount();
        this.columns = new Column[columnCount];
        this.expressions = new Expression[columnCount];
        ArrayList<Expression> expressions = select.getExpressions();
        SessionLocal session = select.getSession();
        for (int i = 0; i < columnCount; i++) {
            Expression expression = expressions.get(i);
            this.columns[i] = new Column(expression.getAlias(session, i), TypeInfo.TYPE_NULL, null, i);
            this.expressions[i] = expression.getNonAliasExpression();
        }
    }

    @Override // org.h2.table.ColumnResolver
    public Column[] getColumns() {
        return this.columns;
    }

    @Override // org.h2.table.ColumnResolver
    public Column findColumn(String str) {
        Database database = this.select.getSession().getDatabase();
        for (Column column : this.columns) {
            if (database.equalsIdentifiers(column.getName(), str)) {
                return column;
            }
        }
        return null;
    }

    @Override // org.h2.table.ColumnResolver
    public Select getSelect() {
        return this.select;
    }

    @Override // org.h2.table.ColumnResolver
    public Value getValue(Column column) {
        return null;
    }

    @Override // org.h2.table.ColumnResolver
    public Expression optimize(ExpressionColumn expressionColumn, Column column) {
        return this.expressions[column.getColumnId()];
    }
}
