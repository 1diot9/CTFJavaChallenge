package org.apache.catalina.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.management.ObjectName;
import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.JmxEnabled;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Valve;
import org.apache.catalina.util.LifecycleBase;
import org.apache.catalina.util.ToStringUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/StandardPipeline.class */
public class StandardPipeline extends LifecycleBase implements Pipeline {
    private static final Log log = LogFactory.getLog((Class<?>) StandardPipeline.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) StandardPipeline.class);
    protected Valve basic;
    protected Container container;
    protected Valve first;

    public StandardPipeline() {
        this(null);
    }

    public StandardPipeline(Container container) {
        this.basic = null;
        this.container = null;
        this.first = null;
        setContainer(container);
    }

    @Override // org.apache.catalina.Pipeline
    public boolean isAsyncSupported() {
        boolean supported = true;
        for (Valve valve = this.first != null ? this.first : this.basic; supported && valve != null; valve = valve.getNext()) {
            supported &= valve.isAsyncSupported();
        }
        return supported;
    }

    @Override // org.apache.catalina.Pipeline
    public void findNonAsyncValves(Set<String> result) {
        Valve valve = this.first != null ? this.first : this.basic;
        while (true) {
            Valve valve2 = valve;
            if (valve2 != null) {
                if (!valve2.isAsyncSupported()) {
                    result.add(valve2.getClass().getName());
                }
                valve = valve2.getNext();
            } else {
                return;
            }
        }
    }

    @Override // org.apache.catalina.Contained
    public Container getContainer() {
        return this.container;
    }

    @Override // org.apache.catalina.Contained
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void initInternal() {
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected synchronized void startInternal() throws LifecycleException {
        Valve current = this.first;
        if (current == null) {
            current = this.basic;
        }
        while (current != null) {
            if (current instanceof Lifecycle) {
                ((Lifecycle) current).start();
            }
            current = current.getNext();
        }
        setState(LifecycleState.STARTING);
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected synchronized void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
        Valve current = this.first;
        if (current == null) {
            current = this.basic;
        }
        while (current != null) {
            if (current instanceof Lifecycle) {
                ((Lifecycle) current).stop();
            }
            current = current.getNext();
        }
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void destroyInternal() {
        Valve[] valves = getValves();
        for (Valve valve : valves) {
            removeValve(valve);
        }
    }

    public String toString() {
        return ToStringUtil.toString(this);
    }

    @Override // org.apache.catalina.Pipeline
    public Valve getBasic() {
        return this.basic;
    }

    @Override // org.apache.catalina.Pipeline
    public void setBasic(Valve valve) {
        Valve oldBasic = this.basic;
        if (oldBasic == valve) {
            return;
        }
        if (oldBasic != null) {
            if (getState().isAvailable() && (oldBasic instanceof Lifecycle)) {
                try {
                    ((Lifecycle) oldBasic).stop();
                } catch (LifecycleException e) {
                    log.error(sm.getString("standardPipeline.basic.stop"), e);
                }
            }
            if (oldBasic instanceof Contained) {
                try {
                    ((Contained) oldBasic).setContainer(null);
                } catch (Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                }
            }
        }
        if (valve == null) {
            return;
        }
        if (valve instanceof Contained) {
            ((Contained) valve).setContainer(this.container);
        }
        if (getState().isAvailable() && (valve instanceof Lifecycle)) {
            try {
                ((Lifecycle) valve).start();
            } catch (LifecycleException e2) {
                log.error(sm.getString("standardPipeline.basic.start"), e2);
                return;
            }
        }
        Valve valve2 = this.first;
        while (true) {
            Valve current = valve2;
            if (current == null) {
                break;
            }
            if (current.getNext() == oldBasic) {
                current.setNext(valve);
                break;
            }
            valve2 = current.getNext();
        }
        this.basic = valve;
    }

    @Override // org.apache.catalina.Pipeline
    public void addValve(Valve valve) {
        if (valve instanceof Contained) {
            ((Contained) valve).setContainer(this.container);
        }
        if (getState().isAvailable() && (valve instanceof Lifecycle)) {
            try {
                ((Lifecycle) valve).start();
            } catch (LifecycleException e) {
                log.error(sm.getString("standardPipeline.valve.start"), e);
            }
        }
        if (this.first == null) {
            this.first = valve;
            valve.setNext(this.basic);
        } else {
            Valve valve2 = this.first;
            while (true) {
                Valve current = valve2;
                if (current == null) {
                    break;
                }
                if (current.getNext() == this.basic) {
                    current.setNext(valve);
                    valve.setNext(this.basic);
                    break;
                }
                valve2 = current.getNext();
            }
        }
        this.container.fireContainerEvent(Container.ADD_VALVE_EVENT, valve);
    }

    @Override // org.apache.catalina.Pipeline
    public Valve[] getValves() {
        List<Valve> valveList = new ArrayList<>();
        Valve current = this.first;
        if (current == null) {
            current = this.basic;
        }
        while (current != null) {
            valveList.add(current);
            current = current.getNext();
        }
        return (Valve[]) valveList.toArray(new Valve[0]);
    }

    public ObjectName[] getValveObjectNames() {
        List<ObjectName> valveList = new ArrayList<>();
        Valve current = this.first;
        if (current == null) {
            current = this.basic;
        }
        while (current != null) {
            if (current instanceof JmxEnabled) {
                valveList.add(((JmxEnabled) current).getObjectName());
            }
            current = current.getNext();
        }
        return (ObjectName[]) valveList.toArray(new ObjectName[0]);
    }

    @Override // org.apache.catalina.Pipeline
    public void removeValve(Valve valve) {
        Valve valve2;
        if (this.first == valve) {
            this.first = this.first.getNext();
            valve2 = null;
        } else {
            valve2 = this.first;
        }
        while (true) {
            Valve current = valve2;
            if (current == null) {
                break;
            }
            if (current.getNext() == valve) {
                current.setNext(valve.getNext());
                break;
            }
            valve2 = current.getNext();
        }
        if (this.first == this.basic) {
            this.first = null;
        }
        if (valve instanceof Contained) {
            ((Contained) valve).setContainer(null);
        }
        if (valve instanceof Lifecycle) {
            if (getState().isAvailable()) {
                try {
                    ((Lifecycle) valve).stop();
                } catch (LifecycleException e) {
                    log.error(sm.getString("standardPipeline.valve.stop"), e);
                }
            }
            try {
                ((Lifecycle) valve).destroy();
            } catch (LifecycleException e2) {
                log.error(sm.getString("standardPipeline.valve.destroy"), e2);
            }
        }
        this.container.fireContainerEvent(Container.REMOVE_VALVE_EVENT, valve);
    }

    @Override // org.apache.catalina.Pipeline
    public Valve getFirst() {
        if (this.first != null) {
            return this.first;
        }
        return this.basic;
    }
}
