package jakarta.el;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/MethodExpression.class */
public abstract class MethodExpression extends Expression {
    private static final long serialVersionUID = 8163925562047324656L;

    public abstract MethodInfo getMethodInfo(ELContext eLContext);

    public abstract Object invoke(ELContext eLContext, Object[] objArr);

    public boolean isParametersProvided() {
        return false;
    }

    public MethodReference getMethodReference(ELContext context) {
        context.notifyBeforeEvaluation(getExpressionString());
        context.notifyAfterEvaluation(getExpressionString());
        return null;
    }
}
