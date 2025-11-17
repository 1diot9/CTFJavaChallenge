package cn.hutool.extra.cglib;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.beans.BeanMap;
import net.sf.cglib.core.Converter;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/cglib/CglibUtil.class */
public class CglibUtil {
    public static <T> T copy(Object obj, Class<T> cls) {
        return (T) copy(obj, (Class) cls, (Converter) null);
    }

    public static <T> T copy(Object obj, Class<T> cls, Converter converter) {
        T t = (T) ReflectUtil.newInstanceIfPossible(cls);
        copy(obj, t, converter);
        return t;
    }

    public static void copy(Object source, Object target) {
        copy(source, target, (Converter) null);
    }

    public static void copy(Object source, Object target, Converter converter) {
        Assert.notNull(source, "Source bean must be not null.", new Object[0]);
        Assert.notNull(target, "Target bean must be not null.", new Object[0]);
        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();
        BeanCopier beanCopier = BeanCopierCache.INSTANCE.get(sourceClass, targetClass, converter);
        beanCopier.copy(source, target, converter);
    }

    public static <S, T> List<T> copyList(Collection<S> source, Supplier<T> target) {
        return copyList(source, target, null, null);
    }

    public static <S, T> List<T> copyList(Collection<S> source, Supplier<T> target, Converter converter) {
        return copyList(source, target, converter, null);
    }

    public static <S, T> List<T> copyList(Collection<S> source, Supplier<T> target, BiConsumer<S, T> callback) {
        return copyList(source, target, null, callback);
    }

    public static <S, T> List<T> copyList(Collection<S> source, Supplier<T> target, Converter converter, BiConsumer<S, T> callback) {
        return (List) source.stream().map(s -> {
            Object obj = target.get();
            copy(s, obj, converter);
            if (callback != null) {
                callback.accept(s, obj);
            }
            return obj;
        }).collect(Collectors.toList());
    }

    public static BeanMap toMap(Object bean) {
        return BeanMap.create(bean);
    }

    public static <T> T fillBean(Map map, T bean) {
        BeanMap.create(bean).putAll(map);
        return bean;
    }

    public static <T> T toBean(Map map, Class<T> cls) {
        return (T) fillBean(map, ReflectUtil.newInstanceIfPossible(cls));
    }
}
