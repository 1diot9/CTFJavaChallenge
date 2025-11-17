package org.h2.expression.function;

import cn.hutool.core.text.CharSequenceUtil;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/TrimFunction.class */
public final class TrimFunction extends Function1_2 {
    public static final int LEADING = 1;
    public static final int TRAILING = 2;
    public static final int MULTI_CHARACTER = 4;
    private int flags;

    public TrimFunction(Expression expression, Expression expression2, int i) {
        super(expression, expression2);
        this.flags = i;
    }

    @Override // org.h2.expression.function.Function1_2
    public Value getValue(SessionLocal sessionLocal, Value value, Value value2) {
        return ValueVarchar.get(StringUtils.trim(value.getString(), (this.flags & 1) != 0, (this.flags & 2) != 0, value2 != null ? value2.getString() : CharSequenceUtil.SPACE), sessionLocal);
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.left = this.left.optimize(sessionLocal);
        if (this.right != null) {
            this.right = this.right.optimize(sessionLocal);
        }
        this.type = TypeInfo.getTypeInfo(2, this.left.getType().getPrecision(), 0, null);
        if (this.left.isConstant() && (this.right == null || this.right.isConstant())) {
            return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
        }
        return this;
    }

    @Override // org.h2.expression.function.Function1_2, org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        sb.append(getName()).append('(');
        if ((this.flags & 4) != 0) {
            this.left.getUnenclosedSQL(sb, i);
            if (this.right != null) {
                this.right.getUnenclosedSQL(sb.append(", "), i);
            }
        } else {
            boolean z = false;
            switch (this.flags) {
                case 1:
                    sb.append("LEADING ");
                    z = true;
                    break;
                case 2:
                    sb.append("TRAILING ");
                    z = true;
                    break;
            }
            if (this.right != null) {
                this.right.getUnenclosedSQL(sb, i);
                z = true;
            }
            if (z) {
                sb.append(" FROM ");
            }
            this.left.getUnenclosedSQL(sb, i);
        }
        return sb.append(')');
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        switch (this.flags) {
            case 5:
                return "LTRIM";
            case 6:
                return "RTRIM";
            case 7:
                return "BTRIM";
            default:
                return "TRIM";
        }
    }
}
