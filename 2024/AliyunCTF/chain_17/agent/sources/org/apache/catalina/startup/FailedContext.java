package org.apache.catalina.startup;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletSecurityElement;
import jakarta.servlet.descriptor.JspConfigDescriptor;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.catalina.AccessLog;
import org.apache.catalina.Authenticator;
import org.apache.catalina.Cluster;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Loader;
import org.apache.catalina.Manager;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Realm;
import org.apache.catalina.ThreadBindingListener;
import org.apache.catalina.Valve;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.catalina.util.ContextName;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.juli.logging.Log;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.descriptor.web.ApplicationParameter;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.http.CookieProcessor;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/FailedContext.class */
public class FailedContext extends LifecycleMBeanBase implements Context {
    protected static final StringManager sm = StringManager.getManager(Constants.Package);
    private URL configFile;
    private String docBase;
    private Container parent;
    private String name = null;
    private String path = null;
    private String webappVersion = null;

    @Override // org.apache.catalina.Context
    public URL getConfigFile() {
        return this.configFile;
    }

    @Override // org.apache.catalina.Context
    public void setConfigFile(URL configFile) {
        this.configFile = configFile;
    }

    @Override // org.apache.catalina.Context
    public String getDocBase() {
        return this.docBase;
    }

    @Override // org.apache.catalina.Context
    public void setDocBase(String docBase) {
        this.docBase = docBase;
    }

    @Override // org.apache.catalina.Container
    public String getName() {
        return this.name;
    }

    @Override // org.apache.catalina.Container
    public void setName(String name) {
        this.name = name;
    }

    @Override // org.apache.catalina.Container
    public Container getParent() {
        return this.parent;
    }

    @Override // org.apache.catalina.Container
    public void setParent(Container parent) {
        this.parent = parent;
    }

    @Override // org.apache.catalina.Context
    public String getPath() {
        return this.path;
    }

    @Override // org.apache.catalina.Context
    public void setPath(String path) {
        this.path = path;
    }

    @Override // org.apache.catalina.Context
    public String getWebappVersion() {
        return this.webappVersion;
    }

