package org.apache.catalina.webresources;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.WebResourceSet;
import org.apache.catalina.util.LifecycleBase;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/webresources/EmptyResourceSet.class */
public class EmptyResourceSet extends LifecycleBase implements WebResourceSet {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private WebResourceRoot root;
    private boolean classLoaderOnly;
    private boolean staticOnly;

    public EmptyResourceSet(WebResourceRoot root) {
        this.root = root;
    }

    @Override // org.apache.catalina.WebResourceSet
    public WebResource getResource(String path) {
        return new EmptyResource(this.root, path);
    }

    @Override // org.apache.catalina.WebResourceSet
    public String[] list(String path) {
        return EMPTY_STRING_ARRAY;
    }

    @Override // org.apache.catalina.WebResourceSet
    public Set<String> listWebAppPaths(String path) {
        return Collections.emptySet();
    }

    @Override // org.apache.catalina.WebResourceSet
    public boolean mkdir(String path) {
        return false;
    }

    @Override // org.apache.catalina.WebResourceSet
    public boolean write(String path, InputStream is, boolean overwrite) {
        return false;
    }

    @Override // org.apache.catalina.WebResourceSet
    public void setRoot(WebResourceRoot root) {
        this.root = root;
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

    @Override // org.apache.catalina.WebResourceSet
    public URL getBaseUrl() {
        return null;
    }

    @Override // org.apache.catalina.WebResourceSet
    public void setReadOnly(boolean readOnly) {
    }

    @Override // org.apache.catalina.WebResourceSet
    public boolean isReadOnly() {
        return true;
    }

    @Override // org.apache.catalina.WebResourceSet
    public void gc() {
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void initInternal() throws LifecycleException {
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void startInternal() throws LifecycleException {
        setState(LifecycleState.STARTING);
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void destroyInternal() throws LifecycleException {
    }
}
