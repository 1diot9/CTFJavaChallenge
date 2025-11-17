package org.springframework.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.jmx.export.naming.IdentityNamingStrategy;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ReflectionUtils.class */
public abstract class ReflectionUtils {
    private static final String CGLIB_RENAMED_METHOD_PREFIX = "CGLIB$";
    public static final MethodFilter USER_DECLARED_METHODS = method -> {
        return (method.isBridge() || method.isSynthetic() || method.getDeclaringClass() == Object.class) ? false : true;
    };
    public static final FieldFilter COPYABLE_FIELDS = field -> {
        return (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) ? false : true;
    };
    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
    private static final Method[] EMPTY_METHOD_ARRAY = new Method[0];
    private static final Field[] EMPTY_FIELD_ARRAY = new Field[0];
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final Map<Class<?>, Method[]> declaredMethodsCache = new ConcurrentReferenceHashMap(256);
    private static final Map<Class<?>, Field[]> declaredFieldsCache = new ConcurrentReferenceHashMap(256);

    @FunctionalInterface
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ReflectionUtils$FieldCallback.class */
    public interface FieldCallback {
        void doWith(Field field) throws IllegalArgumentException, IllegalAccessException;
    }

    @FunctionalInterface
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ReflectionUtils$MethodCallback.class */
    public interface MethodCallback {
        void doWith(Method method) throws IllegalArgumentException, IllegalAccessException;
    }

    public static void handleReflectionException(Exception ex) {
        if (ex instanceof NoSuchMethodException) {
            throw new IllegalStateException("Method not found: " + ex.getMessage());
        }
        if (ex instanceof IllegalAccessException) {
            throw new IllegalStateException("Could not access method or field: " + ex.getMessage());
        }
        if (ex instanceof InvocationTargetException) {
            InvocationTargetException invocationTargetException = (InvocationTargetException) ex;
            handleInvocationTargetException(invocationTargetException);
        }
        if (ex instanceof RuntimeException) {
            RuntimeException runtimeException = (RuntimeException) ex;
            throw runtimeException;
        }
        throw new UndeclaredThrowableException(ex);
    }

    public static void handleInvocationTargetException(InvocationTargetException ex) {
        rethrowRuntimeException(ex.getTargetException());
    }

    public static void rethrowRuntimeException(Throwable ex) {
        if (ex instanceof RuntimeException) {
            RuntimeException runtimeException = (RuntimeException) ex;
            throw runtimeException;
        }
        if (ex instanceof Error) {
            Error error = (Error) ex;
            throw error;
        }
        throw new UndeclaredThrowableException(ex);
    }

    public static void rethrowException(Throwable throwable) throws Exception {
        if (throwable instanceof Exception) {
            Exception exception = (Exception) throwable;
            throw exception;
        }
        if (throwable instanceof Error) {
            Error error = (Error) throwable;
            throw error;
        }
        throw new UndeclaredThrowableException(throwable);
    }

    public static <T> Constructor<T> accessibleConstructor(Class<T> clazz, Class<?>... parameterTypes) throws NoSuchMethodException {
        Constructor<T> ctor = clazz.getDeclaredConstructor(parameterTypes);
        makeAccessible((Constructor<?>) ctor);
        return ctor;
    }

    public static void makeAccessible(Constructor<?> ctor) {
        if ((!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) && !ctor.isAccessible()) {
            ctor.setAccessible(true);
        }
    }

    @Nullable
    public static Method findMethod(Class<?> clazz, String name) {
        return findMethod(clazz, name, EMPTY_CLASS_ARRAY);
    }

    @Nullable
    public static Method findMethod(Class<?> clazz, String name, @Nullable Class<?>... paramTypes) {
        int i;
        Method method;
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(name, "Method name must not be null");
        Class<?> cls = clazz;
        loop0: while (true) {
            Class<?> searchType = cls;
            if (searchType != null) {
                Method[] methods = searchType.isInterface() ? searchType.getMethods() : getDeclaredMethods(searchType, false);
                int length = methods.length;
                for (0; i < length; i + 1) {
                    method = methods[i];
                    i = (name.equals(method.getName()) && (paramTypes == null || hasSameParams(method, paramTypes))) ? 0 : i + 1;
                }
                cls = searchType.getSuperclass();
            } else {
                return null;
            }
        }
        return method;
    }

