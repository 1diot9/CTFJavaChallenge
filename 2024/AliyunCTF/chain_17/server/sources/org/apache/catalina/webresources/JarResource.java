package org.apache.catalina.webresources;

import java.util.jar.JarEntry;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/webresources/JarResource.class */
public class JarResource extends AbstractSingleArchiveResource {
    private static final Log log = LogFactory.getLog((Class<?>) JarResource.class);

    public JarResource(AbstractArchiveResourceSet archiveResourceSet, String webAppPath, String baseUrl, JarEntry jarEntry) {
        super(archiveResourceSet, webAppPath, "jar:" + baseUrl + "!/", jarEntry, baseUrl);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.webresources.AbstractResource
    public Log getLog() {
        return log;
    }
}
