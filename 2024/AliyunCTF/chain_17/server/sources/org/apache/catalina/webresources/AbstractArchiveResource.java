package org.apache.catalina.webresources;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;
import org.apache.catalina.util.URLEncoder;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/webresources/AbstractArchiveResource.class */
public abstract class AbstractArchiveResource extends AbstractResource {
    private final AbstractArchiveResourceSet archiveResourceSet;
    private final String baseUrl;
    private final JarEntry resource;
    private final String codeBaseUrl;
    private final String name;
    private boolean readCerts;
    private Certificate[] certificates;

    protected abstract JarInputStreamWrapper getJarInputStreamWrapper();

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractArchiveResource(AbstractArchiveResourceSet archiveResourceSet, String webAppPath, String baseUrl, JarEntry jarEntry, String codeBaseUrl) {
        super(archiveResourceSet.getRoot(), webAppPath);
        this.readCerts = false;
        this.archiveResourceSet = archiveResourceSet;
        this.baseUrl = baseUrl;
        this.resource = jarEntry;
        this.codeBaseUrl = codeBaseUrl;
        String resourceName = this.resource.getName();
        resourceName = resourceName.charAt(resourceName.length() - 1) == '/' ? resourceName.substring(0, resourceName.length() - 1) : resourceName;
        String internalPath = archiveResourceSet.getInternalPath();
        if (internalPath.length() > 0 && resourceName.equals(internalPath.subSequence(1, internalPath.length()))) {
            this.name = "";
            return;
        }
        int index = resourceName.lastIndexOf(47);
        if (index == -1) {
            this.name = resourceName;
        } else {
            this.name = resourceName.substring(index + 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractArchiveResourceSet getArchiveResourceSet() {
        return this.archiveResourceSet;
    }

    protected final String getBase() {
        return this.archiveResourceSet.getBase();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final String getBaseUrl() {
        return this.baseUrl;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final JarEntry getResource() {
        return this.resource;
    }

    @Override // org.apache.catalina.WebResource
    public long getLastModified() {
        return this.resource.getTime();
    }

    @Override // org.apache.catalina.WebResource
    public boolean exists() {
        return true;
    }

    @Override // org.apache.catalina.WebResource
    public boolean isVirtual() {
        return false;
    }

    @Override // org.apache.catalina.WebResource
    public boolean isDirectory() {
        return this.resource.isDirectory();
    }

    @Override // org.apache.catalina.WebResource
    public boolean isFile() {
        return !this.resource.isDirectory();
    }

    @Override // org.apache.catalina.WebResource
    public boolean delete() {
        return false;
    }

    @Override // org.apache.catalina.WebResource
    public String getName() {
        return this.name;
    }

    @Override // org.apache.catalina.WebResource
    public long getContentLength() {
        if (isDirectory()) {
            return -1L;
        }
        return this.resource.getSize();
    }

    @Override // org.apache.catalina.WebResource
    public String getCanonicalPath() {
        return null;
    }

    @Override // org.apache.catalina.WebResource
    public boolean canRead() {
        return true;
    }

    @Override // org.apache.catalina.WebResource
    public long getCreation() {
        return this.resource.getTime();
    }

    @Override // org.apache.catalina.WebResource
    public URL getURL() {
        String url = this.baseUrl + URLEncoder.DEFAULT.encode(this.resource.getName(), StandardCharsets.UTF_8);
        try {
            return new URI(url).toURL();
        } catch (IllegalArgumentException | MalformedURLException | URISyntaxException e) {
            if (getLog().isDebugEnabled()) {
                getLog().debug(sm.getString("fileResource.getUrlFail", url), e);
                return null;
            }
            return null;
        }
    }

    @Override // org.apache.catalina.WebResource
    public URL getCodeBase() {
        try {
            return new URI(this.codeBaseUrl).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            if (getLog().isDebugEnabled()) {
                getLog().debug(sm.getString("fileResource.getUrlFail", this.codeBaseUrl), e);
                return null;
            }
            return null;
        }
    }

    @Override // org.apache.catalina.WebResource
    public final byte[] getContent() {
        long len = getContentLength();
        if (len > 2147483647L) {
            throw new ArrayIndexOutOfBoundsException(sm.getString("abstractResource.getContentTooLarge", getWebappPath(), Long.valueOf(len)));
        }
        if (len < 0) {
            return null;
        }
        int size = (int) len;
        byte[] result = new byte[size];
        int pos = 0;
        try {
            JarInputStreamWrapper jisw = getJarInputStreamWrapper();
            if (jisw != null) {
                while (pos < size) {
                    try {
                        int n = jisw.read(result, pos, size - pos);
                        if (n < 0) {
                            break;
                        }
                        pos += n;
                    } catch (Throwable th) {
                        if (jisw != null) {
                            try {
                                jisw.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                }
                this.certificates = jisw.getCertificates();
                this.readCerts = true;
                if (jisw != null) {
                    jisw.close();
                }
                return result;
            }
            if (jisw != null) {
                jisw.close();
            }
            return null;
        } catch (IOException ioe) {
            if (getLog().isDebugEnabled()) {
                getLog().debug(sm.getString("abstractResource.getContentFail", getWebappPath()), ioe);
                return null;
            }
            return null;
        }
    }

    @Override // org.apache.catalina.WebResource
    public Certificate[] getCertificates() {
        if (!this.readCerts) {
            throw new IllegalStateException();
        }
        return this.certificates;
    }

    @Override // org.apache.catalina.WebResource
    public Manifest getManifest() {
        return this.archiveResourceSet.getManifest();
    }

    @Override // org.apache.catalina.webresources.AbstractResource
    protected final InputStream doGetInputStream() {
        if (isDirectory()) {
            return null;
        }
        return getJarInputStreamWrapper();
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/webresources/AbstractArchiveResource$JarInputStreamWrapper.class */
    protected class JarInputStreamWrapper extends InputStream {
        private final JarEntry jarEntry;
        private final InputStream is;
        private final AtomicBoolean closed = new AtomicBoolean(false);

        public JarInputStreamWrapper(JarEntry jarEntry, InputStream is) {
            this.jarEntry = jarEntry;
            this.is = is;
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
            if (this.closed.compareAndSet(false, true)) {
                AbstractArchiveResource.this.archiveResourceSet.closeJarFile();
            }
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

        public Certificate[] getCertificates() {
            return this.jarEntry.getCertificates();
        }
    }
}
