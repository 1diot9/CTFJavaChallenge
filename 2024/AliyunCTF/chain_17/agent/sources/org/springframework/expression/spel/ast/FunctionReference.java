package org.springframework.expression.spel.ast;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.StringJoiner;
import org.springframework.asm.MethodVisitor;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.support.ReflectionHelper;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/FunctionReference.class */
public class FunctionReference extends SpelNodeImpl {
    private final String name;

    @Nullable
    private volatile Method method;

    public FunctionReference(String functionName, int startPos, int endPos, SpelNodeImpl... arguments) {
        super(startPos, endPos, arguments);
        this.name = functionName;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        TypedValue value = state.lookupVariable(this.name);
        if (value == TypedValue.NULL) {
            throw new SpelEvaluationException(getStartPosition(), SpelMessage.FUNCTION_NOT_DEFINED, this.name);
        }
        Object resolvedValue = value.getValue();
        if (resolvedValue instanceof MethodHandle) {
            MethodHandle methodHandle = (MethodHandle) resolvedValue;
            try {
                return executeFunctionBoundMethodHandle(state, methodHandle);
            } catch (SpelEvaluationException ex) {
                ex.setPosition(getStartPosition());
                throw ex;
            }
        }
        if (!(resolvedValue instanceof Method)) {
            throw new SpelEvaluationException(SpelMessage.FUNCTION_REFERENCE_CANNOT_BE_INVOKED, this.name, value.getClass());
        }
        Method function = (Method) resolvedValue;
        try {
            return executeFunctionJLRMethod(state, function);
        } catch (SpelEvaluationException ex2) {
            ex2.setPosition(getStartPosition());
            throw ex2;
        }
    }

    private TypedValue executeFunctionJLRMethod(ExpressionState state, Method method) throws EvaluationException {
        int declaredParamCount;
        Object[] functionArgs = getArguments(state);
        if (!method.isVarArgs() && (declaredParamCount = method.getParameterCount()) != functionArgs.length) {
            throw new SpelEvaluationException(SpelMessage.INCORRECT_NUMBER_OF_ARGUMENTS_TO_FUNCTION, Integer.valueOf(functionArgs.length), Integer.valueOf(declaredParamCount));
        }
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new SpelEvaluationException(getStartPosition(), SpelMessage.FUNCTION_MUST_BE_STATIC, ClassUtils.getQualifiedMethodName(method), this.name);
        }
        TypeConverter converter = state.getEvaluationContext().getTypeConverter();
        boolean argumentConversionOccurred = ReflectionHelper.convertAllArguments(converter, functionArgs, method);
        if (method.isVarArgs()) {
            functionArgs = ReflectionHelper.setupArgumentsForVarargsInvocation(method.getParameterTypes(), functionArgs);
        }
        boolean compilable = false;
        try {
            try {
                ReflectionUtils.makeAccessible(method);
                Object result = method.invoke(method.getClass(), functionArgs);
                compilable = !argumentConversionOccurred;
                TypedValue typedValue = new TypedValue(result, new TypeDescriptor(new MethodParameter(method, -1)).narrow(result));
                if (compilable) {
                    this.exitTypeDescriptor = CodeFlow.toDescriptor(method.getReturnType());
                    this.method = method;
                } else {
                    this.exitTypeDescriptor = null;
                    this.method = null;
                }
                return typedValue;
            } catch (Exception ex) {
                throw new SpelEvaluationException(getStartPosition(), ex, SpelMessage.EXCEPTION_DURING_FUNCTION_CALL, this.name, ex.getMessage());
            }
        } catch (Throwable th) {
            if (compilable) {
                this.exitTypeDescriptor = CodeFlow.toDescriptor(method.getReturnType());
                this.method = method;
            } else {
                this.exitTypeDescriptor = null;
                this.method = null;
            }
            throw th;
        }
    }

    private TypedValue executeFunctionBoundMethodHandle(ExpressionState state, MethodHandle methodHandle) throws EvaluationException {
        SpelEvaluationException spelEvaluationException;
        Object[] functionArgs = getArguments(state);
        MethodType declaredParams = methodHandle.type();
        int spelParamCount = functionArgs.length;
        int declaredParamCount = declaredParams.parameterCount();
        boolean isSuspectedVarargs = declaredParams.lastParameterType().isArray();
        if (spelParamCount < declaredParamCount || (spelParamCount > declaredParamCount && !isSuspectedVarargs)) {
            throw new SpelEvaluationException(SpelMessage.INCORRECT_NUMBER_OF_ARGUMENTS_TO_FUNCTION, Integer.valueOf(functionArgs.length), Integer.valueOf(declaredParamCount));
        }
        try {
            if (declaredParamCount == 0) {
                try {
                    TypedValue typedValue = new TypedValue((Object) methodHandle.invoke());
                    this.exitTypeDescriptor = null;
                    this.method = null;
                    return typedValue;
                } catch (Throwable ex) {
                    throw new SpelEvaluationException(getStartPosition(), ex, SpelMessage.EXCEPTION_DURING_FUNCTION_CALL, this.name, ex.getMessage());
                }
            }
            Integer varArgPosition = null;
            if (isSuspectedVarargs) {
                varArgPosition = Integer.valueOf(declaredParamCount - 1);
            }
            TypeConverter converter = state.getEvaluationContext().getTypeConverter();
            ReflectionHelper.convertAllMethodHandleArguments(converter, functionArgs, methodHandle, varArgPosition);
            if (isSuspectedVarargs && declaredParamCount == 1) {
                functionArgs = ReflectionHelper.setupArgumentsForVarargsInvocation(methodHandle.type().parameterArray(), functionArgs);
            }
            try {
                try {
                    TypedValue typedValue2 = new TypedValue(methodHandle.invokeWithArguments(functionArgs));
                    this.exitTypeDescriptor = null;
                    this.method = null;
                    return typedValue2;
                } finally {
                }
            } finally {
            }
        } finally {
        }
    }

    @Override // org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        StringJoiner sj = new StringJoiner(",", "(", ")");
        for (int i = 0; i < getChildCount(); i++) {
            sj.add(getChild(i).toStringAST());
        }
        return "#" + this.name + sj;
    }

    private Object[] getArguments(ExpressionState state) throws EvaluationException {
        Object[] arguments = new Object[getChildCount()];
        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = this.children[i].getValueInternal(state).getValue();
        }
        return arguments;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        Method method = this.method;
        if (method == null) {
            return false;
        }
        int methodModifiers = method.getModifiers();
        if (!Modifier.isStatic(methodModifiers) || !Modifier.isPublic(methodModifiers) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
            return false;
        }
        for (SpelNodeImpl child : this.children) {
            if (!child.isCompilable()) {
                return false;
            }
        }
        return true;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        Method method = this.method;
        Assert.state(method != null, "No method handle");
        String classDesc = method.getDeclaringClass().getName().replace('.', '/');
        generateCodeForArguments(mv, cf, method, this.children);
        mv.visitMethodInsn(184, classDesc, method.getName(), CodeFlow.createSignatureDescriptor(method), false);
        cf.pushDescriptor(this.exitTypeDescriptor);
    }
}
