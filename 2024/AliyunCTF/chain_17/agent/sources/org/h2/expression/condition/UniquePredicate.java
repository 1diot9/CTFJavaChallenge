package org.h2.expression.condition;

import java.util.Arrays;
import org.h2.command.query.Query;
import org.h2.engine.NullsDistinct;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.ValueExpression;
import org.h2.result.LocalResult;
import org.h2.result.ResultTarget;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/condition/UniquePredicate.class */
public class UniquePredicate extends PredicateWithSubquery {
    private final NullsDistinct nullsDistinct;

    @Override // org.h2.expression.condition.PredicateWithSubquery, org.h2.expression.Expression
    public /* bridge */ /* synthetic */ int getCost() {
        return super.getCost();
    }

    @Override // org.h2.expression.condition.PredicateWithSubquery, org.h2.expression.Expression
    public /* bridge */ /* synthetic */ boolean isEverything(ExpressionVisitor expressionVisitor) {
        return super.isEverything(expressionVisitor);
    }

    @Override // org.h2.expression.condition.PredicateWithSubquery, org.h2.expression.Expression
    public /* bridge */ /* synthetic */ void updateAggregate(SessionLocal sessionLocal, int i) {
        super.updateAggregate(sessionLocal, i);
    }

    @Override // org.h2.expression.condition.PredicateWithSubquery, org.h2.expression.Expression
    public /* bridge */ /* synthetic */ void setEvaluatable(TableFilter tableFilter, boolean z) {
        super.setEvaluatable(tableFilter, z);
    }

    @Override // org.h2.expression.condition.PredicateWithSubquery, org.h2.expression.Expression
    public /* bridge */ /* synthetic */ void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        super.mapColumns(columnResolver, i, i2);
    }

    @Override // org.h2.expression.condition.Condition, org.h2.expression.Expression, org.h2.value.Typed
    public /* bridge */ /* synthetic */ TypeInfo getType() {
        return super.getType();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/condition/UniquePredicate$Target.class */
    private static final class Target implements ResultTarget {
        private final int columnCount;
        private final NullsDistinct nullsDistinct;
        private final LocalResult result;
        boolean hasDuplicates;

        Target(int i, NullsDistinct nullsDistinct, LocalResult localResult) {
            this.columnCount = i;
            this.nullsDistinct = nullsDistinct;
            this.result = localResult;
        }

        @Override // org.h2.result.ResultTarget
        public void limitsWereApplied() {
        }

        @Override // org.h2.result.ResultTarget
        public long getRowCount() {
            return 0L;
        }

        @Override // org.h2.result.ResultTarget
        public void addRow(Value... valueArr) {
            if (this.hasDuplicates) {
                return;
            }
            switch (this.nullsDistinct) {
                case DISTINCT:
                    for (int i = 0; i < this.columnCount; i++) {
                        if (valueArr[i] == ValueNull.INSTANCE) {
                            return;
                        }
                    }
                    break;
                case ALL_DISTINCT:
                    for (int i2 = 0; i2 < this.columnCount; i2++) {
                        if (valueArr[i2] != ValueNull.INSTANCE) {
                            break;
                        }
                    }
                    return;
            }
            if (valueArr.length != this.columnCount) {
                valueArr = (Value[]) Arrays.copyOf(valueArr, this.columnCount);
            }
            long rowCount = this.result.getRowCount() + 1;
            this.result.addRow(valueArr);
            if (rowCount != this.result.getRowCount()) {
                this.hasDuplicates = true;
                this.result.close();
            }
        }
    }

    public UniquePredicate(Query query, NullsDistinct nullsDistinct) {
        super(query);
        this.nullsDistinct = nullsDistinct;
    }

    @Override // org.h2.expression.condition.PredicateWithSubquery, org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        super.optimize(sessionLocal);
        if (this.query.isStandardDistinct()) {
            return ValueExpression.TRUE;
        }
        return this;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        this.query.setSession(sessionLocal);
        int columnCount = this.query.getColumnCount();
        LocalResult localResult = new LocalResult(sessionLocal, (Expression[]) this.query.getExpressions().toArray(new Expression[0]), columnCount, columnCount);
        localResult.setDistinct();
        Target target = new Target(columnCount, this.nullsDistinct, localResult);
        this.query.query(2147483647L, target);
        localResult.close();
        return ValueBoolean.get(!target.hasDuplicates);
    }

    @Override // org.h2.expression.condition.PredicateWithSubquery, org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        sb.append("UNIQUE");
        if (this.nullsDistinct != NullsDistinct.DISTINCT) {
            this.nullsDistinct.getSQL(sb.append(' '), 0);
        }
        return super.getUnenclosedSQL(sb, i);
    }
}
