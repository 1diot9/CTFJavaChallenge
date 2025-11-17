package org.apache.tomcat.util.modeler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import javax.management.Attribute;
import javax.management.AttributeChangeNotification;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.management.modelmbean.ModelMBeanNotificationBroadcaster;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/modeler/BaseModelMBean.class */
public class BaseModelMBean implements DynamicMBean, MBeanRegistration, ModelMBeanNotificationBroadcaster {
    protected ObjectName oname = null;
    protected BaseNotificationBroadcaster attributeBroadcaster = null;
    protected BaseNotificationBroadcaster generalBroadcaster = null;
    protected ManagedBean managedBean = null;
    protected Object resource = null;
    protected String resourceType = null;
    private static final Log log = LogFactory.getLog((Class<?>) BaseModelMBean.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) BaseModelMBean.class);
    static final Object[] NO_ARGS_PARAM = new Object[0];

    public Object getAttribute(String name) throws AttributeNotFoundException, MBeanException, ReflectionException {
        Object result;
        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("baseModelMBean.nullAttributeName")), sm.getString("baseModelMBean.nullAttributeName"));
        }
        if ((this.resource instanceof DynamicMBean) && !(this.resource instanceof BaseModelMBean)) {
            return ((DynamicMBean) this.resource).getAttribute(name);
        }
        Method m = this.managedBean.getGetter(name, this, this.resource);
        try {
            Class<?> declaring = m.getDeclaringClass();
            if (declaring.isAssignableFrom(getClass())) {
                result = m.invoke(this, NO_ARGS_PARAM);
            } else {
                result = m.invoke(this.resource, NO_ARGS_PARAM);
            }
            return result;
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t == null) {
                t = e;
            }
            if (t instanceof RuntimeException) {
                throw new RuntimeOperationsException((RuntimeException) t, sm.getString("baseModelMBean.invokeError", name));
            }
            if (t instanceof Error) {
                throw new RuntimeErrorException((Error) t, sm.getString("baseModelMBean.invokeError", name));
            }
            throw new MBeanException(e, sm.getString("baseModelMBean.invokeError", name));
        } catch (Exception e2) {
            throw new MBeanException(e2, sm.getString("baseModelMBean.invokeError", name));
        }
    }

    public AttributeList getAttributes(String[] names) {
        if (names == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("baseModelMBean.nullAttributeNameList")), sm.getString("baseModelMBean.nullAttributeNameList"));
        }
        AttributeList response = new AttributeList();
        for (String name : names) {
            try {
                response.add(new Attribute(name, getAttribute(name)));
            } catch (Exception e) {
            }
        }
        return response;
    }

    public void setManagedBean(ManagedBean managedBean) {
        this.managedBean = managedBean;
    }

    public MBeanInfo getMBeanInfo() {
        return this.managedBean.getMBeanInfo();
    }

    public Object invoke(String name, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        Object result;
        if ((this.resource instanceof DynamicMBean) && !(this.resource instanceof BaseModelMBean)) {
            return ((DynamicMBean) this.resource).invoke(name, params, signature);
        }
        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("baseModelMBean.nullMethodName")), sm.getString("baseModelMBean.nullMethodName"));
        }
        if (log.isDebugEnabled()) {
            log.debug("Invoke " + name);
        }
        Method method = this.managedBean.getInvoke(name, params, signature, this, this.resource);
        try {
            if (method.getDeclaringClass().isAssignableFrom(getClass())) {
                result = method.invoke(this, params);
            } else {
                result = method.invoke(this.resource, params);
            }
            return result;
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            log.error(sm.getString("baseModelMBean.invokeError", name), t);
            if (t == null) {
                t = e;
            }
            if (t instanceof RuntimeException) {
                throw new RuntimeOperationsException((RuntimeException) t, sm.getString("baseModelMBean.invokeError", name));
            }
            if (t instanceof Error) {
                throw new RuntimeErrorException((Error) t, sm.getString("baseModelMBean.invokeError", name));
            }
            throw new MBeanException((Exception) t, sm.getString("baseModelMBean.invokeError", name));
        } catch (Exception e2) {
            log.error(sm.getString("baseModelMBean.invokeError", name), e2);
            throw new MBeanException(e2, sm.getString("baseModelMBean.invokeError", name));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Class<?> getAttributeClass(String signature) throws ReflectionException {
        if (signature.equals(Boolean.TYPE.getName())) {
            return Boolean.TYPE;
        }
        if (signature.equals(Byte.TYPE.getName())) {
            return Byte.TYPE;
        }
        if (signature.equals(Character.TYPE.getName())) {
            return Character.TYPE;
        }
        if (signature.equals(Double.TYPE.getName())) {
            return Double.TYPE;
        }
        if (signature.equals(Float.TYPE.getName())) {
            return Float.TYPE;
        }
        if (signature.equals(Integer.TYPE.getName())) {
            return Integer.TYPE;
        }
        if (signature.equals(Long.TYPE.getName())) {
            return Long.TYPE;
        }
        if (signature.equals(Short.TYPE.getName())) {
            return Short.TYPE;
        }
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl != null) {
                return cl.loadClass(signature);
            }
        } catch (ClassNotFoundException e) {
        }
        try {
            return Class.forName(signature);
        } catch (ClassNotFoundException e2) {
            throw new ReflectionException(e2, sm.getString("baseModelMBean.cnfeForSignature", signature));
        }
    }

    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (log.isDebugEnabled()) {
            log.debug("Setting attribute " + this + " " + attribute);
        }
        if ((this.resource instanceof DynamicMBean) && !(this.resource instanceof BaseModelMBean)) {
            try {
                ((DynamicMBean) this.resource).setAttribute(attribute);
                return;
            } catch (InvalidAttributeValueException e) {
                throw new MBeanException(e);
            }
        }
        if (attribute == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("baseModelMBean.nullAttribute")), sm.getString("baseModelMBean.nullAttribute"));
        }
        String name = attribute.getName();
        Object value = attribute.getValue();
        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("baseModelMBean.nullAttributeName")), sm.getString("baseModelMBean.nullAttributeName"));
        }
        Method m = this.managedBean.getSetter(name, this, this.resource);
        try {
            if (m.getDeclaringClass().isAssignableFrom(getClass())) {
                m.invoke(this, value);
            } else {
                m.invoke(this.resource, value);
            }
            try {
                sendAttributeChangeNotification(new Attribute(name, (Object) null), attribute);
            } catch (Exception ex) {
                log.error(sm.getString("baseModelMBean.notificationError", name), ex);
            }
        } catch (InvocationTargetException e2) {
            Throwable t = e2.getTargetException();
            if (t == null) {
                t = e2;
            }
            if (t instanceof RuntimeException) {
                throw new RuntimeOperationsException((RuntimeException) t, sm.getString("baseModelMBean.invokeError", name));
            }
            if (t instanceof Error) {
                throw new RuntimeErrorException((Error) t, sm.getString("baseModelMBean.invokeError", name));
            }
            throw new MBeanException(e2, sm.getString("baseModelMBean.invokeError", name));
        } catch (Exception e3) {
            log.error(sm.getString("baseModelMBean.invokeError", name), e3);
            throw new MBeanException(e3, sm.getString("baseModelMBean.invokeError", name));
        }
    }

    public String toString() {
        if (this.resource == null) {
            return "BaseModelMbean[" + this.resourceType + "]";
        }
        return this.resource.toString();
    }

    public AttributeList setAttributes(AttributeList attributes) {
        AttributeList response = new AttributeList();
        if (attributes == null) {
            return response;
        }
        String[] names = new String[attributes.size()];
        int n = 0;
        Iterator it = attributes.iterator();
        while (it.hasNext()) {
            Object attribute = it.next();
            Attribute item = (Attribute) attribute;
            int i = n;
            n++;
            names[i] = item.getName();
            try {
                setAttribute(item);
            } catch (Exception e) {
            }
        }
        return getAttributes(names);
    }

    public Object getManagedResource() throws InstanceNotFoundException, InvalidTargetObjectTypeException, MBeanException, RuntimeOperationsException {
        if (this.resource == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("baseModelMBean.nullResource")), sm.getString("baseModelMBean.nullResource"));
        }
        return this.resource;
    }

    public void setManagedResource(Object resource, String type) throws InstanceNotFoundException, MBeanException, RuntimeOperationsException {
        if (resource == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("baseModelMBean.nullResource")), sm.getString("baseModelMBean.nullResource"));
        }
        this.resource = resource;
        this.resourceType = resource.getClass().getName();
    }

    public void addAttributeChangeNotificationListener(NotificationListener listener, String name, Object handback) throws IllegalArgumentException {
        if (listener == null) {
            throw new IllegalArgumentException(sm.getString("baseModelMBean.nullListener"));
        }
        if (this.attributeBroadcaster == null) {
            this.attributeBroadcaster = new BaseNotificationBroadcaster();
        }
        if (log.isDebugEnabled()) {
            log.debug("addAttributeNotificationListener " + listener);
        }
        BaseAttributeFilter filter = new BaseAttributeFilter(name);
        this.attributeBroadcaster.addNotificationListener(listener, filter, handback);
    }

    public void removeAttributeChangeNotificationListener(NotificationListener listener, String name) throws ListenerNotFoundException {
        if (listener == null) {
            throw new IllegalArgumentException(sm.getString("baseModelMBean.nullListener"));
        }
        if (this.attributeBroadcaster != null) {
            this.attributeBroadcaster.removeNotificationListener(listener);
        }
    }

    public void sendAttributeChangeNotification(AttributeChangeNotification notification) throws MBeanException, RuntimeOperationsException {
        if (notification == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("baseModelMBean.nullNotification")), sm.getString("baseModelMBean.nullNotification"));
        }
        if (this.attributeBroadcaster == null) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("AttributeChangeNotification " + notification);
        }
        this.attributeBroadcaster.sendNotification(notification);
    }

    public void sendAttributeChangeNotification(Attribute oldValue, Attribute newValue) throws MBeanException, RuntimeOperationsException {
        String type;
        if (newValue.getValue() != null) {
            type = newValue.getValue().getClass().getName();
        } else if (oldValue.getValue() != null) {
            type = oldValue.getValue().getClass().getName();
        } else {
            return;
        }
        AttributeChangeNotification notification = new AttributeChangeNotification(this, 1L, System.currentTimeMillis(), "Attribute value has changed", oldValue.getName(), type, oldValue.getValue(), newValue.getValue());
        sendAttributeChangeNotification(notification);
    }

    public void sendNotification(Notification notification) throws MBeanException, RuntimeOperationsException {
        if (notification == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("baseModelMBean.nullNotification")), sm.getString("baseModelMBean.nullNotification"));
        }
        if (this.generalBroadcaster == null) {
            return;
        }
        this.generalBroadcaster.sendNotification(notification);
    }

    public void sendNotification(String message) throws MBeanException, RuntimeOperationsException {
        if (message == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("baseModelMBean.nullMessage")), sm.getString("baseModelMBean.nullMessage"));
        }
        Notification notification = new Notification("jmx.modelmbean.generic", this, 1L, message);
        sendNotification(notification);
    }

    public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws IllegalArgumentException {
        if (listener == null) {
            throw new IllegalArgumentException(sm.getString("baseModelMBean.nullListener"));
        }
        if (log.isDebugEnabled()) {
            log.debug("addNotificationListener " + listener);
        }
        if (this.generalBroadcaster == null) {
            this.generalBroadcaster = new BaseNotificationBroadcaster();
        }
        this.generalBroadcaster.addNotificationListener(listener, filter, handback);
        if (this.attributeBroadcaster == null) {
            this.attributeBroadcaster = new BaseNotificationBroadcaster();
        }
        if (log.isDebugEnabled()) {
            log.debug("addAttributeNotificationListener " + listener);
        }
        this.attributeBroadcaster.addNotificationListener(listener, filter, handback);
    }

    public MBeanNotificationInfo[] getNotificationInfo() {
        MBeanNotificationInfo[] current = getMBeanInfo().getNotifications();
        MBeanNotificationInfo[] response = new MBeanNotificationInfo[current.length + 2];
        response[0] = new MBeanNotificationInfo(new String[]{"jmx.modelmbean.generic"}, "GENERIC", "Text message notification from the managed resource");
        response[1] = new MBeanNotificationInfo(new String[]{"jmx.attribute.change"}, "ATTRIBUTE_CHANGE", "Observed MBean attribute value has changed");
        System.arraycopy(current, 0, response, 2, current.length);
        return response;
    }

    public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
        if (listener == null) {
            throw new IllegalArgumentException(sm.getString("baseModelMBean.nullListener"));
        }
        if (this.generalBroadcaster != null) {
            this.generalBroadcaster.removeNotificationListener(listener);
        }
        if (this.attributeBroadcaster != null) {
            this.attributeBroadcaster.removeNotificationListener(listener);
        }
    }

    public String getModelerType() {
        return this.resourceType;
    }

    public String getClassName() {
        return getModelerType();
    }

    public ObjectName getJmxName() {
        return this.oname;
    }

    public String getObjectName() {
        if (this.oname != null) {
            return this.oname.toString();
        }
        return null;
    }

    public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("preRegister " + this.resource + " " + name);
        }
        this.oname = name;
        if (this.resource instanceof MBeanRegistration) {
            this.oname = ((MBeanRegistration) this.resource).preRegister(server, name);
        }
        return this.oname;
    }

    public void postRegister(Boolean registrationDone) {
        if (this.resource instanceof MBeanRegistration) {
            ((MBeanRegistration) this.resource).postRegister(registrationDone);
        }
    }

    public void preDeregister() throws Exception {
        if (this.resource instanceof MBeanRegistration) {
            ((MBeanRegistration) this.resource).preDeregister();
        }
    }

    public void postDeregister() {
        if (this.resource instanceof MBeanRegistration) {
            ((MBeanRegistration) this.resource).postDeregister();
        }
    }
}
