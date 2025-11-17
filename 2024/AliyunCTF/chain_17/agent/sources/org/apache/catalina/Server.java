package org.apache.catalina;

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.catalina.startup.Catalina;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/Server.class */
public interface Server extends Lifecycle {
    NamingResourcesImpl getGlobalNamingResources();

    void setGlobalNamingResources(NamingResourcesImpl namingResourcesImpl);

    javax.naming.Context getGlobalNamingContext();

    int getPort();

    void setPort(int i);

    int getPortOffset();

    void setPortOffset(int i);

    int getPortWithOffset();

    String getAddress();

    void setAddress(String str);

    String getShutdown();

    void setShutdown(String str);

    ClassLoader getParentClassLoader();

    void setParentClassLoader(ClassLoader classLoader);

    Catalina getCatalina();

    void setCatalina(Catalina catalina);

    File getCatalinaBase();

    void setCatalinaBase(File file);

    File getCatalinaHome();

    void setCatalinaHome(File file);

    int getUtilityThreads();

    void setUtilityThreads(int i);

    void addService(Service service);

    void await();

    Service findService(String str);

    Service[] findServices();

    void removeService(Service service);

    Object getNamingToken();

    ScheduledExecutorService getUtilityExecutor();
}
