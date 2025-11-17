package org.apache.catalina.webresources;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.catalina.LifecycleException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.compat.JrePlatform;
import org.apache.tomcat.util.http.RequestUtil;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/webresources/AbstractFileResourceSet.class */
public abstract class AbstractFileResourceSet extends AbstractResourceSet {
    private static final Log log = LogFactory.getLog((Class<?>) AbstractFileResourceSet.class);
    protected static final String[] EMPTY_STRING_ARRAY = new String[0];
    private File fileBase;
    private String absoluteBase;
    private String canonicalBase;
    private boolean readOnly = false;

    protected abstract void checkType(File file);

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractFileResourceSet(String internalPath) {
        setInternalPath(internalPath);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final File getFileBase() {
        return this.fileBase;
    }

    @Override // org.apache.catalina.WebResourceSet
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override // org.apache.catalina.WebResourceSet
    public boolean isReadOnly() {
        return this.readOnly;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final File file(String name, boolean mustExist) {
        String absPath;
        if (name.equals("/")) {
            name = "";
        }
        File file = new File(this.fileBase, name);
        if (name.endsWith("/") && file.isFile()) {
            return null;
        }
        if (mustExist && !file.canRead()) {
            return null;
        }
        if (getRoot().getAllowLinking()) {
            return file;
        }
        if (JrePlatform.IS_WINDOWS && isInvalidWindowsFilename(name)) {
            return null;
        }
        String canPath = null;
        try {
            canPath = file.getCanonicalPath();
        } catch (IOException e) {
        }
        if (canPath == null || !canPath.startsWith(this.canonicalBase) || (absPath = normalize(file.getAbsolutePath())) == null || this.absoluteBase.length() > absPath.length()) {
            return null;
        }
        String absPath2 = absPath.substring(this.absoluteBase.length());
        String canPath2 = canPath.substring(this.canonicalBase.length());
        if (canPath2.length() > 0 && canPath2.charAt(0) != File.separatorChar) {
            return null;
        }
        if (canPath2.length() > 0) {
            canPath2 = normalize(canPath2);
        }
        if (!canPath2.equals(absPath2)) {
            if (!canPath2.equalsIgnoreCase(absPath2)) {
                logIgnoredSymlink(getRoot().getContext().getName(), absPath2, canPath2);
                return null;
            }
            return null;
        }
        return file;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void logIgnoredSymlink(String contextPath, String absPath, String canPath) {
        String msg = sm.getString("abstractFileResourceSet.canonicalfileCheckFailed", contextPath, absPath, canPath);
        if (absPath.startsWith("/META-INF/") || absPath.startsWith("/WEB-INF/")) {
            log.error(msg);
        } else {
            log.warn(msg);
        }
    }

    private boolean isInvalidWindowsFilename(String name) {
        int len = name.length();
        if (len == 0) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            char c = name.charAt(i);
            if (c == '\"' || c == '<' || c == '>' || c == ':') {
                return true;
            }
        }
        if (name.charAt(len - 1) == ' ') {
            return true;
        }
        return false;
    }

    private String normalize(String path) {
        return RequestUtil.normalize(path, File.separatorChar == '\\');
    }

    @Override // org.apache.catalina.WebResourceSet
    public URL getBaseUrl() {
        try {
            return getFileBase().toURI().toURL();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Override // org.apache.catalina.WebResourceSet
    public void gc() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
        this.fileBase = new File(getBase(), getInternalPath());
        checkType(this.fileBase);
        this.absoluteBase = normalize(this.fileBase.getAbsolutePath());
        try {
            this.canonicalBase = this.fileBase.getCanonicalPath();
            if ("/".equals(this.absoluteBase)) {
                this.absoluteBase = "";
            }
            if ("/".equals(this.canonicalBase)) {
                this.canonicalBase = "";
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
