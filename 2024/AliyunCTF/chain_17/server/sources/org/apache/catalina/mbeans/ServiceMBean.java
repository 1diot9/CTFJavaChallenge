package org.apache.catalina.mbeans;

import javax.management.MBeanException;
import org.apache.catalina.Executor;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Constants;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/mbeans/ServiceMBean.class */
public class ServiceMBean extends BaseCatalinaMBean<Service> {
    public void addConnector(String address, int port, boolean isAjp, boolean isSSL) throws MBeanException {
        Service service = doGetManagedResource();
        String protocol = isAjp ? "AJP/1.3" : Constants.HTTP_11;
        Connector connector = new Connector(protocol);
        if (address != null && address.length() > 0) {
            connector.setProperty("address", address);
        }
        connector.setPort(port);
        connector.setSecure(isSSL);
        connector.setScheme(isSSL ? "https" : "http");
        service.addConnector(connector);
    }

    public void addExecutor(String type) throws MBeanException {
        Service service = doGetManagedResource();
        Executor executor = (Executor) newInstance(type);
        service.addExecutor(executor);
    }

    public String[] findConnectors() throws MBeanException {
        Service service = doGetManagedResource();
        Connector[] connectors = service.findConnectors();
        String[] str = new String[connectors.length];
        for (int i = 0; i < connectors.length; i++) {
            str[i] = connectors[i].toString();
        }
        return str;
    }

    public String[] findExecutors() throws MBeanException {
        Service service = doGetManagedResource();
        Executor[] executors = service.findExecutors();
        String[] str = new String[executors.length];
        for (int i = 0; i < executors.length; i++) {
            str[i] = executors[i].toString();
        }
        return str;
    }

    public String getExecutor(String name) throws MBeanException {
        Service service = doGetManagedResource();
        Executor executor = service.getExecutor(name);
        if (executor != null) {
            return executor.toString();
        }
        return null;
    }
}
