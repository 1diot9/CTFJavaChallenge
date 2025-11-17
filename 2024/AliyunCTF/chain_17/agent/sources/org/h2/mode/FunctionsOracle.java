package org.h2.mode;

import java.util.HashMap;
import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ValueExpression;
import org.h2.expression.function.DateTimeFunction;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueUuid;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/FunctionsOracle.class */
public final class FunctionsOracle extends ModeFunction {
    private static final int ADD_MONTHS = 2001;
    private static final int SYS_GUID = 2002;
    private static final int TO_DATE = 2003;
    private static final int TO_TIMESTAMP = 2004;
    private static final int TO_TIMESTAMP_TZ = 2005;
    private static final HashMap<String, FunctionInfo> FUNCTIONS = new HashMap<>();

    static {
        FUNCTIONS.put("ADD_MONTHS", new FunctionInfo("ADD_MONTHS", ADD_MONTHS, 2, 20, true, true));
        FUNCTIONS.put("SYS_GUID", new FunctionInfo("SYS_GUID", SYS_GUID, 0, 6, false, false));
        FUNCTIONS.put("TO_DATE", new FunctionInfo("TO_DATE", TO_DATE, -1, 20, true, true));
        FUNCTIONS.put("TO_TIMESTAMP", new FunctionInfo("TO_TIMESTAMP", TO_TIMESTAMP, -1, 20, true, true));
        FUNCTIONS.put("TO_TIMESTAMP_TZ", new FunctionInfo("TO_TIMESTAMP_TZ", TO_TIMESTAMP_TZ, -1, 21, true, true));
    }

    public static FunctionsOracle getFunction(String str) {
        FunctionInfo functionInfo = FUNCTIONS.get(str);
        if (functionInfo != null) {
            return new FunctionsOracle(functionInfo);
        }
        return null;
    }

    private FunctionsOracle(FunctionInfo functionInfo) {
        super(functionInfo);
    }

    @Override // org.h2.mode.ModeFunction
    protected void checkParameterCount(int i) {
        int i2;
        int i3;
        switch (this.info.type) {
            case TO_DATE /* 2003 */:
                i2 = 1;
                i3 = 3;
                break;
            case TO_TIMESTAMP /* 2004 */:
            case TO_TIMESTAMP_TZ /* 2005 */:
                i2 = 1;
                i3 = 2;
                break;
            default:
                throw DbException.getInternalError("type=" + this.info.type);
        }
        if (i < i2 || i > i3) {
            throw DbException.get(ErrorCode.INVALID_PARAMETER_COUNT_2, this.info.name, i2 + ".." + i3);
        }
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        boolean optimizeArguments = optimizeArguments(sessionLocal);
        switch (this.info.type) {
            case SYS_GUID /* 2002 */:
                this.type = TypeInfo.getTypeInfo(6, 16L, 0, null);
                break;
            default:
                this.type = TypeInfo.getTypeInfo(this.info.returnDataType);
                break;
        }
        if (optimizeArguments) {
            return ValueExpression.get(getValue(sessionLocal));
        }
        return this;
    }

    @Override // org.h2.expression.function.FunctionN, org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value timestampTz;
        Value[] argumentsValues = getArgumentsValues(sessionLocal, this.args);
        if (argumentsValues == null) {
            return ValueNull.INSTANCE;
        }
        Value nullOrValue = getNullOrValue(sessionLocal, this.args, argumentsValues, 0);
        Value nullOrValue2 = getNullOrValue(sessionLocal, this.args, argumentsValues, 1);
        switch (this.info.type) {
            case ADD_MONTHS /* 2001 */:
                timestampTz = DateTimeFunction.dateadd(sessionLocal, 1, nullOrValue2.getInt(), nullOrValue);
                break;
            case SYS_GUID /* 2002 */:
                timestampTz = ValueUuid.getNewRandom().convertTo(TypeInfo.TYPE_VARBINARY);
                break;
            case TO_DATE /* 2003 */:
                timestampTz = ToDateParser.toDate(sessionLocal, nullOrValue.getString(), nullOrValue2 == null ? null : nullOrValue2.getString());
                break;
            case TO_TIMESTAMP /* 2004 */:
                timestampTz = ToDateParser.toTimestamp(sessionLocal, nullOrValue.getString(), nullOrValue2 == null ? null : nullOrValue2.getString());
                break;
            case TO_TIMESTAMP_TZ /* 2005 */:
                timestampTz = ToDateParser.toTimestampTz(sessionLocal, nullOrValue.getString(), nullOrValue2 == null ? null : nullOrValue2.getString());
                break;
            default:
                throw DbException.getInternalError("type=" + this.info.type);
        }
        return timestampTz;
    }
}
