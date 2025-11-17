package org.apache.catalina.mbeans;

import java.util.ArrayList;
import java.util.List;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.JmxEnabled;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Valve;
import org.apache.catalina.core.ContainerBase;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.HostConfig;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/mbeans/ContainerMBean.class */
public class ContainerMBean extends BaseCatalinaMBean<ContainerBase> {
    public void addChild(String type, String name) throws MBeanException {
        Container contained = (Container) newInstance(type);
        contained.setName(name);
        if (contained instanceof StandardHost) {
            HostConfig config = new HostConfig();
            contained.addLifecycleListener(config);
        } else if (contained instanceof StandardContext) {
            ContextConfig config2 = new ContextConfig();
            contained.addLifecycleListener(config2);
        }
        boolean oldValue = true;
        ContainerBase container = doGetManagedResource();
        try {
            try {
                oldValue = container.getStartChildren();
                container.setStartChildren(false);
                container.addChild(contained);
                contained.init();
                if (container != null) {
                    container.setStartChildren(oldValue);
                }
            } catch (LifecycleException e) {
                throw new MBeanException(e);
            }
        } catch (Throwable th) {
            if (container != null) {
                container.setStartChildren(oldValue);
            }
            throw th;
        }
    }

    public void removeChild(String name) throws MBeanException {
        if (name != null) {
            Container container = doGetManagedResource();
            Container contained = container.findChild(name);
            container.removeChild(contained);
        }
    }

    public String addValve(String valveType) throws MBeanException {
        Valve valve = (Valve) newInstance(valveType);
        Container container = doGetManagedResource();
        container.getPipeline().addValve(valve);
        if (valve instanceof JmxEnabled) {
            return ((JmxEnabled) valve).getObjectName().toString();
        }
        return null;
    }

    public void removeValve(String valveName) throws MBeanException {
        Container container = doGetManagedResource();
        try {
            ObjectName oname = new ObjectName(valveName);
            if (container != null) {
                Valve[] valves = container.getPipeline().getValves();
                for (Valve valve : valves) {
                    if (valve instanceof JmxEnabled) {
                        ObjectName voname = ((JmxEnabled) valve).getObjectName();
                        if (voname.equals(oname)) {
                            container.getPipeline().removeValve(valve);
                        }
                    }
                }
            }
        } catch (MalformedObjectNameException | NullPointerException e) {
            throw new MBeanException(e);
        }
    }

    public void addLifecycleListener(String type) throws MBeanException {
        LifecycleListener listener = (LifecycleListener) newInstance(type);
        Container container = doGetManagedResource();
        container.addLifecycleListener(listener);
    }

    public void removeLifecycleListeners(String type) throws MBeanException {
        Container container = doGetManagedResource();
        LifecycleListener[] listeners = container.findLifecycleListeners();
        for (LifecycleListener listener : listeners) {
            if (listener.getClass().getName().equals(type)) {
                container.removeLifecycleListener(listener);
            }
        }
    }

    public String[] findLifecycleListenerNames() throws MBeanException {
        Container container = doGetManagedResource();
        List<String> result = new ArrayList<>();
        LifecycleListener[] listeners = container.findLifecycleListeners();
        for (LifecycleListener listener : listeners) {
            result.add(listener.getClass().getName());
        }
        return (String[]) result.toArray(new String[0]);
    }

    public String[] findContainerListenerNames() throws MBeanException {
        Container container = doGetManagedResource();
        List<String> result = new ArrayList<>();
        ContainerListener[] listeners = container.findContainerListeners();
        for (ContainerListener listener : listeners) {
            result.add(listener.getClass().getName());
        }
        return (String[]) result.toArray(new String[0]);
    }
}
