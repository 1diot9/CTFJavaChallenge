package org.apache.tomcat.util.net.openssl;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import org.apache.tomcat.jni.SSLContext;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/openssl/OpenSSLSessionContext.class */
public class OpenSSLSessionContext implements SSLSessionContext {
    private static final StringManager sm = StringManager.getManager((Class<?>) OpenSSLSessionContext.class);
    private static final Enumeration<byte[]> EMPTY = new EmptyEnumeration();
    private final OpenSSLSessionStats stats;
    private final OpenSSLContext context;
    private final long contextID;

    /* JADX INFO: Access modifiers changed from: package-private */
    public OpenSSLSessionContext(OpenSSLContext context) {
        this.context = context;
        this.contextID = context.getSSLContextID();
        this.stats = new OpenSSLSessionStats(this.contextID);
    }

    @Override // javax.net.ssl.SSLSessionContext
    public SSLSession getSession(byte[] bytes) {
        return null;
    }

    @Override // javax.net.ssl.SSLSessionContext
    public Enumeration<byte[]> getIds() {
        return EMPTY;
    }

    public void setTicketKeys(byte[] keys) {
        if (keys == null) {
            throw new IllegalArgumentException(sm.getString("sessionContext.nullTicketKeys"));
        }
        SSLContext.setSessionTicketKeys(this.contextID, keys);
    }

    public void setSessionCacheEnabled(boolean enabled) {
        long mode = enabled ? 2L : 0L;
        SSLContext.setSessionCacheMode(this.contextID, mode);
    }

    public boolean isSessionCacheEnabled() {
        return SSLContext.getSessionCacheMode(this.contextID) == 2;
    }

    public OpenSSLSessionStats stats() {
        return this.stats;
    }

    @Override // javax.net.ssl.SSLSessionContext
    public void setSessionTimeout(int seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException();
        }
        SSLContext.setSessionCacheTimeout(this.contextID, seconds);
    }

    @Override // javax.net.ssl.SSLSessionContext
    public int getSessionTimeout() {
        return (int) SSLContext.getSessionCacheTimeout(this.contextID);
    }

    @Override // javax.net.ssl.SSLSessionContext
    public void setSessionCacheSize(int size) {
        if (size < 0) {
            throw new IllegalArgumentException();
        }
        SSLContext.setSessionCacheSize(this.contextID, size);
    }

    @Override // javax.net.ssl.SSLSessionContext
    public int getSessionCacheSize() {
        return (int) SSLContext.getSessionCacheSize(this.contextID);
    }

    public boolean setSessionIdContext(byte[] sidCtx) {
        return SSLContext.setSessionIdContext(this.contextID, sidCtx);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/openssl/OpenSSLSessionContext$EmptyEnumeration.class */
    private static final class EmptyEnumeration implements Enumeration<byte[]> {
        private EmptyEnumeration() {
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return false;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Enumeration
        public byte[] nextElement() {
            throw new NoSuchElementException();
        }
    }
}
