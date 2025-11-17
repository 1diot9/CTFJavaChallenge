package org.apache.catalina.connector;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import javax.management.ObjectName;
import org.apache.catalina.Executor;
import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Service;
import org.apache.catalina.core.AprStatus;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.Adapter;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.http11.AbstractHttp11JsseProtocol;
import org.apache.coyote.http11.Constants;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jni.SSL;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.CharsetUtil;
import org.apache.tomcat.util.buf.EncodedSolidusHandling;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.openssl.OpenSSLImplementation;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.asm.Opcodes;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.web.servlet.support.WebContentGenerator;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/Connector.class */
public class Connector extends LifecycleMBeanBase {
    public static final String INTERNAL_EXECUTOR_NAME = "Internal";
    protected Service service;
    protected boolean allowBackslash;
    protected boolean allowTrace;
    protected long asyncTimeout;
    protected boolean enableLookups;
    protected boolean enforceEncodingInGetWriter;
    protected boolean xpoweredBy;
    protected String proxyName;
    protected int proxyPort;
    protected boolean discardFacades;
    protected int redirectPort;
    protected String scheme;
    protected boolean secure;
    private int maxCookieCount;
    protected int maxParameterCount;
    protected int maxPostSize;
    protected int maxSavePostSize;
    protected String parseBodyMethods;
    protected HashSet<String> parseBodyMethodsSet;
    protected boolean useIPVHosts;
    protected final String protocolHandlerClassName;
    protected final String configuredProtocol;
    protected final ProtocolHandler protocolHandler;
    protected Adapter adapter;
    private Charset uriCharset;
    private EncodedSolidusHandling encodedSolidusHandling;
    protected boolean useBodyEncodingForURI;
    private boolean rejectSuspiciousURIs;
    private static final Log log = LogFactory.getLog((Class<?>) Connector.class);
    protected static final StringManager sm = StringManager.getManager((Class<?>) Connector.class);

    public Connector() {
        this(Constants.HTTP_11);
    }

    public Connector(String protocol) {
        this.service = null;
        this.allowBackslash = false;
        this.allowTrace = false;
        this.asyncTimeout = ExponentialBackOff.DEFAULT_MAX_INTERVAL;
        this.enableLookups = false;
        this.enforceEncodingInGetWriter = true;
        this.xpoweredBy = false;
        this.proxyName = null;
        this.proxyPort = 0;
        this.discardFacades = true;
        this.redirectPort = 443;
        this.scheme = "http";
        this.secure = false;
        this.maxCookieCount = 200;
        this.maxParameterCount = 10000;
        this.maxPostSize = SSL.SSL_OP_EPHEMERAL_RSA;
        this.maxSavePostSize = Opcodes.ACC_SYNTHETIC;
        this.parseBodyMethods = WebContentGenerator.METHOD_POST;
        this.useIPVHosts = false;
        this.adapter = null;
        this.uriCharset = StandardCharsets.UTF_8;
        this.encodedSolidusHandling = EncodedSolidusHandling.REJECT;
        this.useBodyEncodingForURI = false;
        this.configuredProtocol = protocol;
        ProtocolHandler p = null;
        try {
            p = ProtocolHandler.create(protocol);
        } catch (Exception e) {
            log.error(sm.getString("coyoteConnector.protocolHandlerInstantiationFailed"), e);
        }
        if (p != null) {
            this.protocolHandler = p;
            this.protocolHandlerClassName = this.protocolHandler.getClass().getName();
        } else {
            this.protocolHandler = null;
            this.protocolHandlerClassName = protocol;
        }
        setThrowOnFailure(Boolean.getBoolean("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE"));
    }

    public Connector(ProtocolHandler protocolHandler) {
        this.service = null;
        this.allowBackslash = false;
        this.allowTrace = false;
        this.asyncTimeout = ExponentialBackOff.DEFAULT_MAX_INTERVAL;
        this.enableLookups = false;
        this.enforceEncodingInGetWriter = true;
        this.xpoweredBy = false;
        this.proxyName = null;
        this.proxyPort = 0;
        this.discardFacades = true;
        this.redirectPort = 443;
        this.scheme = "http";
        this.secure = false;
        this.maxCookieCount = 200;
        this.maxParameterCount = 10000;
        this.maxPostSize = SSL.SSL_OP_EPHEMERAL_RSA;
        this.maxSavePostSize = Opcodes.ACC_SYNTHETIC;
        this.parseBodyMethods = WebContentGenerator.METHOD_POST;
        this.useIPVHosts = false;
        this.adapter = null;
        this.uriCharset = StandardCharsets.UTF_8;
        this.encodedSolidusHandling = EncodedSolidusHandling.REJECT;
        this.useBodyEncodingForURI = false;
        this.protocolHandlerClassName = protocolHandler.getClass().getName();
        this.configuredProtocol = this.protocolHandlerClassName;
        this.protocolHandler = protocolHandler;
        setThrowOnFailure(Boolean.getBoolean("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE"));
    }

