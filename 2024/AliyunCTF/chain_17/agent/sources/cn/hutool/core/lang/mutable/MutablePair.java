package cn.hutool.core.lang.mutable;

import cn.hutool.core.lang.Pair;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/mutable/MutablePair.class */
public class MutablePair<K, V> extends Pair<K, V> implements Mutable<Pair<K, V>> {
    private static final long serialVersionUID = 1;

    public MutablePair(K key, V value) {
        super(key, value);
    }

    public MutablePair<K, V> setKey(K key) {
        this.key = key;
        return this;
    }

    public MutablePair<K, V> setValue(V value) {
        this.value = value;
        return this;
    }

    @Override // cn.hutool.core.lang.mutable.Mutable
    public Pair<K, V> get() {
        return this;
    }

    @Override // cn.hutool.core.lang.mutable.Mutable
    public void set(Pair<K, V> pair) {
        this.key = pair.getKey();
        this.value = pair.getValue();
    }
}
