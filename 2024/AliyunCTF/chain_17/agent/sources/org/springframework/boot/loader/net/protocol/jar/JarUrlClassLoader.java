package org.springframework.boot.loader.net.protocol.jar;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;
import org.springframework.boot.loader.jar.NestedJarFile;

/* loaded from: agent.jar:org/springframework/boot/loader/net/protocol/jar/JarUrlClassLoader.class */
public abstract class JarUrlClassLoader extends URLClassLoader {
    private final URL[] urls;
    private final boolean hasJarUrls;
    private final Map<URL, JarFile> jarFiles;
    private final Set<String> undefinablePackages;

    public JarUrlClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.jarFiles = new ConcurrentHashMap();
        this.undefinablePackages = Collections.newSetFromMap(new ConcurrentHashMap());
        this.urls = urls;
        this.hasJarUrls = Arrays.stream(urls).anyMatch(this::isJarUrl);
    }

    @Override // java.net.URLClassLoader, java.lang.ClassLoader
    public URL findResource(String name) {
        if (!this.hasJarUrls) {
            return super.findResource(name);
        }
        Optimizations.enable(false);
        try {
            URL findResource = super.findResource(name);
            Optimizations.disable();
            return findResource;
        } catch (Throwable th) {
            Optimizations.disable();
            throw th;
        }
    }

    @Override // java.net.URLClassLoader, java.lang.ClassLoader
    public Enumeration<URL> findResources(String name) throws IOException {
        if (!this.hasJarUrls) {
            return super.findResources(name);
        }
        Optimizations.enable(false);
        try {
            OptimizedEnumeration optimizedEnumeration = new OptimizedEnumeration(super.findResources(name));
            Optimizations.disable();
            return optimizedEnumeration;
        } catch (Throwable th) {
            Optimizations.disable();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // java.lang.ClassLoader
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (!this.hasJarUrls) {
            return super.loadClass(name, resolve);
        }
        Optimizations.enable(true);
        try {
            try {
                definePackageIfNecessary(name);
            } catch (IllegalArgumentException ex) {
                tolerateRaceConditionDueToBeingParallelCapable(ex, name);
            }
            Class<?> loadClass = super.loadClass(name, resolve);
            Optimizations.disable();
            return loadClass;
        } catch (Throwable th) {
            Optimizations.disable();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void definePackageIfNecessary(String className) {
        int lastDot;
        if (!className.startsWith("java.") && (lastDot = className.lastIndexOf(46)) >= 0) {
            String packageName = className.substring(0, lastDot);
            if (getDefinedPackage(packageName) == null) {
                try {
                    definePackage(className, packageName);
                } catch (IllegalArgumentException ex) {
                    tolerateRaceConditionDueToBeingParallelCapable(ex, packageName);
                }
            }
        }
    }

    private void definePackage(String className, String packageName) {
        if (this.undefinablePackages.contains(packageName)) {
            return;
        }
        String packageEntryName = packageName.replace('.', '/') + "/";
        String classEntryName = className.replace('.', '/') + ".class";
        for (URL url : this.urls) {
            try {
                JarFile jarFile = getJarFile(url);
                if (jarFile != null && hasEntry(jarFile, classEntryName) && hasEntry(jarFile, packageEntryName) && jarFile.getManifest() != null) {
                    definePackage(packageName, jarFile.getManifest(), url);
                    return;
                }
            } catch (IOException e) {
            }
        }
        this.undefinablePackages.add(packageName);
    }

    private void tolerateRaceConditionDueToBeingParallelCapable(IllegalArgumentException ex, String packageName) throws AssertionError {
        if (getDefinedPackage(packageName) == null) {
            throw new AssertionError("Package %s has already been defined but it could not be found".formatted(packageName), ex);
        }
    }

    private boolean hasEntry(JarFile jarFile, String name) {
        if (!(jarFile instanceof NestedJarFile)) {
            return jarFile.getEntry(name) != null;
        }
        NestedJarFile nestedJarFile = (NestedJarFile) jarFile;
        return nestedJarFile.hasEntry(name);
    }

    private JarFile getJarFile(URL url) throws IOException {
        JarFile jarFile = this.jarFiles.get(url);
        if (jarFile != null) {
            return jarFile;
        }
        URLConnection connection = url.openConnection();
        if (!(connection instanceof JarURLConnection)) {
            return null;
        }
        connection.setUseCaches(false);
        JarFile jarFile2 = ((JarURLConnection) connection).getJarFile();
        synchronized (this.jarFiles) {
            JarFile previous = this.jarFiles.putIfAbsent(url, jarFile2);
            if (previous != null) {
                jarFile2.close();
                jarFile2 = previous;
            }
        }
        return jarFile2;
    }

    public void clearCache() {
        Handler.clearCache();
        org.springframework.boot.loader.net.protocol.nested.Handler.clearCache();
        try {
            clearJarFiles();
        } catch (IOException e) {
        }
        for (URL url : this.urls) {
            if (isJarUrl(url)) {
                clearCache(url);
            }
        }
    }

    private void clearCache(URL url) {
        try {
            URLConnection connection = url.openConnection();
            if (connection instanceof JarURLConnection) {
                JarURLConnection jarUrlConnection = (JarURLConnection) connection;
                clearCache(jarUrlConnection);
            }
        } catch (IOException e) {
        }
    }

    private void clearCache(JarURLConnection connection) throws IOException {
        JarFile jarFile = connection.getJarFile();
        if (jarFile instanceof NestedJarFile) {
            NestedJarFile nestedJarFile = (NestedJarFile) jarFile;
            nestedJarFile.clearCache();
        }
    }

    private boolean isJarUrl(URL url) {
        return "jar".equals(url.getProtocol());
    }

    @Override // java.net.URLClassLoader, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        super.close();
        clearJarFiles();
    }

    private void clearJarFiles() throws IOException {
        synchronized (this.jarFiles) {
            for (JarFile jarFile : this.jarFiles.values()) {
                jarFile.close();
            }
            this.jarFiles.clear();
        }
    }

    /* loaded from: agent.jar:org/springframework/boot/loader/net/protocol/jar/JarUrlClassLoader$OptimizedEnumeration.class */
    private static class OptimizedEnumeration implements Enumeration<URL> {
        private final Enumeration<URL> delegate;

        OptimizedEnumeration(Enumeration<URL> delegate) {
            this.delegate = delegate;
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            Optimizations.enable(false);
            try {
                boolean hasMoreElements = this.delegate.hasMoreElements();
                Optimizations.disable();
                return hasMoreElements;
            } catch (Throwable th) {
                Optimizations.disable();
                throw th;
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Enumeration
        public URL nextElement() {
            Optimizations.enable(false);
            try {
                URL nextElement = this.delegate.nextElement();
                Optimizations.disable();
                return nextElement;
            } catch (Throwable th) {
                Optimizations.disable();
                throw th;
            }
        }
    }
}
