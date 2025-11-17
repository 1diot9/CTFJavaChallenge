package org.h2.command.dml;

import org.h2.command.Prepared;
import org.h2.engine.SessionLocal;
import org.h2.expression.Alias;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.function.table.TableFunction;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.table.Column;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/Call.class */
public class Call extends Prepared {
    private Expression expression;
    private TableFunction tableFunction;
    private Expression[] expressions;

    public Call(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    @Override // org.h2.command.Prepared
    public ResultInterface queryMeta() {
        int length = this.expressions.length;
        LocalResult localResult = new LocalResult(this.session, this.expressions, length, length);
        localResult.done();
        return localResult;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        if (this.tableFunction != null) {
            return super.update();
        }
        switch (this.expression.getValue(this.session).getValueType()) {
            case -1:
            case 0:
                return 0L;
            default:
                return r0.getInt();
        }
    }

    @Override // org.h2.command.Prepared
    public ResultInterface query(long j) {
        setCurrentRowNumber(1L);
        if (this.tableFunction != null) {
            return this.tableFunction.getValue(this.session);
        }
        LocalResult localResult = new LocalResult(this.session, this.expressions, 1, 1);
        localResult.addRow(this.expression.getValue(this.session));
        localResult.done();
        return localResult;
    }

    @Override // org.h2.command.Prepared
    public void prepare() {
        if (this.tableFunction != null) {
            this.prepareAlways = true;
            this.tableFunction.optimize(this.session);
            ResultInterface valueTemplate = this.tableFunction.getValueTemplate(this.session);
            int visibleColumnCount = valueTemplate.getVisibleColumnCount();
            this.expressions = new Expression[visibleColumnCount];
            for (int i = 0; i < visibleColumnCount; i++) {
                String columnName = valueTemplate.getColumnName(i);
                String alias = valueTemplate.getAlias(i);
                Expression expressionColumn = new ExpressionColumn(getDatabase(), new Column(columnName, valueTemplate.getColumnType(i)));
                if (!alias.equals(columnName)) {
                    expressionColumn = new Alias(expressionColumn, alias, false);
                }
                this.expressions[i] = expressionColumn;
            }
            return;
        }
        Expression optimize = this.expression.optimize(this.session);
        this.expression = optimize;
        this.expressions = new Expression[]{optimize};
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public void setTableFunction(TableFunction tableFunction) {
        this.tableFunction = tableFunction;
    }

    @Override // org.h2.command.Prepared
    public boolean isQuery() {
        return true;
    }

    @Override // org.h2.command.Prepared
    public boolean isTransactional() {
        return true;
    }

    @Override // org.h2.command.Prepared
    public boolean isReadOnly() {
        return this.tableFunction == null && this.expression.isEverything(ExpressionVisitor.READONLY_VISITOR);
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 57;
    }

    @Override // org.h2.command.Prepared
    public boolean isCacheable() {
        return this.tableFunction == null;
    }
}
