package org.apache.catalina.security;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/security/SecurityClassLoad.class */
public final class SecurityClassLoad {
    public static void securityClassLoad(ClassLoader loader) throws Exception {
        securityClassLoad(loader, true);
    }

    static void securityClassLoad(ClassLoader loader, boolean requireSecurityManager) throws Exception {
        if (requireSecurityManager && System.getSecurityManager() == null) {
            return;
        }
        loadCorePackage(loader);
        loadCoyotePackage(loader);
        loadLoaderPackage(loader);
        loadRealmPackage(loader);
        loadServletsPackage(loader);
        loadSessionPackage(loader);
        loadUtilPackage(loader);
        loadJakartaPackage(loader);
        loadConnectorPackage(loader);
        loadTomcatPackage(loader);
    }

    private static void loadCorePackage(ClassLoader loader) throws Exception {
        loader.loadClass("org.apache.catalina.core.AccessLogAdapter");
        loader.loadClass("org.apache.catalina.core.ApplicationContextFacade$PrivilegedExecuteMethod");
        loader.loadClass("org.apache.catalina.core.ApplicationDispatcher$PrivilegedForward");
        loader.loadClass("org.apache.catalina.core.ApplicationDispatcher$PrivilegedInclude");
        loader.loadClass("org.apache.catalina.core.ApplicationPushBuilder");
        loader.loadClass("org.apache.catalina.core.AsyncContextImpl");
        loader.loadClass("org.apache.catalina.core.AsyncContextImpl$AsyncRunnable");
        loader.loadClass("org.apache.catalina.core.AsyncContextImpl$DebugException");
        loader.loadClass("org.apache.catalina.core.AsyncListenerWrapper");
        loader.loadClass("org.apache.catalina.core.ContainerBase$PrivilegedAddChild");
        loader.loadClass("org.apache.catalina.core.DefaultInstanceManager$AnnotationCacheEntry");
        loader.loadClass("org.apache.catalina.core.DefaultInstanceManager$AnnotationCacheEntryType");
        loader.loadClass("org.apache.catalina.core.DefaultInstanceManager$PrivilegedGetField");
        loader.loadClass("org.apache.catalina.core.DefaultInstanceManager$PrivilegedGetMethod");
        loader.loadClass("org.apache.catalina.core.DefaultInstanceManager$PrivilegedLoadClass");
        loader.loadClass("org.apache.catalina.core.ApplicationHttpRequest$AttributeNamesEnumerator");
    }

    private static void loadLoaderPackage(ClassLoader loader) throws Exception {
        loader.loadClass("org.apache.catalina.loader.WebappClassLoaderBase$PrivilegedFindClassByName");
        loader.loadClass("org.apache.catalina.loader.WebappClassLoaderBase$PrivilegedHasLoggingConfig");
    }

    private static void loadRealmPackage(ClassLoader loader) throws Exception {
        loader.loadClass("org.apache.catalina.realm.LockOutRealm$LockRecord");
    }

    private static void loadServletsPackage(ClassLoader loader) throws Exception {
        loader.loadClass("org.apache.catalina.servlets.DefaultServlet");
    }

    private static void loadSessionPackage(ClassLoader loader) throws Exception {
        loader.loadClass("org.apache.catalina.session.StandardSession");
        loader.loadClass("org.apache.catalina.session.StandardSession$PrivilegedNewSessionFacade");
        loader.loadClass("org.apache.catalina.session.StandardManager$PrivilegedDoUnload");
    }

    private static void loadUtilPackage(ClassLoader loader) throws Exception {
        loader.loadClass("org.apache.catalina.util.ParameterMap");
        loader.loadClass("org.apache.catalina.util.RequestUtil");
        loader.loadClass("org.apache.catalina.util.TLSUtil");
    }

    private static void loadCoyotePackage(ClassLoader loader) throws Exception {
        loader.loadClass("org.apache.coyote.http11.Constants");
        Class<?> clazz = loader.loadClass("org.apache.coyote.Constants");
        clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        loader.loadClass("org.apache.coyote.http2.Stream$PrivilegedPush");
    }

    private static void loadJakartaPackage(ClassLoader loader) throws Exception {
        loader.loadClass("jakarta.servlet.http.Cookie");
    }

