package org.h2.command.query;

import org.h2.expression.Expression;
import org.h2.result.SortOrder;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/query/QueryOrderBy.class */
public class QueryOrderBy {
    public Expression expression;
    public Expression columnIndexExpr;
    public int sortType;

    public void getSQL(StringBuilder sb, int i) {
        (this.expression != null ? this.expression : this.columnIndexExpr).getUnenclosedSQL(sb, i);
        SortOrder.typeToString(sb, this.sortType);
    }
}
