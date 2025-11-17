package org.apache.tomcat.util.net.openssl;

import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionBindingEvent;
import javax.net.ssl.SSLSessionBindingListener;
import javax.net.ssl.SSLSessionContext;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jni.Buffer;
import org.apache.tomcat.jni.Pool;
import org.apache.tomcat.jni.SSL;
import org.apache.tomcat.jni.SSLContext;
import org.apache.tomcat.util.buf.ByteBufferUtils;
import org.apache.tomcat.util.net.Constants;
import org.apache.tomcat.util.net.SSLUtil;
import org.apache.tomcat.util.net.openssl.ciphers.OpenSSLCipherConfigurationParser;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/openssl/OpenSSLEngine.class */
public final class OpenSSLEngine extends SSLEngine implements SSLUtil.ProtocolInfo {
    private static final Log logger = LogFactory.getLog((Class<?>) OpenSSLEngine.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) OpenSSLEngine.class);
    private static final Certificate[] EMPTY_CERTIFICATES = new Certificate[0];
    public static final Set<String> AVAILABLE_CIPHER_SUITES;
    public static final Set<String> IMPLEMENTED_PROTOCOLS_SET;
    private static final int MAX_PLAINTEXT_LENGTH = 16384;
    private static final int MAX_COMPRESSED_LENGTH = 17408;
    private static final int MAX_CIPHERTEXT_LENGTH = 18432;
    static final int VERIFY_DEPTH = 10;
    static final int MAX_ENCRYPTED_PACKET_LENGTH = 18713;
    static final int MAX_ENCRYPTION_OVERHEAD_LENGTH = 2329;
    private static final String INVALID_CIPHER = "SSL_NULL_WITH_NULL_NULL";
    private static final long EMPTY_ADDR;
    private final OpenSSLState state;
    private final Cleaner.Cleanable cleanable;
    private boolean handshakeFinished;
    private int currentHandshake;
    private boolean receivedShutdown;
    private volatile boolean destroyed;
    private volatile String version;
    private volatile String cipher;
    private volatile String applicationProtocol;
    private volatile Certificate[] peerCerts;

    @Deprecated
    private volatile X509Certificate[] x509PeerCerts;
    private boolean isInboundDone;
    private boolean isOutboundDone;
    private boolean engineClosed;
    private final boolean clientMode;
    private final String fallbackApplicationProtocol;
    private final OpenSSLSessionContext sessionContext;
    private final boolean alpn;
    private final boolean initialized;
    private final int certificateVerificationDepth;
    private final boolean certificateVerificationOptionalNoCA;
    private final OpenSSLSession session;
    private Accepted accepted = Accepted.NOT;
    private volatile ClientAuthMode clientAuth = ClientAuthMode.NONE;
    private boolean sendHandshakeError = false;
    private String selectedProtocol = null;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/openssl/OpenSSLEngine$Accepted.class */
    public enum Accepted {
        NOT,
        IMPLICIT,
        EXPLICIT
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/openssl/OpenSSLEngine$ClientAuthMode.class */
    public enum ClientAuthMode {
        NONE,
        OPTIONAL,
        REQUIRE
    }

    /* JADX WARN: Finally extract failed */
    static {
        Set<String> availableCipherSuites = new LinkedHashSet<>(128);
        long aprPool = Pool.create(0L);
        try {
            try {
                long sslCtx = SSLContext.make(aprPool, SSL.SSL_PROTOCOL_ALL, 1);
                try {
                    SSLContext.setOptions(sslCtx, SSL.SSL_OP_ALL);
                    SSLContext.setCipherSuite(sslCtx, "ALL");
                    long ssl = SSL.newSSL(sslCtx, true);
                    try {
                        for (String c : SSL.getCiphers(ssl)) {
                            if (c != null && c.length() != 0 && !availableCipherSuites.contains(c)) {
                                availableCipherSuites.add(OpenSSLCipherConfigurationParser.openSSLToJsse(c));
                            }
                        }
                        SSL.freeSSL(ssl);
                        SSLContext.free(sslCtx);
                        Pool.destroy(aprPool);
                    } catch (Throwable th) {
                        SSL.freeSSL(ssl);
                        throw th;
                    }
                } catch (Throwable th2) {
                    SSLContext.free(sslCtx);
                    throw th2;
                }
            } catch (Exception e) {
                logger.warn(sm.getString("engine.ciphersFailure"), e);
                Pool.destroy(aprPool);
            }
            AVAILABLE_CIPHER_SUITES = Collections.unmodifiableSet(availableCipherSuites);
            HashSet<String> protocols = new HashSet<>();
            protocols.add(Constants.SSL_PROTO_SSLv2Hello);
            protocols.add(Constants.SSL_PROTO_SSLv2);
            protocols.add(Constants.SSL_PROTO_SSLv3);
            protocols.add(Constants.SSL_PROTO_TLSv1);
            protocols.add(Constants.SSL_PROTO_TLSv1_1);
            protocols.add(Constants.SSL_PROTO_TLSv1_2);
            if (SSL.version() >= 269488143) {
                protocols.add(Constants.SSL_PROTO_TLSv1_3);
            }
            IMPLEMENTED_PROTOCOLS_SET = Collections.unmodifiableSet(protocols);
            EMPTY_ADDR = Buffer.address(ByteBuffer.allocate(0));
        } catch (Throwable th3) {
            Pool.destroy(aprPool);
            throw th3;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public OpenSSLEngine(Cleaner cleaner, long sslCtx, String fallbackApplicationProtocol, boolean clientMode, OpenSSLSessionContext sessionContext, boolean alpn, boolean initialized, int certificateVerificationDepth, boolean certificateVerificationOptionalNoCA) {
        if (sslCtx == 0) {
            throw new IllegalArgumentException(sm.getString("engine.noSSLContext"));
        }
        this.session = new OpenSSLSession();
        long ssl = SSL.newSSL(sslCtx, !clientMode);
        long networkBIO = SSL.makeNetworkBIO(ssl);
        this.state = new OpenSSLState(ssl, networkBIO);
        this.cleanable = cleaner.register(this, this.state);
        this.fallbackApplicationProtocol = fallbackApplicationProtocol;
        this.clientMode = clientMode;
        this.sessionContext = sessionContext;
        this.alpn = alpn;
        this.initialized = initialized;
        this.certificateVerificationDepth = certificateVerificationDepth;
        this.certificateVerificationOptionalNoCA = certificateVerificationOptionalNoCA;
    }

    @Override // org.apache.tomcat.util.net.SSLUtil.ProtocolInfo
    public String getNegotiatedProtocol() {
        return this.selectedProtocol;
    }

    public synchronized void shutdown() {
        if (!this.destroyed) {
            this.destroyed = true;
            this.cleanable.clean();
            this.engineClosed = true;
            this.isOutboundDone = true;
            this.isInboundDone = true;
        }
    }

    private int writePlaintextData(long ssl, ByteBuffer src) throws SSLException {
        int sslWrote;
        clearLastError();
        int pos = src.position();
        int limit = src.limit();
        int len = Math.min(limit - pos, 16384);
        if (src.isDirect()) {
            long addr = Buffer.address(src) + pos;
            sslWrote = SSL.writeToSSL(ssl, addr, len);
            if (sslWrote <= 0) {
                checkLastError();
            }
            if (sslWrote >= 0) {
                src.position(pos + sslWrote);
                return sslWrote;
            }
        } else {
            ByteBuffer buf = ByteBuffer.allocateDirect(len);
            try {
                long addr2 = Buffer.address(buf);
                src.limit(pos + len);
                buf.put(src);
                src.limit(limit);
                sslWrote = SSL.writeToSSL(ssl, addr2, len);
                if (sslWrote <= 0) {
                    checkLastError();
                }
                if (sslWrote >= 0) {
                    src.position(pos + sslWrote);
                    buf.clear();
                    ByteBufferUtils.cleanDirectBuffer(buf);
                    return sslWrote;
                }
                src.position(pos);
                buf.clear();
                ByteBufferUtils.cleanDirectBuffer(buf);
            } catch (Throwable th) {
                buf.clear();
                ByteBufferUtils.cleanDirectBuffer(buf);
                throw th;
            }
        }
        throw new IllegalStateException(sm.getString("engine.writeToSSLFailed", Integer.toString(sslWrote)));
    }

    private int writeEncryptedData(long networkBIO, ByteBuffer src) throws SSLException {
        clearLastError();
        int pos = src.position();
        int len = src.remaining();
        if (src.isDirect()) {
            long addr = Buffer.address(src) + pos;
            int netWrote = SSL.writeToBIO(networkBIO, addr, len);
            if (netWrote <= 0) {
                checkLastError();
            }
            if (netWrote >= 0) {
                src.position(pos + netWrote);
                return netWrote;
            }
            return 0;
        }
        ByteBuffer buf = ByteBuffer.allocateDirect(len);
        try {
            long addr2 = Buffer.address(buf);
            buf.put(src);
            int netWrote2 = SSL.writeToBIO(networkBIO, addr2, len);
            if (netWrote2 <= 0) {
                checkLastError();
            }
            if (netWrote2 >= 0) {
                src.position(pos + netWrote2);
                buf.clear();
                ByteBufferUtils.cleanDirectBuffer(buf);
                return netWrote2;
            }
            src.position(pos);
            buf.clear();
            ByteBufferUtils.cleanDirectBuffer(buf);
            return 0;
        } catch (Throwable th) {
            buf.clear();
            ByteBufferUtils.cleanDirectBuffer(buf);
            throw th;
        }
    }

    private int readPlaintextData(long ssl, ByteBuffer dst) throws SSLException {
        clearLastError();
        if (dst.isDirect()) {
            int pos = dst.position();
            long addr = Buffer.address(dst) + pos;
            int sslRead = SSL.readFromSSL(ssl, addr, dst.limit() - pos);
            if (sslRead > 0) {
                dst.position(pos + sslRead);
                return sslRead;
            }
            checkLastError();
            return 0;
        }
        int pos2 = dst.position();
        int limit = dst.limit();
        int len = Math.min(MAX_ENCRYPTED_PACKET_LENGTH, limit - pos2);
        ByteBuffer buf = ByteBuffer.allocateDirect(len);
        try {
            long addr2 = Buffer.address(buf);
            int sslRead2 = SSL.readFromSSL(ssl, addr2, len);
            if (sslRead2 > 0) {
                buf.limit(sslRead2);
                dst.limit(pos2 + sslRead2);
                dst.put(buf);
                dst.limit(limit);
                buf.clear();
                ByteBufferUtils.cleanDirectBuffer(buf);
                return sslRead2;
            }
            checkLastError();
            buf.clear();
            ByteBufferUtils.cleanDirectBuffer(buf);
            return 0;
        } catch (Throwable th) {
            buf.clear();
            ByteBufferUtils.cleanDirectBuffer(buf);
            throw th;
        }
    }

    private int readEncryptedData(long networkBIO, ByteBuffer dst, int pending) throws SSLException {
        clearLastError();
        if (dst.isDirect() && dst.remaining() >= pending) {
            int pos = dst.position();
            long addr = Buffer.address(dst) + pos;
            int bioRead = SSL.readFromBIO(networkBIO, addr, pending);
            if (bioRead > 0) {
                dst.position(pos + bioRead);
                return bioRead;
            }
            checkLastError();
            return 0;
        }
        ByteBuffer buf = ByteBuffer.allocateDirect(pending);
        try {
            long addr2 = Buffer.address(buf);
            int bioRead2 = SSL.readFromBIO(networkBIO, addr2, pending);
            if (bioRead2 > 0) {
                buf.limit(bioRead2);
                int oldLimit = dst.limit();
                dst.limit(dst.position() + bioRead2);
                dst.put(buf);
                dst.limit(oldLimit);
                buf.clear();
                ByteBufferUtils.cleanDirectBuffer(buf);
                return bioRead2;
            }
            checkLastError();
            buf.clear();
            ByteBufferUtils.cleanDirectBuffer(buf);
            return 0;
        } catch (Throwable th) {
            buf.clear();
            ByteBufferUtils.cleanDirectBuffer(buf);
            throw th;
        }
    }

    @Override // javax.net.ssl.SSLEngine
    public synchronized SSLEngineResult wrap(ByteBuffer[] srcs, int offset, int length, ByteBuffer dst) throws SSLException {
        if (this.destroyed) {
            return new SSLEngineResult(SSLEngineResult.Status.CLOSED, SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, 0, 0);
        }
        if (srcs == null || dst == null) {
            throw new IllegalArgumentException(sm.getString("engine.nullBuffer"));
        }
        if (offset >= srcs.length || offset + length > srcs.length) {
            throw new IndexOutOfBoundsException(sm.getString("engine.invalidBufferArray", Integer.toString(offset), Integer.toString(length), Integer.toString(srcs.length)));
        }
        if (dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        if (this.accepted == Accepted.NOT) {
            beginHandshakeImplicitly();
        }
        SSLEngineResult.HandshakeStatus handshakeStatus = getHandshakeStatus();
        if ((!this.handshakeFinished || this.engineClosed) && handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_UNWRAP) {
            return new SSLEngineResult(getEngineStatus(), SSLEngineResult.HandshakeStatus.NEED_UNWRAP, 0, 0);
        }
        int pendingNet = SSL.pendingWrittenBytesInBIO(this.state.networkBIO);
        if (pendingNet > 0) {
            int capacity = dst.remaining();
            if (capacity < pendingNet) {
                return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, handshakeStatus, 0, 0);
            }
            try {
                int bytesProduced = readEncryptedData(this.state.networkBIO, dst, pendingNet);
                if (this.isOutboundDone) {
                    shutdown();
                }
                return new SSLEngineResult(getEngineStatus(), getHandshakeStatus(), 0, bytesProduced);
            } catch (Exception e) {
                throw new SSLException(e);
            }
        }
        int bytesConsumed = 0;
        int endOffset = offset + length;
        for (int i = offset; i < endOffset; i++) {
            ByteBuffer src = srcs[i];
            if (src == null) {
                throw new IllegalArgumentException(sm.getString("engine.nullBufferInArray"));
            }
            while (src.hasRemaining()) {
                try {
                    bytesConsumed += writePlaintextData(this.state.ssl, src);
                    int pendingNet2 = SSL.pendingWrittenBytesInBIO(this.state.networkBIO);
                    if (pendingNet2 > 0) {
                        int capacity2 = dst.remaining();
                        if (capacity2 < pendingNet2) {
                            return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, getHandshakeStatus(), bytesConsumed, 0);
                        }
                        try {
                            int bytesProduced2 = 0 + readEncryptedData(this.state.networkBIO, dst, pendingNet2);
                            return new SSLEngineResult(getEngineStatus(), getHandshakeStatus(), bytesConsumed, bytesProduced2);
                        } catch (Exception e2) {
                            throw new SSLException(e2);
                        }
                    }
                } catch (Exception e3) {
                    throw new SSLException(e3);
                }
            }
        }
        return new SSLEngineResult(getEngineStatus(), getHandshakeStatus(), bytesConsumed, 0);
    }

    @Override // javax.net.ssl.SSLEngine
    public synchronized SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts, int offset, int length) throws SSLException {
        if (this.destroyed) {
            return new SSLEngineResult(SSLEngineResult.Status.CLOSED, SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, 0, 0);
        }
        if (src == null || dsts == null) {
            throw new IllegalArgumentException(sm.getString("engine.nullBuffer"));
        }
        if (offset >= dsts.length || offset + length > dsts.length) {
            throw new IndexOutOfBoundsException(sm.getString("engine.invalidBufferArray", Integer.toString(offset), Integer.toString(length), Integer.toString(dsts.length)));
        }
        int capacity = 0;
        int endOffset = offset + length;
        for (int i = offset; i < endOffset; i++) {
            ByteBuffer dst = dsts[i];
            if (dst == null) {
                throw new IllegalArgumentException(sm.getString("engine.nullBufferInArray"));
            }
            if (dst.isReadOnly()) {
                throw new ReadOnlyBufferException();
            }
            capacity += dst.remaining();
        }
        if (this.accepted == Accepted.NOT) {
            beginHandshakeImplicitly();
        }
        SSLEngineResult.HandshakeStatus handshakeStatus = getHandshakeStatus();
        if ((!this.handshakeFinished || this.engineClosed) && handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_WRAP) {
            return new SSLEngineResult(getEngineStatus(), SSLEngineResult.HandshakeStatus.NEED_WRAP, 0, 0);
        }
        int len = src.remaining();
        if (len > MAX_ENCRYPTED_PACKET_LENGTH) {
            this.isInboundDone = true;
            this.isOutboundDone = true;
            this.engineClosed = true;
            shutdown();
            throw new SSLException(sm.getString("engine.oversizedPacket"));
        }
        try {
            int written = writeEncryptedData(this.state.networkBIO, src);
            int pendingApp = pendingReadableBytesInSSL();
            if (!this.handshakeFinished) {
                pendingApp = 0;
            }
            int bytesProduced = 0;
            int idx = offset;
            if (capacity == 0) {
                return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, getHandshakeStatus(), written, 0);
            }
            while (pendingApp > 0) {
                if (idx == endOffset) {
                    throw new IllegalStateException(sm.getString("engine.invalidDestinationBuffersState"));
                }
                while (idx < endOffset) {
                    ByteBuffer dst2 = dsts[idx];
                    if (!dst2.hasRemaining()) {
                        idx++;
                    } else {
                        if (pendingApp <= 0) {
                            break;
                        }
                        try {
                            int bytesRead = readPlaintextData(this.state.ssl, dst2);
                            if (bytesRead == 0) {
                                throw new IllegalStateException(sm.getString("engine.failedToReadAvailableBytes"));
                            }
                            bytesProduced += bytesRead;
                            pendingApp -= bytesRead;
                            capacity -= bytesRead;
                            if (!dst2.hasRemaining()) {
                                idx++;
                            }
                        } catch (Exception e) {
                            throw new SSLException(e);
                        }
                    }
                }
                if (capacity == 0) {
                    break;
                }
                if (pendingApp == 0) {
                    pendingApp = pendingReadableBytesInSSL();
                }
            }
            if (!this.receivedShutdown && (SSL.getShutdown(this.state.ssl) & 2) == 2) {
                this.receivedShutdown = true;
                closeOutbound();
                closeInbound();
            }
            if (bytesProduced == 0 && (written == 0 || (written > 0 && !src.hasRemaining() && this.handshakeFinished))) {
                return new SSLEngineResult(SSLEngineResult.Status.BUFFER_UNDERFLOW, getHandshakeStatus(), written, 0);
            }
            return new SSLEngineResult(getEngineStatus(), getHandshakeStatus(), written, bytesProduced);
        } catch (Exception e2) {
            throw new SSLException(e2);
        }
    }

    private int pendingReadableBytesInSSL() throws SSLException {
        clearLastError();
        int lastPrimingReadResult = SSL.readFromSSL(this.state.ssl, EMPTY_ADDR, 0);
        if (lastPrimingReadResult <= 0) {
            checkLastError();
        }
        int pendingReadableBytesInSSL = SSL.pendingReadableBytesInSSL(this.state.ssl);
        if (Constants.SSL_PROTO_TLSv1.equals(this.version) && lastPrimingReadResult == 0 && pendingReadableBytesInSSL == 0) {
            if (SSL.readFromSSL(this.state.ssl, EMPTY_ADDR, 0) <= 0) {
                checkLastError();
            }
            pendingReadableBytesInSSL = SSL.pendingReadableBytesInSSL(this.state.ssl);
        }
        return pendingReadableBytesInSSL;
    }

    @Override // javax.net.ssl.SSLEngine
    public Runnable getDelegatedTask() {
        return null;
    }

    @Override // javax.net.ssl.SSLEngine
    public synchronized void closeInbound() throws SSLException {
        if (this.isInboundDone) {
            return;
        }
        this.isInboundDone = true;
        this.engineClosed = true;
        shutdown();
        if (this.accepted != Accepted.NOT && !this.receivedShutdown) {
            throw new SSLException(sm.getString("engine.inboundClose"));
        }
    }

    @Override // javax.net.ssl.SSLEngine
    public synchronized boolean isInboundDone() {
        return this.isInboundDone || this.engineClosed;
    }

    @Override // javax.net.ssl.SSLEngine
    public synchronized void closeOutbound() {
        if (this.isOutboundDone) {
            return;
        }
        this.isOutboundDone = true;
        this.engineClosed = true;
        if (this.accepted != Accepted.NOT && !this.destroyed) {
            int mode = SSL.getShutdown(this.state.ssl);
            if ((mode & 1) != 1) {
                SSL.shutdownSSL(this.state.ssl);
                return;
            }
            return;
        }
        shutdown();
    }

    @Override // javax.net.ssl.SSLEngine
    public synchronized boolean isOutboundDone() {
        return this.isOutboundDone;
    }

    @Override // javax.net.ssl.SSLEngine
    public String[] getSupportedCipherSuites() {
        Set<String> availableCipherSuites = AVAILABLE_CIPHER_SUITES;
        return (String[]) availableCipherSuites.toArray(new String[0]);
    }

    @Override // javax.net.ssl.SSLEngine
    public synchronized String[] getEnabledCipherSuites() {
        if (this.destroyed) {
            return new String[0];
        }
        String[] enabled = SSL.getCiphers(this.state.ssl);
        if (enabled == null) {
            return new String[0];
        }
        for (int i = 0; i < enabled.length; i++) {
            String mapped = OpenSSLCipherConfigurationParser.openSSLToJsse(enabled[i]);
            if (mapped != null) {
                enabled[i] = mapped;
            }
        }
        return enabled;
    }

    @Override // javax.net.ssl.SSLEngine
    public synchronized void setEnabledCipherSuites(String[] cipherSuites) {
        if (this.initialized) {
            return;
        }
        if (cipherSuites == null) {
            throw new IllegalArgumentException(sm.getString("engine.nullCipherSuite"));
        }
        if (this.destroyed) {
            return;
        }
        StringBuilder buf = new StringBuilder();
        for (String cipherSuite : cipherSuites) {
            if (cipherSuite == null) {
                break;
            }
            String converted = OpenSSLCipherConfigurationParser.jsseToOpenSSL(cipherSuite);
            if (!AVAILABLE_CIPHER_SUITES.contains(cipherSuite)) {
                logger.debug(sm.getString("engine.unsupportedCipher", cipherSuite, converted));
            }
            if (converted != null) {
                cipherSuite = converted;
            }
            buf.append(cipherSuite);
            buf.append(':');
        }
        if (buf.length() == 0) {
            throw new IllegalArgumentException(sm.getString("engine.emptyCipherSuite"));
        }
        buf.setLength(buf.length() - 1);
        String cipherSuiteSpec = buf.toString();
        try {
            SSL.setCipherSuites(this.state.ssl, cipherSuiteSpec);
        } catch (Exception e) {
            throw new IllegalStateException(sm.getString("engine.failedCipherSuite", cipherSuiteSpec), e);
        }
    }

    @Override // javax.net.ssl.SSLEngine
    public String[] getSupportedProtocols() {
        return (String[]) IMPLEMENTED_PROTOCOLS_SET.toArray(new String[0]);
    }

    @Override // javax.net.ssl.SSLEngine
    public synchronized String[] getEnabledProtocols() {
        if (this.destroyed) {
            return new String[0];
        }
        List<String> enabled = new ArrayList<>();
        enabled.add(Constants.SSL_PROTO_SSLv2Hello);
        int opts = SSL.getOptions(this.state.ssl);
        if ((opts & SSL.SSL_OP_NO_TLSv1) == 0) {
            enabled.add(Constants.SSL_PROTO_TLSv1);
        }
        if ((opts & SSL.SSL_OP_NO_TLSv1_1) == 0) {
            enabled.add(Constants.SSL_PROTO_TLSv1_1);
        }
        if ((opts & SSL.SSL_OP_NO_TLSv1_2) == 0) {
            enabled.add(Constants.SSL_PROTO_TLSv1_2);
        }
        if ((opts & SSL.SSL_OP_NO_SSLv2) == 0) {
            enabled.add(Constants.SSL_PROTO_SSLv2);
        }
        if ((opts & SSL.SSL_OP_NO_SSLv3) == 0) {
            enabled.add(Constants.SSL_PROTO_SSLv3);
        }
        return (String[]) enabled.toArray(new String[0]);
    }

    @Override // javax.net.ssl.SSLEngine
    public synchronized void setEnabledProtocols(String[] protocols) {
        if (this.initialized) {
            return;
        }
        if (protocols == null) {
            throw new IllegalArgumentException();
        }
        if (this.destroyed) {
            return;
        }
        boolean sslv2 = false;
        boolean sslv3 = false;
        boolean tlsv1 = false;
        boolean tlsv1_1 = false;
        boolean tlsv1_2 = false;
        for (String p : protocols) {
            if (!IMPLEMENTED_PROTOCOLS_SET.contains(p)) {
                throw new IllegalArgumentException(sm.getString("engine.unsupportedProtocol", p));
            }
            if (p.equals(Constants.SSL_PROTO_SSLv2)) {
                sslv2 = true;
            } else if (p.equals(Constants.SSL_PROTO_SSLv3)) {
                sslv3 = true;
            } else if (p.equals(Constants.SSL_PROTO_TLSv1)) {
                tlsv1 = true;
            } else if (p.equals(Constants.SSL_PROTO_TLSv1_1)) {
                tlsv1_1 = true;
            } else if (p.equals(Constants.SSL_PROTO_TLSv1_2)) {
                tlsv1_2 = true;
            }
        }
        SSL.setOptions(this.state.ssl, SSL.SSL_OP_ALL);
        if (!sslv2) {
            SSL.setOptions(this.state.ssl, SSL.SSL_OP_NO_SSLv2);
        }
        if (!sslv3) {
            SSL.setOptions(this.state.ssl, SSL.SSL_OP_NO_SSLv3);
        }
        if (!tlsv1) {
            SSL.setOptions(this.state.ssl, SSL.SSL_OP_NO_TLSv1);
        }
        if (!tlsv1_1) {
            SSL.setOptions(this.state.ssl, SSL.SSL_OP_NO_TLSv1_1);
        }
        if (!tlsv1_2) {
            SSL.setOptions(this.state.ssl, SSL.SSL_OP_NO_TLSv1_2);
        }
    }

    @Override // javax.net.ssl.SSLEngine
    public SSLSession getSession() {
        return this.session;
    }

    @Override // javax.net.ssl.SSLEngine
    public synchronized void beginHandshake() throws SSLException {
        if (this.engineClosed || this.destroyed) {
            throw new SSLException(sm.getString("engine.engineClosed"));
        }
        switch (this.accepted) {
            case NOT:
                handshake();
                this.accepted = Accepted.EXPLICIT;
                return;
            case IMPLICIT:
                this.accepted = Accepted.EXPLICIT;
                return;
            case EXPLICIT:
                renegotiate();
                return;
            default:
                return;
        }
    }

    private void beginHandshakeImplicitly() throws SSLException {
        handshake();
        this.accepted = Accepted.IMPLICIT;
    }

    private void handshake() throws SSLException {
        this.currentHandshake = SSL.getHandshakeCount(this.state.ssl);
        clearLastError();
        int code = SSL.doHandshake(this.state.ssl);
        if (code <= 0) {
            checkLastError();
            return;
        }
        if (this.alpn) {
            this.selectedProtocol = SSL.getAlpnSelected(this.state.ssl);
        }
        this.session.lastAccessedTime = System.currentTimeMillis();
        this.handshakeFinished = true;
    }

    private synchronized void renegotiate() throws SSLException {
        int code;
        clearLastError();
        if (SSL.getVersion(this.state.ssl).equals(Constants.SSL_PROTO_TLSv1_3)) {
            code = SSL.verifyClientPostHandshake(this.state.ssl);
        } else {
            code = SSL.renegotiate(this.state.ssl);
        }
        if (code <= 0) {
            checkLastError();
        }
        this.handshakeFinished = false;
        this.peerCerts = null;
        this.x509PeerCerts = null;
        this.currentHandshake = SSL.getHandshakeCount(this.state.ssl);
        int code2 = SSL.doHandshake(this.state.ssl);
        if (code2 <= 0) {
            checkLastError();
        }
    }

    private void checkLastError() throws SSLException {
        String sslError = getLastError();
        if (sslError != null) {
            if (!this.handshakeFinished) {
                this.sendHandshakeError = true;
                return;
            }
            throw new SSLException(sslError);
        }
    }

    private static void clearLastError() {
        getLastError();
    }

    private static String getLastError() {
        String sslError = null;
        while (true) {
            long error = SSL.getLastErrorNumber();
            if (error != 0) {
                String err = SSL.getErrorString(error);
                if (sslError == null) {
                    sslError = err;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug(sm.getString("engine.openSSLError", Long.toString(error), err));
                }
            } else {
                return sslError;
            }
        }
    }

    private SSLEngineResult.Status getEngineStatus() {
        return this.engineClosed ? SSLEngineResult.Status.CLOSED : SSLEngineResult.Status.OK;
    }

    @Override // javax.net.ssl.SSLEngine
    public synchronized SSLEngineResult.HandshakeStatus getHandshakeStatus() {
        if (this.accepted == Accepted.NOT || this.destroyed) {
            return SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
        }
        if (!this.handshakeFinished) {
            if (this.sendHandshakeError || SSL.pendingWrittenBytesInBIO(this.state.networkBIO) != 0) {
                if (this.sendHandshakeError) {
                    this.sendHandshakeError = false;
                    this.currentHandshake++;
                }
                return SSLEngineResult.HandshakeStatus.NEED_WRAP;
            }
            int handshakeCount = SSL.getHandshakeCount(this.state.ssl);
            if (handshakeCount != this.currentHandshake && SSL.renegotiatePending(this.state.ssl) == 0 && SSL.getPostHandshakeAuthInProgress(this.state.ssl) == 0) {
                if (this.alpn) {
                    this.selectedProtocol = SSL.getAlpnSelected(this.state.ssl);
                }
                this.session.lastAccessedTime = System.currentTimeMillis();
                this.version = SSL.getVersion(this.state.ssl);
                this.handshakeFinished = true;
                return SSLEngineResult.HandshakeStatus.FINISHED;
            }
            return SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
        }
        if (this.engineClosed) {
            if (SSL.pendingWrittenBytesInBIO(this.state.networkBIO) != 0) {
                return SSLEngineResult.HandshakeStatus.NEED_WRAP;
            }
            return SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
        }
        return SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
    }

    @Override // javax.net.ssl.SSLEngine
    public void setUseClientMode(boolean clientMode) {
        if (clientMode != this.clientMode) {
            throw new UnsupportedOperationException();
        }
    }

    @Override // javax.net.ssl.SSLEngine
    public boolean getUseClientMode() {
        return this.clientMode;
    }

    @Override // javax.net.ssl.SSLEngine
    public void setNeedClientAuth(boolean b) {
        setClientAuth(b ? ClientAuthMode.REQUIRE : ClientAuthMode.NONE);
    }

    @Override // javax.net.ssl.SSLEngine
    public boolean getNeedClientAuth() {
        return this.clientAuth == ClientAuthMode.REQUIRE;
    }

    @Override // javax.net.ssl.SSLEngine
    public void setWantClientAuth(boolean b) {
        setClientAuth(b ? ClientAuthMode.OPTIONAL : ClientAuthMode.NONE);
    }

    @Override // javax.net.ssl.SSLEngine
    public boolean getWantClientAuth() {
        return this.clientAuth == ClientAuthMode.OPTIONAL;
    }

    private void setClientAuth(ClientAuthMode mode) {
        if (this.clientMode) {
            return;
        }
        synchronized (this) {
            if (this.clientAuth == mode) {
                return;
            }
            switch (mode) {
                case NONE:
                    SSL.setVerify(this.state.ssl, 0, this.certificateVerificationDepth);
                    break;
                case REQUIRE:
                    SSL.setVerify(this.state.ssl, 2, this.certificateVerificationDepth);
                    break;
                case OPTIONAL:
                    SSL.setVerify(this.state.ssl, this.certificateVerificationOptionalNoCA ? 3 : 1, this.certificateVerificationDepth);
                    break;
            }
            this.clientAuth = mode;
        }
    }

    @Override // javax.net.ssl.SSLEngine
    public void setEnableSessionCreation(boolean b) {
        if (!b) {
            String msg = sm.getString("engine.noRestrictSessionCreation");
            throw new UnsupportedOperationException(msg);
        }
    }

    @Override // javax.net.ssl.SSLEngine
    public boolean getEnableSessionCreation() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/openssl/OpenSSLEngine$OpenSSLSession.class */
    public class OpenSSLSession implements SSLSession {
        private Map<String, Object> values;
        private long lastAccessedTime = -1;

        private OpenSSLSession() {
        }

        @Override // javax.net.ssl.SSLSession
        public byte[] getId() {
            byte[] id = null;
            synchronized (OpenSSLEngine.this) {
                if (!OpenSSLEngine.this.destroyed) {
                    id = SSL.getSessionId(OpenSSLEngine.this.state.ssl);
                }
            }
            return id;
        }

        @Override // javax.net.ssl.SSLSession
        public SSLSessionContext getSessionContext() {
            return OpenSSLEngine.this.sessionContext;
        }

        @Override // javax.net.ssl.SSLSession
        public long getCreationTime() {
            long creationTime = 0;
            synchronized (OpenSSLEngine.this) {
                if (!OpenSSLEngine.this.destroyed) {
                    creationTime = SSL.getTime(OpenSSLEngine.this.state.ssl);
                }
            }
            return creationTime * 1000;
        }

        @Override // javax.net.ssl.SSLSession
        public long getLastAccessedTime() {
            return this.lastAccessedTime > 0 ? this.lastAccessedTime : getCreationTime();
        }

        @Override // javax.net.ssl.SSLSession
        public void invalidate() {
        }

        @Override // javax.net.ssl.SSLSession
        public boolean isValid() {
            return false;
        }

        @Override // javax.net.ssl.SSLSession
        public void putValue(String name, Object value) {
            if (name == null) {
                throw new IllegalArgumentException(OpenSSLEngine.sm.getString("engine.nullName"));
            }
            if (value == null) {
                throw new IllegalArgumentException(OpenSSLEngine.sm.getString("engine.nullValue"));
            }
            Map<String, Object> values = this.values;
            if (values == null) {
                HashMap hashMap = new HashMap(2);
                this.values = hashMap;
                values = hashMap;
            }
            Object old = values.put(name, value);
            if (value instanceof SSLSessionBindingListener) {
                ((SSLSessionBindingListener) value).valueBound(new SSLSessionBindingEvent(this, name));
            }
            notifyUnbound(old, name);
        }

        @Override // javax.net.ssl.SSLSession
        public Object getValue(String name) {
            if (name == null) {
                throw new IllegalArgumentException(OpenSSLEngine.sm.getString("engine.nullName"));
            }
            if (this.values == null) {
                return null;
            }
            return this.values.get(name);
        }

        @Override // javax.net.ssl.SSLSession
        public void removeValue(String name) {
            if (name == null) {
                throw new IllegalArgumentException(OpenSSLEngine.sm.getString("engine.nullName"));
            }
            Map<String, Object> values = this.values;
            if (values == null) {
                return;
            }
            Object old = values.remove(name);
            notifyUnbound(old, name);
        }

        @Override // javax.net.ssl.SSLSession
        public String[] getValueNames() {
            Map<String, Object> values = this.values;
            if (values == null || values.isEmpty()) {
                return new String[0];
            }
            return (String[]) values.keySet().toArray(new String[0]);
        }

        private void notifyUnbound(Object value, String name) {
            if (value instanceof SSLSessionBindingListener) {
                ((SSLSessionBindingListener) value).valueUnbound(new SSLSessionBindingEvent(this, name));
            }
        }

        @Override // javax.net.ssl.SSLSession
        public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
            byte[][] chain;
            byte[] clientCert;
            Certificate[] certificates;
            Certificate[] c = OpenSSLEngine.this.peerCerts;
            if (c == null) {
                synchronized (OpenSSLEngine.this) {
                    if (OpenSSLEngine.this.destroyed || SSL.isInInit(OpenSSLEngine.this.state.ssl) != 0) {
                        throw new SSLPeerUnverifiedException(OpenSSLEngine.sm.getString("engine.unverifiedPeer"));
                    }
                    chain = SSL.getPeerCertChain(OpenSSLEngine.this.state.ssl);
                    if (!OpenSSLEngine.this.clientMode) {
                        clientCert = SSL.getPeerCertificate(OpenSSLEngine.this.state.ssl);
                    } else {
                        clientCert = null;
                    }
                }
                if (chain == null && clientCert == null) {
                    return null;
                }
                int len = 0;
                if (chain != null) {
                    len = 0 + chain.length;
                }
                int i = 0;
                if (clientCert != null) {
                    certificates = new Certificate[len + 1];
                    i = 0 + 1;
                    certificates[0] = new OpenSSLX509Certificate(clientCert);
                } else {
                    certificates = new Certificate[len];
                }
                if (chain != null) {
                    int a = 0;
                    while (i < certificates.length) {
                        int i2 = a;
                        a++;
                        certificates[i] = new OpenSSLX509Certificate(chain[i2]);
                        i++;
                    }
                }
                Certificate[] certificateArr = certificates;
                OpenSSLEngine.this.peerCerts = certificateArr;
                c = certificateArr;
            }
            return c;
        }

        @Override // javax.net.ssl.SSLSession
        public Certificate[] getLocalCertificates() {
            return OpenSSLEngine.EMPTY_CERTIFICATES;
        }

        @Override // javax.net.ssl.SSLSession
        @Deprecated
        public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
            byte[][] chain;
            X509Certificate[] c = OpenSSLEngine.this.x509PeerCerts;
            if (c == null) {
                synchronized (OpenSSLEngine.this) {
                    if (OpenSSLEngine.this.destroyed || SSL.isInInit(OpenSSLEngine.this.state.ssl) != 0) {
                        throw new SSLPeerUnverifiedException(OpenSSLEngine.sm.getString("engine.unverifiedPeer"));
                    }
                    chain = SSL.getPeerCertChain(OpenSSLEngine.this.state.ssl);
                }
                if (chain == null) {
                    throw new SSLPeerUnverifiedException(OpenSSLEngine.sm.getString("engine.unverifiedPeer"));
                }
                X509Certificate[] peerCerts = new X509Certificate[chain.length];
                for (int i = 0; i < peerCerts.length; i++) {
                    try {
                        peerCerts[i] = X509Certificate.getInstance(chain[i]);
                    } catch (CertificateException e) {
                        throw new IllegalStateException(e);
                    }
                }
                OpenSSLEngine.this.x509PeerCerts = peerCerts;
                c = peerCerts;
            }
            return c;
        }

        @Override // javax.net.ssl.SSLSession
        public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
            Certificate[] peer = getPeerCertificates();
            if (peer == null || peer.length == 0) {
                return null;
            }
            return principal(peer);
        }

        @Override // javax.net.ssl.SSLSession
        public Principal getLocalPrincipal() {
            Certificate[] local = getLocalCertificates();
            if (local == null || local.length == 0) {
                return null;
            }
            return principal(local);
        }

        private Principal principal(Certificate[] certs) {
            return ((java.security.cert.X509Certificate) certs[0]).getIssuerX500Principal();
        }

        @Override // javax.net.ssl.SSLSession
        public String getCipherSuite() {
            if (OpenSSLEngine.this.cipher == null) {
                synchronized (OpenSSLEngine.this) {
                    if (!OpenSSLEngine.this.handshakeFinished) {
                        return OpenSSLEngine.INVALID_CIPHER;
                    }
                    if (OpenSSLEngine.this.destroyed) {
                        return OpenSSLEngine.INVALID_CIPHER;
                    }
                    String ciphers = SSL.getCipherForSSL(OpenSSLEngine.this.state.ssl);
                    String c = OpenSSLCipherConfigurationParser.openSSLToJsse(ciphers);
                    if (c != null) {
                        OpenSSLEngine.this.cipher = c;
                    }
                }
            }
            return OpenSSLEngine.this.cipher;
        }

        @Override // javax.net.ssl.SSLSession
        public String getProtocol() {
            String applicationProtocol = OpenSSLEngine.this.applicationProtocol;
            if (applicationProtocol == null) {
                applicationProtocol = OpenSSLEngine.this.fallbackApplicationProtocol;
                if (applicationProtocol != null) {
                    OpenSSLEngine.this.applicationProtocol = applicationProtocol.replace(':', '_');
                } else {
                    applicationProtocol = "";
                    OpenSSLEngine.this.applicationProtocol = "";
                }
            }
            String version = null;
            synchronized (OpenSSLEngine.this) {
                if (!OpenSSLEngine.this.destroyed) {
                    version = SSL.getVersion(OpenSSLEngine.this.state.ssl);
                }
            }
            if (applicationProtocol.isEmpty()) {
                return version;
            }
            return version + ":" + applicationProtocol;
        }

        @Override // javax.net.ssl.SSLSession
        public String getPeerHost() {
            return null;
        }

        @Override // javax.net.ssl.SSLSession
        public int getPeerPort() {
            return 0;
        }

        @Override // javax.net.ssl.SSLSession
        public int getPacketBufferSize() {
            return OpenSSLEngine.MAX_ENCRYPTED_PACKET_LENGTH;
        }

        @Override // javax.net.ssl.SSLSession
        public int getApplicationBufferSize() {
            return 16384;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/openssl/OpenSSLEngine$OpenSSLState.class */
    public static class OpenSSLState implements Runnable {
        private final long ssl;
        private final long networkBIO;

        private OpenSSLState(long ssl, long networkBIO) {
            this.ssl = ssl;
            this.networkBIO = networkBIO;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.networkBIO != 0) {
                SSL.freeBIO(this.networkBIO);
            }
            if (this.ssl != 0) {
                SSL.freeSSL(this.ssl);
            }
        }
    }
}