    public Object getProperty(String name) {
        if (this.protocolHandler == null) {
            return null;
        }
        return IntrospectionUtils.getProperty(this.protocolHandler, name);
    }

    public boolean setProperty(String name, String value) {
        if (this.protocolHandler == null) {
            return false;
        }
        return IntrospectionUtils.setProperty(this.protocolHandler, name, value);
    }

    public Service getService() {
        return this.service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public boolean getAllowBackslash() {
        return this.allowBackslash;
    }

    public void setAllowBackslash(boolean allowBackslash) {
        this.allowBackslash = allowBackslash;
    }

    public boolean getAllowTrace() {
        return this.allowTrace;
    }

    public void setAllowTrace(boolean allowTrace) {
        this.allowTrace = allowTrace;
    }

    public long getAsyncTimeout() {
        return this.asyncTimeout;
    }

    public void setAsyncTimeout(long asyncTimeout) {
        this.asyncTimeout = asyncTimeout;
    }

    public boolean getDiscardFacades() {
        return this.discardFacades || Globals.IS_SECURITY_ENABLED;
    }

    public void setDiscardFacades(boolean discardFacades) {
        this.discardFacades = discardFacades;
    }

    public boolean getEnableLookups() {
        return this.enableLookups;
    }

    public void setEnableLookups(boolean enableLookups) {
        this.enableLookups = enableLookups;
    }

    public boolean getEnforceEncodingInGetWriter() {
        return this.enforceEncodingInGetWriter;
    }

    public void setEnforceEncodingInGetWriter(boolean enforceEncodingInGetWriter) {
        this.enforceEncodingInGetWriter = enforceEncodingInGetWriter;
    }

    public int getMaxCookieCount() {
        return this.maxCookieCount;
    }

    public void setMaxCookieCount(int maxCookieCount) {
        this.maxCookieCount = maxCookieCount;
    }

    public int getMaxParameterCount() {
        return this.maxParameterCount;
    }

    public void setMaxParameterCount(int maxParameterCount) {
        this.maxParameterCount = maxParameterCount;
    }

    public int getMaxPostSize() {
        return this.maxPostSize;
    }

    public void setMaxPostSize(int maxPostSize) {
        this.maxPostSize = maxPostSize;
    }

    public int getMaxSavePostSize() {
        return this.maxSavePostSize;
    }

    public void setMaxSavePostSize(int maxSavePostSize) {
        this.maxSavePostSize = maxSavePostSize;
        setProperty("maxSavePostSize", String.valueOf(maxSavePostSize));
    }

    public String getParseBodyMethods() {
        return this.parseBodyMethods;
    }

    public void setParseBodyMethods(String methods) {
        HashSet<String> methodSet = new HashSet<>();
        if (null != methods) {
            methodSet.addAll(Arrays.asList(methods.split("\\s*,\\s*")));
        }
        if (methodSet.contains("TRACE")) {
            throw new IllegalArgumentException(sm.getString("coyoteConnector.parseBodyMethodNoTrace"));
        }
        this.parseBodyMethods = methods;
        this.parseBodyMethodsSet = methodSet;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isParseBodyMethod(String method) {
        return this.parseBodyMethodsSet.contains(method);
    }

    public int getPort() {
        if (this.protocolHandler instanceof AbstractProtocol) {
            return ((AbstractProtocol) this.protocolHandler).getPort();
        }
        Object port = getProperty("port");
        if (port instanceof Integer) {
            return ((Integer) port).intValue();
        }
        return -1;
    }

    public void setPort(int port) {
        setProperty("port", String.valueOf(port));
    }

    public int getPortOffset() {
        if (this.protocolHandler instanceof AbstractProtocol) {
            return ((AbstractProtocol) this.protocolHandler).getPortOffset();
        }
        Object port = getProperty("portOffset");
        if (port instanceof Integer) {
            return ((Integer) port).intValue();
        }
        return 0;
    }

    public void setPortOffset(int portOffset) {
        setProperty("portOffset", String.valueOf(portOffset));
    }

    public int getPortWithOffset() {
        int port = getPort();
        if (port > 0) {
            return port + getPortOffset();
        }
        return port;
    }

    public int getLocalPort() {
        return ((Integer) getProperty("localPort")).intValue();
    }

    public String getProtocol() {
        return this.configuredProtocol;
    }

    public String getProtocolHandlerClassName() {
        return this.protocolHandlerClassName;
    }

    public ProtocolHandler getProtocolHandler() {
        return this.protocolHandler;
    }

    public String getProxyName() {
        return this.proxyName;
    }

    public void setProxyName(String proxyName) {
        if (proxyName != null && proxyName.length() > 0) {
            this.proxyName = proxyName;
        } else {
            this.proxyName = null;
        }
    }

    public int getProxyPort() {
        return this.proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public int getRedirectPort() {
        return this.redirectPort;
    }

    public void setRedirectPort(int redirectPort) {
        this.redirectPort = redirectPort;
    }

    public int getRedirectPortWithOffset() {
        return getRedirectPort() + getPortOffset();
    }

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public boolean getSecure() {
        return this.secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
        setProperty("secure", Boolean.toString(secure));
    }

    public String getURIEncoding() {
        return this.uriCharset.name();
    }

    public Charset getURICharset() {
        return this.uriCharset;
    }

    public void setURIEncoding(String URIEncoding) {
        try {
            Charset charset = B2CConverter.getCharset(URIEncoding);
            if (!CharsetUtil.isAsciiSuperset(charset)) {
                log.error(sm.getString("coyoteConnector.notAsciiSuperset", URIEncoding, this.uriCharset.name()));
            } else {
                this.uriCharset = charset;
            }
        } catch (UnsupportedEncodingException e) {
            log.error(sm.getString("coyoteConnector.invalidEncoding", URIEncoding, this.uriCharset.name()), e);
        }
    }

    public boolean getUseBodyEncodingForURI() {
        return this.useBodyEncodingForURI;
    }

    public void setUseBodyEncodingForURI(boolean useBodyEncodingForURI) {
        this.useBodyEncodingForURI = useBodyEncodingForURI;
    }

    public boolean getXpoweredBy() {
        return this.xpoweredBy;
    }

    public void setXpoweredBy(boolean xpoweredBy) {
        this.xpoweredBy = xpoweredBy;
    }

    public void setUseIPVHosts(boolean useIPVHosts) {
        this.useIPVHosts = useIPVHosts;
    }

    public boolean getUseIPVHosts() {
        return this.useIPVHosts;
    }

    public String getExecutorName() {
        Object obj = this.protocolHandler.getExecutor();
        if (obj instanceof Executor) {
            return ((Executor) obj).getName();
        }
        return INTERNAL_EXECUTOR_NAME;
    }

    public void addSslHostConfig(SSLHostConfig sslHostConfig) {
        this.protocolHandler.addSslHostConfig(sslHostConfig);
    }

    public SSLHostConfig[] findSslHostConfigs() {
        return this.protocolHandler.findSslHostConfigs();
    }

    public void addUpgradeProtocol(UpgradeProtocol upgradeProtocol) {
        this.protocolHandler.addUpgradeProtocol(upgradeProtocol);
    }

    public UpgradeProtocol[] findUpgradeProtocols() {
        return this.protocolHandler.findUpgradeProtocols();
    }

    public String getEncodedSolidusHandling() {
        return this.encodedSolidusHandling.getValue();
    }

    public void setEncodedSolidusHandling(String encodedSolidusHandling) {
        this.encodedSolidusHandling = EncodedSolidusHandling.fromString(encodedSolidusHandling);
    }

    public EncodedSolidusHandling getEncodedSolidusHandlingInternal() {
        return this.encodedSolidusHandling;
    }

    public boolean getRejectSuspiciousURIs() {
        return this.rejectSuspiciousURIs;
    }

    public void setRejectSuspiciousURIs(boolean rejectSuspiciousURIs) {
        this.rejectSuspiciousURIs = rejectSuspiciousURIs;
    }

    public Request createRequest() {
        return new Request(this);
    }

    public Response createResponse() {
        int size = this.protocolHandler.getDesiredBufferSize();
        if (size > 0) {
            return new Response(size);
        }
        return new Response();
    }

    protected String createObjectNameKeyProperties(String type) {
        Object addressObj = getProperty("address");
        StringBuilder sb = new StringBuilder("type=");
        sb.append(type);
        String id = this.protocolHandler != null ? this.protocolHandler.getId() : null;
        if (id != null) {
            sb.append(",port=0,address=");
            sb.append(ObjectName.quote(id));
        } else {
            sb.append(",port=");
            int port = getPortWithOffset();
            if (port > 0) {
                sb.append(port);
            } else {
                sb.append("auto-");
                sb.append(getProperty("nameIndex"));
            }
            String address = "";
            if (addressObj instanceof InetAddress) {
                address = ((InetAddress) addressObj).getHostAddress();
            } else if (addressObj != null) {
                address = addressObj.toString();
            }
            if (address.length() > 0) {
                sb.append(",address=");
                sb.append(ObjectName.quote(address));
            }
        }
        return sb.toString();
    }

    public void pause() {
        try {
            if (this.protocolHandler != null) {
                this.protocolHandler.pause();
            }
        } catch (Exception e) {
            log.error(sm.getString("coyoteConnector.protocolHandlerPauseFailed"), e);
        }
    }

    public void resume() {
        try {
            if (this.protocolHandler != null) {
                this.protocolHandler.resume();
            }
        } catch (Exception e) {
            log.error(sm.getString("coyoteConnector.protocolHandlerResumeFailed"), e);
        }
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        if (this.protocolHandler == null) {
            throw new LifecycleException(sm.getString("coyoteConnector.protocolHandlerInstantiationFailed"));
        }
        this.adapter = new CoyoteAdapter(this);
        this.protocolHandler.setAdapter(this.adapter);
        if (null == this.parseBodyMethodsSet) {
            setParseBodyMethods(getParseBodyMethods());
        }
        if (AprStatus.isAprAvailable() && AprStatus.getUseOpenSSL() && (this.protocolHandler instanceof AbstractHttp11JsseProtocol)) {
            AbstractHttp11JsseProtocol<?> jsseProtocolHandler = (AbstractHttp11JsseProtocol) this.protocolHandler;
            if (jsseProtocolHandler.isSSLEnabled() && jsseProtocolHandler.getSslImplementationName() == null) {
                jsseProtocolHandler.setSslImplementationName(OpenSSLImplementation.class.getName());
            }
        }
        try {
            this.protocolHandler.init();
        } catch (Exception e) {
            throw new LifecycleException(sm.getString("coyoteConnector.protocolHandlerInitializationFailed"), e);
        }
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void startInternal() throws LifecycleException {
        String id = this.protocolHandler != null ? this.protocolHandler.getId() : null;
        if (id == null && getPortWithOffset() < 0) {
            throw new LifecycleException(sm.getString("coyoteConnector.invalidPort", Integer.valueOf(getPortWithOffset())));
        }
        setState(LifecycleState.STARTING);
        if (this.protocolHandler != null && this.service != null) {
            this.protocolHandler.setUtilityExecutor(this.service.getServer().getUtilityExecutor());
        }
        try {
            this.protocolHandler.start();
        } catch (Exception e) {
            throw new LifecycleException(sm.getString("coyoteConnector.protocolHandlerStartFailed"), e);
        }
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
        try {
            if (this.protocolHandler != null) {
                this.protocolHandler.stop();
            }
            if (this.protocolHandler != null) {
                this.protocolHandler.setUtilityExecutor(null);
            }
        } catch (Exception e) {
            throw new LifecycleException(sm.getString("coyoteConnector.protocolHandlerStopFailed"), e);
        }
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    protected void destroyInternal() throws LifecycleException {
        try {
            if (this.protocolHandler != null) {
                this.protocolHandler.destroy();
            }
            if (getService() != null) {
                getService().removeConnector(this);
            }
            super.destroyInternal();
        } catch (Exception e) {
            throw new LifecycleException(sm.getString("coyoteConnector.protocolHandlerDestroyFailed"), e);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Connector[");
        String name = (String) getProperty("name");
        if (name == null) {
            sb.append(getProtocol());
            sb.append('-');
            String id = this.protocolHandler != null ? this.protocolHandler.getId() : null;
            if (id != null) {
                sb.append(id);
            } else {
                int port = getPortWithOffset();
                if (port > 0) {
                    sb.append(port);
                } else {
                    sb.append("auto-");
                    sb.append(getProperty("nameIndex"));
                }
            }
        } else {
            sb.append(name);
        }
        sb.append(']');
        return sb.toString();
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getDomainInternal() {
        Service s = getService();
        if (s == null) {
            return null;
        }
        return this.service.getDomain();
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getObjectNameKeyProperties() {
        return createObjectNameKeyProperties("Connector");
    }
}
