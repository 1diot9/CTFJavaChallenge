package org.apache.catalina.webresources;

import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.catalina.webresources.war.Handler;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/webresources/TomcatURLStreamHandlerFactory.class */
public class TomcatURLStreamHandlerFactory implements URLStreamHandlerFactory {
    private static final String WAR_PROTOCOL = "war";
    private static final String CLASSPATH_PROTOCOL = "classpath";
    private static volatile TomcatURLStreamHandlerFactory instance = null;
    private final boolean registered;
    private final List<URLStreamHandlerFactory> userFactories = new CopyOnWriteArrayList();

    public static TomcatURLStreamHandlerFactory getInstance() {
        getInstanceInternal(true);
        return instance;
    }

    private static TomcatURLStreamHandlerFactory getInstanceInternal(boolean register) {
        if (instance == null) {
            synchronized (TomcatURLStreamHandlerFactory.class) {
                if (instance == null) {
                    instance = new TomcatURLStreamHandlerFactory(register);
                }
            }
        }
        return instance;
    }

    public static boolean register() {
        return getInstanceInternal(true).isRegistered();
    }

    public static boolean disable() {
        return !getInstanceInternal(false).isRegistered();
    }

    public static void release(ClassLoader classLoader) {
        if (instance == null) {
            return;
        }
        List<URLStreamHandlerFactory> factories = instance.userFactories;
        for (URLStreamHandlerFactory factory : factories) {
            ClassLoader classLoader2 = factory.getClass().getClassLoader();
            while (true) {
                ClassLoader factoryLoader = classLoader2;
                if (factoryLoader == null) {
                    break;
                }
                if (classLoader.equals(factoryLoader)) {
                    factories.remove(factory);
                    break;
                }
                classLoader2 = factoryLoader.getParent();
            }
        }
    }

    private TomcatURLStreamHandlerFactory(boolean register) {
        this.registered = register;
        if (register) {
            URL.setURLStreamHandlerFactory(this);
        }
    }

    public boolean isRegistered() {
        return this.registered;
    }

    public void addUserFactory(URLStreamHandlerFactory factory) {
        this.userFactories.add(factory);
    }

    @Override // java.net.URLStreamHandlerFactory
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if ("war".equals(protocol)) {
            return new Handler();
        }
        if (CLASSPATH_PROTOCOL.equals(protocol)) {
            return new ClasspathURLStreamHandler();
        }
        for (URLStreamHandlerFactory factory : this.userFactories) {
            URLStreamHandler handler = factory.createURLStreamHandler(protocol);
            if (handler != null) {
                return handler;
            }
        }
        return null;
    }
}
