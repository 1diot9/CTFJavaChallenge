package cn.hutool.cache;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cache/CacheListener.class */
public interface CacheListener<K, V> {
    void onRemove(K k, V v);
}
