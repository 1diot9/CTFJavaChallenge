package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.ExtTypeInfoRow;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueRow;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/ExpressionList.class */
public final class ExpressionList extends Expression {
    private final Expression[] list;
    private final boolean isArray;
    private TypeInfo type;

    public ExpressionList(Expression[] expressionArr, boolean z) {
        this.list = expressionArr;
        this.isArray = z;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value[] valueArr = new Value[this.list.length];
        for (int i = 0; i < this.list.length; i++) {
            valueArr[i] = this.list[i].getValue(sessionLocal);
        }
        return this.isArray ? ValueArray.get((TypeInfo) this.type.getExtTypeInfo(), valueArr, sessionLocal) : ValueRow.get(this.type, valueArr);
    }

    @Override // org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        return this.type;
    }

    @Override // org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        for (Expression expression : this.list) {
            expression.mapColumns(columnResolver, i, i2);
        }
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        boolean z = true;
        int length = this.list.length;
        for (int i = 0; i < length; i++) {
            Expression optimize = this.list[i].optimize(sessionLocal);
            if (!optimize.isConstant()) {
                z = false;
            }
            this.list[i] = optimize;
        }
        initializeType();
        if (z) {
            return ValueExpression.get(getValue(sessionLocal));
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initializeType() {
        this.type = this.isArray ? TypeInfo.getTypeInfo(40, this.list.length, 0, TypeInfo.getHigherType(this.list)) : TypeInfo.getTypeInfo(41, 0L, 0, new ExtTypeInfoRow(this.list));
    }

    @Override // org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        for (Expression expression : this.list) {
            expression.setEvaluatable(tableFilter, z);
        }
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        if (this.isArray) {
            return writeExpressions(sb.append("ARRAY ["), this.list, i).append(']');
        }
        return writeExpressions(sb.append("ROW ("), this.list, i).append(')');
    }

    @Override // org.h2.expression.Expression
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        for (Expression expression : this.list) {
            expression.updateAggregate(sessionLocal, i);
        }
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        for (Expression expression : this.list) {
            if (!expression.isEverything(expressionVisitor)) {
                return false;
            }
        }
        return true;
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        int i = 1;
        for (Expression expression : this.list) {
            i += expression.getCost();
        }
        return i;
    }

    @Override // org.h2.expression.Expression
    public boolean isConstant() {
        for (Expression expression : this.list) {
            if (!expression.isConstant()) {
                return false;
            }
        }
        return true;
    }

    @Override // org.h2.expression.Expression
    public int getSubexpressionCount() {
        return this.list.length;
    }

    @Override // org.h2.expression.Expression
    public Expression getSubexpression(int i) {
        return this.list[i];
    }

    public boolean isArray() {
        return this.isArray;
    }
}
