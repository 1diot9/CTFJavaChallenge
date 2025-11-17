package cn.hutool.core.util;

import cn.hutool.core.bean.NullWrapperBean;
import cn.hutool.core.convert.BasicType;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.ClassScanner;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.Singleton;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/util/ClassUtil.class */
public class ClassUtil {
    public static <T> Class<T> getClass(T t) {
        if (null == t) {
            return null;
        }
        return (Class<T>) t.getClass();
    }

    public static Class<?> getEnclosingClass(Class<?> clazz) {
        if (null == clazz) {
            return null;
        }
        return clazz.getEnclosingClass();
    }

    public static boolean isTopLevelClass(Class<?> clazz) {
        return null != clazz && null == getEnclosingClass(clazz);
    }

    public static String getClassName(Object obj, boolean isSimple) {
        if (null == obj) {
            return null;
        }
        Class<?> clazz = obj.getClass();
        return getClassName(clazz, isSimple);
    }

    public static String getClassName(Class<?> clazz, boolean isSimple) {
        if (null == clazz) {
            return null;
        }
        return isSimple ? clazz.getSimpleName() : clazz.getName();
    }

    public static String getShortClassName(String className) {
        List<String> packages = StrUtil.split((CharSequence) className, '.');
        if (null == packages || packages.size() < 2) {
            return className;
        }
        int size = packages.size();
        StringBuilder result = StrUtil.builder();
        result.append(packages.get(0).charAt(0));
        for (int i = 1; i < size - 1; i++) {
            result.append('.').append(packages.get(i).charAt(0));
        }
        result.append('.').append(packages.get(size - 1));
        return result.toString();
    }

    public static Class<?>[] getClasses(Object... objects) {
        Class<?>[] classes = new Class[objects.length];
        for (int i = 0; i < objects.length; i++) {
            Object obj = objects[i];
            if (obj instanceof NullWrapperBean) {
                classes[i] = ((NullWrapperBean) obj).getWrappedClass();
            } else if (null == obj) {
                classes[i] = Object.class;
            } else {
                classes[i] = obj.getClass();
            }
        }
        return classes;
    }

    public static boolean equals(Class<?> clazz, String className, boolean ignoreCase) {
        if (null == clazz || StrUtil.isBlank(className)) {
            return false;
        }
        return ignoreCase ? className.equalsIgnoreCase(clazz.getName()) || className.equalsIgnoreCase(clazz.getSimpleName()) : className.equals(clazz.getName()) || className.equals(clazz.getSimpleName());
    }

    public static Set<Class<?>> scanPackageByAnnotation(String packageName, Class<? extends Annotation> annotationClass) {
        return ClassScanner.scanPackageByAnnotation(packageName, annotationClass);
    }

    public static Set<Class<?>> scanPackageBySuper(String packageName, Class<?> superClass) {
        return ClassScanner.scanPackageBySuper(packageName, superClass);
    }

    public static Set<Class<?>> scanPackage() {
        return ClassScanner.scanPackage();
    }

    public static Set<Class<?>> scanPackage(String packageName) {
        return ClassScanner.scanPackage(packageName);
    }

    public static Set<Class<?>> scanPackage(String packageName, Filter<Class<?>> classFilter) {
        return ClassScanner.scanPackage(packageName, classFilter);
    }

    public static Set<String> getPublicMethodNames(Class<?> clazz) {
        return ReflectUtil.getPublicMethodNames(clazz);
    }

    public static Method[] getPublicMethods(Class<?> clazz) {
        return ReflectUtil.getPublicMethods(clazz);
    }

    public static List<Method> getPublicMethods(Class<?> clazz, Filter<Method> filter) {
        return ReflectUtil.getPublicMethods(clazz, filter);
    }

    public static List<Method> getPublicMethods(Class<?> clazz, Method... excludeMethods) {
        return ReflectUtil.getPublicMethods(clazz, excludeMethods);
    }

