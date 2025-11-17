package cn.hutool.core.lang.hash;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/hash/Hash128.class */
public interface Hash128<T> extends Hash<T> {
    Number128 hash128(T t);

    @Override // cn.hutool.core.lang.hash.Hash
    default Number hash(T t) {
        return hash128(t);
    }
}
