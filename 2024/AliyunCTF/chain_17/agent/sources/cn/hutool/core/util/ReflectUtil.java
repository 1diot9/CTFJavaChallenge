package cn.hutool.core.util;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import cn.hutool.core.annotation.Alias;
import cn.hutool.core.bean.NullWrapperBean;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.UniqueKeySet;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.InvocationTargetRuntimeException;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.reflect.MethodHandleUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.map.WeakConcurrentMap;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.springframework.jmx.export.naming.IdentityNamingStrategy;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/util/ReflectUtil.class */
public class ReflectUtil {
    private static final WeakConcurrentMap<Class<?>, Constructor<?>[]> CONSTRUCTORS_CACHE = new WeakConcurrentMap<>();
    private static final WeakConcurrentMap<Class<?>, Field[]> FIELDS_CACHE = new WeakConcurrentMap<>();
    private static final WeakConcurrentMap<Class<?>, Method[]> METHODS_CACHE = new WeakConcurrentMap<>();

    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case -1055735361:
                if (implMethodName.equals("lambda$getMethods$ea73458f$1")) {
                    z = 2;
                    break;
                }
                break;
            case -515407122:
                if (implMethodName.equals("lambda$getFields$54eedd5e$1")) {
                    z = false;
                    break;
                }
                break;
            case 1527653860:
                if (implMethodName.equals("lambda$getConstructors$8f84531$1")) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/util/ReflectUtil") && lambda.getImplMethodSignature().equals("(Ljava/lang/Class;)[Ljava/lang/reflect/Field;")) {
                    Class cls = (Class) lambda.getCapturedArg(0);
                    return () -> {
                        return getFieldsDirectly(cls, true);
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/util/ReflectUtil") && lambda.getImplMethodSignature().equals("(Ljava/lang/Class;)[Ljava/lang/reflect/Constructor;")) {
                    Class cls2 = (Class) lambda.getCapturedArg(0);
                    return () -> {
                        return getConstructorsDirectly(cls2);
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/util/ReflectUtil") && lambda.getImplMethodSignature().equals("(Ljava/lang/Class;)[Ljava/lang/reflect/Method;")) {
                    Class cls3 = (Class) lambda.getCapturedArg(0);
                    return () -> {
                        return getMethodsDirectly(cls3, true, true);
                    };
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameterTypes) {
        if (null == clazz) {
            return null;
        }
        for (Constructor<T> constructor : getConstructors(clazz)) {
            Class<?>[] pts = constructor.getParameterTypes();
            if (ClassUtil.isAllAssignableFrom(pts, parameterTypes)) {
                setAccessible(constructor);
                return constructor;
            }
        }
        return null;
    }

    public static <T> Constructor<T>[] getConstructors(Class<T> beanClass) throws SecurityException {
        Assert.notNull(beanClass);
        return CONSTRUCTORS_CACHE.computeIfAbsent((WeakConcurrentMap<Class<?>, Constructor<?>[]>) beanClass, () -> {
            return getConstructorsDirectly(beanClass);
        });
    }

    public static Constructor<?>[] getConstructorsDirectly(Class<?> beanClass) throws SecurityException {
        return beanClass.getDeclaredConstructors();
    }

    public static boolean hasField(Class<?> beanClass, String name) throws SecurityException {
        return null != getField(beanClass, name);
    }

    public static String getFieldName(Field field) {
        if (null == field) {
            return null;
        }
        Alias alias = (Alias) field.getAnnotation(Alias.class);
        if (null != alias) {
            return alias.value();
        }
        return field.getName();
    }

    public static Field getField(Class<?> beanClass, String name) throws SecurityException {
        Field[] fields = getFields(beanClass);
        return (Field) ArrayUtil.firstMatch(field -> {
            return name.equals(getFieldName(field));
        }, fields);
    }

    public static Map<String, Field> getFieldMap(Class<?> beanClass) {
        Field[] fields = getFields(beanClass);
        HashMap<String, Field> map = MapUtil.newHashMap(fields.length, true);
        for (Field field : fields) {
            map.put(field.getName(), field);
        }
        return map;
    }

    public static Field[] getFields(Class<?> beanClass) throws SecurityException {
        Assert.notNull(beanClass);
        return FIELDS_CACHE.computeIfAbsent((WeakConcurrentMap<Class<?>, Field[]>) beanClass, () -> {
            return getFieldsDirectly(beanClass, true);
        });
    }

    public static Field[] getFields(Class<?> beanClass, Filter<Field> fieldFilter) throws SecurityException {
        return (Field[]) ArrayUtil.filter(getFields(beanClass), fieldFilter);
    }

    public static Field[] getFieldsDirectly(Class<?> beanClass, boolean withSuperClassFields) throws SecurityException {
        Assert.notNull(beanClass);
        Field[] allFields = null;
        Class<?> cls = beanClass;
        while (true) {
            Class<?> searchType = cls;
            if (searchType != null) {
                Field[] declaredFields = searchType.getDeclaredFields();
                if (null == allFields) {
                    allFields = declaredFields;
                } else {
                    allFields = (Field[]) ArrayUtil.append((Object[]) allFields, (Object[]) declaredFields);
                }
                cls = withSuperClassFields ? searchType.getSuperclass() : null;
            } else {
                return allFields;
            }
        }
    }

    public static Object getFieldValue(Object obj, String fieldName) throws UtilException {
        if (null == obj || StrUtil.isBlank(fieldName)) {
            return null;
        }
        return getFieldValue(obj, getField(obj instanceof Class ? (Class) obj : obj.getClass(), fieldName));
    }

    public static Object getStaticFieldValue(Field field) throws UtilException {
        return getFieldValue((Object) null, field);
    }

    public static Object getFieldValue(Object obj, Field field) throws UtilException {
        if (null == field) {
            return null;
        }
        if (obj instanceof Class) {
            obj = null;
        }
        setAccessible(field);
        try {
            Object result = field.get(obj);
            return result;
        } catch (IllegalAccessException e) {
            throw new UtilException(e, "IllegalAccess for {}.{}", field.getDeclaringClass(), field.getName());
        }
    }

    public static Object[] getFieldsValue(Object obj) {
        if (null != obj) {
            Field[] fields = getFields(obj instanceof Class ? (Class) obj : obj.getClass());
            if (null != fields) {
                Object[] values = new Object[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    values[i] = getFieldValue(obj, fields[i]);
                }
                return values;
            }
            return null;
        }
        return null;
    }

    public static void setFieldValue(Object obj, String fieldName, Object value) throws UtilException {
        Assert.notNull(obj);
        Assert.notBlank(fieldName);
        Field field = getField(obj instanceof Class ? (Class) obj : obj.getClass(), fieldName);
        Assert.notNull(field, "Field [{}] is not exist in [{}]", fieldName, obj.getClass().getName());
        setFieldValue(obj, field, value);
    }

    public static void setFieldValue(Object obj, Field field, Object value) throws UtilException {
        Object targetValue;
        Assert.notNull(field, "Field in [{}] not exist !", obj);
        Class<?> fieldType = field.getType();
        if (null != value) {
            if (false == fieldType.isAssignableFrom(value.getClass()) && null != (targetValue = Convert.convert((Class<Object>) fieldType, value))) {
                value = targetValue;
            }
        } else {
            value = ClassUtil.getDefaultValue(fieldType);
        }
        setAccessible(field);
        try {
            field.set(obj instanceof Class ? null : obj, value);
        } catch (IllegalAccessException e) {
            throw new UtilException(e, "IllegalAccess for {}.{}", obj, field.getName());
        }
    }

    public static boolean isOuterClassField(Field field) {
        return "this$0".equals(field.getName());
    }

    public static Set<String> getPublicMethodNames(Class<?> clazz) {
        HashSet<String> methodSet = new HashSet<>();
        Method[] methodArray = getPublicMethods(clazz);
        if (ArrayUtil.isNotEmpty((Object[]) methodArray)) {
            for (Method method : methodArray) {
                methodSet.add(method.getName());
            }
        }
        return methodSet;
    }

    public static Method[] getPublicMethods(Class<?> clazz) {
        if (null == clazz) {
            return null;
        }
        return clazz.getMethods();
    }

    public static List<Method> getPublicMethods(Class<?> clazz, Filter<Method> filter) {
        List<Method> methodList;
        if (null == clazz) {
            return null;
        }
        Method[] methods = getPublicMethods(clazz);
        if (null != filter) {
            methodList = new ArrayList<>();
            for (Method method : methods) {
                if (filter.accept(method)) {
                    methodList.add(method);
                }
            }
        } else {
            methodList = CollUtil.newArrayList(methods);
        }
        return methodList;
    }

    public static List<Method> getPublicMethods(Class<?> clazz, Method... excludeMethods) {
        HashSet<Method> excludeMethodSet = CollUtil.newHashSet(excludeMethods);
        return getPublicMethods(clazz, (Filter<Method>) method -> {
            return false == excludeMethodSet.contains(method);
        });
    }

    public static List<Method> getPublicMethods(Class<?> clazz, String... excludeMethodNames) {
        HashSet<String> excludeMethodNameSet = CollUtil.newHashSet(excludeMethodNames);
        return getPublicMethods(clazz, (Filter<Method>) method -> {
            return false == excludeMethodNameSet.contains(method.getName());
        });
    }

    public static Method getPublicMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static Method getMethodOfObj(Object obj, String methodName, Object... args) throws SecurityException {
        if (null == obj || StrUtil.isBlank(methodName)) {
            return null;
        }
        return getMethod(obj.getClass(), methodName, ClassUtil.getClasses(args));
    }

    public static Method getMethodIgnoreCase(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
        return getMethod(clazz, true, methodName, paramTypes);
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
        return getMethod(clazz, false, methodName, paramTypes);
    }

    public static Method getMethod(Class<?> clazz, boolean ignoreCase, String methodName, Class<?>... paramTypes) throws SecurityException {
        if (null == clazz || StrUtil.isBlank(methodName)) {
            return null;
        }
        Method res = null;
        Method[] methods = getMethods(clazz);
        if (ArrayUtil.isNotEmpty((Object[]) methods)) {
            for (Method method : methods) {
                if (StrUtil.equals(methodName, method.getName(), ignoreCase) && ClassUtil.isAllAssignableFrom(method.getParameterTypes(), paramTypes) && (res == null || res.getReturnType().isAssignableFrom(method.getReturnType()))) {
                    res = method;
                }
            }
        }
        return res;
    }

    public static Method getMethodByName(Class<?> clazz, String methodName) throws SecurityException {
        return getMethodByName(clazz, false, methodName);
    }

    public static Method getMethodByNameIgnoreCase(Class<?> clazz, String methodName) throws SecurityException {
        return getMethodByName(clazz, true, methodName);
    }

    public static Method getMethodByName(Class<?> clazz, boolean ignoreCase, String methodName) throws SecurityException {
        if (null == clazz || StrUtil.isBlank(methodName)) {
            return null;
        }
        Method res = null;
        Method[] methods = getMethods(clazz);
        if (ArrayUtil.isNotEmpty((Object[]) methods)) {
            for (Method method : methods) {
                if (StrUtil.equals(methodName, method.getName(), ignoreCase) && (res == null || res.getReturnType().isAssignableFrom(method.getReturnType()))) {
                    res = method;
                }
            }
        }
        return res;
    }

    public static Set<String> getMethodNames(Class<?> clazz) throws SecurityException {
        HashSet<String> methodSet = new HashSet<>();
        Method[] methods = getMethods(clazz);
        for (Method method : methods) {
            methodSet.add(method.getName());
        }
        return methodSet;
    }

    public static Method[] getMethods(Class<?> clazz, Filter<Method> filter) throws SecurityException {
        if (null == clazz) {
            return null;
        }
        return (Method[]) ArrayUtil.filter(getMethods(clazz), filter);
    }

    public static Method[] getMethods(Class<?> beanClass) throws SecurityException {
        Assert.notNull(beanClass);
        return METHODS_CACHE.computeIfAbsent((WeakConcurrentMap<Class<?>, Method[]>) beanClass, () -> {
            return getMethodsDirectly(beanClass, true, true);
        });
    }

    public static Method[] getMethodsDirectly(Class<?> beanClass, boolean withSupers, boolean withMethodFromObject) throws SecurityException {
        Assert.notNull(beanClass);
        if (beanClass.isInterface()) {
            return withSupers ? beanClass.getMethods() : beanClass.getDeclaredMethods();
        }
        UniqueKeySet<String, Method> result = new UniqueKeySet<>(true, (Function<Method, String>) ReflectUtil::getUniqueKey);
        Class<?> cls = beanClass;
        while (true) {
            Class<?> searchType = cls;
            if (searchType == null || (false == withMethodFromObject && Object.class == searchType)) {
                break;
            }
            result.addAllIfAbsent(Arrays.asList(searchType.getDeclaredMethods()));
            result.addAllIfAbsent(getDefaultMethodsFromInterface(searchType));
            cls = (withSupers && false == searchType.isInterface()) ? searchType.getSuperclass() : null;
        }
        return (Method[]) result.toArray(new Method[0]);
    }

    public static boolean isEqualsMethod(Method method) {
        return method != null && 1 == method.getParameterCount() && false != "equals".equals(method.getName()) && method.getParameterTypes()[0] == Object.class;
    }

    public static boolean isHashCodeMethod(Method method) {
        return method != null && IdentityNamingStrategy.HASH_CODE_KEY.equals(method.getName()) && isEmptyParam(method);
    }

    public static boolean isToStringMethod(Method method) {
        return method != null && "toString".equals(method.getName()) && isEmptyParam(method);
    }

    public static boolean isEmptyParam(Method method) {
        return method.getParameterCount() == 0;
    }

    public static boolean isGetterOrSetterIgnoreCase(Method method) {
        return isGetterOrSetter(method, true);
    }

    public static boolean isGetterOrSetter(Method method, boolean ignoreCase) {
        int parameterCount;
        if (null == method || (parameterCount = method.getParameterCount()) > 1) {
            return false;
        }
        String name = method.getName();
        if ("getClass".equals(name)) {
            return false;
        }
        if (ignoreCase) {
            name = name.toLowerCase();
        }
        switch (parameterCount) {
            case 0:
                return name.startsWith(BeanUtil.PREFIX_GETTER_GET) || name.startsWith(BeanUtil.PREFIX_GETTER_IS);
            case 1:
                return name.startsWith("set");
            default:
                return false;
        }
    }

    public static <T> T newInstance(String str) throws UtilException {
        try {
            return (T) Class.forName(str).newInstance();
        } catch (Exception e) {
            throw new UtilException(e, "Instance class [{}] error!", str);
        }
    }

    public static <T> T newInstance(Class<T> clazz, Object... params) throws UtilException {
        if (ArrayUtil.isEmpty(params)) {
            Constructor<T> constructor = getConstructor(clazz, new Class[0]);
            if (null == constructor) {
                throw new UtilException("No constructor for [{}]", clazz);
            }
            try {
                return constructor.newInstance(new Object[0]);
            } catch (Exception e) {
                throw new UtilException(e, "Instance class [{}] error!", clazz);
            }
        }
        Class<?>[] paramTypes = ClassUtil.getClasses(params);
        Constructor<T> constructor2 = getConstructor(clazz, paramTypes);
        if (null == constructor2) {
            throw new UtilException("No Constructor matched for parameter types: [{}]", paramTypes);
        }
        try {
            return constructor2.newInstance(params);
        } catch (Exception e2) {
            throw new UtilException(e2, "Instance class [{}] error!", clazz);
        }
    }

    /* JADX WARN: Type inference failed for: r0v32, types: [T, java.lang.Object] */
    public static <T> T newInstanceIfPossible(Class<T> cls) {
        Assert.notNull(cls);
        if (cls.isPrimitive()) {
            return (T) ClassUtil.getPrimitiveDefaultValue(cls);
        }
        if (cls.isAssignableFrom(AbstractMap.class)) {
            cls = HashMap.class;
        } else if (cls.isAssignableFrom(List.class)) {
            cls = ArrayList.class;
        } else if (cls.isAssignableFrom(Set.class)) {
            cls = HashSet.class;
        }
        try {
            return (T) newInstance(cls, new Object[0]);
        } catch (Exception e) {
            if (cls.isEnum()) {
                return cls.getEnumConstants()[0];
            }
            if (cls.isArray()) {
                return (T) Array.newInstance(cls.getComponentType(), 0);
            }
            for (Constructor constructor : getConstructors(cls)) {
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                if (0 != parameterTypes.length) {
                    setAccessible(constructor);
                    try {
                        return constructor.newInstance(ClassUtil.getDefaultValues(parameterTypes));
                    } catch (Exception e2) {
                    }
                }
            }
            return null;
        }
    }

    public static <T> T invokeStatic(Method method, Object... objArr) throws UtilException {
        return (T) invoke((Object) null, method, objArr);
    }

    public static <T> T invokeWithCheck(Object obj, Method method, Object... objArr) throws UtilException {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (null != objArr) {
            Assert.isTrue(objArr.length == parameterTypes.length, "Params length [{}] is not fit for param length [{}] of method !", Integer.valueOf(objArr.length), Integer.valueOf(parameterTypes.length));
            for (int i = 0; i < objArr.length; i++) {
                Class<?> cls = parameterTypes[i];
                if (cls.isPrimitive() && null == objArr[i]) {
                    objArr[i] = ClassUtil.getDefaultValue(cls);
                }
            }
        }
        return (T) invoke(obj, method, objArr);
    }

    public static <T> T invoke(Object obj, Method method, Object... objArr) throws InvocationTargetRuntimeException, UtilException {
        try {
            return (T) invokeRaw(obj, method, objArr);
        } catch (IllegalAccessException e) {
            throw new UtilException(e);
        } catch (InvocationTargetException e2) {
            throw new InvocationTargetRuntimeException(e2);
        }
    }

    public static <T> T invokeRaw(Object obj, Method method, Object... objArr) throws InvocationTargetException, IllegalAccessException {
        setAccessible(method);
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] objArr2 = new Object[parameterTypes.length];
        if (null != objArr) {
            for (int i = 0; i < objArr2.length; i++) {
                if (i >= objArr.length || null == objArr[i]) {
                    objArr2[i] = ClassUtil.getDefaultValue(parameterTypes[i]);
                } else if (objArr[i] instanceof NullWrapperBean) {
                    objArr2[i] = null;
                } else if (false == parameterTypes[i].isAssignableFrom(objArr[i].getClass())) {
                    Object convertWithCheck = Convert.convertWithCheck(parameterTypes[i], objArr[i], null, true);
                    if (null != convertWithCheck) {
                        objArr2[i] = convertWithCheck;
                    } else {
                        objArr2[i] = objArr[i];
                    }
                } else {
                    objArr2[i] = objArr[i];
                }
            }
        }
        if (method.isDefault()) {
            return (T) MethodHandleUtil.invokeSpecial(obj, method, objArr);
        }
        return (T) method.invoke(ClassUtil.isStatic(method) ? null : obj, objArr2);
    }

    public static <T> T invoke(Object obj, String str, Object... objArr) throws UtilException {
        Assert.notNull(obj, "Object to get method must be not null!", new Object[0]);
        Assert.notBlank(str, "Method name must be not blank!", new Object[0]);
        Method methodOfObj = getMethodOfObj(obj, str, objArr);
        if (null == methodOfObj) {
            throw new UtilException("No such method: [{}] from [{}]", str, obj.getClass());
        }
        return (T) invoke(obj, methodOfObj, objArr);
    }

    public static <T extends AccessibleObject> T setAccessible(T accessibleObject) {
        if (null != accessibleObject && false == accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(true);
        }
        return accessibleObject;
    }

    public static void removeFinalModify(Field field) {
        ModifierUtil.removeFinalModify(field);
    }

    private static String getUniqueKey(Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getReturnType().getName()).append('#');
        sb.append(method.getName());
        Class<?>[] parameters = method.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            if (i == 0) {
                sb.append(':');
            } else {
                sb.append(',');
            }
            sb.append(parameters[i].getName());
        }
        return sb.toString();
    }

    private static List<Method> getDefaultMethodsFromInterface(Class<?> clazz) {
        List<Method> result = new ArrayList<>();
        for (Class<?> ifc : clazz.getInterfaces()) {
            for (Method m : ifc.getMethods()) {
                if (false == ModifierUtil.isAbstract(m)) {
                    result.add(m);
                }
            }
        }
        return result;
    }
}
