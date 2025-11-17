package org.springframework.web.context.request;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/AbstractRequestAttributes.class */
public abstract class AbstractRequestAttributes implements RequestAttributes {
    protected final Map<String, Runnable> requestDestructionCallbacks = new LinkedHashMap(8);
    private volatile boolean requestActive = true;

    protected abstract void updateAccessedSessionAttributes();

    public void requestCompleted() {
        executeRequestDestructionCallbacks();
        updateAccessedSessionAttributes();
        this.requestActive = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean isRequestActive() {
        return this.requestActive;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void registerRequestDestructionCallback(String name, Runnable callback) {
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(callback, "Callback must not be null");
        synchronized (this.requestDestructionCallbacks) {
            this.requestDestructionCallbacks.put(name, callback);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void removeRequestDestructionCallback(String name) {
        Assert.notNull(name, "Name must not be null");
        synchronized (this.requestDestructionCallbacks) {
            this.requestDestructionCallbacks.remove(name);
        }
    }

    private void executeRequestDestructionCallbacks() {
        synchronized (this.requestDestructionCallbacks) {
            for (Runnable runnable : this.requestDestructionCallbacks.values()) {
                runnable.run();
            }
            this.requestDestructionCallbacks.clear();
        }
    }
}
