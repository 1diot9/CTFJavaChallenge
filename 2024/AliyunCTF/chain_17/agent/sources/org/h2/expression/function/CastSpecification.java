package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.schema.Domain;
import org.h2.table.Column;
import org.h2.util.DateTimeTemplate;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/CastSpecification.class */
public final class CastSpecification extends Function1_2 {
    private Domain domain;

    public CastSpecification(Expression expression, Column column, Expression expression2) {
        super(expression, expression2);
        this.type = column.getType();
        this.domain = column.getDomain();
    }

    public CastSpecification(Expression expression, Column column) {
        super(expression, null);
        this.type = column.getType();
        this.domain = column.getDomain();
    }

    public CastSpecification(Expression expression, TypeInfo typeInfo) {
        super(expression, null);
        this.type = typeInfo;
    }

    @Override // org.h2.expression.function.Function1_2
    protected Value getValue(SessionLocal sessionLocal, Value value, Value value2) {
        if (value2 != null) {
            value = getValueWithTemplate(value, value2, sessionLocal);
        }
        Value castTo = value.castTo(this.type, sessionLocal);
        if (this.domain != null) {
            this.domain.checkConstraints(sessionLocal, castTo);
        }
        return castTo;
    }

    private Value getValueWithTemplate(Value value, Value value2, SessionLocal sessionLocal) {
        if (value == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        int valueType = value.getValueType();
        if (DataType.isDateTimeType(valueType)) {
            if (DataType.isCharacterStringType(this.type.getValueType())) {
                return ValueVarchar.get(DateTimeTemplate.of(value2.getString()).format(value), sessionLocal);
            }
        } else if (DataType.isCharacterStringType(valueType) && DataType.isDateTimeType(this.type.getValueType())) {
            return DateTimeTemplate.of(value2.getString()).parse(value.getString(), this.type, sessionLocal);
        }
        throw DbException.getUnsupportedException(this.type.getSQL(value.getType().getSQL(new StringBuilder("CAST with template from "), 3).append(" to "), 0).toString());
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        Value value;
        this.left = this.left.optimize(sessionLocal);
        if (this.right != null) {
            this.right = this.right.optimize(sessionLocal);
        }
        if (this.left.isConstant() && ((this.right == null || this.right.isConstant()) && ((value = getValue(sessionLocal)) == ValueNull.INSTANCE || canOptimizeCast(this.left.getType().getValueType(), this.type.getValueType())))) {
            return TypedValueExpression.get(value, this.type);
        }
        return this;
    }

    @Override // org.h2.expression.Expression
    public boolean isConstant() {
        return (this.left instanceof ValueExpression) && (this.right == null || this.right.isConstant()) && canOptimizeCast(this.left.getType().getValueType(), this.type.getValueType());
    }

    private static boolean canOptimizeCast(int i, int i2) {
        switch (i) {
            case 17:
                if (i2 == 21) {
                    return false;
                }
                return true;
            case 18:
                switch (i2) {
                    case 19:
                    case 20:
                    case 21:
                        return false;
                    default:
                        return true;
                }
            case 19:
                switch (i2) {
                    case 18:
                    case 20:
                    case 21:
                        return false;
                    case 19:
                    default:
                        return true;
                }
            case 20:
                switch (i2) {
                    case 19:
                    case 21:
                        return false;
                    default:
                        return true;
                }
            case 21:
                switch (i2) {
                    case 17:
                    case 18:
                    case 20:
                        return false;
                    case 19:
                    default:
                        return true;
                }
            default:
                return true;
        }
    }

    @Override // org.h2.expression.function.Function1_2, org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        sb.append("CAST(");
        this.left.getUnenclosedSQL(sb, this.left instanceof ValueExpression ? i | 4 : i).append(" AS ");
        (this.domain != null ? this.domain : this.type).getSQL(sb, i);
        if (this.right != null) {
            this.right.getSQL(sb.append(" FORMAT "), i);
        }
        return sb.append(')');
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return "CAST";
    }
}
