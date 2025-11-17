package org.springframework.expression.spel.standard;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.TypedValue;
import org.springframework.expression.common.ExpressionUtils;
import org.springframework.expression.spel.CompiledExpression;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/standard/SpelExpression.class */
public class SpelExpression implements Expression {
    private static final int INTERPRETED_COUNT_THRESHOLD = 100;
    private static final int FAILED_ATTEMPTS_THRESHOLD = 100;
    private final String expression;
    private final SpelNodeImpl ast;
    private final SpelParserConfiguration configuration;

    @Nullable
    private EvaluationContext evaluationContext;

    @Nullable
    private volatile CompiledExpression compiledAst;
    private final AtomicInteger interpretedCount = new AtomicInteger();
    private final AtomicInteger failedAttempts = new AtomicInteger();

    public SpelExpression(String expression, SpelNodeImpl ast, SpelParserConfiguration configuration) {
        this.expression = expression;
        this.ast = ast;
        this.configuration = configuration;
    }

    public void setEvaluationContext(EvaluationContext evaluationContext) {
        this.evaluationContext = evaluationContext;
    }

    public EvaluationContext getEvaluationContext() {
        if (this.evaluationContext == null) {
            this.evaluationContext = new StandardEvaluationContext();
        }
        return this.evaluationContext;
    }

