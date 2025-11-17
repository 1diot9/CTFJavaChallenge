package jakarta.servlet;

import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.descriptor.JspConfigDescriptor;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/ServletContext.class */
public interface ServletContext {
    public static final String TEMPDIR = "jakarta.servlet.context.tempdir";
    public static final String ORDERED_LIBS = "jakarta.servlet.context.orderedLibs";

    String getContextPath();

    ServletContext getContext(String str);

    int getMajorVersion();

    int getMinorVersion();

    int getEffectiveMajorVersion();

    int getEffectiveMinorVersion();

    String getMimeType(String str);

    Set<String> getResourcePaths(String str);

    URL getResource(String str) throws MalformedURLException;

    InputStream getResourceAsStream(String str);

    RequestDispatcher getRequestDispatcher(String str);

    RequestDispatcher getNamedDispatcher(String str);

    void log(String str);

    void log(String str, Throwable th);

    String getRealPath(String str);

    String getServerInfo();

    String getInitParameter(String str);

    Enumeration<String> getInitParameterNames();

    boolean setInitParameter(String str, String str2);

    Object getAttribute(String str);

    Enumeration<String> getAttributeNames();

    void setAttribute(String str, Object obj);

    void removeAttribute(String str);

    String getServletContextName();

    ServletRegistration.Dynamic addServlet(String str, String str2);

    ServletRegistration.Dynamic addServlet(String str, Servlet servlet);

    ServletRegistration.Dynamic addServlet(String str, Class<? extends Servlet> cls);

    ServletRegistration.Dynamic addJspFile(String str, String str2);

    <T extends Servlet> T createServlet(Class<T> cls) throws ServletException;

    ServletRegistration getServletRegistration(String str);

    Map<String, ? extends ServletRegistration> getServletRegistrations();

    FilterRegistration.Dynamic addFilter(String str, String str2);

    FilterRegistration.Dynamic addFilter(String str, Filter filter);

    FilterRegistration.Dynamic addFilter(String str, Class<? extends Filter> cls);

    <T extends Filter> T createFilter(Class<T> cls) throws ServletException;

    FilterRegistration getFilterRegistration(String str);

    Map<String, ? extends FilterRegistration> getFilterRegistrations();

    SessionCookieConfig getSessionCookieConfig();

    void setSessionTrackingModes(Set<SessionTrackingMode> set);

    Set<SessionTrackingMode> getDefaultSessionTrackingModes();

    Set<SessionTrackingMode> getEffectiveSessionTrackingModes();

    void addListener(String str);

    <T extends EventListener> void addListener(T t);

    void addListener(Class<? extends EventListener> cls);

    <T extends EventListener> T createListener(Class<T> cls) throws ServletException;

    JspConfigDescriptor getJspConfigDescriptor();

    ClassLoader getClassLoader();

    void declareRoles(String... strArr);

    String getVirtualServerName();

    int getSessionTimeout();

    void setSessionTimeout(int i);

    String getRequestCharacterEncoding();

    void setRequestCharacterEncoding(String str);

    String getResponseCharacterEncoding();

    void setResponseCharacterEncoding(String str);
}
