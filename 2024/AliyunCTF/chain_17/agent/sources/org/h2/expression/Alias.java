package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.util.ParserUtil;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/Alias.class */
public final class Alias extends Expression {
    private final String alias;
    private Expression expr;
    private final boolean aliasColumnName;

    public Alias(Expression expression, String str, boolean z) {
        this.expr = expression;
        this.alias = str;
        this.aliasColumnName = z;
    }

    @Override // org.h2.expression.Expression
    public Expression getNonAliasExpression() {
        return this.expr;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        return this.expr.getValue(sessionLocal);
    }

    @Override // org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        return this.expr.getType();
    }

    @Override // org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        this.expr.mapColumns(columnResolver, i, i2);
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.expr = this.expr.optimize(sessionLocal);
        return this;
    }

    @Override // org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        this.expr.setEvaluatable(tableFilter, z);
    }

    @Override // org.h2.expression.Expression
    public boolean isIdentity() {
        return this.expr.isIdentity();
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        this.expr.getUnenclosedSQL(sb, i).append(" AS ");
        return ParserUtil.quoteIdentifier(sb, this.alias, i);
    }

    @Override // org.h2.expression.Expression
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        this.expr.updateAggregate(sessionLocal, i);
    }

    @Override // org.h2.expression.Expression
    public String getAlias(SessionLocal sessionLocal, int i) {
        return this.alias;
    }

    @Override // org.h2.expression.Expression
    public String getColumnNameForView(SessionLocal sessionLocal, int i) {
        return this.alias;
    }

    @Override // org.h2.expression.Expression
    public int getNullable() {
        return this.expr.getNullable();
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        return this.expr.isEverything(expressionVisitor);
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        return this.expr.getCost();
    }

    @Override // org.h2.expression.Expression
    public String getSchemaName() {
        if (this.aliasColumnName) {
            return null;
        }
        return this.expr.getSchemaName();
    }

    @Override // org.h2.expression.Expression
    public String getTableName() {
        if (this.aliasColumnName) {
            return null;
        }
        return this.expr.getTableName();
    }

    @Override // org.h2.expression.Expression
    public String getColumnName(SessionLocal sessionLocal, int i) {
        if (!(this.expr instanceof ExpressionColumn) || this.aliasColumnName) {
            return this.alias;
        }
        return this.expr.getColumnName(sessionLocal, i);
    }
}
