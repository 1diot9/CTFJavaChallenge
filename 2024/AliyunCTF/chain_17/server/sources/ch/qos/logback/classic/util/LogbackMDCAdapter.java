package ch.qos.logback.classic.util;

import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.helpers.ThreadLocalMapOfStacks;
import org.slf4j.spi.MDCAdapter;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/util/LogbackMDCAdapter.class */
public class LogbackMDCAdapter implements MDCAdapter {
    final ThreadLocal<Map<String, String>> readWriteThreadLocalMap = new ThreadLocal<>();
    final ThreadLocal<Map<String, String>> readOnlyThreadLocalMap = new ThreadLocal<>();
    private final ThreadLocalMapOfStacks threadLocalMapOfDeques = new ThreadLocalMapOfStacks();

    @Override // org.slf4j.spi.MDCAdapter
    public void put(String key, String val) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        Map<String, String> current = this.readWriteThreadLocalMap.get();
        if (current == null) {
            current = new HashMap();
            this.readWriteThreadLocalMap.set(current);
        }
        current.put(key, val);
        nullifyReadOnlyThreadLocalMap();
    }

    @Override // org.slf4j.spi.MDCAdapter
    public String get(String key) {
        Map<String, String> hashMap = this.readWriteThreadLocalMap.get();
        if (hashMap != null && key != null) {
            return hashMap.get(key);
        }
        return null;
    }

    @Override // org.slf4j.spi.MDCAdapter
    public void remove(String key) {
        Map<String, String> current;
        if (key != null && (current = this.readWriteThreadLocalMap.get()) != null) {
            current.remove(key);
            nullifyReadOnlyThreadLocalMap();
        }
    }

    private void nullifyReadOnlyThreadLocalMap() {
        this.readOnlyThreadLocalMap.set(null);
    }

    @Override // org.slf4j.spi.MDCAdapter
    public void clear() {
        this.readWriteThreadLocalMap.set(null);
        nullifyReadOnlyThreadLocalMap();
    }

    public Map<String, String> getPropertyMap() {
        Map<String, String> current;
        Map<String, String> readOnlyMap = this.readOnlyThreadLocalMap.get();
        if (readOnlyMap == null && (current = this.readWriteThreadLocalMap.get()) != null) {
            Map<String, String> tempMap = new HashMap<>(current);
            readOnlyMap = Collections.unmodifiableMap(tempMap);
            this.readOnlyThreadLocalMap.set(readOnlyMap);
        }
        return readOnlyMap;
    }

    @Override // org.slf4j.spi.MDCAdapter
    public Map getCopyOfContextMap() {
        Map<String, String> readOnlyMap = getPropertyMap();
        if (readOnlyMap == null) {
            return null;
        }
        return new HashMap(readOnlyMap);
    }

    public Set<String> getKeys() {
        Map<String, String> readOnlyMap = getPropertyMap();
        if (readOnlyMap != null) {
            return readOnlyMap.keySet();
        }
        return null;
    }

    @Override // org.slf4j.spi.MDCAdapter
    public void setContextMap(Map contextMap) {
        if (contextMap != null) {
            this.readWriteThreadLocalMap.set(new HashMap(contextMap));
        } else {
            this.readWriteThreadLocalMap.set(null);
        }
        nullifyReadOnlyThreadLocalMap();
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
