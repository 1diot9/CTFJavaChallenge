package org.h2.expression.function;

import org.h2.command.Command;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/SessionControlFunction.class */
public final class SessionControlFunction extends Function1 {
    public static final int ABORT_SESSION = 0;
    public static final int CANCEL_SESSION = 1;
    private static final String[] NAMES = {"ABORT_SESSION", "CANCEL_SESSION"};
    private final int function;

    public SessionControlFunction(Expression expression, int i) {
        super(expression);
        this.function = i;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value value = this.arg.getValue(sessionLocal);
        if (value == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        int i = value.getInt();
        sessionLocal.getUser().checkAdmin();
        SessionLocal[] sessions = sessionLocal.getDatabase().getSessions(false);
        int length = sessions.length;
        int i2 = 0;
        while (true) {
            if (i2 >= length) {
                break;
            }
            SessionLocal sessionLocal2 = sessions[i2];
            if (sessionLocal2.getId() != i) {
                i2++;
            } else {
                Command currentCommand = sessionLocal2.getCurrentCommand();
                switch (this.function) {
                    case 0:
                        if (currentCommand != null) {
                            currentCommand.cancel();
                        }
                        sessionLocal2.close();
                        return ValueBoolean.TRUE;
                    case 1:
                        if (currentCommand != null) {
                            currentCommand.cancel();
                            return ValueBoolean.TRUE;
                        }
                        break;
                    default:
                        throw DbException.getInternalError("function=" + this.function);
                }
            }
        }
        return ValueBoolean.FALSE;
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.arg = this.arg.optimize(sessionLocal);
        this.type = TypeInfo.TYPE_BOOLEAN;
        return this;
    }

    @Override // org.h2.expression.Operation1, org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        switch (expressionVisitor.getType()) {
            case 2:
            case 5:
            case 8:
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
