package org.apache.catalina.webresources;

import java.io.InputStream;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/webresources/AbstractResource.class */
public abstract class AbstractResource implements WebResource {
    protected static final StringManager sm = StringManager.getManager((Class<?>) AbstractResource.class);
    private final WebResourceRoot root;
    private final String webAppPath;
    private String mimeType = null;
    private volatile String weakETag;

    protected abstract InputStream doGetInputStream();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract Log getLog();

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractResource(WebResourceRoot root, String webAppPath) {
        this.root = root;
        this.webAppPath = webAppPath;
    }

    @Override // org.apache.catalina.WebResource
    public final WebResourceRoot getWebResourceRoot() {
        return this.root;
    }

    @Override // org.apache.catalina.WebResource
    public final String getWebappPath() {
        return this.webAppPath;
    }

    @Override // org.apache.catalina.WebResource
    public final String getLastModifiedHttp() {
        return FastHttpDateFormat.formatDate(getLastModified());
    }

    @Override // org.apache.catalina.WebResource
    public final String getETag() {
        if (this.weakETag == null) {
            synchronized (this) {
                if (this.weakETag == null) {
                    long contentLength = getContentLength();
                    long lastModified = getLastModified();
                    if (contentLength >= 0 || lastModified >= 0) {
                        this.weakETag = "W/\"" + contentLength + "-" + this + "\"";
                    }
                }
            }
        }
        return this.weakETag;
    }

    @Override // org.apache.catalina.WebResource
    public final void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override // org.apache.catalina.WebResource
    public final String getMimeType() {
        return this.mimeType;
    }

    @Override // org.apache.catalina.WebResource
    public final InputStream getInputStream() {
        InputStream is = doGetInputStream();
        if (is == null || !this.root.getTrackLockedFiles()) {
            return is;
        }
        return new TrackedInputStream(this.root, getName(), is);
    }
}
