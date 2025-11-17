package org.apache.catalina.core;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletRequestWrapper;
import jakarta.servlet.http.HttpServletMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.PushBuilder;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.util.ParameterMap;
import org.apache.catalina.util.RequestUtil;
import org.apache.catalina.util.URLEncoder;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.Parameters;
import org.apache.tomcat.util.res.StringManager;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ApplicationHttpRequest.class */
public class ApplicationHttpRequest extends HttpServletRequestWrapper {
    private static final StringManager sm = StringManager.getManager((Class<?>) ApplicationHttpRequest.class);
    protected static final String[] specials = {"jakarta.servlet.include.request_uri", "jakarta.servlet.include.context_path", "jakarta.servlet.include.servlet_path", "jakarta.servlet.include.path_info", "jakarta.servlet.include.query_string", RequestDispatcher.INCLUDE_MAPPING, "jakarta.servlet.forward.request_uri", "jakarta.servlet.forward.context_path", "jakarta.servlet.forward.servlet_path", "jakarta.servlet.forward.path_info", "jakarta.servlet.forward.query_string", RequestDispatcher.FORWARD_MAPPING};
    private static final Map<String, Integer> specialsMap = new HashMap();
    private static final int SPECIALS_FIRST_FORWARD_INDEX = 6;
    protected final Context context;
    protected String contextPath;
    protected final boolean crossContext;
    protected DispatcherType dispatcherType;
    protected Map<String, String[]> parameters;
    private boolean parsedParams;
    protected String pathInfo;
    private String queryParamString;
    protected String queryString;
    protected Object requestDispatcherPath;
    protected String requestURI;
    protected String servletPath;
    private HttpServletMapping mapping;
    protected Session session;
    protected final Object[] specialAttributes;