    private static void loadConnectorPackage(ClassLoader loader) throws Exception {
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetAttributePrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetParameterMapPrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetRequestDispatcherPrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetParameterPrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetParameterNamesPrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetParameterValuePrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetCharacterEncodingPrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetHeadersPrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetHeaderNamesPrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetCookiesPrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetLocalePrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetLocalesPrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.ResponseFacade$SetContentTypePrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.ResponseFacade$DateHeaderPrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.RequestFacade$GetSessionPrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.ResponseFacade$FlushBufferPrivilegedAction");
        loader.loadClass("org.apache.catalina.connector.OutputBuffer$PrivilegedCreateConverter");
        loader.loadClass("org.apache.catalina.connector.CoyoteInputStream$PrivilegedAvailable");
        loader.loadClass("org.apache.catalina.connector.CoyoteInputStream$PrivilegedClose");
        loader.loadClass("org.apache.catalina.connector.CoyoteInputStream$PrivilegedRead");
        loader.loadClass("org.apache.catalina.connector.CoyoteInputStream$PrivilegedReadArray");
        loader.loadClass("org.apache.catalina.connector.CoyoteInputStream$PrivilegedReadBuffer");
        loader.loadClass("org.apache.catalina.connector.CoyoteOutputStream");
        loader.loadClass("org.apache.catalina.connector.InputBuffer$PrivilegedCreateConverter");
        loader.loadClass("org.apache.catalina.connector.Response$PrivilegedDoIsEncodable");
        loader.loadClass("org.apache.catalina.connector.Response$PrivilegedGenerateCookieString");
        loader.loadClass("org.apache.catalina.connector.Response$PrivilegedEncodeUrl");
    }

    private static void loadTomcatPackage(ClassLoader loader) throws Exception {
        loader.loadClass("org.apache.tomcat.util.buf.B2CConverter");
        loader.loadClass("org.apache.tomcat.util.buf.ByteBufferUtils");
        loader.loadClass("org.apache.tomcat.util.buf.C2BConverter");
        loader.loadClass("org.apache.tomcat.util.buf.HexUtils");
        loader.loadClass("org.apache.tomcat.util.buf.StringCache");
        loader.loadClass("org.apache.tomcat.util.buf.StringCache$ByteEntry");
        loader.loadClass("org.apache.tomcat.util.buf.StringCache$CharEntry");
        loader.loadClass("org.apache.tomcat.util.buf.UriUtil");
        loader.loadClass("org.apache.tomcat.util.collections.CaseInsensitiveKeyMap");
        loader.loadClass("org.apache.tomcat.util.collections.CaseInsensitiveKeyMap$EntryImpl");
        loader.loadClass("org.apache.tomcat.util.collections.CaseInsensitiveKeyMap$EntryIterator");
        loader.loadClass("org.apache.tomcat.util.collections.CaseInsensitiveKeyMap$EntrySet");
        loader.loadClass("org.apache.tomcat.util.collections.CaseInsensitiveKeyMap$Key");
        loader.loadClass("org.apache.tomcat.util.http.CookieProcessor");
        loader.loadClass("org.apache.tomcat.util.http.NamesEnumerator");
        Class<?> clazz = loader.loadClass("org.apache.tomcat.util.http.FastHttpDateFormat");
        clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        loader.loadClass("org.apache.tomcat.util.http.parser.HttpParser");
        loader.loadClass("org.apache.tomcat.util.http.parser.MediaType");
        loader.loadClass("org.apache.tomcat.util.http.parser.MediaTypeCache");
        loader.loadClass("org.apache.tomcat.util.http.parser.SkipResult");
        loader.loadClass("org.apache.tomcat.util.net.Constants");
        loader.loadClass("org.apache.tomcat.util.net.DispatchType");
        loader.loadClass("org.apache.tomcat.util.net.NioEndpoint$NioSocketWrapper$NioOperationState");
        loader.loadClass("org.apache.tomcat.util.net.Nio2Endpoint$Nio2SocketWrapper$Nio2OperationState");
        loader.loadClass("org.apache.tomcat.util.net.SocketWrapperBase$BlockingMode");
        loader.loadClass("org.apache.tomcat.util.net.SocketWrapperBase$CompletionCheck");
        loader.loadClass("org.apache.tomcat.util.net.SocketWrapperBase$CompletionHandlerCall");
        loader.loadClass("org.apache.tomcat.util.net.SocketWrapperBase$CompletionState");
        loader.loadClass("org.apache.tomcat.util.net.SocketWrapperBase$VectoredIOCompletionHandler");
        loader.loadClass("org.apache.tomcat.util.net.TLSClientHelloExtractor");
        loader.loadClass("org.apache.tomcat.util.net.TLSClientHelloExtractor$ExtractorResult");
        loader.loadClass("org.apache.tomcat.util.security.PrivilegedGetTccl");
        loader.loadClass("org.apache.tomcat.util.security.PrivilegedSetTccl");
        loader.loadClass("org.apache.tomcat.util.security.PrivilegedSetAccessControlContext");
    }
}
