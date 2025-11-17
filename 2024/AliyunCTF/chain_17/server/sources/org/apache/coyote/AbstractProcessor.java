package org.apache.coyote;

import jakarta.servlet.ServletConnection;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import org.apache.el.parser.ELParserConstants;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.parser.Host;
import org.apache.tomcat.util.log.UserDataHelper;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.DispatchType;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/AbstractProcessor.class */
public abstract class AbstractProcessor extends AbstractProcessorLight implements ActionHook {
    private static final StringManager sm = StringManager.getManager((Class<?>) AbstractProcessor.class);
    private char[] hostNameC;
    protected final Adapter adapter;
    protected final AsyncStateMachine asyncStateMachine;
    private volatile long asyncTimeout;
    private volatile long asyncTimeoutGeneration;
    protected final Request request;
    protected final Response response;
    protected volatile SocketWrapperBase<?> socketWrapper;
    protected volatile SSLSupport sslSupport;
    private ErrorState errorState;
    protected final UserDataHelper userDataHelper;

    protected abstract void prepareResponse() throws IOException;

    protected abstract void finishResponse() throws IOException;

    protected abstract void ack(ContinueResponseTiming continueResponseTiming);

    protected abstract void flush() throws IOException;

    protected abstract int available(boolean z);

    protected abstract void setRequestBody(ByteChunk byteChunk);

    protected abstract void setSwallowResponse();

    protected abstract void disableSwallowRequest();

    protected abstract boolean isRequestBodyFullyRead();

    protected abstract void registerReadInterest();

    protected abstract boolean isReadyForWrite();

    protected abstract boolean isTrailerFieldsReady();

    protected abstract ServletConnection getServletConnection();

    protected abstract boolean flushBufferedWrite() throws IOException;

    protected abstract AbstractEndpoint.Handler.SocketState dispatchEndRequest() throws IOException;

