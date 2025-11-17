package org.springframework.boot.loader.net.protocol.nested;

import java.io.File;
import java.io.FilePermission;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.ref.Cleaner;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.security.Permission;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:org/springframework/boot/loader/net/protocol/nested/NestedUrlConnection.class */
class NestedUrlConnection extends URLConnection {
    private static final DateTimeFormatter RFC_1123_DATE_TIME = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.of("GMT"));
    private static final String CONTENT_TYPE = "x-java/jar";
    private final NestedUrlConnectionResources resources;
    private final Cleaner.Cleanable cleanup;
    private long lastModified;
    private FilePermission permission;
    private Map<String, List<String>> headerFields;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NestedUrlConnection(URL url) throws MalformedURLException {
        this(url, org.springframework.boot.loader.ref.Cleaner.instance);
    }

    NestedUrlConnection(URL url, org.springframework.boot.loader.ref.Cleaner cleaner) throws MalformedURLException {
        super(url);
        this.lastModified = -1L;
        NestedLocation location = parseNestedLocation(url);
        this.resources = new NestedUrlConnectionResources(location);
        this.cleanup = cleaner.register(this, this.resources);
    }

    private NestedLocation parseNestedLocation(URL url) throws MalformedURLException {
        try {
            return NestedLocation.parse(url.getPath());
        } catch (IllegalArgumentException ex) {
            throw new MalformedURLException(ex.getMessage());
        }
    }

    @Override // java.net.URLConnection
    public String getHeaderField(String name) {
        List<String> values = getHeaderFields().get(name);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    @Override // java.net.URLConnection
    public String getHeaderField(int n) {
        Map.Entry<String, List<String>> entry = getHeaderEntry(n);
        List<String> values = entry != null ? entry.getValue() : null;
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    @Override // java.net.URLConnection
    public String getHeaderFieldKey(int n) {
        Map.Entry<String, List<String>> entry = getHeaderEntry(n);
        if (entry != null) {
            return entry.getKey();
        }
        return null;
    }

    private Map.Entry<String, List<String>> getHeaderEntry(int n) {
        Iterator<Map.Entry<String, List<String>>> iterator = getHeaderFields().entrySet().iterator();
        Map.Entry<String, List<String>> entry = null;
        for (int i = 0; i < n; i++) {
            entry = !iterator.hasNext() ? null : iterator.next();
        }
        return entry;
    }

    @Override // java.net.URLConnection
    public Map<String, List<String>> getHeaderFields() {
        try {
            connect();
            Map<String, List<String>> headerFields = this.headerFields;
            if (headerFields == null) {
                Map<String, List<String>> headerFields2 = new LinkedHashMap<>();
                long contentLength = getContentLengthLong();
                long lastModified = getLastModified();
                if (contentLength > 0) {
                    headerFields2.put("content-length", List.of(String.valueOf(contentLength)));
                }
                if (getLastModified() > 0) {
                    headerFields2.put("last-modified", List.of(RFC_1123_DATE_TIME.format(Instant.ofEpochMilli(lastModified))));
                }
                headerFields = Collections.unmodifiableMap(headerFields2);
                this.headerFields = headerFields;
            }
            return headerFields;
        } catch (IOException e) {
            return Collections.emptyMap();
        }
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
            return this.resources.getContentLength();
        } catch (IOException e) {
            return -1L;
        }
    }

    @Override // java.net.URLConnection
    public String getContentType() {
        return CONTENT_TYPE;
    }

    @Override // java.net.URLConnection
    public long getLastModified() {
        if (this.lastModified == -1) {
            try {
                this.lastModified = Files.getLastModifiedTime(this.resources.getLocation().path(), new LinkOption[0]).toMillis();
            } catch (IOException e) {
                this.lastModified = 0L;
            }
        }
        return this.lastModified;
    }

    @Override // java.net.URLConnection
    public Permission getPermission() throws IOException {
        if (this.permission == null) {
            File file = this.resources.getLocation().path().toFile();
            this.permission = new FilePermission(file.getCanonicalPath(), "read");
        }
        return this.permission;
    }

    @Override // java.net.URLConnection
    public InputStream getInputStream() throws IOException {
        connect();
        return new ConnectionInputStream(this.resources.getInputStream());
    }

    @Override // java.net.URLConnection
    public void connect() throws IOException {
        if (this.connected) {
            return;
        }
        this.resources.connect();
        this.connected = true;
    }

    /* loaded from: agent.jar:org/springframework/boot/loader/net/protocol/nested/NestedUrlConnection$ConnectionInputStream.class */
    class ConnectionInputStream extends FilterInputStream {
        private volatile boolean closing;

        ConnectionInputStream(InputStream in) {
            super(in);
        }

        @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            if (this.closing) {
                return;
            }
            this.closing = true;
            try {
                super.close();
                try {
                    NestedUrlConnection.this.cleanup.clean();
                } catch (UncheckedIOException ex) {
                    throw ex.getCause();
                }
            } catch (Throwable th) {
                try {
                    NestedUrlConnection.this.cleanup.clean();
                    throw th;
                } catch (UncheckedIOException ex2) {
                    throw ex2.getCause();
                }
            }
        }
    }
}
