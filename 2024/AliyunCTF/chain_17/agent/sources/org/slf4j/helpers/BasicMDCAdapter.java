package org.slf4j.helpers;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.spi.MDCAdapter;

/* loaded from: agent.jar:BOOT-INF/lib/slf4j-api-2.0.11.jar:org/slf4j/helpers/BasicMDCAdapter.class */
public class BasicMDCAdapter implements MDCAdapter {
    private final ThreadLocalMapOfStacks threadLocalMapOfDeques = new ThreadLocalMapOfStacks();
    private final InheritableThreadLocal<Map<String, String>> inheritableThreadLocalMap = new InheritableThreadLocal<Map<String, String>>() { // from class: org.slf4j.helpers.BasicMDCAdapter.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.InheritableThreadLocal
        public Map<String, String> childValue(Map<String, String> parentValue) {
            if (parentValue == null) {
                return null;
            }
            return new HashMap(parentValue);
        }
    };

    @Override // org.slf4j.spi.MDCAdapter
    public void put(String key, String val) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        Map<String, String> map = this.inheritableThreadLocalMap.get();
        if (map == null) {
            map = new HashMap();
            this.inheritableThreadLocalMap.set(map);
        }
        map.put(key, val);
    }

    @Override // org.slf4j.spi.MDCAdapter
    public String get(String key) {
        Map<String, String> map = this.inheritableThreadLocalMap.get();
        if (map != null && key != null) {
            return map.get(key);
        }
        return null;
    }

    @Override // org.slf4j.spi.MDCAdapter
    public void remove(String key) {
        Map<String, String> map = this.inheritableThreadLocalMap.get();
        if (map != null) {
            map.remove(key);
        }
    }

    @Override // org.slf4j.spi.MDCAdapter
    public void clear() {
        Map<String, String> map = this.inheritableThreadLocalMap.get();
        if (map != null) {
            map.clear();
            this.inheritableThreadLocalMap.remove();
        }
    }

    public Set<String> getKeys() {
        Map<String, String> map = this.inheritableThreadLocalMap.get();
        if (map != null) {
            return map.keySet();
        }
        return null;
    }

    @Override // org.slf4j.spi.MDCAdapter
    public Map<String, String> getCopyOfContextMap() {
        Map<String, String> oldMap = this.inheritableThreadLocalMap.get();
        if (oldMap != null) {
            return new HashMap(oldMap);
        }
        return null;
    }

    @Override // org.slf4j.spi.MDCAdapter
    public void setContextMap(Map<String, String> contextMap) {
        Map<String, String> copy = null;
        if (contextMap != null) {
            copy = new HashMap<>(contextMap);
        }
        this.inheritableThreadLocalMap.set(copy);
    }

    @Override // org.slf4j.spi.MDCAdapter
    public void pushByKey(String key, String value) {
        this.threadLocalMapOfDeques.pushByKey(key, value);
    }

    @Override // org.slf4j.spi.MDCAdapter
    public String popByKey(String key) {
        return this.threadLocalMapOfDeques.popByKey(key);
    }

    @Override // org.slf4j.spi.MDCAdapter
    public Deque<String> getCopyOfDequeByKey(String key) {
        return this.threadLocalMapOfDeques.getCopyOfDequeByKey(key);
    }

    @Override // org.slf4j.spi.MDCAdapter
    public void clearDequeByKey(String key) {
        this.threadLocalMapOfDeques.clearDequeByKey(key);
    }
}
