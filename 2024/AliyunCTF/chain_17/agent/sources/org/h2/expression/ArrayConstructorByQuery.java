package org.h2.expression;

import java.util.ArrayList;
import org.h2.api.ErrorCode;
import org.h2.command.query.Query;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/ArrayConstructorByQuery.class */
public final class ArrayConstructorByQuery extends Expression {
    private final Query query;
    private TypeInfo componentType;
    private TypeInfo type;

    public ArrayConstructorByQuery(Query query) {
        this.query = query;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        return StringUtils.indent(sb.append("ARRAY ("), this.query.getPlanSQL(i), 4, false).append(')');
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        this.query.setSession(sessionLocal);
        ArrayList arrayList = new ArrayList();
        ResultInterface query = this.query.query(0L);
        Throwable th = null;
        while (query.next()) {
            try {
                try {
                    arrayList.add(query.currentRow()[0]);
                } finally {
                }
            } catch (Throwable th2) {
                if (query != null) {
                    if (th != null) {
                        try {
                            query.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                    } else {
                        query.close();
                    }
                }
                throw th2;
            }
        }
        if (query != null) {
            if (0 != 0) {
                try {
                    query.close();
                } catch (Throwable th4) {
                    th.addSuppressed(th4);
                }
            } else {
                query.close();
            }
        }
        return ValueArray.get(this.componentType, (Value[]) arrayList.toArray(new Value[0]), sessionLocal);
    }

    @Override // org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        return this.type;
    }

    @Override // org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        this.query.mapColumns(columnResolver, i + 1);
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.query.prepare();
        if (this.query.getColumnCount() != 1) {
            throw DbException.get(ErrorCode.SUBQUERY_IS_NOT_SINGLE_COLUMN);
        }
        this.componentType = this.query.getExpressions().get(0).getType();
        this.type = TypeInfo.getTypeInfo(40, -1L, -1, this.componentType);
        return this;
    }

    @Override // org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        this.query.setEvaluatable(tableFilter, z);
    }

    @Override // org.h2.expression.Expression
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        this.query.updateAggregate(sessionLocal, i);
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        return this.query.isEverything(expressionVisitor);
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        return this.query.getCostAsExpression();
    }
}
