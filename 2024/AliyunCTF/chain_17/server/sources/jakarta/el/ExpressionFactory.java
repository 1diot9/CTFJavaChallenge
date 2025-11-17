package jakarta.el;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/ExpressionFactory.class */
public abstract class ExpressionFactory {
    private static final boolean IS_SECURITY_ENABLED;
    private static final String PROPERTY_NAME = "jakarta.el.ExpressionFactory";
    private static final String PROPERTY_FILE;
    private static final CacheValue nullTcclFactory;
    private static final Map<CacheKey, CacheValue> factoryCache;

    public abstract ValueExpression createValueExpression(ELContext eLContext, String str, Class<?> cls);

    public abstract ValueExpression createValueExpression(Object obj, Class<?> cls);

    public abstract MethodExpression createMethodExpression(ELContext eLContext, String str, Class<?> cls, Class<?>[] clsArr);

    public abstract <T> T coerceToType(Object obj, Class<T> cls);

    static {
        IS_SECURITY_ENABLED = System.getSecurityManager() != null;
        nullTcclFactory = new CacheValue();
        factoryCache = new ConcurrentHashMap();
        if (IS_SECURITY_ENABLED) {
            PROPERTY_FILE = (String) AccessController.doPrivileged(() -> {
                return System.getProperty("java.home") + File.separator + "lib" + File.separator + "el.properties";
            });
        } else {
            PROPERTY_FILE = System.getProperty("java.home") + File.separator + "lib" + File.separator + "el.properties";
        }
    }

    public static ExpressionFactory newInstance() {
        return newInstance(null);
    }

    public static ExpressionFactory newInstance(Properties properties) {
        CacheValue cacheValue;
        ExpressionFactory result;
        ClassLoader tccl = Util.getContextClassLoader();
        if (tccl == null) {
            cacheValue = nullTcclFactory;
        } else {
            CacheKey key = new CacheKey(tccl);
            cacheValue = factoryCache.get(key);
            if (cacheValue == null) {
                CacheValue newCacheValue = new CacheValue();
                cacheValue = factoryCache.putIfAbsent(key, newCacheValue);
                if (cacheValue == null) {
                    cacheValue = newCacheValue;
                }
            }
        }
        Lock readLock = cacheValue.getLock().readLock();
        readLock.lock();
        try {
            Class<?> clazz = cacheValue.getFactoryClass();
            readLock.unlock();
            if (clazz == null) {
                String className = null;
                try {
                    Lock writeLock = cacheValue.getLock().writeLock();
                    writeLock.lock();
                    try {
                        className = cacheValue.getFactoryClassName();
                        if (className == null) {
                            className = discoverClassName(tccl);
                            cacheValue.setFactoryClassName(className);
                        }
                        if (tccl == null) {
                            clazz = Class.forName(className);
                        } else {
                            clazz = tccl.loadClass(className);
                        }
                        cacheValue.setFactoryClass(clazz);
                        writeLock.unlock();
                    } catch (Throwable th) {
                        writeLock.unlock();
                        throw th;
                    }
                } catch (ClassNotFoundException e) {
                    throw new ELException(Util.message(null, "expressionFactory.cannotFind", className), e);
                }
            }
            Constructor<?> constructor = null;
            try {
                if (properties != null) {
                    try {
                        constructor = clazz.getConstructor(Properties.class);
                    } catch (NoSuchMethodException e2) {
                    } catch (SecurityException se) {
                        throw new ELException(se);
                    }
                }
                if (constructor == null) {
                    result = (ExpressionFactory) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                } else {
                    result = (ExpressionFactory) constructor.newInstance(properties);
                }
                return result;
            } catch (IllegalArgumentException | ReflectiveOperationException e3) {
                throw new ELException(Util.message(null, "expressionFactory.cannotCreate", clazz.getName()), e3);
            } catch (InvocationTargetException e4) {
                Throwable cause = e4.getCause();
                Util.handleThrowable(cause);
                throw new ELException(Util.message(null, "expressionFactory.cannotCreate", clazz.getName()), e4);
            }
        } catch (Throwable th2) {
            readLock.unlock();
            throw th2;
        }
    }

    public ELResolver getStreamELResolver() {
        return null;
    }

    public Map<String, Method> getInitFunctionMap() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/ExpressionFactory$CacheKey.class */
    public static class CacheKey {
        private final int hash;
        private final WeakReference<ClassLoader> ref;

        CacheKey(ClassLoader cl) {
            this.hash = cl.hashCode();
            this.ref = new WeakReference<>(cl);
        }

        public int hashCode() {
            return this.hash;
        }

        public boolean equals(Object obj) {
            ClassLoader thisCl;
            if (obj == this) {
                return true;
            }
            return (obj instanceof CacheKey) && (thisCl = this.ref.get()) != null && thisCl == ((CacheKey) obj).ref.get();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/ExpressionFactory$CacheValue.class */
    public static class CacheValue {
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private String className;
        private WeakReference<Class<?>> ref;

        CacheValue() {
        }

        public ReadWriteLock getLock() {
            return this.lock;
        }

        public String getFactoryClassName() {
            return this.className;
        }

        public void setFactoryClassName(String className) {
            this.className = className;
        }

        public Class<?> getFactoryClass() {
            if (this.ref != null) {
                return this.ref.get();
            }
            return null;
        }

        public void setFactoryClass(Class<?> clazz) {
            this.ref = new WeakReference<>(clazz);
        }
    }

    private static String discoverClassName(ClassLoader tccl) {
        String className = getClassNameServices(tccl);
        if (className == null) {
            if (IS_SECURITY_ENABLED) {
                className = (String) AccessController.doPrivileged(ExpressionFactory::getClassNameJreDir);
            } else {
                className = getClassNameJreDir();
            }
        }
        if (className == null) {
            if (IS_SECURITY_ENABLED) {
                className = (String) AccessController.doPrivileged(ExpressionFactory::getClassNameSysProp);
            } else {
                className = getClassNameSysProp();
            }
        }
        if (className == null) {
            className = "org.apache.el.ExpressionFactoryImpl";
        }
        return className;
    }

    private static String getClassNameServices(ClassLoader tccl) {
        ExpressionFactory result = null;
        ServiceLoader<ExpressionFactory> serviceLoader = ServiceLoader.load(ExpressionFactory.class, tccl);
        Iterator<ExpressionFactory> iter = serviceLoader.iterator();
        while (result == null && iter.hasNext()) {
            result = iter.next();
        }
        if (result == null) {
            return null;
        }
        return result.getClass().getName();
    }

    private static String getClassNameJreDir() {
        File file = new File(PROPERTY_FILE);
        if (file.canRead()) {
            try {
                InputStream is = new FileInputStream(file);
                try {
                    Properties props = new Properties();
                    props.load(is);
                    String value = props.getProperty(PROPERTY_NAME);
                    if (value != null && value.trim().length() > 0) {
                        String trim = value.trim();
                        is.close();
                        return trim;
                    }
                    is.close();
                    return null;
                } catch (Throwable th) {
                    try {
                        is.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            } catch (FileNotFoundException e) {
                return null;
            } catch (IOException e2) {
                throw new ELException(Util.message(null, "expressionFactory.readFailed", PROPERTY_FILE), e2);
            }
        }
        return null;
    }

    private static String getClassNameSysProp() {
        String value = System.getProperty(PROPERTY_NAME);
        if (value != null && value.trim().length() > 0) {
            return value.trim();
        }
        return null;
    }
}
