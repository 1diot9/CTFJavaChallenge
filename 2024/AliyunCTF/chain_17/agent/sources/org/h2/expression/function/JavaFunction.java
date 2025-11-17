package org.h2.expression.function;

import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.schema.FunctionAlias;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/JavaFunction.class */
public final class JavaFunction extends Expression implements NamedExpression {
    private final FunctionAlias functionAlias;
    private final FunctionAlias.JavaMethod javaMethod;
    private final Expression[] args;

    public JavaFunction(FunctionAlias functionAlias, Expression[] expressionArr) {
        this.functionAlias = functionAlias;
        this.javaMethod = functionAlias.findJavaMethod(expressionArr);
        if (this.javaMethod.getDataType() == null) {
            throw DbException.get(ErrorCode.FUNCTION_NOT_FOUND_1, getName());
        }
        this.args = expressionArr;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        return this.javaMethod.getValue(sessionLocal, this.args, false);
    }

    @Override // org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        return this.javaMethod.getDataType();
    }

    @Override // org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        for (Expression expression : this.args) {
            expression.mapColumns(columnResolver, i, i2);
        }
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        boolean isDeterministic = this.functionAlias.isDeterministic();
        int length = this.args.length;
        for (int i = 0; i < length; i++) {
            Expression optimize = this.args[i].optimize(sessionLocal);
            this.args[i] = optimize;
            isDeterministic &= optimize.isConstant();
        }
        if (isDeterministic) {
            return ValueExpression.get(getValue(sessionLocal));
        }
        return this;
    }

    @Override // org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        for (Expression expression : this.args) {
            if (expression != null) {
                expression.setEvaluatable(tableFilter, z);
            }
        }
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        return writeExpressions(this.functionAlias.getSQL(sb, i).append('('), this.args, i).append(')');
    }

    @Override // org.h2.expression.Expression
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        for (Expression expression : this.args) {
            if (expression != null) {
                expression.updateAggregate(sessionLocal, i);
            }
        }
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return this.functionAlias.getName();
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        switch (expressionVisitor.getType()) {
            case 2:
            case 5:
            case 8:
                if (!this.functionAlias.isDeterministic()) {
                    return false;
                }
                break;
            case 7:
                expressionVisitor.addDependency(this.functionAlias);
                break;
        }
        for (Expression expression : this.args) {
            if (expression != null && !expression.isEverything(expressionVisitor)) {
                return false;
            }
        }
        return true;
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        int i = this.javaMethod.hasConnectionParam() ? 25 : 5;
        for (Expression expression : this.args) {
            i += expression.getCost();
        }
        return i;
    }

    @Override // org.h2.expression.Expression
    public int getSubexpressionCount() {
        return this.args.length;
    }

    @Override // org.h2.expression.Expression
    public Expression getSubexpression(int i) {
        return this.args[i];
    }
}
