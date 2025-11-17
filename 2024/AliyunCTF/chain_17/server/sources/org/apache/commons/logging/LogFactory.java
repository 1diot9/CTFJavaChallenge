package org.apache.commons.logging;

/* loaded from: server.jar:BOOT-INF/lib/spring-jcl-6.1.3.jar:org/apache/commons/logging/LogFactory.class */
public abstract class LogFactory {
    @Deprecated
    public abstract Object getAttribute(String name);

    @Deprecated
    public abstract String[] getAttributeNames();

    @Deprecated
    public abstract void removeAttribute(String name);

    @Deprecated
    public abstract void setAttribute(String name, Object value);

    @Deprecated
    public abstract void release();

    public static Log getLog(Class<?> clazz) {
        return getLog(clazz.getName());
    }

    public static Log getLog(String name) {
        return LogAdapter.createLog(name);
    }

    @Deprecated
    public static LogFactory getFactory() {
        return new LogFactory() { // from class: org.apache.commons.logging.LogFactory.1
            @Override // org.apache.commons.logging.LogFactory
            public Object getAttribute(String name) {
                return null;
            }

            @Override // org.apache.commons.logging.LogFactory
            public String[] getAttributeNames() {
                return new String[0];
            }

            @Override // org.apache.commons.logging.LogFactory
            public void removeAttribute(String name) {
            }

            @Override // org.apache.commons.logging.LogFactory
            public void setAttribute(String name, Object value) {
            }

            @Override // org.apache.commons.logging.LogFactory
            public void release() {
            }
        };
    }

    @Deprecated
    public Log getInstance(Class<?> clazz) {
        return getLog(clazz);
    }

    @Deprecated
    public Log getInstance(String name) {
        return getLog(name);
    }

    @Deprecated
    public static void release(ClassLoader classLoader) {
    }

    @Deprecated
    public static void releaseAll() {
    }

    @Deprecated
    public static String objectId(Object o) {
        return o == null ? "null" : o.getClass().getName() + "@" + System.identityHashCode(o);
    }
}
