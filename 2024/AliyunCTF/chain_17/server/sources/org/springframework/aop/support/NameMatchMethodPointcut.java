package org.springframework.aop.support;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.PatternMatchUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/NameMatchMethodPointcut.class */
public class NameMatchMethodPointcut extends StaticMethodMatcherPointcut implements Serializable {
    private List<String> mappedNamePatterns = new ArrayList();

    public void setMappedName(String mappedNamePattern) {
        setMappedNames(mappedNamePattern);
    }

    public void setMappedNames(String... mappedNamePatterns) {
        this.mappedNamePatterns = new ArrayList(Arrays.asList(mappedNamePatterns));
    }

    public NameMatchMethodPointcut addMethodName(String mappedNamePattern) {
        this.mappedNamePatterns.add(mappedNamePattern);
        return this;
    }

    @Override // org.springframework.aop.MethodMatcher
    public boolean matches(Method method, Class<?> targetClass) {
        for (String mappedNamePattern : this.mappedNamePatterns) {
            if (mappedNamePattern.equals(method.getName()) || isMatch(method.getName(), mappedNamePattern)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isMatch(String methodName, String mappedNamePattern) {
        return PatternMatchUtils.simpleMatch(mappedNamePattern, methodName);
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof NameMatchMethodPointcut) {
                NameMatchMethodPointcut that = (NameMatchMethodPointcut) other;
                if (this.mappedNamePatterns.equals(that.mappedNamePatterns)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.mappedNamePatterns.hashCode();
    }

    public String toString() {
        return getClass().getName() + ": " + this.mappedNamePatterns;
    }
}
