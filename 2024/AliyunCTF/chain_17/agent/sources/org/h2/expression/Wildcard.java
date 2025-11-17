package org.h2.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/Wildcard.class */
public final class Wildcard extends Expression {
    private final String schema;
    private final String table;
    private ArrayList<ExpressionColumn> exceptColumns;

    public Wildcard(String str, String str2) {
        this.schema = str;
        this.table = str2;
    }

    public ArrayList<ExpressionColumn> getExceptColumns() {
        return this.exceptColumns;
    }

    public void setExceptColumns(ArrayList<ExpressionColumn> arrayList) {
        this.exceptColumns = arrayList;
    }

    public HashMap<Column, ExpressionColumn> mapExceptColumns() {
        HashMap<Column, ExpressionColumn> hashMap = new HashMap<>();
        Iterator<ExpressionColumn> it = this.exceptColumns.iterator();
        while (it.hasNext()) {
            ExpressionColumn next = it.next();
            Column column = next.getColumn();
            if (column == null) {
                throw next.getColumnException(ErrorCode.COLUMN_NOT_FOUND_1);
            }
            if (hashMap.putIfAbsent(column, next) != null) {
                throw next.getColumnException(ErrorCode.DUPLICATE_COLUMN_NAME_1);
            }
        }
        return hashMap;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        throw DbException.getInternalError(toString());
    }

    @Override // org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        throw DbException.getInternalError(toString());
    }

    @Override // org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        if (this.exceptColumns != null) {
            Iterator<ExpressionColumn> it = this.exceptColumns.iterator();
            while (it.hasNext()) {
                it.next().mapColumns(columnResolver, i, i2);
            }
        }
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        throw DbException.get(ErrorCode.SYNTAX_ERROR_1, this.table);
    }

    @Override // org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        throw DbException.getInternalError(toString());
    }

    @Override // org.h2.expression.Expression
    public String getTableAlias() {
        return this.table;
    }

    @Override // org.h2.expression.Expression
    public String getSchemaName() {
        return this.schema;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        if (this.table != null) {
            StringUtils.quoteIdentifier(sb, this.table).append('.');
        }
        sb.append('*');
        if (this.exceptColumns != null) {
            writeExpressions(sb.append(" EXCEPT ("), this.exceptColumns, i).append(')');
        }
        return sb;
    }

    @Override // org.h2.expression.Expression
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        throw DbException.getInternalError(toString());
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        if (expressionVisitor.getType() == 8) {
            return true;
        }
        throw DbException.getInternalError(Integer.toString(expressionVisitor.getType()));
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        throw DbException.getInternalError(toString());
    }
}
