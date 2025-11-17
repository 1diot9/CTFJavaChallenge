package org.apache.catalina;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.mapper.Mapper;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/Service.class */
public interface Service extends Lifecycle {
    Engine getContainer();

    void setContainer(Engine engine);

    String getName();

    void setName(String str);

    Server getServer();

    void setServer(Server server);

    ClassLoader getParentClassLoader();

    void setParentClassLoader(ClassLoader classLoader);

    String getDomain();

    void addConnector(Connector connector);

    Connector[] findConnectors();

    void removeConnector(Connector connector);

    void addExecutor(Executor executor);

    Executor[] findExecutors();

    Executor getExecutor(String str);

    void removeExecutor(Executor executor);

    Mapper getMapper();
}
