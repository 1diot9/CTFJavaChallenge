package cn.hutool.core.lang.reflect;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/reflect/MethodHandleUtil.class */
public class MethodHandleUtil {
    public static MethodHandles.Lookup lookup(Class<?> callerClass) {
        return LookupFactory.lookup(callerClass);
    }

    public static MethodHandle findMethod(Class<?> callerClass, String name, MethodType type) {
        if (StrUtil.isBlank(name)) {
            return findConstructor(callerClass, type);
        }
        MethodHandle handle = null;
        MethodHandles.Lookup lookup = lookup(callerClass);
        try {
            handle = lookup.findVirtual(callerClass, name, type);
        } catch (IllegalAccessException | NoSuchMethodException e) {
        }
        if (null == handle) {
            try {
                handle = lookup.findStatic(callerClass, name, type);
            } catch (IllegalAccessException | NoSuchMethodException e2) {
            }
        }
        if (null == handle) {
            try {
                handle = lookup.findSpecial(callerClass, name, type, callerClass);
            } catch (IllegalAccessException e3) {
                throw new UtilException(e3);
            } catch (NoSuchMethodException e4) {
            }
        }
        return handle;
    }

    public static MethodHandle findConstructor(Class<?> callerClass, Class<?>... args) {
        return findConstructor(callerClass, MethodType.methodType((Class<?>) Void.TYPE, args));
    }

    public static MethodHandle findConstructor(Class<?> callerClass, MethodType type) {
        MethodHandles.Lookup lookup = lookup(callerClass);
        try {
            return lookup.findConstructor(callerClass, type);
        } catch (IllegalAccessException e) {
            throw new UtilException(e);
        } catch (NoSuchMethodException e2) {
            return null;
        }
    }

    public static <T> T invokeSpecial(Object obj, String str, Object... objArr) {
        Assert.notNull(obj, "Object to get method must be not null!", new Object[0]);
        Assert.notBlank(str, "Method name must be not blank!", new Object[0]);
        Method methodOfObj = ReflectUtil.getMethodOfObj(obj, str, objArr);
        if (null == methodOfObj) {
            throw new UtilException("No such method: [{}] from [{}]", str, obj.getClass());
        }
        return (T) invokeSpecial(obj, methodOfObj, objArr);
    }

    public static <T> T invoke(Object obj, Method method, Object... objArr) {
        return (T) invoke(false, obj, method, objArr);
    }

    public static <T> T invokeSpecial(Object obj, Method method, Object... objArr) {
        return (T) invoke(true, obj, method, objArr);
    }

    public static <T> T invoke(boolean z, Object obj, Method method, Object... objArr) {
        Assert.notNull(method, "Method must be not null!", new Object[0]);
        Class<?> declaringClass = method.getDeclaringClass();
        MethodHandles.Lookup lookup = lookup(declaringClass);
        try {
            MethodHandle unreflectSpecial = z ? lookup.unreflectSpecial(method, declaringClass) : lookup.unreflect(method);
            if (null != obj) {
                unreflectSpecial = unreflectSpecial.bindTo(obj);
            }
            return (T) unreflectSpecial.invokeWithArguments(objArr);
        } catch (Throwable th) {
            throw new UtilException(th);
        }
    }
}
