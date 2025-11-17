package org.apache.el.lang;

import jakarta.el.ELContext;
import jakarta.el.ELResolver;
import jakarta.el.EvaluationListener;
import jakarta.el.FunctionMapper;
import jakarta.el.ImportHandler;
import jakarta.el.VariableMapper;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.el.util.MessageFactory;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/lang/EvaluationContext.class */
public final class EvaluationContext extends ELContext {
    private final ELContext elContext;
    private final FunctionMapper fnMapper;
    private final VariableMapper varMapper;
    private LambdaExpressionNestedState lambdaExpressionNestedState;

    public EvaluationContext(ELContext elContext, FunctionMapper fnMapper, VariableMapper varMapper) {
        this.elContext = elContext;
        this.fnMapper = fnMapper;
        this.varMapper = varMapper;
    }

    public ELContext getELContext() {
        return this.elContext;
    }

    @Override // jakarta.el.ELContext
    public FunctionMapper getFunctionMapper() {
        return this.fnMapper;
    }

    @Override // jakarta.el.ELContext
    public VariableMapper getVariableMapper() {
        return this.varMapper;
    }

    @Override // jakarta.el.ELContext
    public Object getContext(Class<?> key) {
        return this.elContext.getContext(key);
    }

    @Override // jakarta.el.ELContext
    public ELResolver getELResolver() {
        return this.elContext.getELResolver();
    }

    @Override // jakarta.el.ELContext
    public boolean isPropertyResolved() {
        return this.elContext.isPropertyResolved();
    }

    @Override // jakarta.el.ELContext
    public void putContext(Class<?> key, Object contextObject) {
        this.elContext.putContext(key, contextObject);
    }

    @Override // jakarta.el.ELContext
    public void setPropertyResolved(boolean resolved) {
        this.elContext.setPropertyResolved(resolved);
    }

    @Override // jakarta.el.ELContext
    public Locale getLocale() {
        return this.elContext.getLocale();
    }

    @Override // jakarta.el.ELContext
    public void setLocale(Locale locale) {
        this.elContext.setLocale(locale);
    }

    @Override // jakarta.el.ELContext
    public void setPropertyResolved(Object base, Object property) {
        this.elContext.setPropertyResolved(base, property);
    }

    @Override // jakarta.el.ELContext
    public ImportHandler getImportHandler() {
        return this.elContext.getImportHandler();
    }

    @Override // jakarta.el.ELContext
    public void addEvaluationListener(EvaluationListener listener) {
        this.elContext.addEvaluationListener(listener);
    }

    @Override // jakarta.el.ELContext
    public List<EvaluationListener> getEvaluationListeners() {
        return this.elContext.getEvaluationListeners();
    }

    @Override // jakarta.el.ELContext
    public void notifyBeforeEvaluation(String expression) {
        this.elContext.notifyBeforeEvaluation(expression);
    }

    @Override // jakarta.el.ELContext
    public void notifyAfterEvaluation(String expression) {
        this.elContext.notifyAfterEvaluation(expression);
    }

    @Override // jakarta.el.ELContext
    public void notifyPropertyResolved(Object base, Object property) {
        this.elContext.notifyPropertyResolved(base, property);
    }

    @Override // jakarta.el.ELContext
    public boolean isLambdaArgument(String name) {
        return this.elContext.isLambdaArgument(name);
    }

    @Override // jakarta.el.ELContext
    public Object getLambdaArgument(String name) {
        return this.elContext.getLambdaArgument(name);
    }

    @Override // jakarta.el.ELContext
    public void enterLambdaScope(Map<String, Object> arguments) {
        this.elContext.enterLambdaScope(arguments);
    }

    @Override // jakarta.el.ELContext
    public void exitLambdaScope() {
        this.elContext.exitLambdaScope();
    }

    @Override // jakarta.el.ELContext
    public <T> T convertToType(Object obj, Class<T> cls) {
        return (T) this.elContext.convertToType(obj, cls);
    }

    public LambdaExpressionNestedState getLambdaExpressionNestedState() {
        if (this.lambdaExpressionNestedState != null) {
            return this.lambdaExpressionNestedState;
        }
        if (this.elContext instanceof EvaluationContext) {
            return ((EvaluationContext) this.elContext).getLambdaExpressionNestedState();
        }
        return null;
    }

    public void setLambdaExpressionNestedState(LambdaExpressionNestedState lambdaExpressionNestedState) {
        if (this.lambdaExpressionNestedState != null) {
            throw new IllegalStateException(MessageFactory.get("error.lambda.wrongNestedState"));
        }
        this.lambdaExpressionNestedState = lambdaExpressionNestedState;
    }
}