    static {
        for (int i = 0; i < specials.length; i++) {
            specialsMap.put(specials[i], Integer.valueOf(i));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ApplicationHttpRequest(HttpServletRequest request, Context context, boolean crossContext) {
        super(request);
        this.contextPath = null;
        this.dispatcherType = null;
        this.parameters = null;
        this.parsedParams = false;
        this.pathInfo = null;
        this.queryParamString = null;
        this.queryString = null;
        this.requestDispatcherPath = null;
        this.requestURI = null;
        this.servletPath = null;
        this.mapping = null;
        this.session = null;
        this.specialAttributes = new Object[specials.length];
        this.context = context;
        this.crossContext = crossContext;
        setRequest(request);
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public ServletContext getServletContext() {
        if (this.context == null) {
            return null;
        }
        return this.context.getServletContext();
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public Object getAttribute(String name) {
        if (name.equals(Globals.DISPATCHER_TYPE_ATTR)) {
            return this.dispatcherType;
        }
        if (name.equals(Globals.DISPATCHER_REQUEST_PATH_ATTR)) {
            if (this.requestDispatcherPath != null) {
                return this.requestDispatcherPath.toString();
            }
            return null;
        }
        int pos = getSpecial(name);
        if (pos == -1) {
            return getRequest().getAttribute(name);
        }
        if (this.specialAttributes[pos] == null && this.specialAttributes[6] == null && pos >= 6) {
            return getRequest().getAttribute(name);
        }
        return this.specialAttributes[pos];
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public Enumeration<String> getAttributeNames() {
        return new AttributeNamesEnumerator();
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public void removeAttribute(String name) {
        if (!removeSpecial(name)) {
            getRequest().removeAttribute(name);
        }
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public void setAttribute(String name, Object value) {
        if (name.equals(Globals.DISPATCHER_TYPE_ATTR)) {
            this.dispatcherType = (DispatcherType) value;
        } else if (name.equals(Globals.DISPATCHER_REQUEST_PATH_ATTR)) {
            this.requestDispatcherPath = value;
        } else if (!setSpecial(name, value)) {
            getRequest().setAttribute(name, value);
        }
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public RequestDispatcher getRequestDispatcher(String path) {
        String requestPath;
        String relative;
        if (this.context == null || path == null) {
            return null;
        }
        int fragmentPos = path.indexOf(35);
        if (fragmentPos > -1) {
            this.context.getLogger().warn(sm.getString("applicationHttpRequest.fragmentInDispatchPath", path));
            path = path.substring(0, fragmentPos);
        }
        if (path.startsWith("/")) {
            return this.context.getServletContext().getRequestDispatcher(path);
        }
        String servletPath = (String) getAttribute("jakarta.servlet.include.servlet_path");
        if (servletPath == null) {
            servletPath = getServletPath();
        }
        String pathInfo = getPathInfo();
        if (pathInfo == null) {
            requestPath = servletPath;
        } else {
            requestPath = servletPath + pathInfo;
        }
        int pos = requestPath.lastIndexOf(47);
        if (this.context.getDispatchersUseEncodedPaths()) {
            if (pos >= 0) {
                relative = URLEncoder.DEFAULT.encode(requestPath.substring(0, pos + 1), StandardCharsets.UTF_8) + path;
            } else {
                relative = URLEncoder.DEFAULT.encode(requestPath, StandardCharsets.UTF_8) + path;
            }
        } else if (pos >= 0) {
            relative = requestPath.substring(0, pos + 1) + path;
        } else {
            relative = requestPath + path;
        }
        return this.context.getServletContext().getRequestDispatcher(relative);
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public DispatcherType getDispatcherType() {
        return this.dispatcherType;
    }

    @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
    public String getContextPath() {
        return this.contextPath;
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public String getParameter(String name) {
        parseParameters();
        String[] value = this.parameters.get(name);
        if (value == null) {
            return null;
        }
        return value[0];
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public Map<String, String[]> getParameterMap() {
        parseParameters();
        return this.parameters;
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public Enumeration<String> getParameterNames() {
        parseParameters();
        return Collections.enumeration(this.parameters.keySet());
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public String[] getParameterValues(String name) {
        parseParameters();
        return this.parameters.get(name);
    }

    @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
    public String getPathInfo() {
        return this.pathInfo;
    }

    @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
    public String getPathTranslated() {
        if (getPathInfo() == null || getServletContext() == null) {
            return null;
        }
        return getServletContext().getRealPath(getPathInfo());
    }

    @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
    public String getQueryString() {
        return this.queryString;
    }

    @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
    public String getRequestURI() {
        return this.requestURI;
    }

    @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
    public StringBuffer getRequestURL() {
        return RequestUtil.getRequestURL(this);
    }

    @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
    public String getServletPath() {
        return this.servletPath;
    }

    @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
    public HttpServletMapping getHttpServletMapping() {
        return this.mapping;
    }

    @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
    public HttpSession getSession() {
        return getSession(true);
    }

    @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
    public HttpSession getSession(boolean create) {
        if (this.crossContext) {
            if (this.context == null) {
                return null;
            }
            if (this.session != null && this.session.isValid()) {
                return this.session.getSession();
            }
            HttpSession other = super.getSession(false);
            if (create && other == null) {
                other = super.getSession(true);
            }
            if (other != null) {
                Session localSession = null;
                try {
                    localSession = this.context.getManager().findSession(other.getId());
                    if (localSession != null) {
                        if (!localSession.isValid()) {
                            localSession = null;
                        }
                    }
                } catch (IOException e) {
                }
                if (localSession == null && create) {
                    localSession = this.context.getManager().createSession(other.getId());
                }
                if (localSession != null) {
                    localSession.access();
                    this.session = localSession;
                    return this.session.getSession();
                }
                return null;
            }
            return null;
        }
        return super.getSession(create);
    }

    @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
    public boolean isRequestedSessionIdValid() {
        Manager manager;
        if (this.crossContext) {
            String requestedSessionId = getRequestedSessionId();
            if (requestedSessionId == null || this.context == null || (manager = this.context.getManager()) == null) {
                return false;
            }
            Session session = null;
            try {
                session = manager.findSession(requestedSessionId);
            } catch (IOException e) {
            }
            if (session != null && session.isValid()) {
                return true;
            }
            return false;
        }
        return super.isRequestedSessionIdValid();
    }

    @Override // jakarta.servlet.http.HttpServletRequestWrapper, jakarta.servlet.http.HttpServletRequest
    public PushBuilder newPushBuilder() {
        ServletRequest current;
        ServletRequest request = getRequest();
        while (true) {
            current = request;
            if (!(current instanceof ServletRequestWrapper)) {
                break;
            }
            request = ((ServletRequestWrapper) current).getRequest();
        }
        if (current instanceof RequestFacade) {
            return ((RequestFacade) current).newPushBuilder(this);
        }
        return null;
    }

    public void recycle() {
        if (this.session != null) {
            try {
                this.session.endAccess();
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                this.context.getLogger().warn(sm.getString("applicationHttpRequest.sessionEndAccessFail"), t);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    void setRequest(HttpServletRequest request) {
        super.setRequest((ServletRequest) request);
        this.dispatcherType = (DispatcherType) request.getAttribute(Globals.DISPATCHER_TYPE_ATTR);
        this.requestDispatcherPath = request.getAttribute(Globals.DISPATCHER_REQUEST_PATH_ATTR);
        this.contextPath = request.getContextPath();
        this.pathInfo = request.getPathInfo();
        this.queryString = request.getQueryString();
        this.requestURI = request.getRequestURI();
        this.servletPath = request.getServletPath();
        this.mapping = request.getHttpServletMapping();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    void parseParameters() {
        if (this.parsedParams) {
            return;
        }
        this.parameters = new ParameterMap();
        this.parameters.putAll(getRequest().getParameterMap());
        mergeParameters();
        ((ParameterMap) this.parameters).setLocked(true);
        this.parsedParams = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setQueryParams(String queryString) {
        this.queryParamString = queryString;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMapping(HttpServletMapping mapping) {
        this.mapping = mapping;
    }

    protected boolean isSpecial(String name) {
        return specialsMap.containsKey(name);
    }

    protected int getSpecial(String name) {
        Integer index = specialsMap.get(name);
        if (index == null) {
            return -1;
        }
        return index.intValue();
    }

    protected boolean setSpecial(String name, Object value) {
        Integer index = specialsMap.get(name);
        if (index == null) {
            return false;
        }
        this.specialAttributes[index.intValue()] = value;
        return true;
    }

    protected boolean removeSpecial(String name) {
        return setSpecial(name, null);
    }

    private String[] mergeValues(String[] values1, String[] values2) {
        List<Object> results = new ArrayList<>();
        if (values1 != null) {
            results.addAll(Arrays.asList(values1));
        }
        if (values2 != null) {
            results.addAll(Arrays.asList(values2));
        }
        return (String[]) results.toArray(new String[0]);
    }

    private void mergeParameters() {
        if (this.queryParamString == null || this.queryParamString.length() < 1) {
            return;
        }
        Parameters paramParser = new Parameters();
        MessageBytes queryMB = MessageBytes.newInstance();
        queryMB.setString(this.queryParamString);
        String encoding = getCharacterEncoding();
        Charset charset = null;
        if (encoding != null) {
            try {
                charset = B2CConverter.getCharset(encoding);
                queryMB.setCharset(charset);
            } catch (UnsupportedEncodingException e) {
                charset = StandardCharsets.ISO_8859_1;
            }
        }
        paramParser.setQuery(queryMB);
        paramParser.setQueryStringCharset(charset);
        paramParser.handleQueryParameters();
        Enumeration<String> dispParamNames = paramParser.getParameterNames();
        while (dispParamNames.hasMoreElements()) {
            String dispParamName = dispParamNames.nextElement();
            String[] dispParamValues = paramParser.getParameterValues(dispParamName);
            String[] originalValues = this.parameters.get(dispParamName);
            if (originalValues == null) {
                this.parameters.put(dispParamName, dispParamValues);
            } else {
                this.parameters.put(dispParamName, mergeValues(dispParamValues, originalValues));
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ApplicationHttpRequest$AttributeNamesEnumerator.class */
    protected class AttributeNamesEnumerator implements Enumeration<String> {
        protected final int last;
        protected final Enumeration<String> parentEnumeration;
        protected int pos = -1;
        protected String next = null;

        public AttributeNamesEnumerator() {
            int last = -1;
            this.parentEnumeration = ApplicationHttpRequest.this.getRequest().getAttributeNames();
            int i = ApplicationHttpRequest.this.specialAttributes.length - 1;
            while (true) {
                if (i < 0) {
                    break;
                }
                if (ApplicationHttpRequest.this.getAttribute(ApplicationHttpRequest.specials[i]) == null) {
                    i--;
                } else {
                    last = i;
                    break;
                }
            }
            this.last = last;
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            if (this.pos == this.last && this.next == null) {
                String findNext = findNext();
                this.next = findNext;
                if (findNext == null) {
                    return false;
                }
            }
            return true;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Enumeration
        public String nextElement() {
            if (this.pos != this.last) {
                for (int i = this.pos + 1; i <= this.last; i++) {
                    if (ApplicationHttpRequest.this.getAttribute(ApplicationHttpRequest.specials[i]) != null) {
                        this.pos = i;
                        return ApplicationHttpRequest.specials[i];
                    }
                }
            }
            String result = this.next;
            if (this.next != null) {
                this.next = findNext();
                return result;
            }
            throw new NoSuchElementException();
        }

        protected String findNext() {
            String result = null;
            while (result == null && this.parentEnumeration.hasMoreElements()) {
                String current = this.parentEnumeration.nextElement();
                if (!ApplicationHttpRequest.this.isSpecial(current)) {
                    result = current;
                }
            }
            return result;
        }
    }
}
