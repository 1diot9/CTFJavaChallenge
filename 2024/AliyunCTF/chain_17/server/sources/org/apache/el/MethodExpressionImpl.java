package org.apache.el;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.FunctionMapper;
import jakarta.el.MethodExpression;
import jakarta.el.MethodInfo;
import jakarta.el.MethodNotFoundException;
import jakarta.el.MethodReference;
import jakarta.el.PropertyNotFoundException;
import jakarta.el.VariableMapper;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.lang.ExpressionBuilder;
import org.apache.el.parser.Node;
import org.apache.el.util.ReflectionUtil;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/MethodExpressionImpl.class */
public final class MethodExpressionImpl extends MethodExpression implements Externalizable {
    private Class<?> expectedType;
    private String expr;
    private FunctionMapper fnMapper;
    private VariableMapper varMapper;
    private transient Node node;
    private Class<?>[] paramTypes;

    public MethodExpressionImpl() {
    }

    public MethodExpressionImpl(String expr, Node node, FunctionMapper fnMapper, VariableMapper varMapper, Class<?> expectedType, Class<?>[] paramTypes) {
        this.expr = expr;
        this.node = node;
        this.fnMapper = fnMapper;
        this.varMapper = varMapper;
        this.expectedType = expectedType;
        this.paramTypes = paramTypes;
    }

    @Override // jakarta.el.Expression
    public boolean equals(Object obj) {
        return (obj instanceof MethodExpressionImpl) && obj.hashCode() == hashCode();
    }

    @Override // jakarta.el.Expression
    public String getExpressionString() {
        return this.expr;
    }

    @Override // jakarta.el.MethodExpression
    public MethodInfo getMethodInfo(ELContext context) throws PropertyNotFoundException, MethodNotFoundException, ELException {
        Node n = getNode();
        EvaluationContext ctx = new EvaluationContext(context, this.fnMapper, this.varMapper);
        ctx.notifyBeforeEvaluation(getExpressionString());
        MethodInfo result = n.getMethodInfo(ctx, this.paramTypes);
        ctx.notifyAfterEvaluation(getExpressionString());
        return result;
    }

    private Node getNode() throws ELException {
        if (this.node == null) {
            this.node = ExpressionBuilder.createNode(this.expr);
        }
        return this.node;
    }

    @Override // jakarta.el.Expression
    public int hashCode() {
        return this.expr.hashCode();
    }

    @Override // jakarta.el.MethodExpression
    public Object invoke(ELContext context, Object[] params) throws PropertyNotFoundException, MethodNotFoundException, ELException {
        EvaluationContext ctx = new EvaluationContext(context, this.fnMapper, this.varMapper);
        ctx.notifyBeforeEvaluation(getExpressionString());
        Object result = getNode().invoke(ctx, this.paramTypes, params);
        ctx.notifyAfterEvaluation(getExpressionString());
        return result;
    }

    @Override // java.io.Externalizable
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.expr = in.readUTF();
        String type = in.readUTF();
        if (!type.isEmpty()) {
            this.expectedType = ReflectionUtil.forName(type);
        }
        this.paramTypes = ReflectionUtil.toTypeArray((String[]) in.readObject());
        this.fnMapper = (FunctionMapper) in.readObject();
        this.varMapper = (VariableMapper) in.readObject();
    }

    @Override // java.io.Externalizable
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(this.expr);
        out.writeUTF(this.expectedType != null ? this.expectedType.getName() : "");
        out.writeObject(ReflectionUtil.toTypeNameArray(this.paramTypes));
        out.writeObject(this.fnMapper);
        out.writeObject(this.varMapper);
    }

    @Override // jakarta.el.Expression
    public boolean isLiteralText() {
        return false;
    }

    @Override // jakarta.el.MethodExpression
    public boolean isParametersProvided() {
        return getNode().isParametersProvided();
    }

    @Override // jakarta.el.MethodExpression
    public MethodReference getMethodReference(ELContext context) {
        EvaluationContext ctx = new EvaluationContext(context, this.fnMapper, this.varMapper);
        ctx.notifyBeforeEvaluation(getExpressionString());
        MethodReference methodReference = getNode().getMethodReference(ctx);
        ctx.notifyAfterEvaluation(getExpressionString());
        return methodReference;
    }
}