    private static boolean hasSameParams(Method method, Class<?>[] paramTypes) {
        return paramTypes.length == method.getParameterCount() && Arrays.equals(paramTypes, method.getParameterTypes());
    }

    @Nullable
    public static Object invokeMethod(Method method, @Nullable Object target) {
        return invokeMethod(method, target, EMPTY_OBJECT_ARRAY);
    }

    @Nullable
    public static Object invokeMethod(Method method, @Nullable Object target, @Nullable Object... args) {
        try {
            return method.invoke(target, args);
        } catch (Exception ex) {
            handleReflectionException(ex);
            throw new IllegalStateException("Should never get here");
        }
    }

    public static boolean declaresException(Method method, Class<?> exceptionType) {
        Assert.notNull(method, "Method must not be null");
        Class<?>[] declaredExceptions = method.getExceptionTypes();
        for (Class<?> declaredException : declaredExceptions) {
            if (declaredException.isAssignableFrom(exceptionType)) {
                return true;
            }
        }
        return false;
    }

    public static void doWithLocalMethods(Class<?> clazz, MethodCallback mc) {
        Method[] methods = getDeclaredMethods(clazz, false);
        for (Method method : methods) {
            try {
                mc.doWith(method);
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
            }
        }
    }

    public static void doWithMethods(Class<?> clazz, MethodCallback mc) {
        doWithMethods(clazz, mc, null);
    }

    public static void doWithMethods(Class<?> clazz, MethodCallback mc, @Nullable MethodFilter mf) {
        if (mf == USER_DECLARED_METHODS && clazz == Object.class) {
            return;
        }
        Method[] methods = getDeclaredMethods(clazz, false);
        for (Method method : methods) {
            if (mf == null || mf.matches(method)) {
                try {
                    mc.doWith(method);
                } catch (IllegalAccessException ex) {
                    throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
                }
            }
        }
        if (clazz.getSuperclass() != null && (mf != USER_DECLARED_METHODS || clazz.getSuperclass() != Object.class)) {
            doWithMethods(clazz.getSuperclass(), mc, mf);
            return;
        }
        if (clazz.isInterface()) {
            for (Class<?> superIfc : clazz.getInterfaces()) {
                doWithMethods(superIfc, mc, mf);
            }
        }
    }

    public static Method[] getAllDeclaredMethods(Class<?> leafClass) {
        List<Method> methods = new ArrayList<>(20);
        Objects.requireNonNull(methods);
        doWithMethods(leafClass, (v1) -> {
            r1.add(v1);
        });
        return (Method[]) methods.toArray(EMPTY_METHOD_ARRAY);
    }

    public static Method[] getUniqueDeclaredMethods(Class<?> leafClass) {
        return getUniqueDeclaredMethods(leafClass, null);
    }

