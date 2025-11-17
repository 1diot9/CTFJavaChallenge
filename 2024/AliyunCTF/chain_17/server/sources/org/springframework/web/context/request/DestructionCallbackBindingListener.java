package org.springframework.web.context.request;

import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;
import java.io.Serializable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/DestructionCallbackBindingListener.class */
public class DestructionCallbackBindingListener implements HttpSessionBindingListener, Serializable {
    private final Runnable destructionCallback;

    public DestructionCallbackBindingListener(Runnable destructionCallback) {
        this.destructionCallback = destructionCallback;
    }

    @Override // jakarta.servlet.http.HttpSessionBindingListener
    public void valueBound(HttpSessionBindingEvent event) {
    }

    @Override // jakarta.servlet.http.HttpSessionBindingListener
    public void valueUnbound(HttpSessionBindingEvent event) {
        this.destructionCallback.run();
    }
}
