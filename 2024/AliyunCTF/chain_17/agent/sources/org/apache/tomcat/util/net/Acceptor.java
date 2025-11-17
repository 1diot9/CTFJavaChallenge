package org.apache.tomcat.util.net;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/Acceptor.class */
public class Acceptor<U> implements Runnable {
    private static final Log log = LogFactory.getLog((Class<?>) Acceptor.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) Acceptor.class);
    private static final int INITIAL_ERROR_DELAY = 50;
    private static final int MAX_ERROR_DELAY = 1600;
    private final AbstractEndpoint<?, U> endpoint;
    private String threadName;
    private volatile boolean stopCalled = false;
    private final CountDownLatch stopLatch = new CountDownLatch(1);
    protected volatile AcceptorState state = AcceptorState.NEW;

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/Acceptor$AcceptorState.class */
    public enum AcceptorState {
        NEW,
        RUNNING,
        PAUSED,
        ENDED
    }

    public Acceptor(AbstractEndpoint<?, U> endpoint) {
        this.endpoint = endpoint;
    }

    public final AcceptorState getState() {
        return this.state;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    final String getThreadName() {
        return this.threadName;
    }

    @Override // java.lang.Runnable
    public void run() {
        int errorDelay = 0;
        long pauseStart = 0;
        while (!this.stopCalled) {
            try {
                while (this.endpoint.isPaused() && !this.stopCalled) {
                    if (this.state != AcceptorState.PAUSED) {
                        pauseStart = System.nanoTime();
                        this.state = AcceptorState.PAUSED;
                    }
                    if (System.nanoTime() - pauseStart > 1000000) {
                        try {
                            if (System.nanoTime() - pauseStart > 10000000) {
                                Thread.sleep(10L);
                            } else {
                                Thread.sleep(1L);
                            }
                        } catch (InterruptedException e) {
                        }
                    }
                }
                if (this.stopCalled) {
                    break;
                }
                this.state = AcceptorState.RUNNING;
                try {
                    this.endpoint.countUpOrAwaitConnection();
                    if (!this.endpoint.isPaused()) {
                        try {
                            U socket = this.endpoint.serverSocketAccept();
                            errorDelay = 0;
                            if (!this.stopCalled && !this.endpoint.isPaused()) {
                                if (!this.endpoint.setSocketOptions(socket)) {
                                    this.endpoint.closeSocket(socket);
                                }
                            } else {
                                this.endpoint.destroySocket(socket);
                            }
                        } catch (Exception ioe) {
                            this.endpoint.countDownConnection();
                            if (!this.endpoint.isRunning()) {
                                break;
                            }
                            errorDelay = handleExceptionWithDelay(errorDelay);
                            throw ioe;
                            break;
                        }
                    }
                } catch (Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                    String msg = sm.getString("endpoint.accept.fail");
                    log.error(msg, t);
                }
            } finally {
                this.stopLatch.countDown();
            }
        }
        this.state = AcceptorState.ENDED;
    }

    public void stop(int waitSeconds) {
        this.stopCalled = true;
        if (waitSeconds > 0) {
            try {
                if (!this.stopLatch.await(waitSeconds, TimeUnit.SECONDS)) {
                    log.warn(sm.getString("acceptor.stop.fail", getThreadName()));
                }
            } catch (InterruptedException e) {
                log.warn(sm.getString("acceptor.stop.interrupted", getThreadName()), e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int handleExceptionWithDelay(int currentErrorDelay) {
        if (currentErrorDelay > 0) {
            try {
                Thread.sleep(currentErrorDelay);
            } catch (InterruptedException e) {
            }
        }
        if (currentErrorDelay == 0) {
            return 50;
        }
        if (currentErrorDelay < MAX_ERROR_DELAY) {
            return currentErrorDelay * 2;
        }
        return MAX_ERROR_DELAY;
    }
}
