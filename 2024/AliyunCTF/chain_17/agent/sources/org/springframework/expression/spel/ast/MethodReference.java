package org.springframework.expression.spel.ast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.ExpressionInvocationTargetException;
import org.springframework.expression.MethodExecutor;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.ast.ValueRef;
import org.springframework.expression.spel.support.ReflectiveMethodExecutor;
import org.springframework.expression.spel.support.ReflectiveMethodResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/MethodReference.class */
public class MethodReference extends SpelNodeImpl {
    private final boolean nullSafe;
    private final String name;

    @Nullable
    private Character originalPrimitiveExitTypeDescriptor;

    @Nullable
    private volatile CachedMethodExecutor cachedExecutor;

    public MethodReference(boolean nullSafe, String methodName, int startPos, int endPos, SpelNodeImpl... arguments) {
        super(startPos, endPos, arguments);
        this.name = methodName;
        this.nullSafe = nullSafe;
    }

    public final boolean isNullSafe() {
        return this.nullSafe;
    }

    public final String getName() {
        return this.name;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public ValueRef getValueRef(ExpressionState state) throws EvaluationException {
        Object[] arguments = getArguments(state);
        if (state.getActiveContextObject().getValue() == null) {
            throwIfNotNullSafe(getArgumentTypes(arguments));
            return ValueRef.NullValueRef.INSTANCE;
        }
        return new MethodValueRef(state, arguments);
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        EvaluationContext evaluationContext = state.getEvaluationContext();
        Object value = state.getActiveContextObject().getValue();
        TypeDescriptor targetType = state.getActiveContextObject().getTypeDescriptor();
        Object[] arguments = getArguments(state);
        TypedValue result = getValueInternal(evaluationContext, value, targetType, arguments);
        updateExitTypeDescriptor();
        return result;
    }

    private TypedValue getValueInternal(EvaluationContext evaluationContext, @Nullable Object value, @Nullable TypeDescriptor targetType, Object[] arguments) {
        Class<?> cls;
        List<TypeDescriptor> argumentTypes = getArgumentTypes(arguments);
        if (value == null) {
            throwIfNotNullSafe(argumentTypes);
            return TypedValue.NULL;
        }
        MethodExecutor executorToUse = getCachedExecutor(evaluationContext, value, targetType, argumentTypes);
        if (executorToUse != null) {
            try {
                return executorToUse.execute(evaluationContext, value, arguments);
            } catch (AccessException ex) {
                throwSimpleExceptionIfPossible(value, ex);
                this.cachedExecutor = null;
            }
        }
        MethodExecutor executorToUse2 = findAccessorForMethod(argumentTypes, value, evaluationContext);
        if (value instanceof Class) {
            Class<?> clazz = (Class) value;
            cls = clazz;
        } else {
            cls = null;
        }
        this.cachedExecutor = new CachedMethodExecutor(executorToUse2, cls, targetType, argumentTypes);
        try {
            return executorToUse2.execute(evaluationContext, value, arguments);
        } catch (AccessException ex2) {
            throwSimpleExceptionIfPossible(value, ex2);
            throw new SpelEvaluationException(getStartPosition(), ex2, SpelMessage.EXCEPTION_DURING_METHOD_INVOCATION, this.name, value.getClass().getName(), ex2.getMessage());
        }
    }

    private void throwIfNotNullSafe(List<TypeDescriptor> argumentTypes) {
        if (!this.nullSafe) {
            throw new SpelEvaluationException(getStartPosition(), SpelMessage.METHOD_CALL_ON_NULL_OBJECT_NOT_ALLOWED, FormatHelper.formatMethodForMessage(this.name, argumentTypes));
        }
    }

    private Object[] getArguments(ExpressionState state) {
        Object[] arguments = new Object[getChildCount()];
        for (int i = 0; i < arguments.length; i++) {
            try {
                state.pushActiveContextObject(state.getScopeRootContextObject());
                arguments[i] = this.children[i].getValueInternal(state).getValue();
                state.popActiveContextObject();
            } catch (Throwable th) {
                state.popActiveContextObject();
                throw th;
            }
        }
        return arguments;
    }

    private List<TypeDescriptor> getArgumentTypes(Object... arguments) {
        List<TypeDescriptor> descriptors = new ArrayList<>(arguments.length);
        for (Object argument : arguments) {
            descriptors.add(TypeDescriptor.forObject(argument));
        }
        return Collections.unmodifiableList(descriptors);
    }

    @Nullable
    private MethodExecutor getCachedExecutor(EvaluationContext evaluationContext, Object value, @Nullable TypeDescriptor target, List<TypeDescriptor> argumentTypes) {
        List<MethodResolver> methodResolvers = evaluationContext.getMethodResolvers();
        if (methodResolvers.size() != 1 || !(methodResolvers.get(0) instanceof ReflectiveMethodResolver)) {
            return null;
        }
        CachedMethodExecutor executorToCheck = this.cachedExecutor;
        if (executorToCheck != null && executorToCheck.isSuitable(value, target, argumentTypes)) {
            return executorToCheck.get();
        }
        this.cachedExecutor = null;
        return null;
    }

    private MethodExecutor findAccessorForMethod(List<TypeDescriptor> argumentTypes, Object targetObject, EvaluationContext evaluationContext) throws SpelEvaluationException {
        Class<?> cls;
        AccessException accessException = null;
        for (MethodResolver methodResolver : evaluationContext.getMethodResolvers()) {
            try {
                MethodExecutor methodExecutor = methodResolver.resolve(evaluationContext, targetObject, this.name, argumentTypes);
                if (methodExecutor != null) {
                    return methodExecutor;
                }
            } catch (AccessException ex) {
                accessException = ex;
            }
        }
        String method = FormatHelper.formatMethodForMessage(this.name, argumentTypes);
        if (targetObject instanceof Class) {
            Class<?> clazz = (Class) targetObject;
            cls = clazz;
        } else {
            cls = targetObject.getClass();
        }
        String className = FormatHelper.formatClassNameForMessage(cls);
        if (accessException != null) {
            throw new SpelEvaluationException(getStartPosition(), accessException, SpelMessage.PROBLEM_LOCATING_METHOD, method, className);
        }
        throw new SpelEvaluationException(getStartPosition(), SpelMessage.METHOD_NOT_FOUND, method, className);
    }

    private void throwSimpleExceptionIfPossible(Object value, AccessException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof InvocationTargetException) {
            InvocationTargetException cause2 = (InvocationTargetException) cause;
            Throwable rootCause = cause2.getCause();
            if (rootCause instanceof RuntimeException) {
                RuntimeException runtimeException = (RuntimeException) rootCause;
                throw runtimeException;
            }
            throw new ExpressionInvocationTargetException(getStartPosition(), "A problem occurred when trying to execute method '" + this.name + "' on object of type [" + value.getClass().getName() + "]", rootCause);
        }
    }

