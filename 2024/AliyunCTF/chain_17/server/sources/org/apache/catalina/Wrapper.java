package org.apache.catalina;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/Wrapper.class */
public interface Wrapper extends Container {
    public static final String ADD_MAPPING_EVENT = "addMapping";
    public static final String REMOVE_MAPPING_EVENT = "removeMapping";

    long getAvailable();

    void setAvailable(long j);

    int getLoadOnStartup();

    void setLoadOnStartup(int i);

    String getRunAs();

    void setRunAs(String str);

    String getServletClass();

    void setServletClass(String str);

    String[] getServletMethods() throws ServletException;

    boolean isUnavailable();

    Servlet getServlet();

    void setServlet(Servlet servlet);

    void addInitParameter(String str, String str2);

    void addMapping(String str);

    void addSecurityReference(String str, String str2);

    Servlet allocate() throws ServletException;

    void deallocate(Servlet servlet) throws ServletException;

    String findInitParameter(String str);

    String[] findInitParameters();

    String[] findMappings();

    String findSecurityReference(String str);

    String[] findSecurityReferences();

    void incrementErrorCount();

    void load() throws ServletException;

    void removeInitParameter(String str);

    void removeMapping(String str);

    void removeSecurityReference(String str);

    void unavailable(UnavailableException unavailableException);

    void unload() throws ServletException;

    MultipartConfigElement getMultipartConfigElement();

    void setMultipartConfigElement(MultipartConfigElement multipartConfigElement);

    boolean isAsyncSupported();

    void setAsyncSupported(boolean z);

    boolean isEnabled();

    void setEnabled(boolean z);

    boolean isOverridable();

    void setOverridable(boolean z);
}
