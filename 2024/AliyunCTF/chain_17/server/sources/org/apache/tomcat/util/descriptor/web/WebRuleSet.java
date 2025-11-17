package org.apache.tomcat.util.descriptor.web;

import org.apache.catalina.Context;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.RuleSet;
import org.apache.tomcat.util.digester.SetNextRule;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/WebRuleSet.class */
public class WebRuleSet implements RuleSet {
    protected static final StringManager sm = StringManager.getManager(Constants.PACKAGE_NAME);
    protected final String prefix;
    protected final String fullPrefix;
    protected final boolean fragment;
    protected final SetSessionConfig sessionConfig;
    protected final SetLoginConfig loginConfig;
    protected final SetJspConfig jspConfig;
    protected final NameRule name;
    protected final AbsoluteOrderingRule absoluteOrdering;
    protected final RelativeOrderingRule relativeOrdering;

    public WebRuleSet() {
        this("", false);
    }

    public WebRuleSet(boolean fragment) {
        this("", fragment);
    }

    public WebRuleSet(String prefix, boolean fragment) {
        this.sessionConfig = new SetSessionConfig();
        this.loginConfig = new SetLoginConfig();
        this.jspConfig = new SetJspConfig();
        this.name = new NameRule();
        this.prefix = prefix;
        this.fragment = fragment;
        if (fragment) {
            this.fullPrefix = prefix + "web-fragment";
        } else {
            this.fullPrefix = prefix + "web-app";
        }
        this.absoluteOrdering = new AbsoluteOrderingRule(fragment);
        this.relativeOrdering = new RelativeOrderingRule(fragment);
    }

