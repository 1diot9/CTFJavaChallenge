package org.springframework.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrencyThrottleSupport.class */
public abstract class ConcurrencyThrottleSupport implements Serializable {
    public static final int UNBOUNDED_CONCURRENCY = -1;
    public static final int NO_CONCURRENCY = 0;
    protected transient Log logger = LogFactory.getLog(getClass());
    private transient Object monitor = new Object();
    private int concurrencyLimit = -1;
    private int concurrencyCount = 0;

    public void setConcurrencyLimit(int concurrencyLimit) {
        this.concurrencyLimit = concurrencyLimit;
    }

    public int getConcurrencyLimit() {
        return this.concurrencyLimit;
    }

    public boolean isThrottleActive() {
        return this.concurrencyLimit >= 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void beforeAccess() {
        if (this.concurrencyLimit == 0) {
            throw new IllegalStateException("Currently no invocations allowed - concurrency limit set to NO_CONCURRENCY");
        }
        if (this.concurrencyLimit > 0) {
            boolean debug = this.logger.isDebugEnabled();
            synchronized (this.monitor) {
                boolean interrupted = false;
                while (this.concurrencyCount >= this.concurrencyLimit) {
                    if (interrupted) {
                        throw new IllegalStateException("Thread was interrupted while waiting for invocation access, but concurrency limit still does not allow for entering");
                    }
                    if (debug) {
                        this.logger.debug("Concurrency count " + this.concurrencyCount + " has reached limit " + this.concurrencyLimit + " - blocking");
                    }
                    try {
                        this.monitor.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        interrupted = true;
                    }
                }
                if (debug) {
                    this.logger.debug("Entering throttle at concurrency count " + this.concurrencyCount);
                }
                this.concurrencyCount++;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void afterAccess() {
        if (this.concurrencyLimit >= 0) {
            boolean debug = this.logger.isDebugEnabled();
            synchronized (this.monitor) {
                this.concurrencyCount--;
                if (debug) {
                    this.logger.debug("Returning from throttle at concurrency count " + this.concurrencyCount);
                }
                this.monitor.notify();
            }
        }
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.logger = LogFactory.getLog(getClass());
        this.monitor = new Object();
    }
}
