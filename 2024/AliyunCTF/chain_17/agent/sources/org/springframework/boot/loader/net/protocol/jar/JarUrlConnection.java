package org.springframework.boot.loader.net.protocol.jar;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.security.Permission;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.springframework.boot.loader.jar.NestedJarFile;
import org.springframework.boot.loader.net.util.UrlDecoder;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:org/springframework/boot/loader/net/protocol/jar/JarUrlConnection.class */
public final class JarUrlConnection extends JarURLConnection {
    static final UrlJarFiles jarFiles = new UrlJarFiles();
    static final InputStream emptyInputStream = new ByteArrayInputStream(new byte[0]);
    static final FileNotFoundException FILE_NOT_FOUND_EXCEPTION = new FileNotFoundException("Jar file or entry not found");
    private static final URL NOT_FOUND_URL;
    static final JarUrlConnection NOT_FOUND_CONNECTION;
    private final String entryName;
    private final Supplier<FileNotFoundException> notFound;
    private JarFile jarFile;
    private URLConnection jarFileConnection;
    private JarEntry jarEntry;
    private String contentType;

    static {
        try {
            NOT_FOUND_URL = new URL("jar:", null, 0, "nested:!/", new EmptyUrlStreamHandler());
            NOT_FOUND_CONNECTION = new JarUrlConnection((Supplier<FileNotFoundException>) () -> {
                return FILE_NOT_FOUND_EXCEPTION;
            });
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private JarUrlConnection(URL url) throws IOException {
        super(url);
        this.entryName = getEntryName();
        this.notFound = null;
        this.jarFileConnection = getJarFileURL().openConnection();
        this.jarFileConnection.setUseCaches(this.useCaches);
    }

    private JarUrlConnection(Supplier<FileNotFoundException> notFound) throws IOException {
        super(NOT_FOUND_URL);
        this.entryName = null;
        this.notFound = notFound;
    }

    @Override // java.net.JarURLConnection
    public JarFile getJarFile() throws IOException {
        connect();
        return this.jarFile;
    }

    @Override // java.net.JarURLConnection
    public JarEntry getJarEntry() throws IOException {
        connect();
        return this.jarEntry;
    }

    @Override // java.net.URLConnection
    public int getContentLength() {
        long contentLength = getContentLengthLong();
        if (contentLength <= 2147483647L) {
            return (int) contentLength;
        }
        return -1;
    }

    @Override // java.net.URLConnection
    public long getContentLengthLong() {
        try {
            connect();
            return this.jarEntry != null ? this.jarEntry.getSize() : this.jarFileConnection.getContentLengthLong();
        } catch (IOException e) {
            return -1L;
        }
    }

    @Override // java.net.URLConnection
    public String getContentType() {
        if (this.contentType == null) {
            this.contentType = deduceContentType();
        }
        return this.contentType;
    }

    private String deduceContentType() {
        String type = this.entryName != null ? null : "x-java/jar";
        String type2 = type != null ? type : deduceContentTypeFromStream();
        String type3 = type2 != null ? type2 : deduceContentTypeFromEntryName();
        return type3 != null ? type3 : "content/unknown";
    }

    private String deduceContentTypeFromStream() {
        try {
            connect();
            InputStream in = this.jarFile.getInputStream(this.jarEntry);
            try {
                String guessContentTypeFromStream = guessContentTypeFromStream(new BufferedInputStream(in));
                if (in != null) {
                    in.close();
                }
                return guessContentTypeFromStream;
            } finally {
            }
        } catch (IOException e) {
            return null;
        }
    }

    private String deduceContentTypeFromEntryName() {
        return guessContentTypeFromName(this.entryName);
    }

    @Override // java.net.URLConnection
    public long getLastModified() {
        return this.jarFileConnection != null ? this.jarFileConnection.getLastModified() : super.getLastModified();
    }

    @Override // java.net.URLConnection
    public String getHeaderField(String name) {
        if (this.jarFileConnection != null) {
            return this.jarFileConnection.getHeaderField(name);
        }
        return null;
    }

    @Override // java.net.URLConnection
    public Object getContent() throws IOException {
        connect();
        return this.entryName != null ? super.getContent() : this.jarFile;
    }

    @Override // java.net.URLConnection
    public Permission getPermission() throws IOException {
        return this.jarFileConnection.getPermission();
    }

    @Override // java.net.URLConnection
    public InputStream getInputStream() throws IOException {
        JarFile cached;
        if (this.notFound != null) {
            throwFileNotFound();
        }
        URL jarFileURL = getJarFileURL();
        if (this.entryName == null && !UrlJarFileFactory.isNestedUrl(jarFileURL)) {
            throw new IOException("no entry name specified");
        }
        if (!getUseCaches() && Optimizations.isEnabled(false) && this.entryName != null && (cached = jarFiles.getCached(jarFileURL)) != null && cached.getEntry(this.entryName) != null) {
            return emptyInputStream;
        }
        connect();
        if (this.jarEntry == null) {
            JarFile jarFile = this.jarFile;
            if (jarFile instanceof NestedJarFile) {
                NestedJarFile nestedJarFile = (NestedJarFile) jarFile;
                return nestedJarFile.getRawZipDataInputStream();
            }
            throwFileNotFound();
        }
        return new ConnectionInputStream();
    }

    @Override // java.net.URLConnection
    public boolean getAllowUserInteraction() {
        if (this.jarFileConnection != null) {
            return this.jarFileConnection.getAllowUserInteraction();
        }
        return false;
    }

    @Override // java.net.URLConnection
    public void setAllowUserInteraction(boolean allowuserinteraction) {
        if (this.jarFileConnection != null) {
            this.jarFileConnection.setAllowUserInteraction(allowuserinteraction);
        }
    }

    @Override // java.net.URLConnection
    public boolean getUseCaches() {
        if (this.jarFileConnection != null) {
            return this.jarFileConnection.getUseCaches();
        }
        return true;
    }

    @Override // java.net.URLConnection
    public void setUseCaches(boolean usecaches) {
        if (this.jarFileConnection != null) {
            this.jarFileConnection.setUseCaches(usecaches);
        }
    }

    @Override // java.net.URLConnection
    public boolean getDefaultUseCaches() {
        if (this.jarFileConnection != null) {
            return this.jarFileConnection.getDefaultUseCaches();
        }
        return true;
    }

    @Override // java.net.URLConnection
    public void setDefaultUseCaches(boolean defaultusecaches) {
        if (this.jarFileConnection != null) {
            this.jarFileConnection.setDefaultUseCaches(defaultusecaches);
        }
    }

    @Override // java.net.URLConnection
    public void setIfModifiedSince(long ifModifiedSince) {
        if (this.jarFileConnection != null) {
            this.jarFileConnection.setIfModifiedSince(ifModifiedSince);
        }
    }

    @Override // java.net.URLConnection
    public String getRequestProperty(String key) {
        if (this.jarFileConnection != null) {
            return this.jarFileConnection.getRequestProperty(key);
        }
        return null;
    }

    @Override // java.net.URLConnection
    public void setRequestProperty(String key, String value) {
        if (this.jarFileConnection != null) {
            this.jarFileConnection.setRequestProperty(key, value);
        }
    }

    @Override // java.net.URLConnection
    public void addRequestProperty(String key, String value) {
        if (this.jarFileConnection != null) {
            this.jarFileConnection.addRequestProperty(key, value);
        }
    }

    @Override // java.net.URLConnection
    public Map<String, List<String>> getRequestProperties() {
        return this.jarFileConnection != null ? this.jarFileConnection.getRequestProperties() : Collections.emptyMap();
    }

    @Override // java.net.URLConnection
    public void connect() throws IOException {
        if (this.connected) {
            return;
        }
        if (this.notFound != null) {
            throwFileNotFound();
        }
        boolean useCaches = getUseCaches();
        URL jarFileURL = getJarFileURL();
        if (this.entryName != null && Optimizations.isEnabled()) {
            assertCachedJarFileHasEntry(jarFileURL, this.entryName);
        }
        this.jarFile = jarFiles.getOrCreate(useCaches, jarFileURL);
        this.jarEntry = getJarEntry(jarFileURL);
        boolean addedToCache = jarFiles.cacheIfAbsent(useCaches, jarFileURL, this.jarFile);
        if (addedToCache) {
            this.jarFileConnection = jarFiles.reconnect(this.jarFile, this.jarFileConnection);
        }
        this.connected = true;
    }

    private void assertCachedJarFileHasEntry(URL jarFileURL, String entryName) throws FileNotFoundException {
        JarFile cachedJarFile = jarFiles.getCached(jarFileURL);
        if (cachedJarFile != null && cachedJarFile.getJarEntry(entryName) == null) {
            throw FILE_NOT_FOUND_EXCEPTION;
        }
    }

    private JarEntry getJarEntry(URL jarFileUrl) throws IOException {
        if (this.entryName == null) {
            return null;
        }
        JarEntry jarEntry = this.jarFile.getJarEntry(this.entryName);
        if (jarEntry == null) {
            jarFiles.closeIfNotCached(jarFileUrl, this.jarFile);
            throwFileNotFound();
        }
        return jarEntry;
    }

    private void throwFileNotFound() throws FileNotFoundException {
        if (Optimizations.isEnabled()) {
            throw FILE_NOT_FOUND_EXCEPTION;
        }
        if (this.notFound != null) {
            throw this.notFound.get();
        }
        throw new FileNotFoundException("JAR entry " + this.entryName + " not found in " + this.jarFile.getName());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static JarUrlConnection open(URL url) throws IOException {
        String spec = url.getFile();
        if (spec.startsWith("nested:")) {
            int separator = spec.indexOf("!/");
            boolean specHasEntry = (separator == -1 || separator + 2 == spec.length()) ? false : true;
            if (specHasEntry) {
                URL jarFileUrl = new URL(spec.substring(0, separator));
                if ("runtime".equals(url.getRef())) {
                    jarFileUrl = new URL(jarFileUrl, "#runtime");
                }
                String entryName = UrlDecoder.decode(spec.substring(separator + 2));
                JarFile jarFile = jarFiles.getOrCreate(true, jarFileUrl);
                jarFiles.cacheIfAbsent(true, jarFileUrl, jarFile);
                if (!hasEntry(jarFile, entryName)) {
                    return notFoundConnection(jarFile.getName(), entryName);
                }
            }
        }
        return new JarUrlConnection(url);
    }

    private static boolean hasEntry(JarFile jarFile, String name) {
        if (!(jarFile instanceof NestedJarFile)) {
            return jarFile.getEntry(name) != null;
        }
        NestedJarFile nestedJarFile = (NestedJarFile) jarFile;
        return nestedJarFile.hasEntry(name);
    }

    private static JarUrlConnection notFoundConnection(String jarFileName, String entryName) throws IOException {
        if (Optimizations.isEnabled()) {
            return NOT_FOUND_CONNECTION;
        }
        return new JarUrlConnection((Supplier<FileNotFoundException>) () -> {
            return new FileNotFoundException("JAR entry " + entryName + " not found in " + jarFileName);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void clearCache() {
        jarFiles.clearCache();
    }

    /* loaded from: agent.jar:org/springframework/boot/loader/net/protocol/jar/JarUrlConnection$ConnectionInputStream.class */
    class ConnectionInputStream extends LazyDelegatingInputStream {
        ConnectionInputStream() {
        }

        @Override // org.springframework.boot.loader.net.protocol.jar.LazyDelegatingInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            try {
                super.close();
            } finally {
                if (!JarUrlConnection.this.getUseCaches()) {
                    JarUrlConnection.this.jarFile.close();
                }
            }
        }

        @Override // org.springframework.boot.loader.net.protocol.jar.LazyDelegatingInputStream
        protected InputStream getDelegateInputStream() throws IOException {
            return JarUrlConnection.this.jarFile.getInputStream(JarUrlConnection.this.jarEntry);
        }
    }

    /* loaded from: agent.jar:org/springframework/boot/loader/net/protocol/jar/JarUrlConnection$EmptyUrlStreamHandler.class */
    private static final class EmptyUrlStreamHandler extends URLStreamHandler {
        private EmptyUrlStreamHandler() {
        }

        @Override // java.net.URLStreamHandler
        protected URLConnection openConnection(URL url) {
            return null;
        }
    }
}
