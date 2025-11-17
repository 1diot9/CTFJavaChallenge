package cn.hutool.core.lang;

import cn.hutool.core.clone.CloneSupport;
import java.io.Serializable;
import java.util.Objects;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/Pair.class */
public class Pair<K, V> extends CloneSupport<Pair<K, V>> implements Serializable {
    private static final long serialVersionUID = 1;
    protected K key;
    protected V value;

    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    public String toString() {
        return "Pair [key=" + this.key + ", value=" + this.value + "]";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Pair) {
            Pair<?, ?> pair = (Pair) o;
            return Objects.equals(getKey(), pair.getKey()) && Objects.equals(getValue(), pair.getValue());
        }
        return false;
    }

    public int hashCode() {
        return Objects.hashCode(this.key) ^ Objects.hashCode(this.value);
    }
}
