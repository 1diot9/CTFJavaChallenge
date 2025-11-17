package org.h2.mode;

import java.util.HashMap;
import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.expression.function.CoalesceFunction;
import org.h2.expression.function.CurrentDateTimeValueFunction;
import org.h2.expression.function.RandFunction;
import org.h2.expression.function.StringFunction;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/FunctionsMSSQLServer.class */
public final class FunctionsMSSQLServer extends ModeFunction {
    private static final int CHARINDEX = 4001;
    private static final int GETDATE = 4002;
    private static final int ISNULL = 4003;
    private static final int LEN = 4004;
    private static final int NEWID = 4005;
    private static final int NEWSEQUENTIALID = 4006;
    private static final int SCOPE_IDENTITY = 4007;
    private static final HashMap<String, FunctionInfo> FUNCTIONS = new HashMap<>();
    private static final TypeInfo SCOPE_IDENTITY_TYPE = TypeInfo.getTypeInfo(13, 38, 0, null);

    static {
        FUNCTIONS.put("CHARINDEX", new FunctionInfo("CHARINDEX", CHARINDEX, -1, 11, true, true));
        FUNCTIONS.put("GETDATE", new FunctionInfo("GETDATE", GETDATE, 0, 20, false, true));
        FUNCTIONS.put("LEN", new FunctionInfo("LEN", LEN, 1, 11, true, true));
        FUNCTIONS.put("NEWID", new FunctionInfo("NEWID", NEWID, 0, 39, true, false));
        FUNCTIONS.put("NEWSEQUENTIALID", new FunctionInfo("NEWSEQUENTIALID", NEWSEQUENTIALID, 0, 39, true, false));
        FUNCTIONS.put("ISNULL", new FunctionInfo("ISNULL", ISNULL, 2, 0, false, true));
        FUNCTIONS.put("SCOPE_IDENTITY", new FunctionInfo("SCOPE_IDENTITY", SCOPE_IDENTITY, 0, 13, true, false));
    }

    public static FunctionsMSSQLServer getFunction(String str) {
        FunctionInfo functionInfo = FUNCTIONS.get(str);
        if (functionInfo != null) {
            return new FunctionsMSSQLServer(functionInfo);
        }
        return null;
    }

    private FunctionsMSSQLServer(FunctionInfo functionInfo) {
        super(functionInfo);
    }

    @Override // org.h2.mode.ModeFunction
    protected void checkParameterCount(int i) {
        switch (this.info.type) {
            case CHARINDEX /* 4001 */:
                if (i < 2 || i > 3) {
                    throw DbException.get(ErrorCode.INVALID_PARAMETER_COUNT_2, this.info.name, "2..3");
                }
                return;
            default:
                throw DbException.getInternalError("type=" + this.info.type);
        }
    }

    @Override // org.h2.expression.function.FunctionN, org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        long charLength;
        Value[] argumentsValues = getArgumentsValues(sessionLocal, this.args);
        if (argumentsValues == null) {
            return ValueNull.INSTANCE;
        }
        Value nullOrValue = getNullOrValue(sessionLocal, this.args, argumentsValues, 0);
        switch (this.info.type) {
            case LEN /* 4004 */:
                if (nullOrValue.getValueType() == 1) {
                    String string = nullOrValue.getString();
                    int length = string.length();
                    while (length > 0 && string.charAt(length - 1) == ' ') {
                        length--;
                    }
                    charLength = length;
                } else {
                    charLength = nullOrValue.charLength();
                }
                return ValueBigint.get(charLength);
            case SCOPE_IDENTITY /* 4007 */:
                return sessionLocal.getLastIdentity().convertTo(this.type);
            default:
                throw DbException.getInternalError("type=" + this.info.type);
        }
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        switch (this.info.type) {
            case CHARINDEX /* 4001 */:
                return new StringFunction(this.args, 0).optimize(sessionLocal);
            case GETDATE /* 4002 */:
                return new CurrentDateTimeValueFunction(4, 3).optimize(sessionLocal);
            case ISNULL /* 4003 */:
                return new CoalesceFunction(0, this.args).optimize(sessionLocal);
            case LEN /* 4004 */:
            default:
                this.type = TypeInfo.getTypeInfo(this.info.returnDataType);
                if (optimizeArguments(sessionLocal)) {
                    return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
                }
                break;
            case NEWID /* 4005 */:
            case NEWSEQUENTIALID /* 4006 */:
                return new RandFunction(null, 2).optimize(sessionLocal);
            case SCOPE_IDENTITY /* 4007 */:
                this.type = SCOPE_IDENTITY_TYPE;
                break;
        }
        return this;
    }
}
