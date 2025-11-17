package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.index.IndexCondition;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/ValueExpression.class */
public class ValueExpression extends Operation0 {
    public static final ValueExpression NULL = new ValueExpression(ValueNull.INSTANCE);
    public static final ValueExpression DEFAULT = new ValueExpression(ValueNull.INSTANCE);
    public static final ValueExpression TRUE = new ValueExpression(ValueBoolean.TRUE);
    public static final ValueExpression FALSE = new ValueExpression(ValueBoolean.FALSE);
    final Value value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ValueExpression(Value value) {
        this.value = value;
    }

    public static ValueExpression get(Value value) {
        if (value == ValueNull.INSTANCE) {
            return NULL;
        }
        if (value.getValueType() == 8) {
            return getBoolean(value.getBoolean());
        }
        return new ValueExpression(value);
    }

    public static ValueExpression getBoolean(Value value) {
        if (value == ValueNull.INSTANCE) {
            return TypedValueExpression.UNKNOWN;
        }
        return getBoolean(value.getBoolean());
    }

    public static ValueExpression getBoolean(boolean z) {
        return z ? TRUE : FALSE;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        return this.value;
    }

    @Override // org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        return this.value.getType();
    }

    @Override // org.h2.expression.Expression
    public void createIndexConditions(SessionLocal sessionLocal, TableFilter tableFilter) {
        if (this.value.getValueType() == 8 && !this.value.getBoolean()) {
            tableFilter.addIndexCondition(IndexCondition.get(9, null, this));
        }
    }

    @Override // org.h2.expression.Expression
    public Expression getNotIfPossible(SessionLocal sessionLocal) {
        if (this.value == ValueNull.INSTANCE) {
            return TypedValueExpression.UNKNOWN;
        }
        return getBoolean(!this.value.getBoolean());
    }

    @Override // org.h2.expression.Expression
    public boolean isConstant() {
        return true;
    }

    @Override // org.h2.expression.Expression
    public boolean isNullConstant() {
        return this == NULL;
    }

    @Override // org.h2.expression.Expression
    public boolean isValueSet() {
        return true;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        if (this == DEFAULT) {
            sb.append("DEFAULT");
        } else {
            this.value.getSQL(sb, i);
        }
        return sb;
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        return true;
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        return 0;
    }
}
