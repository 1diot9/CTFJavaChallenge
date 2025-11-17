package org.apache.tomcat.jni;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/jni/CertificateVerifier.class */
public interface CertificateVerifier {
    boolean verify(long j, byte[][] bArr, String str);
}
