package org.springframework.aop.target.dynamic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.TargetSource;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/target/dynamic/AbstractRefreshableTargetSource.class */
public abstract class AbstractRefreshableTargetSource implements TargetSource, Refreshable {

    @Nullable
    protected Object targetObject;
    protected final Log logger = LogFactory.getLog(getClass());
    private long refreshCheckDelay = -1;
    private long lastRefreshCheck = -1;
    private long lastRefreshTime = -1;
    private long refreshCount = 0;

    protected abstract Object freshTarget();

    public void setRefreshCheckDelay(long refreshCheckDelay) {
        this.refreshCheckDelay = refreshCheckDelay;
    }

    @Override // org.springframework.aop.TargetSource, org.springframework.aop.TargetClassAware
    public synchronized Class<?> getTargetClass() {
        if (this.targetObject == null) {
            refresh();
        }
        return this.targetObject.getClass();
    }

    @Override // org.springframework.aop.TargetSource
    @Nullable
    public final synchronized Object getTarget() {
        if ((refreshCheckDelayElapsed() && requiresRefresh()) || this.targetObject == null) {
            refresh();
        }
        return this.targetObject;
    }

    @Override // org.springframework.aop.target.dynamic.Refreshable
    public final synchronized void refresh() {
        this.logger.debug("Attempting to refresh target");
        this.targetObject = freshTarget();
        this.refreshCount++;
        this.lastRefreshTime = System.currentTimeMillis();
        this.logger.debug("Target refreshed successfully");
    }

    @Override // org.springframework.aop.target.dynamic.Refreshable
    public synchronized long getRefreshCount() {
        return this.refreshCount;
    }

    @Override // org.springframework.aop.target.dynamic.Refreshable
    public synchronized long getLastRefreshTime() {
        return this.lastRefreshTime;
    }

    private boolean refreshCheckDelayElapsed() {
        if (this.refreshCheckDelay < 0) {
            return false;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (this.lastRefreshCheck < 0 || currentTimeMillis - this.lastRefreshCheck > this.refreshCheckDelay) {
            this.lastRefreshCheck = currentTimeMillis;
            this.logger.debug("Refresh check delay elapsed - checking whether refresh is required");
            return true;
        }
        return false;
    }

    protected boolean requiresRefresh() {
        return true;
    }
}
