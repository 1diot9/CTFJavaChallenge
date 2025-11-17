package org.springframework.http.server.reactive;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLSession;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/DefaultSslInfo.class */
final class DefaultSslInfo implements SslInfo {

    @Nullable
    private final String sessionId;

    @Nullable
    private final X509Certificate[] peerCertificates;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultSslInfo(@Nullable String sessionId, X509Certificate[] peerCertificates) {
        Assert.notNull(peerCertificates, "No SSL certificates");
        this.sessionId = sessionId;
        this.peerCertificates = peerCertificates;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultSslInfo(SSLSession session) {
        Assert.notNull(session, "SSLSession is required");
        this.sessionId = initSessionId(session);
        this.peerCertificates = initCertificates(session);
    }

    @Override // org.springframework.http.server.reactive.SslInfo
    @Nullable
    public String getSessionId() {
        return this.sessionId;
    }

    @Override // org.springframework.http.server.reactive.SslInfo
    @Nullable
    public X509Certificate[] getPeerCertificates() {
        return this.peerCertificates;
    }

    @Nullable
    private static String initSessionId(SSLSession session) {
        byte[] bytes = session.getId();
        if (bytes == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String digit = Integer.toHexString(b);
            if (digit.length() < 2) {
                sb.append('0');
            }
            if (digit.length() > 2) {
                digit = digit.substring(digit.length() - 2);
            }
            sb.append(digit);
        }
        return sb.toString();
    }

    @Nullable
    private static X509Certificate[] initCertificates(SSLSession session) {
        try {
            Certificate[] certificates = session.getPeerCertificates();
            List<X509Certificate> result = new ArrayList<>(certificates.length);
            for (Certificate certificate : certificates) {
                if (certificate instanceof X509Certificate) {
                    X509Certificate x509Certificate = (X509Certificate) certificate;
                    result.add(x509Certificate);
                }
            }
            if (result.isEmpty()) {
                return null;
            }
            return (X509Certificate[]) result.toArray(new X509Certificate[0]);
        } catch (Throwable th) {
            return null;
        }
    }
}
