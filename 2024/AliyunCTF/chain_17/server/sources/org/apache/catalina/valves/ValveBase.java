package org.apache.catalina.valves;

import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Valve;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.catalina.util.ToStringUtil;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/ValveBase.class */
public abstract class ValveBase extends LifecycleMBeanBase implements Contained, Valve {
    protected static final StringManager sm = StringManager.getManager((Class<?>) ValveBase.class);
    protected boolean asyncSupported;
    protected Container container;
    protected Log containerLog;
    protected Valve next;

    public ValveBase() {
        this(false);
    }

    public ValveBase(boolean asyncSupported) {
        this.container = null;
        this.containerLog = null;
        this.next = null;
        this.asyncSupported = asyncSupported;
    }

    public Container getContainer() {
        return this.container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    @Override // org.apache.catalina.Valve
    public boolean isAsyncSupported() {
        return this.asyncSupported;
    }

    public void setAsyncSupported(boolean asyncSupported) {
        this.asyncSupported = asyncSupported;
    }

    @Override // org.apache.catalina.Valve
    public Valve getNext() {
        return this.next;
    }

    @Override // org.apache.catalina.Valve
    public void setNext(Valve valve) {
        this.next = valve;
    }

    public void backgroundProcess() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
        super.initInternal();
        this.containerLog = getContainer().getLogger();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        setState(LifecycleState.STARTING);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleBase
    public synchronized void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
    }

    public String toString() {
        return ToStringUtil.toString(this);
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    public String getObjectNameKeyProperties() {
        StringBuilder name = new StringBuilder("type=Valve");
        Container container = getContainer();
        name.append(container.getMBeanKeyProperties());
        int seq = 0;
        Pipeline p = container.getPipeline();
        if (p != null) {
            for (Valve valve : p.getValves()) {
                if (valve != null) {
                    if (valve == this) {
                        break;
                    }
                    if (valve.getClass() == getClass()) {
                        seq++;
                    }
                }
            }
        }
        if (seq > 0) {
            name.append(",seq=");
            name.append(seq);
        }
        String className = getClass().getName();
        int period = className.lastIndexOf(46);
        if (period >= 0) {
            className = className.substring(period + 1);
        }
        name.append(",name=");
        name.append(className);
        return name.toString();
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    public String getDomainInternal() {
        Container c = getContainer();
        if (c == null) {
            return null;
        }
        return c.getDomain();
    }
}
