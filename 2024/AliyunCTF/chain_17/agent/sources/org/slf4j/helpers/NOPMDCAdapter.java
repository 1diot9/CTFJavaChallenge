package org.slf4j.helpers;

import java.util.Deque;
import java.util.Map;
import org.slf4j.spi.MDCAdapter;

/* loaded from: agent.jar:BOOT-INF/lib/slf4j-api-2.0.11.jar:org/slf4j/helpers/NOPMDCAdapter.class */
public class NOPMDCAdapter implements MDCAdapter {
    @Override // org.slf4j.spi.MDCAdapter
    public void clear() {
    }

    @Override // org.slf4j.spi.MDCAdapter
    public String get(String key) {
        return null;
    }

    @Override // org.slf4j.spi.MDCAdapter
    public void put(String key, String val) {
    }

    @Override // org.slf4j.spi.MDCAdapter
    public void remove(String key) {
    }

    @Override // org.slf4j.spi.MDCAdapter
    public Map<String, String> getCopyOfContextMap() {
        return null;
    }

    @Override // org.slf4j.spi.MDCAdapter
    public void setContextMap(Map<String, String> contextMap) {
    }

    @Override // org.slf4j.spi.MDCAdapter
    public void pushByKey(String key, String value) {
    }

    @Override // org.slf4j.spi.MDCAdapter
    public String popByKey(String key) {
        return null;
    }

    @Override // org.slf4j.spi.MDCAdapter
    public Deque<String> getCopyOfDequeByKey(String key) {
        return null;
    }

    @Override // org.slf4j.spi.MDCAdapter
    public void clearDequeByKey(String key) {
    }
}
