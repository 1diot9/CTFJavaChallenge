package org.h2.expression.function.table;

import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.schema.FunctionAlias;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/table/JavaTableFunction.class */
public final class JavaTableFunction extends TableFunction {
    private final FunctionAlias functionAlias;
    private final FunctionAlias.JavaMethod javaMethod;

    public JavaTableFunction(FunctionAlias functionAlias, Expression[] expressionArr) {
        super(expressionArr);
        this.functionAlias = functionAlias;
        this.javaMethod = functionAlias.findJavaMethod(expressionArr);
        if (this.javaMethod.getDataType() != null) {
            throw DbException.get(ErrorCode.FUNCTION_MUST_RETURN_RESULT_SET_1, getName());
        }
    }

    @Override // org.h2.expression.function.table.TableFunction
    public ResultInterface getValue(SessionLocal sessionLocal) {
        return this.javaMethod.getTableValue(sessionLocal, this.args, false);
    }

    @Override // org.h2.expression.function.table.TableFunction
    public ResultInterface getValueTemplate(SessionLocal sessionLocal) {
        return this.javaMethod.getTableValue(sessionLocal, this.args, true);
    }

    @Override // org.h2.expression.function.table.TableFunction
    public void optimize(SessionLocal sessionLocal) {
        super.optimize(sessionLocal);
    }

    @Override // org.h2.expression.function.table.TableFunction, org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return Expression.writeExpressions(this.functionAlias.getSQL(sb, i).append('('), this.args, i).append(')');
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return this.functionAlias.getName();
    }

    @Override // org.h2.expression.function.table.TableFunction
    public boolean isDeterministic() {
        return this.functionAlias.isDeterministic();
    }
}
