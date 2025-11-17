package org.apache.catalina.core;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextAttributeEvent;
import jakarta.servlet.ServletContextAttributeListener;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.ServletRequestAttributeListener;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.ServletSecurityElement;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.descriptor.JspConfigDescriptor;
import jakarta.servlet.http.HttpServletMapping;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionIdListener;
import jakarta.servlet.http.HttpSessionListener;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.naming.NamingException;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Service;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.mapper.MappingData;
import org.apache.catalina.util.Introspection;
import org.apache.catalina.util.ServerInfo;
import org.apache.catalina.util.URLEncoder;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.CharChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.http.RequestUtil;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ApplicationContext.class */
public class ApplicationContext implements ServletContext {
    private final StandardContext context;
    private final Service service;
    private static final StringManager sm = StringManager.getManager((Class<?>) ApplicationContext.class);
    private SessionCookieConfig sessionCookieConfig;
    protected Map<String, Object> attributes = new ConcurrentHashMap();
    private final Map<String, String> readOnlyAttributes = new ConcurrentHashMap();
    private final ServletContext facade = new ApplicationContextFacade(this);
    private final Map<String, String> parameters = new ConcurrentHashMap();
    private final ThreadLocal<DispatchData> dispatchData = new ThreadLocal<>();
    private Set<SessionTrackingMode> sessionTrackingModes = null;
    private Set<SessionTrackingMode> defaultSessionTrackingModes = null;
    private Set<SessionTrackingMode> supportedSessionTrackingModes = null;
    private boolean newServletContextListenerAllowed = true;

    public ApplicationContext(StandardContext context) {
        this.context = context;
        this.service = ((Engine) context.getParent().getParent()).getService();
        this.sessionCookieConfig = new ApplicationSessionCookieConfig(context);
        populateSessionTrackingModes();
    }

    @Override // jakarta.servlet.ServletContext
    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    @Override // jakarta.servlet.ServletContext
    public Enumeration<String> getAttributeNames() {
        Set<String> names = new HashSet<>(this.attributes.keySet());
        return Collections.enumeration(names);
    }

    @Override // jakarta.servlet.ServletContext
    public ServletContext getContext(String uri) {
        if (uri == null || !uri.startsWith("/")) {
            return null;
        }
        try {
            Container host = this.context.getParent();
            Context child = (Context) host.findChild(uri);
            if (child != null && !child.getState().isAvailable()) {
                child = null;
            }
            if (child == null) {
                int i = uri.indexOf("##");
                if (i > -1) {
                    uri = uri.substring(0, i);
                }
                MessageBytes hostMB = MessageBytes.newInstance();
                hostMB.setString(host.getName());
                MessageBytes pathMB = MessageBytes.newInstance();
                pathMB.setString(uri);
                MappingData mappingData = new MappingData();
                this.service.getMapper().map(hostMB, pathMB, null, mappingData);
                child = mappingData.context;
            }
            if (child == null) {
                return null;
            }
            if (this.context.getCrossContext()) {
                return child.getServletContext();
            }
            if (child == this.context) {
                return this.context.getServletContext();
            }
            return null;
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            return null;
        }
    }

    @Override // jakarta.servlet.ServletContext
    public String getContextPath() {
        return this.context.getPath();
    }

    @Override // jakarta.servlet.ServletContext
    public String getInitParameter(String name) {
        if (Globals.JASPER_XML_VALIDATION_TLD_INIT_PARAM.equals(name) && this.context.getTldValidation()) {
            return "true";
        }
        if (Globals.JASPER_XML_BLOCK_EXTERNAL_INIT_PARAM.equals(name) && !this.context.getXmlBlockExternal()) {
            return "false";
        }
        return this.parameters.get(name);
    }

    @Override // jakarta.servlet.ServletContext
    public Enumeration<String> getInitParameterNames() {
        Set<String> names = new HashSet<>(this.parameters.keySet());
        if (this.context.getTldValidation()) {
            names.add(Globals.JASPER_XML_VALIDATION_TLD_INIT_PARAM);
        }
        if (!this.context.getXmlBlockExternal()) {
            names.add(Globals.JASPER_XML_BLOCK_EXTERNAL_INIT_PARAM);
        }
        return Collections.enumeration(names);
    }

