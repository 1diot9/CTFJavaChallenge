package org.h2.expression;

import java.util.Objects;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/TypedValueExpression.class */
public class TypedValueExpression extends ValueExpression {
    public static final TypedValueExpression UNKNOWN = new TypedValueExpression(ValueNull.INSTANCE, TypeInfo.TYPE_BOOLEAN);
    private final TypeInfo type;

    public static ValueExpression get(Value value, TypeInfo typeInfo) {
        return getImpl(value, typeInfo, true);
    }

    public static ValueExpression getTypedIfNull(Value value, TypeInfo typeInfo) {
        return getImpl(value, typeInfo, false);
    }

    private static ValueExpression getImpl(Value value, TypeInfo typeInfo, boolean z) {
        if (value == ValueNull.INSTANCE) {
            switch (typeInfo.getValueType()) {
                case 0:
                    return ValueExpression.NULL;
                case 8:
                    return UNKNOWN;
                default:
                    return new TypedValueExpression(value, typeInfo);
            }
        }
        if (z) {
            DataType dataType = DataType.getDataType(typeInfo.getValueType());
            TypeInfo type = value.getType();
            if ((dataType.supportsPrecision && typeInfo.getPrecision() != type.getPrecision()) || ((dataType.supportsScale && typeInfo.getScale() != type.getScale()) || !Objects.equals(typeInfo.getExtTypeInfo(), type.getExtTypeInfo()))) {
                return new TypedValueExpression(value, typeInfo);
            }
        }
        return ValueExpression.get(value);
    }

    private TypedValueExpression(Value value, TypeInfo typeInfo) {
        super(value);
        this.type = typeInfo;
    }

    @Override // org.h2.expression.ValueExpression, org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        return this.type;
    }

    @Override // org.h2.expression.ValueExpression, org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        if (this == UNKNOWN) {
            sb.append("UNKNOWN");
        } else {
            this.value.getSQL(sb.append("CAST("), i | 4).append(" AS ");
            this.type.getSQL(sb, i).append(')');
        }
        return sb;
    }

    @Override // org.h2.expression.ValueExpression, org.h2.expression.Expression
    public boolean isNullConstant() {
        return this.value == ValueNull.INSTANCE;
    }
}
