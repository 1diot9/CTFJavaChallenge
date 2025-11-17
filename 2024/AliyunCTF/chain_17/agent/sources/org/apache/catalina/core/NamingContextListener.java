package org.apache.catalina.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import org.apache.catalina.Engine;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.naming.ContextAccessController;
import org.apache.naming.ContextBindings;
import org.apache.naming.EjbRef;
import org.apache.naming.HandlerRef;
import org.apache.naming.LookupRef;
import org.apache.naming.NamingContext;
import org.apache.naming.ResourceEnvRef;
import org.apache.naming.ResourceLinkRef;
import org.apache.naming.ResourceRef;
import org.apache.naming.ServiceRef;
import org.apache.naming.TransactionRef;
import org.apache.naming.factory.ResourceLinkFactory;
import org.apache.tomcat.util.descriptor.web.ContextEjb;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.ContextHandler;
import org.apache.tomcat.util.descriptor.web.ContextLocalEjb;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.ContextResourceEnvRef;
import org.apache.tomcat.util.descriptor.web.ContextResourceLink;
import org.apache.tomcat.util.descriptor.web.ContextService;
import org.apache.tomcat.util.descriptor.web.ContextTransaction;
import org.apache.tomcat.util.descriptor.web.MessageDestinationRef;
import org.apache.tomcat.util.descriptor.web.ResourceBase;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/NamingContextListener.class */
public class NamingContextListener implements LifecycleListener, PropertyChangeListener {
    private static final Log log = LogFactory.getLog((Class<?>) NamingContextListener.class);
    protected static final StringManager sm = StringManager.getManager((Class<?>) NamingContextListener.class);
    protected String name = "/";
    protected Object container = null;
    private Object token = null;
    protected boolean initialized = false;
    protected NamingResourcesImpl namingResources = null;
    protected NamingContext namingContext = null;
    protected Context compCtx = null;
    protected Context envCtx = null;
    protected HashMap<String, ObjectName> objectNames = new HashMap<>();
    private boolean exceptionOnFailedWrite = true;

    public boolean getExceptionOnFailedWrite() {
        return this.exceptionOnFailedWrite;
    }

