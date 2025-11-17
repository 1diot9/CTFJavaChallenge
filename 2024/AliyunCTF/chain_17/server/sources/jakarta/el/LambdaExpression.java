package jakarta.el;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/LambdaExpression.class */
public class LambdaExpression {
    private final List<String> formalParameters;
    private final ValueExpression expression;
    private final Map<String, Object> nestedArguments = new HashMap();
    private ELContext context = null;

    public LambdaExpression(List<String> formalParameters, ValueExpression expression) {
        this.formalParameters = formalParameters;
        this.expression = expression;
    }

    public void setELContext(ELContext context) {
        this.context = context;
    }

    public Object invoke(ELContext context, Object... args) throws ELException {
        Objects.requireNonNull(context);
        int formalParamCount = 0;
        if (this.formalParameters != null) {
            formalParamCount = this.formalParameters.size();
        }
        int argCount = 0;
        if (args != null) {
            argCount = args.length;
        }
        if (formalParamCount > argCount) {
            throw new ELException(Util.message(context, "lambdaExpression.tooFewArgs", Integer.valueOf(argCount), Integer.valueOf(formalParamCount)));
        }
        Map<String, Object> lambdaArguments = new HashMap<>(this.nestedArguments);
        for (int i = 0; i < formalParamCount; i++) {
            lambdaArguments.put(this.formalParameters.get(i), args[i]);
        }
        context.enterLambdaScope(lambdaArguments);
        try {
            Object result = this.expression.getValue(context);
            if (result instanceof LambdaExpression) {
                ((LambdaExpression) result).nestedArguments.putAll(lambdaArguments);
            }
            return result;
        } finally {
            context.exitLambdaScope();
        }
    }

    public Object invoke(Object... args) {
        return invoke(this.context, args);
    }
}