    public static List<Method> getPublicMethods(Class<?> clazz, String... excludeMethodNames) {
        return ReflectUtil.getPublicMethods(clazz, excludeMethodNames);
    }

    public static Method getPublicMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
        return ReflectUtil.getPublicMethod(clazz, methodName, paramTypes);
    }

    public static Set<String> getDeclaredMethodNames(Class<?> clazz) {
        return ReflectUtil.getMethodNames(clazz);
    }

    public static Method[] getDeclaredMethods(Class<?> clazz) {
        return ReflectUtil.getMethods(clazz);
    }

    public static Method getDeclaredMethodOfObj(Object obj, String methodName, Object... args) throws SecurityException {
        return getDeclaredMethod(obj.getClass(), methodName, getClasses(args));
    }

    public static Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws SecurityException {
        return ReflectUtil.getMethod(clazz, methodName, parameterTypes);
    }

    public static Field getDeclaredField(Class<?> clazz, String fieldName) throws SecurityException {
        if (null == clazz || StrUtil.isBlank(fieldName)) {
            return null;
        }
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    public static Field[] getDeclaredFields(Class<?> clazz) throws SecurityException {
        if (null == clazz) {
            return null;
        }
        return clazz.getDeclaredFields();
    }

    public static Set<String> getClassPathResources() {
        return getClassPathResources(false);
    }

    public static Set<String> getClassPathResources(boolean isDecode) {
        return getClassPaths("", isDecode);
    }

    public static Set<String> getClassPaths(String packageName) {
        return getClassPaths(packageName, false);
    }

    public static Set<String> getClassPaths(String packageName, boolean isDecode) {
        String packagePath = packageName.replace(".", "/");
        try {
            Enumeration<URL> resources = getClassLoader().getResources(packagePath);
            Set<String> paths = new HashSet<>();
            while (resources.hasMoreElements()) {
                String path = resources.nextElement().getPath();
                paths.add(isDecode ? URLUtil.decode(path, CharsetUtil.systemCharsetName()) : path);
            }
            return paths;
        } catch (IOException e) {
            throw new UtilException(e, "Loading classPath [{}] error!", packagePath);
        }
    }

    public static String getClassPath() {
        return getClassPath(false);
    }

    public static String getClassPath(boolean isEncoded) {
        URL classPathURL = getClassPathURL();
        String url = isEncoded ? classPathURL.getPath() : URLUtil.getDecodedPath(classPathURL);
        return FileUtil.normalize(url);
    }

    public static URL getClassPathURL() {
        return getResourceURL("");
    }

    public static URL getResourceURL(String resource) throws IORuntimeException {
        return ResourceUtil.getResource(resource);
    }

    public static List<URL> getResources(String resource) {
        return ResourceUtil.getResources(resource);
    }

    public static URL getResourceUrl(String resource, Class<?> baseClass) {
        return ResourceUtil.getResource(resource, baseClass);
    }

    public static String[] getJavaClassPaths() {
        return System.getProperty("java.class.path").split(System.getProperty("path.separator"));
    }

    public static ClassLoader getContextClassLoader() {
        return ClassLoaderUtil.getContextClassLoader();
    }

    public static ClassLoader getClassLoader() {
        return ClassLoaderUtil.getClassLoader();
    }

    public static boolean isAllAssignableFrom(Class<?>[] types1, Class<?>[] types2) {
        if (ArrayUtil.isEmpty((Object[]) types1) && ArrayUtil.isEmpty((Object[]) types2)) {
            return true;
        }
        if (null == types1 || null == types2 || types1.length != types2.length) {
            return false;
        }
        for (int i = 0; i < types1.length; i++) {
            Class<?> type1 = types1[i];
            Class<?> type2 = types2[i];
            if (isBasicType(type1) && isBasicType(type2)) {
                if (BasicType.unWrap(type1) != BasicType.unWrap(type2)) {
                    return false;
                }
            } else if (false == type1.isAssignableFrom(type2)) {
                return false;
            }
        }
        return true;
    }

    public static <T> Class<T> loadClass(String str, boolean z) {
        return (Class<T>) ClassLoaderUtil.loadClass(str, z);
    }

    public static <T> Class<T> loadClass(String className) {
        return loadClass(className, true);
    }

    public static <T> T invoke(String str, Object[] objArr) {
        return (T) invoke(str, false, objArr);
    }

    public static <T> T invoke(String str, boolean z, Object... objArr) {
        if (StrUtil.isBlank(str)) {
            throw new UtilException("Blank classNameDotMethodName!");
        }
        int lastIndexOf = str.lastIndexOf(35);
        if (lastIndexOf <= 0) {
            lastIndexOf = str.lastIndexOf(46);
        }
        if (lastIndexOf <= 0) {
            throw new UtilException("Invalid classNameWithMethodName [{}]!", str);
        }
        return (T) invoke(str.substring(0, lastIndexOf), str.substring(lastIndexOf + 1), z, objArr);
    }

    public static <T> T invoke(String str, String str2, Object[] objArr) {
        return (T) invoke(str, str2, false, objArr);
    }

    public static <T> T invoke(String str, String str2, boolean z, Object... objArr) {
        Class loadClass = loadClass(str);
        try {
            Method declaredMethod = getDeclaredMethod(loadClass, str2, getClasses(objArr));
            if (null == declaredMethod) {
                throw new NoSuchMethodException(StrUtil.format("No such method: [{}]", str2));
            }
            if (isStatic(declaredMethod)) {
                return (T) ReflectUtil.invoke((Object) null, declaredMethod, objArr);
            }
            return (T) ReflectUtil.invoke(z ? Singleton.get(loadClass, new Object[0]) : loadClass.newInstance(), declaredMethod, objArr);
        } catch (Exception e) {
            throw new UtilException(e);
        }
    }

    public static boolean isPrimitiveWrapper(Class<?> clazz) {
        if (null == clazz) {
            return false;
        }
        return BasicType.WRAPPER_PRIMITIVE_MAP.containsKey(clazz);
    }

    public static boolean isBasicType(Class<?> clazz) {
        if (null == clazz) {
            return false;
        }
        return clazz.isPrimitive() || isPrimitiveWrapper(clazz);
    }

    public static boolean isSimpleTypeOrArray(Class<?> clazz) {
        if (null == clazz) {
            return false;
        }
        return isSimpleValueType(clazz) || (clazz.isArray() && isSimpleValueType(clazz.getComponentType()));
    }

    public static boolean isSimpleValueType(Class<?> clazz) {
        return isBasicType(clazz) || clazz.isEnum() || CharSequence.class.isAssignableFrom(clazz) || Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz) || clazz.equals(URI.class) || clazz.equals(URL.class) || clazz.equals(Locale.class) || clazz.equals(Class.class) || TemporalAccessor.class.isAssignableFrom(clazz);
    }

    public static boolean isAssignable(Class<?> targetType, Class<?> sourceType) {
        if (null == targetType || null == sourceType) {
            return false;
        }
        if (targetType.isAssignableFrom(sourceType)) {
            return true;
        }
        if (targetType.isPrimitive()) {
            Class<?> resolvedPrimitive = BasicType.WRAPPER_PRIMITIVE_MAP.get(sourceType);
            return targetType.equals(resolvedPrimitive);
        }
        Class<?> resolvedWrapper = BasicType.PRIMITIVE_WRAPPER_MAP.get(sourceType);
        return resolvedWrapper != null && targetType.isAssignableFrom(resolvedWrapper);
    }

    public static boolean isPublic(Class<?> clazz) {
        if (null == clazz) {
            throw new NullPointerException("Class to provided is null.");
        }
        return Modifier.isPublic(clazz.getModifiers());
    }

    public static boolean isPublic(Method method) {
        Assert.notNull(method, "Method to provided is null.", new Object[0]);
        return Modifier.isPublic(method.getModifiers());
    }

    public static boolean isNotPublic(Class<?> clazz) {
        return false == isPublic(clazz);
    }

    public static boolean isNotPublic(Method method) {
        return false == isPublic(method);
    }

    public static boolean isStatic(Method method) {
        Assert.notNull(method, "Method to provided is null.", new Object[0]);
        return Modifier.isStatic(method.getModifiers());
    }

    public static Method setAccessible(Method method) {
        if (null != method && false == method.isAccessible()) {
            method.setAccessible(true);
        }
        return method;
    }

    public static boolean isAbstract(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    public static boolean isNormalClass(Class<?> clazz) {
        return null != clazz && false == clazz.isInterface() && false == isAbstract(clazz) && false == clazz.isEnum() && false == clazz.isArray() && false == clazz.isAnnotation() && false == clazz.isSynthetic() && false == clazz.isPrimitive();
    }

    public static boolean isEnum(Class<?> clazz) {
        return null != clazz && clazz.isEnum();
    }

    public static Class<?> getTypeArgument(Class<?> clazz) {
        return getTypeArgument(clazz, 0);
    }

    public static Class<?> getTypeArgument(Class<?> clazz, int index) {
        Type argumentType = TypeUtil.getTypeArgument(clazz, index);
        return TypeUtil.getClass(argumentType);
    }

    public static String getPackage(Class<?> clazz) {
        String className;
        int packageEndIndex;
        if (clazz == null || (packageEndIndex = (className = clazz.getName()).lastIndexOf(".")) == -1) {
            return "";
        }
        return className.substring(0, packageEndIndex);
    }

    public static String getPackagePath(Class<?> clazz) {
        return getPackage(clazz).replace('.', '/');
    }

    public static Object getDefaultValue(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return getPrimitiveDefaultValue(clazz);
        }
        return null;
    }

    public static Object getPrimitiveDefaultValue(Class<?> clazz) {
        if (Long.TYPE == clazz) {
            return 0L;
        }
        if (Integer.TYPE == clazz) {
            return 0;
        }
        if (Short.TYPE == clazz) {
            return (short) 0;
        }
        if (Character.TYPE == clazz) {
            return (char) 0;
        }
        if (Byte.TYPE == clazz) {
            return (byte) 0;
        }
        if (Double.TYPE == clazz) {
            return Double.valueOf(0.0d);
        }
        if (Float.TYPE == clazz) {
            return Float.valueOf(0.0f);
        }
        if (Boolean.TYPE == clazz) {
            return false;
        }
        return null;
    }

    public static Object[] getDefaultValues(Class<?>... classes) {
        Object[] values = new Object[classes.length];
        for (int i = 0; i < classes.length; i++) {
            values[i] = getDefaultValue(classes[i]);
        }
        return values;
    }

    public static boolean isJdkClass(Class<?> clazz) {
        Package objectPackage = clazz.getPackage();
        if (null == objectPackage) {
            return false;
        }
        String objectPackageName = objectPackage.getName();
        return objectPackageName.startsWith("java.") || objectPackageName.startsWith("javax.") || clazz.getClassLoader() == null;
    }

    public static URL getLocation(Class<?> clazz) {
        if (null == clazz) {
            return null;
        }
        return clazz.getProtectionDomain().getCodeSource().getLocation();
    }

    public static String getLocationPath(Class<?> clazz) {
        URL location = getLocation(clazz);
        if (null == location) {
            return null;
        }
        return location.getPath();
    }

    public static boolean isAbstractOrInterface(Class<?> clazz) {
        return isAbstract(clazz) || isInterface(clazz);
    }

    public static boolean isInterface(Class<?> clazz) {
        return clazz.isInterface();
    }
}
