package org.apache.tomcat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.Manifest;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/Jar.class */
public interface Jar extends AutoCloseable {
    URL getJarFileURL();

    InputStream getInputStream(String str) throws IOException;

    long getLastModified(String str) throws IOException;

    boolean exists(String str) throws IOException;

    @Override // java.lang.AutoCloseable
    void close();

    void nextEntry();

    String getEntryName();

    InputStream getEntryInputStream() throws IOException;

    String getURL(String str);

    Manifest getManifest() throws IOException;

    void reset() throws IOException;
}
