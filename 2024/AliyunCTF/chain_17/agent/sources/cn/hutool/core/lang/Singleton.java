package cn.hutool.core.lang;

import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.map.SafeConcurrentHashMap;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import java.lang.invoke.SerializedLambda;
import java.util.Set;
import java.util.stream.Collectors;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/Singleton.class */
public final class Singleton {
    private static final SafeConcurrentHashMap<String, Object> POOL = new SafeConcurrentHashMap<>();

    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case -424253257:
                if (implMethodName.equals("lambda$get$3f3ed817$1")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/lang/Singleton") && lambda.getImplMethodSignature().equals("(Ljava/lang/Class;[Ljava/lang/Object;)Ljava/lang/Object;")) {
                    Class cls = (Class) lambda.getCapturedArg(0);
                    Object[] objArr = (Object[]) lambda.getCapturedArg(1);
                    return () -> {
                        return ReflectUtil.newInstance(cls, objArr);
                    };
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    private Singleton() {
    }

    public static <T> T get(Class<T> cls, Object... objArr) {
        Assert.notNull(cls, "Class must be not null !", new Object[0]);
        return (T) get(buildKey(cls.getName(), objArr), () -> {
            return ReflectUtil.newInstance(cls, objArr);
        });
    }

    public static <T> T get(String str, Func0<T> func0) {
        return (T) POOL.computeIfAbsent(str, k -> {
            return func0.callWithRuntimeException();
        });
    }

    public static <T> T get(String str, Object... objArr) {
        Assert.notBlank(str, "Class name must be not blank !", new Object[0]);
        return (T) get(ClassUtil.loadClass(str), objArr);
    }

    public static void put(Object obj) {
        Assert.notNull(obj, "Bean object must be not null !", new Object[0]);
        put(obj.getClass().getName(), obj);
    }

    public static void put(String key, Object obj) {
        POOL.put(key, obj);
    }

    public static boolean exists(Class<?> clazz, Object... params) {
        if (null != clazz) {
            String key = buildKey(clazz.getName(), params);
            return POOL.containsKey(key);
        }
        return false;
    }

    public static Set<Class<?>> getExistClass() {
        return (Set) POOL.values().stream().map((v0) -> {
            return v0.getClass();
        }).collect(Collectors.toSet());
    }

    public static void remove(Class<?> clazz) {
        if (null != clazz) {
            remove(clazz.getName());
        }
    }

    public static void remove(String key) {
        POOL.remove(key);
    }

    public static void destroy() {
        POOL.clear();
    }

    private static String buildKey(String className, Object... params) {
        if (ArrayUtil.isEmpty(params)) {
            return className;
        }
        return StrUtil.format("{}#{}", className, ArrayUtil.join(params, (CharSequence) "_"));
    }
}
