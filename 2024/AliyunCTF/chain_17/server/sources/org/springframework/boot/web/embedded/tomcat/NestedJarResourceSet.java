package org.springframework.boot.web.embedded.tomcat;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Supplier;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.webresources.AbstractSingleArchiveResourceSet;
import org.apache.catalina.webresources.JarResource;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/tomcat/NestedJarResourceSet.class */
class NestedJarResourceSet extends AbstractSingleArchiveResourceSet {
    private static final Attributes.Name MULTI_RELEASE = new Attributes.Name("Multi-Release");
    private final URL url;
    private JarFile archive = null;
    private long archiveUseCount = 0;
    private boolean useCaches;
    private volatile Boolean multiRelease;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NestedJarResourceSet(URL url, WebResourceRoot root, String webAppMount, String internalPath) throws IllegalArgumentException {
        this.url = url;
        setRoot(root);
        setWebAppMount(webAppMount);
        setInternalPath(internalPath);
        setStaticOnly(true);
        if (getRoot().getState().isAvailable()) {
            try {
                start();
            } catch (LifecycleException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

    @Override // org.apache.catalina.webresources.AbstractArchiveResourceSet
    protected WebResource createArchiveResource(JarEntry jarEntry, String webAppPath, Manifest manifest) {
        return new JarResource(this, webAppPath, getBaseUrlString(), jarEntry);
    }

    /* JADX WARN: Finally extract failed */
    @Override // org.apache.catalina.webresources.AbstractSingleArchiveResourceSet, org.apache.catalina.util.LifecycleBase
    protected void initInternal() throws LifecycleException {
        try {
            JarURLConnection connection = connect();
            try {
                setManifest(connection.getManifest());
                setBaseUrl(connection.getJarFileURL());
                if (!connection.getUseCaches()) {
                    connection.getJarFile().close();
                }
            } catch (Throwable th) {
                if (!connection.getUseCaches()) {
                    connection.getJarFile().close();
                }
                throw th;
            }
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.webresources.AbstractArchiveResourceSet
    public JarFile openJarFile() throws IOException {
        JarFile jarFile;
        synchronized (this.archiveLock) {
            if (this.archive == null) {
                JarURLConnection connection = connect();
                this.useCaches = connection.getUseCaches();
                this.archive = connection.getJarFile();
            }
            this.archiveUseCount++;
            jarFile = this.archive;
        }
        return jarFile;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.webresources.AbstractArchiveResourceSet
    public void closeJarFile() {
        synchronized (this.archiveLock) {
            this.archiveUseCount--;
        }
    }

    @Override // org.apache.catalina.webresources.AbstractSingleArchiveResourceSet, org.apache.catalina.webresources.AbstractArchiveResourceSet
    protected boolean isMultiRelease() {
        if (this.multiRelease == null) {
            synchronized (this.archiveLock) {
                if (this.multiRelease == null) {
                    Manifest manifest = getManifest();
                    Attributes attributes = manifest != null ? manifest.getMainAttributes() : null;
                    this.multiRelease = Boolean.valueOf(attributes != null ? attributes.containsKey(MULTI_RELEASE) : false);
                }
            }
        }
        return this.multiRelease.booleanValue();
    }

    @Override // org.apache.catalina.webresources.AbstractArchiveResourceSet, org.apache.catalina.WebResourceSet
    public void gc() {
        synchronized (this.archiveLock) {
            if (this.archive != null && this.archiveUseCount == 0) {
                try {
                    if (!this.useCaches) {
                        this.archive.close();
                    }
                } catch (IOException e) {
                }
                this.archive = null;
                this.archiveEntries = null;
            }
        }
    }

    private JarURLConnection connect() throws IOException {
        URLConnection connection = this.url.openConnection();
        ResourceUtils.useCachesIfNecessary(connection);
        Assert.state(connection instanceof JarURLConnection, (Supplier<String>) () -> {
            return "URL '%s' did not return a JAR connection".formatted(this.url);
        });
        connection.connect();
        return (JarURLConnection) connection;
    }
}
