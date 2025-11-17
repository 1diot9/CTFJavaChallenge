package org.apache.tomcat;

import jakarta.servlet.ServletContext;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/JarScanner.class */
public interface JarScanner {
    void scan(JarScanType jarScanType, ServletContext servletContext, JarScannerCallback jarScannerCallback);

    JarScanFilter getJarScanFilter();

    void setJarScanFilter(JarScanFilter jarScanFilter);
}
