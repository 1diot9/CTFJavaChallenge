package org.apache.catalina.webresources;

import java.io.File;
import java.io.InputStream;
import java.util.Set;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.util.ResourceSet;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/webresources/FileResourceSet.class */
public class FileResourceSet extends AbstractFileResourceSet {
    public FileResourceSet() {
        super("/");
    }

    public FileResourceSet(WebResourceRoot root, String webAppMount, String base, String internalPath) {
        super(internalPath);
        setRoot(root);
        setWebAppMount(webAppMount);
        setBase(base);
        if (getRoot().getState().isAvailable()) {
            try {
                start();
            } catch (LifecycleException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override // org.apache.catalina.WebResourceSet
    public WebResource getResource(String path) {
        checkPath(path);
        String webAppMount = getWebAppMount();
        WebResourceRoot root = getRoot();
        if (path.equals(webAppMount)) {
            File f = file("", true);
            if (f == null) {
                return new EmptyResource(root, path);
            }
            return new FileResource(root, path, f, isReadOnly(), null);
        }
        if (path.charAt(path.length() - 1) != '/') {
            path = path + "/";
        }
        if (webAppMount.startsWith(path)) {
            String name = path.substring(0, path.length() - 1);
            String name2 = name.substring(name.lastIndexOf(47) + 1);
            if (name2.length() > 0) {
                return new VirtualResource(root, path, name2);
            }
        }
        return new EmptyResource(root, path);
    }

    @Override // org.apache.catalina.WebResourceSet
    public String[] list(String path) {
        checkPath(path);
        if (path.charAt(path.length() - 1) != '/') {
            path = path + "/";
        }
        String webAppMount = getWebAppMount();
        if (webAppMount.startsWith(path)) {
            String webAppMount2 = webAppMount.substring(path.length());
            if (webAppMount2.equals(getFileBase().getName())) {
                return new String[]{getFileBase().getName()};
            }
            int i = webAppMount2.indexOf(47);
            if (i > 0) {
                return new String[]{webAppMount2.substring(0, i)};
            }
        }
        return EMPTY_STRING_ARRAY;
    }

    @Override // org.apache.catalina.WebResourceSet
    public Set<String> listWebAppPaths(String path) {
        checkPath(path);
        ResourceSet<String> result = new ResourceSet<>();
        if (path.charAt(path.length() - 1) != '/') {
            path = path + "/";
        }
        String webAppMount = getWebAppMount();
        if (webAppMount.startsWith(path)) {
            String webAppMount2 = webAppMount.substring(path.length());
            if (webAppMount2.equals(getFileBase().getName())) {
                result.add(path + getFileBase().getName());
            } else {
                int i = webAppMount2.indexOf(47);
                if (i > 0) {
                    result.add(path + webAppMount2.substring(0, i + 1));
                }
            }
        }
        result.setLocked(true);
        return result;
    }

    @Override // org.apache.catalina.WebResourceSet
    public boolean mkdir(String path) {
        checkPath(path);
        return false;
    }

    @Override // org.apache.catalina.WebResourceSet
    public boolean write(String path, InputStream is, boolean overwrite) {
        checkPath(path);
        return false;
    }

    @Override // org.apache.catalina.webresources.AbstractFileResourceSet
    protected void checkType(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException(sm.getString("fileResourceSet.notFile", getBase(), File.separator, getInternalPath()));
        }
    }
}
