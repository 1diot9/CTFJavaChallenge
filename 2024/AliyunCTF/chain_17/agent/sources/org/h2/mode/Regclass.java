package org.h2.mode;

import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.Operation1;
import org.h2.expression.ValueExpression;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/Regclass.class */
public final class Regclass extends Operation1 {
    public Regclass(Expression expression) {
        super(expression);
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value value = this.arg.getValue(sessionLocal);
        if (value == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        int valueType = value.getValueType();
        if (valueType >= 9 && valueType <= 11) {
            return value.convertToInt(null);
        }
        if (valueType == 12) {
            return ValueInteger.get((int) value.getLong());
        }
        String string = value.getString();
        for (Schema schema : sessionLocal.getDatabase().getAllSchemas()) {
            Table findTableOrView = schema.findTableOrView(sessionLocal, string);
            if (findTableOrView != null && !findTableOrView.isHidden()) {
                return ValueInteger.get(findTableOrView.getId());
            }
            Index findIndex = schema.findIndex(sessionLocal, string);
            if (findIndex != null && findIndex.getCreateSQL() != null) {
                return ValueInteger.get(findIndex.getId());
            }
        }
        throw DbException.get(ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1, string);
    }

    @Override // org.h2.expression.Operation1, org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.TYPE_INTEGER;
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.arg = this.arg.optimize(sessionLocal);
        if (this.arg.isConstant()) {
            return ValueExpression.get(getValue(sessionLocal));
        }
        return this;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        return this.arg.getSQL(sb, i, 0).append("::REGCLASS");
    }

    @Override // org.h2.expression.Operation1, org.h2.expression.Expression
    public int getCost() {
        return this.arg.getCost() + 100;
    }
}
