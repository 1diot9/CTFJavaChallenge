package org.apache.tomcat.websocket;

import jakarta.websocket.CloseReason;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.WsFrameBase;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsFrameClient.class */
public class WsFrameClient extends WsFrameBase {
    private final Log log;
    private static final StringManager sm = StringManager.getManager((Class<?>) WsFrameClient.class);
    private final AsyncChannelWrapper channel;
    private final CompletionHandler<Integer, Void> handler;
    private volatile ByteBuffer response;

    public WsFrameClient(ByteBuffer response, AsyncChannelWrapper channel, WsSession wsSession, Transformation transformation) {
        super(wsSession, transformation);
        this.log = LogFactory.getLog((Class<?>) WsFrameClient.class);
        this.response = response;
        this.channel = channel;
        this.handler = new WsFrameClientCompletionHandler();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startInputProcessing() {
        try {
            processSocketRead();
        } catch (IOException e) {
            close(e);
        }
    }

    private void processSocketRead() throws IOException {
        while (true) {
            switch (getReadState()) {
                case WAITING:
                    if (!changeReadState(WsFrameBase.ReadState.WAITING, WsFrameBase.ReadState.PROCESSING)) {
                        break;
                    } else {
                        while (this.response.hasRemaining()) {
                            if (isSuspended()) {
                                if (changeReadState(WsFrameBase.ReadState.SUSPENDING_PROCESS, WsFrameBase.ReadState.SUSPENDED)) {
                                    return;
                                }
                            } else {
                                this.inputBuffer.mark();
                                this.inputBuffer.position(this.inputBuffer.limit()).limit(this.inputBuffer.capacity());
                                int toCopy = Math.min(this.response.remaining(), this.inputBuffer.remaining());
                                int orgLimit = this.response.limit();
                                this.response.limit(this.response.position() + toCopy);
                                this.inputBuffer.put(this.response);
                                this.response.limit(orgLimit);
                                this.inputBuffer.limit(this.inputBuffer.position()).reset();
                                processInputBuffer();
                            }
                        }
                        this.response.clear();
                        if (isOpen()) {
                            this.channel.read(this.response, null, this.handler);
                            return;
                        } else {
                            changeReadState(WsFrameBase.ReadState.CLOSING);
                            return;
                        }
                    }
                case SUSPENDING_WAIT:
                    if (!changeReadState(WsFrameBase.ReadState.SUSPENDING_WAIT, WsFrameBase.ReadState.SUSPENDED)) {
                        break;
                    } else {
                        return;
                    }
                default:
                    throw new IllegalStateException(sm.getString("wsFrameServer.illegalReadState", getReadState()));
            }
        }
    }

    private void close(Throwable t) {
        CloseReason cr;
        changeReadState(WsFrameBase.ReadState.CLOSING);
        if (t instanceof WsIOException) {
            cr = ((WsIOException) t).getCloseReason();
        } else {
            cr = new CloseReason(CloseReason.CloseCodes.CLOSED_ABNORMALLY, t.getMessage());
        }
        this.wsSession.doClose(cr, cr, true);
    }

    @Override // org.apache.tomcat.websocket.WsFrameBase
    protected boolean isMasked() {
        return false;
    }

    @Override // org.apache.tomcat.websocket.WsFrameBase
    protected Log getLog() {
        return this.log;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsFrameClient$WsFrameClientCompletionHandler.class */
    private class WsFrameClientCompletionHandler implements CompletionHandler<Integer, Void> {
        private WsFrameClientCompletionHandler() {
        }

        @Override // java.nio.channels.CompletionHandler
        public void completed(Integer result, Void attachment) {
            if (result.intValue() == -1) {
                if (WsFrameClient.this.isOpen()) {
                    WsFrameClient.this.close(new EOFException());
                }
            } else {
                WsFrameClient.this.response.flip();
                doResumeProcessing(true);
            }
        }

        @Override // java.nio.channels.CompletionHandler
        public void failed(Throwable exc, Void attachment) {
            if (WsFrameClient.this.log.isDebugEnabled()) {
                WsFrameClient.this.log.debug(WsFrameClient.sm.getString("wsFrame.readFailed"), exc);
            }
            if (exc instanceof ReadBufferOverflowException) {
                WsFrameClient.this.response = ByteBuffer.allocate(((ReadBufferOverflowException) exc).getMinBufferSize());
                WsFrameClient.this.response.flip();
                doResumeProcessing(false);
                return;
            }
            WsFrameClient.this.close(exc);
        }

        private void doResumeProcessing(boolean checkOpenOnError) {
            while (true) {
                switch (WsFrameClient.this.getReadState()) {
                    case PROCESSING:
                        if (!WsFrameClient.this.changeReadState(WsFrameBase.ReadState.PROCESSING, WsFrameBase.ReadState.WAITING)) {
                            break;
                        } else {
                            WsFrameClient.this.resumeProcessing(checkOpenOnError);
                            return;
                        }
                    case SUSPENDING_PROCESS:
                        if (!WsFrameClient.this.changeReadState(WsFrameBase.ReadState.SUSPENDING_PROCESS, WsFrameBase.ReadState.SUSPENDED)) {
                            break;
                        } else {
                            return;
                        }
                    default:
                        throw new IllegalStateException(WsFrameClient.sm.getString("wsFrame.illegalReadState", WsFrameClient.this.getReadState()));
                }
            }
        }
    }

    @Override // org.apache.tomcat.websocket.WsFrameBase
    protected void resumeProcessing() {
        resumeProcessing(true);
    }

    private void resumeProcessing(boolean checkOpenOnError) {
        try {
            processSocketRead();
        } catch (IOException e) {
            if (checkOpenOnError) {
                if (isOpen()) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug(sm.getString("wsFrameClient.ioe"), e);
                    }
                    close(e);
                    return;
                }
                return;
            }
            close(e);
        }
    }
}
