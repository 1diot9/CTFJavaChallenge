package cn.hutool.core.map;

import cn.hutool.core.builder.Builder;
import java.util.Map;
import java.util.function.Supplier;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/MapBuilder.class */
public class MapBuilder<K, V> implements Builder<Map<K, V>> {
    private static final long serialVersionUID = 1;
    private final Map<K, V> map;

    public static <K, V> MapBuilder<K, V> create() {
        return create(false);
    }

    public static <K, V> MapBuilder<K, V> create(boolean isLinked) {
        return create(MapUtil.newHashMap(isLinked));
    }

    public static <K, V> MapBuilder<K, V> create(Map<K, V> map) {
        return new MapBuilder<>(map);
    }

    public MapBuilder(Map<K, V> map) {
        this.map = map;
    }

    public MapBuilder<K, V> put(K k, V v) {
        this.map.put(k, v);
        return this;
    }

    public MapBuilder<K, V> put(boolean condition, K k, V v) {
        if (condition) {
            put(k, v);
        }
        return this;
    }

    public MapBuilder<K, V> put(boolean condition, K k, Supplier<V> supplier) {
        if (condition) {
            put(k, supplier.get());
        }
        return this;
    }

    public MapBuilder<K, V> putAll(Map<K, V> map) {
        this.map.putAll(map);
        return this;
    }

    public MapBuilder<K, V> clear() {
        this.map.clear();
        return this;
    }

    public Map<K, V> map() {
        return this.map;
    }

    @Override // cn.hutool.core.builder.Builder
    public Map<K, V> build() {
        return map();
    }

    public String join(String separator, String keyValueSeparator) {
        return MapUtil.join(this.map, separator, keyValueSeparator, new String[0]);
    }

    public String joinIgnoreNull(String separator, String keyValueSeparator) {
        return MapUtil.joinIgnoreNull(this.map, separator, keyValueSeparator, new String[0]);
    }

    public String join(String separator, String keyValueSeparator, boolean isIgnoreNull) {
        return MapUtil.join(this.map, separator, keyValueSeparator, isIgnoreNull, new String[0]);
    }
}
