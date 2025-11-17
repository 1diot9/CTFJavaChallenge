package org.apache.tomcat;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/JarScanFilter.class */
public interface JarScanFilter {
    boolean check(JarScanType jarScanType, String str);

    default boolean isSkipAll() {
        return false;
    }
}
