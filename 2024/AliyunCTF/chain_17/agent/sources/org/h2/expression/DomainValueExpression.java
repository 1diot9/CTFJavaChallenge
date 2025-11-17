package org.h2.expression;

import org.h2.api.ErrorCode;
import org.h2.constraint.DomainColumnResolver;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.table.ColumnResolver;
import org.h2.util.ParserUtil;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/DomainValueExpression.class */
public final class DomainValueExpression extends Operation0 {
    private DomainColumnResolver columnResolver;

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        return this.columnResolver.getValue(null);
    }

    @Override // org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        return this.columnResolver.getValueType();
    }

    @Override // org.h2.expression.Operation0, org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        if (columnResolver instanceof DomainColumnResolver) {
            this.columnResolver = (DomainColumnResolver) columnResolver;
        }
    }

    @Override // org.h2.expression.Operation0, org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        if (this.columnResolver == null) {
            throw DbException.get(ErrorCode.COLUMN_NOT_FOUND_1, "VALUE");
        }
        return this;
    }

    @Override // org.h2.expression.Expression
    public boolean isValueSet() {
        return this.columnResolver.getValue(null) != null;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        String columnName;
        if (this.columnResolver != null && (columnName = this.columnResolver.getColumnName()) != null) {
            return ParserUtil.quoteIdentifier(sb, columnName, i);
        }
        return sb.append("VALUE");
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        return true;
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        return 1;
    }
}
