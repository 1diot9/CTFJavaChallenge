package org.jooq.tools.reflect;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/reflect/Reflect.class */
public class Reflect {
    static final Constructor<MethodHandles.Lookup> CACHED_LOOKUP_CONSTRUCTOR = null;
    private final Class<?> type;
    private final Object object;

    public static Reflect compile(String name, String content) throws ReflectException {
        return compile(name, content, new CompileOptions());
    }

    public static Reflect compile(String name, String content, CompileOptions options) throws ReflectException {
        return onClass(Compile.compile(name, content, options));
    }

    public static void process(String name, String content) throws ReflectException {
        process(name, content, new CompileOptions());
    }

    public static void process(String name, String content, CompileOptions options) throws ReflectException {
        if (!options.hasOption("-proc:only")) {
            List<String> o = new ArrayList<>(options.options);
            o.add("-proc:only");
            options = options.options(o);
        }
        Compile.compile(name, content, options, false);
    }

    @Deprecated
    public static Reflect on(String name) throws ReflectException {
        return onClass(name);
    }

    @Deprecated
    public static Reflect on(String name, ClassLoader classLoader) throws ReflectException {
        return onClass(name, classLoader);
    }

    @Deprecated
    public static Reflect on(Class<?> clazz) {
        return onClass(clazz);
    }

    public static Reflect onClass(String name) throws ReflectException {
        return onClass(forName(name));
    }

    public static Reflect onClass(String name, ClassLoader classLoader) throws ReflectException {
        return onClass(forName(name, classLoader));
    }

    public static Reflect onClass(Class<?> clazz) {
        return new Reflect(clazz);
    }

    public static Reflect on(Object object) {
        return new Reflect(object == null ? Object.class : object.getClass(), object);
    }

    private static Reflect on(Class<?> type, Object object) {
        return new Reflect(type, object);
    }

    public static <T> T initValue(Class<T> cls) {
        if (cls == Boolean.TYPE) {
            return (T) Boolean.FALSE;
        }
        if (cls == Byte.TYPE) {
            return (T) (byte) 0;
        }
        if (cls == Short.TYPE) {
            return (T) (short) 0;
        }
        if (cls == Integer.TYPE) {
            return (T) 0;
        }
        if (cls == Long.TYPE) {
            return (T) 0L;
        }
        if (cls == Double.TYPE) {
            return (T) Double.valueOf(0.0d);
        }
        if (cls == Float.TYPE) {
            return (T) Float.valueOf(0.0f);
        }
        if (cls == Character.TYPE) {
            return (T) (char) 0;
        }
        return null;
    }

    public static <T extends AccessibleObject> T accessible(T accessible) {
        if (accessible == null) {
            return null;
        }
        if (accessible instanceof Member) {
            Member member = (Member) accessible;
            if (Modifier.isPublic(member.getModifiers()) && Modifier.isPublic(member.getDeclaringClass().getModifiers())) {
                return accessible;
            }
        }
        if (!accessible.isAccessible()) {
            accessible.setAccessible(true);
        }
        return accessible;
    }

    private Reflect(Class<?> type) {
        this(type, type);
    }

    private Reflect(Class<?> type, Object object) {
        this.type = type;
        this.object = object;
    }

    public <T> T get() {
        return (T) this.object;
    }

    public Reflect set(String name, Object value) throws ReflectException {
        try {
            Field field = field0(name);
            if ((field.getModifiers() & 16) == 16) {
                try {
                    Field modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    modifiersField.setInt(field, field.getModifiers() & (-17));
                } catch (NoSuchFieldException e) {
                }
            }
            field.set(this.object, unwrap(value));
            return this;
        } catch (Exception e2) {
            throw new ReflectException(e2);
        }
    }

    public <T> T get(String str) throws ReflectException {
        return (T) field(str).get();
    }

