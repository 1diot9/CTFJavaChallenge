package org.h2.table;

import java.util.ArrayList;
import org.h2.command.query.Query;
import org.h2.engine.SessionLocal;
import org.h2.expression.ExpressionVisitor;
import org.h2.index.QueryExpressionIndex;
import org.h2.message.DbException;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/DerivedTable.class */
public final class DerivedTable extends QueryExpressionTable {
    private String querySQL;
    private Query topQuery;

    public DerivedTable(SessionLocal sessionLocal, String str, Column[] columnArr, Query query, Query query2) {
        super(sessionLocal.getDatabase().getMainSchema(), 0, str);
        setTemporary(true);
        this.topQuery = query2;
        query.prepareExpressions();
        try {
            this.querySQL = query.getPlanSQL(0);
            this.index = new QueryExpressionIndex(this, this.querySQL, query.getParameters(), false);
            this.tables = new ArrayList<>(query.getTables());
            setColumns(initColumns(sessionLocal, columnArr, query, true));
            this.viewQuery = query;
        } catch (DbException e) {
            if (e.getErrorCode() == 90156) {
                throw e;
            }
            e.addSQL(getCreateSQL());
            throw e;
        }
    }

    @Override // org.h2.table.QueryExpressionTable, org.h2.table.Table
    public boolean isQueryComparable() {
        if (!super.isQueryComparable()) {
            return false;
        }
        if (this.topQuery != null && !this.topQuery.isEverything(ExpressionVisitor.QUERY_COMPARABLE_VISITOR)) {
            return false;
        }
        return true;
    }

    @Override // org.h2.table.Table
    public boolean canDrop() {
        return false;
    }

    @Override // org.h2.table.Table
    public TableType getTableType() {
        return null;
    }

    @Override // org.h2.table.QueryExpressionTable
    public Query getTopQuery() {
        return this.topQuery;
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQL() {
        return null;
    }

    @Override // org.h2.schema.SchemaObject, org.h2.engine.DbObject, org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return StringUtils.indent(sb.append("(\n"), this.querySQL, 4, true).append(')');
    }
}
