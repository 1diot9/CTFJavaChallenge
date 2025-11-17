package org.apache.tomcat.jni;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/jni/SSLContext.class */
public final class SSLContext {
    public static final byte[] DEFAULT_SESSION_ID_CONTEXT = {100, 101, 102, 97, 117, 108, 116};
    private static final Map<Long, SNICallBack> sniCallBacks = new ConcurrentHashMap();

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/jni/SSLContext$SNICallBack.class */
    public interface SNICallBack {
        long getSslContext(String str);
    }

    public static native long make(long j, int i, int i2) throws Exception;

    public static native int free(long j);

    public static native void setOptions(long j, int i);

    public static native int getOptions(long j);

    public static native void clearOptions(long j, int i);

    public static native String[] getCiphers(long j);

    public static native boolean setCipherSuite(long j, String str) throws Exception;

    public static native boolean setCARevocation(long j, String str, String str2) throws Exception;

    public static native boolean setCertificateChainFile(long j, String str, boolean z);

    public static native boolean setCertificate(long j, String str, String str2, String str3, int i) throws Exception;

    public static native long setSessionCacheSize(long j, long j2);

    public static native long getSessionCacheSize(long j);

    public static native long setSessionCacheTimeout(long j, long j2);

    public static native long getSessionCacheTimeout(long j);

    public static native long setSessionCacheMode(long j, long j2);

    public static native long getSessionCacheMode(long j);

    public static native long sessionAccept(long j);

    public static native long sessionAcceptGood(long j);

    public static native long sessionAcceptRenegotiate(long j);

    public static native long sessionCacheFull(long j);

    public static native long sessionCbHits(long j);

    public static native long sessionConnect(long j);

    public static native long sessionConnectGood(long j);

    public static native long sessionConnectRenegotiate(long j);

    public static native long sessionHits(long j);

    public static native long sessionMisses(long j);

    public static native long sessionNumber(long j);

    public static native long sessionTimeouts(long j);

    public static native void setSessionTicketKeys(long j, byte[] bArr);

    public static native boolean setCACertificate(long j, String str, String str2) throws Exception;

    public static native void setVerify(long j, int i, int i2);

    public static native void setCertVerifyCallback(long j, CertificateVerifier certificateVerifier);

    public static native void setAlpnProtos(long j, String[] strArr, int i);

    public static native boolean setSessionIdContext(long j, byte[] bArr);

    public static native boolean setCertificateRaw(long j, byte[] bArr, byte[] bArr2, int i);

    public static native boolean addChainCertificateRaw(long j, byte[] bArr);

    public static native boolean addClientCACertificateRaw(long j, byte[] bArr);

    public static long sniCallBack(long currentCtx, String sniHostName) {
        SNICallBack sniCallBack = sniCallBacks.get(Long.valueOf(currentCtx));
        if (sniCallBack == null) {
            return 0L;
        }
        String hostName = sniHostName == null ? null : sniHostName.toLowerCase(Locale.ENGLISH);
        return sniCallBack.getSslContext(hostName);
    }
}
