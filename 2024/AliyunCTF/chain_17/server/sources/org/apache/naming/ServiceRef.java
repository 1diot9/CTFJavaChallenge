package org.apache.naming;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.naming.StringRefAddr;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/naming/ServiceRef.class */
public class ServiceRef extends AbstractRef {
    private static final long serialVersionUID = 1;
    public static final String DEFAULT_FACTORY = "org.apache.naming.factory.webservices.ServiceRefFactory";
    public static final String SERVICE_INTERFACE = "serviceInterface";
    public static final String SERVICE_NAMESPACE = "service namespace";
    public static final String SERVICE_LOCAL_PART = "service local part";
    public static final String WSDL = "wsdl";
    public static final String JAXRPCMAPPING = "jaxrpcmapping";
    public static final String PORTCOMPONENTLINK = "portcomponentlink";
    public static final String SERVICEENDPOINTINTERFACE = "serviceendpointinterface";
    private final List<HandlerRef> handlers;

    public ServiceRef(String refname, String serviceInterface, String[] serviceQname, String wsdl, String jaxrpcmapping) {
        this(refname, serviceInterface, serviceQname, wsdl, jaxrpcmapping, null, null);
    }

    public ServiceRef(String refname, String serviceInterface, String[] serviceQname, String wsdl, String jaxrpcmapping, String factory, String factoryLocation) {
        super(serviceInterface, factory, factoryLocation);
        this.handlers = new CopyOnWriteArrayList();
        if (serviceInterface != null) {
            StringRefAddr refAddr = new StringRefAddr(SERVICE_INTERFACE, serviceInterface);
            add(refAddr);
        }
        if (serviceQname[0] != null) {
            StringRefAddr refAddr2 = new StringRefAddr(SERVICE_NAMESPACE, serviceQname[0]);
            add(refAddr2);
        }
        if (serviceQname[1] != null) {
            StringRefAddr refAddr3 = new StringRefAddr(SERVICE_LOCAL_PART, serviceQname[1]);
            add(refAddr3);
        }
        if (wsdl != null) {
            StringRefAddr refAddr4 = new StringRefAddr(WSDL, wsdl);
            add(refAddr4);
        }
        if (jaxrpcmapping != null) {
            StringRefAddr refAddr5 = new StringRefAddr(JAXRPCMAPPING, jaxrpcmapping);
            add(refAddr5);
        }
    }

    public HandlerRef getHandler() {
        return this.handlers.remove(0);
    }

    public int getHandlersSize() {
        return this.handlers.size();
    }

    public void addHandler(HandlerRef handler) {
        this.handlers.add(handler);
    }

    @Override // org.apache.naming.AbstractRef
    protected String getDefaultFactoryClassName() {
        return "org.apache.naming.factory.webservices.ServiceRefFactory";
    }
}
