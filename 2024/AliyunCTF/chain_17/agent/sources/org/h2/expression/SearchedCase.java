package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/SearchedCase.class */
public final class SearchedCase extends OperationN {
    public SearchedCase() {
        super(new Expression[4]);
    }

    public SearchedCase(Expression[] expressionArr) {
        super(expressionArr);
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        int length = this.args.length - 1;
        for (int i = 0; i < length; i += 2) {
            if (this.args[i].getBooleanValue(sessionLocal)) {
                return this.args[i + 1].getValue(sessionLocal).convertTo(this.type, sessionLocal);
            }
        }
        if ((length & 1) == 0) {
            return this.args[length].getValue(sessionLocal).convertTo(this.type, sessionLocal);
        }
        return ValueNull.INSTANCE;
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        TypeInfo typeInfo = TypeInfo.TYPE_UNKNOWN;
        int length = this.args.length - 1;
        boolean z = true;
        for (int i = 0; i < length; i += 2) {
            Expression optimize = this.args[i].optimize(sessionLocal);
            Expression optimize2 = this.args[i + 1].optimize(sessionLocal);
            if (z) {
                if (optimize.isConstant()) {
                    if (optimize.getBooleanValue(sessionLocal)) {
                        return optimize2;
                    }
                } else {
                    z = false;
                }
            }
            this.args[i] = optimize;
            this.args[i + 1] = optimize2;
            typeInfo = SimpleCase.combineTypes(typeInfo, optimize2);
        }
        if ((length & 1) == 0) {
            Expression optimize3 = this.args[length].optimize(sessionLocal);
            if (z) {
                return optimize3;
            }
            this.args[length] = optimize3;
            typeInfo = SimpleCase.combineTypes(typeInfo, optimize3);
        } else if (z) {
            return ValueExpression.NULL;
        }
        if (typeInfo.getValueType() == -1) {
            typeInfo = TypeInfo.TYPE_VARCHAR;
        }
        this.type = typeInfo;
        return this;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        sb.append("CASE");
        int length = this.args.length - 1;
        for (int i2 = 0; i2 < length; i2 += 2) {
            sb.append(" WHEN ");
            this.args[i2].getUnenclosedSQL(sb, i);
            sb.append(" THEN ");
            this.args[i2 + 1].getUnenclosedSQL(sb, i);
        }
        if ((length & 1) == 0) {
            sb.append(" ELSE ");
            this.args[length].getUnenclosedSQL(sb, i);
        }
        return sb.append(" END");
    }
}
