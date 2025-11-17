package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueJson;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/Format.class */
public final class Format extends Operation1 {
    private final FormatEnum format;

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/Format$FormatEnum.class */
    public enum FormatEnum {
        JSON
    }

    public Format(Expression expression, FormatEnum formatEnum) {
        super(expression);
        this.format = formatEnum;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        return getValue(this.arg.getValue(sessionLocal));
    }

    public Value getValue(Value value) {
        switch (value.getValueType()) {
            case 0:
                return ValueJson.NULL;
            case 1:
            case 2:
            case 3:
            case 4:
                return ValueJson.fromJson(value.getString());
            default:
                return value.convertTo(TypeInfo.TYPE_JSON);
        }
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.arg = this.arg.optimize(sessionLocal);
        if (this.arg.isConstant()) {
            return ValueExpression.get(getValue(sessionLocal));
        }
        if ((this.arg instanceof Format) && this.format == ((Format) this.arg).format) {
            return this.arg;
        }
        this.type = TypeInfo.TYPE_JSON;
        return this;
    }

    @Override // org.h2.expression.Expression
    public boolean isIdentity() {
        return this.arg.isIdentity();
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        return this.arg.getSQL(sb, i, 0).append(" FORMAT ").append(this.format.name());
    }

    @Override // org.h2.expression.Expression
    public int getNullable() {
        return this.arg.getNullable();
    }

    @Override // org.h2.expression.Expression
    public String getTableName() {
        return this.arg.getTableName();
    }

    @Override // org.h2.expression.Expression
    public String getColumnName(SessionLocal sessionLocal, int i) {
        return this.arg.getColumnName(sessionLocal, i);
    }
}
