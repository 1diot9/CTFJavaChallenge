package org.h2.mode;

import java.util.HashMap;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/FunctionsLegacy.class */
public class FunctionsLegacy extends ModeFunction {
    private static final HashMap<String, FunctionInfo> FUNCTIONS = new HashMap<>();
    private static final int IDENTITY = 6001;
    private static final int SCOPE_IDENTITY = 6002;

    static {
        FUNCTIONS.put("IDENTITY", new FunctionInfo("IDENTITY", IDENTITY, 0, 12, true, false));
        FUNCTIONS.put("SCOPE_IDENTITY", new FunctionInfo("SCOPE_IDENTITY", SCOPE_IDENTITY, 0, 12, true, false));
    }

    public static FunctionsLegacy getFunction(String str) {
        FunctionInfo functionInfo = FUNCTIONS.get(str);
        if (functionInfo != null) {
            return new FunctionsLegacy(functionInfo);
        }
        return null;
    }

    private FunctionsLegacy(FunctionInfo functionInfo) {
        super(functionInfo);
    }

    @Override // org.h2.expression.function.FunctionN, org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        switch (this.info.type) {
            case IDENTITY /* 6001 */:
            case SCOPE_IDENTITY /* 6002 */:
                return sessionLocal.getLastIdentity().convertTo(this.type);
            default:
                throw DbException.getInternalError("type=" + this.info.type);
        }
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.type = TypeInfo.getTypeInfo(this.info.returnDataType);
        return this;
    }
}
