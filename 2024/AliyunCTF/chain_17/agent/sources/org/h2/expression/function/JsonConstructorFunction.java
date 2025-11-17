package org.h2.expression.function;

import ch.qos.logback.core.joran.JoranConstants;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionWithFlags;
import org.h2.expression.Format;
import org.h2.expression.OperationN;
import org.h2.expression.Subquery;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.util.json.JsonConstructorUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueJson;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/JsonConstructorFunction.class */
public final class JsonConstructorFunction extends OperationN implements ExpressionWithFlags, NamedExpression {
    private final boolean array;
    private int flags;

    public JsonConstructorFunction(boolean z) {
        super(new Expression[4]);
        this.array = z;
    }

    @Override // org.h2.expression.ExpressionWithFlags
    public void setFlags(int i) {
        this.flags = i;
    }

    @Override // org.h2.expression.ExpressionWithFlags
    public int getFlags() {
        return this.flags;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        return this.array ? jsonArray(sessionLocal, this.args) : jsonObject(sessionLocal, this.args);
    }

    private Value jsonObject(SessionLocal sessionLocal, Expression[] expressionArr) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(123);
        int i = 0;
        int length = expressionArr.length;
        while (i < length) {
            int i2 = i;
            int i3 = i + 1;
            String string = expressionArr[i2].getValue(sessionLocal).getString();
            if (string == null) {
                throw DbException.getInvalidValueException("JSON_OBJECT key", JoranConstants.NULL);
            }
            i = i3 + 1;
            Value value = expressionArr[i3].getValue(sessionLocal);
            if (value == ValueNull.INSTANCE) {
                if ((this.flags & 1) == 0) {
                    value = ValueJson.NULL;
                }
            }
            JsonConstructorUtils.jsonObjectAppend(byteArrayOutputStream, string, value);
        }
        return JsonConstructorUtils.jsonObjectFinish(byteArrayOutputStream, this.flags);
    }

    private Value jsonArray(SessionLocal sessionLocal, Expression[] expressionArr) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(91);
        int length = expressionArr.length;
        if (length == 1) {
            Expression expression = expressionArr[0];
            if (expression instanceof Subquery) {
                Iterator<Value> it = ((Subquery) expression).getAllRows(sessionLocal).iterator();
                while (it.hasNext()) {
                    JsonConstructorUtils.jsonArrayAppend(byteArrayOutputStream, it.next(), this.flags);
                }
            } else if (expression instanceof Format) {
                Format format = (Format) expression;
                Expression subexpression = format.getSubexpression(0);
                if (subexpression instanceof Subquery) {
                    Iterator<Value> it2 = ((Subquery) subexpression).getAllRows(sessionLocal).iterator();
                    while (it2.hasNext()) {
                        JsonConstructorUtils.jsonArrayAppend(byteArrayOutputStream, format.getValue(it2.next()), this.flags);
                    }
                }
            }
            byteArrayOutputStream.write(93);
            return ValueJson.getInternal(byteArrayOutputStream.toByteArray());
        }
        int i = 0;
        while (i < length) {
            int i2 = i;
            i++;
            JsonConstructorUtils.jsonArrayAppend(byteArrayOutputStream, expressionArr[i2].getValue(sessionLocal), this.flags);
        }
        byteArrayOutputStream.write(93);
        return ValueJson.getInternal(byteArrayOutputStream.toByteArray());
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        boolean optimizeArguments = optimizeArguments(sessionLocal, true);
        this.type = TypeInfo.TYPE_JSON;
        if (optimizeArguments) {
            return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
        }
        return this;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        sb.append(getName()).append('(');
        if (this.array) {
            writeExpressions(sb, this.args, i);
        } else {
            int i2 = 0;
            int length = this.args.length;
            while (i2 < length) {
                if (i2 > 0) {
                    sb.append(", ");
                }
                int i3 = i2;
                int i4 = i2 + 1;
                this.args[i3].getUnenclosedSQL(sb, i).append(": ");
                i2 = i4 + 1;
                this.args[i4].getUnenclosedSQL(sb, i);
            }
        }
        return getJsonFunctionFlagsSQL(sb, this.flags, this.array).append(')');
    }

    public static StringBuilder getJsonFunctionFlagsSQL(StringBuilder sb, int i, boolean z) {
        if ((i & 1) != 0) {
            if (!z) {
                sb.append(" ABSENT ON NULL");
            }
        } else if (z) {
            sb.append(" NULL ON NULL");
        }
        if (!z && (i & 2) != 0) {
            sb.append(" WITH UNIQUE KEYS");
        }
        return sb;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return this.array ? "JSON_ARRAY" : "JSON_OBJECT";
    }
}
