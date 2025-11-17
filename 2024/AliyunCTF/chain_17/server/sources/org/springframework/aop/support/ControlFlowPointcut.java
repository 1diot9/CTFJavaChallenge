package org.springframework.aop.support;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.PatternMatchUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/ControlFlowPointcut.class */
public class ControlFlowPointcut implements Pointcut, ClassFilter, MethodMatcher, Serializable {
    protected final Class<?> clazz;
    protected final List<String> methodNamePatterns;
    private final AtomicInteger evaluationCount;

    public ControlFlowPointcut(Class<?> clazz) {
        this(clazz, (String) null);
    }

    public ControlFlowPointcut(Class<?> clazz, @Nullable String methodNamePattern) {
        this.evaluationCount = new AtomicInteger();
        Assert.notNull(clazz, "Class must not be null");
        this.clazz = clazz;
        this.methodNamePatterns = methodNamePattern != null ? Collections.singletonList(methodNamePattern) : Collections.emptyList();
    }

    public ControlFlowPointcut(Class<?> clazz, String... methodNamePatterns) {
        this(clazz, (List<String>) Arrays.asList(methodNamePatterns));
    }

    public ControlFlowPointcut(Class<?> clazz, List<String> methodNamePatterns) {
        this.evaluationCount = new AtomicInteger();
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodNamePatterns, "List of method name patterns must not be null");
        Assert.noNullElements(methodNamePatterns, "List of method name patterns must not contain null elements");
        this.clazz = clazz;
        this.methodNamePatterns = methodNamePatterns.stream().distinct().toList();
    }

    @Override // org.springframework.aop.ClassFilter
    public boolean matches(Class<?> clazz) {
        return true;
    }

    @Override // org.springframework.aop.MethodMatcher
    public boolean matches(Method method, Class<?> targetClass) {
        return true;
    }

    @Override // org.springframework.aop.MethodMatcher
    public boolean isRuntime() {
        return true;
    }

    @Override // org.springframework.aop.MethodMatcher
    public boolean matches(Method method, Class<?> targetClass, Object... args) {
        incrementEvaluationCount();
        for (StackTraceElement element : new Throwable().getStackTrace()) {
            if (element.getClassName().equals(this.clazz.getName())) {
                if (this.methodNamePatterns.isEmpty()) {
                    return true;
                }
                String methodName = element.getMethodName();
                for (int i = 0; i < this.methodNamePatterns.size(); i++) {
                    if (isMatch(methodName, i)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int getEvaluations() {
        return this.evaluationCount.get();
    }

    protected final void incrementEvaluationCount() {
        this.evaluationCount.incrementAndGet();
    }

    protected boolean isMatch(String methodName, int patternIndex) {
        String methodNamePattern = this.methodNamePatterns.get(patternIndex);
        return isMatch(methodName, methodNamePattern);
    }

    protected boolean isMatch(String methodName, String methodNamePattern) {
        return methodName.equals(methodNamePattern) || PatternMatchUtils.simpleMatch(methodNamePattern, methodName);
    }

    @Override // org.springframework.aop.Pointcut
    public ClassFilter getClassFilter() {
        return this;
    }

    @Override // org.springframework.aop.Pointcut
    public MethodMatcher getMethodMatcher() {
        return this;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof ControlFlowPointcut) {
                ControlFlowPointcut that = (ControlFlowPointcut) other;
                if (!this.clazz.equals(that.clazz) || !this.methodNamePatterns.equals(that.methodNamePatterns)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        int code = this.clazz.hashCode();
        return (37 * code) + this.methodNamePatterns.hashCode();
    }

    public String toString() {
        return getClass().getName() + ": class = " + this.clazz.getName() + "; methodNamePatterns = " + this.methodNamePatterns;
    }
}
