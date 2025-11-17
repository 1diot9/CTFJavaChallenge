package org.apache.catalina.webresources;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import org.apache.catalina.webresources.AbstractArchiveResource;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.UriUtil;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/webresources/JarWarResource.class */
public class JarWarResource extends AbstractArchiveResource {
    private static final Log log = LogFactory.getLog((Class<?>) JarWarResource.class);
    private final String archivePath;

    public JarWarResource(AbstractArchiveResourceSet archiveResourceSet, String webAppPath, String baseUrl, JarEntry jarEntry, String archivePath) {
        super(archiveResourceSet, webAppPath, "jar:war:" + baseUrl + UriUtil.getWarSeparator() + archivePath + "!/", jarEntry, "war:" + baseUrl + UriUtil.getWarSeparator() + archivePath);
        this.archivePath = archivePath;
    }

    @Override // org.apache.catalina.webresources.AbstractArchiveResource
    protected AbstractArchiveResource.JarInputStreamWrapper getJarInputStreamWrapper() {
        JarFile warFile = null;
        JarInputStream jarIs = null;
        JarEntry entry = null;
        try {
            try {
                warFile = getArchiveResourceSet().openJarFile();
                JarEntry jarFileInWar = warFile.getJarEntry(this.archivePath);
                InputStream isInWar = warFile.getInputStream(jarFileInWar);
                jarIs = new JarInputStream(isInWar);
                entry = jarIs.getNextJarEntry();
                while (entry != null && !entry.getName().equals(getResource().getName())) {
                    entry = jarIs.getNextJarEntry();
                }
                if (entry == null) {
                    if (entry == null) {
                        if (jarIs != null) {
                            try {
                                jarIs.close();
                            } catch (IOException e) {
                            }
                        }
                        if (warFile != null) {
                            getArchiveResourceSet().closeJarFile();
                        }
                    }
                    return null;
                }
                AbstractArchiveResource.JarInputStreamWrapper jarInputStreamWrapper = new AbstractArchiveResource.JarInputStreamWrapper(this, entry, jarIs);
                if (entry == null) {
                    if (jarIs != null) {
                        try {
                            jarIs.close();
                        } catch (IOException e2) {
                        }
                    }
                    if (warFile != null) {
                        getArchiveResourceSet().closeJarFile();
                    }
                }
                return jarInputStreamWrapper;
            } catch (IOException e3) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("jarResource.getInputStreamFail", getResource().getName(), getBaseUrl()), e3);
                }
                if (0 == 0) {
                    if (jarIs != null) {
                        try {
                            jarIs.close();
                        } catch (IOException e4) {
                        }
                    }
                    if (warFile != null) {
                        getArchiveResourceSet().closeJarFile();
                    }
                }
                return null;
            }
        } catch (Throwable th) {
            if (entry == null) {
                if (jarIs != null) {
                    try {
                        jarIs.close();
                    } catch (IOException e5) {
                    }
                }
                if (warFile != null) {
                    getArchiveResourceSet().closeJarFile();
                }
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.webresources.AbstractResource
    public Log getLog() {
        return log;
    }
}
