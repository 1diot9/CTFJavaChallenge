package org.h2.mode;

import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.Mode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.function.CurrentDateTimeValueFunction;
import org.h2.expression.function.FunctionN;
import org.h2.message.DbException;
import org.h2.value.Value;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/ModeFunction.class */
public abstract class ModeFunction extends FunctionN {
    protected static final int VAR_ARGS = -1;
    protected final FunctionInfo info;

    public static ModeFunction getFunction(Database database, String str) {
        Mode.ModeEnum modeEnum = database.getMode().getEnum();
        if (modeEnum != Mode.ModeEnum.REGULAR) {
            return getCompatibilityModeFunction(str, modeEnum);
        }
        return null;
    }

    private static ModeFunction getCompatibilityModeFunction(String str, Mode.ModeEnum modeEnum) {
        switch (modeEnum) {
            case LEGACY:
                return FunctionsLegacy.getFunction(str);
            case DB2:
            case Derby:
                return FunctionsDB2Derby.getFunction(str);
            case MSSQLServer:
                return FunctionsMSSQLServer.getFunction(str);
            case MariaDB:
            case MySQL:
                return FunctionsMySQL.getFunction(str);
            case Oracle:
                return FunctionsOracle.getFunction(str);
            case PostgreSQL:
                return FunctionsPostgreSQL.getFunction(str);
            default:
                return null;
        }
    }

    public static Expression getCompatibilityDateTimeValueFunction(Database database, String str, int i) {
        boolean z = -1;
        switch (str.hashCode()) {
            case -1019868197:
                if (str.equals("SYSDATE")) {
                    z = false;
                    break;
                }
                break;
            case 79996705:
                if (str.equals("TODAY")) {
                    z = 2;
                    break;
                }
                break;
            case 525052137:
                if (str.equals("SYSTIMESTAMP")) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                switch (database.getMode().getEnum()) {
                    case LEGACY:
                    case Oracle:
                    case HSQLDB:
                        return new CompatibilityDateTimeValueFunction(0, -1);
                    default:
                        return null;
                }
            case true:
                switch (database.getMode().getEnum()) {
                    case LEGACY:
                    case Oracle:
                        return new CompatibilityDateTimeValueFunction(1, i);
                    default:
                        return null;
                }
            case true:
                switch (database.getMode().getEnum()) {
                    case LEGACY:
                    case HSQLDB:
                        return new CurrentDateTimeValueFunction(0, i);
                    default:
                        return null;
                }
            default:
                return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ModeFunction(FunctionInfo functionInfo) {
        super(new Expression[functionInfo.parameterCount != -1 ? functionInfo.parameterCount : 4]);
        this.info = functionInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Value getNullOrValue(SessionLocal sessionLocal, Expression[] expressionArr, Value[] valueArr, int i) {
        if (i >= expressionArr.length) {
            return null;
        }
        Value value = valueArr[i];
        if (value == null) {
            Expression expression = expressionArr[i];
            if (expression == null) {
                return null;
            }
            Value value2 = expression.getValue(sessionLocal);
            valueArr[i] = value2;
            value = value2;
        }
        return value;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Value[] getArgumentsValues(SessionLocal sessionLocal, Expression[] expressionArr) {
        Value[] valueArr = new Value[expressionArr.length];
        if (this.info.nullIfParameterIsNull) {
            int length = expressionArr.length;
            for (int i = 0; i < length; i++) {
                Value value = expressionArr[i].getValue(sessionLocal);
                if (value == ValueNull.INSTANCE) {
                    return null;
                }
                valueArr[i] = value;
            }
        }
        return valueArr;
    }

    void checkParameterCount(int i) {
        throw DbException.getInternalError("type=" + this.info.type);
    }

    @Override // org.h2.expression.OperationN, org.h2.expression.ExpressionWithVariableParameters
    public void doneWithParameters() {
        int i = this.info.parameterCount;
        if (i == -1) {
            checkParameterCount(this.argsCount);
            super.doneWithParameters();
        } else if (i != this.argsCount) {
            throw DbException.get(ErrorCode.INVALID_PARAMETER_COUNT_2, this.info.name, Integer.toString(this.argsCount));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean optimizeArguments(SessionLocal sessionLocal) {
        return optimizeArguments(sessionLocal, this.info.deterministic);
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return this.info.name;
    }

    @Override // org.h2.expression.OperationN, org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        if (!super.isEverything(expressionVisitor)) {
            return false;
        }
        switch (expressionVisitor.getType()) {
            case 2:
            case 5:
            case 8:
                return this.info.deterministic;
            default:
                return true;
        }
    }
}
