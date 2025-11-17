package org.apache.naming.factory.webservices;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.spi.ObjectFactory;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerChain;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.HandlerRegistry;
import org.apache.naming.HandlerRef;
import org.apache.naming.ServiceRef;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/naming/factory/webservices/ServiceRefFactory.class */
public class ServiceRefFactory implements ObjectFactory {
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        Service service;
        Object proxyInstance;
        if (obj instanceof ServiceRef) {
            ServiceRef ref = (ServiceRef) obj;
            ClassLoader tcl = Thread.currentThread().getContextClassLoader();
            if (tcl == null) {
                tcl = getClass().getClassLoader();
            }
            ServiceFactory factory = ServiceFactory.newInstance();
            RefAddr tmp = ref.get(ServiceRef.SERVICE_INTERFACE);
            String serviceInterface = null;
            if (tmp != null) {
                serviceInterface = (String) tmp.getContent();
            }
            RefAddr tmp2 = ref.get(ServiceRef.WSDL);
            String wsdlRefAddr = null;
            if (tmp2 != null) {
                wsdlRefAddr = (String) tmp2.getContent();
            }
            Hashtable<String, QName> portComponentRef = new Hashtable<>();
            QName serviceQname = null;
            RefAddr tmp3 = ref.get(ServiceRef.SERVICE_LOCAL_PART);
            if (tmp3 != null) {
                String serviceLocalPart = (String) tmp3.getContent();
                RefAddr tmp4 = ref.get(ServiceRef.SERVICE_NAMESPACE);
                if (tmp4 == null) {
                    serviceQname = new QName(serviceLocalPart);
                } else {
                    String serviceNamespace = (String) tmp4.getContent();
                    serviceQname = new QName(serviceNamespace, serviceLocalPart);
                }
            }
            if (serviceInterface == null) {
                if (serviceQname == null) {
                    throw new NamingException("Could not create service-ref instance");
                }
                try {
                    if (wsdlRefAddr == null) {
                        service = factory.createService(serviceQname);
                    } else {
                        service = factory.createService(new URI(wsdlRefAddr).toURL(), serviceQname);
                    }
                } catch (Exception e) {
                    NamingException ex = new NamingException("Could not create service");
                    ex.initCause(e);
                    throw ex;
                }
            } else {
                try {
                    Class<?> serviceInterfaceClass = tcl.loadClass(serviceInterface);
                    if (serviceInterfaceClass == null) {
                        throw new NamingException("Could not load service Interface");
                    }
                    try {
                        if (wsdlRefAddr == null) {
                            if (!Service.class.isAssignableFrom(serviceInterfaceClass)) {
                                throw new NamingException("service Interface should extend javax.xml.rpc.Service");
                            }
                            service = factory.loadService(serviceInterfaceClass);
                        } else {
                            service = factory.loadService(new URI(wsdlRefAddr).toURL(), serviceInterfaceClass, new Properties());
                        }
                    } catch (Exception e2) {
                        NamingException ex2 = new NamingException("Could not create service");
                        ex2.initCause(e2);
                        throw ex2;
                    }
                } catch (ClassNotFoundException e3) {
                    NamingException ex3 = new NamingException("Could not load service Interface");
                    ex3.initCause(e3);
                    throw ex3;
                }
            }
            if (service == null) {
                throw new NamingException("Cannot create service object");
            }
            QName serviceQname2 = service.getServiceName();
            Class<?> serviceInterfaceClass2 = service.getClass();
            if (wsdlRefAddr != null) {
                try {
                    WSDLFactory wsdlfactory = WSDLFactory.newInstance();
                    WSDLReader reader = wsdlfactory.newWSDLReader();
                    reader.setFeature("javax.wsdl.importDocuments", true);
                    Definition def = reader.readWSDL(new URI(wsdlRefAddr).toURL().toExternalForm());
                    javax.wsdl.Service wsdlservice = def.getService(serviceQname2);
                    Map<String, ?> ports = wsdlservice.getPorts();
                    Method m = serviceInterfaceClass2.getMethod("setEndpointAddress", String.class, String.class);
                    for (String portName : ports.keySet()) {
                        Port port = wsdlservice.getPort(portName);
                        String endpoint = getSOAPLocation(port);
                        m.invoke(service, port.getName(), endpoint);
                        portComponentRef.put(endpoint, new QName(port.getName()));
                    }
                } catch (Exception e4) {
                    if (e4 instanceof InvocationTargetException) {
                        Throwable cause = e4.getCause();
                        if (cause instanceof ThreadDeath) {
                            throw ((ThreadDeath) cause);
                        }
                        if (cause instanceof VirtualMachineError) {
                            throw ((VirtualMachineError) cause);
                        }
                    }
                    NamingException ex4 = new NamingException("Error while reading Wsdl File");
                    ex4.initCause(e4);
                    throw ex4;
                }
            }
            ServiceProxy proxy = new ServiceProxy(service);
            int i = 0;
            while (i < ref.size()) {
                if (ServiceRef.SERVICEENDPOINTINTERFACE.equals(ref.get(i).getType())) {
                    String portlink = "";
                    String serviceendpoint = (String) ref.get(i).getContent();
                    if (ServiceRef.PORTCOMPONENTLINK.equals(ref.get(i + 1).getType())) {
                        i++;
                        portlink = (String) ref.get(i).getContent();
                    }
                    portComponentRef.put(serviceendpoint, new QName(portlink));
                }
                i++;
            }
            proxy.setPortComponentRef(portComponentRef);
            Class<?>[] serviceInterfaces = serviceInterfaceClass2.getInterfaces();
            Class<?>[] interfaces = (Class[]) Arrays.copyOf(serviceInterfaces, serviceInterfaces.length + 1);
            interfaces[interfaces.length - 1] = Service.class;
            try {
                proxyInstance = Proxy.newProxyInstance(tcl, interfaces, proxy);
            } catch (IllegalArgumentException e5) {
                proxyInstance = Proxy.newProxyInstance(tcl, serviceInterfaces, proxy);
            }
            if (ref.getHandlersSize() > 0) {
                HandlerRegistry handlerRegistry = service.getHandlerRegistry();
                List<String> soaproles = new ArrayList<>();
                while (ref.getHandlersSize() > 0) {
                    HandlerRef handlerRef = ref.getHandler();
                    HandlerInfo handlerInfo = new HandlerInfo();
                    RefAddr tmp5 = handlerRef.get(HandlerRef.HANDLER_CLASS);
                    if (tmp5 == null || tmp5.getContent() == null) {
                        break;
                    }
                    try {
                        Class<?> handlerClass = tcl.loadClass((String) tmp5.getContent());
                        List<QName> headers = new ArrayList<>();
                        Hashtable<String, String> config = new Hashtable<>();
                        List<String> portNames = new ArrayList<>();
                        int i2 = 0;
                        while (i2 < handlerRef.size()) {
                            if (HandlerRef.HANDLER_LOCALPART.equals(handlerRef.get(i2).getType())) {
                                String namespace = "";
                                String localpart = (String) handlerRef.get(i2).getContent();
                                if (HandlerRef.HANDLER_NAMESPACE.equals(handlerRef.get(i2 + 1).getType())) {
                                    i2++;
                                    namespace = (String) handlerRef.get(i2).getContent();
                                }
                                QName header = new QName(namespace, localpart);
                                headers.add(header);
                            } else if (HandlerRef.HANDLER_PARAMNAME.equals(handlerRef.get(i2).getType())) {
                                String paramValue = "";
                                String paramName = (String) handlerRef.get(i2).getContent();
                                if (HandlerRef.HANDLER_PARAMVALUE.equals(handlerRef.get(i2 + 1).getType())) {
                                    i2++;
                                    paramValue = (String) handlerRef.get(i2).getContent();
                                }
                                config.put(paramName, paramValue);
                            } else if (HandlerRef.HANDLER_SOAPROLE.equals(handlerRef.get(i2).getType())) {
                                String soaprole = (String) handlerRef.get(i2).getContent();
                                soaproles.add(soaprole);
                            } else if (HandlerRef.HANDLER_PORTNAME.equals(handlerRef.get(i2).getType())) {
                                String portName2 = (String) handlerRef.get(i2).getContent();
                                portNames.add(portName2);
                            }
                            i2++;
                        }
                        handlerInfo.setHandlerClass(handlerClass);
                        handlerInfo.setHeaders((QName[]) headers.toArray(new QName[0]));
                        handlerInfo.setHandlerConfig(config);
                        if (!portNames.isEmpty()) {
                            for (String portName3 : portNames) {
                                initHandlerChain(new QName(portName3), handlerRegistry, handlerInfo, soaproles);
                            }
                        } else {
                            Enumeration<QName> e6 = portComponentRef.elements();
                            while (e6.hasMoreElements()) {
                                initHandlerChain(e6.nextElement(), handlerRegistry, handlerInfo, soaproles);
                            }
                        }
                    } catch (ClassNotFoundException e7) {
                    }
                }
            }
            return proxyInstance;
        }
        return null;
    }

    private String getSOAPLocation(Port port) {
        String endpoint = null;
        List<ExtensibilityElement> extensions = port.getExtensibilityElements();
        for (ExtensibilityElement extensibilityElement : extensions) {
            if (extensibilityElement instanceof SOAPAddress) {
                SOAPAddress addr = (SOAPAddress) extensibilityElement;
                endpoint = addr.getLocationURI();
            }
        }
        return endpoint;
    }

    private void initHandlerChain(QName portName, HandlerRegistry handlerRegistry, HandlerInfo handlerInfo, List<String> soaprolesToAdd) {
        HandlerChain handlerChain = handlerRegistry.getHandlerChain(portName);
        for (Handler handler : handlerChain) {
            handler.init(handlerInfo);
        }
        String[] soaprolesRegistered = handlerChain.getRoles();
        String[] soaproles = new String[soaprolesRegistered.length + soaprolesToAdd.size()];
        int i = 0;
        while (i < soaprolesRegistered.length) {
            soaproles[i] = soaprolesRegistered[i];
            i++;
        }
        for (int j = 0; j < soaprolesToAdd.size(); j++) {
            soaproles[i + j] = soaprolesToAdd.get(j);
        }
        handlerChain.setRoles(soaproles);
        handlerRegistry.setHandlerChain(portName, handlerChain);
    }
}
