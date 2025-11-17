package org.apache.catalina.valves;

import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.Store;
import org.apache.catalina.StoreManager;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.filters.RateLimitFilter;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/PersistentValve.class */
public class PersistentValve extends ValveBase {
    private static final ClassLoader MY_CLASSLOADER = PersistentValve.class.getClassLoader();
    private volatile boolean clBindRequired;
    protected Pattern filter;
    private ConcurrentMap<String, UsageCountingSemaphore> sessionToSemaphoreMap;
    private boolean semaphoreFairness;
    private boolean semaphoreBlockOnAcquire;
    private boolean semaphoreAcquireUninterruptibly;

    public PersistentValve() {
        super(true);
        this.filter = null;
        this.sessionToSemaphoreMap = new ConcurrentHashMap();
        this.semaphoreFairness = true;
        this.semaphoreBlockOnAcquire = true;
        this.semaphoreAcquireUninterruptibly = true;
    }

    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.Contained
    public void setContainer(Container container) {
        super.setContainer(container);
        if ((container instanceof Engine) || (container instanceof Host)) {
            this.clBindRequired = true;
        } else {
            this.clBindRequired = false;
        }
    }

    /* JADX WARN: Finally extract failed */
    @Override // org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws IOException, ServletException {
        Session hsess;
        Store store;
        if (isRequestWithoutSession(request.getDecodedRequestURI())) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug(sm.getString("persistentValve.requestIgnore", request.getDecodedRequestURI()));
            }
            getNext().invoke(request, response);
            return;
        }
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug(sm.getString("persistentValve.requestProcess", request.getDecodedRequestURI()));
        }
        Context context = request.getContext();
        if (context == null) {
            response.sendError(500, sm.getString("standardHost.noContext"));
            return;
        }
        String sessionId = request.getRequestedSessionId();
        UsageCountingSemaphore semaphore = null;
        if (sessionId != null) {
            try {
                semaphore = this.sessionToSemaphoreMap.compute(sessionId, (k, v) -> {
                    return v == null ? new UsageCountingSemaphore(this.semaphoreFairness) : v.incrementUsageCount();
                });
                if (this.semaphoreBlockOnAcquire) {
                    if (this.semaphoreAcquireUninterruptibly) {
                        semaphore.acquireUninterruptibly();
                    } else {
                        try {
                            semaphore.acquire();
                        } catch (InterruptedException e) {
                            onSemaphoreNotAcquired(request, response);
                            if (this.containerLog.isDebugEnabled()) {
                                this.containerLog.debug(sm.getString("persistentValve.acquireInterrupted", request.getDecodedRequestURI()));
                            }
                            if (semaphore != null) {
                                if (0 != 0) {
                                    semaphore.release();
                                }
                                this.sessionToSemaphoreMap.computeIfPresent(sessionId, (k2, v2) -> {
                                    if (v2.decrementAndGetUsageCount() == 0) {
                                        return null;
                                    }
                                    return v2;
                                });
                                return;
                            }
                            return;
                        }
                    }
                } else if (!semaphore.tryAcquire()) {
                    onSemaphoreNotAcquired(request, response);
                    if (this.containerLog.isDebugEnabled()) {
                        this.containerLog.debug(sm.getString("persistentValve.acquireFailed", request.getDecodedRequestURI()));
                    }
                    if (semaphore != null) {
                        if (1 != 0) {
                            semaphore.release();
                        }
                        this.sessionToSemaphoreMap.computeIfPresent(sessionId, (k22, v22) -> {
                            if (v22.decrementAndGetUsageCount() == 0) {
                                return null;
                            }
                            return v22;
                        });
                        return;
                    }
                    return;
                }
            } finally {
                if (semaphore != null) {
                    if (1 != 0) {
                        semaphore.release();
                    }
                    this.sessionToSemaphoreMap.computeIfPresent(sessionId, (k222, v222) -> {
                        if (v222.decrementAndGetUsageCount() == 0) {
                            return null;
                        }
                        return v222;
                    });
                }
            }
        }
        Manager manager = context.getManager();
        if (sessionId != null && (manager instanceof StoreManager) && (store = ((StoreManager) manager).getStore()) != null) {
            Session session = null;
            try {
                session = store.load(sessionId);
            } catch (Exception e2) {
                this.containerLog.error("deserializeError");
            }
            if (session != null) {
                if (!session.isValid() || isSessionStale(session, System.currentTimeMillis())) {
                    if (this.containerLog.isDebugEnabled()) {
                        this.containerLog.debug("session swapped in is invalid or expired");
                    }
                    session.expire();
                    store.remove(sessionId);
                } else {
                    session.setManager(manager);
                    manager.add(session);
                    session.access();
                    session.endAccess();
                }
            }
        }
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug("sessionId: " + sessionId);
        }
        getNext().invoke(request, response);
        if (!request.isAsync()) {
            try {
                hsess = request.getSessionInternal(false);
            } catch (Exception e3) {
                hsess = null;
            }
            String newsessionId = null;
            if (hsess != null) {
                newsessionId = hsess.getIdInternal();
            }
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug("newsessionId: " + newsessionId);
            }
            if (newsessionId != null) {
                try {
                    bind(context);
                    if (manager instanceof StoreManager) {
                        Session session2 = manager.findSession(newsessionId);
                        Store store2 = ((StoreManager) manager).getStore();
                        boolean stored = false;
                        if (session2 != null && store2 != null && session2.isValid() && !isSessionStale(session2, System.currentTimeMillis())) {
                            store2.save(session2);
                            ((StoreManager) manager).removeSuper(session2);
                            session2.recycle();
                            stored = true;
                        }
                        if (!stored && this.containerLog.isDebugEnabled()) {
                            this.containerLog.debug("newsessionId store: " + store2 + " session: " + session2 + " valid: " + (session2 == null ? "N/A" : Boolean.toString(session2.isValid())) + " stale: " + isSessionStale(session2, System.currentTimeMillis()));
                        }
                    } else if (this.containerLog.isDebugEnabled()) {
                        this.containerLog.debug("newsessionId Manager: " + manager);
                    }
                    unbind(context);
                } catch (Throwable th) {
                    unbind(context);
                    throw th;
                }
            }
        }
    }

    protected void onSemaphoreNotAcquired(Request request, Response response) throws IOException {
        response.sendError(RateLimitFilter.DEFAULT_STATUS_CODE);
    }

    protected boolean isSessionStale(Session session, long timeNow) {
        int maxInactiveInterval;
        if (session != null && (maxInactiveInterval = session.getMaxInactiveInterval()) > 0) {
            int timeIdle = (int) (session.getIdleTimeInternal() / 1000);
            if (timeIdle >= maxInactiveInterval) {
                return true;
            }
            return false;
        }
        return false;
    }

    private void bind(Context context) {
        if (this.clBindRequired) {
            context.bind(Globals.IS_SECURITY_ENABLED, MY_CLASSLOADER);
        }
    }

    private void unbind(Context context) {
        if (this.clBindRequired) {
            context.unbind(Globals.IS_SECURITY_ENABLED, MY_CLASSLOADER);
        }
    }

    protected boolean isRequestWithoutSession(String uri) {
        Pattern f = this.filter;
        return f != null && f.matcher(uri).matches();
    }

    public String getFilter() {
        if (this.filter == null) {
            return null;
        }
        return this.filter.toString();
    }

    public void setFilter(String filter) {
        if (filter == null || filter.length() == 0) {
            this.filter = null;
            return;
        }
        try {
            this.filter = Pattern.compile(filter);
        } catch (PatternSyntaxException pse) {
            this.container.getLogger().error(sm.getString("persistentValve.filter.failure", filter), pse);
        }
    }

    public boolean isSemaphoreFairness() {
        return this.semaphoreFairness;
    }

    public void setSemaphoreFairness(boolean semaphoreFairness) {
        this.semaphoreFairness = semaphoreFairness;
    }

    public boolean isSemaphoreBlockOnAcquire() {
        return this.semaphoreBlockOnAcquire;
    }

    public void setSemaphoreBlockOnAcquire(boolean semaphoreBlockOnAcquire) {
        this.semaphoreBlockOnAcquire = semaphoreBlockOnAcquire;
    }

    public boolean isSemaphoreAcquireUninterruptibly() {
        return this.semaphoreAcquireUninterruptibly;
    }

    public void setSemaphoreAcquireUninterruptibly(boolean semaphoreAcquireUninterruptibly) {
        this.semaphoreAcquireUninterruptibly = semaphoreAcquireUninterruptibly;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/PersistentValve$UsageCountingSemaphore.class */
    public static class UsageCountingSemaphore {
        private final AtomicLong usageCount = new AtomicLong(1);
        private final Semaphore semaphore;

        private UsageCountingSemaphore(boolean fairness) {
            this.semaphore = new Semaphore(1, fairness);
        }

        private UsageCountingSemaphore incrementUsageCount() {
            this.usageCount.incrementAndGet();
            return this;
        }

        private long decrementAndGetUsageCount() {
            return this.usageCount.decrementAndGet();
        }

        private void acquire() throws InterruptedException {
            this.semaphore.acquire();
        }

        private void acquireUninterruptibly() {
            this.semaphore.acquireUninterruptibly();
        }

        private boolean tryAcquire() {
            return this.semaphore.tryAcquire();
        }

        private void release() {
            this.semaphore.release();
        }
    }
}
