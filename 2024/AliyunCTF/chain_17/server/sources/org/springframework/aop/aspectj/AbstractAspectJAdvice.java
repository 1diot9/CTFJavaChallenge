package org.springframework.aop.aspectj;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.weaver.tools.JoinPointMatch;
import org.aspectj.weaver.tools.PointcutParameter;
import org.springframework.aop.AopInvocationException;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.MethodMatchers;
import org.springframework.aop.support.StaticMethodMatcher;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/AbstractAspectJAdvice.class */
public abstract class AbstractAspectJAdvice implements Advice, AspectJPrecedenceInformation, Serializable {
    protected static final String JOIN_POINT_KEY = JoinPoint.class.getName();
    private final Class<?> declaringClass;
    private final String methodName;
    private final Class<?>[] parameterTypes;
    protected transient Method aspectJAdviceMethod;
    private final AspectJExpressionPointcut pointcut;
    private final AspectInstanceFactory aspectInstanceFactory;
    private int declarationOrder;

    @Nullable
    private String[] argumentNames;

    @Nullable
    private String throwingName;

    @Nullable
    private String returningName;

    @Nullable
    private Map<String, Integer> argumentBindings;

    @Nullable
    private Type discoveredReturningGenericType;
    private String aspectName = "";
    private Class<?> discoveredReturningType = Object.class;
    private Class<?> discoveredThrowingType = Object.class;
    private int joinPointArgumentIndex = -1;
    private int joinPointStaticPartArgumentIndex = -1;
    private boolean argumentsIntrospected = false;

    public static JoinPoint currentJoinPoint() {
        MethodInvocation mi = ExposeInvocationInterceptor.currentInvocation();
        if (!(mi instanceof ProxyMethodInvocation)) {
            throw new IllegalStateException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
        }
        ProxyMethodInvocation pmi = (ProxyMethodInvocation) mi;
        MethodInvocationProceedingJoinPoint methodInvocationProceedingJoinPoint = (JoinPoint) pmi.getUserAttribute(JOIN_POINT_KEY);
        if (methodInvocationProceedingJoinPoint == null) {
            methodInvocationProceedingJoinPoint = new MethodInvocationProceedingJoinPoint(pmi);
            pmi.setUserAttribute(JOIN_POINT_KEY, methodInvocationProceedingJoinPoint);
        }
        return methodInvocationProceedingJoinPoint;
    }

