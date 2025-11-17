package cn.hutool.setting;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/setting/GroupedMap.class */
public class GroupedMap extends LinkedHashMap<String, LinkedHashMap<String, String>> {
    private static final long serialVersionUID = -7777365130776081931L;
    private final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = this.cacheLock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = this.cacheLock.writeLock();
    private int size = -1;

    public String get(String group, String key) {
        this.readLock.lock();
        try {
            LinkedHashMap<String, String> map = get((Object) StrUtil.nullToEmpty(group));
            if (MapUtil.isNotEmpty(map)) {
                String str = map.get(key);
                this.readLock.unlock();
                return str;
            }
            this.readLock.unlock();
            return null;
        } catch (Throwable th) {
            this.readLock.unlock();
            throw th;
        }
    }

    @Override // java.util.LinkedHashMap, java.util.HashMap, java.util.AbstractMap, java.util.Map
    public LinkedHashMap<String, String> get(Object key) {
        this.readLock.lock();
        try {
            return (LinkedHashMap) super.get(key);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public int size() {
        this.writeLock.lock();
        try {
            if (this.size < 0) {
                this.size = 0;
                for (LinkedHashMap<String, String> value : values()) {
                    this.size += value.size();
                }
            }
            return this.size;
        } finally {
            this.writeLock.unlock();
        }
    }

    public String put(String group, String key, String value) {
        String group2 = StrUtil.nullToEmpty(group).trim();
        this.writeLock.lock();
        try {
            LinkedHashMap<String, String> valueMap = (LinkedHashMap) computeIfAbsent(group2, k -> {
                return new LinkedHashMap();
            });
            this.size = -1;
            String put = valueMap.put(key, value);
            this.writeLock.unlock();
            return put;
        } catch (Throwable th) {
            this.writeLock.unlock();
            throw th;
        }
    }

    public GroupedMap putAll(String group, Map<? extends String, ? extends String> m) {
        for (Map.Entry<? extends String, ? extends String> entry : m.entrySet()) {
            put(group, entry.getKey(), entry.getValue());
        }
        return this;
    }

    public String remove(String group, String key) {
        String group2 = StrUtil.nullToEmpty(group).trim();
        this.writeLock.lock();
        try {
            LinkedHashMap<String, String> valueMap = get((Object) group2);
            if (MapUtil.isNotEmpty(valueMap)) {
                String remove = valueMap.remove(key);
                this.writeLock.unlock();
                return remove;
            }
            this.writeLock.unlock();
            return null;
        } catch (Throwable th) {
            this.writeLock.unlock();
            throw th;
        }
    }

    public boolean isEmpty(String group) {
        String group2 = StrUtil.nullToEmpty(group).trim();
        this.readLock.lock();
        try {
            LinkedHashMap<String, String> valueMap = get((Object) group2);
            if (MapUtil.isNotEmpty(valueMap)) {
                boolean isEmpty = valueMap.isEmpty();
                this.readLock.unlock();
                return isEmpty;
            }
            this.readLock.unlock();
            return true;
        } catch (Throwable th) {
            this.readLock.unlock();
            throw th;
        }
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean containsKey(String group, String key) {
        String group2 = StrUtil.nullToEmpty(group).trim();
        this.readLock.lock();
        try {
            LinkedHashMap<String, String> valueMap = get((Object) group2);
            if (MapUtil.isNotEmpty(valueMap)) {
                boolean containsKey = valueMap.containsKey(key);
                this.readLock.unlock();
                return containsKey;
            }
            this.readLock.unlock();
            return false;
        } catch (Throwable th) {
            this.readLock.unlock();
            throw th;
        }
    }

    public boolean containsValue(String group, String value) {
        String group2 = StrUtil.nullToEmpty(group).trim();
        this.readLock.lock();
        try {
            LinkedHashMap<String, String> valueMap = get((Object) group2);
            if (MapUtil.isNotEmpty(valueMap)) {
                boolean containsValue = valueMap.containsValue(value);
                this.readLock.unlock();
                return containsValue;
            }
            this.readLock.unlock();
            return false;
        } catch (Throwable th) {
            this.readLock.unlock();
            throw th;
        }
    }

    public GroupedMap clear(String group) {
        String group2 = StrUtil.nullToEmpty(group).trim();
        this.writeLock.lock();
        try {
            LinkedHashMap<String, String> valueMap = get((Object) group2);
            if (MapUtil.isNotEmpty(valueMap)) {
                valueMap.clear();
            }
            return this;
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override // java.util.LinkedHashMap, java.util.HashMap, java.util.AbstractMap, java.util.Map
    public Set<String> keySet() {
        this.readLock.lock();
        try {
            return super.keySet();
        } finally {
            this.readLock.unlock();
        }
    }

    public Set<String> keySet(String group) {
        String group2 = StrUtil.nullToEmpty(group).trim();
        this.readLock.lock();
        try {
            LinkedHashMap<String, String> valueMap = get((Object) group2);
            if (MapUtil.isNotEmpty(valueMap)) {
                Set<String> keySet = valueMap.keySet();
                this.readLock.unlock();
                return keySet;
            }
            this.readLock.unlock();
            return Collections.emptySet();
        } catch (Throwable th) {
            this.readLock.unlock();
            throw th;
        }
    }

    public Collection<String> values(String group) {
        String group2 = StrUtil.nullToEmpty(group).trim();
        this.readLock.lock();
        try {
            LinkedHashMap<String, String> valueMap = get((Object) group2);
            if (MapUtil.isNotEmpty(valueMap)) {
                Collection<String> values = valueMap.values();
                this.readLock.unlock();
                return values;
            }
            this.readLock.unlock();
            return Collections.emptyList();
        } catch (Throwable th) {
            this.readLock.unlock();
            throw th;
        }
    }

    @Override // java.util.LinkedHashMap, java.util.HashMap, java.util.AbstractMap, java.util.Map
    public Set<Map.Entry<String, LinkedHashMap<String, String>>> entrySet() {
        this.readLock.lock();
        try {
            return super.entrySet();
        } finally {
            this.readLock.unlock();
        }
    }

    public Set<Map.Entry<String, String>> entrySet(String group) {
        String group2 = StrUtil.nullToEmpty(group).trim();
        this.readLock.lock();
        try {
            LinkedHashMap<String, String> valueMap = get((Object) group2);
            if (MapUtil.isNotEmpty(valueMap)) {
                Set<Map.Entry<String, String>> entrySet = valueMap.entrySet();
                this.readLock.unlock();
                return entrySet;
            }
            this.readLock.unlock();
            return Collections.emptySet();
        } catch (Throwable th) {
            this.readLock.unlock();
            throw th;
        }
    }

    @Override // java.util.AbstractMap
    public String toString() {
        this.readLock.lock();
        try {
            return super.toString();
        } finally {
            this.readLock.unlock();
        }
    }
}
