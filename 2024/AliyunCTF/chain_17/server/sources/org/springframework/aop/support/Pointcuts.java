package org.springframework.aop.support;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.io.Serializable;
import java.lang.reflect.Method;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/Pointcuts.class */
public abstract class Pointcuts {
    public static final Pointcut SETTERS = SetterPointcut.INSTANCE;
    public static final Pointcut GETTERS = GetterPointcut.INSTANCE;

    public static Pointcut union(Pointcut pc1, Pointcut pc2) {
        return new ComposablePointcut(pc1).union(pc2);
    }

    public static Pointcut intersection(Pointcut pc1, Pointcut pc2) {
        return new ComposablePointcut(pc1).intersection(pc2);
    }

    public static boolean matches(Pointcut pointcut, Method method, Class<?> targetClass, Object... args) {
        Assert.notNull(pointcut, "Pointcut must not be null");
        if (pointcut == Pointcut.TRUE) {
            return true;
        }
        if (pointcut.getClassFilter().matches(targetClass)) {
            MethodMatcher mm = pointcut.getMethodMatcher();
            if (mm.matches(method, targetClass)) {
                return !mm.isRuntime() || mm.matches(method, targetClass, args);
            }
            return false;
        }
        return false;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/Pointcuts$SetterPointcut.class */
    private static class SetterPointcut extends StaticMethodMatcherPointcut implements Serializable {
        public static final SetterPointcut INSTANCE = new SetterPointcut();

        private SetterPointcut() {
        }

        @Override // org.springframework.aop.MethodMatcher
        public boolean matches(Method method, Class<?> targetClass) {
            return method.getName().startsWith("set") && method.getParameterCount() == 1 && method.getReturnType() == Void.TYPE;
        }

        private Object readResolve() {
            return INSTANCE;
        }

        public String toString() {
            return "Pointcuts.SETTERS";
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/Pointcuts$GetterPointcut.class */
    private static class GetterPointcut extends StaticMethodMatcherPointcut implements Serializable {
        public static final GetterPointcut INSTANCE = new GetterPointcut();

        private GetterPointcut() {
        }

        @Override // org.springframework.aop.MethodMatcher
        public boolean matches(Method method, Class<?> targetClass) {
            return method.getName().startsWith(BeanUtil.PREFIX_GETTER_GET) && method.getParameterCount() == 0 && method.getReturnType() != Void.TYPE;
        }

        private Object readResolve() {
            return INSTANCE;
        }

        public String toString() {
            return "Pointcuts.GETTERS";
        }
    }
}
