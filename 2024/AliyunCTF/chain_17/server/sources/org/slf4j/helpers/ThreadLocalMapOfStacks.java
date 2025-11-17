package org.slf4j.helpers;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/slf4j-api-2.0.11.jar:org/slf4j/helpers/ThreadLocalMapOfStacks.class */
public class ThreadLocalMapOfStacks {
    final ThreadLocal<Map<String, Deque<String>>> tlMapOfStacks = new ThreadLocal<>();

    public void pushByKey(String key, String value) {
        if (key == null) {
            return;
        }
        Map<String, Deque<String>> map = this.tlMapOfStacks.get();
        if (map == null) {
            map = new HashMap();
            this.tlMapOfStacks.set(map);
        }
        Deque<String> deque = map.get(key);
        if (deque == null) {
            deque = new ArrayDeque();
        }
        deque.push(value);
        map.put(key, deque);
    }

    public String popByKey(String key) {
        Map<String, Deque<String>> map;
        Deque<String> deque;
        if (key == null || (map = this.tlMapOfStacks.get()) == null || (deque = map.get(key)) == null) {
            return null;
        }
        return deque.pop();
    }

    public Deque<String> getCopyOfDequeByKey(String key) {
        Map<String, Deque<String>> map;
        Deque<String> deque;
        if (key == null || (map = this.tlMapOfStacks.get()) == null || (deque = map.get(key)) == null) {
            return null;
        }
        return new ArrayDeque(deque);
    }

    public void clearDequeByKey(String key) {
        Map<String, Deque<String>> map;
        Deque<String> deque;
        if (key == null || (map = this.tlMapOfStacks.get()) == null || (deque = map.get(key)) == null) {
            return;
        }
        deque.clear();
    }
}
