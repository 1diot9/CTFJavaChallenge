package org.h2.expression;

import org.h2.command.Prepared;
import org.h2.engine.SessionLocal;
import org.h2.schema.Sequence;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/SequenceValue.class */
public final class SequenceValue extends Operation0 {
    private final Sequence sequence;
    private final boolean current;
    private final Prepared prepared;

    public SequenceValue(Sequence sequence, Prepared prepared) {
        this.sequence = sequence;
        this.current = false;
        this.prepared = prepared;
    }

    public SequenceValue(Sequence sequence) {
        this.sequence = sequence;
        this.current = true;
        this.prepared = null;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        return this.current ? sessionLocal.getCurrentValueFor(this.sequence) : sessionLocal.getNextValueFor(this.sequence, this.prepared);
    }

    @Override // org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        return this.sequence.getDataType();
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        sb.append(this.current ? "CURRENT" : "NEXT").append(" VALUE FOR ");
        return this.sequence.getSQL(sb, i);
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        switch (expressionVisitor.getType()) {
            case 0:
            case 2:
            case 8:
                return false;
            case 1:
            case 3:
            case 6:
            default:
                return true;
            case 4:
                expressionVisitor.addDataModificationId(this.sequence.getModificationId());
                return true;
            case 5:
                return this.current;
            case 7:
                expressionVisitor.addDependency(this.sequence);
                return true;
        }
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        return 1;
    }
}
