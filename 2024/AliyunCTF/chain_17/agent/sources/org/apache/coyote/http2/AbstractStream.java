package org.apache.coyote.http2;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/AbstractStream.class */
abstract class AbstractStream {
    private static final Log log = LogFactory.getLog((Class<?>) AbstractStream.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) AbstractStream.class);
    private final Integer identifier;
    private final String idAsString;
    private long windowSize = 65535;
    protected final Lock windowAllocationLock = new ReentrantLock();
    protected final Condition windowAllocationAvailable = this.windowAllocationLock.newCondition();
    private volatile int connectionAllocationRequested = 0;
    private volatile int connectionAllocationMade = 0;

    abstract String getConnectionId();

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractStream(Integer identifier) {
        this.identifier = identifier;
        this.idAsString = identifier.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Integer getIdentifier() {
        return this.identifier;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final String getIdAsString() {
        return this.idAsString;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int getIdAsInt() {
        return this.identifier.intValue();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setWindowSize(long windowSize) {
        this.windowAllocationLock.lock();
        try {
            this.windowSize = windowSize;
        } finally {
            this.windowAllocationLock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final long getWindowSize() {
        this.windowAllocationLock.lock();
        try {
            return this.windowSize;
        } finally {
            this.windowAllocationLock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void incrementWindowSize(int increment) throws Http2Exception {
        this.windowAllocationLock.lock();
        try {
            this.windowSize += increment;
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("abstractStream.windowSizeInc", getConnectionId(), getIdAsString(), Integer.toString(increment), Long.toString(this.windowSize)));
            }
            if (this.windowSize > 2147483647L) {
                String msg = sm.getString("abstractStream.windowSizeTooBig", getConnectionId(), this.identifier, Integer.toString(increment), Long.toString(this.windowSize));
                if (this.identifier.intValue() == 0) {
                    throw new ConnectionException(msg, Http2Error.FLOW_CONTROL_ERROR);
                }
                throw new StreamException(msg, Http2Error.FLOW_CONTROL_ERROR, this.identifier.intValue());
            }
        } finally {
            this.windowAllocationLock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void decrementWindowSize(int decrement) {
        this.windowAllocationLock.lock();
        try {
            this.windowSize -= decrement;
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("abstractStream.windowSizeDec", getConnectionId(), getIdAsString(), Integer.toString(decrement), Long.toString(this.windowSize)));
            }
        } finally {
            this.windowAllocationLock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int getConnectionAllocationRequested() {
        return this.connectionAllocationRequested;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setConnectionAllocationRequested(int connectionAllocationRequested) {
        log.debug(sm.getString("abstractStream.setConnectionAllocationRequested", getConnectionId(), getIdAsString(), Integer.toString(this.connectionAllocationRequested), Integer.toString(connectionAllocationRequested)));
        this.connectionAllocationRequested = connectionAllocationRequested;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int getConnectionAllocationMade() {
        return this.connectionAllocationMade;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setConnectionAllocationMade(int connectionAllocationMade) {
        log.debug(sm.getString("abstractStream.setConnectionAllocationMade", getConnectionId(), getIdAsString(), Integer.toString(this.connectionAllocationMade), Integer.toString(connectionAllocationMade)));
        this.connectionAllocationMade = connectionAllocationMade;
    }
}
