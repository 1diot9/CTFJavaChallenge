package org.h2.expression;

import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.expression.condition.Comparison;
import org.h2.message.DbException;
import org.h2.table.Column;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/Parameter.class */
public final class Parameter extends Operation0 implements ParameterInterface {
    private Value value;
    private Column column;
    private final int index;

    public Parameter(int i) {
        this.index = i;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        return sb.append('?').append(this.index + 1);
    }

    @Override // org.h2.expression.ParameterInterface
    public void setValue(Value value, boolean z) {
        this.value = value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    @Override // org.h2.expression.ParameterInterface
    public Value getParamValue() {
        if (this.value == null) {
            return ValueNull.INSTANCE;
        }
        return this.value;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        return getParamValue();
    }

    @Override // org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        if (this.value != null) {
            return this.value.getType();
        }
        if (this.column != null) {
            return this.column.getType();
        }
        return TypeInfo.TYPE_UNKNOWN;
    }

    @Override // org.h2.expression.ParameterInterface
    public void checkSet() {
        if (this.value == null) {
            throw DbException.get(ErrorCode.PARAMETER_NOT_SET_1, "#" + (this.index + 1));
        }
    }

    @Override // org.h2.expression.Operation0, org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        if (sessionLocal.getDatabase().getMode().treatEmptyStringsAsNull && (this.value instanceof ValueVarchar) && this.value.getString().isEmpty()) {
            this.value = ValueNull.INSTANCE;
        }
        return this;
    }

    @Override // org.h2.expression.Expression
    public boolean isValueSet() {
        return this.value != null;
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        switch (expressionVisitor.getType()) {
            case 0:
                return this.value != null;
            default:
                return true;
        }
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        return 0;
    }

    @Override // org.h2.expression.Expression
    public Expression getNotIfPossible(SessionLocal sessionLocal) {
        return new Comparison(0, this, ValueExpression.FALSE, false);
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public int getIndex() {
        return this.index;
    }
}
