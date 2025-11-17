package ch.qos.logback.classic.util;

import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.helpers.ThreadLocalMapOfStacks;
import org.slf4j.spi.MDCAdapter;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/util/LogbackMDCAdapterSimple.class */
public class LogbackMDCAdapterSimple implements MDCAdapter {
    final ThreadLocal<Map<String, String>> threadLocalUnmodifiableMap = new ThreadLocal<>();
    private final ThreadLocalMapOfStacks threadLocalMapOfDeques = new ThreadLocalMapOfStacks();

    private Map<String, String> duplicateMap(Map<String, String> oldMap) {
        if (oldMap != null) {
            return new HashMap(oldMap);
        }
        return new HashMap();
    }

    @Override // org.slf4j.spi.MDCAdapter
    public void put(String key, String val) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        Map<String, String> oldMap = this.threadLocalUnmodifiableMap.get();
        Map<String, String> newMap = duplicateMap(oldMap);
        newMap.put(key, val);
        makeUnmodifiableAndThreadLocalSet(newMap);
    }

    private void makeUnmodifiableAndThreadLocalSet(Map<String, String> aMap) {
        Map<String, String> unmodifiable = Collections.unmodifiableMap(aMap);
        this.threadLocalUnmodifiableMap.set(unmodifiable);
    }

    @Override // org.slf4j.spi.MDCAdapter
    public void remove(String key) {
        Map<String, String> oldMap;
        if (key == null || (oldMap = this.threadLocalUnmodifiableMap.get()) == null) {
            return;
        }
        Map<String, String> newMap = duplicateMap(oldMap);
        newMap.remove(key);
        makeUnmodifiableAndThreadLocalSet(newMap);
    }

    @Override // org.slf4j.spi.MDCAdapter
    public void clear() {
        this.threadLocalUnmodifiableMap.remove();
    }

    @Override // org.slf4j.spi.MDCAdapter
    public String get(String key) {
        Map<String, String> map = this.threadLocalUnmodifiableMap.get();
        if (map != null && key != null) {
            return map.get(key);
        }
        return null;
    }

    public Map<String, String> getPropertyMap() {
        return this.threadLocalUnmodifiableMap.get();
    }

    public Set<String> getKeys() {
        Map<String, String> map = getPropertyMap();
        if (map != null) {
            return map.keySet();
        }
        return null;
    }

    @Override // org.slf4j.spi.MDCAdapter
    public Map<String, String> getCopyOfContextMap() {
        Map<String, String> hashMap = this.threadLocalUnmodifiableMap.get();
        return duplicateMap(hashMap);
    }

    @Override // org.slf4j.spi.MDCAdapter
    public void setContextMap(Map<String, String> contextMap) {
        duplicateMap(contextMap);
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
