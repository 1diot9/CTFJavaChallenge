package org.h2.expression.function;

import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/XMLFunction.class */
public final class XMLFunction extends FunctionN {
    public static final int XMLATTR = 0;
    public static final int XMLCDATA = 1;
    public static final int XMLCOMMENT = 2;
    public static final int XMLNODE = 3;
    public static final int XMLSTARTDOC = 4;
    public static final int XMLTEXT = 5;
    private static final String[] NAMES = {"XMLATTR", "XMLCDATA", "XMLCOMMENT", "XMLNODE", "XMLSTARTDOC", "XMLTEXT"};
    private final int function;

    public XMLFunction(int i) {
        super(new Expression[4]);
        this.function = i;
    }

    @Override // org.h2.expression.function.FunctionN, org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        switch (this.function) {
            case 3:
                return xmlNode(sessionLocal);
            case 4:
                return ValueVarchar.get(StringUtils.xmlStartDoc(), sessionLocal);
            default:
                return super.getValue(sessionLocal);
        }
    }

    private Value xmlNode(SessionLocal sessionLocal) {
        boolean z;
        Value value = this.args[0].getValue(sessionLocal);
        if (value == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        int length = this.args.length;
        String string = length >= 2 ? this.args[1].getValue(sessionLocal).getString() : null;
        String string2 = length >= 3 ? this.args[2].getValue(sessionLocal).getString() : null;
        if (length >= 4) {
            Value value2 = this.args[3].getValue(sessionLocal);
            if (value2 == ValueNull.INSTANCE) {
                return ValueNull.INSTANCE;
            }
            z = value2.getBoolean();
        } else {
            z = true;
        }
        return ValueVarchar.get(StringUtils.xmlNode(value.getString(), string, string2, z), sessionLocal);
    }

    @Override // org.h2.expression.function.FunctionN
    protected Value getValue(SessionLocal sessionLocal, Value value, Value value2, Value value3) {
        Value value4;
        switch (this.function) {
            case 0:
                value4 = ValueVarchar.get(StringUtils.xmlAttr(value.getString(), value2.getString()), sessionLocal);
                break;
            case 1:
                value4 = ValueVarchar.get(StringUtils.xmlCData(value.getString()), sessionLocal);
                break;
            case 2:
                value4 = ValueVarchar.get(StringUtils.xmlComment(value.getString()), sessionLocal);
                break;
            case 3:
            case 4:
            default:
                throw DbException.getInternalError("function=" + this.function);
            case 5:
                value4 = ValueVarchar.get(StringUtils.xmlText(value.getString(), value2 != null && value2.getBoolean()), sessionLocal);
                break;
        }
        return value4;
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        int i;
        int i2;
        boolean optimizeArguments = optimizeArguments(sessionLocal, true);
        switch (this.function) {
            case 0:
                i = 2;
                i2 = 2;
                break;
            case 1:
            case 2:
                i = 1;
                i2 = 1;
                break;
            case 3:
                i = 1;
                i2 = 4;
                break;
            case 4:
                i = 0;
                i2 = 0;
                break;
            case 5:
                i = 1;
                i2 = 2;
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        int length = this.args.length;
        if (length < i || length > i2) {
            throw DbException.get(ErrorCode.INVALID_PARAMETER_COUNT_2, getName(), i + ".." + i2);
        }
        this.type = TypeInfo.TYPE_VARCHAR;
        if (optimizeArguments) {
            return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
        }
        return this;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return NAMES[this.function];
    }
}
