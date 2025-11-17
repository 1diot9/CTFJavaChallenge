package org.apache.catalina.core;

import java.util.concurrent.Executor;
import org.apache.catalina.ContainerEvent;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.ProtocolHandler;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ThreadLocalLeakPreventionListener.class */
public class ThreadLocalLeakPreventionListener extends FrameworkListener {
    private volatile boolean serverStopping = false;
    private static final Log log = LogFactory.getLog((Class<?>) ThreadLocalLeakPreventionListener.class);
    protected static final StringManager sm = StringManager.getManager((Class<?>) ThreadLocalLeakPreventionListener.class);

    @Override // org.apache.catalina.core.FrameworkListener, org.apache.catalina.LifecycleListener
    public void lifecycleEvent(LifecycleEvent event) {
        try {
            super.lifecycleEvent(event);
            Lifecycle lifecycle = event.getLifecycle();
            if (Lifecycle.BEFORE_STOP_EVENT.equals(event.getType()) && (lifecycle instanceof Server)) {
                this.serverStopping = true;
            }
            if (Lifecycle.AFTER_STOP_EVENT.equals(event.getType()) && (lifecycle instanceof Context)) {
                stopIdleThreads((Context) lifecycle);
            }
        } catch (Exception e) {
            String msg = sm.getString("threadLocalLeakPreventionListener.lifecycleEvent.error", event);
            log.error(msg, e);
        }
    }

    @Override // org.apache.catalina.core.FrameworkListener, org.apache.catalina.ContainerListener
    public void containerEvent(ContainerEvent event) {
        try {
            super.containerEvent(event);
        } catch (Exception e) {
            String msg = sm.getString("threadLocalLeakPreventionListener.containerEvent.error", event);
            log.error(msg, e);
        }
    }

    private void stopIdleThreads(Context context) {
        if (this.serverStopping) {
            return;
        }
        if (!(context instanceof StandardContext) || !((StandardContext) context).getRenewThreadsWhenStoppingContext()) {
            log.debug("Not renewing threads when the context is stopping. It is not configured to do it.");
            return;
        }
        Engine engine = (Engine) context.getParent().getParent();
        Service service = engine.getService();
        Connector[] connectors = service.findConnectors();
        if (connectors != null) {
            for (Connector connector : connectors) {
                ProtocolHandler handler = connector.getProtocolHandler();
                Executor executor = null;
                if (handler != null) {
                    executor = handler.getExecutor();
                }
                if (executor instanceof ThreadPoolExecutor) {
                    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
                    threadPoolExecutor.contextStopping();
                } else if (executor instanceof StandardThreadExecutor) {
                    StandardThreadExecutor stdThreadExecutor = (StandardThreadExecutor) executor;
                    stdThreadExecutor.contextStopping();
                }
            }
        }
    }

    @Override // org.apache.catalina.core.FrameworkListener
    protected LifecycleListener createLifecycleListener(Context context) {
        return this;
    }
}
