package org.apache.catalina;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletSecurityElement;
import jakarta.servlet.descriptor.JspConfigDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.tomcat.ContextBind;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.descriptor.web.ApplicationParameter;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.apache.tomcat.util.http.CookieProcessor;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/Context.class */
public interface Context extends Container, ContextBind {
    public static final String ADD_WELCOME_FILE_EVENT = "addWelcomeFile";
    public static final String REMOVE_WELCOME_FILE_EVENT = "removeWelcomeFile";
    public static final String CLEAR_WELCOME_FILES_EVENT = "clearWelcomeFiles";
    public static final String CHANGE_SESSION_ID_EVENT = "changeSessionId";
    public static final String WEBAPP_PROTOCOL = "webapp:";

    boolean getAllowCasualMultipartParsing();

    void setAllowCasualMultipartParsing(boolean z);

    Object[] getApplicationEventListeners();

    void setApplicationEventListeners(Object[] objArr);

    Object[] getApplicationLifecycleListeners();

    void setApplicationLifecycleListeners(Object[] objArr);

    String getCharset(Locale locale);

    URL getConfigFile();

    void setConfigFile(URL url);

    boolean getConfigured();

    void setConfigured(boolean z);

    boolean getCookies();

    void setCookies(boolean z);

    String getSessionCookieName();

    void setSessionCookieName(String str);

    boolean getUseHttpOnly();

    void setUseHttpOnly(boolean z);

    boolean getUsePartitioned();

    void setUsePartitioned(boolean z);

    String getSessionCookieDomain();

    void setSessionCookieDomain(String str);

    String getSessionCookiePath();

    void setSessionCookiePath(String str);

    boolean getSessionCookiePathUsesTrailingSlash();

    void setSessionCookiePathUsesTrailingSlash(boolean z);

    boolean getCrossContext();

    String getAltDDName();

    void setAltDDName(String str);

    void setCrossContext(boolean z);

    boolean getDenyUncoveredHttpMethods();

    void setDenyUncoveredHttpMethods(boolean z);

    String getDisplayName();

    void setDisplayName(String str);

    boolean getDistributable();

    void setDistributable(boolean z);

    String getDocBase();

    void setDocBase(String str);

    String getEncodedPath();

    boolean getIgnoreAnnotations();

    void setIgnoreAnnotations(boolean z);

    LoginConfig getLoginConfig();

    void setLoginConfig(LoginConfig loginConfig);

    NamingResourcesImpl getNamingResources();

    void setNamingResources(NamingResourcesImpl namingResourcesImpl);

    String getPath();

    void setPath(String str);

    String getPublicId();

    void setPublicId(String str);

    boolean getReloadable();

    void setReloadable(boolean z);

    boolean getOverride();

    void setOverride(boolean z);

    boolean getPrivileged();

    void setPrivileged(boolean z);

    ServletContext getServletContext();

    int getSessionTimeout();

    void setSessionTimeout(int i);

    boolean getSwallowAbortedUploads();

    void setSwallowAbortedUploads(boolean z);

    boolean getSwallowOutput();

    void setSwallowOutput(boolean z);

    String getWrapperClass();

    void setWrapperClass(String str);

    boolean getXmlNamespaceAware();

    void setXmlNamespaceAware(boolean z);

    boolean getXmlValidation();

    void setXmlValidation(boolean z);

    boolean getXmlBlockExternal();

    void setXmlBlockExternal(boolean z);

    boolean getTldValidation();

    void setTldValidation(boolean z);

    JarScanner getJarScanner();

    void setJarScanner(JarScanner jarScanner);

    Authenticator getAuthenticator();

    void setLogEffectiveWebXml(boolean z);

    boolean getLogEffectiveWebXml();

    InstanceManager getInstanceManager();

    void setInstanceManager(InstanceManager instanceManager);

    void setContainerSciFilter(String str);

    String getContainerSciFilter();

    boolean getParallelAnnotationScanning();

    void setParallelAnnotationScanning(boolean z);

    void addApplicationListener(String str);

    void addApplicationParameter(ApplicationParameter applicationParameter);

    void addConstraint(SecurityConstraint securityConstraint);

    void addErrorPage(ErrorPage errorPage);

    void addFilterDef(FilterDef filterDef);

    void addFilterMap(FilterMap filterMap);

    void addFilterMapBefore(FilterMap filterMap);

    void addLocaleEncodingMappingParameter(String str, String str2);

    void addMimeMapping(String str, String str2);

    void addParameter(String str, String str2);

    void addRoleMapping(String str, String str2);

    void addSecurityRole(String str);

    void addServletMappingDecoded(String str, String str2, boolean z);

    void addWatchedResource(String str);

    void addWelcomeFile(String str);

    void addWrapperLifecycle(String str);

    void addWrapperListener(String str);

    InstanceManager createInstanceManager();

    Wrapper createWrapper();

    String[] findApplicationListeners();

    ApplicationParameter[] findApplicationParameters();

    SecurityConstraint[] findConstraints();

    ErrorPage findErrorPage(int i);

    ErrorPage findErrorPage(Throwable th);

    ErrorPage[] findErrorPages();

    FilterDef findFilterDef(String str);

    FilterDef[] findFilterDefs();

    FilterMap[] findFilterMaps();

    String findMimeMapping(String str);

    String[] findMimeMappings();

    String findParameter(String str);

    String[] findParameters();

    String findRoleMapping(String str);

    boolean findSecurityRole(String str);

    String[] findSecurityRoles();

    String findServletMapping(String str);