    public static Method[] getUniqueDeclaredMethods(Class<?> leafClass, @Nullable MethodFilter mf) {
        List<Method> methods = new ArrayList<>(20);
        doWithMethods(leafClass, method -> {
            boolean knownSignature = false;
            Method methodBeingOverriddenWithCovariantReturnType = null;
            Iterator it = methods.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Method existingMethod = (Method) it.next();
                if (method.getName().equals(existingMethod.getName()) && method.getParameterCount() == existingMethod.getParameterCount() && Arrays.equals(method.getParameterTypes(), existingMethod.getParameterTypes())) {
                    if (existingMethod.getReturnType() != method.getReturnType() && existingMethod.getReturnType().isAssignableFrom(method.getReturnType())) {
                        methodBeingOverriddenWithCovariantReturnType = existingMethod;
                    } else {
                        knownSignature = true;
                    }
                }
            }
            if (methodBeingOverriddenWithCovariantReturnType != null) {
                methods.remove(methodBeingOverriddenWithCovariantReturnType);
            }
            if (!knownSignature && !isCglibRenamedMethod(method)) {
                methods.add(method);
            }
        }, mf);
        return (Method[]) methods.toArray(EMPTY_METHOD_ARRAY);
    }

    public static Method[] getDeclaredMethods(Class<?> clazz) {
        return getDeclaredMethods(clazz, true);
    }

    private static Method[] getDeclaredMethods(Class<?> clazz, boolean defensive) {
        Assert.notNull(clazz, "Class must not be null");
        Method[] result = declaredMethodsCache.get(clazz);
        if (result == null) {
            try {
                Method[] declaredMethods = clazz.getDeclaredMethods();
                List<Method> defaultMethods = findDefaultMethodsOnInterfaces(clazz);
                if (defaultMethods != null) {
                    result = new Method[declaredMethods.length + defaultMethods.size()];
                    System.arraycopy(declaredMethods, 0, result, 0, declaredMethods.length);
                    int index = declaredMethods.length;
                    for (Method defaultMethod : defaultMethods) {
                        result[index] = defaultMethod;
                        index++;
                    }
                } else {
                    result = declaredMethods;
                }
                declaredMethodsCache.put(clazz, result.length == 0 ? EMPTY_METHOD_ARRAY : result);
            } catch (Throwable ex) {
                throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() + "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
            }
        }
        return (result.length == 0 || !defensive) ? result : (Method[]) result.clone();
    }

    @Nullable
    private static List<Method> findDefaultMethodsOnInterfaces(Class<?> clazz) {
        List<Method> result = null;
        for (Class<?> ifc : clazz.getInterfaces()) {
            for (Method method : ifc.getMethods()) {
                if (method.isDefault()) {
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(method);
                }
            }
        }
        return result;
    }

    public static boolean isEqualsMethod(@Nullable Method method) {
        return method != null && method.getParameterCount() == 1 && method.getName().equals("equals") && method.getParameterTypes()[0] == Object.class;
    }

    public static boolean isHashCodeMethod(@Nullable Method method) {
        return method != null && method.getParameterCount() == 0 && method.getName().equals(IdentityNamingStrategy.HASH_CODE_KEY);
    }

    public static boolean isToStringMethod(@Nullable Method method) {
        return method != null && method.getParameterCount() == 0 && method.getName().equals("toString");
    }

    public static boolean isObjectMethod(@Nullable Method method) {
        return method != null && (method.getDeclaringClass() == Object.class || isEqualsMethod(method) || isHashCodeMethod(method) || isToStringMethod(method));
    }

    public static boolean isCglibRenamedMethod(Method renamedMethod) {
        String name = renamedMethod.getName();
        if (name.startsWith(CGLIB_RENAMED_METHOD_PREFIX)) {
            int i = name.length() - 1;
            while (i >= 0 && Character.isDigit(name.charAt(i))) {
                i--;
            }
            return i > CGLIB_RENAMED_METHOD_PREFIX.length() && i < name.length() - 1 && name.charAt(i) == '$';
        }
        return false;
    }

    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    @Nullable
    public static Field findField(Class<?> clazz, String name) {
        return findField(clazz, name, null);
    }

    /* JADX WARN: Code restructure failed: missing block: B:26:0x0067, code lost:            return r0;     */
    @org.springframework.lang.Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.lang.reflect.Field findField(java.lang.Class<?> r3, @org.springframework.lang.Nullable java.lang.String r4, @org.springframework.lang.Nullable java.lang.Class<?> r5) {
        /*
            r0 = r3
            java.lang.String r1 = "Class must not be null"
            org.springframework.util.Assert.notNull(r0, r1)
            r0 = r4
            if (r0 != 0) goto Le
            r0 = r5
            if (r0 == 0) goto L12
        Le:
            r0 = 1
            goto L13
        L12:
            r0 = 0
        L13:
            java.lang.String r1 = "Either name or type of the field must be specified"
            org.springframework.util.Assert.isTrue(r0, r1)
            r0 = r3
            r6 = r0
        L1b:
            java.lang.Class<java.lang.Object> r0 = java.lang.Object.class
            r1 = r6
            if (r0 == r1) goto L76
            r0 = r6
            if (r0 == 0) goto L76
            r0 = r6
            java.lang.reflect.Field[] r0 = getDeclaredFields(r0)
            r7 = r0
            r0 = r7
            r8 = r0
            r0 = r8
            int r0 = r0.length
            r9 = r0
            r0 = 0
            r10 = r0
        L37:
            r0 = r10
            r1 = r9
            if (r0 >= r1) goto L6e
            r0 = r8
            r1 = r10
            r0 = r0[r1]
            r11 = r0
            r0 = r4
            if (r0 == 0) goto L55
            r0 = r4
            r1 = r11
            java.lang.String r1 = r1.getName()
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L68
        L55:
            r0 = r5
            if (r0 == 0) goto L65
            r0 = r5
            r1 = r11
            java.lang.Class r1 = r1.getType()
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L68
        L65:
            r0 = r11
            return r0
        L68:
            int r10 = r10 + 1
            goto L37
        L6e:
            r0 = r6
            java.lang.Class r0 = r0.getSuperclass()
            r6 = r0
            goto L1b
        L76:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.util.ReflectionUtils.findField(java.lang.Class, java.lang.String, java.lang.Class):java.lang.reflect.Field");
    }

    @Nullable
    public static Field findFieldIgnoreCase(Class<?> clazz, String name) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(name, "Name must not be null");
        Class<?> cls = clazz;
        while (true) {
            Class<?> searchType = cls;
            if (Object.class != searchType && searchType != null) {
                Field[] fields = getDeclaredFields(searchType);
                for (Field field : fields) {
                    if (name.equalsIgnoreCase(field.getName())) {
                        return field;
                    }
                }
                cls = searchType.getSuperclass();
            } else {
                return null;
            }
        }
    }

    public static void setField(Field field, @Nullable Object target, @Nullable Object value) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException ex) {
            handleReflectionException(ex);
        }
    }

    @Nullable
    public static Object getField(Field field, @Nullable Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException ex) {
            handleReflectionException(ex);
            throw new IllegalStateException("Should never get here");
        }
    }

    public static void doWithLocalFields(Class<?> clazz, FieldCallback fc) {
        for (Field field : getDeclaredFields(clazz)) {
            try {
                fc.doWith(field);
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
            }
        }
    }

    public static void doWithFields(Class<?> clazz, FieldCallback fc) {
        doWithFields(clazz, fc, null);
    }

    public static void doWithFields(Class<?> clazz, FieldCallback fc, @Nullable FieldFilter ff) {
        Class<?> targetClass = clazz;
        do {
            Field[] fields = getDeclaredFields(targetClass);
            for (Field field : fields) {
                if (ff == null || ff.matches(field)) {
                    try {
                        fc.doWith(field);
                    } catch (IllegalAccessException ex) {
                        throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
                    }
                }
            }
            targetClass = targetClass.getSuperclass();
            if (targetClass == null) {
                return;
            }
        } while (targetClass != Object.class);
    }

    private static Field[] getDeclaredFields(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        Field[] result = declaredFieldsCache.get(clazz);
        if (result == null) {
            try {
                result = clazz.getDeclaredFields();
                declaredFieldsCache.put(clazz, result.length == 0 ? EMPTY_FIELD_ARRAY : result);
            } catch (Throwable ex) {
                throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() + "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
            }
        }
        return result;
    }

    public static void shallowCopyFieldState(final Object src, final Object dest) {
        Assert.notNull(src, "Source for field copy cannot be null");
        Assert.notNull(dest, "Destination for field copy cannot be null");
        if (!src.getClass().isAssignableFrom(dest.getClass())) {
            throw new IllegalArgumentException("Destination class [" + dest.getClass().getName() + "] must be same or subclass as source class [" + src.getClass().getName() + "]");
        }
        doWithFields(src.getClass(), field -> {
            makeAccessible(field);
            Object srcValue = field.get(src);
            field.set(dest, srcValue);
        }, COPYABLE_FIELDS);
    }

    public static boolean isPublicStaticFinal(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
    }

    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    public static void clearCache() {
        declaredMethodsCache.clear();
        declaredFieldsCache.clear();
    }

    @FunctionalInterface
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ReflectionUtils$MethodFilter.class */
    public interface MethodFilter {
        boolean matches(Method method);

        default MethodFilter and(MethodFilter next) {
            Assert.notNull(next, "Next MethodFilter must not be null");
            return method -> {
                return matches(method) && next.matches(method);
            };
        }
    }

    @FunctionalInterface
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ReflectionUtils$FieldFilter.class */
    public interface FieldFilter {
        boolean matches(Field field);

        default FieldFilter and(FieldFilter next) {
            Assert.notNull(next, "Next FieldFilter must not be null");
            return field -> {
                return matches(field) && next.matches(field);
            };
        }
    }
}
