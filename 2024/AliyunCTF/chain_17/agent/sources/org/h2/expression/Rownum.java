package org.h2.expression;

import org.h2.command.Prepared;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/Rownum.class */
public final class Rownum extends Operation0 {
    private final Prepared prepared;
    private boolean singleRow;

    public Rownum(Prepared prepared) {
        if (prepared == null) {
            throw DbException.getInternalError();
        }
        this.prepared = prepared;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        return ValueBigint.get(this.prepared.getCurrentRowNumber());
    }

    @Override // org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.TYPE_BIGINT;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        return sb.append("ROWNUM()");
    }

    @Override // org.h2.expression.Operation0, org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        return this.singleRow ? ValueExpression.get(ValueBigint.get(1L)) : this;
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        switch (expressionVisitor.getType()) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 8:
                return false;
            case 4:
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
            default:
                return true;
            case 11:
                if (expressionVisitor.getQueryLevel() > 0) {
                    this.singleRow = true;
                    return true;
                }
                return true;
        }
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        return 0;
    }
}
