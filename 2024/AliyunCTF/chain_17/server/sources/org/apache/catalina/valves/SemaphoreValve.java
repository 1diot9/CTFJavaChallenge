package org.apache.catalina.valves;

import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/SemaphoreValve.class */
public class SemaphoreValve extends ValveBase {
    protected Semaphore semaphore;
    protected int concurrency;
    protected boolean fairness;
    protected boolean block;
    protected boolean interruptible;

    public SemaphoreValve() {
        super(true);
        this.semaphore = null;
        this.concurrency = 10;
        this.fairness = false;
        this.block = true;
        this.interruptible = false;
    }

    public int getConcurrency() {
        return this.concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public boolean getFairness() {
        return this.fairness;
    }

    public void setFairness(boolean fairness) {
        this.fairness = fairness;
    }

    public boolean getBlock() {
        return this.block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public boolean getInterruptible() {
        return this.interruptible;
    }

    public void setInterruptible(boolean interruptible) {
        this.interruptible = interruptible;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        this.semaphore = new Semaphore(this.concurrency, this.fairness);
        super.startInternal();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleBase
    public synchronized void stopInternal() throws LifecycleException {
        super.stopInternal();
        this.semaphore = null;
    }

    @Override // org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws IOException, ServletException {
        if (!controlConcurrency(request, response)) {
            getNext().invoke(request, response);
            return;
        }
        try {
            if (this.block) {
                if (this.interruptible) {
                    try {
                        this.semaphore.acquire();
                    } catch (InterruptedException e) {
                        permitDenied(request, response);
                        if (0 != 0) {
                            this.semaphore.release();
                            return;
                        }
                        return;
                    }
                } else {
                    this.semaphore.acquireUninterruptibly();
                }
            } else if (!this.semaphore.tryAcquire()) {
                permitDenied(request, response);
                if (0 != 0) {
                    this.semaphore.release();
                    return;
                }
                return;
            }
            getNext().invoke(request, response);
            if (1 != 0) {
                this.semaphore.release();
            }
        } catch (Throwable th) {
            if (1 != 0) {
                this.semaphore.release();
            }
            throw th;
        }
    }

    public boolean controlConcurrency(Request request, Response response) {
        return true;
    }

    public void permitDenied(Request request, Response response) throws IOException, ServletException {
    }
}
