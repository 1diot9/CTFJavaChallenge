package org.apache.coyote.ajp;

import jakarta.servlet.ServletConnection;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.catalina.valves.LoadBalancerDrainingValve;
import org.apache.coyote.AbstractProcessor;
import org.apache.coyote.ActionCode;
import org.apache.coyote.Adapter;
import org.apache.coyote.ContinueResponseTiming;
import org.apache.coyote.ErrorState;
import org.apache.coyote.InputBuffer;
import org.apache.coyote.OutputBuffer;
import org.apache.coyote.RequestInfo;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.WebContentGenerator;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/ajp/AjpProcessor.class */
public class AjpProcessor extends AbstractProcessor {
    private static final Log log = LogFactory.getLog((Class<?>) AjpProcessor.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) AjpProcessor.class);
    private static final byte[] endMessageArray;
    private static final byte[] endAndCloseMessageArray;
    private static final byte[] flushMessageArray;
    private static final byte[] pongMessageArray;
    private static final Map<String, String> jakartaAttributeMapping;
    private static final Set<String> iisTlsAttributes;
    private final AbstractAjpProtocol<?> protocol;
    private final byte[] getBodyMessageArray;
    private final int outputMaxChunkSize;
    private final AjpMessage requestHeaderMessage;
    private final AjpMessage responseMessage;
    private int responseMsgPos;
    private final AjpMessage bodyMessage;
    private final MessageBytes bodyBytes;
    private final MessageBytes tmpMB;
    private final MessageBytes certificates;
    private boolean endOfStream;
    private boolean empty;
    private boolean first;
    private boolean waitingForBodyMessage;
    private boolean replay;
    private boolean swallowResponse;
    private boolean responseFinished;
    private long bytesWritten;

    static {
        AjpMessage endMessage = new AjpMessage(16);
        endMessage.reset();
        endMessage.appendByte(5);
        endMessage.appendByte(1);
        endMessage.end();
        endMessageArray = new byte[endMessage.getLen()];
        System.arraycopy(endMessage.getBuffer(), 0, endMessageArray, 0, endMessage.getLen());
        AjpMessage endAndCloseMessage = new AjpMessage(16);
        endAndCloseMessage.reset();
        endAndCloseMessage.appendByte(5);
        endAndCloseMessage.appendByte(0);
        endAndCloseMessage.end();
        endAndCloseMessageArray = new byte[endAndCloseMessage.getLen()];
        System.arraycopy(endAndCloseMessage.getBuffer(), 0, endAndCloseMessageArray, 0, endAndCloseMessage.getLen());
        AjpMessage flushMessage = new AjpMessage(16);
        flushMessage.reset();
        flushMessage.appendByte(3);
        flushMessage.appendInt(0);
        flushMessage.appendByte(0);
        flushMessage.end();
        flushMessageArray = new byte[flushMessage.getLen()];
        System.arraycopy(flushMessage.getBuffer(), 0, flushMessageArray, 0, flushMessage.getLen());
        AjpMessage pongMessage = new AjpMessage(16);
        pongMessage.reset();
        pongMessage.appendByte(9);
        pongMessage.end();
        pongMessageArray = new byte[pongMessage.getLen()];
        System.arraycopy(pongMessage.getBuffer(), 0, pongMessageArray, 0, pongMessage.getLen());
        Map<String, String> m = new HashMap<>();
        m.put("jakarta.servlet.request.cipher_suite", "jakarta.servlet.request.cipher_suite");
        m.put("jakarta.servlet.request.key_size", "jakarta.servlet.request.key_size");
        m.put("jakarta.servlet.request.ssl_session", "jakarta.servlet.request.ssl_session");
        m.put("jakarta.servlet.request.X509Certificate", "jakarta.servlet.request.X509Certificate");
        m.put("javax.servlet.request.cipher_suite", "jakarta.servlet.request.cipher_suite");
        m.put("javax.servlet.request.key_size", "jakarta.servlet.request.key_size");
        m.put("javax.servlet.request.ssl_session", "jakarta.servlet.request.ssl_session");
        m.put("javax.servlet.request.X509Certificate", "jakarta.servlet.request.X509Certificate");
        jakartaAttributeMapping = Collections.unmodifiableMap(m);
        Set<String> s = new HashSet<>();
        s.add("CERT_ISSUER");
        s.add("CERT_SUBJECT");
        s.add("CERT_COOKIE");
        s.add("HTTPS_SERVER_SUBJECT");
        s.add("CERT_FLAGS");
        s.add("HTTPS_SECRETKEYSIZE");
        s.add("CERT_SERIALNUMBER");
        s.add("HTTPS_SERVER_ISSUER");
        s.add("HTTPS_KEYSIZE");
        iisTlsAttributes = Collections.unmodifiableSet(s);
    }

    public AjpProcessor(AbstractAjpProtocol<?> protocol, Adapter adapter) {
        super(adapter);
        this.responseMsgPos = -1;
        this.bodyBytes = MessageBytes.newInstance();
        this.tmpMB = MessageBytes.newInstance();
        this.certificates = MessageBytes.newInstance();
        this.endOfStream = false;
        this.empty = true;
        this.first = true;
        this.waitingForBodyMessage = false;
        this.replay = false;
        this.swallowResponse = false;
        this.responseFinished = false;
        this.bytesWritten = 0L;
        this.protocol = protocol;
        int packetSize = protocol.getPacketSize();
        this.outputMaxChunkSize = packetSize - 8;
        this.request.setInputBuffer(new SocketInputBuffer());
        this.requestHeaderMessage = new AjpMessage(packetSize);
        this.responseMessage = new AjpMessage(packetSize);
        this.bodyMessage = new AjpMessage(packetSize);
        AjpMessage getBodyMessage = new AjpMessage(16);
        getBodyMessage.reset();
        getBodyMessage.appendByte(6);
        getBodyMessage.appendInt((Constants.MAX_READ_SIZE + packetSize) - 8192);
        getBodyMessage.end();
        this.getBodyMessageArray = new byte[getBodyMessage.getLen()];
        System.arraycopy(getBodyMessage.getBuffer(), 0, this.getBodyMessageArray, 0, getBodyMessage.getLen());
        this.response.setOutputBuffer(new SocketOutputBuffer());
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected boolean flushBufferedWrite() throws IOException {
        if (hasDataToWrite()) {
            this.socketWrapper.flush(false);
            if (hasDataToWrite()) {
                this.response.checkRegisterForWrite();
                return true;
            }
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.coyote.AbstractProcessor
    public void dispatchNonBlockingRead() {
        if (available(true) > 0) {
            super.dispatchNonBlockingRead();
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected AbstractEndpoint.Handler.SocketState dispatchEndRequest() {
        this.socketWrapper.setReadTimeout(this.protocol.getKeepAliveTimeout());
        recycle();
        if (this.protocol.isPaused()) {
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
        return AbstractEndpoint.Handler.SocketState.OPEN;
    }

    @Override // org.apache.coyote.AbstractProcessorLight
    public AbstractEndpoint.Handler.SocketState service(SocketWrapperBase<?> socket) throws IOException {
        RequestInfo rp = this.request.getRequestProcessor();
        rp.setStage(1);
        this.socketWrapper = socket;
        boolean cping = false;
        boolean firstRead = true;
        while (true) {
            if (!getErrorState().isError() && !this.protocol.isPaused()) {
                try {
                } catch (IOException e) {
                    setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e);
                } catch (Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                    getLog().debug(sm.getString("ajpprocessor.header.error"), t);
                    this.response.setStatus(400);
                    setErrorState(ErrorState.CLOSE_CLEAN, t);
                }
                if (!readMessage(this.requestHeaderMessage, firstRead)) {
                    break;
                }
                firstRead = false;
                this.socketWrapper.setReadTimeout(this.protocol.getConnectionTimeout());
                int type = this.requestHeaderMessage.getByte();
                if (type == 10) {
                    if (this.protocol.isPaused()) {
                        recycle();
                        break;
                    }
                    cping = true;
                    try {
                        this.socketWrapper.write(true, pongMessageArray, 0, pongMessageArray.length);
                        this.socketWrapper.flush(true);
                    } catch (IOException e2) {
                        if (getLog().isDebugEnabled()) {
                            getLog().debug("Pong message failed", e2);
                        }
                        setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e2);
                    }
                    recycle();
                } else if (type != 2) {
                    if (getLog().isDebugEnabled()) {
                        getLog().debug("Unexpected message: " + type);
                    }
                    setErrorState(ErrorState.CLOSE_CONNECTION_NOW, null);
                } else {
                    this.request.setStartTimeNanos(System.nanoTime());
                    if (getErrorState().isIoAllowed()) {
                        rp.setStage(2);
                        try {
                            prepareRequest();
                        } catch (Throwable t2) {
                            ExceptionUtils.handleThrowable(t2);
                            getLog().debug(sm.getString("ajpprocessor.request.prepare"), t2);
                            this.response.setStatus(500);
                            setErrorState(ErrorState.CLOSE_CLEAN, t2);
                        }
                    }
                    if (getErrorState().isIoAllowed() && !cping && this.protocol.isPaused()) {
                        this.response.setStatus(503);
                        setErrorState(ErrorState.CLOSE_CLEAN, null);
                    }
                    cping = false;
                    if (getErrorState().isIoAllowed()) {
                        try {
                            rp.setStage(3);
                            getAdapter().service(this.request, this.response);
                        } catch (InterruptedIOException e3) {
                            setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e3);
                        } catch (Throwable t3) {
                            ExceptionUtils.handleThrowable(t3);
                            getLog().error(sm.getString("ajpprocessor.request.process"), t3);
                            this.response.setStatus(500);
                            setErrorState(ErrorState.CLOSE_CLEAN, t3);
                            getAdapter().log(this.request, this.response, 0L);
                        }
                    }
                    if (isAsync() && !getErrorState().isError()) {
                        break;
                    }
                    if (!this.responseFinished && getErrorState().isIoAllowed()) {
                        try {
                            action(ActionCode.COMMIT, null);
                            finishResponse();
                        } catch (IOException ioe) {
                            setErrorState(ErrorState.CLOSE_CONNECTION_NOW, ioe);
                        } catch (Throwable t4) {
                            ExceptionUtils.handleThrowable(t4);
                            setErrorState(ErrorState.CLOSE_NOW, t4);
                        }
                    }
                    if (getErrorState().isError()) {
                        this.response.setStatus(500);
                    }
                    this.request.updateCounters();
                    rp.setStage(6);
                    this.socketWrapper.setReadTimeout(this.protocol.getKeepAliveTimeout());
                    recycle();
                }
            } else {
                break;
            }
        }
        rp.setStage(7);
        if (getErrorState().isError() || this.protocol.isPaused()) {
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
        if (isAsync()) {
            return AbstractEndpoint.Handler.SocketState.LONG;
        }
        return AbstractEndpoint.Handler.SocketState.OPEN;
    }

    @Override // org.apache.coyote.AbstractProcessor, org.apache.coyote.Processor
    public void recycle() {
        getAdapter().checkRecycled(this.request, this.response);
        super.recycle();
        this.request.recycle();
        this.response.recycle();
        this.first = true;
        this.endOfStream = false;
        this.waitingForBodyMessage = false;
        this.empty = true;
        this.replay = false;
        this.responseFinished = false;
        this.certificates.recycle();
        this.swallowResponse = false;
        this.bytesWritten = 0L;
    }

    @Override // org.apache.coyote.Processor
    public void pause() {
    }

    private boolean receive(boolean block) throws IOException {
        this.bodyMessage.reset();
        if (!readMessage(this.bodyMessage, block)) {
            return false;
        }
        this.waitingForBodyMessage = false;
        if (this.bodyMessage.getLen() == 0) {
            return false;
        }
        int blen = this.bodyMessage.peekInt();
        if (blen == 0) {
            return false;
        }
        this.bodyMessage.getBodyBytes(this.bodyBytes);
        this.empty = false;
        return true;
    }

    private boolean readMessage(AjpMessage message, boolean block) throws IOException {
        byte[] buf = message.getBuffer();
        if (!read(buf, 0, 4, block)) {
            return false;
        }
        int messageLength = message.processHeader(true);
        if (messageLength < 0) {
            throw new IOException(sm.getString("ajpmessage.invalidLength", Integer.valueOf(messageLength)));
        }
        if (messageLength == 0) {
            return true;
        }
        if (messageLength > message.getBuffer().length) {
            String msg = sm.getString("ajpprocessor.header.tooLong", Integer.valueOf(messageLength), Integer.valueOf(buf.length));
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        read(buf, 4, messageLength, true);
        return true;
    }

    protected boolean refillReadBuffer(boolean block) throws IOException {
        if (this.replay) {
            this.endOfStream = true;
        }
        if (this.endOfStream) {
            return false;
        }
        if (this.first) {
            this.first = false;
            long contentLength = this.request.getContentLengthLong();
            if (contentLength > 0) {
                this.waitingForBodyMessage = true;
            } else if (contentLength == 0) {
                this.endOfStream = true;
                return false;
            }
        }
        if (!this.waitingForBodyMessage) {
            this.socketWrapper.write(true, this.getBodyMessageArray, 0, this.getBodyMessageArray.length);
            this.socketWrapper.flush(true);
            this.waitingForBodyMessage = true;
        }
        boolean moreData = receive(block);
        if (!moreData && !this.waitingForBodyMessage) {
            this.endOfStream = true;
        }
        return moreData;
    }

    private void prepareRequest() {
        MessageBytes vMB;
        byte methodCode = this.requestHeaderMessage.getByte();
        if (methodCode != -1) {
            String methodName = Constants.getMethodForCode(methodCode - 1);
            this.request.method().setString(methodName);
        }
        this.requestHeaderMessage.getBytes(this.request.protocol());
        this.requestHeaderMessage.getBytes(this.request.requestURI());
        this.requestHeaderMessage.getBytes(this.request.remoteAddr());
        this.requestHeaderMessage.getBytes(this.request.remoteHost());
        this.requestHeaderMessage.getBytes(this.request.localName());
        this.request.setLocalPort(this.requestHeaderMessage.getInt());
        if (this.socketWrapper != null) {
            this.request.peerAddr().setString(this.socketWrapper.getRemoteAddr());
        }
        boolean isSSL = this.requestHeaderMessage.getByte() != 0;
        if (isSSL) {
            this.request.scheme().setString("https");
        }
        MimeHeaders headers = this.request.getMimeHeaders();
        headers.setLimit(this.protocol.getMaxHeaderCount());
        boolean contentLengthSet = false;
        int hCount = this.requestHeaderMessage.getInt();
        for (int i = 0; i < hCount; i++) {
            int isc = this.requestHeaderMessage.peekInt();
            int hId = isc & 255;
            if (40960 == (isc & 65280)) {
                this.requestHeaderMessage.getInt();
                String hName = Constants.getHeaderForCode(hId - 1);
                vMB = headers.addValue(hName);
            } else {
                hId = -1;
                this.requestHeaderMessage.getBytes(this.tmpMB);
                ByteChunk bc = this.tmpMB.getByteChunk();
                vMB = headers.addValue(bc.getBuffer(), bc.getStart(), bc.getLength());
            }
            this.requestHeaderMessage.getBytes(vMB);
            if (hId == 8 || (hId == -1 && this.tmpMB.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH))) {
                long cl = vMB.getLong();
                if (contentLengthSet) {
                    this.response.setStatus(400);
                    setErrorState(ErrorState.CLOSE_CLEAN, null);
                } else {
                    contentLengthSet = true;
                    this.request.setContentLength(cl);
                }
            } else if (hId == 7 || (hId == -1 && this.tmpMB.equalsIgnoreCase(HttpHeaders.CONTENT_TYPE))) {
                ByteChunk bchunk = vMB.getByteChunk();
                this.request.contentType().setBytes(bchunk.getBytes(), bchunk.getOffset(), bchunk.getLength());
            }
        }
        String secret = this.protocol.getSecret();
        boolean secretPresentInRequest = false;
        while (true) {
            byte attributeCode = this.requestHeaderMessage.getByte();
            if (attributeCode != -1) {
                switch (attributeCode) {
                    case 1:
                        this.requestHeaderMessage.getBytes(this.tmpMB);
                        break;
                    case 2:
                        this.requestHeaderMessage.getBytes(this.tmpMB);
                        break;
                    case 3:
                        boolean tomcatAuthorization = this.protocol.getTomcatAuthorization();
                        if (tomcatAuthorization || !this.protocol.getTomcatAuthentication()) {
                            this.requestHeaderMessage.getBytes(this.request.getRemoteUser());
                            this.request.setRemoteUserNeedsAuthorization(tomcatAuthorization);
                            break;
                        } else {
                            this.requestHeaderMessage.getBytes(this.tmpMB);
                            break;
                        }
                    case 4:
                        if (this.protocol.getTomcatAuthorization() || !this.protocol.getTomcatAuthentication()) {
                            this.requestHeaderMessage.getBytes(this.request.getAuthType());
                            break;
                        } else {
                            this.requestHeaderMessage.getBytes(this.tmpMB);
                            break;
                        }
                    case 5:
                        this.requestHeaderMessage.getBytes(this.request.queryString());
                        break;
                    case 6:
                        this.requestHeaderMessage.getBytes(this.tmpMB);
                        break;
                    case 7:
                        this.requestHeaderMessage.getBytes(this.certificates);
                        break;
                    case 8:
                        this.requestHeaderMessage.getBytes(this.tmpMB);
                        this.request.setAttribute("jakarta.servlet.request.cipher_suite", this.tmpMB.toString());
                        break;
                    case 9:
                        this.requestHeaderMessage.getBytes(this.tmpMB);
                        this.request.setAttribute("jakarta.servlet.request.ssl_session_id", this.tmpMB.toString());
                        break;
                    case 10:
                        this.requestHeaderMessage.getBytes(this.tmpMB);
                        String n = this.tmpMB.toString();
                        this.requestHeaderMessage.getBytes(this.tmpMB);
                        String v = this.tmpMB.toString();
                        if (n.equals(Constants.SC_A_REQ_LOCAL_ADDR)) {
                            this.request.localAddr().setString(v);
                            break;
                        } else if (n.equals(Constants.SC_A_REQ_REMOTE_PORT)) {
                            try {
                                this.request.setRemotePort(Integer.parseInt(v));
                                break;
                            } catch (NumberFormatException e) {
                                break;
                            }
                        } else if (n.equals(Constants.SC_A_SSL_PROTOCOL)) {
                            this.request.setAttribute(SSLSupport.PROTOCOL_VERSION_KEY, v);
                            break;
                        } else if (n.equals(LoadBalancerDrainingValve.ATTRIBUTE_KEY_JK_LB_ACTIVATION)) {
                            this.request.setAttribute(n, v);
                            break;
                        } else if (jakartaAttributeMapping.containsKey(n)) {
                            this.request.setAttribute(jakartaAttributeMapping.get(n), v);
                            break;
                        } else if (iisTlsAttributes.contains(n)) {
                            this.request.setAttribute(n, v);
                            break;
                        } else {
                            Pattern pattern = this.protocol.getAllowedRequestAttributesPatternInternal();
                            if (pattern != null && pattern.matcher(n).matches()) {
                                this.request.setAttribute(n, v);
                                break;
                            } else {
                                log.warn(sm.getString("ajpprocessor.unknownAttribute", n));
                                this.response.setStatus(403);
                                setErrorState(ErrorState.CLOSE_CLEAN, null);
                                break;
                            }
                        }
                        break;
                    case 11:
                        this.request.setAttribute("jakarta.servlet.request.key_size", Integer.valueOf(this.requestHeaderMessage.getInt()));
                        break;
                    case 12:
                        this.requestHeaderMessage.getBytes(this.tmpMB);
                        if (secret != null && secret.length() > 0) {
                            secretPresentInRequest = true;
                            if (this.tmpMB.equals(secret)) {
                                break;
                            } else {
                                this.response.setStatus(403);
                                setErrorState(ErrorState.CLOSE_CLEAN, null);
                                break;
                            }
                        }
                        break;
                    case 13:
                        this.requestHeaderMessage.getBytes(this.request.method());
                        break;
                }
            } else {
                if (secret != null && secret.length() > 0 && !secretPresentInRequest) {
                    this.response.setStatus(403);
                    setErrorState(ErrorState.CLOSE_CLEAN, null);
                }
                ByteChunk uriBC = this.request.requestURI().getByteChunk();
                if (uriBC.startsWithIgnoreCase("http", 0)) {
                    int pos = uriBC.indexOf("://", 0, 3, 4);
                    int uriBCStart = uriBC.getStart();
                    if (pos != -1) {
                        byte[] uriB = uriBC.getBytes();
                        int slashPos = uriBC.indexOf('/', pos + 3);
                        if (slashPos == -1) {
                            slashPos = uriBC.getLength();
                            this.request.requestURI().setBytes(uriB, uriBCStart + pos + 1, 1);
                        } else {
                            this.request.requestURI().setBytes(uriB, uriBCStart + slashPos, uriBC.getLength() - slashPos);
                        }
                        MessageBytes hostMB = headers.setValue("host");
                        hostMB.setBytes(uriB, uriBCStart + pos + 3, (slashPos - pos) - 3);
                    }
                }
                MessageBytes valueMB = this.request.getMimeHeaders().getValue("host");
                parseHost(valueMB);
                if (!getErrorState().isIoAllowed()) {
                    getAdapter().log(this.request, this.response, 0L);
                    return;
                }
                return;
            }
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected void populateHost() {
        try {
            this.request.serverName().duplicate(this.request.localName());
        } catch (IOException e) {
            this.response.setStatus(400);
            setErrorState(ErrorState.CLOSE_CLEAN, e);
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected void populatePort() {
        this.request.setServerPort(this.request.getLocalPort());
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void prepareResponse() throws IOException {
        this.response.setCommitted(true);
        int statusCode = this.response.getStatus();
        if (statusCode < 200 || statusCode == 204 || statusCode == 205 || statusCode == 304 || this.request.method().equals(WebContentGenerator.METHOD_HEAD)) {
            this.swallowResponse = true;
        }
        MimeHeaders headers = this.response.getMimeHeaders();
        String contentType = this.response.getContentType();
        if (contentType != null) {
            headers.setValue(HttpHeaders.CONTENT_TYPE).setString(contentType);
        }
        String contentLanguage = this.response.getContentLanguage();
        if (contentLanguage != null) {
            headers.setValue(HttpHeaders.CONTENT_LANGUAGE).setString(contentLanguage);
        }
        long contentLength = this.response.getContentLengthLong();
        if (contentLength >= 0) {
            headers.setValue(HttpHeaders.CONTENT_LENGTH).setLong(contentLength);
        }
        this.tmpMB.recycle();
        this.responseMsgPos = -1;
        int numHeaders = headers.size();
        boolean needAjpMessageHeader = true;
        while (needAjpMessageHeader) {
            this.responseMessage.reset();
            this.responseMessage.appendByte(4);
            this.responseMessage.appendInt(statusCode);
            this.tmpMB.setString(Integer.toString(this.response.getStatus()));
            this.responseMessage.appendBytes(this.tmpMB);
            this.responseMessage.appendInt(numHeaders);
            needAjpMessageHeader = false;
            for (int i = 0; i < numHeaders; i++) {
                try {
                    MessageBytes hN = headers.getName(i);
                    int hC = Constants.getResponseAjpIndex(hN.toString());
                    if (hC > 0) {
                        this.responseMessage.appendInt(hC);
                    } else {
                        this.responseMessage.appendBytes(hN);
                    }
                    MessageBytes hV = headers.getValue(i);
                    this.responseMessage.appendBytes(hV);
                } catch (IllegalArgumentException iae) {
                    log.warn(sm.getString("ajpprocessor.response.invalidHeader", headers.getName(i), headers.getValue(i)), iae);
                    headers.removeHeader(i);
                    numHeaders--;
                    needAjpMessageHeader = true;
                }
            }
        }
        this.responseMessage.end();
        this.socketWrapper.write(true, this.responseMessage.getBuffer(), 0, this.responseMessage.getLen());
        this.socketWrapper.flush(true);
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void flush() throws IOException {
        if (!this.responseFinished) {
            if (this.protocol.getAjpFlush()) {
                this.socketWrapper.write(true, flushMessageArray, 0, flushMessageArray.length);
            }
            this.socketWrapper.flush(true);
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void finishResponse() throws IOException {
        if (this.responseFinished) {
            return;
        }
        this.responseFinished = true;
        if (this.waitingForBodyMessage || (this.first && this.request.getContentLengthLong() > 0)) {
            refillReadBuffer(true);
        }
        if (getErrorState().isError()) {
            this.socketWrapper.write(true, endAndCloseMessageArray, 0, endAndCloseMessageArray.length);
        } else {
            this.socketWrapper.write(true, endMessageArray, 0, endMessageArray.length);
        }
        this.socketWrapper.flush(true);
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void ack(ContinueResponseTiming continueResponseTiming) {
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final int available(boolean doRead) {
        if (this.endOfStream) {
            return 0;
        }
        if (this.empty && doRead) {
            try {
                refillReadBuffer(false);
            } catch (IOException e) {
                return 1;
            }
        }
        if (this.empty) {
            return 0;
        }
        return this.request.getInputBuffer().available();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void setRequestBody(ByteChunk body) {
        int length = body.getLength();
        this.bodyBytes.setBytes(body.getBytes(), body.getStart(), length);
        this.request.setContentLength(length);
        this.first = false;
        this.empty = false;
        this.replay = true;
        this.endOfStream = false;
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void setSwallowResponse() {
        this.swallowResponse = true;
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void disableSwallowRequest() {
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final boolean getPopulateRequestAttributesFromSocket() {
        return false;
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void populateRequestAttributeRemoteHost() {
        if (this.request.remoteHost().isNull()) {
            try {
                this.request.remoteHost().setString(InetAddress.getByName(this.request.remoteAddr().toString()).getHostName());
            } catch (IOException e) {
            }
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void populateSslRequestAttributes() {
        CertificateFactory cf;
        if (!this.certificates.isNull()) {
            List<X509Certificate> jsseCerts = new ArrayList<>();
            ByteChunk certData = this.certificates.getByteChunk();
            ByteArrayInputStream bais = new ByteArrayInputStream(certData.getBytes(), certData.getStart(), certData.getLength());
            try {
                String clientCertProvider = this.protocol.getClientCertProvider();
                if (clientCertProvider == null) {
                    cf = CertificateFactory.getInstance("X.509");
                } else {
                    cf = CertificateFactory.getInstance("X.509", clientCertProvider);
                }
                while (bais.available() > 0) {
                    X509Certificate cert = (X509Certificate) cf.generateCertificate(bais);
                    jsseCerts.add(cert);
                }
                this.request.setAttribute("jakarta.servlet.request.X509Certificate", jsseCerts.toArray(new X509Certificate[0]));
            } catch (NoSuchProviderException | CertificateException e) {
                getLog().error(sm.getString("ajpprocessor.certs.fail"), e);
            }
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final boolean isRequestBodyFullyRead() {
        return this.endOfStream;
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void registerReadInterest() {
        this.socketWrapper.registerReadInterest();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final boolean isReadyForWrite() {
        return this.responseMsgPos == -1 && this.socketWrapper.isReadyForWrite();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected boolean isTrailerFieldsReady() {
        return true;
    }

    private boolean read(byte[] buf, int pos, int n, boolean block) throws IOException {
        int read = this.socketWrapper.read(block, buf, pos, n);
        if (read > 0 && read < n) {
            int left = n - read;
            int i = pos;
            while (true) {
                int start = i + read;
                if (left <= 0) {
                    break;
                }
                read = this.socketWrapper.read(true, buf, start, left);
                if (read == -1) {
                    throw new EOFException();
                }
                left -= read;
                i = start;
            }
        } else if (read == -1) {
            throw new EOFException();
        }
        return read > 0;
    }

    private void writeData(ByteBuffer chunk) throws IOException {
        boolean blocking = this.response.getWriteListener() == null;
        int len = chunk.remaining();
        int i = 0;
        while (true) {
            int off = i;
            if (len > 0) {
                int thisTime = Math.min(len, this.outputMaxChunkSize);
                this.responseMessage.reset();
                this.responseMessage.appendByte(3);
                chunk.limit(chunk.position() + thisTime);
                this.responseMessage.appendBytes(chunk);
                this.responseMessage.end();
                this.socketWrapper.write(blocking, this.responseMessage.getBuffer(), 0, this.responseMessage.getLen());
                this.socketWrapper.flush(blocking);
                len -= thisTime;
                i = off + thisTime;
            } else {
                this.bytesWritten += off;
                return;
            }
        }
    }

    private boolean hasDataToWrite() {
        return this.responseMsgPos != -1 || this.socketWrapper.hasDataToWrite();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.coyote.AbstractProcessorLight
    public Log getLog() {
        return log;
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected ServletConnection getServletConnection() {
        return this.socketWrapper.getServletConnection("ajp", "");
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/ajp/AjpProcessor$SocketInputBuffer.class */
    protected class SocketInputBuffer implements InputBuffer {
        protected SocketInputBuffer() {
        }

        @Override // org.apache.coyote.InputBuffer
        public int doRead(ApplicationBufferHandler handler) throws IOException {
            if (AjpProcessor.this.endOfStream) {
                return -1;
            }
            if (AjpProcessor.this.empty && !AjpProcessor.this.refillReadBuffer(true)) {
                return -1;
            }
            ByteChunk bc = AjpProcessor.this.bodyBytes.getByteChunk();
            handler.setByteBuffer(ByteBuffer.wrap(bc.getBuffer(), bc.getStart(), bc.getLength()));
            AjpProcessor.this.empty = true;
            return handler.getByteBuffer().remaining();
        }

        @Override // org.apache.coyote.InputBuffer
        public int available() {
            if (AjpProcessor.this.empty) {
                return 0;
            }
            return AjpProcessor.this.bodyBytes.getByteChunk().getLength();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/ajp/AjpProcessor$SocketOutputBuffer.class */
    protected class SocketOutputBuffer implements OutputBuffer {
        protected SocketOutputBuffer() {
        }

        @Override // org.apache.coyote.OutputBuffer
        public int doWrite(ByteBuffer chunk) throws IOException {
            if (!AjpProcessor.this.response.isCommitted()) {
                try {
                    AjpProcessor.this.prepareResponse();
                } catch (IOException e) {
                    AjpProcessor.this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e);
                }
            }
            int len = 0;
            if (!AjpProcessor.this.swallowResponse) {
                try {
                    int len2 = chunk.remaining();
                    AjpProcessor.this.writeData(chunk);
                    len = len2 - chunk.remaining();
                } catch (IOException ioe) {
                    AjpProcessor.this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, ioe);
                    throw ioe;
                }
            }
            return len;
        }

        @Override // org.apache.coyote.OutputBuffer
        public long getBytesWritten() {
            return AjpProcessor.this.bytesWritten;
        }
    }
}
