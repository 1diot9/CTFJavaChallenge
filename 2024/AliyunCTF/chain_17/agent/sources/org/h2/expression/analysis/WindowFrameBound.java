package org.h2.expression.analysis;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.table.ColumnResolver;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/analysis/WindowFrameBound.class */
public class WindowFrameBound {
    private final WindowFrameBoundType type;
    private Expression value;
    private boolean isVariable;
    private int expressionIndex = -1;

    public WindowFrameBound(WindowFrameBoundType windowFrameBoundType, Expression expression) {
        this.type = windowFrameBoundType;
        if (windowFrameBoundType == WindowFrameBoundType.PRECEDING || windowFrameBoundType == WindowFrameBoundType.FOLLOWING) {
            this.value = expression;
        } else {
            this.value = null;
        }
    }

    public WindowFrameBoundType getType() {
        return this.type;
    }

    public Expression getValue() {
        return this.value;
    }

    public boolean isParameterized() {
        return this.type == WindowFrameBoundType.PRECEDING || this.type == WindowFrameBoundType.FOLLOWING;
    }

    public boolean isVariable() {
        return this.isVariable;
    }

    public int getExpressionIndex() {
        return this.expressionIndex;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setExpressionIndex(int i) {
        this.expressionIndex = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        if (this.value != null) {
            this.value.mapColumns(columnResolver, i, i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void optimize(SessionLocal sessionLocal) {
        if (this.value != null) {
            this.value = this.value.optimize(sessionLocal);
            if (!this.value.isConstant()) {
                this.isVariable = true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        if (this.value != null) {
            this.value.updateAggregate(sessionLocal, i);
        }
    }

    public StringBuilder getSQL(StringBuilder sb, boolean z, int i) {
        if (this.type == WindowFrameBoundType.PRECEDING || this.type == WindowFrameBoundType.FOLLOWING) {
            this.value.getUnenclosedSQL(sb, i).append(' ');
        }
        return sb.append(this.type.getSQL());
    }
}
