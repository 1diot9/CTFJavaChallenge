package cn.hutool.extra.cglib;

import cn.hutool.core.map.WeakConcurrentMap;
import cn.hutool.core.util.StrUtil;
import java.lang.invoke.SerializedLambda;
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.core.Converter;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/cglib/BeanCopierCache.class */
public enum BeanCopierCache {
    INSTANCE;

    private final WeakConcurrentMap<String, BeanCopier> cache = new WeakConcurrentMap<>();

    BeanCopierCache() {
    }

    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case -1207448498:
                if (implMethodName.equals("lambda$get$3b77ef17$1")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/extra/cglib/BeanCopierCache") && lambda.getImplMethodSignature().equals("(Ljava/lang/Class;Ljava/lang/Class;Z)Lnet/sf/cglib/beans/BeanCopier;")) {
                    Class cls = (Class) lambda.getCapturedArg(0);
                    Class cls2 = (Class) lambda.getCapturedArg(1);
                    boolean booleanValue = ((Boolean) lambda.getCapturedArg(2)).booleanValue();
                    return () -> {
                        return BeanCopier.create(cls, cls2, booleanValue);
                    };
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    public BeanCopier get(Class<?> srcClass, Class<?> targetClass, Converter converter) {
        return get(srcClass, targetClass, null != converter);
    }

    public BeanCopier get(Class<?> srcClass, Class<?> targetClass, boolean useConverter) {
        String key = genKey(srcClass, targetClass, useConverter);
        return this.cache.computeIfAbsent((WeakConcurrentMap<String, BeanCopier>) key, () -> {
            return BeanCopier.create(srcClass, targetClass, useConverter);
        });
    }

    private String genKey(Class<?> srcClass, Class<?> targetClass, boolean useConverter) {
        StringBuilder key = StrUtil.builder().append(srcClass.getName()).append('#').append(targetClass.getName()).append('#').append(useConverter ? 1 : 0);
        return key.toString();
    }
}
