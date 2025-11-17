package org.springframework.aop.aspectj;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.runtime.internal.AroundClosure;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/MethodInvocationProceedingJoinPoint.class */
public class MethodInvocationProceedingJoinPoint implements ProceedingJoinPoint, JoinPoint.StaticPart {
    private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private final ProxyMethodInvocation methodInvocation;

    @Nullable
    private Object[] args;

    @Nullable
    private Signature signature;

    @Nullable
    private SourceLocation sourceLocation;

    public MethodInvocationProceedingJoinPoint(ProxyMethodInvocation methodInvocation) {
        Assert.notNull(methodInvocation, "MethodInvocation must not be null");
        this.methodInvocation = methodInvocation;
    }

    public void set$AroundClosure(AroundClosure aroundClosure) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    public Object proceed() throws Throwable {
        return this.methodInvocation.invocableClone().proceed();
    }

    @Nullable
    public Object proceed(Object[] arguments) throws Throwable {
        Assert.notNull(arguments, "Argument array passed to proceed cannot be null");
        if (arguments.length != this.methodInvocation.getArguments().length) {
            throw new IllegalArgumentException("Expecting " + this.methodInvocation.getArguments().length + " arguments to proceed, but was passed " + arguments.length + " arguments");
        }
        this.methodInvocation.setArguments(arguments);
        return this.methodInvocation.invocableClone(arguments).proceed();
    }

    public Object getThis() {
        return this.methodInvocation.getProxy();
    }

    @Nullable
    public Object getTarget() {
        return this.methodInvocation.getThis();
    }

    public Object[] getArgs() {
        if (this.args == null) {
            this.args = (Object[]) this.methodInvocation.getArguments().clone();
        }
        return this.args;
    }

    public Signature getSignature() {
        if (this.signature == null) {
            this.signature = new MethodSignatureImpl();
        }
        return this.signature;
    }

    public SourceLocation getSourceLocation() {
        if (this.sourceLocation == null) {
            this.sourceLocation = new SourceLocationImpl();
        }
        return this.sourceLocation;
    }

    public String getKind() {
        return "method-execution";
    }

    public int getId() {
        return 0;
    }

    public JoinPoint.StaticPart getStaticPart() {
        return this;
    }

    public String toShortString() {
        return "execution(" + getSignature().toShortString() + ")";
    }

    public String toLongString() {
        return "execution(" + getSignature().toLongString() + ")";
    }

    public String toString() {
        return "execution(" + getSignature().toString() + ")";
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/MethodInvocationProceedingJoinPoint$MethodSignatureImpl.class */
    public class MethodSignatureImpl implements MethodSignature {

        @Nullable
        private volatile String[] parameterNames;

        private MethodSignatureImpl() {
        }

        public String getName() {
            return MethodInvocationProceedingJoinPoint.this.methodInvocation.getMethod().getName();
        }

        public int getModifiers() {
            return MethodInvocationProceedingJoinPoint.this.methodInvocation.getMethod().getModifiers();
        }

        public Class<?> getDeclaringType() {
            return MethodInvocationProceedingJoinPoint.this.methodInvocation.getMethod().getDeclaringClass();
        }

        public String getDeclaringTypeName() {
            return MethodInvocationProceedingJoinPoint.this.methodInvocation.getMethod().getDeclaringClass().getName();
        }

        public Class<?> getReturnType() {
            return MethodInvocationProceedingJoinPoint.this.methodInvocation.getMethod().getReturnType();
        }

        public Method getMethod() {
            return MethodInvocationProceedingJoinPoint.this.methodInvocation.getMethod();
        }

        public Class<?>[] getParameterTypes() {
            return MethodInvocationProceedingJoinPoint.this.methodInvocation.getMethod().getParameterTypes();
        }

        @Nullable
        public String[] getParameterNames() {
            String[] parameterNames = this.parameterNames;
            if (parameterNames == null) {
                parameterNames = MethodInvocationProceedingJoinPoint.parameterNameDiscoverer.getParameterNames(getMethod());
                this.parameterNames = parameterNames;
            }
            return parameterNames;
        }

        public Class<?>[] getExceptionTypes() {
            return MethodInvocationProceedingJoinPoint.this.methodInvocation.getMethod().getExceptionTypes();
        }

        public String toShortString() {
            return toString(false, false, false, false);
        }

        public String toLongString() {
            return toString(true, true, true, true);
        }

        public String toString() {
            return toString(false, true, false, true);
        }

        private String toString(boolean includeModifier, boolean includeReturnTypeAndArgs, boolean useLongReturnAndArgumentTypeName, boolean useLongTypeName) {
            StringBuilder sb = new StringBuilder();
            if (includeModifier) {
                sb.append(Modifier.toString(getModifiers()));
                sb.append(' ');
            }
            if (includeReturnTypeAndArgs) {
                appendType(sb, getReturnType(), useLongReturnAndArgumentTypeName);
                sb.append(' ');
            }
            appendType(sb, getDeclaringType(), useLongTypeName);
            sb.append('.');
            sb.append(getMethod().getName());
            sb.append('(');
            Class<?>[] parametersTypes = getParameterTypes();
            appendTypes(sb, parametersTypes, includeReturnTypeAndArgs, useLongReturnAndArgumentTypeName);
            sb.append(')');
            return sb.toString();
        }

        private void appendTypes(StringBuilder sb, Class<?>[] types, boolean includeArgs, boolean useLongReturnAndArgumentTypeName) {
            if (includeArgs) {
                int size = types.length;
                for (int i = 0; i < size; i++) {
                    appendType(sb, types[i], useLongReturnAndArgumentTypeName);
                    if (i < size - 1) {
                        sb.append(',');
                    }
                }
                return;
            }
            if (types.length != 0) {
                sb.append("..");
            }
        }

        private void appendType(StringBuilder sb, Class<?> type, boolean useLongTypeName) {
            if (type.isArray()) {
                appendType(sb, type.componentType(), useLongTypeName);
                sb.append(ClassUtils.ARRAY_SUFFIX);
            } else {
                sb.append(useLongTypeName ? type.getName() : type.getSimpleName());
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/MethodInvocationProceedingJoinPoint$SourceLocationImpl.class */
    private class SourceLocationImpl implements SourceLocation {
        private SourceLocationImpl() {
        }

        public Class<?> getWithinType() {
            if (MethodInvocationProceedingJoinPoint.this.methodInvocation.getThis() == null) {
                throw new UnsupportedOperationException("No source location joinpoint available: target is null");
            }
            return MethodInvocationProceedingJoinPoint.this.methodInvocation.getThis().getClass();
        }

        public String getFileName() {
            throw new UnsupportedOperationException();
        }

        public int getLine() {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        public int getColumn() {
            throw new UnsupportedOperationException();
        }
    }
}
