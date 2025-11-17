package org.apache.tomcat;

import java.lang.reflect.InvocationTargetException;
import javax.naming.NamingException;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/SimpleInstanceManager.class */
public class SimpleInstanceManager implements InstanceManager {
    @Override // org.apache.tomcat.InstanceManager
    public Object newInstance(Class<?> clazz) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, NoSuchMethodException {
        return prepareInstance(clazz.getConstructor(new Class[0]).newInstance(new Object[0]));
    }

    @Override // org.apache.tomcat.InstanceManager
    public Object newInstance(String className) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, ClassNotFoundException, NoSuchMethodException {
        Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        return prepareInstance(clazz.getConstructor(new Class[0]).newInstance(new Object[0]));
    }

    @Override // org.apache.tomcat.InstanceManager
    public Object newInstance(String fqcn, ClassLoader classLoader) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, ClassNotFoundException, NoSuchMethodException {
        Class<?> clazz = classLoader.loadClass(fqcn);
        return prepareInstance(clazz.getConstructor(new Class[0]).newInstance(new Object[0]));
    }

    @Override // org.apache.tomcat.InstanceManager
    public void newInstance(Object o) throws IllegalAccessException, InvocationTargetException, NamingException {
    }

    @Override // org.apache.tomcat.InstanceManager
    public void destroyInstance(Object o) throws IllegalAccessException, InvocationTargetException {
    }

    private Object prepareInstance(Object o) {
        return o;
    }
}