    public Reflect field(String name) throws ReflectException {
        try {
            Field field = field0(name);
            return on(field.getType(), field.get(this.object));
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    private Field field0(String name) throws ReflectException {
        Class<?> t = type();
        try {
            return (Field) accessible(t.getField(name));
        } catch (NoSuchFieldException e) {
            do {
                try {
                    return (Field) accessible(t.getDeclaredField(name));
                } catch (NoSuchFieldException e2) {
                    t = t.getSuperclass();
                    if (t != null) {
                        throw new ReflectException(e);
                    }
                }
            } while (t != null);
            throw new ReflectException(e);
        }
    }

    public Map<String, Reflect> fields() {
        Map<String, Reflect> result = new LinkedHashMap<>();
        Class<?> t = type();
        do {
            for (Field field : t.getDeclaredFields()) {
                if ((this.type != this.object) ^ Modifier.isStatic(field.getModifiers())) {
                    String name = field.getName();
                    if (!result.containsKey(name)) {
                        result.put(name, field(name));
                    }
                }
            }
            t = t.getSuperclass();
        } while (t != null);
        return result;
    }

    public Reflect call(String name) throws ReflectException {
        return call(name, new Object[0]);
    }

    public Reflect call(String name, Object... args) throws ReflectException {
        Class<?>[] types = types(args);
        try {
            Method method = exactMethod(name, types);
            return on(method, this.object, args);
        } catch (NoSuchMethodException e) {
            try {
                Method method2 = similarMethod(name, types);
                return on(method2, this.object, args);
            } catch (NoSuchMethodException e1) {
                throw new ReflectException(e1);
            }
        }
    }

    private Method exactMethod(String name, Class<?>[] types) throws NoSuchMethodException {
        Class<?> t = type();
        try {
            return t.getMethod(name, types);
        } catch (NoSuchMethodException e) {
            do {
                try {
                    return t.getDeclaredMethod(name, types);
                } catch (NoSuchMethodException e2) {
                    t = t.getSuperclass();
                }
            } while (t != null);
            throw new NoSuchMethodException();
        }
    }

    private Method similarMethod(String name, Class<?>[] types) throws NoSuchMethodException {
        Class<?> t = type();
        for (Method method : t.getMethods()) {
            if (isSimilarSignature(method, name, types)) {
                return method;
            }
        }
        do {
            for (Method method2 : t.getDeclaredMethods()) {
                if (isSimilarSignature(method2, name, types)) {
                    return method2;
                }
            }
            t = t.getSuperclass();
        } while (t != null);
        throw new NoSuchMethodException("No similar method " + name + " with params " + Arrays.toString(types) + " could be found on type " + String.valueOf(type()) + ".");
    }

    private boolean isSimilarSignature(Method possiblyMatchingMethod, String desiredMethodName, Class<?>[] desiredParamTypes) {
        return possiblyMatchingMethod.getName().equals(desiredMethodName) && match(possiblyMatchingMethod.getParameterTypes(), desiredParamTypes);
    }

    public Reflect create() throws ReflectException {
        return create(new Object[0]);
    }

    public Reflect create(Object... args) throws ReflectException {
        Class<?>[] types = types(args);
        try {
            return on(type().getDeclaredConstructor(types), args);
        } catch (NoSuchMethodException e) {
            for (Constructor<?> constructor : type().getDeclaredConstructors()) {
                if (match(constructor.getParameterTypes(), types)) {
                    return on(constructor, args);
                }
            }
            throw new ReflectException(e);
        }
    }

    public <P> P as(Class<P> cls) {
        return (P) as(cls, new Class[0]);
    }

    public <P> P as(final Class<P> cls, Class<?>... clsArr) {
        final boolean z = this.object instanceof Map;
        InvocationHandler invocationHandler = new InvocationHandler() { // from class: org.jooq.tools.reflect.Reflect.1
            @Override // java.lang.reflect.InvocationHandler
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                MethodHandles.Lookup proxyLookup;
                String name = method.getName();
                try {
                    return Reflect.on(Reflect.this.type, Reflect.this.object).call(name, args).get();
                } catch (ReflectException e) {
                    if (z) {
                        Map<String, Object> map = (Map) Reflect.this.object;
                        int length = args == null ? 0 : args.length;
                        if (length == 0 && name.startsWith(BeanUtil.PREFIX_GETTER_GET)) {
                            return map.get(Reflect.property(name.substring(3)));
                        }
                        if (length == 0 && name.startsWith(BeanUtil.PREFIX_GETTER_IS)) {
                            return map.get(Reflect.property(name.substring(2)));
                        }
                        if (length == 1 && name.startsWith("set")) {
                            map.put(Reflect.property(name.substring(3)), args[0]);
                            return null;
                        }
                    }
                    if (method.isDefault()) {
                        if (Reflect.CACHED_LOOKUP_CONSTRUCTOR == null) {
                            proxyLookup = MethodHandles.privateLookupIn(cls, MethodHandles.lookup()).in(cls);
                            if (proxyLookup == null) {
                                proxyLookup = (MethodHandles.Lookup) Reflect.onClass((Class<?>) MethodHandles.class).call("privateLookupIn", cls, MethodHandles.lookup()).call("in", cls).get();
                            }
                        } else {
                            proxyLookup = Reflect.CACHED_LOOKUP_CONSTRUCTOR.newInstance(cls);
                        }
                        return proxyLookup.unreflectSpecial(method, cls).bindTo(proxy).invokeWithArguments(args);
                    }
                    throw e;
                }
            }
        };
        Class[] clsArr2 = new Class[1 + clsArr.length];
        clsArr2[0] = cls;
        System.arraycopy(clsArr, 0, clsArr2, 1, clsArr.length);
        return (P) Proxy.newProxyInstance(cls.getClassLoader(), clsArr2, invocationHandler);
    }

    private static String property(String string) {
        int length = string.length();
        if (length == 0) {
            return "";
        }
        if (length == 1) {
            return string.toLowerCase();
        }
        return string.substring(0, 1).toLowerCase() + string.substring(1);
    }

    private boolean match(Class<?>[] declaredTypes, Class<?>[] actualTypes) {
        if (declaredTypes.length == actualTypes.length) {
            for (int i = 0; i < actualTypes.length; i++) {
                if (actualTypes[i] != NULL.class && !wrapper(declaredTypes[i]).isAssignableFrom(wrapper(actualTypes[i]))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.object.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof Reflect) {
            return this.object.equals(((Reflect) obj).get());
        }
        return false;
    }

    public String toString() {
        return String.valueOf(this.object);
    }

    private static Reflect on(Constructor<?> constructor, Object... args) throws ReflectException {
        try {
            return on(constructor.getDeclaringClass(), ((Constructor) accessible(constructor)).newInstance(args));
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    private static Reflect on(Method method, Object object, Object... args) throws ReflectException {
        try {
            accessible(method);
            if (method.getReturnType() == Void.TYPE) {
                method.invoke(object, args);
                return on(object);
            }
            return on(method.invoke(object, args));
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    private static Object unwrap(Object object) {
        if (object instanceof Reflect) {
            return ((Reflect) object).get();
        }
        return object;
    }

    private static Class<?>[] types(Object... values) {
        if (values == null) {
            return new Class[0];
        }
        Class<?>[] result = new Class[values.length];
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            result[i] = value == null ? NULL.class : value.getClass();
        }
        return result;
    }

    private static Class<?> forName(String name) throws ReflectException {
        try {
            return Class.forName(name);
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    private static Class<?> forName(String name, ClassLoader classLoader) throws ReflectException {
        try {
            return Class.forName(name, true, classLoader);
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    public Class<?> type() {
        return this.type;
    }

    public static <T> Class<T> wrapper(Class<T> type) {
        if (type == null) {
            return null;
        }
        if (type.isPrimitive()) {
            if (Boolean.TYPE == type) {
                return Boolean.class;
            }
            if (Integer.TYPE == type) {
                return Integer.class;
            }
            if (Long.TYPE == type) {
                return Long.class;
            }
            if (Short.TYPE == type) {
                return Short.class;
            }
            if (Byte.TYPE == type) {
                return Byte.class;
            }
            if (Double.TYPE == type) {
                return Double.class;
            }
            if (Float.TYPE == type) {
                return Float.class;
            }
            if (Character.TYPE == type) {
                return Character.class;
            }
            if (Void.TYPE == type) {
                return Void.class;
            }
        }
        return type;
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/reflect/Reflect$NULL.class */
    private static class NULL {
        private NULL() {
        }
    }
}