    public void setExceptionOnFailedWrite(boolean exceptionOnFailedWrite) {
        this.exceptionOnFailedWrite = exceptionOnFailedWrite;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Context getEnvContext() {
        return this.envCtx;
    }

    @Override // org.apache.catalina.LifecycleListener
    public void lifecycleEvent(LifecycleEvent event) {
        this.container = event.getLifecycle();
        if (this.container instanceof org.apache.catalina.Context) {
            this.namingResources = ((org.apache.catalina.Context) this.container).getNamingResources();
            this.token = ((org.apache.catalina.Context) this.container).getNamingToken();
        } else if (this.container instanceof Server) {
            this.namingResources = ((Server) this.container).getGlobalNamingResources();
            this.token = ((Server) this.container).getNamingToken();
        } else {
            return;
        }
        if (Lifecycle.CONFIGURE_START_EVENT.equals(event.getType())) {
            if (this.initialized) {
                return;
            }
            try {
                Hashtable<String, Object> contextEnv = new Hashtable<>();
                this.namingContext = new NamingContext(contextEnv, getName());
                ContextAccessController.setSecurityToken(getName(), this.token);
                ContextAccessController.setSecurityToken(this.container, this.token);
                ContextBindings.bindContext(this.container, this.namingContext, this.token);
                if (log.isDebugEnabled()) {
                    log.debug("Bound " + this.container);
                }
                this.namingContext.setExceptionOnFailedWrite(getExceptionOnFailedWrite());
                ContextAccessController.setWritable(getName(), this.token);
                try {
                    createNamingContext();
                } catch (NamingException e) {
                    log.error(sm.getString("naming.namingContextCreationFailed", e));
                }
                this.namingResources.addPropertyChangeListener(this);
                if (this.container instanceof org.apache.catalina.Context) {
                    ContextAccessController.setReadOnly(getName());
                    try {
                        ContextBindings.bindClassLoader(this.container, this.token, ((org.apache.catalina.Context) this.container).getLoader().getClassLoader());
                    } catch (NamingException e2) {
                        log.error(sm.getString("naming.bindFailed", e2));
                    }
                }
                if (this.container instanceof Server) {
                    ResourceLinkFactory.setGlobalContext(this.namingContext);
                    try {
                        ContextBindings.bindClassLoader(this.container, this.token, getClass().getClassLoader());
                    } catch (NamingException e3) {
                        log.error(sm.getString("naming.bindFailed", e3));
                    }
                    if (this.container instanceof StandardServer) {
                        ((StandardServer) this.container).setGlobalNamingContext(this.namingContext);
                    }
                }
                return;
            } finally {
                this.initialized = true;
            }
        }
        if (!Lifecycle.CONFIGURE_STOP_EVENT.equals(event.getType()) || !this.initialized) {
            return;
        }
        try {
            ContextAccessController.setWritable(getName(), this.token);
            ContextBindings.unbindContext(this.container, this.token);
            if (this.container instanceof org.apache.catalina.Context) {
                ContextBindings.unbindClassLoader(this.container, this.token, ((org.apache.catalina.Context) this.container).getLoader().getClassLoader());
            }
            if (this.container instanceof Server) {
                ContextBindings.unbindClassLoader(this.container, this.token, getClass().getClassLoader());
            }
            this.namingResources.removePropertyChangeListener(this);
            ContextAccessController.unsetSecurityToken(getName(), this.token);
            ContextAccessController.unsetSecurityToken(this.container, this.token);
            if (!this.objectNames.isEmpty()) {
                Collection<ObjectName> names = this.objectNames.values();
                Registry registry = Registry.getRegistry(null, null);
                for (ObjectName objectName : names) {
                    registry.unregisterComponent(objectName);
                }
            }
            Context global = getGlobalNamingContext();
            if (global != null) {
                ResourceLinkFactory.deregisterGlobalResourceAccess(global);
            }
        } finally {
            this.objectNames.clear();
            this.namingContext = null;
            this.envCtx = null;
            this.compCtx = null;
            this.initialized = false;
        }
    }

    @Override // java.beans.PropertyChangeListener
    public void propertyChange(PropertyChangeEvent event) {
        if (!this.initialized) {
            return;
        }
        Object source = event.getSource();
        if (source == this.namingResources) {
            ContextAccessController.setWritable(getName(), this.token);
            processGlobalResourcesChange(event.getPropertyName(), event.getOldValue(), event.getNewValue());
            ContextAccessController.setReadOnly(getName());
        }
    }

    private void processGlobalResourcesChange(String name, Object oldValue, Object newValue) {
        if (name.equals("ejb")) {
            if (oldValue != null) {
                ContextEjb ejb = (ContextEjb) oldValue;
                if (ejb.getName() != null) {
                    removeEjb(ejb.getName());
                }
            }
            if (newValue != null) {
                ContextEjb ejb2 = (ContextEjb) newValue;
                if (ejb2.getName() != null) {
                    addEjb(ejb2);
                    return;
                }
                return;
            }
            return;
        }
        if (name.equals("environment")) {
            if (oldValue != null) {
                ContextEnvironment env = (ContextEnvironment) oldValue;
                if (env.getName() != null) {
                    removeEnvironment(env.getName());
                }
            }
            if (newValue != null) {
                ContextEnvironment env2 = (ContextEnvironment) newValue;
                if (env2.getName() != null) {
                    addEnvironment(env2);
                    return;
                }
                return;
            }
            return;
        }
        if (name.equals("localEjb")) {
            if (oldValue != null) {
                ContextLocalEjb ejb3 = (ContextLocalEjb) oldValue;
                if (ejb3.getName() != null) {
                    removeLocalEjb(ejb3.getName());
                }
            }
            if (newValue != null) {
                ContextLocalEjb ejb4 = (ContextLocalEjb) newValue;
                if (ejb4.getName() != null) {
                    addLocalEjb(ejb4);
                    return;
                }
                return;
            }
            return;
        }
        if (name.equals("messageDestinationRef")) {
            if (oldValue != null) {
                MessageDestinationRef mdr = (MessageDestinationRef) oldValue;
                if (mdr.getName() != null) {
                    removeMessageDestinationRef(mdr.getName());
                }
            }
            if (newValue != null) {
                MessageDestinationRef mdr2 = (MessageDestinationRef) newValue;
                if (mdr2.getName() != null) {
                    addMessageDestinationRef(mdr2);
                    return;
                }
                return;
            }
            return;
        }
        if (name.equals(DefaultBeanDefinitionDocumentReader.RESOURCE_ATTRIBUTE)) {
            if (oldValue != null) {
                ContextResource resource = (ContextResource) oldValue;
                if (resource.getName() != null) {
                    removeResource(resource.getName());
                }
            }
            if (newValue != null) {
                ContextResource resource2 = (ContextResource) newValue;
                if (resource2.getName() != null) {
                    addResource(resource2);
                    return;
                }
                return;
            }
            return;
        }
        if (name.equals("resourceEnvRef")) {
            if (oldValue != null) {
                ContextResourceEnvRef resourceEnvRef = (ContextResourceEnvRef) oldValue;
                if (resourceEnvRef.getName() != null) {
                    removeResourceEnvRef(resourceEnvRef.getName());
                }
            }
            if (newValue != null) {
                ContextResourceEnvRef resourceEnvRef2 = (ContextResourceEnvRef) newValue;
                if (resourceEnvRef2.getName() != null) {
                    addResourceEnvRef(resourceEnvRef2);
                    return;
                }
                return;
            }
            return;
        }
        if (name.equals("resourceLink")) {
            if (oldValue != null) {
                ContextResourceLink rl = (ContextResourceLink) oldValue;
                if (rl.getName() != null) {
                    removeResourceLink(rl.getName());
                }
            }
            if (newValue != null) {
                ContextResourceLink rl2 = (ContextResourceLink) newValue;
                if (rl2.getName() != null) {
                    addResourceLink(rl2);
                    return;
                }
                return;
            }
            return;
        }
        if (name.equals("service")) {
            if (oldValue != null) {
                ContextService service = (ContextService) oldValue;
                if (service.getName() != null) {
                    removeService(service.getName());
                }
            }
            if (newValue != null) {
                ContextService service2 = (ContextService) newValue;
                if (service2.getName() != null) {
                    addService(service2);
                }
            }
        }
    }

    private void createNamingContext() throws NamingException {
        if (this.container instanceof Server) {
            this.compCtx = this.namingContext;
            this.envCtx = this.namingContext;
        } else {
            this.compCtx = this.namingContext.createSubcontext("comp");
            this.envCtx = this.compCtx.createSubcontext("env");
        }
        if (log.isDebugEnabled()) {
            log.debug("Creating JNDI naming context");
        }
        if (this.namingResources == null) {
            this.namingResources = new NamingResourcesImpl();
            this.namingResources.setContainer(this.container);
        }
        ContextResourceLink[] resourceLinks = this.namingResources.findResourceLinks();
        for (ContextResourceLink contextResourceLink : resourceLinks) {
            addResourceLink(contextResourceLink);
        }
        ContextResource[] resources = this.namingResources.findResources();
        for (ContextResource contextResource : resources) {
            addResource(contextResource);
        }
        ContextResourceEnvRef[] resourceEnvRefs = this.namingResources.findResourceEnvRefs();
        for (ContextResourceEnvRef contextResourceEnvRef : resourceEnvRefs) {
            addResourceEnvRef(contextResourceEnvRef);
        }
        ContextEnvironment[] contextEnvironments = this.namingResources.findEnvironments();
        for (ContextEnvironment contextEnvironment : contextEnvironments) {
            addEnvironment(contextEnvironment);
        }
        ContextEjb[] ejbs = this.namingResources.findEjbs();
        for (ContextEjb contextEjb : ejbs) {
            addEjb(contextEjb);
        }
        MessageDestinationRef[] mdrs = this.namingResources.findMessageDestinationRefs();
        for (MessageDestinationRef messageDestinationRef : mdrs) {
            addMessageDestinationRef(messageDestinationRef);
        }
        ContextService[] services = this.namingResources.findServices();
        for (ContextService contextService : services) {
            addService(contextService);
        }
        if (this.container instanceof org.apache.catalina.Context) {
            try {
                Reference ref = new TransactionRef();
                this.compCtx.bind("UserTransaction", ref);
                ContextTransaction transaction = this.namingResources.getTransaction();
                if (transaction != null) {
                    Iterator<String> params = transaction.listProperties();
                    while (params.hasNext()) {
                        String paramName = params.next();
                        String paramValue = (String) transaction.getProperty(paramName);
                        StringRefAddr refAddr = new StringRefAddr(paramName, paramValue);
                        ref.add(refAddr);
                    }
                }
            } catch (NamingException e) {
                log.error(sm.getString("naming.bindFailed", e));
            } catch (NameAlreadyBoundException e2) {
            }
        }
        if (this.container instanceof org.apache.catalina.Context) {
            try {
                this.compCtx.bind("Resources", ((org.apache.catalina.Context) this.container).getResources());
            } catch (NamingException e3) {
                log.error(sm.getString("naming.bindFailed", e3));
            }
        }
    }

    protected ObjectName createObjectName(ContextResource resource) throws MalformedObjectNameException {
        String domain = null;
        if (this.container instanceof StandardServer) {
            domain = ((StandardServer) this.container).getDomain();
        } else if (this.container instanceof ContainerBase) {
            domain = ((ContainerBase) this.container).getDomain();
        }
        if (domain == null) {
            domain = Globals.DEFAULT_MBEAN_DOMAIN;
        }
        ObjectName name = null;
        String quotedResourceName = ObjectName.quote(resource.getName());
        if (this.container instanceof Server) {
            name = new ObjectName(domain + ":type=DataSource,class=" + resource.getType() + ",name=" + quotedResourceName);
        } else if (this.container instanceof org.apache.catalina.Context) {
            String contextName = ((org.apache.catalina.Context) this.container).getName();
            if (!contextName.startsWith("/")) {
                contextName = "/" + contextName;
            }
            Host host = (Host) ((org.apache.catalina.Context) this.container).getParent();
            name = new ObjectName(domain + ":type=DataSource,host=" + host.getName() + ",context=" + contextName + ",class=" + resource.getType() + ",name=" + quotedResourceName);
        }
        return name;
    }

    public void addEjb(ContextEjb ejb) {
        Reference ref = lookForLookupRef(ejb);
        if (ref == null) {
            ref = new EjbRef(ejb.getType(), ejb.getHome(), ejb.getRemote(), ejb.getLink());
            Iterator<String> params = ejb.listProperties();
            while (params.hasNext()) {
                String paramName = params.next();
                String paramValue = (String) ejb.getProperty(paramName);
                StringRefAddr refAddr = new StringRefAddr(paramName, paramValue);
                ref.add(refAddr);
            }
        }
        try {
            createSubcontexts(this.envCtx, ejb.getName());
            this.envCtx.bind(ejb.getName(), ref);
        } catch (NamingException e) {
            log.error(sm.getString("naming.bindFailed", e));
        }
    }

    public void addEnvironment(ContextEnvironment env) {
        Object value = lookForLookupRef(env);
        if (value == null) {
            String type = env.getType();
            try {
                if (type.equals("java.lang.String")) {
                    value = env.getValue();
                } else if (type.equals("java.lang.Byte")) {
                    value = env.getValue() == null ? (byte) 0 : Byte.decode(env.getValue());
                } else if (type.equals("java.lang.Short")) {
                    value = env.getValue() == null ? (short) 0 : Short.decode(env.getValue());
                } else if (type.equals("java.lang.Integer")) {
                    value = env.getValue() == null ? 0 : Integer.decode(env.getValue());
                } else if (type.equals("java.lang.Long")) {
                    value = env.getValue() == null ? 0L : Long.decode(env.getValue());
                } else if (type.equals("java.lang.Boolean")) {
                    value = Boolean.valueOf(env.getValue());
                } else if (type.equals("java.lang.Double")) {
                    value = env.getValue() == null ? Double.valueOf(0.0d) : Double.valueOf(env.getValue());
                } else if (type.equals("java.lang.Float")) {
                    value = env.getValue() == null ? Float.valueOf(0.0f) : Float.valueOf(env.getValue());
                } else if (type.equals("java.lang.Character")) {
                    if (env.getValue() == null) {
                        value = (char) 0;
                    } else if (env.getValue().length() == 1) {
                        value = Character.valueOf(env.getValue().charAt(0));
                    } else {
                        throw new IllegalArgumentException();
                    }
                } else {
                    value = constructEnvEntry(env.getType(), env.getValue());
                    if (value == null) {
                        log.error(sm.getString("naming.invalidEnvEntryType", env.getName()));
                    }
                }
            } catch (IllegalArgumentException e) {
                log.error(sm.getString("naming.invalidEnvEntryValue", env.getName()));
            }
        }
        if (value != null) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("naming.addEnvEntry", env.getName()));
                }
                createSubcontexts(this.envCtx, env.getName());
                this.envCtx.bind(env.getName(), value);
            } catch (NamingException e2) {
                log.error(sm.getString("naming.invalidEnvEntryValue", e2));
            }
        }
    }

    private Object constructEnvEntry(String type, String value) {
        try {
            Class<?> clazz = Class.forName(type);
            try {
                Constructor<?> c = clazz.getConstructor(String.class);
                return c.newInstance(value);
            } catch (NoSuchMethodException e) {
                if (value.length() != 1) {
                    return null;
                }
                try {
                    Constructor<?> c2 = clazz.getConstructor(Character.TYPE);
                    return c2.newInstance(Character.valueOf(value.charAt(0)));
                } catch (NoSuchMethodException e2) {
                    return null;
                }
            }
        } catch (Exception e3) {
            return null;
        }
    }

    public void addLocalEjb(ContextLocalEjb localEjb) {
    }

    public void addMessageDestinationRef(MessageDestinationRef mdr) {
    }

    public void addService(ContextService service) {
        Reference ref = lookForLookupRef(service);
        if (ref == null) {
            if (service.getWsdlfile() != null) {
                URL wsdlURL = null;
                try {
                    URI wsdlURI = new URI(service.getWsdlfile());
                    wsdlURL = wsdlURI.toURL();
                } catch (IllegalArgumentException | MalformedURLException | URISyntaxException e) {
                }
                if (wsdlURL == null) {
                    try {
                        wsdlURL = ((org.apache.catalina.Context) this.container).getServletContext().getResource(service.getWsdlfile());
                    } catch (MalformedURLException e2) {
                    }
                }
                if (wsdlURL == null) {
                    try {
                        wsdlURL = ((org.apache.catalina.Context) this.container).getServletContext().getResource("/" + service.getWsdlfile());
                        log.debug("  Changing service ref wsdl file for /" + service.getWsdlfile());
                    } catch (MalformedURLException e3) {
                        log.error(sm.getString("naming.wsdlFailed", e3));
                    }
                }
                if (wsdlURL == null) {
                    service.setWsdlfile(null);
                } else {
                    service.setWsdlfile(wsdlURL.toString());
                }
            }
            if (service.getJaxrpcmappingfile() != null) {
                URL jaxrpcURL = null;
                try {
                    URI jaxrpcURI = new URI(service.getJaxrpcmappingfile());
                    jaxrpcURL = jaxrpcURI.toURL();
                } catch (IllegalArgumentException | MalformedURLException | URISyntaxException e4) {
                }
                if (jaxrpcURL == null) {
                    try {
                        jaxrpcURL = ((org.apache.catalina.Context) this.container).getServletContext().getResource(service.getJaxrpcmappingfile());
                    } catch (MalformedURLException e5) {
                    }
                }
                if (jaxrpcURL == null) {
                    try {
                        jaxrpcURL = ((org.apache.catalina.Context) this.container).getServletContext().getResource("/" + service.getJaxrpcmappingfile());
                        log.debug("  Changing service ref jaxrpc file for /" + service.getJaxrpcmappingfile());
                    } catch (MalformedURLException e6) {
                        log.error(sm.getString("naming.wsdlFailed", e6));
                    }
                }
                if (jaxrpcURL == null) {
                    service.setJaxrpcmappingfile(null);
                } else {
                    service.setJaxrpcmappingfile(jaxrpcURL.toString());
                }
            }
            ref = new ServiceRef(service.getName(), service.getInterface(), service.getServiceqname(), service.getWsdlfile(), service.getJaxrpcmappingfile());
            Iterator<String> portcomponent = service.getServiceendpoints();
            while (portcomponent.hasNext()) {
                String serviceendpoint = portcomponent.next();
                StringRefAddr refAddr = new StringRefAddr(ServiceRef.SERVICEENDPOINTINTERFACE, serviceendpoint);
                ref.add(refAddr);
                String portlink = service.getPortlink(serviceendpoint);
                StringRefAddr refAddr2 = new StringRefAddr(ServiceRef.PORTCOMPONENTLINK, portlink);
                ref.add(refAddr2);
            }
            Iterator<String> handlers = service.getHandlers();
            while (handlers.hasNext()) {
                String handlername = handlers.next();
                ContextHandler handler = service.getHandler(handlername);
                HandlerRef handlerRef = new HandlerRef(handlername, handler.getHandlerclass());
                Iterator<String> localParts = handler.getLocalparts();
                while (localParts.hasNext()) {
                    String localPart = localParts.next();
                    String namespaceURI = handler.getNamespaceuri(localPart);
                    handlerRef.add(new StringRefAddr(HandlerRef.HANDLER_LOCALPART, localPart));
                    handlerRef.add(new StringRefAddr(HandlerRef.HANDLER_NAMESPACE, namespaceURI));
                }
                Iterator<String> params = handler.listProperties();
                while (params.hasNext()) {
                    String paramName = params.next();
                    String paramValue = (String) handler.getProperty(paramName);
                    handlerRef.add(new StringRefAddr(HandlerRef.HANDLER_PARAMNAME, paramName));
                    handlerRef.add(new StringRefAddr(HandlerRef.HANDLER_PARAMVALUE, paramValue));
                }
                for (int i = 0; i < handler.getSoapRolesSize(); i++) {
                    handlerRef.add(new StringRefAddr(HandlerRef.HANDLER_SOAPROLE, handler.getSoapRole(i)));
                }
                for (int i2 = 0; i2 < handler.getPortNamesSize(); i2++) {
                    handlerRef.add(new StringRefAddr(HandlerRef.HANDLER_PORTNAME, handler.getPortName(i2)));
                }
                ((ServiceRef) ref).addHandler(handlerRef);
            }
        }
        try {
            if (log.isDebugEnabled()) {
                log.debug("  Adding service ref " + service.getName() + "  " + ref);
            }
            createSubcontexts(this.envCtx, service.getName());
            this.envCtx.bind(service.getName(), ref);
        } catch (NamingException e7) {
            log.error(sm.getString("naming.bindFailed", e7));
        }
    }

    public void addResource(ContextResource resource) {
        Reference ref = lookForLookupRef(resource);
        if (ref == null) {
            ref = new ResourceRef(resource.getType(), resource.getDescription(), resource.getScope(), resource.getAuth(), resource.getSingleton());
            Iterator<String> params = resource.listProperties();
            while (params.hasNext()) {
                String paramName = params.next();
                String paramValue = (String) resource.getProperty(paramName);
                StringRefAddr refAddr = new StringRefAddr(paramName, paramValue);
                ref.add(refAddr);
            }
        }
        try {
            if (log.isDebugEnabled()) {
                log.debug("  Adding resource ref " + resource.getName() + "  " + ref);
            }
            createSubcontexts(this.envCtx, resource.getName());
            this.envCtx.bind(resource.getName(), ref);
        } catch (NamingException e) {
            log.error(sm.getString("naming.bindFailed", e));
        }
        if (("javax.sql.DataSource".equals(ref.getClassName()) || "javax.sql.XADataSource".equals(ref.getClassName())) && resource.getSingleton()) {
            Object actualResource = null;
            try {
                ObjectName on = createObjectName(resource);
                actualResource = this.envCtx.lookup(resource.getName());
                Registry.getRegistry(null, null).registerComponent(actualResource, on, (String) null);
                this.objectNames.put(resource.getName(), on);
            } catch (Exception e2) {
                log.warn(sm.getString("naming.jmxRegistrationFailed", e2));
            }
            if ((actualResource instanceof AutoCloseable) && !resource.getCloseMethodConfigured()) {
                resource.setCloseMethod(org.apache.coyote.http11.Constants.CLOSE);
            }
        }
    }

    public void addResourceEnvRef(ContextResourceEnvRef resourceEnvRef) {
        Reference ref = lookForLookupRef(resourceEnvRef);
        if (ref == null) {
            ref = new ResourceEnvRef(resourceEnvRef.getType());
            Iterator<String> params = resourceEnvRef.listProperties();
            while (params.hasNext()) {
                String paramName = params.next();
                String paramValue = (String) resourceEnvRef.getProperty(paramName);
                StringRefAddr refAddr = new StringRefAddr(paramName, paramValue);
                ref.add(refAddr);
            }
        }
        try {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("naming.addResourceEnvRef", resourceEnvRef.getName()));
            }
            createSubcontexts(this.envCtx, resourceEnvRef.getName());
            this.envCtx.bind(resourceEnvRef.getName(), ref);
        } catch (NamingException e) {
            log.error(sm.getString("naming.bindFailed", e));
        }
    }

    public void addResourceLink(ContextResourceLink resourceLink) {
        Reference ref = new ResourceLinkRef(resourceLink.getType(), resourceLink.getGlobal(), resourceLink.getFactory(), null);
        Iterator<String> i = resourceLink.listProperties();
        while (i.hasNext()) {
            String key = i.next();
            Object val = resourceLink.getProperty(key);
            if (val != null) {
                StringRefAddr refAddr = new StringRefAddr(key, val.toString());
                ref.add(refAddr);
            }
        }
        Context ctx = "UserTransaction".equals(resourceLink.getName()) ? this.compCtx : this.envCtx;
        try {
            if (log.isDebugEnabled()) {
                log.debug("  Adding resource link " + resourceLink.getName());
            }
            createSubcontexts(this.envCtx, resourceLink.getName());
            ctx.bind(resourceLink.getName(), ref);
        } catch (NamingException e) {
            log.error(sm.getString("naming.bindFailed", e));
        }
        ResourceLinkFactory.registerGlobalResourceAccess(getGlobalNamingContext(), resourceLink.getName(), resourceLink.getGlobal());
    }

    private Context getGlobalNamingContext() {
        if (this.container instanceof org.apache.catalina.Context) {
            Engine e = (Engine) ((org.apache.catalina.Context) this.container).getParent().getParent();
            Server s = e.getService().getServer();
            if (s != null) {
                return s.getGlobalNamingContext();
            }
            return null;
        }
        return null;
    }

    public void removeEjb(String name) {
        try {
            this.envCtx.unbind(name);
        } catch (NamingException e) {
            log.error(sm.getString("naming.unbindFailed", name), e);
        }
    }

    public void removeEnvironment(String name) {
        try {
            this.envCtx.unbind(name);
        } catch (NamingException e) {
            log.error(sm.getString("naming.unbindFailed", name), e);
        }
    }

    public void removeLocalEjb(String name) {
        try {
            this.envCtx.unbind(name);
        } catch (NamingException e) {
            log.error(sm.getString("naming.unbindFailed", name), e);
        }
    }

    public void removeMessageDestinationRef(String name) {
        try {
            this.envCtx.unbind(name);
        } catch (NamingException e) {
            log.error(sm.getString("naming.unbindFailed", name), e);
        }
    }

    public void removeService(String name) {
        try {
            this.envCtx.unbind(name);
        } catch (NamingException e) {
            log.error(sm.getString("naming.unbindFailed", name), e);
        }
    }

    public void removeResource(String name) {
        try {
            this.envCtx.unbind(name);
        } catch (NamingException e) {
            log.error(sm.getString("naming.unbindFailed", name), e);
        }
        ObjectName on = this.objectNames.get(name);
        if (on != null) {
            Registry.getRegistry(null, null).unregisterComponent(on);
        }
    }

    public void removeResourceEnvRef(String name) {
        try {
            this.envCtx.unbind(name);
        } catch (NamingException e) {
            log.error(sm.getString("naming.unbindFailed", name), e);
        }
    }

    public void removeResourceLink(String name) {
        try {
            this.envCtx.unbind(name);
        } catch (NamingException e) {
            log.error(sm.getString("naming.unbindFailed", name), e);
        }
        ResourceLinkFactory.deregisterGlobalResourceAccess(getGlobalNamingContext(), name);
    }

    private void createSubcontexts(Context ctx, String name) throws NamingException {
        Context currentContext = ctx;
        StringTokenizer tokenizer = new StringTokenizer(name, "/");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (!token.equals("") && tokenizer.hasMoreTokens()) {
                try {
                    currentContext = currentContext.createSubcontext(token);
                } catch (NamingException e) {
                    currentContext = (Context) currentContext.lookup(token);
                }
            }
        }
    }

    private LookupRef lookForLookupRef(ResourceBase resourceBase) {
        String lookupName = resourceBase.getLookupName();
        if (lookupName != null && !lookupName.equals("")) {
            return new LookupRef(resourceBase.getType(), lookupName);
        }
        return null;
    }
}
