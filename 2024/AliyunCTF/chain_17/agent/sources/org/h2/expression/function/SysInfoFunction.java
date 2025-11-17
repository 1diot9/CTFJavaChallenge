package org.h2.expression.function;

import org.h2.engine.Constants;
import org.h2.engine.SessionLocal;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Operation0;
import org.h2.message.DbException;
import org.h2.util.Utils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/SysInfoFunction.class */
public final class SysInfoFunction extends Operation0 implements NamedExpression {
    public static final int AUTOCOMMIT = 0;
    public static final int DATABASE_PATH = 1;
    public static final int H2VERSION = 2;
    public static final int LOCK_MODE = 3;
    public static final int LOCK_TIMEOUT = 4;
    public static final int MEMORY_FREE = 5;
    public static final int MEMORY_USED = 6;
    public static final int READONLY = 7;
    public static final int SESSION_ID = 8;
    public static final int TRANSACTION_ID = 9;
    private static final int[] TYPES = {8, 2, 2, 11, 11, 12, 12, 8, 11, 2};
    private static final String[] NAMES = {"AUTOCOMMIT", "DATABASE_PATH", "H2VERSION", "LOCK_MODE", "LOCK_TIMEOUT", "MEMORY_FREE", "MEMORY_USED", "READONLY", "SESSION_ID", "TRANSACTION_ID"};
    private final int function;
    private final TypeInfo type;

    public static String getName(int i) {
        return NAMES[i];
    }

    public SysInfoFunction(int i) {
        this.function = i;
        this.type = TypeInfo.getTypeInfo(TYPES[i]);
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value transactionId;
        switch (this.function) {
            case 0:
                transactionId = ValueBoolean.get(sessionLocal.getAutoCommit());
                break;
            case 1:
                String databasePath = sessionLocal.getDatabase().getDatabasePath();
                transactionId = databasePath != null ? ValueVarchar.get(databasePath, sessionLocal) : ValueNull.INSTANCE;
                break;
            case 2:
                transactionId = ValueVarchar.get(Constants.VERSION, sessionLocal);
                break;
            case 3:
                transactionId = ValueInteger.get(sessionLocal.getDatabase().getLockMode());
                break;
            case 4:
                transactionId = ValueInteger.get(sessionLocal.getLockTimeout());
                break;
            case 5:
                sessionLocal.getUser().checkAdmin();
                transactionId = ValueBigint.get(Utils.getMemoryFree());
                break;
            case 6:
                sessionLocal.getUser().checkAdmin();
                transactionId = ValueBigint.get(Utils.getMemoryUsed());
                break;
            case 7:
                transactionId = ValueBoolean.get(sessionLocal.getDatabase().isReadOnly());
                break;
            case 8:
                transactionId = ValueInteger.get(sessionLocal.getId());
                break;
            case 9:
                transactionId = sessionLocal.getTransactionId();
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        return transactionId;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        return sb.append(getName()).append("()");
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
        return this.type;
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        return 1;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return NAMES[this.function];
    }
}
