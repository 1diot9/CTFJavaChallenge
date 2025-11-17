package org.apache.catalina.webresources;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.catalina.TrackedWebResource;
import org.apache.catalina.WebResourceRoot;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/webresources/TrackedInputStream.class */
class TrackedInputStream extends InputStream implements TrackedWebResource {
    private final WebResourceRoot root;
    private final String name;
    private final InputStream is;
    private final Exception creation = new Exception();

    /* JADX INFO: Access modifiers changed from: package-private */
    public TrackedInputStream(WebResourceRoot root, String name, InputStream is) {
        this.root = root;
        this.name = name;
        this.is = is;
        root.registerTrackedResource(this);
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        return this.is.read();
    }

    @Override // java.io.InputStream
    public int read(byte[] b) throws IOException {
        return this.is.read(b);
    }

    @Override // java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        return this.is.read(b, off, len);
    }

    @Override // java.io.InputStream
    public long skip(long n) throws IOException {
        return this.is.skip(n);
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        return this.is.available();
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.root.deregisterTrackedResource(this);
        this.is.close();
    }

    @Override // java.io.InputStream
    public synchronized void mark(int readlimit) {
        this.is.mark(readlimit);
    }

    @Override // java.io.InputStream
    public synchronized void reset() throws IOException {
        this.is.reset();
    }

    @Override // java.io.InputStream
    public boolean markSupported() {
        return this.is.markSupported();
    }

    @Override // org.apache.catalina.TrackedWebResource
    public String getName() {
        return this.name;
    }

    @Override // org.apache.catalina.TrackedWebResource
    public Exception getCreatedBy() {
        return this.creation;
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        sw.append('[');
        sw.append((CharSequence) this.name);
        sw.append(']');
        sw.append((CharSequence) System.lineSeparator());
        this.creation.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}
