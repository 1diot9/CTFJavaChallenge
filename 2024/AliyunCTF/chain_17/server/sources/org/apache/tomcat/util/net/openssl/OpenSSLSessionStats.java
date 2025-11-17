package org.apache.tomcat.util.net.openssl;

import org.apache.tomcat.jni.SSLContext;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/openssl/OpenSSLSessionStats.class */
public final class OpenSSLSessionStats {
    private final long context;

    /* JADX INFO: Access modifiers changed from: package-private */
    public OpenSSLSessionStats(long context) {
        this.context = context;
    }

    public long number() {
        return SSLContext.sessionNumber(this.context);
    }

    public long connect() {
        return SSLContext.sessionConnect(this.context);
    }

    public long connectGood() {
        return SSLContext.sessionConnectGood(this.context);
    }

    public long connectRenegotiate() {
        return SSLContext.sessionConnectRenegotiate(this.context);
    }

    public long accept() {
        return SSLContext.sessionAccept(this.context);
    }

    public long acceptGood() {
        return SSLContext.sessionAcceptGood(this.context);
    }

    public long acceptRenegotiate() {
        return SSLContext.sessionAcceptRenegotiate(this.context);
    }

    public long hits() {
        return SSLContext.sessionHits(this.context);
    }

    public long cbHits() {
        return SSLContext.sessionCbHits(this.context);
    }

    public long misses() {
        return SSLContext.sessionMisses(this.context);
    }

    public long timeouts() {
        return SSLContext.sessionTimeouts(this.context);
    }

    public long cacheFull() {
        return SSLContext.sessionCacheFull(this.context);
    }
}
