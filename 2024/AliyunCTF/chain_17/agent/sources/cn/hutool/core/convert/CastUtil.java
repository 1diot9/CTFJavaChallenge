package cn.hutool.core.convert;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/CastUtil.class */
public class CastUtil {
    /* JADX WARN: Multi-variable type inference failed */
    public static <T> Collection<T> castUp(Collection<? extends T> collection) {
        return collection;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> Collection<T> castDown(Collection<? super T> collection) {
        return collection;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> Set<T> castUp(Set<? extends T> set) {
        return set;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> Set<T> castDown(Set<? super T> set) {
        return set;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> List<T> castUp(List<? extends T> list) {
        return list;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> List<T> castDown(List<? super T> list) {
        return list;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <K, V> Map<K, V> castUp(Map<? extends K, ? extends V> map) {
        return map;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <K, V> Map<K, V> castDown(Map<? super K, ? super V> map) {
        return map;
    }
}
