package org.apache.catalina.webresources;

import java.util.jar.JarEntry;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.UriUtil;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/webresources/WarResource.class */
public class WarResource extends AbstractSingleArchiveResource {
    private static final Log log = LogFactory.getLog((Class<?>) WarResource.class);

    public WarResource(AbstractArchiveResourceSet archiveResourceSet, String webAppPath, String baseUrl, JarEntry jarEntry) {
        super(archiveResourceSet, webAppPath, "war:" + baseUrl + UriUtil.getWarSeparator(), jarEntry, baseUrl);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.webresources.AbstractResource
    public Log getLog() {
        return log;
    }
}
