package org.apache.tomcat.util.scan;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarInputStream;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/scan/NonClosingJarInputStream.class */
public class NonClosingJarInputStream extends JarInputStream {
    public NonClosingJarInputStream(InputStream in, boolean verify) throws IOException {
        super(in, verify);
    }

    public NonClosingJarInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override // java.util.zip.ZipInputStream, java.util.zip.InflaterInputStream, java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
    }

    public void reallyClose() throws IOException {
        super.close();
    }
}
