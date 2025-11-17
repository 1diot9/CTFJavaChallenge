package org.apache.catalina.webresources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.util.ResourceSet;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/webresources/AbstractArchiveResourceSet.class */
public abstract class AbstractArchiveResourceSet extends AbstractResourceSet {
    private URL baseUrl;
    private String baseUrlString;
    private JarContents jarContents;
    private JarFile archive = null;
    protected Map<String, JarEntry> archiveEntries = null;
    protected final Object archiveLock = new Object();
    private long archiveUseCount = 0;
    private boolean retainBloomFilterForArchives = false;

    protected abstract Map<String, JarEntry> getArchiveEntries(boolean z);

    protected abstract JarEntry getArchiveEntry(String str);

    protected abstract boolean isMultiRelease();

    protected abstract WebResource createArchiveResource(JarEntry jarEntry, String str, Manifest manifest);

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setBaseUrl(URL baseUrl) {
        this.baseUrl = baseUrl;
        if (baseUrl == null) {
            this.baseUrlString = null;
        } else {
            this.baseUrlString = baseUrl.toString();
        }
    }

    @Override // org.apache.catalina.WebResourceSet
    public final URL getBaseUrl() {
        return this.baseUrl;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final String getBaseUrlString() {
        return this.baseUrlString;
    }

    @Override // org.apache.catalina.WebResourceSet
    public final String[] list(String path) {
        String name;
        checkPath(path);
        String webAppMount = getWebAppMount();
        ArrayList<String> result = new ArrayList<>();
        if (path.startsWith(webAppMount)) {
            String pathInJar = getInternalPath() + path.substring(webAppMount.length());
            if (pathInJar.length() > 0 && pathInJar.charAt(0) == '/') {
                pathInJar = pathInJar.substring(1);
            }
            for (String name2 : getArchiveEntries(false).keySet()) {
                if (name2.length() > pathInJar.length() && name2.startsWith(pathInJar)) {
                    if (name2.charAt(name2.length() - 1) == '/') {
                        name = name2.substring(pathInJar.length(), name2.length() - 1);
                    } else {
                        name = name2.substring(pathInJar.length());
                    }
                    if (name.length() != 0) {
                        if (name.charAt(0) == '/') {
                            name = name.substring(1);
                        }
                        if (name.length() > 0 && name.lastIndexOf(47) == -1) {
                            result.add(name);
                        }
                    }
                }
            }
        } else {
            if (!path.endsWith("/")) {
                path = path + "/";
            }
            if (webAppMount.startsWith(path)) {
                int i = webAppMount.indexOf(47, path.length());
                if (i == -1) {
                    return new String[]{webAppMount.substring(path.length())};
                }
                return new String[]{webAppMount.substring(path.length(), i)};
            }
        }
        return (String[]) result.toArray(new String[0]);
    }

    @Override // org.apache.catalina.WebResourceSet
    public final Set<String> listWebAppPaths(String path) {
        checkPath(path);
        String webAppMount = getWebAppMount();
        ResourceSet<String> result = new ResourceSet<>();
        if (path.startsWith(webAppMount)) {
            String pathInJar = getInternalPath() + path.substring(webAppMount.length());
            if (pathInJar.length() > 0) {
                if (pathInJar.charAt(pathInJar.length() - 1) != '/') {
                    pathInJar = pathInJar.substring(1) + "/";
                }
                if (pathInJar.charAt(0) == '/') {
                    pathInJar = pathInJar.substring(1);
                }
            }
            for (String name : getArchiveEntries(false).keySet()) {
                if (name.length() > pathInJar.length() && name.startsWith(pathInJar)) {
                    int nextSlash = name.indexOf(47, pathInJar.length());
                    if (nextSlash != -1 && nextSlash != name.length() - 1) {
                        name = name.substring(0, nextSlash + 1);
                    }
                    result.add(webAppMount + "/" + name.substring(getInternalPath().length()));
                }
            }
        } else {
            if (!path.endsWith("/")) {
                path = path + "/";
            }
            if (webAppMount.startsWith(path)) {
                int i = webAppMount.indexOf(47, path.length());
                if (i == -1) {
                    result.add(webAppMount + "/");
                } else {
                    result.add(webAppMount.substring(0, i + 1));
                }
            }
        }
        result.setLocked(true);
        return result;
    }

    @Override // org.apache.catalina.WebResourceSet
    public final boolean mkdir(String path) {
        checkPath(path);
        return false;
    }

    @Override // org.apache.catalina.WebResourceSet
    public final boolean write(String path, InputStream is, boolean overwrite) {
        checkPath(path);
        if (is == null) {
            throw new NullPointerException(sm.getString("dirResourceSet.writeNpe"));
        }
        return false;
    }

    @Override // org.apache.catalina.WebResourceSet
    public final WebResource getResource(String path) {
        checkPath(path);
        String webAppMount = getWebAppMount();
        WebResourceRoot root = getRoot();
        if (this.jarContents != null && !this.jarContents.mightContainResource(path, webAppMount)) {
            return new EmptyResource(root, path);
        }
        if (path.startsWith(webAppMount)) {
            String pathInJar = getInternalPath() + path.substring(webAppMount.length());
            if (pathInJar.length() > 0 && pathInJar.charAt(0) == '/') {
                pathInJar = pathInJar.substring(1);
            }
            if (pathInJar.equals("")) {
                if (!path.endsWith("/")) {
                    path = path + "/";
                }
                return new JarResourceRoot(root, new File(getBase()), this.baseUrlString, path);
            }
            JarEntry jarEntry = null;
            if (isMultiRelease()) {
                jarEntry = getArchiveEntry(pathInJar);
            } else {
                Map<String, JarEntry> jarEntries = getArchiveEntries(true);
                if (pathInJar.charAt(pathInJar.length() - 1) != '/') {
                    if (jarEntries == null) {
                        jarEntry = getArchiveEntry(pathInJar + "/");
                    } else {
                        jarEntry = jarEntries.get(pathInJar + "/");
                    }
                    if (jarEntry != null) {
                        path = path + "/";
                    }
                }
                if (jarEntry == null) {
                    if (jarEntries == null) {
                        jarEntry = getArchiveEntry(pathInJar);
                    } else {
                        jarEntry = jarEntries.get(pathInJar);
                    }
                }
            }
            if (jarEntry == null) {
                return new EmptyResource(root, path);
            }
            return createArchiveResource(jarEntry, path, getManifest());
        }
        return new EmptyResource(root, path);
    }

    @Override // org.apache.catalina.WebResourceSet
    public final boolean isReadOnly() {
        return true;
    }

    @Override // org.apache.catalina.WebResourceSet
    public void setReadOnly(boolean readOnly) {
        if (readOnly) {
        } else {
            throw new IllegalArgumentException(sm.getString("abstractArchiveResourceSet.setReadOnlyFalse"));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JarFile openJarFile() throws IOException {
        JarFile jarFile;
        synchronized (this.archiveLock) {
            if (this.archive == null) {
                this.archive = new JarFile(new File(getBase()), true, 1, Runtime.version());
                WebResourceRoot root = getRoot();
                if (root.getArchiveIndexStrategyEnum().getUsesBloom() || (root.getContext() != null && root.getContext().getUseBloomFilterForArchives())) {
                    this.jarContents = new JarContents(this.archive);
                    this.retainBloomFilterForArchives = root.getArchiveIndexStrategyEnum().getRetain();
                }
            }
            this.archiveUseCount++;
            jarFile = this.archive;
        }
        return jarFile;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void closeJarFile() {
        synchronized (this.archiveLock) {
            this.archiveUseCount--;
        }
    }

    @Override // org.apache.catalina.WebResourceSet
    public void gc() {
        synchronized (this.archiveLock) {
            if (this.archive != null && this.archiveUseCount == 0) {
                try {
                    this.archive.close();
                } catch (IOException e) {
                }
                this.archive = null;
                this.archiveEntries = null;
                if (!this.retainBloomFilterForArchives) {
                    this.jarContents = null;
                }
            }
        }
    }
}
