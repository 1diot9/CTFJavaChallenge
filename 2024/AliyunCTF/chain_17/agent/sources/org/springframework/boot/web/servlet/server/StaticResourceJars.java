package org.springframework.boot.web.servlet.server;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/server/StaticResourceJars.class */
class StaticResourceJars {
    /* JADX INFO: Access modifiers changed from: package-private */
    public List<URL> getUrls() {
        ClassLoader classLoader = getClass().getClassLoader();
        if (classLoader instanceof URLClassLoader) {
            URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
            return getUrlsFrom(urlClassLoader.getURLs());
        }
        return getUrlsFrom((URL[]) Stream.of((Object[]) ManagementFactory.getRuntimeMXBean().getClassPath().split(File.pathSeparator)).map(this::toUrl).toArray(x$0 -> {
            return new URL[x$0];
        }));
    }

    List<URL> getUrlsFrom(URL... urls) {
        List<URL> resourceJarUrls = new ArrayList<>();
        for (URL url : urls) {
            addUrl(resourceJarUrls, url);
        }
        return resourceJarUrls;
    }

    private URL toUrl(String classPathEntry) {
        try {
            return new File(classPathEntry).toURI().toURL();
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("URL could not be created from '" + classPathEntry + "'", ex);
        }
    }

    private File toFile(URL url) {
        try {
            return new File(url.toURI());
        } catch (IllegalArgumentException e) {
            return null;
        } catch (URISyntaxException e2) {
            throw new IllegalStateException("Failed to create File from URL '" + url + "'");
        }
    }

    private void addUrl(List<URL> urls, URL url) {
        try {
            if (!"file".equals(url.getProtocol())) {
                addUrlConnection(urls, url, url.openConnection());
            } else {
                File file = toFile(url);
                if (file != null) {
                    addUrlFile(urls, url, file);
                } else {
                    addUrlConnection(urls, url, url.openConnection());
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void addUrlFile(List<URL> urls, URL url, File file) {
        if ((file.isDirectory() && new File(file, "META-INF/resources").isDirectory()) || isResourcesJar(file)) {
            urls.add(url);
        }
    }

    private void addUrlConnection(List<URL> urls, URL url, URLConnection connection) {
        if (connection instanceof JarURLConnection) {
            JarURLConnection jarURLConnection = (JarURLConnection) connection;
            if (isResourcesJar(jarURLConnection)) {
                urls.add(url);
            }
        }
    }

    private boolean isResourcesJar(JarURLConnection connection) {
        try {
            return isResourcesJar(connection.getJarFile(), !connection.getUseCaches());
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isResourcesJar(File file) {
        try {
            return isResourcesJar(new JarFile(file), true);
        } catch (IOException | InvalidPathException e) {
            return false;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:9:0x001f A[DONT_GENERATE] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean isResourcesJar(java.util.jar.JarFile r4, boolean r5) throws java.io.IOException {
        /*
            r3 = this;
            r0 = r4
            java.lang.String r0 = r0.getName()     // Catch: java.lang.Throwable -> L25
            java.lang.String r1 = ".jar"
            boolean r0 = r0.endsWith(r1)     // Catch: java.lang.Throwable -> L25
            if (r0 == 0) goto L19
            r0 = r4
            java.lang.String r1 = "META-INF/resources"
            java.util.jar.JarEntry r0 = r0.getJarEntry(r1)     // Catch: java.lang.Throwable -> L25
            if (r0 == 0) goto L19
            r0 = 1
            goto L1a
        L19:
            r0 = 0
        L1a:
            r6 = r0
            r0 = r5
            if (r0 == 0) goto L23
            r0 = r4
            r0.close()
        L23:
            r0 = r6
            return r0
        L25:
            r7 = move-exception
            r0 = r5
            if (r0 == 0) goto L2f
            r0 = r4
            r0.close()
        L2f:
            r0 = r7
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.boot.web.servlet.server.StaticResourceJars.isResourcesJar(java.util.jar.JarFile, boolean):boolean");
    }
}
