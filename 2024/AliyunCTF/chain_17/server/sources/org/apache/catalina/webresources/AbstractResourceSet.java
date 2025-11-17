package org.apache.catalina.webresources;

import java.util.jar.Manifest;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.WebResourceSet;
import org.apache.catalina.util.LifecycleBase;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/webresources/AbstractResourceSet.class */
public abstract class AbstractResourceSet extends LifecycleBase implements WebResourceSet {
    private WebResourceRoot root;
    private String base;
    private String internalPath = "";
    private String webAppMount;
    private boolean classLoaderOnly;
    private boolean staticOnly;
    private Manifest manifest;
    protected static final StringManager sm = StringManager.getManager((Class<?>) AbstractResourceSet.class);

    /* JADX INFO: Access modifiers changed from: protected */
    public final void checkPath(String path) {
        if (path == null || path.length() == 0 || path.charAt(0) != '/') {
            throw new IllegalArgumentException(sm.getString("abstractResourceSet.checkPath", path));
        }
    }

    @Override // org.apache.catalina.WebResourceSet
    public final void setRoot(WebResourceRoot root) {
        this.root = root;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final WebResourceRoot getRoot() {
        return this.root;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final String getInternalPath() {
        return this.internalPath;
    }

    public final void setInternalPath(String internalPath) {
        checkPath(internalPath);
        if (internalPath.equals("/")) {
            this.internalPath = "";
        } else {
            this.internalPath = internalPath;
        }
    }

    public final void setWebAppMount(String webAppMount) {
        checkPath(webAppMount);
        if (webAppMount.equals("/")) {
            this.webAppMount = "";
        } else {
            this.webAppMount = webAppMount;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final String getWebAppMount() {
        return this.webAppMount;
    }

    public final void setBase(String base) {
        this.base = base;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final String getBase() {
        return this.base;
    }

    @Override // org.apache.catalina.WebResourceSet
    public boolean getClassLoaderOnly() {
        return this.classLoaderOnly;
    }

    @Override // org.apache.catalina.WebResourceSet
    public void setClassLoaderOnly(boolean classLoaderOnly) {
        this.classLoaderOnly = classLoaderOnly;
    }

    @Override // org.apache.catalina.WebResourceSet
    public boolean getStaticOnly() {
        return this.staticOnly;
    }

    @Override // org.apache.catalina.WebResourceSet
    public void setStaticOnly(boolean staticOnly) {
        this.staticOnly = staticOnly;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setManifest(Manifest manifest) {
        this.manifest = manifest;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Manifest getManifest() {
        return this.manifest;
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected final void startInternal() throws LifecycleException {
        setState(LifecycleState.STARTING);
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected final void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected final void destroyInternal() throws LifecycleException {
        gc();
    }
}
