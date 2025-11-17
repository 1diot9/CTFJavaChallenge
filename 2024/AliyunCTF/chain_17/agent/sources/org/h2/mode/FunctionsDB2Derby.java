package org.h2.mode;

import java.util.HashMap;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.value.ExtTypeInfoNumeric;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/FunctionsDB2Derby.class */
public final class FunctionsDB2Derby extends ModeFunction {
    private static final int IDENTITY_VAL_LOCAL = 5001;
    private static final HashMap<String, FunctionInfo> FUNCTIONS = new HashMap<>();
    private static final TypeInfo IDENTITY_VAL_LOCAL_TYPE = TypeInfo.getTypeInfo(13, 31, 0, ExtTypeInfoNumeric.DECIMAL);

    static {
        FUNCTIONS.put("IDENTITY_VAL_LOCAL", new FunctionInfo("IDENTITY_VAL_LOCAL", IDENTITY_VAL_LOCAL, 0, 12, true, false));
    }

    public static FunctionsDB2Derby getFunction(String str) {
        FunctionInfo functionInfo = FUNCTIONS.get(str);
        if (functionInfo != null) {
            return new FunctionsDB2Derby(functionInfo);
        }
        return null;
    }

    private FunctionsDB2Derby(FunctionInfo functionInfo) {
        super(functionInfo);
    }

    @Override // org.h2.expression.function.FunctionN, org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        switch (this.info.type) {
            case IDENTITY_VAL_LOCAL /* 5001 */:
                return sessionLocal.getLastIdentity().convertTo(this.type);
            default:
                throw DbException.getInternalError("type=" + this.info.type);
        }
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        switch (this.info.type) {
            case IDENTITY_VAL_LOCAL /* 5001 */:
                this.type = IDENTITY_VAL_LOCAL_TYPE;
                return this;
            default:
                throw DbException.getInternalError("type=" + this.info.type);
        }
    }
}
