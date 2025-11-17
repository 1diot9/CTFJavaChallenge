package org.apache.catalina.core;

import org.apache.catalina.Executor;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.threads.VirtualThreadExecutor;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/StandardVirtualThreadExecutor.class */
public class StandardVirtualThreadExecutor extends LifecycleMBeanBase implements Executor {
    private static final StringManager sm = StringManager.getManager((Class<?>) StandardVirtualThreadExecutor.class);
    private String name;
    private java.util.concurrent.Executor executor;
    private String namePrefix = "tomcat-virt-";

    public void setName(String name) {
        this.name = name;
    }

    @Override // org.apache.catalina.Executor
    public String getName() {
        return this.name;
    }

    public String getNamePrefix() {
        return this.namePrefix;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    @Override // java.util.concurrent.Executor
    public void execute(Runnable command) {
        if (this.executor == null) {
            throw new IllegalStateException(sm.getString("standardVirtualThreadExecutor.notStarted"));
        }
        this.executor.execute(command);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
        super.initInternal();
        if (!JreCompat.isJre21Available()) {
            throw new LifecycleException(sm.getString("standardVirtualThreadExecutor.noVirtualThreads"));
        }
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void startInternal() throws LifecycleException {
        this.executor = new VirtualThreadExecutor(getNamePrefix());
        setState(LifecycleState.STARTING);
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void stopInternal() throws LifecycleException {
        this.executor = null;
        setState(LifecycleState.STOPPING);
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getDomainInternal() {
        return null;
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getObjectNameKeyProperties() {
        return "type=Executor,name=" + getName();
    }
}
