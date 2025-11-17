package org.apache.tomcat.util.scan;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/scan/UrlJar.class */
public class UrlJar extends AbstractInputStreamJar {
    public UrlJar(URL jarFileURL) {
        super(jarFileURL);
    }

    @Override // org.apache.tomcat.Jar, java.lang.AutoCloseable
    public void close() {
        closeStream();
    }

    @Override // org.apache.tomcat.util.scan.AbstractInputStreamJar
    protected NonClosingJarInputStream createJarInputStream() throws IOException {
        JarURLConnection jarConn = (JarURLConnection) getJarFileURL().openConnection();
        URL resourceURL = jarConn.getJarFileURL();
        URLConnection resourceConn = resourceURL.openConnection();
        resourceConn.setUseCaches(false);
        return new NonClosingJarInputStream(resourceConn.getInputStream());
    }
}
