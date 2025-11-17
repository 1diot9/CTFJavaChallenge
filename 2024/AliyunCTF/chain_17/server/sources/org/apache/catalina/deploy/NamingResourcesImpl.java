package org.apache.catalina.deploy;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.naming.NamingException;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.JmxEnabled;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Server;
import org.apache.catalina.mbeans.MBeanUtils;
import org.apache.catalina.util.Introspection;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.naming.ContextBindings;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.descriptor.web.ContextEjb;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.ContextLocalEjb;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.ContextResourceEnvRef;
import org.apache.tomcat.util.descriptor.web.ContextResourceLink;
import org.apache.tomcat.util.descriptor.web.ContextService;
import org.apache.tomcat.util.descriptor.web.ContextTransaction;
import org.apache.tomcat.util.descriptor.web.InjectionTarget;
import org.apache.tomcat.util.descriptor.web.MessageDestinationRef;
import org.apache.tomcat.util.descriptor.web.NamingResources;
import org.apache.tomcat.util.descriptor.web.ResourceBase;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/deploy/NamingResourcesImpl.class */
public class NamingResourcesImpl extends LifecycleMBeanBase implements Serializable, NamingResources {
    private static final long serialVersionUID = 1;
    private static final Log log = LogFactory.getLog((Class<?>) NamingResourcesImpl.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) NamingResourcesImpl.class);
    private volatile boolean resourceRequireExplicitRegistration = false;
    private Object container = null;
    private final Set<String> entries = new HashSet();
    private final Map<String, ContextEjb> ejbs = new HashMap();
    private final Map<String, ContextEnvironment> envs = new HashMap();
    private final Map<String, ContextLocalEjb> localEjbs = new HashMap();
    private final Map<String, MessageDestinationRef> mdrs = new HashMap();
    private final HashMap<String, ContextResourceEnvRef> resourceEnvRefs = new HashMap<>();
    private final HashMap<String, ContextResource> resources = new HashMap<>();
    private final HashMap<String, ContextResourceLink> resourceLinks = new HashMap<>();
    private final HashMap<String, ContextService> services = new HashMap<>();
    private ContextTransaction transaction = null;
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);

    @Override // org.apache.tomcat.util.descriptor.web.NamingResources
    public Object getContainer() {
        return this.container;
    }

    public void setContainer(Object container) {
        this.container = container;
    }

    public void setTransaction(ContextTransaction transaction) {
        this.transaction = transaction;
    }

    public ContextTransaction getTransaction() {
        return this.transaction;
    }

    public void addEjb(ContextEjb ejb) {
        String ejbLink = ejb.getLink();
        String lookupName = ejb.getLookupName();
        if (ejbLink != null && ejbLink.length() > 0 && lookupName != null && lookupName.length() > 0) {
            throw new IllegalArgumentException(sm.getString("namingResources.ejbLookupLink", ejb.getName()));
        }
        if (this.entries.contains(ejb.getName())) {
            return;
        }
        this.entries.add(ejb.getName());
        synchronized (this.ejbs) {
            ejb.setNamingResources(this);
            this.ejbs.put(ejb.getName(), ejb);
        }
        this.support.firePropertyChange("ejb", (Object) null, ejb);
    }

    @Override // org.apache.tomcat.util.descriptor.web.NamingResources
    public void addEnvironment(ContextEnvironment environment) {
        if (this.entries.contains(environment.getName())) {
            ContextEnvironment ce = findEnvironment(environment.getName());
            ContextResourceLink rl = findResourceLink(environment.getName());
            if (ce != null) {
                if (ce.getOverride()) {
                    removeEnvironment(environment.getName());
                } else {
                    return;
                }
            } else if (rl != null) {
                NamingResourcesImpl global = getServer().getGlobalNamingResources();
                if (global.findEnvironment(rl.getGlobal()) != null) {
                    if (global.findEnvironment(rl.getGlobal()).getOverride()) {
                        removeResourceLink(environment.getName());
                    } else {
                        return;
                    }
                }
            } else {
                return;
            }
        }
        List<InjectionTarget> injectionTargets = environment.getInjectionTargets();
        String value = environment.getValue();
        String lookupName = environment.getLookupName();
        if (injectionTargets != null && injectionTargets.size() > 0 && (value == null || value.length() == 0)) {
            return;
        }
        if (value != null && value.length() > 0 && lookupName != null && lookupName.length() > 0) {
            throw new IllegalArgumentException(sm.getString("namingResources.envEntryLookupValue", environment.getName()));
        }
        if (!checkResourceType(environment)) {
            throw new IllegalArgumentException(sm.getString("namingResources.resourceTypeFail", environment.getName(), environment.getType()));
        }
        this.entries.add(environment.getName());
        synchronized (this.envs) {
            environment.setNamingResources(this);
            this.envs.put(environment.getName(), environment);
        }
        this.support.firePropertyChange("environment", (Object) null, environment);
        if (this.resourceRequireExplicitRegistration) {
            try {
                MBeanUtils.createMBean(environment);
            } catch (Exception e) {
                log.warn(sm.getString("namingResources.mbeanCreateFail", environment.getName()), e);
            }
        }
    }

    private Server getServer() {
        if (this.container instanceof Server) {
            return (Server) this.container;
        }
        if (this.container instanceof Context) {
            Engine engine = (Engine) ((Context) this.container).getParent().getParent();
            return engine.getService().getServer();
        }
        return null;
    }

    public void addLocalEjb(ContextLocalEjb ejb) {
        if (this.entries.contains(ejb.getName())) {
            return;
        }
        this.entries.add(ejb.getName());
        synchronized (this.localEjbs) {
            ejb.setNamingResources(this);
            this.localEjbs.put(ejb.getName(), ejb);
        }
        this.support.firePropertyChange("localEjb", (Object) null, ejb);
    }

    public void addMessageDestinationRef(MessageDestinationRef mdr) {
        if (this.entries.contains(mdr.getName())) {
            return;
        }
        if (!checkResourceType(mdr)) {
            throw new IllegalArgumentException(sm.getString("namingResources.resourceTypeFail", mdr.getName(), mdr.getType()));
        }
        this.entries.add(mdr.getName());
        synchronized (this.mdrs) {
            mdr.setNamingResources(this);
            this.mdrs.put(mdr.getName(), mdr);
        }
        this.support.firePropertyChange("messageDestinationRef", (Object) null, mdr);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    @Override // org.apache.tomcat.util.descriptor.web.NamingResources
    public void addResource(ContextResource resource) {
        if (this.entries.contains(resource.getName())) {
            return;
        }
        if (!checkResourceType(resource)) {
            throw new IllegalArgumentException(sm.getString("namingResources.resourceTypeFail", resource.getName(), resource.getType()));
        }
        this.entries.add(resource.getName());
        synchronized (this.resources) {
            resource.setNamingResources(this);
            this.resources.put(resource.getName(), resource);
        }
        this.support.firePropertyChange(DefaultBeanDefinitionDocumentReader.RESOURCE_ATTRIBUTE, (Object) null, resource);
        if (this.resourceRequireExplicitRegistration) {
            try {
                MBeanUtils.createMBean(resource);
            } catch (Exception e) {
                log.warn(sm.getString("namingResources.mbeanCreateFail", resource.getName()), e);
            }
        }
    }

    public void addResourceEnvRef(ContextResourceEnvRef resource) {
        if (this.entries.contains(resource.getName())) {
            return;
        }
        if (!checkResourceType(resource)) {
            throw new IllegalArgumentException(sm.getString("namingResources.resourceTypeFail", resource.getName(), resource.getType()));
        }
        this.entries.add(resource.getName());
        synchronized (this.resourceEnvRefs) {
            resource.setNamingResources(this);
            this.resourceEnvRefs.put(resource.getName(), resource);
        }
        this.support.firePropertyChange("resourceEnvRef", (Object) null, resource);
    }

    @Override // org.apache.tomcat.util.descriptor.web.NamingResources
    public void addResourceLink(ContextResourceLink resourceLink) {
        if (this.entries.contains(resourceLink.getName())) {
            return;
        }
        this.entries.add(resourceLink.getName());
        synchronized (this.resourceLinks) {
            resourceLink.setNamingResources(this);
            this.resourceLinks.put(resourceLink.getName(), resourceLink);
        }
        this.support.firePropertyChange("resourceLink", (Object) null, resourceLink);
        if (this.resourceRequireExplicitRegistration) {
            try {
                MBeanUtils.createMBean(resourceLink);
            } catch (Exception e) {
                log.warn(sm.getString("namingResources.mbeanCreateFail", resourceLink.getName()), e);
            }
        }
    }

    public void addService(ContextService service) {
        if (this.entries.contains(service.getName())) {
            return;
        }
        this.entries.add(service.getName());
        synchronized (this.services) {
            service.setNamingResources(this);
            this.services.put(service.getName(), service);
        }
        this.support.firePropertyChange("service", (Object) null, service);
    }

    public ContextEjb findEjb(String name) {
        ContextEjb contextEjb;
        synchronized (this.ejbs) {
            contextEjb = this.ejbs.get(name);
        }
        return contextEjb;
    }

    public ContextEjb[] findEjbs() {
        ContextEjb[] contextEjbArr;
        synchronized (this.ejbs) {
            contextEjbArr = (ContextEjb[]) this.ejbs.values().toArray(new ContextEjb[0]);
        }
        return contextEjbArr;
    }

    public ContextEnvironment findEnvironment(String name) {
        ContextEnvironment contextEnvironment;
        synchronized (this.envs) {
            contextEnvironment = this.envs.get(name);
        }
        return contextEnvironment;
    }

    public ContextEnvironment[] findEnvironments() {
        ContextEnvironment[] contextEnvironmentArr;
        synchronized (this.envs) {
            contextEnvironmentArr = (ContextEnvironment[]) this.envs.values().toArray(new ContextEnvironment[0]);
        }
        return contextEnvironmentArr;
    }

    public ContextLocalEjb findLocalEjb(String name) {
        ContextLocalEjb contextLocalEjb;
        synchronized (this.localEjbs) {
            contextLocalEjb = this.localEjbs.get(name);
        }
        return contextLocalEjb;
    }

    public ContextLocalEjb[] findLocalEjbs() {
        ContextLocalEjb[] contextLocalEjbArr;
        synchronized (this.localEjbs) {
            contextLocalEjbArr = (ContextLocalEjb[]) this.localEjbs.values().toArray(new ContextLocalEjb[0]);
        }
        return contextLocalEjbArr;
    }

    public MessageDestinationRef findMessageDestinationRef(String name) {
        MessageDestinationRef messageDestinationRef;
        synchronized (this.mdrs) {
            messageDestinationRef = this.mdrs.get(name);
        }
        return messageDestinationRef;
    }

    public MessageDestinationRef[] findMessageDestinationRefs() {
        MessageDestinationRef[] messageDestinationRefArr;
        synchronized (this.mdrs) {
            messageDestinationRefArr = (MessageDestinationRef[]) this.mdrs.values().toArray(new MessageDestinationRef[0]);
        }
        return messageDestinationRefArr;
    }

    public ContextResource findResource(String name) {
        ContextResource contextResource;
        synchronized (this.resources) {
            contextResource = this.resources.get(name);
        }
        return contextResource;
    }

    public ContextResourceLink findResourceLink(String name) {
        ContextResourceLink contextResourceLink;
        synchronized (this.resourceLinks) {
            contextResourceLink = this.resourceLinks.get(name);
        }
        return contextResourceLink;
    }

    public ContextResourceLink[] findResourceLinks() {
        ContextResourceLink[] contextResourceLinkArr;
        synchronized (this.resourceLinks) {
            contextResourceLinkArr = (ContextResourceLink[]) this.resourceLinks.values().toArray(new ContextResourceLink[0]);
        }
        return contextResourceLinkArr;
    }

    public ContextResource[] findResources() {
        ContextResource[] contextResourceArr;
        synchronized (this.resources) {
            contextResourceArr = (ContextResource[]) this.resources.values().toArray(new ContextResource[0]);
        }
        return contextResourceArr;
    }

    public ContextResourceEnvRef findResourceEnvRef(String name) {
        ContextResourceEnvRef contextResourceEnvRef;
        synchronized (this.resourceEnvRefs) {
            contextResourceEnvRef = this.resourceEnvRefs.get(name);
        }
        return contextResourceEnvRef;
    }

    public ContextResourceEnvRef[] findResourceEnvRefs() {
        ContextResourceEnvRef[] contextResourceEnvRefArr;
        synchronized (this.resourceEnvRefs) {
            contextResourceEnvRefArr = (ContextResourceEnvRef[]) this.resourceEnvRefs.values().toArray(new ContextResourceEnvRef[0]);
        }
        return contextResourceEnvRefArr;
    }

    public ContextService findService(String name) {
        ContextService contextService;
        synchronized (this.services) {
            contextService = this.services.get(name);
        }
        return contextService;
    }

    public ContextService[] findServices() {
        ContextService[] contextServiceArr;
        synchronized (this.services) {
            contextServiceArr = (ContextService[]) this.services.values().toArray(new ContextService[0]);
        }
        return contextServiceArr;
    }

    public void removeEjb(String name) {
        ContextEjb ejb;
        this.entries.remove(name);
        synchronized (this.ejbs) {
            ejb = this.ejbs.remove(name);
        }
        if (ejb != null) {
            this.support.firePropertyChange("ejb", ejb, (Object) null);
            ejb.setNamingResources(null);
        }
    }

    @Override // org.apache.tomcat.util.descriptor.web.NamingResources
    public void removeEnvironment(String name) {
        ContextEnvironment environment;
        this.entries.remove(name);
        synchronized (this.envs) {
            environment = this.envs.remove(name);
        }
        if (environment != null) {
            this.support.firePropertyChange("environment", environment, (Object) null);
            if (this.resourceRequireExplicitRegistration) {
                try {
                    MBeanUtils.destroyMBean(environment);
                } catch (Exception e) {
                    log.warn(sm.getString("namingResources.mbeanDestroyFail", environment.getName()), e);
                }
            }
            environment.setNamingResources(null);
        }
    }

    public void removeLocalEjb(String name) {
        ContextLocalEjb localEjb;
        this.entries.remove(name);
        synchronized (this.localEjbs) {
            localEjb = this.localEjbs.remove(name);
        }
        if (localEjb != null) {
            this.support.firePropertyChange("localEjb", localEjb, (Object) null);
            localEjb.setNamingResources(null);
        }
    }

    public void removeMessageDestinationRef(String name) {
        MessageDestinationRef mdr;
        this.entries.remove(name);
        synchronized (this.mdrs) {
            mdr = this.mdrs.remove(name);
        }
        if (mdr != null) {
            this.support.firePropertyChange("messageDestinationRef", mdr, (Object) null);
            mdr.setNamingResources(null);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    @Override // org.apache.tomcat.util.descriptor.web.NamingResources
    public void removeResource(String name) {
        ContextResource resource;
        this.entries.remove(name);
        synchronized (this.resources) {
            resource = this.resources.remove(name);
        }
        if (resource != null) {
            this.support.firePropertyChange(DefaultBeanDefinitionDocumentReader.RESOURCE_ATTRIBUTE, resource, (Object) null);
            if (this.resourceRequireExplicitRegistration) {
                try {
                    MBeanUtils.destroyMBean(resource);
                } catch (Exception e) {
                    log.warn(sm.getString("namingResources.mbeanDestroyFail", resource.getName()), e);
                }
            }
            resource.setNamingResources(null);
        }
    }

    public void removeResourceEnvRef(String name) {
        ContextResourceEnvRef resourceEnvRef;
        this.entries.remove(name);
        synchronized (this.resourceEnvRefs) {
            resourceEnvRef = this.resourceEnvRefs.remove(name);
        }
        if (resourceEnvRef != null) {
            this.support.firePropertyChange("resourceEnvRef", resourceEnvRef, (Object) null);
            resourceEnvRef.setNamingResources(null);
        }
    }

    @Override // org.apache.tomcat.util.descriptor.web.NamingResources
    public void removeResourceLink(String name) {
        ContextResourceLink resourceLink;
        this.entries.remove(name);
        synchronized (this.resourceLinks) {
            resourceLink = this.resourceLinks.remove(name);
        }
        if (resourceLink != null) {
            this.support.firePropertyChange("resourceLink", resourceLink, (Object) null);
            if (this.resourceRequireExplicitRegistration) {
                try {
                    MBeanUtils.destroyMBean(resourceLink);
                } catch (Exception e) {
                    log.warn(sm.getString("namingResources.mbeanDestroyFail", resourceLink.getName()), e);
                }
            }
            resourceLink.setNamingResources(null);
        }
    }

    public void removeService(String name) {
        ContextService service;
        this.entries.remove(name);
        synchronized (this.services) {
            service = this.services.remove(name);
        }
        if (service != null) {
            this.support.firePropertyChange("service", service, (Object) null);
            service.setNamingResources(null);
        }
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        this.resourceRequireExplicitRegistration = true;
        for (ContextResource cr : this.resources.values()) {
            try {
                MBeanUtils.createMBean(cr);
            } catch (Exception e) {
                log.warn(sm.getString("namingResources.mbeanCreateFail", cr.getName()), e);
            }
        }
        for (ContextEnvironment ce : this.envs.values()) {
            try {
                MBeanUtils.createMBean(ce);
            } catch (Exception e2) {
                log.warn(sm.getString("namingResources.mbeanCreateFail", ce.getName()), e2);
            }
        }
        for (ContextResourceLink crl : this.resourceLinks.values()) {
            try {
                MBeanUtils.createMBean(crl);
            } catch (Exception e3) {
                log.warn(sm.getString("namingResources.mbeanCreateFail", crl.getName()), e3);
            }
        }
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void startInternal() throws LifecycleException {
        fireLifecycleEvent(Lifecycle.CONFIGURE_START_EVENT, null);
        setState(LifecycleState.STARTING);
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void stopInternal() throws LifecycleException {
        cleanUp();
        setState(LifecycleState.STOPPING);
        fireLifecycleEvent(Lifecycle.CONFIGURE_STOP_EVENT, null);
    }

    private void cleanUp() {
        javax.naming.Context ctxt;
        String closeMethod;
        if (this.resources.size() == 0) {
            return;
        }
        try {
            if (this.container instanceof Server) {
                ctxt = ((Server) this.container).getGlobalNamingContext();
            } else {
                javax.naming.Context ctxt2 = ContextBindings.getClassLoader();
                ctxt = (javax.naming.Context) ctxt2.lookup("comp/env");
            }
            for (ContextResource cr : this.resources.values()) {
                if (cr.getSingleton() && (closeMethod = cr.getCloseMethod()) != null && closeMethod.length() > 0) {
                    String name = cr.getName();
                    try {
                        Object resource = ctxt.lookup(name);
                        cleanUp(resource, name, closeMethod);
                    } catch (NamingException e) {
                        log.warn(sm.getString("namingResources.cleanupNoResource", cr.getName(), this.container), e);
                    }
                }
            }
        } catch (NamingException e2) {
            log.warn(sm.getString("namingResources.cleanupNoContext", this.container), e2);
        }
    }

    private void cleanUp(Object resource, String name, String closeMethod) {
        try {
            Method m = resource.getClass().getMethod(closeMethod, (Class[]) null);
            try {
                m.invoke(resource, (Object[]) null);
            } catch (IllegalAccessException | IllegalArgumentException e) {
                log.warn(sm.getString("namingResources.cleanupCloseFailed", closeMethod, name, this.container), e);
            } catch (InvocationTargetException e2) {
                Throwable t = ExceptionUtils.unwrapInvocationTargetException(e2);
                ExceptionUtils.handleThrowable(t);
                log.warn(sm.getString("namingResources.cleanupCloseFailed", closeMethod, name, this.container), t);
            }
        } catch (NoSuchMethodException e3) {
            log.debug(sm.getString("namingResources.cleanupNoClose", name, this.container, closeMethod));
        } catch (SecurityException e4) {
            log.debug(sm.getString("namingResources.cleanupCloseSecurity", closeMethod, name, this.container));
        }
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    protected void destroyInternal() throws LifecycleException {
        this.resourceRequireExplicitRegistration = false;
        for (ContextResourceLink crl : this.resourceLinks.values()) {
            try {
                MBeanUtils.destroyMBean(crl);
            } catch (Exception e) {
                log.warn(sm.getString("namingResources.mbeanDestroyFail", crl.getName()), e);
            }
        }
        for (ContextEnvironment ce : this.envs.values()) {
            try {
                MBeanUtils.destroyMBean(ce);
            } catch (Exception e2) {
                log.warn(sm.getString("namingResources.mbeanDestroyFail", ce.getName()), e2);
            }
        }
        for (ContextResource cr : this.resources.values()) {
            try {
                MBeanUtils.destroyMBean(cr);
            } catch (Exception e3) {
                log.warn(sm.getString("namingResources.mbeanDestroyFail", cr.getName()), e3);
            }
        }
        super.destroyInternal();
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getDomainInternal() {
        Object c = getContainer();
        if (c instanceof JmxEnabled) {
            return ((JmxEnabled) c).getDomain();
        }
        return null;
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getObjectNameKeyProperties() {
        Object c = getContainer();
        if (c instanceof Container) {
            return "type=NamingResources" + ((Container) c).getMBeanKeyProperties();
        }
        return "type=NamingResources";
    }

    private boolean checkResourceType(ResourceBase resource) {
        if (!(this.container instanceof Context) || resource.getInjectionTargets() == null || resource.getInjectionTargets().size() == 0) {
            return true;
        }
        Context context = (Context) this.container;
        String typeName = resource.getType();
        Class<?> typeClass = null;
        if (typeName != null) {
            typeClass = Introspection.loadClass(context, typeName);
            if (typeClass == null) {
                return true;
            }
        }
        Class<?> compatibleClass = getCompatibleType(context, resource, typeClass);
        if (compatibleClass == null) {
            return false;
        }
        resource.setType(compatibleClass.getCanonicalName());
        return true;
    }

    private Class<?> getCompatibleType(Context context, ResourceBase resource, Class<?> typeClass) {
        Class<?> result = null;
        for (InjectionTarget injectionTarget : resource.getInjectionTargets()) {
            Class<?> clazz = Introspection.loadClass(context, injectionTarget.getTargetClass());
            if (clazz != null) {
                String targetName = injectionTarget.getTargetName();
                Class<?> targetType = getSetterType(clazz, targetName);
                if (targetType == null) {
                    targetType = getFieldType(clazz, targetName);
                }
                if (targetType == null) {
                    continue;
                } else {
                    Class<?> targetType2 = Introspection.convertPrimitiveType(targetType);
                    if (typeClass == null) {
                        if (result == null) {
                            result = targetType2;
                        } else if (targetType2.isAssignableFrom(result)) {
                            continue;
                        } else if (result.isAssignableFrom(targetType2)) {
                            result = targetType2;
                        } else {
                            return null;
                        }
                    } else if (targetType2.isAssignableFrom(typeClass)) {
                        result = typeClass;
                    } else {
                        return null;
                    }
                }
            }
        }
        return result;
    }

    private Class<?> getSetterType(Class<?> clazz, String name) {
        Method[] methods = Introspection.getDeclaredMethods(clazz);
        if (methods != null && methods.length > 0) {
            for (Method method : methods) {
                if (Introspection.isValidSetter(method) && Introspection.getPropertyName(method).equals(name)) {
                    return method.getParameterTypes()[0];
                }
            }
            return null;
        }
        return null;
    }

    private Class<?> getFieldType(Class<?> clazz, String name) {
        Field[] fields = Introspection.getDeclaredFields(clazz);
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                if (field.getName().equals(name)) {
                    return field.getType();
                }
            }
            return null;
        }
        return null;
    }
}