    private void updateExitTypeDescriptor() {
        CachedMethodExecutor executorToCheck = this.cachedExecutor;
        if (executorToCheck != null) {
            MethodExecutor methodExecutor = executorToCheck.get();
            if (methodExecutor instanceof ReflectiveMethodExecutor) {
                ReflectiveMethodExecutor reflectiveMethodExecutor = (ReflectiveMethodExecutor) methodExecutor;
                Method method = reflectiveMethodExecutor.getMethod();
                String descriptor = CodeFlow.toDescriptor(method.getReturnType());
                if (this.nullSafe && CodeFlow.isPrimitive(descriptor) && descriptor.charAt(0) != 'V') {
                    this.originalPrimitiveExitTypeDescriptor = Character.valueOf(descriptor.charAt(0));
                    this.exitTypeDescriptor = CodeFlow.toBoxedDescriptor(descriptor);
                } else {
                    this.exitTypeDescriptor = descriptor;
                }
            }
        }
    }

    @Override // org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        StringJoiner sj = new StringJoiner(",", "(", ")");
        for (int i = 0; i < getChildCount(); i++) {
            sj.add(getChild(i).toStringAST());
        }
        return this.name + sj;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        CachedMethodExecutor executorToCheck = this.cachedExecutor;
        if (executorToCheck != null && !executorToCheck.hasProxyTarget()) {
            MethodExecutor methodExecutor = executorToCheck.get();
            if (!(methodExecutor instanceof ReflectiveMethodExecutor)) {
                return false;
            }
            ReflectiveMethodExecutor executor = (ReflectiveMethodExecutor) methodExecutor;
            for (SpelNodeImpl child : this.children) {
                if (!child.isCompilable()) {
                    return false;
                }
            }
            if (executor.didArgumentConversionOccur()) {
                return false;
            }
            Class<?> clazz = executor.getMethod().getDeclaringClass();
            return Modifier.isPublic(clazz.getModifiers()) || executor.getPublicDeclaringClass() != null;
        }
        return false;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        String classDesc;
        CachedMethodExecutor executorToCheck = this.cachedExecutor;
        if (executorToCheck != null) {
            MethodExecutor methodExecutor = executorToCheck.get();
            if (methodExecutor instanceof ReflectiveMethodExecutor) {
                ReflectiveMethodExecutor methodExecutor2 = (ReflectiveMethodExecutor) methodExecutor;
                Method method = methodExecutor2.getMethod();
                boolean isStaticMethod = Modifier.isStatic(method.getModifiers());
                String descriptor = cf.lastDescriptor();
                if (descriptor == null && !isStaticMethod) {
                    cf.loadTarget(mv);
                }
                Label skipIfNull = null;
                if (this.nullSafe && (descriptor != null || !isStaticMethod)) {
                    skipIfNull = new Label();
                    Label continueLabel = new Label();
                    mv.visitInsn(89);
                    mv.visitJumpInsn(Opcodes.IFNONNULL, continueLabel);
                    CodeFlow.insertCheckCast(mv, this.exitTypeDescriptor);
                    mv.visitJumpInsn(Opcodes.GOTO, skipIfNull);
                    mv.visitLabel(continueLabel);
                }
                if (descriptor != null && isStaticMethod) {
                    mv.visitInsn(87);
                }
                if (CodeFlow.isPrimitive(descriptor)) {
                    CodeFlow.insertBoxIfNecessary(mv, descriptor.charAt(0));
                }
                if (Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
                    classDesc = method.getDeclaringClass().getName().replace('.', '/');
                } else {
                    Class<?> publicDeclaringClass = methodExecutor2.getPublicDeclaringClass();
                    Assert.state(publicDeclaringClass != null, "No public declaring class");
                    classDesc = publicDeclaringClass.getName().replace('.', '/');
                }
                if (!isStaticMethod && (descriptor == null || !descriptor.substring(1).equals(classDesc))) {
                    CodeFlow.insertCheckCast(mv, "L" + classDesc);
                }
                generateCodeForArguments(mv, cf, method, this.children);
                mv.visitMethodInsn(isStaticMethod ? 184 : method.isDefault() ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL, classDesc, method.getName(), CodeFlow.createSignatureDescriptor(method), method.getDeclaringClass().isInterface());
                cf.pushDescriptor(this.exitTypeDescriptor);
                if (this.originalPrimitiveExitTypeDescriptor != null) {
                    CodeFlow.insertBoxIfNecessary(mv, this.originalPrimitiveExitTypeDescriptor.charValue());
                }
                if (skipIfNull != null) {
                    if ("V".equals(this.exitTypeDescriptor)) {
                        mv.visitInsn(1);
                    }
                    mv.visitLabel(skipIfNull);
                    return;
                }
                return;
            }
        }
        throw new IllegalStateException("No applicable cached executor found: " + executorToCheck);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/MethodReference$MethodValueRef.class */
    private class MethodValueRef implements ValueRef {
        private final EvaluationContext evaluationContext;

