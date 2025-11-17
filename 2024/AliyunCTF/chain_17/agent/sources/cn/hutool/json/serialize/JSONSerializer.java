package cn.hutool.json.serialize;

import cn.hutool.json.JSON;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/json/serialize/JSONSerializer.class */
public interface JSONSerializer<T extends JSON, V> {
    void serialize(T t, V v);
}
