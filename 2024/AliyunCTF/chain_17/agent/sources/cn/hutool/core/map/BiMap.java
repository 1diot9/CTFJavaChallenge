package cn.hutool.core.map;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/BiMap.class */
public class BiMap<K, V> extends MapWrapper<K, V> {
    private static final long serialVersionUID = 1;
    private Map<V, K> inverse;

    public BiMap(Map<K, V> raw) {
        super(raw);
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public V put(K k, V v) {
        if (null != this.inverse) {
            this.inverse.put(v, k);
        }
        return (V) super.put(k, v);
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public void putAll(Map<? extends K, ? extends V> m) {
        super.putAll(m);
        if (null != this.inverse) {
            m.forEach((key, value) -> {
                this.inverse.put(value, key);
            });
        }
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public V remove(Object obj) {
        V v = (V) super.remove(obj);
        if (null != this.inverse && null != v) {
            this.inverse.remove(v);
        }
        return v;
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public boolean remove(Object key, Object value) {
        return super.remove(key, value) && null != this.inverse && this.inverse.remove(value, key);
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public void clear() {
        super.clear();
        this.inverse = null;
    }

    public Map<V, K> getInverse() {
        if (null == this.inverse) {
            this.inverse = MapUtil.inverse(getRaw());
        }
        return this.inverse;
    }

    public K getKey(V value) {
        return getInverse().get(value);
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public V putIfAbsent(K k, V v) {
        if (null != this.inverse) {
            this.inverse.putIfAbsent(v, k);
        }
        return (V) super.putIfAbsent(k, v);
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public V computeIfAbsent(K k, Function<? super K, ? extends V> function) {
        V v = (V) super.computeIfAbsent(k, function);
        resetInverseMap();
        return v;
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public V computeIfPresent(K k, BiFunction<? super K, ? super V, ? extends V> biFunction) {
        V v = (V) super.computeIfPresent(k, biFunction);
        resetInverseMap();
        return v;
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public V compute(K k, BiFunction<? super K, ? super V, ? extends V> biFunction) {
        V v = (V) super.compute(k, biFunction);
        resetInverseMap();
        return v;
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public V merge(K k, V v, BiFunction<? super V, ? super V, ? extends V> biFunction) {
        V v2 = (V) super.merge(k, v, biFunction);
        resetInverseMap();
        return v2;
    }

    private void resetInverseMap() {
        if (null != this.inverse) {
            this.inverse = null;
        }
    }
}
