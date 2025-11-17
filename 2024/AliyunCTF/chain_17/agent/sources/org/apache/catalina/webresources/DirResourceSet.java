package org.apache.catalina.webresources;

import cn.hutool.http.useragent.UserAgentInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.jar.Manifest;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.util.ResourceSet;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/webresources/DirResourceSet.class */
public class DirResourceSet extends AbstractFileResourceSet {
    private static final Log log = LogFactory.getLog((Class<?>) DirResourceSet.class);

    public DirResourceSet() {
        super("/");
    }

    public DirResourceSet(WebResourceRoot root, String webAppMount, String base, String internalPath) {
        super(internalPath);
        setRoot(root);
        setWebAppMount(webAppMount);
        setBase(base);
        if (root.getContext().getAddWebinfClassesResources()) {
            File f = new File(new File(base, internalPath), "/WEB-INF/classes/META-INF/resources");
            if (f.isDirectory()) {
                root.createWebResourceSet(WebResourceRoot.ResourceSetType.RESOURCE_JAR, "/", f.getAbsolutePath(), null, "/");
            }
        }
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
        if (path.startsWith(webAppMount)) {
            File f = file(path.substring(webAppMount.length()), false);
            if (f == null) {
                return new EmptyResource(root, path);
            }
            if (!f.exists()) {
                return new EmptyResource(root, path, f);
            }
            if (f.isDirectory() && path.charAt(path.length() - 1) != '/') {
                path = path + "/";
            }
            return new FileResource(root, path, f, isReadOnly(), getManifest());
        }
        return new EmptyResource(root, path);
    }

    @Override // org.apache.catalina.WebResourceSet
    public String[] list(String path) {
        checkPath(path);
        String webAppMount = getWebAppMount();
        if (path.startsWith(webAppMount)) {
            File f = file(path.substring(webAppMount.length()), true);
            if (f == null) {
                return EMPTY_STRING_ARRAY;
            }
            String[] result = f.list();
            if (result == null) {
                return EMPTY_STRING_ARRAY;
            }
            return result;
        }
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
        return EMPTY_STRING_ARRAY;
    }

    @Override // org.apache.catalina.WebResourceSet
    public Set<String> listWebAppPaths(String path) {
        File[] list;
        checkPath(path);
        String webAppMount = getWebAppMount();
        ResourceSet<String> result = new ResourceSet<>();
        if (path.startsWith(webAppMount)) {
            File f = file(path.substring(webAppMount.length()), true);
            if (f != null && (list = f.listFiles()) != null) {
                for (File entry : list) {
                    if (!getRoot().getAllowLinking()) {
                        boolean symlink = true;
                        String absPath = null;
                        String canPath = null;
                        try {
                            absPath = entry.getAbsolutePath().substring(f.getAbsolutePath().length());
                            String entryCanPath = entry.getCanonicalPath();
                            String fCanPath = f.getCanonicalPath();
                            if (entryCanPath.length() >= fCanPath.length()) {
                                canPath = entryCanPath.substring(fCanPath.length());
                                if (absPath.equals(canPath)) {
                                    symlink = false;
                                }
                            }
                        } catch (IOException e) {
                            canPath = UserAgentInfo.NameUnknown;
                        }
                        if (symlink) {
                            logIgnoredSymlink(getRoot().getContext().getName(), absPath, canPath);
                        }
                    }
                    StringBuilder sb = new StringBuilder(path);
                    if (path.charAt(path.length() - 1) != '/') {
                        sb.append('/');
                    }
                    sb.append(entry.getName());
                    if (entry.isDirectory()) {
                        sb.append('/');
                    }
                    result.add(sb.toString());
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
    public boolean mkdir(String path) {
        File f;
        checkPath(path);
        if (isReadOnly()) {
            return false;
        }
        String webAppMount = getWebAppMount();
        if (!path.startsWith(webAppMount) || (f = file(path.substring(webAppMount.length()), false)) == null) {
            return false;
        }
        return f.mkdir();
    }

    @Override // org.apache.catalina.WebResourceSet
    public boolean write(String path, InputStream is, boolean overwrite) {
        File dest;
        checkPath(path);
        if (is == null) {
            throw new NullPointerException(sm.getString("dirResourceSet.writeNpe"));
        }
        if (isReadOnly() || path.endsWith("/")) {
            return false;
        }
        String webAppMount = getWebAppMount();
        if (!path.startsWith(webAppMount) || (dest = file(path.substring(webAppMount.length()), false)) == null) {
            return false;
        }
        if (dest.exists() && !overwrite) {
            return false;
        }
        try {
            if (overwrite) {
                Files.copy(is, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.copy(is, dest.toPath(), new CopyOption[0]);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override // org.apache.catalina.webresources.AbstractFileResourceSet
    protected void checkType(File file) {
        if (!file.isDirectory()) {
            throw new IllegalArgumentException(sm.getString("dirResourceSet.notDirectory", getBase(), File.separator, getInternalPath()));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.webresources.AbstractFileResourceSet, org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
        File mf;
        super.initInternal();
        if (getWebAppMount().equals("") && (mf = file("META-INF/MANIFEST.MF", true)) != null && mf.isFile()) {
            try {
                FileInputStream fis = new FileInputStream(mf);
                try {
                    setManifest(new Manifest(fis));
                    fis.close();
                } finally {
                }
            } catch (IOException e) {
                log.warn(sm.getString("dirResourceSet.manifestFail", mf.getAbsolutePath()), e);
            }
        }
    }
}
