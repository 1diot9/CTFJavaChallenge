package org.apache.catalina.core;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerEvent;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.catalina.Service;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/FrameworkListener.class */
public abstract class FrameworkListener implements LifecycleListener, ContainerListener {
    protected final ConcurrentHashMap<Context, LifecycleListener> contextListeners = new ConcurrentHashMap<>();

    protected abstract LifecycleListener createLifecycleListener(Context context);

    @Override // org.apache.catalina.LifecycleListener
    public void lifecycleEvent(LifecycleEvent event) {
        Lifecycle lifecycle = event.getLifecycle();
        if (Lifecycle.BEFORE_START_EVENT.equals(event.getType()) && (lifecycle instanceof Server)) {
            Server server = (Server) lifecycle;
            registerListenersForServer(server);
        }
    }

    @Override // org.apache.catalina.ContainerListener
    public void containerEvent(ContainerEvent event) {
        String type = event.getType();
        if (Container.ADD_CHILD_EVENT.equals(type)) {
            processContainerAddChild((Container) event.getData());
        } else if (Container.REMOVE_CHILD_EVENT.equals(type)) {
            processContainerRemoveChild((Container) event.getData());
        }
    }

    protected void registerListenersForServer(Server server) {
        for (Service service : server.findServices()) {
            Engine engine = service.getContainer();
            if (engine != null) {
                engine.addContainerListener(this);
                registerListenersForEngine(engine);
            }
        }
    }

    protected void registerListenersForEngine(Engine engine) {
        for (Container hostContainer : engine.findChildren()) {
            Host host = (Host) hostContainer;
            host.addContainerListener(this);
            registerListenersForHost(host);
        }
    }

    protected void registerListenersForHost(Host host) {
        for (Container contextContainer : host.findChildren()) {
            Context context = (Context) contextContainer;
            registerContextListener(context);
        }
    }

    protected void registerContextListener(Context context) {
        LifecycleListener listener = createLifecycleListener(context);
        this.contextListeners.put(context, listener);
        context.addLifecycleListener(listener);
    }

    protected void processContainerAddChild(Container child) {
        if (child instanceof Context) {
            registerContextListener((Context) child);
        } else if (child instanceof Engine) {
            registerListenersForEngine((Engine) child);
        } else if (child instanceof Host) {
            registerListenersForHost((Host) child);
        }
    }

    protected void processContainerRemoveChild(Container child) {
        if (child instanceof Context) {
            LifecycleListener listener = this.contextListeners.remove(child);
            if (listener != null) {
                child.removeLifecycleListener(listener);
                return;
            }
            return;
        }
        if ((child instanceof Host) || (child instanceof Engine)) {
            child.removeContainerListener(this);
        }
    }
}
