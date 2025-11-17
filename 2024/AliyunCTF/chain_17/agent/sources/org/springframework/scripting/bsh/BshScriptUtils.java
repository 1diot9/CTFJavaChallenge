package org.springframework.scripting.bsh;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.Primitive;
import bsh.XThis;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scripting/bsh/BshScriptUtils.class */
public abstract class BshScriptUtils {
    public static Object createBshObject(String scriptSource) throws EvalError {
        return createBshObject(scriptSource, null, null);
    }

    public static Object createBshObject(String scriptSource, @Nullable Class<?>... scriptInterfaces) throws EvalError {
        return createBshObject(scriptSource, scriptInterfaces, ClassUtils.getDefaultClassLoader());
    }

    public static Object createBshObject(String scriptSource, @Nullable Class<?>[] scriptInterfaces, @Nullable ClassLoader classLoader) throws EvalError {
        Object result = evaluateBshScript(scriptSource, scriptInterfaces, classLoader);
        if (result instanceof Class) {
            Class<?> clazz = (Class) result;
            try {
                return ReflectionUtils.accessibleConstructor(clazz, new Class[0]).newInstance(new Object[0]);
            } catch (Throwable ex) {
                throw new IllegalStateException("Could not instantiate script class: " + clazz.getName(), ex);
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public static Class<?> determineBshObjectType(String scriptSource, @Nullable ClassLoader classLoader) throws EvalError {
        Assert.hasText(scriptSource, "Script source must not be empty");
        Interpreter interpreter = new Interpreter();
        if (classLoader != null) {
            interpreter.setClassLoader(classLoader);
        }
        Object result = interpreter.eval(scriptSource);
        if (result instanceof Class) {
            Class<?> clazz = (Class) result;
            return clazz;
        }
        if (result != null) {
            return result.getClass();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object evaluateBshScript(String scriptSource, @Nullable Class<?>[] scriptInterfaces, @Nullable ClassLoader classLoader) throws EvalError {
        Assert.hasText(scriptSource, "Script source must not be empty");
        Interpreter interpreter = new Interpreter();
        interpreter.setClassLoader(classLoader);
        Object result = interpreter.eval(scriptSource);
        if (result != null) {
            return result;
        }
        if (ObjectUtils.isEmpty((Object[]) scriptInterfaces)) {
            throw new IllegalArgumentException("Given script requires a script proxy: At least one script interface is required.\nScript: " + scriptSource);
        }
        XThis xt = (XThis) interpreter.eval("return this");
        return Proxy.newProxyInstance(classLoader, scriptInterfaces, new BshObjectInvocationHandler(xt));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scripting/bsh/BshScriptUtils$BshObjectInvocationHandler.class */
    public static class BshObjectInvocationHandler implements InvocationHandler {
        private final XThis xt;

        public BshObjectInvocationHandler(XThis xt) {
            this.xt = xt;
        }

        @Override // java.lang.reflect.InvocationHandler
        @Nullable
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (ReflectionUtils.isEqualsMethod(method)) {
                return Boolean.valueOf(isProxyForSameBshObject(args[0]));
            }
            if (ReflectionUtils.isHashCodeMethod(method)) {
                return Integer.valueOf(this.xt.hashCode());
            }
            if (ReflectionUtils.isToStringMethod(method)) {
                return "BeanShell object [" + this.xt + "]";
            }
            try {
                Object result = this.xt.invokeMethod(method.getName(), args);
                if (result == Primitive.NULL || result == Primitive.VOID) {
                    return null;
                }
                if (result instanceof Primitive) {
                    Primitive primitive = (Primitive) result;
                    return primitive.getValue();
                }
                return result;
            } catch (EvalError ex) {
                throw new BshExecutionException(ex);
            }
        }

        private boolean isProxyForSameBshObject(Object obj) {
            if (!Proxy.isProxyClass(obj.getClass())) {
                return false;
            }
            InvocationHandler ih = Proxy.getInvocationHandler(obj);
            if (ih instanceof BshObjectInvocationHandler) {
                BshObjectInvocationHandler that = (BshObjectInvocationHandler) ih;
                if (this.xt.equals(that.xt)) {
                    return true;
                }
            }
            return false;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scripting/bsh/BshScriptUtils$BshExecutionException.class */
    public static final class BshExecutionException extends NestedRuntimeException {
        private BshExecutionException(EvalError ex) {
            super("BeanShell script execution failed", ex);
        }
    }
}
