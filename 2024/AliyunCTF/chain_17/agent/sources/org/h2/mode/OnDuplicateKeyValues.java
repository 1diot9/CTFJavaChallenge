package org.h2.mode;

import org.h2.command.dml.Update;
import org.h2.engine.SessionLocal;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Operation0;
import org.h2.message.DbException;
import org.h2.table.Column;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/OnDuplicateKeyValues.class */
public final class OnDuplicateKeyValues extends Operation0 {
    private final Column column;
    private final Update update;

    public OnDuplicateKeyValues(Column column, Update update) {
        this.column = column;
        this.update = update;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value onDuplicateKeyValue = this.update.getOnDuplicateKeyInsert().getOnDuplicateKeyValue(this.column.getColumnId());
        if (onDuplicateKeyValue == null) {
            throw DbException.getUnsupportedException(getTraceSQL());
        }
        return onDuplicateKeyValue;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        return this.column.getSQL(sb.append("VALUES("), i).append(')');
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        switch (expressionVisitor.getType()) {
            case 2:
                return false;
            default:
                return true;
        }
    }

    @Override // org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        return this.column.getType();
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        return 1;
    }
}
