package org.apache.el;

import jakarta.el.ELContext;
import jakarta.el.PropertyNotWritableException;
import jakarta.el.ValueExpression;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.apache.el.util.MessageFactory;
import org.apache.el.util.ReflectionUtil;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/ValueExpressionLiteral.class */
public final class ValueExpressionLiteral extends ValueExpression implements Externalizable {
    private static final long serialVersionUID = 1;
    private Object value;
    private String valueString;
    private Class<?> expectedType;

    public ValueExpressionLiteral() {
    }

    public ValueExpressionLiteral(Object value, Class<?> expectedType) {
        this.value = value;
        this.expectedType = expectedType;
    }

    @Override // jakarta.el.ValueExpression
    public <T> T getValue(ELContext eLContext) {
        Object obj;
        eLContext.notifyBeforeEvaluation(getExpressionString());
        if (this.expectedType != null) {
            obj = eLContext.convertToType(this.value, this.expectedType);
        } else {
            obj = this.value;
        }
        eLContext.notifyAfterEvaluation(getExpressionString());
        return (T) obj;
    }

    @Override // jakarta.el.ValueExpression
    public void setValue(ELContext context, Object value) {
        context.notifyBeforeEvaluation(getExpressionString());
        throw new PropertyNotWritableException(MessageFactory.get("error.value.literal.write", this.value));
    }

    @Override // jakarta.el.ValueExpression
    public boolean isReadOnly(ELContext context) {
        context.notifyBeforeEvaluation(getExpressionString());
        context.notifyAfterEvaluation(getExpressionString());
        return true;
    }

    @Override // jakarta.el.ValueExpression
    public Class<?> getType(ELContext context) {
        context.notifyBeforeEvaluation(getExpressionString());
        Class<?> result = this.value != null ? this.value.getClass() : null;
        context.notifyAfterEvaluation(getExpressionString());
        return result;
    }

    @Override // jakarta.el.ValueExpression
    public Class<?> getExpectedType() {
        return this.expectedType;
    }

    @Override // jakarta.el.Expression
    public String getExpressionString() {
        if (this.valueString == null) {
            this.valueString = this.value != null ? this.value.toString() : null;
        }
        return this.valueString;
    }

    @Override // jakarta.el.Expression
    public boolean equals(Object obj) {
        return (obj instanceof ValueExpressionLiteral) && equals((ValueExpressionLiteral) obj);
    }

    public boolean equals(ValueExpressionLiteral ve) {
        return (ve == null || this.value == null || ve.value == null || (this.value != ve.value && !this.value.equals(ve.value))) ? false : true;
    }

    @Override // jakarta.el.Expression
    public int hashCode() {
        if (this.value != null) {
            return this.value.hashCode();
        }
        return 0;
    }

    @Override // jakarta.el.Expression
    public boolean isLiteralText() {
        return true;
    }

    @Override // java.io.Externalizable
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.value);
        out.writeUTF(this.expectedType != null ? this.expectedType.getName() : "");
    }

    @Override // java.io.Externalizable
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.value = in.readObject();
        String type = in.readUTF();
        if (!type.isEmpty()) {
            this.expectedType = ReflectionUtil.forName(type);
        }
    }
}