    public AbstractAspectJAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aspectInstanceFactory) {
        Assert.notNull(aspectJAdviceMethod, "Advice method must not be null");
        this.declaringClass = aspectJAdviceMethod.getDeclaringClass();
        this.methodName = aspectJAdviceMethod.getName();
        this.parameterTypes = aspectJAdviceMethod.getParameterTypes();
        this.aspectJAdviceMethod = aspectJAdviceMethod;
        this.pointcut = pointcut;
        this.aspectInstanceFactory = aspectInstanceFactory;
    }

    public final Method getAspectJAdviceMethod() {
        return this.aspectJAdviceMethod;
    }

    public final AspectJExpressionPointcut getPointcut() {
        calculateArgumentBindings();
        return this.pointcut;
    }

    public final Pointcut buildSafePointcut() {
        Pointcut pc = getPointcut();
        MethodMatcher safeMethodMatcher = MethodMatchers.intersection(new AdviceExcludingMethodMatcher(this.aspectJAdviceMethod), pc.getMethodMatcher());
        return new ComposablePointcut(pc.getClassFilter(), safeMethodMatcher);
    }

    public final AspectInstanceFactory getAspectInstanceFactory() {
        return this.aspectInstanceFactory;
    }

    @Nullable
    public final ClassLoader getAspectClassLoader() {
        return this.aspectInstanceFactory.getAspectClassLoader();
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.aspectInstanceFactory.getOrder();
    }

    public void setAspectName(String name) {
        this.aspectName = name;
    }

    @Override // org.springframework.aop.aspectj.AspectJPrecedenceInformation
    public String getAspectName() {
        return this.aspectName;
    }

    public void setDeclarationOrder(int order) {
        this.declarationOrder = order;
    }

    @Override // org.springframework.aop.aspectj.AspectJPrecedenceInformation
    public int getDeclarationOrder() {
        return this.declarationOrder;
    }

    public void setArgumentNames(String argumentNames) {
        String[] tokens = StringUtils.commaDelimitedListToStringArray(argumentNames);
        setArgumentNamesFromStringArray(tokens);
    }

    public void setArgumentNamesFromStringArray(String... argumentNames) {
        this.argumentNames = new String[argumentNames.length];
        for (int i = 0; i < argumentNames.length; i++) {
            this.argumentNames[i] = argumentNames[i].strip();
            if (!isVariableName(this.argumentNames[i])) {
                throw new IllegalArgumentException("'argumentNames' property of AbstractAspectJAdvice contains an argument name '" + this.argumentNames[i] + "' that is not a valid Java identifier");
            }
        }
        if (this.aspectJAdviceMethod.getParameterCount() == this.argumentNames.length + 1) {
            Class<?> firstArgType = this.aspectJAdviceMethod.getParameterTypes()[0];
            if (firstArgType == JoinPoint.class || firstArgType == ProceedingJoinPoint.class || firstArgType == JoinPoint.StaticPart.class) {
                String[] oldNames = this.argumentNames;
                this.argumentNames = new String[oldNames.length + 1];
                this.argumentNames[0] = "THIS_JOIN_POINT";
                System.arraycopy(oldNames, 0, this.argumentNames, 1, oldNames.length);
            }
        }
    }

    public void setReturningName(String name) {
        throw new UnsupportedOperationException("Only afterReturning advice can be used to bind a return value");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setReturningNameNoCheck(String name) {
        if (isVariableName(name)) {
            this.returningName = name;
            return;
        }
        try {
            this.discoveredReturningType = ClassUtils.forName(name, getAspectClassLoader());
        } catch (Throwable ex) {
            throw new IllegalArgumentException("Returning name '" + name + "' is neither a valid argument name nor the fully-qualified name of a Java type on the classpath. Root cause: " + ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Class<?> getDiscoveredReturningType() {
        return this.discoveredReturningType;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Type getDiscoveredReturningGenericType() {
        return this.discoveredReturningGenericType;
    }

    public void setThrowingName(String name) {
        throw new UnsupportedOperationException("Only afterThrowing advice can be used to bind a thrown exception");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setThrowingNameNoCheck(String name) {
        if (isVariableName(name)) {
            this.throwingName = name;
            return;
        }
        try {
            this.discoveredThrowingType = ClassUtils.forName(name, getAspectClassLoader());
        } catch (Throwable ex) {
            throw new IllegalArgumentException("Throwing name '" + name + "' is neither a valid argument name nor the fully-qualified name of a Java type on the classpath. Root cause: " + ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Class<?> getDiscoveredThrowingType() {
        return this.discoveredThrowingType;
    }

    private static boolean isVariableName(String name) {
        return AspectJProxyUtils.isVariableName(name);
    }

    public final void calculateArgumentBindings() {
        if (this.argumentsIntrospected || this.parameterTypes.length == 0) {
            return;
        }
        int numUnboundArgs = this.parameterTypes.length;
        Class<?>[] parameterTypes = this.aspectJAdviceMethod.getParameterTypes();
        if (maybeBindJoinPoint(parameterTypes[0]) || maybeBindProceedingJoinPoint(parameterTypes[0]) || maybeBindJoinPointStaticPart(parameterTypes[0])) {
            numUnboundArgs--;
        }
        if (numUnboundArgs > 0) {
            bindArgumentsByName(numUnboundArgs);
        }
        this.argumentsIntrospected = true;
    }

    private boolean maybeBindJoinPoint(Class<?> candidateParameterType) {
        if (JoinPoint.class == candidateParameterType) {
            this.joinPointArgumentIndex = 0;
            return true;
        }
        return false;
    }

    private boolean maybeBindProceedingJoinPoint(Class<?> candidateParameterType) {
        if (ProceedingJoinPoint.class == candidateParameterType) {
            if (!supportsProceedingJoinPoint()) {
                throw new IllegalArgumentException("ProceedingJoinPoint is only supported for around advice");
            }
            this.joinPointArgumentIndex = 0;
            return true;
        }
        return false;
    }

    protected boolean supportsProceedingJoinPoint() {
        return false;
    }

    private boolean maybeBindJoinPointStaticPart(Class<?> candidateParameterType) {
        if (JoinPoint.StaticPart.class == candidateParameterType) {
            this.joinPointStaticPartArgumentIndex = 0;
            return true;
        }
        return false;
    }

    private void bindArgumentsByName(int numArgumentsExpectingToBind) {
        if (this.argumentNames == null) {
            this.argumentNames = createParameterNameDiscoverer().getParameterNames(this.aspectJAdviceMethod);
        }
        if (this.argumentNames != null) {
            bindExplicitArguments(numArgumentsExpectingToBind);
            return;
        }
        throw new IllegalStateException("Advice method [" + this.aspectJAdviceMethod.getName() + "] requires " + numArgumentsExpectingToBind + " arguments to be bound by name, but the argument names were not specified and could not be discovered.");
    }

    protected ParameterNameDiscoverer createParameterNameDiscoverer() {
        DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
        AspectJAdviceParameterNameDiscoverer adviceParameterNameDiscoverer = new AspectJAdviceParameterNameDiscoverer(this.pointcut.getExpression());
        adviceParameterNameDiscoverer.setReturningName(this.returningName);
        adviceParameterNameDiscoverer.setThrowingName(this.throwingName);
        adviceParameterNameDiscoverer.setRaiseExceptions(true);
        discoverer.addDiscoverer(adviceParameterNameDiscoverer);
        return discoverer;
    }

    private void bindExplicitArguments(int numArgumentsLeftToBind) {
        Assert.state(this.argumentNames != null, "No argument names available");
        this.argumentBindings = new HashMap();
        int numExpectedArgumentNames = this.aspectJAdviceMethod.getParameterCount();
        if (this.argumentNames.length != numExpectedArgumentNames) {
            throw new IllegalStateException("Expecting to find " + numExpectedArgumentNames + " arguments to bind by name in advice, but actually found " + this.argumentNames.length + " arguments.");
        }
        int argumentIndexOffset = this.parameterTypes.length - numArgumentsLeftToBind;
        for (int i = argumentIndexOffset; i < this.argumentNames.length; i++) {
            this.argumentBindings.put(this.argumentNames[i], Integer.valueOf(i));
        }
        if (this.returningName != null) {
            if (!this.argumentBindings.containsKey(this.returningName)) {
                throw new IllegalStateException("Returning argument name '" + this.returningName + "' was not bound in advice arguments");
            }
            Integer index = this.argumentBindings.get(this.returningName);
            this.discoveredReturningType = this.aspectJAdviceMethod.getParameterTypes()[index.intValue()];
            this.discoveredReturningGenericType = this.aspectJAdviceMethod.getGenericParameterTypes()[index.intValue()];
        }
        if (this.throwingName != null) {
            if (!this.argumentBindings.containsKey(this.throwingName)) {
                throw new IllegalStateException("Throwing argument name '" + this.throwingName + "' was not bound in advice arguments");
            }
            this.discoveredThrowingType = this.aspectJAdviceMethod.getParameterTypes()[this.argumentBindings.get(this.throwingName).intValue()];
        }
        configurePointcutParameters(this.argumentNames, argumentIndexOffset);
    }

    private void configurePointcutParameters(String[] argumentNames, int argumentIndexOffset) {
        int numParametersToRemove = argumentIndexOffset;
        if (this.returningName != null) {
            numParametersToRemove++;
        }
        if (this.throwingName != null) {
            numParametersToRemove++;
        }
        String[] pointcutParameterNames = new String[argumentNames.length - numParametersToRemove];
        Class<?>[] pointcutParameterTypes = new Class[pointcutParameterNames.length];
        Class<?>[] methodParameterTypes = this.aspectJAdviceMethod.getParameterTypes();
        int index = 0;
        for (int i = 0; i < argumentNames.length; i++) {
            if (i >= argumentIndexOffset && !argumentNames[i].equals(this.returningName) && !argumentNames[i].equals(this.throwingName)) {
                pointcutParameterNames[index] = argumentNames[i];
                pointcutParameterTypes[index] = methodParameterTypes[i];
                index++;
            }
        }
        this.pointcut.setParameterNames(pointcutParameterNames);
        this.pointcut.setParameterTypes(pointcutParameterTypes);
    }

    protected Object[] argBinding(JoinPoint jp, @Nullable JoinPointMatch jpMatch, @Nullable Object returnValue, @Nullable Throwable ex) {
        calculateArgumentBindings();
        Object[] adviceInvocationArgs = new Object[this.parameterTypes.length];
        int numBound = 0;
        if (this.joinPointArgumentIndex != -1) {
            adviceInvocationArgs[this.joinPointArgumentIndex] = jp;
            numBound = 0 + 1;
        } else if (this.joinPointStaticPartArgumentIndex != -1) {
            adviceInvocationArgs[this.joinPointStaticPartArgumentIndex] = jp.getStaticPart();
            numBound = 0 + 1;
        }
        if (!CollectionUtils.isEmpty(this.argumentBindings)) {
            if (jpMatch != null) {
                PointcutParameter[] parameterBindings = jpMatch.getParameterBindings();
                for (PointcutParameter parameter : parameterBindings) {
                    String name = parameter.getName();
                    Integer index = this.argumentBindings.get(name);
                    adviceInvocationArgs[index.intValue()] = parameter.getBinding();
                    numBound++;
                }
            }
            if (this.returningName != null) {
                Integer index2 = this.argumentBindings.get(this.returningName);
                adviceInvocationArgs[index2.intValue()] = returnValue;
                numBound++;
            }
            if (this.throwingName != null) {
                Integer index3 = this.argumentBindings.get(this.throwingName);
                adviceInvocationArgs[index3.intValue()] = ex;
                numBound++;
            }
        }
        if (numBound != this.parameterTypes.length) {
            throw new IllegalStateException("Required to bind " + this.parameterTypes.length + " arguments, but only bound " + numBound + " (JoinPointMatch " + (jpMatch == null ? "was NOT" : "WAS") + " bound in invocation)");
        }
        return adviceInvocationArgs;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object invokeAdviceMethod(@Nullable JoinPointMatch jpMatch, @Nullable Object returnValue, @Nullable Throwable ex) throws Throwable {
        return invokeAdviceMethodWithGivenArgs(argBinding(getJoinPoint(), jpMatch, returnValue, ex));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object invokeAdviceMethod(JoinPoint jp, @Nullable JoinPointMatch jpMatch, @Nullable Object returnValue, @Nullable Throwable t) throws Throwable {
        return invokeAdviceMethodWithGivenArgs(argBinding(jp, jpMatch, returnValue, t));
    }

    protected Object invokeAdviceMethodWithGivenArgs(Object[] args) throws Throwable {
        Object[] actualArgs = args;
        if (this.aspectJAdviceMethod.getParameterCount() == 0) {
            actualArgs = null;
        }
        try {
            ReflectionUtils.makeAccessible(this.aspectJAdviceMethod);
            return this.aspectJAdviceMethod.invoke(this.aspectInstanceFactory.getAspectInstance(), actualArgs);
        } catch (IllegalArgumentException ex) {
            throw new AopInvocationException("Mismatch on arguments to advice method [" + this.aspectJAdviceMethod + "]; pointcut expression [" + this.pointcut.getPointcutExpression() + "]", ex);
        } catch (InvocationTargetException ex2) {
            throw ex2.getTargetException();
        }
    }

    protected JoinPoint getJoinPoint() {
        return currentJoinPoint();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public JoinPointMatch getJoinPointMatch() {
        MethodInvocation mi = ExposeInvocationInterceptor.currentInvocation();
        if (!(mi instanceof ProxyMethodInvocation)) {
            throw new IllegalStateException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
        }
        ProxyMethodInvocation pmi = (ProxyMethodInvocation) mi;
        return getJoinPointMatch(pmi);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public JoinPointMatch getJoinPointMatch(ProxyMethodInvocation pmi) {
        String expression = this.pointcut.getExpression();
        if (expression != null) {
            return (JoinPointMatch) pmi.getUserAttribute(expression);
        }
        return null;
    }

    public String toString() {
        return getClass().getName() + ": advice method [" + this.aspectJAdviceMethod + "]; aspect name '" + this.aspectName + "'";
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        inputStream.defaultReadObject();
        try {
            this.aspectJAdviceMethod = this.declaringClass.getMethod(this.methodName, this.parameterTypes);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException("Failed to find advice method on deserialization", ex);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/AbstractAspectJAdvice$AdviceExcludingMethodMatcher.class */
    private static class AdviceExcludingMethodMatcher extends StaticMethodMatcher {
        private final Method adviceMethod;

        public AdviceExcludingMethodMatcher(Method adviceMethod) {
            this.adviceMethod = adviceMethod;
        }

        @Override // org.springframework.aop.MethodMatcher
        public boolean matches(Method method, Class<?> targetClass) {
            return !this.adviceMethod.equals(method);
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof AdviceExcludingMethodMatcher) {
                    AdviceExcludingMethodMatcher that = (AdviceExcludingMethodMatcher) other;
                    if (this.adviceMethod.equals(that.adviceMethod)) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return this.adviceMethod.hashCode();
        }

        public String toString() {
            return getClass().getName() + ": " + this.adviceMethod;
        }
    }
}
