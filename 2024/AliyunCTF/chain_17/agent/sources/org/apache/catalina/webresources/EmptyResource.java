package org.apache.catalina.webresources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.jar.Manifest;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/webresources/EmptyResource.class */
public class EmptyResource implements WebResource {
    private final WebResourceRoot root;
    private final String webAppPath;
    private final File file;

    public EmptyResource(WebResourceRoot root, String webAppPath) {
        this(root, webAppPath, null);
    }

    public EmptyResource(WebResourceRoot root, String webAppPath, File file) {
        this.root = root;
        this.webAppPath = webAppPath;
        this.file = file;
    }

    @Override // org.apache.catalina.WebResource
    public long getLastModified() {
        return 0L;
    }

    @Override // org.apache.catalina.WebResource
    public String getLastModifiedHttp() {
        return null;
    }

    @Override // org.apache.catalina.WebResource
    public boolean exists() {
        return false;
    }

    @Override // org.apache.catalina.WebResource
    public boolean isVirtual() {
        return false;
    }

    @Override // org.apache.catalina.WebResource
    public boolean isDirectory() {
        return false;
    }

    @Override // org.apache.catalina.WebResource
    public boolean isFile() {
        return false;
    }

    @Override // org.apache.catalina.WebResource
    public boolean delete() {
        return false;
    }

    @Override // org.apache.catalina.WebResource
    public String getName() {
        int index = this.webAppPath.lastIndexOf(47);
        if (index == -1) {
            return this.webAppPath;
        }
        return this.webAppPath.substring(index + 1);
    }

    @Override // org.apache.catalina.WebResource
    public long getContentLength() {
        return -1L;
    }

    @Override // org.apache.catalina.WebResource
    public String getCanonicalPath() {
        if (this.file == null) {
            return null;
        }
        try {
            return this.file.getCanonicalPath();
        } catch (IOException e) {
            return null;
        }
    }

    @Override // org.apache.catalina.WebResource
    public boolean canRead() {
        return false;
    }

    @Override // org.apache.catalina.WebResource
    public String getWebappPath() {
        return this.webAppPath;
    }

    @Override // org.apache.catalina.WebResource
    public String getETag() {
        return null;
    }

    @Override // org.apache.catalina.WebResource
    public void setMimeType(String mimeType) {
    }

    @Override // org.apache.catalina.WebResource
    public String getMimeType() {
        return null;
    }

    @Override // org.apache.catalina.WebResource
    public InputStream getInputStream() {
        return null;
    }

    @Override // org.apache.catalina.WebResource
    public byte[] getContent() {
        return null;
    }

    @Override // org.apache.catalina.WebResource
    public long getCreation() {
        return 0L;
    }

    @Override // org.apache.catalina.WebResource
    public URL getURL() {
        return null;
    }

    @Override // org.apache.catalina.WebResource
    public URL getCodeBase() {
        return null;
    }

    @Override // org.apache.catalina.WebResource
    public Certificate[] getCertificates() {
        return null;
    }

    @Override // org.apache.catalina.WebResource
    public Manifest getManifest() {
        return null;
    }

    @Override // org.apache.catalina.WebResource
    public WebResourceRoot getWebResourceRoot() {
        return this.root;
    }
}
