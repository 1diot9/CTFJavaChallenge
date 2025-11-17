package org.springframework.beans;

import java.beans.ConstructorProperties;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.JvmInline;
import kotlin.reflect.KClass;
import kotlin.reflect.KFunction;
import kotlin.reflect.KParameter;
import kotlin.reflect.full.KAnnotatedElements;
import kotlin.reflect.full.KClasses;
import kotlin.reflect.jvm.KCallablesJvm;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/BeanUtils.class */
public abstract class BeanUtils {
    private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private static final Set<Class<?>> unknownEditorTypes = Collections.newSetFromMap(new ConcurrentReferenceHashMap(64));
    private static final Map<Class<?>, Object> DEFAULT_TYPE_VALUES = Map.of(Boolean.TYPE, false, Byte.TYPE, (byte) 0, Short.TYPE, (short) 0, Integer.TYPE, 0, Long.TYPE, 0L, Float.TYPE, Float.valueOf(0.0f), Double.TYPE, Double.valueOf(0.0d), Character.TYPE, (char) 0);

    @Deprecated
    public static <T> T instantiate(Class<T> clazz) throws BeanInstantiationException {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isInterface()) {
            throw new BeanInstantiationException(clazz, "Specified class is an interface");
        }
        try {
            return clazz.newInstance();
        } catch (IllegalAccessException ex) {
            throw new BeanInstantiationException((Class<?>) clazz, "Is the constructor accessible?", (Throwable) ex);
        } catch (InstantiationException ex2) {
            throw new BeanInstantiationException((Class<?>) clazz, "Is it an abstract class?", (Throwable) ex2);
        }
    }

    public static <T> T instantiateClass(Class<T> cls) throws BeanInstantiationException {
        Constructor<T> findPrimaryConstructor;
        Assert.notNull(cls, "Class must not be null");
        if (cls.isInterface()) {
            throw new BeanInstantiationException(cls, "Specified class is an interface");
        }
        try {
            findPrimaryConstructor = cls.getDeclaredConstructor(new Class[0]);
        } catch (LinkageError e) {
            throw new BeanInstantiationException((Class<?>) cls, "Unresolvable class definition", (Throwable) e);
        } catch (NoSuchMethodException e2) {
            findPrimaryConstructor = findPrimaryConstructor(cls);
            if (findPrimaryConstructor == null) {
                throw new BeanInstantiationException((Class<?>) cls, "No default constructor found", (Throwable) e2);
            }
        }
        return (T) instantiateClass(findPrimaryConstructor, new Object[0]);
    }

    public static <T> T instantiateClass(Class<?> cls, Class<T> cls2) throws BeanInstantiationException {
        Assert.isAssignable(cls2, cls);
        return (T) instantiateClass(cls);
    }

    public static <T> T instantiateClass(Constructor<T> constructor, Object... objArr) throws BeanInstantiationException {
        Assert.notNull(constructor, "Constructor must not be null");
        try {
            ReflectionUtils.makeAccessible((Constructor<?>) constructor);
            if (KotlinDetector.isKotlinReflectPresent() && KotlinDetector.isKotlinType(constructor.getDeclaringClass())) {
                return (T) KotlinDelegate.instantiateClass(constructor, objArr);
            }
            int parameterCount = constructor.getParameterCount();
            Assert.isTrue(objArr.length <= parameterCount, "Can't specify more arguments than constructor parameters");
            if (parameterCount == 0) {
                return constructor.newInstance(new Object[0]);
            }
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            Object[] objArr2 = new Object[objArr.length];
            for (int i = 0; i < objArr.length; i++) {
                if (objArr[i] == null) {
                    Class<?> cls = parameterTypes[i];
                    objArr2[i] = cls.isPrimitive() ? DEFAULT_TYPE_VALUES.get(cls) : null;
                } else {
                    objArr2[i] = objArr[i];
                }
            }
            return constructor.newInstance(objArr2);
        } catch (IllegalAccessException e) {
            throw new BeanInstantiationException((Constructor<?>) constructor, "Is the constructor accessible?", (Throwable) e);
        } catch (IllegalArgumentException e2) {
            throw new BeanInstantiationException((Constructor<?>) constructor, "Illegal arguments for constructor", (Throwable) e2);
        } catch (InstantiationException e3) {
            throw new BeanInstantiationException((Constructor<?>) constructor, "Is it an abstract class?", (Throwable) e3);
        } catch (InvocationTargetException e4) {
            throw new BeanInstantiationException((Constructor<?>) constructor, "Constructor threw exception", e4.getTargetException());
        }
    }

    public static <T> Constructor<T> getResolvableConstructor(Class<T> cls) {
        Constructor<T> findPrimaryConstructor = findPrimaryConstructor(cls);
        if (findPrimaryConstructor != null) {
            return findPrimaryConstructor;
        }
        Object[] constructors = cls.getConstructors();
        if (constructors.length == 1) {
            return (Constructor<T>) constructors[0];
        }
        if (constructors.length == 0) {
            Object[] declaredConstructors = cls.getDeclaredConstructors();
            if (declaredConstructors.length == 1) {
                return (Constructor<T>) declaredConstructors[0];
            }
        }
        try {
            return cls.getDeclaredConstructor(new Class[0]);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("No primary or single unique constructor found for " + cls);
        }
    }

    @Nullable
    public static <T> Constructor<T> findPrimaryConstructor(Class<T> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        if (KotlinDetector.isKotlinReflectPresent() && KotlinDetector.isKotlinType(clazz)) {
            return KotlinDelegate.findPrimaryConstructor(clazz);
        }
        return null;
    }

    @Nullable
    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            return findDeclaredMethod(clazz, methodName, paramTypes);
        }
    }

    @Nullable
    public static Method findDeclaredMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null) {
                return findDeclaredMethod(clazz.getSuperclass(), methodName, paramTypes);
            }
            return null;
        }
    }

    @Nullable
    public static Method findMethodWithMinimalParameters(Class<?> clazz, String methodName) throws IllegalArgumentException {
        Method targetMethod = findMethodWithMinimalParameters(clazz.getMethods(), methodName);
        if (targetMethod == null) {
            targetMethod = findDeclaredMethodWithMinimalParameters(clazz, methodName);
        }
        return targetMethod;
    }

    @Nullable
    public static Method findDeclaredMethodWithMinimalParameters(Class<?> clazz, String methodName) throws IllegalArgumentException {
        Method targetMethod = findMethodWithMinimalParameters(clazz.getDeclaredMethods(), methodName);
        if (targetMethod == null && clazz.getSuperclass() != null) {
            targetMethod = findDeclaredMethodWithMinimalParameters(clazz.getSuperclass(), methodName);
        }
        return targetMethod;
    }

    @Nullable
    public static Method findMethodWithMinimalParameters(Method[] methods, String methodName) throws IllegalArgumentException {
        Method targetMethod = null;
        int numMethodsFoundWithCurrentMinimumArgs = 0;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                int numParams = method.getParameterCount();
                if (targetMethod == null || numParams < targetMethod.getParameterCount()) {
                    targetMethod = method;
                    numMethodsFoundWithCurrentMinimumArgs = 1;
                } else if (!method.isBridge() && targetMethod.getParameterCount() == numParams) {
                    if (targetMethod.isBridge()) {
                        targetMethod = method;
                    } else {
                        numMethodsFoundWithCurrentMinimumArgs++;
                    }
                }
            }
        }
        if (numMethodsFoundWithCurrentMinimumArgs > 1) {
            throw new IllegalArgumentException("Cannot resolve method '" + methodName + "' to a unique method. Attempted to resolve to overloaded method with the least number of parameters but there were " + numMethodsFoundWithCurrentMinimumArgs + " candidates.");
        }
        return targetMethod;
    }

    @Nullable
    public static Method resolveSignature(String signature, Class<?> clazz) {
        Assert.hasText(signature, "'signature' must not be empty");
        Assert.notNull(clazz, "Class must not be null");
        int startParen = signature.indexOf(40);
        int endParen = signature.indexOf(41);
        if (startParen > -1 && endParen == -1) {
            throw new IllegalArgumentException("Invalid method signature '" + signature + "': expected closing ')' for args list");
        }
        if (startParen == -1 && endParen > -1) {
            throw new IllegalArgumentException("Invalid method signature '" + signature + "': expected opening '(' for args list");
        }
        if (startParen == -1) {
            return findMethodWithMinimalParameters(clazz, signature);
        }
        String methodName = signature.substring(0, startParen);
        String[] parameterTypeNames = StringUtils.commaDelimitedListToStringArray(signature.substring(startParen + 1, endParen));
        Class<?>[] parameterTypes = new Class[parameterTypeNames.length];
        for (int i = 0; i < parameterTypeNames.length; i++) {
            String parameterTypeName = parameterTypeNames[i].trim();
            try {
                parameterTypes[i] = ClassUtils.forName(parameterTypeName, clazz.getClassLoader());
            } catch (Throwable ex) {
                throw new IllegalArgumentException("Invalid method signature: unable to resolve type [" + parameterTypeName + "] for argument " + i + ". Root cause: " + ex);
            }
        }
        return findMethod(clazz, methodName, parameterTypes);
    }

    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) throws BeansException {
        return CachedIntrospectionResults.forClass(clazz).getPropertyDescriptors();
    }

    @Nullable
    public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String propertyName) throws BeansException {
        return CachedIntrospectionResults.forClass(clazz).getPropertyDescriptor(propertyName);
    }

    @Nullable
    public static PropertyDescriptor findPropertyForMethod(Method method) throws BeansException {
        return findPropertyForMethod(method, method.getDeclaringClass());
    }

    @Nullable
    public static PropertyDescriptor findPropertyForMethod(Method method, Class<?> clazz) throws BeansException {
        Assert.notNull(method, "Method must not be null");
        PropertyDescriptor[] pds = getPropertyDescriptors(clazz);
        for (PropertyDescriptor pd : pds) {
            if (method.equals(pd.getReadMethod()) || method.equals(pd.getWriteMethod())) {
                return pd;
            }
        }
        return null;
    }

    @Nullable
    public static PropertyEditor findEditorByConvention(@Nullable Class<?> targetType) {
        if (targetType == null || targetType.isArray() || unknownEditorTypes.contains(targetType)) {
            return null;
        }
        ClassLoader cl = targetType.getClassLoader();
        if (cl == null) {
            try {
                cl = ClassLoader.getSystemClassLoader();
                if (cl == null) {
                    return null;
                }
            } catch (Throwable th) {
                return null;
            }
        }
        String targetTypeName = targetType.getName();
        String editorName = targetTypeName + "Editor";
        try {
            Class<?> editorClass = cl.loadClass(editorName);
            if (editorClass != null) {
                if (!PropertyEditor.class.isAssignableFrom(editorClass)) {
                    unknownEditorTypes.add(targetType);
                    return null;
                }
                return (PropertyEditor) instantiateClass(editorClass);
            }
        } catch (ClassNotFoundException e) {
        }
        unknownEditorTypes.add(targetType);
        return null;
    }

    public static Class<?> findPropertyType(String propertyName, @Nullable Class<?>... beanClasses) {
        if (beanClasses != null) {
            for (Class<?> beanClass : beanClasses) {
                PropertyDescriptor pd = getPropertyDescriptor(beanClass, propertyName);
                if (pd != null) {
                    return pd.getPropertyType();
                }
            }
            return Object.class;
        }
        return Object.class;
    }

    public static MethodParameter getWriteMethodParameter(PropertyDescriptor pd) {
        if (pd instanceof GenericTypeAwarePropertyDescriptor) {
            GenericTypeAwarePropertyDescriptor gpd = (GenericTypeAwarePropertyDescriptor) pd;
            return new MethodParameter(gpd.getWriteMethodParameter());
        }
        Method writeMethod = pd.getWriteMethod();
        Assert.state(writeMethod != null, "No write method available");
        return new MethodParameter(writeMethod, 0);
    }

    public static String[] getParameterNames(Constructor<?> ctor) {
        ConstructorProperties cp = ctor.getAnnotation(ConstructorProperties.class);
        String[] paramNames = cp != null ? cp.value() : parameterNameDiscoverer.getParameterNames(ctor);
        Assert.state(paramNames != null, (Supplier<String>) () -> {
            return "Cannot resolve parameter names for constructor " + ctor;
        });
        Assert.state(paramNames.length == ctor.getParameterCount(), (Supplier<String>) () -> {
            return "Invalid number of parameter names: " + paramNames.length + " for constructor " + ctor;
        });
        return paramNames;
    }

    public static boolean isSimpleProperty(Class<?> type) {
        Assert.notNull(type, "'type' must not be null");
        return isSimpleValueType(type) || (type.isArray() && isSimpleValueType(type.componentType()));
    }

    public static boolean isSimpleValueType(Class<?> type) {
        return ClassUtils.isSimpleValueType(type);
    }

    public static void copyProperties(Object source, Object target) throws BeansException {
        copyProperties(source, target, null, (String[]) null);
    }

    public static void copyProperties(Object source, Object target, Class<?> editable) throws BeansException {
        copyProperties(source, target, editable, (String[]) null);
    }

    public static void copyProperties(Object source, Object target, String... ignoreProperties) throws BeansException {
        copyProperties(source, target, null, ignoreProperties);
    }

    private static void copyProperties(Object source, Object target, @Nullable Class<?> editable, @Nullable String... ignoreProperties) throws BeansException {
        Method readMethod;
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");
        Class<?> actualEditable = target.getClass();
        if (editable != null) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class [" + target.getClass().getName() + "] not assignable to editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        Set<String> ignoredProps = ignoreProperties != null ? new HashSet<>(Arrays.asList(ignoreProperties)) : null;
        CachedIntrospectionResults sourceResults = actualEditable != source.getClass() ? CachedIntrospectionResults.forClass(source.getClass()) : null;
        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoredProps == null || !ignoredProps.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = sourceResults != null ? sourceResults.getPropertyDescriptor(targetPd.getName()) : targetPd;
                if (sourcePd != null && (readMethod = sourcePd.getReadMethod()) != null && isAssignable(writeMethod, readMethod, sourcePd, targetPd)) {
                    try {
                        ReflectionUtils.makeAccessible(readMethod);
                        Object value = readMethod.invoke(source, new Object[0]);
                        ReflectionUtils.makeAccessible(writeMethod);
                        writeMethod.invoke(target, value);
                    } catch (Throwable ex) {
                        throw new FatalBeanException("Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                    }
                }
            }
        }
    }

    private static boolean isAssignable(Method writeMethod, Method readMethod, PropertyDescriptor sourcePd, PropertyDescriptor targetPd) {
        Type paramType = writeMethod.getGenericParameterTypes()[0];
        if (paramType instanceof Class) {
            Class<?> clazz = (Class) paramType;
            return ClassUtils.isAssignable(clazz, readMethod.getReturnType());
        }
        if (paramType.equals(readMethod.getGenericReturnType())) {
            return true;
        }
        ResolvableType sourceType = ((GenericTypeAwarePropertyDescriptor) sourcePd).getReadMethodType();
        ResolvableType targetType = ((GenericTypeAwarePropertyDescriptor) targetPd).getWriteMethodType();
        if (sourceType.hasUnresolvableGenerics() || targetType.hasUnresolvableGenerics()) {
            return ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType());
        }
        return targetType.isAssignableFrom(sourceType);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/BeanUtils$KotlinDelegate.class */
    public static class KotlinDelegate {
        private KotlinDelegate() {
        }

        @Nullable
        public static <T> Constructor<T> findPrimaryConstructor(Class<T> cls) {
            try {
                KClass kotlinClass = JvmClassMappingKt.getKotlinClass(cls);
                KFunction primaryConstructor = KClasses.getPrimaryConstructor(kotlinClass);
                if (primaryConstructor == null) {
                    return null;
                }
                if (kotlinClass.isValue() && !KAnnotatedElements.findAnnotations(kotlinClass, JvmClassMappingKt.getKotlinClass(JvmInline.class)).isEmpty()) {
                    Object[] declaredConstructors = cls.getDeclaredConstructors();
                    Assert.state(declaredConstructors.length == 1, "Kotlin value classes annotated with @JvmInline are expected to have a single JVM constructor");
                    return (Constructor<T>) declaredConstructors[0];
                }
                Constructor<T> javaConstructor = ReflectJvmMapping.getJavaConstructor(primaryConstructor);
                if (javaConstructor == null) {
                    throw new IllegalStateException("Failed to find Java constructor for Kotlin primary constructor: " + cls.getName());
                }
                return javaConstructor;
            } catch (UnsupportedOperationException e) {
                return null;
            }
        }

        public static <T> T instantiateClass(Constructor<T> constructor, Object... objArr) throws IllegalAccessException, InvocationTargetException, InstantiationException {
            KFunction kotlinFunction = ReflectJvmMapping.getKotlinFunction(constructor);
            if (kotlinFunction == null) {
                return constructor.newInstance(objArr);
            }
            if (!Modifier.isPublic(constructor.getModifiers()) || !Modifier.isPublic(constructor.getDeclaringClass().getModifiers())) {
                KCallablesJvm.setAccessible(kotlinFunction, true);
            }
            List parameters = kotlinFunction.getParameters();
            Assert.isTrue(objArr.length <= parameters.size(), "Number of provided arguments must be less than or equal to the number of constructor parameters");
            if (parameters.isEmpty()) {
                return (T) kotlinFunction.call(new Object[0]);
            }
            HashMap newHashMap = CollectionUtils.newHashMap(parameters.size());
            for (int i = 0; i < objArr.length; i++) {
                if (!((KParameter) parameters.get(i)).isOptional() || objArr[i] != null) {
                    newHashMap.put((KParameter) parameters.get(i), objArr[i]);
                }
            }
            return (T) kotlinFunction.callBy(newHashMap);
        }
    }
}