    @Override // org.apache.tomcat.util.digester.RuleSet
    public void addRuleInstances(Digester digester) {
        digester.addRule(this.fullPrefix, new SetPublicIdRule("setPublicId"));
        digester.addRule(this.fullPrefix, new IgnoreAnnotationsRule());
        digester.addRule(this.fullPrefix, new VersionRule());
        digester.addRule(this.fullPrefix + "/absolute-ordering", this.absoluteOrdering);
        digester.addRule(this.fullPrefix + "/ordering", this.relativeOrdering);
        if (this.fragment) {
            digester.addRule(this.fullPrefix + "/name", this.name);
            digester.addCallMethod(this.fullPrefix + "/ordering/after/name", "addAfterOrdering", 0);
            digester.addCallMethod(this.fullPrefix + "/ordering/after/others", "addAfterOrderingOthers");
            digester.addCallMethod(this.fullPrefix + "/ordering/before/name", "addBeforeOrdering", 0);
            digester.addCallMethod(this.fullPrefix + "/ordering/before/others", "addBeforeOrderingOthers");
        } else {
            digester.addCallMethod(this.fullPrefix + "/absolute-ordering/name", "addAbsoluteOrdering", 0);
            digester.addCallMethod(this.fullPrefix + "/absolute-ordering/others", "addAbsoluteOrderingOthers");
            digester.addRule(this.fullPrefix + "/deny-uncovered-http-methods", new SetDenyUncoveredHttpMethodsRule());
            digester.addCallMethod(this.fullPrefix + "/request-character-encoding", "setRequestCharacterEncoding", 0);
            digester.addCallMethod(this.fullPrefix + "/response-character-encoding", "setResponseCharacterEncoding", 0);
        }
        digester.addCallMethod(this.fullPrefix + "/context-param", "addContextParam", 2);
        digester.addCallParam(this.fullPrefix + "/context-param/param-name", 0);
        digester.addCallParam(this.fullPrefix + "/context-param/param-value", 1);
        digester.addCallMethod(this.fullPrefix + "/display-name", "setDisplayName", 0);
        digester.addRule(this.fullPrefix + "/distributable", new SetDistributableRule());
        configureNamingRules(digester);
        digester.addObjectCreate(this.fullPrefix + "/error-page", "org.apache.tomcat.util.descriptor.web.ErrorPage");
        digester.addSetNext(this.fullPrefix + "/error-page", "addErrorPage", "org.apache.tomcat.util.descriptor.web.ErrorPage");
        digester.addCallMethod(this.fullPrefix + "/error-page/error-code", "setErrorCode", 0);
        digester.addCallMethod(this.fullPrefix + "/error-page/exception-type", "setExceptionType", 0);
        digester.addCallMethod(this.fullPrefix + "/error-page/location", "setLocation", 0);
        digester.addObjectCreate(this.fullPrefix + "/filter", "org.apache.tomcat.util.descriptor.web.FilterDef");
        digester.addSetNext(this.fullPrefix + "/filter", "addFilter", "org.apache.tomcat.util.descriptor.web.FilterDef");
        digester.addCallMethod(this.fullPrefix + "/filter/description", "setDescription", 0);
        digester.addCallMethod(this.fullPrefix + "/filter/display-name", "setDisplayName", 0);
        digester.addCallMethod(this.fullPrefix + "/filter/filter-class", "setFilterClass", 0);
        digester.addCallMethod(this.fullPrefix + "/filter/filter-name", "setFilterName", 0);
        digester.addCallMethod(this.fullPrefix + "/filter/icon/large-icon", "setLargeIcon", 0);
        digester.addCallMethod(this.fullPrefix + "/filter/icon/small-icon", "setSmallIcon", 0);
        digester.addCallMethod(this.fullPrefix + "/filter/async-supported", "setAsyncSupported", 0);
        digester.addCallMethod(this.fullPrefix + "/filter/init-param", "addInitParameter", 2);
        digester.addCallParam(this.fullPrefix + "/filter/init-param/param-name", 0);
        digester.addCallParam(this.fullPrefix + "/filter/init-param/param-value", 1);
        digester.addObjectCreate(this.fullPrefix + "/filter-mapping", "org.apache.tomcat.util.descriptor.web.FilterMap");
        digester.addSetNext(this.fullPrefix + "/filter-mapping", "addFilterMapping", "org.apache.tomcat.util.descriptor.web.FilterMap");
        digester.addCallMethod(this.fullPrefix + "/filter-mapping/filter-name", "setFilterName", 0);
        digester.addCallMethod(this.fullPrefix + "/filter-mapping/servlet-name", "addServletName", 0);
        digester.addCallMethod(this.fullPrefix + "/filter-mapping/url-pattern", "addURLPattern", 0);
        digester.addCallMethod(this.fullPrefix + "/filter-mapping/dispatcher", "setDispatcher", 0);
        digester.addCallMethod(this.fullPrefix + "/listener/listener-class", "addListener", 0);
        digester.addRule(this.fullPrefix + "/jsp-config", this.jspConfig);
        digester.addObjectCreate(this.fullPrefix + "/jsp-config/jsp-property-group", "org.apache.tomcat.util.descriptor.web.JspPropertyGroup");
        digester.addSetNext(this.fullPrefix + "/jsp-config/jsp-property-group", "addJspPropertyGroup", "org.apache.tomcat.util.descriptor.web.JspPropertyGroup");
        digester.addCallMethod(this.fullPrefix + "/jsp-config/jsp-property-group/deferred-syntax-allowed-as-literal", "setDeferredSyntax", 0);
        digester.addCallMethod(this.fullPrefix + "/jsp-config/jsp-property-group/el-ignored", "setElIgnored", 0);
        digester.addCallMethod(this.fullPrefix + "/jsp-config/jsp-property-group/error-on-el-not-found", "setErrorOnELNotFound", 0);
        digester.addCallMethod(this.fullPrefix + "/jsp-config/jsp-property-group/include-coda", "addIncludeCoda", 0);
        digester.addCallMethod(this.fullPrefix + "/jsp-config/jsp-property-group/include-prelude", "addIncludePrelude", 0);
        digester.addCallMethod(this.fullPrefix + "/jsp-config/jsp-property-group/is-xml", "setIsXml", 0);
        digester.addCallMethod(this.fullPrefix + "/jsp-config/jsp-property-group/page-encoding", "setPageEncoding", 0);
        digester.addCallMethod(this.fullPrefix + "/jsp-config/jsp-property-group/scripting-invalid", "setScriptingInvalid", 0);
        digester.addCallMethod(this.fullPrefix + "/jsp-config/jsp-property-group/trim-directive-whitespaces", "setTrimWhitespace", 0);
        digester.addCallMethod(this.fullPrefix + "/jsp-config/jsp-property-group/url-pattern", "addUrlPattern", 0);
        digester.addCallMethod(this.fullPrefix + "/jsp-config/jsp-property-group/default-content-type", "setDefaultContentType", 0);
        digester.addCallMethod(this.fullPrefix + "/jsp-config/jsp-property-group/buffer", "setBuffer", 0);
        digester.addCallMethod(this.fullPrefix + "/jsp-config/jsp-property-group/error-on-undeclared-namespace", "setErrorOnUndeclaredNamespace", 0);
        digester.addRule(this.fullPrefix + "/login-config", this.loginConfig);
        digester.addObjectCreate(this.fullPrefix + "/login-config", "org.apache.tomcat.util.descriptor.web.LoginConfig");
        digester.addSetNext(this.fullPrefix + "/login-config", "setLoginConfig", "org.apache.tomcat.util.descriptor.web.LoginConfig");
        digester.addCallMethod(this.fullPrefix + "/login-config/auth-method", "setAuthMethod", 0);
        digester.addCallMethod(this.fullPrefix + "/login-config/realm-name", "setRealmName", 0);
        digester.addCallMethod(this.fullPrefix + "/login-config/form-login-config/form-error-page", "setErrorPage", 0);
        digester.addCallMethod(this.fullPrefix + "/login-config/form-login-config/form-login-page", "setLoginPage", 0);
        digester.addCallMethod(this.fullPrefix + "/mime-mapping", "addMimeMapping", 2);
        digester.addCallParam(this.fullPrefix + "/mime-mapping/extension", 0);
        digester.addCallParam(this.fullPrefix + "/mime-mapping/mime-type", 1);
        digester.addObjectCreate(this.fullPrefix + "/security-constraint", "org.apache.tomcat.util.descriptor.web.SecurityConstraint");
        digester.addSetNext(this.fullPrefix + "/security-constraint", "addSecurityConstraint", "org.apache.tomcat.util.descriptor.web.SecurityConstraint");
        digester.addRule(this.fullPrefix + "/security-constraint/auth-constraint", new SetAuthConstraintRule());
        digester.addCallMethod(this.fullPrefix + "/security-constraint/auth-constraint/role-name", "addAuthRole", 0);
        digester.addCallMethod(this.fullPrefix + "/security-constraint/display-name", "setDisplayName", 0);
        digester.addCallMethod(this.fullPrefix + "/security-constraint/user-data-constraint/transport-guarantee", "setUserConstraint", 0);
        digester.addObjectCreate(this.fullPrefix + "/security-constraint/web-resource-collection", "org.apache.tomcat.util.descriptor.web.SecurityCollection");
        digester.addSetNext(this.fullPrefix + "/security-constraint/web-resource-collection", "addCollection", "org.apache.tomcat.util.descriptor.web.SecurityCollection");
        digester.addCallMethod(this.fullPrefix + "/security-constraint/web-resource-collection/http-method", "addMethod", 0);
        digester.addCallMethod(this.fullPrefix + "/security-constraint/web-resource-collection/http-method-omission", "addOmittedMethod", 0);
        digester.addCallMethod(this.fullPrefix + "/security-constraint/web-resource-collection/url-pattern", "addPattern", 0);
        digester.addCallMethod(this.fullPrefix + "/security-constraint/web-resource-collection/web-resource-name", "setName", 0);
        digester.addCallMethod(this.fullPrefix + "/security-role/role-name", "addSecurityRole", 0);
        digester.addRule(this.fullPrefix + "/servlet", new ServletDefCreateRule());
        digester.addSetNext(this.fullPrefix + "/servlet", "addServlet", "org.apache.tomcat.util.descriptor.web.ServletDef");
        digester.addCallMethod(this.fullPrefix + "/servlet/init-param", "addInitParameter", 2);
        digester.addCallParam(this.fullPrefix + "/servlet/init-param/param-name", 0);
        digester.addCallParam(this.fullPrefix + "/servlet/init-param/param-value", 1);
        digester.addCallMethod(this.fullPrefix + "/servlet/jsp-file", "setJspFile", 0);
        digester.addCallMethod(this.fullPrefix + "/servlet/load-on-startup", "setLoadOnStartup", 0);
        digester.addCallMethod(this.fullPrefix + "/servlet/run-as/role-name", "setRunAs", 0);
        digester.addObjectCreate(this.fullPrefix + "/servlet/security-role-ref", "org.apache.tomcat.util.descriptor.web.SecurityRoleRef");
        digester.addSetNext(this.fullPrefix + "/servlet/security-role-ref", "addSecurityRoleRef", "org.apache.tomcat.util.descriptor.web.SecurityRoleRef");
        digester.addCallMethod(this.fullPrefix + "/servlet/security-role-ref/role-link", "setLink", 0);
        digester.addCallMethod(this.fullPrefix + "/servlet/security-role-ref/role-name", "setName", 0);
        digester.addCallMethod(this.fullPrefix + "/servlet/servlet-class", "setServletClass", 0);
        digester.addCallMethod(this.fullPrefix + "/servlet/servlet-name", "setServletName", 0);
        digester.addObjectCreate(this.fullPrefix + "/servlet/multipart-config", "org.apache.tomcat.util.descriptor.web.MultipartDef");
        digester.addSetNext(this.fullPrefix + "/servlet/multipart-config", "setMultipartDef", "org.apache.tomcat.util.descriptor.web.MultipartDef");
        digester.addCallMethod(this.fullPrefix + "/servlet/multipart-config/location", "setLocation", 0);
        digester.addCallMethod(this.fullPrefix + "/servlet/multipart-config/max-file-size", "setMaxFileSize", 0);
        digester.addCallMethod(this.fullPrefix + "/servlet/multipart-config/max-request-size", "setMaxRequestSize", 0);
        digester.addCallMethod(this.fullPrefix + "/servlet/multipart-config/file-size-threshold", "setFileSizeThreshold", 0);
        digester.addCallMethod(this.fullPrefix + "/servlet/async-supported", "setAsyncSupported", 0);
        digester.addCallMethod(this.fullPrefix + "/servlet/enabled", "setEnabled", 0);
        digester.addRule(this.fullPrefix + "/servlet-mapping", new CallMethodMultiRule("addServletMapping", 2, 0));
        digester.addCallParam(this.fullPrefix + "/servlet-mapping/servlet-name", 1);
        digester.addRule(this.fullPrefix + "/servlet-mapping/url-pattern", new CallParamMultiRule(0));
        digester.addRule(this.fullPrefix + "/session-config", this.sessionConfig);
        digester.addObjectCreate(this.fullPrefix + "/session-config", "org.apache.tomcat.util.descriptor.web.SessionConfig");
        digester.addSetNext(this.fullPrefix + "/session-config", "setSessionConfig", "org.apache.tomcat.util.descriptor.web.SessionConfig");
        digester.addCallMethod(this.fullPrefix + "/session-config/session-timeout", "setSessionTimeout", 0);
        digester.addCallMethod(this.fullPrefix + "/session-config/cookie-config/name", "setCookieName", 0);
        digester.addCallMethod(this.fullPrefix + "/session-config/cookie-config/domain", "setCookieDomain", 0);
        digester.addCallMethod(this.fullPrefix + "/session-config/cookie-config/path", "setCookiePath", 0);
        digester.addCallMethod(this.fullPrefix + "/session-config/cookie-config/comment", "setCookieComment", 0);
        digester.addCallMethod(this.fullPrefix + "/session-config/cookie-config/http-only", "setCookieHttpOnly", 0);
        digester.addCallMethod(this.fullPrefix + "/session-config/cookie-config/secure", "setCookieSecure", 0);
        digester.addCallMethod(this.fullPrefix + "/session-config/cookie-config/max-age", "setCookieMaxAge", 0);
        digester.addCallMethod(this.fullPrefix + "/session-config/cookie-config/attribute", "setCookieAttribute", 2);
        digester.addCallParam(this.fullPrefix + "/session-config/cookie-config/attribute/attribute-name", 0);
        digester.addCallParam(this.fullPrefix + "/session-config/cookie-config/attribute/attribute-value", 1);
        digester.addCallMethod(this.fullPrefix + "/session-config/tracking-mode", "addSessionTrackingMode", 0);
        digester.addRule(this.fullPrefix + "/taglib", new TaglibLocationRule(false));
        digester.addCallMethod(this.fullPrefix + "/taglib", "addTaglib", 2);
        digester.addCallParam(this.fullPrefix + "/taglib/taglib-location", 1);
        digester.addCallParam(this.fullPrefix + "/taglib/taglib-uri", 0);
        digester.addRule(this.fullPrefix + "/jsp-config/taglib", new TaglibLocationRule(true));
        digester.addCallMethod(this.fullPrefix + "/jsp-config/taglib", "addTaglib", 2);
        digester.addCallParam(this.fullPrefix + "/jsp-config/taglib/taglib-location", 1);
        digester.addCallParam(this.fullPrefix + "/jsp-config/taglib/taglib-uri", 0);
        digester.addCallMethod(this.fullPrefix + "/welcome-file-list/welcome-file", Context.ADD_WELCOME_FILE_EVENT, 0);
        digester.addCallMethod(this.fullPrefix + "/locale-encoding-mapping-list/locale-encoding-mapping", "addLocaleEncodingMapping", 2);
        digester.addCallParam(this.fullPrefix + "/locale-encoding-mapping-list/locale-encoding-mapping/locale", 0);
        digester.addCallParam(this.fullPrefix + "/locale-encoding-mapping-list/locale-encoding-mapping/encoding", 1);
        digester.addRule(this.fullPrefix + "/post-construct", new LifecycleCallbackRule("addPostConstructMethods", 2, true));
        digester.addCallParam(this.fullPrefix + "/post-construct/lifecycle-callback-class", 0);
        digester.addCallParam(this.fullPrefix + "/post-construct/lifecycle-callback-method", 1);
        digester.addRule(this.fullPrefix + "/pre-destroy", new LifecycleCallbackRule("addPreDestroyMethods", 2, false));
        digester.addCallParam(this.fullPrefix + "/pre-destroy/lifecycle-callback-class", 0);
        digester.addCallParam(this.fullPrefix + "/pre-destroy/lifecycle-callback-method", 1);
    }