    @Override // org.springframework.expression.Expression
    public String getExpressionString() {
        return this.expression;
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public Object getValue() throws EvaluationException {
        CompiledExpression compiledAst = this.compiledAst;
        if (compiledAst != null) {
            try {
                EvaluationContext context = getEvaluationContext();
                return compiledAst.getValue(context.getRootObject().getValue(), context);
            } catch (Throwable ex) {
                if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
                    this.compiledAst = null;
                    this.interpretedCount.set(0);
                } else {
                    throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION, new Object[0]);
                }
            }
        }
        ExpressionState expressionState = new ExpressionState(getEvaluationContext(), this.configuration);
        Object result = this.ast.getValue(expressionState);
        checkCompile(expressionState);
        return result;
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public <T> T getValue(@Nullable Class<T> cls) throws EvaluationException {
        CompiledExpression compiledExpression = this.compiledAst;
        if (compiledExpression != null) {
            try {
                EvaluationContext evaluationContext = getEvaluationContext();
                T t = (T) compiledExpression.getValue(evaluationContext.getRootObject().getValue(), evaluationContext);
                if (cls == null) {
                    return t;
                }
                return (T) ExpressionUtils.convertTypedValue(getEvaluationContext(), new TypedValue(t), cls);
            } catch (Throwable th) {
                if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
                    this.compiledAst = null;
                    this.interpretedCount.set(0);
                } else {
                    throw new SpelEvaluationException(th, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION, new Object[0]);
                }
            }
        }
        ExpressionState expressionState = new ExpressionState(getEvaluationContext(), this.configuration);
        TypedValue typedValue = this.ast.getTypedValue(expressionState);
        checkCompile(expressionState);
        return (T) ExpressionUtils.convertTypedValue(expressionState.getEvaluationContext(), typedValue, cls);
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public Object getValue(@Nullable Object rootObject) throws EvaluationException {
        CompiledExpression compiledAst = this.compiledAst;
        if (compiledAst != null) {
            try {
                return compiledAst.getValue(rootObject, getEvaluationContext());
            } catch (Throwable ex) {
                if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
                    this.compiledAst = null;
                    this.interpretedCount.set(0);
                } else {
                    throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION, new Object[0]);
                }
            }
        }
        ExpressionState expressionState = new ExpressionState(getEvaluationContext(), toTypedValue(rootObject), this.configuration);
        Object result = this.ast.getValue(expressionState);
        checkCompile(expressionState);
        return result;
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public <T> T getValue(@Nullable Object obj, @Nullable Class<T> cls) throws EvaluationException {
        CompiledExpression compiledExpression = this.compiledAst;
        if (compiledExpression != null) {
            try {
                T t = (T) compiledExpression.getValue(obj, getEvaluationContext());
                if (cls == null) {
                    return t;
                }
                return (T) ExpressionUtils.convertTypedValue(getEvaluationContext(), new TypedValue(t), cls);
            } catch (Throwable th) {
                if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
                    this.compiledAst = null;
                    this.interpretedCount.set(0);
                } else {
                    throw new SpelEvaluationException(th, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION, new Object[0]);
                }
            }
        }
        ExpressionState expressionState = new ExpressionState(getEvaluationContext(), toTypedValue(obj), this.configuration);
        TypedValue typedValue = this.ast.getTypedValue(expressionState);
        checkCompile(expressionState);
        return (T) ExpressionUtils.convertTypedValue(expressionState.getEvaluationContext(), typedValue, cls);
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public Object getValue(EvaluationContext context) throws EvaluationException {
        Assert.notNull(context, "EvaluationContext must not be null");
        CompiledExpression compiledAst = this.compiledAst;
        if (compiledAst != null) {
            try {
                return compiledAst.getValue(context.getRootObject().getValue(), context);
            } catch (Throwable ex) {
                if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
                    this.compiledAst = null;
                    this.interpretedCount.set(0);
                } else {
                    throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION, new Object[0]);
                }
            }
        }
        ExpressionState expressionState = new ExpressionState(context, this.configuration);
        Object result = this.ast.getValue(expressionState);
        checkCompile(expressionState);
        return result;
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public <T> T getValue(EvaluationContext evaluationContext, @Nullable Class<T> cls) throws EvaluationException {
        Assert.notNull(evaluationContext, "EvaluationContext must not be null");
        CompiledExpression compiledExpression = this.compiledAst;
        if (compiledExpression != null) {
            try {
                T t = (T) compiledExpression.getValue(evaluationContext.getRootObject().getValue(), evaluationContext);
                if (cls != null) {
                    return (T) ExpressionUtils.convertTypedValue(evaluationContext, new TypedValue(t), cls);
                }
                return t;
            } catch (Throwable th) {
                if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
                    this.compiledAst = null;
                    this.interpretedCount.set(0);
                } else {
                    throw new SpelEvaluationException(th, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION, new Object[0]);
                }
            }
        }
        ExpressionState expressionState = new ExpressionState(evaluationContext, this.configuration);
        TypedValue typedValue = this.ast.getTypedValue(expressionState);
        checkCompile(expressionState);
        return (T) ExpressionUtils.convertTypedValue(evaluationContext, typedValue, cls);
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public Object getValue(EvaluationContext context, @Nullable Object rootObject) throws EvaluationException {
        Assert.notNull(context, "EvaluationContext must not be null");
        CompiledExpression compiledAst = this.compiledAst;
        if (compiledAst != null) {
            try {
                return compiledAst.getValue(rootObject, context);
            } catch (Throwable ex) {
                if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
                    this.compiledAst = null;
                    this.interpretedCount.set(0);
                } else {
                    throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION, new Object[0]);
                }
            }
        }
        ExpressionState expressionState = new ExpressionState(context, toTypedValue(rootObject), this.configuration);
        Object result = this.ast.getValue(expressionState);
        checkCompile(expressionState);
        return result;
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public <T> T getValue(EvaluationContext evaluationContext, @Nullable Object obj, @Nullable Class<T> cls) throws EvaluationException {
        Assert.notNull(evaluationContext, "EvaluationContext must not be null");
        CompiledExpression compiledExpression = this.compiledAst;
        if (compiledExpression != null) {
            try {
                T t = (T) compiledExpression.getValue(obj, evaluationContext);
                if (cls != null) {
                    return (T) ExpressionUtils.convertTypedValue(evaluationContext, new TypedValue(t), cls);
                }
                return t;
            } catch (Throwable th) {
                if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
                    this.compiledAst = null;
                    this.interpretedCount.set(0);
                } else {
                    throw new SpelEvaluationException(th, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION, new Object[0]);
                }
            }
        }
        ExpressionState expressionState = new ExpressionState(evaluationContext, toTypedValue(obj), this.configuration);
        TypedValue typedValue = this.ast.getTypedValue(expressionState);
        checkCompile(expressionState);
        return (T) ExpressionUtils.convertTypedValue(evaluationContext, typedValue, cls);
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public Class<?> getValueType() throws EvaluationException {
        return getValueType(getEvaluationContext());
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public Class<?> getValueType(@Nullable Object rootObject) throws EvaluationException {
        return getValueType(getEvaluationContext(), rootObject);
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public Class<?> getValueType(EvaluationContext context) throws EvaluationException {
        Assert.notNull(context, "EvaluationContext must not be null");
        ExpressionState expressionState = new ExpressionState(context, this.configuration);
        TypeDescriptor typeDescriptor = this.ast.getValueInternal(expressionState).getTypeDescriptor();
        if (typeDescriptor != null) {
            return typeDescriptor.getType();
        }
        return null;
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public Class<?> getValueType(EvaluationContext context, @Nullable Object rootObject) throws EvaluationException {
        ExpressionState expressionState = new ExpressionState(context, toTypedValue(rootObject), this.configuration);
        TypeDescriptor typeDescriptor = this.ast.getValueInternal(expressionState).getTypeDescriptor();
        if (typeDescriptor != null) {
            return typeDescriptor.getType();
        }
        return null;
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public TypeDescriptor getValueTypeDescriptor() throws EvaluationException {
        return getValueTypeDescriptor(getEvaluationContext());
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public TypeDescriptor getValueTypeDescriptor(@Nullable Object rootObject) throws EvaluationException {
        ExpressionState expressionState = new ExpressionState(getEvaluationContext(), toTypedValue(rootObject), this.configuration);
        return this.ast.getValueInternal(expressionState).getTypeDescriptor();
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public TypeDescriptor getValueTypeDescriptor(EvaluationContext context) throws EvaluationException {
        Assert.notNull(context, "EvaluationContext must not be null");
        ExpressionState expressionState = new ExpressionState(context, this.configuration);
        return this.ast.getValueInternal(expressionState).getTypeDescriptor();
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public TypeDescriptor getValueTypeDescriptor(EvaluationContext context, @Nullable Object rootObject) throws EvaluationException {
        Assert.notNull(context, "EvaluationContext must not be null");
        ExpressionState expressionState = new ExpressionState(context, toTypedValue(rootObject), this.configuration);
        return this.ast.getValueInternal(expressionState).getTypeDescriptor();
    }

    @Override // org.springframework.expression.Expression
    public boolean isWritable(@Nullable Object rootObject) throws EvaluationException {
        return this.ast.isWritable(new ExpressionState(getEvaluationContext(), toTypedValue(rootObject), this.configuration));
    }

    @Override // org.springframework.expression.Expression
    public boolean isWritable(EvaluationContext context) throws EvaluationException {
        Assert.notNull(context, "EvaluationContext must not be null");
        return this.ast.isWritable(new ExpressionState(context, this.configuration));
    }

    @Override // org.springframework.expression.Expression
    public boolean isWritable(EvaluationContext context, @Nullable Object rootObject) throws EvaluationException {
        Assert.notNull(context, "EvaluationContext must not be null");
        return this.ast.isWritable(new ExpressionState(context, toTypedValue(rootObject), this.configuration));
    }

    @Override // org.springframework.expression.Expression
    public void setValue(@Nullable Object rootObject, @Nullable Object value) throws EvaluationException {
        this.ast.setValue(new ExpressionState(getEvaluationContext(), toTypedValue(rootObject), this.configuration), value);
    }

    @Override // org.springframework.expression.Expression
    public void setValue(EvaluationContext context, @Nullable Object value) throws EvaluationException {
        Assert.notNull(context, "EvaluationContext must not be null");
        this.ast.setValue(new ExpressionState(context, this.configuration), value);
    }

    @Override // org.springframework.expression.Expression
    public void setValue(EvaluationContext context, @Nullable Object rootObject, @Nullable Object value) throws EvaluationException {
        Assert.notNull(context, "EvaluationContext must not be null");
        this.ast.setValue(new ExpressionState(context, toTypedValue(rootObject), this.configuration), value);
    }

    private void checkCompile(ExpressionState expressionState) {
        this.interpretedCount.incrementAndGet();
        SpelCompilerMode compilerMode = expressionState.getConfiguration().getCompilerMode();
        if (compilerMode != SpelCompilerMode.OFF) {
            if (compilerMode == SpelCompilerMode.IMMEDIATE) {
                if (this.interpretedCount.get() > 1) {
                    compileExpression();
                }
            } else if (this.interpretedCount.get() > 100) {
                compileExpression();
            }
        }
    }

    public boolean compileExpression() {
        if (this.compiledAst != null) {
            return true;
        }
        if (this.failedAttempts.get() > 100) {
            return false;
        }
        synchronized (this) {
            if (this.compiledAst != null) {
                return true;
            }
            try {
                SpelCompiler compiler = SpelCompiler.getCompiler(this.configuration.getCompilerClassLoader());
                CompiledExpression compiledAst = compiler.compile(this.ast);
                if (compiledAst != null) {
                    this.compiledAst = compiledAst;
                    return true;
                }
                this.failedAttempts.incrementAndGet();
                return false;
            } catch (Exception ex) {
                this.failedAttempts.incrementAndGet();
                if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
                    this.compiledAst = null;
                    this.interpretedCount.set(0);
                    return false;
                }
                throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_COMPILING_EXPRESSION, new Object[0]);
            }
        }
    }

    public void revertToInterpreted() {
        this.compiledAst = null;
        this.interpretedCount.set(0);
        this.failedAttempts.set(0);
    }

    public SpelNode getAST() {
        return this.ast;
    }

    public String toStringAST() {
        return this.ast.toStringAST();
    }

    private TypedValue toTypedValue(@Nullable Object object) {
        return object != null ? new TypedValue(object) : TypedValue.NULL;
    }
}