    public AbstractProcessor(Adapter adapter) {
        this(adapter, new Request(), new Response());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractProcessor(Adapter adapter, Request coyoteRequest, Response coyoteResponse) {
        this.hostNameC = new char[0];
        this.asyncTimeout = -1L;
        this.asyncTimeoutGeneration = 0L;
        this.socketWrapper = null;
        this.errorState = ErrorState.NONE;
        this.adapter = adapter;
        this.asyncStateMachine = new AsyncStateMachine(this);
        this.request = coyoteRequest;
        this.response = coyoteResponse;
        this.response.setHook(this);
        this.request.setResponse(this.response);
        this.request.setHook(this);
        this.userDataHelper = new UserDataHelper(getLog());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setErrorState(ErrorState errorState, Throwable t) {
        if (getLog().isDebugEnabled()) {
            getLog().debug(sm.getString("abstractProcessor.setErrorState", errorState), t);
        }
        this.response.setError();
        boolean blockIo = this.errorState.isIoAllowed() && !errorState.isIoAllowed();
        this.errorState = this.errorState.getMostSevere(errorState);
        if (this.response.getStatus() < 400 && !(t instanceof IOException)) {
            this.response.setStatus(500);
        }
        if (t != null) {
            this.request.setAttribute("jakarta.servlet.error.exception", t);
        }
        if (blockIo && isAsync() && this.asyncStateMachine.asyncError()) {
            processSocketEvent(SocketEvent.ERROR, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ErrorState getErrorState() {
        return this.errorState;
    }

    @Override // org.apache.coyote.Processor
    public Request getRequest() {
        return this.request;
    }

    public Adapter getAdapter() {
        return this.adapter;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setSocketWrapper(SocketWrapperBase<?> socketWrapper) {
        this.socketWrapper = socketWrapper;
    }

    protected final SocketWrapperBase<?> getSocketWrapper() {
        return this.socketWrapper;
    }

    @Override // org.apache.coyote.Processor
    public final void setSslSupport(SSLSupport sslSupport) {
        this.sslSupport = sslSupport;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void execute(Runnable runnable) {
        SocketWrapperBase<?> socketWrapper = this.socketWrapper;
        if (socketWrapper == null) {
            throw new RejectedExecutionException(sm.getString("abstractProcessor.noExecute"));
        }
        socketWrapper.execute(runnable);
    }

    @Override // org.apache.coyote.Processor
    public boolean isAsync() {
        return this.asyncStateMachine.isAsync();
    }

    @Override // org.apache.coyote.AbstractProcessorLight
    public AbstractEndpoint.Handler.SocketState asyncPostProcess() {
        return this.asyncStateMachine.asyncPostProcess();
    }

    @Override // org.apache.coyote.AbstractProcessorLight
    public final AbstractEndpoint.Handler.SocketState dispatch(SocketEvent status) throws IOException {
        AbstractEndpoint.Handler.SocketState state;
        if (status == SocketEvent.OPEN_WRITE && this.response.getWriteListener() != null) {
            this.asyncStateMachine.asyncOperation();
            try {
                if (flushBufferedWrite()) {
                    return AbstractEndpoint.Handler.SocketState.LONG;
                }
            } catch (IOException ioe) {
                if (getLog().isDebugEnabled()) {
                    getLog().debug("Unable to write async data.", ioe);
                }
                status = SocketEvent.ERROR;
                this.request.setAttribute("jakarta.servlet.error.exception", ioe);
            }
        } else if (status == SocketEvent.OPEN_READ && this.request.getReadListener() != null) {
            dispatchNonBlockingRead();
        } else if (status == SocketEvent.ERROR) {
            if (this.request.getAttribute("jakarta.servlet.error.exception") == null) {
                this.request.setAttribute("jakarta.servlet.error.exception", this.socketWrapper.getError());
            }
            if (this.request.getReadListener() != null || this.response.getWriteListener() != null) {
                this.asyncStateMachine.asyncOperation();
            }
        }
        RequestInfo rp = this.request.getRequestProcessor();
        try {
            rp.setStage(3);
            if (!getAdapter().asyncDispatch(this.request, this.response, status)) {
                setErrorState(ErrorState.CLOSE_NOW, null);
            }
        } catch (InterruptedIOException e) {
            setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e);
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            setErrorState(ErrorState.CLOSE_NOW, t);
            getLog().error(sm.getString("http11processor.request.process"), t);
        }
        rp.setStage(7);
        if (getErrorState().isError()) {
            this.request.updateCounters();
            state = AbstractEndpoint.Handler.SocketState.CLOSED;
        } else if (isAsync()) {
            state = AbstractEndpoint.Handler.SocketState.LONG;
        } else {
            this.request.updateCounters();
            state = dispatchEndRequest();
        }
        if (getLog().isDebugEnabled()) {
            getLog().debug("Socket: [" + this.socketWrapper + "], Status in: [" + status + "], State out: [" + state + "]");
        }
        return state;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void parseHost(MessageBytes valueMB) {
        if (valueMB == null || valueMB.isNull()) {
            populateHost();
            populatePort();
            return;
        }
        if (valueMB.getLength() == 0) {
            this.request.serverName().setString("");
            populatePort();
            return;
        }
        ByteChunk valueBC = valueMB.getByteChunk();
        byte[] valueB = valueBC.getBytes();
        int valueL = valueBC.getLength();
        int valueS = valueBC.getStart();
        if (this.hostNameC.length < valueL) {
            this.hostNameC = new char[valueL];
        }
        try {
            int colonPos = Host.parse(valueMB);
            if (colonPos != -1) {
                int port = 0;
                for (int i = colonPos + 1; i < valueL; i++) {
                    char c = (char) valueB[i + valueS];
                    if (c < '0' || c > '9') {
                        this.response.setStatus(400);
                        setErrorState(ErrorState.CLOSE_CLEAN, null);
                        return;
                    }
                    port = ((port * 10) + c) - 48;
                }
                this.request.setServerPort(port);
                valueL = colonPos;
            }
            for (int i2 = 0; i2 < valueL; i2++) {
                this.hostNameC[i2] = (char) valueB[i2 + valueS];
            }
            this.request.serverName().setChars(this.hostNameC, 0, valueL);
        } catch (IllegalArgumentException e) {
            UserDataHelper.Mode logMode = this.userDataHelper.getNextMode();
            if (logMode != null) {
                String message = sm.getString("abstractProcessor.hostInvalid", valueMB.toString());
                switch (logMode) {
                    case INFO_THEN_DEBUG:
                        message = message + sm.getString("abstractProcessor.fallToDebug");
                    case INFO:
                        getLog().info(message, e);
                        break;
                    case DEBUG:
                        getLog().debug(message, e);
                        break;
                }
            }
            this.response.setStatus(400);
            setErrorState(ErrorState.CLOSE_CLEAN, e);
        }
    }

    protected void populateHost() {
    }

    protected void populatePort() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.apache.coyote.AbstractProcessor$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/AbstractProcessor$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$coyote$ActionCode = new int[ActionCode.values().length];

        static {
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.COMMIT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.CLOSE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.ACK.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.CLIENT_FLUSH.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.AVAILABLE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.REQ_SET_BODY_REPLAY.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.IS_ERROR.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.IS_IO_ALLOWED.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.CLOSE_NOW.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.DISABLE_SWALLOW_INPUT.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.REQ_HOST_ADDR_ATTRIBUTE.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.REQ_PEER_ADDR_ATTRIBUTE.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.REQ_HOST_ATTRIBUTE.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.REQ_LOCALPORT_ATTRIBUTE.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.REQ_LOCAL_ADDR_ATTRIBUTE.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.REQ_LOCAL_NAME_ATTRIBUTE.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.REQ_REMOTEPORT_ATTRIBUTE.ordinal()] = 17;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.REQ_SSL_ATTRIBUTE.ordinal()] = 18;
            } catch (NoSuchFieldError e18) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.REQ_SSL_CERTIFICATE.ordinal()] = 19;
            } catch (NoSuchFieldError e19) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.ASYNC_START.ordinal()] = 20;
            } catch (NoSuchFieldError e20) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.ASYNC_COMPLETE.ordinal()] = 21;
            } catch (NoSuchFieldError e21) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.ASYNC_DISPATCH.ordinal()] = 22;
            } catch (NoSuchFieldError e22) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.ASYNC_DISPATCHED.ordinal()] = 23;
            } catch (NoSuchFieldError e23) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.ASYNC_ERROR.ordinal()] = 24;
            } catch (NoSuchFieldError e24) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.ASYNC_IS_ASYNC.ordinal()] = 25;
            } catch (NoSuchFieldError e25) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.ASYNC_IS_COMPLETING.ordinal()] = 26;
            } catch (NoSuchFieldError e26) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.ASYNC_IS_DISPATCHING.ordinal()] = 27;
            } catch (NoSuchFieldError e27) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.ASYNC_IS_ERROR.ordinal()] = 28;
            } catch (NoSuchFieldError e28) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.ASYNC_IS_STARTED.ordinal()] = 29;
            } catch (NoSuchFieldError e29) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.ASYNC_IS_TIMINGOUT.ordinal()] = 30;
            } catch (NoSuchFieldError e30) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.ASYNC_RUN.ordinal()] = 31;
            } catch (NoSuchFieldError e31) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.ASYNC_SETTIMEOUT.ordinal()] = 32;
            } catch (NoSuchFieldError e32) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.ASYNC_TIMEOUT.ordinal()] = 33;
            } catch (NoSuchFieldError e33) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.ASYNC_POST_PROCESS.ordinal()] = 34;
            } catch (NoSuchFieldError e34) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.REQUEST_BODY_FULLY_READ.ordinal()] = 35;
            } catch (NoSuchFieldError e35) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.NB_READ_INTEREST.ordinal()] = 36;
            } catch (NoSuchFieldError e36) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.NB_WRITE_INTEREST.ordinal()] = 37;
            } catch (NoSuchFieldError e37) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.DISPATCH_READ.ordinal()] = 38;
            } catch (NoSuchFieldError e38) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.DISPATCH_WRITE.ordinal()] = 39;
            } catch (NoSuchFieldError e39) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.DISPATCH_EXECUTE.ordinal()] = 40;
            } catch (NoSuchFieldError e40) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.UPGRADE.ordinal()] = 41;
            } catch (NoSuchFieldError e41) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.IS_PUSH_SUPPORTED.ordinal()] = 42;
            } catch (NoSuchFieldError e42) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.PUSH_REQUEST.ordinal()] = 43;
            } catch (NoSuchFieldError e43) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.IS_TRAILER_FIELDS_READY.ordinal()] = 44;
            } catch (NoSuchFieldError e44) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.IS_TRAILER_FIELDS_SUPPORTED.ordinal()] = 45;
            } catch (NoSuchFieldError e45) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.PROTOCOL_REQUEST_ID.ordinal()] = 46;
            } catch (NoSuchFieldError e46) {
            }
            try {
                $SwitchMap$org$apache$coyote$ActionCode[ActionCode.SERVLET_CONNECTION.ordinal()] = 47;
            } catch (NoSuchFieldError e47) {
            }
            $SwitchMap$org$apache$tomcat$util$log$UserDataHelper$Mode = new int[UserDataHelper.Mode.values().length];
            try {
                $SwitchMap$org$apache$tomcat$util$log$UserDataHelper$Mode[UserDataHelper.Mode.INFO_THEN_DEBUG.ordinal()] = 1;
            } catch (NoSuchFieldError e48) {
            }
            try {
                $SwitchMap$org$apache$tomcat$util$log$UserDataHelper$Mode[UserDataHelper.Mode.INFO.ordinal()] = 2;
            } catch (NoSuchFieldError e49) {
            }
            try {
                $SwitchMap$org$apache$tomcat$util$log$UserDataHelper$Mode[UserDataHelper.Mode.DEBUG.ordinal()] = 3;
            } catch (NoSuchFieldError e50) {
            }
        }
    }

    @Override // org.apache.coyote.ActionHook
    public final void action(ActionCode actionCode, Object param) {
        switch (AnonymousClass1.$SwitchMap$org$apache$coyote$ActionCode[actionCode.ordinal()]) {
            case 1:
                if (!this.response.isCommitted()) {
                    try {
                        prepareResponse();
                        return;
                    } catch (IOException e) {
                        handleIOException(e);
                        return;
                    }
                }
                return;
            case 2:
                action(ActionCode.COMMIT, null);
                try {
                    finishResponse();
                    return;
                } catch (IOException e2) {
                    handleIOException(e2);
                    return;
                }
            case 3:
                ack((ContinueResponseTiming) param);
                return;
            case 4:
                action(ActionCode.COMMIT, null);
                try {
                    flush();
                    return;
                } catch (IOException e3) {
                    handleIOException(e3);
                    this.response.setErrorException(e3);
                    return;
                }
            case 5:
                this.request.setAvailable(available(Boolean.TRUE.equals(param)));
                return;
            case 6:
                ByteChunk body = (ByteChunk) param;
                setRequestBody(body);
                return;
            case 7:
                ((AtomicBoolean) param).set(getErrorState().isError());
                return;
            case 8:
                ((AtomicBoolean) param).set(getErrorState().isIoAllowed());
                return;
            case 9:
                setSwallowResponse();
                if (param instanceof Throwable) {
                    setErrorState(ErrorState.CLOSE_NOW, (Throwable) param);
                    return;
                } else {
                    setErrorState(ErrorState.CLOSE_NOW, null);
                    return;
                }
            case 10:
                disableSwallowRequest();
                setErrorState(ErrorState.CLOSE_CLEAN, null);
                return;
            case 11:
                if (getPopulateRequestAttributesFromSocket() && this.socketWrapper != null) {
                    this.request.remoteAddr().setString(this.socketWrapper.getRemoteAddr());
                    return;
                }
                return;
            case 12:
                if (getPopulateRequestAttributesFromSocket() && this.socketWrapper != null) {
                    this.request.peerAddr().setString(this.socketWrapper.getRemoteAddr());
                    return;
                }
                return;
            case 13:
                populateRequestAttributeRemoteHost();
                return;
            case 14:
                if (getPopulateRequestAttributesFromSocket() && this.socketWrapper != null) {
                    this.request.setLocalPort(this.socketWrapper.getLocalPort());
                    return;
                }
                return;
            case 15:
                if (getPopulateRequestAttributesFromSocket() && this.socketWrapper != null) {
                    this.request.localAddr().setString(this.socketWrapper.getLocalAddr());
                    return;
                }
                return;
            case 16:
                if (getPopulateRequestAttributesFromSocket() && this.socketWrapper != null) {
                    this.request.localName().setString(this.socketWrapper.getLocalName());
                    return;
                }
                return;
            case 17:
                if (getPopulateRequestAttributesFromSocket() && this.socketWrapper != null) {
                    this.request.setRemotePort(this.socketWrapper.getRemotePort());
                    return;
                }
                return;
            case 18:
                populateSslRequestAttributes();
                return;
            case 19:
                try {
                    sslReHandShake();
                    return;
                } catch (IOException ioe) {
                    setErrorState(ErrorState.CLOSE_CONNECTION_NOW, ioe);
                    return;
                }
            case 20:
                this.asyncStateMachine.asyncStart((AsyncContextCallback) param);
                return;
            case 21:
                clearDispatches();
                if (this.asyncStateMachine.asyncComplete()) {
                    processSocketEvent(SocketEvent.OPEN_READ, true);
                    return;
                }
                return;
            case 22:
                if (this.asyncStateMachine.asyncDispatch()) {
                    processSocketEvent(SocketEvent.OPEN_READ, true);
                    return;
                }
                return;
            case 23:
                this.asyncStateMachine.asyncDispatched();
                return;
            case 24:
                this.asyncStateMachine.asyncError();
                return;
            case 25:
                ((AtomicBoolean) param).set(this.asyncStateMachine.isAsync());
                return;
            case 26:
                ((AtomicBoolean) param).set(this.asyncStateMachine.isCompleting());
                return;
            case 27:
                ((AtomicBoolean) param).set(this.asyncStateMachine.isAsyncDispatching());
                return;
            case 28:
                ((AtomicBoolean) param).set(this.asyncStateMachine.isAsyncError());
                return;
            case 29:
                ((AtomicBoolean) param).set(this.asyncStateMachine.isAsyncStarted());
                return;
            case 30:
                ((AtomicBoolean) param).set(this.asyncStateMachine.isAsyncTimingOut());
                return;
            case 31:
                this.asyncStateMachine.asyncRun((Runnable) param);
                return;
            case 32:
                if (param == null) {
                    return;
                }
                long timeout = ((Long) param).longValue();
                setAsyncTimeout(timeout);
                return;
            case 33:
                AtomicBoolean result = (AtomicBoolean) param;
                result.set(this.asyncStateMachine.asyncTimeout());
                return;
            case 34:
                this.asyncStateMachine.asyncPostProcess();
                return;
            case 35:
                AtomicBoolean result2 = (AtomicBoolean) param;
                result2.set(isRequestBodyFullyRead());
                return;
            case 36:
                AtomicBoolean isReady = (AtomicBoolean) param;
                isReady.set(isReadyForRead());
                return;
            case 37:
                AtomicBoolean isReady2 = (AtomicBoolean) param;
                isReady2.set(isReadyForWrite());
                return;
            case 38:
                addDispatch(DispatchType.NON_BLOCKING_READ);
                return;
            case 39:
                addDispatch(DispatchType.NON_BLOCKING_WRITE);
                return;
            case 40:
                executeDispatches();
                return;
            case 41:
                doHttpUpgrade((UpgradeToken) param);
                return;
            case 42:
                AtomicBoolean result3 = (AtomicBoolean) param;
                result3.set(isPushSupported());
                return;
            case ELParserConstants.EMPTY /* 43 */:
                doPush((Request) param);
                return;
            case 44:
                AtomicBoolean result4 = (AtomicBoolean) param;
                result4.set(isTrailerFieldsReady());
                return;
            case 45:
                AtomicBoolean result5 = (AtomicBoolean) param;
                result5.set(isTrailerFieldsSupported());
                return;
            case 46:
                AtomicReference<Object> result6 = (AtomicReference) param;
                result6.set(getProtocolRequestId());
                return;
            case 47:
                AtomicReference<Object> result7 = (AtomicReference) param;
                result7.set(getServletConnection());
                return;
            default:
                return;
        }
    }

    private void handleIOException(IOException ioe) {
        if (ioe instanceof CloseNowException) {
            setErrorState(ErrorState.CLOSE_NOW, ioe);
        } else {
            setErrorState(ErrorState.CLOSE_CONNECTION_NOW, ioe);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dispatchNonBlockingRead() {
        this.asyncStateMachine.asyncOperation();
    }

    @Override // org.apache.coyote.Processor
    public void timeoutAsync(long now) {
        if (now < 0) {
            doTimeoutAsync();
            return;
        }
        long asyncTimeout = getAsyncTimeout();
        if (asyncTimeout <= 0) {
            if (!this.asyncStateMachine.isAvailable()) {
                doTimeoutAsync();
            }
        } else {
            long asyncStart = this.asyncStateMachine.getLastAsyncStart();
            if (now - asyncStart > asyncTimeout) {
                doTimeoutAsync();
            }
        }
    }

    private void doTimeoutAsync() {
        setAsyncTimeout(-1L);
        this.asyncTimeoutGeneration = this.asyncStateMachine.getCurrentGeneration();
        processSocketEvent(SocketEvent.TIMEOUT, true);
    }

    @Override // org.apache.coyote.Processor
    public boolean checkAsyncTimeoutGeneration() {
        return this.asyncTimeoutGeneration == this.asyncStateMachine.getCurrentGeneration();
    }

    public void setAsyncTimeout(long timeout) {
        this.asyncTimeout = timeout;
    }

    public long getAsyncTimeout() {
        return this.asyncTimeout;
    }

    @Override // org.apache.coyote.Processor
    public void recycle() {
        this.errorState = ErrorState.NONE;
        this.asyncStateMachine.recycle();
    }

    protected boolean getPopulateRequestAttributesFromSocket() {
        return true;
    }

    protected void populateRequestAttributeRemoteHost() {
        if (getPopulateRequestAttributesFromSocket() && this.socketWrapper != null) {
            this.request.remoteHost().setString(this.socketWrapper.getRemoteHost());
        }
    }

    protected void populateSslRequestAttributes() {
        try {
            if (this.sslSupport != null) {
                Object sslO = this.sslSupport.getCipherSuite();
                if (sslO != null) {
                    this.request.setAttribute("jakarta.servlet.request.cipher_suite", sslO);
                }
                Object sslO2 = this.sslSupport.getPeerCertificateChain();
                if (sslO2 != null) {
                    this.request.setAttribute("jakarta.servlet.request.X509Certificate", sslO2);
                }
                Object sslO3 = this.sslSupport.getKeySize();
                if (sslO3 != null) {
                    this.request.setAttribute("jakarta.servlet.request.key_size", sslO3);
                }
                Object sslO4 = this.sslSupport.getSessionId();
                if (sslO4 != null) {
                    this.request.setAttribute("jakarta.servlet.request.ssl_session_id", sslO4);
                }
                Object sslO5 = this.sslSupport.getProtocol();
                if (sslO5 != null) {
                    this.request.setAttribute(SSLSupport.PROTOCOL_VERSION_KEY, sslO5);
                }
                Object sslO6 = this.sslSupport.getRequestedProtocols();
                if (sslO6 != null) {
                    this.request.setAttribute(SSLSupport.REQUESTED_PROTOCOL_VERSIONS_KEY, sslO6);
                }
                Object sslO7 = this.sslSupport.getRequestedCiphers();
                if (sslO7 != null) {
                    this.request.setAttribute(SSLSupport.REQUESTED_CIPHERS_KEY, sslO7);
                }
                this.request.setAttribute("jakarta.servlet.request.ssl_session_mgr", this.sslSupport);
            }
        } catch (Exception e) {
            getLog().warn(sm.getString("abstractProcessor.socket.ssl"), e);
        }
    }

    protected void sslReHandShake() throws IOException {
    }

    protected void processSocketEvent(SocketEvent event, boolean dispatch) {
        SocketWrapperBase<?> socketWrapper = getSocketWrapper();
        if (socketWrapper != null) {
            socketWrapper.processSocket(event, dispatch);
        }
    }

    protected boolean isReadyForRead() {
        if (available(true) > 0) {
            return true;
        }
        if (!isRequestBodyFullyRead()) {
            registerReadInterest();
            return false;
        }
        return false;
    }

    protected void executeDispatches() {
        SocketWrapperBase<?> socketWrapper = getSocketWrapper();
        Iterator<DispatchType> dispatches = getIteratorAndClearDispatches();
        if (socketWrapper != null) {
            Lock lock = socketWrapper.getLock();
            lock.lock();
            while (dispatches != null) {
                try {
                    if (!dispatches.hasNext()) {
                        break;
                    }
                    DispatchType dispatchType = dispatches.next();
                    socketWrapper.processSocket(dispatchType.getSocketStatus(), false);
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    @Override // org.apache.coyote.Processor
    public UpgradeToken getUpgradeToken() {
        throw new IllegalStateException(sm.getString("abstractProcessor.httpupgrade.notsupported"));
    }

    protected void doHttpUpgrade(UpgradeToken upgradeToken) {
        throw new UnsupportedOperationException(sm.getString("abstractProcessor.httpupgrade.notsupported"));
    }

    @Override // org.apache.coyote.Processor
    public ByteBuffer getLeftoverInput() {
        throw new IllegalStateException(sm.getString("abstractProcessor.httpupgrade.notsupported"));
    }

    @Override // org.apache.coyote.Processor
    public boolean isUpgrade() {
        return false;
    }

    protected boolean isPushSupported() {
        return false;
    }

    protected void doPush(Request pushTarget) {
        throw new UnsupportedOperationException(sm.getString("abstractProcessor.pushrequest.notsupported"));
    }

    protected boolean isTrailerFieldsSupported() {
        return false;
    }

    protected Object getProtocolRequestId() {
        return null;
    }

    @Override // org.apache.coyote.AbstractProcessorLight
    protected final void logAccess(SocketWrapperBase<?> socketWrapper) throws IOException {
        setSocketWrapper(socketWrapper);
        this.request.setStartTimeNanos(System.nanoTime());
        this.response.setStatus(400);
        this.response.setError();
        getAdapter().log(this.request, this.response, 0L);
    }
}
