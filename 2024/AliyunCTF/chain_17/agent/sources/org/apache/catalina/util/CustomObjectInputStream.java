package org.apache.catalina.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/util/CustomObjectInputStream.class */
public final class CustomObjectInputStream extends ObjectInputStream {
    private static final StringManager sm = StringManager.getManager((Class<?>) CustomObjectInputStream.class);
    private static final WeakHashMap<ClassLoader, Set<String>> reportedClassCache = new WeakHashMap<>();
    private final ClassLoader classLoader;
    private final Set<String> reportedClasses;
    private final Log log;
    private final Pattern allowedClassNamePattern;
    private final String allowedClassNameFilter;
    private final boolean warnOnFailure;

    public CustomObjectInputStream(InputStream stream, ClassLoader classLoader) throws IOException {
        this(stream, classLoader, null, null, false);
    }

    public CustomObjectInputStream(InputStream stream, ClassLoader classLoader, Log log, Pattern allowedClassNamePattern, boolean warnOnFailure) throws IOException {
        super(stream);
        Set<String> reportedClasses;
        Set<String> original;
        if (log == null && allowedClassNamePattern != null && warnOnFailure) {
            throw new IllegalArgumentException(sm.getString("customObjectInputStream.logRequired"));
        }
        this.classLoader = classLoader;
        this.log = log;
        this.allowedClassNamePattern = allowedClassNamePattern;
        if (allowedClassNamePattern == null) {
            this.allowedClassNameFilter = null;
        } else {
            this.allowedClassNameFilter = allowedClassNamePattern.toString();
        }
        this.warnOnFailure = warnOnFailure;
        synchronized (reportedClassCache) {
            reportedClasses = reportedClassCache.get(classLoader);
        }
        if (reportedClasses == null) {
            reportedClasses = ConcurrentHashMap.newKeySet();
            synchronized (reportedClassCache) {
                original = reportedClassCache.putIfAbsent(classLoader, reportedClasses);
            }
            if (original != null) {
                reportedClasses = original;
            }
        }
        this.reportedClasses = reportedClasses;
    }

    @Override // java.io.ObjectInputStream
    public Class<?> resolveClass(ObjectStreamClass classDesc) throws ClassNotFoundException, IOException {
        String name = classDesc.getName();
        if (this.allowedClassNamePattern != null) {
            boolean allowed = this.allowedClassNamePattern.matcher(name).matches();
            if (!allowed) {
                boolean doLog = this.warnOnFailure && this.reportedClasses.add(name);
                String msg = sm.getString("customObjectInputStream.nomatch", name, this.allowedClassNameFilter);
                if (doLog) {
                    this.log.warn(msg);
                } else if (this.log.isDebugEnabled()) {
                    this.log.debug(msg);
                }
                throw new InvalidClassException(msg);
            }
        }
        try {
            return Class.forName(name, false, this.classLoader);
        } catch (ClassNotFoundException e) {
            try {
                return super.resolveClass(classDesc);
            } catch (ClassNotFoundException e2) {
                throw e;
            }
        }
    }

    @Override // java.io.ObjectInputStream
    protected Class<?> resolveProxyClass(String[] interfaces) throws IOException, ClassNotFoundException {
        Class<?>[] cinterfaces = new Class[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            cinterfaces[i] = this.classLoader.loadClass(interfaces[i]);
        }
        try {
            Class<?> proxyClass = Proxy.getProxyClass(this.classLoader, cinterfaces);
            return proxyClass;
        } catch (IllegalArgumentException e) {
            throw new ClassNotFoundException(null, e);
        }
    }
}