    @Override // org.apache.catalina.Context
    public void setWebappVersion(String webappVersion) {
        this.webappVersion = webappVersion;
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getDomainInternal() {
        Container p = getParent();
        if (p == null) {
            return null;
        }
        return p.getDomain();
    }

    @Override // org.apache.catalina.Container
    public String getMBeanKeyProperties() {
        Container c = this;
        StringBuilder keyProperties = new StringBuilder();
        int containerCount = 0;
        while (true) {
            if (c instanceof Engine) {
                break;
            }
            if (c instanceof Context) {
                keyProperties.append(",context=");
                ContextName cn2 = new ContextName(c.getName(), false);
                keyProperties.append(cn2.getDisplayName());
            } else if (c instanceof Host) {
                keyProperties.append(",host=");
                keyProperties.append(c.getName());
            } else {
                if (c == null) {
                    keyProperties.append(",container");
                    int i = containerCount;
                    int i2 = containerCount + 1;
                    keyProperties.append(i);
                    keyProperties.append("=null");
                    break;
                }
                keyProperties.append(",container");
                int i3 = containerCount;
                containerCount++;
                keyProperties.append(i3);
                keyProperties.append('=');
                keyProperties.append(c.getName());
            }
            c = c.getParent();
        }
        return keyProperties.toString();
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getObjectNameKeyProperties() {
        StringBuilder keyProperties = new StringBuilder("j2eeType=WebModule,name=//");
        String hostname = getParent().getName();
        if (hostname == null) {
            keyProperties.append("DEFAULT");
        } else {
            keyProperties.append(hostname);
        }
        String contextName = getName();
        if (!contextName.startsWith("/")) {
            keyProperties.append('/');
        }
        keyProperties.append(contextName);
        keyProperties.append(",J2EEApplication=none,J2EEServer=none");
        return keyProperties.toString();
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void startInternal() throws LifecycleException {
        throw new LifecycleException(sm.getString("failedContext.start", getName()));
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void stopInternal() throws LifecycleException {
    }

    @Override // org.apache.catalina.Context
    public void addWatchedResource(String name) {
    }

    @Override // org.apache.catalina.Context
    public String[] findWatchedResources() {
        return new String[0];
    }

    @Override // org.apache.catalina.Context
    public void removeWatchedResource(String name) {
    }

    @Override // org.apache.catalina.Container
    public void addChild(Container child) {
    }

    @Override // org.apache.catalina.Container
    public Container findChild(String name) {
        return null;
    }

    @Override // org.apache.catalina.Container
    public Container[] findChildren() {
        return new Container[0];
    }

    @Override // org.apache.catalina.Container
    public void removeChild(Container child) {
    }

    public String toString() {
        return getName();
    }

    @Override // org.apache.catalina.Context
    public Loader getLoader() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setLoader(Loader loader) {
    }

    @Override // org.apache.catalina.Container
    public Log getLogger() {
        return null;
    }

    @Override // org.apache.catalina.Container
    public String getLogName() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public Manager getManager() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setManager(Manager manager) {
    }

    @Override // org.apache.catalina.Container
    public Pipeline getPipeline() {
        return null;
    }

    @Override // org.apache.catalina.Container
    public Cluster getCluster() {
        return null;
    }

    @Override // org.apache.catalina.Container
    public void setCluster(Cluster cluster) {
    }

    @Override // org.apache.catalina.Container
    public int getBackgroundProcessorDelay() {
        return -1;
    }

    @Override // org.apache.catalina.Container
    public void setBackgroundProcessorDelay(int delay) {
    }

    @Override // org.apache.catalina.Container
    public ClassLoader getParentClassLoader() {
        return null;
    }

    @Override // org.apache.catalina.Container
    public void setParentClassLoader(ClassLoader parent) {
    }

    @Override // org.apache.catalina.Container
    public Realm getRealm() {
        return null;
    }

    @Override // org.apache.catalina.Container
    public void setRealm(Realm realm) {
    }

    @Override // org.apache.catalina.Context
    public WebResourceRoot getResources() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setResources(WebResourceRoot resources) {
    }

    @Override // org.apache.catalina.Container
    public void backgroundProcess() {
    }

    @Override // org.apache.catalina.Container
    public void addContainerListener(ContainerListener listener) {
    }

    @Override // org.apache.catalina.Container
    public ContainerListener[] findContainerListeners() {
        return null;
    }

    @Override // org.apache.catalina.Container
    public void removeContainerListener(ContainerListener listener) {
    }

    @Override // org.apache.catalina.Container
    public void addPropertyChangeListener(PropertyChangeListener listener) {
    }

    @Override // org.apache.catalina.Container
    public void removePropertyChangeListener(PropertyChangeListener listener) {
    }

    @Override // org.apache.catalina.Container
    public void fireContainerEvent(String type, Object data) {
    }

    @Override // org.apache.catalina.Container
    public void logAccess(Request request, Response response, long time, boolean useDefault) {
    }

    @Override // org.apache.catalina.Container
    public AccessLog getAccessLog() {
        return null;
    }

    @Override // org.apache.catalina.Container
    public int getStartStopThreads() {
        return 0;
    }

    @Override // org.apache.catalina.Container
    public void setStartStopThreads(int startStopThreads) {
    }

    @Override // org.apache.catalina.Context
    public boolean getAllowCasualMultipartParsing() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setAllowCasualMultipartParsing(boolean allowCasualMultipartParsing) {
    }

    @Override // org.apache.catalina.Context
    public Object[] getApplicationEventListeners() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setApplicationEventListeners(Object[] listeners) {
    }

    @Override // org.apache.catalina.Context
    public Object[] getApplicationLifecycleListeners() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setApplicationLifecycleListeners(Object[] listeners) {
    }

    @Override // org.apache.catalina.Context
    public String getCharset(Locale locale) {
        return null;
    }

    @Override // org.apache.catalina.Context
    public boolean getConfigured() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setConfigured(boolean configured) {
    }

    @Override // org.apache.catalina.Context
    public boolean getCookies() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setCookies(boolean cookies) {
    }

    @Override // org.apache.catalina.Context
    public String getSessionCookieName() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setSessionCookieName(String sessionCookieName) {
    }

    @Override // org.apache.catalina.Context
    public boolean getUseHttpOnly() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setUseHttpOnly(boolean useHttpOnly) {
    }

    @Override // org.apache.catalina.Context
    public boolean getUsePartitioned() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setUsePartitioned(boolean usePartitioned) {
    }

    @Override // org.apache.catalina.Context
    public String getSessionCookieDomain() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setSessionCookieDomain(String sessionCookieDomain) {
    }

    @Override // org.apache.catalina.Context
    public String getSessionCookiePath() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setSessionCookiePath(String sessionCookiePath) {
    }

    @Override // org.apache.catalina.Context
    public boolean getSessionCookiePathUsesTrailingSlash() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setSessionCookiePathUsesTrailingSlash(boolean sessionCookiePathUsesTrailingSlash) {
    }

    @Override // org.apache.catalina.Context
    public boolean getCrossContext() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setCrossContext(boolean crossContext) {
    }

    @Override // org.apache.catalina.Context
    public String getAltDDName() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setAltDDName(String altDDName) {
    }

    @Override // org.apache.catalina.Context
    public boolean getDenyUncoveredHttpMethods() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setDenyUncoveredHttpMethods(boolean denyUncoveredHttpMethods) {
    }

    @Override // org.apache.catalina.Context
    public String getDisplayName() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setDisplayName(String displayName) {
    }

    @Override // org.apache.catalina.Context
    public boolean getDistributable() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setDistributable(boolean distributable) {
    }

    @Override // org.apache.catalina.Context
    public String getEncodedPath() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public boolean getIgnoreAnnotations() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setIgnoreAnnotations(boolean ignoreAnnotations) {
    }

    @Override // org.apache.catalina.Context
    public LoginConfig getLoginConfig() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setLoginConfig(LoginConfig config) {
    }

    @Override // org.apache.catalina.Context
    public NamingResourcesImpl getNamingResources() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setNamingResources(NamingResourcesImpl namingResources) {
    }

    @Override // org.apache.catalina.Context
    public String getPublicId() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setPublicId(String publicId) {
    }

    @Override // org.apache.catalina.Context
    public boolean getReloadable() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setReloadable(boolean reloadable) {
    }

    @Override // org.apache.catalina.Context
    public boolean getOverride() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setOverride(boolean override) {
    }

    @Override // org.apache.catalina.Context
    public boolean getPrivileged() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setPrivileged(boolean privileged) {
    }

    @Override // org.apache.catalina.Context
    public ServletContext getServletContext() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public int getSessionTimeout() {
        return 0;
    }

    @Override // org.apache.catalina.Context
    public void setSessionTimeout(int timeout) {
    }

    @Override // org.apache.catalina.Context
    public boolean getSwallowAbortedUploads() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setSwallowAbortedUploads(boolean swallowAbortedUploads) {
    }

    @Override // org.apache.catalina.Context
    public boolean getSwallowOutput() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setSwallowOutput(boolean swallowOutput) {
    }

    @Override // org.apache.catalina.Context
    public String getWrapperClass() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setWrapperClass(String wrapperClass) {
    }

    @Override // org.apache.catalina.Context
    public boolean getXmlNamespaceAware() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setXmlNamespaceAware(boolean xmlNamespaceAware) {
    }

    @Override // org.apache.catalina.Context
    public boolean getXmlValidation() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setXmlValidation(boolean xmlValidation) {
    }

    @Override // org.apache.catalina.Context
    public boolean getXmlBlockExternal() {
        return true;
    }

    @Override // org.apache.catalina.Context
    public void setXmlBlockExternal(boolean xmlBlockExternal) {
    }

    @Override // org.apache.catalina.Context
    public boolean getTldValidation() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setTldValidation(boolean tldValidation) {
    }

    @Override // org.apache.catalina.Context
    public JarScanner getJarScanner() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setJarScanner(JarScanner jarScanner) {
    }

    @Override // org.apache.catalina.Context
    public Authenticator getAuthenticator() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setLogEffectiveWebXml(boolean logEffectiveWebXml) {
    }

    @Override // org.apache.catalina.Context
    public boolean getLogEffectiveWebXml() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void addApplicationListener(String listener) {
    }

    @Override // org.apache.catalina.Context
    public String[] findApplicationListeners() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void removeApplicationListener(String listener) {
    }

    @Override // org.apache.catalina.Context
    public void addApplicationParameter(ApplicationParameter parameter) {
    }

    @Override // org.apache.catalina.Context
    public ApplicationParameter[] findApplicationParameters() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void removeApplicationParameter(String name) {
    }

    @Override // org.apache.catalina.Context
    public void addConstraint(SecurityConstraint constraint) {
    }

    @Override // org.apache.catalina.Context
    public SecurityConstraint[] findConstraints() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void removeConstraint(SecurityConstraint constraint) {
    }

    @Override // org.apache.catalina.Context
    public void addErrorPage(ErrorPage errorPage) {
    }

    @Override // org.apache.catalina.Context
    public ErrorPage findErrorPage(int errorCode) {
        return null;
    }

    @Override // org.apache.catalina.Context
    public ErrorPage findErrorPage(Throwable throwable) {
        return null;
    }

    @Override // org.apache.catalina.Context
    public ErrorPage[] findErrorPages() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void removeErrorPage(ErrorPage errorPage) {
    }

    @Override // org.apache.catalina.Context
    public void addFilterDef(FilterDef filterDef) {
    }

    @Override // org.apache.catalina.Context
    public FilterDef findFilterDef(String filterName) {
        return null;
    }

    @Override // org.apache.catalina.Context
    public FilterDef[] findFilterDefs() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void removeFilterDef(FilterDef filterDef) {
    }

    @Override // org.apache.catalina.Context
    public void addFilterMap(FilterMap filterMap) {
    }

    @Override // org.apache.catalina.Context
    public void addFilterMapBefore(FilterMap filterMap) {
    }

    @Override // org.apache.catalina.Context
    public FilterMap[] findFilterMaps() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void removeFilterMap(FilterMap filterMap) {
    }

    @Override // org.apache.catalina.Context
    public void addLocaleEncodingMappingParameter(String locale, String encoding) {
    }

    @Override // org.apache.catalina.Context
    public void addMimeMapping(String extension, String mimeType) {
    }

    @Override // org.apache.catalina.Context
    public String findMimeMapping(String extension) {
        return null;
    }

    @Override // org.apache.catalina.Context
    public String[] findMimeMappings() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void removeMimeMapping(String extension) {
    }

    @Override // org.apache.catalina.Context
    public void addParameter(String name, String value) {
    }

    @Override // org.apache.catalina.Context
    public String findParameter(String name) {
        return null;
    }

    @Override // org.apache.catalina.Context
    public String[] findParameters() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void removeParameter(String name) {
    }

    @Override // org.apache.catalina.Context
    public void addRoleMapping(String role, String link) {
    }

    @Override // org.apache.catalina.Context
    public String findRoleMapping(String role) {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void removeRoleMapping(String role) {
    }

    @Override // org.apache.catalina.Context
    public void addSecurityRole(String role) {
    }

    @Override // org.apache.catalina.Context
    public boolean findSecurityRole(String role) {
        return false;
    }

    @Override // org.apache.catalina.Context
    public String[] findSecurityRoles() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void removeSecurityRole(String role) {
    }

    @Override // org.apache.catalina.Context
    public void addServletMappingDecoded(String pattern, String name, boolean jspWildcard) {
    }

    @Override // org.apache.catalina.Context
    public String findServletMapping(String pattern) {
        return null;
    }

    @Override // org.apache.catalina.Context
    public String[] findServletMappings() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void removeServletMapping(String pattern) {
    }

    @Override // org.apache.catalina.Context
    public void addWelcomeFile(String name) {
    }

    @Override // org.apache.catalina.Context
    public boolean findWelcomeFile(String name) {
        return false;
    }

    @Override // org.apache.catalina.Context
    public String[] findWelcomeFiles() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void removeWelcomeFile(String name) {
    }

    @Override // org.apache.catalina.Context
    public void addWrapperLifecycle(String listener) {
    }

    @Override // org.apache.catalina.Context
    public String[] findWrapperLifecycles() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void removeWrapperLifecycle(String listener) {
    }

    @Override // org.apache.catalina.Context
    public void addWrapperListener(String listener) {
    }

    @Override // org.apache.catalina.Context
    public String[] findWrapperListeners() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void removeWrapperListener(String listener) {
    }

    @Override // org.apache.catalina.Context
    public InstanceManager createInstanceManager() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public Wrapper createWrapper() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public boolean fireRequestInitEvent(ServletRequest request) {
        return false;
    }

    @Override // org.apache.catalina.Context
    public boolean fireRequestDestroyEvent(ServletRequest request) {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void reload() {
    }

    @Override // org.apache.catalina.Context
    public String getRealPath(String path) {
        return null;
    }

    @Override // org.apache.catalina.Context
    public int getEffectiveMajorVersion() {
        return 0;
    }

    @Override // org.apache.catalina.Context
    public void setEffectiveMajorVersion(int major) {
    }

    @Override // org.apache.catalina.Context
    public int getEffectiveMinorVersion() {
        return 0;
    }

    @Override // org.apache.catalina.Context
    public void setEffectiveMinorVersion(int minor) {
    }

    @Override // org.apache.catalina.Context
    public JspConfigDescriptor getJspConfigDescriptor() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setJspConfigDescriptor(JspConfigDescriptor descriptor) {
    }

    @Override // org.apache.catalina.Context
    public void addServletContainerInitializer(ServletContainerInitializer sci, Set<Class<?>> classes) {
    }

    @Override // org.apache.catalina.Context
    public boolean getPaused() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public boolean isServlet22() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public Set<String> addServletSecurity(ServletRegistration.Dynamic registration, ServletSecurityElement servletSecurityElement) {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setResourceOnlyServlets(String resourceOnlyServlets) {
    }

    @Override // org.apache.catalina.Context
    public String getResourceOnlyServlets() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public boolean isResourceOnlyServlet(String servletName) {
        return false;
    }

    @Override // org.apache.catalina.Context
    public String getBaseName() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setFireRequestListenersOnForwards(boolean enable) {
    }

    @Override // org.apache.catalina.Context
    public boolean getFireRequestListenersOnForwards() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setPreemptiveAuthentication(boolean enable) {
    }

    @Override // org.apache.catalina.Context
    public boolean getPreemptiveAuthentication() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setSendRedirectBody(boolean enable) {
    }

    @Override // org.apache.catalina.Context
    public boolean getSendRedirectBody() {
        return false;
    }

    public synchronized void addValve(Valve valve) {
    }

    @Override // org.apache.catalina.Container
    public File getCatalinaBase() {
        return null;
    }

    @Override // org.apache.catalina.Container
    public File getCatalinaHome() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setAddWebinfClassesResources(boolean addWebinfClassesResources) {
    }

    @Override // org.apache.catalina.Context
    public boolean getAddWebinfClassesResources() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void addPostConstructMethod(String clazz, String method) {
    }

    @Override // org.apache.catalina.Context
    public void addPreDestroyMethod(String clazz, String method) {
    }

    @Override // org.apache.catalina.Context
    public void removePostConstructMethod(String clazz) {
    }

    @Override // org.apache.catalina.Context
    public void removePreDestroyMethod(String clazz) {
    }

    @Override // org.apache.catalina.Context
    public String findPostConstructMethod(String clazz) {
        return null;
    }

    @Override // org.apache.catalina.Context
    public String findPreDestroyMethod(String clazz) {
        return null;
    }

    @Override // org.apache.catalina.Context
    public Map<String, String> findPostConstructMethods() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public Map<String, String> findPreDestroyMethods() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public InstanceManager getInstanceManager() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setInstanceManager(InstanceManager instanceManager) {
    }

    @Override // org.apache.catalina.Context
    public void setContainerSciFilter(String containerSciFilter) {
    }

    @Override // org.apache.catalina.Context
    public String getContainerSciFilter() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public ThreadBindingListener getThreadBindingListener() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setThreadBindingListener(ThreadBindingListener threadBindingListener) {
    }

    @Override // org.apache.tomcat.ContextBind
    public ClassLoader bind(boolean usePrivilegedAction, ClassLoader originalClassLoader) {
        return null;
    }

    @Override // org.apache.tomcat.ContextBind
    public void unbind(boolean usePrivilegedAction, ClassLoader originalClassLoader) {
    }

    @Override // org.apache.catalina.Context
    public Object getNamingToken() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setCookieProcessor(CookieProcessor cookieProcessor) {
    }

    @Override // org.apache.catalina.Context
    public CookieProcessor getCookieProcessor() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setValidateClientProvidedNewSessionId(boolean validateClientProvidedNewSessionId) {
    }

    @Override // org.apache.catalina.Context
    public boolean getValidateClientProvidedNewSessionId() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setMapperContextRootRedirectEnabled(boolean mapperContextRootRedirectEnabled) {
    }

    @Override // org.apache.catalina.Context
    public boolean getMapperContextRootRedirectEnabled() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setMapperDirectoryRedirectEnabled(boolean mapperDirectoryRedirectEnabled) {
    }

    @Override // org.apache.catalina.Context
    public boolean getMapperDirectoryRedirectEnabled() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setUseRelativeRedirects(boolean useRelativeRedirects) {
    }

    @Override // org.apache.catalina.Context
    public boolean getUseRelativeRedirects() {
        return true;
    }

    @Override // org.apache.catalina.Context
    public void setDispatchersUseEncodedPaths(boolean dispatchersUseEncodedPaths) {
    }

    @Override // org.apache.catalina.Context
    public boolean getDispatchersUseEncodedPaths() {
        return true;
    }

    @Override // org.apache.catalina.Context
    public void setRequestCharacterEncoding(String encoding) {
    }

    @Override // org.apache.catalina.Context
    public String getRequestCharacterEncoding() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setResponseCharacterEncoding(String encoding) {
    }

    @Override // org.apache.catalina.Context
    public String getResponseCharacterEncoding() {
        return null;
    }

    @Override // org.apache.catalina.Context
    public void setAllowMultipleLeadingForwardSlashInPath(boolean allowMultipleLeadingForwardSlashInPath) {
    }

    @Override // org.apache.catalina.Context
    public boolean getAllowMultipleLeadingForwardSlashInPath() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void incrementInProgressAsyncCount() {
    }

    @Override // org.apache.catalina.Context
    public void decrementInProgressAsyncCount() {
    }

    @Override // org.apache.catalina.Context
    public void setCreateUploadTargets(boolean createUploadTargets) {
    }

    @Override // org.apache.catalina.Context
    public boolean getCreateUploadTargets() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public boolean getAlwaysAccessSession() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setAlwaysAccessSession(boolean alwaysAccessSession) {
    }

    @Override // org.apache.catalina.Context
    public boolean getContextGetResourceRequiresSlash() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setContextGetResourceRequiresSlash(boolean contextGetResourceRequiresSlash) {
    }

    @Override // org.apache.catalina.Context
    public boolean getDispatcherWrapsSameObject() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setDispatcherWrapsSameObject(boolean dispatcherWrapsSameObject) {
    }

    @Override // org.apache.catalina.Context
    public boolean getParallelAnnotationScanning() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setParallelAnnotationScanning(boolean parallelAnnotationScanning) {
    }

    @Override // org.apache.catalina.Context
    public boolean getUseBloomFilterForArchives() {
        return false;
    }

    @Override // org.apache.catalina.Context
    public void setUseBloomFilterForArchives(boolean useBloomFilterForArchives) {
    }
}