    String[] findServletMappings();

    ThreadBindingListener getThreadBindingListener();

    void setThreadBindingListener(ThreadBindingListener threadBindingListener);

    String[] findWatchedResources();

    boolean findWelcomeFile(String str);

    String[] findWelcomeFiles();

    String[] findWrapperLifecycles();

    String[] findWrapperListeners();

    boolean fireRequestInitEvent(ServletRequest servletRequest);

    boolean fireRequestDestroyEvent(ServletRequest servletRequest);

    void reload();

    void removeApplicationListener(String str);

    void removeApplicationParameter(String str);

    void removeConstraint(SecurityConstraint securityConstraint);

    void removeErrorPage(ErrorPage errorPage);

    void removeFilterDef(FilterDef filterDef);

    void removeFilterMap(FilterMap filterMap);

    void removeMimeMapping(String str);

    void removeParameter(String str);

    void removeRoleMapping(String str);

    void removeSecurityRole(String str);

    void removeServletMapping(String str);

    void removeWatchedResource(String str);

    void removeWelcomeFile(String str);

    void removeWrapperLifecycle(String str);

    void removeWrapperListener(String str);

    String getRealPath(String str);

    int getEffectiveMajorVersion();

    void setEffectiveMajorVersion(int i);

    int getEffectiveMinorVersion();

    void setEffectiveMinorVersion(int i);

    JspConfigDescriptor getJspConfigDescriptor();

    void setJspConfigDescriptor(JspConfigDescriptor jspConfigDescriptor);

    void addServletContainerInitializer(ServletContainerInitializer servletContainerInitializer, Set<Class<?>> set);

    boolean getPaused();

    boolean isServlet22();

    Set<String> addServletSecurity(ServletRegistration.Dynamic dynamic, ServletSecurityElement servletSecurityElement);

    void setResourceOnlyServlets(String str);

    String getResourceOnlyServlets();

    boolean isResourceOnlyServlet(String str);

    String getBaseName();

    void setWebappVersion(String str);

    String getWebappVersion();

    void setFireRequestListenersOnForwards(boolean z);

    boolean getFireRequestListenersOnForwards();

    void setPreemptiveAuthentication(boolean z);

    boolean getPreemptiveAuthentication();

    void setSendRedirectBody(boolean z);

    boolean getSendRedirectBody();

    Loader getLoader();

    void setLoader(Loader loader);

    WebResourceRoot getResources();

    void setResources(WebResourceRoot webResourceRoot);

    Manager getManager();

    void setManager(Manager manager);

    void setAddWebinfClassesResources(boolean z);

    boolean getAddWebinfClassesResources();

    void addPostConstructMethod(String str, String str2);

    void addPreDestroyMethod(String str, String str2);

    void removePostConstructMethod(String str);

    void removePreDestroyMethod(String str);

    String findPostConstructMethod(String str);

    String findPreDestroyMethod(String str);

    Map<String, String> findPostConstructMethods();

    Map<String, String> findPreDestroyMethods();

    Object getNamingToken();

    void setCookieProcessor(CookieProcessor cookieProcessor);

    CookieProcessor getCookieProcessor();

    void setValidateClientProvidedNewSessionId(boolean z);

    boolean getValidateClientProvidedNewSessionId();

    void setMapperContextRootRedirectEnabled(boolean z);

    boolean getMapperContextRootRedirectEnabled();

    void setMapperDirectoryRedirectEnabled(boolean z);

    boolean getMapperDirectoryRedirectEnabled();

    void setUseRelativeRedirects(boolean z);

    boolean getUseRelativeRedirects();

    void setDispatchersUseEncodedPaths(boolean z);

    boolean getDispatchersUseEncodedPaths();

    void setRequestCharacterEncoding(String str);

    String getRequestCharacterEncoding();

    void setResponseCharacterEncoding(String str);

    String getResponseCharacterEncoding();

    void setAllowMultipleLeadingForwardSlashInPath(boolean z);

    boolean getAllowMultipleLeadingForwardSlashInPath();

    void incrementInProgressAsyncCount();

    void decrementInProgressAsyncCount();

    void setCreateUploadTargets(boolean z);

    boolean getCreateUploadTargets();

    boolean getAlwaysAccessSession();

    void setAlwaysAccessSession(boolean z);

    boolean getContextGetResourceRequiresSlash();

    void setContextGetResourceRequiresSlash(boolean z);

    boolean getDispatcherWrapsSameObject();

    void setDispatcherWrapsSameObject(boolean z);

    @Deprecated
    boolean getUseBloomFilterForArchives();

    @Deprecated
    void setUseBloomFilterForArchives(boolean z);

    @Deprecated
    default boolean isParallelAnnotationScanning() {
        return getParallelAnnotationScanning();
    }

    default void addServletMappingDecoded(String pattern, String name) {
        addServletMappingDecoded(pattern, name, false);
    }

    default ConfigurationSource.Resource findConfigFileResource(String name) throws IOException {
        if (name.startsWith(WEBAPP_PROTOCOL)) {
            String path = name.substring(WEBAPP_PROTOCOL.length());
            WebResource resource = getResources().getResource(path);
            if (resource.canRead() && resource.isFile()) {
                InputStream stream = resource.getInputStream();
                try {
                    return new ConfigurationSource.Resource(stream, resource.getURL().toURI());
                } catch (URISyntaxException e) {
                    stream.close();
                }
            }
            throw new FileNotFoundException(name);
        }
        return ConfigFileLoader.getSource().getResource(name);
    }
}
