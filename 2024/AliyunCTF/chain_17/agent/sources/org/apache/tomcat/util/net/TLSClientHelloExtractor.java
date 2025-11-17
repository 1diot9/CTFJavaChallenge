package org.apache.tomcat.util.net;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.bcel.Const;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.apache.tomcat.util.net.openssl.ciphers.Cipher;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/TLSClientHelloExtractor.class */
public class TLSClientHelloExtractor {
    private final ExtractorResult result;
    private final List<Cipher> clientRequestedCiphers;
    private final List<String> clientRequestedCipherNames;
    private final String sniValue;
    private final List<String> clientRequestedApplicationProtocols;
    private final List<String> clientRequestedProtocols;
    private static final int TLS_RECORD_HEADER_LEN = 5;
    private static final int TLS_EXTENSION_SERVER_NAME = 0;
    private static final int TLS_EXTENSION_ALPN = 16;
    private static final int TLS_EXTENSION_SUPPORTED_VERSION = 43;
    private static final Log log = LogFactory.getLog((Class<?>) TLSClientHelloExtractor.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) TLSClientHelloExtractor.class);
    public static byte[] USE_TLS_RESPONSE = "HTTP/1.1 400 \r\nContent-Type: text/plain;charset=UTF-8\r\nConnection: close\r\n\r\nBad Request\r\nThis combination of host and port requires TLS.\r\n".getBytes(StandardCharsets.UTF_8);

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/TLSClientHelloExtractor$ExtractorResult.class */
    public enum ExtractorResult {
        COMPLETE,
        NOT_PRESENT,
        UNDERFLOW,
        NEED_READ,
        NON_SECURE
    }

    public TLSClientHelloExtractor(ByteBuffer netInBuffer) throws IOException {
        int pos = netInBuffer.position();
        int limit = netInBuffer.limit();
        ExtractorResult result = ExtractorResult.NOT_PRESENT;
        List<Cipher> clientRequestedCiphers = new ArrayList<>();
        List<String> clientRequestedCipherNames = new ArrayList<>();
        List<String> clientRequestedApplicationProtocols = new ArrayList<>();
        List<String> clientRequestedProtocols = new ArrayList<>();
        String sniValue = null;
        try {
            try {
                netInBuffer.flip();
                if (!isAvailable(netInBuffer, 5)) {
                    this.result = handleIncompleteRead(netInBuffer);
                    this.clientRequestedCiphers = clientRequestedCiphers;
                    this.clientRequestedCipherNames = clientRequestedCipherNames;
                    this.clientRequestedApplicationProtocols = clientRequestedApplicationProtocols;
                    this.sniValue = null;
                    this.clientRequestedProtocols = clientRequestedProtocols;
                    netInBuffer.limit(limit);
                    netInBuffer.position(pos);
                    return;
                }
                if (!isTLSHandshake(netInBuffer)) {
                    this.result = isHttp(netInBuffer) ? ExtractorResult.NON_SECURE : result;
                    this.clientRequestedCiphers = clientRequestedCiphers;
                    this.clientRequestedCipherNames = clientRequestedCipherNames;
                    this.clientRequestedApplicationProtocols = clientRequestedApplicationProtocols;
                    this.sniValue = null;
                    this.clientRequestedProtocols = clientRequestedProtocols;
                    netInBuffer.limit(limit);
                    netInBuffer.position(pos);
                    return;
                }
                if (!isAllRecordAvailable(netInBuffer)) {
                    this.result = handleIncompleteRead(netInBuffer);
                    this.clientRequestedCiphers = clientRequestedCiphers;
                    this.clientRequestedCipherNames = clientRequestedCipherNames;
                    this.clientRequestedApplicationProtocols = clientRequestedApplicationProtocols;
                    this.sniValue = null;
                    this.clientRequestedProtocols = clientRequestedProtocols;
                    netInBuffer.limit(limit);
                    netInBuffer.position(pos);
                    return;
                }
                if (isClientHello(netInBuffer)) {
                    if (!isAllClientHelloAvailable(netInBuffer)) {
                        log.warn(sm.getString("sniExtractor.clientHelloTooBig"));
                        this.result = result;
                        this.clientRequestedCiphers = clientRequestedCiphers;
                        this.clientRequestedCipherNames = clientRequestedCipherNames;
                        this.clientRequestedApplicationProtocols = clientRequestedApplicationProtocols;
                        this.sniValue = null;
                        this.clientRequestedProtocols = clientRequestedProtocols;
                        netInBuffer.limit(limit);
                        netInBuffer.position(pos);
                        return;
                    }
                    String legacyVersion = readProtocol(netInBuffer);
                    skipBytes(netInBuffer, 32);
                    skipBytes(netInBuffer, netInBuffer.get() & 255);
                    int cipherCount = netInBuffer.getChar() / 2;
                    for (int i = 0; i < cipherCount; i++) {
                        char cipherId = netInBuffer.getChar();
                        Cipher c = Cipher.valueOf(cipherId);
                        if (c == null) {
                            clientRequestedCipherNames.add("Unknown(0x" + HexUtils.toHexString(cipherId) + ")");
                        } else {
                            clientRequestedCiphers.add(c);
                            clientRequestedCipherNames.add(c.name());
                        }
                    }
                    skipBytes(netInBuffer, netInBuffer.get() & 255);
                    if (!netInBuffer.hasRemaining()) {
                        this.result = result;
                        this.clientRequestedCiphers = clientRequestedCiphers;
                        this.clientRequestedCipherNames = clientRequestedCipherNames;
                        this.clientRequestedApplicationProtocols = clientRequestedApplicationProtocols;
                        this.sniValue = null;
                        this.clientRequestedProtocols = clientRequestedProtocols;
                        netInBuffer.limit(limit);
                        netInBuffer.position(pos);
                        return;
                    }
                    skipBytes(netInBuffer, 2);
                    while (netInBuffer.hasRemaining() && (sniValue == null || clientRequestedApplicationProtocols.isEmpty() || clientRequestedProtocols.isEmpty())) {
                        char extensionType = netInBuffer.getChar();
                        char extensionDataSize = netInBuffer.getChar();
                        switch (extensionType) {
                            case 0:
                                sniValue = readSniExtension(netInBuffer);
                                break;
                            case 16:
                                readAlpnExtension(netInBuffer, clientRequestedApplicationProtocols);
                                break;
                            case '+':
                                readSupportedVersions(netInBuffer, clientRequestedProtocols);
                                break;
                            default:
                                skipBytes(netInBuffer, extensionDataSize);
                                break;
                        }
                    }
                    if (clientRequestedProtocols.isEmpty()) {
                        clientRequestedProtocols.add(legacyVersion);
                    }
                    this.result = ExtractorResult.COMPLETE;
                    this.clientRequestedCiphers = clientRequestedCiphers;
                    this.clientRequestedCipherNames = clientRequestedCipherNames;
                    this.clientRequestedApplicationProtocols = clientRequestedApplicationProtocols;
                    this.sniValue = sniValue;
                    this.clientRequestedProtocols = clientRequestedProtocols;
                    netInBuffer.limit(limit);
                    netInBuffer.position(pos);
                }
            } catch (IllegalArgumentException | BufferUnderflowException e) {
                throw new IOException(sm.getString("sniExtractor.clientHelloInvalid"), e);
            }
        } finally {
            this.result = result;
            this.clientRequestedCiphers = clientRequestedCiphers;
            this.clientRequestedCipherNames = clientRequestedCipherNames;
            this.clientRequestedApplicationProtocols = clientRequestedApplicationProtocols;
            this.sniValue = null;
            this.clientRequestedProtocols = clientRequestedProtocols;
            netInBuffer.limit(limit);
            netInBuffer.position(pos);
        }
    }

    public ExtractorResult getResult() {
        return this.result;
    }

    public String getSNIValue() {
        if (this.result == ExtractorResult.COMPLETE) {
            return this.sniValue;
        }
        throw new IllegalStateException(sm.getString("sniExtractor.tooEarly"));
    }

    public List<Cipher> getClientRequestedCiphers() {
        if (this.result == ExtractorResult.COMPLETE || this.result == ExtractorResult.NOT_PRESENT) {
            return this.clientRequestedCiphers;
        }
        throw new IllegalStateException(sm.getString("sniExtractor.tooEarly"));
    }

    public List<String> getClientRequestedCipherNames() {
        if (this.result == ExtractorResult.COMPLETE || this.result == ExtractorResult.NOT_PRESENT) {
            return this.clientRequestedCipherNames;
        }
        throw new IllegalStateException(sm.getString("sniExtractor.tooEarly"));
    }

    public List<String> getClientRequestedApplicationProtocols() {
        if (this.result == ExtractorResult.COMPLETE || this.result == ExtractorResult.NOT_PRESENT) {
            return this.clientRequestedApplicationProtocols;
        }
        throw new IllegalStateException(sm.getString("sniExtractor.tooEarly"));
    }

    public List<String> getClientRequestedProtocols() {
        if (this.result == ExtractorResult.COMPLETE || this.result == ExtractorResult.NOT_PRESENT) {
            return this.clientRequestedProtocols;
        }
        throw new IllegalStateException(sm.getString("sniExtractor.tooEarly"));
    }

    private static ExtractorResult handleIncompleteRead(ByteBuffer bb) {
        if (bb.limit() == bb.capacity()) {
            return ExtractorResult.UNDERFLOW;
        }
        return ExtractorResult.NEED_READ;
    }

    private static boolean isAvailable(ByteBuffer bb, int size) {
        if (bb.remaining() < size) {
            bb.position(bb.limit());
            return false;
        }
        return true;
    }

    private static boolean isTLSHandshake(ByteBuffer bb) {
        if (bb.get() != 22) {
            return false;
        }
        byte b2 = bb.get();
        byte b3 = bb.get();
        if (b2 < 3) {
            return false;
        }
        if (b2 == 3 && b3 == 0) {
            return false;
        }
        return true;
    }

    private static boolean isHttp(ByteBuffer bb) {
        bb.position(0);
        while (bb.hasRemaining()) {
            byte chr = bb.get();
            if (chr != 13 && chr != 10) {
                while (HttpParser.isToken(chr) && bb.hasRemaining()) {
                    chr = bb.get();
                    if (chr == 32 || chr == 9) {
                        while (true) {
                            if (chr == 32 || chr == 9) {
                                if (!bb.hasRemaining()) {
                                    return false;
                                }
                                chr = bb.get();
                            } else {
                                while (chr != 32 && chr != 9) {
                                    if (HttpParser.isNotRequestTarget(chr) || !bb.hasRemaining()) {
                                        return false;
                                    }
                                    chr = bb.get();
                                }
                                while (true) {
                                    if (chr == 32 || chr == 9) {
                                        if (!bb.hasRemaining()) {
                                            return false;
                                        }
                                        chr = bb.get();
                                    } else {
                                        while (HttpParser.isHttpProtocol(chr) && bb.hasRemaining()) {
                                            chr = bb.get();
                                            if (chr == 13 || chr == 10) {
                                                return true;
                                            }
                                        }
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
                return false;
            }
        }
        return false;
    }

    private static boolean isAllRecordAvailable(ByteBuffer bb) {
        int size = bb.getChar();
        return isAvailable(bb, size);
    }

    private static boolean isClientHello(ByteBuffer bb) {
        if (bb.get() == 1) {
            return true;
        }
        return false;
    }

    private static boolean isAllClientHelloAvailable(ByteBuffer bb) {
        int size = ((bb.get() & 255) << 16) + ((bb.get() & 255) << 8) + (bb.get() & 255);
        return isAvailable(bb, size);
    }

    private static void skipBytes(ByteBuffer bb, int size) {
        bb.position(bb.position() + size);
    }

    private static String readProtocol(ByteBuffer bb) {
        char protocol = bb.getChar();
        switch (protocol) {
            case 768:
                return "SSLv3";
            case 769:
                return Constants.SSL_PROTO_TLSv1_0;
            case 770:
                return "TLSv1.1";
            case 771:
                return "TLSv1.2";
            case 772:
                return Constants.SSL_PROTO_TLSv1_3;
            default:
                return "Unknown(0x" + HexUtils.toHexString(protocol) + ")";
        }
    }

    private static String readSniExtension(ByteBuffer bb) {
        skipBytes(bb, 3);
        byte[] serverNameBytes = new byte[bb.getChar()];
        bb.get(serverNameBytes);
        return new String(serverNameBytes, StandardCharsets.UTF_8).toLowerCase(Locale.ENGLISH);
    }

    private static void readAlpnExtension(ByteBuffer bb, List<String> protocolNames) {
        char toRead = bb.getChar();
        byte[] inputBuffer = new byte[Const.MAX_ARRAY_DIMENSIONS];
        while (toRead > 0) {
            int len = bb.get() & 255;
            bb.get(inputBuffer, 0, len);
            protocolNames.add(new String(inputBuffer, 0, len, StandardCharsets.UTF_8));
            toRead = (char) (((char) (toRead - 1)) - len);
        }
    }

    private static void readSupportedVersions(ByteBuffer bb, List<String> protocolNames) {
        int count = (bb.get() & 255) / 2;
        for (int i = 0; i < count; i++) {
            protocolNames.add(readProtocol(bb));
        }
    }
}
