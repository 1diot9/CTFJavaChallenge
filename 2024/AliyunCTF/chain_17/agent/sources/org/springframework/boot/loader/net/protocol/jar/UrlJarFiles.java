package org.springframework.boot.loader.net.protocol.jar;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:org/springframework/boot/loader/net/protocol/jar/UrlJarFiles.class */
public class UrlJarFiles {
    private final UrlJarFileFactory factory;
    private final Cache cache;

    /* JADX INFO: Access modifiers changed from: package-private */
    public UrlJarFiles() {
        this(new UrlJarFileFactory());
    }

    UrlJarFiles(UrlJarFileFactory factory) {
        this.cache = new Cache();
        this.factory = factory;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JarFile getOrCreate(boolean useCaches, URL jarFileUrl) throws IOException {
        JarFile cached;
        if (useCaches && (cached = getCached(jarFileUrl)) != null) {
            return cached;
        }
        return this.factory.createJarFile(jarFileUrl, this::onClose);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JarFile getCached(URL jarFileUrl) {
        return this.cache.get(jarFileUrl);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean cacheIfAbsent(boolean useCaches, URL jarFileUrl, JarFile jarFile) {
        if (!useCaches) {
            return false;
        }
        return this.cache.putIfAbsent(jarFileUrl, jarFile);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void closeIfNotCached(URL jarFileUrl, JarFile jarFile) throws IOException {
        JarFile cached = getCached(jarFileUrl);
        if (cached != jarFile) {
            jarFile.close();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public URLConnection reconnect(JarFile jarFile, URLConnection existingConnection) throws IOException {
        Boolean useCaches = existingConnection != null ? Boolean.valueOf(existingConnection.getUseCaches()) : null;
        URLConnection connection = openConnection(jarFile);
        if (useCaches != null && connection != null) {
            connection.setUseCaches(useCaches.booleanValue());
        }
        return connection;
    }

    private URLConnection openConnection(JarFile jarFile) throws IOException {
        URL url = this.cache.get(jarFile);
        if (url != null) {
            return url.openConnection();
        }
        return null;
    }

    private void onClose(JarFile jarFile) {
        this.cache.remove(jarFile);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearCache() {
        this.cache.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:org/springframework/boot/loader/net/protocol/jar/UrlJarFiles$Cache.class */
    public static final class Cache {
        private final Map<String, JarFile> jarFileUrlToJarFile = new HashMap();
        private final Map<JarFile, URL> jarFileToJarFileUrl = new HashMap();

        private Cache() {
        }

        JarFile get(URL jarFileUrl) {
            JarFile jarFile;
            String urlKey = JarFileUrlKey.get(jarFileUrl);
            synchronized (this) {
                jarFile = this.jarFileUrlToJarFile.get(urlKey);
            }
            return jarFile;
        }

        URL get(JarFile jarFile) {
            URL url;
            synchronized (this) {
                url = this.jarFileToJarFileUrl.get(jarFile);
            }
            return url;
        }

        boolean putIfAbsent(URL jarFileUrl, JarFile jarFile) {
            String urlKey = JarFileUrlKey.get(jarFileUrl);
            synchronized (this) {
                JarFile cached = this.jarFileUrlToJarFile.get(urlKey);
                if (cached == null) {
                    this.jarFileUrlToJarFile.put(urlKey, jarFile);
                    this.jarFileToJarFileUrl.put(jarFile, jarFileUrl);
                    return true;
                }
                return false;
            }
        }

        void remove(JarFile jarFile) {
            synchronized (this) {
                URL removedUrl = this.jarFileToJarFileUrl.remove(jarFile);
                if (removedUrl != null) {
                    this.jarFileUrlToJarFile.remove(JarFileUrlKey.get(removedUrl));
                }
            }
        }

        void clear() {
            synchronized (this) {
                this.jarFileToJarFileUrl.clear();
                this.jarFileUrlToJarFile.clear();
            }
        }
    }
}
