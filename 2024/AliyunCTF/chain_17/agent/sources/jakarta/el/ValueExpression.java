package jakarta.el;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/ValueExpression.class */
public abstract class ValueExpression extends Expression {
    private static final long serialVersionUID = 8577809572381654673L;

    public abstract <T> T getValue(ELContext eLContext);

    public abstract void setValue(ELContext eLContext, Object obj);

    public abstract boolean isReadOnly(ELContext eLContext);

    public abstract Class<?> getType(ELContext eLContext);

    public abstract Class<?> getExpectedType();

    public ValueReference getValueReference(ELContext context) {
        context.notifyBeforeEvaluation(getExpressionString());
        context.notifyAfterEvaluation(getExpressionString());
        return null;
    }
}
