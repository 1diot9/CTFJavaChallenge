package org.springframework.cglib.core;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.springframework.asm.Type;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/ReflectUtils.class */
public class ReflectUtils {
    private static final Method classLoaderDefineClassMethod;
    private static final Throwable THROWABLE;
    private static final ProtectionDomain PROTECTION_DOMAIN;
    private static BiConsumer<String, byte[]> generatedClassHandler;
    private static Consumer<Class<?>> loadedClassHandler;
    private static final String[] CGLIB_PACKAGES;
    private static final Map primitives = new HashMap(8);
    private static final Map transforms = new HashMap(8);
    private static final ClassLoader defaultLoader = ReflectUtils.class.getClassLoader();
    private static final List<Method> OBJECT_METHODS = new ArrayList();

    private ReflectUtils() {
    }

    static {
        Method classLoaderDefineClass;
        Throwable throwable = null;
        try {
            classLoaderDefineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE, ProtectionDomain.class);
        } catch (Throwable t) {
            classLoaderDefineClass = null;
            throwable = t;
        }
        classLoaderDefineClassMethod = classLoaderDefineClass;
        THROWABLE = throwable;
        PROTECTION_DOMAIN = getProtectionDomain(ReflectUtils.class);
        for (Method method : Object.class.getDeclaredMethods()) {
            if (!"finalize".equals(method.getName()) && (method.getModifiers() & 24) <= 0) {
                OBJECT_METHODS.add(method);
            }
        }
        CGLIB_PACKAGES = new String[]{"java.lang"};
        primitives.put("byte", Byte.TYPE);
        primitives.put("char", Character.TYPE);
        primitives.put("double", Double.TYPE);
        primitives.put("float", Float.TYPE);
        primitives.put("int", Integer.TYPE);
        primitives.put("long", Long.TYPE);
        primitives.put("short", Short.TYPE);
        primitives.put("boolean", Boolean.TYPE);
        transforms.put("byte", "B");
        transforms.put("char", "C");
        transforms.put("double", "D");
        transforms.put("float", "F");
        transforms.put("int", "I");
        transforms.put("long", "J");
        transforms.put("short", "S");
        transforms.put("boolean", "Z");
    }

    public static ProtectionDomain getProtectionDomain(final Class source) {
        if (source == null) {
            return null;
        }
        return source.getProtectionDomain();
    }

    public static Type[] getExceptionTypes(Member member) {
        if (member instanceof Method) {
            Method method = (Method) member;
            return TypeUtils.getTypes(method.getExceptionTypes());
        }
        if (member instanceof Constructor) {
            Constructor<?> constructor = (Constructor) member;
            return TypeUtils.getTypes(constructor.getExceptionTypes());
        }
        throw new IllegalArgumentException("Cannot get exception types of a field");
    }

    public static Signature getSignature(Member member) {
        if (member instanceof Method) {
            Method method = (Method) member;
            return new Signature(member.getName(), Type.getMethodDescriptor(method));
        }
        if (member instanceof Constructor) {
            Constructor<?> constructor = (Constructor) member;
            Type[] types = TypeUtils.getTypes(constructor.getParameterTypes());
            return new Signature(Constants.CONSTRUCTOR_NAME, Type.getMethodDescriptor(Type.VOID_TYPE, types));
        }
        throw new IllegalArgumentException("Cannot get signature of a field");
    }

    public static Constructor findConstructor(String desc) {
        return findConstructor(desc, defaultLoader);
    }

    public static Constructor findConstructor(String desc, ClassLoader loader) {
        try {
            int lparen = desc.indexOf(40);
            String className = desc.substring(0, lparen).trim();
            return getClass(className, loader).getConstructor(parseTypes(desc, loader));
        } catch (ClassNotFoundException | NoSuchMethodException ex) {
            throw new CodeGenerationException(ex);
        }
    }

    public static Method findMethod(String desc) {
        return findMethod(desc, defaultLoader);
    }

    public static Method findMethod(String desc, ClassLoader loader) {
        try {
            int lparen = desc.indexOf(40);
            int dot = desc.lastIndexOf(46, lparen);
            String className = desc.substring(0, dot).trim();
            String methodName = desc.substring(dot + 1, lparen).trim();
            return getClass(className, loader).getDeclaredMethod(methodName, parseTypes(desc, loader));
        } catch (ClassNotFoundException | NoSuchMethodException ex) {
            throw new CodeGenerationException(ex);
        }
    }

    private static Class[] parseTypes(String desc, ClassLoader loader) throws ClassNotFoundException {
        int start;
        int lparen = desc.indexOf(40);
        int rparen = desc.indexOf(41, lparen);
        List params = new ArrayList();
        int i = lparen;
        while (true) {
            start = i + 1;
            int comma = desc.indexOf(44, start);
            if (comma < 0) {
                break;
            }
            params.add(desc.substring(start, comma).trim());
            i = comma;
        }
        if (start < rparen) {
            params.add(desc.substring(start, rparen).trim());
        }
        Class[] types = new Class[params.size()];
        for (int i2 = 0; i2 < types.length; i2++) {
            types[i2] = getClass((String) params.get(i2), loader);
        }
        return types;
    }

    private static Class getClass(String className, ClassLoader loader) throws ClassNotFoundException {
        return getClass(className, loader, CGLIB_PACKAGES);
    }

    private static Class getClass(String className, ClassLoader loader, String[] packages) throws ClassNotFoundException {
        int dimensions = 0;
        int index = 0;
        while (true) {
            int indexOf = className.indexOf(ClassUtils.ARRAY_SUFFIX, index) + 1;
            index = indexOf;
            if (indexOf <= 0) {
                break;
            }
            dimensions++;
        }
        StringBuilder brackets = new StringBuilder(className.length() - dimensions);
        for (int i = 0; i < dimensions; i++) {
            brackets.append('[');
        }
        String className2 = className.substring(0, className.length() - (2 * dimensions));
        String prefix = dimensions > 0 ? brackets + "L" : "";
        String suffix = dimensions > 0 ? ";" : "";
        try {
            return Class.forName(prefix + className2 + suffix, false, loader);
        } catch (ClassNotFoundException e) {
            for (String pkg : packages) {
                try {
                    return Class.forName(prefix + pkg + "." + className2 + suffix, false, loader);
                } catch (ClassNotFoundException e2) {
                }
            }
            if (dimensions == 0) {
                Class c = (Class) primitives.get(className2);
                if (c != null) {
                    return c;
                }
            } else {
                String transform = (String) transforms.get(className2);
                if (transform != null) {
                    try {
                        return Class.forName(brackets + transform, false, loader);
                    } catch (ClassNotFoundException e3) {
                        throw new ClassNotFoundException(className);
                    }
                }
            }
            throw new ClassNotFoundException(className);
        }
    }

    public static Object newInstance(Class type) {
        return newInstance(type, Constants.EMPTY_CLASS_ARRAY, null);
    }

    public static Object newInstance(Class type, Class[] parameterTypes, Object[] args) {
        return newInstance(getConstructor(type, parameterTypes), args);
    }

    public static Object newInstance(final Constructor cstruct, final Object[] args) {
        boolean flag = cstruct.isAccessible();
        try {
            if (!flag) {
                try {
                    try {
                        cstruct.setAccessible(true);
                    } catch (IllegalAccessException | InstantiationException e) {
                        throw new CodeGenerationException(e);
                    }
                } catch (InvocationTargetException e2) {
                    throw new CodeGenerationException(e2.getTargetException());
                }
            }
            Object result = cstruct.newInstance(args);
            if (!flag) {
                cstruct.setAccessible(flag);
            }
            return result;
        } catch (Throwable th) {
            if (!flag) {
                cstruct.setAccessible(flag);
            }
            throw th;
        }
    }

    public static Constructor getConstructor(Class type, Class[] parameterTypes) {
        try {
            Constructor constructor = type.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException e) {
            throw new CodeGenerationException(e);
        }
    }

    public static String[] getNames(Class[] classes) {
        if (classes == null) {
            return null;
        }
        String[] names = new String[classes.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = classes[i].getName();
        }
        return names;
    }

    public static Class[] getClasses(Object[] objects) {
        Class[] classes = new Class[objects.length];
        for (int i = 0; i < objects.length; i++) {
            classes[i] = objects[i].getClass();
        }
        return classes;
    }

    public static Method findNewInstance(Class iface) {
        Method m = findInterfaceMethod(iface);
        if (!m.getName().equals("newInstance")) {
            throw new IllegalArgumentException(iface + " missing newInstance method");
        }
        return m;
    }

    public static Method[] getPropertyMethods(PropertyDescriptor[] properties, boolean read, boolean write) {
        Set methods = new HashSet();
        for (PropertyDescriptor pd : properties) {
            if (read) {
                methods.add(pd.getReadMethod());
            }
            if (write) {
                methods.add(pd.getWriteMethod());
            }
        }
        methods.remove(null);
        return (Method[]) methods.toArray(new Method[methods.size()]);
    }

    public static PropertyDescriptor[] getBeanProperties(Class type) {
        return getPropertiesHelper(type, true, true);
    }

    public static PropertyDescriptor[] getBeanGetters(Class type) {
        return getPropertiesHelper(type, true, false);
    }

    public static PropertyDescriptor[] getBeanSetters(Class type) {
        return getPropertiesHelper(type, false, true);
    }

    private static PropertyDescriptor[] getPropertiesHelper(Class type, boolean read, boolean write) {
        try {
            BeanInfo info = Introspector.getBeanInfo(type, Object.class);
            PropertyDescriptor[] all = info.getPropertyDescriptors();
            if (read && write) {
                return all;
            }
            List properties = new ArrayList(all.length);
            for (PropertyDescriptor pd : all) {
                if ((read && pd.getReadMethod() != null) || (write && pd.getWriteMethod() != null)) {
                    properties.add(pd);
                }
            }
            return (PropertyDescriptor[]) properties.toArray(new PropertyDescriptor[properties.size()]);
        } catch (IntrospectionException e) {
            throw new CodeGenerationException(e);
        }
    }

    public static Method findDeclaredMethod(final Class type, final String methodName, final Class[] parameterTypes) throws NoSuchMethodException {
        Class cls = type;
        while (true) {
            Class cl = cls;
            if (cl != null) {
                try {
                    return cl.getDeclaredMethod(methodName, parameterTypes);
                } catch (NoSuchMethodException e) {
                    cls = cl.getSuperclass();
                }
            } else {
                throw new NoSuchMethodException(methodName);
            }
        }
    }

    public static List addAllMethods(final Class type, final List list) {
        if (type == Object.class) {
            list.addAll(OBJECT_METHODS);
        } else {
            list.addAll(Arrays.asList(type.getDeclaredMethods()));
        }
        Class superclass = type.getSuperclass();
        if (superclass != null) {
            addAllMethods(superclass, list);
        }
        Class[] interfaces = type.getInterfaces();
        for (Class element : interfaces) {
            addAllMethods(element, list);
        }
        return list;
    }

    public static List addAllInterfaces(Class type, List list) {
        Class superclass = type.getSuperclass();
        if (superclass != null) {
            list.addAll(Arrays.asList(type.getInterfaces()));
            addAllInterfaces(superclass, list);
        }
        return list;
    }

    public static Method findInterfaceMethod(Class iface) {
        if (!iface.isInterface()) {
            throw new IllegalArgumentException(iface + " is not an interface");
        }
        Method[] methods = iface.getDeclaredMethods();
        if (methods.length != 1) {
            throw new IllegalArgumentException("expecting exactly 1 method in " + iface);
        }
        return methods[0];
    }

    public static void setGeneratedClassHandler(BiConsumer<String, byte[]> handler) {
        generatedClassHandler = handler;
    }

    public static Class defineClass(String className, byte[] b, ClassLoader loader) throws Exception {
        return defineClass(className, b, loader, null, null);
    }

    public static Class defineClass(String className, byte[] b, ClassLoader loader, ProtectionDomain protectionDomain) throws Exception {
        return defineClass(className, b, loader, protectionDomain, null);
    }

    public static Class defineClass(String className, byte[] b, final ClassLoader loader, ProtectionDomain protectionDomain, final Class<?> contextClass) throws Exception {
        Class c = null;
        InaccessibleObjectException inaccessibleObjectException = THROWABLE;
        BiConsumer<String, byte[]> handlerToUse = generatedClassHandler;
        if (handlerToUse != null) {
            handlerToUse.accept(className, b);
        }
        if (contextClass != null && contextClass.getClassLoader() == loader) {
            try {
                MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(contextClass, MethodHandles.lookup());
                c = lookup.defineClass(b);
            } catch (IllegalArgumentException | LinkageError e) {
                inaccessibleObjectException = e;
            } catch (Throwable ex) {
                throw new CodeGenerationException(ex);
            }
        }
        if (c == null) {
            if (protectionDomain == null) {
                protectionDomain = PROTECTION_DOMAIN;
            }
            try {
                Method publicDefineClass = loader.getClass().getMethod("publicDefineClass", String.class, byte[].class, ProtectionDomain.class);
                c = (Class) publicDefineClass.invoke(loader, className, b, protectionDomain);
            } catch (InvocationTargetException ex2) {
                if (!(ex2.getTargetException() instanceof UnsupportedOperationException)) {
                    throw new CodeGenerationException(ex2.getTargetException());
                }
                inaccessibleObjectException = ex2.getTargetException();
            } catch (Throwable th) {
                inaccessibleObjectException = th;
            }
            if (c == null && classLoaderDefineClassMethod != null) {
                Object[] args = {className, b, 0, Integer.valueOf(b.length), protectionDomain};
                try {
                    if (!classLoaderDefineClassMethod.isAccessible()) {
                        classLoaderDefineClassMethod.setAccessible(true);
                    }
                    c = (Class) classLoaderDefineClassMethod.invoke(loader, args);
                } catch (InvocationTargetException ex3) {
                    throw new CodeGenerationException(ex3.getTargetException());
                } catch (InaccessibleObjectException ex4) {
                    inaccessibleObjectException = ex4;
                } catch (Throwable ex5) {
                    throw new CodeGenerationException(ex5);
                }
            }
        }
        if (c == null && contextClass != null && contextClass.getClassLoader() != loader) {
            try {
                MethodHandles.Lookup lookup2 = MethodHandles.privateLookupIn(contextClass, MethodHandles.lookup());
                c = lookup2.defineClass(b);
            } catch (IllegalAccessException | LinkageError ex6) {
                throw new CodeGenerationException(ex6) { // from class: org.springframework.cglib.core.ReflectUtils.1
                    @Override // java.lang.Throwable
                    public String getMessage() {
                        return "ClassLoader mismatch for [" + contextClass.getName() + "]: JVM should be started with --add-opens=java.base/java.lang=ALL-UNNAMED for ClassLoader.defineClass to be accessible on " + loader.getClass().getName() + "; consider co-locating the affected class in that target ClassLoader instead.";
                    }
                };
            } catch (Throwable ex7) {
                throw new CodeGenerationException(ex7);
            }
        }
        if (c == null) {
            throw new CodeGenerationException(inaccessibleObjectException);
        }
        Class.forName(className, true, loader);
        return c;
    }

    public static void setLoadedClassHandler(Consumer<Class<?>> loadedClassHandler2) {
        loadedClassHandler = loadedClassHandler2;
    }

    public static Class<?> loadClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className, true, classLoader);
        Consumer<Class<?>> handlerToUse = loadedClassHandler;
        if (handlerToUse != null) {
            handlerToUse.accept(clazz);
        }
        return clazz;
    }

    public static int findPackageProtected(Class[] classes) {
        for (int i = 0; i < classes.length; i++) {
            if (!Modifier.isPublic(classes[i].getModifiers())) {
                return i;
            }
        }
        return 0;
    }

    public static MethodInfo getMethodInfo(final Member member, final int modifiers) {
        final Signature sig = getSignature(member);
        return new MethodInfo() { // from class: org.springframework.cglib.core.ReflectUtils.2
            private ClassInfo ci;

            @Override // org.springframework.cglib.core.MethodInfo
            public ClassInfo getClassInfo() {
                if (this.ci == null) {
                    this.ci = ReflectUtils.getClassInfo(member.getDeclaringClass());
                }
                return this.ci;
            }

            @Override // org.springframework.cglib.core.MethodInfo
            public int getModifiers() {
                return modifiers;
            }

            @Override // org.springframework.cglib.core.MethodInfo
            public Signature getSignature() {
                return sig;
            }

            @Override // org.springframework.cglib.core.MethodInfo
            public Type[] getExceptionTypes() {
                return ReflectUtils.getExceptionTypes(member);
            }
        };
    }

    public static MethodInfo getMethodInfo(Member member) {
        return getMethodInfo(member, member.getModifiers());
    }

    public static ClassInfo getClassInfo(final Class clazz) {
        final Type type = Type.getType((Class<?>) clazz);
        final Type sc = clazz.getSuperclass() == null ? null : Type.getType((Class<?>) clazz.getSuperclass());
        return new ClassInfo() { // from class: org.springframework.cglib.core.ReflectUtils.3
            @Override // org.springframework.cglib.core.ClassInfo
            public Type getType() {
                return Type.this;
            }

            @Override // org.springframework.cglib.core.ClassInfo
            public Type getSuperType() {
                return sc;
            }

            @Override // org.springframework.cglib.core.ClassInfo
            public Type[] getInterfaces() {
                return TypeUtils.getTypes(clazz.getInterfaces());
            }

            @Override // org.springframework.cglib.core.ClassInfo
            public int getModifiers() {
                return clazz.getModifiers();
            }
        };
    }

    public static Method[] findMethods(String[] namesAndDescriptors, Method[] methods) {
        Map map = new HashMap();
        for (Method method : methods) {
            map.put(method.getName() + Type.getMethodDescriptor(method), method);
        }
        Method[] result = new Method[namesAndDescriptors.length / 2];
        for (int i = 0; i < result.length; i++) {
            result[i] = (Method) map.get(namesAndDescriptors[i * 2] + namesAndDescriptors[(i * 2) + 1]);
            if (result[i] == null) {
            }
        }
        return result;
    }
}
