package org.apache.catalina.webresources;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.jar.Manifest;
import org.apache.catalina.WebResourceRoot;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/webresources/JarResourceRoot.class */
public class JarResourceRoot extends AbstractResource {
    private static final Log log = LogFactory.getLog((Class<?>) JarResourceRoot.class);
    private final File base;
    private final String baseUrl;
    private final String name;

    public JarResourceRoot(WebResourceRoot root, File base, String baseUrl, String webAppPath) {
        super(root, webAppPath);
        if (!webAppPath.endsWith("/")) {
            throw new IllegalArgumentException(sm.getString("jarResourceRoot.invalidWebAppPath", webAppPath));
        }
        this.base = base;
        this.baseUrl = "jar:" + baseUrl;
        String resourceName = webAppPath.substring(0, webAppPath.length() - 1);
        int i = resourceName.lastIndexOf(47);
        this.name = i > -1 ? resourceName.substring(i + 1) : resourceName;
    }

    @Override // org.apache.catalina.WebResource
    public long getLastModified() {
        return this.base.lastModified();
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
        return true;
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
        return this.name;
    }

    @Override // org.apache.catalina.WebResource
    public long getContentLength() {
        return -1L;
    }

    @Override // org.apache.catalina.WebResource
    public String getCanonicalPath() {
        return null;
    }

    @Override // org.apache.catalina.WebResource
    public boolean canRead() {
        return true;
    }

    @Override // org.apache.catalina.webresources.AbstractResource
    protected InputStream doGetInputStream() {
        return null;
    }

    @Override // org.apache.catalina.WebResource
    public byte[] getContent() {
        return null;
    }

    @Override // org.apache.catalina.WebResource
    public long getCreation() {
        return this.base.lastModified();
    }

    @Override // org.apache.catalina.WebResource
    public URL getURL() {
        String url = this.baseUrl + "!/";
        try {
            return new URI(url).toURL();
        } catch (IllegalArgumentException | MalformedURLException | URISyntaxException e) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("fileResource.getUrlFail", url), e);
                return null;
            }
            return null;
        }
    }

    @Override // org.apache.catalina.WebResource
    public URL getCodeBase() {
        try {
            return new URI(this.baseUrl).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            if (getLog().isDebugEnabled()) {
                getLog().debug(sm.getString("fileResource.getUrlFail", this.baseUrl), e);
                return null;
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.webresources.AbstractResource
    public Log getLog() {
        return log;
    }

    @Override // org.apache.catalina.WebResource
    public Certificate[] getCertificates() {
        return null;
    }

    @Override // org.apache.catalina.WebResource
    public Manifest getManifest() {
        return null;
    }
}