    protected void configureNamingRules(Digester digester) {
        digester.addObjectCreate(this.fullPrefix + "/ejb-local-ref", "org.apache.tomcat.util.descriptor.web.ContextLocalEjb");
        digester.addSetNext(this.fullPrefix + "/ejb-local-ref", "addEjbLocalRef", "org.apache.tomcat.util.descriptor.web.ContextLocalEjb");
        digester.addCallMethod(this.fullPrefix + "/ejb-local-ref/description", "setDescription", 0);
        digester.addCallMethod(this.fullPrefix + "/ejb-local-ref/ejb-link", "setLink", 0);
        digester.addCallMethod(this.fullPrefix + "/ejb-local-ref/ejb-ref-name", "setName", 0);
        digester.addCallMethod(this.fullPrefix + "/ejb-local-ref/ejb-ref-type", "setType", 0);
        digester.addCallMethod(this.fullPrefix + "/ejb-local-ref/local", "setLocal", 0);
        digester.addCallMethod(this.fullPrefix + "/ejb-local-ref/local-home", "setHome", 0);
        digester.addRule(this.fullPrefix + "/ejb-local-ref/mapped-name", new MappedNameRule());
        digester.addCallMethod(this.fullPrefix + "/ejb-local-ref/lookup-name", "setLookupName", 0);
        configureInjectionRules(digester, "web-app/ejb-local-ref/");
        digester.addObjectCreate(this.fullPrefix + "/ejb-ref", "org.apache.tomcat.util.descriptor.web.ContextEjb");
        digester.addSetNext(this.fullPrefix + "/ejb-ref", "addEjbRef", "org.apache.tomcat.util.descriptor.web.ContextEjb");
        digester.addCallMethod(this.fullPrefix + "/ejb-ref/description", "setDescription", 0);
        digester.addCallMethod(this.fullPrefix + "/ejb-ref/ejb-link", "setLink", 0);
        digester.addCallMethod(this.fullPrefix + "/ejb-ref/ejb-ref-name", "setName", 0);
        digester.addCallMethod(this.fullPrefix + "/ejb-ref/ejb-ref-type", "setType", 0);
        digester.addCallMethod(this.fullPrefix + "/ejb-ref/home", "setHome", 0);
        digester.addCallMethod(this.fullPrefix + "/ejb-ref/remote", "setRemote", 0);
        digester.addRule(this.fullPrefix + "/ejb-ref/mapped-name", new MappedNameRule());
        digester.addCallMethod(this.fullPrefix + "/ejb-ref/lookup-name", "setLookupName", 0);
        configureInjectionRules(digester, "web-app/ejb-ref/");
        digester.addObjectCreate(this.fullPrefix + "/env-entry", "org.apache.tomcat.util.descriptor.web.ContextEnvironment");
        digester.addSetNext(this.fullPrefix + "/env-entry", "addEnvEntry", "org.apache.tomcat.util.descriptor.web.ContextEnvironment");
        digester.addRule(this.fullPrefix + "/env-entry", new SetOverrideRule());
        digester.addCallMethod(this.fullPrefix + "/env-entry/description", "setDescription", 0);
        digester.addCallMethod(this.fullPrefix + "/env-entry/env-entry-name", "setName", 0);
        digester.addCallMethod(this.fullPrefix + "/env-entry/env-entry-type", "setType", 0);
        digester.addCallMethod(this.fullPrefix + "/env-entry/env-entry-value", "setValue", 0);
        digester.addRule(this.fullPrefix + "/env-entry/mapped-name", new MappedNameRule());
        digester.addCallMethod(this.fullPrefix + "/env-entry/lookup-name", "setLookupName", 0);
        configureInjectionRules(digester, "web-app/env-entry/");
        digester.addObjectCreate(this.fullPrefix + "/resource-env-ref", "org.apache.tomcat.util.descriptor.web.ContextResourceEnvRef");
        digester.addSetNext(this.fullPrefix + "/resource-env-ref", "addResourceEnvRef", "org.apache.tomcat.util.descriptor.web.ContextResourceEnvRef");
        digester.addCallMethod(this.fullPrefix + "/resource-env-ref/resource-env-ref-name", "setName", 0);
        digester.addCallMethod(this.fullPrefix + "/resource-env-ref/resource-env-ref-type", "setType", 0);
        digester.addRule(this.fullPrefix + "/resource-env-ref/mapped-name", new MappedNameRule());
        digester.addCallMethod(this.fullPrefix + "/resource-env-ref/lookup-name", "setLookupName", 0);
        configureInjectionRules(digester, "web-app/resource-env-ref/");
        digester.addObjectCreate(this.fullPrefix + "/message-destination", "org.apache.tomcat.util.descriptor.web.MessageDestination");
        digester.addSetNext(this.fullPrefix + "/message-destination", "addMessageDestination", "org.apache.tomcat.util.descriptor.web.MessageDestination");
        digester.addCallMethod(this.fullPrefix + "/message-destination/description", "setDescription", 0);
        digester.addCallMethod(this.fullPrefix + "/message-destination/display-name", "setDisplayName", 0);
        digester.addCallMethod(this.fullPrefix + "/message-destination/icon/large-icon", "setLargeIcon", 0);
        digester.addCallMethod(this.fullPrefix + "/message-destination/icon/small-icon", "setSmallIcon", 0);
        digester.addCallMethod(this.fullPrefix + "/message-destination/message-destination-name", "setName", 0);
        digester.addRule(this.fullPrefix + "/message-destination/mapped-name", new MappedNameRule());
        digester.addCallMethod(this.fullPrefix + "/message-destination/lookup-name", "setLookupName", 0);
        digester.addObjectCreate(this.fullPrefix + "/message-destination-ref", "org.apache.tomcat.util.descriptor.web.MessageDestinationRef");
        digester.addSetNext(this.fullPrefix + "/message-destination-ref", "addMessageDestinationRef", "org.apache.tomcat.util.descriptor.web.MessageDestinationRef");
        digester.addCallMethod(this.fullPrefix + "/message-destination-ref/description", "setDescription", 0);
        digester.addCallMethod(this.fullPrefix + "/message-destination-ref/message-destination-link", "setLink", 0);
        digester.addCallMethod(this.fullPrefix + "/message-destination-ref/message-destination-ref-name", "setName", 0);
        digester.addCallMethod(this.fullPrefix + "/message-destination-ref/message-destination-type", "setType", 0);
        digester.addCallMethod(this.fullPrefix + "/message-destination-ref/message-destination-usage", "setUsage", 0);
        digester.addRule(this.fullPrefix + "/message-destination-ref/mapped-name", new MappedNameRule());
        digester.addCallMethod(this.fullPrefix + "/message-destination-ref/lookup-name", "setLookupName", 0);
        configureInjectionRules(digester, "web-app/message-destination-ref/");
        digester.addObjectCreate(this.fullPrefix + "/resource-ref", "org.apache.tomcat.util.descriptor.web.ContextResource");
        digester.addSetNext(this.fullPrefix + "/resource-ref", "addResourceRef", "org.apache.tomcat.util.descriptor.web.ContextResource");
        digester.addCallMethod(this.fullPrefix + "/resource-ref/description", "setDescription", 0);
        digester.addCallMethod(this.fullPrefix + "/resource-ref/res-auth", "setAuth", 0);
        digester.addCallMethod(this.fullPrefix + "/resource-ref/res-ref-name", "setName", 0);
        digester.addCallMethod(this.fullPrefix + "/resource-ref/res-sharing-scope", "setScope", 0);
        digester.addCallMethod(this.fullPrefix + "/resource-ref/res-type", "setType", 0);
        digester.addRule(this.fullPrefix + "/resource-ref/mapped-name", new MappedNameRule());
        digester.addCallMethod(this.fullPrefix + "/resource-ref/lookup-name", "setLookupName", 0);
        configureInjectionRules(digester, "web-app/resource-ref/");
        digester.addObjectCreate(this.fullPrefix + "/service-ref", "org.apache.tomcat.util.descriptor.web.ContextService");
        digester.addSetNext(this.fullPrefix + "/service-ref", "addServiceRef", "org.apache.tomcat.util.descriptor.web.ContextService");
        digester.addCallMethod(this.fullPrefix + "/service-ref/description", "setDescription", 0);
        digester.addCallMethod(this.fullPrefix + "/service-ref/display-name", "setDisplayname", 0);
        digester.addCallMethod(this.fullPrefix + "/service-ref/icon/large-icon", "setLargeIcon", 0);
        digester.addCallMethod(this.fullPrefix + "/service-ref/icon/small-icon", "setSmallIcon", 0);
        digester.addCallMethod(this.fullPrefix + "/service-ref/service-ref-name", "setName", 0);
        digester.addCallMethod(this.fullPrefix + "/service-ref/service-interface", "setInterface", 0);
        digester.addCallMethod(this.fullPrefix + "/service-ref/service-ref-type", "setType", 0);
        digester.addCallMethod(this.fullPrefix + "/service-ref/wsdl-file", "setWsdlfile", 0);
        digester.addCallMethod(this.fullPrefix + "/service-ref/jaxrpc-mapping-file", "setJaxrpcmappingfile", 0);
        digester.addRule(this.fullPrefix + "/service-ref/service-qname", new ServiceQnameRule());
        digester.addRule(this.fullPrefix + "/service-ref/port-component-ref", new CallMethodMultiRule("addPortcomponent", 2, 1));
        digester.addCallParam(this.fullPrefix + "/service-ref/port-component-ref/service-endpoint-interface", 0);
        digester.addRule(this.fullPrefix + "/service-ref/port-component-ref/port-component-link", new CallParamMultiRule(1));
        digester.addObjectCreate(this.fullPrefix + "/service-ref/handler", "org.apache.tomcat.util.descriptor.web.ContextHandler");
        digester.addRule(this.fullPrefix + "/service-ref/handler", new SetNextRule("addHandler", "org.apache.tomcat.util.descriptor.web.ContextHandler"));
        digester.addCallMethod(this.fullPrefix + "/service-ref/handler/handler-name", "setName", 0);
        digester.addCallMethod(this.fullPrefix + "/service-ref/handler/handler-class", "setHandlerclass", 0);
        digester.addCallMethod(this.fullPrefix + "/service-ref/handler/init-param", "setProperty", 2);
        digester.addCallParam(this.fullPrefix + "/service-ref/handler/init-param/param-name", 0);
        digester.addCallParam(this.fullPrefix + "/service-ref/handler/init-param/param-value", 1);
        digester.addRule(this.fullPrefix + "/service-ref/handler/soap-header", new SoapHeaderRule());
        digester.addCallMethod(this.fullPrefix + "/service-ref/handler/soap-role", "addSoapRole", 0);
        digester.addCallMethod(this.fullPrefix + "/service-ref/handler/port-name", "addPortName", 0);
        digester.addRule(this.fullPrefix + "/service-ref/mapped-name", new MappedNameRule());
        digester.addCallMethod(this.fullPrefix + "/service-ref/lookup-name", "setLookupName", 0);
        configureInjectionRules(digester, "web-app/service-ref/");
    }

    protected void configureInjectionRules(Digester digester, String base) {
        digester.addCallMethod(this.prefix + base + "injection-target", "addInjectionTarget", 2);
        digester.addCallParam(this.prefix + base + "injection-target/injection-target-class", 0);
        digester.addCallParam(this.prefix + base + "injection-target/injection-target-name", 1);
    }

    public void recycle() {
        this.jspConfig.isJspConfigSet = false;
        this.sessionConfig.isSessionConfigSet = false;
        this.loginConfig.isLoginConfigSet = false;
        this.name.isNameSet = false;
        this.absoluteOrdering.isAbsoluteOrderingSet = false;
        this.relativeOrdering.isRelativeOrderingSet = false;
    }
}
