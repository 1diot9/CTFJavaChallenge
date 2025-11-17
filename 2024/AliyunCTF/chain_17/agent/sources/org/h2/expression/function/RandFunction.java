package org.h2.expression.function;

import java.util.Random;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.message.DbException;
import org.h2.util.MathUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueDouble;
import org.h2.value.ValueNull;
import org.h2.value.ValueUuid;
import org.h2.value.ValueVarbinary;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/RandFunction.class */
public final class RandFunction extends Function0_1 {
    public static final int RAND = 0;
    public static final int SECURE_RAND = 1;
    public static final int RANDOM_UUID = 2;
    private static final String[] NAMES = {"RAND", "SECURE_RAND", "RANDOM_UUID"};
    private final int function;

    public RandFunction(Expression expression, int i) {
        super(expression);
        this.function = i;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value value;
        Value newRandom;
        if (this.arg != null) {
            value = this.arg.getValue(sessionLocal);
            if (value == ValueNull.INSTANCE) {
                return ValueNull.INSTANCE;
            }
        } else {
            value = null;
        }
        switch (this.function) {
            case 0:
                Random random = sessionLocal.getRandom();
                if (value != null) {
                    random.setSeed(value.getInt());
                }
                newRandom = ValueDouble.get(random.nextDouble());
                break;
            case 1:
                newRandom = ValueVarbinary.getNoCopy(MathUtils.secureRandomBytes(value.getInt()));
                break;
            case 2:
                newRandom = ValueUuid.getNewRandom();
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        return newRandom;
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        Value value;
        if (this.arg != null) {
            this.arg = this.arg.optimize(sessionLocal);
        }
        switch (this.function) {
            case 0:
                this.type = TypeInfo.TYPE_DOUBLE;
                break;
            case 1:
                this.type = (!this.arg.isConstant() || (value = this.arg.getValue(sessionLocal)) == ValueNull.INSTANCE) ? TypeInfo.TYPE_VARBINARY : TypeInfo.getTypeInfo(6, Math.max(value.getInt(), 1), 0, null);
                break;
            case 2:
                this.type = TypeInfo.TYPE_UUID;
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        return this;
    }

    @Override // org.h2.expression.function.Function0_1, org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        switch (expressionVisitor.getType()) {
            case 2:
                return false;
            default:
                return super.isEverything(expressionVisitor);
        }
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return NAMES[this.function];
    }
}
