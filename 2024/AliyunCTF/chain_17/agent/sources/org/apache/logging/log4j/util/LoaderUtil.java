package org.apache.logging.log4j.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Objects;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/LoaderUtil.class */
public final class LoaderUtil {
    public static final String IGNORE_TCCL_PROPERTY = "log4j.ignoreTCL";
    private static Boolean ignoreTCCL;
    private static final boolean GET_CLASS_LOADER_DISABLED;
    private static final ClassLoader[] EMPTY_CLASS_LOADER_ARRAY = new ClassLoader[0];
    private static final SecurityManager SECURITY_MANAGER = System.getSecurityManager();
    private static final PrivilegedAction<ClassLoader> TCCL_GETTER = new ThreadContextClassLoaderGetter();

    static {
        boolean getClassLoaderDisabled;
        if (SECURITY_MANAGER != null) {
            try {
                SECURITY_MANAGER.checkPermission(new RuntimePermission("getClassLoader"));
                getClassLoaderDisabled = false;
            } catch (SecurityException e) {
                getClassLoaderDisabled = true;
            }
            GET_CLASS_LOADER_DISABLED = getClassLoaderDisabled;
            return;
        }
        GET_CLASS_LOADER_DISABLED = false;
    }

    private LoaderUtil() {
    }

    public static ClassLoader getThreadContextClassLoader() {
        if (GET_CLASS_LOADER_DISABLED) {
            return LoaderUtil.class.getClassLoader();
        }
        return SECURITY_MANAGER == null ? TCCL_GETTER.run() : (ClassLoader) AccessController.doPrivileged(TCCL_GETTER);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/LoaderUtil$ThreadContextClassLoaderGetter.class */
    private static class ThreadContextClassLoaderGetter implements PrivilegedAction<ClassLoader> {
        private ThreadContextClassLoaderGetter() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public ClassLoader run() {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl != null) {
                return cl;
            }
            ClassLoader ccl = LoaderUtil.class.getClassLoader();
            return (ccl != null || LoaderUtil.GET_CLASS_LOADER_DISABLED) ? ccl : ClassLoader.getSystemClassLoader();
        }
    }

    public static boolean isClassAvailable(final String className) {
        try {
            Class<?> clazz = loadClass(className);
            return clazz != null;
        } catch (ClassNotFoundException | LinkageError e) {
            return false;
        } catch (Throwable e2) {
            LowLevelLogUtil.logException("Unknown error checking for existence of class: " + className, e2);
            return false;
        }
    }

    public static Class<?> loadClass(final String className) throws ClassNotFoundException {
        if (isIgnoreTccl()) {
            return Class.forName(className);
        }
        try {
            ClassLoader tccl = getThreadContextClassLoader();
            if (tccl != null) {
                return tccl.loadClass(className);
            }
        } catch (Throwable th) {
        }
        return Class.forName(className);
    }

    public static <T> T newInstanceOf(final Class<T> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        try {
            return clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (NoSuchMethodException e) {
            return clazz.newInstance();
        }
    }

    public static <T> T newInstanceOf(String str) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
        return (T) newInstanceOf(loadClass(str));
    }

    public static <T> T newCheckedInstanceOf(final String className, final Class<T> clazz) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return clazz.cast(newInstanceOf(className));
    }

    public static <T> T newCheckedInstanceOfProperty(String str, Class<T> cls) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String stringProperty = PropertiesUtil.getProperties().getStringProperty(str);
        if (stringProperty == null) {
            return null;
        }
        return (T) newCheckedInstanceOf(stringProperty, cls);
    }

    private static boolean isIgnoreTccl() {
        if (ignoreTCCL == null) {
            String ignoreTccl = PropertiesUtil.getProperties().getStringProperty(IGNORE_TCCL_PROPERTY, null);
            ignoreTCCL = Boolean.valueOf((ignoreTccl == null || "false".equalsIgnoreCase(ignoreTccl.trim())) ? false : true);
        }
        return ignoreTCCL.booleanValue();
    }

    public static Collection<URL> findResources(final String resource) {
        return findResources(resource, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Collection<URL> findResources(final String resource, final boolean useTccl) {
        Collection<UrlResource> urlResources = findUrlResources(resource, useTccl);
        Collection<URL> resources = new LinkedHashSet<>(urlResources.size());
        for (UrlResource urlResource : urlResources) {
            resources.add(urlResource.getUrl());
        }
        return resources;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Collection<UrlResource> findUrlResources(final String resource, final boolean useTccl) {
        ClassLoader[] candidates = new ClassLoader[3];
        candidates[0] = useTccl ? getThreadContextClassLoader() : null;
        candidates[1] = LoaderUtil.class.getClassLoader();
        candidates[2] = GET_CLASS_LOADER_DISABLED ? null : ClassLoader.getSystemClassLoader();
        Collection<UrlResource> resources = new LinkedHashSet<>();
        for (ClassLoader cl : candidates) {
            if (cl != null) {
                try {
                    Enumeration<URL> resourceEnum = cl.getResources(resource);
                    while (resourceEnum.hasMoreElements()) {
                        resources.add(new UrlResource(cl, resourceEnum.nextElement()));
                    }
                } catch (IOException e) {
                    LowLevelLogUtil.logException(e);
                }
            }
        }
        return resources;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/LoaderUtil$UrlResource.class */
    public static class UrlResource {
        private final ClassLoader classLoader;
        private final URL url;

        UrlResource(final ClassLoader classLoader, final URL url) {
            this.classLoader = classLoader;
            this.url = url;
        }

        public ClassLoader getClassLoader() {
            return this.classLoader;
        }

        public URL getUrl() {
            return this.url;
        }

        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            UrlResource that = (UrlResource) o;
            if (this.classLoader != null) {
                if (!this.classLoader.equals(that.classLoader)) {
                    return false;
                }
            } else if (that.classLoader != null) {
                return false;
            }
            if (this.url != null) {
                if (!this.url.equals(that.url)) {
                    return false;
                }
                return true;
            }
            if (that.url != null) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return Objects.hashCode(this.classLoader) + Objects.hashCode(this.url);
        }
    }
}
