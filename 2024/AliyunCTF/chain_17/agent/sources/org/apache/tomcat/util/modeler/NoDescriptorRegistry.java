package org.apache.tomcat.util.modeler;

import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.loading.ClassLoaderRepository;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/modeler/NoDescriptorRegistry.class */
public class NoDescriptorRegistry extends Registry {
    private final MBeanServer mBeanServer = new NoJmxMBeanServer();
    private final ManagedBean defaultMBean = new PassthroughMBean();

    @Override // org.apache.tomcat.util.modeler.Registry, org.apache.tomcat.util.modeler.RegistryMBean
    public void registerComponent(Object bean, String oname, String type) throws Exception {
    }

    @Override // org.apache.tomcat.util.modeler.Registry, org.apache.tomcat.util.modeler.RegistryMBean
    public void unregisterComponent(String oname) {
    }

    @Override // org.apache.tomcat.util.modeler.Registry, org.apache.tomcat.util.modeler.RegistryMBean
    public void invoke(List<ObjectName> mbeans, String operation, boolean failFirst) throws Exception {
    }

    @Override // org.apache.tomcat.util.modeler.Registry, org.apache.tomcat.util.modeler.RegistryMBean
    public int getId(String domain, String name) {
        return 0;
    }

    @Override // org.apache.tomcat.util.modeler.Registry
    public void addManagedBean(ManagedBean bean) {
    }

    @Override // org.apache.tomcat.util.modeler.Registry
    public ManagedBean findManagedBean(String name) {
        return this.defaultMBean;
    }

    @Override // org.apache.tomcat.util.modeler.Registry
    public String getType(ObjectName oname, String attName) {
        return null;
    }

    @Override // org.apache.tomcat.util.modeler.Registry
    public MBeanOperationInfo getMethodInfo(ObjectName oname, String opName) {
        return null;
    }

    @Override // org.apache.tomcat.util.modeler.Registry
    public ManagedBean findManagedBean(Object bean, Class<?> beanClass, String type) throws Exception {
        return null;
    }

    @Override // org.apache.tomcat.util.modeler.Registry
    public List<ObjectName> load(String sourceType, Object source, String param) throws Exception {
        return Collections.emptyList();
    }

    @Override // org.apache.tomcat.util.modeler.Registry
    public void loadDescriptors(String packageName, ClassLoader classLoader) {
    }

    @Override // org.apache.tomcat.util.modeler.Registry
    public void registerComponent(Object bean, ObjectName oname, String type) throws Exception {
    }

    @Override // org.apache.tomcat.util.modeler.Registry
    public void unregisterComponent(ObjectName oname) {
    }

    @Override // org.apache.tomcat.util.modeler.Registry
    public MBeanServer getMBeanServer() {
        return this.mBeanServer;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/modeler/NoDescriptorRegistry$NoJmxMBeanServer.class */
    private static class NoJmxMBeanServer implements MBeanServer {
        private NoJmxMBeanServer() {
        }

        public ObjectInstance createMBean(String className, ObjectName name) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MBeanRegistrationException {
            return null;
        }

        public ObjectInstance createMBean(String className, ObjectName name, ObjectName loaderName) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, InstanceNotFoundException, MBeanRegistrationException {
            return null;
        }

        public ObjectInstance createMBean(String className, ObjectName name, Object[] params, String[] signature) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MBeanRegistrationException {
            return null;
        }

        public ObjectInstance createMBean(String className, ObjectName name, ObjectName loaderName, Object[] params, String[] signature) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, InstanceNotFoundException, MBeanRegistrationException {
            return null;
        }

        public ObjectInstance registerMBean(Object object, ObjectName name) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
            return null;
        }

        public void unregisterMBean(ObjectName name) throws InstanceNotFoundException, MBeanRegistrationException {
        }

        public ObjectInstance getObjectInstance(ObjectName name) throws InstanceNotFoundException {
            return null;
        }

        public Set<ObjectInstance> queryMBeans(ObjectName name, QueryExp query) {
            return Collections.emptySet();
        }

        public Set<ObjectName> queryNames(ObjectName name, QueryExp query) {
            return Collections.emptySet();
        }

        public boolean isRegistered(ObjectName name) {
            return false;
        }

        public Integer getMBeanCount() {
            return null;
        }

        public Object getAttribute(ObjectName name, String attribute) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
            return null;
        }

        public AttributeList getAttributes(ObjectName name, String[] attributes) throws InstanceNotFoundException, ReflectionException {
            return null;
        }

        public void setAttribute(ObjectName name, Attribute attribute) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        }

        public AttributeList setAttributes(ObjectName name, AttributeList attributes) throws InstanceNotFoundException, ReflectionException {
            return null;
        }

        public Object invoke(ObjectName name, String operationName, Object[] params, String[] signature) throws InstanceNotFoundException, MBeanException, ReflectionException {
            return null;
        }

        public String getDefaultDomain() {
            return null;
        }

        public String[] getDomains() {
            return new String[0];
        }

        public void addNotificationListener(ObjectName name, NotificationListener listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException {
        }

        public void addNotificationListener(ObjectName name, ObjectName listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException {
        }

        public void removeNotificationListener(ObjectName name, ObjectName listener) throws InstanceNotFoundException, ListenerNotFoundException {
        }

        public void removeNotificationListener(ObjectName name, ObjectName listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException, ListenerNotFoundException {
        }

        public void removeNotificationListener(ObjectName name, NotificationListener listener) throws InstanceNotFoundException, ListenerNotFoundException {
        }

        public void removeNotificationListener(ObjectName name, NotificationListener listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException, ListenerNotFoundException {
        }

        public MBeanInfo getMBeanInfo(ObjectName name) throws InstanceNotFoundException, IntrospectionException, ReflectionException {
            return null;
        }

        public boolean isInstanceOf(ObjectName name, String className) throws InstanceNotFoundException {
            return false;
        }

        public Object instantiate(String className) throws ReflectionException, MBeanException {
            return null;
        }

        public Object instantiate(String className, ObjectName loaderName) throws ReflectionException, MBeanException, InstanceNotFoundException {
            return null;
        }

        public Object instantiate(String className, Object[] params, String[] signature) throws ReflectionException, MBeanException {
            return null;
        }

        public Object instantiate(String className, ObjectName loaderName, Object[] params, String[] signature) throws ReflectionException, MBeanException, InstanceNotFoundException {
            return null;
        }

        public ObjectInputStream deserialize(ObjectName name, byte[] data) throws InstanceNotFoundException, OperationsException {
            return null;
        }

        public ObjectInputStream deserialize(String className, byte[] data) throws OperationsException, ReflectionException {
            return null;
        }

        public ObjectInputStream deserialize(String className, ObjectName loaderName, byte[] data) throws InstanceNotFoundException, OperationsException, ReflectionException {
            return null;
        }

        public ClassLoader getClassLoaderFor(ObjectName mbeanName) throws InstanceNotFoundException {
            return null;
        }

        public ClassLoader getClassLoader(ObjectName loaderName) throws InstanceNotFoundException {
            return null;
        }

        public ClassLoaderRepository getClassLoaderRepository() {
            return null;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/modeler/NoDescriptorRegistry$PassthroughMBean.class */
    private static class PassthroughMBean extends ManagedBean {
        private static final long serialVersionUID = 1;

        private PassthroughMBean() {
        }
    }
}