        @Nullable
        private final Object value;

        @Nullable
        private final TypeDescriptor targetType;
        private final Object[] arguments;

        public MethodValueRef(ExpressionState state, Object[] arguments) {
            this.evaluationContext = state.getEvaluationContext();
            this.value = state.getActiveContextObject().getValue();
            this.targetType = state.getActiveContextObject().getTypeDescriptor();
            this.arguments = arguments;
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public TypedValue getValue() {
            TypedValue result = MethodReference.this.getValueInternal(this.evaluationContext, this.value, this.targetType, this.arguments);
            MethodReference.this.updateExitTypeDescriptor();
            return result;
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public void setValue(@Nullable Object newValue) {
            throw new IllegalAccessError();
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public boolean isWritable() {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/MethodReference$CachedMethodExecutor.class */
    public static class CachedMethodExecutor {
        private final MethodExecutor methodExecutor;

        @Nullable
        private final Class<?> staticClass;

        @Nullable
        private final TypeDescriptor target;
        private final List<TypeDescriptor> argumentTypes;

        public CachedMethodExecutor(MethodExecutor methodExecutor, @Nullable Class<?> staticClass, @Nullable TypeDescriptor target, List<TypeDescriptor> argumentTypes) {
            this.methodExecutor = methodExecutor;
            this.staticClass = staticClass;
            this.target = target;
            this.argumentTypes = argumentTypes;
        }

        public boolean isSuitable(Object value, @Nullable TypeDescriptor target, List<TypeDescriptor> argumentTypes) {
            return (this.staticClass == null || this.staticClass == value) && ObjectUtils.nullSafeEquals(this.target, target) && this.argumentTypes.equals(argumentTypes);
        }

        public boolean hasProxyTarget() {
            return this.target != null && Proxy.isProxyClass(this.target.getType());
        }

        public MethodExecutor get() {
            return this.methodExecutor;
        }
    }
}
