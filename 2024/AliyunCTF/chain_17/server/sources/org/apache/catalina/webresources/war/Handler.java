package org.apache.catalina.webresources.war;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import org.springframework.util.ResourceUtils;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/webresources/war/Handler.class */
public class Handler extends URLStreamHandler {
    @Override // java.net.URLStreamHandler
    protected URLConnection openConnection(URL u) throws IOException {
        return new WarURLConnection(u);
    }

    @Override // java.net.URLStreamHandler
    protected void setURL(URL u, String protocol, String host, int port, String authority, String userInfo, String path, String query, String ref) {
        if (path.startsWith(ResourceUtils.FILE_URL_PREFIX) && !path.startsWith("file:/")) {
            path = "file:/" + path.substring(5);
        }
        super.setURL(u, protocol, host, port, authority, userInfo, path, query, ref);
    }
}
