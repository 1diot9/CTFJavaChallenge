package org.apache.coyote.http2;

import jakarta.servlet.ServletConnection;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.coyote.AbstractProcessor;
import org.apache.coyote.ActionCode;
import org.apache.coyote.Adapter;
import org.apache.coyote.ContinueResponseTiming;
import org.apache.coyote.ErrorState;
import org.apache.coyote.Request;
import org.apache.coyote.RequestGroupInfo;
import org.apache.coyote.Response;
import org.apache.coyote.http11.filters.GzipOutputFilter;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.DispatchType;
import org.apache.tomcat.util.net.SendfileState;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/StreamProcessor.class */
public class StreamProcessor extends AbstractProcessor {
    private static final Log log = LogFactory.getLog((Class<?>) StreamProcessor.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) StreamProcessor.class);
    private static final Set<String> H2_PSEUDO_HEADERS_REQUEST = new HashSet();
    private final Lock processLock;
    private final Http2UpgradeHandler handler;
    private final Stream stream;
    private SendfileData sendfileData;
    private SendfileState sendfileState;

    static {
        H2_PSEUDO_HEADERS_REQUEST.add(":method");
        H2_PSEUDO_HEADERS_REQUEST.add(":scheme");
        H2_PSEUDO_HEADERS_REQUEST.add(":authority");
        H2_PSEUDO_HEADERS_REQUEST.add(":path");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public StreamProcessor(Http2UpgradeHandler handler, Stream stream, Adapter adapter, SocketWrapperBase<?> socketWrapper) {
        super(adapter, stream.getCoyoteRequest(), stream.getCoyoteResponse());
        this.processLock = new ReentrantLock();
        this.sendfileData = null;
        this.sendfileState = null;
        this.handler = handler;
        this.stream = stream;
        setSocketWrapper(socketWrapper);
    }

    /*  JADX ERROR: Types fix failed
        java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryPossibleTypes(FixTypesVisitor.java:183)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:242)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:221)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:91)
        */
    /* JADX WARN: Failed to apply debug info
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.applyWithWiderIgnoreUnknown(TypeUpdate.java:74)
    	at jadx.core.dex.visitors.debuginfo.DebugInfoApplyVisitor.applyDebugInfo(DebugInfoApplyVisitor.java:137)
    	at jadx.core.dex.visitors.debuginfo.DebugInfoApplyVisitor.applyDebugInfo(DebugInfoApplyVisitor.java:133)
    	at jadx.core.dex.visitors.debuginfo.DebugInfoApplyVisitor.searchAndApplyVarDebugInfo(DebugInfoApplyVisitor.java:75)
    	at jadx.core.dex.visitors.debuginfo.DebugInfoApplyVisitor.lambda$applyDebugInfo$0(DebugInfoApplyVisitor.java:68)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.debuginfo.DebugInfoApplyVisitor.applyDebugInfo(DebugInfoApplyVisitor.java:68)
    	at jadx.core.dex.visitors.debuginfo.DebugInfoApplyVisitor.visit(DebugInfoApplyVisitor.java:55)
     */
    /* JADX WARN: Failed to calculate best type for var: r1v0 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.ifListener(TypeUpdate.java:633)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:188)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:145)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:123)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:101)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:101)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
     */
    /* JADX WARN: Finally extract failed */
    /* JADX WARN: Not initialized variable reg: 11, insn: 0x01ac: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = 
  (r11 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY] A[D('state' org.apache.tomcat.util.net.AbstractEndpoint$Handler$SocketState)])
 A[TRY_LEAVE], block:B:51:0x01ac */
    final void process(org.apache.tomcat.util.net.SocketEvent r10) {
        /*
            Method dump skipped, instructions count: 491
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.coyote.http2.StreamProcessor.process(org.apache.tomcat.util.net.SocketEvent):void");
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void prepareResponse() throws IOException {
        this.response.setCommitted(true);
        if (this.handler.hasAsyncIO() && this.handler.getProtocol().getUseSendfile()) {
            prepareSendfile();
        }
        prepareHeaders(this.request, this.response, this.sendfileData == null, this.handler.getProtocol(), this.stream);
        this.stream.writeHeaders();
    }

    private void prepareSendfile() {
        String fileName = (String) this.stream.getCoyoteRequest().getAttribute("org.apache.tomcat.sendfile.filename");
        if (fileName != null) {
            this.sendfileData = new SendfileData();
            this.sendfileData.path = new File(fileName).toPath();
            this.sendfileData.pos = ((Long) this.stream.getCoyoteRequest().getAttribute("org.apache.tomcat.sendfile.start")).longValue();
            this.sendfileData.end = ((Long) this.stream.getCoyoteRequest().getAttribute("org.apache.tomcat.sendfile.end")).longValue();
            this.sendfileData.left = this.sendfileData.end - this.sendfileData.pos;
            this.sendfileData.stream = this.stream;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void prepareHeaders(Request coyoteRequest, Response coyoteResponse, boolean noSendfile, Http2Protocol protocol, Stream stream) {
        MimeHeaders headers = coyoteResponse.getMimeHeaders();
        int statusCode = coyoteResponse.getStatus();
        headers.addValue(":status").setString(Integer.toString(statusCode));
        if (noSendfile && protocol != null && protocol.useCompression(coyoteRequest, coyoteResponse)) {
            stream.addOutputFilter(new GzipOutputFilter());
        }
        if (statusCode >= 200 && statusCode != 204 && statusCode != 205 && statusCode != 304) {
            String contentType = coyoteResponse.getContentType();
            if (contentType != null) {
                headers.setValue("content-type").setString(contentType);
            }
            String contentLanguage = coyoteResponse.getContentLanguage();
            if (contentLanguage != null) {
                headers.setValue("content-language").setString(contentLanguage);
            }
            long contentLength = coyoteResponse.getContentLengthLong();
            if (contentLength != -1 && headers.getValue("content-length") == null) {
                headers.addValue("content-length").setLong(contentLength);
            }
        } else {
            if (stream != null) {
                stream.configureVoidOutputFilter();
            }
            if (statusCode == 205) {
                coyoteResponse.setContentLength(0L);
            } else {
                coyoteResponse.setContentLength(-1L);
            }
        }
        if (statusCode >= 200 && headers.getValue("date") == null) {
            headers.addValue("date").setString(FastHttpDateFormat.getCurrentDate());
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void finishResponse() throws IOException {
        this.sendfileState = this.handler.processSendfile(this.sendfileData);
        if (this.sendfileState != SendfileState.PENDING) {
            this.stream.getOutputBuffer().end();
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void ack(ContinueResponseTiming continueResponseTiming) {
        if ((continueResponseTiming == ContinueResponseTiming.ALWAYS || continueResponseTiming == this.handler.getProtocol().getContinueResponseTimingInternal()) && !this.response.isCommitted() && this.request.hasExpectation()) {
            try {
                this.stream.writeAck();
            } catch (IOException ioe) {
                setErrorState(ErrorState.CLOSE_CONNECTION_NOW, ioe);
            }
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void flush() throws IOException {
        this.stream.getOutputBuffer().flush();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final int available(boolean doRead) {
        return this.stream.getInputBuffer().available();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void setRequestBody(ByteChunk body) {
        this.stream.getInputBuffer().insertReplayedBody(body);
        try {
            this.stream.receivedEndOfStream();
        } catch (ConnectionException e) {
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void setSwallowResponse() {
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void disableSwallowRequest() {
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected void processSocketEvent(SocketEvent event, boolean dispatch) {
        if (dispatch) {
            this.handler.processStreamOnContainerThread(this, event);
        } else {
            process(event);
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final boolean isReadyForRead() {
        return this.stream.getInputBuffer().isReadyForRead();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final boolean isRequestBodyFullyRead() {
        return this.stream.getInputBuffer().isRequestBodyFullyRead();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void registerReadInterest() {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final boolean isReadyForWrite() {
        return this.stream.isReadyForWrite();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void executeDispatches() {
        Iterator<DispatchType> dispatches = getIteratorAndClearDispatches();
        while (dispatches != null && dispatches.hasNext()) {
            DispatchType dispatchType = dispatches.next();
            processSocketEvent(dispatchType.getSocketStatus(), true);
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final boolean isPushSupported() {
        return this.stream.isPushSupported();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void doPush(Request pushTarget) {
        try {
            this.stream.push(pushTarget);
        } catch (IOException ioe) {
            setErrorState(ErrorState.CLOSE_CONNECTION_NOW, ioe);
            this.response.setErrorException(ioe);
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected boolean isTrailerFieldsReady() {
        return this.stream.isTrailerFieldsReady();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected boolean isTrailerFieldsSupported() {
        return this.stream.isTrailerFieldsSupported();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.coyote.AbstractProcessor
    public String getProtocolRequestId() {
        return this.stream.getIdAsString();
    }

    @Override // org.apache.coyote.AbstractProcessor, org.apache.coyote.Processor
    public final void recycle() {
        RequestGroupInfo global = this.handler.getProtocol().getGlobal();
        if (global != null) {
            global.removeRequestProcessor(this.request.getRequestProcessor());
        }
        setSocketWrapper(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.coyote.AbstractProcessorLight
    public final Log getLog() {
        return log;
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected ServletConnection getServletConnection() {
        return this.handler.getServletConnection();
    }

    @Override // org.apache.coyote.Processor
    public final void pause() {
    }

    @Override // org.apache.coyote.AbstractProcessorLight
    public final AbstractEndpoint.Handler.SocketState service(SocketWrapperBase<?> socket) throws IOException {
        try {
            if (validateRequest()) {
                this.adapter.service(this.request, this.response);
            } else {
                this.response.setStatus(400);
                this.adapter.log(this.request, this.response, 0L);
                setErrorState(ErrorState.CLOSE_CLEAN, null);
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("streamProcessor.service.error"), e);
            }
            this.response.setStatus(500);
            setErrorState(ErrorState.CLOSE_NOW, e);
        }
        if (this.sendfileState == SendfileState.PENDING) {
            return AbstractEndpoint.Handler.SocketState.SENDFILE;
        }
        if (getErrorState().isError()) {
            action(ActionCode.CLOSE, null);
            this.request.updateCounters();
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
        if (isAsync()) {
            return AbstractEndpoint.Handler.SocketState.LONG;
        }
        action(ActionCode.CLOSE, null);
        this.request.updateCounters();
        return AbstractEndpoint.Handler.SocketState.CLOSED;
    }

    private boolean validateRequest() {
        HttpParser httpParser = new HttpParser(this.handler.getProtocol().getHttp11Protocol().getRelaxedPathChars(), this.handler.getProtocol().getHttp11Protocol().getRelaxedQueryChars());
        String method = this.request.method().toString();
        if (!HttpParser.isToken(method)) {
            return false;
        }
        String scheme = this.request.scheme().toString();
        if (!HttpParser.isScheme(scheme)) {
            return false;
        }
        ByteChunk bc = this.request.requestURI().getByteChunk();
        for (int i = bc.getStart(); i < bc.getEnd(); i++) {
            if (httpParser.isNotRequestTargetRelaxed(bc.getBuffer()[i])) {
                return false;
            }
        }
        String qs = this.request.queryString().toString();
        if (qs != null) {
            for (char c : qs.toCharArray()) {
                if (!httpParser.isQueryRelaxed(c)) {
                    return false;
                }
            }
        }
        MimeHeaders headers = this.request.getMimeHeaders();
        Enumeration<String> names = headers.names();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (!H2_PSEUDO_HEADERS_REQUEST.contains(name) && !HttpParser.isToken(name)) {
                return false;
            }
        }
        return true;
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final boolean flushBufferedWrite() throws IOException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("streamProcessor.flushBufferedWrite.entry", this.stream.getConnectionId(), this.stream.getIdAsString()));
        }
        if (this.stream.flush(false)) {
            if (this.stream.isReadyForWrite()) {
                throw new IllegalStateException();
            }
            return true;
        }
        return false;
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final AbstractEndpoint.Handler.SocketState dispatchEndRequest() throws IOException {
        return AbstractEndpoint.Handler.SocketState.CLOSED;
    }
}