    @Override // jakarta.servlet.ServletContext
    public int getMajorVersion() {
        return 6;
    }

    @Override // jakarta.servlet.ServletContext
    public int getMinorVersion() {
        return 0;
    }

    @Override // jakarta.servlet.ServletContext
    public String getMimeType(String file) {
        int period;
        if (file == null || (period = file.lastIndexOf(46)) < 0) {
            return null;
        }
        String extension = file.substring(period + 1);
        if (extension.length() < 1) {
            return null;
        }
        return this.context.findMimeMapping(extension);
    }

    @Override // jakarta.servlet.ServletContext
    public RequestDispatcher getNamedDispatcher(String name) {
        Wrapper wrapper;
        if (name == null || (wrapper = (Wrapper) this.context.findChild(name)) == null) {
            return null;
        }
        return new ApplicationDispatcher(wrapper, null, null, null, null, null, name);
    }

    @Override // jakarta.servlet.ServletContext
    public String getRealPath(String path) {
        String validatedPath = validateResourcePath(path, true);
        return this.context.getRealPath(validatedPath);
    }

    @Override // jakarta.servlet.ServletContext
    public RequestDispatcher getRequestDispatcher(String path) {
        String uri;
        String queryString;
        String uri2;
        if (path == null) {
            return null;
        }
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException(sm.getString("applicationContext.requestDispatcher.iae", path));
        }
        int pos = path.indexOf(63);
        if (pos >= 0) {
            uri = path.substring(0, pos);
            queryString = path.substring(pos + 1);
        } else {
            uri = path;
            queryString = null;
        }
        String uriNoParams = stripPathParams(uri);
        String normalizedUri = RequestUtil.normalize(uriNoParams);
        if (normalizedUri == null) {
            return null;
        }
        if (getContext().getDispatchersUseEncodedPaths()) {
            String decodedUri = UDecoder.URLDecode(normalizedUri, StandardCharsets.UTF_8);
            normalizedUri = RequestUtil.normalize(decodedUri);
            if (!decodedUri.equals(normalizedUri)) {
                getContext().getLogger().warn(sm.getString("applicationContext.illegalDispatchPath", path), new IllegalArgumentException());
                return null;
            }
            uri2 = URLEncoder.DEFAULT.encode(getContextPath(), StandardCharsets.UTF_8) + uri;
        } else {
            uri2 = URLEncoder.DEFAULT.encode(getContextPath() + uri, StandardCharsets.UTF_8);
        }
        DispatchData dd = this.dispatchData.get();
        if (dd == null) {
            dd = new DispatchData();
            this.dispatchData.set(dd);
        }
        MessageBytes uriMB = dd.uriMB;
        MappingData mappingData = dd.mappingData;
        try {
            uriMB.setChars(MessageBytes.EMPTY_CHAR_ARRAY, 0, 0);
            CharChunk uriCC = uriMB.getCharChunk();
            try {
                uriCC.append(this.context.getPath());
                uriCC.append(normalizedUri);
                this.service.getMapper().map(this.context, uriMB, mappingData);
                if (mappingData.wrapper == null) {
                    uriMB.recycle();
                    mappingData.recycle();
                    return null;
                }
                Wrapper wrapper = mappingData.wrapper;
                String wrapperPath = mappingData.wrapperPath.toString();
                String pathInfo = mappingData.pathInfo.toString();
                HttpServletMapping mapping = new ApplicationMapping(mappingData).getHttpServletMapping();
                ApplicationDispatcher applicationDispatcher = new ApplicationDispatcher(wrapper, uri2, wrapperPath, pathInfo, queryString, mapping, null);
                uriMB.recycle();
                mappingData.recycle();
                return applicationDispatcher;
            } catch (Exception e) {
                log(sm.getString("applicationContext.mapping.error"), e);
                uriMB.recycle();
                mappingData.recycle();
                return null;
            }
        } catch (Throwable th) {
            uriMB.recycle();
            mappingData.recycle();
            throw th;
        }
    }

    static String stripPathParams(String input) {
        if (input.indexOf(59) < 0) {
            return input;
        }
        StringBuilder sb = new StringBuilder(input.length());
        int pos = 0;
        int limit = input.length();
        while (pos < limit) {
            int nextSemiColon = input.indexOf(59, pos);
            if (nextSemiColon < 0) {
                nextSemiColon = limit;
            }
            sb.append(input.substring(pos, nextSemiColon));
            int followingSlash = input.indexOf(47, nextSemiColon);
            if (followingSlash < 0) {
                pos = limit;
            } else {
                pos = followingSlash;
            }
        }
        return sb.toString();
    }

    @Override // jakarta.servlet.ServletContext
    public URL getResource(String path) throws MalformedURLException {
        String validatedPath = validateResourcePath(path, !this.context.getContextGetResourceRequiresSlash());
        if (validatedPath == null) {
            throw new MalformedURLException(sm.getString("applicationContext.requestDispatcher.iae", path));
        }
        WebResourceRoot resources = this.context.getResources();
        if (resources != null) {
            return resources.getResource(validatedPath).getURL();
        }
        return null;
    }

    @Override // jakarta.servlet.ServletContext
    public InputStream getResourceAsStream(String path) {
        WebResourceRoot resources;
        String validatedPath = validateResourcePath(path, !this.context.getContextGetResourceRequiresSlash());
        if (validatedPath != null && (resources = this.context.getResources()) != null) {
            return resources.getResource(validatedPath).getInputStream();
        }
        return null;
    }

    private String validateResourcePath(String path, boolean addMissingInitialSlash) {
        if (path == null) {
            return null;
        }
        if (!path.startsWith("/")) {
            if (addMissingInitialSlash) {
                return "/" + path;
            }
            return null;
        }
        return path;
    }

    @Override // jakarta.servlet.ServletContext
    public Set<String> getResourcePaths(String path) {
        if (path == null) {
            return null;
        }
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException(sm.getString("applicationContext.resourcePaths.iae", path));
        }
        WebResourceRoot resources = this.context.getResources();
        if (resources != null) {
            return resources.listWebAppPaths(path);
        }
        return null;
    }

    @Override // jakarta.servlet.ServletContext
    public String getServerInfo() {
        return ServerInfo.getServerInfo();
    }

    @Override // jakarta.servlet.ServletContext
    public String getServletContextName() {
        return this.context.getDisplayName();
    }

    @Override // jakarta.servlet.ServletContext
    public void log(String message) {
        this.context.getLogger().info(message);
    }

    @Override // jakarta.servlet.ServletContext
    public void log(String message, Throwable throwable) {
        this.context.getLogger().error(message, throwable);
    }

    @Override // jakarta.servlet.ServletContext
    public void removeAttribute(String name) {
        Object value;
        Object[] listeners;
        if (this.readOnlyAttributes.containsKey(name) || (value = this.attributes.remove(name)) == null || (listeners = this.context.getApplicationEventListeners()) == null || listeners.length == 0) {
            return;
        }
        ServletContextAttributeEvent event = new ServletContextAttributeEvent(this.context.getServletContext(), name, value);
        for (Object obj : listeners) {
            if (obj instanceof ServletContextAttributeListener) {
                ServletContextAttributeListener listener = (ServletContextAttributeListener) obj;
                try {
                    this.context.fireContainerEvent("beforeContextAttributeRemoved", listener);
                    listener.attributeRemoved(event);
                    this.context.fireContainerEvent("afterContextAttributeRemoved", listener);
                } catch (Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                    this.context.fireContainerEvent("afterContextAttributeRemoved", listener);
                    log(sm.getString("applicationContext.attributeEvent"), t);
                }
            }
        }
    }

    @Override // jakarta.servlet.ServletContext
    public void setAttribute(String name, Object value) {
        ServletContextAttributeEvent event;
        if (name == null) {
            throw new NullPointerException(sm.getString("applicationContext.setAttribute.namenull"));
        }
        if (value == null) {
            removeAttribute(name);
            return;
        }
        if (this.readOnlyAttributes.containsKey(name)) {
            return;
        }
        Object oldValue = this.attributes.put(name, value);
        boolean replaced = oldValue != null;
        Object[] listeners = this.context.getApplicationEventListeners();
        if (listeners == null || listeners.length == 0) {
            return;
        }
        if (replaced) {
            event = new ServletContextAttributeEvent(this.context.getServletContext(), name, oldValue);
        } else {
            event = new ServletContextAttributeEvent(this.context.getServletContext(), name, value);
        }
        for (Object obj : listeners) {
            if (obj instanceof ServletContextAttributeListener) {
                ServletContextAttributeListener listener = (ServletContextAttributeListener) obj;
                if (replaced) {
                    try {
                        this.context.fireContainerEvent("beforeContextAttributeReplaced", listener);
                        listener.attributeReplaced(event);
                        this.context.fireContainerEvent("afterContextAttributeReplaced", listener);
                    } catch (Throwable t) {
                        ExceptionUtils.handleThrowable(t);
                        if (replaced) {
                            this.context.fireContainerEvent("afterContextAttributeReplaced", listener);
                        } else {
                            this.context.fireContainerEvent("afterContextAttributeAdded", listener);
                        }
                        log(sm.getString("applicationContext.attributeEvent"), t);
                    }
                } else {
                    this.context.fireContainerEvent("beforeContextAttributeAdded", listener);
                    listener.attributeAdded(event);
                    this.context.fireContainerEvent("afterContextAttributeAdded", listener);
                }
            }
        }
    }

    @Override // jakarta.servlet.ServletContext
    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        return addFilter(filterName, className, null);
    }

    @Override // jakarta.servlet.ServletContext
    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        return addFilter(filterName, null, filter);
    }

    @Override // jakarta.servlet.ServletContext
    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        return addFilter(filterName, filterClass.getName(), null);
    }

    private FilterRegistration.Dynamic addFilter(String filterName, String filterClass, Filter filter) throws IllegalStateException {
        if (filterName == null || filterName.equals("")) {
            throw new IllegalArgumentException(sm.getString("applicationContext.invalidFilterName", filterName));
        }
        checkState("applicationContext.addFilter.ise");
        FilterDef filterDef = this.context.findFilterDef(filterName);
        if (filterDef == null) {
            filterDef = new FilterDef();
            filterDef.setFilterName(filterName);
            this.context.addFilterDef(filterDef);
        } else if (filterDef.getFilterName() != null && filterDef.getFilterClass() != null) {
            return null;
        }
        if (filter == null) {
            filterDef.setFilterClass(filterClass);
        } else {
            filterDef.setFilterClass(filter.getClass().getName());
            filterDef.setFilter(filter);
        }
        return new ApplicationFilterRegistration(filterDef, this.context);
    }

    @Override // jakarta.servlet.ServletContext
    public <T extends Filter> T createFilter(Class<T> c) throws ServletException {
        try {
            return (T) this.context.getInstanceManager().newInstance(c.getName());
        } catch (InvocationTargetException e) {
            ExceptionUtils.handleThrowable(e.getCause());
            throw new ServletException(e);
        } catch (ReflectiveOperationException | NamingException e2) {
            throw new ServletException(e2);
        }
    }

    @Override // jakarta.servlet.ServletContext
    public FilterRegistration getFilterRegistration(String filterName) {
        FilterDef filterDef = this.context.findFilterDef(filterName);
        if (filterDef == null) {
            return null;
        }
        return new ApplicationFilterRegistration(filterDef, this.context);
    }

    @Override // jakarta.servlet.ServletContext
    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        return addServlet(servletName, className, null, null);
    }

    @Override // jakarta.servlet.ServletContext
    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        return addServlet(servletName, null, servlet, null);
    }

    @Override // jakarta.servlet.ServletContext
    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        return addServlet(servletName, servletClass.getName(), null, null);
    }

    @Override // jakarta.servlet.ServletContext
    public ServletRegistration.Dynamic addJspFile(String jspName, String jspFile) {
        String jspServletClassName;
        if (jspFile == null || !jspFile.startsWith("/")) {
            throw new IllegalArgumentException(sm.getString("applicationContext.addJspFile.iae", jspFile));
        }
        Map<String, String> jspFileInitParams = new HashMap<>();
        Wrapper jspServlet = (Wrapper) this.context.findChild("jsp");
        if (jspServlet == null) {
            jspServletClassName = Constants.JSP_SERVLET_CLASS;
        } else {
            jspServletClassName = jspServlet.getServletClass();
            String[] params = jspServlet.findInitParameters();
            for (String param : params) {
                jspFileInitParams.put(param, jspServlet.findInitParameter(param));
            }
        }
        jspFileInitParams.put("jspFile", jspFile);
        return addServlet(jspName, jspServletClassName, null, jspFileInitParams);
    }

    private ServletRegistration.Dynamic addServlet(String servletName, String servletClass, Servlet servlet, Map<String, String> initParams) throws IllegalStateException {
        if (servletName == null || servletName.equals("")) {
            throw new IllegalArgumentException(sm.getString("applicationContext.invalidServletName", servletName));
        }
        checkState("applicationContext.addServlet.ise");
        Wrapper wrapper = (Wrapper) this.context.findChild(servletName);
        if (wrapper == null) {
            wrapper = this.context.createWrapper();
            wrapper.setName(servletName);
            this.context.addChild(wrapper);
        } else if (wrapper.getName() != null && wrapper.getServletClass() != null) {
            if (wrapper.isOverridable()) {
                wrapper.setOverridable(false);
            } else {
                return null;
            }
        }
        ServletSecurity annotation = null;
        if (servlet == null) {
            wrapper.setServletClass(servletClass);
            Class<?> clazz = Introspection.loadClass(this.context, servletClass);
            if (clazz != null) {
                annotation = (ServletSecurity) clazz.getAnnotation(ServletSecurity.class);
            }
        } else {
            wrapper.setServletClass(servlet.getClass().getName());
            wrapper.setServlet(servlet);
            if (this.context.wasCreatedDynamicServlet(servlet)) {
                annotation = (ServletSecurity) servlet.getClass().getAnnotation(ServletSecurity.class);
            }
        }
        if (initParams != null) {
            for (Map.Entry<String, String> initParam : initParams.entrySet()) {
                wrapper.addInitParameter(initParam.getKey(), initParam.getValue());
            }
        }
        ServletRegistration.Dynamic registration = new ApplicationServletRegistration(wrapper, this.context);
        if (annotation != null) {
            registration.setServletSecurity(new ServletSecurityElement(annotation));
        }
        return registration;
    }

    @Override // jakarta.servlet.ServletContext
    public <T extends Servlet> T createServlet(Class<T> c) throws ServletException {
        try {
            T t = (T) this.context.getInstanceManager().newInstance(c.getName());
            this.context.dynamicServletCreated(t);
            return t;
        } catch (InvocationTargetException e) {
            ExceptionUtils.handleThrowable(e.getCause());
            throw new ServletException(e);
        } catch (ReflectiveOperationException | NamingException e2) {
            throw new ServletException(e2);
        }
    }

    @Override // jakarta.servlet.ServletContext
    public ServletRegistration getServletRegistration(String servletName) {
        Wrapper wrapper = (Wrapper) this.context.findChild(servletName);
        if (wrapper == null) {
            return null;
        }
        return new ApplicationServletRegistration(wrapper, this.context);
    }

    @Override // jakarta.servlet.ServletContext
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return this.defaultSessionTrackingModes;
    }

    private void populateSessionTrackingModes() {
        this.defaultSessionTrackingModes = EnumSet.of(SessionTrackingMode.URL);
        this.supportedSessionTrackingModes = EnumSet.of(SessionTrackingMode.URL);
        if (this.context.getCookies()) {
            this.defaultSessionTrackingModes.add(SessionTrackingMode.COOKIE);
            this.supportedSessionTrackingModes.add(SessionTrackingMode.COOKIE);
        }
        Connector[] connectors = this.service.findConnectors();
        for (Connector connector : connectors) {
            if (Boolean.TRUE.equals(connector.getProperty("SSLEnabled"))) {
                this.supportedSessionTrackingModes.add(SessionTrackingMode.SSL);
                return;
            }
        }
    }

    @Override // jakarta.servlet.ServletContext
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        if (this.sessionTrackingModes != null) {
            return this.sessionTrackingModes;
        }
        return this.defaultSessionTrackingModes;
    }

    @Override // jakarta.servlet.ServletContext
    public SessionCookieConfig getSessionCookieConfig() {
        return this.sessionCookieConfig;
    }

    @Override // jakarta.servlet.ServletContext
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
        checkState("applicationContext.setSessionTracking.ise");
        for (SessionTrackingMode sessionTrackingMode : sessionTrackingModes) {
            if (!this.supportedSessionTrackingModes.contains(sessionTrackingMode)) {
                throw new IllegalArgumentException(sm.getString("applicationContext.setSessionTracking.iae.invalid", sessionTrackingMode.toString(), getContextPath()));
            }
        }
        if (sessionTrackingModes.contains(SessionTrackingMode.SSL) && sessionTrackingModes.size() > 1) {
            throw new IllegalArgumentException(sm.getString("applicationContext.setSessionTracking.iae.ssl", getContextPath()));
        }
        this.sessionTrackingModes = sessionTrackingModes;
    }

    @Override // jakarta.servlet.ServletContext
    public boolean setInitParameter(String name, String value) {
        if (name == null) {
            throw new NullPointerException(sm.getString("applicationContext.setAttribute.namenull"));
        }
        checkState("applicationContext.setInitParam.ise");
        return this.parameters.putIfAbsent(name, value) == null;
    }

    @Override // jakarta.servlet.ServletContext
    public void addListener(Class<? extends EventListener> listenerClass) {
        try {
            EventListener listener = createListener(listenerClass);
            addListener((ApplicationContext) listener);
        } catch (ServletException e) {
            throw new IllegalArgumentException(sm.getString("applicationContext.addListener.iae.init", listenerClass.getName()), e);
        }
    }

    @Override // jakarta.servlet.ServletContext
    public void addListener(String className) {
        try {
            if (this.context.getInstanceManager() != null) {
                Object obj = this.context.getInstanceManager().newInstance(className);
                if (!(obj instanceof EventListener)) {
                    throw new IllegalArgumentException(sm.getString("applicationContext.addListener.iae.wrongType", className));
                }
                EventListener listener = (EventListener) obj;
                addListener((ApplicationContext) listener);
            }
        } catch (InvocationTargetException e) {
            ExceptionUtils.handleThrowable(e.getCause());
            throw new IllegalArgumentException(sm.getString("applicationContext.addListener.iae.cnfe", className), e);
        } catch (ReflectiveOperationException | NamingException e2) {
            throw new IllegalArgumentException(sm.getString("applicationContext.addListener.iae.cnfe", className), e2);
        }
    }

    @Override // jakarta.servlet.ServletContext
    public <T extends EventListener> void addListener(T t) {
        checkState("applicationContext.addListener.ise");
        boolean match = false;
        if ((t instanceof ServletContextAttributeListener) || (t instanceof ServletRequestListener) || (t instanceof ServletRequestAttributeListener) || (t instanceof HttpSessionIdListener) || (t instanceof HttpSessionAttributeListener)) {
            this.context.addApplicationEventListener(t);
            match = true;
        }
        if ((t instanceof HttpSessionListener) || ((t instanceof ServletContextListener) && this.newServletContextListenerAllowed)) {
            this.context.addApplicationLifecycleListener(t);
            match = true;
        }
        if (match) {
            return;
        }
        if (t instanceof ServletContextListener) {
            throw new IllegalArgumentException(sm.getString("applicationContext.addListener.iae.sclNotAllowed", t.getClass().getName()));
        }
        throw new IllegalArgumentException(sm.getString("applicationContext.addListener.iae.wrongType", t.getClass().getName()));
    }

    @Override // jakarta.servlet.ServletContext
    public <T extends EventListener> T createListener(Class<T> c) throws ServletException {
        try {
            try {
                T t = (T) this.context.getInstanceManager().newInstance((Class<?>) c);
                if ((t instanceof ServletContextListener) || (t instanceof ServletContextAttributeListener) || (t instanceof ServletRequestListener) || (t instanceof ServletRequestAttributeListener) || (t instanceof HttpSessionListener) || (t instanceof HttpSessionIdListener) || (t instanceof HttpSessionAttributeListener)) {
                    return t;
                }
                throw new IllegalArgumentException(sm.getString("applicationContext.addListener.iae.wrongType", t.getClass().getName()));
            } catch (ReflectiveOperationException | NamingException e) {
                throw new ServletException(e);
            }
        } catch (InvocationTargetException e2) {
            ExceptionUtils.handleThrowable(e2.getCause());
            throw new ServletException(e2);
        }
    }

    @Override // jakarta.servlet.ServletContext
    public void declareRoles(String... roleNames) {
        checkState("applicationContext.addRole.ise");
        if (roleNames == null) {
            throw new IllegalArgumentException(sm.getString("applicationContext.roles.iae", getContextPath()));
        }
        for (String role : roleNames) {
            if (role == null || role.isEmpty()) {
                throw new IllegalArgumentException(sm.getString("applicationContext.role.iae", getContextPath()));
            }
            this.context.addSecurityRole(role);
        }
    }

    @Override // jakarta.servlet.ServletContext
    public ClassLoader getClassLoader() {
        ClassLoader parent;
        ClassLoader result = this.context.getLoader().getClassLoader();
        if (Globals.IS_SECURITY_ENABLED) {
            ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            ClassLoader classLoader = result;
            while (true) {
                parent = classLoader;
                if (parent == null || parent == tccl) {
                    break;
                }
                classLoader = parent.getParent();
            }
            if (parent == null) {
                System.getSecurityManager().checkPermission(new RuntimePermission("getClassLoader"));
            }
        }
        return result;
    }

    @Override // jakarta.servlet.ServletContext
    public int getEffectiveMajorVersion() {
        return this.context.getEffectiveMajorVersion();
    }

    @Override // jakarta.servlet.ServletContext
    public int getEffectiveMinorVersion() {
        return this.context.getEffectiveMinorVersion();
    }

    @Override // jakarta.servlet.ServletContext
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        Map<String, ApplicationFilterRegistration> result = new HashMap<>();
        FilterDef[] filterDefs = this.context.findFilterDefs();
        for (FilterDef filterDef : filterDefs) {
            result.put(filterDef.getFilterName(), new ApplicationFilterRegistration(filterDef, this.context));
        }
        return result;
    }

    @Override // jakarta.servlet.ServletContext
    public JspConfigDescriptor getJspConfigDescriptor() {
        return this.context.getJspConfigDescriptor();
    }

    @Override // jakarta.servlet.ServletContext
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        Map<String, ApplicationServletRegistration> result = new HashMap<>();
        Container[] wrappers = this.context.findChildren();
        for (Container wrapper : wrappers) {
            result.put(wrapper.getName(), new ApplicationServletRegistration((Wrapper) wrapper, this.context));
        }
        return result;
    }

    @Override // jakarta.servlet.ServletContext
    public String getVirtualServerName() {
        Container host = this.context.getParent();
        Container engine = host.getParent();
        return engine.getName() + "/" + host.getName();
    }

    @Override // jakarta.servlet.ServletContext
    public int getSessionTimeout() {
        return this.context.getSessionTimeout();
    }

    @Override // jakarta.servlet.ServletContext
    public void setSessionTimeout(int sessionTimeout) {
        checkState("applicationContext.setSessionTimeout.ise");
        this.context.setSessionTimeout(sessionTimeout);
    }

    @Override // jakarta.servlet.ServletContext
    public String getRequestCharacterEncoding() {
        return this.context.getRequestCharacterEncoding();
    }

    @Override // jakarta.servlet.ServletContext
    public void setRequestCharacterEncoding(String encoding) {
        checkState("applicationContext.setRequestEncoding.ise");
        this.context.setRequestCharacterEncoding(encoding);
    }

    @Override // jakarta.servlet.ServletContext
    public String getResponseCharacterEncoding() {
        return this.context.getResponseCharacterEncoding();
    }

    @Override // jakarta.servlet.ServletContext
    public void setResponseCharacterEncoding(String encoding) {
        checkState("applicationContext.setResponseEncoding.ise");
        this.context.setResponseCharacterEncoding(encoding);
    }

    private void checkState(String messageKey) {
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(sm.getString(messageKey, getContextPath()));
        }
    }

    protected StandardContext getContext() {
        return this.context;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void clearAttributes() {
        List<String> list = new ArrayList<>(this.attributes.keySet());
        for (String key : list) {
            removeAttribute(key);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ServletContext getFacade() {
        return this.facade;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAttributeReadOnly(String name) {
        if (this.attributes.containsKey(name)) {
            this.readOnlyAttributes.put(name, name);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setNewServletContextListenerAllowed(boolean allowed) {
        this.newServletContextListenerAllowed = allowed;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ApplicationContext$DispatchData.class */
    private static final class DispatchData {
        public MessageBytes uriMB = MessageBytes.newInstance();
        public MappingData mappingData;

        DispatchData() {
            CharChunk uriCC = this.uriMB.getCharChunk();
            uriCC.setLimit(-1);
            this.mappingData = new MappingData();
        }
    }
}
