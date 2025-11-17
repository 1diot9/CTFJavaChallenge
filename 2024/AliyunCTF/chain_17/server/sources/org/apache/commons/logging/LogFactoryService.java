package org.apache.commons.logging;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
/* loaded from: server.jar:BOOT-INF/lib/spring-jcl-6.1.3.jar:org/apache/commons/logging/LogFactoryService.class */
public class LogFactoryService extends LogFactory {
    private final Map<String, Object> attributes = new ConcurrentHashMap();

    public LogFactoryService() {
        System.out.println("Standard Commons Logging discovery in action with spring-jcl: please remove commons-logging.jar from classpath in order to avoid potential conflicts");
    }

    @Override // org.apache.commons.logging.LogFactory
    public Log getInstance(Class<?> clazz) {
        return getInstance(clazz.getName());
    }

    @Override // org.apache.commons.logging.LogFactory
    public Log getInstance(String name) {
        return LogAdapter.createLog(name);
    }

    @Override // org.apache.commons.logging.LogFactory
    public void setAttribute(String name, Object value) {
        if (value != null) {
            this.attributes.put(name, value);
        } else {
            this.attributes.remove(name);
        }
    }

    @Override // org.apache.commons.logging.LogFactory
    public void removeAttribute(String name) {
        this.attributes.remove(name);
    }

    @Override // org.apache.commons.logging.LogFactory
    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    @Override // org.apache.commons.logging.LogFactory
    public String[] getAttributeNames() {
        return (String[]) this.attributes.keySet().toArray(new String[0]);
    }

    @Override // org.apache.commons.logging.LogFactory
    public void release() {
    }
}
