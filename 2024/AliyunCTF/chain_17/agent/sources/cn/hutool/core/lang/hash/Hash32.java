package cn.hutool.core.lang.hash;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/hash/Hash32.class */
public interface Hash32<T> extends Hash<T> {
    int hash32(T t);

    @Override // cn.hutool.core.lang.hash.Hash
    default Number hash(T t) {
        return Integer.valueOf(hash32(t));
    }
}
