package org.apache.tomcat;

import java.io.File;
import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/JarScannerCallback.class */
public interface JarScannerCallback {
    void scan(Jar jar, String str, boolean z) throws IOException;

    void scan(File file, String str, boolean z) throws IOException;

    void scanWebInfClasses() throws IOException;
}
