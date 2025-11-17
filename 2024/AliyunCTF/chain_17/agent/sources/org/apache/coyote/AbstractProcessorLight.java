package org.apache.coyote;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.DispatchType;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/AbstractProcessorLight.class */
public abstract class AbstractProcessorLight implements Processor {
    private Set<DispatchType> dispatches = new CopyOnWriteArraySet();

    protected abstract AbstractEndpoint.Handler.SocketState service(SocketWrapperBase<?> socketWrapperBase) throws IOException;

    protected abstract AbstractEndpoint.Handler.SocketState dispatch(SocketEvent socketEvent) throws IOException;

    protected abstract AbstractEndpoint.Handler.SocketState asyncPostProcess();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract Log getLog();

    @Override // org.apache.coyote.Processor
    public AbstractEndpoint.Handler.SocketState process(SocketWrapperBase<?> socketWrapper, SocketEvent status) throws IOException {
        AbstractEndpoint.Handler.SocketState state = AbstractEndpoint.Handler.SocketState.CLOSED;
        Iterator<DispatchType> dispatches = null;
        while (true) {
            if (dispatches != null) {
                DispatchType nextDispatch = dispatches.next();
                if (getLog().isDebugEnabled()) {
                    getLog().debug("Processing dispatch type: [" + nextDispatch + "]");
                }
                state = dispatch(nextDispatch.getSocketStatus());
                if (!dispatches.hasNext()) {
                    state = checkForPipelinedData(state, socketWrapper);
                }
            } else if (status != SocketEvent.DISCONNECT) {
                if (isAsync() || isUpgrade() || state == AbstractEndpoint.Handler.SocketState.ASYNC_END) {
                    AbstractEndpoint.Handler.SocketState state2 = dispatch(status);
                    state = checkForPipelinedData(state2, socketWrapper);
                } else if (status == SocketEvent.OPEN_WRITE) {
                    state = AbstractEndpoint.Handler.SocketState.LONG;
                } else if (status == SocketEvent.OPEN_READ) {
                    state = service(socketWrapper);
                } else if (status == SocketEvent.CONNECT_FAIL) {
                    logAccess(socketWrapper);
                } else {
                    state = AbstractEndpoint.Handler.SocketState.CLOSED;
                }
            }
            if (getLog().isDebugEnabled()) {
                getLog().debug("Socket: [" + socketWrapper + "], Status in: [" + status + "], State out: [" + state + "]");
            }
            if (isAsync()) {
                state = asyncPostProcess();
                if (getLog().isDebugEnabled()) {
                    getLog().debug("Socket: [" + socketWrapper + "], State after async post processing: [" + state + "]");
                }
            }
            if (dispatches == null || !dispatches.hasNext()) {
                dispatches = getIteratorAndClearDispatches();
            }
            if (state == AbstractEndpoint.Handler.SocketState.ASYNC_END || (dispatches != null && state != AbstractEndpoint.Handler.SocketState.CLOSED)) {
            }
        }
        return state;
    }

    private AbstractEndpoint.Handler.SocketState checkForPipelinedData(AbstractEndpoint.Handler.SocketState inState, SocketWrapperBase<?> socketWrapper) throws IOException {
        if (inState == AbstractEndpoint.Handler.SocketState.OPEN) {
            return service(socketWrapper);
        }
        return inState;
    }

    public void addDispatch(DispatchType dispatchType) {
        synchronized (this.dispatches) {
            this.dispatches.add(dispatchType);
        }
    }

    public Iterator<DispatchType> getIteratorAndClearDispatches() {
        Iterator<DispatchType> result;
        synchronized (this.dispatches) {
            result = this.dispatches.iterator();
            if (result.hasNext()) {
                this.dispatches.clear();
            } else {
                result = null;
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void clearDispatches() {
        synchronized (this.dispatches) {
            this.dispatches.clear();
        }
    }

    protected void logAccess(SocketWrapperBase<?> socketWrapper) throws IOException {
    }
}
