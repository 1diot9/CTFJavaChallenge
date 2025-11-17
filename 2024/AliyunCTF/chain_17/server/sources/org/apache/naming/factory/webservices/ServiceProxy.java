package org.apache.naming.factory.webservices;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.util.Hashtable;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceException;
import org.apache.naming.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/naming/factory/webservices/ServiceProxy.class */
public class ServiceProxy implements InvocationHandler {
    private final Service service;
    private Hashtable<String, QName> portComponentRef = null;
    private static final StringManager sm = StringManager.getManager((Class<?>) ServiceProxy.class);
    private static Method portQNameClass = null;
    private static Method portClass = null;

    public ServiceProxy(Service service) throws ServiceException {
        this.service = service;
        try {
            portQNameClass = Service.class.getDeclaredMethod("getPort", QName.class, Class.class);
            portClass = Service.class.getDeclaredMethod("getPort", Class.class);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override // java.lang.reflect.InvocationHandler
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (portQNameClass.equals(method)) {
            return getProxyPortQNameClass(args);
        }
        if (portClass.equals(method)) {
            return getProxyPortClass(args);
        }
        try {
            return method.invoke(this.service, args);
        } catch (InvocationTargetException ite) {
            throw ite.getTargetException();
        }
    }

    private Object getProxyPortQNameClass(Object[] args) throws ServiceException {
        QName name = (QName) args[0];
        String nameString = name.getLocalPart();
        Class<?> serviceendpointClass = (Class) args[1];
        Iterator<QName> ports = this.service.getPorts();
        while (ports.hasNext()) {
            QName portName = ports.next();
            String portnameString = portName.getLocalPart();
            if (portnameString.equals(nameString)) {
                return this.service.getPort(name, serviceendpointClass);
            }
        }
        throw new ServiceException(sm.getString("serviceProxy.portNotFound", name));
    }

    public void setPortComponentRef(Hashtable<String, QName> portComponentRef) {
        this.portComponentRef = portComponentRef;
    }

    private Remote getProxyPortClass(Object[] args) throws ServiceException {
        Class<?> serviceendpointClass = (Class) args[0];
        if (this.portComponentRef == null) {
            return this.service.getPort(serviceendpointClass);
        }
        QName portname = this.portComponentRef.get(serviceendpointClass.getName());
        if (portname != null) {
            return this.service.getPort(portname, serviceendpointClass);
        }
        return this.service.getPort(serviceendpointClass);
    }
}
